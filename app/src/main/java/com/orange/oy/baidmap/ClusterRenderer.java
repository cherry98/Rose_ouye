/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */

package com.orange.oy.baidmap;

import com.orange.oy.clusterutil.clustering.Cluster;
import com.orange.oy.clusterutil.clustering.ClusterItem;
import com.orange.oy.baidmap.ClusterManager;

import java.util.Set;

/**
 * Renders clusters.
 */
public interface ClusterRenderer<T extends ClusterItem> {

    /**
     * Called when the view needs to be updated because new clusters need to be displayed.
     *
     * @param clusters the clusters to be displayed.
     */
    void onClustersChanged(Set<Cluster<T>> clusters, float curZoom);

    void setOnClusterClickListener(ClusterManager.OnClusterClickListener<T> listener);

    void setOnClusterInfoWindowClickListener(ClusterManager.OnClusterInfoWindowClickListener<T> listener);

    void setOnClusterItemClickListener(ClusterManager.OnClusterItemClickListener<T> listener);

    void setOnClusterItemInfoWindowClickListener(ClusterManager.OnClusterItemInfoWindowClickListener<T> listener);

    /**
     * Called when the view is added.
     */
    void onAdd();

    /**
     * Called when the view is removed.
     */
    void onRemove();
}