package com.example.demo.Exception;

public class ApiCallException extends RuntimeException {
    public ApiCallException(String message, Throwable cause) {
        super(message, cause);
    }
}
