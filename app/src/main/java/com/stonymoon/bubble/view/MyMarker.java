package com.stonymoon.bubble.view;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.autonavi.amap.mapcore.interfaces.IMarker;

/**
 * Created by A on 2017/10/18.
 */

public class MyMarker {
    private Marker marker;


    private MyMarker(AMap aMap, double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
        final Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title("北京").snippet("Infowindow"));
        // 定义 Marker 点击事件监听
        AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
            // marker 对象被点击时回调的接口
            // 返回 true 则表示接口已响应事件，否则返回false
            @Override
            public boolean onMarkerClick(Marker marker) {


                return true;
            }
        };
        // 绑定 Marker 被点击事件
        aMap.setOnMarkerClickListener(markerClickListener);


    }

    public static MyMarker userOf(AMap aMap, double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
        final Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title("北京").snippet("Infowindow"));
        // 定义 Marker 点击事件监听
        AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
            // marker 对象被点击时回调的接口
            // 返回 true 则表示接口已响应事件，否则返回false
            @Override
            public boolean onMarkerClick(Marker marker) {
                return true;
            }
        };
        // 绑定 Marker 被点击事件
        aMap.setOnMarkerClickListener(markerClickListener);

        return new MyMarker(aMap, latitude, longitude);
    }

}
