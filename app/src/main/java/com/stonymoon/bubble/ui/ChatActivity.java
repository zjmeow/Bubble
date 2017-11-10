package com.stonymoon.bubble.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.bean.DefaultUser;
import com.stonymoon.bubble.bean.MyMessage;
import com.stonymoon.bubble.util.LogUtil;
import com.stonymoon.bubble.view.ChatView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.jiguang.imui.chatinput.ChatInputView;
import cn.jiguang.imui.chatinput.listener.OnClickEditTextListener;
import cn.jiguang.imui.chatinput.listener.OnMenuClickListener;
import cn.jiguang.imui.chatinput.model.FileItem;
import cn.jiguang.imui.chatinput.model.VideoItem;
import cn.jiguang.imui.commons.ImageLoader;
import cn.jiguang.imui.commons.models.IMessage;
import cn.jiguang.imui.messages.MsgListAdapter;
import cn.jiguang.imui.messages.ptr.PtrHandler;
import cn.jiguang.imui.messages.ptr.PullToRefreshLayout;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.ContactNotifyEvent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.OfflineMessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;


public class ChatActivity extends AppCompatActivity implements ChatView.OnKeyboardChangedListener,
        ChatView.OnSizeChangedListener, View.OnTouchListener {

    private final static String TAG = "ChatActivity";
    private final int RC_RECORD_VOICE = 0x0001;
    private final int RC_CAMERA = 0x0002;
    private final int RC_PHOTO = 0x0003;

    private ChatView mChatView;
    private MsgListAdapter<MyMessage> mAdapter;
    private List<MyMessage> mData;

    private InputMethodManager mImm;
    private Window mWindow;
    private HeadsetDetectReceiver mReceiver;
    private SensorManager mSensorManager;
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    private DefaultUser user;
    private DefaultUser otherUser;

    public static void startActivity(Context context, String phone, String username, String url) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("phone", phone);
        intent.putExtra("username", username);
        intent.putExtra("url", url);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        this.mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mWindow = getWindow();
        registerProximitySensorListener();
        initUser();
        mChatView = (ChatView) findViewById(R.id.chat_view);
        mChatView.initModule();
        mData = getMessages();
        initMsgAdapter();
        mReceiver = new HeadsetDetectReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(mReceiver, intentFilter);
        mChatView.setKeyboardChangedListener(this);
        mChatView.setOnSizeChangedListener(this);
        mChatView.setOnTouchListener(this);
        mChatView.setMenuClickListener(new OnMenuClickListener() {
            @Override
            public boolean switchToMicrophoneMode() {
                return false;
            }

            @Override
            public boolean switchToCameraMode() {
                return false;
            }

            @Override
            public boolean onSendTextMessage(CharSequence input) {
                if (input.length() == 0) {
                    return false;
                }
                MyMessage message = new MyMessage(input.toString(), IMessage.MessageType.SEND_TEXT);
                message.setUserInfo(user);
                message.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
                mAdapter.addToStart(message, true);
                Message sendMessage = JMessageClient.createSingleTextMessage(user.getId() + "", input.toString());
                JMessageClient.sendMessage(sendMessage);
                return true;
            }

            @Override
            public void onSendFiles(List<FileItem> list) {
                if (list == null || list.isEmpty()) {
                    return;
                }

                MyMessage message;
                for (FileItem item : list) {
                    if (item.getType() == FileItem.Type.Image) {
                        message = new MyMessage(null, IMessage.MessageType.SEND_IMAGE);

                    } else {
                        throw new RuntimeException("Invalid FileItem type. Must be Type.Image or Type.Video");
                    }

                    message.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
                    message.setMediaFilePath(item.getFilePath());
                    message.setUserInfo(new DefaultUser("1", "Ironman", "R.drawable.ironman"));

                    final MyMessage fMsg = message;
                    ChatActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.addToStart(fMsg, true);
                        }
                    });
                }
            }


            @Override
            public boolean switchToGalleryMode() {
                scrollToBottom();
                return true;
            }

            public boolean switchToEmojiMode() {
                scrollToBottom();
                return true;
            }
        });


        mChatView.setOnTouchEditTextListener(new OnClickEditTextListener() {
            @Override
            public void onTouchEditText() {
//                mAdapter.getLayoutManager().scrollToPosition(0);
            }
        });


        JMessageClient.registerEventReceiver(this);

    }

    private void registerProximitySensorListener() {
        try {
            mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
            mWakeLock = mPowerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, TAG);
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setScreenOn() {
        if (mWakeLock != null) {
            mWakeLock.setReferenceCounted(false);
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    private void setScreenOff() {
        if (mWakeLock == null) {
            mWakeLock = mPowerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, TAG);
        }
        mWakeLock.acquire();
    }

    private List<MyMessage> getMessages() {
        List<MyMessage> list = new ArrayList<>();
        Resources res = getResources();
        String[] messages = {"测试"};
        for (int i = 0; i < messages.length; i++) {
            MyMessage message;
            if (i % 2 == 0) {
                message = new MyMessage(messages[i], IMessage.MessageType.RECEIVE_TEXT);
                message.setUserInfo(user);
            } else {
                message = new MyMessage(messages[i], IMessage.MessageType.SEND_TEXT);
                message.setUserInfo(otherUser);
            }
            message.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
            list.add(message);
        }
        Collections.reverse(list);
        return list;
    }

    private void initMsgAdapter() {
        ImageLoader imageLoader = new ImageLoader() {
            @Override
            public void loadAvatarImage(ImageView avatarImageView, String string) {
                // You can use other image load libraries.
                if (string.contains("R.drawable")) {
                    Integer resId = getResources().getIdentifier(string.replace("R.drawable.", ""),
                            "drawable", getPackageName());

                    avatarImageView.setImageResource(resId);
                } else {
                    Glide.with(getApplicationContext())
                            .load(string)
                            .placeholder(R.drawable.aurora_headicon_default)
                            .into(avatarImageView);
                }
            }

            @Override
            public void loadImage(ImageView imageView, String string) {
                // You can use other image load libraries.
                Glide.with(getApplicationContext())
                        .load(string)
                        .fitCenter()
                        .placeholder(R.drawable.aurora_picture_not_found)
                        .override(400, Target.SIZE_ORIGINAL)
                        .into(imageView);
            }
        };

        // Use default layout
        MsgListAdapter.HoldersConfig holdersConfig = new MsgListAdapter.HoldersConfig();
        mAdapter = new MsgListAdapter<>("0", holdersConfig, imageLoader);
        // If you want to customise your layout, try to create custom ViewHolder:
        // holdersConfig.setSenderTxtMsg(CustomViewHolder.class, layoutRes);
        // holdersConfig.setReceiverTxtMsg(CustomViewHolder.class, layoutRes);
        // CustomViewHolder must extends ViewHolders defined in MsgListAdapter.
        // Current ViewHolders are TxtViewHolder, VoiceViewHolder.

        mAdapter.setOnMsgClickListener(new MsgListAdapter.OnMsgClickListener<MyMessage>() {
            @Override
            public void onMessageClick(MyMessage message) {
                // do something
                if (message.getType() == IMessage.MessageType.RECEIVE_VIDEO
                        || message.getType() == IMessage.MessageType.SEND_VIDEO) {
                    if (!TextUtils.isEmpty(message.getMediaFilePath())) {
                    }
                } else {
                    //todo 点击消息的处理,图片弹出。
                }
            }
        });

        mAdapter.setMsgLongClickListener(new MsgListAdapter.OnMsgLongClickListener<MyMessage>() {
            @Override
            public void onMessageLongClick(MyMessage message) {
                Toast.makeText(getApplicationContext(),
                        "长按消息",
                        Toast.LENGTH_SHORT).show();
                // todo 长按信息的处理
            }
        });

        mAdapter.setOnAvatarClickListener(new MsgListAdapter.OnAvatarClickListener<MyMessage>() {
            @Override
            public void onAvatarClick(MyMessage message) {
                //todo 弹出用户资料
            }
        });

        mAdapter.setMsgStatusViewClickListener(new MsgListAdapter.OnMsgStatusViewClickListener<MyMessage>() {
            @Override
            public void onStatusViewClick(MyMessage message) {
                // message status view click, resend or download here
            }
        });
        //todo 离线消息
//        MyMessage message = new MyMessage("Hello World", IMessage.MessageType.RECEIVE_TEXT);
//        message.setUserInfo(new DefaultUser("0", "Deadpool", "R.drawable.deadpool"));
//        mAdapter.addToStart(message, true);
        MyMessage eventMsg = new MyMessage("haha", IMessage.MessageType.EVENT);
        mAdapter.addToStart(eventMsg, true);
        mAdapter.addToEnd(mData);
        PullToRefreshLayout layout = mChatView.getPtrLayout();
        layout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PullToRefreshLayout layout) {
                LogUtil.i("MessageListActivity", "Loading next page");
                loadNextPage();
            }
        });
        // Deprecated, should use onRefreshBegin to load next page
        mAdapter.setOnLoadMoreListener(new MsgListAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int page, int totalCount) {
//                Log.i("MessageListActivity", "Loading next page");
//                loadNextPage();
            }
        });

        mChatView.setAdapter(mAdapter);
        mAdapter.getLayoutManager().scrollToPosition(0);
    }

    private void loadNextPage() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<MyMessage> list = new ArrayList<>();
                Resources res = getResources();
                String[] messages = {"测试"};
                for (int i = 0; i < messages.length; i++) {
                    MyMessage message;
                    if (i % 2 == 0) {
                        message = new MyMessage(messages[i], IMessage.MessageType.RECEIVE_TEXT);
                        message.setUserInfo(otherUser);
                    } else {
                        message = new MyMessage(messages[i], IMessage.MessageType.SEND_TEXT);
                        message.setUserInfo(user);
                    }
                    message.setTimeString(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
                    list.add(message);
                }
                Collections.reverse(list);
                mAdapter.addToEnd(list);
                mChatView.getPtrLayout().refreshComplete();
            }
        }, 1500);
    }

    @Override
    public void onKeyBoardStateChanged(int state) {
        switch (state) {
            case ChatInputView.KEYBOARD_STATE_INIT:
                ChatInputView chatInputView = mChatView.getChatInputView();
                if (mImm != null) {
                    mImm.isActive();
                }
                if (chatInputView.getMenuState() == View.INVISIBLE
                        || (!chatInputView.getSoftInputState()
                        && chatInputView.getMenuState() == View.GONE)) {

                    mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                            | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    chatInputView.dismissMenuLayout();
                }
                break;
        }
    }

    private void scrollToBottom() {
        mAdapter.getLayoutManager().scrollToPosition(0);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (oldh - h > 300) {
            mChatView.setMenuHeight(oldh - h);
        }
        scrollToBottom();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ChatInputView chatInputView = mChatView.getChatInputView();

                if (view.getId() == chatInputView.getInputView().getId()) {

                    if (chatInputView.getMenuState() == View.VISIBLE
                            && !chatInputView.getSoftInputState()) {
                        chatInputView.dismissMenuAndResetSoftMode();
                        return false;
                    } else {
                        return false;
                    }
                }
                if (chatInputView.getMenuState() == View.VISIBLE) {
                    chatInputView.dismissMenuLayout();
                }
                try {
                    View v = getCurrentFocus();
                    if (mImm != null && v != null) {
                        mImm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                        view.clearFocus();
                        chatInputView.setSoftInputState(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        JMessageClient.unRegisterEventReceiver(this);
    }

    public void onEventMainThread(MessageEvent event) {
        String msg = "";
        try {
            JSONObject json = new JSONObject(event.getMessage().getContent().toJson());
            msg = json.getString("text");
        } catch (Exception e) {
            LogUtil.e("ChatActivity", e.toString());

        }
        MyMessage message = new MyMessage(msg, IMessage.MessageType.RECEIVE_TEXT);
        message.setUserInfo(otherUser);
        mAdapter.addToStart(message, true);

    }

    public void onEvent(OfflineMessageEvent event) {
        //获取事件发生的会话对象
        Conversation conversation = event.getConversation();
        List<Message> newMessageList = event.getOfflineMessageList();//获取此次离线期间会话收到的新消息列表
        System.out.println(String.format(Locale.SIMPLIFIED_CHINESE, "收到%d条来自%s的离线消息。\n", newMessageList.size(), conversation.getTargetId()));
    }


    private void initUser() {
        Intent intent = getIntent();
        UserInfo info = JMessageClient.getMyInfo();
        String phone = intent.getStringExtra("phone");
        String username = intent.getStringExtra("username");
        String url = intent.getStringExtra("url");
        user = new DefaultUser(phone, username, url);
        otherUser = new DefaultUser(info.getUserName(), info.getUserName(), info.getExtra("url"));
    }

    private class HeadsetDetectReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                if (intent.hasExtra("state")) {
                    int state = intent.getIntExtra("state", 0);
                    mAdapter.setAudioPlayByEarPhone(state);
                }
            }
        }
    }


}






