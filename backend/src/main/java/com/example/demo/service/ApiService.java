package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.Request.DeleteRequest;
import com.example.demo.dto.Request.PageFilterRequest;
import com.example.demo.dto.Request.UpdateRequest;
import com.example.demo.dto.Response.DeleteResponse;
import com.example.demo.dto.Response.UpdateResponse;
import com.example.demo.entity.PageEntity;

public interface ApiService {
    // TagName搜尋
    List<PageEntity> findBySentimentTag(String sentimentTag);

    // 搜尋TagNameByTime
    List<PageEntity> findPages(String sentimentTag, PageFilterRequest request);

    // 更新資料
    UpdateResponse updatePage(UpdateRequest request);

    // 刪除資料ByTime
    DeleteResponse deleteBySentimentTag(String sentimentTag, DeleteRequest request);

    // DeleteResponse deleteBySentimentTagAndPostTimeBetweenAndSName(String
    // sentimentTag, LocalDateTime start,
    // LocalDateTime end, String sName);
}