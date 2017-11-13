package com.stonymoon.bubble.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.Recorder;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.squareup.picasso.Picasso;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.bean.ContentBean;
import com.stonymoon.bubble.bean.JUserBean;
import com.stonymoon.bubble.util.HttpUtil;

import com.stonymoon.bubble.util.LogUtil;
import com.tamic.novate.callback.RxStringCallback;
import com.vondear.rxtools.RxBarTool;
import com.vondear.rxtools.RxPhotoTool;
import com.vondear.rxtools.RxSPTool;
import com.vondear.rxtools.activity.ActivityBase;
import com.vondear.rxtools.view.dialog.RxDialogChooseImage;
import com.vondear.rxtools.view.dialog.RxDialogScaleView;
import com.vondear.rxtools.view.dialog.RxDialogSureCancel;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import org.apaches.commons.codec.digest.DigestUtils;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupIDListCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.vondear.rxtools.view.dialog.RxDialogChooseImage.LayoutType.TITLE;

public class ProfileActivity extends ActivityBase {

    //todo 这个是打开别人的资料，只需要展示出资料即可

    @BindView(R.id.iv_profile_avatar)
    ImageView mIvAvatar;
    @BindView(R.id.tv_profile_username)
    TextView tvUsername;
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
                    Toast.makeText(ProfileActivity.this, "发送请求成功", Toast.LENGTH_SHORT);
                } else {
                    Toast.makeText(ProfileActivity.this, "请求发送失败", Toast.LENGTH_SHORT);
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
                Picasso.with(ProfileActivity.this).load(userInfo.getExtra("url")).into(mIvAvatar);

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


}
