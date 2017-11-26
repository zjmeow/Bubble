package com.stonymoon.bubble.ui.share;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.gson.Gson;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.bean.AUserBean;
import com.stonymoon.bubble.bean.BubbleBean;
import com.stonymoon.bubble.bean.UserBean;
import com.stonymoon.bubble.ui.common.PhotoActivity;
import com.stonymoon.bubble.ui.friend.ProfileActivity;
import com.stonymoon.bubble.util.DateUtil;
import com.stonymoon.bubble.util.HttpUtil;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.RxStringCallback;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.http.HTTP;

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
    @BindView(R.id.tv_bubble_detail_time)
    TextView tvTime;
    @BindView(R.id.toolbar_bubble_detail)
    Toolbar toolbar;
    @BindView(R.id.tv_bubble_detail_survival_time)
    TextView tvSurvival;
    long survivalMinute;

    private Context mContext;
    private Map<String, Object> parameters = new HashMap<>();
    private BubbleBean.ContentBean bean;
    private AUserBean userBean;
    public static void startActivity(Context context, BubbleBean.ContentBean bean) {
        Intent intent = new Intent(context, BubbleDetailActivity.class);
        intent.putExtra("bean", bean);
        context.startActivity(intent);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @OnClick(R.id.iv_bubble_detail_head)
    void openProfile() {
        ProfileActivity.startActivity(this, userBean.getContent().getPhone());

    }

    @OnClick(R.id.iv_bubble_detail)
    void openImage() {
        PhotoActivity.startActivity(this, bean.getImage());

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QMUIStatusBarHelper.translucent(this);
        setContentView(R.layout.activity_bubble_detail);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        Intent intent = getIntent();
        bean = (BubbleBean.ContentBean) intent.getSerializableExtra("bean");
        Glide.with(mContext).load(bean.getImage()).into(ivBubbleDetail);
        tvTitle.setText(bean.getTitle());
        tvContent.setText(bean.getContent());
        tvTime.setText(DateUtil.CalculateTime(bean.getTime()));


        survivalMinute = (bean.getDeadline() - bean.getTime()) / 60000;
        tvSurvival.setText("泡泡将在" + survivalMinute + "分钟后消失");
        if (bean.getAnonymous() == 0) {
            loadUser();
        } else {
            Glide.with(mContext).load(R.drawable.anonymous).into(ivHead);
            tvAuthorName.setText("匿名用户");
            ivHead.setClickable(false);

        }







        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
        }


    }

    private void loadUser() {
        String url = "user/" + bean.getUid();
        HttpUtil.sendHttpRequest(this).rxGet(url, parameters
                , new RxStringCallback() {
                    @Override
                    public void onNext(Object tag, String response) {
                        Gson gson = new Gson();
                        userBean = gson.fromJson(response, AUserBean.class);
                        Glide.with(mContext).load(userBean.getContent().getImage()).into(ivHead);
                        tvAuthorName.setText(userBean.getContent().getUsername());
                    }

                    @Override
                    public void onError(Object tag, Throwable e) {

                    }

                    @Override
                    public void onCancel(Object tag, Throwable e) {

                    }
                });

    }

    @OnClick(R.id.btn_bubble_detail_add)
    void add() {
        parameters.clear();
        parameters.put("id", bean.getId() + "");
        HttpUtil.sendHttpRequest(this).rxPost("tap", parameters, new RxStringCallback() {
            @Override
            public void onNext(Object tag, String response) {
                survivalMinute++;
                tvSurvival.setText("泡泡将在" + survivalMinute + "分钟后消失");
            }

            @Override
            public void onError(Object tag, Throwable e) {

            }

            @Override
            public void onCancel(Object tag, Throwable e) {

            }
        });


    }


}
