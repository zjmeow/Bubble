package com.stonymoon.bubble.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.util.HttpUtil;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.RxStringCallback;


import org.apaches.commons.codec.digest.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.vondear.rxtools.RxConstTool.REGEX_MOBILE_SIMPLE;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.et_register_username)
    EditText usernameText;
    @BindView(R.id.et_register_phone_number)
    EditText phoneNumberText;
    @BindView(R.id.et_register_password)
    EditText passwordText;
    @BindView(R.id.et_register_confirm_password)
    EditText confirmPasswordText;
    @BindView(R.id.btn_register_send_identification_code)
    QMUIRoundButton sendButton;
    @BindView(R.id.et_register_identification_code)
    EditText identificationText;
    @BindView(R.id.btn_register_register)
    QMUIRoundButton registerButton;
    private Map<String, Object> parameters = new HashMap<>();
    private String phone = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

    }

    //发送验证码，发送完以后手机号不能修改
    @OnClick(R.id.btn_register_send_identification_code)
    void sendCode() {
        boolean isPhone = Pattern.matches(REGEX_MOBILE_SIMPLE, phoneNumberText.getText().toString());
        if (!isPhone) {
            Toast.makeText(RegisterActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
        } else {
            phone = phoneNumberText.getText().toString();
            phoneNumberText.setFocusable(false);
            phoneNumberText.setEnabled(false);
            phoneNumberText.setTextColor(Color.GRAY);
        }

    }


    @OnClick(R.id.btn_register_register)
    void register() {
        String password = passwordText.getText().toString();
        String confirm = confirmPasswordText.getText().toString();
        if (!password.equals(confirm)) {
            Toast.makeText(RegisterActivity.this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        parameters.clear();
        parameters.put("username", usernameText.getText().toString());
        parameters.put("password", password);
        parameters.put("token", token(phone));


        String url = "create";
        HttpUtil.sendHttpRequest(this)
                .rxPost(url, parameters, new RxStringCallback() {
                    @Override
                    public void onNext(Object tag, String response) {
                        Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Object tag, Throwable e) {
                        Toast.makeText(RegisterActivity.this, "加载失败，请检查网络", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onCancel(Object tag, Throwable e) {

                    }
                });

    }


    private String token(String phone) {
        String key = phone + "stonymoon";
        return DigestUtils.md5Hex(key);
    }

}
