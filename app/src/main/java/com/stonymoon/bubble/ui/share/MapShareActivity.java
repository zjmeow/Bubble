package com.stonymoon.bubble.ui.share;

import android.app.Activity;
import android.os.Bundle;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.bean.BubbleBean;
import com.stonymoon.bubble.bean.LocationBean;
import com.stonymoon.bubble.ui.friend.MapActivity;
import com.stonymoon.bubble.util.AuthUtil;
import com.stonymoon.bubble.util.HttpUtil;
import com.stonymoon.bubble.util.clusterutil.clustering.Cluster;
import com.stonymoon.bubble.util.clusterutil.clustering.ClusterItem;
import com.stonymoon.bubble.util.clusterutil.clustering.ClusterManager;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.RxStringCallback;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class MapShareActivity extends Activity implements OnMapLoadedCallback {
    public LocationClient mLocationClient = null;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Map parameters = new HashMap();
    private MapStatus ms;
    private ClusterManager<MyItem> mClusterManager;
    private List<MyItem> itemList = new ArrayList<>();
    private List<MyItem> seenItems = new ArrayList<>();
    private double latitude;
    private double longitude;
    private boolean isLocated = false;

    @OnClick(R.id.fab_map_share)
    void share() {
        ShareActivity.startActivity(this, latitude, longitude);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_share_map);
        ButterKnife.bind(this);
        mMapView = (MapView) findViewById(R.id.bmapView);
        ms = new MapStatus.Builder().target(new LatLng(39.914935, 116.403119)).zoom(8).build();
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setOnMapLoadedCallback(this);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
        // 定义点聚合管理类ClusterManager
        mClusterManager = new ClusterManager<MyItem>(this, mBaiduMap);
        // 设置地图监听，当地图状态发生改变时，进行点聚合运算
        mBaiduMap.setOnMapStatusChangeListener(mClusterManager);
        // 设置maker点击时的响应
        mBaiduMap.setOnMarkerClickListener(mClusterManager);

        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyItem>() {
            @Override
            public boolean onClusterClick(Cluster cluster) {
                Toast.makeText(MapShareActivity.this,
                        "有" + cluster.getSize() + "个点", Toast.LENGTH_SHORT).show();

                return false;
            }
        });

        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
            @Override
            public boolean onClusterItemClick(MyItem item) {
                BubbleDetailActivity.startActivity(MapShareActivity.this, item.getBean());
                return false;
            }
        });
        initBubble();
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {


            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                addSeenItem();
            }
        });
        initLocate();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

// 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
//        CurrentMarker mCurrentMarker = BitmapDescriptorFactory
//                .fromResource(R.drawable.icon_geo);
//        MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
//        mBaiduMap.setMyLocationConfiguration(config);




    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    /**
     * 向地图添加Marker点
     */
    public void addMarkers(List<MyItem> items) {
        mClusterManager.clearItems();
        mClusterManager.addItems(items);
        mClusterManager.cluster();
    }

    @Override
    public void onMapLoaded() {
        ms = new MapStatus.Builder().zoom(9).build();
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
    }

    private void initBubble() {
        String url = "download";
        HttpUtil.sendHttpRequest(this).rxGet(url, parameters, new RxStringCallback() {
            @Override
            public void onNext(Object tag, String response) {
                Gson gson = new Gson();
                BubbleBean bean = gson.fromJson(response, BubbleBean.class);
                for (BubbleBean.ContentBean b : bean.getContent()) {
                    itemList.add(new MyItem(b));
                }

                addSeenItem();
                addMarkers(seenItems);
            }

            @Override
            public void onError(Object tag, Throwable e) {

            }

            @Override
            public void onCancel(Object tag, Throwable e) {

            }
        });


    }

    private void addSeenItem() {
        seenItems.clear();
        for (MyItem item : itemList) {
            if (mBaiduMap.getMapStatusLimit().contains(item.getPosition())) {
                seenItems.add(item);
            }
        }
        addMarkers(seenItems);
    }

    private void initLocate() {
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(new MyLocationListener());
        //注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        //bd09：百度墨卡托坐标；
        option.setScanSpan(5000);
        //设置发起定位请求的间隔，int类型，单位ms
        option.setOpenGps(true);
        option.setLocationNotify(false);
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.setIgnoreKillProcess(true);
        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.setEnableSimulateGps(false);
//可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false
        mLocationClient.setLocOption(option);
//mLocationClient为第二步初始化过的LocationClient对象
        mLocationClient.start();

    }

    //根据marker来设置地图镜头移动
    private void zoomIn(BaiduMap baiduMap, LatLng latLng, float v) {
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(latLng)
                .zoom(v)
                .build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        baiduMap.animateMapStatus(mMapStatusUpdate);
    }

    /**
     * 每个Marker点，包含Marker点坐标以及图标
     */
    private class MyItem implements ClusterItem {
        private final LatLng mPosition;
        private final BubbleBean.ContentBean bean;

        public MyItem(BubbleBean.ContentBean bean) {
            mPosition = new LatLng(bean.getLatitude(), bean.getLongitude());
            this.bean = bean;
        }

        public BubbleBean.ContentBean getBean() {
            return bean;
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }

        @Override
        public BitmapDescriptor getBitmapDescriptor() {
            return BitmapDescriptorFactory.fromResource(R.mipmap.bubble);

        }

    }

    private class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(final BDLocation location) {
            // 构造定位数据
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
// 设置定位数据
            mBaiduMap.setMyLocationData(locData);

            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            latitude = location.getLatitude();    //获取纬度信息
            longitude = location.getLongitude();    //获取经度信息
            if (isLocated == false) {
                zoomIn(mBaiduMap, new LatLng(latitude, longitude), 30);
            }
            isLocated = true;

        }

    }

}
