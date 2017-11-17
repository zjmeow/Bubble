package com.stonymoon.bubble.ui.friend;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.squareup.picasso.Picasso;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.adapter.MyFragmentPagerAdapter;
import com.stonymoon.bubble.bean.LocationBean;
import com.stonymoon.bubble.util.AuthUtil;
import com.stonymoon.bubble.util.HttpUtil;
import com.stonymoon.bubble.util.LogUtil;
import com.stonymoon.bubble.util.MessageUtil;
import com.stonymoon.bubble.util.MyCallback;
import com.stonymoon.bubble.util.SpringScaleInterpolator;
import com.stonymoon.bubble.util.clusterutil.clustering.ClusterItem;
import com.stonymoon.bubble.util.clusterutil.clustering.ClusterManager;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.RxStringCallback;
import com.vondear.rxtools.RxImageTool;
import com.vondear.rxtools.RxTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.event.ContactNotifyEvent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.OfflineMessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;

//本地图显示附近的人
//动态地图另外开一个地图显示
//todo 优化地图页面构造
//todo 离线表情一次性发完
public class MapActivity extends AppCompatActivity {
    private static final String TAG = "MapActivity";
    public LocationClient mLocationClient = null;
    public BDAbstractLocationListener myListener = new MyLocationListener();
    @BindView(R.id.activity_map)
    CoordinatorLayout mainLayout;
    @BindView(R.id.map)
    MapView mapView;
    @BindView(R.id.map_bubble)
    RelativeLayout bubble;
    @BindView(R.id.iv_map_bubble_head)
    QMUIRadiusImageView headImage;
    @BindView(R.id.tv_map_bubble_username)
    TextView usernameText;
    @BindView(R.id.btn_map_send_message)
    FloatingActionButton button;
    @BindView(R.id.btn_map_friend)
    Button btnMapFriend;
    @BindView(R.id.btn_map_location)
    Button btnMapLocation;
    @BindView(R.id.btn_map_message)
    Button btnMapMessage;
    @BindView(R.id.iv_map_receive_emoji)
    ImageView ivReceiveEmoji;

    @BindView(R.id.iv_map_emoji_shit1)
    ImageView ivEmoji1;


    @BindView(R.id.ll_map_emoji)
    LinearLayout llEmoji;

    //管理聚合地图
    private ClusterManager<MyItem> mClusterManager;
    private List<MyItem> myItems = new ArrayList<>();
    private boolean isFirstLoacted = true;
    private boolean isSelected = false;
    private boolean isUpdateMap = true;
    private String locationId;
    private String id;
    private LocationBean.PoisBean chosenUserBean;
    private AnimatorSet showBubbleSet;
    private AnimatorSet receiveEmojiSet;
    private Map<String, Object> parameters = new HashMap<>();
    private LatLng myLatLng = new LatLng(0, 0);


    private MyCallback callback = new MyCallback(this);

    //用marker的id绑定信息，为点击回调提供信息

