package com.orange.oy.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ImageView;

import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.service.RecordService;
import com.orange.oy.view.MovieRecorderView;

/**
 * 录制页
 */
public class ShotActivity extends BaseActivity {
    private MovieRecorderView mRecorderView;
    private ImageView mShootBtn;
    private boolean isFinish = true;
    private int index;
    private String dirName, fileName;

    /**
     * 通过forresult方式启动本页，
     * 需要传递两个参数，1、index，2、dirName
     * 第一个参数将当作视频的名字，且必须是整型
     * 第二个参数是视频所在文件夹的名字
     *
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PackageManager pm = getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.CAMERA",
                "com.orange.oy"));
        if (!permission) {
            Tools.showToast(this, "没有摄像头权限！请开启！");
            baseFinish();
            return;
        } else {
            Tools.d("无效");
        }
        setContentView(R.layout.activity_shot);
        Intent data = getIntent();
        if (data == null) {
            finish();
            return;
        }
        index = data.getIntExtra("index", 0);
        fileName = data.getStringExtra("fileName");
        dirName = data.getStringExtra("dirName");
        mRecorderView = (MovieRecorderView) findViewById(R.id.movieRecorderView);
        mShootBtn = (ImageView) findViewById(R.id.shoot_button);
        mRecorderView.setOnRecordTypeChangeListener(new MovieRecorderView.OnRecordTypeChangeListener() {
            public void start() {
            }

            public void exception(Exception e) {
                baseFinish();
            }
        });
        mShootBtn.setOnClickListener(shootBtnListener);
        service = RecordService.getIntent();
        stopService(new Intent("com.orange.oy.recordservice").setPackage("com.orange.oy"));
    }

    private View.OnClickListener shootBtnListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (mShootBtn.getTag() != null) {
                mShootBtn.setTag(null);
                if (mRecorderView.getTimeCount() > 1)
                    handler.sendEmptyMessage(1);
                else {
                    if (mRecorderView.getmRecordFile() != null)
                        mRecorderView.getmRecordFile().delete();
                    mRecorderView.stop();
                    mShootBtn.setImageResource(R.mipmap.video_start);
                    ConfirmDialog.showDialog(ShotActivity.this, "提示", 3, "视频录制时间太短，要重新录制吗？", "不要", "重新录制",
                            null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                public void leftClick(Object object) {
                                    baseFinish();
                                }

                                public void rightClick(Object object) {
                                    shootBtnListener.onClick(mShootBtn);
                                }
                            });
                }
            } else {
                if (!isVoicePermission()) {
                    Tools.showToast(ShotActivity.this, "录音功能不可用，请检查录音权限是否开启！");
                } else {
                    mRecorderView.record(dirName, fileName, new MovieRecorderView.OnRecordFinishListener() {
                        public void onRecordFinish() {
                            handler.sendEmptyMessage(1);
                        }
                    });
                    mShootBtn.setTag("停止");
                    mShootBtn.setImageResource(R.mipmap.video_end);
                }
            }
        }
    };

    public boolean isVoicePermission() {
        try {
            AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.MIC, 22050, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, AudioRecord.getMinBufferSize(22050, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT));
            record.startRecording();
            int recordingState = record.getRecordingState();
            if (recordingState == AudioRecord.RECORDSTATE_STOPPED) {
                return false;
            }
            record.release();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private ResolveInfo getResolveInfo() {
        Intent intent = createVoiceSearchIntent();
        ResolveInfo ri = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return ri;

    }

    protected Intent createVoiceSearchIntent() {
        return new Intent(RecognizerIntent.ACTION_WEB_SEARCH);

    }

    private Intent service;

    public void onResume() {
        super.onResume();
        isFinish = true;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        isFinish = false;
        mRecorderView.stop();
    }

    public void onPause() {
        super.onPause();
        try {
            mRecorderView.stop();
            mShootBtn.setImageResource(R.mipmap.video_start);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onStop() {
        super.onStop();
        if (service != null && !RecordService.isStart()) {
            service.setClass(this, RecordService.class);
            String filename = service.getStringExtra("fileName");
            service.putExtra("fileName", filename + Tools.getTimeSS());
            startService(service);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mRecorderView != null)
            mRecorderView.freeCameraResource();
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            finishActivity();
        }
    };

    private void finishActivity() {
        if (isFinish) {
            mRecorderView.stop();
            // 返回到播放页面
            Intent intent = new Intent();
            Tools.d(mRecorderView.getmRecordFile().getAbsolutePath());
            intent.putExtra("path", mRecorderView.getmRecordFile().getAbsolutePath());
            intent.putExtra("index", index);
            intent.putExtra("position", getIntent().getIntExtra("position", 0));
            setResult(AppInfo.ShotSuccessResultCode, intent);
        }
        // isFinish = false;
        finish();
    }

}
