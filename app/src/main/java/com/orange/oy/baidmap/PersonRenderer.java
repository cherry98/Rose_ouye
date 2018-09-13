package com.orange.oy.baidmap;

/**
 * Created by jiong103 on 2017/5/2.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.facebook.common.executors.UiThreadImmediateExecutorService;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.orange.oy.clusterutil.clustering.Cluster;
import com.orange.oy.info.shakephoto.ShakePhotoInfo;
import com.orange.oy.network.Urls;

import java.io.File;
import java.util.WeakHashMap;

/**
 * Draws profile photos inside markers (using IconGenerator).
 * When there are multiple people in the cluster, draw multiple photos (using MultiDrawable).
 */
public class PersonRenderer extends DefaultClusterRenderer<ShakePhotoInfo> implements com.orange.oy.baidmap.ClusterRenderer<ShakePhotoInfo> {
//    private WeakHashMap<Cluster<ShakePhotoInfo>, SimpleTarget> cancleMap = new WeakHashMap<>();

    private WeakHashMap<Cluster<ShakePhotoInfo>, DataSource<CloseableReference<CloseableImage>>> cancleMap1 = new WeakHashMap<>();

    private Activity mContext;


    public PersonRenderer(Activity context, BaiduMap mBaiduMap, ClusterManager mClusterManager) {
        super(context.getApplicationContext(), mBaiduMap, mClusterManager);

        mContext = context;

        //初始化占位图片
    }

    @Override
    protected void onBeforeClusterItemRendered(final ShakePhotoInfo person, final MarkerOptions markerOptions) {

    }

    @Override
    protected void onBeforeClusterRendered(final Cluster<ShakePhotoInfo> cluster, final MarkerOptions markerOptions) {


        System.out.println("jinlai : " + cluster.getSize());
        DataSource<CloseableReference<CloseableImage>> target = cancleMap1.get(cluster);
        if (target != null) {
            target.close();
            cancleMap1.remove(target);
        }

        final ShakePhotoInfo person = cluster.getItems().iterator().next();

        ImageRequest imageRequest = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(Urls.Endpoint3 + person.getFile_url() + "?x-oss-process=image/resize,l_100"))
                .setProgressiveRenderingEnabled(false)
                .setResizeOptions(new ResizeOptions(50, 50))
                .setPostprocessor(new BadgViewPostprocessor(mContext, cluster))
                .build();

        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource =
                imagePipeline.fetchDecodedImage(imageRequest, mContext);

        dataSource.subscribe(new BaseBitmapDataSubscriber() {

            @Override
            public void onNewResultImpl(@Nullable Bitmap bitmap) {
                // You can use the bitmap in only limited ways
                // No need to do any cleanup.
                if (bitmap != null && !bitmap.isRecycled()) {
                    //you can use bitmap here
                    setIconByCluster(Urls.Endpoint3 + person.getFile_url() + "?x-oss-process=image/resize,l_100", cluster,
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
                }
                cancleMap1.remove(cluster);
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {
                // No cleanup required here.
                System.out.println("shibai");
            }

        }, UiThreadImmediateExecutorService.getInstance());


        cancleMap1.put(cluster, dataSource);

    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        // Always render clusters.
        return cluster.getSize() > 1;
    }

    @Override
    protected void onRemoveCluster(Cluster cluster) {
        DataSource<CloseableReference<CloseableImage>> target = cancleMap1.get(cluster);
        if (target != null) {
            target.close();
            cancleMap1.remove(cluster);
        }
    }
}