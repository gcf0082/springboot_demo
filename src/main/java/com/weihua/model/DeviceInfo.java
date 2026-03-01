package com.weihua.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceInfo {
    private String deviceIp;
    private String deviceName;
    private String deviceType;
    private String status;
    private String location;
    private LocalDateTime lastModified;
    private String managedBy;
}
