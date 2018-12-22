package com.stonymoon.bubble.ui.common;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.base.BaseActivity;
import com.stonymoon.bubble.bean.JUserBean;
import com.stonymoon.bubble.bean.LoginBean;
import com.stonymoon.bubble.ui.auth.LoginActivity;
import com.stonymoon.bubble.ui.auth.RegisterActivity;
import com.stonymoon.bubble.ui.friend.MapActivity;
import com.stonymoon.bubble.util.AuthUtil;
import com.stonymoon.bubble.util.HttpUtil;
import com.stonymoon.bubble.util.LogUtil;
import com.stonymoon.bubble.util.UrlUtil;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.RxStringCallback;
import com.vondear.rxtools.RxPermissionsTool;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

public class SplashActivity extends BaseActivity {
    private Map<String, Object> parameters = new HashMap<String, Object>();
    private LoginBean bean;
    @OnClick(R.id.btn_splash_login)
    void login() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_splash_register)
    void register() {
        Intent intent = new Intent(SplashActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QMUIStatusBarHelper.translucent(this);
        setContentView(R.layout.activity_splash);
        askForPermission();
        attemptLogin();
        //todo 极光debug，发布时要去掉
        JMessageClient.setDebugMode(true);
        //初始化极光推送,是否开启缓存消息
        JMessageClient.init(SplashActivity.this, true);
        ButterKnife.bind(this);
        QMUIStatusBarHelper.setStatusBarLightMode(this);
    }


    //如果登录过则直接进入主页面
    private void attemptLogin() {
        JMessageClient.init(SplashActivity.this, true);
        //判断是否已经登录
        String token = AuthUtil.getToken();
        final String id = AuthUtil.getId();
        String phone = AuthUtil.getPhone();
        String password = AuthUtil.getPassword();
        if (!token.equals("") && !id.equals("")) {
            //已经登录过，直接进入定位页面
            //login(phone,password);
            MapActivity.startActivity(SplashActivity.this, id);
            finish();
            JMessageClient.login(phone, password, new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {


                }
            });

        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //强制拿到权限
        for (int i : grantResults) {
            if (i == -1) {
                Toast.makeText(SplashActivity.this, "必须获取权限才能正常使用定位和分享功能", Toast.LENGTH_SHORT).show();
                askForPermission();
            }


        }

    }

    private void askForPermission() {
        RxPermissionsTool.with(this).addPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                .addPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .addPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .addPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .addPermission(Manifest.permission.READ_PHONE_STATE)
                .initPermission();


    }

    private void login(final String phoneNum, final String password) {
        String url = UrlUtil.getLogin();
        parameters.put("phone", phoneNum);
        parameters.put("password", password);
        HttpUtil.sendHttpRequest(this)
                .rxPost(url, parameters, new RxStringCallback() {
                    @Override
                    public void onNext(Object tag, String response) {
                        Gson gson = new Gson();
                        bean = gson.fromJson(response, LoginBean.class);
                        JUserBean jUserBean = new JUserBean();
                        jUserBean.setAddress(bean.getData().getId() + "");
                        JMessageClient.updateMyInfo(UserInfo.Field.address, jUserBean, new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {
                            }
                        });
                        AuthUtil.saveUser(phoneNum, password, bean.getData().getToken(), bean.getData().getId() + "");
                        JMessageClient.login(phoneNum, password, new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {

                            }
                        });


                    }

                    @Override
                    public void onError(Object tag, Throwable e) {
                        LogUtil.e(TAG, e.getMessage());
                    }

                    @Override
                    public void onCancel(Object tag, Throwable e) {

                    }
                });
    }


}
