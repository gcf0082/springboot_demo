#!/bin/bash

# 修改密码接口 curl 调用脚本
# 用法: ./change_password_api.sh <device_ip> <username> <old_password> <new_password>

DEVICE_IP=$1
USERNAME=$2
OLD_PASSWORD=$3
NEW_PASSWORD=$4

if [ -z "$DEVICE_IP" ] || [ -z "$USERNAME" ] || [ -z "$OLD_PASSWORD" ] || [ -z "$NEW_PASSWORD" ]; then
    echo "用法: $0 <device_ip> <username> <old_password> <new_password>"
    exit 1
fi

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
