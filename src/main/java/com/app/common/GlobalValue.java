package com.app.common;

import java.util.Map;

public class ThreadLocal {

    private static java.lang.ThreadLocal<Map<String,String>> threadLocalMap = new java.lang.ThreadLocal();

    public static void set(Map<String,String> map){
        threadLocalMap.set(map);
    }

    public static String find(String key){
        return threadLocalMap.get().get(key);
    }
}
