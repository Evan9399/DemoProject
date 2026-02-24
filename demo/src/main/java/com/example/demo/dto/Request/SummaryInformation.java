package com.example.demo.dto.Request;

import java.util.List;

import lombok.Data;

@Data
public class SummaryInformation {
    private String startDate;
    private String endDate;
    private String keyword;
    private Integer pageSize;
    private Boolean enableAiSummary; // 控制是否要呼叫 AI
    private String search_topic;
    private String time_range;
    private String search_source;
    private List<SearchOrder> search_order;
}