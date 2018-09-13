package com.orange.oy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.OfflineDBHelper;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 任务列表-录制任务页
 */
public class OfflineTaskitemShotActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View
        .OnClickListener {

    private void initTitle() {
        AppTitle taskitemshot_title = (AppTitle) findViewById(R.id.taskitemshot_title);
        taskitemshot_title.settingName("视频任务");
        taskitemshot_title.showBack(this);
    }

    public void onBack() {
        baseFinish();
    }

    protected void onStop() {
        super.onStop();
    }

    private ImageView taskitemshot_video1;
    private String task_pack_id, task_id, store_id, task_name, task_pack_name, store_name, project_id, project_name, store_num;
    private EditText taskitemshot_edit;
    private String category1 = "", category2 = "", category3 = "";
    private String categoryPath;
    private boolean isHad;
    private OfflineDBHelper offlineDBHelper;
    private String batch;
    private String code, brand;
    private String outlet_batch, p_batch;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitemshot);
        initTitle();
        offlineDBHelper = new OfflineDBHelper(this);
        updataDBHelper = new UpdataDBHelper(this);
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
        isHad = data.getBooleanExtra("isHad", false);
        category1 = data.getStringExtra("category1");
        category2 = data.getStringExtra("category2");
        category3 = data.getStringExtra("category3");
        batch = data.getStringExtra("batch");
        outlet_batch = data.getStringExtra("outlet_batch");
        p_batch = data.getStringExtra("p_batch");
        code = data.getStringExtra("code");
        brand = data.getStringExtra("brand");
        if (TextUtils.isEmpty(batch) || batch.equals("null")) {
            batch = "1";
        }
        categoryPath = Tools.toByte(category1 + category2 + category3 + project_id);
        taskitemshot_video1 = (ImageView) findViewById(R.id.taskitemshot_video1);
        TextView taskitemshot_name = (TextView) findViewById(R.id.taskitemshot_name);
        taskitemshot_name.setText(task_name);
        taskitemshot_edit = (EditText) findViewById(R.id.taskitemshot_edit);
        taskitemshot_video1.setOnClickListener(this);
        findViewById(R.id.taskitemshot_button).setOnClickListener(this);
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taskitemshot_video1: {
                Intent intent = new Intent(this, ShotActivity.class);
                intent.putExtra("index", 1);
                intent.putExtra("fileName", Tools.getTimeSS() + Tools.getDeviceId(OfflineTaskitemShotActivity.this) + task_id);
                intent.putExtra("dirName", AppInfo.getName(this) + "/" + project_id + store_id + task_pack_id + task_id +
                        categoryPath);
                startActivityForResult(intent, AppInfo.TaskitemShotRequestCodeForShot);
            }
            break;
            case R.id.taskitemshot_button: {
                if (taskitemshot_video1.getTag() != null) {
                    try {

                        String tag = taskitemshot_video1.getTag().toString();
                        if (new File(tag).exists()) {
                            sendData();
                        } else {
                            Tools.showToast(OfflineTaskitemShotActivity.this, "视频录制失败，请重新录制！");
                        }
                    } catch (UnsupportedEncodingException e) {
                        Tools.showToast(this, "存储失败，未知异常！");
                        MobclickAgent.reportError(this, "offline task y:" + e.getMessage());
                    }
                } else {
                    Tools.showToast(OfflineTaskitemShotActivity.this, "请录制视频");
                }
            }
            break;
        }
    }

    private UpdataDBHelper updataDBHelper;

    private void sendData() throws UnsupportedEncodingException {
        String username = AppInfo.getName(this);
        if (TextUtils.isEmpty(task_pack_id)) {
            offlineDBHelper.completedTask(username, project_id, store_id, task_id);
        } else {
            offlineDBHelper.insertOfflineCompleted(username, project_id, store_id, task_pack_id, task_id, category1,
                    category2, category3);
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_mobile", username);
        map.put("usermobile", username);
        map.put("task_pack_id", task_pack_id);//特别修改
        map.put("task_id", task_id);
        map.put("token", Tools.getToken());
        map.put("storeid", store_id);
        map.put("note", taskitemshot_edit.getText().toString().trim());
        map.put("category1", category1);
        map.put("category2", category2);
        map.put("category3", category3);
        map.put("outlet_batch", outlet_batch);
        map.put("p_batch", p_batch);
        map.put("batch", batch);
        map.put("flag", (isHad) ? "1" : "0");
        String key = "video1";
        updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, brand, store_id, store_name, task_pack_id,
                task_pack_name, "2", task_id, task_name, category1, category2, category3,
                username + project_id +
                        store_id + task_pack_id + category1 + category2 + category3 + task_id, Urls
                        .Videocomplete,
                key, taskitemshot_video1.getTag().toString(), UpdataDBHelper.Updata_file_type_video,
                map, null, true, Urls.Videoup, paramsToString(), true);
        Intent service = new Intent("com.orange.oy.UpdataNewService");
        service.setPackage("com.orange.oy");
        startService(service);
        OfflinePackageActivity.isRefresh = true;
        OfflineTaskActivity.isRefresh = true;
        baseFinish();
    }

    private String paramsToString() throws UnsupportedEncodingException {
        Map<String, String> parames = new HashMap<>();
        parames.put("usermobile", AppInfo.getName(OfflineTaskitemShotActivity.this));
        parames.put("token", Tools.getToken());
        parames.put("pid", task_pack_id);
        parames.put("task_id", task_id);
        parames.put("storeid", store_id);
        parames.put("note", taskitemshot_edit.getText().toString().trim());
        parames.put("category1", category1);
        parames.put("category2", category2);
        parames.put("category3", category3);
        parames.put("batch", batch);
        parames.put("outlet_batch", outlet_batch);
        parames.put("p_batch", p_batch);
        parames.put("flag", (isHad) ? "1" : "0");
        String data = "";
        Iterator<String> iterator = parames.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (TextUtils.isEmpty(data)) {
                data = key + "=" + URLEncoder.encode(parames.get(key).trim(), "utf-8");
            } else {
                data = data + "&" + key + "=" + URLEncoder.encode(parames.get(key).trim(), "utf-8");
            }
        }
        return data;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppInfo.TaskitemShotRequestCodeForShot: {
                if (resultCode == AppInfo.ShotSuccessResultCode) {
                    int index = data.getIntExtra("index", 0);
                    String path = data.getStringExtra("path");
                    if (index == 1) {
                        taskitemshot_video1.setImageBitmap(Tools.createVideoThumbnail(path));
                        taskitemshot_video1.setTag(path);
                    }
                }
            }
            break;
        }
    }
}
