package com.stonymoon.bubble.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.bean.BubbleBean;
import com.stonymoon.bubble.ui.share.BubbleDetailActivity;
import com.stonymoon.bubble.util.DateUtil;
import com.stonymoon.bubble.view.FloatingMenu;

import java.util.List;

import cn.jpush.im.android.api.model.UserInfo;


public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ViewHolder> {
    private List<BubbleBean.ContentBean> mList;
    private Context mContext;

    public ShareAdapter(List<BubbleBean.ContentBean> list) {
        this.mList = list;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();

        }
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_share, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                BubbleBean.ContentBean bean = mList.get(position);
                BubbleDetailActivity.startActivity(mContext, bean);

            }

        });


        return holder;

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        BubbleBean.ContentBean bean = mList.get(position);
        Picasso.with(mContext)
                .load(bean.getImage())
                .placeholder(R.mipmap.test)
                .into(holder.imageView)
        ;
        holder.titleView.setText(bean.getTitle());
        if (bean.getContent().equals("")) {
            holder.contentView.setVisibility(View.GONE);
        } else {
            holder.contentView.setVisibility(View.VISIBLE);
            holder.contentView.setText(bean.getContent());
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleView;
        TextView contentView;
        CardView cardView;

        public ViewHolder(View view) {


            super(view);
            imageView = (ImageView) view.findViewById(R.id.iv_share_item_image);
            titleView = (TextView) view.findViewById(R.id.tv_share_item_title);
            cardView = (CardView) view.findViewById(R.id.constraint_layout_share_item);
            contentView = (TextView) view.findViewById(R.id.tv_share_item_content);
        }

    }

}
