package com.orange.oy.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.activity.TaskitemPhotographyActivity;
import com.orange.oy.activity.TaskitemShotillustrateActivity;
import com.orange.oy.activity.VideoViewActivity;
import com.orange.oy.allinterface.FinishTaskProgressRefresh;
import com.orange.oy.base.BaseView;
import com.orange.oy.base.Tools;
import com.orange.oy.info.TaskFinishInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.reord.AudioManager;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


public class FinishshotView extends LinearLayout implements View.OnClickListener, FinishTaskProgressRefresh, BaseView, MyVideoView.OnMyVideoViewListener {
    private boolean isPhoto;
    private ProgressBar viewfdt_shot_progress;

    public FinishshotView(Context context, TaskFinishInfo taskFinishInfo, boolean isPhoto, boolean isAgain) {
        super(context);
        Tools.loadLayout(this, R.layout.view_finishdt_shot);
        init();
        this.taskFinishInfo = taskFinishInfo;
        if (isAgain)
            findViewById(R.id.viewfdt_shot_reset).setOnClickListener(this);
        else
            findViewById(R.id.viewfdt_shot_reset).setVisibility(GONE);
        this.isPhoto = isPhoto;
    }

    public FinishshotView(Context context, boolean isAgain) {
        super(context);
        Tools.loadLayout(this, R.layout.view_finishdt_shot);
        init();
        findViewById(R.id.viewfdt_shot_reset).setVisibility(View.GONE);
    }

    public void showOnlyEdit() {
        ((ImageView) findViewById(R.id.viewfdt_shot_ico)).setImageResource(R.mipmap.task_ico4);
    }

    private TaskFinishInfo taskFinishInfo;
    private TextView name, viewfdt_shot_note;
    private ImageView right;
    private View value, viewfdt_shot_note_layout;
    private ImageView viewfdt_shot_video1, viewfdt_shot_video2, viewfdt_shot_video3, viewfdt_shot_video4;
    private int length;
    private MyVideoView myVideoView;
    private TextView viewfdt_shot_progressvalue;
    private RecodePlayView.OnRecodePlayerListener onRecodePlayerListener;

    public void setOnRecodePlayerListener(RecodePlayView.OnRecodePlayerListener onRecodePlayerListener) {
        this.onRecodePlayerListener = onRecodePlayerListener;
    }

    private void init() {
        viewfdt_shot_progressvalue = (TextView) findViewById(R.id.viewfdt_shot_progressvalue);
        viewfdt_shot_progress = (ProgressBar) findViewById(R.id.viewfdt_shot_progress);
        viewfdt_shot_note_layout = findViewById(R.id.viewfdt_shot_note_layout);
        myVideoView = (MyVideoView) findViewById(R.id.viewfdt_shot_videoview);
        myVideoView.setOnMyVideoViewListener(this);
        MyMediaController myMediaController = (MyMediaController) findViewById(R.id.viewfdt_shot_mediacontroller);
        myVideoView.setMediaController(myMediaController);
        name = (TextView) findViewById(R.id.viewfdt_shot_name);
        value = findViewById(R.id.viewfdt_shot_value);
        right = (ImageView) findViewById(R.id.viewfdt_shot_right);
        viewfdt_shot_note = (TextView) findViewById(R.id.viewfdt_shot_note);
        viewfdt_shot_video1 = (ImageView) findViewById(R.id.viewfdt_shot_video1);
        viewfdt_shot_video2 = (ImageView) findViewById(R.id.viewfdt_shot_video2);
        viewfdt_shot_video3 = (ImageView) findViewById(R.id.viewfdt_shot_video3);
        viewfdt_shot_video4 = (ImageView) findViewById(R.id.viewfdt_shot_video4);
        value.setVisibility(View.GONE);
        viewfdt_shot_note_layout.setVisibility(GONE);
        viewfdt_shot_video4.setVisibility(View.GONE);
        myVideoView.setVisibility(View.GONE);
        findViewById(R.id.viewfdt_shot_layout).setOnClickListener(this);
//        onClick(right);
    }

    private boolean isProgress;//是否展示进度条，如果展示进度条将不会展开详情

