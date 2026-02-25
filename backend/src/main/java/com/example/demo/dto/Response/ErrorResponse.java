package com.example.demo.dto.Response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "標準化 API 錯誤回應模型")
public class ErrorResponse {

    @Schema(description = "HTTP 狀態碼", example = "400")
    private int status;

    @Schema(description = "錯誤代碼或類型", example = "INVALID_INPUT")
    private String code;

    @Schema(description = "錯誤訊息描述", example = "請求參數格式不正確或缺失。")
    private String message;

    @Schema(description = "錯誤發生時間", example = "2025-12-10 14:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp = LocalDateTime.now();
}