package com.stonymoon.bubble.adapter;

import android.content.ClipboardManager;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.squareup.picasso.Picasso;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.ui.friend.ProfileActivity;
import com.stonymoon.bubble.util.DateUtil;

import java.util.List;

import static com.stonymoon.bubble.bean.BubbleCommentBean.DataBean;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private List<DataBean> mList;
    private Context mContext;

    public CommentAdapter(List<DataBean> list) {
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
                DataBean bean = mList.get(position - 2);
                ProfileActivity.startActivity(mContext, bean.getUserId() + "");

            }

        });
        holder.constraintLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                if (holder.tvContent.getText() != null) {
                    cm.setText(holder.tvContent.getText().toString());
                }
                Toast.makeText(mContext, "内容已复制到剪贴板", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        return holder;

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        DataBean bean = mList.get(position);
        Picasso.with(mContext)
                .load(bean.getAvatar())
                .into(holder.ivAvatar);
        //.placeholder(R.drawable.icon_placeholder)
        holder.tvName.setText(bean.getUsername());
        holder.tvContent.setText(bean.getContent());
        holder.tvDate.setText(DateUtil.CalculateTime(bean.getCreatedTime().getTime()));

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        QMUIRadiusImageView ivAvatar;
        TextView tvName;
        TextView tvContent;
        TextView tvDate;
        ConstraintLayout constraintLayout;


        public ViewHolder(View view) {
            super(view);
            ivAvatar = (QMUIRadiusImageView) view.findViewById(R.id.iv_comment_item_avatar);
            tvName = (TextView) view.findViewById(R.id.tv_comment_item_username);
            tvDate = (TextView) view.findViewById(R.id.tv_comment_item_date);
            tvContent = (TextView) view.findViewById(R.id.tv_comment_item_content);
            constraintLayout = (ConstraintLayout) view.findViewById(R.id.constraint_layout_comment_item);
        }

    }


}
