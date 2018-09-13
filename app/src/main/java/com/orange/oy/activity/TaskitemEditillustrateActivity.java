package com.orange.oy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.adapter.TaskitemReqPgAdapter;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.SelecterDialog;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.photoview.PhotoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 问卷任务说明页
 */
public class TaskitemEditillustrateActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View
        .OnClickListener, AdapterView.OnItemClickListener {

    private void initTitle(String str) {
        AppTitle appTitle = (AppTitle) findViewById(R.id.taskitmeditill_title);
        appTitle.settingName("问卷任务");
        appTitle.showBack(this);
    }

    public void onBack() {
        baseFinish();
    }

    protected void onStop() {
        super.onStop();
        if (Recorddesc != null) {
            Recorddesc.stop(Urls.Recorddesc);
        }
    }

    private void initNetworkConnection() {
        Recorddesc = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("taskid", task_id);
                params.put("token", Tools.getToken());
                return params;
            }
        };
        Recorddesc.setIsShowDialog(true);
    }

    private TextView taskitmeditill_desc, taskitmeditill_name;
    private NetworkConnection Recorddesc;
    private Intent data;
    private String task_id;
    private GridView taskitmpg_gridview;
    private ArrayList<String> picList = new ArrayList<>();
    private TaskitemReqPgAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitemeditillustrate);
        initNetworkConnection();
        data = getIntent();
        initTitle(data.getStringExtra("task_name"));
        task_id = data.getStringExtra("taskid");
        taskitmeditill_desc = (TextView) findViewById(R.id.taskitmeditill_desc);
        taskitmeditill_name = (TextView) findViewById(R.id.taskitmeditill_name);
        taskitmpg_gridview = (GridView) findViewById(R.id.taskitmpg_gridview);
        adapter = new TaskitemReqPgAdapter(this, picList);
        taskitmpg_gridview.setAdapter(adapter);
        taskitmpg_gridview.setOnItemClickListener(this);
        findViewById(R.id.taskitmeditill_button).setOnClickListener(this);
        Recorddesc();
    }

    private String batch;

    private void Recorddesc() {
        Recorddesc.sendPostRequest(Urls.Recorddesc, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        taskitmeditill_name.setText(jsonObject.getString("task_name"));
                        taskitmeditill_desc.setText(jsonObject.getString("note"));
                        batch = jsonObject.getString("batch");
                        String picStr = jsonObject.getString("pics");
                        if (TextUtils.isEmpty(picStr) || "null".equals(picStr) || picStr.length() == 4) {
                            findViewById(R.id.shili).setVisibility(View.GONE);
                            taskitmpg_gridview.setVisibility(View.GONE);
                        } else {
                            picStr = picStr.substring(1, picStr.length() - 1);
                            String[] pics = picStr.split(",");
                            for (int i = 0; i < pics.length; i++) {
                                picList.add(Urls.ImgIp + pics[i].replaceAll("\"", "").replaceAll("\\\\", ""));
                            }
                            if (pics.length > 0) {
                                int t = (int) Math.ceil(pics.length / 3d);
                                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) taskitmpg_gridview.getLayoutParams();
                                lp.height = (int) ((Tools.getScreeInfoWidth(TaskitemEditillustrateActivity.this) -
                                        getResources().getDimension(R.dimen.taskphoto_gridview_mar) * 2 -
                                        getResources().getDimension(R.dimen.taskphoto_gridview_item_mar) * 2) / 3) * t;
                                taskitmpg_gridview.setLayoutParams(lp);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Tools.showToast(TaskitemEditillustrateActivity.this, jsonObject.getString("msg"));
                    }
                    CustomProgressDialog.Dissmiss();
                } catch (JSONException e) {
                    CustomProgressDialog.Dissmiss();
                    Tools.showToast(TaskitemEditillustrateActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskitemEditillustrateActivity.this, getResources().getString(R.string.network_error));
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taskitmeditill_button: {
                data.setClass(this, TaskitemEditActivity.class);
                data.putExtra("batch", batch);
                startActivity(data);
                baseFinish();
            }
            break;
        }
    }

    private ImageLoader imageLoader;

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (imageLoader == null) {
            imageLoader = new ImageLoader(this);
        }
        PhotoView imageView = new PhotoView(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageLoader.DisplayImage(picList.get(position), imageView);
        SelecterDialog.showView(this, imageView);
    }
}
