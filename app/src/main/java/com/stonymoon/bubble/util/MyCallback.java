package com.stonymoon.bubble.util;

import android.content.Context;
import android.widget.Toast;

import com.stonymoon.bubble.ui.MapActivity;

import cn.jpush.im.api.BasicCallback;

/**
 * Created by Administrator on 2017/11/5.
 */

public class MyCallback extends BasicCallback {
    Context mContext;

    public MyCallback(Context context) {
        this.mContext = context;
    }

    @Override
    public void gotResult(int responseCode, String responseMessage) {
        if (0 == responseCode) {
            //接收好友请求成功
        } else {
            Toast.makeText(mContext, "发送请求失败", Toast.LENGTH_SHORT);
        }
    }

}
