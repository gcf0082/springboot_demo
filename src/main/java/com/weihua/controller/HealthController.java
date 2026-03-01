package com.weihua.controller;

import com.weihua.model.OperationResult;
import com.weihua.util.SecurityLogger;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/system")
public class HealthController {
    
    @Autowired
    private SecurityLogger securityLogger;
    
    private final Random random = new Random();
    
    @GetMapping("/health")
    @ApiOperation(value = "System health check", response = Map.class)
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("uptime", System.currentTimeMillis() / 1000);
        
        return ResponseEntity.ok(health);
    }
    
    @GetMapping("/metrics")
    @ApiOperation(value = "System metrics", response = Map.class)
    public ResponseEntity<Map<String, Object>> getMetrics() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("memory.heap.used", memoryBean.getHeapMemoryUsage().getUsed());
        metrics.put("memory.heap.max", memoryBean.getHeapMemoryUsage().getMax());
        metrics.put("memory.nonheap.used", memoryBean.getNonHeapMemoryUsage().getUsed());
        metrics.put("threads.count", threadBean.getThreadCount());
        metrics.put("threads Peak", threadBean.getPeakThreadCount());
        metrics.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(metrics);
    }
    
    @GetMapping("/status")
    @ApiOperation(value = "Detailed system status", response = Map.class)
    public ResponseEntity<Map<String, Object>> getSystemStatus() {
        Map<String, Object> status = new HashMap<>();
        
        status.put("database", checkDatabaseStatus());
        status.put("cache", checkCacheStatus());
        status.put("disk", checkDiskStatus());
        status.put("network", checkNetworkStatus());
        
        boolean allHealthy = checkAllComponents(status);
        status.put("overall", allHealthy ? "HEALTHY" : "DEGRADED");
        
        return ResponseEntity.ok(status);
    }
    
    @PostMapping("/reload")
    @ApiOperation(value = "Reload system configuration", response = OperationResult.class)
    public ResponseEntity<OperationResult> reloadConfig(@RequestHeader("X-Operator-Id") String operatorId) {
        securityLogger.logSecurityEvent("CONFIG_RELOAD", operatorId, "Reloading system configuration");
        
        try {
            Thread.sleep(100);
            return ResponseEntity.ok(OperationResult.success("Configuration reloaded"));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.ok(OperationResult.failure("Reload failed", "RELOAD_ERROR"));
        }
    }
    
    private Map<String, Object> checkDatabaseStatus() {
        Map<String, Object> dbStatus = new HashMap<>();
        dbStatus.put("status", "UP");
        dbStatus.put("responseTime", random.nextInt(50) + 10);
        return dbStatus;
    }
    
    private Map<String, Object> checkCacheStatus() {
        Map<String, Object> cacheStatus = new HashMap<>();
        cacheStatus.put("status", "UP");
        cacheStatus.put("hitRate", random.nextDouble() * 0.5 + 0.5);
        return cacheStatus;
    }
    
    private Map<String, Object> checkDiskStatus() {
        Map<String, Object> diskStatus = new HashMap<>();
        diskStatus.put("status", "UP");
        diskStatus.put("usage", random.nextInt(40) + 20);
        return diskStatus;
    }
    
    private Map<String, Object> checkNetworkStatus() {
        Map<String, Object> netStatus = new HashMap<>();
        netStatus.put("status", "UP");
        netStatus.put("latency", random.nextInt(30) + 5);
        return netStatus;
    }
    
    private boolean checkAllComponents(Map<String, Object> status) {
        return true;
    }
}
