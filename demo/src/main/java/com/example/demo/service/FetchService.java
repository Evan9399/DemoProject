package com.example.demo.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.example.demo.config.FetchPageConfig;
import com.example.demo.dto.Request.PageRequestDTO;
import com.example.demo.dto.Response.FetchResponse;
import com.example.demo.entity.PageEntity;
import com.example.demo.repository.PageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FetchService {

    private final PageRepository pageRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final FetchPageConfig fetchPageConfig;

    /**
     * 呼叫外部 API 取得資料並儲存
     */
    public FetchResponse fetchAndSave() throws Exception {
        // 從config帶入帳號、Token、時間範圍等
        PageRequestDTO req = fetchPageConfig.buildRequest();

        // JSON → String (因為 API 要求 Form-urlencoded)
        String json = objectMapper.writeValueAsString(req);

        // 構建 Form-urlencoded Body to JSON
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("txtInput_json", json);

        // 構建請求 header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        // 呼叫 API
        String responseBody = restTemplate.postForObject(fetchPageConfig.getUrl(), requestEntity, String.class);
        log.info("呼叫 API: {}", fetchPageConfig.getUrl());

        // JSON 解析
        FetchResponse apiResponse = objectMapper.readValue(responseBody, FetchResponse.class);

        // 存資料庫
        // apiResponse 是整份 API 回傳的物件，包含回應資訊與多筆資料。
        // 先檢查回傳是否成功，再把 result 裡的每一筆 PageResponse 轉換成 PageEntity，
        // 最後用 saveAll 一次寫入資料庫。
        if (apiResponse != null && apiResponse.getResult() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            List<PageEntity> entities = apiResponse.getResult().stream()
                    .map(r -> PageEntity.builder()
                            .id(r.getId())
                            .title(r.getTitle())
                            .content(r.getContent())
                            .sName(r.getSName())
                            .sAreaName(r.getSAreaName())
                            .pageUrl(r.getPageUrl())
                            // 將 DTO 的 String 轉成 Entity 的 LocalDateTime
                            .postTime(r.getPostTime() != null && !r.getPostTime().isBlank()
                                    ? LocalDateTime.parse(r.getPostTime(), formatter)
                                    : null)
                            .author(r.getAuthor())
                            .mainId(r.getMainId())
                            .positivePerc(r.getPositivePerc())
                            .nagativePerc(r.getNagativePerc())
                            .commentCount(r.getCommentCount())
                            .viewCount(r.getViewCount())
                            .usedCount(r.getUsedCount())
                            .contentType(r.getContentType())
                            .sentimentTag(r.getSentimentTag())
                            .hitNum(r.getHitNum())
                            .articleType(r.getArticleType())
                            .aiSummary(r.getSummary())
                            .build())
                    .collect(Collectors.toList());

            pageRepository.saveAll(entities);
        }

        return apiResponse;
    }

    /**
     * 定時任務，每天 10 點抓資料
     */
    @Scheduled(cron = "0 0 10 * * *")
    public void scheduleTask() {
        try {
            log.info("定時任務開始...");
            fetchAndSave();
            log.info("定時任務完成。");
        } catch (Exception e) {
            log.error("定時任務執行失敗: {}", e.getMessage(), e);
        }
    }
}