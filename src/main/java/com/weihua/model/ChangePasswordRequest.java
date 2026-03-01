package com.weihua.model;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class ChangePasswordRequest {
    
    @NotBlank(message = "Device IP cannot be empty")
    @Pattern(regexp = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$", 
             message = "Invalid IP address format")
    private String deviceIp;
    
    @NotBlank(message = "Username cannot be empty")
    private String username;
    
    @NotBlank(message = "Old password cannot be empty")
    private String oldPassword;
    
    @NotBlank(message = "New password cannot be empty")
    private String newPassword;
    
    private String operatorId;
    private String department;
}
