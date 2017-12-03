package com.stonymoon.bubble.bean;

public class BubbleDetailBean {


    /**
     * code : 1
     * message : 内容：
     * content : {"id":182,"uid":42,"title":"呀呀呀","time":1512186717000,"latitude":26.070267,"longitude":119.208032,"image":"http://oupl6wdxc.bkt.clouddn.com/EIEFELJJMLKFG","deadline":1512446157000,"anonymous":0,"click":4,"type":0,"comments":null,"content":null,"miniUser":{"username":"测试20号","phone":"13101411920","image":"http://oupl6wdxc.bkt.clouddn.com/AELFGLMGFHG1512300540962"}}
     */

    private int code;
    private String message;
    private ContentBean content;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

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
         * id : 182
         * uid : 42
         * title : 呀呀呀
         * time : 1512186717000
         * latitude : 26.070267
         * longitude : 119.208032
         * image : http://oupl6wdxc.bkt.clouddn.com/EIEFELJJMLKFG
         * deadline : 1512446157000
         * anonymous : 0
         * click : 4
         * type : 0
         * comments : null
         * content : null
         * miniUser : {"username":"测试20号","phone":"13101411920","image":"http://oupl6wdxc.bkt.clouddn.com/AELFGLMGFHG1512300540962"}
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
        private int comments;
        private String content;
        private MiniUserBean miniUser;

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

        public int getComments() {
            return comments;
        }

        public void setComments(int comments) {
            this.comments = comments;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public MiniUserBean getMiniUser() {
            return miniUser;
        }

        public void setMiniUser(MiniUserBean miniUser) {
            this.miniUser = miniUser;
        }

        public static class MiniUserBean {
            /**
             * username : 测试20号
             * phone : 13101411920
             * image : http://oupl6wdxc.bkt.clouddn.com/AELFGLMGFHG1512300540962
             */

            private String username;
            private String phone;
            private String image;

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }
        }
    }
}
