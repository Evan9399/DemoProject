package com.example.demo.dto.Response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiSummaryResponse {
    private String id;
    private String title;
    private String content;

    // AI 生成摘要的結果
    private String aiSummary;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    // 可以保留文章發布時間
    private LocalDateTime postTime;
}