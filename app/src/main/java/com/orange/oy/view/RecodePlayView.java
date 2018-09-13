package com.orange.oy.view;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.allinterface.RecordPlayForFinishTask;
import com.orange.oy.base.Tools;

import java.io.IOException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Administrator on 2018/4/2.
 */
public class RecodePlayView extends LinearLayout implements RecordPlayForFinishTask, View.OnClickListener {
    public interface OnRecodePlayerListener {
        void play(RecodePlayView recodePlayView);

        void stop(RecodePlayView recodePlayView);
    }

    /**
     * @param hashcode 特例的hashcode
     */
    public static void closeAllRecodeplay(int hashcode) {
        if (recodePlayViewHashMap == null || recodePlayViewHashMap.size() == 0) {
            return;
        }
        int size = recodePlayViewHashMap.size();
        for (int i = 0; i < size; i++) {
            int key = recodePlayViewHashMap.keyAt(i);
            if (key == hashcode) continue;
            if (recodePlayViewHashMap.get(key) == null) {
                recodePlayViewHashMap.remove(key);
                size--;
                i--;
                continue;
            }
            recodePlayViewHashMap.get(key).stopPlaying();
        }
    }

    public static void closeAllRecodeplay() {
        if (recodePlayViewHashMap == null || recodePlayViewHashMap.size() == 0) {
            return;
        }
        int size = recodePlayViewHashMap.size();
        for (int i = 0; i < size; i++) {
            int key = recodePlayViewHashMap.keyAt(i);
            if (recodePlayViewHashMap.get(key) == null) {
                recodePlayViewHashMap.remove(key);
                size--;
                i--;
                continue;
            }
            recodePlayViewHashMap.get(key).stopPlaying();
        }
    }

    public static void clearRecodePlayViewMap() {
        recodePlayViewHashMap.clear();
    }

    private static SparseArray<RecodePlayView> recodePlayViewHashMap = new SparseArray<>();
    private static final int MESSAGEWHAT = 1;
    private MyHandler handler;

    private ImageView play;
    private ProgressBar seekbar;
    private TextView time;
    private MediaPlayer mPlayer = null;
    private String recordSrc;
    private OnRecodePlayerListener onRecodePlayerListener;

    public void setOnRecodePlayerListener(OnRecodePlayerListener onRecodePlayerListener) {
        this.onRecodePlayerListener = onRecodePlayerListener;
    }

    public RecodePlayView(Context context) {
        this(context, R.layout.view_recodeplay);
    }

    public RecodePlayView(Context context, int layoutResourceId) {
        super(context);
        Tools.loadLayout(this, layoutResourceId);
        init();
    }

    public RecodePlayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecodePlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Tools.loadLayout(this, R.layout.view_recodeplay);
        init();
    }

    private void init() {
        handler = new MyHandler();
        play = (ImageView) findViewById(R.id.viewrec_play_play);
        seekbar = (ProgressBar) findViewById(R.id.viewrec_play_progress);
        time = (TextView) findViewById(R.id.viewrec_play_current);
        play.setOnClickListener(this);
        recodePlayViewHashMap.put(hashCode(), this);
    }

    public void settingREC(String recordSrc) {
        this.recordSrc = recordSrc;
    }

    public void startPlaying() {
        if (TextUtils.isEmpty(recordSrc)) return;
        if (handler == null) return;
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(recordSrc);
            mPlayer.prepare();
            mPlayer.start();
            Message message = handler.obtainMessage();
            message.obj = this;
            message.what = MESSAGEWHAT;
            handler.sendMessage(message);
            play.setImageResource(R.mipmap.ic_media_pause);
            if (onRecodePlayerListener != null) {
                onRecodePlayerListener.play(this);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Tools.showToast(getContext(), "播放失败");
        }
    }

    //停止播放
    public void stopPlaying() {
        play.setImageResource(R.mipmap.lyrw_button_kaishi);
        if (mPlayer == null) return;
        try {
            if (mPlayer.isPlaying())
                mPlayer.stop();
            mPlayer.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        seekbar.setProgress(0);
        mPlayer = null;
        if (onRecodePlayerListener != null) {
            onRecodePlayerListener.stop(this);
        }
    }

    public int getCurrentPosition() {
        if (mPlayer == null) return 0;
        return mPlayer.getCurrentPosition();
    }

    public int getDuration() {
        if (mPlayer == null) return 0;
        return mPlayer.getDuration();
    }

    public void seekTo(int seek) {
        if (mPlayer == null) return;
        mPlayer.seekTo((int) (getDuration() * (seek / 100d)));
    }

    public void setProgressFroRecord(int progress) {
        if (mPlayer == null) return;
        seekbar.setProgress(progress);
    }

    public boolean isPlaying() {
        return mPlayer != null && mPlayer.isPlaying();
    }

    @Override
    public void setTime(String time) {
        this.time.setText(time);
    }

    @Override
    public void onFinishView() {
        if (handler != null) {
            handler.removeMessages(MESSAGEWHAT);
        }
        stopPlaying();
        if (recodePlayViewHashMap != null) {
            int size = recodePlayViewHashMap.size();
            for (int i = 0; i < size; i++) {
                int key = recodePlayViewHashMap.keyAt(i);
                if (hashCode() == key) {
                    recodePlayViewHashMap.remove(key);
                }
            }
        }
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

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.viewrec_play_play: {
                if (isPlaying()) {
                    stopPlaying();
                } else {
                    startPlaying();
                }
            }
            break;
        }
    }

    private static class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGEWHAT: {
                    RecordPlayForFinishTask callTaskView = (RecordPlayForFinishTask) msg.obj;
                    int dur = callTaskView.getDuration();
                    int pro = callTaskView.getCurrentPosition();
                    if (callTaskView.isPlaying()) {
                        callTaskView.setProgressFroRecord(100 * pro / dur);
                        callTaskView.setTime(stringForTime(pro));
                        Message message = obtainMessage();
                        message.obj = callTaskView;
                        message.what = MESSAGEWHAT;
                        sendMessageDelayed(message, 1000);
                    } else {
                        callTaskView.setTime("");
                        callTaskView.setProgressFroRecord(0);
                        callTaskView.stopPlaying();
                    }
                }
                break;
            }
        }
    }
}