    public static void startActivity(Context context, String id, String locationId) {
        Intent intent = new Intent(context, MapActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("locationId", locationId);
        context.startActivity(intent);

    }


    @OnClick(R.id.iv_map_emoji_shit1)
    void sendEmoji1() {
        startSendEmoji(ivEmoji1);
    }


    //接收到事件的处理
    public void onEventMainThread(MessageEvent event) {
        Message message = event.getMessage();
        switch (message.getContentType()) {
            case custom:
                CustomContent customContent = (CustomContent) message.getContent();
                customContent.getStringExtra("emoji");
                receiveEmoji();
                //todo 判断并且添加多种表情
                break;

        }


        LogUtil.d(TAG, event.getMessage().toString());

    }

    public void onEventMainThread(OfflineMessageEvent event) {
        //获取事件发生的会话对象
        Conversation conversation = event.getConversation();
        List<Message> newMessageList = event.getOfflineMessageList();//获取此次离线期间会话收到的新消息列表
        for (Message message : newMessageList) {
            switch (message.getContentType()) {
                case custom:
                    CustomContent customContent = (CustomContent) message.getContent();
                    customContent.getStringExtra("emoji");
                    receiveEmoji();
                    //todo 判断并且添加多种表情
                    break;


            }

        }

    }

    public void onEvent(ContactNotifyEvent event) {
        //获取事件发生的会话对象
        LogUtil.v("MapActivity", "receive friend" + event.toString());
        String target = JMessageClient.getMyInfo().getUserName();
        showMessagePositiveDialog(event.getFromUsername(), target);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            Transition leftExplode = TransitionInflater.from(this).inflateTransition(R.transition.left_transition);
            Transition rightExplode = TransitionInflater.from(this).inflateTransition(R.transition.right_transition);
            getWindow().setExitTransition(rightExplode);
            getWindow().setEnterTransition(leftExplode);

        }
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
        id = intent.getStringExtra("id");
        locationId = intent.getStringExtra("locationId");
        setMap();
        initLocate();
        mLocationClient.start();
        initAnimation();
        //初始化像素工具
        RxTool.init(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JMessageClient.unRegisterEventReceiver(this);
        mapView.onDestroy();
        mLocationClient.stop();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();

        if (!isSelected) {
            isUpdateMap = true;
            mLocationClient.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        if (!isSelected) {
            isUpdateMap = false;
            //mLocationClient.stop();
        }
    }

    private void setMap() {
        final BaiduMap baiduMap = mapView.getMap();
        mapView.showZoomControls(false);
        mapView.showScaleControl(false);
        baiduMap.setCompassEnable(false);
        // 删除百度地图logo
        mapView.removeViewAt(1);

        mClusterManager = new ClusterManager<MyItem>(this, baiduMap);
        mClusterManager.setClusterDistance(100);
        baiduMap.setOnMapStatusChangeListener(mClusterManager);
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        baiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
                closeBubble();
            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {

            }
        });


        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
            @Override
            public boolean onClusterItemClick(MyItem item) {
                zoomIn(baiduMap, item.getPosition(), 30f);
                LocationBean.PoisBean bean = item.getPoisBean();
                chosenUserBean = bean;
                mClusterManager.clearItems();
                baiduMap.clear();
                mClusterManager.cluster();
                mLocationClient.stop();
                openBottomSheet();
                //当点击item时删除全部item并且把自定义view引入
                //此时为选中状态，当用户离开选中状态时，隐藏自定义view
                //加载小图

                Picasso.with(MapActivity.this).
                        load(bean.getUrl() + "?imageMogr2/thumbnail/!150x150r/gravity/Center/crop/200x/blur/1x0/quality/20|imageslim")
                        .into(headImage);
                //usernameText.setText(bean.getUsername());
                bubble.setVisibility(View.VISIBLE);
                isSelected = true;
                showBubbleSet.start();
                return false;
            }


        });

        // 绑定 Marker 被点击事件
        baiduMap.setOnMarkerClickListener(mClusterManager);

    }


    @Override
    public void onBackPressed() {
        if (isSelected) {
            closeBubble();
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

        option.setIgnoreKillProcess(true);
        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.setEnableSimulateGps(false);
//可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false
        mLocationClient.setLocOption(option);
//mLocationClient为第二步初始化过的LocationClient对象

    }


    private void initAnimation() {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(bubble, "scaleX", 1.0f, 1.8f);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(bubble, "scaleY", 1.0f, 1.8f);
        showBubbleSet = new AnimatorSet();
        showBubbleSet.setDuration(1000);
        showBubbleSet.setInterpolator(new SpringScaleInterpolator(0.4f));
        showBubbleSet.playTogether(animatorX, animatorY);

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
                                dialog.dismiss();
                                break;
                            case 1:
                                ContactManager.acceptInvitation(targetUsername, "", callback);
                                dialog.dismiss();
                                break;
                        }
                    }
                })
                .show();
    }

    @OnClick(R.id.btn_map_send_message)
    void sendMessage() {
        if (chosenUserBean == null) {
            return;
        }
        //startSendEmoji(button);
        receiveEmoji();
    }

    @OnClick(R.id.map_bubble)
    void startProfile() {
        new QMUIDialog.MessageDialogBuilder(MapActivity.this)
                .setTitle(chosenUserBean.getUsername())
                .setMessage(chosenUserBean.getModifTime())
                .addAction("查看资料", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        ProfileActivity.startActivity(MapActivity.this,
                                chosenUserBean.getPhone()
                        );
                        dialog.dismiss();
                    }
                })
                .addAction("聊天", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        ChatActivity.startActivity(MapActivity.this, chosenUserBean.getPhone());
                        dialog.dismiss();

                    }
                })
                .show();


    }

    void closeBubble() {
        if (isSelected) {
            mLocationClient.start();
            bubble.clearAnimation();
            bubble.setVisibility(View.GONE);
            isSelected = false;
            addMarkers(myItems);
            closeBottomSheet();
        }
    }

    private void startSendEmoji(final View view) {
        MessageUtil.sendEmoji(chosenUserBean.getPhone());
        ViewGroup parent = (ViewGroup) view.getParent();
        view.setClickable(false);
        final float x = view.getX();
        final float y = view.getY();
        float dy = parent.getY() + y;
        float dx = parent.getX() + x;

        ObjectAnimator animator1 = ObjectAnimator.ofFloat(
                view,
                "translationX",
                bubble.getX() - dx);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(
                view,
                "translationY",
                bubble.getY() - dy);

        AnimatorSet set = new AnimatorSet();
        set.setDuration(200);
        set.play(animator1).with(animator2);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                showBubbleSet.start();
                //((ViewGroup)view.getParent()).removeView(imageView);
                view.setX(x);
                view.setY(y);
                view.setClickable(true);
            }
        });
        set.start();

    }

    @OnClick({R.id.btn_map_friend, R.id.btn_map_location, R.id.btn_map_message})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_map_friend:
                FriendActivity.startActivity(this);
                break;
            case R.id.btn_map_location:
                zoomIn(mapView.getMap(), myLatLng, 30);
                break;
            case R.id.btn_map_message:
                MessageListActivity.startActivity(this);
                break;
        }
    }

    private void closeBottomSheet() {

        ObjectAnimator animator = ObjectAnimator.ofFloat(
                llEmoji,
                "translationY",
                RxImageTool.dp2px(120));
        animator.setDuration(400);
        animator.start();

    }

    private void openBottomSheet() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(
                llEmoji,
                "translationY",
                -RxImageTool.dp2px(120));
        animator.setDuration(400);
        animator.start();

    }

    private void receiveEmoji() {

        ivReceiveEmoji.setVisibility(View.VISIBLE);
        mainLayout.bringChildToFront(ivReceiveEmoji);
        AnimationSet sendEmojiSet = new AnimationSet(true);

        ScaleAnimation scaleAnimation = new ScaleAnimation(1, 20f, 1, 20f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        );
        sendEmojiSet.addAnimation(scaleAnimation);
        sendEmojiSet.setDuration(300);
        sendEmojiSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ivReceiveEmoji.clearAnimation();
                ivReceiveEmoji.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        ivReceiveEmoji.startAnimation(sendEmojiSet);

    }

    private void locateUserByPhone(String phone) {

        for (MyItem item : myItems) {
            String p = item.getPoisBean().getPhone();
            if (p == null) {
                continue;
            } else if (p.equals(phone)) {
                zoomIn(mapView.getMap(), item.getPosition(), 30f);
                LocationBean.PoisBean bean = item.getPoisBean();
                chosenUserBean = bean;
                mClusterManager.clearItems();
                mapView.getMap().clear();
                mClusterManager.cluster();
                mLocationClient.stop();
                openBottomSheet();
                Picasso.with(MapActivity.this).
                        load(bean.getUrl() + "?imageMogr2/thumbnail/!150x150r/gravity/Center/crop/200x/blur/1x0/quality/20|imageslim")
                        .into(headImage);
                bubble.setVisibility(View.VISIBLE);
                isSelected = true;
                showBubbleSet.start();
                return;
            }

        }

        Toast.makeText(this, "用户距离太远", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        locateUserByPhone(intent.getStringExtra("phone"));
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
            QMUIRadiusImageView imageView = (QMUIRadiusImageView) userLayout.findViewById(R.id.iv_bubble_head);
            //加载小图
            Picasso.with(MapActivity.this).
                    load(poisBean.getUrl() + "?imageMogr2/thumbnail/!150x150r/gravity/Center/crop/200x/blur/1x0/quality/20|imageslim").into(imageView);
            return BitmapDescriptorFactory.fromView(userLayout);
        }

    }

    private class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(final BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果

            double latitude = location.getLatitude();    //获取纬度信息
            double longitude = location.getLongitude();    //获取经度信息
            myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            //拿到百度地图上定位的id
            if (isFirstLoacted) {
                zoomIn(mapView.getMap(), myLatLng, 30f);
                isFirstLoacted = false;
            }

            if (locationId == null || locationId.equals("")) {
                HttpUtil.getUser(MapActivity.this, id, new RxStringCallback() {
                    @Override
                    public void onNext(Object tag, String response) {
                        Gson gson = new Gson();
                        LocationBean bean = gson.fromJson(response, LocationBean.class);
                        locationId = bean.getPois().get(0).getId();
                        AuthUtil.setLocationId("locationId");
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


            if (!isUpdateMap) {
                return;
            }

            HttpUtil.updateMap(MapActivity.this, new RxStringCallback() {
                @Override
                public void onNext(Object tag, String response) {
                    Gson gson = new Gson();
                    LocationBean bean = gson.fromJson(response, LocationBean.class);
                    final BaiduMap baiduMap = mapView.getMap();
                    parameters.clear();
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