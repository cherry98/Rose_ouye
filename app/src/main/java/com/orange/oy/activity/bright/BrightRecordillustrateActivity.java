package com.orange.oy.activity.bright;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.StoreDescActivity;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BrightRecordillustrateActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle,
        View.OnClickListener, MediaRecorder.OnErrorListener {
    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.taskitmrecodill_title);
        appTitle.settingName("录音任务");
        appTitle.showBack(this);
        if ("1".equals(is_desc)) {
            appTitle.setIllustrate( new AppTitle.OnExitClickForAppTitle() {
                public void onExit() {
                    Intent intent = new Intent(BrightRecordillustrateActivity.this, StoreDescActivity.class);
                    intent.putExtra("id", store_id);
                    intent.putExtra("store_name", store_name);
                    intent.putExtra("is_task", true);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Soundtask != null) {
            Soundtask.stop(Urls.Soundtask);
        }
        if (service != null && !RecordService.isStart()) {
            service.setClass(this, RecordService.class);
            String filename = service.getStringExtra("fileName");
            service.putExtra("fileName", filename + "_" + Tools.getTimeByPattern("yyyy-MM-dd-HH-mm-ss"));
            startService(service);
        } else {
//            Tools.showToast(MyApplication.getInstance(), "进店录音重启失败！");
        }
    }

    private void initNetworkConnection() {
        Soundtask = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("task_pack_id", "");
                params.put("task_id", taskid);
                return params;
            }
        };
        Soundtask.setIsShowDialog(true);
    }

    private String taskid;
    private NetworkConnection Soundtask;
    private TextView taskitmrecodill_desc, taskitmrecodill_name;
    private Intent data;
    private NetworkConnection assistantTask;
    private String date, executeid, taskbatch, task_id, project_id, project_name, codeStr,
            brand, store_num, store_id, categoryPath, taskName, store_name, is_desc;
    private Intent service;
    private UpdataDBHelper updataDBHelper;
    private TextView taskitmrecodill_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitemrecodillustrate);
        data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        updataDBHelper = new UpdataDBHelper(this);
        initNetworkConnection();
        taskid = data.getIntExtra("taskid", 0) + "";
        is_desc = data.getStringExtra("is_desc");
        store_name = data.getStringExtra("store_name");
        initTitle();
        taskitmrecodill_desc = (TextView) findViewById(R.id.taskitmrecodill_desc);
        taskitmrecodill_name = (TextView) findViewById(R.id.taskitmrecodill_name);
        taskitmrecodill_button = (TextView) findViewById(R.id.taskitmrecodill_button);
        taskitmrecodill_button.setOnClickListener(this);
        getData();
    }

    private String batch;

    private void getData() {
        Soundtask.sendPostRequest(Urls.Soundtask, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        batch = jsonObject.getString("batch");
                        taskName = jsonObject.getString("taskName");
                        taskitmrecodill_name.setText(taskName);
                        taskitmrecodill_desc.setText(jsonObject.getString("note"));
                    } else {
                        Tools.showToast(BrightRecordillustrateActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(BrightRecordillustrateActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(BrightRecordillustrateActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }


    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taskitmrecodill_button: {
                taskitmrecodill_button.setText("结束录音");
                if (isStart) {
                    stopRecord();
                    sendData();
                } else {
                    if (!isVoicePermission()) {
                        Tools.showToast(this, "录音功能不可用，请检查录音权限是否开启！");
                    } else {
                        try {
                            startRecord(project_id + taskid + categoryPath,
                                    Tools.getTimeSS() + Tools.getDeviceId(BrightRecordillustrateActivity.this) + taskid);
                        } catch (IOException e) {
                            Tools.showToast(this, "录音启动失败！");
                            e.printStackTrace();
                        }
                    }
                }
            }
            break;
        }
    }

    private void sendData() {
        assistantTask.sendPostRequest(Urls.AssistantTask, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Map<String, String> map = new HashMap<String, String>();
                        String username = AppInfo.getName(BrightRecordillustrateActivity.this);
                        map.put("usermobile", username);
                        map.put("executeid", executeid);
                        map.put("note", "");
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
                        Tools.showToast(BrightRecordillustrateActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(BrightRecordillustrateActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(BrightRecordillustrateActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    private String paramsToString() {
        Map<String, String> parames = new HashMap<>();
        parames.put("usermobile", AppInfo.getName(BrightRecordillustrateActivity.this));
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
    private int time;
    private boolean isStart;


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
        isStart = true;
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
    }

}
