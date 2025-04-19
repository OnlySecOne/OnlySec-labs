package com.only.www.onlyseclabs.utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SystemCommandExecutor {

    /**
     * 判断当前操作系统类型
     * @return 操作系统类型，如"windows", "linux", "mac"
     */
    private static String getOS() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return "windows";
        } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            return "unix";
        }
        return "unknown";
    }

    /**
     * 执行系统命令并返回输出结果
     * @param command 要执行的系统命令
     * @return 命令的输出结果
     * @throws IOException 如果发生 I/O 错误
     * @throws InterruptedException 如果命令执行过程中被中断
     */
    public static String executeCommand(String command) throws IOException, InterruptedException {
        String os = getOS();  // 获取操作系统类型
        ProcessBuilder processBuilder;

        // 根据操作系统类型来选择正确的命令执行方式
        if ("windows".equals(os)) {
            processBuilder = new ProcessBuilder("cmd", "/c", command);  // Windows 下的命令
        } else if ("unix".equals(os)) {
            processBuilder = new ProcessBuilder("sh", "-c", command);  // Unix/Linux/Mac 下的命令
        } else {
            throw new UnsupportedOperationException("Unsupported OS: " + os);
        }

        // 启动进程
        Process process = processBuilder.start();

        // 获取命令执行的输出流
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;

        // 读取命令输出
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        // 获取命令执行的错误流
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        StringBuilder error = new StringBuilder();
        while ((line = errorReader.readLine()) != null) {
            error.append(line).append("\n");
        }

        // 等待命令执行完成
        int exitCode = process.waitFor();

        // 如果命令执行有错误，抛出异常
        if (exitCode != 0) {
            throw new IOException("Error executing command: " + error.toString());
        }

        return output.toString();
    }

}
