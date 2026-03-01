package com.weihua.controller;

import com.weihua.model.OperationResult;
import com.weihua.repository.DeviceRepository;
import com.weihua.util.SecurityLogger;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/batch")
public class BatchOperationController {
    
    @Autowired
    private DeviceRepository deviceRepository;
    
    @Autowired
    private SecurityLogger securityLogger;
    
    private final Map<String, BatchJob> jobs = new ConcurrentHashMap<>();
    
    @PostMapping("/execute")
    @ApiOperation(value = "Execute batch operation", response = Map.class)
    public ResponseEntity<Map<String, Object>> executeBatch(
            @RequestBody BatchRequest request,
            @RequestHeader("X-Operator-Id") String operatorId) {
        
        String jobId = "job_" + System.currentTimeMillis();
        
        securityLogger.logSecurityEvent("BATCH_START", operatorId, 
            "Starting batch operation: " + request.getOperationType());
        
        BatchJob job = new BatchJob();
        job.setJobId(jobId);
        job.setOperationType(request.getOperationType());
        job.setTargetDevices(request.getDeviceIps());
        job.setStatus("RUNNING");
        job.setStartTime(LocalDateTime.now());
        
        jobs.put(jobId, job);
        
        Map<String, Object> response = new HashMap<>();
        response.put("jobId", jobId);
        response.put("status", "RUNNING");
        response.put("totalDevices", request.getDeviceIps().size());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/status/{jobId}")
    @ApiOperation(value = "Get batch job status", response = Map.class)
    public ResponseEntity<Map<String, Object>> getJobStatus(
            @PathVariable String jobId,
            @RequestHeader("X-Operator-Id") String operatorId) {
        
        BatchJob job = jobs.get(jobId);
        
        if (job == null) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> status = new HashMap<>();
        status.put("jobId", job.getJobId());
        status.put("operationType", job.getOperationType());
        status.put("status", job.getStatus());
        status.put("totalDevices", job.getTargetDevices().size());
        status.put("completedDevices", job.getCompletedDevices());
        status.put("failedDevices", job.getFailedDevices());
        status.put("startTime", job.getStartTime());
        status.put("endTime", job.getEndTime());
        
        return ResponseEntity.ok(status);
    }
    
    @GetMapping("/jobs")
    @ApiOperation(value = "List all batch jobs", response = List.class)
    public ResponseEntity<List<Map<String, Object>>> listJobs(
            @RequestHeader("X-Operator-Id") String operatorId) {
        
        List<Map<String, Object>> jobList = new ArrayList<>();
        
        for (BatchJob job : jobs.values()) {
            Map<String, Object> jobInfo = new HashMap<>();
            jobInfo.put("jobId", job.getJobId());
            jobInfo.put("operationType", job.getOperationType());
            jobInfo.put("status", job.getStatus());
            jobInfo.put("totalDevices", job.getTargetDevices().size());
            jobInfo.put("startTime", job.getStartTime());
            jobList.add(jobInfo);
        }
        
        return ResponseEntity.ok(jobList);
    }
    
    @DeleteMapping("/jobs/{jobId}")
    @ApiOperation(value = "Cancel batch job", response = OperationResult.class)
    public ResponseEntity<OperationResult> cancelJob(
            @PathVariable String jobId,
            @RequestHeader("X-Operator-Id") String operatorId) {
        
        BatchJob job = jobs.get(jobId);
        
        if (job == null) {
            return ResponseEntity.ok(OperationResult.failure("Job not found", "JOB_NOT_FOUND"));
        }
        
        if ("COMPLETED".equals(job.getStatus()) || "FAILED".equals(job.getStatus())) {
            return ResponseEntity.ok(OperationResult.failure("Job already finished", "INVALID_STATE"));
        }
        
        job.setStatus("CANCELLED");
        job.setEndTime(LocalDateTime.now());
        
        securityLogger.logSecurityEvent("BATCH_CANCEL", operatorId, "Cancelled job: " + jobId);
        
        return ResponseEntity.ok(OperationResult.success("Job cancelled"));
    }
    
    public static class BatchRequest {
        private String operationType;
        private List<String> deviceIps;
        
        public String getOperationType() { return operationType; }
        public void setOperationType(String operationType) { this.operationType = operationType; }
        public List<String> getDeviceIps() { return deviceIps; }
        public void setDeviceIps(List<String> deviceIps) { this.deviceIps = deviceIps; }
    }
    
    public static class BatchJob {
        private String jobId;
        private String operationType;
        private List<String> targetDevices;
        private String status;
        private int completedDevices;
        private int failedDevices;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        
        public String getJobId() { return jobId; }
        public void setJobId(String jobId) { this.jobId = jobId; }
        public String getOperationType() { return operationType; }
        public void setOperationType(String operationType) { this.operationType = operationType; }
        public List<String> getTargetDevices() { return targetDevices; }
        public void setTargetDevices(List<String> targetDevices) { this.targetDevices = targetDevices; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public int getCompletedDevices() { return completedDevices; }
        public void setCompletedDevices(int completedDevices) { this.completedDevices = completedDevices; }
        public int getFailedDevices() { return failedDevices; }
        public void setFailedDevices(int failedDevices) { this.failedDevices = failedDevices; }
        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    }
}
