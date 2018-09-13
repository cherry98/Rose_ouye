package com.orange.oy.activity.scan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.karics.library.zxing.android.CaptureActivity;
import com.orange.oy.R;
import com.orange.oy.activity.TaskFinishActivity;
import com.orange.oy.activity.TaskitemDetailActivity;
import com.orange.oy.activity.TaskitemDetailActivity_12;
import com.orange.oy.activity.TaskitemListActivity;
import com.orange.oy.activity.TaskitemListActivity_12;
import com.orange.oy.adapter.ScanTaskAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.AppDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.ScanTaskInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.MyListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 扫码任务重做页
 */
public class ScanTaskResetActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle,
        View.OnClickListener, AppTitle.OnExitClickForAppTitle, AdapterView.OnItemClickListener {
    private AppTitle appTitle;

    private void initTitle() {
        appTitle = (AppTitle) findViewById(R.id.scantaskreset_title);
        appTitle.settingName("扫码任务");
        appTitle.showBack(this);
    }

    public void initNetworkConnection() {
        scan = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("storeid", store_id);
                params.put("token", Tools.getToken());
                params.put("pid", task_pack_id);
                params.put("p_batch", p_batch);
                params.put("outlet_batch", outlet_batch);
                params.put("taskid", taskid);
                return params;
            }
        };
        scan.setIsShowDialog(true);
        scanTaskup = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("executeid", executeid);
                params.put("barcodelist", barcodelist);
                params.put("usermobile", AppInfo.getName(ScanTaskResetActivity.this));
                params.put("taskbatch", batch);
                params.put("upflag", "2");
                params.put("storeid", store_id);
                return params;
            }
        };
        scanTaskup.setIsShowDialog(true);
    }

    private NetworkConnection scan, scanTaskup;
    private String store_id, task_pack_id, p_batch, outlet_batch, taskid, barcodelist, batch, executeid;
    private TextView scantask_name;
    private ArrayList<ScanTaskInfo> list1, list2;
    private ArrayList<String> codeList, scanedCode;
    private ScanTaskAdapter scanTaskAdapter1, scanTaskAdapter2;
    private MyListView scantaskreset_listview1, scantaskreset_listview2;
    private TextView scantaskreset_sccuess, scantaskreset_unsccuess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_task_reset);
        list1 = new ArrayList<>();
        list2 = new ArrayList<>();
        codeList = new ArrayList<>();
        scanedCode = new ArrayList<>();
        initTitle();
        Intent data = getIntent();
        if (data == null) {
            return;
        }
        initNetworkConnection();
        store_id = data.getStringExtra("store_id");
        task_pack_id = data.getStringExtra("task_pack_id");
        p_batch = data.getStringExtra("p_batch");
        outlet_batch = data.getStringExtra("outlet_batch");
        taskid = data.getStringExtra("task_id");
        scantask_name = (TextView) findViewById(R.id.scantaskreset_name);
        scantaskreset_listview1 = (MyListView) findViewById(R.id.scantaskreset_listview1);
        scantaskreset_listview2 = (MyListView) findViewById(R.id.scantaskreset_listview2);
        scantaskreset_sccuess = (TextView) findViewById(R.id.scantaskreset_sccuess);
        scantaskreset_unsccuess = (TextView) findViewById(R.id.scantaskreset_unsccuess);
        ((ScrollView) findViewById(R.id.scantaskreset_scroll)).smoothScrollTo(0, 20);
        getData();
        scanTaskAdapter1 = new ScanTaskAdapter(this, list1);
        scantaskreset_listview1.setAdapter(scanTaskAdapter1);
        scanTaskAdapter2 = new ScanTaskAdapter(this, list2);
        scantaskreset_listview2.setAdapter(scanTaskAdapter2);
        scantaskreset_listview2.setOnItemClickListener(this);
        scantaskreset_sccuess.setOnClickListener(this);
        scantaskreset_unsccuess.setOnClickListener(this);
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    public void getData() {
        scan.sendPostRequest(Urls.TaskFinish, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (!list1.isEmpty()) {
                        list1.clear();
                    }
                    if (!list2.isEmpty()) {
                        list2.clear();
                    }
                    if (jsonObject.getInt("code") == 200) {
                        batch = jsonObject.getString("batch");
                        executeid = jsonObject.getString("executeid");
                        scantask_name.setText(jsonObject.getString("task_name"));
                        String scannum = jsonObject.getString("scannum");//已扫描的数量
                        scantaskreset_sccuess.setText("成功扫码" + scannum + "件");
                        String unscannum = jsonObject.getString("unscannum");//未扫描的数量
                        scantaskreset_unsccuess.setText("未成功扫码" + unscannum + "件");
                        JSONArray jsonArray1 = jsonObject.getJSONArray("success_standard");//已扫描详情
                        if (jsonArray1 != null) {
                            for (int i = 0; i < jsonArray1.length(); i++) {
                                JSONObject object = jsonArray1.getJSONObject(i);
                                ScanTaskInfo scanTaskInfo = new ScanTaskInfo();
                                scanTaskInfo.setBarcode(object.getString("barcode"));
                                scanTaskInfo.setName(object.getString("name"));
                                scanTaskInfo.setPicurl(object.getString("picurl"));
                                scanTaskInfo.setSize(object.getString("size"));
                                list1.add(scanTaskInfo);
                            }
                        }
                        JSONArray jsonArray2 = jsonObject.getJSONArray("standard");//未扫描详情
                        if (jsonArray2 != null) {
                            for (int i = 0; i < jsonArray2.length(); i++) {
                                JSONObject object = jsonArray2.getJSONObject(i);
                                ScanTaskInfo scanTaskInfo = new ScanTaskInfo();
                                scanTaskInfo.setBarcode(object.getString("barcode"));
                                scanTaskInfo.setName(object.getString("name"));
                                scanTaskInfo.setPicurl(object.getString("picurl"));
                                scanTaskInfo.setSize(object.getString("size"));
                                codeList.add(object.getString("barcode"));
                                list2.add(scanTaskInfo);
                            }
                        }
                        if (scanTaskAdapter1 != null) {
                            scanTaskAdapter1.notifyDataSetChanged();
                        }
                        if (scanTaskAdapter2 != null) {
                            scanTaskAdapter2.notifyDataSetChanged();
                        }
                    } else {
                        Tools.showToast(ScanTaskResetActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(ScanTaskResetActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(ScanTaskResetActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        }, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scantaskreset_sccuess: {
                scantaskreset_sccuess.setTextColor(getResources().getColor(R.color.homepage_select));
                scantaskreset_unsccuess.setTextColor(getResources().getColor(R.color.homepage_notselect));
                findViewById(R.id.scantaskreset_sccuessview).setVisibility(View.VISIBLE);
                findViewById(R.id.scantaskreset_unsccuessview).setVisibility(View.INVISIBLE);
                ((TextView) findViewById(R.id.scantaskreset_list)).setText("成功扫码的商品列表：");
                scantaskreset_listview1.setVisibility(View.VISIBLE);
                scantaskreset_listview2.setVisibility(View.GONE);
                appTitle.hideExit();
                findViewById(R.id.scantaskreset_button).setVisibility(View.GONE);
                findViewById(R.id.scantaskreset_button).setOnClickListener(null);
            }
            break;
            case R.id.scantaskreset_unsccuess: {
                scantaskreset_sccuess.setTextColor(getResources().getColor(R.color.homepage_notselect));
                scantaskreset_unsccuess.setTextColor(getResources().getColor(R.color.homepage_select));
                findViewById(R.id.scantaskreset_sccuessview).setVisibility(View.INVISIBLE);
                findViewById(R.id.scantaskreset_unsccuessview).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.scantaskreset_list)).setText("未找到的商品列表：");
                scantaskreset_listview1.setVisibility(View.GONE);
                scantaskreset_listview2.setVisibility(View.VISIBLE);
                appTitle.settingExit("编辑", getResources().getColor(R.color.homepage_select), ScanTaskResetActivity.this);
            }
            break;
            case R.id.scantaskreset_button: {
                if (scanedCode.isEmpty()) {
                    Tools.showToast(this, "请先扫码再进行提交~");
                    return;
                }
                sendData();
            }
            break;
        }
    }

    private void sendData() {
        barcodelist = scanedCode.toString().trim().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", "");
        Tools.d("已扫到的码：" + barcodelist);
        scanTaskup.sendPostRequest(Urls.ScanTaskup, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        baseFinish();
                    } else {
                        Tools.showToast(ScanTaskResetActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(ScanTaskResetActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(ScanTaskResetActivity.this, getResources().getString(R.string.network_batch_error));
            }
        });
    }

    @Override
    public void onExit() {
        findViewById(R.id.scantaskreset_button).setVisibility(View.VISIBLE);
        findViewById(R.id.scantaskreset_button).setOnClickListener(this);
        if (!list2.isEmpty()) {
            for (int i = 0; i < list2.size(); i++) {
                list2.get(i).setState("4");
            }
            if (scanTaskAdapter2 != null) {
                scanTaskAdapter2.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (scanTaskAdapter2 != null) {
            if (scanTaskAdapter2.isclick && "4".equals(list2.get(position).getState())) {
                Intent intent = new Intent(ScanTaskResetActivity.this, CaptureActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("codeList", codeList);
                intent.putExtra("data", bundle);
                intent.putExtra("flag", "1");//扫码任务
                startActivityForResult(intent, 0);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0) {
            boolean isSuccess = data.getBooleanExtra("isSuccess", false);
            String content = data.getStringExtra("codedContent");
            if (isSuccess) {//扫码成功 更新UI
                scanedCode.add(content);
                for (int i = 0; i < list2.size(); i++) {
                    if (list2.get(i).getBarcode().equals(content)) {
                        list2.get(i).setState("1");
                    }
                }
                scanTaskAdapter2.notifyDataSetChanged();
                codeList.remove(data.getStringExtra("codedContent"));
            } else {//扫码失败 继续扫码
                Intent intent = new Intent(ScanTaskResetActivity.this, CaptureActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("codeList", codeList);
                intent.putExtra("data", bundle);
                intent.putExtra("flag", "1");//扫码任务
                startActivityForResult(intent, 0);
            }
        }
    }
}
