package com.stonymoon.bubble.ui.common;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.stonymoon.bubble.R;
import com.stonymoon.bubble.util.HttpUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HttpActivity extends AppCompatActivity {

    @BindView(R.id.et1)
    EditText et1;
    @BindView(R.id.et2)
    EditText et2;
    @BindView(R.id.et3)
    EditText et3;
    @BindView(R.id.btn_http_register)
    Button btnHttpRegister;
    @BindView(R.id.btn_http_map)
    Button btnHttpMap;
    @BindView(R.id.btn_http_locate)
    Button btnHttpLocate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_http_register, R.id.btn_http_map, R.id.btn_http_locate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_http_register:
                HttpUtil.createUser(this, et1.getText().toString(), et2.getText().toString());
                break;
            case R.id.btn_http_map:

                break;
            case R.id.btn_http_locate:
                HttpUtil.updateLocate(this, et1.getText().toString()
                        , Double.valueOf(et2.getText().toString())
                        , Double.valueOf(et3.getText().toString()));
                break;
        }
    }
}
