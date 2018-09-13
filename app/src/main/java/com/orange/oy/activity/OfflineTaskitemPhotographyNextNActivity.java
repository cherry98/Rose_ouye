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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 任务分类-拍照任务-任务说明页-信息录入页
 */
public class OfflineTaskitemPhotographyNextNActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View
        .OnClickListener {

    private void initTitle(String title) {
        AppTitle taskitempgnext_title = (AppTitle) findViewById(R.id.taskitempgnextn_title);
        taskitempgnext_title.settingName("拍照任务");
        taskitempgnext_title.showBack(this);
    }

    public void onBack() {
        baseFinish();
    }

    protected void onStop() {
        super.onStop();
    }

    private String task_pack_id, task_id, store_id, task_name, task_pack_name, store_name, project_id, project_name, store_num;
    private ImageView taskitempgnextn_video1, taskitempgnextn_video2, taskitempgnextn_video3;
    private TextView taskitempgnextn_name;
    private EditText taskitempgnextn_edit;
    private String category1 = "", category2 = "", category3 = "";
    private String categoryPath;
    private OfflineDBHelper offlineDBHelper;
    private String batch, code, brand;
    private String outlet_batch = null, p_batch = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitempgnext_n);
        offlineDBHelper = new OfflineDBHelper(this);
        Intent data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        updataDBHelper = new UpdataDBHelper(this);
        project_id = data.getStringExtra("project_id");
        project_name = data.getStringExtra("project_name");
        task_id = data.getStringExtra("task_id");
        task_pack_id = data.getStringExtra("task_pack_id");
        task_pack_name = data.getStringExtra("task_pack_name");
        store_id = data.getStringExtra("store_id");
        store_name = data.getStringExtra("store_name");
        store_num = data.getStringExtra("store_num");
        task_name = data.getStringExtra("task_name");
        category1 = data.getStringExtra("category1");
        category2 = data.getStringExtra("category2");
        category3 = data.getStringExtra("category3");
        batch = data.getStringExtra("batch");
        code = data.getStringExtra("code");
        brand = data.getStringExtra("brand");
        outlet_batch = data.getStringExtra("outlet_batch");
        p_batch = data.getStringExtra("p_batch");
        if (TextUtils.isEmpty(batch) || batch.equals("null")) {
            batch = "1";
        }
        categoryPath = Tools.toByte(category1 + category2 + category3 + project_id);
        initTitle(task_name);
        taskitempgnextn_video1 = (ImageView) findViewById(R.id.taskitempgnextn_video1);
        taskitempgnextn_video2 = (ImageView) findViewById(R.id.taskitempgnextn_video2);
        taskitempgnextn_video3 = (ImageView) findViewById(R.id.taskitempgnextn_video3);
        taskitempgnextn_edit = (EditText) findViewById(R.id.taskitempgnextn_edit);
        taskitempgnextn_name = (TextView) findViewById(R.id.taskitempgnextn_name);
        taskitempgnextn_name.setText(task_name);
        findViewById(R.id.taskitempgnextn_button).setOnClickListener(this);
        taskitempgnextn_video1.setOnClickListener(this);
        taskitempgnextn_video2.setOnClickListener(this);
        taskitempgnextn_video3.setOnClickListener(this);
    }

    private UpdataDBHelper updataDBHelper;

    private void Closetask() throws UnsupportedEncodingException {
        String username = AppInfo.getName(this);
        if (TextUtils.isEmpty(task_pack_id)) {
            offlineDBHelper.completedTask(username, project_id, store_id, task_id);
        } else {
            offlineDBHelper.insertOfflineCompleted(username, project_id, store_id, task_pack_id, task_id, category1,
                    category2, category3);
        }
        Map<String, String> params = new HashMap<>();
        params.put("token", Tools.getToken());
        params.put("task_pack_id", task_pack_id);
        params.put("taskid", task_id);
        params.put("storeid", store_id);
        params.put("usermobile", username);
        params.put("note", taskitempgnextn_edit.getText().toString().trim());
        params.put("category1", category1);
        params.put("category2", category2);
        params.put("category3", category3);
        params.put("outlet_batch", outlet_batch);
        params.put("p_batch", p_batch);
        params.put("batch", batch);
        String key = null;
        String name = null;
        String v1 = null, v2 = null, v3 = null;
        if (taskitempgnextn_video1.getTag() != null) {
            v1 = taskitempgnextn_video1.getTag().toString();
        }
        if (taskitempgnextn_video2.getTag() != null) {
            v2 = taskitempgnextn_video2.getTag().toString();
        }
        if (taskitempgnextn_video3.getTag() != null) {
            v3 = taskitempgnextn_video3.getTag().toString();
        }
        if (!TextUtils.isEmpty(v1)) {
            key = "video1";
            name = v1;
        }
        if (!TextUtils.isEmpty(v2)) {
            if (key == null) {
                key = "video2";
                name = v2;
            } else {
                key = key + ",video2";
                name = name + "," + v2;
            }
        }
        if (!TextUtils.isEmpty(v3)) {
            if (key == null) {
                key = "video3";
                name = v3;
            } else {
                key = key + ",video3";
                name = name + "," + v3;
            }
        }
        updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, brand, store_id, store_name, task_pack_id,
                task_pack_name, "1", task_id, task_name, category1, category2, category3,
                username + project_id +
                        store_id + task_pack_id + category1 + category2 + category3 + task_id, Urls
                        .Closetaskcomplete,
                key, name, UpdataDBHelper.Updata_file_type_video, params, null, true, Urls.Closetask, paramsToString(), true);
        Intent service = new Intent("com.orange.oy.UpdataNewService");
        service.setPackage("com.orange.oy");
        startService(service);
        OfflinePackageActivity.isRefresh = true;
        OfflineTaskActivity.isRefresh = true;
        baseFinish();
    }

    private String paramsToString() throws UnsupportedEncodingException {
        Map<String, String> parames = new HashMap<>();
        parames.put("token", Tools.getToken());
        parames.put("pid", task_pack_id);
        parames.put("taskid", task_id);
        parames.put("storeid", store_id);
        parames.put("note", taskitempgnextn_edit.getText().toString().trim());
        parames.put("usermobile", AppInfo.getName(OfflineTaskitemPhotographyNextNActivity.this));
        parames.put("category1", category1);
        parames.put("category2", category2);
        parames.put("category3", category3);
        parames.put("batch", batch);
        parames.put("outlet_batch", outlet_batch);
        parames.put("p_batch", p_batch);
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

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taskitempgnextn_video1: {
                Intent intent = new Intent(this, ShotActivity.class);
                intent.putExtra("index", 1);
                intent.putExtra("fileName", Tools.getTimeSS() + Tools.getDeviceId(OfflineTaskitemPhotographyNextNActivity.this)
                        + task_id + 1);
                intent.putExtra("dirName", AppInfo.getName(this) + "/" + project_id + store_id + task_pack_id + task_id +
                        categoryPath);
                startActivityForResult(intent, AppInfo.TaskitemPhotogpnCodeForShot);
            }
            break;
            case R.id.taskitempgnextn_video2: {
                Intent intent = new Intent(this, ShotActivity.class);
                intent.putExtra("index", 2);
                intent.putExtra("fileName", Tools.getTimeSS() + Tools.getDeviceId(OfflineTaskitemPhotographyNextNActivity.this)
                        + task_id + 2);
                intent.putExtra("dirName", AppInfo.getName(this) + "/" + project_id + store_id + task_pack_id + task_id +
                        categoryPath);
                startActivityForResult(intent, AppInfo.TaskitemPhotogpnCodeForShot);
            }
            break;
            case R.id.taskitempgnextn_video3: {
                Intent intent = new Intent(this, ShotActivity.class);
                intent.putExtra("index", 3);
                intent.putExtra("fileName", Tools.getTimeSS() + Tools.getDeviceId(OfflineTaskitemPhotographyNextNActivity.this)
                        + task_id + 3);
                intent.putExtra("dirName", AppInfo.getName(this) + "/" + project_id + store_id + task_pack_id + task_id +
                        categoryPath);
                startActivityForResult(intent, AppInfo.TaskitemPhotogpnCodeForShot);
            }
            break;
            case R.id.taskitempgnextn_button: {
                if (TextUtils.isEmpty(taskitempgnextn_edit.getText().toString().trim())) {
                    Tools.showToast(this, "请填写备注");
                    return;
                }
                if (taskitempgnextn_video3.getTag() == null && taskitempgnextn_video2.getTag() == null &&
                        taskitempgnextn_video1.getTag() == null) {
                    Tools.showToast(this, "请拍摄视频");
                    return;
                }
                try {
                    Closetask();
                } catch (UnsupportedEncodingException e) {
                    Tools.showToast(this, "存储失败，未知异常！");
                    MobclickAgent.reportError(this, "offline task n:" + e.getMessage());
                    e.printStackTrace();
                }
            }
            break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case AppInfo.TaskitemPhotogpnCodeForShot: {
                if (resultCode == AppInfo.ShotSuccessResultCode) {
                    int index = data.getIntExtra("index", 0);
                    String path = data.getStringExtra("path");
                    switch (index) {
                        case 1: {
                            taskitempgnextn_video1.setImageBitmap(Tools.createVideoThumbnail(path));
                            taskitempgnextn_video1.setTag(path);
                        }
                        break;
                        case 2: {
                            taskitempgnextn_video2.setImageBitmap(Tools.createVideoThumbnail(path));
                            taskitempgnextn_video2.setTag(path);
                        }
                        break;
                        case 3: {
                            taskitempgnextn_video3.setImageBitmap(Tools.createVideoThumbnail(path));
                            taskitempgnextn_video3.setTag(path);
                        }
                        break;
                    }
                }
            }
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
