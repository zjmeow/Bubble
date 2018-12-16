package com.stonymoon.bubble.ui.auth;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.base.BaseActivity;
import com.vondear.rxtools.RxTool;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.sms.SMSSDK;
import cn.jpush.sms.listener.SmscheckListener;

import static com.vondear.rxtools.RxConstTool.REGEX_MOBILE_SIMPLE;

public class RegisterActivity extends BaseActivity {


    @BindView(R.id.et_register_phone_number)
    EditText phoneNumberText;
    @BindView(R.id.btn_register_next)
    QMUIRoundButton btnNext;


    @BindView(R.id.tv_register_send_identification_code)
    TextView tvSendCode;


    @BindView(R.id.et_register_identification_code)
    EditText identificationText;


    private String phone = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);
        QMUIStatusBarHelper.setStatusBarLightMode(this);
        SMSSDK.getInstance().initSdk(this);
        SMSSDK.getInstance().setIntervalTime(60000);


    }

    //发送验证码，发送完以后手机号不能修改
    @OnClick(R.id.tv_register_send_identification_code)
    void sendCode() {
        boolean isPhone = Pattern.matches(REGEX_MOBILE_SIMPLE, phoneNumberText.getText().toString());

        if (!isPhone) {
            Toast.makeText(RegisterActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        } else {
            phone = phoneNumberText.getText().toString();


            // TODO: 2017/12/8  暂时关闭验证码

            Toast.makeText(this, "短信验证码暂时失效，输入1234即可", Toast.LENGTH_SHORT).show();
            phoneNumberText.setFocusable(false);
            phoneNumberText.setEnabled(false);
            phoneNumberText.setTextColor(Color.GRAY);
            RxTool.countDown(tvSendCode, 60000, 1000, "获取验证码");
            tvSendCode.setClickable(false);

        }

    }


    @OnClick(R.id.btn_register_next)
    void register() {
        String code = identificationText.getText().toString();
        if (code.equals("1234")) {
            RegisterPhoneActivity.startActivity(RegisterActivity.this, phone);
            finish();
        } else {
            Toast.makeText(RegisterActivity.this, "请输入正确的验证码", Toast.LENGTH_SHORT).show();
        }


        SMSSDK.getInstance().checkSmsCodeAsyn(phone, identificationText.getText().toString(), new SmscheckListener() {
            @Override
            public void checkCodeSuccess(final String code) {
                //RegisterPhoneActivity.startActivity(RegisterActivity.this, phone);

            }

            @Override
            public void checkCodeFail(int errCode, final String errMsg) {
                //Toast.makeText(RegisterActivity.this, errMsg, Toast.LENGTH_SHORT).show();

            }
        });

    }

}
