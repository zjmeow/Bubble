package com.stonymoon.bubble.ui.friend;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        ButterKnife.bind(this);
        conversationRecycler.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        conversationRecycler.setLayoutManager(linearLayoutManager);
        initMessage();
    }

    private void initMessage() {
        if (list == null) {
            return;
        }
        list.addAll(JMessageClient.getConversationList());
        adapter.notifyDataSetChanged();

    }

}
