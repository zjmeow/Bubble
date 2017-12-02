package com.stonymoon.bubble.ui.friend;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Window;
import android.widget.RelativeLayout;

import com.stonymoon.bubble.R;
import com.stonymoon.bubble.adapter.ConversationAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;

public class MessageListActivity extends AppCompatActivity {
    List<Conversation> list = new ArrayList<>();
    ConversationAdapter adapter = new ConversationAdapter(list);
    @BindView(R.id.recycler_conversation)
    RecyclerView conversationRecycler;
    @BindView(R.id.swipe_refresh_message)
    SwipeRefreshLayout refreshLayout;
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MessageListActivity.class);
        context.startActivity(intent);

    }

    @OnClick(R.id.iv_message_back)
    void back() {
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_message_list);
        ButterKnife.bind(this);
        conversationRecycler.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        conversationRecycler.setLayoutManager(linearLayoutManager);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initMessage();
            }
        });

    }

    private void initMessage() {
        if (list == null) {
            refreshLayout.setRefreshing(false);
            return;
        }
        list.clear();
        list.addAll(JMessageClient.getConversationList());
        adapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initMessage();

    }
}
