package com.weihua.util;

import com.weihua.model.ChangePasswordRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ParameterValidator {
    
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 32;
    private static final int MAX_USERNAME_LENGTH = 50;
    
    public ValidationResult validateChangePasswordRequest(ChangePasswordRequest request) {
        List<String> errors = new ArrayList<>();
        
        if (request == null) {
            errors.add("Request cannot be null");
            return new ValidationResult(false, errors);
        }
        
        if (!validateIpAddress(request.getDeviceIp())) {
            errors.add("Invalid device IP address format");
        }
        
        if (!validateUsername(request.getUsername())) {
            errors.add("Invalid username: must be 1-" + MAX_USERNAME_LENGTH + " characters");
        }
        
        if (!validatePassword(request.getOldPassword())) {
            errors.add("Old password does not meet security requirements");
        }
        
        if (!validatePassword(request.getNewPassword())) {
            errors.add("New password does not meet security requirements");
        }
        
        if (request.getOldPassword() != null && request.getNewPassword() != null 
            && request.getOldPassword().equals(request.getNewPassword())) {
            errors.add("New password must be different from old password");
        }
        
        if (isCommonPassword(request.getNewPassword())) {
            errors.add("New password is too common, please use a stronger password");
        }
        
        return new ValidationResult(errors.isEmpty(), errors);
    }
    
    private boolean validateIpAddress(String ip) {
        if (StringUtils.isBlank(ip)) {
            return false;
        }
        
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return false;
        }
        
        try {
            for (String part : parts) {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) {
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private boolean validateUsername(String username) {
        if (StringUtils.isBlank(username)) {
            return false;
        }
        if (username.length() > MAX_USERNAME_LENGTH) {
            return false;
        }
        return username.matches("^[a-zA-Z0-9_-]+$");
    }
    
    private boolean validatePassword(String password) {
        if (StringUtils.isBlank(password)) {
            return false;
        }
        if (password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
            return false;
        }
        return true;
    }
    
    private boolean isCommonPassword(String password) {
        String[] commonPasswords = {"123456", "password", "12345678", "qwerty", "admin", "letmein"};
        for (String common : commonPasswords) {
            if (common.equals(password)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isInternalIp(String ip) {
        return ip.startsWith("10.") || ip.startsWith("192.168.") || ip.startsWith("172.16.");
    }
    
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;
        
        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public List<String> getErrors() {
            return errors;
        }
        
        public String getErrorMessage() {
            return String.join("; ", errors);
        }
    }
}
