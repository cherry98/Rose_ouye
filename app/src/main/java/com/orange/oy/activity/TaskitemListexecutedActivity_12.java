package com.orange.oy.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.activity.calltask.CallTaskResetActivity;
import com.orange.oy.activity.createtask_321.ScreenshotActivity;
import com.orange.oy.activity.scan.ScanTaskResetActivity;
import com.orange.oy.adapter.TaskitemListAdapter_12;
import com.orange.oy.allinterface.NewOnItemClickListener;
import com.orange.oy.allinterface.OnRightClickListener;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.SystemDBHelper;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.TaskitemDetailNewInfo;
import com.orange.oy.info.TaskitemListInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * 0
 * 任务列表_执行完成的任务包列表
 */
public class TaskitemListexecutedActivity_12 extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View
        .OnClickListener {
    private void initTitle(String str) {
        AppTitle taskitemlist_title = (AppTitle) findViewById(R.id.taskitemlist_title);
        taskitemlist_title.settingName((!TextUtils.isEmpty(str)) ? str : "任务列表");
        taskitemlist_title.showBack(this);
    }

    public void onBack() {
        baseFinish();
    }

    protected void onStop() {
        super.onStop();
        if (Tasklistcomplete != null) {
            Tasklistcomplete.stop(Urls.Tasklist);
        }
        if (Packagecomplete != null) {
            Packagecomplete.stop(Urls.Packagecomplete);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this);
    }

    private void initNetworkConnection() {
        Tasklistcomplete = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("pid", task_pack_id);
                params.put("storeid", store_id);
                params.put("token", Tools.getToken());
                params.put("outlet_batch", outlet_batch);
                params.put("p_batch", p_batch);
                return params;
            }
        };
        Packagecomplete = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("storeid", store_id);
                params.put("pid", task_pack_id);
                params.put("usermobile", AppInfo.getUserName(TaskitemListexecutedActivity_12.this));
                params.put("token", Tools.getToken());
                params.put("outlet_batch", outlet_batch);
                params.put("p_batch", p_batch);
                return params;
            }
        };
        Packagecomplete.setIsShowDialog(true);
        taskFinish = new NetworkConnection(this) {//单个任务查看详情
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("storeid", store_id);
                params.put("token", Tools.getToken());
                params.put("pid", task_pack_id);
                params.put("p_batch", p_batch);
                params.put("outlet_batch", outlet_batch);
                params.put("taskid", task_id);
                return params;
            }
        };
    }

    private TaskitemListAdapter_12 taskitemListAdapter;
    private PullToRefreshListView taskitemlist_listview;
    private ArrayList<TaskitemListInfo> list;
    private TextView taskitemlist_package_name;
    private NetworkConnection Packagecomplete, Tasklistcomplete, taskFinish;
    private String task_pack_id, store_id, pack_name, store_name, project_id, project_name, task_id, task_name;
    private int fill_num;
    private boolean isCategory;
    private String category1 = "", category2 = "", category3 = "";
    private String photo_compression;
    private String store_num;
    private String is_desc;
    private String code, brand;
    private String is_watermark;
    private String outlet_batch, p_batch;
    private static final int TakeRequest = 0x100;
    private SystemDBHelper systemDBHelper;
    private ArrayList<String> selectImgList = new ArrayList<>();
    private ArrayList<String> originalImgList = new ArrayList<>();
    private String username;
    private UpdataDBHelper updataDBHelper;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitemlist);
        registerReceiver(this);
        username = AppInfo.getName(this);
        updataDBHelper = new UpdataDBHelper(this);
        initNetworkConnection();
        Intent data = getIntent();
        systemDBHelper = new SystemDBHelper(this);
        initTitle(data.getStringExtra("pack_name"));
        project_id = data.getStringExtra("project_id");
        project_name = data.getStringExtra("project_name");
        task_pack_id = data.getStringExtra("task_pack_id");
        pack_name = data.getStringExtra("pack_name");
        store_id = data.getStringExtra("store_id");
        store_name = data.getStringExtra("store_name");
        store_num = data.getStringExtra("store_num");
        isCategory = data.getBooleanExtra("isCategory", false);
        photo_compression = data.getStringExtra("photo_compression");
        is_desc = data.getStringExtra("is_desc");
        code = data.getStringExtra("code");
        brand = data.getStringExtra("brand");
        is_watermark = data.getStringExtra("is_watermark");
        outlet_batch = data.getStringExtra("outlet_batch");
        p_batch = data.getStringExtra("p_batch");
        fill_num = data.getIntExtra("fill_num", 0);
        taskitemlist_listview = (PullToRefreshListView) findViewById(R.id.taskitemlist_listview);
        taskitemlist_listview.setCanDelete(true);
        taskitemlist_package_name = (TextView) findViewById(R.id.taskitemlist_package_name);
        findViewById(R.id.taskitemlist_package).setOnClickListener(this);
        findViewById(R.id.taskitemlist_package_button).setOnClickListener(this);
        list = new ArrayList<>();
        taskitemListAdapter = new TaskitemListAdapter_12(this, taskitemlist_listview, list);
        taskitemListAdapter.settingRightText("补拍");
        taskitemListAdapter.isShowProgressbar(true);
        taskitemListAdapter.setNewOnItemClickListener(newOnItemClickListener);
        taskitemListAdapter.setOnRightClickListener(new OnRightClickListener() {
            public void onRightClick(Object object) {//连拍按钮
                if (object == null) {
                    Tools.showToast(TaskitemListexecutedActivity_12.this, "异常，请清理内存后重新打开页面");
                    return;
                }
                TaskitemListInfo taskitemListInfo = (TaskitemListInfo) object;
                task_id = taskitemListInfo.getTask_id();
                task_name = taskitemListInfo.getTaskname();
                int size = systemDBHelper.getPhotoNumFortaskstate4(username, project_id, store_id, task_pack_id, "");
                if (size < taskitemListInfo.getMaxTask() - taskitemListInfo.getFill_num()) {
                    Intent intent = new Intent(TaskitemListexecutedActivity_12.this, Camerase.class);
                    intent.putExtra("projectid", project_id);
                    intent.putExtra("storeid", store_id);
                    intent.putExtra("storecode", store_num);
                    intent.putExtra("packageid", task_pack_id);
                    intent.putExtra("taskid", taskitemListInfo.getTask_id());
                    Tools.d(taskitemListInfo.getMaxTask() + "");
                    intent.putExtra("maxTake", taskitemListInfo.getMaxTask() - size - taskitemListInfo.getFill_num());
                    intent.putExtra("state", 4);
                    startActivityForResult(intent, TakeRequest);
                } else {
                    Tools.showToast(TaskitemListexecutedActivity_12.this, "拍照数量已达最大值！");
                }
            }
        });
        taskitemlist_listview.setAdapter(taskitemListAdapter);
        taskitemlist_listview.setMode(PullToRefreshBase.Mode.DISABLED);
        if (!isCategory) {
            findViewById(R.id.taskitemlist_package).setVisibility(View.GONE);
            findViewById(R.id.taskitemlist_line2).setVisibility(View.GONE);
            findViewById(R.id.taskitemlist_line3).setVisibility(View.GONE);
        }
        getData();
        isRefresh = false;
    }

    private void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppInfo.BroadcastReceiver_TAKEPHOTO);
        context.registerReceiver(UpProgressbarBroadcast, filter);
    }

    private void unregisterReceiver(Context context) {
        context.unregisterReceiver(UpProgressbarBroadcast);
    }

    private BroadcastReceiver UpProgressbarBroadcast = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(AppInfo.BroadcastReceiver_TAKEPHOTO)) {
                String username = intent.getStringExtra("username");
                String projectid = intent.getStringExtra("projectid");
                String storeid = intent.getStringExtra("storeid");
                String taskpackid = intent.getStringExtra("taskpackid");
                String taskid = intent.getStringExtra("taskid");
                int progress = intent.getIntExtra("size", 0);
                if (!isRefreshing && !TextUtils.isEmpty(taskid) &&
                        TaskitemListexecutedActivity_12.this.username.equals(username) &&
                        !TextUtils.isEmpty(taskpackid) && taskpackid.equals(task_pack_id) &&
                        !TextUtils.isEmpty(projectid) && projectid.equals(project_id) &&
                        !TextUtils.isEmpty(storeid) && storeid.equals(TaskitemListexecutedActivity_12.this.store_id)) {
                    for (TaskitemListInfo temp : list) {
                        if (isRefreshing) {
                            break;
                        }
                        if (taskid.equals(temp.getTask_id())) {
                            temp.setProgress(progress);
                            temp.getTaskitemDetail_12View().settingProgressbar(progress);
                            break;
                        }
                    }
                }
            }
        }
    };

    public static boolean isRefresh;
    public static boolean isRefreshing = false;//是否正在更新

    protected void onResume() {
        super.onResume();
        if (!isCategory && isRefresh) {
            isRefresh = false;
            getData();
        }
    }

    private NewOnItemClickListener newOnItemClickListener = new NewOnItemClickListener() {
        public void onItemClick(Object object) {
            if (list == null) return;
            if (isCategory && TextUtils.isEmpty(category1)) {//是否有分类
                Tools.showToast(TaskitemListexecutedActivity_12.this, "请先选择分类属性");
                return;
            }
            final TaskitemListInfo taskitemListInfo = (TaskitemListInfo) object;
            if (!"2".equals(taskitemListInfo.getState())) {
                Tools.showToast(TaskitemListexecutedActivity_12.this, "亲，要等资料传完才能看的哦～");
                return;
            }
            task_id = taskitemListInfo.getTask_id();
            if (taskitemListInfo.getType().equals("1") || taskitemListInfo.getType().equals("8")) {
                Intent intent = new Intent();
                intent.putExtra("project_id", project_id);
                intent.putExtra("project_name", project_name);
                intent.putExtra("task_pack_id", taskitemListInfo.getP_id());
                intent.putExtra("task_pack_name", pack_name);
                intent.putExtra("task_id", taskitemListInfo.getTask_id());
                intent.putExtra("task_name", taskitemListInfo.getTaskname());
                intent.putExtra("task_type", taskitemListInfo.getType());
                intent.putExtra("store_id", store_id);
                intent.putExtra("store_name", store_name);
                intent.putExtra("store_num", store_num);
                intent.putExtra("photo_compression", photo_compression);
                intent.putExtra("category1", category1);
                intent.putExtra("category2", category2);
                intent.putExtra("category3", category3);
                intent.putExtra("is_desc", is_desc);
                intent.putExtra("is_watermark", is_watermark);
                intent.putExtra("code", code);
                intent.putExtra("brand", brand);
                intent.putExtra("fill_num", fill_num);
                intent.putExtra("max_num", taskitemListInfo.getMaxTask());
                intent.putExtra("outlet_batch", taskitemListInfo.getOutlet_batch());
                intent.putExtra("p_batch", taskitemListInfo.getP_batch());
                if ("1".equals(taskitemListInfo.getIs_close())) {//无效
                    intent.setClass(TaskitemListexecutedActivity_12.this, TaskitemPhotographyResetcloseActivity.class);
                } else {//正常
                    intent.setClass(TaskitemListexecutedActivity_12.this, TaskitemPhotographyResetActivity.class);
                }
                startActivity(intent);
            } else if (taskitemListInfo.getType().equals("2")) {
                Intent intent = new Intent(TaskitemListexecutedActivity_12.this, TaskitemShotResetActivity.class);
                intent.putExtra("project_id", project_id);
                intent.putExtra("project_name", project_name);
                intent.putExtra("task_pack_id", taskitemListInfo.getP_id());
                intent.putExtra("task_pack_name", pack_name);
                intent.putExtra("store_num", store_num);
                intent.putExtra("task_id", taskitemListInfo.getTask_id());
                intent.putExtra("task_name", taskitemListInfo.getTaskname());
                intent.putExtra("store_id", store_id);
                intent.putExtra("store_name", store_name);
                intent.putExtra("category1", category1);
                intent.putExtra("category2", category2);
                intent.putExtra("category3", category3);
                intent.putExtra("is_desc", is_desc);
                intent.putExtra("code", code);
                intent.putExtra("brand", brand);
                intent.putExtra("outlet_batch", taskitemListInfo.getOutlet_batch());
                intent.putExtra("p_batch", taskitemListInfo.getP_batch());
                startActivity(intent);
            } else if (taskitemListInfo.getType().equals("3")) {
                Intent intent = new Intent(TaskitemListexecutedActivity_12.this, TaskitemEditReset2Activity.class);
                intent.putExtra("project_id", project_id);
                intent.putExtra("project_name", project_name);
                intent.putExtra("task_pack_id", taskitemListInfo.getP_id());
                intent.putExtra("task_pack_name", pack_name);
                intent.putExtra("taskid", taskitemListInfo.getTask_id());
                intent.putExtra("task_name", taskitemListInfo.getTaskname());
                intent.putExtra("tasktype", "3");
                intent.putExtra("store_num", store_num);
                intent.putExtra("store_id", store_id);
                intent.putExtra("store_name", store_name);
                intent.putExtra("category1", category1);
                intent.putExtra("category2", category2);
                intent.putExtra("category3", category3);
                intent.putExtra("is_desc", is_desc);
                intent.putExtra("outlet_batch", taskitemListInfo.getOutlet_batch());
                intent.putExtra("p_batch", taskitemListInfo.getP_batch());
                startActivity(intent);
            } else if (taskitemListInfo.getType().equals("4")) {
                Intent intent = new Intent(TaskitemListexecutedActivity_12.this, TaskitemMapResetActivity.class);
                intent.putExtra("project_id", project_id);
                intent.putExtra("project_name", project_name);
                intent.putExtra("task_pack_id", taskitemListInfo.getP_id());
                intent.putExtra("task_pack_name", pack_name);
                intent.putExtra("task_id", taskitemListInfo.getTask_id());
                intent.putExtra("store_num", store_num);
                intent.putExtra("task_name", taskitemListInfo.getTaskname());
                intent.putExtra("store_id", store_id);
                intent.putExtra("store_name", store_name);
                intent.putExtra("category1", category1);
                intent.putExtra("category2", category2);
                intent.putExtra("category3", category3);
                intent.putExtra("is_desc", is_desc);
                intent.putExtra("code", code);
                intent.putExtra("brand", brand);
                intent.putExtra("outlet_batch", taskitemListInfo.getOutlet_batch());
                intent.putExtra("p_batch", taskitemListInfo.getP_batch());
                startActivity(intent);
            } else if (taskitemListInfo.getType().equals("5")) {
                Intent intent = new Intent(TaskitemListexecutedActivity_12.this, TaskitemRecodResetActivity.class);
                intent.putExtra("project_id", project_id);
                intent.putExtra("project_name", project_name);
                intent.putExtra("task_pack_id", taskitemListInfo.getP_id());
                intent.putExtra("task_pack_name", pack_name);
                intent.putExtra("store_num", store_num);
                intent.putExtra("task_id", taskitemListInfo.getTask_id());
                intent.putExtra("task_name", taskitemListInfo.getTaskname());
                intent.putExtra("store_id", store_id);
                intent.putExtra("store_name", store_name);
                intent.putExtra("category1", category1);
                intent.putExtra("category2", category2);
                intent.putExtra("category3", category3);
                intent.putExtra("is_desc", is_desc);
                intent.putExtra("code", code);
                intent.putExtra("brand", brand);
                intent.putExtra("outlet_batch", taskitemListInfo.getOutlet_batch());
                intent.putExtra("p_batch", taskitemListInfo.getP_batch());
                startActivity(intent);
            } else if (taskitemListInfo.getType().equals("6")) {
                Intent intent = new Intent(TaskitemListexecutedActivity_12.this, ScanTaskResetActivity.class);
                intent.putExtra("project_id", project_id);
                intent.putExtra("project_name", project_name);
                intent.putExtra("task_pack_id", taskitemListInfo.getP_id());
                intent.putExtra("task_pack_name", pack_name);
                intent.putExtra("store_num", store_num);
                intent.putExtra("task_id", taskitemListInfo.getTask_id());
                intent.putExtra("task_name", taskitemListInfo.getTaskname());
                intent.putExtra("store_id", store_id);
                intent.putExtra("store_name", store_name);
                intent.putExtra("category1", category1);
                intent.putExtra("category2", category2);
                intent.putExtra("category3", category3);
                intent.putExtra("is_desc", is_desc);
                intent.putExtra("code", code);
                intent.putExtra("brand", brand);
                intent.putExtra("outlet_batch", taskitemListInfo.getOutlet_batch());
                intent.putExtra("p_batch", taskitemListInfo.getP_batch());
                startActivity(intent);
            } else if (taskitemListInfo.getType().equals("7")) {//电话任务
                taskFinish.sendPostRequest(Urls.TaskFinish, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            if (jsonObject.getInt("code") == 200) {
                                if ("1".equals(jsonObject.getString("wuxiao"))) {
                                    Tools.showToast(TaskitemListexecutedActivity_12.this, "此任务已关闭");
                                } else {
                                    Intent intent = new Intent(TaskitemListexecutedActivity_12.this, CallTaskResetActivity.class);
                                    intent.putExtra("project_id", project_id);
                                    intent.putExtra("project_name", project_name);
                                    intent.putExtra("task_pack_id", taskitemListInfo.getP_id());
                                    intent.putExtra("task_pack_name", pack_name);
                                    intent.putExtra("store_num", store_num);
                                    intent.putExtra("task_id", taskitemListInfo.getTask_id());
                                    intent.putExtra("task_name", taskitemListInfo.getTaskname());
                                    intent.putExtra("store_id", store_id);
                                    intent.putExtra("store_name", store_name);
                                    intent.putExtra("category1", category1);
                                    intent.putExtra("category2", category2);
                                    intent.putExtra("category3", category3);
                                    intent.putExtra("is_desc", is_desc);
                                    intent.putExtra("code", code);
                                    intent.putExtra("brand", brand);
                                    intent.putExtra("outlet_batch", taskitemListInfo.getOutlet_batch());
                                    intent.putExtra("p_batch", taskitemListInfo.getP_batch());
                                    startActivity(intent);
                                }
                            }
                        } catch (JSONException e) {
                            Tools.showToast(TaskitemListexecutedActivity_12.this, getString(R.string.network_error));
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Tools.showToast(TaskitemListexecutedActivity_12.this, getString(R.string.network_volleyerror));
                    }
                });
            } else if (taskitemListInfo.getType().equals("9")) {//体验任务
                Intent intent = new Intent(TaskitemListexecutedActivity_12.this, ScreenshotActivity.class);
                intent.putExtra("task_id", taskitemListInfo.getTask_id());
                intent.putExtra("storeid", taskitemListInfo.getStoreid());
                intent.putExtra("outlet_batch", taskitemListInfo.getOutlet_batch());
                intent.putExtra("p_batch", taskitemListInfo.getP_batch());
                intent.putExtra("pid", taskitemListInfo.getP_id());
                intent.putExtra("which_page", "1");//查看详情
                startActivity(intent);
            }
        }
    };

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taskitemlist_package: {//选择属性
                if (isCategory) {
                    Intent intent = new Intent(this, SelectAttributeActivity.class);
                    intent.putExtra("task_pack_id", task_pack_id);
                    intent.putExtra("storeid", store_id);
                    startActivityForResult(intent, AppInfo.TaskitemListRequestCode);
                } else {
                    Tools.showToast(this, "没有属性");
                }
            }
            break;
            case R.id.taskitemlist_package_button: {//完成按钮
                packageComplete();
            }
            break;
        }
    }

    private void getData() {
        isRefreshing = true;
        Tasklistcomplete.sendPostRequest(Urls.Tasklistcomplete, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        JSONArray jsonArray = jsonObject.getJSONArray("datas");
                        int length = jsonArray.length();
                        if (list == null) {
                            list = new ArrayList<TaskitemListInfo>();
                            taskitemListAdapter.resetList(list);
                        } else {
                            list.clear();
                        }
                        for (int i = 0; i < length; i++) {
                            TaskitemListInfo taskitemListInfo = new TaskitemListInfo();
                            jsonObject = jsonArray.getJSONObject(i);
                            taskitemListInfo.setP_id(jsonObject.getString("p_id"));
                            taskitemListInfo.setTask_id(jsonObject.getString("task_id"));
                            taskitemListInfo.setTaskname(jsonObject.getString("task_name"));
                            taskitemListInfo.setType(jsonObject.getString("task_type"));
                            taskitemListInfo.setOutlet_batch(jsonObject.getString("outlet_batch"));
                            taskitemListInfo.setP_batch(jsonObject.getString("p_batch"));
                            taskitemListInfo.setFill_num(Tools.StringToInt(jsonObject.getString("fill_num")));
                            taskitemListInfo.setMaxTask(Tools.StringToInt(jsonObject.getString("max_num")));
                            taskitemListInfo.setIs_close(jsonObject.getString("is_close"));
                            taskitemListInfo.setState(jsonObject.getString("state"));
                            list.add(taskitemListInfo);
                        }
                        taskitemListAdapter.notifyDataSetChanged();
                    } else {
                        Tools.showToast(TaskitemListexecutedActivity_12.this, jsonObject.getString("msg"));
                    }
                    taskitemlist_listview.onRefreshComplete();
                } catch (JSONException e) {
                    taskitemlist_listview.onRefreshComplete();
                    Tools.showToast(TaskitemListexecutedActivity_12.this, getResources().getString(R.string.network_error));
                } finally {
                    isRefreshing = false;
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                isRefreshing = false;
                Tools.showToast(TaskitemListexecutedActivity_12.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private void packageComplete() {
        Packagecomplete.sendPostRequest(Urls.Packagecomplete, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        TaskitemDetailActivity_12.isRefresh = true;
                        TaskitemDetailActivity_12.packageid = task_pack_id;
                        CustomProgressDialog.Dissmiss();
                        baseFinish();
                    } else {
                        CustomProgressDialog.Dissmiss();
                        Tools.showToast(TaskitemListexecutedActivity_12.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    CustomProgressDialog.Dissmiss();
                    Tools.showToast(TaskitemListexecutedActivity_12.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskitemListexecutedActivity_12.this, getResources().getString(R.string.network_volleyerror));
            }
        }, "正在执行...");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) return;
        if (requestCode == AppInfo.TaskitemListRequestCode) {
            if (resultCode == RESULT_OK) {
                category1 = data.getStringExtra("classfiy1");
                category2 = data.getStringExtra("classfiy2");
                category3 = data.getStringExtra("classfiy3");
                String text = "";
                if (!TextUtils.isEmpty(category1)) {
                    text = text + " " + category1;
                }
                if (!TextUtils.isEmpty(category2)) {
                    text = text + " " + category2;
                }
                if (!TextUtils.isEmpty(category3)) {
                    text = text + " " + category3;
                }
                taskitemlist_package_name.setText(text);
            }
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == TakeRequest) {
                String path = data.getStringExtra("path");
                String[] paths;
                if (!TextUtils.isEmpty(path)) {
                    paths = path.split(",");
                    for (int i = 0; i < paths.length; i++) {
                        File file = new File(paths[i]);
                        if (!file.isFile()) {
                            Tools.showToast(this, "第" + (i + 1) + "张异常，已去除");
                        }
                    }
                    Collections.addAll(selectImgList, paths);
                }
                new zoomImageAsyncTask().executeOnExecutor(Executors.newCachedThreadPool());
            }

        }
