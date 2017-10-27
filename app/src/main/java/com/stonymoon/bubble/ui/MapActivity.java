package com.stonymoon.bubble.ui;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.bean.LocationBean;
import com.stonymoon.bubble.bean.UserBean;
import com.stonymoon.bubble.util.HttpUtil;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.RxStringCallback;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.stonymoon.bubble.ui.MapActivity.MyMarker.TEXT_MARKER;
import static com.stonymoon.bubble.ui.MapActivity.MyMarker.USER_MARKER;


public class MapActivity extends AppCompatActivity {
    public LocationClient mLocationClient = null;
    public BDAbstractLocationListener myListener = new MyLocationListener();
    @BindView(R.id.map)
    MapView mapView;
    private Map<String, Object> parameters = new HashMap<>();
    //用marker的id绑定信息，为点击回调提供信息
    private Map<String, MyMarker> markerMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //设置沉浸式状态栏
        QMUIStatusBarHelper.translucent(this);
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        setMap();
        initLocate();
        mLocationClient.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mapView.onPause();
    }

    private void showBottomSheetGrid() {
        final Context context = MapActivity.this;
        final int TAG_SHARE_WECHAT_FRIEND = 0;
        final int TAG_SHARE_WECHAT_MOMENT = 1;
        final int TAG_SHARE_WEIBO = 2;
        QMUIBottomSheet.BottomGridSheetBuilder builder = new QMUIBottomSheet.BottomGridSheetBuilder(context);
        builder.addItem(R.mipmap.test, "查看用户信息", TAG_SHARE_WECHAT_FRIEND, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                .addItem(R.mipmap.test, "聊天", TAG_SHARE_WECHAT_MOMENT, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                .addItem(R.mipmap.test, "互动", TAG_SHARE_WEIBO, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                .setOnSheetItemClickListener(new QMUIBottomSheet.BottomGridSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView) {
                        dialog.dismiss();
                        int tag = (int) itemView.getTag();
                        switch (tag) {
                            case TAG_SHARE_WECHAT_FRIEND:
                                Toast.makeText(context, "分享到微信", Toast.LENGTH_SHORT).show();
                                break;
                            case TAG_SHARE_WECHAT_MOMENT:
                                Toast.makeText(context, "分享到朋友圈", Toast.LENGTH_SHORT).show();
                                break;
                            case TAG_SHARE_WEIBO:
                                Toast.makeText(context, "分享到微博", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }).build().show();


    }


    private void setMap() {
        final BaiduMap baiduMap = mapView.getMap();
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        BaiduMap.OnMarkerClickListener markerClickListener = new BaiduMap.OnMarkerClickListener() {

            // marker 对象被点击时回调的接口
            // 返回 true 则表示接口已响应事件，否则返回false
            @Override
            public boolean onMarkerClick(Marker marker) {
                MyMarker myMarker = markerMap.get(marker.getId());
                zoomIn(baiduMap, marker, 30f);
                switch (myMarker.getType()) {
                    case USER_MARKER:
                        LocationBean.PoisBean bean = myMarker.getUserBean();
                        Toast.makeText(MapActivity.this, bean.getUsername(), Toast.LENGTH_SHORT).show();
                        //点击后把镜头移动到气泡上
                        //ProfileActivity.startActivity(MapActivity.this, bean.getUserImage(), bean.getUserName(), bean.getUserId());
                        break;
                    case TEXT_MARKER:
                        Toast.makeText(MapActivity.this, "text", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }

                return true;
            }


        };
        // 绑定 Marker 被点击事件

        baiduMap.setOnMarkerClickListener(markerClickListener);

    }

    //根据marker来设置地图镜头移动
    private void zoomIn(BaiduMap baiduMap, Marker marker, float v) {
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(marker.getPosition())
                .zoom(v)
                .build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        baiduMap.animateMapStatus(mMapStatusUpdate);
    }


    //把用用户的bean来在地图上添加Marker
    public MyMarker addUserMarker(BaiduMap baiduMap, LocationBean.PoisBean bean) {


        RelativeLayout userLayout = (RelativeLayout) View.inflate(MapActivity.this, R.layout.test_view, null);
        TextView usernameText = (TextView) userLayout.findViewById(R.id.tv_bubble_username);
        usernameText.setText(bean.getUsername());
        LatLng latLng = new LatLng(bean.getLocation().get(0), bean.getLocation().get(1));

        OverlayOptions options = new MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.fromView(userLayout));
        Marker marker = (Marker) baiduMap.addOverlay(options);
        Animation animation = new ScaleAnimation(1, 0.5f, 1, 0.5f);
        animation.setDuration(1000);
        //todo marker动画 marker.setAnimation(animation);
        MyMarker myMarker = new MyMarker(bean);
        markerMap.put(marker.getId(), myMarker);
        return myMarker;

    }

    private void initLocate() {
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        //bd09：百度墨卡托坐标；
        option.setScanSpan(2000);
        //设置发起定位请求的间隔，int类型，单位ms
        option.setOpenGps(true);
        option.setLocationNotify(false);
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.setIgnoreKillProcess(false);
        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.setEnableSimulateGps(false);
//可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false
        mLocationClient.setLocOption(option);
//mLocationClient为第二步初始化过的LocationClient对象


    }

    //储存Marker中的信息，用Map把mark的id与它关联起来
    class MyMarker {
        public static final int USER_MARKER = 0;
        public static final int TEXT_MARKER = 1;
        private int type;
        private LocationBean.PoisBean poisBean;

        public MyMarker(LocationBean.PoisBean bean) {
            this.poisBean = bean;
            type = USER_MARKER;
        }

        public LocationBean.PoisBean getUserBean() {
            return poisBean;
        }

        public int getType() {
            return type;
        }

    }

    class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            double latitude = location.getLatitude();    //获取纬度信息
            double longitude = location.getLongitude();    //获取经度信息
            parameters.put("latitude ", latitude);
            parameters.put("longitude", longitude);
            Toast.makeText(MapActivity.this, String.valueOf(latitude), Toast.LENGTH_SHORT).show();

            HttpUtil.updateMap(MapActivity.this, new RxStringCallback() {
                @Override
                public void onNext(Object tag, String response) {
                    Gson gson = new Gson();
                    LocationBean bean = gson.fromJson(response, LocationBean.class);
                    final BaiduMap baiduMap = mapView.getMap();
                    for (LocationBean.PoisBean b : bean.getPois()) {
                        addUserMarker(baiduMap, b);
                    }

                }

                @Override
                public void onError(Object tag, Throwable e) {
                    Toast.makeText(MapActivity.this, "加载失败，请检查网络", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                @Override
                public void onCancel(Object tag, Throwable e) {

                }
            }, latitude, longitude);


        }
    }

}