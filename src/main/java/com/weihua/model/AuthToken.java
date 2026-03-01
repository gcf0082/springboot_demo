package com.weihua.model;

import lombok.Data;

@Data
public class AuthToken {
    private String token;
    private String userId;
    private String username;
    private long expireTime;
    private String[] permissions;
    private String department;
}
