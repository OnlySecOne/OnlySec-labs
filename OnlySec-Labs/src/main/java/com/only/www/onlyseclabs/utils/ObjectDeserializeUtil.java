package com.only.www.onlyseclabs.utils;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class ObjectDeserializeUtil {

    /**
     * 从字节数组中反序列化对象
     *
     * @param bytes 字节数组
     * @return 反序列化后的对象
     * @throws IOException            如果反序列化时发生错误
     * @throws ClassNotFoundException 如果找不到类时发生错误
     */
    public static Object deserializeFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            return objectInputStream.readObject();
        }
    }

    /**
     * 从文件中反序列化对象
     *
     * @param filePath 文件路径
     * @return 反序列化后的对象
     * @throws IOException            如果反序列化时发生错误
     * @throws ClassNotFoundException 如果找不到类时发生错误
     */
    public static Object deserializeFromFile(String filePath) throws IOException, ClassNotFoundException {
        try (FileInputStream fileInputStream = new FileInputStream(filePath);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            return objectInputStream.readObject();
        }
    }
    public static Object deserializeFromAny(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            return objectInputStream.readObject();
        }
    }
}