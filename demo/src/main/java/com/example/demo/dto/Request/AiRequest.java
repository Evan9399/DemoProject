package com.example.demo.dto.Request;

import lombok.Data;

@Data
public class AiRequest {
    private String id; // 文章 ID
    // 可以加入額外參數，例如摘要長度限制
    private Integer maxLength;
}