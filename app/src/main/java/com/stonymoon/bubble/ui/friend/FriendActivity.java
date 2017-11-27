package com.stonymoon.bubble.ui.friend;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Window;

import com.stonymoon.bubble.R;
import com.stonymoon.bubble.adapter.FriendAdapter;
import com.stonymoon.bubble.bean.JUserBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.model.UserInfo;

public class FriendActivity extends AppCompatActivity {
    final int RIGHT = 0;
    final int LEFT = 1;
    @BindView(R.id.recycler_friend)
    RecyclerView mRecyclerView;
    private List<UserInfo> mList = new ArrayList<>();
    FriendAdapter adapter = new FriendAdapter(mList);
    private GestureDetector gestureDetector;
    private GestureDetector.OnGestureListener onGestureListener =
            new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                       float velocityY) {
                    float x = e2.getX() - e1.getX();//滑动后的x值减去滑动前的x值 就是滑动的横向水平距离(x)
                    if (x > 100) {
                        doResult(RIGHT);
                    }
                    //如果滑动的横向距离大于100，表明是左滑了(因为左滑为负数，所以距离大于100就是x值小于-100)
                    if (x < -100) {
                        doResult(LEFT);
                    }
                    return true;
                }
            };

    public static void startActivity(Activity context) {
        Intent intent = new Intent(context, FriendActivity.class);
            context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //切换动画效果
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            Transition leftExplode = TransitionInflater.from(this).inflateTransition(R.transition.left_transition);
            Transition rightExplode = TransitionInflater.from(this).inflateTransition(R.transition.right_transition);
            getWindow().setExitTransition(rightExplode);
            getWindow().setEnterTransition(leftExplode);

        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        ButterKnife.bind(this);
        mRecyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        ContactManager.getFriendList(new GetUserInfoListCallback() {
            @Override
            public void gotResult(int i, String s, List<UserInfo> list) {
                mList.addAll(list);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });


            }
        });
        gestureDetector = new GestureDetector(FriendActivity.this, onGestureListener);

    }

    public void doResult(int action) {
        switch (action) {
            case RIGHT:
                finish();
                break;
            case LEFT:
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }


    @OnClick(R.id.iv_friend_back)
    void back() {
        finish();
    }


}
