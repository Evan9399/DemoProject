package com.example.demo.dto.Request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DeleteRequest {
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @Schema(description = "刪除起始時間", example = "2025-12-04 08:00:00")
        private LocalDateTime startTime;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @Schema(description = "刪除起始時間", example = "2025-12-04 08:00:00")
        private LocalDateTime endTime;

}