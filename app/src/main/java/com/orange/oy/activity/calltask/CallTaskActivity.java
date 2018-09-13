package com.orange.oy.activity.calltask;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.TaskFinishActivity;
import com.orange.oy.activity.TaskitemDetailActivity;
import com.orange.oy.activity.TaskitemDetailActivity_12;
import com.orange.oy.activity.TaskitemListActivity;
import com.orange.oy.activity.TaskitemListActivity_12;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.NonExecutionTask;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 电话任务页
 */
public class CallTaskActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {
    private AppTitle appTitle;

    public void initTitle() {
        appTitle = (AppTitle) findViewById(R.id.calltask_title);
        if (index != null && "0".equals(index)) {
            appTitle.settingName("电话任务（预览）");
        } else {
            appTitle.settingName("电话任务");
        }
        appTitle.showBack(this);
    }

    public void showExit(boolean isShow) {
        if (appTitle == null) return;
        if (isShow) {
            appTitle.settingExit("无法执行");
            appTitle.settingExitColor(Color.parseColor("#F65D57"));
            appTitle.showExit(new AppTitle.OnExitClickForAppTitle() {
                @Override
                public void onExit() {
                    noWayExecute();
                }
            });
        } else {
            appTitle.hideExit();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (callTask != null) {
            callTask.stop(Urls.CallTask);
        }
        if (callTaskUp != null) {
            callTaskUp.stop(Urls.CallTaskUp);
        }
    }

    public void initNetworkConnection() {
        callTask = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("store_id", store_id);
                params.put("task_id", task_id);
                params.put("task_pack_id", task_pack_id);
                return params;
            }
        };
        callTask.setIsShowDialog(true);
        callTaskUp = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("storeid", store_id);
                params.put("taskid", task_id);
                params.put("pid", task_pack_id);
                params.put("p_batch", p_batch);
                params.put("usermobile", AppInfo.getName(CallTaskActivity.this));
                params.put("batch", batch);
                params.put("outlet_batch", outlet_batch);
                params.put("flag", flag2);
                if (time == null) {
                    time = "";
                }
                params.put("time", time);//拨打电话时间
                params.put("calltime", calltime + "");//通话时长
                params.put("telphone", telphone);
                if (note == null) {
                    note = "";
                }
                params.put("note", note);
                return params;
            }
        };
    }

    private String is_desc, store_id, store_name, task_id, task_pack_id, p_batch, batch, outlet_batch, time;
    private String project_id;
    private NetworkConnection callTask, callTaskUp;
    private View lin_call, lin_Upload; //拨打电话的布局，上传
    private TextView tv_recordingfile;
    private String telphone;
    private long calltime;
    private String flag2;//1置无效 0不置无效
    //    private TextView calltask_button2, tv_recordingfile, tv_findrecordingfile;//无法执行 ,显示录音，查找录音
    private String note;
    private String index;//扫码任务预览
    private View calltask_findrecode;//查找界面
    private View calltask_button;//拨打号码
    private UpdataDBHelper updataDBHelper;
    private String project_name, task_pack_name, task_name, store_num, brand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_task);
        Intent data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        updataDBHelper = new UpdataDBHelper(this);
        index = data.getStringExtra("index");
        is_desc = data.getStringExtra("is_desc");
        store_name = data.getStringExtra("store_name");
        store_id = data.getStringExtra("store_id");
        task_id = data.getStringExtra("task_id");
        task_pack_id = data.getStringExtra("task_pack_id");
        p_batch = data.getStringExtra("p_batch");
        outlet_batch = data.getStringExtra("outlet_batch");
        project_id = data.getStringExtra("project_id");
        project_name = data.getStringExtra("project_name");
        task_pack_id = data.getStringExtra("task_pack_id");
        task_pack_name = data.getStringExtra("task_pack_name");
        store_name = data.getStringExtra("store_name");
        task_name = data.getStringExtra("task_name");
        store_num = data.getStringExtra("store_num");
        brand = data.getStringExtra("brand");
        calltask_findrecode = findViewById(R.id.calltask_findrecode);
        calltask_button = findViewById(R.id.calltask_button);
        initTitle();
        initNetworkConnection();
        checkPhonePermission();
        checkCallLogPermission();
        getData();
        calltask_button.setOnClickListener(this);
//        calltask_button2 = (TextView) findViewById(R.id.calltask_button2);
//        calltask_button2.setOnClickListener(this);
//        lin_call = (LinearLayout) findViewById(R.id.lin_call);
        tv_recordingfile = (TextView) findViewById(R.id.tv_recordingfile);
