#!/bin/bash

# 修改密码接口 curl 调用脚本（参数已内置）

DEVICE_IP="192.168.1.100"
USERNAME="admin"
OLD_PASSWORD="old123"
NEW_PASSWORD="new456"

curl -s -X POST "http://localhost:8080/api/device/changePassword" \
  -H "Content-Type: application/json" \
  -H "X-Operator-Id: admin" \
  -H "X-Auth-Token: 12345678901" \
  -u "admin:admin123" \
  -d "{
    \"deviceIp\": \"$DEVICE_IP\",
    \"username\": \"$USERNAME\",
    \"oldPassword\": \"$OLD_PASSWORD\",
    \"newPassword\": \"$NEW_PASSWORD\"
  }"
