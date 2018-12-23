package com.stonymoon.bubble.ui.friend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.squareup.picasso.Picasso;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.adapter.ProfileBubbleAdapter;
import com.stonymoon.bubble.api.BaseDataManager;
import com.stonymoon.bubble.api.serivces.BubbleService;
import com.stonymoon.bubble.api.serivces.UserService;
import com.stonymoon.bubble.base.StatusBarLightActivity;
import com.stonymoon.bubble.bean.BubbleListBean;
import com.stonymoon.bubble.bean.UserProfileBean;
import com.stonymoon.bubble.ui.common.MyProfileActivity;
import com.stonymoon.bubble.ui.common.PhotoActivity;
import com.stonymoon.bubble.util.AuthUtil;
import com.stonymoon.bubble.util.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.api.BasicCallback;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class ProfileActivity extends StatusBarLightActivity {


    private static final String TAG = "ProfileActivity";
    @BindView(R.id.iv_profile_avatar)
    ImageView mIvAvatar;
    @BindView(R.id.tv_profile_username)
    TextView tvUsername;
    @BindView(R.id.tv_profile_signature)
    TextView tvSignature;
    @BindView(R.id.iv_profile_background)
    ImageView ivBackground;
    @BindView(R.id.toolbar_profile)
    Toolbar toolbar;
    @BindView(R.id.recycler_profile_bubble)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_profile_no_bubble)
    TextView tvNoBubble;
    private String phone;
    private String url;
    private String uid;
    private List<BubbleListBean.DataBean> mList = new ArrayList<>();
    private ProfileBubbleAdapter adapter = new ProfileBubbleAdapter(mList);

    public static void startActivity(Context context, String uid) {
        if (AuthUtil.getId().equals(uid)) {
            MyProfileActivity.startActivity(context);
            return;
        }
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra("uid", uid);
        context.startActivity(intent);

    }

    @OnClick(R.id.btn_profile_make_friend)
    void sendMessage() {
        ContactManager.sendInvitationRequest(phone, "", "请求加你为好友", new BasicCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage) {
                Log.v("SelectPhoto", responseMessage);
                Toast.makeText(ProfileActivity.this, responseMessage, Toast.LENGTH_SHORT);
                if (0 == responseCode) {
                    Toast.makeText(ProfileActivity.this, "发送请求成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "已经是好友了哟", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QMUIStatusBarHelper.translucent(this);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        uid = intent.getStringExtra("uid");
        setBar();
        initView();
        initBubble();

    }


    protected void initView() {
        BaseDataManager.getHttpManager()
                .create(UserService.class)
                .getUserDetail(Integer.valueOf(uid))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<UserProfileBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(java.lang.Throwable e) {

                    }

                    @Override
                    public void onNext(UserProfileBean userProfileBean) {
                        UserProfileBean.DataBean bean = userProfileBean.getData();
                        tvUsername.setText(bean.getUsername());
                        tvSignature.setText(bean.getInfo());
                        url = bean.getAvatar();
                        Picasso.with(ProfileActivity.this).load(url).into(mIvAvatar);
                        Picasso.with(ProfileActivity.this)
                                .load(url)
                                .transform(new jp.wasabeef.picasso.transformations.BlurTransformation(ProfileActivity.this, 14, 5))
                                .into(ivBackground);
                    }
                });


        mIvAvatar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PhotoActivity.startActivity(ProfileActivity.this, url);
                return true;
            }
        });
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


    }


    void locate() {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("phone", phone);//创一个intent用于传值
        startActivity(intent);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_locate:
                locate();
                break;
        }
        return true;
    }


    private void setBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
        }
    }

    private void initBubble() {
        BaseDataManager.getHttpManager()
                .create(BubbleService.class)
                .getBubbleByUserId(Integer.valueOf(uid))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<BubbleListBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(java.lang.Throwable e) {
                        tvNoBubble.setVisibility(View.VISIBLE);
                        LogUtil.e(TAG, e.getMessage());
                    }

                    @Override
                    public void onNext(BubbleListBean bubbleListBean) {

                        mList.addAll(bubbleListBean.getData());
                        Collections.reverse(mList);
                        if (mList.isEmpty()) {
                            tvNoBubble.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }


}
