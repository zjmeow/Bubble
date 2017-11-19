package com.stonymoon.bubble.ui.friend;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.bean.DefaultUser;
import com.stonymoon.bubble.bean.MyMessage;
import com.stonymoon.bubble.util.LogUtil;
import com.stonymoon.bubble.util.MessageUtil;
import com.stonymoon.bubble.view.ChatView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.jiguang.imui.commons.ImageLoader;
import cn.jiguang.imui.commons.models.IMessage;
import cn.jiguang.imui.messages.MsgListAdapter;
import cn.jiguang.imui.messages.ptr.PtrHandler;
import cn.jiguang.imui.messages.ptr.PullToRefreshLayout;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;

public class ChatActivity extends AppCompatActivity {

    private final static String TAG = "ChatActivity";

    private ChatView mChatView;
    private EditText editText;
    private MsgListAdapter<MyMessage> mAdapter;
    private List<MyMessage> historyMessage;
    private DefaultUser user;
    private DefaultUser otherUser;
    //读取页面条数
    private int page = 0;


    public static void startActivity(Context context, String phone, String username, String url) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("phone", phone);
        intent.putExtra("username", username);
        intent.putExtra("url", url);
        context.startActivity(intent);

    }

    public static void startActivity(Context context, String phone) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("phone", phone);
        context.startActivity(intent);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initUser();
        mChatView = (ChatView) findViewById(R.id.chat_view);
        editText = (EditText) findViewById(R.id.et_chat_input);
        mChatView.initModule();


        initMsgAdapter();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        mChatView.setOnSendClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyMessage message = new MyMessage(editText.getText().toString(), IMessage.MessageType.SEND_TEXT);
                        message.setUserInfo(user);
                        //message.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
                        mAdapter.addToStart(message, true);
                        Message sendMessage = JMessageClient.createSingleTextMessage(otherUser.getId() + "", editText.getText().toString());
                        JMessageClient.sendMessage(sendMessage);
                        mChatView.clearText();

                    }


                }


        );

        JMessageClient.registerEventReceiver(this);
        historyMessage = getHistoryMessages();


    }


    private List<MyMessage> getHistoryMessages() {
        Intent intent = getIntent();
        Conversation conversation = JMessageClient.getSingleConversation(intent.getStringExtra("phone"));
        List<MyMessage> list = new ArrayList<>();

        if (conversation == null) {
            return list;
        }
        List<Message> messageList = conversation.getAllMessage();
        for (Message message : messageList) {
            if (message.getContentType() == ContentType.custom) {
                continue;
            }
            String phone = message.getFromUser().getUserName();
            String messageText = MessageUtil.getMessageText(message);
            if (phone.equals(user.getId())) {
                MyMessage myMessage = new MyMessage(messageText, IMessage.MessageType.SEND_TEXT);
                myMessage.setUserInfo(user);
                list.add(myMessage);
            } else {
                MyMessage myMessage = new MyMessage(messageText, IMessage.MessageType.RECEIVE_TEXT);
                myMessage.setUserInfo(otherUser);
                list.add(myMessage);
            }

        }
        Collections.reverse(list);
        historyMessage = list;
        loadNextPage(page);
        page += 20;
        return list;
    }

    private void initMsgAdapter() {
        ImageLoader imageLoader = new ImageLoader() {
            @Override
            public void loadAvatarImage(ImageView avatarImageView, String string) {
                Glide.with(ChatActivity.this)
                        .load(string)
                        .into(avatarImageView);

            }

            @Override
            public void loadImage(ImageView imageView, String string) {
                Glide.with(getApplicationContext())
                        .load(string)
                        .fitCenter()
                        .placeholder(R.drawable.aurora_picture_not_found)
                        .into(imageView);
            }
        };
        MsgListAdapter.HoldersConfig holdersConfig = new MsgListAdapter.HoldersConfig();
        mAdapter = new MsgListAdapter<>("0", holdersConfig, imageLoader);

//TODO 可以添加删除消息
        mAdapter.setMsgLongClickListener(new MsgListAdapter.OnMsgLongClickListener<MyMessage>() {
            @Override
            public void onMessageLongClick(MyMessage message) {

            }
        });

        mAdapter.setOnAvatarClickListener(new MsgListAdapter.OnAvatarClickListener<MyMessage>() {
            @Override
            public void onAvatarClick(MyMessage message) {

                ProfileActivity.startActivity(ChatActivity.this, message.getFromUser().getId());

            }
        });

        mAdapter.setMsgStatusViewClickListener(new MsgListAdapter.OnMsgStatusViewClickListener<MyMessage>() {
            @Override
            public void onStatusViewClick(MyMessage message) {

            }
        });

        PullToRefreshLayout layout = mChatView.getPtrLayout();
        layout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PullToRefreshLayout layout) {
                LogUtil.i("MessageListActivity", "Loading next page");
                loadNextPage(page);
                page += 20;

            }
        });

        mChatView.setAdapter(mAdapter);
        mAdapter.getLayoutManager().scrollToPosition(0);
    }

    private void loadNextPage(int page) {
        final int end = page + 20;
        final int start = page;
        final int length = historyMessage.size();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<MyMessage> list = new ArrayList<>();
                for (int i = start; i < end && i < length; i++) {
                    list.add(historyMessage.get(i));
                }
                mAdapter.addToEnd(list);
                mAdapter.notifyDataSetChanged();

                mChatView.getPtrLayout().refreshComplete();
            }
        }, 500);
    }


    private void scrollToBottom() {
        mAdapter.getLayoutManager().scrollToPosition(0);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        JMessageClient.unRegisterEventReceiver(this);
    }

    public void onEventMainThread(MessageEvent event) {
        if (event.getMessage().getContentType() == ContentType.custom) {
            return;
        }
        String msg = MessageUtil.getMessageText(event.getMessage());
        MyMessage message = new MyMessage(msg, IMessage.MessageType.RECEIVE_TEXT);
        message.setUserInfo(otherUser);
        if (!event.getMessage().getFromUser().getUserName().equals(user.getId())) {
            mAdapter.addToStart(message, true);
        }


    }


    private void initUser() {
        Intent intent = getIntent();
        Conversation conversation = JMessageClient.getSingleConversation(intent.getStringExtra("phone"));
        UserInfo targetInfo = (UserInfo) conversation.getTargetInfo();
        UserInfo myInfo = JMessageClient.getMyInfo();
        String phone = myInfo.getUserName();
        String username = myInfo.getDisplayName();
        String url = myInfo.getExtra("url");
        user = new DefaultUser(phone, username, url);
        otherUser = new DefaultUser(targetInfo.getUserName(), targetInfo.getDisplayName(), targetInfo.getExtra("url"));

    }


}






