package com.stonymoon.bubble.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.squareup.picasso.Picasso;
import com.stonymoon.bubble.R;

import static com.stonymoon.bubble.bean.CommentBean.ContentBean.ListBean;

import com.stonymoon.bubble.ui.friend.ProfileActivity;
import com.stonymoon.bubble.util.DateUtil;

import java.util.List;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private List<ListBean> mList;
    private Context mContext;

    public CommentAdapter(List<ListBean> list) {
        this.mList = list;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();

        }
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                ListBean bean = mList.get(position);
                ProfileActivity.startActivity(mContext, bean.getMiniUser().getPhone());

            }

        });


        return holder;

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        ListBean bean = mList.get(position);
        Picasso.with(mContext)
                .load(bean.getMiniUser().getImage())
                .into(holder.ivAvatar);
        //.placeholder(R.drawable.icon_placeholder)
        holder.tvName.setText(bean.getMiniUser().getUsername());
        holder.tvContent.setText(bean.getContent());
        holder.tvDate.setText(DateUtil.CalculateTime(bean.getTime()));

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        QMUIRadiusImageView ivAvatar;
        TextView tvName;
        TextView tvContent;
        TextView tvDate;


        public ViewHolder(View view) {
            super(view);
            ivAvatar = (QMUIRadiusImageView) view.findViewById(R.id.iv_comment_item_avatar);
            tvName = (TextView) view.findViewById(R.id.tv_comment_item_username);
            tvDate = (TextView) view.findViewById(R.id.tv_comment_item_date);
            tvContent = (TextView) view.findViewById(R.id.tv_comment_item_content);

        }

    }


}
