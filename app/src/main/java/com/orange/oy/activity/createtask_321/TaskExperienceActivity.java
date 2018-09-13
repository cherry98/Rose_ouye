package com.orange.oy.activity.createtask_321;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 网店体验任务 V3.21
 */
public class TaskExperienceActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {

    private String task_id, online_store_url, task_batch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_experience);
        AppTitle appTitle = (AppTitle) findViewById(R.id.taskexp_title);
        appTitle.settingName("网上体验任务");
        appTitle.showBack(this);
        task_id = getIntent().getStringExtra("task_id");
        getData();
        findViewById(R.id.taskexp_begin).setOnClickListener(this);
    }

    private void getData() {
        NetworkConnection experienceTaskInfo = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(TaskExperienceActivity.this));
                params.put("task_id", task_id);
                return params;
            }
        };
        experienceTaskInfo.sendPostRequest(Urls.ExperienceTaskInfo, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        jsonObject = jsonObject.getJSONObject("data");
                        if (jsonObject != null) {
                            ((TextView) findViewById(R.id.taskexp_name)).setText(jsonObject.getString("task_name"));
                            ((TextView) findViewById(R.id.taskexp_desc)).setText(jsonObject.getString("note"));
                            ((TextView) findViewById(R.id.taskexp_storename)).setText(jsonObject.getString("online_store_name"));
                            task_batch = jsonObject.getString("task_batch");
                            online_store_url = jsonObject.getString("online_store_url");
                        }
                    } else {
                        Tools.showToast(TaskExperienceActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskExperienceActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(TaskExperienceActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.taskexp_begin) {//开始逛店
            Intent intent = getIntent();
            intent.setClass(this, WebpageTaskActivity.class);
            intent.putExtra("online_store_url", online_store_url);
            intent.putExtra("task_bath", task_batch);
            startActivity(intent);
            baseFinish();
        }
    }
}
