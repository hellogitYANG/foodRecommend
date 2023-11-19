package com.example.foodrecommend.config;

public class UserLocal {
    private static final ThreadLocal<String> USER_LOCAL = new ThreadLocal<>();


    public static String getToken() {
        return USER_LOCAL.get();
    }

}