//        super.onActivityResult(requestCode, resultCode, data);
    }

    class zoomImageAsyncTask extends AsyncTask {
        String msg = "图片压缩失败！";

        protected void onPreExecute() {
            if (photo_compression.equals("-1")) {
                CustomProgressDialog.showProgressDialog(TaskitemListexecutedActivity_12.this, "图片处理中...");
            } else {
                CustomProgressDialog.showProgressDialog(TaskitemListexecutedActivity_12.this, "图片压缩中...");
            }
            super.onPreExecute();
        }

        private File getTempFile(String oPath) throws FileNotFoundException {
            File returnvalue = null;
            FileInputStream fis = null;
            FileOutputStream fos = null;
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            try {
                if (!isLegal(oPath)) {
                    return null;
                }
                File oldfile = new File(oPath);
                if (!oldfile.exists()) {
                    return null;
                }
                if (!oldfile.isFile()) {
                    return null;
                }
                if (!oldfile.canRead()) {
                    return null;
                }
                File f = new File(oPath + "temp");
                fis = new FileInputStream(oldfile);
                bis = new BufferedInputStream(fis);
                fos = new FileOutputStream(f);
                bos = new BufferedOutputStream(fos);
                byte[] b = new byte[1024];
                while (bis.read(b) != -1) {
                    for (int i = 0; i < b.length; i++) {
                        b[i] = (byte) (255 - b[i]);
                    }
                    bos.write(b);
                }
                bos.flush();
                if (isLegal(oPath + "temp")) {
                    returnvalue = f;
                } else {
                    returnvalue = null;
                    f.delete();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new FileNotFoundException();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                throw new OutOfMemoryError();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bis != null) {
                        bis.close();
                    }
                    if (fis != null) {
                        fis.close();
                    }
                    if (bos != null) {
                        bos.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return returnvalue;
        }

        protected Object doInBackground(Object[] params) {
            try {
                int size = selectImgList.size();
                for (int i = 0; i < size; i++) {
                    String tPath = selectImgList.get(i);
                    if ("camera_default".equals(tPath)) {
                        continue;
                    }
                    String oPath = systemDBHelper.searchForOriginalpath(tPath);
                    if (!TextUtils.isEmpty(oPath) && isLegal(oPath) && checkPicture(oPath)) {
                        if (photo_compression.equals("-1")) {
                            if (systemDBHelper.bindTaskForPicture(oPath, task_pack_id, task_id)) {
                                if (!originalImgList.contains(oPath)) {
                                    originalImgList.add(oPath);
                                } else {
                                    msg = "发现重复照片，已自动去重，请重新提交";
                                    selectImgList.remove(i);
                                    i--;
                                    size--;
                                    isHadUnlegal = true;
                                }
                            } else {
                                selectImgList.remove(i);
                                i--;
                                size--;
                                isHadUnlegal = true;
                            }
                        } else {//加水印
                            File tempFile = getTempFile(oPath);//生成临时文件
                            if (tempFile == null) {
                                selectImgList.remove(i);
                                i--;
                                size--;
                                isHadUnlegal = true;
                                continue;
                            }
                            if (saveBitmap(imageZoom(Tools.getBitmap(tempFile.getPath(), 1024, 1024),
                                    Tools.StringToInt(photo_compression)), tempFile.getPath(), oPath) != null) {
                                Tools.d(oPath);
                                if (systemDBHelper.bindTaskForPicture(oPath, task_pack_id, task_id)) {
                                    if (!originalImgList.contains(oPath)) {
                                        originalImgList.add(oPath);
                                    } else {
                                        msg = "发现重复照片，已自动去重，请重新提交";
                                        selectImgList.remove(i);
                                        i--;
                                        size--;
                                        isHadUnlegal = true;
                                    }
                                } else {
                                    selectImgList.remove(i);
                                    i--;
                                    size--;
                                    isHadUnlegal = true;
                                }
                            } else {
                                selectImgList.remove(i);
                                i--;
                                size--;
                                isHadUnlegal = true;
                            }
                            if (tempFile.exists()) {
                                tempFile.delete();
                            }
                        }
                    } else {
                        selectImgList.remove(i);
                        i--;
                        size--;
                        if (!TextUtils.isEmpty(oPath)) {//判定为异常图片，执行删除操作
                            new File(oPath).delete();
                            new File(tPath).delete();
                            systemDBHelper.deletePicture(oPath);
                        }
                        msg = "有图片异常，已自动删除异常图片,请重新提交";
                        isHadUnlegal = true;
                    }
                }
            } catch (OutOfMemoryError e) {
                msg = "内存不足，请清理内存或重启手机";
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        private boolean isHadUnlegal = false;

        protected void onPostExecute(Object o) {
            if (o == null || !(boolean) o || isHadUnlegal) {
                Tools.showToast(TaskitemListexecutedActivity_12.this, msg);
                if (originalImgList != null) {
                    for (String temp : originalImgList) {
                        systemDBHelper.deletePicture(temp);
                        File file = new File(temp);
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                    originalImgList.clear();
                }
                if (selectImgList != null) {
                    for (String temp : selectImgList) {
                        systemDBHelper.deletePictureForThum(temp);
                    }
                    selectImgList.clear();
                }
                CustomProgressDialog.Dissmiss();
            } else {
                CustomProgressDialog.Dissmiss();
            }
            sendData();
//            }
        }
    }

    private boolean isLegal(String path) {
        File file = new File(path);
        return file.length() > 51200;
    }

    private void sendData() {
        Map<String, String> params = new HashMap<>();
        params.put("task_id", task_id);
        params.put("task_pack_id", task_pack_id);
        params.put("user_mobile", username);
        params.put("storeid", store_id);
        params.put("outlet_batch", outlet_batch);
        params.put("p_batch", p_batch);
        String imgs = "";
        int size;
        String key = "";
        size = originalImgList.size();
        for (int i = 0; i < size; i++) {
            String path = originalImgList.get(i);
            if (path.equals("camera_default")) {
                continue;
            }
            if (TextUtils.isEmpty(imgs)) {
                imgs = originalImgList.get(i);
            } else {
                imgs = imgs + "," + originalImgList.get(i);
            }
            if (TextUtils.isEmpty(key)) {
                key = "img" + (i + 1);
            } else {
                key = key + ",img" + (i + 1);
            }
        }
        updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, brand,
                store_id, store_name, task_pack_id,
                pack_name, "1", task_id, task_name, category1, category2, category3,
                username + project_id + store_id + task_id + task_pack_id + Tools.getTimeSS() + "bp",
                Urls.Filecomplete,
                key, imgs, UpdataDBHelper.Updata_file_type_img, params, photo_compression,
                true, Urls.Taskphotoup, paramsToString(), false);
        selectImgList.clear();
        originalImgList.clear();
        Intent service = new Intent("com.orange.oy.UpdataNewService");
        service.setPackage("com.orange.oy");
        startService(service);
    }

    private String paramsToString() {
        Map<String, String> parames = new HashMap<>();
        parames.put("task_id", task_id);
        parames.put("task_pack_id", task_pack_id);
        parames.put("user_mobile", AppInfo.getName(this));
        parames.put("storeid", store_id);
        parames.put("token", Tools.getToken());
        parames.put("outlet_batch", outlet_batch);
        parames.put("p_batch", p_batch);
        parames.put("is_fill", "1");
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
                try {
                    data = data + "&" + key + "=" + URLEncoder.encode(parames.get(key).trim(), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    data = data + "&" + key + "=" + parames.get(key).trim();
                }
            }
        }
        return data;
    }

    private Bitmap imageZoom(Bitmap bitMap, double maxSize) throws OutOfMemoryError {
        boolean isOutOfMemoryError = false;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            double mid = b.length / 1024;
            if (mid > maxSize) {
                double i = mid / maxSize;
                bitMap = zoomImage(bitMap, bitMap.getWidth() / Math.sqrt(i), bitMap.getHeight() / Math.sqrt(i));
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            isOutOfMemoryError = true;
            throw new OutOfMemoryError();
        } finally {
            if (isOutOfMemoryError && bitMap != null && !bitMap.isRecycled()) {
                bitMap.recycle();
            }
        }
        return bitMap;
    }

    private Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
        if (newBitmap != null) {
            bgimage.recycle();
        }
        return newBitmap;
    }

    private File saveBitmap(Bitmap bm, String tempPath, String oPath) throws FileNotFoundException,
            OutOfMemoryError {
        File returnvalue = null;
        FileOutputStream out = null;
        try {
            File f = new File(tempPath);
            out = new FileOutputStream(f);
            ExifInterface exif = new ExifInterface(oPath);
            bm = addWatermark(bm, systemDBHelper.searchForWatermark(oPath));
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            ExifInterface exif1 = new ExifInterface(f.getPath());
            exif1.setAttribute(ExifInterface.TAG_ORIENTATION, exif.getAttribute(ExifInterface.TAG_ORIENTATION));
            exif1.saveAttributes();
            if (isLegal(f.getPath())) {
                if (encryptPicture(tempPath, oPath)) {
                    systemDBHelper.updataIswater(oPath);
                    returnvalue = new File(oPath);
                    f.delete();
                } else {
                    f.delete();
                    returnvalue = null;
                }
            } else {
                f.delete();
                returnvalue = null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new FileNotFoundException();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            throw new OutOfMemoryError();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (bm != null) {
                    bm.recycle();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return returnvalue;
    }

    private boolean encryptPicture(String oldPath, String newPath) {
        boolean returnvalue = false;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            if (!isLegal(oldPath)) {
                return false;
            }
            File oldfile = new File(oldPath);
            if (!oldfile.exists()) {
                return false;
            }
            if (!oldfile.isFile()) {
                return false;
            }
            if (!oldfile.canRead()) {
                return false;
            }
            File f = new File(newPath);
            fis = new FileInputStream(oldfile);
            bis = new BufferedInputStream(fis);
            fos = new FileOutputStream(f);
            bos = new BufferedOutputStream(fos);
            byte[] b = new byte[1024];
            while (bis.read(b) != -1) {
                for (int i = 0; i < b.length; i++) {
                    b[i] = (byte) (255 - b[i]);
                }
                bos.write(b);
            }
            bos.flush();
            if (isLegal(f.getPath())) {
                returnvalue = true;
            } else {
                if (f.exists())
                    f.delete();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (fis != null) {
                    fis.close();
                }
                if (bos != null) {
                    bos.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return returnvalue;
    }

    private Bitmap addWatermark(Bitmap bitmap, String msg) {
        Bitmap newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        bitmap.recycle();
        int width = newBitmap.getWidth();
        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint();
        paint.setAlpha(100);
        paint.setColor(Color.RED);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setTextSize(AppInfo.PaintSize);
        int xNum = width / AppInfo.PaintSize;
        String[] msgs = msg.split("\n");
        int xN = 1;
        for (String str : msgs) {
            if (paint.measureText(str) <= width) {
                canvas.drawText(str, 0, AppInfo.PaintSize * xN++, paint);
            } else {
                int yNum = (int) Math.ceil(str.length() * 1d / xNum);
                int yb = 0;
                for (int i = 1; i <= yNum; i++) {
                    int temp = yb + xNum;
                    if (temp > str.length()) {
                        temp = str.length();
                    }
                    canvas.drawText(str, yb, temp, 0, AppInfo.PaintSize * xN++, paint);
                    yb = temp;
                }
            }
        }
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();//存储
        return newBitmap;
    }

    private boolean checkPicture(String path) {
        FileInputStream is = null;
        String value = null;
        try {
            is = new FileInputStream(path);
            byte[] b = new byte[4];
            is.read(b, 0, b.length);
            for (int i = 0; i < b.length; i++) {
                b[i] = (byte) (255 - b[i]);
            }
            value = bytesToHexString(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return value != null && (value.equals("FFD8FFE1") || value.equals("89504E47"));
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (byte aSrc : src) {
            hv = Integer.toHexString(aSrc & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        return builder.toString();
    }
}
