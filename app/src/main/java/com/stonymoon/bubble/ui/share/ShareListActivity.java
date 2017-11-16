package com.stonymoon.bubble.ui.share;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.stonymoon.bubble.R;
import com.stonymoon.bubble.adapter.ShareAdapter;
import com.stonymoon.bubble.bean.BubbleBean;
import com.stonymoon.bubble.bean.BubbleHolder;
import com.stonymoon.bubble.util.clusterutil.clustering.Cluster;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShareListActivity extends AppCompatActivity {


    @BindView(R.id.recycler_share)
    RecyclerView recyclerShare;
    List<BubbleBean.ContentBean> mList = new ArrayList<>();
    private ShareAdapter adapter = new ShareAdapter(mList);

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, ShareListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_list);
        ButterKnife.bind(this);
        recyclerShare.setAdapter(adapter);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerShare.setLayoutManager(manager);
        initBeans();
    }

    private void initBeans() {
        Cluster<MapShareActivity.MyItem> cluster = BubbleHolder.getInstance().getData();
        for (MapShareActivity.MyItem item : cluster.getItems()) {
            mList.add(item.getBean());
        }
        adapter.notifyDataSetChanged();
    }


}
