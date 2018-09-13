package com.orange.oy.activity.black;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.BlackoutstoreInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.FileCache;
import com.orange.oy.view.AppTitle;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 暗访任务说明
 */
public class BlackillustrateActivity extends BaseActivity implements View.OnClickListener, MediaRecorder.OnErrorListener {
    private WebView blackillustrate_webview;
    private TextView blackillustrate_button;
    //    public static boolean isIntoStore = true;//true：进店，false：出店
    public static String isUpdata;//0：进店 1：出店 2：体验完成
    public static boolean isNormal = false;//false 正常做任务 true 待执行列表进入

    private String project_id, store_id;
    private NetworkConnection Outletdescription, Questionnairelist, Startupload;
    private Intent data;

    private void initNetworkConnection() {
        Outletdescription = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("storeid", store_id);
                return params;
            }
        };
        Outletdescription.setIsShowDialog(true);
        Questionnairelist = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("storeid", store_id);
                return params;
            }
        };
        Questionnairelist.setIsShowDialog(true);
        Startupload = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("storeid", store_id);
                params.put("usermobile", AppInfo.getName(BlackillustrateActivity.this));
                return params;
            }
        };
        Startupload.setIsShowDialog(true);
    }

    protected void onStop() {
        super.onStop();
        if (Outletdescription != null) {
            Outletdescription.stop(Urls.Outletdescription);
        }
        if (Startupload != null) {
            Startupload.stop(Urls.Blackstartupload);
        }
        if (Questionnairelist != null) {
            Questionnairelist.stop(Urls.Questionnairelist);
        }
    }

    private String fileName, dirName;
    private String project_name, store_num, store_name, photo_compression;
    private String province, city, address;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blackillustrate);
        initNetworkConnection();
        updataDBHelper = new UpdataDBHelper(this);
        AppTitle blackillustrate_title = (AppTitle) findViewById(R.id.blackillustrate_title);
        blackillustrate_title.settingName("执行任务");
        blackillustrate_title.showBack(new AppTitle.OnBackClickForAppTitle() {
            public void onBack() {
                baseFinish();
            }
        });
        data = getIntent();
        isUpdata = data.getStringExtra("isUpdata");
        isNormal = data.getBooleanExtra("isNormal", false);
        project_id = data.getStringExtra("project_id");
        store_id = data.getStringExtra("store_id");
        project_name = data.getStringExtra("project_name");
        store_num = data.getStringExtra("store_num");
        store_name = data.getStringExtra("store_name");
        photo_compression = data.getStringExtra("photo_compression");
        province = data.getStringExtra("province");
        city = data.getStringExtra("city");
        address = data.getStringExtra("address");
        fileName = Tools.getTimeSS() + "";
        dirName = Tools.getTimeSS() + project_id + store_id;
        blackillustrate_webview = (WebView) findViewById(R.id.blackillustrate_webview);
        blackillustrate_button = (TextView) findViewById(R.id.blackillustrate_button);
        if (isUpdata.equals("0")) {
            getData();
        } else {
            blackillustrate_webview.loadUrl(Urls.OutDesc + "?storeid=" + store_id);
        }
        blackillustrate_button.setOnClickListener(this);
        checkPermission();
    }

    protected void onResume() {
        super.onResume();
        if (isUpdata.equals("1") || isUpdata.equals("2")) {
            blackillustrate_button.setText("出店");
            blackillustrate_webview.loadUrl(Urls.OutDesc + "?storeid=" + store_id);
            if (isVoicePermission()) {
                startRecordThread();
            } else {
                Tools.showToast(this, "请开启录音权限！");
                baseFinish();
            }
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, AppInfo
                        .REQUEST_CODE_ASK_RECORD_AUDIO);
                return;
            }
            checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, AppInfo
                        .REQUEST_CODE_ASK_CAMERA);
                return;
            }
            checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, AppInfo
                        .REQUEST_CODE_ASK_READ_PHONE_STATE);
                return;
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        switch (requestCode) {
            case AppInfo.REQUEST_CODE_ASK_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                } else {
                    Tools.showToast(BlackillustrateActivity.this, "拍照权限获取失败");
                    baseFinish();
                }
                break;
            case AppInfo.REQUEST_CODE_ASK_RECORD_AUDIO:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                } else {
                    Tools.showToast(BlackillustrateActivity.this, "录音权限获取失败");
                    baseFinish();
                }
                break;
            case AppInfo.REQUEST_CODE_ASK_READ_PHONE_STATE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                } else {
                    Tools.showToast(BlackillustrateActivity.this, "电话状态读取权限获取失败");
                    baseFinish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void getData() {
        Outletdescription.sendPostRequest(Urls.Outletdescription, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                String description = null;
                try {
                    description = new JSONObject(s).getString("description");
                    if (!TextUtils.isEmpty(description) && !"null".equals(description)) {
                        blackillustrate_webview.loadData(description, "text/html; charset=UTF-8", null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Tools.showToast(BlackillustrateActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(BlackillustrateActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private String batch;
    private ArrayList<BlackoutstoreInfo> list = new ArrayList<>();

    public void outStoreData() {
        Questionnairelist.sendPostRequest(Urls.Questionnairelist, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d("问卷" + s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    batch = jsonObject.getString("batch");
                    if ("200".equals(jsonObject.getString("code"))) {
                        JSONArray jsonArray = jsonObject.getJSONArray("datas");
                        int length = jsonArray.length();
                        for (int i = 0; i < length; i++) {
                            BlackoutstoreInfo blackoutstoreInfo = new BlackoutstoreInfo();
                            JSONObject json = jsonArray.getJSONObject(i);
                            blackoutstoreInfo.setProjectid(json.getString("projectid"));
                            blackoutstoreInfo.setProjectname(json.getString("projectname"));
                            blackoutstoreInfo.setStroeid(json.getString("storeid"));
                            blackoutstoreInfo.setStorenum(json.getString("storenum"));
                            blackoutstoreInfo.setStorename(json.getString("storename"));
                            blackoutstoreInfo.setTaskid(json.getString("taskid"));
                            if (json.optJSONArray("datas") != null) {
                                blackoutstoreInfo.setDatas(json.getString("datas"));
                            }
                            blackoutstoreInfo.setTaskbatch(json.getString("taskbatch"));
                            blackoutstoreInfo.setTaskname(json.getString("taskname"));
                            blackoutstoreInfo.setTasktype(json.getString("tasktype"));
                            blackoutstoreInfo.setNote(json.getString("note"));
                            blackoutstoreInfo.setQuestionnaire_type(json.getString("questionnaire_type"));
                            blackoutstoreInfo.setNum(json.getString("num"));
                            blackoutstoreInfo.setWuxiao(json.getString("wuxiao"));
                            blackoutstoreInfo.setPhoto_type(json.getString("photo_type"));
                            blackoutstoreInfo.setSta_location(json.getString("sta_location"));
                            blackoutstoreInfo.setIsphoto(json.getString("isphoto"));
                            blackoutstoreInfo.setIs_watermark(Tools.StringToInt(json.getString("is_watermark")));
                            blackoutstoreInfo.setMin_num(Tools.StringToInt(json.getString("min_num")));
                            blackoutstoreInfo.setPics(json.getString("pics"));
                            blackoutstoreInfo.setProvince(province);
                            blackoutstoreInfo.setCity(city);
                            blackoutstoreInfo.setAddress(address);
                            blackoutstoreInfo.setBatch(batch);
                            blackoutstoreInfo.setPhoto_compression(photo_compression);
                            list.add(blackoutstoreInfo);
                        }
                        if (!list.isEmpty()) {
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("list", list);
                            intent.putExtra("data", bundle);
                            if ("5".equals(list.get(0).getTasktype())) {
                                intent.setClass(BlackillustrateActivity.this, OutSurveyRecordillustrateActivity.class);
                                startActivity(intent);
                            } else if ("3".equals(list.get(0).getTasktype())) {
                                intent.setClass(BlackillustrateActivity.this, OutSurveyEditActivity.class);
                                startActivity(intent);
                            } else if ("4".equals(list.get(0).getTasktype())) {
                                intent.setClass(BlackillustrateActivity.this, OutSurveyMapActivity.class);
                                startActivity(intent);
                            } else if ("1".equals(list.get(0).getTasktype())) {
                                intent.setClass(BlackillustrateActivity.this, OutSurveyTakephotoActivity.class);
                                startActivity(intent);
                            } else if ("8".equals(list.get(0).getTasktype())) {
                                intent.setClass(BlackillustrateActivity.this, OutSurveyTakephotoActivity.class);
                                intent.putExtra("tasktype", list.get(0).getTasktype());
                                startActivity(intent);
                            }
                        } else {
                            BlackDZXListActivity.isRefresh = true;
                        }
                        baseFinish();
                    } else {
                        Tools.showToast(BlackillustrateActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Tools.showToast(BlackillustrateActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(BlackillustrateActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private void sendStartUpload() {
        Startupload.sendPostRequest(Urls.Blackstartupload, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d("出店" + s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        outStoreData();
                    } else {
                        Tools.showToast(BlackillustrateActivity.this, jsonObject.getString("msg"));
                        CustomProgressDialog.Dissmiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Tools.showToast(BlackillustrateActivity.this, getResources().getString(R.string.network_error));
                    CustomProgressDialog.Dissmiss();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(BlackillustrateActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    protected void onDestroy() {
        super.onDestroy();
        stopRecord();
        startRecordThread = null;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.blackillustrate_button: {
                if (isUpdata.equals("0")) {//进店
                    if (isVoicePermission()) {
                        data.setClass(this, BlackWorkActivity.class);
                        data.putExtra("fileName", Tools.getTimeSS() + "");
                        data.putExtra("dirName", Tools.getTimeSS() + project_id + store_id);
                        startActivity(data);
                    } else {
                        Tools.showToast(this, "请开启录音权限！");
                    }
                } else if (isUpdata.equals("1")) {//出店
                    if (isNormal) {//待执行进入
                        stopRecord();
                        addRecord();
                        outStoreData();
                    } else {//正常做任务
                        stopRecord();
                        addRecord();
                        sendStartUpload();
                    }
                } else if (isUpdata.equals("2")) {//体验完成
                    if (isNormal) {
                        stopRecord();
                        addRecord();
                        sendStartUpload();
                    } else {
                        stopRecord();
                        addRecord();
                        sendStartUpload();
                    }
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


    //添加开启录音关闭录音代码
    private boolean isAgain = false;
    private static MediaRecorder mediaRecorder;
    private static File mRecordFile;
    private boolean isStart;
    private boolean isCall = false;
    private TelephonyManager telManager;
    private int index = 0;
    private String recordKeys = "";
    private String recordValues = "";
    private UpdataDBHelper updataDBHelper;

    //开始录音
    private void startRecord() throws IOException, IllegalStateException {
        if (isStart) {
            return;
        }
        if (!createRecordDir(dirName, fileName + "_" + (index++))) {
            Tools.showToast(this, "录音文件创建失败！启动失败！");
            baseFinish();
            return;
        }
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        Tools.d(mRecordFile.getAbsolutePath());
        mediaRecorder.setOutputFile(mRecordFile.getAbsolutePath());
        mediaRecorder.setOnErrorListener(this);
        mediaRecorder.prepare();
        mediaRecorder.start();
        isStart = true;
//        startTime();
        isAgain = false;
    }


    //创建录音文件
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

    //结束录音
    private void stopRecord() {
        if (mediaRecorder == null) {
            return;
        }
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
        isStart = false;
        if (mRecordFile != null && new File(mRecordFile.getAbsolutePath()).exists()) {
            if (TextUtils.isEmpty(recordKeys)) {
                recordKeys = index + "";
            } else {
                recordKeys = recordKeys + "," + index;
            }
            if (TextUtils.isEmpty(recordValues)) {
                recordValues = mRecordFile.getAbsolutePath();
            } else {
                recordValues = recordValues + "," + mRecordFile.getAbsolutePath();
            }
        }
    }

    //录音线程
    private static StartRecordThread startRecordThread;

    private void startRecordThread() {
        if (startRecordThread == null) {
            startRecordThread = new StartRecordThread();
            startRecordThread.start();
        }
    }

    class StartRecordThread extends Thread {
        public void run() {
            try {
                sleep(1300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!isCall) {
                try {
                    startRecord();
                } catch (IOException e) {
                    MobclickAgent.reportError(BlackillustrateActivity.this, "BlackWorkActivity recorder onError 3:" + e
                            .getMessage());
//                    errorDialog("录音异常！");
                }
            }
        }
    }


    public void onError(MediaRecorder mr, int what, int extra) {
        try {
            if (mr != null) {
                mr.reset();
                if (!isAgain) {
                    isAgain = true;
                    startRecord();
                } else {
                    MobclickAgent.reportError(this, "BlackWorkActivity recorder onError 0: what" + what + ";extra:" + extra);
//                    errorDialog("录音异常！");
                }
            }
        } catch (IllegalStateException e) {
            MobclickAgent.reportError(this, "BlackWorkActivity recorder onError 1:" + e.getMessage());
//            errorDialog("录音异常！");
        } catch (Exception e) {
            MobclickAgent.reportError(this, "BlackWorkActivity recorder onError 2:" + e.getMessage());
//            errorDialog("录音异常！");
        }
    }

    //出店保存录音文件
    public void addRecord() {
        String usermobile = AppInfo.getName(BlackillustrateActivity.this);
        updataDBHelper.addUpdataTask(usermobile, project_id, project_name, store_num, null,
                store_id, store_name + "_" + store_name, null, null, "wxly", "", "神秘客户任务", null, null, null,
                usermobile + project_id + store_id + "wxly", null,
                recordKeys, recordValues, UpdataDBHelper.Updata_file_type_video, null, null, false, null, null, true, true);
        Intent service = new Intent("com.orange.oy.UpdataNewService");
        service.setPackage("com.orange.oy");
        startService(service);
    }
}
