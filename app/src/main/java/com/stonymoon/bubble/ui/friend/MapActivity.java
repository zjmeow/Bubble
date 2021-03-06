package com.stonymoon.bubble.ui.friend;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.bumptech.glide.Glide;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.squareup.picasso.Picasso;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.api.BaseDataManager;
import com.stonymoon.bubble.api.serivces.UserService;
import com.stonymoon.bubble.base.BaseActivity;
import com.stonymoon.bubble.bean.LocationBean;
import com.stonymoon.bubble.bean.MapUserBean;
import com.stonymoon.bubble.ui.common.MyProfileActivity;
import com.stonymoon.bubble.ui.share.MapShareActivity;
import com.stonymoon.bubble.util.AuthUtil;
import com.stonymoon.bubble.util.DateUtil;
import com.stonymoon.bubble.util.HttpUtil;
import com.stonymoon.bubble.util.LogUtil;
import com.stonymoon.bubble.util.MapUtil;
import com.stonymoon.bubble.util.MessageUtil;
import com.stonymoon.bubble.util.MyCallback;
import com.stonymoon.bubble.util.SpringScaleInterpolator;
import com.stonymoon.bubble.util.clusterutil.clustering.ClusterItem;
import com.stonymoon.bubble.util.clusterutil.clustering.ClusterManager;
import com.stonymoon.bubble.view.FloatingMenu;
import com.stonymoon.bubble.view.MyDialog;
import com.vondear.rxtools.RxImageTool;
import com.vondear.rxtools.RxTool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.event.ContactNotifyEvent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.NotificationClickEvent;
import cn.jpush.im.android.api.event.OfflineMessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.stonymoon.bubble.util.MapUtil.clearMap;
import static com.stonymoon.bubble.util.MapUtil.zoomIn;

//本地图显示附近的人
//动态地图另外开一个地图显示

public class MapActivity extends BaseActivity {
    private static final String TAG = "MapActivity";
    public LocationClient mLocationClient = null;
    public BDAbstractLocationListener myListener = new MyLocationListener();
    @BindView(R.id.map)
    MapView mapView;
    @BindView(R.id.map_bubble)
    ConstraintLayout bubble;
    @BindView(R.id.iv_map_bubble_head)
    QMUIRadiusImageView headImage;
    @BindView(R.id.iv_map_receive_emoji)
    ImageView ivReceiveEmoji;
    @BindView(R.id.iv_map_emoji0)
    ImageView ivEmoji0;
    @BindView(R.id.iv_map_emoji1)
    ImageView ivEmoji1;
    @BindView(R.id.iv_map_emoji2)
    ImageView ivEmoji2;
    @BindView(R.id.iv_map_emoji3)
    ImageView ivEmoji3;

    @BindView(R.id.fl_map_emoji)
    FrameLayout llEmoji;
    @BindView(R.id.iv_bottom_sheet_big_emoji)
    ImageView ivBigEmoji;
    @BindView(R.id.floating_menu_map)
    FloatingMenu mFloatingMenu;

    @BindView(R.id.iv_map_receive_message)
    ImageView ivMessage;


    //管理聚合地图
    private ClusterManager<MyItem> mClusterManager;
    private List<MyItem> myItems = new ArrayList<>();
    private List<MyItem> cacheItems = new ArrayList<>();
    private HashMap<String, Queue<String>> receivedEmojiMap = new HashMap<>();
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

    private MyDialog myDialog;

    private MyCallback callback = new MyCallback(this);

    //用marker的id绑定信息，为点击回调提供信息

    public static void startActivity(Context context, String id) {
        Intent intent = new Intent(context, MapActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);

    }


    @OnClick({R.id.iv_map_emoji0, R.id.iv_map_emoji1, R.id.iv_map_emoji2, R.id.iv_map_emoji3, R.id.iv_map_message})
    void sendEmoji(View view) {
        int id = view.getId();
        switch (id) {
            //todo 取一个优雅的名字
            case R.id.iv_map_emoji0:
                startSendEmoji(ivEmoji0, "e0");
                break;
            case R.id.iv_map_emoji1:
                startSendEmoji(ivEmoji1, "e1");
                break;
            case R.id.iv_map_emoji2:
                startSendEmoji(ivEmoji2, "e2");
                break;
            case R.id.iv_map_emoji3:
                startSendEmoji(ivEmoji3, "e3");
                break;

            case R.id.iv_map_message:
                ChatActivity.startActivity(MapActivity.this, chosenUserBean.getPhone());
                break;
            default:
                break;
        }


    }


