/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */

package com.stonymoon.bubble.util.clusterutil.clustering.algo;

import com.stonymoon.bubble.util.clusterutil.clustering.Cluster;
import com.stonymoon.bubble.util.clusterutil.clustering.ClusterItem;

import java.util.Collection;
import java.util.Set;

/**
 * Logic for computing clusters
 */
public interface Algorithm<T extends ClusterItem> {
    void addItem(T item);

    void addItems(Collection<T> items);

    void clearItems();

    void removeItem(T item);

    Set<? extends Cluster<T>> getClusters(double zoom);

    Collection<T> getItems();
}