package com.orange.oy.activity.newtask;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.fragment.TaskNewFragment;
import com.orange.oy.info.ProjectRecListInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.service.RecordService;
import com.orange.oy.util.FileCache;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.CollapsibleTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TaskNewRecordillustrateActivity extends BaseActivity implements View.OnClickListener, MediaRecorder.OnErrorListener {
    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.recodill_title_outsurvey);
        appTitle.settingName("录音任务");
    }

    public void initNetworkConnection() {
        recruitmentSoundComplete = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> parames = new HashMap<>();
                parames.put("taskid", taskid);
                parames.put("fileurl", fileurl);
                parames.put("usermobile", AppInfo.getName(TaskNewRecordillustrateActivity.this));
                parames.put("projectid", projectid);
                parames.put("taskbatch", taskbatch);
                parames.put("relbatch", relbatch);
                parames.put("type", "1");
                return parames;
            }
        };
        recruitmentSoundComplete.setIsShowDialog(true);
    }

    private ArrayList<ProjectRecListInfo> list;
    private Intent service;
    private NetworkConnection recruitmentSoundComplete;
    private UpdataDBHelper updataDBHelper;
    private String task_name, taskid, taskbatch, projectid, categoryPath, clienttime, fileurl;
    private Button recodill_button_outsurvey;
    private ImageView recodill_img_outsurvey;
    private String relbatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_survey_recordillustrate);
        initTitle();
        initNetworkConnection();
        Intent data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        relbatch = data.getStringExtra("relbatch");
        updataDBHelper = new UpdataDBHelper(this);
        recodill_button_outsurvey = (Button) findViewById(R.id.recodill_button_outsurvey);
        recodill_img_outsurvey = (ImageView) findViewById(R.id.recodill_img_outsurvey);
        list = (ArrayList<ProjectRecListInfo>) getIntent().getBundleExtra("data").getSerializable("list");
        ProjectRecListInfo projectRecListInfo = list.remove(0);
        ((TextView) findViewById(R.id.recodill_name_outsurvey)).setText(projectRecListInfo.getTaskname());
        ((CollapsibleTextView) findViewById(R.id.recodill_desc_outsurvey)).setDesc(projectRecListInfo.getNote(), TextView.BufferType.NORMAL);
        recodill_button_outsurvey.setOnClickListener(this);
        recodill_img_outsurvey.setOnClickListener(this);
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        clienttime = sDateFormat.format(new java.util.Date());
        task_name = projectRecListInfo.getTaskname();
        projectid = data.getStringExtra("projectid");
        categoryPath = Tools.toByte(projectid);
        taskid = projectRecListInfo.getTaskid();
        taskbatch = projectRecListInfo.getTaskbatch();
        service = RecordService.getIntent();
        stopService(new Intent("com.orange.oy.recordservice").setPackage("com.orange.oy"));
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
                Tools.showToast(TaskNewRecordillustrateActivity.this, "请先录音！");
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
                        startRecord(projectid + taskid + categoryPath,
                                Tools.getTimeSS() + Tools.getDeviceId(TaskNewRecordillustrateActivity.this) + taskid);
                    } catch (IOException e) {
                        Tools.showToast(this, "录音启动失败！");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void Soundtaskup() {
        if (mRecordFile == null || !new File(mRecordFile.getAbsolutePath()).exists()) {
            Tools.showToast(this, "录音失败，请重新录制");
            return;
        }
        //录音文件上传
        sendOSSData(mRecordFile.getAbsolutePath());
    }

    public void sendData() {
        recruitmentSoundComplete.sendPostRequest(Urls.RecruitmentSoundComplete, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        //跳转下一个操作
                        if (list != null && !list.isEmpty()) {
                            String tasktype = list.get(0).getTasktype();
                            if (tasktype.equals("3")) {
                                Intent intent = new Intent(TaskNewRecordillustrateActivity.this, TaskNewEditActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("list", list);
                                intent.putExtra("data", bundle);
                                intent.putExtra("projectid", projectid);
                                intent.putExtra("relbatch", relbatch);
                                startActivity(intent);
                            } else if (tasktype.equals("5")) {
                                Intent intent = new Intent(TaskNewRecordillustrateActivity.this, TaskNewRecordillustrateActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("list", list);
                                intent.putExtra("projectid", projectid);
                                intent.putExtra("data", bundle);
                                intent.putExtra("relbatch", relbatch);
                                startActivity(intent);
                            }
                        }
                        TaskNewFragment.isRefresh = true;
                        baseFinish();
                    } else {
                        Tools.showToast2(TaskNewRecordillustrateActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskNewRecordillustrateActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskNewRecordillustrateActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {
                    sendData();
                }
                break;
            }
            Tools.d("handler");
        }
    };


    /**
     * OSS上传录音文件
     */

    private OSS oss;
    private OSSCredentialProvider credentialProvider;
    private OSSAsyncTask task;

    private void sendOSSData(String path) {//录音文件oss上传
        CustomProgressDialog.showProgressDialog(this, "正在提交");
        Tools.d(path);
        final File file = new File(path);
        String objectKey = file.getName();
        fileurl = Urls.Endpoint2 + "/" + objectKey;
//        objectKey = Urls.EndpointDir + "/" + objectKey;
        Tools.d("objectKey:" + objectKey);
        Tools.d("fileurl:" + fileurl);
        if (credentialProvider == null) {
            credentialProvider = new OSSPlainTextAKSKCredentialProvider("YiMYmCHzNNO8k2l8", "oPDfExOB5wAgmT0LHRA35Ts1tGzfjH");
        }
        if (oss == null) {
            oss = new OSSClient(getApplicationContext(), Urls.Endpoint, credentialProvider);
        }
        //构造上传请求
        PutObjectRequest put = new PutObjectRequest(Urls.BucketName, objectKey, path);
        //异步上传时可设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest putObjectRequest, long currentSize, long totalSize) {
                Tools.d("currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });
        task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                Tools.d(putObjectResult.getStatusCode() + "上传成功");
                //上传成功后删除本地文件(调资料回收完成接口)
                file.delete();
                handler.sendEmptyMessage(0);
            }

            @Override
            public void onFailure(PutObjectRequest putObjectRequest, ClientException clientExcepion, ServiceException serviceException) {
                Tools.d("onFailure");
                CustomProgressDialog.Dissmiss();
                if (clientExcepion != null) {
                    //本地异常如本地网络异常
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Tools.d("ErrorCode" + serviceException.getErrorCode());
                    Tools.d("RequestId", serviceException.getRequestId());
                    Tools.d("HostId", serviceException.getHostId());
                    Tools.d("RawMessage", serviceException.getRawMessage());
                }
                task.cancel();
            }
        });
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
        if (task != null) {
            task.cancel();
        }
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
        if (recruitmentSoundComplete != null) {
            recruitmentSoundComplete.stop(Urls.RecruitmentSoundComplete);
        }
    }
}
