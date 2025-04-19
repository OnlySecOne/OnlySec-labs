package com.only.www.onlyseclabs.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileReadUtil {
    /**
     * 读取文本文件并返回内容作为字符串
     * @param filePath 文件路径
     * @return 文件内容（字符串）
     * @throws IOException 如果读取文件时发生错误
     */
    public static String readFileAsString(String filePath) throws IOException {
        // 使用 Files.readString() 读取文本文件并返回内容作为字符串（适用于 Java 11+）
        return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
    }

    /**
     * 读取文本文件并将每行作为元素返回为列表
     * @param filePath 文件路径
     * @return 文件内容的列表（每行一个字符串）
     * @throws IOException 如果读取文件时发生错误
     */
    public static List<String> readFileAsLines(String filePath) throws IOException {
        return Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
    }

    /**
     * 读取二进制文件并返回字节数组
     * @param filePath 文件路径
     * @return 文件内容的字节数组
     * @throws IOException 如果读取文件时发生错误
     */
    public static byte[] readFileAsBytes(String filePath) throws IOException {
        return Files.readAllBytes(Paths.get(filePath));
    }

    /**
     * 使用 BufferedReader 逐行读取文本文件
     * @param filePath 文件路径
     * @return 文件内容（逐行读取）
     * @throws IOException 如果读取文件时发生错误
     */
    public static String readFileLineByLine(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
}
