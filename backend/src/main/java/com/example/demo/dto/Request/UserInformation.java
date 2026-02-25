package com.example.demo.dto.Request;

import lombok.Data;

@Data
public class UserInformation {
    private String service_account;
    private String user_account;
    private String token;
}