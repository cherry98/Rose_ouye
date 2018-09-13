package com.orange.oy.activity.black;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.newtask.MyTaskListActivity;
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
import com.orange.oy.view.CollapsibleTextView;

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

public class OutSurveyRecordillustrateActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle,
        View.OnClickListener, MediaRecorder.OnErrorListener {

    private void initTitle(String str) {
        AppTitle appTitle = (AppTitle) findViewById(R.id.recodill_title_outsurvey);
        appTitle.settingName("录音任务");
        appTitle.showBack(this);
    }

    private void initNetworkConnection() {
        OutSurvey_Recordfinish = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("usermobile", AppInfo.getName(OutSurveyRecordillustrateActivity.this));
                map.put("taskid", task_id);
                map.put("storeid", store_id);
                map.put("batch", batch);
                map.put("taskbatch", taskbatch);
                return map;
            }
        };
        OutSurvey_Recordfinish.setIsShowDialog(true);
    }

    private ArrayList<BlackoutstoreInfo> list;
    private Intent service;
    private String task_id, store_id, task_name, store_name, project_id, project_name, batch, taskbatch, store_num;
    private String category1 = "", category2 = "", category3 = "";
    private String categoryPath;
    private NetworkConnection OutSurvey_Recordfinish;
    private Button recodill_button_outsurvey;
    private JSONArray datas;
    private ImageView recodill_img_outsurvey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_survey_recordillustrate);
        initNetworkConnection();
        list = (ArrayList<BlackoutstoreInfo>) getIntent().getBundleExtra("data").getSerializable("list");
        BlackoutstoreInfo blackoutstoreInfo = list.remove(0);
        updataDBHelper = new UpdataDBHelper(this);
        initTitle(blackoutstoreInfo.getTaskname());
        project_id = blackoutstoreInfo.getProjectid();
        project_name = blackoutstoreInfo.getProjectname();
        store_id = blackoutstoreInfo.getStroeid();
        store_name = blackoutstoreInfo.getStorename();
        store_num = blackoutstoreInfo.getStorenum();
        task_name = blackoutstoreInfo.getTaskname();
        task_id = blackoutstoreInfo.getTaskid();
        taskbatch = blackoutstoreInfo.getTaskbatch();
        datas = blackoutstoreInfo.getDatas();
        batch = blackoutstoreInfo.getBatch();
        categoryPath = Tools.toByte(category1 + category2 + category3 + project_id);
        ((TextView) findViewById(R.id.recodill_name_outsurvey)).setText(blackoutstoreInfo.getTaskname());
        ((CollapsibleTextView) findViewById(R.id.recodill_desc_outsurvey)).setDesc(blackoutstoreInfo.getNote(), TextView.BufferType.NORMAL);
        recodill_button_outsurvey = (Button) findViewById(R.id.recodill_button_outsurvey);
        recodill_button_outsurvey.setOnClickListener(this);
        recodill_img_outsurvey = (ImageView) findViewById(R.id.recodill_img_outsurvey);
        recodill_img_outsurvey.setOnClickListener(this);
        service = RecordService.getIntent();
        stopService(new Intent("com.orange.oy.recordservice").setPackage("com.orange.oy"));
    }

    @Override
    public void onBack() {
//        if (isStart) {
//            ConfirmDialog.showDialog(OutSurveyRecordillustrateActivity.this, null, "返回上级页面，录音将自动暂停，是否确认返回？", "取消", "确认", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
//                @Override
//                public void leftClick(Object object) {
//                }
//
//                @Override
//                public void rightClick(Object object) {
//                    BlackDZXListActivity.isRefresh = true;
//                    baseFinish();
//                }
//            });
//        } else {
//            baseFinish();
//        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.recodill_button_outsurvey) {
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
                Tools.showToast(OutSurveyRecordillustrateActivity.this, "请先录音！");
            }
        } else if (v.getId() == R.id.recodill_img_outsurvey) {
            recodill_img_outsurvey.setImageResource(R.mipmap.stop_tape);
            if (isStart) {
                recodill_img_outsurvey.setAlpha(0.5f);
                recodill_img_outsurvey.setOnClickListener(null);
                stopRecord();
            } else {
                if (!isVoicePermission()) {
                    Tools.showToast(this, "录音功能不可用，请检查录音权限是否开启！");
                } else {
                    try {
                        startRecord(project_id + task_id + store_id + categoryPath,
                                Tools.getTimeSS() + Tools.getDeviceId(OutSurveyRecordillustrateActivity.this) + task_id);
                    } catch (IOException e) {
                        Tools.showToast(this, "录音启动失败！");
                        e.printStackTrace();
                    }
                }
            }
        }
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
                    if (code == 200 || code == 2) {
                        String username = AppInfo.getName(OutSurveyRecordillustrateActivity.this);
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
                                Intent intent = new Intent(OutSurveyRecordillustrateActivity.this, OutSurveyEditActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("list", list);
                                intent.putExtra("data", bundle);
                                startActivity(intent);
                            } else if (tasktype.equals("5")) {
                                Intent intent = new Intent(OutSurveyRecordillustrateActivity.this, OutSurveyRecordillustrateActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("list", list);
                                intent.putExtra("data", bundle);
                                startActivity(intent);
                            } else if (tasktype.equals("4")) {
                                Intent intent = new Intent(OutSurveyRecordillustrateActivity.this, OutSurveyMapActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("list", list);
                                intent.putExtra("data", bundle);
                                startActivity(intent);
                            } else if (tasktype.equals("1")) {//tasktype为1的时候是拍照任务
                                Intent intent = new Intent(OutSurveyRecordillustrateActivity.this,
                                        OutSurveyTakephotoActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("list", list);
                                intent.putExtra("data", bundle);
                                intent.putExtra("tasktype", tasktype);
                                startActivity(intent);
                            } else if (tasktype.equals("8")) {//tasktype为1的时候是防翻拍-拍照任务
                                Intent intent = new Intent(OutSurveyRecordillustrateActivity.this,
                                        OutSurveyTakephotoActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("list", list);
                                intent.putExtra("data", bundle);
                                intent.putExtra("tasktype", tasktype);
                                startActivity(intent);
                            }
                        } else {
                            BlackDZXListActivity.isRefresh = true;
                        }
                        if (code == 2) {
                            ConfirmDialog.showDialog(OutSurveyRecordillustrateActivity.this, null, jsonObject.getString("msg"), null,
                                    "确定", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                                        @Override
                                        public void leftClick(Object object) {

                                        }

                                        @Override
                                        public void rightClick(Object object) {
                                            baseFinish();
                                        }
                                    }).goneLeft();
                        } else if (code == 200) {
                            baseFinish();
                        }
                    } else {
                        Tools.showToast(OutSurveyRecordillustrateActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(OutSurveyRecordillustrateActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(OutSurveyRecordillustrateActivity.this, getResources().getString(R.string.network_volleyerror));
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
        if (OutSurvey_Recordfinish != null) {
            OutSurvey_Recordfinish.stop(Urls.OutSurvey_Recordfinish);
        }
    }
}
