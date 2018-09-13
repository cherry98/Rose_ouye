package com.orange.oy.activity.scan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.karics.library.zxing.android.CaptureActivity;
import com.orange.oy.R;
import com.orange.oy.activity.TaskFinishActivity;
import com.orange.oy.activity.TaskitemDetailActivity;
import com.orange.oy.activity.TaskitemDetailActivity_12;
import com.orange.oy.activity.TaskitemEditActivity;
import com.orange.oy.activity.TaskitemListActivity;
import com.orange.oy.activity.TaskitemListActivity_12;
import com.orange.oy.activity.TaskitemMapActivity;
import com.orange.oy.activity.TaskitemPhotographyNextYActivity;
import com.orange.oy.activity.TaskitemRecodillustrateActivity;
import com.orange.oy.activity.TaskitemShotActivity;
import com.orange.oy.adapter.ScanTaskAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.ScanTaskInfo;
import com.orange.oy.info.TaskNewInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.CollapsibleTextView;
import com.orange.oy.view.MyListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 扫码任务页
 */
public class ScanTaskActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle,
        View.OnClickListener {

    public void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.scantask_title);
        appTitle.settingName("扫码任务");
        if (!"1".equals(newtask)) {//不是新手
            appTitle.showBack(this);
        }
    }

    @Override
    public void onBackPressed() {
        if (!"1".equals(newtask)) {
            super.onBackPressed();
        }
    }


    private void initNetworkConnection() {
        ScanTaskup = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("executeid", executeid);
                params.put("barcodelist", matchList.toString().substring(1, matchList.toString().length() - 1));
                params.put("otherlist", mismatchList.toString().substring(1, mismatchList.toString().length() - 1));
                params.put("usermobile", usermobile);
                params.put("taskbatch", taskbatch);
                return params;
            }
        };
        ScanTaskup.setIsShowDialog(true);
    }

    private ScanTaskAdapter scanTaskAdapter;
    private MyListView listView;
    private ArrayList<ScanTaskInfo> list;
    private Intent data2;
    private String executeid, taskbatch;
    private ArrayList<String> matchList;//匹配上的数据
    private ArrayList<String> mismatchList;//没有匹配上的数据
    private ArrayList<String> tempCode;
    public static ScanTaskActivity scanActivity = null;
    private NetworkConnection ScanTaskup;
    private String index;//扫码任务预览

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_task);
        data2 = getIntent();
        if (data2 == null) {
            return;
        }
        index = data2.getStringExtra("index");
        initNetworkConnection();
        scanActivity = this;
        list = (ArrayList<ScanTaskInfo>) getIntent().getBundleExtra("data").getSerializable("temp");
        executeid = data2.getStringExtra("executeid");
        taskbatch = data2.getStringExtra("batch");
        matchList = (ArrayList<String>) data2.getBundleExtra("data").getSerializable("matchList");
        mismatchList = (ArrayList<String>) data2.getBundleExtra("data").getSerializable("mismatchList");
        tempCode = (ArrayList<String>) data2.getBundleExtra("data").getSerializable("tempCode");
        newtask = getIntent().getStringExtra("newtask");
        initTitle();
        ((TextView) findViewById(R.id.scantask_name)).setText(data2.getStringExtra("taskname"));
        ((CollapsibleTextView) findViewById(R.id.scantask_desc)).setDesc(data2.getStringExtra("note"), TextView.BufferType.NORMAL);
        ((ScrollView) findViewById(R.id.scantask_scroll)).smoothScrollTo(0, 20);
        listView = (MyListView) findViewById(R.id.scantask_listview);
        scanTaskAdapter = new ScanTaskAdapter(this, list);
        listView.setAdapter(scanTaskAdapter);
        findViewById(R.id.scantask_submit).setOnClickListener(this);
        findViewById(R.id.scantask_continue).setOnClickListener(this);
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    private static final int REQUEST_CODE_SCAN = 0x0000;
    private static final String DECODED_CONTENT_KEY = "codedContent";

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scantask_submit:
                if (index != null && "0".equals(index)) {
                    Tools.showToast(ScanTaskActivity.this, "抱歉，预览时任务无法执行。");
                    return;
                }
                sendData();
                break;
            case R.id.scantask_continue: {
                if (list.size() != 0) {
                    Intent intent = new Intent(ScanTaskActivity.this, CaptureActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("matchList", matchList);
                    bundle.putSerializable("mismatchList", mismatchList);
                    intent.putExtra("data", bundle);
                    intent.putExtra("type", "1");
                    intent.putExtra("executeid", executeid);
                    intent.putExtra("batch", taskbatch);
                    intent.putExtra("flag", "1");//扫码任务
                    startActivityForResult(intent, REQUEST_CODE_SCAN);
                } else {
                    Tools.showToast(ScanTaskActivity.this, "扫码任务已完成");
                }
            }
            break;
        }
    }


    private String usermobile, projectid, project_name, storeid, store_num, pid, task_pack_name, taskid, task_name;
    private String newtask;//判断是否是新手任务 1是0否

    private void sendData() {
        usermobile = AppInfo.getName(this);
        projectid = data2.getStringExtra("project_id");
        project_name = data2.getStringExtra("project_name");
        storeid = data2.getStringExtra("store_id");
        store_num = data2.getStringExtra("store_num");
        pid = data2.getStringExtra("task_pack_id");
        task_name = data2.getStringExtra("task_name");
        task_pack_name = data2.getStringExtra("task_pack_name");
        taskid = data2.getStringExtra("task_id");
        Map<String, String> params = new HashMap<>();
        String barcodelist = matchList.toString().substring(1, matchList.toString().length() - 1).replaceAll(" ", "");
        String otherlist = mismatchList.toString().substring(1, mismatchList.toString().length() - 1).replaceAll(" ", "");
        params.put("executeid", executeid);
        params.put("barcodelist", barcodelist);
        params.put("otherlist", otherlist);
        params.put("usermobile", usermobile);
        params.put("taskbatch", taskbatch);
        sendData2();
    }

    private int code;

    public void sendData2() {
        ScanTaskup.sendPostRequest(Urls.ScanTaskup, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    code = jsonObject.getInt("code");
                    if (code == 200 || code == 2) {
                        Tools.d("扫码完成------");
                        TaskitemDetailActivity.isRefresh = true;
                        TaskitemDetailActivity_12.isRefresh = true;
                        TaskitemDetailActivity.taskid = taskid;
                        TaskitemDetailActivity_12.taskid = taskid;
                        TaskFinishActivity.isRefresh = true;
                        TaskitemListActivity.isRefresh = true;
                        TaskitemListActivity_12.isRefresh = true;
                        ScanTaskillustrateActivity.scanillActivity.finish();
                        if (code == 200) {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            CustomProgressDialog.Dissmiss();
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
                                        intent.putExtra("photo_compression", taskNewInfo.getPhoto_compression());
                                        if ("1".equals(type) || "8".equals(type)) {//拍照任务
                                            intent.setClass(ScanTaskActivity.this, TaskitemPhotographyNextYActivity.class);
                                            startActivity(intent);
                                        } else if ("2".equals(type)) {//视频任务
                                            intent.setClass(ScanTaskActivity.this, TaskitemShotActivity.class);
                                            startActivity(intent);
                                        } else if ("3".equals(type)) {//记录任务
                                            intent.setClass(ScanTaskActivity.this, TaskitemEditActivity.class);
                                            startActivity(intent);
                                        } else if ("4".equals(type)) {//定位任务
                                            intent.setClass(ScanTaskActivity.this, TaskitemMapActivity.class);
                                            startActivity(intent);
                                        } else if ("5".equals(type)) {//录音任务
                                            intent.setClass(ScanTaskActivity.this, TaskitemRecodillustrateActivity.class);
                                            startActivity(intent);
                                        } else if ("6".equals(type)) {//扫码任务
                                            intent.setClass(ScanTaskActivity.this, ScanTaskNewActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                }
                            }
                            baseFinish();
                        } else if (code == 2) {
                            ConfirmDialog.showDialog(ScanTaskActivity.this, null, "任务均已执行完毕，但由于您的定位位置不在网点指定位置附近，您的执行结果可能无效。", null,
                                    "确定", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                                        @Override
                                        public void leftClick(Object object) {

                                        }

                                        @Override
                                        public void rightClick(Object object) {
                                            try {
                                                Thread.sleep(500);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            CustomProgressDialog.Dissmiss();
                                            baseFinish();
                                        }
                                    }).goneLeft();
                        }
                    } else {
                        Tools.showToast(ScanTaskActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(ScanTaskActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(ScanTaskActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            String content = data.getStringExtra(DECODED_CONTENT_KEY);
            ArrayList<ScanTaskInfo> temp = list;//没有扫描到的商品信息
            ArrayList<String> tempCode2 = tempCode;//没有扫描到的条码id
            if (tempCode.contains(content) && !matchList.contains(content)) {
                matchList.add(content);
            } else if (!tempCode.contains(content) && !mismatchList.contains(content)) {
                mismatchList.add(content);
            }
            for (int i = 0; i < temp.size(); i++) {//查找没有扫描的条形码
                if (temp.get(i).getBarcode().equals(content)) {
                    temp.remove(temp.get(i));
                    tempCode2.remove(tempCode2.get(i));
                }
            }
            data2.setClass(ScanTaskActivity.this, CaptureActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("matchList", matchList);
            bundle.putSerializable("mismatchList", mismatchList);
            bundle.putSerializable("tempCode", tempCode2);
            bundle.putSerializable("temp", temp);
            data2.putExtra("data", bundle);
            data2.putExtra("type", "3");
            data2.putExtra("intent", "1");//扫码任务
            startActivityForResult(data2, REQUEST_CODE_SCAN);
        }
    }
}
