package com.stonymoon.bubble.ui.share;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.bean.BubbleBean;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BubbleDetailActivity extends AppCompatActivity {
    @BindView(R.id.iv_bubble_detail)
    ImageView ivBubbleDetail;
    @BindView(R.id.tv_bubble_detail_title)
    TextView tvTitle;
    @BindView(R.id.tv_bubble_detail_content)
    TextView tvContent;
    @BindView(R.id.iv_bubble_detail_head)
    ImageView ivHead;
    @BindView(R.id.tv_bubble_detail_author_name)
    TextView tvAuthorName;
    private Context mContext;
    private Map parameters = new HashMap();
    private BubbleBean.ContentBean bean;

    public static void startActivity(Context context, BubbleBean.ContentBean bean) {
        Intent intent = new Intent(context, BubbleDetailActivity.class);
        intent.putExtra("bean", bean);
        context.startActivity(intent);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubble_detail);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        Intent intent = getIntent();
        bean = (BubbleBean.ContentBean) intent.getSerializableExtra("bean");
        Glide.with(mContext).load(bean.getImage()).into(ivBubbleDetail);
        tvTitle.setText(bean.getTitle());
        tvContent.setText(bean.getContent());





    }



}
