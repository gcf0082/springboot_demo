package com.weihua.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationResult {
    private boolean success;
    private String message;
    private String errorCode;
    private LocalDateTime timestamp;
    private Object data;
    
    public static OperationResult success(String message) {
        return new OperationResult(true, message, null, LocalDateTime.now(), null);
    }
    
    public static OperationResult success(String message, Object data) {
        return new OperationResult(true, message, null, LocalDateTime.now(), data);
    }
    
    public static OperationResult failure(String message, String errorCode) {
        return new OperationResult(false, message, errorCode, LocalDateTime.now(), null);
    }
}
