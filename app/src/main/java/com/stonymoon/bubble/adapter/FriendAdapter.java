package com.stonymoon.bubble.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.squareup.picasso.Picasso;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.ui.friend.ChatActivity;

import java.util.List;

import cn.jpush.im.android.api.model.UserInfo;


public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private List<UserInfo> userList;
    private Context mContext;

    public FriendAdapter(List<UserInfo> list) {
        this.userList = list;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();

        }
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                UserInfo bean = userList.get(position);
                ChatActivity.startActivity(mContext, bean.getUserName());

            }

        });


        return holder;

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        UserInfo bean = userList.get(position);
        Picasso.with(mContext)
                .load(bean.getExtra("url"))
                .into(holder.ivAvatar);
        //.placeholder(R.drawable.icon_placeholder)
        holder.tvName.setText(bean.getDisplayName());
        holder.tvSign.setText(bean.getSignature());

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        QMUIRadiusImageView ivAvatar;
        TextView tvName;
        TextView tvSign;
        ConstraintLayout constraintLayout;

        public ViewHolder(View view) {
            super(view);
            ivAvatar = (QMUIRadiusImageView) view.findViewById(R.id.iv_friend_head);
            tvName = (TextView) view.findViewById(R.id.tv_friend_text);
            constraintLayout = (ConstraintLayout) view.findViewById(R.id.ll_friend_item);

            tvSign = (TextView) view.findViewById(R.id.tv_friend_sign);

        }

    }


}
