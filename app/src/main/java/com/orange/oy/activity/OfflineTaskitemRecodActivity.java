package com.orange.oy.activity;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.OfflineDBHelper;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.network.Urls;
import com.orange.oy.service.RecordService;
import com.orange.oy.util.FileCache;
import com.orange.oy.view.AppTitle;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 录音任务页面
 */
public class OfflineTaskitemRecodActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View
        .OnClickListener, MediaRecorder.OnErrorListener {
    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.taskitemrecod_title);
        appTitle.settingName("录音任务");
        appTitle.showBack(this);
    }

    public void onBack() {
        baseFinish();
    }

    private TextView taskitemrecod_name;
    private TextView taskitemrecod_time_h, taskitemrecod_time_m, taskitemrecod_time_s;
    private ImageView taskitemrecod_start;
    private TextView taskitemrecod_edittext;
    private Intent service;
    private String task_pack_id, task_id, store_id, task_name, task_pack_name, store_name, project_id, project_name, store_num;
    private String category1 = "", category2 = "", category3 = "";
    private String categoryPath;
    private OfflineDBHelper offlineDBHelper;
    private String batch, code, brand;
    private String outlet_batch, p_batch;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_taskitemrecod);
        offlineDBHelper = new OfflineDBHelper(this);
        initTitle();
        updataDBHelper = new UpdataDBHelper(this);
        Intent data = getIntent();
        project_id = data.getStringExtra("project_id");
        project_name = data.getStringExtra("project_name");
        task_pack_id = data.getStringExtra("task_pack_id");
        task_id = data.getStringExtra("task_id");
        store_id = data.getStringExtra("store_id");
        store_num = data.getStringExtra("store_num");
        task_pack_name = data.getStringExtra("task_pack_name");
        store_name = data.getStringExtra("store_name");
        task_name = data.getStringExtra("task_name");
        category1 = data.getStringExtra("category1");
        category2 = data.getStringExtra("category2");
        category3 = data.getStringExtra("category3");
        batch = data.getStringExtra("batch");
        code = data.getStringExtra("code");
        brand = data.getStringExtra("brand");
        outlet_batch = data.getStringExtra("outlet_batch");
        p_batch = data.getStringExtra("p_batch");
        if (TextUtils.isEmpty(batch) || batch.equals("null")) {
            batch = "1";
        }
        categoryPath = Tools.toByte(category1 + category2 + category3 + project_id);
        taskitemrecod_name = (TextView) findViewById(R.id.taskitemrecod_name);
        taskitemrecod_time_h = (TextView) findViewById(R.id.taskitemrecod_time_h);
        taskitemrecod_time_m = (TextView) findViewById(R.id.taskitemrecod_time_m);
        taskitemrecod_time_s = (TextView) findViewById(R.id.taskitemrecod_time_s);
        taskitemrecod_start = (ImageView) findViewById(R.id.taskitemrecod_start);
        taskitemrecod_edittext = (TextView) findViewById(R.id.taskitemrecod_edittext);
        taskitemrecod_start.setOnClickListener(this);
        findViewById(R.id.taskitemrecod_button).setOnClickListener(this);
        service = RecordService.getIntent();
        stopService(new Intent("com.orange.oy.recordservice").setPackage("com.orange.oy"));
        time = 0;
    }

    private UpdataDBHelper updataDBHelper;

    private void Soundtaskup() throws UnsupportedEncodingException {
        String username = AppInfo.getName(this);
        if (TextUtils.isEmpty(task_pack_id)) {
            offlineDBHelper.completedTask(username, project_id, store_id, task_id);
        } else {
            offlineDBHelper.insertOfflineCompleted(username, project_id, store_id, task_pack_id, task_id, category1,
                    category2, category3);
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("usermobile", username);
        map.put("task_pack_id", task_pack_id);
        map.put("task_id", task_id);
        map.put("storeid", store_id);
        map.put("note", taskitemrecod_edittext.getText().toString());
        map.put("category1", category1);
        map.put("category2", category2);
        map.put("category3", category3);
        map.put("outlet_batch", outlet_batch);
        map.put("p_batch", p_batch);
        map.put("batch", batch);
        String key = "video";
        updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, brand, store_id, store_name, task_pack_id,
                task_pack_name, "5", task_id, task_name, category1, category2, category3,
                username + project_id +
                        store_id + task_pack_id + category1 + category2 + category3 + task_id, Urls
                        .Filecomplete, key, mRecordFile.getAbsolutePath(), UpdataDBHelper
                        .Updata_file_type_video, map, null, true, Urls.Soundtaskup, paramsToString(), true);
        Intent service = new Intent("com.orange.oy.UpdataNewService");
        service.setPackage("com.orange.oy");
        startService(service);
        OfflinePackageActivity.isRefresh = true;
        OfflineTaskActivity.isRefresh = true;
        baseFinish();
    }

    private String paramsToString() throws UnsupportedEncodingException {
        Map<String, String> parames = new HashMap<>();
        parames.put("usermobile", AppInfo.getName(this));
        parames.put("pid", task_pack_id);
        parames.put("task_id", task_id);
        parames.put("note", taskitemrecod_edittext.getText().toString());
        parames.put("storeid", store_id);
        parames.put("category1", category1);
        parames.put("category2", category2);
        parames.put("category3", category3);
        parames.put("batch", batch);
        parames.put("outlet_batch", outlet_batch);
        parames.put("p_batch", p_batch);
        String data = "";
        Iterator<String> iterator = parames.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (TextUtils.isEmpty(data)) {
                data = key + "=" + URLEncoder.encode(parames.get(key).trim(), "utf-8");
            } else {
                data = data + "&" + key + "=" + URLEncoder.encode(parames.get(key).trim(), "utf-8");
            }
        }
        return data;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taskitemrecod_start: {
                if (isStart) {
                    stopRecord();
                } else {
                    if (!isVoicePermission()) {
                        Tools.showToast(this, "录音功能不可用，请检查录音权限是否开启！");
                    } else {
                        try {
                            startRecord(project_id + task_pack_id + task_id + store_id + categoryPath,
                                    Tools.getTimeSS() + Tools.getDeviceId(OfflineTaskitemRecodActivity.this) + task_id);
                        } catch (IOException e) {
                            Tools.showToast(this, "录音启动失败！");
                            e.printStackTrace();
                        }
                    }
                }
            }
            break;
            case R.id.taskitemrecod_button: {
                if (mRecordFile != null) {
                    if (isStart) {
                        ConfirmDialog.showDialog(this, "录音未关闭，确定提交？", true, new ConfirmDialog
                                .OnSystemDialogClickListener() {
                            public void leftClick(Object object) {
                            }

                            public void rightClick(Object object) {
                                stopRecord();
                                try {
                                    Soundtaskup();
                                } catch (UnsupportedEncodingException e) {
                                    Tools.showToast(OfflineTaskitemRecodActivity.this, "存储失败，未知异常！");
                                    MobclickAgent.reportError(OfflineTaskitemRecodActivity.this, "offline edit:" + e.getMessage
                                            ());
                                }
                            }
                        });
                    } else {
                        try {
                            Soundtaskup();
                        } catch (UnsupportedEncodingException e) {
                            Tools.showToast(OfflineTaskitemRecodActivity.this, "存储失败，未知异常！");
                            MobclickAgent.reportError(OfflineTaskitemRecodActivity.this, "offline edit:" + e.getMessage());
                        }
                    }
                } else {
                    Tools.showToast(OfflineTaskitemRecodActivity.this, "请先录音！");
                }
            }
            break;
        }
    }

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

    private static MediaRecorder mediaRecorder;
    private static File mRecordFile;
    private Count count;
    private int time;
    private boolean isStart;

    class Count implements Runnable {
        public void run() {
            time++;
            int s = time % 60;
            int m = time / 60;
            int h = m / 60;
            String timestr = ((h > 9) ? h + "" : "0" + h) + ":" + ((m > 9) ? m + "" : "0" + m) + ":" + ((s > 9) ? s +
                    "" : "0"
                    + s);
            if (Timerhandler != null && isStart) {
                Message msg = Message.obtain();
                msg.obj = timestr;
                Timerhandler.sendMessage(msg);
                Timerhandler.postDelayed(count, 1000);
            }
        }
    }

    private Handler Timerhandler = new Handler() {
        public void handleMessage(Message msg) {
            if (taskitemrecod_time_h != null && taskitemrecod_time_m != null && taskitemrecod_time_s != null) {
                String[] ss = msg.obj.toString().split(":");
                taskitemrecod_time_h.setText(ss[0]);
                taskitemrecod_time_m.setText(ss[1]);
                taskitemrecod_time_s.setText(ss[2]);
            }
        }
    };

    private void startRecord(String dirName, String fileName) throws IOException, IllegalStateException {
        if (!createRecordDir(dirName, fileName)) {
            Tools.showToast(this, "录音文件创建失败！启动失败！");
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
        mediaRecorder.setOnErrorListener(this);
        mediaRecorder.prepare();
        mediaRecorder.start();
        taskitemrecod_start.setImageResource(R.mipmap.rec_stop);
        count = new Count();
        isStart = true;
        Timerhandler.post(count);
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
            if (taskitemrecod_start != null)
                taskitemrecod_start.setImageResource(R.mipmap.rec);
        }
        isStart = false;
        time = 0;
    }

    public void onError(MediaRecorder mr, int what, int extra) {
        try {
            if (mr != null)
                mr.reset();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        stopRecord();
        Timerhandler = null;
    }

    protected void onStop() {
        super.onStop();
        if (service != null && !RecordService.isStart()) {
            service.setClass(this, RecordService.class);
            String filename = service.getStringExtra("fileName");
            service.putExtra("fileName", filename + "_" + Tools.getTimeByPattern("yyyy-MM-dd-HH-mm-ss"));
            startService(service);
        } else {
//            Tools.showToast(MyApplication.getInstance(), "进店录音重启失败！");
        }
    }
}
