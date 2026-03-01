package com.weihua.controller;

import com.weihua.model.DeviceConfigRequest;
import com.weihua.model.OperationResult;
import com.weihua.repository.DeviceRepository;
import com.weihua.util.SecurityLogger;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/device/config")
public class DeviceConfigController {
    
    @Autowired
    private DeviceRepository deviceRepository;
    
    @Autowired
    private SecurityLogger securityLogger;
    
    @PostMapping("/update")
    @ApiOperation(value = "Update device configuration", response = OperationResult.class)
    public ResponseEntity<OperationResult> updateConfig(
            @RequestBody DeviceConfigRequest request,
            @RequestHeader("X-Operator-Id") String operatorId) {
        
        securityLogger.logSecurityEvent("CONFIG_UPDATE", operatorId, 
            "Updating config for device: " + request.getDeviceIp());
        
        if (!deviceRepository.deviceExists(request.getDeviceIp())) {
            return ResponseEntity.ok(OperationResult.failure("Device not found", "DEVICE_NOT_FOUND"));
        }
        
        boolean success = updateDeviceConfiguration(request);
        
        if (success) {
            securityLogger.logSecurityEvent("CONFIG_UPDATE_SUCCESS", operatorId, 
                "Config updated for device: " + request.getDeviceIp());
            return ResponseEntity.ok(OperationResult.success("Configuration updated successfully"));
        } else {
            return ResponseEntity.ok(OperationResult.failure("Failed to update configuration", "UPDATE_FAILED"));
        }
    }
    
    @GetMapping("/{deviceIp}")
    @ApiOperation(value = "Get device configuration", response = Map.class)
    public ResponseEntity<Map<String, Object>> getConfig(
            @PathVariable String deviceIp,
            @RequestHeader("X-Operator-Id") String operatorId) {
        
        if (!deviceRepository.deviceExists(deviceIp)) {
            return ResponseEntity.ok(Map.of("error", "Device not found"));
        }
        
        Map<String, Object> config = new HashMap<>();
        config.put("deviceIp", deviceIp);
        config.put("configType", "network");
        config.put("lastModified", System.currentTimeMillis());
        
        return ResponseEntity.ok(config);
    }
    
    @PostMapping("/backup")
    @ApiOperation(value = "Backup device configuration", response = OperationResult.class)
    public ResponseEntity<OperationResult> backupConfig(
            @RequestParam String deviceIp,
            @RequestHeader("X-Operator-Id") String operatorId) {
        
        boolean exists = deviceRepository.deviceExists(deviceIp);
        if (!exists) {
            return ResponseEntity.ok(OperationResult.failure("Device not found", "DEVICE_NOT_FOUND"));
        }
        
        String backupId = "backup_" + System.currentTimeMillis();
        securityLogger.logSecurityEvent("CONFIG_BACKUP", operatorId, 
            "Backing up config for device: " + deviceIp);
        
        return ResponseEntity.ok(OperationResult.success("Configuration backed up", backupId));
    }
    
    @PostMapping("/restore")
    @ApiOperation(value = "Restore device configuration", response = OperationResult.class)
    public ResponseEntity<OperationResult> restoreConfig(
            @RequestParam String deviceIp,
            @RequestParam String backupId,
            @RequestHeader("X-Operator-Id") String operatorId) {
        
        securityLogger.logSecurityEvent("CONFIG_RESTORE", operatorId, 
            "Restoring config for device: " + deviceIp + " from backup: " + backupId);
        
        return ResponseEntity.ok(OperationResult.success("Configuration restored successfully"));
    }
    
    private boolean updateDeviceConfiguration(DeviceConfigRequest request) {
        return true;
    }
}
