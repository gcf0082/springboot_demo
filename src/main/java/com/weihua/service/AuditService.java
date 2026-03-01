package com.weihua.service;

import com.weihua.model.OperationLog;
import com.weihua.repository.OperationLogRepository;
import com.weihua.util.SecurityLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditService {
    
    @Autowired
    private OperationLogRepository operationLogRepository;
    
    @Autowired
    private SecurityLogger securityLogger;
    
    public void recordSuccessfulOperation(String operatorId, String operationType, String target) {
        OperationLog log = new OperationLog();
        log.setOperatorId(operatorId);
        log.setOperationType(operationType);
        log.setTargetDevice(target);
        log.setOperationStatus("SUCCESS");
        log.setOperationTime(LocalDateTime.now());
        log.setResult("Operation completed successfully");
        
        operationLogRepository.saveOperationLog(log);
        securityLogger.logSecurityEvent("AUDIT", operatorId, "Successful operation: " + operationType);
    }
    
    public void recordFailedOperation(String operatorId, String operationType, String target, String reason) {
        OperationLog log = new OperationLog();
        log.setOperatorId(operatorId);
        log.setOperationType(operationType);
        log.setTargetDevice(target);
        log.setOperationStatus("FAILED");
        log.setOperationTime(LocalDateTime.now());
        log.setResult("Failed: " + reason);
        
        operationLogRepository.saveOperationLog(log);
        securityLogger.logSecurityEvent("AUDIT_FAILURE", operatorId, "Failed operation: " + operationType + " - " + reason);
    }
    
    public void recordOperationAttempt(String operatorId, String operationType, String target) {
        OperationLog log = new OperationLog();
        log.setOperatorId(operatorId);
        log.setOperationType(operationType);
        log.setTargetDevice(target);
        log.setOperationStatus("ATTEMPTED");
        log.setOperationTime(LocalDateTime.now());
        
        operationLogRepository.saveOperationLog(log);
    }
    
    public List<OperationLog> getOperationHistory(String operatorId) {
        return operationLogRepository.findByOperatorId(operatorId);
    }
    
    public List<OperationLog> getDeviceHistory(String deviceIp) {
        return operationLogRepository.findByDeviceIp(deviceIp);
    }
}
