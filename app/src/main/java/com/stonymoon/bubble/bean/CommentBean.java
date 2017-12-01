package com.stonymoon.bubble.bean;


import java.util.List;

public class CommentBean {


    /**
     * content : {"total":4,"list":[{"id":1,"pid":177,"uid":42,"content":"嗯嗯嗯","time":1512052082000,"miniUser":{"username":"测试20号","phone":"13101411920","image":"http://oupl6wdxc.bkt.clouddn.com/AELFGLMGFHG1512124519733"}},{"id":2,"pid":177,"uid":42,"content":"哦哦哦大家觉得刺激刺激","time":1512052347000,"miniUser":{"username":"测试20号","phone":"13101411920","image":"http://oupl6wdxc.bkt.clouddn.com/AELFGLMGFHG1512124519733"}},{"id":3,"pid":177,"uid":42,"content":"想几次打开手机第几次发","time":1512052350000,"miniUser":{"username":"测试20号","phone":"13101411920","image":"http://oupl6wdxc.bkt.clouddn.com/AELFGLMGFHG1512124519733"}},{"id":4,"pid":177,"uid":42,"content":"数据线坚持坚持减肥","time":1512052357000,"miniUser":{"username":"测试20号","phone":"13101411920","image":"http://oupl6wdxc.bkt.clouddn.com/AELFGLMGFHG1512124519733"}}],"hasNextPage":false}
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
         * total : 4
         * list : [{"id":1,"pid":177,"uid":42,"content":"嗯嗯嗯","time":1512052082000,"miniUser":{"username":"测试20号","phone":"13101411920","image":"http://oupl6wdxc.bkt.clouddn.com/AELFGLMGFHG1512124519733"}},{"id":2,"pid":177,"uid":42,"content":"哦哦哦大家觉得刺激刺激","time":1512052347000,"miniUser":{"username":"测试20号","phone":"13101411920","image":"http://oupl6wdxc.bkt.clouddn.com/AELFGLMGFHG1512124519733"}},{"id":3,"pid":177,"uid":42,"content":"想几次打开手机第几次发","time":1512052350000,"miniUser":{"username":"测试20号","phone":"13101411920","image":"http://oupl6wdxc.bkt.clouddn.com/AELFGLMGFHG1512124519733"}},{"id":4,"pid":177,"uid":42,"content":"数据线坚持坚持减肥","time":1512052357000,"miniUser":{"username":"测试20号","phone":"13101411920","image":"http://oupl6wdxc.bkt.clouddn.com/AELFGLMGFHG1512124519733"}}]
         * hasNextPage : false
         */

        private int total;
        private boolean hasNextPage;
        private List<ListBean> list;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public boolean isHasNextPage() {
            return hasNextPage;
        }

        public void setHasNextPage(boolean hasNextPage) {
            this.hasNextPage = hasNextPage;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * id : 1
             * pid : 177
             * uid : 42
             * content : 嗯嗯嗯
             * time : 1512052082000
             * miniUser : {"username":"测试20号","phone":"13101411920","image":"http://oupl6wdxc.bkt.clouddn.com/AELFGLMGFHG1512124519733"}
             */

            private int id;
            private int pid;
            private int uid;
            private String content;
            private long time;
            private MiniUserBean miniUser;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getPid() {
                return pid;
            }

            public void setPid(int pid) {
                this.pid = pid;
            }

            public int getUid() {
                return uid;
            }

            public void setUid(int uid) {
                this.uid = uid;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public long getTime() {
                return time;
            }

            public void setTime(long time) {
                this.time = time;
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
                 * image : http://oupl6wdxc.bkt.clouddn.com/AELFGLMGFHG1512124519733
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
}
