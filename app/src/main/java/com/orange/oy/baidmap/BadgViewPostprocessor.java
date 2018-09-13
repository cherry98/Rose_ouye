package com.orange.oy.baidmap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;

import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orange.oy.R;
import com.orange.oy.clusterutil.clustering.Cluster;
import com.orange.oy.info.shakephoto.ShakePhotoInfo;

import java.lang.ref.WeakReference;

import q.rorbin.badgeview.QBadgeView;

/**
 * Created by jiong103 on 2017/5/9.
 */

public class BadgViewPostprocessor extends BasePostprocessor {


    private WeakReference<Activity> mActivity;
    Cluster<ShakePhotoInfo> cluster;

    public BadgViewPostprocessor(Activity context, Cluster<ShakePhotoInfo> cluster) {
        mActivity = new WeakReference<Activity>(context);
        this.cluster = cluster;
    }


    @Override
    public CloseableReference<Bitmap> process(
            Bitmap sourceBitmap,
            PlatformBitmapFactory bitmapFactory) {

        Activity context = mActivity.get();
        if (context == null) {
            return super.process(sourceBitmap, bitmapFactory);
        }

        long time = System.currentTimeMillis();
        IconGenerator mClusterIconGenerator = new IconGenerator(context.getApplicationContext());
        View multiProfile = context.getLayoutInflater().inflate(R.layout.multi_profile, null, false);
        mClusterIconGenerator.setContentView(multiProfile);
        RoundedImageView mClusterImageView = (RoundedImageView) multiProfile.findViewById(R.id.image);

        if (cluster.getSize() > 1) {
            String numberText = cluster.getSize() > 999 ? "999+" : String.valueOf(cluster.getSize());
            new QBadgeView(context)
                    .bindTarget(mClusterImageView)
                    .setBadgeText(numberText)
                    .setShowShadow(true)
                    .setBadgeTextSize(8, true)
                    .setBadgeTextColor(Color.WHITE)
                    .setBadgeGravity(Gravity.TOP | Gravity.END)
                    .setBadgePadding(4.f, true)
                    .setBadgeBackgroundColor(0xffF65D57);
        }
        mClusterImageView.setImageBitmap(sourceBitmap);
        Bitmap ret = mClusterIconGenerator.makeIcon();

        CloseableReference<Bitmap> bitmapRef = bitmapFactory.createBitmap(
                ret.getWidth(),
                ret.getHeight());
        try {
            Bitmap destBitmap = bitmapRef.get();

            destBitmap.eraseColor(android.graphics.Color.TRANSPARENT);

            Canvas canvas = new Canvas(destBitmap);
            canvas.drawBitmap(ret, 0, 0, null);
            ret.recycle();
            return CloseableReference.cloneOrNull(bitmapRef);
        } finally {
            CloseableReference.closeSafely(bitmapRef);
        }
    }


    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public CacheKey getPostprocessorCacheKey() {
        return new SimpleCacheKey("cluster.size=" + cluster.getSize());
    }

}
