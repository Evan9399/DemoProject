package com.example.demo.config;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.context.annotation.Configuration;

import com.example.demo.dto.Request.PageRequestDTO;
import com.example.demo.dto.Request.SearchOrder;
import com.example.demo.dto.Request.SummaryInformation;
import com.example.demo.dto.Request.UserInformation;

@Configuration
public class FetchPageConfig {

    // API URL
    public String getUrl() {
        return "https://capi.opview.com.tw/summary-api-dev/summary_search.jsp";
    }

    // 帳號資訊
    private final String serviceAccount = "ps_trial_001"; //
    private final String userAccount = "ps_trial_001"; //
    private final String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJwc190cmlhbF8wMDEiLCJzdWIiOiJwc190cmlhbF8wMDEiLCJ1aWQiOjEzMCwiaXNzIjoiZWxhbmQiLCJleHAiOjE3NjU1NTUxOTksImlhdCI6MTc2NDMwOTM1MX0.AsmeKDenT9HtStZ6XEObo5o9fCkYXmCLEETeVYVYhww"; //
    private final String searchTopicId = "1636"; // 搜尋主題ID
    private final String searchSources = "facebook;facebook_reply;bbs;bbs_reply"; // 搜尋來源

    // 日期（自動抓昨天 yyyy/MM/dd）
    public String generateTodayTimeRange() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String date = yesterday.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        LocalDate beforeYesterday = yesterday.minusDays(1);
        String date2 = beforeYesterday.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return date2 + " 00:00:00~" + date + " 23:59:59";
    }

    /**
     * 建立 PageRequest，自動帶入固定的 topicId 和昨日日期範圍。
     */
    public PageRequestDTO buildRequest() {
        // user_information
        UserInformation user = new UserInformation();
        user.setService_account(serviceAccount);
        user.setUser_account(userAccount);
        user.setToken(token);

        // summary_information
        SummaryInformation sum = new SummaryInformation();
        sum.setSearch_topic(searchTopicId);
        sum.setTime_range(generateTodayTimeRange());
        sum.setSearch_source(searchSources);

        // 排序條件
        SearchOrder order = new SearchOrder();
        order.setField("post_time");
        order.setOrder_type("des");

        sum.setSearch_order(List.of(order));

        // 組 PageRequest
        PageRequestDTO req = new PageRequestDTO();
        req.setUser_information(user);
        req.setSummary_information(sum);

        return req;
    }
}