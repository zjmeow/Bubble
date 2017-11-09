package com.stonymoon.bubble.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.bean.BubbleBean;
import com.stonymoon.bubble.bean.BubbleDetailBean;
import com.stonymoon.bubble.util.HttpUtil;
import com.stonymoon.bubble.util.LogUtil;
import com.stonymoon.bubble.util.clusterutil.clustering.Cluster;
import com.stonymoon.bubble.util.clusterutil.clustering.ClusterItem;
import com.stonymoon.bubble.util.clusterutil.clustering.ClusterManager;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.RxStringCallback;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;


/**
 * 此Demo用来说明点聚合功能
 */
public class MapTestActivity extends Activity implements OnMapLoadedCallback {
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Map parameters = new HashMap();

    private MapStatus ms;
    private ClusterManager<MyItem> mClusterManager;
    private List<MyItem> itemList = new ArrayList<>();
    private List<MyItem> seenItems = new ArrayList<>();
    private int screenWidth;
    private Point leftTop = new Point(0, 0);
    private Point rightBottom = new Point(0, 0);

    private int screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_map_test);

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
                Toast.makeText(MapTestActivity.this,
                        "有" + cluster.getSize() + "个点", Toast.LENGTH_SHORT).show();

                return false;
            }
        });
        mClusterManager.setClusterDistance(700);

        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
            @Override
            public boolean onClusterItemClick(MyItem item) {
                BubbleDetailActivity.startActivity(MapTestActivity.this, item.getBean().getId() + "");
                return false;
            }
        });
        initScreen();
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
        String url = "map/bubble";
        HttpUtil.sendHttpRequest(this).rxGet(url, parameters, new RxStringCallback() {
            @Override
            public void onNext(Object tag, String response) {
                Gson gson = new Gson();
                BubbleBean bean = gson.fromJson(response, BubbleBean.class);
                for (BubbleBean.ResultBean b : bean.getResult()) {
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

    private void initScreen() {
        WindowManager wm = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        screenWidth = outMetrics.widthPixels;
        screenHeight = outMetrics.heightPixels;
        rightBottom.x = screenWidth;
        rightBottom.y = screenHeight;

    }

    private void addSeenItem() {
        seenItems.clear();
        LatLng ll = mBaiduMap.getProjection().fromScreenLocation(leftTop);
        LatLng llr = mBaiduMap.getProjection().fromScreenLocation(rightBottom);
        for (MyItem item : itemList) {
            LatLng latLng = item.getPosition();
            double lat = latLng.latitude;
            double lng = latLng.longitude;
            if (ll.latitude > lat && ll.longitude < lng && llr.latitude < lat && llr.longitude > lng) {
                seenItems.add(item);
            }

        }
        addMarkers(seenItems);

    }

    /**
     * 每个Marker点，包含Marker点坐标以及图标
     */
    private class MyItem implements ClusterItem {
        private final LatLng mPosition;
        private final BubbleBean.ResultBean bean;

        public MyItem(BubbleBean.ResultBean bean) {
            mPosition = new LatLng(bean.getLatitude(), bean.getLongitude());
            this.bean = bean;
        }

        public BubbleBean.ResultBean getBean() {
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


}
