package com.hgt.obj;

import java.io.*;

/**
 * 利用序列化实现对象的复制
 * Created by root on 11/6/16.
 */
public class CloneUtils {
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T clone(T obj) {
        T cloneObj = null;
        try {
            //写入字节流
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream obs = new ObjectOutputStream(out);
            obs.writeObject(obj);
            obs.close();

            //分配内存，写入原始对象，生成新对象
            ByteArrayInputStream ios = new ByteArrayInputStream(out.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(ios);
            //返回生成的新对象
            cloneObj = (T) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cloneObj;
    }
}