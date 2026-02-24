package com.example.demo.utils;

import java.util.List;

import com.example.demo.entity.PageEntity;

//Prompt AI模板生成
public class AiPromptBuilder {
    public static String buildBatchPrompt(
            String template, // ai prompt
            List<PageEntity> pages, // 待摘要的文章列表
            int excerptMaxLength) {// 每篇最大摘要長度

        StringBuilder sb = new StringBuilder();
        sb.append(template).append("\n\n");
        // 空值給""
        for (PageEntity p : pages) {
            String content = p.getContent() == null ? "" : p.getContent();
            // 取content 如果太長則截斷+...
            String excerpt = content.length() > excerptMaxLength
                    ? content.substring(0, excerptMaxLength) + "..."
                    : content;

            sb.append("---ARTICLE---\n");
            // 回傳自身續接方法，排版更乾淨
            sb.append("id: ").append(p.getId()).append("\n");
            sb.append("title: ").append(p.getTitle()).append("\n");
            sb.append("content: ").append(excerpt).append("\n");
            sb.append("---END---\n\n");
        }
        // 整份資料給AI、物件轉文字
        return sb.toString();
    }

    public static String buildDailySummaryPrompt(
            String template,
            List<PageEntity> articles,
            int maxArticles,
            int maxCharsPerArticle) {

        StringBuilder sb = new StringBuilder(
                String.format(template, Math.min(articles.size(), maxArticles)));
        sb.append("\n\n以下是文章內容：\n");

        articles.stream()
                .limit(maxArticles)
                .forEach(a -> {
                    String content = a.getContent();
                    if (content != null && content.length() > maxCharsPerArticle) {
                        content = content.substring(0, maxCharsPerArticle) + "...";
                    }
                    sb.append("\n--- 文章 ID: ").append(a.getId()).append(" ---\n");
                    sb.append(content == null ? "" : content);
                });

        return sb.toString();
    }
}
