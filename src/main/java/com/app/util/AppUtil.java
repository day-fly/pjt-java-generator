package com.app.util;

public class AppUtil {
    public static String getKorean(String type) {
        switch (type) {
            case "list":
                return "리스트";
            case "insert":
                return "등록";
            case "update":
                return "수정";
            case "delete":
                return "삭제";
            default:
                return "";
        }
    }
}
