package com.weihua.security;

import com.weihua.util.SecurityLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationService {
    
    @Autowired
    private SecurityLogger securityLogger;
    
    @Autowired
    private AuthenticationService authenticationService;
    
    private static final String REQUIRED_PERMISSION = "DEVICE_MANAGE";
    
    public AuthorizationResult checkAuthorization(String username, String operation) {
        if (username == null || username.isEmpty()) {
            securityLogger.logAccessDenied(username, operation, "Empty username");
            return AuthorizationResult.denied("User not authenticated");
        }
        
        boolean authenticated = authenticationService.hasPermission(username, REQUIRED_PERMISSION);
        
        if (!authenticated) {
            securityLogger.logAccessDenied(username, operation, "Insufficient permissions");
            return AuthorizationResult.denied("User lacks required permission: " + REQUIRED_PERMISSION);
        }
        
        return AuthorizationResult.authorized();
    }
    
    public boolean checkDepartmentAccess(String username, String targetDepartment) {
        if (username.equals("admin")) {
            return true;
        }
        
        if (username.equals("operator1") && "OPS".equals(targetDepartment)) {
            return true;
        }
        
        if (username.equals("engineer") && "TECH".equals(targetDepartment)) {
            return true;
        }
        
        return false;
    }
    
    public static class AuthorizationResult {
        private final boolean authorized;
        private final String reason;
        
        private AuthorizationResult(boolean authorized, String reason) {
            this.authorized = authorized;
            this.reason = reason;
        }
        
        public static AuthorizationResult authorized() {
            return new AuthorizationResult(true, null);
        }
        
        public static AuthorizationResult denied(String reason) {
            return new AuthorizationResult(false, reason);
        }
        
        public boolean isAuthorized() {
            return authorized;
        }
        
        public String getReason() {
            return reason;
        }
    }
}
