package com.example.demo.dto.Response;

import lombok.Data;

@Data
public class DeleteResponse {
    private boolean success;
    private int deletedCount;
    private String message;
}