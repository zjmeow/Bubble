package com.stonymoon.bubble.bean;

import java.util.Date;
import java.util.List;

/**
 * 用于显示用户在地图上的位置
 */

public class MapUserBean {


    /**
     * success : true
     * message : ok
     * data : [{"username":"zjmeow","id":36,"avatar":"https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1545457975&di=a7b6effcc32d4a0da876118aa6d10a1c&src=http://img5.duitang.com/uploads/item/201611/11/20161111175736_QV5vE.thumb.700_0.jpeg","loginTime":null,"lat":119.199316,"lng":26.056876}]
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
         * username : zjmeow
         * id : 36
         * avatar : https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1545457975&di=a7b6effcc32d4a0da876118aa6d10a1c&src=http://img5.duitang.com/uploads/item/201611/11/20161111175736_QV5vE.thumb.700_0.jpeg
         * loginTime : null
         * lat : 119.199316
         * lng : 26.056876
         */

        private String username;
        private int id;
        private String avatar;
        private Date loginTime;
        private double lat;
        private double lng;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public Date getLoginTime() {
            return loginTime;
        }

        public void setLoginTime(Date loginTime) {
            this.loginTime = loginTime;
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
    }
}
