package com.stonymoon.bubble.bean;

import java.util.Date;

/**
 * Created by Stony on 2018/12/23.
 */

public class UserProfileBean {

    /**
     * data : {"avatar":"string","id":0,"info":"string","loginTime":"2018-12-23T12:20:39.294Z","username":"string"}
     * errorCode : string
     * message : string
     * success : true
     */

    private DataBean data;
    private String errorCode;
    private String message;
    private boolean success;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public static class DataBean {
        /**
         * avatar : string
         * id : 0
         * info : string
         * loginTime : 2018-12-23T12:20:39.294Z
         * username : string
         */

        private String avatar;
        private int id;
        private String info;
        private Date loginTime;
        private String username;

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public Date getLoginTime() {
            return loginTime;
        }

        public void setLoginTime(Date loginTime) {
            this.loginTime = loginTime;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
