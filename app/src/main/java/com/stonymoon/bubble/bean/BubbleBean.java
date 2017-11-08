package com.stonymoon.bubble.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class BubbleBean {
    @SerializedName("resource")
    private List<ResultBean> result;

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * longitude : null
         * latitude : null
         * id : 1
         * title : null
         */

        private double longitude;
        private double latitude;
        private int id;
        private String title;

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
