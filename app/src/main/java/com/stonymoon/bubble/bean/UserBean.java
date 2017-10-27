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
     * result : [{"longitude":119.18,"latitude":26.03,"timestamp":123456789,"user_image":"http://i0.hdslb.com/bfs/bangumi/8870e00ca6d3d38eed52bde82e4a84df8073973b.jpg","user_name":"血色苍穹","user_id":"123"},{"longitude":118.18,"latitude":25.02,"timestamp":123456789,"user_image":"http://i0.hdslb.com/bfs/bangumi/8870e00ca6d3d38eed52bde82e4a84df8073973b.jpg","user_name":"血色苍穹","user_id":"123"}]
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
         * longitude : 119.18
         * latitude : 26.03
         * timestamp : 123456789
         * user_image : http://i0.hdslb.com/bfs/bangumi/8870e00ca6d3d38eed52bde82e4a84df8073973b.jpg
         * user_name : 血色苍穹
         * user_id : 123
         */

        private double longitude;
        private double latitude;
        private long timestamp;
        @SerializedName("user_image")
        private String userImage;
        @SerializedName("user_name")
        private String userName;
        @SerializedName("user_id")
        private String userId;

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
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
