package com.stonymoon.bubble.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.squareup.picasso.Picasso;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.bean.LocationBean;
import com.stonymoon.bubble.util.HttpUtil;
import com.stonymoon.bubble.util.LogUtil;
import com.stonymoon.bubble.util.MyCallback;
import com.stonymoon.bubble.util.SpringScaleInterpolator;
import com.stonymoon.bubble.util.clusterutil.clustering.ClusterItem;
import com.stonymoon.bubble.util.clusterutil.clustering.ClusterManager;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.RxStringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.ContactNotifyEvent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.OfflineMessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;

//本地图显示附近的人
//动态地图另外开一个地图显示
public class MapActivity extends AppCompatActivity {
    public LocationClient mLocationClient = null;
    public BDAbstractLocationListener myListener = new MyLocationListener();
    @BindView(R.id.map)
    MapView mapView;
    @BindView(R.id.et_map_message)
    EditText messageEditText;
    @BindView(R.id.map_bubble)
    RelativeLayout bubble;
    @BindView(R.id.iv_map_bubble_head)
    ImageView headImage;
    @BindView(R.id.tv_map_bubble_username)
    TextView usernameText;

    //管理聚合地图
    private ClusterManager<MyItem> mClusterManager;
    private List<MyItem> myItems = new ArrayList<>();
    private HugeItem selectedItem;
    //todo 设置是否选中marker状态,当选中时不刷新
    private boolean isSelected = false;
    private String locationId;
    private String token;
    private String id;
    private String phone;
    private LocationBean.PoisBean chosenUserBean;
    private AnimatorSet set;


    private Map<String, Object> parameters = new HashMap<>();
    //用marker的id绑定信息，为点击回调提供信息
    private Map<String, MyMarker> markerMap = new HashMap<>();

    private MyCallback callback = new MyCallback(this);





    public static void startActivity(Context context, String id, String token, String locationId) {
        Intent intent = new Intent(context, MapActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("token", token);
        intent.putExtra("locationId", locationId);
        context.startActivity(intent);

    }

    @OnClick(R.id.btn_map_send_message)
    void sendMessage() {
        if (chosenUserBean == null) {
            return;
        }
        if (messageEditText.getText().equals("")) {
            return;
        }
        Message message = JMessageClient.createSingleTextMessage(chosenUserBean.getPhone(), messageEditText.getText().toString());
        messageEditText.setText("");
        JMessageClient.sendMessage(message);
        // TODO 测试好友列表
        Intent intent = new Intent(MapActivity.this, FriendActivity.class);
        startActivity(intent);

    }

    //接收到事件的处理
    public void onEventMainThread(MessageEvent event) {
        Toast.makeText(MapActivity.this, event.getMessage().toString() + "接收成功", Toast.LENGTH_SHORT).show();

    }

    public void onEvent(OfflineMessageEvent event) {
        //获取事件发生的会话对象
        Conversation conversation = event.getConversation();
        List<Message> newMessageList = event.getOfflineMessageList();//获取此次离线期间会话收到的新消息列表
        System.out.println(String.format(Locale.SIMPLIFIED_CHINESE, "收到%d条来自%s的离线消息。\n", newMessageList.size(), conversation.getTargetId()));
    }

    public void onEvent(ContactNotifyEvent event) {
        //获取事件发生的会话对象
        LogUtil.v("MapActivity", "receive friend" + event.toString());
        String target = JMessageClient.getMyInfo().getUserName();
        showMessagePositiveDialog(event.getFromUsername(), target);

    }




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
        JMessageClient.registerEventReceiver(this);
        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        id = intent.getStringExtra("id");
        locationId = intent.getStringExtra("locationId");
        setMap();
        initLocate();
        mLocationClient.start();
        initAnimation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JMessageClient.unRegisterEventReceiver(this);
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        mLocationClient.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        mLocationClient.stop();
    }

