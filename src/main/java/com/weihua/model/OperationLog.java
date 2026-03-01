package com.weihua.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OperationLog {
    private Long id;
    private String operatorId;
    private String operationType;
    private String targetDevice;
    private String operationStatus;
    private String ipAddress;
    private LocalDateTime operationTime;
    private String details;
    private String result;
}
