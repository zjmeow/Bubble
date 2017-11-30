package com.stonymoon.bubble.ui.share;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.gson.Gson;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.bean.AUserBean;
import com.stonymoon.bubble.bean.BubbleBean;
import com.stonymoon.bubble.bean.BubbleDetailBean;
import com.stonymoon.bubble.bean.UserBean;
import com.stonymoon.bubble.ui.common.PhotoActivity;
import com.stonymoon.bubble.ui.friend.ProfileActivity;
import com.stonymoon.bubble.util.AuthUtil;
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

    @OnClick(R.id.iv_bubble_detail_comment)
    void comment() {
        showEditTextDialog();
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
        bean.getClick();


        initDeadline();


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


    private void initDeadline() {
        String url = "download/m/" + bean.getId();
        HttpUtil.sendHttpRequest(this).rxGet(url, new HashMap<String, Object>(), new RxStringCallback() {
            @Override
            public void onNext(Object tag, String response) {
                Gson gson = new Gson();
                BubbleDetailBean.ContentBean bubbleDetailBean = gson.fromJson(response, BubbleDetailBean.class).getContent();
                survivalMinute = (bubbleDetailBean.getDeadline() - bubbleDetailBean.getTime()) / 60000;
                tvSurvival.setText("泡泡将在" + (survivalMinute / 60) + "小时" + survivalMinute % 60 + "分钟" + "后破掉");
            }

            @Override
            public void onError(Object tag, Throwable e) {
                survivalMinute = (bean.getDeadline() - bean.getTime()) / 60000;
                tvSurvival.setText("泡泡将在" + (survivalMinute / 60) + "小时" + survivalMinute % 60 + "分钟" + "后破掉");
            }

            @Override
            public void onCancel(Object tag, Throwable e) {

            }
        });


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
        HttpUtil.sendHttpRequest(this).rxPost("time/add", parameters, new RxStringCallback() {
            @Override
            public void onNext(Object tag, String response) {
                survivalMinute++;
                tvSurvival.setText("泡泡将在" + (survivalMinute / 60) + "小时" + survivalMinute % 60 + "分钟" + "后破掉");
            }

            @Override
            public void onError(Object tag, Throwable e) {

            }

            @Override
            public void onCancel(Object tag, Throwable e) {

            }
        });


    }


    private void showEditTextDialog() {
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(BubbleDetailActivity.this);
        builder.setTitle("评论")
                .setPlaceholder("评论")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        CharSequence text = builder.getEditText().getText();
                        if (text != null && text.length() > 0) {
                            uploadComment(text.toString(), bean.getId() + "");


                            dialog.dismiss();
                        } else {
                            Toast.makeText(BubbleDetailActivity.this, "请输入内容", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();
    }

    private void uploadComment(String content, String id) {
        parameters.clear();
        parameters.put("pid", id);
        parameters.put("token", AuthUtil.getToken());
        parameters.put("content", content);
        String url = "comment";
        HttpUtil.sendHttpRequest(this).rxPost(url, parameters, new RxStringCallback() {
            @Override
            public void onNext(Object tag, String response) {
                Toast.makeText(BubbleDetailActivity.this, "评论成功", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(Object tag, Throwable e) {
                Toast.makeText(BubbleDetailActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(Object tag, Throwable e) {

            }
        });


    }


}
