package com.stonymoon.bubble.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.baidu.mapapi.model.LatLng;
import com.stonymoon.bubble.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShareActivity extends AppCompatActivity {


    @BindView(R.id.et_share)
    EditText shareEt;

    public static void startActivity(Context context, double latitude, double longitude) {
        Intent intent = new Intent(context, ShareActivity.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        context.startActivity(intent);
    }

    @OnClick(R.id.et_share_submit)
    void submit() {


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        ButterKnife.bind(this);


    }


}
