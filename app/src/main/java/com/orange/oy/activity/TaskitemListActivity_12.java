package com.orange.oy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.activity.calltask.CallTaskActivity;
import com.orange.oy.activity.createtask_321.TaskExperienceActivity;
import com.orange.oy.activity.scan.ScanTaskNewActivity;
import com.orange.oy.adapter.TaskitemListAdapter_12;
import com.orange.oy.allinterface.NewOnItemClickListener;
import com.orange.oy.allinterface.OnRightClickListener;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.SystemDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.TaskitemListInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.service.RecordService;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 任务列表_待执行的任务包列表
 */
public class TaskitemListActivity_12 extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {
    private void initTitle(String str) {
        AppTitle taskitemlist_title = (AppTitle) findViewById(R.id.taskitemlist_title);
        taskitemlist_title.settingName((!TextUtils.isEmpty(str)) ? str : "任务列表");
        taskitemlist_title.showBack(this);
    }

    public void onBack() {
        Intent intent = new Intent();
        if (RecordService.isStart()) {//正在录音
            intent.putExtra("task_id", taskid_Record);
        } else {
            intent.putExtra("task_id", "");
        }
        setResult(RESULT_OK, intent);
        baseFinish();
    }

    public void onBackPressed() {
        Intent intent = new Intent();
        if (RecordService.isStart()) {//正在录音
            intent.putExtra("task_id", taskid_Record);
        } else {
            intent.putExtra("task_id", "");
        }
        setResult(RESULT_OK, intent);
        super.onBackPressed();
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
                params.put("usermobile", AppInfo.getUserName(TaskitemListActivity_12.this));
                params.put("token", Tools.getToken());
                params.put("outlet_batch", outlet_batch);
                params.put("p_batch", p_batch);
                return params;
            }
        };
        Packagecomplete.setIsShowDialog(true);
    }

    private TaskitemListAdapter_12 taskitemListAdapter;
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
    private String is_watermark;
    private String outlet_batch, p_batch, project_type;
    private SystemDBHelper systemDBHelper;
    private String index;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitemlist);
        initNetworkConnection();
        Intent data = getIntent();
        initTitle(data.getStringExtra("pack_name"));
        taskid_Record = data.getStringExtra("taskid_Record");
        project_type = data.getStringExtra("project_type");
        index = data.getStringExtra("index");
        systemDBHelper = new SystemDBHelper(this);
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
        taskitemlist_listview = (PullToRefreshListView) findViewById(R.id.taskitemlist_listview);
        taskitemlist_listview.setCanDelete(true);
        taskitemlist_package_name = (TextView) findViewById(R.id.taskitemlist_package_name);
        findViewById(R.id.taskitemlist_package).setOnClickListener(this);
        findViewById(R.id.taskitemlist_package_button).setOnClickListener(this);
        list = new ArrayList<>();
        taskitemListAdapter = new TaskitemListAdapter_12(this, taskitemlist_listview, list);
        taskitemListAdapter.settingRightText("连拍");
        taskitemListAdapter.setNewOnItemClickListener(newOnItemClickListener);
        taskitemListAdapter.setOnRightClickListener(new OnRightClickListener() {
            public void onRightClick(Object object) {//连拍按钮
                if (object == null) {
                    Tools.showToast(TaskitemListActivity_12.this, "异常，请清理内存后重新打开页面");
                    return;
                }
                TaskitemListInfo taskitemListInfo = (TaskitemListInfo) object;
                int size = systemDBHelper.getPictureNumForprivate(AppInfo.getName(TaskitemListActivity_12.this),
                        taskitemListInfo.getP_id(), store_id, null, task_pack_id);
                if (size < taskitemListInfo.getMaxTask()) {
                    Intent intent = new Intent(TaskitemListActivity_12.this, Camerase.class);
                    intent.putExtra("projectid", project_id);
                    intent.putExtra("storeid", store_id);
                    intent.putExtra("packageid", task_pack_id);
                    intent.putExtra("storecode", store_num);
                    intent.putExtra("taskid", taskitemListInfo.getTask_id());
                    Tools.d(taskitemListInfo.getMaxTask() + "");
                    intent.putExtra("maxTake", taskitemListInfo.getMaxTask() - size);
                    intent.putExtra("state", 1);
                    startActivity(intent);
                } else {
                    Tools.showToast(TaskitemListActivity_12.this, "拍照数量已达最大值！");
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

    public static boolean isRefresh;

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
                Tools.showToast(TaskitemListActivity_12.this, "请先选择分类属性");
                return;
            }
            TaskitemListInfo taskitemListInfo = (TaskitemListInfo) object;
            if (taskitemListInfo.getType().equals("1") || taskitemListInfo.getType().equals("8")) {
                Intent intent = new Intent(TaskitemListActivity_12.this, TaskitemPhotographyNextYActivity.class);
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
                intent.putExtra("outlet_batch", taskitemListInfo.getOutlet_batch());
                intent.putExtra("p_batch", taskitemListInfo.getP_batch());
                intent.putExtra("newtask", "0");//判断是否是新手任务 1是0否
                intent.putExtra("index", index);
                startActivity(intent);
            } else if (taskitemListInfo.getType().equals("2")) {
                Intent intent = new Intent(TaskitemListActivity_12.this, TaskitemShotActivity.class);
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
                intent.putExtra("newtask", "0");//判断是否是新手任务 1是0否
                intent.putExtra("index", index);
                startActivity(intent);
            } else if (taskitemListInfo.getType().equals("3")) {
                Intent intent = new Intent(TaskitemListActivity_12.this, TaskitemEditActivity.class);
                intent.putExtra("project_id", project_id);
                intent.putExtra("project_name", project_name);
                intent.putExtra("task_pack_id", taskitemListInfo.getP_id());
                intent.putExtra("task_pack_name", pack_name);
                intent.putExtra("task_id", taskitemListInfo.getTask_id());
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
                intent.putExtra("newtask", "0");//判断是否是新手任务 1是0否
                intent.putExtra("index", index);
                startActivity(intent);
            } else if (taskitemListInfo.getType().equals("4")) {
                Intent intent = new Intent(TaskitemListActivity_12.this, TaskitemMapActivity.class);
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
                intent.putExtra("newtask", "0");//判断是否是新手任务 1是0否
                intent.putExtra("project_type", project_type);
                intent.putExtra("index", index);
                startActivity(intent);
            } else if (taskitemListInfo.getType().equals("5")) {
                if (!TextUtils.isEmpty(taskid_Record) && RecordService.isStart() && !taskitemListInfo.getTask_id().equals(taskid_Record)) {
                    ConfirmDialog.showDialog(TaskitemListActivity_12.this, "提示！", 2, "您的录音任务还没有结束，请先提交后开始下一个任务~", null, "我知道了"
                            , null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                @Override
                                public void leftClick(Object object) {

                                }

                                @Override
                                public void rightClick(Object object) {
                                }
                            }).goneLeft();
                    return;
                }
                Intent intent = new Intent(TaskitemListActivity_12.this, TaskitemRecodillustrateActivity.class);
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
                intent.putExtra("newtask", "0");//判断是否是新手任务 1是0否
                intent.putExtra("index", index);
                startActivityForResult(intent, 0);
            } else if (taskitemListInfo.getType().equals("6")) {
                Intent intent = new Intent(TaskitemListActivity_12.this, ScanTaskNewActivity.class);
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
                intent.putExtra("newtask", "0");//判断是否是新手任务 1是0否
                intent.putExtra("index", index);
                startActivity(intent);
            } else if (taskitemListInfo.getType().equals("7")) {//电话任务
                Intent intent = new Intent(TaskitemListActivity_12.this, CallTaskActivity.class);
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
                intent.putExtra("index", index);
                startActivity(intent);
            } else if (taskitemListInfo.getType().equals("9")) {//体验任务
                Intent intent = new Intent(TaskitemListActivity_12.this, TaskExperienceActivity.class);
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
                intent.putExtra("index", index);
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
                            taskitemListInfo.setFill_num(Tools.StringToInt(jsonObject.getString("fill_num")));
                            taskitemListInfo.setMaxTask(Tools.StringToInt(jsonObject.getString("max_num")));
                            list.add(taskitemListInfo);
                        }
                        taskitemListAdapter.notifyDataSetChanged();
                    } else {
                        Tools.showToast(TaskitemListActivity_12.this, jsonObject.getString("msg"));
                    }
                    taskitemlist_listview.onRefreshComplete();
                } catch (JSONException e) {
                    taskitemlist_listview.onRefreshComplete();
                    Tools.showToast(TaskitemListActivity_12.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(TaskitemListActivity_12.this, getResources().getString(R.string.network_volleyerror));
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
                        TaskitemDetailActivity.packageid = task_pack_id;
                        TaskitemDetailActivity_12.isRefresh = true;
                        TaskitemDetailActivity_12.packageid = task_pack_id;
                        CustomProgressDialog.Dissmiss();
                        baseFinish();
                    } else {
                        CustomProgressDialog.Dissmiss();
                        Tools.showToast(TaskitemListActivity_12.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    CustomProgressDialog.Dissmiss();
                    Tools.showToast(TaskitemListActivity_12.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskitemListActivity_12.this, getResources().getString(R.string.network_volleyerror));
            }
        }, "正在执行...");
    }

    private String taskid_Record = "";

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) return;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AppInfo.TaskitemListRequestCode: {
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
                break;
                case 0: {//录音任务
                    taskid_Record = data.getStringExtra("task_id");
                    int size = list.size();
                    for (int i = 0; i < size; i++) {
                        TaskitemListInfo taskitemListInfo = list.get(i);
                        if (taskitemListInfo == null) {
                            continue;
                        }
                        if (!TextUtils.isEmpty(taskid_Record) && taskitemListInfo.getTask_id().equals(taskid_Record)) {//是任务
                            taskitemListInfo.setIs_Record(true);
                        } else {
                            taskitemListInfo.setIs_Record(false);
                        }
                    }
                    if (taskitemListAdapter != null)
                        taskitemListAdapter.notifyDataSetChanged();
                }
                break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
