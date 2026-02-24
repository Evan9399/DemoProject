package com.example.demo.dto.Response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BatchSummaryResponse {
    // 這是原本的單筆資料列表
    private List<AiSummaryResponse> data;

    // 額外的統計資訊
    private int totalRequest; // 原本撈到幾筆 (e.g., 10)
    private int successCount; // 成功幾筆 (e.g., 8)
    private String status; // 狀態 (e.g., "PARTIAL_SUCCESS")
}