package com.stonymoon.bubble.ui;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.animation.AlphaAnimation;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.RotateAnimation;
import com.amap.api.maps.model.animation.ScaleAnimation;
import com.amap.api.maps.model.animation.TranslateAnimation;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.stonymoon.bubble.R;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.stonymoon.bubble.ui.MapActivity.MyMarker.TEXT_MARKER;
import static com.stonymoon.bubble.ui.MapActivity.MyMarker.USER_MARKER;


public class MapActivity extends AppCompatActivity {
    @BindView(R.id.map)
    MapView mapView;
    //用marker的id绑定信息，为点击回调提供信息
    private Map<String, MyMarker> markerMap = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //设置沉浸式状态栏
        QMUIStatusBarHelper.translucent(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        setMap(savedInstanceState);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }


    private void showSimpleBottomSheetGrid() {
        final Context context = MapActivity.this;
        final int TAG_SHARE_WECHAT_FRIEND = 0;
        final int TAG_SHARE_WECHAT_MOMENT = 1;
        final int TAG_SHARE_WEIBO = 2;
        final int TAG_SHARE_CHAT = 3;
        final int TAG_SHARE_LOCAL = 4;
        QMUIBottomSheet.BottomGridSheetBuilder builder = new QMUIBottomSheet.BottomGridSheetBuilder(context);
        builder.addItem(R.mipmap.test, "分享到微信", TAG_SHARE_WECHAT_FRIEND, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                .addItem(R.mipmap.test, "分享到朋友圈", TAG_SHARE_WECHAT_MOMENT, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                .addItem(R.mipmap.test, "分享到微博", TAG_SHARE_WEIBO, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                .addItem(R.mipmap.test, "分享到私信", TAG_SHARE_CHAT, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                .addItem(R.mipmap.test, "保存到本地", TAG_SHARE_LOCAL, QMUIBottomSheet.BottomGridSheetBuilder.SECOND_LINE)
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
                            case TAG_SHARE_CHAT:
                                Toast.makeText(context, "分享到私信", Toast.LENGTH_SHORT).show();
                                break;
                            case TAG_SHARE_LOCAL:
                                Toast.makeText(context, "保存到本地", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }).build().show();


    }


    private void setMap(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        final AMap aMap = mapView.getMap();
        MyLocationStyle myLocationStyle = new MyLocationStyle();//初始化
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        // 定义 Marker 点击事件监听
        AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {

            // marker 对象被点击时回调的接口
            // 返回 true 则表示接口已响应事件，否则返回false
            @Override
            public boolean onMarkerClick(Marker marker) {
                MyMarker myMarker = markerMap.get(marker.getId());
                switch (myMarker.getType()) {
                    case USER_MARKER:
                        Toast.makeText(MapActivity.this, "user", Toast.LENGTH_SHORT).show();
                        //点击后把镜头移动到气泡上
                        zoomIn(aMap, marker, 30f);
                        break;
                    case TEXT_MARKER:
                        Toast.makeText(MapActivity.this, "text", Toast.LENGTH_SHORT).show();
                        zoomIn(aMap, marker, 30f);
                        marker.startAnimation();
                        break;
                    default:
                        break;
                }

                return true;
            }


        };
        // 绑定 Marker 被点击事件

        aMap.setOnMarkerClickListener(markerClickListener);
        new MyMarker(aMap, 28, 28, USER_MARKER);
        new MyMarker(aMap, 30, 30, TEXT_MARKER);

    }

    private void zoomIn(AMap aMap, Marker marker, float v) {
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), v));


    }

    class MyMarker {
        public static final int USER_MARKER = 0;
        public static final int TEXT_MARKER = 1;
        private int type;

        public MyMarker(AMap aMap, double latitude, double longitude, int type) {
            LatLng latLng = new LatLng(latitude, longitude);
            ImageView imageView = (ImageView) View.inflate(MapActivity.this, R.layout.test_view, null);
            Marker marker = aMap.addMarker(new MarkerOptions().position(latLng)
                    .icon(BitmapDescriptorFactory.fromView(imageView)));

            Animation animation = new ScaleAnimation(1, 0.5f, 1, 0.5f);
            animation.setDuration(1000);
            marker.setAnimation(animation);
            this.type = type;
            markerMap.put(marker.getId(), this);
        }

        public int getType() {
            return type;
        }

        public void addUserMarker(AMap aMap, double latitude, double longitude) {
            LatLng latLng = new LatLng(latitude, longitude);
            Marker marker = aMap.addMarker(new MarkerOptions().position(latLng));
            new MyMarker(aMap, latitude, longitude, USER_MARKER);
        }
    }




}


