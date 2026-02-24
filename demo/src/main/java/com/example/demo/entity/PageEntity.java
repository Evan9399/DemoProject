package com.example.demo.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ts_page_content")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PageEntity {
    @Id
    @Column(name = "id")
    private String id;
    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;
    @UpdateTimestamp
    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;
    @Column(name = "title")
    private String title;
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    @Column(name = "s_name")
    private String sName;
    @Column(name = "s_area_name")
    private String sAreaName;
    @Column(name = "page_url", columnDefinition = "TEXT")
    private String pageUrl;
    @Column(name = "post_time", nullable = false)
    private LocalDateTime postTime;
    @Column(name = "author")
    private String author;
    @Column(name = "main_id")
    private String mainId;
    @Column(name = "positive_perc")
    private String positivePerc;
    @Column(name = "nagative_perc")
    private String nagativePerc;
    @Column(name = "comment_count")
    private Integer commentCount;
    @Column(name = "view_count")
    private Integer viewCount;
    @Column(name = "used_count")
    private Integer usedCount;
    @Column(name = "content_type")
    private String contentType;
    @Column(name = "sentiment_tag")
    private String sentimentTag;
    @Column(name = "hit_num")
    private Integer hitNum;
    @Column(name = "article_type")
    private String articleType;
    @Column(name = "ai_summary", columnDefinition = "TEXT")
    private String aiSummary;

}
