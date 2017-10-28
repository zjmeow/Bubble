package com.stonymoon.bubble.bean;

/**
 * Created by Administrator on 2017/10/28.
 */

public class LoginBean {


    /**
     * code : 1
     * message : 登陆成功！
     * content : {"id":8,"token":"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJpZFwiOjgsXCJ1c2VybmFtZVwiOlwic3Rvbnltb29uXCIsXCJwYXNzd29yZFwiOlwiJDJhJDEwJEdPMTR6cldxTlpldXhvMmVMMWQ1WXVzL1NTWUkwVTdLd1NKcWNKOHl2TFlzaVRwOENHaUdXXCIsXCJwaG9uZVwiOlwiMTMxMDE0MTE5MTFcIn0iLCJleHAiOjE1MDkyNjc4NDV9.wLb6RlxcC1_RUHHMnIfySMO-peM73bOwmzOjaV1HdAMOXlx7eNL_vXk4B8Ibmu8TnpllaTVGnvv3hDa8SRU7mQ"}
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
         * id : 8
         * token : eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJpZFwiOjgsXCJ1c2VybmFtZVwiOlwic3Rvbnltb29uXCIsXCJwYXNzd29yZFwiOlwiJDJhJDEwJEdPMTR6cldxTlpldXhvMmVMMWQ1WXVzL1NTWUkwVTdLd1NKcWNKOHl2TFlzaVRwOENHaUdXXCIsXCJwaG9uZVwiOlwiMTMxMDE0MTE5MTFcIn0iLCJleHAiOjE1MDkyNjc4NDV9.wLb6RlxcC1_RUHHMnIfySMO-peM73bOwmzOjaV1HdAMOXlx7eNL_vXk4B8Ibmu8TnpllaTVGnvv3hDa8SRU7mQ
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
