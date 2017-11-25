package com.stonymoon.bubble.ui.auth;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.bean.LoginBean;
import com.stonymoon.bubble.ui.friend.MapActivity;
import com.stonymoon.bubble.util.AuthUtil;
import com.stonymoon.bubble.util.HttpUtil;
import com.stonymoon.bubble.util.LogUtil;
import com.stonymoon.bubble.view.MyDialog;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.RxStringCallback;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.Manifest.permission.READ_CONTACTS;


public class LoginActivity extends Activity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_READ_CONTACTS = 0;

    private static final String[] DUMMY_CREDENTIALS = new String[]{
    };
    @BindView(R.id.et_login_phone_number)
    EditText phoneNumberView;
    @BindView(R.id.et_login_password)
    EditText passwordView;
    @BindView(R.id.wrapper_login_phone_number)
    TextInputLayout wrapperLoginPhoneNumber;
    @BindView(R.id.wrapper_login_password)
    TextInputLayout wrapperLoginPassword;
    @BindView(R.id.btn_login_sign_in)
    Button submitButton;
    private Map<String, Object> parameters = new HashMap<String, Object>();
    private LoginBean bean;

    @OnClick(R.id.btn_login_sign_in)
    void login() {
        login(phoneNumberView.getText().toString(), passwordView.getText().toString());

    }

    @OnClick(R.id.tv_login_register)
    void register() {
        Intent intent = new Intent(this, RegisterPhoneActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        //初始化极光推送,是否开启缓存消息
        JMessageClient.init(LoginActivity.this, true);

        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_NULL) {
                    login(phoneNumberView.getText().toString(), passwordView.getText().toString());
                    return true;
                }
                return false;
            }
        });
        //attemptLogin();
        wrapperLoginPhoneNumber.setHint("请输入手机号");
        wrapperLoginPassword.setHint("请输入密码");


    }

    //申请权限
    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(phoneNumberView, "登陆成功", Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }


    private void login(final String phoneNum, final String password) {
        if (phoneNum.equals("") || password.equals("")) {
            Toast.makeText(LoginActivity.this, "请输入正确的账户密码", Toast.LENGTH_SHORT).show();
        }
        String url = "login";
        parameters.put("phone", phoneNum);
        parameters.put("password", password);


        final MyDialog myDialog = new MyDialog(LoginActivity.this);
        myDialog.showProgress("登录中");
        HttpUtil.sendHttpRequest(this)
                .rxPost(url, parameters, new RxStringCallback() {
                    @Override
                    public void onNext(Object tag, String response) {
                        Gson gson = new Gson();
                        bean = gson.fromJson(response, LoginBean.class);
                        AuthUtil.saveUser(phoneNum, password, bean.getContent().getToken(), bean.getContent().getId() + "");
                        JMessageClient.login(phoneNum, password, new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {
                                MapActivity.startActivity(LoginActivity.this, "" + bean.getContent().getId());
                                myDialog.success("登陆成功");
                                finish();

                            }
                        });

                    }

                    @Override
                    public void onError(Object tag, Throwable e) {
                        myDialog.fail("登录失败");
                        LogUtil.e(TAG, e.getMessage());
                    }

                    @Override
                    public void onCancel(Object tag, Throwable e) {

                    }
                });


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
                    Toast.makeText(LoginActivity.this, "登录成功" + s, Toast.LENGTH_SHORT).show();
                    finish();
                }
            });


        }


    }


}

