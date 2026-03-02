#!/bin/bash

curl -s -X POST "http://localhost:8080/api/device/changePassword" \
  -H "Content-Type: application/json" \
  -H "X-Operator-Id: admin" \
  -H "X-Auth-Token: 12345678901" \
  -u "admin:admin123" \
  -d '{"deviceIp": "192.168.1.100", "username": "admin", "oldPassword": "old123", "newPassword": "new456"}'
