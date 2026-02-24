package com.example.demo.service.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.demo.Exception.InvalidInputException;
import com.example.demo.dto.Request.PageFilterRequest;
import com.example.demo.dto.Response.AiSummaryResponse;
import com.example.demo.dto.Response.BatchSummaryResponse;
import com.example.demo.dto.Response.CsvExportResponse;
import com.example.demo.dto.Response.DailySummaryResponse;
import com.example.demo.entity.PageEntity;
import com.example.demo.repository.PageRepository;
import com.example.demo.service.AiSummaryService;
import com.example.demo.service.LogService;
import com.example.demo.utils.AiApiCaller;
import com.example.demo.utils.AiJsonParser;
import com.example.demo.utils.AiPromptBuilder;
import com.example.demo.utils.CsvUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiSummaryServiceImpl implements AiSummaryService {

    private final PageRepository pageRepository;
    private final LogService logService;
    private final AiApiCaller apiCaller;

    @Value("${ai.prompt.batch-summary-template}")
    private String SUMMARY_PROMPT;

    @Value("${ai.prompt.daily-summary-template}")
    private String DAILY_TEMPLATE;

    @Value("${ai.excerpt.max-chars:300}")
    private int EXCERPT_MAX_CHARS;

    @Value("${ai.batch.max-total:10}")
    private int MAX_TOTAL;

    @Override
    public BatchSummaryResponse generateAndFetchBatchSummaries(String sentimentTag, PageFilterRequest request) {
        // 驗證輸入值
        if (sentimentTag == null || sentimentTag.isBlank()) {
            logService.logToFramework("WARN", "sentimentTag 不可為空");
            throw new InvalidInputException("sentimentTag 不可為空");
        }
        if (!sentimentTag.equals("M") && !sentimentTag.equals("P") && !sentimentTag.equals("N")) {
            logService.logToFramework("WARN", "錯誤 sentimentTag: " + sentimentTag);
            throw new InvalidInputException("sentimentTag 只能是M、P、N");
        }

        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new InvalidInputException("startDate 與 endDate 皆為必填");
        }
        List<PageEntity> pages = pageRepository.findBySentimentTagAndPostTimeBetween(
                sentimentTag,
                request.getStartDate().atStartOfDay(),
                request.getEndDate().atTime(LocalTime.MAX), // 發文時間
                PageRequest.of(0, MAX_TOTAL, Sort.by("postTime").descending()));// desc排序，最多篇數設定

        logService.startLog("INFO", "確認資料");

        if (pages == null || pages.isEmpty()) {
            // 若是空值回傳NO_DATA
            return BatchSummaryResponse.builder()
                    .data(Collections.emptyList())
                    .totalRequest(0)
                    .successCount(0)
                    .status("NO_DATA")
                    .build();
        }

        int totalRequested = pages.size();

        // 1) 文章放到prompt , util抽出此方法
        String prompt = AiPromptBuilder.buildBatchPrompt(SUMMARY_PROMPT, pages, EXCERPT_MAX_CHARS);

        // 2) 傳給AI
        String raw;
        try {
            raw = apiCaller.callWithRetry(prompt);
        } catch (RuntimeException e) {
            String message = e.getMessage() == null ? "" : e.getMessage();
            if (message.contains("429") || message.contains("⚠️ 伺服器忙碌中，稍後再試...")) {
                // friendly server busy response (use builder)
                return BatchSummaryResponse.builder()
                        .data(Collections.emptyList())
                        .totalRequest(totalRequested)
                        .successCount(0)
                        .status("⚠️ AI 模型服務過載，等待後重試...")
                        .build();
            }
            // 失敗的數值
            log.error("AI 呼叫失敗", e);
            return BatchSummaryResponse.builder()
                    .data(Collections.emptyList())
                    .totalRequest(totalRequested)
                    .successCount(0)
                    .status("FAILED")
                    .build();
        }

        if (raw == null || raw.isBlank()) {
            return BatchSummaryResponse.builder()
                    .data(Collections.emptyList())
                    .totalRequest(totalRequested)
                    .successCount(0)
                    .status("FAILED")
                    .build();
        }

        // 3) 解析AI回傳的json
        List<AiSummaryResponse> results = new ArrayList<>(pages.size());
        for (PageEntity p : pages) {
            AiSummaryResponse r = AiSummaryResponse.builder()
                    .id(Objects.toString(p.getId(), ""))
                    .title(p.getTitle())
                    .content(p.getContent())
                    .postTime(p.getPostTime())
                    .aiSummary(raw)
                    .build();
            results.add(r);
        }

        logService.endLog("INFO", "完成");

        return BatchSummaryResponse.builder()
                .data(results)
                .totalRequest(totalRequested)
                .successCount(results.size())
                .status("COMPLETED")
                .build();
    }

    // 文章摘要 CSV
    @Override
    public CsvExportResponse generateDailySummariesCSV(LocalDate startDate, LocalDate endDate) throws IOException {
        // 儲存每天摘要
        List<DailySummaryResponse> allDailySummaries = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {

            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

            List<PageEntity> dailyPages = pageRepository.findByPostTimeBetween(startOfDay, endOfDay);
            if (dailyPages == null || dailyPages.isEmpty()) {
                log.info("日期 [{}] 無文章資料，跳過。", date);
                continue;
            }

            Map<String, List<PageEntity>> groupedBySentiment = dailyPages.stream()
                    .filter(p -> p.getSentimentTag() != null && !p.getSentimentTag().isBlank())
                    .collect(Collectors.groupingBy(PageEntity::getSentimentTag));

            for (Map.Entry<String, List<PageEntity>> entry : groupedBySentiment.entrySet()) {
                String sentimentTag = entry.getKey();
                List<PageEntity> articles = entry.getValue();

                LocalDateTime lastPostTime = articles.stream()
                        .map(PageEntity::getPostTime)
                        .filter(Objects::nonNull)
                        .max(LocalDateTime::compareTo)
                        .orElse(null);

                String combinedSummary = "";

                // M的不印
                if (!"M".equalsIgnoreCase(sentimentTag)) {

                    String dailyPrompt = AiPromptBuilder.buildDailySummaryPrompt(
                            DAILY_TEMPLATE,
                            articles,
                            20, // MAX_ARTICLES
                            500 // MAX_CHARS_PER_ARTICLE
                    );

                    String raw = "";
                    try {
                        raw = apiCaller.callWithRetry(dailyPrompt);
                    } catch (RuntimeException e) {
                        log.warn("AI 生成每日摘要失敗: {}", e.getMessage());
                        raw = "";
                    }

                    if (raw != null && !raw.isBlank()) {
                        try {
                            Map<String, String> parsed = AiJsonParser.parseAsMap(raw);
                            combinedSummary = parsed.getOrDefault("summary", raw);
                        } catch (Exception ex) {
                            // if parsing fails, fallback to raw text
                            combinedSummary = raw;
                        }
                    }
                }

                DailySummaryResponse dto = new DailySummaryResponse();
                dto.setDate(date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
                dto.setSentimentTag(sentimentTag);
                dto.setArticleCount(articles.size());
                dto.setSummary(combinedSummary);
                dto.setLastPostTime(lastPostTime);

                allDailySummaries.add(dto);
            }
        }

        byte[] csvBytes;
        try {
            csvBytes = CsvUtil.convertDailySummariesToCsv(allDailySummaries);
        } catch (Exception e) {
            throw new IOException("CSV 轉換失敗", e);
        }

        String filename = String.format("DailySummary_%s_to_%s.csv", startDate.toString(), endDate.toString());
        return new CsvExportResponse(csvBytes, filename);
    }
}
