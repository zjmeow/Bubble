package com.stonymoon.bubble.ui.share;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
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
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.stonymoon.bubble.R;
import com.stonymoon.bubble.adapter.CommentAdapter;
import com.stonymoon.bubble.api.BaseDataManager;
import com.stonymoon.bubble.api.serivces.BubbleService;
import com.stonymoon.bubble.api.serivces.CommentService;
import com.stonymoon.bubble.base.StatusBarLightActivity;
import com.stonymoon.bubble.bean.BubbleCommentBean;
import com.stonymoon.bubble.bean.BubbleDetailBean;
import com.stonymoon.bubble.bean.UpdateBean;
import com.stonymoon.bubble.ui.common.PhotoActivity;
import com.stonymoon.bubble.ui.friend.ProfileActivity;
import com.stonymoon.bubble.util.DateUtil;
import com.stonymoon.bubble.util.HttpUtil;
import com.stonymoon.bubble.util.LogUtil;
import com.stonymoon.bubble.util.SpringScaleInterpolator;
import com.stonymoon.bubble.util.UrlUtil;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.RxStringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class BubbleDetailActivity extends StatusBarLightActivity implements View.OnClickListener {
    private static final String TAG = "BubbleDetailActivity";
    @BindView(R.id.iv_bubble_detail)
    ImageView ivBubbleDetail;
    @BindView(R.id.toolbar_bubble_detail)
    Toolbar toolbar;

    @BindView(R.id.recycler_bubble_detail_comment)
    XRecyclerView recyclerComment;
    BubbleDetailBean.DataBean bean;
    private TextView tvTitle;
    private TextView tvContent;
    private ImageView ivHead;
    private TextView tvAuthorName;
    private TextView tvTime;
    private TextView tvSurvival;
    private ImageView ivComment;
    private ImageView ivAdd;
    private TextView tvEmojiNumber;
    private long survivalMinute;
    private Context mContext;
    private Map<String, Object> parameters = new HashMap<>();
    private List<BubbleCommentBean.DataBean> commentBeanList = new ArrayList<>();
    private CommentAdapter adapter = new CommentAdapter(commentBeanList);
    private int page = 1;
    private int emojiNumber;
    private View headView;
    private boolean isFirstGetComment = true;

    public static void startActivity(Context context, int id) {
        Intent intent = new Intent(context, BubbleDetailActivity.class);
        intent.putExtra("id", id);
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
        getData();
        initView();
        initRecyclerView();


    }

    private void initRecyclerView() {
        recyclerComment.setAdapter(adapter);
        recyclerComment.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
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
                loadNothing();
            }


        });


    }

    private void loadNothing() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {

                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //getComment();
                        }
                    });
                }

            }
        }.start();
    }

    private void initView() {
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


        ivBubbleDetail.setOnClickListener(this);
        ivHead.setOnClickListener(this);
        ivComment.setOnClickListener(this);
        ivAdd.setOnClickListener(this);
//        bean.getClick();
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
        BaseDataManager.getHttpManager()
                .create(CommentService.class)
                .comment(content, Integer.valueOf(id))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<UpdateBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(java.lang.Throwable e) {
                        Toast.makeText(BubbleDetailActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
                        LogUtil.e(TAG, e.getMessage());
                    }

                    @Override
                    public void onNext(UpdateBean bean) {
                        Toast.makeText(BubbleDetailActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                        page = 1;
                        commentBeanList.clear();
                        getComment();
                    }
                });


    }

    private void getComment() {
        BaseDataManager.getHttpManager()
                .create(CommentService.class)
                .getComment(getIntent().getIntExtra("id", 1))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<BubbleCommentBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(java.lang.Throwable e) {
                        recyclerComment.refreshComplete();
                    }

                    @Override
                    public void onNext(BubbleCommentBean bubbleCommentBean) {
                        if (bubbleCommentBean.getData().isEmpty() && !isFirstGetComment) {
                            Toast.makeText(BubbleDetailActivity.this, "没有更多的评论了", Toast.LENGTH_SHORT).show();
                            recyclerComment.loadMoreComplete();
                            return;
                        }
                        commentBeanList.addAll(bubbleCommentBean.getData());
                        adapter.notifyDataSetChanged();
                        page++;
                        recyclerComment.loadMoreComplete();
                        isFirstGetComment = false;
                        survivalMinute = (bean.getDeadline().getTime() - System.currentTimeMillis()) / 60000;
                        if (survivalMinute > 0) {
                            tvSurvival.setText((survivalMinute / 60) + "小时" + survivalMinute % 60 + "分钟");
                        } else {
                            tvSurvival.setText("泡泡已过期");
                        }
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
                survivalMinute += 60;
                emojiNumber++;
                tvEmojiNumber.setText(emojiNumber + "");
                if (survivalMinute > 0) {
                    tvSurvival.setText((survivalMinute / 60) + "小时" + survivalMinute % 60 + "分钟");
                } else {

                }

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
                ProfileActivity.startActivity(this, bean.getUserId() + "");
                break;
            case R.id.iv_bubble_detail:
                PhotoActivity.startActivity(this, bean.getPic());
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
                break;
        }

    }


    public void getData() {
        BaseDataManager.getHttpManager()
                .create(BubbleService.class)
                .getBubbleDetail(getIntent().getIntExtra("id", 1))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<BubbleDetailBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(java.lang.Throwable e) {

                    }

                    @Override
                    public void onNext(BubbleDetailBean bubbleDetailBean) {

                        BubbleDetailBean.DataBean bean = bubbleDetailBean.getData();
                        BubbleDetailActivity.this.bean = bean;
                        Glide.with(mContext).load(bean.getPic()).into(ivBubbleDetail);
                        tvTitle.setText(bean.getTitle());
                        tvContent.setText(bean.getContent());
                        tvTime.setText(DateUtil.CalculateTime(System.currentTimeMillis()));
                        tvEmojiNumber.setText(bean.getTap() + "");
                        Glide.with(mContext).load(bean.getAvatar()).into(ivHead);
                        tvAuthorName.setText(bean.getUsername());
                        getComment();
                    }
                });


    }
}
