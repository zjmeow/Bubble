package com.stonymoon.bubble.ui.friend;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.stonymoon.bubble.R;
import com.stonymoon.bubble.adapter.FriendAdapter;
import com.stonymoon.bubble.base.StatusBarLightActivity;
import com.stonymoon.bubble.bean.JUserBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.model.UserInfo;

public class FriendActivity extends StatusBarLightActivity {
    @BindView(R.id.recycler_friend)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_friend)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.iv_friend_no_friend)
    ImageView ivNoFriend;
    private List<UserInfo> mList = new ArrayList<>();
    FriendAdapter adapter = new FriendAdapter(mList);


    public static void startActivity(Activity context) {
        Intent intent = new Intent(context, FriendActivity.class);
            context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        ButterKnife.bind(this);
        mRecyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        initFriend();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initFriend();
            }
        });
    }

    private void initFriend() {
        mList.clear();
        ContactManager.getFriendList(new GetUserInfoListCallback() {
            @Override
            public void gotResult(int i, String s, List<UserInfo> list) {
                if (list == null) {
                    refreshLayout.setRefreshing(false);
                    ivNoFriend.setVisibility(View.VISIBLE);
                    return;
                }
                if (list.size() == 0) {
                    ivNoFriend.setVisibility(View.VISIBLE);
                }
                mList.addAll(list);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        refreshLayout.setRefreshing(false);
                    }
                });


            }
        });


    }


    @OnClick(R.id.iv_friend_back)
    void back() {
        finish();
    }


}
