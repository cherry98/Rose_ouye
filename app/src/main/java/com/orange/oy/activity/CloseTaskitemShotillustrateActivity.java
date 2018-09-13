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
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.OfflineDBHelper;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 视频任务说明页
 */
public class CloseTaskitemShotillustrateActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View
        .OnClickListener {

    private void initTitle(String str) {
        AppTitle appTitle = (AppTitle) findViewById(R.id.taskitmshotill_title);
        appTitle.settingName("视频任务");
        appTitle.showBack(this);
    }

    public void onBack() {
        TaskitemDetailActivity_12.isRefresh = true;
        baseFinish();
    }

    private void initNetworkConnection() {
        Closepackagetask = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("tasktype", "2");
                params.put("taskid", task_id);
                params.put("token", Tools.getToken());
                return params;
            }
        };
        Closepackagetask.setIsShowDialog(true);
    }

    private TextView taskitmshotill_desc, taskitmshotill_name, taskitemshotill_video_title;
    private ImageView taskitmshotill_shotimg;
    private View taskitemshotill_video_layout;
    private NetworkConnection Closepackagetask;
    private String task_id, project_id, store_id, task_pack_id;
    private Intent data;
    private OfflineDBHelper offlineDBHelper;
    private boolean isOffline;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitemshotillustrate);
        initNetworkConnection();
        offlineDBHelper = new OfflineDBHelper(this);
        data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        initTitle(data.getStringExtra("task_name"));
        project_id = data.getStringExtra("project_id");
        store_id = data.getStringExtra("store_id");
        task_id = data.getStringExtra("task_id");
        task_pack_id = data.getStringExtra("task_pack_id");
        isOffline = data.getBooleanExtra("isOffline", false);
        taskitmshotill_desc = (TextView) findViewById(R.id.taskitmshotill_desc);
        taskitmshotill_name = (TextView) findViewById(R.id.taskitmshotill_name);
        taskitmshotill_shotimg = (ImageView) findViewById(R.id.taskitmshotill_shotimg);
        taskitemshotill_video_title = (TextView) findViewById(R.id.taskitemshotill_video_title);
        taskitemshotill_video_layout = findViewById(R.id.taskitemshotill_video_layout);
        findViewById(R.id.taskitmshotill_radiolayout).setVisibility(View.INVISIBLE);
        findViewById(R.id.taskitmshotill_button2).setVisibility(View.GONE);
        taskitmshotill_shotimg.setOnClickListener(this);
        View butotn = findViewById(R.id.taskitmshotill_button);
        butotn.setLayoutParams(findViewById(R.id.taskitmshotill_button3).getLayoutParams());
        butotn.setOnClickListener(this);
        findViewById(R.id.taskitmshotill_shot_play).setOnClickListener(this);
        if (isOffline) {
            SelectvideoOffline();
        } else {
            Selectvideo();
        }
    }

    private String outlet_batch = null, p_batch = null;

    private void SelectvideoOffline() {
        String username = AppInfo.getName(this);
        String result = offlineDBHelper.getTaskDetail(username, project_id, store_id, task_pack_id, task_id);
        outlet_batch = offlineDBHelper.getTaskOutletBatch(username, project_id, store_id, task_pack_id, task_id);
        p_batch = offlineDBHelper.getTaskPBatch(username, project_id, store_id, task_pack_id, task_id);
        try {
            JSONObject jsonObject = new JSONObject(result);
            taskitmshotill_name.setText(jsonObject.getString("taskName"));
            String url = jsonObject.getString("url");
            if (TextUtils.isEmpty(url) || url.equals("null")) {
                taskitemshotill_video_title.setText("示例:无");
                taskitemshotill_video_layout.setVisibility(View.GONE);
            } else {
                String urlstr = jsonObject.getString("url");
                taskitmshotill_shotimg.setTag(offlineDBHelper.getDownPath(AppInfo.getName(this), project_id, store_id,
                        task_pack_id, task_id, urlstr));
            }
            new CloseTaskitemShotillustrateActivity.getVideoThumbnail().execute(new Object[]{taskitmshotill_shotimg.getTag()});
            taskitmshotill_desc.setText(jsonObject.getString("note"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void Selectvideo() {
        Closepackagetask.sendPostRequest(Urls.Closepackagetask, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        String task_name = jsonObject.getString("task_name");
                        data.putExtra("task_name", task_name);
                        taskitmshotill_name.setText(task_name);
                        String url = jsonObject.getString("video_url");
                        if (TextUtils.isEmpty(url) || url.equals("null")) {
                            taskitemshotill_video_title.setText("示例:无");
                            taskitemshotill_video_layout.setVisibility(View.GONE);
                        } else {
                            taskitmshotill_shotimg.setTag(url);
                        }
                        new getVideoThumbnail().execute(new Object[]{taskitmshotill_shotimg.getTag()});
                        taskitmshotill_desc.setText(jsonObject.getString("task_note"));
                    } else {
                        Tools.showToast(CloseTaskitemShotillustrateActivity.this, jsonObject.getString("msg"));
                    }
                    CustomProgressDialog.Dissmiss();
                } catch (JSONException e) {
                    CustomProgressDialog.Dissmiss();
                    Tools.showToast(CloseTaskitemShotillustrateActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(CloseTaskitemShotillustrateActivity.this, getResources().getString(R.string.network_error));
            }
        });
    }

    class getVideoThumbnail extends AsyncTask {
        protected Object doInBackground(Object[] params) {
            try {
                String url = params[0].toString();
                return Tools.createVideoThumbnail(url, 400, 300);
            } catch (Exception e) {
                return null;
            }
        }

        protected void onPostExecute(Object o) {
            if (o != null) {
                if (taskitmshotill_shotimg != null) {
                    taskitmshotill_shotimg.setImageBitmap((Bitmap) o);
                }
            }
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taskitmshotill_button: {
                data.setClass(this, CloseTaskitemShotActivity.class);
                if (isOffline) {
                    data.putExtra("outlet_batch", outlet_batch);
                    data.putExtra("p_batch", p_batch);
                }
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
