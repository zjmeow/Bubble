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
        Novate novate = new Novate.Builder(context).baseUrl("http://123.207.26.208:9700/api/v1/").build();
        return novate;
    }

//http://120.24.238.200:5678/
}
