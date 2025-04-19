package com.only.www.onlyseclabs.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

public class SSRFTool {
    public static String sendRequest_1(String urlStr) throws IOException {
        // 确保用户输入的是合法的 URL
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // 连接超时和读取超时
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        // 发送请求并获取响应
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("HTTP request failed with response code " + responseCode);
        }

        // 读取响应内容
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line).append("\n");
        }
        reader.close();

        return response.toString();
    }
    public static int sendRequest_2(String urlStr) throws IOException {
        // 禁止访问本地地址（localhost, 127.0.0.1等）
        if (urlStr.contains("localhost") || urlStr.contains("127.0.0.1")) {
            throw new IOException("Access to localhost is not allowed");
        }

        // 向目标 URL 发送请求
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        // 获取 HTTP 响应码
        int responseCode = connection.getResponseCode();

        // 不回显响应内容，执行后端操作而不返回
        connection.getInputStream().close(); // 忽略输入流

        // 返回状态码
        return responseCode;
    }
    public static String sendDictRequest_3(String urlStr) throws IOException {
        // 检查协议是否为 dict
        if (!urlStr.startsWith("dict://")) {
            throw new IOException("Only dict protocol is allowed.");
        }

        // 提取主机和端口
        String host = urlStr.substring(7, urlStr.lastIndexOf(":"));
        int port = Integer.parseInt(urlStr.substring(urlStr.lastIndexOf(":") + 1));

        // 连接到字典服务器并获取响应
        try (Socket socket = new Socket(host, port);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             OutputStream writer = socket.getOutputStream()) {

            // 发送查询命令
            String query = "DEFINE example";  // 示例查询词汇
            writer.write((query + "\n").getBytes());
            writer.flush();

            // 获取并返回第一行响应，通常是服务的 banner
            String banner = reader.readLine();  // 读取第一行，即 banner
            return banner != null ? banner : "No banner received.";
        } catch (IOException e) {
            throw new IOException("Failed to connect to dict server: " + e.getMessage());
        }
    }
    }
