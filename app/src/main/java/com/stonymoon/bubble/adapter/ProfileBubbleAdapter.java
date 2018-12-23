package com.stonymoon.bubble.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.bean.BubbleBean;
import com.stonymoon.bubble.ui.share.BubbleDetailActivity;

import java.util.List;


public class ProfileBubbleAdapter extends RecyclerView.Adapter<ProfileBubbleAdapter.ViewHolder> {
    private List<BubbleBean.DataBean> mList;
    private Context mContext;

    public ProfileBubbleAdapter(List<BubbleBean.DataBean> list) {
        this.mList = list;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();

        }
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile_bubble, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                BubbleBean.DataBean bean = mList.get(position);
                BubbleDetailActivity.startActivity(mContext, bean.getId());

            }

        });


        return holder;

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        BubbleBean.DataBean bean = mList.get(position);
        Picasso.with(mContext)
                .load(bean.getPic())
                .placeholder(R.mipmap.test)
                .into(holder.ivContent);
//        holder.tvCommentNum.setText(bean.ge() + "");
//        holder.tvEmojiNum.setText(bean.g() + "");
        holder.tvContent.setText(bean.getTitle());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivContent;
        TextView tvContent;
        LinearLayout linearLayout;
        TextView tvCommentNum;
        TextView tvEmojiNum;

        public ViewHolder(View view) {
            super(view);
            ivContent = (ImageView) view.findViewById(R.id.iv_share_item_image);
            linearLayout = (LinearLayout) view.findViewById(R.id.constraint_layout_share_item);
            tvContent = (TextView) view.findViewById(R.id.tv_share_item_content);
            tvCommentNum = (TextView) view.findViewById(R.id.tv_share_item_comments_num);
            tvEmojiNum = (TextView) view.findViewById(R.id.tv_share_item_emoji_num);
        }

    }
}
