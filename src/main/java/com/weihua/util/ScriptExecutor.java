package com.weihua.util;

import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Arrays;

@Component
public class ScriptExecutor {
    
    private static final String SCRIPT_PATH = "change_device.sh";
    private static final int SCRIPT_TIMEOUT = 30000;
    
    public ScriptExecutionResult executeChangePasswordScript(String deviceIp, String username, 
                                                            String oldPassword, String newPassword) {
        String[] command = buildCommand(deviceIp, username, oldPassword, newPassword);
        
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            
            int exitCode = process.waitFor();
            
            return new ScriptExecutionResult(exitCode == 0, exitCode, output.toString());
            
        } catch (IOException e) {
            return new ScriptExecutionResult(false, -1, "Script execution failed: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new ScriptExecutionResult(false, -1, "Script execution interrupted");
        }
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
        try {
            ProcessBuilder pb = new ProcessBuilder("test", "-f", SCRIPT_PATH);
            Process p = pb.start();
            return p.waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
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
