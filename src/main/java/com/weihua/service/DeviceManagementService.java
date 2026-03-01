package com.weihua.service;

import com.weihua.model.ChangePasswordRequest;
import com.weihua.model.DeviceInfo;
import com.weihua.model.OperationLog;
import com.weihua.model.OperationResult;
import com.weihua.repository.DeviceRepository;
import com.weihua.repository.OperationLogRepository;
import com.weihua.security.AuthenticationService;
import com.weihua.security.AuthorizationService;
import com.weihua.util.ParameterValidator;
import com.weihua.util.ScriptExecutor;
import com.weihua.util.SecurityLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceManagementService {
    
    @Autowired
    private ParameterValidator parameterValidator;
    
    @Autowired
    private ScriptExecutor scriptExecutor;
    
    @Autowired
    private DeviceRepository deviceRepository;
    
    @Autowired
    private OperationLogRepository operationLogRepository;
    
    @Autowired
    private AuthenticationService authenticationService;
    
    @Autowired
    private AuthorizationService authorizationService;
    
    @Autowired
    private SecurityLogger securityLogger;
    
    @Autowired
    private AuditService auditService;
    
    @Autowired
    private NotificationService notificationService;
    
    public OperationResult changeDevicePassword(ChangePasswordRequest request, String operatorId) {
        securityLogger.logSecurityEvent("PASSWORD_CHANGE_START", operatorId, 
            "Attempting password change for device: " + request.getDeviceIp());
        
        ParameterValidator.ValidationResult validationResult = parameterValidator.validateChangePasswordRequest(request);
        
        if (!validationResult.isValid()) {
            securityLogger.logSecurityEvent("PASSWORD_CHANGE_FAILED", operatorId, 
                "Validation failed: " + validationResult.getErrorMessage());
            return OperationResult.failure(validationResult.getErrorMessage(), "VALIDATION_ERROR");
        }
        
        boolean authorized = performAuthorizationCheck(operatorId, request.getDeviceIp());
        if (!authorized) {
            securityLogger.logAccessDenied(operatorId, "CHANGE_PASSWORD", "Unauthorized device access");
            return OperationResult.failure("Unauthorized to change password for this device", "AUTHORIZATION_FAILED");
        }
        
        DeviceInfo device = retrieveDeviceInfo(request.getDeviceIp());
        if (device == null) {
            return OperationResult.failure("Device not found", "DEVICE_NOT_FOUND");
        }
        
        boolean deviceAccessible = checkDeviceAccessibility(request.getDeviceIp());
        if (!deviceAccessible) {
            return OperationResult.failure("Device is not accessible", "DEVICE_UNREACHABLE");
        }
        
        ScriptExecutor.ScriptExecutionResult scriptResult = executePasswordChange(
            request.getDeviceIp(),
            request.getUsername(),
            request.getOldPassword(),
            request.getNewPassword()
        );
        
        if (scriptResult.isSuccess()) {
            handleSuccessfulPasswordChange(request, operatorId, device);
            return OperationResult.success("Password changed successfully");
        } else {
            handleFailedPasswordChange(request, operatorId, scriptResult.getOutput());
            return OperationResult.failure("Failed to change password: " + scriptResult.getOutput(), "SCRIPT_EXECUTION_FAILED");
        }
    }
    
    private boolean performAuthorizationCheck(String operatorId, String deviceIp) {
        AuthorizationService.AuthorizationResult authResult = 
            authorizationService.checkAuthorization(operatorId, "CHANGE_PASSWORD");
        
        if (!authResult.isAuthorized()) {
            return false;
        }
        
        return deviceRepository.isDeviceManagedBy(deviceIp, operatorId) || 
               "admin".equals(operatorId);
    }
    
    private DeviceInfo retrieveDeviceInfo(String deviceIp) {
        return deviceRepository.findDeviceByIp(deviceIp).orElse(null);
    }
    
    private boolean checkDeviceAccessibility(String deviceIp) {
        return parameterValidator.isInternalIp(deviceIp) || deviceRepository.deviceExists(deviceIp);
    }
    
    private ScriptExecutor.ScriptExecutionResult executePasswordChange(String deviceIp, String username, 
                                                                       String oldPassword, String newPassword) {
        return scriptExecutor.executeChangePasswordScript(deviceIp, username, oldPassword, newPassword);
    }
    
    private void handleSuccessfulPasswordChange(ChangePasswordRequest request, String operatorId, DeviceInfo device) {
        deviceRepository.updateDeviceLastModified(request.getDeviceIp());
        
        OperationLog operationLog = new OperationLog();
        operationLog.setOperatorId(operatorId);
        operationLog.setOperationType("CHANGE_PASSWORD");
        operationLog.setTargetDevice(request.getDeviceIp());
        operationLog.setOperationStatus("SUCCESS");
        operationLog.setDetails("Password changed for user: " + request.getUsername());
        operationLogRepository.saveOperationLog(operationLog);
        
        auditService.recordSuccessfulOperation(operatorId, "CHANGE_PASSWORD", request.getDeviceIp());
        
        notificationService.sendSuccessNotification(operatorId, request.getDeviceIp());
        
        securityLogger.logSecurityEvent("PASSWORD_CHANGE_SUCCESS", operatorId, 
            "Password changed for device: " + request.getDeviceIp());
    }
    
    private void handleFailedPasswordChange(ChangePasswordRequest request, String operatorId, String errorMessage) {
        OperationLog operationLog = new OperationLog();
        operationLog.setOperatorId(operatorId);
        operationLog.setOperationType("CHANGE_PASSWORD");
        operationLog.setTargetDevice(request.getDeviceIp());
        operationLog.setOperationStatus("FAILED");
        operationLog.setDetails("Failed: " + errorMessage);
        operationLogRepository.saveOperationLog(operationLog);
        
        auditService.recordFailedOperation(operatorId, "CHANGE_PASSWORD", request.getDeviceIp(), errorMessage);
        
        securityLogger.logSecurityEvent("PASSWORD_CHANGE_FAILED", operatorId, 
            "Password change failed: " + errorMessage);
    }
    
    public OperationResult validateDeviceConnection(String deviceIp, String operatorId) {
        if (!parameterValidator.isInternalIp(deviceIp) && !deviceRepository.deviceExists(deviceIp)) {
            return OperationResult.failure("Invalid IP address", "INVALID_IP");
        }
        
        boolean exists = deviceRepository.deviceExists(deviceIp);
        if (!exists) {
            return OperationResult.failure("Device not found in database", "DEVICE_UNKNOWN");
        }
        
        return OperationResult.success("Device is registered");
    }
}
