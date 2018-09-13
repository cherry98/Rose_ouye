/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */

package com.orange.oy.baidmap;


import com.baidu.mapapi.model.LatLngBounds;
import com.orange.oy.clusterutil.clustering.Cluster;
import com.orange.oy.clusterutil.clustering.ClusterItem;

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

    Set<Cluster<T>> getClusters(double zoom, LatLngBounds l);

    Collection<T> getItems();
}