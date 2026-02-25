package com.example.demo.dto.Response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class DailySummaryResponse {
    private String date; // 日期 (yyyy-MM-dd)
    private String sentimentTag; // 情緒標籤 (Positive, Negative, Neutral)
    private long articleCount; // 文章總數
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastPostTime; // 最後一篇文章的發表時間
    private String summary; // 該情緒標籤下的文章摘要 (如果使用 AI，這是 AI 摘要)

    // 💡 提示：在實際應用中，summary 可能需要是 List<String> 或 String (將所有摘要連接起來)
}
