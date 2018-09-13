package com.orange.oy.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.orange.oy.R;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;

/**
 * app广告页面
 */
public class StartLoadingActivity extends BaseActivity implements View.OnClickListener {

    private String link_url;
    private boolean isClick = false;
    private int time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_loading);
        ImageLoader imageLoader = new ImageLoader(this);
        ImageView startloading_img = (ImageView) findViewById(R.id.startloading_img);
        String photo_url = getIntent().getStringExtra("photo_url");
        link_url = getIntent().getStringExtra("link_url");
        imageLoader.DisplayImage(Urls.ImgIp + photo_url, startloading_img, -1);
        time = getIntent().getIntExtra("time", 0);
        findViewById(R.id.startloading_img).setOnClickListener(this);
        findViewById(R.id.enter_layout).setOnClickListener(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isClick) {
                    startActivity(new Intent(StartLoadingActivity.this, MainActivity.class));
                    baseFinish();
                }
            }
        }, time * 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isClick) {
            startActivity(new Intent(StartLoadingActivity.this, MainActivity.class));
            baseFinish();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.enter_layout) {
            isClick = true;
            startActivity(new Intent(StartLoadingActivity.this, MainActivity.class));
        } else if (v.getId() == R.id.startloading_img) {
            if (!TextUtils.isEmpty(link_url) && !"null".equals(link_url)) {
                isClick = true;
                Intent intent = new Intent(this, BrowserActivity.class);
                intent.putExtra("content", link_url);
                intent.putExtra("flag", "7");
                startActivity(intent);
            }
        }
    }
}
