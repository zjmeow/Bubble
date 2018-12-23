package com.stonymoon.bubble.ui.share;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.api.BaseDataManager;
import com.stonymoon.bubble.api.serivces.BubbleService;
import com.stonymoon.bubble.base.BaseActivity;
import com.stonymoon.bubble.bean.BubbleBean;
import com.stonymoon.bubble.bean.BubbleHolder;
import com.stonymoon.bubble.util.LogUtil;
import com.stonymoon.bubble.util.MapUtil;
import com.stonymoon.bubble.util.UrlUtil;
import com.stonymoon.bubble.util.clusterutil.clustering.Cluster;
import com.stonymoon.bubble.util.clusterutil.clustering.ClusterItem;
import com.stonymoon.bubble.util.clusterutil.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.stonymoon.bubble.util.MapUtil.clearMap;
import static com.stonymoon.bubble.util.MapUtil.zoomIn;


public class MapShareActivity extends BaseActivity implements OnMapLoadedCallback {
    private static final String TAG = "MapShareActivity";
    public LocationClient mLocationClient = null;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private MapStatus ms;
    private ClusterManager<MyItem> mClusterManager;
    private List<MyItem> itemList = new ArrayList<>();
    private List<MyItem> seenItems = new ArrayList<>();
    private double latitude;
    private double longitude;
    private boolean isLocated = false;

    @OnClick(R.id.iv_map_share)
    void share() {
        ShareActivity.startActivity(this, latitude, longitude);
    }

    @OnClick(R.id.iv_map_refresh)
    void fresh() {
        finish();
    }


    @OnClick(R.id.iv_share_map_location)
    void locate() {
        zoomIn(mBaiduMap, new LatLng(latitude, longitude), 30);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QMUIStatusBarHelper.translucent(this);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_share_map);
        ButterKnife.bind(this);
        setMap();

    }

    private void setMap() {
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        clearMap(mMapView);
        mBaiduMap.setOnMapLoadedCallback(this);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
        // 定义点聚合管理类ClusterManager
        mClusterManager = new ClusterManager<>(this, mBaiduMap);
        // 设置地图监听，当地图状态发生改变时，进行点聚合运算
        mBaiduMap.setOnMapStatusChangeListener(mClusterManager);
        // 设置maker点击时的响应
        mBaiduMap.setOnMarkerClickListener(mClusterManager);

        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyItem>() {
            @Override
            public boolean onClusterClick(Cluster cluster) {
                BubbleHolder.getInstance().setData(cluster);
                ShareListActivity.startActivity(MapShareActivity.this);
                return false;
            }
        });

        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
            @Override
            public boolean onClusterItemClick(MyItem item) {
                BubbleDetailActivity.startActivity(MapShareActivity.this, item.getBean().getId());
                return false;
            }
        });
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
        mLocationClient = MapUtil.getDefaultLocationClient(new MyLocationListener());
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mLocationClient.start();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        initBubble();
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
        mMapView.setMapCustomEnable(true);
    }

    private void initBubble() {
        seenItems.clear();
        itemList.clear();
        BaseDataManager.getHttpManager()
                .create(BubbleService.class)
                .getAroundBubble(longitude, latitude)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<BubbleBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(java.lang.Throwable e) {
                        LogUtil.e(TAG, e.toString());

                    }

                    @Override
                    public void onNext(BubbleBean bean) {
                        for (BubbleBean.DataBean b : bean.getData()) {
                            itemList.add(new MyItem(b));
                        }

                        addSeenItem();
                        addMarkers(seenItems);
                    }
                });


        String url = UrlUtil.getMapBubble();


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


    /**
     * 每个Marker点，包含Marker点坐标以及图标
     */
    public class MyItem implements ClusterItem {
        private final LatLng mPosition;
        private final BubbleBean.DataBean bean;

        public MyItem(BubbleBean.DataBean bean) {
            mPosition = new LatLng(bean.getLat(), bean.getLng());
            this.bean = bean;
        }

        public BubbleBean.DataBean getBean() {
            return bean;
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }

        @Override
        public BitmapDescriptor getBitmapDescriptor() {
            RelativeLayout bubbleLayout = (RelativeLayout) View.inflate(MapShareActivity.this
                    , R.layout.bubble_text, null);
            TextView tvTitle = (TextView) bubbleLayout.findViewById(R.id.tv_bubble_map_title);
            tvTitle.setText(bean.getTitle());
            return BitmapDescriptorFactory.fromView(bubbleLayout);
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
                zoomIn(mBaiduMap, new LatLng(latitude, longitude), 18);
            }
            isLocated = true;

        }

    }


}
