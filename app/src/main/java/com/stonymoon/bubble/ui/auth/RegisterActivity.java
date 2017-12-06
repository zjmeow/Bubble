package com.stonymoon.bubble.ui.auth;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.stonymoon.bubble.R;
import com.stonymoon.bubble.base.BaseActivity;
import com.vondear.rxtools.RxTool;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.sms.SMSSDK;
import cn.jpush.sms.listener.SmscheckListener;
import cn.jpush.sms.listener.SmscodeListener;

import static com.vondear.rxtools.RxConstTool.REGEX_MOBILE_SIMPLE;

public class RegisterActivity extends BaseActivity {


    @BindView(R.id.et_register_phone_number)
    EditText phoneNumberText;
    @BindView(R.id.tv_register_send_identification_code)
    TextView tvSendCode;


    @BindView(R.id.et_register_identification_code)
    EditText identificationText;
    @BindView(R.id.btn_register_next)
    Button registerButton;

    private String phone = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        SMSSDK.getInstance().initSdk(this);
        SMSSDK.getInstance().setIntervalTime(60000);

    }

    //发送验证码，发送完以后手机号不能修改
    @OnClick(R.id.tv_register_send_identification_code)
    void sendCode() {

        boolean isPhone = Pattern.matches(REGEX_MOBILE_SIMPLE, phoneNumberText.getText().toString());
        if (!isPhone) {
            Toast.makeText(RegisterActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
        } else {
            phone = phoneNumberText.getText().toString();
            phoneNumberText.setFocusable(false);
            phoneNumberText.setEnabled(false);
            phoneNumberText.setTextColor(Color.GRAY);
            RxTool.countDown(tvSendCode, 60000, 1000, "获取验证码");
            tvSendCode.setClickable(false);

            SMSSDK.getInstance().getSmsCodeAsyn(phone, "1", new SmscodeListener() {
                @Override
                public void getCodeSuccess(final String uuid) {
                    // 获取验证码成功，uuid 为此次获取的唯一标识码。
                }

                @Override
                public void getCodeFail(int errCode, final String errMsg) {
                    // 获取验证码失败 errCode 为错误码，详情请见文档后面的错误码表；errMsg 为错误描述。
                }
            });

        }

    }


    @OnClick(R.id.btn_register_next)
    void register() {

        RegisterPhoneActivity.startActivity(RegisterActivity.this, phone);
        SMSSDK.getInstance().checkSmsCodeAsyn(phone, identificationText.getText().toString(), new SmscheckListener() {
            @Override
            public void checkCodeSuccess(final String code) {
                //RegisterPhoneActivity.startActivity(RegisterActivity.this, phone);

            }

            @Override
            public void checkCodeFail(int errCode, final String errMsg) {
                Toast.makeText(RegisterActivity.this, errMsg, Toast.LENGTH_SHORT).show();
            }
        });

    }

}
