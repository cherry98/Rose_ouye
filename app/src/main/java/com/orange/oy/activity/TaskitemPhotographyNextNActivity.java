package com.orange.oy.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.orange.oy.R;
import com.orange.oy.activity.experience.ExperienceTakePhotoActivity;
import com.orange.oy.activity.scan.ScanTaskNewActivity;
import com.orange.oy.activity.scan.ScanTaskillustrateActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.AppDBHelper;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.DataUploadDialog;
import com.orange.oy.info.TaskNewInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.MyImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 任务分类-拍照任务-任务说明页-信息录入页
 */
public class TaskitemPhotographyNextNActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View
        .OnClickListener {

    private void initTitle(String title) {
        AppTitle taskitempgnext_title = (AppTitle) findViewById(R.id.taskitempgnextn_title);
        taskitempgnext_title.settingName("无法执行");
        if (!"1".equals(newtask)) {//不是新手
            taskitempgnext_title.showBack(this);
        }
        if ("1".equals(is_desc)) {
            taskitempgnext_title.setIllustrate(new AppTitle.OnExitClickForAppTitle() {
                public void onExit() {
                    Intent intent = new Intent(TaskitemPhotographyNextNActivity.this, StoreDescActivity.class);
                    intent.putExtra("id", store_id);
                    intent.putExtra("store_name", store_name);
                    intent.putExtra("is_task", true);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if ("1".equals(newtask)) {
                return true;
            } else {
                if (!isBackEnable) {
                    Tools.showToast(TaskitemPhotographyNextNActivity.this, "请提交资料，稍后返回");
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onBack() {
        if (isBackEnable) {
            baseFinish();
        } else {
            Tools.showToast(TaskitemPhotographyNextNActivity.this, "请提交资料，稍后返回");
        }
    }

    protected void onStop() {
        super.onStop();
        if (Closetask != null) {
            Closetask.stop(Urls.Closetask);
        }
    }

    private void initNetworkConnection() {
        Closetask = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("pid", task_pack_id);
                params.put("taskid", task_id);
                params.put("storeid", store_id);
                params.put("note", filterEmoji(taskitempgnextn_edit.getText().toString().trim()));
                params.put("usermobile", AppInfo.getName(TaskitemPhotographyNextNActivity.this));
                if (category1 != null) {
                    params.put("category1", category1);
                }
                if (category2 != null) {
                    params.put("category2", category2);
                }
                if (category3 != null) {
                    params.put("category3", category3);
                }
                params.put("batch", batch);
                params.put("outlet_batch", outlet_batch);
                if (p_batch != null) {
                    params.put("p_batch", p_batch);
                }
                if ("1".equals(carrytype) || "2".equals(carrytype)) {
                    params.put("type", carrytype);
                    params.put("lon", latitude + "");
                    params.put("lon", longitude + "");
                    params.put("address", address);
                }
                return params;
            }
        };
        Closetask.setIsShowDialog(true);
    }

    public static String filterEmoji(String source) {//去除表情

        if (!containsEmoji(source)) {
            return source;// 如果不包含，直接返回
        }
        // 到这里铁定包含
        StringBuilder buf = null;

        int len = source.length();

        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);

            if (isEmojiCharacter(codePoint)) {
                if (buf == null) {
                    buf = new StringBuilder(source.length());
                }

                buf.append(codePoint);
            } else {
            }
        }

        if (buf == null) {
            return source;// 如果没有找到 emoji表情，则返回源字符串
        } else {
            if (buf.length() == len) {// 这里的意义在于尽可能少的toString，因为会重新生成字符串
                buf = null;
                return source;
            } else {
                return buf.toString();
            }
        }

    }

    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
                || (codePoint == 0xD)
                || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
                || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }

    /**
     * 检测是否有emoji字符
     *
     * @param source
     * @return FALSE，包含图片
     */
    public static boolean containsEmoji(String source) {
        if (source.equals("")) {
            return false;
        }

        int len = source.length();

        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);

            if (isEmojiCharacter(codePoint)) {
                // do nothing，判断到了这里表明，确认有表情字符
                return true;
            }
        }

        return false;
    }

    private String task_pack_id, task_id, store_id, task_name, task_pack_name, store_name, project_id, project_name, store_num;
    private ImageView taskitempgnextn_video2, taskitempgnextn_video3;
    private MyImageView taskitempgnextn_video1;
    private TextView taskitempgnextn_name;
    private EditText taskitempgnextn_edit;
    private NetworkConnection Closetask;
    private String category1 = "", category2 = "", category3 = "";
    private String categoryPath;
    private String is_desc;
    private String batch;
    private String codeStr, brand;
    private String outlet_batch = null, p_batch = null;
    private String newtask;
    private LocationClient locationClient = null;
    private BDLocationListener myListener = new MyLocationListener();
    private double longitude, latitude;
    private String carrytype, address;
    private AppDBHelper appDBHelper;
    private boolean isBackEnable = true;//是否可返回上一页

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitempgnext_n);
        appDBHelper = new AppDBHelper(this);
        registerReceiver(this);
        Intent data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        carrytype = data.getStringExtra("carrytype");
        if (carrytype == null) {
            carrytype = "";
        }
        if ("1".equals(carrytype)) {
            ((Button) findViewById(R.id.taskitempgnextn_button)).setText("完成");
        }
        updataDBHelper = new UpdataDBHelper(this);
        initNetworkConnection();
        project_id = data.getStringExtra("project_id");
        project_name = data.getStringExtra("project_name");
        task_id = data.getStringExtra("task_id");
        task_pack_id = data.getStringExtra("task_pack_id");
        task_pack_name = data.getStringExtra("task_pack_name");
        store_id = data.getStringExtra("store_id");
        store_name = data.getStringExtra("store_name");
        store_num = data.getStringExtra("store_num");
        task_name = data.getStringExtra("task_name");
        if (task_name == null) {
            task_name = "拍照任务";
        }
        category1 = data.getStringExtra("category1");
        category2 = data.getStringExtra("category2");
        category3 = data.getStringExtra("category3");
        is_desc = data.getStringExtra("is_desc");
        batch = data.getStringExtra("batch");
        outlet_batch = data.getStringExtra("outlet_batch");
        p_batch = data.getStringExtra("p_batch");
        codeStr = data.getStringExtra("code");
        brand = data.getStringExtra("brand");
        newtask = data.getStringExtra("newtask");
        if (TextUtils.isEmpty(batch) || batch.equals("null")) {
            batch = "1";
        }
        categoryPath = Tools.toByte(category1 + category2 + category3 + project_id);
        initTitle(task_name);
        taskitempgnextn_video1 = (MyImageView) findViewById(R.id.taskitempgnextn_video1);
        taskitempgnextn_video1.setImageResource(R.mipmap.tianjiayuxi);
        taskitempgnextn_video1.setScaleType();
        taskitempgnextn_video2 = (ImageView) findViewById(R.id.taskitempgnextn_video2);
        taskitempgnextn_video3 = (ImageView) findViewById(R.id.taskitempgnextn_video3);
        taskitempgnextn_edit = (EditText) findViewById(R.id.taskitempgnextn_edit);
        taskitempgnextn_name = (TextView) findViewById(R.id.taskitempgnextn_name);
        taskitempgnextn_name.setText(task_name);
        findViewById(R.id.taskitempgnextn_button).setOnClickListener(this);
        taskitempgnextn_video1.setOnClickListener(this);
        taskitempgnextn_video2.setOnClickListener(this);
        taskitempgnextn_video3.setOnClickListener(this);
        checkPermission();
        initLocation();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, AppInfo
                        .REQUEST_CODE_ASK_CAMERA);
                return;
            }
            checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, AppInfo
                        .REQUEST_CODE_ASK_LOCATION);
                return;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        switch (requestCode) {
            case AppInfo.REQUEST_CODE_ASK_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                } else {
                    Tools.showToast(this, "摄像头权限获取失败");
                    baseFinish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    private UpdataDBHelper updataDBHelper;
    private int locType;

    private void Closetask(final String url1, final String url2) {//Closetaskcomplete
        if (isComplete) {
            goStep();
            baseFinish();
            return;
        }
        if (!isLoading) {
            if (locType == 61 || locType == 161) {
                Closetask.sendPostRequest(url1, new Response.Listener<String>() {
                    public void onResponse(String s) {
                        Tools.d(s);
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            int code = jsonObject.getInt("code");
                            if (code == 200 || code == 2) {
                                isLoading = true;
                                String executeid = jsonObject.getString("executeid");
                                String username = AppInfo.getName(TaskitemPhotographyNextNActivity.this);
                                Map<String, String> params = new HashMap<>();
                                params.put("token", Tools.getToken());
                                params.put("task_pack_id", task_pack_id);
                                params.put("taskid", task_id);
                                params.put("executeid", executeid);
                                params.put("storeid", store_id);
                                params.put("usermobile", username);
                                params.put("note", filterEmoji(taskitempgnextn_edit.getText().toString().trim()));
                                if (category1 != null) {
                                    params.put("category1", category1);
                                }
                                if (category2 != null) {
                                    params.put("category2", category2);
                                }
                                if (category3 != null) {
                                    params.put("category3", category3);
                                }
                                params.put("outlet_batch", outlet_batch);
                                if (p_batch != null) {
                                    params.put("p_batch", p_batch);
                                }
                                if ("1".equals(carrytype) || "2".equals(carrytype)) {
                                    params.put("type", carrytype);
                                }
                                updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, brand, store_id,
                                        store_name, task_pack_id,
                                        task_pack_name, "111", task_id, task_name, category1, category2, category3,
                                        username + project_id +
                                                store_id + task_pack_id + category1 + category2 + category3 + task_id, url2,
                                        null, null, UpdataDBHelper.Updata_file_type_video, params, null,
                                        true, url1, paramsToString(), false);
                                Intent service = new Intent("com.orange.oy.UpdataNewService");
                                service.setPackage("com.orange.oy");
                                startService(service);
                                TaskitemDetailActivity.isRefresh = true;
                                TaskitemDetailActivity.taskid = task_id;
                                TaskitemDetailActivity_12.isRefresh = true;
                                TaskitemDetailActivity_12.taskid = task_id;
                                TaskFinishActivity.isRefresh = true;
                                TaskitemListActivity.isRefresh = true;
//                                if (code == 2) {
//                                    ConfirmDialog.showDialog(TaskitemPhotographyNextNActivity.this, null, jsonObject.getString("msg"), null,
//                                            "确定", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
//                                                @Override
//                                                public void leftClick(Object object) {
//
//                                                }
//
//                                                @Override
//                                                public void rightClick(Object object) {
//                                                    baseFinish();
//                                                }
//                                            }).goneLeft();
//                                } else if (code == 200) {
                                String fileurl = appDBHelper.getAllPhotoUrl(username, project_id, store_id, task_id);
                                if (fileurl != null && (appDBHelper.getPhotoUrlIsCompete(fileurl, project_id, store_id, task_id))) {
                                    goStep();
                                    baseFinish();
                                } else {
                                    selectUploadMode();
                                }
//                                }
                            } else {
                                Tools.showToast(TaskitemPhotographyNextNActivity.this, jsonObject.getString("msg"));
                            }
                        } catch (JSONException e) {
                            Tools.showToast(TaskitemPhotographyNextNActivity.this, getResources().getString(R.string
                                    .network_error));
                        }
                        CustomProgressDialog.Dissmiss();
                    }
                }, new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError volleyError) {
                        CustomProgressDialog.Dissmiss();
                        Tools.showToast(TaskitemPhotographyNextNActivity.this, getResources().getString(R.string
                                .network_volleyerror));
                    }
                }, null);
            } else if (locType == 167) {
                Tools.showToast2(TaskitemPhotographyNextNActivity.this, "请您检查是否开启权限，尝试重新请求定位");
            } else {
                Tools.showToast2(TaskitemPhotographyNextNActivity.this, "请您检查网络是否通畅或是否允许访问位置信息,尝试重新请求定位");
            }
        } else {
            selectUploadMode();
        }
    }

    private void goStep() {
        if ("1".equals(newtask)) {//新手任务
            ArrayList<TaskNewInfo> list = (ArrayList<TaskNewInfo>) getIntent().getBundleExtra("data").getSerializable("list");
            if (list != null) {
                if (!list.isEmpty()) {
                    TaskNewInfo taskNewInfo = list.remove(0);
                    String type = taskNewInfo.getTask_type();
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("list", list);
                    intent.putExtra("data", bundle);
                    intent.putExtra("project_id", taskNewInfo.getProjectid());
                    intent.putExtra("project_name", taskNewInfo.getProject_name());
                    intent.putExtra("task_pack_id", "");
                    intent.putExtra("task_pack_name", "");
                    intent.putExtra("task_id", taskNewInfo.getTask_id());
                    intent.putExtra("task_name", taskNewInfo.getTask_name());
                    intent.putExtra("store_id", taskNewInfo.getStore_id());
                    intent.putExtra("store_num", taskNewInfo.getStore_num());
                    intent.putExtra("store_name", taskNewInfo.getStore_name());
                    intent.putExtra("category1", "");
                    intent.putExtra("category2", "");
                    intent.putExtra("category3", "");
                    intent.putExtra("is_desc", "");
                    intent.putExtra("code", taskNewInfo.getCode());
                    intent.putExtra("brand", taskNewInfo.getBrand());
                    intent.putExtra("outlet_batch", taskNewInfo.getOutlet_batch());
                    intent.putExtra("p_batch", taskNewInfo.getP_batch());
                    intent.putExtra("newtask", "1");//判断是否是新手任务 1是0否
                    if ("1".equals(type)) {//拍照任务
                        intent.setClass(TaskitemPhotographyNextNActivity.this, TaskitemPhotographyNextYActivity.class);
                        startActivity(intent);
                    } else if ("2".equals(type)) {//视频任务
                        intent.setClass(TaskitemPhotographyNextNActivity.this, TaskitemShotActivity.class);
                        startActivity(intent);
                    } else if ("3".equals(type)) {//记录任务
                        intent.setClass(TaskitemPhotographyNextNActivity.this, TaskitemEditActivity.class);
                        startActivity(intent);
                    } else if ("4".equals(type)) {//定位任务
                        intent.setClass(TaskitemPhotographyNextNActivity.this, TaskitemMapActivity.class);
                        startActivity(intent);
                    } else if ("5".equals(type)) {//录音任务
                        intent.setClass(TaskitemPhotographyNextNActivity.this, TaskitemRecodillustrateActivity.class);
                        startActivity(intent);
                    } else if ("6".equals(type)) {//扫码任务
                        intent.setClass(TaskitemPhotographyNextNActivity.this, ScanTaskNewActivity.class);
                        startActivity(intent);
                    }
                }
            }
        }
        if (ExperienceTakePhotoActivity.experienceTakePhotoActivity != null) {
            ExperienceTakePhotoActivity.experienceTakePhotoActivity.finish();
        }
        if (TaskitemPhotographyNextYActivity.activity != null) {
            TaskitemPhotographyNextYActivity.activity.finish();
        }
    }

    public void selectUploadMode() {
        if (!isComplete) {
            String mode = appDBHelper.getDataUploadMode(store_id);
            Tools.d("上传模式....." + mode);
            if ("1".equals(mode)) {//弹框选择==1
                DataUploadDialog.showDialog(TaskitemPhotographyNextNActivity.this, false, new DataUploadDialog.OnDataUploadClickListener() {
                    @Override
                    public void firstClick() {
                        appDBHelper.addDataUploadRecord(store_id, "1");
                    }

                    @Override
                    public void secondClick() {
                        appDBHelper.addDataUploadRecord(store_id, "2");
                        goStep();
                        baseFinish();
                    }

                    @Override
                    public void thirdClick() {
                        appDBHelper.addDataUploadRecord(store_id, "3");
                        goStep();
                        baseFinish();
                    }
                });
            } else if ("2".equals(mode)) {//弹框选择===2
                DataUploadDialog.showDialog(TaskitemPhotographyNextNActivity.this, false, new DataUploadDialog.OnDataUploadClickListener() {
                    @Override
                    public void firstClick() {
                        appDBHelper.addDataUploadRecord(store_id, "1");
                    }

                    @Override
                    public void secondClick() {
                        appDBHelper.addDataUploadRecord(store_id, "2");
                        goStep();
                        baseFinish();
                    }

                    @Override
                    public void thirdClick() {
                        appDBHelper.addDataUploadRecord(store_id, "3");
                        goStep();
                        baseFinish();
                    }
                });
            } else if ("3".equals(mode)) {//直接关闭
                appDBHelper.addDataUploadRecord(store_id, "3");
                goStep();
                baseFinish();
            }
        }
    }

    private String paramsToString() {
        Map<String, String> params = new HashMap<>();
        params.put("token", Tools.getToken());
        params.put("pid", task_pack_id);
        params.put("taskid", task_id);
        params.put("storeid", store_id);
        params.put("note", filterEmoji(taskitempgnextn_edit.getText().toString().trim()));
        params.put("usermobile", AppInfo.getName(TaskitemPhotographyNextNActivity.this));
        if (category1 != null) {
            params.put("category1", category1);
        }
        if (category2 != null) {
            params.put("category2", category2);
        }
        if (category3 != null) {
            params.put("category3", category3);
        }
        params.put("batch", batch);
        params.put("outlet_batch", outlet_batch);
        if (p_batch != null) {
            params.put("p_batch", p_batch);
        }
        if ("1".equals(carrytype) || "2".equals(carrytype)) {
            params.put("type", carrytype);
            params.put("lon", latitude + "");
            params.put("lon", longitude + "");
            params.put("address", address);
        }
        String data = "";
        Iterator<String> iterator = params.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (TextUtils.isEmpty(data)) {
                try {
                    data = key + "=" + URLEncoder.encode(params.get(key).trim(), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    data = key + "=" + params.get(key).trim();
                }
            } else {
                try {
                    data = data + "&" + key + "=" + URLEncoder.encode(params.get(key).trim(), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    data = data + "&" + key + "=" + params.get(key).trim();
                }
            }
        }
        return data;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taskitempgnextn_video1: {
                Intent intent = new Intent(this, ShotActivity.class);
                intent.putExtra("index", 1);
                intent.putExtra("fileName", Tools.getTimeSS() + Tools.getDeviceId(TaskitemPhotographyNextNActivity.this) +
                        task_id + 1);
                intent.putExtra("dirName", AppInfo.getName(this) + "/" + project_id + store_id + task_pack_id + task_id +
                        categoryPath);
                startActivityForResult(intent, AppInfo.TaskitemPhotogpnCodeForShot);
            }
            break;
            case R.id.taskitempgnextn_video2: {
                Intent intent = new Intent(this, ShotActivity.class);
                intent.putExtra("index", 2);
                intent.putExtra("fileName", Tools.getTimeSS() + Tools.getDeviceId(TaskitemPhotographyNextNActivity.this) +
                        task_id + 2);
                intent.putExtra("dirName", AppInfo.getName(this) + "/" + project_id + store_id + task_pack_id + task_id +
                        categoryPath);
                startActivityForResult(intent, AppInfo.TaskitemPhotogpnCodeForShot);
            }
            break;
            case R.id.taskitempgnextn_video3: {
                Intent intent = new Intent(this, ShotActivity.class);
                intent.putExtra("index", 3);
                intent.putExtra("fileName", Tools.getTimeSS() + Tools.getDeviceId(TaskitemPhotographyNextNActivity.this) +
                        task_id + 3);
                intent.putExtra("dirName", AppInfo.getName(this) + "/" + project_id + store_id + task_pack_id + task_id +
                        categoryPath);
                startActivityForResult(intent, AppInfo.TaskitemPhotogpnCodeForShot);
            }
            break;
            case R.id.taskitempgnextn_button: {
                if (TextUtils.isEmpty(taskitempgnextn_edit.getText().toString().trim())) {
                    Tools.showToast(this, "请填写备注");
                    return;
                }
                if (taskitempgnextn_video3.getTag() == null && taskitempgnextn_video2.getTag() == null &&
                        taskitempgnextn_video1.getTag() == null) {
                    Tools.showToast(this, "请拍摄视频");
                    return;
                }
                if ("1".equals(carrytype)) {//开始体验
                    stopService(new Intent("com.orange.oy.recordservice").setPackage("com.orange.oy"));
                    Closetask(Urls.CloseExperienceTask1, Urls.ExperienceFileComplete1);
                } else if ("2".equals(carrytype)) {//我已离店
                    Closetask(Urls.CloseExperienceTask, Urls.ExperienceFileComplete);
                } else {
                    Closetask(Urls.Closetask, Urls.Closetaskcomplete);
                }
            }
            break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case AppInfo.TaskitemPhotogpnCodeForShot: {
                if (resultCode == AppInfo.ShotSuccessResultCode) {
                    appDBHelper.deletePhotoUrl(project_id, store_id, task_id);
                    isBackEnable = false;
                    int index = data.getIntExtra("index", 0);
                    String path = data.getStringExtra("path");
                    switch (index) {
                        case 1: {
                            taskitempgnextn_video1.setOnClickListener(null);
                            taskitempgnextn_video1.setAlpha(0.4f);
                            taskitempgnextn_video1.setmImageThumbnail(path);
                            taskitempgnextn_video1.setTag(path);
                            upDataVideo(path);
                        }
                        break;
//                        case 2: {
//                            taskitempgnextn_video2.setOnClickListener(null);
//                            taskitempgnextn_video2.setAlpha(0.3f);
//                            taskitempgnextn_video2.setImageBitmap(Tools.createVideoThumbnail(path));
//                            taskitempgnextn_video2.setTag(path);
//                            upDataVideo(path);
//                        }
//                        break;
//                        case 3: {
//                            taskitempgnextn_video3.setOnClickListener(null);
//                            taskitempgnextn_video3.setAlpha(0.3f);
//                            taskitempgnextn_video3.setImageBitmap(Tools.createVideoThumbnail(path));
//                            taskitempgnextn_video3.setTag(path);
//                            upDataVideo(path);
//                        }
//                        break;
                    }
                }
            }
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppInfo.BroadcastReceiver_TAKEPHOTO);
        context.registerReceiver(takePhotoBroadcastReceiver, filter);
    }

    private void unregisterReceiver(Context context) {
        context.unregisterReceiver(takePhotoBroadcastReceiver);
    }

    private boolean isComplete = false;
    private boolean isLoading = false;
    private BroadcastReceiver takePhotoBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent in) {
            String type2 = in.getStringExtra("type");
            if ("1".equals(type2)) {//可更新UI
                String path = in.getStringExtra("path");
                if (taskitempgnextn_video1.getTag() != null) {
                    if (taskitempgnextn_video1.getTag().toString().equals(path)) {
                        String rate = in.getStringExtra("rate");
                        if ("0".equals(rate)) {
                            taskitempgnextn_video1.setText(rate + "%" + "\n等待上传");
                        } else if ("100".equals(rate)) {
                            taskitempgnextn_video1.setText(rate + "%" + "\n上传完成");
                        } else {
                            taskitempgnextn_video1.setText(rate + "%" + "\n正在上传");
                        }
                    }
                }
            } else if ("2".equals(type2) && !isBackEnable) {//资料回收完成
                isComplete = true;
            }

        }
    };

    private void upDataVideo(String path) {
        String username = AppInfo.getName(TaskitemPhotographyNextNActivity.this);
        updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, brand,
                store_id, store_name, task_pack_id,
                task_pack_name, "11", task_id, task_name, "", "", "",
                username + project_id +
                        store_id + task_pack_id + task_id, null,
                "key", path, UpdataDBHelper.Updata_file_type_video,
                null, null, false, null, paramsToString(), false);
        appDBHelper.addPhotoUrlRecord(username, project_id, store_id, task_id, path, null);
        appDBHelper.setFileNum(path, "1");
        Intent service = new Intent("com.orange.oy.UpdataNewService");
        service.setPackage("com.orange.oy");
        startService(service);
    }

    /**
     * 初始化定位
     */

    private void initLocation() {
        if (locationClient != null && locationClient.isStarted()) {
            Tools.showToast(TaskitemPhotographyNextNActivity.this, "正在定位...");
            return;
        }
        locationClient = new LocationClient(this);
        locationClient.registerLocationListener(myListener);
        setLocationOption();
        locationClient.start();
    }

    public void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(1000);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps
        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        locationClient.setLocOption(option);
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            locationClient.stop();
            if (bdLocation == null) {
                Tools.showToast(TaskitemPhotographyNextNActivity.this, getResources().getString(R.string.location_fail));
                return;
            }
            latitude = bdLocation.getLatitude();
            longitude = bdLocation.getLongitude();
            address = bdLocation.getAddrStr();
            locType = bdLocation.getLocType();
        }

        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this);
        DataUploadDialog.dissmisDialog();
    }
}
