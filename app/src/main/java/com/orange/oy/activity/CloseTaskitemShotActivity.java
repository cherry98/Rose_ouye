package com.orange.oy.activity;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.AppDBHelper;
import com.orange.oy.db.OfflineDBHelper;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.DataUploadDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.CollapsibleTextView;
import com.orange.oy.view.MyImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;



/**
 * 任务列表-录制任务页
 */
public class CloseTaskitemShotActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View
        .OnClickListener {

    private void initTitle() {
        AppTitle taskitemshot_title = (AppTitle) findViewById(R.id.taskitemshot_title);
        if (indextwo != null && "0".equals(indextwo)) {
            taskitemshot_title.settingName("视频任务（预览）");
        } else {
            taskitemshot_title.settingName("视频任务");
        }
        taskitemshot_title.showBack(this);
        if ("1".equals(is_desc)) {
            taskitemshot_title.setIllustrate(new AppTitle.OnExitClickForAppTitle() {
                public void onExit() {
                    Intent intent = new Intent(CloseTaskitemShotActivity.this, StoreDescActivity.class);
                    intent.putExtra("id", store_id);
                    intent.putExtra("store_name", store_name);
                    intent.putExtra("is_task", true);
                    startActivity(intent);
                }
            });
        }
    }

    protected void onStop() {
        super.onStop();
    }

    private void initNetworkConnection() {
        Closepackagetask = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("tasktype", "2");
                params.put("taskid", task_id);
                params.put("token", Tools.getToken());
                return params;
            }
        };
        Closepackagetask.setIsShowDialog(true);
        Closepackage = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> closeMap = new HashMap<>();
                closeMap.put("token", Tools.getToken());
                closeMap.put("pid", task_pack_id);
                closeMap.put("pname", task_pack_name);
                closeMap.put("storeid", store_id);
                closeMap.put("storename", store_name);
                closeMap.put("note", taskitemshot_edit.getText().toString().trim());
                closeMap.put("outlet_batch", outlet_batch);
                closeMap.put("p_batch", p_batch);
                closeMap.put("taskid", task_id);
                return closeMap;
            }
        };
        Closepackage.setIsShowDialog(true);
    }

    private MyImageView taskitemshot_video1;
    private ImageView taskitemshot_shotimg;
    private String task_pack_id, task_id, store_id, task_name, task_pack_name, store_name, project_id, project_name, store_num;
    private NetworkConnection Closepackage, Closepackagetask;
    private TextView taskitemshot_name, taskitemshot_video_title, tv_nowsave, tv_savetime, itemapplyone_runnow;
    private EditText taskitemshot_edit;
    private String is_desc;
    private String codeStr, brand;
    private String outlet_batch, p_batch, indextwo;
    private boolean isOffline;
    private OfflineDBHelper offlineDBHelper;
    private AppDBHelper appDBHelper;
    private boolean isBackEnable = true;//是否可返回上一页
    private TextView taskitemshot_desc;  //// TODO: 2018/3/29 视频上面的话
    private ImageView spread_button;
    private LinearLayout lin_bottom;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitemshot);
        initNetworkConnection();
        appDBHelper = new AppDBHelper(this);
        updataDBHelper = new UpdataDBHelper(this);
        offlineDBHelper = new OfflineDBHelper(this);
        registerReceiver(this);
        Intent data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        project_id = data.getStringExtra("project_id");
        project_name = data.getStringExtra("project_name");
        task_pack_id = data.getStringExtra("task_pack_id");
        task_id = data.getStringExtra("task_id");
        store_id = data.getStringExtra("store_id");
        store_num = data.getStringExtra("store_num");
        task_pack_name = data.getStringExtra("task_pack_name");
        store_name = data.getStringExtra("store_name");
        task_name = data.getStringExtra("task_name");

        if (TextUtils.isEmpty(task_name) || "null".equals(task_name)) {
            task_name = "视频任务";
        }
        indextwo = data.getStringExtra("index");  //是否是预览

        is_desc = data.getStringExtra("is_desc");
        codeStr = data.getStringExtra("code");
        brand = data.getStringExtra("brand");
        outlet_batch = data.getStringExtra("outlet_batch");
        p_batch = data.getStringExtra("p_batch");
        isOffline = data.getBooleanExtra("isOffline", false);
        appDBHelper.deletePhotoUrl(project_id, store_id, task_id);
        initTitle();
        taskitemshot_video1 = (MyImageView) findViewById(R.id.taskitemshot_video1);
        taskitemshot_video1.setImageResource(R.mipmap.tianjiayuxi);
        taskitemshot_video1.setScaleType();
        taskitemshot_name = (TextView) findViewById(R.id.taskitemshot_name);
        taskitemshot_shotimg = (ImageView) findViewById(R.id.taskitemshot_shotimg);
        taskitemshot_edit = (EditText) findViewById(R.id.taskitemshot_edit);
        taskitemshot_video1.setOnClickListener(this);
        taskitemshot_desc = (TextView) findViewById(R.id.taskitemshot_desc);
        spread_button = (ImageView) findViewById(R.id.spread_button);
        tv_nowsave = (TextView) findViewById(R.id.tv_nowsave);
        itemapplyone_runnow = (TextView) findViewById(R.id.itemapplyone_runnow);
        tv_savetime = (TextView) findViewById(R.id.tv_savetime);
        lin_bottom= (LinearLayout) findViewById(R.id.lin_bottom);


        if (indextwo != null && "0".equals(indextwo)) {
            lin_bottom.setVisibility(View.GONE);
            findViewById(R.id.lin_alls).setVisibility(View.GONE);
        } else {
            findViewById(R.id.lin_alls).setVisibility(View.VISIBLE);
            lin_bottom.setVisibility(View.VISIBLE);
        }

        findViewById(R.id.taskitemshot_button).setOnClickListener(this);
        findViewById(R.id.taskitemshot_shot_play).setOnClickListener(this);
        findViewById(R.id.taskitemshot_button2).setOnClickListener(this);
        findViewById(R.id.spread_button_layout).setOnClickListener(this);
        Selectvideo();
        getAvailMemory();
        itemapplyone_runnow.setVisibility(View.GONE);
    }


    // 获取android当前可用内存大小
    private String getAvailMemory() {

        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        //mi.availMem; 当前系统的可用内存
        return Formatter.formatFileSize(getBaseContext(), mi.availMem);// 将获取的内存大小规格化

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (!isBackEnable) {
                Tools.showToast(CloseTaskitemShotActivity.this, "请提交资料，稍后返回");
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onBack() {
        if (isBackEnable) {
            baseFinish();
        } else {
            Tools.showToast(CloseTaskitemShotActivity.this, "请提交资料，稍后返回");
        }
    }

    private void Selectvideo() {
        Closepackagetask.sendPostRequest(Urls.Closepackagetask, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        String task_name = jsonObject.getString("task_name");
                        taskitemshot_name.setText(task_name);
                        String url = jsonObject.getString("video_url");
                        if (TextUtils.isEmpty(url) || url.equals("null")) {
                            findViewById(R.id.taskitemshot_video_title).setVisibility(View.GONE);
                            findViewById(R.id.taskitemshot_video_layout).setVisibility(View.GONE);
                            taskitemshot_shotimg.setTag("");
                        } else {
                            taskitemshot_shotimg.setTag(url);
                        }

                        new getVideoThumbnail().execute(new Object[]{taskitemshot_shotimg.getTag()});
                        TextView taskitemshot_desc = (TextView) findViewById(R.id.taskitemshot_desc);
                        taskitemshot_desc.setText(jsonObject.getString("task_note"));
                        // taskitemshot_desc.setText("该片讲述了一对男女在过年回家的火车上相识，从那之后，二人的命运便纠缠在一起，历经恋爱、分手、错过、重逢的故事该片讲述了一对男女在过年回家的火车上相识，从那之后，二人的命运便纠缠在一起，历经恋爱、分手、错过、重逢的故事该片讲述了一对男女在过年回家的火车上相识，从那之后，二人的命运便纠缠在一起，历经恋爱、分手、错过、重逢的故事该片讲述了一对男女在过年回家的火车上相识，从那之后，二人的命运便纠缠在一起，历经恋爱、分手、错过、重逢的故事");

                        if (taskitemshot_desc.getLineCount() > 1) {
                            taskitemshot_desc.setSingleLine(true);
                            findViewById(R.id.spread_button_layout).setOnClickListener(CloseTaskitemShotActivity.this);
                            findViewById(R.id.spread_button_layout).setVisibility(View.VISIBLE);
                            findViewById(R.id.taskitemshot_video_title).setVisibility(View.GONE);
                            findViewById(R.id.taskitemshot_video_layout).setVisibility(View.GONE);
                            if (indextwo != null && "0".equals(indextwo))
                                onClick(findViewById(R.id.spread_button_layout));
                        } else {
                            findViewById(R.id.spread_button_layout).setVisibility(View.GONE);
                            findViewById(R.id.taskitemshot_video_title).setVisibility(View.GONE);
                            findViewById(R.id.taskitemshot_video_layout).setVisibility(View.GONE);
                        }

                    } else {
                        Tools.showToast(CloseTaskitemShotActivity.this, jsonObject.getString("msg"));
                    }
                    CustomProgressDialog.Dissmiss();
                } catch (JSONException e) {
                    CustomProgressDialog.Dissmiss();
                    Tools.showToast(CloseTaskitemShotActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(CloseTaskitemShotActivity.this, getResources().getString(R.string.network_error));
            }
        });
    }

    class getVideoThumbnail extends AsyncTask {
        protected Object doInBackground(Object[] params) {
            try {
                String url = params[0].toString();
                return Tools.createVideoThumbnail(url, 400, 300);
            } catch (Exception e) {
                return null;
            }
        }

        protected void onPostExecute(Object o) {
            if (o != null) {
                if (taskitemshot_shotimg != null) {
                    taskitemshot_shotimg.setImageBitmap((Bitmap) o);
                }
            }
        }
    }

    private int totoll = 1;

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taskitemshot_video1: {
                Intent intent = new Intent(this, ShotActivity.class);
                intent.putExtra("index", 1);
                intent.putExtra("fileName", Tools.getTimeSS() + Tools.getDeviceId(CloseTaskitemShotActivity.this) + task_id);
                intent.putExtra("dirName", AppInfo.getName(this) + "/" + project_id + store_id + task_pack_id + task_id);
                startActivityForResult(intent, AppInfo.TaskitemShotRequestCodeForShot);
            }
            break;
            case R.id.taskitemshot_button: {
                TaskitemDetailActivity_12.isRefresh = false;
                if (taskitemshot_video1.getTag() != null) {
                    String tag = taskitemshot_video1.getTag().toString();
                    if (tag != null && !"null".equals(tag)) {
                        sendData();
                    } else {
                        Tools.showToast(CloseTaskitemShotActivity.this, "视频录制失败，请重新录制！");
                    }
                } else {
                    Tools.showToast(CloseTaskitemShotActivity.this, "请录制视频");
                }
            }
            break;

            case R.id.taskitemshot_shot_play: {
                Intent intent = new Intent(this, VideoViewActivity.class);
                intent.putExtra("path", taskitemshot_shotimg.getTag().toString());
                startActivity(intent);
            }
            break;

            case R.id.spread_button_layout:
                //初始状态,  totoll == 1是展开
                if (totoll == 1) {
                    spread_button.setImageResource(R.mipmap.spread_button_up);
                    taskitemshot_desc.setSingleLine(false);
                    if (!TextUtils.isEmpty(taskitemshot_shotimg.getTag().toString())) {
                        findViewById(R.id.taskitemshot_video_title).setVisibility(View.VISIBLE);
                        findViewById(R.id.taskitemshot_video_layout).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.taskitemshot_video_title).setVisibility(View.GONE);
                        findViewById(R.id.taskitemshot_video_layout).setVisibility(View.GONE);
                    }
                    totoll = 2;
                } else {
                    //收缩
                    totoll = 1;
                    if (taskitemshot_desc.getLineCount() > 1) {
                        spread_button.setImageResource(R.mipmap.spread_button_down);
                        taskitemshot_desc.setSingleLine(true);
                        findViewById(R.id.taskitemshot_video_title).setVisibility(View.GONE);
                        findViewById(R.id.taskitemshot_video_layout).setVisibility(View.GONE);
                    }
                }

                break;
        }

    }


    private UpdataDBHelper updataDBHelper;

    private void sendDataOffline() {
        String username = AppInfo.getName(CloseTaskitemShotActivity.this);
        Map<String, String> closeMap = new HashMap<String, String>();
        closeMap.put("token", Tools.getToken());
        closeMap.put("pid", task_pack_id);
        closeMap.put("pname", task_pack_name);
        closeMap.put("storeid", store_id);
        closeMap.put("storename", store_name);
        closeMap.put("note", taskitemshot_edit.getText().toString().trim());
        closeMap.put("outlet_batch", outlet_batch);
        closeMap.put("p_batch", p_batch);
        closeMap.put("taskid", task_id);
        updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, brand,
                store_id, store_name, task_pack_id,
                task_pack_name, "111", task_id, task_name, null, null, null,
                username + project_id + store_id + task_pack_id, Urls.Closepackagecomplete,
                null, null, UpdataDBHelper.Updata_file_type_img, closeMap, null,
                true, Urls.Closepackage, paramsToString(), true);
        if (isOffline) {
            offlineDBHelper.closePackage(username, project_id, store_id, task_pack_id);
        }
        Intent service = new Intent("com.orange.oy.UpdataNewService");
        service.setPackage("com.orange.oy");
        startService(service);
        baseFinish();
    }


    private void sendData() {
        if (isComplete) {
            baseFinish();
            return;
        }
        if (!isLoading) {
            Closepackage.sendPostRequest(Urls.Closepackage, new Response.Listener<String>() {
                public void onResponse(String s) {
                    Tools.d(s);
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        int code = jsonObject.getInt("code");
                        if (code == 200 || code == 2) {
                            isLoading = true;
                            String username = AppInfo.getName(CloseTaskitemShotActivity.this);
                            Map<String, String> closeMap = new HashMap<String, String>();
                            closeMap.put("token", Tools.getToken());
                            closeMap.put("pid", task_pack_id);
                            closeMap.put("pname", task_pack_name);
                            closeMap.put("storeid", store_id);
                            closeMap.put("storename", store_name);
                            closeMap.put("note", taskitemshot_edit.getText().toString().trim());
                            closeMap.put("outlet_batch", outlet_batch);
                            closeMap.put("p_batch", p_batch);
                            closeMap.put("taskid", task_id);
                            updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, brand,
                                    store_id, store_name, task_pack_id,
                                    task_pack_name, "111", task_id, task_name, null, null, null,
                                    username + project_id + store_id + task_pack_id, Urls.Closepackagecomplete,
                                    null, null, UpdataDBHelper.Updata_file_type_img, closeMap, null,
                                    true, Urls.Closepackage, paramsToString(), false);
                            if (isOffline) {
                                offlineDBHelper.closePackage(username, project_id, store_id, task_pack_id);
                            }
                            Intent service = new Intent("com.orange.oy.UpdataNewService");
                            service.setPackage("com.orange.oy");
                            startService(service);
                            TaskitemDetailActivity.isRefresh = true;
                            TaskitemDetailActivity_12.isRefresh = true;
                            TaskitemDetailActivity.taskid = task_id;
                            TaskitemDetailActivity_12.taskid = task_id;
                            TaskFinishActivity.isRefresh = true;
                            TaskitemListActivity.isRefresh = true;
                            OfflineStoreActivity.isRefresh = true;
//                            if (code == 2) {
//                                ConfirmDialog.showDialog(CloseTaskitemShotActivity.this, null, jsonObject.getString("msg"), null,
//                                        "确定", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
//                                            @Override
//                                            public void leftClick(Object object) {
//
//                                            }
//
//                                            @Override
//                                            public void rightClick(Object object) {
//                                                baseFinish();
//                                            }
//                                        }).goneLeft();
//                            } else if (code == 200) {
                            String fileurl = appDBHelper.getAllPhotoUrl(username, project_id, store_id, task_id);
                            if (fileurl != null && (appDBHelper.getPhotoUrlIsCompete(fileurl, project_id, store_id, task_id))) {
                                baseFinish();
                            } else {
                                selectUploadMode();
                            }
//                            }
                        } else {
                            Tools.showToast(CloseTaskitemShotActivity.this, jsonObject.getString("msg"));
                        }
                    } catch (JSONException e) {
                        Tools.showToast(CloseTaskitemShotActivity.this, getResources().getString(R.string.network_error));
                    }
                    CustomProgressDialog.Dissmiss();
                }
            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError volleyError) {
                    CustomProgressDialog.Dissmiss();
                    Tools.showToast(CloseTaskitemShotActivity.this, getResources().getString(R.string.network_volleyerror));
                }
            }, "执行中...");
        } else {
            selectUploadMode();
        }
    }

    private void selectUploadMode() {
        if (!isComplete) {
            String mode = appDBHelper.getDataUploadMode(store_id);
            Tools.d("上传模式....." + mode);
            if ("1".equals(mode)) {//弹框选择==1
                DataUploadDialog.showDialog(CloseTaskitemShotActivity.this, false, new DataUploadDialog.OnDataUploadClickListener() {
                    @Override
                    public void firstClick() {
                        appDBHelper.addDataUploadRecord(store_id, "1");
                    }

                    @Override
                    public void secondClick() {
                        appDBHelper.addDataUploadRecord(store_id, "2");
                        baseFinish();
                    }

                    @Override
                    public void thirdClick() {
                        appDBHelper.addDataUploadRecord(store_id, "3");
                        baseFinish();
                    }
                });
            } else if ("2".equals(mode)) {//弹框选择===2
                DataUploadDialog.showDialog(CloseTaskitemShotActivity.this, false, new DataUploadDialog.OnDataUploadClickListener() {
                    @Override
                    public void firstClick() {
                        appDBHelper.addDataUploadRecord(store_id, "1");
                    }

                    @Override
                    public void secondClick() {
                        appDBHelper.addDataUploadRecord(store_id, "2");
                        baseFinish();
                    }

                    @Override
                    public void thirdClick() {
                        appDBHelper.addDataUploadRecord(store_id, "3");
                        baseFinish();
                    }
                });
            } else if ("3".equals(mode)) {//直接关闭
                appDBHelper.addDataUploadRecord(store_id, "3");
                baseFinish();
            }
        }
    }

    private String paramsToString() {
        Map<String, String> closeMap = new HashMap<>();
        closeMap.put("token", Tools.getToken());
        closeMap.put("pid", task_pack_id);
        closeMap.put("pname", task_pack_name);
        closeMap.put("storeid", store_id);
        closeMap.put("storename", store_name);
        closeMap.put("note", taskitemshot_edit.getText().toString().trim());
        closeMap.put("outlet_batch", outlet_batch);
        closeMap.put("p_batch", p_batch);
        closeMap.put("taskid", task_id);
        String data = "";
        Iterator<String> iterator = closeMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (TextUtils.isEmpty(data)) {
                try {
                    data = key + "=" + URLEncoder.encode(closeMap.get(key).trim(), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    data = key + "=" + closeMap.get(key).trim();
                }
            } else {
                try {
                    data = data + "&" + key + "=" + URLEncoder.encode(closeMap.get(key).trim(), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    data = data + "&" + key + "=" + closeMap.get(key).trim();
                }
            }
        }
        return data;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppInfo.TaskitemShotRequestCodeForShot: {
                if (resultCode == AppInfo.ShotSuccessResultCode) {
                    isBackEnable = false;
                    taskitemshot_video1.setOnClickListener(null);
                    int index = data.getIntExtra("index", 0);
                    String path = data.getStringExtra("path");
                    if (index == 1) {
                        taskitemshot_video1.setAlpha(0.4f);
                        taskitemshot_video1.setmImageThumbnail(path);
                        taskitemshot_video1.setTag(path);
                        upDataVideo(path);
                    }
                }
            }
            break;
        }
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
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("type");
            if ("1".equals(type)) {//可更新UI
                String path = intent.getStringExtra("path");
                if (taskitemshot_video1.getTag() != null) {
                    if (taskitemshot_video1.getTag().toString().equals(path)) {
                        String rate = intent.getStringExtra("rate");
                        if ("0".equals(rate)) {
                            taskitemshot_video1.setText(rate + "%" + "\n等待上传");
                        } else if ("100".equals(rate)) {
                            taskitemshot_video1.setText(rate + "%" + "\n上传完成");
                        } else {
                            taskitemshot_video1.setText(rate + "%" + "\n正在上传");
                        }
                    }
                }
            } else if ("2".equals(type) && !isBackEnable) {//资料回收完成
                isComplete = true;
            }
        }
    };

    private void upDataVideo(String path) {
        String username = AppInfo.getName(CloseTaskitemShotActivity.this);
        updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, brand,
                store_id, store_name, task_pack_id,
                task_pack_name, "11", task_id, task_name, "", "", "",
                username + project_id +
                        store_id + task_pack_id + task_id + "1", null,
                "key", path, UpdataDBHelper.Updata_file_type_video,
                null, null, false, null, paramsToString(), false);
        appDBHelper.addPhotoUrlRecord(username, project_id, store_id, task_id, path, null);
        appDBHelper.setFileNum(path, "1");
        Intent service = new Intent("com.orange.oy.UpdataNewService");
        service.setPackage("com.orange.oy");
        startService(service);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataUploadDialog.dissmisDialog();
        unregisterReceiver(this);
    }
}
