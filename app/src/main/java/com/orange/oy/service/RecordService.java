package com.orange.oy.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;

import com.orange.oy.R;
import com.orange.oy.activity.MainActivity;
import com.orange.oy.activity.TaskitemRecodillustrateActivity;
import com.orange.oy.base.MyApplication;
import com.orange.oy.base.ScreenManager;
import com.orange.oy.base.Tools;
import com.orange.oy.receiver.SinoReceiver;
import com.orange.oy.util.FileCache;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.IOException;

/**
 * 录音服务
 */
public class RecordService extends Service implements MediaRecorder.OnErrorListener {
    public IBinder onBind(Intent arg0) {
        return myBinder;
    }

    public class MyBinder extends Binder {
        public RecordService getService() {
            return RecordService.this;
        }
    }

    public void MyMethod(String dirName, String fileName, boolean isNormal, Intent intent) {
        Tools.d("RecordService MyMethod");
        if (mediaRecorder != null) {
            stopRecord();
        }
        try {
            startRecord(dirName, fileName, isNormal);
        } catch (Exception e) {
            e.printStackTrace();
            String str = "";
            if (mRecordFile != null) {
                str = mRecordFile.getAbsolutePath();
            }
            MobclickAgent.reportError(MyApplication.getInstance(), "RecordService record start fail(" + str + "): " + e
                    .getMessage());
            Tools.showToast(this, "录音启动异常!");
        }
        RecordService.intent = intent;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        String dirName, fileName;
        boolean isNormal;
        if (intent == null) {
            if (RecordService.intent != null) {
                startNotification();
                RecordService.intent.putExtra("fileName", "record" + Tools.getTimeSS());
                dirName = RecordService.intent.getStringExtra("dirName");
                fileName = RecordService.intent.getStringExtra("fileName");
                isNormal = RecordService.intent.getBooleanExtra("isNormal", false);
                Tools.d("全程录音fileName:" + fileName);
                MyMethod(dirName, fileName, isNormal, RecordService.intent);
                return super.onStartCommand(RecordService.intent, Service.START_REDELIVER_INTENT, startId);
            } else {
                Tools.showToast(this, "录音启动失败(1001)");
                MobclickAgent.reportError(MyApplication.getInstance(), "RecordService Intent is NULL");
                stopSelf();
                return super.onStartCommand(intent, flags, startId);
            }
        } else {
            startNotification();
            dirName = intent.getStringExtra("dirName");
            fileName = intent.getStringExtra("fileName");
            isNormal = intent.getBooleanExtra("isNormal", false);
            MyMethod(dirName, fileName, isNormal, intent);
            return super.onStartCommand(intent, Service.START_REDELIVER_INTENT, startId);
        }
    }

    private MyBinder myBinder = new MyBinder();
    private static MediaRecorder mediaRecorder;
    private static File mRecordFile;
    private static Intent intent;

    public static Intent getIntent() {
        return intent;
    }

    public static void clearIntent() {
        intent = null;
    }

    public static boolean isStart() {
        return mediaRecorder != null;
    }

    public void onDestroy() {
        super.onDestroy();
        Tools.d("RecordService onDestroy");
        stopRecord();
        isStop = true;
        mRecordFile = null;
        if (intent != null) {
            intent.setClass(this, SinoReceiver.class);
            intent.setAction("service.RecordService.destory");
            sendBroadcast(intent);
        }
        stopForeground(false);
    }

    private void startRecord(String dirName, String fileName, boolean isNormal) throws IOException, IllegalStateException {
        if (!createRecordDir(dirName, fileName)) {
            Tools.showToast(this, "录音文件创建失败！启动失败！");
            ScreenManager.AppExitTaskitemDetailActivity();
            stopSelf();
            return;
        }
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
//        mediaRecorder.setAudioChannels(AudioFormat.CHANNEL_IN_DEFAULT);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//        mediaRecorder.setAudioSamplingRate(44100);//音频采样率 44100 22050
        Tools.d(mRecordFile.getAbsolutePath());
        mediaRecorder.setOutputFile(mRecordFile.getAbsolutePath());
        mediaRecorder.prepare();
        mediaRecorder.start();
        if (isNormal) {//开一个线程计时
            new Thread(new MyThread()).start();//启动线程
        }
    }

    private int i = 0;
    private boolean isStop;

    class MyThread implements Runnable {

        public void run() {
            while (!isStop) {
                i++;
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = i;
                    TaskitemRecodillustrateActivity.handler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean createRecordDir(String dirName, String name) {
        File vecordDir = FileCache.getDirForRecord(this, dirName);
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

    private void stopRecord() {
        Tools.d("停止了录音服务");
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            try {
                mediaRecorder.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mediaRecorder = null;
        }
    }

    public void onError(MediaRecorder mr, int what, int extra) {
        try {
            if (mr != null)
                mr.reset();
            stopSelf();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startNotification() {
        Notification notification;
        int icon = R.mipmap.ic_launcher;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            CharSequence tickerText = this.getResources().getString(R.string.app_name);
            Notification.Builder builder = new Notification.Builder(this).setTicker(tickerText).setSmallIcon(icon);
            Intent i = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            notification = builder.setContentIntent(pendingIntent).setContentTitle("进店").setContentText
                    ("").build();
            notification.flags = Notification.FLAG_NO_CLEAR;
            startForeground(2016, notification);
        } else {
            CharSequence tickerText = this.getResources().getString(R.string.app_name);
            Notification.Builder builder = new Notification.Builder(this).setTicker(tickerText).setSmallIcon(icon);
            builder.setContentTitle("进店");
            builder.setContentText("");
            notification = builder.getNotification();
            notification.flags = Notification.FLAG_NO_CLEAR;
            startForeground(2017, notification);
        }
    }
}
