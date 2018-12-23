package com.stonymoon.bubble.bean;

import java.util.Date;
import java.util.List;

/**
 * Created by Stony on 2018/12/23.
 */

public class BubbleListBean {

    /**
     * success : true
     * message : ok
     * data : [{"id":16,"lat":26.056666,"lng":119.198634,"title":"d","pic":"https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=46753002,1263792215&fm=27&gp=0.jpg","createdTime":"2018-12-23T05:44:12.499+0000","content":"d","tap":0}]
     */

    private boolean success;
    private String message;
    private List<DataBean> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 16
         * lat : 26.056666
         * lng : 119.198634
         * title : d
         * pic : https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=46753002,1263792215&fm=27&gp=0.jpg
         * createdTime : 2018-12-23T05:44:12.499+0000
         * content : d
         * tap : 0
         */

        private int id;
        private double lat;
        private double lng;
        private String title;
        private String pic;
        private Date createdTime;
        private String content;
        private int tap;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public Date getCreatedTime() {
            return createdTime;
        }

        public void setCreatedTime(Date createdTime) {
            this.createdTime = createdTime;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getTap() {
            return tap;
        }

        public void setTap(int tap) {
            this.tap = tap;
        }
    }
}
