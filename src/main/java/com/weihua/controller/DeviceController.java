package com.weihua.controller;

import com.weihua.model.ChangePasswordRequest;
import com.weihua.model.OperationResult;
import com.weihua.service.AuditService;
import com.weihua.service.DeviceManagementService;
import com.weihua.util.ParameterValidator;
import com.weihua.util.SecurityLogger;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/device")
@Validated
public class DeviceController {
    
    @Autowired
    private DeviceManagementService deviceManagementService;
    
    @Autowired
    private ParameterValidator parameterValidator;
    
    @Autowired
    private SecurityLogger securityLogger;
    
    @Autowired
    private AuditService auditService;
    
    @PostMapping("/changePassword")
    @ApiOperation(value = "Change device password", 
                  notes = "Change password for a network device by executing change_device.sh script",
                  response = OperationResult.class)
    public ResponseEntity<OperationResult> changeDevicePassword(
            @ApiParam(value = "Change password request", required = true) 
            @Valid @RequestBody ChangePasswordRequest request,
            @RequestHeader(value = "X-Operator-Id", defaultValue = "anonymous") String operatorId,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken) {
        
        securityLogger.logSecurityEvent("API_REQUEST", operatorId, 
            "Password change request for device: " + request.getDeviceIp());
        
        if (!validateAuthToken(authToken)) {
            securityLogger.logAccessDenied(operatorId, "CHANGE_PASSWORD_API", "Invalid or missing auth token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(OperationResult.failure("Authentication required", "AUTH_REQUIRED"));
        }
        
        if (!validateOperatorAccess(operatorId, request.getDeviceIp())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(OperationResult.failure("Access denied to this device", "ACCESS_DENIED"));
        }
        
        OperationResult result = deviceManagementService.changeDevicePassword(request, operatorId);
        
        HttpStatus status = result.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(result);
    }
    
    @GetMapping("/validate/{deviceIp}")
    @ApiOperation(value = "Validate device", 
                  notes = "Check if device is registered and accessible",
                  response = OperationResult.class)
    public ResponseEntity<OperationResult> validateDevice(
            @ApiParam(value = "Device IP address", required = true) 
            @PathVariable String deviceIp,
            @RequestHeader(value = "X-Operator-Id", defaultValue = "anonymous") String operatorId) {
        
        OperationResult result = deviceManagementService.validateDeviceConnection(deviceIp, operatorId);
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/history/{deviceIp}")
    @ApiOperation(value = "Get device operation history", 
                  notes = "Retrieve operation history for a specific device",
                  response = OperationResult.class)
    public ResponseEntity<OperationResult> getDeviceHistory(
            @ApiParam(value = "Device IP address", required = true) 
            @PathVariable String deviceIp,
            @RequestHeader(value = "X-Operator-Id", defaultValue = "anonymous") String operatorId) {
        
        var history = auditService.getDeviceHistory(deviceIp);
        
        return ResponseEntity.ok(OperationResult.success("History retrieved", history));
    }
    
    private boolean validateAuthToken(String token) {
        return token != null && token.length() > 10;
    }
    
    private boolean validateOperatorAccess(String operatorId, String deviceIp) {
        return operatorId != null && !operatorId.isEmpty();
    }
}
