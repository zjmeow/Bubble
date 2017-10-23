package com.stonymoon.bubble.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.stonymoon.bubble.R;
import com.vondear.rxtools.RxTool;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.R.attr.countDown;
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

    }


}
