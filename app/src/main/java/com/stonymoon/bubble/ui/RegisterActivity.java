package com.stonymoon.bubble.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.stonymoon.bubble.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {


    @BindView(R.id.et_register_username)
    EditText usernameText;
    @BindView(R.id.et_register_phone_number)
    EditText phoneNumberText;
    @BindView(R.id.et_register_password)
    EditText PasswordText;
    @BindView(R.id.et_register_confirm_password)
    EditText confirmPasswordText;
    @BindView(R.id.btn_register_send_identification_code)
    QMUIRoundButton SendButton;
    @BindView(R.id.et_register_identification_code)
    EditText IdentificationText;
    @BindView(R.id.btn_register_register)
    QMUIRoundButton registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }


}
