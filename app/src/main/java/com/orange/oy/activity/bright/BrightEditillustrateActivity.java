package com.orange.oy.activity.bright;

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

public class BrightEditillustrateActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle,
        View.OnClickListener, AdapterView.OnItemClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.taskitmeditill_title);
        appTitle.settingName("问卷任务");
        appTitle.showBack(this);
    }

    @Override
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
                params.put("taskid", taskid);
                params.put("token", Tools.getToken());
                return params;
            }
        };
        Recorddesc.setIsShowDialog(true);
    }

    private Intent data;
    private NetworkConnection Recorddesc;
    private String taskid;
    private TextView taskitmeditill_desc, taskitmeditill_name;
    private GridView taskitmpg_gridview;
    private ArrayList<String> picList = new ArrayList<>();
    private TaskitemReqPgAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitemeditillustrate);
        data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        initTitle();
        initNetworkConnection();
        taskid = data.getIntExtra("taskid", 0) + "";
        taskitmeditill_desc = (TextView) findViewById(R.id.taskitmeditill_desc);
        taskitmeditill_name = (TextView) findViewById(R.id.taskitmeditill_name);
        taskitmpg_gridview = (GridView) findViewById(R.id.taskitmpg_gridview);
        adapter = new TaskitemReqPgAdapter(this, picList);
        taskitmpg_gridview.setAdapter(adapter);
        taskitmpg_gridview.setOnItemClickListener(this);
        findViewById(R.id.taskitmeditill_button).setOnClickListener(this);
        Recorddesc();
    }

    private String batch, taskName;

    private void Recorddesc() {
        Recorddesc.sendPostRequest(Urls.Recorddesc, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        taskName = jsonObject.getString("task_name");
                        taskitmeditill_name.setText(taskName);
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
                                lp.height = (int) ((Tools.getScreeInfoWidth(BrightEditillustrateActivity.this) -
                                        getResources().getDimension(R.dimen.taskphoto_gridview_mar) * 2 -
                                        getResources().getDimension(R.dimen.taskphoto_gridview_item_mar) * 2) / 3) * t;
                                taskitmpg_gridview.setLayoutParams(lp);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Tools.showToast(BrightEditillustrateActivity.this, jsonObject.getString("msg"));
                    }
                    CustomProgressDialog.Dissmiss();
                } catch (JSONException e) {
                    CustomProgressDialog.Dissmiss();
                    Tools.showToast(BrightEditillustrateActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(BrightEditillustrateActivity.this, getResources().getString(R.string.network_error));
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taskitmeditill_button: {
                data.setClass(this, BrightEditActivity.class);
                data.putExtra("batch", batch);
                data.putExtra("taskName", taskName);
                data.putExtra("tasktype", "3");
                startActivity(data);
                baseFinish();
            }
            break;
        }
    }

    private ImageLoader imageLoader;

    @Override
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
