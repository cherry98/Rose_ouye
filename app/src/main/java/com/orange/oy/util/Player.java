package com.orange.oy.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * V3.1.1 播放MP3
 */
public class Player implements OnBufferingUpdateListener, OnCompletionListener,
        OnPreparedListener {

    public MediaPlayer mediaPlayer; // 媒体播放器
    private SeekBar seekBar; // 拖动条
    private TextView musicTime; // 时间显示
    private Timer mTimer = new Timer(); // 计时器
    private Context context;

    private SimpleDateFormat time = new SimpleDateFormat("m:ss");

    // 初始化播放器
    public Player(SeekBar seekBar, TextView musicTime) {
        super();
        this.seekBar = seekBar;
        this.musicTime = musicTime;
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型
//            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnPreparedListener(this);


        } catch (Exception e) {
            e.printStackTrace();
        }
        // 每一秒触发一次
        mTimer.schedule(timerTask, 0, 1000);
    }

    // 计时器
    TimerTask timerTask = new TimerTask() {

        @Override
        public void run() {
            if (mediaPlayer == null)
                return;
            if (mediaPlayer.isPlaying() && seekBar.isPressed() == false) {
                handler.sendEmptyMessage(0); // 发送消息
            }
        }

    };

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int position = mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();
            if (duration > 0) {
                // 计算进度（获取进度条最大刻度*当前音乐播放位置 / 当前音乐时长）
                long pos = seekBar.getMax() * position / duration;
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                /***
                 * 播放的时间显示
                 */
                musicTime.setText(time.format(mediaPlayer.getCurrentPosition()) + "/"
                        + time.format(mediaPlayer.getDuration()));
            }
        }

    };

    public void play() {
        mediaPlayer.start();
    }

    /**
     * @param url url地址
     */
    public void playUrl(String url) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url); // 设置数据源
            mediaPlayer.prepare();
            //  mediaPlayer.prepareAsync(); // prepare自动播放
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 暂停
    public void pause() {
        mediaPlayer.pause();
    }

    // 停止
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        Log.e("mediaPlayer", "onPrepared");
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.e("mediaPlayer", "onCompletion");
    }

    /**
     * 缓冲更新
     */
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        seekBar.setSecondaryProgress(percent);
        int currentProgress = seekBar.getMax()
                * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
        Log.e(currentProgress + "% play", percent + " buffer");
    }

}
