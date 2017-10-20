package com.stonymoon.bubble.bean;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于显示用户在地图上的位置
 */

public class UserBean {

    @SerializedName("result")
    public List<User> users = new ArrayList<>();

    public class User {
        double longitude;
        double latitude;
        long timestamp;
        @SerializedName("user_image")
        String userImage;
        @SerializedName("user_name")
        String userName;
        @SerializedName("user_id")
        String userId;

    }


}
