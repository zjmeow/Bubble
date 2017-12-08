package com.stonymoon.bubble.base;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v4.app.SupportActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.stonymoon.bubble.R;
import com.stonymoon.bubble.ui.friend.MessageListActivity;
import com.stonymoon.bubble.util.LogUtil;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.NotificationClickEvent;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d("BaseActivity", getClass().getSimpleName());
        ActivityCollector.addActivity(this);
        JMessageClient.init(this);
        JMessageClient.setNotificationFlag(JMessageClient.FLAG_NOTIFY_WITH_LED | JMessageClient.FLAG_NOTIFY_WITH_SOUND);
        JMessageClient.registerEventReceiver(this);



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JMessageClient.unRegisterEventReceiver(this);
        ActivityCollector.removeActivity(this);
    }

    public void onEventMainThread(NotificationClickEvent event) {
        MessageListActivity.startActivity(this);

    }

}
