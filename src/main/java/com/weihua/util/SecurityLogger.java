package com.weihua.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SecurityLogger {
    
    private static final Logger logger = LoggerFactory.getLogger("SECURITY");
    private static final Logger operationLogger = LoggerFactory.getLogger("OPERATION");
    
    public void logSecurityEvent(String event, String userId, String details) {
        logger.info("[SECURITY] User: {} - Event: {} - Details: {}", userId, event, details);
    }
    
    public void logAccessDenied(String userId, String resource, String reason) {
        logger.warn("[ACCESS DENIED] User: {} - Resource: {} - Reason: {}", userId, resource, reason);
    }
    
    public void logOperation(String operation, String operator, String target, String result) {
        operationLogger.info("[OPERATION] Operator: {} - Operation: {} - Target: {} - Result: {}", 
                           operator, operation, target, result);
    }
    
    public void logParameterValidation(String paramName, boolean valid, String reason) {
        logger.debug("[VALIDATION] Parameter: {} - Valid: {} - Reason: {}", paramName, valid, reason);
    }
    
    public void logScriptExecution(String scriptName, String[] params, int exitCode) {
        logger.info("[SCRIPT] Executed: {} - ExitCode: {}", scriptName, exitCode);
    }
}