//        tv_findrecordingfile = (TextView) findViewById(R.id.tv_findrecordingfile);
        findViewById(R.id.tv_findrecordingfile).setOnClickListener(this);
        lin_Upload = findViewById(R.id.lin_Upload);
        lin_Upload.setOnClickListener(this);
    }

    protected void onResume() {
        super.onResume();
        if (phoneListen != null && phoneListen.isCalled) {
            if (readCallLog()) {
                calltask_findrecode.setVisibility(View.VISIBLE);
                lin_Upload.setVisibility(View.VISIBLE);
                calltask_button.setVisibility(View.GONE);
            }
        }
    }

    private class PhoneListen extends PhoneStateListener {
        private final Context context;
        boolean isCalling;
        private ExecutorService service;
        boolean isCalled = false;

        PhoneListen(Context context) {
            this.context = context;
            service = Executors.newSingleThreadExecutor();
        }

        @Override
        public void onServiceStateChanged(ServiceState serviceState) {
            Tools.d("serviceState:" + serviceState.getState());
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            Tools.d("state:" + state);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE://挂断电话
                    if (isCalling) {
                        isCalling = false;
                        isCalled = true;
                        if (readCallLog()) {
                            calltask_findrecode.setVisibility(View.VISIBLE);
                            lin_Upload.setVisibility(View.VISIBLE);
                            calltask_button.setVisibility(View.GONE);
                        }
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK://接起电话
                    isCalling = true;
                    isCalled = false;
                    break;
                case TelephonyManager.CALL_STATE_RINGING://响铃
//                    isFinish = false;
//                    if (service.isShutdown()) {
//                        service = Executors.newSingleThreadExecutor();
//                    }
                    break;
            }
        }
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    public void getData() {
        callTask.sendPostRequest(Urls.CallTask, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        ((TextView) findViewById(R.id.calltak_name)).setText(jsonObject.getString("taskname"));
                        ((TextView) findViewById(R.id.calltak_desc)).setText(jsonObject.getString("note"));
                        telphone = jsonObject.getString("telphone");
                        batch = jsonObject.getString("batch");
                        showExit("1".equals(jsonObject.getString("invalid")));
                    } else {
                        Tools.showToast(CallTaskActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(CallTaskActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(CallTaskActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });


    }

    private boolean isCall = false;
    private PhoneListen phoneListen;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calltask_button: {
                if (index != null && "0".equals(index)) {
                    Tools.showToast(CallTaskActivity.this, "抱歉，预览时任务无法执行。");
                    return;
                }
                flag2 = "0";
                TelephonyManager phoneManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                phoneListen = new PhoneListen(this);
                phoneManager.listen(phoneListen, PhoneStateListener.LISTEN_CALL_STATE);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_CALL);
                Uri data = Uri.parse("tel:" + telphone);
                intent.setData(data);
                startActivity(intent);
                isCall = true;
            }
            break;
            case R.id.tv_findrecordingfile: {
                startActivityForResult(new Intent(this, CallfindrecodeActivity.class), CALLFINDFILE);
            }
            break;
            case R.id.lin_Upload: {
                if (tv_recordingfile.getTag() == null) {
                    Tools.showToast(CallTaskActivity.this, "请先选择录音");
                    return;
                }
                if (readCallLog()) {
                    sendData();
                } else {
                    ConfirmDialog.showDialog(CallTaskActivity.this, "未查到有效通话记录！", true, null);
                }
            }
            break;
        }
    }

    private static final int CALLFINDFILE = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CALLFINDFILE: {
                    String path = data.getStringExtra("path");
                    tv_recordingfile.setText(new File(path).getName());
                    tv_recordingfile.setTag(path);
                }
                break;
            }
        }
    }

    public void noWayExecute() {
        if (index != null && "0".equals(index)) {
            Tools.showToast(CallTaskActivity.this, "抱歉，预览时任务无法执行。");
            return;
        }
        flag2 = "1";
        if (isCall) {
            NonExecutionTask.showDialog(this, "无法执行", "无法执行说明", new NonExecutionTask.OnNonExecutionTaskDialogListener() {
                @Override
                public void sumbit(String edittext) {
                    note = edittext;
                    sendData2();
                }
            });
        } else {
            Tools.showToast(CallTaskActivity.this, "请先进行电话拨打");
        }
    }

