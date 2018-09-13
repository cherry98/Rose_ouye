package com.orange.oy.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.orange.oy.R;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.network.Urls;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

//import io.vov.vitamio.MediaPlayer;
//import io.vov.vitamio.Vitamio;
//import io.vov.vitamio.widget.MediaController;
//import io.vov.vitamio.widget.VideoView;

/**
 * 播放视频
 */
public class VideoViewActivity extends BaseActivity {
    VideoView mVideoView;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.videoview);
        Intent data = getIntent();
        String path;
        if (data == null) return;
        path = data.getStringExtra("path");
        try {
            path = URLDecoder.decode(path, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Tools.d(path);
            path = "";
        }
        Tools.d(path);
//        path = "http://182.92.73.31:8899/D7F29EDC7255C7602036894B7B719036.mp4";
//        if (path.startsWith("http")) {
//            webView = (WebView) findViewById(R.id.webView);
//            webView.loadUrl(path);
//        } else {
        if (TextUtils.isEmpty(path)) {
            Tools.showToast(this, "播放路径异常！");
            baseFinish();
        } else {
            File f = new File(path);
            if (!f.exists() || !f.isFile()) {
                if (!path.startsWith("http://"))
                    path = Urls.VideoIp + path;
            }
//        Vitamio.isInitialized(this);
            mVideoView = (VideoView) findViewById(R.id.surface_view);
            mVideoView.setVisibility(View.VISIBLE);
//        path = FileCache.getDirForVideo(this, "0120121").getPath() + "/1.mp4";
            mVideoView.setMediaController(new MediaController(this));
//        mVideoView.setVideoPath(path);
            mVideoView.setVideoURI(Uri.parse(path));
            mVideoView.requestFocus();
            mVideoView.start();
//        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            public void onPrepared(MediaPlayer mediaPlayer) {
//                mediaPlayer.setPlaybackSpeed(1.0f);
//            }
//        });
//        mVideoView.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
//            public void onBufferingUpdate(MediaPlayer mp, int percent) {
//            }
//        });
//        }
        }
    }

    protected void onPause() {
        super.onPause();
        if (mVideoView != null) {
            mVideoView.pause();
        }
    }

    protected void onResume() {
        super.onResume();
        if (mVideoView != null) {
            mVideoView.resume();
        }
    }

    protected void onStop() {
        super.onStop();
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (mVideoView != null) {
            mVideoView.destroyDrawingCache();
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
