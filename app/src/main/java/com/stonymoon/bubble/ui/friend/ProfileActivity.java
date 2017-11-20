package com.stonymoon.bubble.ui.friend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.squareup.picasso.Picasso;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.ui.common.PhotoActivity;

import com.vondear.rxtools.activity.ActivityBase;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

public class ProfileActivity extends ActivityBase {


    @BindView(R.id.iv_profile_avatar)
    ImageView mIvAvatar;
    @BindView(R.id.tv_profile_username)
    TextView tvUsername;
    @BindView(R.id.tv_profile_signature)
    TextView tvSignature;

    private String phone;
    private String url;

    public static void startActivity(Context context, String phone) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra("phone", phone);
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
                    Toast.makeText(ProfileActivity.this, "请求发送失败", Toast.LENGTH_SHORT).show();
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
        initView();
    }


    protected void initView() {

        JMessageClient.getUserInfo(phone, new GetUserInfoCallback() {
            @Override
            public void gotResult(int i, String s, UserInfo userInfo) {
                tvUsername.setText(userInfo.getDisplayName());
                tvSignature.setText(userInfo.getSignature());
                url = userInfo.getExtra("url");
                Picasso.with(ProfileActivity.this).load(url).into(mIvAvatar);

            }
        });

        mIvAvatar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PhotoActivity.startActivity(ProfileActivity.this, url);
                return true;
            }
        });
    }

    @OnClick(R.id.iv_profile_back)
    void back() {
        finish();
    }

    @OnClick(R.id.iv_profile_location)
    void locate() {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("phone", phone);//创一个intent用于传值
        startActivity(intent);
    }





}
