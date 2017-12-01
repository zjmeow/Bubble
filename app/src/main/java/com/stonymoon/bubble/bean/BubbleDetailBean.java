package com.stonymoon.bubble.bean;

public class BubbleDetailBean {


    /**
     * code : 1
     * message : 内容：
     * content : {"id":176,"uid":39,"title":"啊啊啊","time":1511785426000,"latitude":26.056992,"longitude":119.19907,"image":"http://oupl6wdxc.bkt.clouddn.com/EIEEKLIHFFKIF","deadline":1512044626000,"anonymous":1,"click":0,"type":0,"content":"哦哦哦"}
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
         * id : 176
         * uid : 39
         * title : 啊啊啊
         * time : 1511785426000
         * latitude : 26.056992
         * longitude : 119.19907
         * image : http://oupl6wdxc.bkt.clouddn.com/EIEEKLIHFFKIF
         * deadline : 1512044626000
         * anonymous : 1
         * click : 0
         * type : 0
         * content : 哦哦哦
         */

        private int id;
        private int uid;
        private String title;
        private long time;
        private double latitude;
        private double longitude;
        private String image;
        private long deadline;
        private int anonymous;
        private int click;
        private int type;
        private String content;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public long getDeadline() {
            return deadline;
        }

        public void setDeadline(long deadline) {
            this.deadline = deadline;
        }

        public int getAnonymous() {
            return anonymous;
        }

        public void setAnonymous(int anonymous) {
            this.anonymous = anonymous;
        }

        public int getClick() {
            return click;
        }

        public void setClick(int click) {
            this.click = click;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
