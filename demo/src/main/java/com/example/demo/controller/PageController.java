package com.example.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.Request.DeleteRequest;
import com.example.demo.dto.Request.PageFilterRequest;
import com.example.demo.dto.Request.UpdateRequest;
import com.example.demo.dto.Response.DeleteResponse;
import com.example.demo.dto.Response.FetchResponse;
import com.example.demo.dto.Response.UpdateResponse;
import com.example.demo.entity.PageEntity;
import com.example.demo.service.ApiService;
import com.example.demo.service.FetchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Page/文章頁面 APIs", description = "文章資料的 CRUD 及擷取操作") // UI介面 - 分組標籤
@RestController // controller + responsebody 全部都為JSON格式
@RequestMapping("/page")
@RequiredArgsConstructor
// @ApiResponses({ // 全域對應碼
// @ApiResponse(responseCode = "400", description = "無效輸入", content =
// @Content(schema = @Schema(implementation = ErrorResponse.class))),
// @ApiResponse(responseCode = "404", description = "找不到資源", content =
// @Content(schema = @Schema(implementation = ErrorResponse.class))),
// @ApiResponse(responseCode = "500", description = "伺服器內部錯誤", content =
// @Content(schema = @Schema(implementation = ErrorResponse.class)))
// })
public class PageController {

    private final ApiService apiService;
    private final FetchService fetchService;

    /**
     * POST /page/fetch
     * 呼叫外部 API 擷取資料並儲存至資料庫，不須任何參數。
     * 
     * @return 外部 API 的 JSON 響應內容
     */
    @Operation(summary = "新增最新文章", description = "呼叫外部 API 並將最新文章寫入資料庫。")
    @ApiResponse(responseCode = "200", description = "新增成功", content = @Content(schema = @Schema(implementation = FetchResponse.class)))
    @GetMapping("/fetchNewPage")
    public ResponseEntity<FetchResponse> fetchAndSaveData() throws Exception {
        // 呼叫 Service 執行業務邏輯和 API 存取
        FetchResponse response = fetchService.fetchAndSave(); // 發生 RuntimeException 自動被 GlobalExceptionHandler 捕捉
        // 返回 200 OK 或者 handle exception
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "讀取文章", description = "根據 sentimentTag 讀取文章")
    @ApiResponse(responseCode = "200", description = "讀取成功")
    @GetMapping("/getPagesByTag/{sentimentTag}")
    public List<PageEntity> findBySentimentTag(@PathVariable String sentimentTag) {
        return apiService.findBySentimentTag(sentimentTag);
    }

    @Operation(summary = "時間段讀取文章", description = "根據 sentimentTag + 時間區間查詢文章")
    @ApiResponse(responseCode = "200", description = "查詢成功")
    @PostMapping("/findPages/{sentimentTag}")
    public List<PageEntity> findPages(@PathVariable String sentimentTag, @RequestBody PageFilterRequest request) {
        return apiService.findPages(sentimentTag, request);
    }

    @Operation(summary = "刪除文章", description = "根據 sentimentTag + 時間區間刪除文章")
    @ApiResponse(responseCode = "200", description = "刪除成功", content = @Content(schema = @Schema(implementation = DeleteResponse.class)))
    @DeleteMapping("/delete/{sentimentTag}")
    public DeleteResponse deleteBySentimentTag(@PathVariable String sentimentTag, @RequestBody DeleteRequest request) {
        return apiService.deleteBySentimentTag(sentimentTag, request);
    }

    @Operation(summary = "更新文章", description = "根據 ID 部分更新文章欄位。")
    @ApiResponse(responseCode = "200", description = "更新成功", content = @Content(schema = @Schema(implementation = UpdateResponse.class)))
    @PutMapping("/update")
    public UpdateResponse updatePage(@RequestBody UpdateRequest request) {
        return apiService.updatePage(request);
    }

    // @DeleteMapping("/deleteBySentimentTagAndPostTimeBetweenAndSName")
    // public void deleteBySentimentTagAndPostTimeBetweenAndSName(String
    // sentimentTag, LocalDateTime start,
    // LocalDateTime end, String sName) {
    // apiService.deleteBySentimentTagAndPostTimeBetweenAndSName(sentimentTag,
    // start, end, sName);
    // }
}