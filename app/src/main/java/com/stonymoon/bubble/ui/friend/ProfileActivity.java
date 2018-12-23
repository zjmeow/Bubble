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

import com.google.gson.Gson;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.squareup.picasso.Picasso;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.adapter.ProfileBubbleAdapter;
import com.stonymoon.bubble.base.StatusBarLightActivity;
import com.stonymoon.bubble.bean.BubbleBean;
import com.stonymoon.bubble.ui.common.MyProfileActivity;
import com.stonymoon.bubble.ui.common.PhotoActivity;
import com.stonymoon.bubble.util.AuthUtil;
import com.stonymoon.bubble.util.HttpUtil;
import com.stonymoon.bubble.util.LogUtil;
import com.stonymoon.bubble.util.UrlUtil;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.RxStringCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;


public class ProfileActivity extends StatusBarLightActivity {


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
    private List<BubbleBean.DataBean> mList = new ArrayList<>();
    private ProfileBubbleAdapter adapter = new ProfileBubbleAdapter(mList);


    public static void startActivity(Context context, String phone, String uid) {
        if (AuthUtil.getPhone().equals(phone)) {
            MyProfileActivity.startActivity(context);
            return;
        }
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra("phone", phone);
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

        JMessageClient.getUserInfo(phone, new GetUserInfoCallback() {
            @Override
            public void gotResult(int i, String s, UserInfo userInfo) {

                tvUsername.setText(userInfo.getDisplayName());
                tvSignature.setText(userInfo.getSignature());
                url = userInfo.getExtra("url");
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
        String url = UrlUtil.guestGetBubble(uid);
        HttpUtil.sendHttpRequest(this).rxGet(url, new HashMap<String, Object>(), new RxStringCallback() {
            @Override
            public void onNext(Object tag, String response) {
                Gson gson = new Gson();
                BubbleBean bean = gson.fromJson(response, BubbleBean.class);
                mList.addAll(bean.getData());
                Collections.reverse(mList);
                if (mList.isEmpty()) {
                    tvNoBubble.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onError(Object tag, Throwable e) {
                tvNoBubble.setVisibility(View.VISIBLE);
                LogUtil.e(TAG, e.getMessage());
            }

            @Override
            public void onCancel(Object tag, Throwable e) {

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }


}