    public void setIsProgress(boolean isProgress) {
        this.isProgress = isProgress;
        if (isProgress) {
            viewfdt_shot_progress.setVisibility(VISIBLE);
            findViewById(R.id.viewfdt_shot_layout).setOnClickListener(null);
            viewfdt_shot_progressvalue.setVisibility(VISIBLE);
        }
    }

    public void settingValue(String name, String value) {
        this.name.setText(name);
    }

    public void settingNote(String note) {
        if (TextUtils.isEmpty(note) || "null".equals(note)) {
            note = "";
        }
        try {
            viewfdt_shot_note.setText(URLDecoder.decode(note, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void settingName(String name) {
        this.name.setText(name);
    }

    public void settingValue(String name, String[] shots) {
        this.name.setText(name);
        length = shots.length;
        if (shots.length == 1 && !TextUtils.isEmpty(shots[0])) {
            viewfdt_shot_video4.setTag(shots[0].replaceAll("\\\\", ""));
            File f = new File(shots[0]);
            if (f.isFile()) {
                viewfdt_shot_video4.setImageBitmap(Tools.createVideoThumbnail(shots[0]));
            } else {
                viewfdt_shot_video4.setImageResource(R.mipmap.bof);
                new getVideoThumbnail(4).execute(new Object[]{shots[0]});
            }
            viewfdt_shot_video4.setOnClickListener(this);
        }
        if (shots.length > 1 && !TextUtils.isEmpty(shots[0])) {
            viewfdt_shot_video1.setVisibility(View.VISIBLE);
            viewfdt_shot_video1.setTag(shots[0].replaceAll("\\\\", ""));
            File f = new File(shots[0]);
            if (f.isFile()) {
                viewfdt_shot_video1.setImageBitmap(Tools.createVideoThumbnail(shots[0]));
            } else {
                viewfdt_shot_video1.setImageResource(R.mipmap.bof);
                new getVideoThumbnail(1).execute(new Object[]{shots[0]});
//                viewfdt_shot_video1.setTag(Urls.ImgIp + shots[0]);
            }
            viewfdt_shot_video1.setOnClickListener(this);
        }
        if (shots.length >= 2 && !TextUtils.isEmpty(shots[1])) {
            viewfdt_shot_video2.setVisibility(View.VISIBLE);
            viewfdt_shot_video2.setTag(shots[1]);
            File f = new File(shots[1].replaceAll("\\\\", ""));
            if (f.isFile()) {
                viewfdt_shot_video2.setImageBitmap(Tools.createVideoThumbnail(shots[1]));
            } else {
                viewfdt_shot_video2.setImageResource(R.mipmap.bof);
                new getVideoThumbnail(2).execute(new Object[]{shots[1]});
//                viewfdt_shot_video2.setTag(Urls.ImgIp + shots[1]);
            }
            viewfdt_shot_video2.setOnClickListener(this);
        }
        if (shots.length >= 3 && !TextUtils.isEmpty(shots[2])) {
            viewfdt_shot_video3.setVisibility(View.VISIBLE);
            viewfdt_shot_video3.setTag(shots[2]);
            File f = new File(shots[2].replaceAll("\\\\", ""));
            if (f.isFile()) {
                viewfdt_shot_video3.setImageBitmap(Tools.createVideoThumbnail(shots[2]));
            } else {
                viewfdt_shot_video3.setImageResource(R.mipmap.bof);
                new getVideoThumbnail(3).execute(new Object[]{shots[2]});
//                viewfdt_shot_video3.setTag(Urls.ImgIp + shots[2]);
            }
            viewfdt_shot_video3.setOnClickListener(this);
        }
    }

    @Override
    public void setProgress(int progress) {
        if (isProgress) {
            viewfdt_shot_progress.setProgress(progress);
            if (progress < 100) {
                viewfdt_shot_progressvalue.setText(progress + "%");
            } else {
                viewfdt_shot_progressvalue.setVisibility(GONE);
                viewfdt_shot_progress.setVisibility(GONE);
            }
        }
    }

    @Override
    public Object getInfo() {
        return taskFinishInfo;
    }

    @Override
    public void play(MyVideoView myVideoView) {

    }

    @Override
    public void stop(MyVideoView myVideoView) {
        viewfdt_shot_video4.setVisibility(View.GONE);
        myVideoView.setVisibility(View.GONE);
        viewfdt_shot_note_layout.setVisibility(View.GONE);
        right.setImageResource(R.mipmap.text_spread);
    }

    private class getVideoThumbnail extends AsyncTask {
        private int index;

        getVideoThumbnail(int index) {
            this.index = index;
        }

        protected Object doInBackground(Object[] params) {
            String url = null;
            try {
                url = URLDecoder.decode(params[0].toString(), "utf-8");
                return Tools.createVideoThumbnail(url, 400, 300);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(Object o) {
            if (o != null) {
                switch (index) {
                    case 1: {
                        if (viewfdt_shot_video1 != null)
                            viewfdt_shot_video1.setImageBitmap((Bitmap) o);
                    }
                    break;
                    case 2: {
                        if (viewfdt_shot_video2 != null)
                            viewfdt_shot_video2.setImageBitmap((Bitmap) o);
                    }
                    break;
                    case 3: {
                        if (viewfdt_shot_video3 != null)
                            viewfdt_shot_video3.setImageBitmap((Bitmap) o);
                    }
                    case 4: {
                        if (viewfdt_shot_video4 != null)
                            viewfdt_shot_video4.setImageBitmap((Bitmap) o);
                    }
                    break;
                }
            }
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.viewfdt_shot_layout: {
                if (length > 1) {//如果视频大于一段
                    if (value.getVisibility() == View.VISIBLE) {
                        value.setVisibility(View.GONE);
                        viewfdt_shot_note_layout.setVisibility(View.GONE);
                        right.setImageResource(R.mipmap.text_spread);
                    } else {
                        value.setVisibility(View.VISIBLE);
                        if (!TextUtils.isEmpty(viewfdt_shot_note.getText())) {
                            viewfdt_shot_note_layout.setVisibility(View.VISIBLE);
                        }
                        right.setImageResource(R.mipmap.text_shrinkup);
                    }
                } else if (length == 1) {//如果视频只有一段
                    if (myVideoView.getVisibility() == View.VISIBLE || viewfdt_shot_video4.getVisibility() == VISIBLE) {
                        viewfdt_shot_video4.setVisibility(View.GONE);
                        myVideoView.setVisibility(View.GONE);
                        myVideoView.stopPlayback();
                        viewfdt_shot_note_layout.setVisibility(View.GONE);
                        right.setImageResource(R.mipmap.text_spread);
                    } else {
                        viewfdt_shot_video4.setVisibility(View.VISIBLE);
                        myVideoView.setVisibility(View.GONE);
                        if (!TextUtils.isEmpty(viewfdt_shot_note.getText())) {
                            viewfdt_shot_note_layout.setVisibility(View.VISIBLE);
                        }
                        right.setImageResource(R.mipmap.text_shrinkup);
                    }
                }
            }
            break;
            case R.id.viewfdt_shot_video1: {
                Intent intent = new Intent(getContext(), VideoViewActivity.class);
                intent.putExtra("path", viewfdt_shot_video1.getTag().toString());
                getContext().startActivity(intent);
            }
            break;
            case R.id.viewfdt_shot_video2: {
                Intent intent = new Intent(getContext(), VideoViewActivity.class);
                intent.putExtra("path", viewfdt_shot_video2.getTag().toString());
                getContext().startActivity(intent);
            }
            break;
            case R.id.viewfdt_shot_video3: {
                Intent intent = new Intent(getContext(), VideoViewActivity.class);
                intent.putExtra("path", viewfdt_shot_video3.getTag().toString());
                getContext().startActivity(intent);
            }
            break;
            case R.id.viewfdt_shot_video4: {
                if (myVideoView == null) return;
                String path = viewfdt_shot_video4.getTag().toString();
                try {
                    path = URLDecoder.decode(path, "utf-8");
                    File f = new File(path);
                    if (!f.exists() || !f.isFile()) {
                        if (!path.startsWith("http://"))
                            path = Urls.VideoIp + path;
                    }
                    myVideoView.setVisibility(VISIBLE);
                    myVideoView.setVideoURI(Uri.parse(path));
                    myVideoView.requestFocus();
                    myVideoView.start();
                    RecodePlayView.closeAllRecodeplay();
                    AudioManager.stopPlaying();
                    if (onRecodePlayerListener != null) {
                        onRecodePlayerListener.play(null);
                    }
                    viewfdt_shot_video4.setVisibility(GONE);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Tools.d(path);
                    Tools.showToast(getContext(), "视频地址异常");
                }
//                Intent intent = new Intent(getContext(), VideoViewActivity.class);
//                intent.putExtra("path", viewfdt_shot_video4.getTag().toString());
//                getContext().startActivity(intent);
            }
            break;
            case R.id.viewfdt_shot_reset: {
                if (isPhoto) {
                    Intent intent = new Intent(getContext(), TaskitemPhotographyActivity.class);
                    intent.putExtra("task_pack_id", taskFinishInfo.getPid());
                    intent.putExtra("task_id", taskFinishInfo.getTaskid());
                    intent.putExtra("task_name", taskFinishInfo.getName());
                    intent.putExtra("task_type", "1");
                    intent.putExtra("store_id", taskFinishInfo.getStoreid());
                    intent.putExtra("photo_compression", taskFinishInfo.getCompression());
                    intent.putExtra("category1", taskFinishInfo.getCategory1());
                    intent.putExtra("category2", taskFinishInfo.getCategory2());
                    intent.putExtra("category3", taskFinishInfo.getCategory3());
                    intent.putExtra("project_id", taskFinishInfo.getProjectid());
                    intent.putExtra("project_name", taskFinishInfo.getProjectname());
                    intent.putExtra("task_pack_name", taskFinishInfo.getPackage_name());
                    intent.putExtra("store_num", taskFinishInfo.getStorenum());
                    intent.putExtra("store_name", taskFinishInfo.getStorename());
                    intent.putExtra("outlet_batch", taskFinishInfo.getOutlet_batch());
                    intent.putExtra("p_batch", taskFinishInfo.getP_batch());
                    getContext().startActivity(intent);
                } else {
                    Intent intent = new Intent(getContext(), TaskitemShotillustrateActivity.class);
                    intent.putExtra("task_pack_id", taskFinishInfo.getPid());
                    intent.putExtra("task_id", taskFinishInfo.getTaskid());
                    intent.putExtra("store_id", taskFinishInfo.getStoreid());
                    intent.putExtra("category1", taskFinishInfo.getCategory1());
                    intent.putExtra("category2", taskFinishInfo.getCategory2());
                    intent.putExtra("category3", taskFinishInfo.getCategory3());
                    intent.putExtra("project_id", taskFinishInfo.getProjectid());
                    intent.putExtra("project_name", taskFinishInfo.getProjectname());
                    intent.putExtra("task_pack_name", taskFinishInfo.getPackage_name());
                    intent.putExtra("task_name", taskFinishInfo.getName());
                    intent.putExtra("store_num", taskFinishInfo.getStorenum());
                    intent.putExtra("store_name", taskFinishInfo.getStorename());
                    intent.putExtra("outlet_batch", taskFinishInfo.getOutlet_batch());
                    intent.putExtra("p_batch", taskFinishInfo.getP_batch());
                    getContext().startActivity(intent);
                }
            }
            break;
        }
    }

    @Override
    public void onResume(Object object) {
        if (myVideoView != null) {
            myVideoView.resume();
        }
    }

    @Override
    public void onPause(Object object) {
        if (myVideoView != null) {
            myVideoView.pause();
        }
    }

    @Override
    public void onStop(Object object) {
        if (myVideoView != null) {
            myVideoView.stopPlayback();
        }
    }

    @Override
    public void onDestory(Object object) {
        if (myVideoView != null) {
            myVideoView.destroyDrawingCache();
        }
    }

    @Override
    public Object getBaseData() {
        return taskFinishInfo;
    }

}
