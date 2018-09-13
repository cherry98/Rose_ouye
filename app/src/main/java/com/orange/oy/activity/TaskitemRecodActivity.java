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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.AppDBHelper;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.service.RecordService;
import com.orange.oy.util.FileCache;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.SpreadTextView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * 录音任务页面
 */
public class TaskitemRecodActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View
        .OnClickListener, MediaRecorder.OnErrorListener {
    private void initTitle(String str) {
        AppTitle appTitle = (AppTitle) findViewById(R.id.taskitemrecod_title);
        appTitle.settingName("录音任务");
        appTitle.showBack(this);
        if ("1".equals(is_desc)) {
            appTitle.setIllustrate(new AppTitle.OnExitClickForAppTitle() {
                public void onExit() {
                    Intent intent = new Intent(TaskitemRecodActivity.this, StoreDescActivity.class);
                    intent.putExtra("id", store_id);
                    intent.putExtra("store_name", store_name);
                    intent.putExtra("is_task", true);
                    startActivity(intent);
                }
            });
        }
    }

    public void onBack() {
        baseFinish();
    }

    private void initNetworkConnection() {
        Soundtaskup = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(TaskitemRecodActivity.this));
                params.put("pid", task_pack_id);
                params.put("task_id", task_id);
                params.put("note", taskitemrecod_edittext.getText().toString());
                params.put("storeid", store_id);
                params.put("category1", category1);
                params.put("category2", category2);
                params.put("category3", category3);
                params.put("batch", batch);
                params.put("outlet_batch", outlet_batch);
                params.put("p_batch", p_batch);
                return params;
            }
        };
        Soundtaskup.setIsShowDialog(true);

        soundupdate = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("storeid", store_id);  //网点id
                params.put("token", Tools.getToken());
                params.put("pid", task_pack_id);  //任务包id
                params.put("p_batch", p_batch); // 任务包批次
                params.put("outlet_batch", outlet_batch); //网点任务批次
                params.put("taskid", task_id);  //任务id
                params.put("usermobile", username); //用户账号
                params.put("original_url", original_url);  // 原始文件路径【必填】
                params.put("new_url", new_url); // 	新的文件路径，当type为0时该参数必填
                params.put("type", "0");  // （0为重做只替换路径，1为删除）

                return params;
            }
        };
        soundupdate.setIsShowDialog(true);
    }

    private NetworkConnection Soundtaskup, soundupdate;
    private TextView taskitemrecod_name;
    private ImageView taskitemrecod_start;
    private TextView taskitemrecod_edittext;
    private Intent service;
    private String task_pack_id, task_id, store_id, task_name, task_pack_name, store_name, project_id, project_name, store_num;
    private String category1 = "", category2 = "", category3 = "";
    private String categoryPath;
    private String is_desc, username;
    private TextView taskitemrecod_time_h, taskitemrecod_time_m, taskitemrecod_time_s;
    private String batch,taskNote;
    private String codeStr, brand;
    private String outlet_batch, p_batch;
    private String original_url; //原始文件路径
    private String new_url; //新文件路径
    private AppDBHelper appDBHelper;
    private SpreadTextView spacer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_taskitemrecod);
        initNetworkConnection();
        updataDBHelper = new UpdataDBHelper(this);
        appDBHelper = new AppDBHelper(this);
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
        is_desc = data.getStringExtra("is_desc");
        batch = data.getStringExtra("batch");
        codeStr = data.getStringExtra("code");
        brand = data.getStringExtra("brand");
        outlet_batch = data.getStringExtra("outlet_batch");
        p_batch = data.getStringExtra("p_batch");
        original_url = data.getStringExtra("original_url");
        Tools.d("tag", "original_url===========>>>>>" + original_url);
        taskNote=data.getStringExtra("taskNote");
        username = AppInfo.getName(this);
        if (TextUtils.isEmpty(batch) || batch.equals("null")) {
            batch = "1";
        }
        initTitle(data.getStringExtra("task_name"));
        categoryPath = Tools.toByte(category1 + category2 + category3 + project_id);
        taskitemrecod_name = (TextView) findViewById(R.id.taskitemrecod_name);
        taskitemrecod_time_h = (TextView) findViewById(R.id.taskitemrecod_time_h);
        taskitemrecod_time_m = (TextView) findViewById(R.id.taskitemrecod_time_m);
        taskitemrecod_time_s = (TextView) findViewById(R.id.taskitemrecod_time_s);
        taskitemrecod_start = (ImageView) findViewById(R.id.taskitemrecod_start);
        taskitemrecod_edittext = (TextView) findViewById(R.id.taskitemrecod_edittext);
        spacer = (SpreadTextView) findViewById(R.id.spacer);
        taskitemrecod_start.setOnClickListener(this);
        findViewById(R.id.taskitemrecod_button).setOnClickListener(this);
        service = RecordService.getIntent();
        stopService(new Intent("com.orange.oy.recordservice").setPackage("com.orange.oy"));
        time = 0;
        spacer.setDesc(taskNote);
    }

    private UpdataDBHelper updataDBHelper;

    /////888  重做
    private void SoundtaskupReply() {

        soundupdate.sendPostRequest(Urls.soundupdate, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Tools.showToast(getBaseContext(), jsonObject.getString("msg"));
                        EventBus.getDefault().post("5");
                        baseFinish();
                    } else {
                        Tools.showToast(TaskitemRecodActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskitemRecodActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(TaskitemRecodActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    private void Soundtaskup() {
        Soundtaskup.sendPostRequest(Urls.Soundtaskup, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        String executeid = jsonObject.getString("executeid");
                        String username = AppInfo.getName(TaskitemRecodActivity.this);
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("usermobile", username);
                        map.put("task_pack_id", task_pack_id);
                        map.put("task_id", task_id);
                        map.put("executeid", executeid);
                        map.put("storeid", store_id);
                        map.put("note", taskitemrecod_edittext.getText().toString());
                        map.put("category1", category1);
                        map.put("category2", category2);
                        map.put("category3", category3);
                        map.put("outlet_batch", outlet_batch);
                        map.put("p_batch", p_batch);
                        map.put("batch", batch);
                        String key = "video";
                        updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, brand,
                                store_id, store_name, task_pack_id,
                                task_pack_name, "5", task_id, task_name, category1, category2, category3,
                                username + project_id +
                                        store_id + task_pack_id + category1 + category2 + category3 + task_id, Urls
                                        .Filecomplete, key, mRecordFile.getAbsolutePath(), UpdataDBHelper
                                        .Updata_file_type_video, map, null,
                                true, Urls.Soundtaskup, paramsToString(), false);
                        Intent service = new Intent("com.orange.oy.UpdataNewService");
                        service.setPackage("com.orange.oy");
                        startService(service);
                        TaskitemDetailActivity.isRefresh = true;
                        TaskitemDetailActivity.taskid = task_id;
                        TaskitemDetailActivity_12.isRefresh = true;
                        TaskitemDetailActivity_12.taskid = task_id;
                        TaskFinishActivity.isRefresh = true;
                        TaskitemListActivity.isRefresh = true;
                        TaskitemListActivity_12.isRefresh = true;
                        //  baseFinish();
                        SoundtaskupReply();
                    } else {
                        Tools.showToast(TaskitemRecodActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskitemRecodActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskitemRecodActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private String paramsToString() {
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
                                    Tools.getTimeSS() + Tools.getDeviceId(TaskitemRecodActivity.this) + task_id);
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
                                Soundtaskup();
                            }
                        });
                    } else {
                        Soundtaskup();
                    }
                } else {
                    Tools.showToast(TaskitemRecodActivity.this, "请先录音！");
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
        taskitemrecod_start.setImageResource(R.mipmap.stop_tape);
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
            new_url = Urls.Endpoint2 + "/" + mRecordFile.getName();
            //Tools.d("tag", "new_url===========>>>>>" + new_url);
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
                taskitemrecod_start.setImageResource(R.mipmap.start_tape);
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
