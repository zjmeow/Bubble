package com.stonymoon.bubble.ui.share;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.adapter.CommentAdapter;
import com.stonymoon.bubble.base.StatusBarLightActivity;
import com.stonymoon.bubble.bean.AUserBean;
import com.stonymoon.bubble.bean.BubbleBean;
import com.stonymoon.bubble.bean.BubbleDetailBean;
import com.stonymoon.bubble.bean.CommentBean;
import com.stonymoon.bubble.bean.UserBean;
import com.stonymoon.bubble.ui.common.PhotoActivity;
import com.stonymoon.bubble.ui.friend.ProfileActivity;
import com.stonymoon.bubble.util.AuthUtil;
import com.stonymoon.bubble.util.DateUtil;
import com.stonymoon.bubble.util.HttpUtil;
import com.stonymoon.bubble.util.SpringScaleInterpolator;
import com.stonymoon.bubble.util.UrlUtil;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.RxStringCallback;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.http.HTTP;
import retrofit2.http.Url;

public class BubbleDetailActivity extends StatusBarLightActivity implements View.OnClickListener {
    @BindView(R.id.iv_bubble_detail)
    ImageView ivBubbleDetail;
    @BindView(R.id.toolbar_bubble_detail)
    Toolbar toolbar;


    @BindView(R.id.recycler_bubble_detail_comment)
    XRecyclerView recyclerComment;
    private TextView tvTitle;
    private TextView tvContent;
    private ImageView ivHead;
    private TextView tvAuthorName;
    private TextView tvTime;
    private TextView tvSurvival;
    private ImageView ivComment;
    private ImageView ivAdd;
    private TextView tvEmojiNumber;
    private BubbleBean.ContentBean bean;
    private ImageView ivSurvival;

    private long survivalMinute;
    private Context mContext;
    private Map<String, Object> parameters = new HashMap<>();

