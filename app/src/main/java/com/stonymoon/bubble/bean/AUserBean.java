package com.stonymoon.bubble.bean;

/**
 * Created by Administrator on 2017/11/18.
 */

public class AUserBean {


    /**
     * code : 1
     * message : 用户信息
     * content : {"id":37,"username":"测试三号","image":"http://oupl6wdxc.bkt.clouddn.com/AELFGLMGFEH1510981299800","longitude":null,"latitude":null,"emojicount":0,"logintime":1510981299000}
     */

    private String message;
    private ContentBean content;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }

    public static class ContentBean {
        /**
         * id : 37
         * username : 测试三号
         * image : http://oupl6wdxc.bkt.clouddn.com/AELFGLMGFEH1510981299800
         * longitude : null
         * latitude : null
         * emojicount : 0
         * logintime : 1510981299000
         */

        private int id;
        private String username;
        private String image;
        private double longitude;
        private double latitude;
        private int emojicount;
        private long logintime;
        private String phone;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

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

        public int getEmojicount() {
            return emojicount;
        }

        public void setEmojicount(int emojicount) {
            this.emojicount = emojicount;
        }

        public long getLogintime() {
            return logintime;
        }

        public void setLogintime(long logintime) {
            this.logintime = logintime;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }
}
