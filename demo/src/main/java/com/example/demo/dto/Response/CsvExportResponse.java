package com.example.demo.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CsvExportResponse {
    private byte[] fileContent; // 檔案內容 (CSV bytes)
    private String filename; // 建議的檔名

}