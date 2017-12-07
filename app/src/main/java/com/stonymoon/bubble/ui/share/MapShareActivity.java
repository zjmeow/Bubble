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
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.qmuiteam.qmui.span.QMUITextSizeSpan;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.base.BaseActivity;
import com.stonymoon.bubble.bean.BubbleBean;
import com.stonymoon.bubble.bean.BubbleHolder;
import com.stonymoon.bubble.bean.LocationBean;
import com.stonymoon.bubble.ui.friend.MapActivity;
import com.stonymoon.bubble.util.AuthUtil;
import com.stonymoon.bubble.util.HttpUtil;
import com.stonymoon.bubble.util.LogUtil;
import com.stonymoon.bubble.util.MapUtil;
import com.stonymoon.bubble.util.UrlUtil;
import com.stonymoon.bubble.util.clusterutil.clustering.Cluster;
import com.stonymoon.bubble.util.clusterutil.clustering.ClusterItem;
import com.stonymoon.bubble.util.clusterutil.clustering.ClusterManager;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.RxStringCallback;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.support.constraint.ConstraintLayout;
import android.text.InputType;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.stonymoon.bubble.util.MapUtil.clearMap;
import static com.stonymoon.bubble.util.MapUtil.zoomIn;


public class MapShareActivity extends BaseActivity implements OnMapLoadedCallback {
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
        mClusterManager = new ClusterManager<MyItem>(this, mBaiduMap);
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
                BubbleDetailActivity.startActivity(MapShareActivity.this, item.getBean());
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
        String url = UrlUtil.getMapBubble();
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
                LogUtil.e(TAG, e.getMessage());
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

    private void showEditTextDialog() {
        //todo 上传
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(MapShareActivity.this);
        builder.setTitle("分享")
                .setPlaceholder("在此输入想留在地图上的文字")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        CharSequence text = builder.getEditText().getText();
                        if (text != null && text.length() > 0) {
                            Toast.makeText(MapShareActivity.this, "记录成功" + text, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(MapShareActivity.this, "请输入内容", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();
    }

    /**
     * 每个Marker点，包含Marker点坐标以及图标
     */
    public class MyItem implements ClusterItem {
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
            RelativeLayout bubbleLayout = (RelativeLayout) View.inflate(MapShareActivity.this, R.layout.bubble_text, null);
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
