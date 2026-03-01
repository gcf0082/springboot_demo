package com.weihua.controller;

import com.weihua.model.UserLoginRequest;
import com.weihua.security.AuthenticationService;
import com.weihua.util.SecurityLogger;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private AuthenticationService authenticationService;
    
    @Autowired
    private SecurityLogger securityLogger;
    
    @PostMapping("/login")
    @ApiOperation(value = "User login", response = Map.class)
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserLoginRequest request) {
        boolean authenticated = authenticationService.authenticate(request.getUsername(), request.getPassword());
        
        Map<String, Object> response = new HashMap<>();
        if (authenticated) {
            String token = "token_" + System.currentTimeMillis();
            response.put("success", true);
            response.put("token", token);
            response.put("username", request.getUsername());
            response.put("permissions", authenticationService.getPermissions(request.getUsername()));
            securityLogger.logSecurityEvent("LOGIN", request.getUsername(), "Login successful");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Invalid credentials");
            securityLogger.logSecurityEvent("LOGIN_FAILED", request.getUsername(), "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    @GetMapping("/permissions/{username}")
    @ApiOperation(value = "Get user permissions", response = Map.class)
    public ResponseEntity<Map<String, Object>> getUserPermissions(@PathVariable String username) {
        String[] permissions = authenticationService.getPermissions(username);
        
        Map<String, Object> response = new HashMap<>();
        response.put("username", username);
        response.put("permissions", permissions);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/validate")
    @ApiOperation(value = "Validate token", response = Map.class)
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("X-Auth-Token") String token) {
        boolean valid = authenticationService.validateToken(token);
        
        Map<String, Object> response = new HashMap<>();
        response.put("valid", valid);
        
        return ResponseEntity.ok(response);
    }
}
