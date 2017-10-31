package com.stonymoon.bubble.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.gson.Gson;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.bean.LocationBean;
import com.stonymoon.bubble.bean.LoginBean;
import com.stonymoon.bubble.util.HttpUtil;
import com.tamic.novate.Novate;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.RxStringCallback;


import org.apaches.commons.codec.digest.DigestUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import okhttp3.Call;
import retrofit2.http.QueryMap;

import static android.Manifest.permission.READ_CONTACTS;


public class LoginActivity extends Activity {

    private static final int REQUEST_READ_CONTACTS = 0;

    private static final String[] DUMMY_CREDENTIALS = new String[]{
    };
    Map<String, Object> parameters = new HashMap<String, Object>();

    @BindView(R.id.et_login_phone_number)
    TextView phoneNumberView;
    @BindView(R.id.et_login_password)
    EditText passwordView;
    @BindView(R.id.pbar_login_progress)
    ProgressBar progressView;

    @OnClick(R.id.btn_login_sign_in)
    void login() {
        login(phoneNumberView.getText().toString(), passwordView.getText().toString());

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    login(phoneNumberView.getText().toString(), passwordView.getText().toString());
                    return true;
                }
                return false;
            }
        });
        //attemptLogin();


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

        String url = "login";
        parameters.put("phone", phoneNum);
        parameters.put("password", password);
        HttpUtil.sendHttpRequest(this)
                .rxPost(url, parameters, new RxStringCallback() {
                    @Override
                    public void onNext(Object tag, String response) {
                        Gson gson = new Gson();
                        LoginBean bean = gson.fromJson(response, LoginBean.class);
                        saveUser(phoneNum, password, bean.getContent().getToken(), bean.getContent().getId() + "");
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        MapActivity.startActivity(LoginActivity.this, "" + bean.getContent().getId(), bean.getContent().getToken(), "");
                        JMessageClient.login(phoneNum, password, new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {
                                Toast.makeText(LoginActivity.this, "测试极光推送" + s, Toast.LENGTH_SHORT).show();

                            }
                        });
                        finish();
                    }

                    @Override
                    public void onError(Object tag, Throwable e) {
                        Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onCancel(Object tag, Throwable e) {

                    }
                });



    }


    //如果登录过则直接进入主页面
    private void attemptLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        //判断是否已经登录
        String token = sharedPreferences.getString("token", "");
        String id = sharedPreferences.getString("id", "");
        String phone = sharedPreferences.getString("phone", "");
        String password = sharedPreferences.getString("password", "");


        String locationId = sharedPreferences.getString("locationId", "");
        if (!token.equals("") && !id.equals("")) {
            //已经登录过，直接进入定位页面
            MapActivity.startActivity(this, id, token, locationId);
            JMessageClient.login(phone, password, new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    Toast.makeText(LoginActivity.this, "测试极光推送" + s, Toast.LENGTH_SHORT).show();
                }
            });
            finish();
        }


    }

    //保存用户信息
    private void saveUser(String phone, String password, String token, String id) {
        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phone", phone);
        editor.putString("password", password);
        editor.putString("token", token);
        editor.putString("id", id);
        editor.apply();

    }

}

