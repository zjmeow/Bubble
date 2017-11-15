package com.stonymoon.bubble.ui.common;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.stonymoon.bubble.R;
import com.stonymoon.bubble.bean.JUserBean;
import com.stonymoon.bubble.util.LogUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

public class EditActivity extends AppCompatActivity {
    @BindView(R.id.et_edit_signature)
    EditText etEignature;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, EditActivity.class);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.btn_edit_signature)
    void editSignature() {
        JUserBean bean = new JUserBean();
        bean.setSignature(etEignature.getText().toString());
        JMessageClient.updateMyInfo(UserInfo.Field.signature, bean, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                LogUtil.v("MyProfile", s);
                Toast.makeText(EditActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


    }

}
