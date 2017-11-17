package com.stonymoon.bubble.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/10/27.
 */

public class LocationBean {


    /**
     * status : 0
     * message : 成功
     * size : 1
     * pois : [{"id":"924528024416712288","geotable_id":"1000002164","location":[119.198768,26.056791],"gcj_location":[119.19224827941872,26.050831076722915],"province":"福建省","city":"福州市","district":"闽侯县","city_id":"300","uid":2,"username":"stony","url":"http://api.map.baidu.com/geodata/v4/poi/create","create_time":"2017-10-29 14:47:12","modify_time":"2017-10-29 14:47:12"}]
     */

    private List<PoisBean> pois;

    public List<PoisBean> getPois() {
        return pois;
    }

    public void setPois(List<PoisBean> pois) {
        this.pois = pois;
    }

    public static class PoisBean {
        /**
         * id : 924528024416712288
         * geotable_id : 1000002164
         * location : [119.198768,26.056791]
         * gcj_location : [119.19224827941872,26.050831076722915]
         * province : 福建省
         * city : 福州市
         * district : 闽侯县
         * city_id : 300
         * uid : 2
         * username : stony
         * url : http://api.map.baidu.com/geodata/v4/poi/create
         * create_time : 2017-10-29 14:47:12
         * modify_time : 2017-10-29 14:47:12
         */

        private String id;
        private String province;
        private String city;
        private String district;
        private int uid;
        private String username;
        private String url;
        @SerializedName("modify_time")
        private String modifyTime;
        private String phone;
        private List<Double> location;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getModifTime() {
            return modifyTime;
        }

        public void getModifTime(String modifyTime) {
            this.modifyTime = modifyTime;
        }

        public List<Double> getLocation() {
            return location;
        }

        public void setLocation(List<Double> location) {
            this.location = location;
        }
    }
}
