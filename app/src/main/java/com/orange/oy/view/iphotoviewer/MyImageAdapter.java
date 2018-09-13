package com.orange.oy.view.iphotoviewer;

import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;


import com.bumptech.glide.Glide;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.view.photoview.PhotoView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/3/28.
 */

public class MyImageAdapter extends PagerAdapter {
    public static final String TAG = MyImageAdapter.class.getSimpleName();
    private ArrayList<String> imageUrls;
    private BaseActivity activity;

    public MyImageAdapter(ArrayList<String> imageUrls, PhotoViewActivity activity) {
        this.imageUrls = imageUrls;
        this.activity = activity;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        String url = imageUrls.get(position);
        PhotoView photoView = new PhotoView(activity);
        Glide.with(activity)
                .load(url)
                .into(photoView);
        container.addView(photoView);
       /* photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tools.d(TAG, "onClick: ");
                activity.finish();
            }
        });*/
        return photoView;
    }

    @Override
    public int getCount() {
        return imageUrls != null ? imageUrls.size() : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
