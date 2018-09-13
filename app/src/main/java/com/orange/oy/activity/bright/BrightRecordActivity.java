package com.orange.oy.activity.bright;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.service.RecordService;
import com.orange.oy.util.FileCache;
import com.orange.oy.view.AppTitle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BrightRecordActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle,
        View.OnClickListener, MediaRecorder.OnErrorListener {

    private void initTitle(String str) {
        AppTitle appTitle = (AppTitle) findViewById(R.id.taskitemrecod_title);
        appTitle.settingName("录音任务");
        appTitle.showBack(this);
    }

    public void initNetworkConnection() {
        assistantTask = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(BrightRecordActivity.this));
                params.put("clienttime", date);
                params.put("executeid", executeid);
                params.put("taskbatch", taskbatch);
                return params;
            }
        };
    }

    private NetworkConnection assistantTask;
    private String date, executeid, taskbatch, task_id, project_id, project_name, codeStr,
            brand, store_num, store_id, categoryPath, taskName, store_name;
    private Intent service;
    private TextView taskitemrecod_time_h, taskitemrecod_time_m, taskitemrecod_time_s;
    private TextView taskitemrecod_name;
    private ImageView taskitemrecod_start;
    private EditText taskitemrecod_edittext;
    private UpdataDBHelper updataDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitemrecod);
        Intent data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        taskName = data.getStringExtra("taskName");
        updataDBHelper = new UpdataDBHelper(this);
        initTitle(taskName);
        initNetworkConnection();
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        date = sDateFormat.format(new java.util.Date());
        executeid = data.getIntExtra("executeid", 0) + "";
        task_id = data.getIntExtra("taskid", 0) + "";
        project_id = data.getStringExtra("project_id");
        project_name = data.getStringExtra("projectname");
        codeStr = data.getStringExtra("code");
        brand = data.getStringExtra("brand");
        store_num = data.getStringExtra("store_num");
        store_name = data.getStringExtra("store_name");
        store_id = data.getStringExtra("outletid");
        taskbatch = data.getStringExtra("batch");
        categoryPath = Tools.toByte(project_id);
        taskitemrecod_name = (TextView) findViewById(R.id.taskitemrecod_name);
        taskitemrecod_time_h = (TextView) findViewById(R.id.taskitemrecod_time_h);
        taskitemrecod_time_m = (TextView) findViewById(R.id.taskitemrecod_time_m);
        taskitemrecod_time_s = (TextView) findViewById(R.id.taskitemrecod_time_s);
        taskitemrecod_start = (ImageView) findViewById(R.id.taskitemrecod_start);
        taskitemrecod_edittext = (EditText) findViewById(R.id.taskitemrecod_edittext);
        taskitemrecod_name.setText(taskName);
        taskitemrecod_start.setOnClickListener(this);
        findViewById(R.id.taskitemrecod_button).setOnClickListener(this);
        service = RecordService.getIntent();
        stopService(new Intent("com.orange.oy.recordservice").setPackage("com.orange.oy"));
        time = 0;
    }

    private void sendData() {
        assistantTask.sendPostRequest(Urls.AssistantTask, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Map<String, String> map = new HashMap<String, String>();
                        String username = AppInfo.getName(BrightRecordActivity.this);
                        map.put("usermobile", username);
                        map.put("executeid", executeid);
                        map.put("note", taskitemrecod_edittext.getText().toString().trim());
                        String key = "video";
                        updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, brand,
                                store_id, store_name, null,
                                null, "5-5", task_id, taskName, null, null, null,
                                username + project_id + store_id + task_id, Urls
                                        .AssistantTaskComplete, key, mRecordFile.getAbsolutePath(), UpdataDBHelper
                                        .Updata_file_type_video, map, null,
                                true, Urls.AssistantTask, paramsToString(), false);
                        Intent service = new Intent("com.orange.oy.UpdataNewService");
                        service.setPackage("com.orange.oy");
                        startService(service);
                        BrightBallotResultActivity.isRefresh = true;
                        BrightBallotActivity.isRefresh = true;
                        BrightPersonInfoActivity.isRefresh = true;
                        baseFinish();
                    } else {
                        Tools.showToast(BrightRecordActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(BrightRecordActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(BrightRecordActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    private String paramsToString() {
        Map<String, String> parames = new HashMap<>();
        parames.put("usermobile", AppInfo.getName(BrightRecordActivity.this));
        parames.put("clienttime", date);
        parames.put("executeid", executeid);
        parames.put("taskbatch", taskbatch);
        String data = "";
        Iterator<String> iterator = parames.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (TextUtils.isEmpty(data)) {
                try {
                    data = key + "=" + URLEncoder.encode(parames.get(key).trim(), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    data = key + "=" + parames.get(key).trim();
                }
            } else {
                data = data + "&" + key + "=" + parames.get(key).trim();
            }
        }
        return data;
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
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
                            startRecord(project_id + task_id + store_id + categoryPath,
                                    Tools.getTimeSS() + Tools.getDeviceId(BrightRecordActivity.this) + task_id);
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
                                sendData();
                            }
                        });
                    } else {
                        sendData();
                    }
                } else {
                    Tools.showToast(BrightRecordActivity.this, "请先录音！");
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
                    "" : "0" + s);
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
        if (mRecordFile != null) {//若点击了开始录音之后返回上个页面需要清空数据
            mRecordFile = null;
        }
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
