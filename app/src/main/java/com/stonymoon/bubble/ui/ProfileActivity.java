package com.stonymoon.bubble.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qmuiteam.qmui.widget.QMUIAppBarLayout;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.stonymoon.bubble.R;
import com.vondear.rxtools.RxPhotoTool;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.radius_image_view_profile_user_head)
    QMUIRadiusImageView userHeadImage;
    @BindView(R.id.tv_profile_username)
    TextView usernameText;
    @BindView(R.id.tv_profile_emoji)
    TextView emojiText;
    @BindView(R.id.topbar_profile)
    QMUITopBar topbar;


    public static void startActivity(Context context, String url, String username, String userId) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("username", username);
        intent.putExtra("userId", userId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);




        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        String userName = intent.getStringExtra("userName");
        String userId = intent.getStringExtra("userId");
        Glide.with(this).load(url).into(userHeadImage);

        usernameText.setText(userName);
        emojiText.setText(userId);

    }

    @OnClick(R.id.radius_image_view_profile_user_head)
    public void onViewClicked() {
        RxPhotoTool.openLocalImage(this);


    }


}
