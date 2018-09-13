package com.orange.oy.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.adapter.Video2Adapter;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.MyGridView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.orange.oy.R.id.taskphoto_gridview;

/**
 * 视频任务说明页
 */
public class TaskitemShotillustrateActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View
        .OnClickListener {

    private void initTitle(String str) {
        AppTitle appTitle = (AppTitle) findViewById(R.id.taskitmshotill_title);
        appTitle.settingName("视频任务");
        appTitle.showBack(this);
    }

    public void onBack() {
        baseFinish();
    }

    private void initNetworkConnection() {
        Selectvideo = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("task_pack_id", task_pack_id);
                params.put("task_id", task_id);
                params.put("token", Tools.getToken());
                return params;
            }
        };
        Selectvideo.setIsShowDialog(true);
    }

    private TextView taskitmshotill_desc, taskitmshotill_name, taskitemshotill_video_title;
    private ImageView taskitmshotill_shotimg;
    private View taskitemshotill_video_layout;
    private NetworkConnection Selectvideo;
    private String task_pack_id, task_id;
    private Intent data;
    private View taskitmshotill_button2, taskitmshotill_button;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitemshotillustrate);
        initNetworkConnection();
        data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        initTitle(data.getStringExtra("task_name"));
        task_pack_id = data.getStringExtra("task_pack_id");
        task_id = data.getStringExtra("task_id");
        taskitmshotill_desc = (TextView) findViewById(R.id.taskitmshotill_desc);
        taskitmshotill_name = (TextView) findViewById(R.id.taskitmshotill_name);
        taskitmshotill_shotimg = (ImageView) findViewById(R.id.taskitmshotill_shotimg);
        taskphoto_gridview = (MyGridView) findViewById(R.id.taskphoto_gridview);
        taskitemshotill_video_title = (TextView) findViewById(R.id.taskitemshotill_video_title);
        taskitemshotill_video_layout = findViewById(R.id.taskitemshotill_video_layout);
        taskitmshotill_button = findViewById(R.id.taskitmshotill_button);
        taskitmshotill_button2 = findViewById(R.id.taskitmshotill_button2);
        taskitmshotill_shotimg.setOnClickListener(this);
        findViewById(R.id.taskitmshotill_shot_play).setOnClickListener(this);
        Selectvideo();
    }

    private String batch;
    private Video2Adapter video2Adapter;
    private MyGridView taskphoto_gridview;

    private void Selectvideo() {
        Selectvideo.sendPostRequest(Urls.Selectvideo, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        jsonObject = jsonObject.getJSONObject("datas");
                        batch = jsonObject.getString("batch");
                        taskitmshotill_name.setText(jsonObject.getString("taskName"));
                        String url = jsonObject.getString("url");
                        if (TextUtils.isEmpty(url) || url.equals("null")) {
                            taskitemshotill_video_title.setVisibility(View.GONE);
                            taskitemshotill_video_layout.setVisibility(View.GONE);
                        } else {
                            taskitemshotill_video_title.setVisibility(View.VISIBLE);
                            String[] arr = url.split(",");
                            List list = Arrays.asList(arr);
                            video2Adapter = new Video2Adapter(TaskitemShotillustrateActivity.this, list);
                            taskphoto_gridview.setAdapter(video2Adapter);
                        }
                        taskitmshotill_desc.setText(jsonObject.getString("note"));
                        taskitmshotill_button.setOnClickListener(TaskitemShotillustrateActivity.this);
                        taskitmshotill_button2.setOnClickListener(TaskitemShotillustrateActivity.this);
                    } else {
                        Tools.showToast(TaskitemShotillustrateActivity.this, jsonObject.getString("msg"));
                    }
                    CustomProgressDialog.Dissmiss();
                } catch (JSONException e) {
                    CustomProgressDialog.Dissmiss();
                    Tools.showToast(TaskitemShotillustrateActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskitemShotillustrateActivity.this, getResources().getString(R.string.network_error));
            }
        });
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taskitmshotill_button: {
                data.setClass(this, TaskitemShotActivity.class);
                data.putExtra("isHad", true);
                data.putExtra("batch", batch);
                startActivity(data);
                baseFinish();
            }
            break;
            case R.id.taskitmshotill_button2: {
                data.setClass(this, TaskitemShotActivity.class);
                data.putExtra("isHad", false);
                data.putExtra("batch", batch);
                startActivity(data);
                baseFinish();
            }
            break;
            case R.id.taskitmshotill_shot_play: {
                Intent intent = new Intent(this, VideoViewActivity.class);
                intent.putExtra("path", taskitmshotill_shotimg.getTag().toString());
                startActivity(intent);
            }
            break;
        }
    }
}
