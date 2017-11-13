package com.stonymoon.bubble.util;


import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.stonymoon.bubble.MyApplication;
import com.stonymoon.bubble.bean.ContentBean;
import com.stonymoon.bubble.ui.SelectPhotoActivity;
import com.tamic.novate.callback.RxStringCallback;

//用于储存本地用户信息
public class AuthUtil {
    private AuthUtil() {

    }

    public static String getToken() {
        SharedPreferences sharedPreferences = MyApplication.getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        return token;
    }


}