    private List<CommentBean.ContentBean.ListBean> commentBeanList = new ArrayList<>();
    private CommentAdapter adapter = new CommentAdapter(commentBeanList);
    private int page = 1;
    private int emojiNumber;
    private boolean hasMoreComment = true;
    private View headView;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QMUIStatusBarHelper.translucent(this);
        setContentView(R.layout.activity_bubble_detail);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        initView();
        initRecyclerView();

    }

    private void initRecyclerView() {
        recyclerComment.setAdapter(adapter);
        recyclerComment.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        if (hasMoreComment) {
            getComment();
        }
        recyclerComment.addHeaderView(headView);
        recyclerComment.setPullRefreshEnabled(false);
        recyclerComment.setLoadingMoreEnabled(true);
        recyclerComment.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                page = 1;
                commentBeanList.clear();
                getComment();
            }

            @Override
            public void onLoadMore() {
                if (hasMoreComment) {
                    getComment();
                } else {
                    recyclerComment.loadMoreComplete();
                    Toast.makeText(BubbleDetailActivity.this, "没有更多的评论了", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void initView() {
        Intent intent = getIntent();
        bean = (BubbleBean.ContentBean) intent.getSerializableExtra("bean");
        Glide.with(mContext).load(bean.getImage()).into(ivBubbleDetail);
        headView = LayoutInflater.from(this).inflate(R.layout.header_bubble_detail, (ViewGroup) findViewById(R.id.layout_bubble_detail), false);
        tvTitle = (TextView) headView.findViewById(R.id.tv_bubble_detail_title);
        tvContent = (TextView) headView.findViewById(R.id.tv_bubble_detail_content);
        tvTime = (TextView) headView.findViewById(R.id.tv_bubble_detail_time);
        tvAuthorName = (TextView) headView.findViewById(R.id.tv_bubble_detail_author_name);
        ivHead = (QMUIRadiusImageView) headView.findViewById(R.id.iv_bubble_detail_head);
        tvSurvival = (TextView) headView.findViewById(R.id.tv_bubble_detail_survival_time);
        ivComment = (ImageView) headView.findViewById(R.id.iv_bubble_detail_comment);
        ivAdd = (ImageView) headView.findViewById(R.id.iv_bubble_detail_add);
        tvEmojiNumber = (TextView) headView.findViewById(R.id.tv_bubble_detail_emoji_number);
        ivSurvival = (ImageView) headView.findViewById(R.id.iv_bubble_detail_time);



        ivBubbleDetail.setOnClickListener(this);
        ivHead.setOnClickListener(this);
        ivComment.setOnClickListener(this);
        ivAdd.setOnClickListener(this);
        bean.getClick();
        initDeadline();
        setBar();
    }

    private void setBar() {
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
        String url = UrlUtil.getBubbleDetail(bean.getId() + "");
        HttpUtil.sendHttpRequest(this).rxGet(url, new HashMap<String, Object>(), new RxStringCallback() {
            @Override
            public void onNext(Object tag, String response) {
                Gson gson = new Gson();
                BubbleDetailBean.ContentBean bean = gson.fromJson(response, BubbleDetailBean.class).getContent();
                survivalMinute = (bean.getDeadline() - System.currentTimeMillis()) / 60000;
                tvSurvival.setText((survivalMinute / 60) + "小时" + survivalMinute % 60 + "分钟");
                tvTitle.setText(bean.getTitle());
                tvContent.setText(bean.getContent());
                tvTime.setText(DateUtil.CalculateTime(bean.getTime()));

                emojiNumber = bean.getClick();
                tvEmojiNumber.setText(emojiNumber + "");
                if (bean.getAnonymous() == 0) {
                    Glide.with(mContext).load(bean.getMiniUser().getImage()).into(ivHead);
                    tvAuthorName.setText(bean.getMiniUser().getUsername());
                } else {
                    Glide.with(mContext).load(R.drawable.anonymous).into(ivHead);
                    tvAuthorName.setText("匿名用户");
                    ivHead.setClickable(false);

                }



            }

            @Override
            public void onError(Object tag, Throwable e) {
                survivalMinute = (bean.getDeadline() - System.currentTimeMillis()) / 60000;
                tvSurvival.setText((survivalMinute / 60) + "小时" + survivalMinute % 60 + "分钟");
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
        String url = UrlUtil.postBubbleComments();
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

    private void getComment() {
        parameters.clear();
        String url = UrlUtil.getBubbleComments(bean.getId() + "", page + "");
        HttpUtil.sendHttpRequest(this).rxGet(url, parameters, new RxStringCallback() {
            @Override
            public void onNext(Object tag, String response) {
                Gson gson = new Gson();
                CommentBean bean = gson.fromJson(response, CommentBean.class);
                commentBeanList.addAll(bean.getContent().getList());
                hasMoreComment = bean.getContent().isHasNextPage();
                adapter.notifyDataSetChanged();
                page++;
                recyclerComment.loadMoreComplete();
            }

            @Override
            public void onError(Object tag, Throwable e) {
                recyclerComment.refreshComplete();
            }

            @Override
            public void onCancel(Object tag, Throwable e) {

            }
        });


    }


    void add() {
        parameters.clear();
        parameters.put("id", bean.getId() + "");
        String url = UrlUtil.getBubbleAddTime();
        HttpUtil.sendHttpRequest(this).rxPost(url, parameters, new RxStringCallback() {
            @Override
            public void onNext(Object tag, String response) {
                survivalMinute++;
                emojiNumber++;
                tvEmojiNumber.setText(emojiNumber + "");
                tvSurvival.setText((survivalMinute / 60) + "小时" + survivalMinute % 60 + "分钟");
            }

            @Override
            public void onError(Object tag, Throwable e) {

            }

            @Override
            public void onCancel(Object tag, Throwable e) {

            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_bubble_detail_comment:
                showEditTextDialog();
                break;
            case R.id.iv_bubble_detail_head:
                ProfileActivity.startActivity(this, bean.getMiniUser().getPhone(), bean.getUid() + "");
                break;
            case R.id.iv_bubble_detail:
                PhotoActivity.startActivity(this, bean.getImage());
                break;
            case R.id.iv_bubble_detail_add:
                add();
                ObjectAnimator animatorX = ObjectAnimator.ofFloat(ivAdd, "scaleX", 0.7f, 1.0f);
                ObjectAnimator animatorY = ObjectAnimator.ofFloat(ivAdd, "scaleY", 0.7f, 1.0f);
                AnimatorSet showBubbleSet = new AnimatorSet();
                showBubbleSet.setDuration(1000);
                showBubbleSet.setInterpolator(new SpringScaleInterpolator(0.4f));
                showBubbleSet.playTogether(animatorX, animatorY);
                showBubbleSet.start();
                ObjectAnimator animatorX1 = ObjectAnimator.ofFloat(ivSurvival, "scaleX", 0.7f, 1.0f);
                ObjectAnimator animatorY1 = ObjectAnimator.ofFloat(ivSurvival, "scaleY", 0.7f, 1.0f);
                AnimatorSet showBubbleSet1 = new AnimatorSet();
                showBubbleSet1.setDuration(1000);
                showBubbleSet1.setInterpolator(new SpringScaleInterpolator(0.4f));
                showBubbleSet1.playTogether(animatorX1, animatorY1);
                showBubbleSet1.start();

                break;
        }

    }


}
