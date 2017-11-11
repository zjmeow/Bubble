package com.stonymoon.bubble.util;

import org.json.JSONObject;

import cn.jpush.im.android.api.model.Message;


public class MessageUtil {
    private MessageUtil() {
    }

    public static String getMessageText(Message message) {
        String msg = "";
        try {
            JSONObject json = new JSONObject(message.getContent().toJson());
            msg = json.getString("text");
        } catch (Exception e) {
            LogUtil.e("ChatActivity", e.toString());

        }
        return msg;

    }


}
