package com.weihua.service;

import com.weihua.util.SecurityLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    
    @Autowired
    private SecurityLogger securityLogger;
    
    public void sendSuccessNotification(String operatorId, String deviceIp) {
        securityLogger.logSecurityEvent("NOTIFICATION", operatorId, 
            "Success notification sent for device: " + deviceIp);
    }
    
    public void sendFailureNotification(String operatorId, String deviceIp, String reason) {
        securityLogger.logSecurityEvent("NOTIFICATION", operatorId, 
            "Failure notification sent for device: " + deviceIp + " - Reason: " + reason);
    }
    
    public void sendAlertNotification(String alertType, String message) {
        securityLogger.logSecurityEvent("ALERT", alertType, message);
    }
}
