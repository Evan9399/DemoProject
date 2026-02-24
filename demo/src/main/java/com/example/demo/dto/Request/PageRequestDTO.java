package com.example.demo.dto.Request;

import lombok.Data;

@Data
public class PageRequestDTO {
    private SummaryInformation summary_information; // 文章搜尋條件
    private UserInformation user_information;// 帳號，密碼，Token等資訊
}