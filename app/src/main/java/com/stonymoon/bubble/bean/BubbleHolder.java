package com.stonymoon.bubble.bean;

import com.stonymoon.bubble.util.clusterutil.clustering.Cluster;

/**
 * 用来分享bubble的单例
 */

public class BubbleHolder {
    private static final BubbleHolder holder = new BubbleHolder();
    private Cluster cluster;

    public static BubbleHolder getInstance() {
        return holder;
    }

    public Cluster getData() {
        return cluster;
    }

    public void setData(Cluster data) {
        this.cluster = data;
    }
}