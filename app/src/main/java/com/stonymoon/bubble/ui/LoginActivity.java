package com.stonymoon.bubble.ui;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.app.Activity;


import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.stonymoon.bubble.R;
import com.stonymoon.bubble.util.HttpUtil;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.RxStringCallback;


import org.apaches.commons.codec.digest.DigestUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.Manifest.permission.READ_CONTACTS;
import static com.vondear.rxtools.RxConstTool.REGEX_MOBILE_SIMPLE;


public class LoginActivity extends Activity {

    private static final int REQUEST_READ_CONTACTS = 0;
    private static final String[] DUMMY_CREDENTIALS = new String[]{
    };
    Map<String, Object> parameters = new HashMap<String, Object>();

    @BindView(R.id.et_login_phone_number)
    TextView phoneNumberText;
    @BindView(R.id.et_login_password)
    EditText passwordText;
    @BindView(R.id.pbar_login_progress)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        attemptLogin();

    }

    @OnClick(R.id.btn_login_sign_in)
    void GetTextAndLogin() {
        String phone = phoneNumberText.getText().toString();
        boolean isPhone = Pattern.matches(REGEX_MOBILE_SIMPLE, phoneNumberText.getText().toString());
        if (!isPhone) {
            Toast.makeText(LoginActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
        }
        String password = phoneNumberText.getText().toString();
        login(phone, password);
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
            Snackbar.make(phoneNumberText, "登陆成功", Snackbar.LENGTH_INDEFINITE)
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
        //加载参数
        parameters.put("phone", phoneNum);
        parameters.put("password", password);
        HttpUtil.sendHttpRequest(this)
                .rxGet(url, parameters, new RxStringCallback() {
                    @Override
                    public void onNext(Object tag, String response) {
                        saveUser(phoneNum, password);
                        //todo 登录成功就直接进入定位页面
                        Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();

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

    private String token(String phone, double latitude, double longitude) {
        String key = phone + latitude + longitude + "stonymoon";
        return DigestUtils.md5Hex(key);

    }


    private void attemptLogin() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        String phone = sharedPreferences.getString("phone", "none");
        String password = sharedPreferences.getString("password", "none");
        if (!phone.equals("none")) {
            login(phone, password);
        }

    }

    private void saveUser(String phone, String password) {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phone", phone);
        editor.putString("password", password);
        editor.apply();

    }


}

