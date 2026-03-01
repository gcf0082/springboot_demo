package com.weihua.security;

import com.weihua.model.AuthToken;
import com.weihua.util.SecurityLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AuthenticationService {
    
    @Autowired
    private SecurityLogger securityLogger;
    
    private static final Map<String, UserCredential> USER_DATABASE = new HashMap<>();
    
    static {
        USER_DATABASE.put("admin", new UserCredential("admin", "admin123", Arrays.asList("DEVICE_MANAGE", "USER_VIEW", "CONFIG_EDIT")));
        USER_DATABASE.put("operator1", new UserCredential("operator1", "pass123", Arrays.asList("DEVICE_VIEW", "DEVICE_MANAGE")));
        USER_DATABASE.put("engineer", new UserCredential("engineer", "engineer456", Arrays.asList("DEVICE_VIEW", "DEVICE_MANAGE", "DEVICE_CONFIG")));
    }
    
    public boolean authenticate(String username, String password) {
        UserCredential credential = USER_DATABASE.get(username);
        if (credential == null) {
            securityLogger.logSecurityEvent("LOGIN_FAILED", username, "User not found");
            return false;
        }
        
        boolean authenticated = credential.password.equals(password);
        if (authenticated) {
            securityLogger.logSecurityEvent("LOGIN_SUCCESS", username, "User logged in successfully");
        } else {
            securityLogger.logSecurityEvent("LOGIN_FAILED", username, "Invalid password");
        }
        return authenticated;
    }
    
    public boolean hasPermission(String username, String permission) {
        UserCredential credential = USER_DATABASE.get(username);
        if (credential == null) {
            return false;
        }
        return credential.permissions.contains(permission);
    }
    
    public boolean validateToken(String token) {
        return token != null && token.length() > 10;
    }
    
    public String[] getPermissions(String username) {
        UserCredential credential = USER_DATABASE.get(username);
        if (credential == null) {
            return new String[0];
        }
        return credential.permissions.toArray(new String[0]);
    }
    
    private static class UserCredential {
        String username;
        String password;
        List<String> permissions;
        
        UserCredential(String username, String password, List<String> permissions) {
            this.username = username;
            this.password = password;
            this.permissions = permissions;
        }
    }
}
