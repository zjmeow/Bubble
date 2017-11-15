package com.stonymoon.bubble.ui.friend;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MessageListActivity.class);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            Transition leftExplode = TransitionInflater.from(this).inflateTransition(R.transition.left_transition);
            Transition rightExplode = TransitionInflater.from(this).inflateTransition(R.transition.right_transition);
            getWindow().setExitTransition(rightExplode);
            getWindow().setEnterTransition(leftExplode);

        }

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
