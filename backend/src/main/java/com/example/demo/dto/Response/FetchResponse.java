package com.example.demo.dto.Response;

import java.util.List;
import lombok.Data;

@Data
public class FetchResponse {
    private FetchInfoResponse response_info;
    private List<FetchPageResponse> result;
}