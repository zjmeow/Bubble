package com.stonymoon.bubble.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.stonymoon.bubble.R;
import com.vondear.rxtools.RxPhotoTool;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends Activity {

    @BindView(R.id.radius_image_view_profile_user_head)
    QMUIRadiusImageView UserHeadImage;
    @BindView(R.id.tv_profile_username)
    TextView UsernameText;
    @BindView(R.id.tv_profile_emoji)
    TextView EmojiText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.radius_image_view_profile_user_head)
    public void onViewClicked() {
        RxPhotoTool.openLocalImage(this);


    }
}
