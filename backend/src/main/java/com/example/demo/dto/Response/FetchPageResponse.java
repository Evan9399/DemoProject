package com.example.demo.dto.Response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FetchPageResponse {
    @JsonProperty("id")
    private String id;
    @JsonProperty("title")
    private String title;
    @JsonProperty("content")
    private String content;
    @JsonProperty("s_name")
    private String sName;
    @JsonProperty("s_area_name")
    private String sAreaName;
    @JsonProperty("page_url")
    private String pageUrl;
    @JsonProperty("post_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String postTime;
    @JsonProperty("author")
    private String author;
    @JsonProperty("main_id")
    private String mainId;
    @JsonProperty("positive_perc")
    private String positivePerc;
    @JsonProperty("nagative_perc")
    private String nagativePerc;
    @JsonProperty("comment_count")
    private Integer commentCount;
    @JsonProperty("view_count")
    private Integer viewCount;
    @JsonProperty("used_count")
    private Integer usedCount;
    @JsonProperty("like_count")
    private String contentType;
    @JsonProperty("sentiment_tag")
    private String sentimentTag;
    @JsonProperty("hit_num")
    private Integer hitNum;
    @JsonProperty("article_type")
    private String articleType;
    @JsonProperty("summary")
    private String summary;
}