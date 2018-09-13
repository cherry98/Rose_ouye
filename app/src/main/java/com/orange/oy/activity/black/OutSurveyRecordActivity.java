
package com.orange.oy.activity.black;

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
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.BlackoutstoreInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.service.RecordService;
import com.orange.oy.util.FileCache;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
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
 * 录音任务页面（出店）
 */
public class OutSurveyRecordActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View
        .OnClickListener, MediaRecorder.OnErrorListener {
    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.taskitemrecod_title);
        appTitle.settingName("录音任务");
        appTitle.showBack(this);
    }

    public void onBack() {
        BlackDZXListActivity.isRefresh = true;
        baseFinish();
    }

    private void initNetworkConnection() {
        OutSurvey_Recordfinish = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("usermobile", AppInfo.getName(OutSurveyRecordActivity.this));
                map.put("taskid", task_id);
                map.put("storeid", store_id);
                map.put("batch", batch);
                map.put("taskbatch", taskbatch);
                return map;
            }
        };
        OutSurvey_Recordfinish.setIsShowDialog(true);
    }

    private NetworkConnection OutSurvey_Recordfinish;
    private TextView taskitemrecod_name;
    private ImageView taskitemrecod_start;
    private TextView taskitemrecod_edittext;
    private Intent service;
    private String task_pack_id, task_id, store_id, task_name, task_pack_name, store_name, project_id, project_name, store_num;
    private String category1 = "", category2 = "", category3 = "";
    private String categoryPath;
    //    private String is_desc;
    private TextView taskitemrecod_time_h, taskitemrecod_time_m, taskitemrecod_time_s;
    private String batch;
    private String codeStr, brand;
    private String outlet_batch, p_batch;

    private String tasktype;
    private String note;
    private String taskbatch;
    private JSONArray datas;
    private String questionnaire_type;
    private BlackoutstoreInfo blackoutstoreInfo;
    private ArrayList<BlackoutstoreInfo> list;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_taskitemrecod);
        initNetworkConnection();
        updataDBHelper = new UpdataDBHelper(this);
        list = (ArrayList<BlackoutstoreInfo>) getIntent().getBundleExtra("data").getSerializable("list");
        blackoutstoreInfo = list.remove(0);
        project_id = blackoutstoreInfo.getProjectid();
        project_name = blackoutstoreInfo.getProjectname();
        store_id = blackoutstoreInfo.getStroeid();
        store_num = blackoutstoreInfo.getStorenum();
        store_name = blackoutstoreInfo.getStorename();
        tasktype = blackoutstoreInfo.getTasktype();
        task_name = blackoutstoreInfo.getTaskname();
        note = blackoutstoreInfo.getNote();
        task_id = blackoutstoreInfo.getTaskid();
        taskbatch = blackoutstoreInfo.getTaskbatch();
        datas = blackoutstoreInfo.getDatas();
        questionnaire_type = blackoutstoreInfo.getQuestionnaire_type();
        batch = blackoutstoreInfo.getBatch();
        initTitle();
        categoryPath = Tools.toByte(category1 + category2 + category3 + project_id);
        taskitemrecod_name = (TextView) findViewById(R.id.taskitemrecod_name);
        taskitemrecod_name.setText(task_name);
        taskitemrecod_time_h = (TextView) findViewById(R.id.taskitemrecod_time_h);
        taskitemrecod_time_m = (TextView) findViewById(R.id.taskitemrecod_time_m);
        taskitemrecod_time_s = (TextView) findViewById(R.id.taskitemrecod_time_s);
        taskitemrecod_start = (ImageView) findViewById(R.id.taskitemrecod_start);
        taskitemrecod_edittext = (TextView) findViewById(R.id.taskitemrecod_edittext);
        taskitemrecod_start.setOnClickListener(this);
        findViewById(R.id.taskitemrecod_button).setOnClickListener(this);
        findViewById(R.id.taskitemrecod_note_title).setVisibility(View.GONE);
        taskitemrecod_edittext.setVisibility(View.GONE);
        service = RecordService.getIntent();
        stopService(new Intent("com.orange.oy.recordservice").setPackage("com.orange.oy"));
        time = 0;
    }

    private UpdataDBHelper updataDBHelper;

    private void Soundtaskup() {
        if (mRecordFile == null || !new File(mRecordFile.getAbsolutePath()).exists()) {
            Tools.showToast(this, "录音失败，请重新录制");
            return;
        }
        OutSurvey_Recordfinish.sendPostRequest(Urls.OutSurvey_Recordfinish, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        String username = AppInfo.getName(OutSurveyRecordActivity.this);
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("usermobile", username);
                        map.put("taskid", task_id);
                        map.put("storeid", store_id);
                        map.put("batch", batch);
                        map.put("taskbatch", taskbatch);
                        String key = "video";
                        updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, null,
                                store_id, store_name, null, null, "5", task_id, task_name, null, null, null,
                                username + project_id + store_id + task_id, Urls.OutSurvey_SoundUp
                                , key, mRecordFile.getAbsolutePath(), UpdataDBHelper
                                        .Updata_file_type_video, map, null, true, Urls.OutSurvey_Recordfinish, paramsToString()
                                , true);
                        Intent service = new Intent("com.orange.oy.UpdataNewService");
                        service.setPackage("com.orange.oy");
                        startService(service);
                        if (list != null && !list.isEmpty()) {
                            String tasktype = list.get(0).getTasktype();
                            if (tasktype.equals("3")) {
                                Intent intent = new Intent(OutSurveyRecordActivity.this, OutSurveyEditillustrateActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("list", list);
                                intent.putExtra("data", bundle);
                                startActivity(intent);
                            } else if (tasktype.equals("5")) {
                                Intent intent = new Intent(OutSurveyRecordActivity.this, OutSurveyRecordillustrateActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("list", list);
                                intent.putExtra("data", bundle);
                                startActivity(intent);
                            } else if (tasktype.equals("4")) {
                                Intent intent = new Intent(OutSurveyRecordActivity.this, OutSurveyMapActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("list", list);
                                intent.putExtra("data", bundle);
                                startActivity(intent);
                            } else if (tasktype.equals("1")) {
                                Intent intent = new Intent(OutSurveyRecordActivity.this, OutSurveyTakephotoillustrateActivity
                                        .class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("list", list);
                                intent.putExtra("data", bundle);
                                startActivity(intent);
                            }
                        } else {
                            BlackDZXListActivity.isRefresh = true;
                        }
                        baseFinish();
                    } else {
                        Tools.showToast(OutSurveyRecordActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(OutSurveyRecordActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(OutSurveyRecordActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private String paramsToString() {
        Map<String, String> parames = new HashMap<>();
        parames.put("usermobile", AppInfo.getName(this));
        parames.put("taskid", task_id);
        parames.put("storeid", store_id);
        parames.put("batch", batch);
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
                                    Tools.getTimeSS() + Tools.getDeviceId(OutSurveyRecordActivity.this) + task_id);
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
                    Tools.showToast(OutSurveyRecordActivity.this, "请先录音！");
                }
            }
            break;
        }
    }

    public boolean isVoicePermission() {
        try {
            AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.MIC, 22050, AudioFormat
                    .CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, AudioRecord.getMinBufferSize(22050, AudioFormat
                            .CHANNEL_CONFIGURATION_MONO,
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
