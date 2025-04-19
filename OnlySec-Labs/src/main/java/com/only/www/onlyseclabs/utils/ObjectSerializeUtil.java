package com.only.www.onlyseclabs.utils;

import java.io.*;

public class ObjectSerializeUtil {

    /**
     * 将对象序列化为字节数组
     * @param object 要序列化的对象
     * @return 序列化后的字节数组
     * @throws IOException 如果序列化时发生错误
     */
    public static byte[] serializeToBytes(Object object) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(object);
            return byteArrayOutputStream.toByteArray();
        }
    }

    /**
     * 将对象序列化到文件
     * @param object 要序列化的对象
     * @param filePath 保存的文件路径
     * @throws IOException 如果序列化时发生错误
     */
    public static void serializeToFile(Object object, String filePath) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(object);
        }
    }
}
