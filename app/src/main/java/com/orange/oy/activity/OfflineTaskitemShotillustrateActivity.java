package com.orange.oy.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.OfflineDBHelper;
import com.orange.oy.view.AppTitle;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 视频任务说明页
 */
public class OfflineTaskitemShotillustrateActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View
        .OnClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.taskitmshotill_title);
        appTitle.settingName("视频任务");
        appTitle.showBack(this);
    }

    public void onBack() {
        baseFinish();
    }

    private TextView taskitmshotill_desc, taskitmshotill_name, taskitemshotill_video_title;
    private ImageView taskitmshotill_shotimg;
    private View taskitemshotill_video_layout;
    private String task_pack_id, task_id, store_id, task_name, task_pack_name, store_name, project_id, project_name;
    private String category1 = "", category2 = "", category3 = "";
    private String store_num;
    private OfflineDBHelper offlineDBHelper;
    private View taskitmshotill_button2, taskitmshotill_button;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitemshotillustrate);
        offlineDBHelper = new OfflineDBHelper(this);
        Intent data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        project_id = data.getStringExtra("project_id");
        project_name = data.getStringExtra("project_name");
        task_pack_id = data.getStringExtra("task_pack_id");
        task_id = data.getStringExtra("task_id");
        store_id = data.getStringExtra("store_id");
        store_num = data.getStringExtra("store_num");
        task_pack_name = data.getStringExtra("task_pack_name");
        store_name = data.getStringExtra("store_name");
        task_name = data.getStringExtra("task_name");
        category1 = data.getStringExtra("category1");
        category2 = data.getStringExtra("category2");
        category3 = data.getStringExtra("category3");
        initTitle();
        taskitmshotill_desc = (TextView) findViewById(R.id.taskitmshotill_desc);
        taskitmshotill_name = (TextView) findViewById(R.id.taskitmshotill_name);
        taskitmshotill_shotimg = (ImageView) findViewById(R.id.taskitmshotill_shotimg);
        taskitemshotill_video_title = (TextView) findViewById(R.id.taskitemshotill_video_title);
        taskitemshotill_video_layout = findViewById(R.id.taskitemshotill_video_layout);
        taskitmshotill_shotimg.setOnClickListener(this);
        taskitmshotill_button = findViewById(R.id.taskitmshotill_button);
        taskitmshotill_button2 = findViewById(R.id.taskitmshotill_button2);
        taskitmshotill_button.setOnClickListener(this);
        taskitmshotill_button2.setOnClickListener(this);
        findViewById(R.id.taskitmshotill_shot_play).setOnClickListener(this);
        Selectvideo();
    }

    private String batch, outlet_batch = null, p_batch = null;

    private void Selectvideo() {
        String username = AppInfo.getName(this);
        String result = offlineDBHelper.getTaskDetail(username, project_id, store_id, task_pack_id, task_id);
        outlet_batch = offlineDBHelper.getTaskOutletBatch(username, project_id, store_id, task_pack_id, task_id);
        p_batch = offlineDBHelper.getTaskPBatch(username, project_id, store_id, task_pack_id, task_id);
        try {
            JSONObject jsonObject = new JSONObject(result);
            batch = jsonObject.getString("batch");
            taskitmshotill_name.setText(jsonObject.getString("taskName"));
            String url = jsonObject.getString("url");
            if (TextUtils.isEmpty(url) || url.equals("null")) {
                taskitemshotill_video_title.setVisibility(View.GONE);
                taskitemshotill_video_layout.setVisibility(View.GONE);
            } else {
                String urlstr = jsonObject.getString("url");
                taskitmshotill_shotimg.setTag(offlineDBHelper.getDownPath(AppInfo.getName(this), project_id, store_id,
                        task_pack_id,
                        task_id, urlstr));
            }
            new getVideoThumbnail().execute(new Object[]{taskitmshotill_shotimg.getTag()});
            taskitmshotill_desc.setText(jsonObject.getString("note"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                if (outlet_batch == null || p_batch == null) {
                    Tools.showToast(this, getResources().getString(R.string.batch_error));
                    return;
                }
                Intent intent = new Intent(this, OfflineTaskitemShotActivity.class);
                intent.putExtra("project_id", project_id);
                intent.putExtra("project_name", project_name);
                intent.putExtra("task_pack_id", task_pack_id);
                intent.putExtra("task_id", task_id);
                intent.putExtra("store_id", store_id);
                intent.putExtra("store_num", store_num);
                intent.putExtra("store_name", store_name);
                intent.putExtra("task_name", task_name);
                intent.putExtra("task_pack_name", task_pack_name);
                intent.putExtra("category1", category1);
                intent.putExtra("category2", category2);
                intent.putExtra("category3", category3);
                intent.putExtra("isHad", true);
                intent.putExtra("batch", batch);
                intent.putExtra("outlet_batch", outlet_batch);
                intent.putExtra("p_batch", p_batch);
                startActivity(intent);
                baseFinish();
            }
            break;
            case R.id.taskitmshotill_button2: {
                if (outlet_batch == null || p_batch == null) {
                    Tools.showToast(this, getResources().getString(R.string.batch_error));
                    return;
                }
                Intent intent = new Intent(this, OfflineTaskitemShotActivity.class);
                intent.putExtra("project_id", project_id);
                intent.putExtra("project_name", project_name);
                intent.putExtra("task_pack_id", task_pack_id);
                intent.putExtra("task_id", task_id);
                intent.putExtra("store_id", store_id);
                intent.putExtra("store_num", store_num);
                intent.putExtra("store_name", store_name);
                intent.putExtra("task_name", task_name);
                intent.putExtra("task_pack_name", task_pack_name);
                intent.putExtra("category1", category1);
                intent.putExtra("category2", category2);
                intent.putExtra("category3", category3);
                intent.putExtra("isHad", false);
                intent.putExtra("batch", batch);
                intent.putExtra("outlet_batch", outlet_batch);
                intent.putExtra("p_batch", p_batch);
                startActivity(intent);
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
