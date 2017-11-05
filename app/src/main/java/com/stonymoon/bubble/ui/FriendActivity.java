package com.stonymoon.bubble.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.stonymoon.bubble.R;
import com.stonymoon.bubble.adapter.FriendAdapter;
import com.stonymoon.bubble.bean.JUserBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.model.UserInfo;

public class FriendActivity extends AppCompatActivity {
    @BindView(R.id.recycler_friend)
    RecyclerView mRecyclerView;
    private List<JUserBean> mList = new ArrayList<>();
    FriendAdapter adapter = new FriendAdapter(mList);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        ButterKnife.bind(this);
        mRecyclerView.setAdapter(adapter);
        ContactManager.getFriendList(new GetUserInfoListCallback() {
            @Override
            public void gotResult(int i, String s, List<UserInfo> list) {
                mList.addAll((List) list);
                adapter.notifyDataSetChanged();
            }
        });
    }


}
