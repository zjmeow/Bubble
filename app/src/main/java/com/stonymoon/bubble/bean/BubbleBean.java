package com.stonymoon.bubble.bean;

import java.io.Serializable;
import java.util.List;


public class BubbleBean {

    /**
     * code : 1
     * message : 返回内容：
     * content : [{"id":179,"uid":42,"title":"标题要长才能看","time":1511872465000,"latitude":26.056899,"longitude":119.198834,"image":"http://oupl6wdxc.bkt.clouddn.com/EIEELKFHJFHLM","deadline":1512131665000,"anonymous":0,"click":0,"type":0,"comments":"","content":"UI好难调啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊内容要长才能看","miniUser":{"username":"测试20号","phone":"13101411920","image":"http://oupl6wdxc.bkt.clouddn.com/AELFGLMGFHG1511788848933"}}]
     */

    private int code;
    private String message;
    private List<ContentBean> content;

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

    public List<ContentBean> getContent() {
        return content;
    }

    public void setContent(List<ContentBean> content) {
        this.content = content;
    }

    public static class ContentBean implements Serializable {
        /**
         * id : 179
         * uid : 42
         * title : 标题要长才能看
         * time : 1511872465000
         * latitude : 26.056899
         * longitude : 119.198834
         * image : http://oupl6wdxc.bkt.clouddn.com/EIEELKFHJFHLM
         * deadline : 1512131665000
         * anonymous : 0
         * click : 0
         * type : 0
         * comments :
         * content : UI好难调啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊内容要长才能看
         * miniUser : {"username":"测试20号","phone":"13101411920","image":"http://oupl6wdxc.bkt.clouddn.com/AELFGLMGFHG1511788848933"}
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

        public static class MiniUserBean implements Serializable {
            /**
             * username : 测试20号
             * phone : 13101411920
             * image : http://oupl6wdxc.bkt.clouddn.com/AELFGLMGFHG1511788848933
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
