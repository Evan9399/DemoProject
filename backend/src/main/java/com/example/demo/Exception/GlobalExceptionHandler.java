package com.example.demo.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.demo.dto.Response.ErrorResponse;

// 保持 Service 層的業務異常和框架異常處理
@ControllerAdvice
public class GlobalExceptionHandler {

    // --- 400 BAD REQUEST 錯誤處理 ---

    // 檢驗輸入值
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setCode("VALIDATION_FAILED");
        error.setMessage("輸入參數驗證失敗，請檢查格式或內容。");

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 檢驗輸入值正確性
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ErrorResponse> handleInvalidInputException(InvalidInputException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setCode("INVALID_INPUT");
        error.setMessage(ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 3. 處理 @RequestParam 參數缺失 (例如 URL 缺少參數)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParams(MissingServletRequestParameterException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setCode("MISSING_PARAMETER");
        // 最簡化訊息
        error.setMessage("請求缺少必要的 URL 參數。");

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // --- 404 NOT FOUND 錯誤處理 ---

    // 4. 處理 Service 層拋出的資源找不到異常 (例如：更新或刪除時 ID 不存在)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setStatus(HttpStatus.NOT_FOUND.value()); // HTTP狀態碼404
        error.setCode("RESOURCE_NOT_FOUND");// 錯誤代碼 可自定義
        // 使用 Service 層傳來的精確訊息
        error.setMessage(ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // --- 500 INTERNAL SERVER ERROR 處理 ---

    // 5. 處理所有未捕捉到的異常
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        // 🚨 這裡應記錄 ex 的 Stack Trace 日誌！
        ErrorResponse error = new ErrorResponse();
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setCode("INTERNAL_SERVER_ERROR");
        error.setMessage("伺服器內部錯誤，請聯繫管理員。");

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR); // 500
    }

    // 處理 ApiCall異常
    @ExceptionHandler(ApiCallException.class)
    public ResponseEntity<ErrorResponse> handleApiCall(ApiCallException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setStatus(HttpStatus.BAD_GATEWAY.value());
        error.setCode("API_CALL_FAILED");
        error.setMessage(ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_GATEWAY);
    }

    // JsonParseException Json異常
    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<ErrorResponse> handleJsonParse(JsonParseException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setCode("JSON_PARSE_FAILED");
        error.setMessage("JSON 解析失敗");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}