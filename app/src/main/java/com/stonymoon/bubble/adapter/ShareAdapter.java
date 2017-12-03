package com.stonymoon.bubble.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.squareup.picasso.Picasso;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.bean.BubbleBean;
import com.stonymoon.bubble.ui.share.BubbleDetailActivity;
import com.stonymoon.bubble.util.DateUtil;
import com.stonymoon.bubble.view.FloatingMenu;

import org.w3c.dom.Text;

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
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
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
                .into(holder.ivContent);
        holder.tvContent.setText(bean.getTitle());
//        if (bean.getContent().equals("")) {
//            holder.tvContent.setVisibility(View.GONE);
//        } else {
//            holder.tvContent.setVisibility(View.VISIBLE);
//            holder.tvContent.setText(bean.getContent());
//        }
        holder.tvName.setText(bean.getMiniUser().getUsername());
        holder.tvDate.setText(DateUtil.CalculateTime(bean.getTime()));
        Picasso.with(mContext)
                .load(bean.getMiniUser().getImage())
                .placeholder(R.mipmap.test)
                .into(holder.ivAvatar);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivContent;
        //TextView tvTitle;
        TextView tvContent;
        LinearLayout linearLayout;
        TextView tvName;
        TextView tvDate;
        QMUIRadiusImageView ivAvatar;


        public ViewHolder(View view) {
            super(view);
            ivContent = (ImageView) view.findViewById(R.id.iv_share_item_image);
            //tvTitle = (TextView) view.findViewById(R.id.tv_share_item_title);
            linearLayout = (LinearLayout) view.findViewById(R.id.constraint_layout_share_item);
            tvContent = (TextView) view.findViewById(R.id.tv_share_item_content);
            tvName = (TextView) view.findViewById(R.id.tv_share_item_username);
            tvDate = (TextView) view.findViewById(R.id.tv_share_item_date);
            ivAvatar = (QMUIRadiusImageView) view.findViewById(R.id.iv_share_item_avatar);

        }

    }

}
