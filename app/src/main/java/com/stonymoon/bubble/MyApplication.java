package com.stonymoon.bubble;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2017/11/12.
 */

public class MyApplication extends Application {
    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        context = getApplicationContext();
        super.onCreate();
    }


}
