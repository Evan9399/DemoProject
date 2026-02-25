package com.example.demo.utils;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import dev.langchain4j.model.chat.ChatLanguageModel;

@Service
public class AiApiCaller {
    // 呼叫AI用(可替換模型，不須手動輸入)
    private final ChatLanguageModel chatModel;

    public AiApiCaller(ChatLanguageModel chatModel) {
        this.chatModel = chatModel;
    }

    // Spring Retry ,如果拋出RuntimeException,會自動重試,最大3次，間隔15s 每次*2
    @Retryable(value = RuntimeException.class, maxAttemptsExpression = "${ai.retry.max-attempts:3}", backoff = @Backoff(delayExpression = "${ai.retry.base-delay-ms:15000}", multiplier = 2))
    public String callWithRetry(String prompt) {
        String result = chatModel.generate(prompt);
        if (result == null || result.isBlank()) {
            throw new RuntimeException("AI 回傳空內容");
        }
        return result;
    }
}