package com.weihua.model;

import lombok.Data;

@Data
public class DeviceConfigRequest {
    private String deviceIp;
    private String configType;
    private String configContent;
    private String operatorId;
}
