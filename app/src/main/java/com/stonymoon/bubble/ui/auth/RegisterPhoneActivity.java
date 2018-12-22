package com.stonymoon.bubble.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.api.BaseDataManager;
import com.stonymoon.bubble.api.serivces.AuthService;
import com.stonymoon.bubble.base.ActivityCollector;
import com.stonymoon.bubble.base.BaseActivity;
import com.stonymoon.bubble.bean.RegisterBean;
import com.stonymoon.bubble.util.StringCheckUtil;

import org.apaches.commons.codec.digest.DigestUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.options.RegisterOptionalUserInfo;
import cn.jpush.im.api.BasicCallback;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class RegisterPhoneActivity extends BaseActivity {
    private static final String TAG = "RegisterPhoneActivity";
    @BindView(R.id.et_register_username)
    EditText usernameText;
    @BindView(R.id.et_register_password)
    EditText passwordText;
    String phone;

    public static void startActivity(Context context, String phone) {
        Intent intent = new Intent(context, RegisterPhoneActivity.class);
        intent.putExtra("phone", phone);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phone);
        ButterKnife.bind(this);
        QMUIStatusBarHelper.setStatusBarLightMode(this);
        phone = getIntent().getStringExtra("phone");

    }

    @OnClick(R.id.btn_register_register)
    void successRegister() {
        String username = usernameText.getText().toString().trim();
        String password = passwordText.getText().toString();
        if (username.equals("")) {
            Toast.makeText(RegisterPhoneActivity.this, "请输入正确的用户名", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isPassword = StringCheckUtil.isPassword(password);
        if (!isPassword) {
            Toast.makeText(RegisterPhoneActivity.this, "密码必须在6位以上", Toast.LENGTH_SHORT).show();
            return;
        }

        BaseDataManager.getHttpManager()
                .create(AuthService.class)
                .register(phone, password, usernameText.getText().toString(), token(phone))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<RegisterBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(RegisterPhoneActivity.this, "网络异常或者用户已经存在", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, e.toString());
                    }

                    @Override
                    public void onNext(RegisterBean loginBean) {
                        //注册聊天帐号
                        RegisterOptionalUserInfo userInfo = new RegisterOptionalUserInfo();
                        Map<String, String> para = new HashMap<String, String>();
                        para.put("url", "http://oupl6wdxc.bkt.clouddn.com/EIEFJILJELJEG");
                        userInfo.setExtras(para);
                        userInfo.setNickname(usernameText.getText().toString());
                        JMessageClient.register(
                                phone,
                                passwordText.getText().toString(), userInfo, new BasicCallback() {
                                    @Override
                                    public void gotResult(int i, String s) {
                                        ActivityCollector.finishAll();
                                        LoginActivity.startActivity(RegisterPhoneActivity.this);
                                    }
                                });
                    }
                });


    }


    private String token(String phone) {
        String key = phone + "stonymoon";
        return DigestUtils.md5Hex(key);
    }


}
