package com.orange.oy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
 * 录音任务说明页
 */
public class OfflineTaskitemRecodillustrateActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View
        .OnClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.taskitmrecodill_title);
        appTitle.settingName("录音任务");
        appTitle.showBack(this);
    }

    public void onBack() {
        baseFinish();
    }

    protected void onStop() {
        super.onStop();
    }

    private TextView taskitmrecodill_desc, taskitmrecodill_name;
    private String task_pack_id, task_id, store_id, task_name, task_pack_name, store_name, project_id, project_name;
    private String category1 = "", category2 = "", category3 = "";
    private String store_num;
    private OfflineDBHelper offlineDBHelper;
    private Intent data;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitemrecodillustrate);
        offlineDBHelper = new OfflineDBHelper(this);
        initTitle();
        data = getIntent();
        project_id = data.getStringExtra("project_id");
        task_pack_id = data.getStringExtra("task_pack_id");
        task_id = data.getStringExtra("task_id");
        store_id = data.getStringExtra("store_id");
        taskitmrecodill_desc = (TextView) findViewById(R.id.taskitmrecodill_desc);
        taskitmrecodill_name = (TextView) findViewById(R.id.taskitmrecodill_name);
        findViewById(R.id.taskitmrecodill_button).setOnClickListener(this);
        getData();
    }

    private String batch;
    private String outlet_batch = null, p_batch = null;

    private void getData() {
        String username = AppInfo.getName(this);
        String result = offlineDBHelper.getTaskDetail(username, project_id, store_id, task_pack_id, task_id);
        outlet_batch = offlineDBHelper.getTaskOutletBatch(username, project_id, store_id, task_pack_id, task_id);
        p_batch = offlineDBHelper.getTaskPBatch(username, project_id, store_id, task_pack_id, task_id);
        try {
            JSONObject jsonObject = new JSONObject(result);
            taskitmrecodill_name.setText(jsonObject.getString("taskName"));
            taskitmrecodill_desc.setText(jsonObject.getString("note"));
            batch = jsonObject.getString("batch");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taskitmrecodill_button: {
                if (p_batch == null || outlet_batch == null) {
                    Tools.showToast(this, getResources().getString(R.string.batch_error));
                    return;
                }
                data.setClass(OfflineTaskitemRecodillustrateActivity.this, OfflineTaskitemRecodActivity.class);
                data.putExtra("batch", batch);
                data.putExtra("outlet_batch", outlet_batch);
                data.putExtra("p_batch", p_batch);
                startActivity(data);
                baseFinish();
            }
            break;
        }
    }
}
