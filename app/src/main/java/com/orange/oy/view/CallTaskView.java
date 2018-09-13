package com.orange.oy.view;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.allinterface.FinishTaskProgressRefresh;
import com.orange.oy.allinterface.RecordPlayForFinishTask;
import com.orange.oy.base.BaseView;
import com.orange.oy.base.Tools;
import com.orange.oy.info.TaskFinishInfo;
import com.orange.oy.reord.AudioManager;

import java.io.IOException;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by xiedongyan on 2017/4/20.
 * 电话任务查看详情页
 */
public class CallTaskView extends LinearLayout implements View.OnClickListener, RecordPlayForFinishTask, BaseView, FinishTaskProgressRefresh, RecodePlayView.OnRecodePlayerListener {

    private TextView name;
    private ImageView right;
    private TaskFinishInfo taskFinishInfo;
    private LinearLayout viewfdt_call_layout2;
    private View viewfdt_call_layout3;
    private String wuxiao;
    private Context context;
    private TextView viewfdt_call_note;
    private ProgressBar viewfdt_call_rate;
    private TextView viewfdt_call_state;
    private RecodePlayView viewfdt_call_recodeplay;


    public CallTaskView(Context context, TaskFinishInfo taskFinishInfo, boolean isAgain) {
        super(context);
        this.context = context;
        Tools.loadLayout(this, R.layout.view_finishdt_call);
        init(isAgain);
        this.taskFinishInfo = taskFinishInfo;
    }

    private void init(boolean isAgain) {
        viewfdt_call_recodeplay = (RecodePlayView) findViewById(R.id.viewfdt_call_recodeplay);
        viewfdt_call_state = (TextView) findViewById(R.id.viewfdt_call_state);
        viewfdt_call_rate = (ProgressBar) findViewById(R.id.viewfdt_call_rate);
        viewfdt_call_note = (TextView) findViewById(R.id.viewfdt_call_note);
        name = (TextView) findViewById(R.id.viewfdt_call_name);
        right = (ImageView) findViewById(R.id.viewfdt_call_right);
        viewfdt_call_layout2 = (LinearLayout) findViewById(R.id.viewfdt_call_layout2);
        viewfdt_call_layout3 = findViewById(R.id.viewfdt_call_layout3);
        findViewById(R.id.viewfdt_call_layout).setOnClickListener(this);
        if (isAgain) {
            findViewById(R.id.viewfdt_call_reset).setOnClickListener(this);
        } else {
            findViewById(R.id.viewfdt_call_reset).setVisibility(View.GONE);
        }
    }


    public void setIsProgress(boolean isshow) {
        if (isshow) {
            viewfdt_call_rate.setVisibility(VISIBLE);
            viewfdt_call_state.setVisibility(VISIBLE);
            findViewById(R.id.viewfdt_call_layout).setOnClickListener(null);
        }
    }
//
//    public void setData(String task_name, String storename, String telphone, String time, String calltime) {
//        name.setText(task_name);
////        ((TextView) findViewById(R.id.viewfdt_call_name2)).setText("网点名称：" + storename);
////        ((TextView) findViewById(R.id.viewfdt_call_num)).setText("电话号码：" + telphone);
////        ((TextView) findViewById(R.id.viewfdt_call_time)).setText("拨号时间：" + time);
////        ((TextView) findViewById(R.id.viewfdt_call_calltime)).setText("拨打时长：" + calltime + "s");
//    }

    public void setData2(String task_name, String wuxiao, String note) {
        name.setText(task_name);
        this.wuxiao = wuxiao;
        viewfdt_call_note.setText(note);
        viewfdt_call_layout2.setVisibility(GONE);
    }

//    public void setHandler(Handler handler) {
//        this.handler = handler;
//    }

    /**
     * 设置音频路径
     *
     * @param src
     */
    public void settingRecSrc(String src) {
        viewfdt_call_recodeplay.settingREC(src);
    }

    public void startPlaying() {
        viewfdt_call_recodeplay.startPlaying();
    }

    //停止播放
    public void stopPlaying() {
        viewfdt_call_recodeplay.stopPlaying();
    }

    public int getCurrentPosition() {
        return viewfdt_call_recodeplay.getCurrentPosition();
    }

    public int getDuration() {
        return viewfdt_call_recodeplay.getDuration();
    }

    public void seekTo(int seek) {
        viewfdt_call_recodeplay.seekTo((int) (getDuration() * (seek / 100d)));
    }

    public void setProgressFroRecord(int progress) {
        viewfdt_call_recodeplay.setProgressFroRecord(progress);
    }

    @Override
    public void setProgress(int progress) {
        viewfdt_call_rate.setProgress(progress);
        if (progress < 100) {
            viewfdt_call_state.setText(progress + "%");
        } else {
            viewfdt_call_rate.setVisibility(GONE);
            viewfdt_call_rate.setVisibility(GONE);
        }
    }

    @Override
    public Object getInfo() {
        return taskFinishInfo;
    }

    public boolean isPlaying() {
        return viewfdt_call_recodeplay.isPlaying();
    }

    @Override
    public void setTime(String time) {
    }

    @Override
    public void onFinishView() {
        stopPlaying();
    }

    private static String stringForTime(int timeMs) {
        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    @Override
    public void onResume(Object object) {

    }

    @Override
    public void onPause(Object object) {
        stopPlaying();
    }

    @Override
    public void onStop(Object object) {
        stopPlaying();
    }

    @Override
    public void onDestory(Object object) {
        onFinishView();
    }

    @Override
    public Object getBaseData() {
        return taskFinishInfo;
    }

    @Override
    public void play(RecodePlayView recodePlayView) {
        RecodePlayView.closeAllRecodeplay(viewfdt_call_recodeplay.hashCode());
        AudioManager.stopPlaying();
        MyVideoView.closeAllMyVideoView();
    }

    @Override
    public void stop(RecodePlayView recodePlayView) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.viewfdt_call_layout: {
                if ("1".equals(wuxiao)) {
                    if (viewfdt_call_layout3.getVisibility() == VISIBLE) {
                        viewfdt_call_layout3.setVisibility(GONE);
                        right.setImageResource(R.mipmap.text_spread);
                    } else {
                        viewfdt_call_layout3.setVisibility(VISIBLE);
                        right.setImageResource(R.mipmap.text_shrinkup);
                    }
                } else {
                    if (viewfdt_call_layout2.getVisibility() == View.VISIBLE) {
                        viewfdt_call_layout2.setVisibility(View.GONE);
                        right.setImageResource(R.mipmap.text_spread);
                    } else if (viewfdt_call_layout2.getVisibility() == View.GONE) {
                        viewfdt_call_layout2.setVisibility(View.VISIBLE);
                        right.setImageResource(R.mipmap.text_shrinkup);
                    }
                }
            }
            break;
        }
    }
}
