package com.stonymoon.bubble.ui.common;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.stonymoon.bubble.R;
import com.stonymoon.bubble.ui.auth.LoginActivity;
import com.stonymoon.bubble.ui.auth.RegisterActivity;
import com.stonymoon.bubble.ui.friend.MapActivity;
import com.stonymoon.bubble.util.AuthUtil;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

public class SplashActivity extends AppCompatActivity {

    @OnClick(R.id.rl_splash_login)
    void login() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.rl_splash_register)
    void register() {
        Intent intent = new Intent(SplashActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        attemptLogin();
        ButterKnife.bind(this);
    }


    //如果登录过则直接进入主页面
    private void attemptLogin() {
        //判断是否已经登录
        String token = AuthUtil.getToken();
        String id = AuthUtil.getId();
        String phone = AuthUtil.getPhone();
        String password = AuthUtil.getPassword();
        if (!token.equals("") && !id.equals("")) {
            //已经登录过，直接进入定位页面
            MapActivity.startActivity(this, id);
            JMessageClient.login(phone, password, new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    finish();
                }
            });


        }


    }
}
