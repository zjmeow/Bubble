package com.stonymoon.bubble.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.squareup.picasso.Picasso;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.ui.friend.ChatActivity;
import com.stonymoon.bubble.util.MessageUtil;

import java.util.List;

import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;

import static com.stonymoon.bubble.util.DateUtil.CalculateTime;


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
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
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
                .into(holder.ivAvatar);
        //.placeholder(R.drawable.icon_placeholder)
        holder.tvTitle.setText(userInfo.getDisplayName());
        String latestMessage = MessageUtil.getMessageText(bean.getLatestMessage());
        holder.tvFirst.setText(latestMessage);
        holder.tvTime.setText(String.valueOf(CalculateTime(bean.getLatestMessage().getCreateTime())));
//极光推送在未读消息处理上有bug
//        if(bean.getUnReadMsgCnt() != 0) {
//            holder.tvNewMessage.setText(String.valueOf(bean.getUnReadMsgCnt()));
//            holder.tvNewMessage.setVisibility(View.VISIBLE);
//
//        } else {
//            holder.tvNewMessage.setText("");
//            holder.tvNewMessage.setVisibility(View.GONE);
//        }

    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        QMUIRadiusImageView ivAvatar;
        TextView tvTitle;
        TextView tvFirst;
        TextView tvTime;
        TextView tvNewMessage;
        RelativeLayout relativeLayout;
        public ViewHolder(View view) {
            super(view);
            ivAvatar = (QMUIRadiusImageView) view.findViewById(R.id.msg_item_head_icon);
            tvTitle = (TextView) view.findViewById(R.id.conv_item_name);
            tvFirst = (TextView) view.findViewById(R.id.msg_item_content);
            tvTime = (TextView) view.findViewById(R.id.msg_item_date);

            tvNewMessage = (TextView) view.findViewById(R.id.new_msg_number);
            relativeLayout = (RelativeLayout) view.findViewById(R.id.rl_msg_item);



        }

    }

}
