//package com.app.common;
//
//import com.app.dto.RequestDto;
//
//import java.util.Map;
//
//public class GlobalValue {
//
//    private static java.lang.ThreadLocal<RequestDto> threadLocalMap = new java.lang.ThreadLocal();
//
//    public static void set(RequestDto requestDto){
//        threadLocalMap.set(requestDto);
//    }
//
//    public static String find(String key){
//        return threadLocalMap.get()              v  .get(key);
//    }
//}
