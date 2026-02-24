package com.example.demo.dto.Request;

import lombok.Data;

@Data
public class SearchOrder {
    private String field;
    private String order_type;
}