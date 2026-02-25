package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// ... (LangChain4j 相關引入) ...

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;

@Configuration
public class AiConfig {

    @Bean
    public ChatLanguageModel chatLanguageModel(
            @Value("${google.ai.gemini.api-key}") String apiKey,
            @Value("${google.ai.gemini.model-name}") String modelName) {

        return GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(0.0) // 回復隨機性 0
                // 摘要、報表或固定格式回覆 → temperature 設 0 ~ 0.3
                // 創意寫作或對話生成 → 可以調高到 0.7 ~ 1.0 (預設0.7左右)
                .maxOutputTokens(2048) // AI最大回覆的TOKEN數 -2048 ~ 8192 tokens (預設512~1024)
                .build();
    }
}