package com.example.demo.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FetchInfoResponse {
    private String search_id;
    private String version;
    private String errorCode;
    private String errorMessage;
    private int total_mention;
}