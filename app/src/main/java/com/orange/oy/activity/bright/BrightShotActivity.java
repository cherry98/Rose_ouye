package com.orange.oy.activity.bright;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.ShotActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BrightShotActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle,
        View.OnClickListener {
    private void initTitle(String str) {
        AppTitle taskitemshot_title = (AppTitle) findViewById(R.id.brightshot_title);
        taskitemshot_title.settingName("视频任务");
        taskitemshot_title.showBack(this);
    }

    private void initNetworkConnection() {
        assistanttask = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(BrightShotActivity.this));
                params.put("clienttime", date);
                params.put("executeid", executeid);
                params.put("taskbatch", taskbatch);
                return params;
            }
        };
        assistanttask.setIsShowDialog(true);
    }

    private TextView brightshot_name;
    private ImageView brightshot_video1;
    private EditText brightshot_edit;
    private Button brightshot_button;
    private UpdataDBHelper updataDBHelper;
    private String project_name, project_id, store_id, task_name, codeStr, brand,
            store_num, categoryPath, store_name;
    private NetworkConnection assistanttask;
    private String date, executeid, taskbatch, task_id;
    private Intent data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bright_shot);
        updataDBHelper = new UpdataDBHelper(this);
        data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        date = sDateFormat.format(new java.util.Date());
        project_id = data.getStringExtra("project_id");
        project_name = data.getStringExtra("projectname");
        codeStr = data.getStringExtra("code");
        brand = data.getStringExtra("brand");
        store_num = data.getStringExtra("store_num");
        store_id = data.getStringExtra("outletid");
        task_name = data.getStringExtra("taskName");
        executeid = data.getIntExtra("executeid", 0) + "";
        task_id = data.getIntExtra("taskid", 0) + "";
        store_name = data.getStringExtra("store_name");
        taskbatch = data.getStringExtra("batch");
        categoryPath = Tools.toByte(project_id);
        initTitle(task_name);
        initNetworkConnection();
        brightshot_name = (TextView) findViewById(R.id.brightshot_name);
        brightshot_video1 = (ImageView) findViewById(R.id.brightshot_video1);
        brightshot_edit = (EditText) findViewById(R.id.brightshot_edit);
        brightshot_button = (Button) findViewById(R.id.brightshot_button);
        brightshot_video1.setOnClickListener(this);
        brightshot_button.setOnClickListener(this);
        brightshot_name.setText(date);
    }


    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.brightshot_video1: {
                Intent intent = new Intent(this, ShotActivity.class);
                intent.putExtra("index", 1);
                intent.putExtra("fileName", Tools.getTimeSS() + Tools.getDeviceId(BrightShotActivity.this) + task_id);
                intent.putExtra("dirName", AppInfo.getName(this) + "/" + project_id + store_id + task_id +
                        categoryPath);
                startActivityForResult(intent, AppInfo.TaskitemShotRequestCodeForShot);
            }
            break;
            case R.id.brightshot_button: {
                if (brightshot_video1.getTag() != null) {
                    String tag = brightshot_video1.getTag().toString();
                    if (new File(tag).exists()) {
                        sendData();
                    } else {
                        Tools.showToast(BrightShotActivity.this, "视频录制失败，请重新录制！");
                    }
                } else {
                    Tools.showToast(BrightShotActivity.this, "请录制视频");
                }
            }
            break;
        }
    }

    private void sendData() {
        assistanttask.sendPostRequest(Urls.AssistantTask, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Map<String, String> map = new HashMap<String, String>();
                        String username = AppInfo.getName(BrightShotActivity.this);
                        map.put("usermobile", username);
                        map.put("executeid", executeid);
                        map.put("note", brightshot_edit.getText().toString().trim());
                        String key = "video1";
                        updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, brand,
                                store_id, store_name, null,
                                null, "2-2", task_id, task_name, null, null, null,
                                username + project_id + store_id + task_id,
                                Urls.AssistantTaskComplete,
                                key, brightshot_video1.getTag().toString(), UpdataDBHelper.Updata_file_type_video,
                                map, null, true, Urls.AssistantTask, paramsToString(), false);
                        Intent service = new Intent("com.orange.oy.UpdataNewService");
                        service.setPackage("com.orange.oy");
                        startService(service);
                        BrightBallotResultActivity.isRefresh = true;
                        BrightBallotActivity.isRefresh = true;
                        BrightPersonInfoActivity.isRefresh = true;
                        baseFinish();
                    } else {
                        Tools.showToast(BrightShotActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(BrightShotActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(BrightShotActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    private String paramsToString() {
        Map<String, String> parames = new HashMap<>();
        parames.put("usermobile", AppInfo.getName(BrightShotActivity.this));
        parames.put("clienttime", date);
        parames.put("executeid", executeid);
        parames.put("taskbatch", taskbatch);
        String data = "";
        Iterator<String> iterator = parames.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (TextUtils.isEmpty(data)) {
                try {
                    data = key + "=" + URLEncoder.encode(parames.get(key).trim(), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    data = key + "=" + parames.get(key).trim();
                }
            } else {
                try {
                    data = data + "&" + key + "=" + URLEncoder.encode(parames.get(key).trim(), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    data = data + "&" + key + "=" + parames.get(key).trim();
                }
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
                        brightshot_video1.setImageBitmap(Tools.createVideoThumbnail(path));
                        brightshot_video1.setTag(path);
                    }
                }
            }
            break;
        }
    }
}
