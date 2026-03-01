package com.weihua.controller;

import com.weihua.model.DeviceInfo;
import com.weihua.repository.DeviceRepository;
import com.weihua.util.SecurityLogger;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/device/monitor")
public class DeviceMonitorController {
    
    @Autowired
    private DeviceRepository deviceRepository;
    
    @Autowired
    private SecurityLogger securityLogger;
    
    @GetMapping("/list")
    @ApiOperation(value = "List all devices", response = List.class)
    public ResponseEntity<List<DeviceInfo>> listDevices(
            @RequestParam(required = false) String status,
            @RequestHeader("X-Operator-Id") String operatorId) {
        
        List<DeviceInfo> devices = new ArrayList<>();
        deviceRepository.findDeviceByIp("192.168.1.100").ifPresent(devices::add);
        deviceRepository.findDeviceByIp("192.168.1.101").ifPresent(devices::add);
        deviceRepository.findDeviceByIp("10.0.0.50").ifPresent(devices::add);
        
        if (status != null && !status.isEmpty()) {
            devices = devices.stream()
                .filter(d -> status.equals(d.getStatus()))
                .collect(Collectors.toList());
        }
        
        return ResponseEntity.ok(devices);
    }
    
    @GetMapping("/{deviceIp}/stats")
    @ApiOperation(value = "Get device statistics", response = Map.class)
    public ResponseEntity<Map<String, Object>> getDeviceStats(
            @PathVariable String deviceIp,
            @RequestHeader("X-Operator-Id") String operatorId) {
        
        if (!deviceRepository.deviceExists(deviceIp)) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("deviceIp", deviceIp);
        stats.put("cpu", new Random().nextInt(80) + 10);
        stats.put("memory", new Random().nextInt(70) + 20);
        stats.put("bandwidth", new Random().nextInt(500) + 100);
        stats.put("packetsIn", new Random().nextInt(10000));
        stats.put("packetsOut", new Random().nextInt(10000));
        stats.put("errors", new Random().nextInt(10));
        stats.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(stats);
    }
    
    @PostMapping("/{deviceIp}/reboot")
    @ApiOperation(value = "Reboot device", response = Map.class)
    public ResponseEntity<Map<String, Object>> rebootDevice(
            @PathVariable String deviceIp,
            @RequestHeader("X-Operator-Id") String operatorId) {
        
        securityLogger.logSecurityEvent("DEVICE_REBOOT", operatorId, 
            "Rebooting device: " + deviceIp);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Device reboot initiated");
        response.put("deviceIp", deviceIp);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/alerts")
    @ApiOperation(value = "Get device alerts", response = List.class)
    public ResponseEntity<List<Map<String, Object>>> getAlerts(
            @RequestParam(required = false) String level,
            @RequestHeader("X-Operator-Id") String operatorId) {
        
        List<Map<String, Object>> alerts = new ArrayList<>();
        
        Map<String, Object> alert1 = new HashMap<>();
        alert1.put("id", "alert_001");
        alert1.put("deviceIp", "192.168.1.100");
        alert1.put("level", "WARNING");
        alert1.put("message", "High CPU usage detected");
        alert1.put("timestamp", LocalDateTime.now());
        alerts.add(alert1);
        
        Map<String, Object> alert2 = new HashMap<>();
        alert2.put("id", "alert_002");
        alert2.put("deviceIp", "192.168.1.101");
        alert2.put("level", "INFO");
        alert2.put("message", "Configuration changed");
        alert2.put("timestamp", LocalDateTime.now());
        alerts.add(alert2);
        
        if (level != null && !level.isEmpty()) {
            alerts = alerts.stream()
                .filter(a -> level.equals(a.get("level")))
                .collect(Collectors.toList());
        }
        
        return ResponseEntity.ok(alerts);
    }
}
