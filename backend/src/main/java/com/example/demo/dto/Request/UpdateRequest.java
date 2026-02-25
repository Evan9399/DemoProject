package com.example.demo.dto.Request;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UpdateRequest {
    @Schema(example = "1", required = true)
    private String id; // 必須有，指定更新哪筆資料
    @Schema(required = true, example = "{\"title\":\"新標題\",\"content\":\"新內容\",\"sName\":\"FB粉絲團\"}")
    private Map<String, Object> fields; // 欄位名稱 → 新值

}