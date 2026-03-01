#!/bin/bash

DEVICE_IP=$1
USERNAME=$2
OLD_PASSWORD=$3
NEW_PASSWORD=$4

if [ -z "$DEVICE_IP" ] || [ -z "$USERNAME" ] || [ -z "$OLD_PASSWORD" ] || [ -z "$NEW_PASSWORD" ]; then
    echo "Usage: $0 <device_ip> <username> <old_password> <new_password>"
    exit 1
fi

expect <<EOF
set timeout 30
spawn ssh \$USERNAME@\$DEVICE_IP

expect {
    "yes/no" {
        send "yes\r"
        exp_continue
    }
    "password:" {
        send "\$OLD_PASSWORD\r"
    }
    timeout {
        puts "Connection timeout"
        exit 1
    }
    eof {
        puts "Connection closed"
        exit 1
    }
}

expect {
    "> $" {
        send "enable\r"
    }
    "#" {
        send "\r"
    }
    timeout {
        puts "Timeout waiting for prompt"
        exit 1
    }
}

expect "#"
send "configure terminal\r"

expect "(config)#"
send "username \$USERNAME password \$NEW_PASSWORD\r"

expect "(config)#"
send "exit\r"

expect "#"
send "write memory\r"

expect "#"
send "exit\r"

expect eof
EOF

exit $?
