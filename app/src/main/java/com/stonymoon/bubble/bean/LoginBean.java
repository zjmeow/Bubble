package com.stonymoon.bubble.bean;

/**
 * Created by Administrator on 2017/10/28.
 */

public class LoginBean {


    /**
     * code : 1
     * message : 登陆成功！
     * content : {"id":22,"token":"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJpZFwiOjIyLFwidXNlcm5hbWVcIjpcInN0b255XCIsXCJwYXNzd29yZFwiOlwiJDJhJDEwJHouZXI0VlBCa3N0VXVOL2JrY2NMQ2VrYXh3WlhaNnJjNEF2cEoubEhoeDJnNGZpaVBwSmlpXCIsXCJwaG9uZVwiOlwiMTMxMDE0MTE5MTFcIn0iLCJleHAiOjE1MDk3MDQyNDN9.IWTie_i6col1mJcmaEJ8RaXmQKxFviRJsF-ukv5K2pXjfV2NxmDHid6j87h1uCgx4BkJdJiZJjsrQE0TNSjT5Q"}
     */

    private ContentBean content;

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }

    public static class ContentBean {
        /**
         * id : 22
         * token : eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJpZFwiOjIyLFwidXNlcm5hbWVcIjpcInN0b255XCIsXCJwYXNzd29yZFwiOlwiJDJhJDEwJHouZXI0VlBCa3N0VXVOL2JrY2NMQ2VrYXh3WlhaNnJjNEF2cEoubEhoeDJnNGZpaVBwSmlpXCIsXCJwaG9uZVwiOlwiMTMxMDE0MTE5MTFcIn0iLCJleHAiOjE1MDk3MDQyNDN9.IWTie_i6col1mJcmaEJ8RaXmQKxFviRJsF-ukv5K2pXjfV2NxmDHid6j87h1uCgx4BkJdJiZJjsrQE0TNSjT5Q
         */

        private int id;
        private String token;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
