package com.weihua.repository;

import com.weihua.model.OperationLog;
import com.weihua.util.SecurityLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class OperationLogRepository {
    
    @Autowired
    private SecurityLogger securityLogger;
    
    private final ConcurrentHashMap<Long, OperationLog> logStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    public Long saveOperationLog(OperationLog log) {
        if (log.getId() == null) {
            log.setId(idGenerator.getAndIncrement());
        }
        if (log.getOperationTime() == null) {
            log.setOperationTime(LocalDateTime.now());
        }
        
        logStore.put(log.getId(), log);
        securityLogger.logOperation(
            log.getOperationType(),
            log.getOperatorId(),
            log.getTargetDevice(),
            log.getOperationStatus()
        );
        
        return log.getId();
    }
    
    public List<OperationLog> findByOperatorId(String operatorId) {
        List<OperationLog> results = new ArrayList<>();
        for (OperationLog log : logStore.values()) {
            if (log.getOperatorId().equals(operatorId)) {
                results.add(log);
            }
        }
        return results;
    }
    
    public List<OperationLog> findByDeviceIp(String deviceIp) {
        List<OperationLog> results = new ArrayList<>();
        for (OperationLog log : logStore.values()) {
            if (log.getTargetDevice() != null && log.getTargetDevice().equals(deviceIp)) {
                results.add(log);
            }
        }
        return results;
    }
    
    public List<OperationLog> findAll() {
        return new ArrayList<>(logStore.values());
    }
    
    public void clear() {
        logStore.clear();
    }
}