    @OnClick(R.id.iv_map_receive_message)
    void getMessage() {
        for (String key : receivedEmojiMap.keySet()) {
            locateUserByPhone(key);
            receiveOfflineEmoji(receivedEmojiMap.get(key));
            receivedEmojiMap.remove(key);
            break;
        }

        if (receivedEmojiMap.isEmpty()) {
            ivMessage.setVisibility(View.GONE);

        }

    }


    //接收到事件的处理
    public void onEventMainThread(MessageEvent event) {
        Message message = event.getMessage();
        switch (message.getContentType()) {
            case custom:
                String phone = message.getFromUser().getUserName();
                CustomContent customContent = (CustomContent) message.getContent();
                String emojiName = customContent.getStringValue("emoji");
                if (chosenUserBean != null && chosenUserBean.getPhone().equals(phone)) {
                    receiveEmoji(emojiName);

                } else if (receivedEmojiMap.get(phone) == null) {
                    receivedEmojiMap.put(phone, new LinkedList<String>());
                    receivedEmojiMap.get(phone)
                            .add(emojiName);

                } else {
                    receivedEmojiMap.get(phone)
                            .add(emojiName);
                }
                ivMessage.setVisibility(View.VISIBLE);
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
                    String phone = message.getFromUser().getUserName();
                    CustomContent customContent = (CustomContent) message.getContent();
                    if (receivedEmojiMap.get(phone) == null) {
                        receivedEmojiMap.put(phone, new LinkedList<String>());
                        receivedEmojiMap.get(phone)
                                .add(customContent.getStringValue("emoji"));

                    } else {
                        receivedEmojiMap.get(phone)
                                .add(customContent.getStringValue("emoji"));
                    }

                    break;

            }

        }
        if (!receivedEmojiMap.isEmpty()) {
            ivMessage.setVisibility(View.VISIBLE);

        }

    }

    public void onEvent(ContactNotifyEvent event) {
        //获取事件发生的会话对象
        LogUtil.v("MapActivity", "receive friend" + event.toString());
        JMessageClient.getUserInfo(event.getFromUsername(), new GetUserInfoCallback() {
            @Override
            public void gotResult(int i, String s, UserInfo userInfo) {
                showMessagePositiveDialog(userInfo.getDisplayName(), userInfo.getUserName());
            }
        });


    }

    public void onEvent(NotificationClickEvent event) {
        //获取事件发生的会话对象
        LogUtil.v("MapActivity", "receive friend" + event.toString());
        UserInfo info = event.getMessage().getFromUser();
        ChatActivity.startActivity(this, info.getUserName());

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //设置沉浸式状态栏
        QMUIStatusBarHelper.translucent(this);
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setMapCustomFile();
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        JMessageClient.registerEventReceiver(this);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        setMap();
        mLocationClient = MapUtil.getDefaultLocationClient(myListener);
        mLocationClient.start();
        initAnimation();
        //初始化像素工具
        RxTool.init(this);
        ivBigEmoji.bringToFront();
        setFloatingMenu();
        myDialog = new MyDialog(this);
        myDialog.showProgress("地图加载中");


    }

    private void setFloatingMenu() {
        mFloatingMenu.setMoveDistance(RxImageTool.dp2px(60));
        mFloatingMenu.setOnclickListener(new FloatingMenu.OnPictureClickListener() {
            @Override
            public void onFirstClick() {

                FriendActivity.startActivity(MapActivity.this);
            }

            @Override
            public void onSecondClick() {
                MessageListActivity.startActivity(MapActivity.this);
            }

            @Override
            public void onThirdClick() {
                Intent intent = new Intent(MapActivity.this, MyProfileActivity.class);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        JMessageClient.unRegisterEventReceiver(this);
        mapView.onDestroy();
        //JMessageClient.logout();

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
        clearMap(mapView);
        mClusterManager = new ClusterManager<>(this, baiduMap);
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
        baiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mapView.setMapCustomEnable(true);


            }
        });


        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
            @Override
            public boolean onClusterItemClick(MyItem item) {
                zoomIn(baiduMap, item.getPosition(), 30f);
                LocationBean.PoisBean bean = item.getPoisBean();
                if (String.valueOf(bean.getUid()).equals(AuthUtil.getId())) {
                    return true;
                }
                chosenUserBean = bean;
                mClusterManager.clearItems();
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

    public void addMarkers(List<MyItem> items) {
        // 添加Marker点
        //addItems添加一组
        mClusterManager.addItems(items);
        mClusterManager.cluster();

    }

    private void initAnimation() {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(bubble, "scaleX", 1.0f, 1.5f);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(bubble, "scaleY", 1.0f, 1.5f);
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
                                ContactManager.declineInvitation(targetUsername, "", "", callback);
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


    @OnClick(R.id.iv_map_bubble_head)
    void startProfile() {
        new QMUIDialog.MessageDialogBuilder(MapActivity.this)
                .setTitle(chosenUserBean.getUsername())
                .setMessage(DateUtil.formatTime((chosenUserBean.getModifTime())))
                .addAction("查看资料", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        ProfileActivity.startActivity(MapActivity.this,
                                chosenUserBean.getUid() + ""

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

    @OnClick(R.id.iv_map_switch)
    void changeMap() {
        Intent intent = new Intent(MapActivity.this, MapShareActivity.class);
        startActivity(intent);

    }


    void closeBubble() {
        if (isSelected) {
            mLocationClient.start();
            bubble.clearAnimation();
            bubble.setVisibility(View.GONE);
            isSelected = false;
            chosenUserBean = null;
            addMarkers(myItems);
            closeBottomSheet();
        }
    }

    private void startSendEmoji(final View view, String emojiName) {
        MessageUtil.sendEmoji(chosenUserBean.getPhone(), emojiName);
        ViewGroup parent = (ViewGroup) view.getParent();
        view.setClickable(false);
        final float x = view.getX();
        final float y = view.getY();
        float dy = parent.getY() + y + ((ViewGroup) (parent.getParent())).getY();
        float dx = parent.getX() + x + ((ViewGroup) (parent.getParent())).getX();

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

    @OnClick(R.id.iv_map_location)
    void locate() {
        mapView.setMapCustomEnable(true);
        zoomIn(mapView.getMap(), myLatLng, 18);
        mClusterManager.clearItems();
        mClusterManager.cluster();
        addMarkers(myItems);

    }

    private void closeBottomSheet() {

        ObjectAnimator animator = ObjectAnimator.ofFloat(
                llEmoji,
                "translationY",
                RxImageTool.dp2px(210));
        animator.setDuration(400);
        animator.start();

    }

    private void openBottomSheet() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(
                llEmoji,
                "translationY",
                -RxImageTool.dp2px(210));
        animator.setDuration(400);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();

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
                        load(bean.getUrl())
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

    private void receiveEmoji(String emojiName) {
        switch (emojiName) {
            case "e0":
                Glide.with(this).load(R.drawable.shit).into(ivReceiveEmoji);
                break;
            case "e1":
                Glide.with(this).load(R.drawable.emoji_cry).into(ivReceiveEmoji);
                break;
            case "e2":
                Glide.with(this).load(R.drawable.emoji_love).into(ivReceiveEmoji);
                break;
            case "e3":
                Glide.with(this).load(R.drawable.emoji_boring).into(ivReceiveEmoji);
                break;
            default:
                break;
        }

        ivReceiveEmoji.setVisibility(View.VISIBLE);
        final AnimationSet sendEmojiSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, 15f, 1, 15f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        );
        sendEmojiSet.addAnimation(scaleAnimation);
        sendEmojiSet.setDuration(400);
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

    private void receiveOfflineEmoji(final Queue<String> queue) {

        String emojiName = queue.remove();

        switch (emojiName) {
            case "e0":
                Glide.with(this).load(R.drawable.shit).into(ivReceiveEmoji);
                break;
            case "e1":
                Glide.with(this).load(R.drawable.emoji_cry).into(ivReceiveEmoji);
                break;
            case "e2":
                Glide.with(this).load(R.drawable.emoji_love).into(ivReceiveEmoji);
                break;
            case "e3":
                Glide.with(this).load(R.drawable.emoji_boring).into(ivReceiveEmoji);
                break;
            default:
                break;
        }
        ivReceiveEmoji.setVisibility(View.VISIBLE);
        final AnimationSet sendEmojiSet = new AnimationSet(true);

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
                if (!queue.isEmpty()) {
                    receiveOfflineEmoji(queue);
                } else {
                    ivReceiveEmoji.clearAnimation();
                    ivReceiveEmoji.setVisibility(View.GONE);
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        ivReceiveEmoji.startAnimation(sendEmojiSet);

    }


    private void setMapCustomFile() {
        FileOutputStream out = null;
        InputStream inputStream = null;
        try {
            inputStream = getAssets().open("custom_configdir.txt");
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            String moduleName = getFilesDir().getAbsolutePath();
            File f = new File(moduleName + "/map_style.json");
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            out = new FileOutputStream(f);
            out.write(b);
            MapView.setCustomMapStylePath(moduleName + "/map_style.json");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private class MyItem implements ClusterItem {
        private final LatLng mPosition;
        private final LocationBean.PoisBean poisBean;


        public MyItem(MapUserBean.DataBean bean) {
            mPosition = new LatLng(bean.getLat(), bean.getLng());
            this.poisBean = new LocationBean.PoisBean();
            poisBean.setId(bean.getId() + "");
            poisBean.setUid(bean.getId());
            poisBean.setUrl(bean.getAvatar());
            poisBean.setModifyTime(bean.getLoginTime());
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
            Picasso.with(MapActivity.this).
                    load(poisBean.getUrl()).
                    placeholder(R.drawable.avatar_holder)
                    .into(imageView);
            return BitmapDescriptorFactory.fromView(userLayout);
        }

        @Override
        public boolean equals(Object obj) {
            //todo 更好的equals
            MyItem that = (MyItem) obj;
            if (this.getPosition().latitude == that.getPosition().latitude &&
                    this.getPosition().longitude == that.getPosition().longitude) {
                return true;

            }


            return false;
        }
    }

    private class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(final BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果

            double latitude = location.getLatitude();    //获取纬度信息
            double longitude = location.getLongitude();    //获取经度信息
            myLatLng = new LatLng(latitude, longitude);
            //拿到百度地图上定位的id
            if (isFirstLoacted) {
                myDialog.getInstance().dismiss();
                zoomIn(mapView.getMap(), myLatLng, 30f);
                isFirstLoacted = false;
            }

            HttpUtil.updateLocate(latitude, longitude);
            if (!isUpdateMap) {
                return;
            }


            BaseDataManager.getHttpManager()
                    .create(UserService.class)
                    .getAroundUsers(latitude, longitude)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<MapUserBean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            LogUtil.e(TAG, e.toString());
                        }

                        @Override
                        public void onNext(MapUserBean bean) {
                            parameters.clear();
                            if (bean.getData() == null) {
                                return;
                            }
                            //这里在for循环里判断，如果有移动则重绘，否则不重绘
                            //需要判断是否存在新的用户，然后判断旧的用户是否移动，移动则重绘。
                            //mClusterManager.clearItems();
                            myItems.clear();
                            for (MapUserBean.DataBean b : bean.getData()) {
                                myItems.add(new MyItem(b));
                            }
                            for (MyItem newItem : myItems) {
                                Boolean isInMap = false;
                                for (MyItem mapItem : cacheItems) {
                                    //如果两个id相同，判断是否移动
                                    if (newItem.getPoisBean().getId().equals(mapItem.getPoisBean().getId())) {
                                        isInMap = true;
                                        if (!mapItem.equals(newItem)) {
                                            //发生了移动，重绘
                                            mClusterManager.removeItem(mapItem);
                                            mClusterManager.addItem(newItem);
                                        } else {
                                            break;
                                        }

                                    }

                                }

                                if (!isInMap) {
                                    mClusterManager.addItem(newItem);
                                }


                            }
                            cacheItems.clear();
                            cacheItems.addAll(myItems);
                            mClusterManager.cluster();
                            //addMarkers(myItems);
                            myLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                        }

                    });


        }


    }


}