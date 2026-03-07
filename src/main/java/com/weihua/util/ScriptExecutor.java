package com.weihua.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScriptExecutor {
    
    private static final String SCRIPT_PATH = "/opt/change_device.sh";
    private static final int SCRIPT_TIMEOUT = 30000;
    
    private final CommandExecutor commandExecutor;
    
    @Autowired
    public ScriptExecutor(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }
    
    public ScriptExecutionResult executeChangePasswordScript(String deviceIp, String username, 
                                                            String oldPassword, String newPassword) {
        String[] command = buildCommand(deviceIp, username, oldPassword, newPassword);
        
        CommandExecutor.CommandResult result = commandExecutor.execute(command);
        return new ScriptExecutionResult(result.isSuccess(), result.getExitCode(), result.getOutput());
    }
    
    private String[] buildCommand(String deviceIp, String username, String oldPassword, String newPassword) {
        return new String[]{
            "/bin/bash",
            SCRIPT_PATH,
            deviceIp,
            username,
            oldPassword,
            newPassword
        };
    }
    
    public boolean validateScriptExists() {
        CommandExecutor.CommandResult result = commandExecutor.execute(new String[]{"test", "-f", SCRIPT_PATH});
        return result.getExitCode() == 0;
    }
    
    public static class ScriptExecutionResult {
        private final boolean success;
        private final int exitCode;
        private final String output;
        
        public ScriptExecutionResult(boolean success, int exitCode, String output) {
            this.success = success;
            this.exitCode = exitCode;
            this.output = output;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public int getExitCode() {
            return exitCode;
        }
        
        public String getOutput() {
            return output;
        }
    }
}
