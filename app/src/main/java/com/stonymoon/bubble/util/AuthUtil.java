package com.stonymoon.bubble.util;


import android.content.Context;
import android.content.SharedPreferences;

import com.stonymoon.bubble.application.MyApplication;
import com.stonymoon.bubble.base.ActivityCollector;

import cn.jpush.im.android.api.JMessageClient;

import static android.content.Context.MODE_PRIVATE;

//用于储存本地用户信息
public class AuthUtil {
    private static Context mContext = MyApplication.getContext();
    private static SharedPreferences sharedPreferences = mContext.getSharedPreferences("login", MODE_PRIVATE);

    private AuthUtil() {

    }

    public static String getToken() {
        String token = sharedPreferences.getString("token", "");
        return token;
    }

    /**
     * 保存用户信息
     * 参数不能为空
     *
     * @param phone
     * @param password
     * @param token
     * @param id
     * @throws IllegalArgumentException
     */
    public static void saveUser(String phone, String password, String token, String id) throws IllegalArgumentException {
        if (phone == null || password == null || token == null || id == null) {
            throw new IllegalArgumentException("缺少登录必要的参数");
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phone", phone);
        editor.putString("password", password);
        editor.putString("token", token);
        editor.putString("id", id);
        //重新拿到locationId
        editor.putString("locationId", "");
        editor.apply();

    }


    public static String getPhone() {
        return sharedPreferences.getString("phone", "");
    }

    public static String getPassword() {
        return sharedPreferences.getString("password", "");

    }

    public static String getId() {
        return sharedPreferences.getString("id", "");
    }


    public static String getLocationId() {
        return sharedPreferences.getString("locationId", "");
    }

    public static void setLocationId(String locationId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("locationId", locationId);
        editor.apply();
    }

    public static void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phone", "");
        editor.putString("password", "");
        editor.putString("token", "");
        editor.putString("id", "");
        //重新拿到locationId
        editor.putString("locationId", "");
        editor.apply();
        JMessageClient.logout();


    }
}
