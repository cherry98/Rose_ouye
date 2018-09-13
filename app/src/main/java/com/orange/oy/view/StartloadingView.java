package com.orange.oy.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.orange.oy.R;
import com.orange.oy.activity.BrowserActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;

/**
 * Created by Administrator on 2018/6/27.
 * 广告页
 */

public class StartloadingView extends LinearLayout {
    public interface OnStartloadingListener {
        void onStartloadingEnd();
    }

    private OnStartloadingListener onStartloadingListener;

    public void setOnStartloadingListener(OnStartloadingListener onStartloadingListener) {
        this.onStartloadingListener = onStartloadingListener;
    }

    public StartloadingView(Context context) {
        this(context, null, 0);
    }

    public StartloadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StartloadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Tools.loadLayout(this, R.layout.view_start_loading);
        initView();
    }

    private ImageView startloading_img;
    private ImageLoader imageLoader;
    private String link_url;

    private void initView() {
        imageLoader = new ImageLoader(getContext());
        startloading_img = (ImageView) findViewById(R.id.startloading_img);
    }

    public void startLoad(String photo_url, String link_url1) {
        this.link_url = link_url1;
        imageLoader.DisplayImage(Urls.ImgIp + photo_url, startloading_img, -1);
        startloading_img.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (!TextUtils.isEmpty(link_url) && !"null".equals(link_url)) {
                    Intent intent = new Intent(getContext(), BrowserActivity.class);
                    intent.putExtra("content", link_url);
                    intent.putExtra("flag", "7");
                    getContext().startActivity(intent);
                }
            }
        });
        findViewById(R.id.enter_layout).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (onStartloadingListener != null) {
                    onStartloadingListener.onStartloadingEnd();
                }
            }
        });
    }
}
