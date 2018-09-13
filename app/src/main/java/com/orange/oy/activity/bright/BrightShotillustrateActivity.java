package com.orange.oy.activity.bright;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.TaskitemShotActivity;
import com.orange.oy.activity.VideoViewActivity;
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

public class BrightShotillustrateActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle,
        View.OnClickListener {

    private void initTitle(String str) {
        AppTitle appTitle = (AppTitle) findViewById(R.id.brightshotill_title);
        appTitle.settingName("视频任务");
        appTitle.showBack(this);
    }

    private Intent data;
    private TextView brightshotill_name, brightshotill_desc, brightshotill_video_title;
    private View brightshotill_video_layout;
    private ImageView brightshotill_shotimg;
    private Button brightshotill_button;
    private NetworkConnection Selectvideo;
    private String batch;
    private String task_id, taskpackid;

    private void initNetworkConnection() {
        Selectvideo = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("task_pack_id", "");
                params.put("task_id", task_id);
                params.put("token", Tools.getToken());
                return params;
            }
        };
        Selectvideo.setIsShowDialog(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bright_shotillustrate);
        initNetworkConnection();
        data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        initTitle(data.getStringExtra("taskName"));
        task_id = data.getIntExtra("taskid", 0) + "";
        brightshotill_name = (TextView) findViewById(R.id.brightshotill_name);
        brightshotill_desc = (TextView) findViewById(R.id.brightshotill_desc);
        brightshotill_video_title = (TextView) findViewById(R.id.brightshotill_video_title);
        brightshotill_video_layout = findViewById(R.id.brightshotill_video_layout);
        brightshotill_shotimg = (ImageView) findViewById(R.id.brightshotill_shotimg);
        brightshotill_button = (Button) findViewById(R.id.brightshotill_button);
        taskphoto_gridview = (MyGridView) findViewById(R.id.taskphoto_gridview);
        brightshotill_shotimg.setOnClickListener(this);
        findViewById(R.id.brightshotill_shot_play).setOnClickListener(this);
        Selectvideo();
    }

    private String taskName, note;
    private MyGridView taskphoto_gridview;
    private Video2Adapter video2Adapter;

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
                        taskName = jsonObject.getString("taskName");
                        brightshotill_name.setText(taskName);
                        taskpackid = jsonObject.getString("taskpackid");
                        String url = jsonObject.getString("url");
                        if (TextUtils.isEmpty(url) || url.equals("null")) {
                            brightshotill_video_title.setVisibility(View.GONE);
                            brightshotill_video_layout.setVisibility(View.GONE);
                        } else {
                            brightshotill_video_title.setVisibility(View.VISIBLE);
                            // brightshotill_shotimg.setTag(jsonObject.getString("url"));
                            String[] arr = url.split(",");
                            List list = Arrays.asList(arr);
                            video2Adapter = new Video2Adapter(BrightShotillustrateActivity.this, list);
                            taskphoto_gridview.setAdapter(video2Adapter);
                        }
                        note = jsonObject.getString("note");
                        brightshotill_desc.setText(note);
                        brightshotill_button.setOnClickListener(BrightShotillustrateActivity.this);
                    } else {
                        Tools.showToast(BrightShotillustrateActivity.this, jsonObject.getString("msg"));
                    }
                    CustomProgressDialog.Dissmiss();
                } catch (JSONException e) {
                    CustomProgressDialog.Dissmiss();
                    Tools.showToast(BrightShotillustrateActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(BrightShotillustrateActivity.this, getResources().getString(R.string.network_error));
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
            case R.id.brightshotill_shot_play: {
                Intent intent = new Intent(this, VideoViewActivity.class);
                intent.putExtra("path", brightshotill_shotimg.getTag().toString());
                startActivity(intent);
            }
            break;
            case R.id.brightshotill_button: {
                data.setClass(this, BrightShotActivity.class);
                data.putExtra("isHad", true);
                data.putExtra("batch", batch);
                data.putExtra("taskpackid", taskpackid);
                data.putExtra("taskName", taskName);
                data.putExtra("note", note);
                startActivity(data);
                baseFinish();
            }
            break;
        }
    }
}
