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
import com.stonymoon.bubble.bean.JUserBean;

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
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                UserInfo bean = userList.get(position);

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
                .into(holder.imageView);
        //.placeholder(R.drawable.icon_placeholder)
        holder.titleView.setText(bean.getUserName());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleView;

        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.iv_friend_head);
            titleView = (TextView) view.findViewById(R.id.tv_friend_text);

        }

    }

}
