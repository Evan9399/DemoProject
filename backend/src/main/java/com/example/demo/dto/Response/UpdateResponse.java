package com.example.demo.dto.Response;

import lombok.Data;

@Data
public class UpdateResponse {
    private boolean success;
    private String message;
    private int updatedCount;
}