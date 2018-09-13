package com.orange.oy.activity.scan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.karics.library.zxing.android.CaptureActivity;
import com.orange.oy.R;
import com.orange.oy.activity.StoreDescActivity;
import com.orange.oy.adapter.ScanTaskAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.ScanTaskInfo;
import com.orange.oy.info.TaskNewInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.CollapsibleTextView;
import com.orange.oy.view.MyListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 扫码任务说明页
 */
public class ScanTaskillustrateActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle,
        View.OnClickListener {


    private void initTitle() {
        AppTitle taskitemlist_title = (AppTitle) findViewById(R.id.scantaskillustrate_title);
        taskitemlist_title.settingName("扫码任务");
        if (!"1".equals(newtask)) {//不是新手
            taskitemlist_title.showBack(this);
        }
        if ("1".equals(is_desc)) {
            taskitemlist_title.setIllustrate(new AppTitle.OnExitClickForAppTitle() {
                public void onExit() {
                    Intent intent = new Intent(ScanTaskillustrateActivity.this, StoreDescActivity.class);
                    intent.putExtra("id", store_id);
                    intent.putExtra("store_name", store_name);
                    intent.putExtra("is_task", true);
                    startActivity(intent);
                }
            });
        }
    }

    private void initNetworkConnection() {
        scanTask = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("storeid", storeid);
                params.put("taskid", taskid);
                params.put("pid", pid);
                params.put("usermobile", AppInfo.getName(ScanTaskillustrateActivity.this));
                params.put("p_batch", p_batch);
                params.put("outlet_batch", outlet_batch);
                return params;
            }
        };
        scanTask.setIsShowDialog(true);
    }

    @Override
    public void onBackPressed() {
        if (!"1".equals(newtask)) {
            super.onBackPressed();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (scanTask != null) {
            scanTask.stop(Urls.ScanTask);
        }
    }

    private MyListView listView;
    private ScanTaskAdapter scanTaskAdapter;
    private NetworkConnection scanTask;
    private String storeid, taskid, pid, p_batch, outlet_batch, is_desc, store_id, store_name;
    private ArrayList<ScanTaskInfo> list = new ArrayList<>();
    private ArrayList<String> picList = new ArrayList<>();
    private String executeid, batch;
    private ArrayList<String> codeList = new ArrayList<>();
    private Intent data2;
    public static ScanTaskillustrateActivity scanillActivity = null;
    private String taskname, note;
    private String newtask;//判断是否是新手任务 1是0否
    private ArrayList<TaskNewInfo> list_taskdatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_taskillustrate);
        initNetworkConnection();
        data2 = getIntent();
        if (data2 == null) {
            return;
        }
        scanillActivity = this;
        storeid = data2.getStringExtra("store_id");
        taskid = data2.getStringExtra("task_id");
        pid = data2.getStringExtra("task_pack_id");
        p_batch = data2.getStringExtra("p_batch");
        outlet_batch = data2.getStringExtra("outlet_batch");
        store_name = data2.getStringExtra("store_name");
        store_id = data2.getStringExtra("store_id");
        is_desc = data2.getStringExtra("is_desc");
        newtask = data2.getStringExtra("newtask");
        if ("1".equals(newtask)) {
            list_taskdatas = (ArrayList<TaskNewInfo>) data2.getBundleExtra("data").getSerializable("list");
        }
        initTitle();
        ((ScrollView) findViewById(R.id.scanilltask_scroll)).smoothScrollTo(0, 20);
        findViewById(R.id.scantaskillustrate_scan).setOnClickListener(this);
        listView = (MyListView) findViewById(R.id.scantaskillustrate_listview);
        scanTaskAdapter = new ScanTaskAdapter(this, list);
        listView.setAdapter(scanTaskAdapter);
        getData();
    }

    public void getData() {
        scanTask.sendPostRequest(Urls.ScanTask, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        taskname = jsonObject.getString("taskname");
                        note = jsonObject.getString("note");
                        ((TextView) findViewById(R.id.scantaskillustrate_name)).setText(taskname);
                        ((CollapsibleTextView) findViewById(R.id.scantaskillustrate_desc)).setDesc(note, TextView.BufferType.NORMAL);
                        executeid = jsonObject.getString("executeid");
                        batch = jsonObject.getString("batch");
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            ScanTaskInfo scanTaskInfo = new ScanTaskInfo();
                            scanTaskInfo.setBarcode(object.getString("barcode"));
                            scanTaskInfo.setName(object.getString("name"));
                            scanTaskInfo.setSize(object.getString("size"));
                            scanTaskInfo.setPicurl(object.getString("picurl"));
                            codeList.add(object.getString("barcode"));
                            list.add(scanTaskInfo);
                        }
                        scanTaskAdapter.notifyDataSetChanged();
                    } else {
                        Tools.showToast(ScanTaskillustrateActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(ScanTaskillustrateActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(ScanTaskillustrateActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        }, null);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.scantaskillustrate_scan) {
            data2.setClass(ScanTaskillustrateActivity.this, CaptureActivity.class);
            data2.putExtra("type", "1");
            data2.putExtra("executeid", executeid);
            data2.putExtra("batch", batch);
            data2.putExtra("taskname", taskname);
            data2.putExtra("note", note);
            data2.putExtra("flag", "1");//扫码任务
            startActivityForResult(data2, REQUEST_CODE_SCAN);
        }
    }


    private static final int REQUEST_CODE_SCAN = 0x0000;
    private static final String DECODED_CONTENT_KEY = "codedContent";
    private ArrayList<String> matchList = new ArrayList<>();//匹配上的数据
    private ArrayList<String> mismatchList = new ArrayList<>();//没有匹配上的数据

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            String content = data.getStringExtra(DECODED_CONTENT_KEY);
            ArrayList<ScanTaskInfo> temp = list;//没有扫描到的商品信息
            ArrayList<String> tempCode = codeList;//没有扫描到的条码id
            if (codeList.contains(content) && !matchList.contains(content)) {
                matchList.add(content);
            } else if (!codeList.contains(content) && !mismatchList.contains(content)) {
                mismatchList.add(content);
            }
            //查找没有扫描的条形码
            for (int i = 0; i < temp.size(); i++) {
                if (temp.get(i).getBarcode().equals(content)) {
                    temp.remove(i);
                    tempCode.remove(i);
                }
            }
            data2.setClass(ScanTaskillustrateActivity.this, CaptureActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("temp", temp);
            bundle.putSerializable("matchList", matchList);
            bundle.putSerializable("mismatchList", mismatchList);
            bundle.putSerializable("tempCode", tempCode);
            bundle.putSerializable("list", list_taskdatas);
            data2.putExtra("data", bundle);
            data2.putExtra("executeid", executeid);
            data2.putExtra("batch", batch);
            data2.putExtra("type", "2");
            data2.putExtra("taskname", taskname);
            data2.putExtra("note", note);
            data2.putExtra("flag", "1");//扫码任务
            startActivityForResult(data2, REQUEST_CODE_SCAN);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        baseFinish();
    }
}