//    public int flag = 1;

    /**
     * 查找通话记录
     */
    public boolean readCallLog() {
        boolean result = false;
        ContentResolver cr = getContentResolver();
        checkCallLogPermission();
        Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME, CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.DURATION}, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
        if (cursor != null) {
            cursor.moveToFirst();
            try {
                if (cursor.getString(0).equals(telphone)) {
                    time = cursor.getString(3);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(Long.parseLong(time));
                    time = simpleDateFormat.format(date);
                    calltime = cursor.getLong(4);
//                    if (calltime > 0 && flag == 1) {
//                        sendData();
//                    }
                    if (calltime > 0) {
                        result = true;
                    }
                }
//                else {
//                    Tools.showToast(CallTaskActivity.this, "未找到此通话的记录");
//                }
            } catch (Exception e) {
                if (!isFinishing())
                    ConfirmDialog.showDialog(CallTaskActivity.this, "您的读取通讯录权限可能没有开启，请手动开启", true, null);
                else
                    Tools.showToast(this, "您的读取通讯录权限可能没有开启，请手动开启");
            }
            cursor.close();
        } else {
            if (!isFinishing())
                ConfirmDialog.showDialog(CallTaskActivity.this, "您的读取通讯录权限可能没有开启，请手动开启", true, null);
            else
                Tools.showToast(this, "您的读取通讯录权限可能没有开启，请手动开启");
        }
        return result;
    }

    public void sendData() {
        callTaskUp.sendPostRequest(Urls.CallTaskUp, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
//                        flag++;
                        TaskitemDetailActivity.isRefresh = true;
                        TaskitemDetailActivity_12.isRefresh = true;
                        TaskFinishActivity.isRefresh = true;
                        TaskitemListActivity.isRefresh = true;
                        TaskitemListActivity_12.isRefresh = true;
                        String executeid = jsonObject.getString("executeid");
                        // 存数据库上传录音
                        String username = AppInfo.getName(CallTaskActivity.this);
                        Map<String, String> params = new HashMap<>();
                        params.put("task_id", task_id);
                        params.put("user_mobile", username);
                        params.put("status", "1");
                        params.put("executeid", executeid);
                        params.put("task_pack_id", task_pack_id);
                        params.put("storeid", store_id);
                        params.put("outlet_batch", outlet_batch);
                        params.put("p_batch", p_batch);
                        params.put("batch", batch);
                        updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, brand,
                                store_id, store_name, task_pack_id,
                                task_pack_name, "10", task_id, task_name, null, null, null,
                                username + project_id + store_id + task_pack_id + null + null + null + task_id
                                , Urls.Filecomplete, "path", tv_recordingfile.getTag() + "", UpdataDBHelper.Updata_file_type_video,
                                params, null, false, null, null, false);
                        Intent service = new Intent("com.orange.oy.UpdataNewService");
                        service.setPackage("com.orange.oy");
                        startService(service);
                        Tools.showToast(CallTaskActivity.this, "正在后台上传");
                        TaskitemDetailActivity.isRefresh = true;
                        TaskitemDetailActivity.taskid = task_id;
                        TaskitemDetailActivity_12.isRefresh = true;
                        TaskitemDetailActivity_12.taskid = task_id;
                        TaskFinishActivity.isRefresh = true;
                        TaskitemListActivity.isRefresh = true;
                        TaskitemListActivity_12.isRefresh = true;
                        CustomProgressDialog.Dissmiss();
//                        if (code == 2) {
//                            ConfirmDialog.showDialog(CallTaskActivity.this, null, jsonObject.getString("msg"), null,
//                                    "确定", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
//                                        @Override
//                                        public void leftClick(Object object) {
//
//                                        }
//
//                                        @Override
//                                        public void rightClick(Object object) {
//                                            baseFinish();
//                                        }
//                                    }).goneLeft();
//                        } else {
                        baseFinish();
//                        }
                    } else {
                        Tools.showToast(CallTaskActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(CallTaskActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(CallTaskActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    public void sendData2() {
        callTaskUp.sendPostRequest(Urls.UNCallTaskUp, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200 || code == 2) {
                        TaskitemDetailActivity.isRefresh = true;
                        TaskitemDetailActivity_12.isRefresh = true;
                        TaskFinishActivity.isRefresh = true;
                        TaskitemListActivity.isRefresh = true;
                        TaskitemListActivity_12.isRefresh = true;
                        baseFinish();
                    } else {
                        Tools.showToast(CallTaskActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(CallTaskActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(CallTaskActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AppInfo.REQUEST_CODE_ASK_CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkPhonePermission();
            } else {
                Tools.showToast(CallTaskActivity.this, "拨打电话权限获取失败");
                baseFinish();
            }
        } else if (requestCode == AppInfo.REQUEST_CODE_ASK_READ_CALL_LOG) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkCallLogPermission();
            } else {
                Tools.showToast(CallTaskActivity.this, "读取通话记录权限失败");
                baseFinish();
            }
        }
    }

    private void checkPhonePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, AppInfo
                        .REQUEST_CODE_ASK_CALL_PHONE);
            }
        }
    }

    private void checkCallLogPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG}, AppInfo
                        .REQUEST_CODE_ASK_READ_CALL_LOG);
            }
        } else {
            ContentResolver cr = getContentResolver();
            Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME, CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.DURATION}, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.getCount() == 0) {
                    ConfirmDialog.showDialog(CallTaskActivity.this, "请确认通讯录读取权限已开启！", true, null);
                }
                cursor.close();
            } else {
                ConfirmDialog.showDialog(CallTaskActivity.this, "请确认通讯录读取权限已开启！", true, null);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (NonExecutionTask.myDialog != null) {
            NonExecutionTask.myDialog.dismiss();
        }
    }
}
