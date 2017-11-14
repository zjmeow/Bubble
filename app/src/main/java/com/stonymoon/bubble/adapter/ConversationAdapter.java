package com.stonymoon.bubble.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.ui.friend.ChatActivity;
import com.stonymoon.bubble.util.MessageUtil;

import java.util.List;

import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.UserInfo;


public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {
    private List<Conversation> conversationList;
    private Context mContext;

    public ConversationAdapter(List<Conversation> list) {
        this.conversationList = list;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();

        }
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Conversation conversation = conversationList.get(position);
                ChatActivity.startActivity(mContext, ((UserInfo) conversation.getTargetInfo()).getUserName());

            }

        });


        return holder;

    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        Conversation bean = conversationList.get(position);

        UserInfo userInfo = (UserInfo) bean.getTargetInfo();

        userInfo.getExtra("url");
        Picasso.with(mContext)
                .load(userInfo.getExtra("url"))
                .into(holder.imageView);
        //.placeholder(R.drawable.icon_placeholder)
        holder.titleView.setText(userInfo.getDisplayName());
        String latestMessage = MessageUtil.getMessageText(bean.getLatestMessage());
        holder.firstText.setText(latestMessage);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleView;
        TextView firstText;

        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.msg_item_head_icon);
            titleView = (TextView) view.findViewById(R.id.conv_item_name);
            firstText = (TextView) view.findViewById(R.id.msg_item_content);

        }

    }

}
