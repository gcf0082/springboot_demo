package com.weihua.repository;

import com.weihua.model.DeviceInfo;
import com.weihua.util.SecurityLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class DeviceRepository {
    
    @Autowired
    private SecurityLogger securityLogger;
    
    private static final Map<String, DeviceInfo> DEVICE_DATABASE = new HashMap<>();
    
    static {
        DEVICE_DATABASE.put("192.168.1.100", new DeviceInfo("192.168.1.100", "Router-Core-01", "Router", "Online", "DataCenter-A", LocalDateTime.now(), "admin"));
        DEVICE_DATABASE.put("192.168.1.101", new DeviceInfo("192.168.1.101", "Switch-Access-01", "Switch", "Online", "DataCenter-A", LocalDateTime.now(), "admin"));
        DEVICE_DATABASE.put("10.0.0.50", new DeviceInfo("10.0.0.50", "Firewall-Main", "Firewall", "Online", "DataCenter-B", LocalDateTime.now(), "engineer"));
    }
    
    public Optional<DeviceInfo> findDeviceByIp(String deviceIp) {
        DeviceInfo device = DEVICE_DATABASE.get(deviceIp);
        if (device != null) {
            securityLogger.logSecurityEvent("DEVICE_LOOKUP", deviceIp, "Device found");
        } else {
            securityLogger.logSecurityEvent("DEVICE_LOOKUP", deviceIp, "Device not found");
        }
        return Optional.ofNullable(device);
    }
    
    public boolean updateDeviceLastModified(String deviceIp) {
        DeviceInfo device = DEVICE_DATABASE.get(deviceIp);
        if (device != null) {
            device.setLastModified(LocalDateTime.now());
            return true;
        }
        return false;
    }
    
    public boolean deviceExists(String deviceIp) {
        return DEVICE_DATABASE.containsKey(deviceIp);
    }
    
    public boolean isDeviceManagedBy(String deviceIp, String username) {
        DeviceInfo device = DEVICE_DATABASE.get(deviceIp);
        if (device == null) {
            return false;
        }
        return username.equals(device.getManagedBy()) || "admin".equals(username);
    }
}
