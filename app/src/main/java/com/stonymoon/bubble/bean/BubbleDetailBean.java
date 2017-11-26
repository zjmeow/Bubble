package com.stonymoon.bubble.bean;

/**
 * Created by A on 2017/11/8.
 */

public class BubbleDetailBean {

    /**
     * resource : {"content":"test","id":240,"uid":1,"timestamp":"Wed, 08 Nov 2017 13:32:39 -0000","title":"test","image":"http://oupl6wdxc.bkt.clouddn.com/nene.png","latitude":21.4945,"longitude":60.1309}
     */

    private ResourceBean resource;

    public ResourceBean getResource() {
        return resource;
    }

    public void setResource(ResourceBean resource) {
        this.resource = resource;
    }

    public static class ResourceBean {
        /**
         * content : test
         * id : 240
         * uid : 1
         * timestamp : Wed, 08 Nov 2017 13:32:39 -0000
         * title : test
         * image : http://oupl6wdxc.bkt.clouddn.com/nene.png
         * latitude : 21.4945
         * longitude : 60.1309
         */

        private String content;

        private int id;
        private int uid;
        private String timestamp;
        private String title;
        private String image;
        private double latitude;
        private double longitude;

        public String getContent() {
            return content;
        }


        public void setContent(String content) {
            this.content = content;
        }

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

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
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
    }
}
