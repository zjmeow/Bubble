package com.stonymoon.bubble.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 用于显示用户在地图上的位置
 */

public class UserBean {
    /**
     * code : 0
     * message : success
     * result : [{"longitude":0,"latitude":0,"timestamp":123456789,"user_image":"http://i0.hdslb.com/bfs/bangumi/8870e00ca6d3d38eed52bde82e4a84df8073973b.jpg","user_name":"血色苍穹","user_id":"123"},{"longitude":0,"latitude":0,"timestamp":123456789,"user_image":"http://i0.hdslb.com/bfs/bangumi/8870e00ca6d3d38eed52bde82e4a84df8073973b.jpg","user_name":"血色苍穹","user_id":"123"}]
     */

    private List<ResultBean> result;

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * longitude : 0
         * latitude : 0
         * timestamp : 123456789
         * user_image : http://i0.hdslb.com/bfs/bangumi/8870e00ca6d3d38eed52bde82e4a84df8073973b.jpg
         * user_name : 血色苍穹
         * user_id : 123
         */

        private int longitude;
        private int latitude;
        private int timestamp;
        @SerializedName("user_image")
        private String userImage;
        @SerializedName("user_name")
        private String userName;
        @SerializedName("user_id")
        private String userId;

        public int getLongitude() {
            return longitude;
        }

        public void setLongitude(int longitude) {
            this.longitude = longitude;
        }

        public int getLatitude() {
            return latitude;
        }

        public void setLatitude(int latitude) {
            this.latitude = latitude;
        }

        public int getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(int timestamp) {
            this.timestamp = timestamp;
        }

        public String getUserImage() {
            return userImage;
        }

        public void setUserImage(String userImage) {
            this.userImage = userImage;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
}
