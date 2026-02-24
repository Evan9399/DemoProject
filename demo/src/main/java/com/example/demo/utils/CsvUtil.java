package com.example.demo.utils;

import com.example.demo.dto.Response.DailySummaryResponse;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class CsvUtil {

    public static final byte[] UTF_BOM = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };

    private CsvUtil() {
    }

    public static byte[] convertDailySummariesToCsv(List<DailySummaryResponse> data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 寫入 BOM
        baos.write(UTF_BOM);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8))) {
            // CSV 標題列
            writer.write("日期,情緒標籤,文章數量,最後文章時間,摘要內容\r\n");

            for (DailySummaryResponse dto : data) {
                String formattedTime = dto.getLastPostTime() != null ? timeFormatter.format(dto.getLastPostTime()) : "";
                String line = String.join(",",
                        escapeCsv(dto.getDate()),
                        escapeCsv(dto.getSentimentTag()),
                        escapeCsv(String.valueOf(dto.getArticleCount())),
                        escapeCsv(formattedTime),
                        escapeCsv(dto.getSummary()));
                writer.write(line + "\r\n");
            }
        }

        return baos.toByteArray();
    }

    /**
     * CSV escape:
     * - null 轉為 ""
     * - 雙引號 " 轉成 ""
     * - 換行 \n、回車 \r 轉空格
     */
    private static String escapeCsv(String value) {
        if (value == null)
            return "\"\"";
        return "\"" + value.replace("\"", "\"\"").replace("\n", " ").replace("\r", " ") + "\"";
    }
}
