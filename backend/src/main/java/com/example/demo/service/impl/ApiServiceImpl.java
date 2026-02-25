package com.example.demo.service.impl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.example.demo.Exception.InvalidInputException;
import com.example.demo.Exception.ResourceNotFoundException;
import com.example.demo.dto.Request.DeleteRequest;
import com.example.demo.dto.Request.PageFilterRequest;
import com.example.demo.dto.Request.UpdateRequest;
import com.example.demo.dto.Response.DeleteResponse;
import com.example.demo.dto.Response.UpdateResponse;
import com.example.demo.entity.PageEntity;
import com.example.demo.repository.PageRepository;
import com.example.demo.service.ApiService;
import com.example.demo.service.LogService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApiServiceImpl implements ApiService {
    private final PageRepository pageRepository;
    private final LogService logService;
    Logger logger = LogManager.getLogger(ApiServiceImpl.class);

    @Override // 不限時間查詢DB所有TanName文章資料
    public List<PageEntity> findBySentimentTag(String sentimentTag) {
        if (sentimentTag == null || sentimentTag.isBlank()) {
            logService.logToFramework("WARN", "sentimentTag 不可為空");
            throw new InvalidInputException("sentimentTag 不可為空");
        }
        if (!sentimentTag.equals("M") && !sentimentTag.equals("P") && !sentimentTag.equals("N")) {
            logService.logToFramework("WARN", "錯誤 sentimentTag: " + sentimentTag);
            throw new InvalidInputException("sentimentTag 只能是M、P、N");
        }
        return pageRepository.findBySentimentTag(sentimentTag);
    }

    @Override
    public List<PageEntity> findPages(String sentimentTag, PageFilterRequest request) {

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

        // LocalDate 轉成 LocalDateTime
        LocalDateTime start = request.getStartDate().atStartOfDay(); // 00:00:00
        LocalDateTime end = request.getEndDate().atTime(LocalTime.MAX); // 23:59:59

        // 如果 end < start，自動交換
        if (end.isBefore(start)) {
            LocalDateTime tmp = start;
            start = end;
            end = tmp;
        }

        // 4️⃣ JAP查詢
        return pageRepository.findBySentimentTagAndCreateTimeBetween(
                sentimentTag,
                start,
                end);
    }

    // 更新資料
    @Override
    @Transactional
    public UpdateResponse updatePage(UpdateRequest request) {
        // 驗證資料
        if (request.getId() == null || request.getId().isBlank()) {
            throw new InvalidInputException("id不可為空");
        }

        // 找資料
        Optional<PageEntity> entityOpt = pageRepository.findById(request.getId());
        if (entityOpt.isEmpty()) {
            throw new ResourceNotFoundException("找不到指定文章 ID: " + request.getId());
        }

        PageEntity entity = entityOpt.get();
        Map<String, Object> fields = request.getFields();

        if (fields != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            // 塞入更新資料
            fields.forEach((key, value) -> {
                switch (key) {
                    case "title" -> entity.setTitle((String) value);
                    case "content" -> entity.setContent((String) value);
                    case "sName" -> entity.setSName((String) value);
                    case "sAreaName" -> entity.setSAreaName((String) value);
                    case "pageUrl" -> entity.setPageUrl((String) value);
                    case "author" -> entity.setAuthor((String) value);
                    case "mainId" -> entity.setMainId((String) value);
                    case "positivePerc" -> entity.setPositivePerc((String) value);
                    case "nagativePerc" -> entity.setNagativePerc((String) value);
                    case "sentimentTag" -> entity.setSentimentTag((String) value);
                    case "commentCount" -> entity.setCommentCount((Integer) value);
                    case "viewCount" -> entity.setViewCount((Integer) value);
                    case "usedCount" -> entity.setUsedCount((Integer) value);
                    case "hitNum" -> entity.setHitNum((Integer) value);
                    case "createTime" -> {
                        if (value != null) {
                            entity.setCreateTime(LocalDateTime.parse((String) value, formatter));
                        }
                    }
                    case "postTime" -> {
                        if (value != null) {
                            entity.setPostTime(LocalDateTime.parse((String) value, formatter));
                        }
                    }
                }
            });
        }

        // 自動更新最後修改時間
        entity.setUpdateTime(LocalDateTime.now());

        pageRepository.save(entity);

        UpdateResponse response = new UpdateResponse();
        response.setSuccess(true);
        response.setUpdatedCount(1);
        response.setMessage("更新成功");
        return response;
    }

    // 刪除資料ByTime跟TagName
    public DeleteResponse deleteBySentimentTag(String sentimentTag, DeleteRequest request) {

        // 驗證 sentimentTag 不可為空
        if (sentimentTag == null || sentimentTag.isBlank()) {
            throw new InvalidInputException("請指定 sentimentTag");
        }

        int deleted = pageRepository.deleteBySentimentTagAndCreateTimeBetween(
                sentimentTag, request.getStartTime(), request.getEndTime());

        DeleteResponse response = new DeleteResponse();
        response.setDeletedCount(deleted);
        response.setSuccess(true);
        response.setMessage(deleted > 0 ? "刪除成功" : "沒有符合條件的資料");
        return response;
    }

    // public DeleteResponse deleteBySentimentTagAndSNameAndCreateTimeBetween(String sentimentTag, LocalDateTime startTime,
    //         LocalDateTime endTime, String sName) {
    //     // 驗證 sentimentTag 不可為空
    //     if (sentimentTag == null || sentimentTag.isBlank()) {
    //         throw new InvalidInputException("請指定 sentimentTag");
    //     }
    // }
}