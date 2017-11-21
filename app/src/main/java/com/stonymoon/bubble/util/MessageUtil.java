package com.stonymoon.bubble.util;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;
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

    public static void sendEmoji(String phone, String emojiName) {
        Map<String, String> map = new HashMap<>();
        map.put("emoji", emojiName);
        Message message = JMessageClient.createSingleCustomMessage(phone, map);
        JMessageClient.sendMessage(message);
    }









}
