package com.stonymoon.bubble.ui.share;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.bean.AUserBean;
import com.stonymoon.bubble.bean.BubbleBean;
import com.stonymoon.bubble.bean.UserBean;
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
    private Context mContext;
    private Map parameters = new HashMap();
    private BubbleBean.ContentBean bean;

    public static void startActivity(Context context, BubbleBean.ContentBean bean) {
        Intent intent = new Intent(context, BubbleDetailActivity.class);
        intent.putExtra("bean", bean);
        context.startActivity(intent);


    }

    @OnClick(R.id.iv_bubble_detail_head)
    void openProfile() {
        //todo 用户资料全部要放在java服务器上托管或者JAVA服务端要提供手机号
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
        loadUser();

    }

    private void loadUser() {
        String url = "user/" + bean.getUid();
        HttpUtil.sendHttpRequest(this).rxGet(url, parameters
                , new RxStringCallback() {
                    @Override
                    public void onNext(Object tag, String response) {
                        Gson gson = new Gson();
                        AUserBean userBean = gson.fromJson(response, AUserBean.class);
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


}
