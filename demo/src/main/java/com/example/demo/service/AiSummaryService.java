package com.example.demo.service;

import java.io.IOException;
import java.time.LocalDate;

import com.example.demo.dto.Request.PageFilterRequest;
import com.example.demo.dto.Response.BatchSummaryResponse;
import com.example.demo.dto.Response.CsvExportResponse;

public interface AiSummaryService {
    /**
     * 根據文章 ID 觸發摘要生成
     * 
     * @param pageId 文章 ID
     * @return 更新後的 PageEntity
     */
    // AiSummaryResponse generateSummary(String pageId);

    BatchSummaryResponse generateAndFetchBatchSummaries(String sentimentTag, PageFilterRequest request);

    CsvExportResponse generateDailySummariesCSV(LocalDate startDate, LocalDate endDate) throws IOException;
}