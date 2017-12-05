package com.stonymoon.bubble.util;


public class UrlUtil {
    public static String getCreateUser() {
        return "user/create";
    }

    public static String getImageToken() {
        return "user/image/imageToken";
    }

    public static String getImageUpload() {
        return "user/image/imageUpload";
    }

    public static String getLogin() {
        return "user/login";

    }

    public static String getUserInfo(String uid) {
        return "user/" + uid;
    }

    public static String getBubbleComments(String id, String pageNum) {
        return "bubbles/comment/" + id + "/" + pageNum;
    }

    public static String postBubbleComments() {
        return "bubbles/comment";
    }

    public static String guestGetBubble(String uid) {
        return "bubbles/guest/" + uid;
    }

    public static String getMapBubble() {
        return "bubbles/map";
    }

    public static String getShare() {
        return "bubbles/share";
    }

    public static String getBubbleAddTime() {
        return "bubbles/time/add";

    }

    public static String getUserBubble(String uid) {
        return "bubbles/user/" + uid;
    }

    public static String getBubbleDetail(String id) {
        return "bubbles/" + id;
    }


}
