package com.orange.oy.activity.scan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.karics.library.zxing.android.CaptureActivity;
import com.orange.oy.R;
import com.orange.oy.activity.ShotActivity;
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
import com.orange.oy.db.AppDBHelper;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.ScanTaskInfo;
import com.orange.oy.info.TaskNewInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.MyListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * V3.11 扫码任务
 */
public class ScanTaskNewActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener, AdapterView.OnItemClickListener {

    private void initNetwork() {
        scanTask = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("storeid", storeid);
                params.put("taskid", taskid);
                params.put("pid", pid);
                params.put("usermobile", AppInfo.getName(ScanTaskNewActivity.this));
                params.put("p_batch", p_batch);
                params.put("outlet_batch", outlet_batch);
                return params;
            }
        };
        scanTaskup = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("executeid", executeid);
                params.put("barcodelist", barcodelist + "");
                params.put("usermobile", AppInfo.getName(ScanTaskNewActivity.this));
                params.put("taskbatch", batch);
                params.put("upflag", upflag);
                params.put("storeid", storeid);
                return params;
            }
        };
        scanTaskup.setIsShowDialog(true);
    }

    private void initView() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.scantask_title);
        if (index != null && "0".equals(index)) {
            appTitle.settingName("扫码任务（预览）");
        } else {
            appTitle.settingName("扫码任务");
        }
        appTitle.showBack(this);
    }

    @Override
    public void onBack() {
        if (!scanedCode.isEmpty()) {
            upflag = "0";
            if (!"0".equals(index)) {
                sendData();
            }
        } else {
            baseFinish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (!scanedCode.isEmpty()) {
                upflag = "0";
                if (!"0".equals(index)) {
                    sendData();
                }
            } else {
                baseFinish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private NetworkConnection scanTask, scanTaskup;
    private String storeid, taskid, pid, p_batch, outlet_batch, store_id, store_name, project_id,
            project_name, store_num, brand, task_pack_name;
    private Intent scanData;
    private String newtask;//判断是否是新手任务 1是0否
    private MyListView listView;
    private ScanTaskAdapter scanTaskAdapter;
    private ArrayList<ScanTaskInfo> list = new ArrayList<>();
    private ArrayList<String> codeList = new ArrayList<>();//待扫描商品的条码
    private String taskname, note, executeid, batch, invalid;
    private final static int RESULT_SERIES_CODE = 1;
    private final static int RESULT_SINGLE_CODE = 2;
    private ArrayList<String> scanedCode = new ArrayList<>();//所有已扫到的码
    private TextView scantask_num;
    private String upflag, barcodelist;//0为返回1为提交任务，2为重做
    private UpdataDBHelper updataDBHelper;
    private AppDBHelper appDBHelper;
    private TextView scantask_unscan;
    private String index;//任务预览
    private RelativeLayout lin_alls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_task_new);
        updataDBHelper = new UpdataDBHelper(this);
        appDBHelper = new AppDBHelper(this);
        initNetwork();
        scanData = getIntent();
        if (scanData == null) {
            return;
        }
        index = scanData.getStringExtra("index");
        initView();
        project_id = scanData.getStringExtra("project_id");
        project_name = scanData.getStringExtra("project_name");
        store_num = scanData.getStringExtra("store_num");
        brand = scanData.getStringExtra("brand");
        storeid = scanData.getStringExtra("store_id");
        taskid = scanData.getStringExtra("task_id");
        pid = scanData.getStringExtra("task_pack_id");
        task_pack_name = scanData.getStringExtra("task_pack_name");
        p_batch = scanData.getStringExtra("p_batch");
        outlet_batch = scanData.getStringExtra("outlet_batch");
        store_name = scanData.getStringExtra("store_name");
        store_id = scanData.getStringExtra("store_id");
        newtask = scanData.getStringExtra("newtask");
        appDBHelper.deletePhotoUrl(project_id, storeid, taskid);
        scantask_unscan = (TextView) findViewById(R.id.scantask_unscan);
        ((ScrollView) findViewById(R.id.scantask_scroll)).smoothScrollTo(0, 20);
        listView = (MyListView) findViewById(R.id.scantask_listview);
        lin_alls = (RelativeLayout) findViewById(R.id.lin_alls);
        scantask_num = (TextView) findViewById(R.id.scantask_num);
        scanTaskAdapter = new ScanTaskAdapter(this, list);
        listView.setAdapter(scanTaskAdapter);
        listView.setOnItemClickListener(this);
        getData();
        findViewById(R.id.series_scan).setOnClickListener(this);
        findViewById(R.id.single_scan).setOnClickListener(this);
        findViewById(R.id.scan_finish).setOnClickListener(this);
        if (index != null && "0".equals(index)) {
            lin_alls.setVisibility(View.GONE);
        } else {
            lin_alls.setVisibility(View.VISIBLE);
        }
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
                        ((TextView) findViewById(R.id.scantask_name)).setText(taskname);
                        ((TextView) findViewById(R.id.scantask_desc)).setText(note);
                        executeid = jsonObject.getString("executeid");
                        invalid = jsonObject.getString("invalid");
                        batch = jsonObject.getString("batch");
                        int temp = 0;
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            ScanTaskInfo scanTaskInfo = new ScanTaskInfo();
                            scanTaskInfo.setBarcode(object.getString("barcode"));
                            scanTaskInfo.setName(object.getString("name"));
                            scanTaskInfo.setSize(object.getString("size"));
                            scanTaskInfo.setPicurl(object.getString("picurl"));
                            scanTaskInfo.setState(object.getString("state"));
                            if ("1".equals(scanTaskInfo.getState())) {
                                if (barcodelist == null) {
                                    barcodelist = object.getString("barcode");
                                } else {
                                    barcodelist = barcodelist + "," + object.getString("barcode");
                                }
                                temp++;
                            } else {
                                codeList.add(object.getString("barcode"));
                            }
                            scanTaskInfo.setTaskScanId(object.getString("taskScanId"));
                            list.add(scanTaskInfo);
                        }
                        scantask_num.setText("扫码商品" + temp + "/" + list.size());
                        scanTaskAdapter.notifyDataSetChanged();
                    } else {
                        Tools.showToast(ScanTaskNewActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(ScanTaskNewActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(ScanTaskNewActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        }, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scan_finish: {//扫码完成
                if (index != null && "0".equals(index)) {
                    Tools.showToast(ScanTaskNewActivity.this, "抱歉，预览时任务无法执行。");
                    return;
                }
                if (codeList.isEmpty()) {//全部扫描成功
                    upflag = "1";
                    sendData();
                    return;
                }
                if (!scanedCode.isEmpty()) {//已进行扫码
                    for (int i = 0; i < list.size(); i++) {
                        for (int j = 0; j < scanedCode.size(); j++) {
                            boolean contains = codeList.contains(list.get(i).getBarcode());
                            boolean temp = (list.get(i).getBarcode()).equals(scanedCode.get(j));
                            Tools.d("======" + contains);
                            Tools.d("======2" + temp);
                            if (!temp && contains) {
                                if ("1".equals(invalid)) {//可置无效
                                    list.get(i).setState("2");
                                } else {
                                    list.get(i).setState("3");
                                }
                            }
                        }
                    }
                } else {//未进行扫码
                    for (int i = 0; i < list.size(); i++) {
                        for (int j = 0; j < codeList.size(); j++) {
                            if (list.get(i).getBarcode().equals(codeList.get(j))) {
                                if ("1".equals(invalid)) {//可置无效
                                    list.get(i).setState("2");
                                } else {
                                    list.get(i).setState("3");
                                }
                            }
                        }
                    }
                }
                scantask_unscan.setText("您还有" + codeList.size() + "个商品未扫描完成");
                scantask_unscan.setVisibility(View.VISIBLE);
                findViewById(R.id.scantask_view).setVisibility(View.GONE);
                scanTaskAdapter.notifyDataSetChanged();
                if ("0".equals(invalid)) {
                    ConfirmDialog.showDialog(this, "提示！", 3, "您有" + codeList.size() + "个未扫描商品", "再找找",
                            "我知道了", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                @Override
                                public void leftClick(Object object) {

                                }

                                @Override
                                public void rightClick(Object object) {
                                    upflag = "1";
                                    sendData();
                                }
                            }).goneLeft();
                }
            }
            break;
            case R.id.series_scan: {//连续扫码
                scanData.setClass(ScanTaskNewActivity.this, CaptureActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("codeList", codeList);
                scanData.putExtra("data", bundle);
                scanData.putExtra("flag", "1");//扫码任务
                startActivityForResult(scanData, RESULT_SERIES_CODE);
            }
            break;
            case R.id.single_scan: {//单个扫码
                scanData.setClass(ScanTaskNewActivity.this, CaptureActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("codeList", codeList);
                scanData.putExtra("data", bundle);
                scanData.putExtra("flag", "1");//扫码任务
                startActivityForResult(scanData, RESULT_SINGLE_CODE);
            }
            break;
        }
    }

    private void sendData() {
        if (scanedCode != null && !scanedCode.isEmpty()) {
            for (String temp : scanedCode) {
                if (TextUtils.isEmpty(barcodelist)) {
                    barcodelist = temp.trim();
                } else {
                    barcodelist = barcodelist + "," + temp.trim();
                }
            }
        }
        Tools.d("已扫到的码：" + barcodelist);
        scanTaskup.sendPostRequest(Urls.ScanTaskup, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if ("0".equals(upflag)) {//返回
                            baseFinish();
                        } else if ("1".equals(upflag)) {//任务提交
                            String username = AppInfo.getName(ScanTaskNewActivity.this);
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("executeid", executeid);
                            map.put("usermobile", username);//使用invalid代替category1区分是不是需要拼接fileurl
                            updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, brand,
                                    store_id, store_name, pid,
                                    task_pack_name, "222", taskid, taskname, invalid, null, null,
                                    username + project_id + store_id + pid + taskid, Urls.ScanTaskComplete,
                                    null, null, UpdataDBHelper.Updata_file_type_video,
                                    map, null, true, Urls.ScanTaskup, paramsToString(), false);
                            Intent service = new Intent("com.orange.oy.UpdataNewService");
                            service.setPackage("com.orange.oy");
                            startService(service);
                            TaskitemDetailActivity_12.isRefresh = true;
                            TaskitemDetailActivity_12.taskid = taskid;
                            TaskFinishActivity.isRefresh = true;
                            TaskitemListActivity.isRefresh = true;
                            TaskitemListActivity_12.isRefresh = true;
                            goStep();
                        }
                    } else {
                        Tools.showToast(ScanTaskNewActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(ScanTaskNewActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(ScanTaskNewActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
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
                    intent.putExtra("photo_compression", taskNewInfo.getPhoto_compression());
                    if ("1".equals(type) || "8".equals(type)) {//拍照任务
                        intent.setClass(ScanTaskNewActivity.this, TaskitemPhotographyNextYActivity.class);
                        startActivity(intent);
                    } else if ("2".equals(type)) {//视频任务
                        intent.setClass(ScanTaskNewActivity.this, TaskitemShotActivity.class);
                        startActivity(intent);
                    } else if ("3".equals(type)) {//记录任务
                        intent.setClass(ScanTaskNewActivity.this, TaskitemEditActivity.class);
                        startActivity(intent);
                    } else if ("4".equals(type)) {//定位任务
                        intent.setClass(ScanTaskNewActivity.this, TaskitemMapActivity.class);
                        startActivity(intent);
                    } else if ("5".equals(type)) {//录音任务
                        intent.setClass(ScanTaskNewActivity.this, TaskitemRecodillustrateActivity.class);
                        startActivity(intent);
                    } else if ("6".equals(type)) {//扫码任务
                        intent.setClass(ScanTaskNewActivity.this, ScanTaskNewActivity.class);
                        startActivity(intent);
                    }
                }
            }
        }
        baseFinish();
    }

    private String paramsToString() {
        Map<String, String> params = new HashMap<>();
        params.put("executeid", executeid);
        params.put("barcodelist", barcodelist + "");
        params.put("usermobile", AppInfo.getName(ScanTaskNewActivity.this));
        params.put("taskbatch", batch);
        params.put("upflag", upflag);
        params.put("storeid", storeid);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_SINGLE_CODE: {//单个扫码任务
                    boolean isSuccess = data.getBooleanExtra("isSuccess", false);
                    String content = data.getStringExtra("codedContent");
                    if (isSuccess) {//扫码成功 更新UI
                        scanedCode.add(content);
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getBarcode().equals(content)) {
                                list.get(i).setState("1");
                            }
                        }
                        scanTaskAdapter.notifyDataSetChanged();
                        codeList.remove(data.getStringExtra("codedContent"));
                        scantask_num.setText("扫码商品" + scanedCode.size() + "/" + list.size());
                    } else {//扫码失败 继续扫码
                        scanData.setClass(ScanTaskNewActivity.this, CaptureActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("codeList", codeList);
                        scanData.putExtra("data", bundle);
                        scanData.putExtra("flag", "1");//扫码任务
                        startActivityForResult(scanData, RESULT_SINGLE_CODE);
                    }
                }
                break;
                case RESULT_SERIES_CODE: {
                    boolean isSuccess = data.getBooleanExtra("isSuccess", false);
                    String content = data.getStringExtra("codedContent");
                    if (isSuccess) {//扫码成功 更新UI
                        scanedCode.add(content);
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getBarcode().equals(content)) {
                                list.get(i).setState("1");
                            }
                        }
                        scanTaskAdapter.notifyDataSetChanged();
                        codeList.remove(data.getStringExtra("codedContent"));
                        scantask_num.setText("扫码商品" + scanedCode.size() + "/" + list.size());
                    }
                    scanData.setClass(ScanTaskNewActivity.this, CaptureActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("codeList", codeList);
                    scanData.putExtra("data", bundle);
                    scanData.putExtra("flag", "1");//扫码任务
                    startActivityForResult(scanData, RESULT_SERIES_CODE);
                }
                break;
            }
        } else if (resultCode == AppInfo.ShotSuccessResultCode) {
            if (requestCode == AppInfo.TaskitemShotRequestCodeForShot) {
                int position = data.getIntExtra("position", 0);
                codeList.remove(list.get(position).getBarcode());
                scantask_unscan.setText("您还有" + codeList.size() + "个商品未扫描完成");
                list.get(position).setState("3");
                if (scanTaskAdapter != null) {
                    scanTaskAdapter.notifyDataSetChanged();
                }
                num++;
                upDataVideo(data.getStringExtra("path"), list.get(position).getTaskScanId());
            }
        }
    }

    private int num = 0;//视频数量

    private void upDataVideo(String path, String taskScanId) {//置无效视频记录
        String username = AppInfo.getName(ScanTaskNewActivity.this);
        updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, taskScanId,
                store_id, store_name, pid,
                task_pack_name, "22", taskid, taskname, null, null, null,
                username + project_id + store_id + pid + taskid + num, null,
                "key", path, UpdataDBHelper.Updata_file_type_video,
                null, null, false, null, null, false);
        appDBHelper.addPhotoUrlRecord(username, project_id, store_id, taskid, path, null);
        appDBHelper.setFileNum(path, num + "");
        Intent service = new Intent("com.orange.oy.UpdataNewService");
        service.setPackage("com.orange.oy");
        startService(service);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (scanTaskAdapter != null) {
            if (scanTaskAdapter.isclick() && "2".equals(list.get(position).getState())) {
                Intent intent = new Intent(this, ShotActivity.class);
                intent.putExtra("index", 1);
                intent.putExtra("position", position);
                intent.putExtra("fileName", Tools.getTimeSS() + Tools.getDeviceId(ScanTaskNewActivity.this) + taskid);
                intent.putExtra("dirName", AppInfo.getName(this) + "/" + project_id + store_id + pid + taskid +
                        Tools.toByte(project_id));
                startActivityForResult(intent, AppInfo.TaskitemShotRequestCodeForShot);
            }
            scanTaskAdapter.isclick = false;
        }
    }
}
