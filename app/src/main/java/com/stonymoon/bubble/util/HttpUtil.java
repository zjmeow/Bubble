package com.stonymoon.bubble.util;

import android.content.Context;

import com.tamic.novate.Novate;

/**
 * Created by A on 2017/10/18.
 */

public class HttpUtil {
    private HttpUtil() {
    }

    public static Novate sendHttpRequest(Context context) {
        Novate novate = new Novate.Builder(context).baseUrl("http://192.168.191.1:8080/api/v1/").build();
        return novate;
    }

//http://120.24.238.200:5678/
}