    private void setMap() {
        final BaiduMap baiduMap = mapView.getMap();
        mClusterManager = new ClusterManager<MyItem>(this, baiduMap);
        baiduMap.setOnMapStatusChangeListener(mClusterManager);
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
            //TODO 点击marker上拉弹窗启动onPause，然后地图停止刷新，关闭弹窗地图继续刷新
            @Override
            public boolean onClusterItemClick(MyItem item) {

                zoomIn(baiduMap, item.getPosition(), 30f);
                Point p = baiduMap.getProjection().toScreenLocation(item.getPosition());
                LocationBean.PoisBean bean = item.getPoisBean();
                chosenUserBean = bean;
                mClusterManager.clearItems();
                mClusterManager.cluster();
                mLocationClient.stop();

                //当点击item时删除全部item并且把自定义view引入
                //此时为选中状态，当用户离开选中状态时，隐藏自定义view
                //加载小图

                Picasso.with(MapActivity.this).
                        load(bean.getUrl() + "?imageMogr2/thumbnail/!150x150r/gravity/Center/crop/200x/blur/1x0/quality/20|imageslim").into(headImage);
                usernameText.setText(bean.getUsername());
                bubble.setVisibility(View.VISIBLE);
                isSelected = true;
                set.start();

                return false;
            }

//                ProfileActivity.startActivity(MapActivity.this,
//                        bean.getUrl(),
//                        bean.getUsername(),
//                        bean.getId());


        });


        // 绑定 Marker 被点击事件
        baiduMap.setOnMarkerClickListener(mClusterManager);

    }


    @Override
    public void onBackPressed() {
        if (isSelected) {
            mLocationClient.start();
            bubble.clearAnimation();
            bubble.setVisibility(View.GONE);
            isSelected = false;

        } else {
            super.onBackPressed();
        }
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

    public void addMarkers(List<MyItem> items) {
        // 添加Marker点
        //addItems添加一组
        mClusterManager.addItems(items);
        mClusterManager.cluster();

    }

    public void chooseMarker(MyItem item) {
        // 添加Marker点
        //addItems添加一组

        mClusterManager.addItem(item);
        mClusterManager.cluster();

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
        option.setScanSpan(5000);
        //设置发起定位请求的间隔，int类型，单位ms
        option.setOpenGps(true);
        option.setLocationNotify(false);
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.setIgnoreKillProcess(false);
        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.setEnableSimulateGps(true);
//可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false
        mLocationClient.setLocOption(option);
//mLocationClient为第二步初始化过的LocationClient对象


    }


    private void initAnimation() {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(bubble, "scaleX", 1.0f, 1.8f);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(bubble, "scaleY", 1.0f, 1.8f);
        set = new AnimatorSet();
        set.setDuration(1000);
        set.setInterpolator(new SpringScaleInterpolator(0.4f));
        set.playTogether(animatorX, animatorY);
        //set.setRepeatCount(Animation.INFINITE);
        //set.setRepeatMode(Animation.REVERSE);


    }

    @OnClick(R.id.map_bubble)
    void startProfile() {
        SelectPhotoActivity.startActivity(MapActivity.this,
                chosenUserBean.getUrl(),
                chosenUserBean.getUsername(),
                "" + chosenUserBean.getUid(),
                chosenUserBean.getId(),
                chosenUserBean.getPhone()
        );

    }

    private void showMessagePositiveDialog(final String username, final String targetUsername) {
        new QMUIDialog.MessageDialogBuilder(MapActivity.this)
                .setTitle(username + "请求加你为好友")
                .setMessage("要同意吗？")
                .addAction("拒绝", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("接受", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        switch (index) {
                            case 0:
                                ContactManager.declineInvitation(username, "", targetUsername + "拒绝了你的请求", callback);
                                break;
                            case 1:
                                ContactManager.acceptInvitation(targetUsername, "", callback);
                                break;
                        }
                        dialog.dismiss();

                    }
                })
                .show();
    }

    //储存Marker中的信息，用Map把mark的id与它关联起来
    class MyMarker {
        private LocationBean.PoisBean poisBean;

        public MyMarker(LocationBean.PoisBean bean) {
            this.poisBean = bean;
        }

        public LocationBean.PoisBean getUserBean() {
            return poisBean;
        }


    }

    private class MyItem implements ClusterItem {
        private final LatLng mPosition;
        private final LocationBean.PoisBean poisBean;

        public MyItem(LocationBean.PoisBean bean) {
            mPosition = new LatLng(bean.getLocation().get(1), bean.getLocation().get(0));
            this.poisBean = bean;
        }

        public LocationBean.PoisBean getPoisBean() {
            return poisBean;
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }

        @Override
        public BitmapDescriptor getBitmapDescriptor() {
            RelativeLayout userLayout = (RelativeLayout) View.inflate(MapActivity.this, R.layout.view_map_bubble, null);
            TextView usernameText = (TextView) userLayout.findViewById(R.id.tv_bubble_username);
            ImageView imageView = (ImageView) userLayout.findViewById(R.id.iv_bubble_head);
            //加载小图
            Picasso.with(MapActivity.this).
                    load(poisBean.getUrl() + "?imageMogr2/thumbnail/!100x100r/gravity/Center/crop/200x/blur/1x0/quality/20|imageslim").into(imageView);
            return BitmapDescriptorFactory.fromView(userLayout);
        }


    }

    private class HugeItem extends MyItem {
        public HugeItem(LocationBean.PoisBean bean) {
            super(bean);
        }

        @Override
        public BitmapDescriptor getBitmapDescriptor() {
            RelativeLayout userLayout = (RelativeLayout) View.inflate(MapActivity.this, R.layout.view_map_huge_bubble, null);
            TextView usernameText = (TextView) userLayout.findViewById(R.id.tv_huge_bubble_username);
            ImageView imageView = (ImageView) userLayout.findViewById(R.id.iv_huge_bubble_head);
            //加载小图
            Picasso.with(MapActivity.this).
                    load(super.poisBean.getUrl() + "?imageMogr2/thumbnail/!100x100r/gravity/Center/crop/200x/blur/1x0/quality/20|imageslim").into(imageView);
            return BitmapDescriptorFactory.fromView(userLayout);
        }

    }

    class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(final BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果

            double latitude = location.getLatitude();    //获取纬度信息
            double longitude = location.getLongitude();    //获取经度信息
            //拿到百度地图上定位的id
            //todo 不会自动恢复= =
            zoomIn(mapView.getMap(), new LatLng(location.getLatitude(), location.getLongitude()), 30f);
            if (locationId == null || locationId.equals("")) {
                HttpUtil.getUser(MapActivity.this, id, new RxStringCallback() {
                    @Override
                    public void onNext(Object tag, String response) {
                        Gson gson = new Gson();
                        LocationBean bean = gson.fromJson(response, LocationBean.class);
                        locationId = bean.getPois().get(0).getId();
                        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("locationId", locationId);
                        editor.apply();
                    }

                    @Override
                    public void onError(Object tag, Throwable e) {

                    }

                    @Override
                    public void onCancel(Object tag, Throwable e) {

                    }
                });
            } else {
                HttpUtil.updateLocate(MapActivity.this, locationId, latitude, longitude);
            }
            HttpUtil.updateMap(MapActivity.this, new RxStringCallback() {
                @Override
                public void onNext(Object tag, String response) {
                    Gson gson = new Gson();
                    LocationBean bean = gson.fromJson(response, LocationBean.class);
                    final BaiduMap baiduMap = mapView.getMap();
                    parameters.clear();
                    mLocationClient.stop();
                    if (bean.getPois() == null) {
                        return;
                    }
                    baiduMap.clear();
                    mClusterManager.clearItems();
                    myItems.clear();
                    for (LocationBean.PoisBean b : bean.getPois()) {
                        myItems.add(new MyItem(b));
                    }
                    addMarkers(myItems);

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