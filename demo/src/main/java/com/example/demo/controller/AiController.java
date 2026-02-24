package com.example.demo.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.Request.PageFilterRequest;
import com.example.demo.dto.Response.BatchSummaryResponse;
import com.example.demo.dto.Response.CsvExportResponse;
import com.example.demo.service.AiSummaryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "AI", description = "AI 相關操作")
@RestController
@RequestMapping("/ai")
// @ApiResponses({
// @ApiResponse(responseCode = "400", description = "無效輸入", content =
// @Content(schema = @Schema(implementation = ErrorResponse.class))),
// @ApiResponse(responseCode = "404", description = "找不到資源", content =
// @Content(schema = @Schema(implementation = ErrorResponse.class))),
// @ApiResponse(responseCode = "500", description = "伺服器內部錯誤", content =
// @Content(schema = @Schema(implementation = ErrorResponse.class)))
// })
public class AiController {

    @Autowired
    private AiSummaryService aiSummaryService;

    @Operation(summary = "生成批次摘要", description = "根據 sentimentTag 生成批次摘要")
    @ApiResponse(responseCode = "200", description = "批次摘要生成成功", content = @Content(schema = @Schema(implementation = BatchSummaryResponse.class)))
    @PostMapping("/batch-summaries/{sentimentTag}") // 新的測試 API 接口
    public ResponseEntity<BatchSummaryResponse> getBatchSummaries(@PathVariable String sentimentTag,
            @RequestBody PageFilterRequest request) {

        BatchSummaryResponse response = aiSummaryService.generateAndFetchBatchSummaries(sentimentTag, request);

        // ... (根據狀態回傳 response) ...
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "匯出每日摘要", description = "匯出每日摘要及統計總數")
    @ApiResponse(responseCode = "200", description = "匯出成功", content = @Content(schema = @Schema(implementation = CsvExportResponse.class)))
    @GetMapping("/exportDaily")
    public ResponseEntity<byte[]> exportDailySummaries(
            @Parameter(description = "開始日期", example = "2025-12-01") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "結束日期", example = "2025-12-31") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            // 1. 呼叫服務，現在期望回傳 CsvExportResponse
            CsvExportResponse responseDto = aiSummaryService.generateDailySummariesCSV(startDate, endDate);

            // 2. 僅處理 HTTP Header
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment",
                    responseDto.getFilename());
            headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
            headers.setContentLength(responseDto.getFileContent().length);

            // 3. 回傳最終的 ResponseEntity
            return new ResponseEntity<>(responseDto.getFileContent(), headers,
                    HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("CSV 匯出失敗: " + e.getMessage());
            return new ResponseEntity<>(new byte[0], HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
