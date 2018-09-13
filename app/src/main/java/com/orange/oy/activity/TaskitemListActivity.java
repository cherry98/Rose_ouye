package com.orange.oy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.activity.scan.ScanTaskNewActivity;
import com.orange.oy.activity.scan.ScanTaskillustrateActivity;
import com.orange.oy.adapter.TaskitemListAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.TaskitemListInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 任务列表
 */
public class TaskitemListActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {
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
        if (Tasklist != null) {
            Tasklist.stop(Urls.Tasklist);
        }
        if (Packagecomplete != null) {
            Packagecomplete.stop(Urls.Packagecomplete);
        }
    }

    private void initNetworkConnection() {
        Tasklist = new NetworkConnection(this) {
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
                params.put("usermobile", AppInfo.getUserName(TaskitemListActivity.this));
                params.put("token", Tools.getToken());
                params.put("outlet_batch", outlet_batch);
                params.put("p_batch", p_batch);
                return params;
            }
        };
        Packagecomplete.setIsShowDialog(true);
    }

    private TaskitemListAdapter taskitemListAdapter;
    private PullToRefreshListView taskitemlist_listview;
    private ArrayList<TaskitemListInfo> list;
    private TextView taskitemlist_package_name;
    private NetworkConnection Packagecomplete, Tasklist;
    private String task_pack_id, store_id, pack_name, store_name, project_id, project_name;
    private boolean isCategory;
    private String category1 = "", category2 = "", category3 = "";
    private String photo_compression;
    private String store_num;
    private String is_desc;
    private String code, brand;
    private int is_watermark;
    private String outlet_batch, p_batch;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitemlist);
        initNetworkConnection();
        Intent data = getIntent();
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
        is_watermark = data.getIntExtra("is_watermark", 0);
        outlet_batch = data.getStringExtra("outlet_batch");
        p_batch = data.getStringExtra("p_batch");
        taskitemlist_listview = (PullToRefreshListView) findViewById(R.id.taskitemlist_listview);
        taskitemlist_package_name = (TextView) findViewById(R.id.taskitemlist_package_name);
        findViewById(R.id.taskitemlist_package).setOnClickListener(this);
        findViewById(R.id.taskitemlist_package_button).setOnClickListener(this);
        list = new ArrayList<>();
        taskitemListAdapter = new TaskitemListAdapter(this, list);
        taskitemlist_listview.setAdapter(taskitemListAdapter);
        taskitemlist_listview.setMode(PullToRefreshBase.Mode.DISABLED);
        if (!isCategory) {
            findViewById(R.id.taskitemlist_package).setVisibility(View.GONE);
            findViewById(R.id.taskitemlist_line2).setVisibility(View.GONE);
            findViewById(R.id.taskitemlist_line3).setVisibility(View.GONE);
        }
        taskitemlist_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (list == null) return;
                if (isCategory && TextUtils.isEmpty(category1)) {//是否有分类
                    Tools.showToast(TaskitemListActivity.this, "请先选择分类属性");
                    return;
                }
                TaskitemListInfo taskitemListInfo = list.get(position - 1);
                if (taskitemListInfo.getType().equals("1") || taskitemListInfo.getType().equals("8")) {
                    Intent intent = new Intent(TaskitemListActivity.this, TaskitemPhotographyActivity.class);
                    intent.putExtra("project_id", project_id);
                    intent.putExtra("project_name", project_name);
                    intent.putExtra("task_type", taskitemListInfo.getType());
                    intent.putExtra("task_pack_id", taskitemListInfo.getP_id());
                    intent.putExtra("task_pack_name", pack_name);
                    intent.putExtra("task_id", taskitemListInfo.getTask_id());
                    intent.putExtra("task_name", taskitemListInfo.getTaskname());
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
                    intent.putExtra("outlet_batch", taskitemListInfo.getOutlet_batch());
                    intent.putExtra("p_batch", taskitemListInfo.getP_batch());
                    startActivity(intent);
                } else if (taskitemListInfo.getType().equals("2")) {
                    Intent intent = new Intent(TaskitemListActivity.this, TaskitemShotillustrateActivity.class);
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
                    Intent intent = new Intent(TaskitemListActivity.this, TaskitemEditillustrateActivity.class);
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
                    Intent intent = new Intent(TaskitemListActivity.this, TaskitemMapActivity.class);
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
                    Intent intent = new Intent(TaskitemListActivity.this, TaskitemRecodillustrateActivity.class);
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
                    Intent intent = new Intent(TaskitemListActivity.this, ScanTaskNewActivity.class);
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
        });
        getData();
        isRefresh = false;
    }

    public static boolean isRefresh;

    protected void onResume() {
        super.onResume();
        if (!isCategory && isRefresh) {
            isRefresh = false;
            getData();
        }
    }

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
        Tasklist.sendPostRequest(Urls.Tasklist, new Response.Listener<String>() {
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
                            list.add(taskitemListInfo);
                        }
                        taskitemListAdapter.notifyDataSetChanged();
                    } else {
                        Tools.showToast(TaskitemListActivity.this, jsonObject.getString("msg"));
                    }
                    taskitemlist_listview.onRefreshComplete();
                } catch (JSONException e) {
                    taskitemlist_listview.onRefreshComplete();
                    Tools.showToast(TaskitemListActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(TaskitemListActivity.this, getResources().getString(R.string.network_volleyerror));
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
                        TaskitemDetailActivity.isRefresh = true;
                        TaskitemDetailActivity_12.isRefresh = true;
                        TaskitemDetailActivity.packageid = task_pack_id;
                        TaskitemDetailActivity_12.packageid = task_pack_id;
                        CustomProgressDialog.Dissmiss();
                        baseFinish();
                    } else {
                        CustomProgressDialog.Dissmiss();
                        Tools.showToast(TaskitemListActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    CustomProgressDialog.Dissmiss();
                    Tools.showToast(TaskitemListActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskitemListActivity.this, getResources().getString(R.string.network_volleyerror));
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
        super.onActivityResult(requestCode, resultCode, data);
    }
}
