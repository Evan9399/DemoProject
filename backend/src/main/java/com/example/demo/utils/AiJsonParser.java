package com.example.demo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.List;
import java.util.Map;

// JSON解析
public final class AiJsonParser {
    // 因為沒有下bean 所以要NEW --> JSON <-> JAVA
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private AiJsonParser() {
    }

    // JSON清理
    public static String cleanRaw(String raw) {
        if (raw == null)
            return "";
        String cleaned = raw.replaceAll("(?is)^```(json)?", "")
                .replaceAll("```$", "")
                .trim();
        return cleaned;
    }

    public static Map<String, String> parseAsMap(String raw) throws IOException {
        String cleaned = cleanRaw(raw);
        // JSON -> Map
        return MAPPER.readValue(cleaned, new TypeReference<Map<String, String>>() {
        });
    }

    public static List<Map<String, String>> parseAsList(String raw) throws IOException {
        String cleaned = cleanRaw(raw);
        // 轉LIST
        return MAPPER.readValue(cleaned, new TypeReference<List<Map<String, String>>>() {
        });
    }

}