package com.weihua.util;

import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

@Component
public class CommandExecutor {
    
    public CommandResult execute(String[] command) {
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
            
            return new CommandResult(exitCode == 0, exitCode, output.toString());
            
        } catch (IOException e) {
            return new CommandResult(false, -1, "Command execution failed: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new CommandResult(false, -1, "Command execution interrupted");
        }
    }
    
    public static class CommandResult {
        private final boolean success;
        private final int exitCode;
        private final String output;
        
        public CommandResult(boolean success, int exitCode, String output) {
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
