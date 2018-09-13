package com.orange.oy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.adapter.TaskitemListAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.OfflineDBHelper;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.info.TaskitemListInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 任务列表
 */
public class OfflineTaskActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {
    private void initTitle() {
        AppTitle taskitemlist_title = (AppTitle) findViewById(R.id.taskitemlist_title);
        taskitemlist_title.settingName((!TextUtils.isEmpty(pack_name)) ? pack_name : "任务列表");
        taskitemlist_title.showBack(this);
    }

    public void onBack() {
        baseFinish();
    }

    private TaskitemListAdapter taskitemListAdapter;
    private PullToRefreshListView taskitemlist_listview;
    private ArrayList<TaskitemListInfo> list;
    private TextView taskitemlist_package_name;
    private String task_pack_id, store_id, pack_name, store_name, project_id, project_name;
    private boolean isCategory;
    private String category1 = "", category2 = "", category3 = "";
    private String photo_compression;
    private String store_num;
    private OfflineDBHelper offlineDBHelper;
    private UpdataDBHelper updataDBHelper;
    private int is_watermark;
    private String code, brand;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitemlist);
        offlineDBHelper = new OfflineDBHelper(this);
        updataDBHelper = new UpdataDBHelper(this);
        Intent data = getIntent();
        project_id = data.getStringExtra("project_id");
        project_name = data.getStringExtra("project_name");
        task_pack_id = data.getStringExtra("task_pack_id");
        pack_name = data.getStringExtra("pack_name");
        store_id = data.getStringExtra("store_id");
        store_name = data.getStringExtra("store_name");
        store_num = data.getStringExtra("store_num");
        isCategory = data.getBooleanExtra("isCategory", false);
        photo_compression = data.getStringExtra("photo_compression");
        is_watermark = data.getIntExtra("is_watermark", 0);
        code = data.getStringExtra("code");
        brand = data.getStringExtra("brand");
        initTitle();
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
                    Tools.showToast(OfflineTaskActivity.this, "请先选择分类属性");
                    return;
                }
                TaskitemListInfo taskitemListInfo = list.get(position - 1);
                if (taskitemListInfo.getType().equals("1")) {
                    Intent intent = new Intent(OfflineTaskActivity.this, OfflineTaskitemPhotographyActivity.class);
                    intent.putExtra("project_id", project_id);
                    intent.putExtra("project_name", project_name);
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
                    intent.putExtra("is_watermark", is_watermark);
                    intent.putExtra("code", code);
                    intent.putExtra("brand", brand);
                    startActivity(intent);
                } else if (taskitemListInfo.getType().equals("2")) {
                    Intent intent = new Intent(OfflineTaskActivity.this, OfflineTaskitemShotillustrateActivity.class);
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
                    intent.putExtra("code", code);
                    intent.putExtra("brand", brand);
                    startActivity(intent);
                } else if (taskitemListInfo.getType().equals("3")) {
                    Intent intent = new Intent(OfflineTaskActivity.this, OfflineTaskitemEditillustrateActivity.class);
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
                    startActivity(intent);
                } else if (taskitemListInfo.getType().equals("4")) {
                    Intent intent = new Intent(OfflineTaskActivity.this, OfflineTaskitemMapActivity.class);
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
                    intent.putExtra("code", code);
                    intent.putExtra("brand", brand);
                    startActivity(intent);
                } else if (taskitemListInfo.getType().equals("5")) {
                    Intent intent = new Intent(OfflineTaskActivity.this, OfflineTaskitemRecodillustrateActivity.class);
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
                    intent.putExtra("code", code);
                    intent.putExtra("brand", brand);
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
                    Intent intent = new Intent(this, OfflineSelectAttributeActivity.class);
                    intent.putExtra("project_id", project_id);
                    intent.putExtra("task_pack_id", task_pack_id);
                    intent.putExtra("storeid", store_id);
                    intent.putExtra("task_size", list.size());
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

    private String outlet_batch, p_batch;

    private void getData() {
        String username = AppInfo.getName(this);
        outlet_batch = offlineDBHelper.getTaskOutletBatch(username, project_id, store_id, task_pack_id, null);
        p_batch = offlineDBHelper.getTaskPBatch(username, project_id, store_id, task_pack_id, null);
        if (isCategory) {
            list = offlineDBHelper.getTaskList(username, project_id, store_id, task_pack_id);
        } else {
            list = offlineDBHelper.getTaskList2(username, project_id, store_id, task_pack_id);
        }
        taskitemListAdapter.resetList(list);
        taskitemListAdapter.notifyDataSetChanged();
    }

    private void packageComplete() {
        if (offlineDBHelper.canCompleted(AppInfo.getName(this), project_id, store_id, task_pack_id)) {
            ConfirmDialog.showDialog(this, "提示", "确定完成吗？", "取消",
                    "确定", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                        public void leftClick(Object object) {
                        }

                        public void rightClick(Object object) {
                            String username = AppInfo.getName(OfflineTaskActivity.this);
                            try {
                                boolean issuccess = updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, brand,
                                        store_id, store_name, "-2", null, "-2", null, null,
                                        category1, category2, category3,
                                        username + project_id + store_id + task_pack_id + "-2", null,
                                        null, null, UpdataDBHelper.Updata_file_type_video,
                                        null, null, true, Urls.Packagecomplete, paramsToString(), true);
                                if (issuccess) {
                                    offlineDBHelper.completedPackage(username, project_id, store_id, task_pack_id);
                                    Intent service = new Intent("com.orange.oy.UpdataNewService");
                                    service.setPackage("com.orange.oy");
                                    startService(service);
                                    OfflinePackageActivity.isRefresh = true;
                                    onBack();
                                } else {
                                    Tools.showToast(OfflineTaskActivity.this, "完成失败");
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                                Tools.showToast(OfflineTaskActivity.this, "转码失败！" + store_id + "/" + task_pack_id);
                            }
                        }
                    });
        } else {
            Tools.showToast(this, "请至少完成一组任务");
        }
    }

    private String paramsToString() throws UnsupportedEncodingException {
        Map<String, String> parames = new HashMap<>();
        parames.put("storeid", store_id);
        parames.put("pid", task_pack_id);
        parames.put("usermobile", AppInfo.getUserName(OfflineTaskActivity.this));
        parames.put("outlet_batch", outlet_batch);
        parames.put("p_batch", p_batch);
        String data = "";
        Iterator<String> iterator = parames.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (TextUtils.isEmpty(data)) {
                data = key + "=" + URLEncoder.encode(parames.get(key).trim(), "utf-8");
            } else {
                data = data + "&" + key + "=" + URLEncoder.encode(parames.get(key).trim(), "utf-8");
            }
        }
        return data;
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
