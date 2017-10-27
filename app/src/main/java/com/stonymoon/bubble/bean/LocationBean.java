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
     * size : 10
     * pois : [{"id":"923906845016359286","geotable_id":"1000002164","location":[1,1],"gcj_location":[0.9934856269655112,0.993986706569649],"province":"","city":"","district":"","city_id":"0","username":"stony","create_time":"2017-10-27 21:38:51","modify_time":"2017-10-27 21:38:51"},{"id":"923906844236218074","geotable_id":"1000002164","location":[1,1],"gcj_location":[0.9934856269655112,0.993986706569649],"province":"","city":"","district":"","city_id":"0","username":"stony","create_time":"2017-10-27 21:38:51","modify_time":"2017-10-27 21:38:51"},{"id":"923906843556738212","geotable_id":"1000002164","location":[1,1],"gcj_location":[0.9934856269655112,0.993986706569649],"province":"","city":"","district":"","city_id":"0","username":"stony","create_time":"2017-10-27 21:38:51","modify_time":"2017-10-27 21:38:51"},{"id":"923906842776600280","geotable_id":"1000002164","location":[1,1],"gcj_location":[0.9934856269655112,0.993986706569649],"province":"","city":"","district":"","city_id":"0","username":"stony","create_time":"2017-10-27 21:38:51","modify_time":"2017-10-27 21:38:51"},{"id":"923906841929350870","geotable_id":"1000002164","location":[1,1],"gcj_location":[0.9934856269655112,0.993986706569649],"province":"","city":"","district":"","city_id":"0","username":"stony","create_time":"2017-10-27 21:38:51","modify_time":"2017-10-27 21:38:51"},{"id":"923906841111458978","geotable_id":"1000002164","location":[10,10],"gcj_location":[9.993480703470995,9.993991630060519],"province":"Bauchi","city":"Alkaleri","district":"","city_id":"75745","username":"stony","create_time":"2017-10-27 21:38:50","modify_time":"2017-10-27 21:40:30"},{"id":"923906840402622784","geotable_id":"1000002164","location":[1,1],"gcj_location":[0.9934856269655112,0.993986706569649],"province":"","city":"","district":"","city_id":"0","username":"stony","create_time":"2017-10-27 21:38:50","modify_time":"2017-10-27 21:38:50"},{"id":"923906837953148064","geotable_id":"1000002164","location":[1,1],"gcj_location":[0.9934856269655112,0.993986706569649],"province":"","city":"","district":"","city_id":"0","username":"stony","create_time":"2017-10-27 21:38:50","modify_time":"2017-10-27 21:38:50"},{"id":"923906836329955700","geotable_id":"1000002164","location":[1,1],"gcj_location":[0.9934856269655112,0.993986706569649],"province":"","city":"","district":"","city_id":"0","username":"stony","create_time":"2017-10-27 21:38:49","modify_time":"2017-10-27 21:38:49"},{"id":"923906781250356762","geotable_id":"1000002164","location":[1,1],"gcj_location":[0.9934856269655112,0.993986706569649],"province":"","city":"","district":"","city_id":"0","username":"stony","create_time":"2017-10-27 21:38:36","modify_time":"2017-10-27 21:38:36"}]
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
         * id : 923906845016359286
         * geotable_id : 1000002164
         * location : [1,1]
         * gcj_location : [0.9934856269655112,0.993986706569649]
         * province :
         * city :
         * district :
         * city_id : 0
         * username : stony
         * create_time : 2017-10-27 21:38:51
         * modify_time : 2017-10-27 21:38:51
         */

        private String id;
        private String username;
        @SerializedName("modify_time")
        private String modifyTime;
        private List<Double> location;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getModifyTime() {
            return modifyTime;
        }

        public void setModifyTime(String modifyTime) {
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
