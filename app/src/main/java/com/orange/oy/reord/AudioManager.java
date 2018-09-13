package com.orange.oy.reord;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.orange.oy.base.Tools;
import com.orange.oy.util.FileCache;

import java.io.File;
import java.io.IOException;

/**
 * 录音播放工具类
 */

public class AudioManager implements MediaRecorder.OnErrorListener {
    private static MediaRecorder mRecorder = null;
    private static MediaPlayer mPlayer = null;

    public void clear() {
        stopRecording();
        stopPlaying();
    }

    private File mRecordFile;

    public void settingRecordFile(File file) {
        mRecordFile = file;
    }

    public File getRecordFile() {
        return mRecordFile;
    }

    private boolean createRecordDir(Context context, String dirName, String name) {
        File vecordDir = FileCache.getDirForRecord(context, dirName);
        try {
            mRecordFile = new File(vecordDir, name + ".amr");
            if (mRecordFile.exists()) {
                mRecordFile.delete();
            }
            mRecordFile.createNewFile();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 开始录音
     */
    public void startRecording(Context context, String dirName, String fileName) throws IOException, IllegalStateException {
        if (!createRecordDir(context, dirName, fileName)) {
            Tools.showToast(context, "录音文件创建失败！启动失败！");
            return;
        }
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
//        mediaRecorder.setAudioChannels(AudioFormat.CHANNEL_IN_DEFAULT);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//        mediaRecorder.setAudioSamplingRate(44100);//音频采样率 44100 22050
        Tools.d(mRecordFile.getAbsolutePath());
        mRecorder.setOutputFile(mRecordFile.getAbsolutePath());
        mRecorder.setOnErrorListener(this);
        updateMicStatus();
        mRecorder.prepare();
        try {
            mRecorder.start();
        } catch (IllegalStateException e) {
            Tools.d("录音开启失败");
        }
    }

    /**
     * 停止录音
     */
    public void stopRecording() {
        if (null == mRecorder) {
            Tools.d("停止录音1");
            return;
        }
        Tools.d("停止录音2");
        mRecorder.release();
        mRecorder = null;
    }


    private final Handler mHandler = new Handler();
    private Runnable mUpdateMicStatusTimer = new Runnable() {
        @Override
        public void run() {
            updateMicStatus();
        }
    };

    /**
     * 更新话筒状态
     */
    private void updateMicStatus() {
        int base = 1, space = 100;
        if (mRecorder != null) {
            double ratio = (double) mRecorder.getMaxAmplitude() / base;
            // 分贝
            double db = 0;
            if (ratio > 1) {
                db = 20 * Math.log10(ratio);
            }
            Tools.d("音量值：" + db);
            onVolumeChangeListener.onVolumeChange(db);
            mHandler.postDelayed(mUpdateMicStatusTimer, space);
        }
    }

    private String netUrl = "";

    public String getNetUrl() {
        return netUrl;
    }

    public void setNetUrl(String netUrl) {
        this.netUrl = netUrl;
    }

    /**
     * 开始播放
     */
    public void startPlaying(Context context) {
        if (mRecordFile == null && TextUtils.isEmpty(netUrl)) {
            Tools.showToast(context, "播放地址异常！");
            return;
        }
        mPlayer = new MediaPlayer();
        try {
            if (mRecordFile == null || !mRecordFile.exists()) {
                mPlayer.setDataSource(netUrl);
                Tools.d("开始播放 netUrl:" + netUrl);
            } else {
                mPlayer.setDataSource(mRecordFile.getPath());
                Tools.d("开始播放 path:" + mRecordFile.getPath());
            }
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Tools.d("prepare() failed");
        }
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopPlaying();
            }
        });
    }

    /**
     * 停止播放
     */
    public static void stopPlaying() {
        if (null == mPlayer) {
            return;
        }
        mPlayer.release();
        mPlayer = null;
    }


    /**
     * 获取录音的时长
     *
     * @param fileName 录音的文件
     * @return 时长的毫秒值
     */
    public int getTime(String fileName) {
        int duration = 1000;
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(fileName);
            mPlayer.prepare();
            duration = mPlayer.getDuration();
        } catch (IOException e) {
            e.printStackTrace();
            Tools.d("getTime is error");
        }
        stopPlaying();
        return duration;
    }

    public void onError(MediaRecorder mr, int what, int extra) {

    }


    public interface OnVolumeChangeListener {

        /**
         * 音量变化的监听回调
         *
         * @param value 分贝值
         */
        void onVolumeChange(double value);
    }

    private OnVolumeChangeListener onVolumeChangeListener;

    public void setOnVolumeChangeListener(OnVolumeChangeListener onVolumeChangeListener) {
        this.onVolumeChangeListener = onVolumeChangeListener;
    }


}
