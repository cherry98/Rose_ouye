package com.orange.oy.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.black.BlackDZXListActivity;
import com.orange.oy.activity.scan.ScanTaskNewActivity;
import com.orange.oy.activity.scan.ScanTaskillustrateActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.AppDBHelper;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.Mp3Model;
import com.orange.oy.info.TaskNewInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.service.RecordService;
import com.orange.oy.util.FileCache;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.CollapsibleTextView;
import com.orange.oy.view.MyListView;
import com.orange.oy.view.RecodePlayView;
import com.orange.oy.view.SpreadTextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 录音任务说明页
 */
public class TaskitemRecodillustrateActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View
        .OnClickListener, MediaRecorder.OnErrorListener {
    AppTitle appTitle;

    private void initTitle(String str) {
        appTitle = (AppTitle) findViewById(R.id.taskitmrecodill_title);
        if (index != null && "0".equals(index)) {
            appTitle.settingName("录音任务（预览）");
        } else {
            appTitle.settingName("录音任务");
        }
        if (!"1".equals(newtask)) {//不是新手
            appTitle.showBack(this);
        }
        appTitle.hideIllustrate();
    }

    public void onBack() {
        Intent intent = new Intent();
        if (RecordService.isStart()) {//正在录音
            intent.putExtra("task_id", task_id);
        } else {
            intent.putExtra("task_id", "");
        }
        setResult(RESULT_OK, intent);
        baseFinish();
    }

    protected void onStop() {
        super.onStop();
        RecodePlayView.closeAllRecodeplay();
        RecodePlayView.clearRecodePlayViewMap();
    }

    private void initNetworkConnection() {
        Soundtask = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("task_pack_id", task_pack_id);
                params.put("task_id", task_id);
                return params;
            }
        };
        Soundtask.setIsShowDialog(true);
        Soundtaskup = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(TaskitemRecodillustrateActivity.this));
                params.put("pid", task_pack_id);
                params.put("task_id", task_id);
                params.put("note", taskitmrecodill_edittext.getText().toString().trim());
                params.put("storeid", store_id);
                params.put("category1", category1);
                params.put("category2", category2);
                params.put("category3", category3);
                params.put("batch", batch);
                params.put("outlet_batch", outlet_batch);
                params.put("p_batch", p_batch);
                return params;
            }
        };
        Soundtaskup.setIsShowDialog(true);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if (RecordService.isStart()) {//正在录音
            intent.putExtra("task_id", task_id);
        } else {
            intent.putExtra("task_id", "");
        }
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    private NetworkConnection Soundtask, Soundtaskup;
    private TextView taskitmrecodill_name;
    private SpreadTextView taskitmrecodill_desc;
    private Intent data;
    private String task_pack_id, task_id, store_id, task_name, task_pack_name, project_id, project_name, store_num,
            codeStr, brand, outlet_batch, p_batch, batch, is_desc, store_name;
    private String category1 = "", category2 = "", category3 = "", categoryPath;
    private UpdataDBHelper updataDBHelper;
    private String newtask;//判断是否是新手任务 1是0否
    private ImageView taskitmrecodill_img;
    private String index;//扫码任务预览
    private TextView taskitmrecodill_button;
    private static TextView taskitemrecod_time_h, taskitemrecod_time_m, taskitemrecod_time_s;
    private AppDBHelper appDBHelper;
    private ListView taskitmrecodill_listview;
    private ArrayList<Mp3Model> pathList = new ArrayList<>();
    private MyAdapter myAdapter;
    private EditText taskitmrecodill_edittext;
    private RelativeLayout rl_times;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitemrecodillustrate);
        if (RecordService.isStart()) {
            Intent recodeserver = RecordService.getIntent();
            if (recodeserver != null) {
                boolean isNormal = recodeserver.getBooleanExtra("isNormal", false);
                if (!isNormal) {
                    stopService(new Intent(this, RecordService.class));
                }
            }
        }
        appDBHelper = new AppDBHelper(this);
        initNetworkConnection();
        data = getIntent();
        index = data.getStringExtra("index");
        updataDBHelper = new UpdataDBHelper(this);
        task_pack_id = data.getStringExtra("task_pack_id");
        task_id = data.getStringExtra("task_id");
        project_id = data.getStringExtra("project_id");
        project_name = data.getStringExtra("project_name");
        task_pack_id = data.getStringExtra("task_pack_id");
        task_id = data.getStringExtra("task_id");
        store_id = data.getStringExtra("store_id");
        store_num = data.getStringExtra("store_num");
        task_pack_name = data.getStringExtra("task_pack_name");
        task_name = data.getStringExtra("task_name");
        category1 = data.getStringExtra("category1");
        category2 = data.getStringExtra("category2");
        category3 = data.getStringExtra("category3");
        codeStr = data.getStringExtra("code");
        brand = data.getStringExtra("brand");
        outlet_batch = data.getStringExtra("outlet_batch");
        p_batch = data.getStringExtra("p_batch");
        is_desc = data.getStringExtra("is_desc");
        store_name = data.getStringExtra("store_name");
        newtask = data.getStringExtra("newtask");
        initTitle(data.getStringExtra("task_name"));
        taskitmrecodill_layout = findViewById(R.id.taskitmrecodill_layout);
        taskitmrecodill_edittext = (EditText) findViewById(R.id.taskitmrecodill_edittext);
        categoryPath = Tools.toByte(category1 + category2 + category3 + project_id);
        taskitmrecodill_desc = (SpreadTextView) findViewById(R.id.taskitmrecodill_desc);
        taskitmrecodill_name = (TextView) findViewById(R.id.taskitmrecodill_name);
        taskitmrecodill_button = (TextView) findViewById(R.id.taskitmrecodill_button);
        taskitemrecod_time_h = (TextView) findViewById(R.id.taskitemrecod_time_h);
        taskitemrecod_time_m = (TextView) findViewById(R.id.taskitemrecod_time_m);
        taskitemrecod_time_s = (TextView) findViewById(R.id.taskitemrecod_time_s);
        ((ScrollView) findViewById(R.id.taskitmrecodill_scroll)).smoothScrollTo(0, 20);
        taskitmrecodill_img = (ImageView) findViewById(R.id.taskitmrecodill_img);
        taskitmrecodill_listview = (ListView) findViewById(R.id.taskitmrecodill_listview);
        rl_times = (RelativeLayout) findViewById(R.id.rl_times);

        if (index != null && "0".equals(index)) {
            rl_times.setVisibility(View.GONE);
            taskitmrecodill_img.setVisibility(View.GONE);
            taskitmrecodill_button.setVisibility(View.GONE);
            appTitle.hideIllustrate();
            taskitmrecodill_desc.setIsup(true);
        } else {
            rl_times.setVisibility(View.VISIBLE);
            taskitmrecodill_img.setVisibility(View.VISIBLE);
            taskitmrecodill_button.setVisibility(View.VISIBLE);
        }
        taskitmrecodill_img.setOnClickListener(this);
        myAdapter = new MyAdapter();
        checkPermission();
        getData();
        if (RecordService.isStart()) {
            taskitmrecodill_button.setBackgroundResource(R.color.makesure2);
            taskitmrecodill_button.setOnClickListener(null);
            taskitmrecodill_img.setImageResource(R.mipmap.stop_tape);
        } else {
            taskitmrecodill_button.setBackgroundResource(R.color.makesure2);
            taskitmrecodill_button.setOnClickListener(null);
            taskitmrecodill_img.setImageResource(R.mipmap.start_tape);
        }
        ArrayList<String> list = appDBHelper.getTapePath(AppInfo.getName(TaskitemRecodillustrateActivity.this), project_id, store_id, task_id);
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                Mp3Model mp3Model = new Mp3Model(list.get(i));
                pathList.add(mp3Model);
            }
            taskitmrecodill_layout.setVisibility(View.VISIBLE);
            if (!RecordService.isStart()) {
                taskitmrecodill_img.setImageResource(R.mipmap.keep_tape);
                taskitmrecodill_button.setOnClickListener(this);
                taskitmrecodill_button.setBackgroundResource(R.drawable.identitysure);
            }
            int size = pathList.size();
            int height = Tools.dipToPx(TaskitemRecodillustrateActivity.this, 30 + 7) * size;
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) taskitmrecodill_listview.getLayoutParams();
            lp.height = height;
            taskitmrecodill_listview.setLayoutParams(lp);
        }
        taskitmrecodill_listview.setAdapter(myAdapter);
    }

    private View taskitmrecodill_layout;

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, AppInfo
                        .REQUEST_CODE_ASK_RECORD_AUDIO);
                return;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        switch (requestCode) {
            case AppInfo.REQUEST_CODE_ASK_RECORD_AUDIO:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                } else {
                    Tools.showToast(this, "录音权限获取失败");
                    baseFinish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void getData() {
        Soundtask.sendPostRequest(Urls.Soundtask, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        batch = jsonObject.getString("batch");
                        taskitmrecodill_name.setText(jsonObject.getString("taskName"));
                        taskitmrecodill_desc.setDesc(jsonObject.getString("note"));
                    } else {
                        Tools.showToast(TaskitemRecodillustrateActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskitemRecodillustrateActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskitemRecodillustrateActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what != 0) {
                int time = msg.what;
                int s = time % 60;
                int m = time / 60;
                int h = m / 60;
                taskitemrecod_time_h.setText((h > 9) ? h + "" : "0" + h);
                taskitemrecod_time_m.setText((m > 9) ? m + "" : "0" + m);
                taskitemrecod_time_s.setText((s > 9) ? s + "" : "0" + s);
            }
        }
    };

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taskitmrecodill_button: {
                if (index != null && "0".equals(index)) {
                    Tools.showToast(TaskitemRecodillustrateActivity.this, "抱歉，预览时任务无法执行。");
                    return;
                }
                if (RecordService.isStart()) {
                    Tools.showToast(TaskitemRecodillustrateActivity.this, "请先关闭录音再提交~");
                    return;
                }
                Soundtaskup();
            }
            break;
            case R.id.taskitmrecodill_img: {
                if (index != null && "0".equals(index)) {
                    Tools.showToast(TaskitemRecodillustrateActivity.this, "抱歉，预览时任务无法执行。");
                    return;
                }
                if (!RecordService.isStart()) {
                    taskitmrecodill_button.setBackgroundResource(R.color.makesure2);
                    taskitmrecodill_button.setOnClickListener(null);
                    taskitmrecodill_img.setImageResource(R.mipmap.stop_tape);
                    if (!isVoicePermission()) {
                        Tools.showToast(this, "录音功能不可用，请检查录音权限是否开启！");
                    } else {
                        Intent service = new Intent("com.orange.oy.recordservice").setPackage("com.orange.oy");
                        service.putExtra("usermobile", AppInfo.getName(TaskitemRecodillustrateActivity.this));
                        service.putExtra("project_id", project_id);
                        service.putExtra("projectname", project_name);
                        service.putExtra("store_name", store_name);
                        service.putExtra("store_num", store_num);
                        service.putExtra("storeid", store_id);
                        service.putExtra("dirName", AppInfo.getName(TaskitemRecodillustrateActivity.this) + "/" + store_id);
                        service.putExtra("fileName", Tools.getTimeSS() + Tools.getDeviceId(TaskitemRecodillustrateActivity.this) +
                                store_id);
                        service.putExtra("isOffline", false);
                        service.putExtra("code", codeStr);
                        service.putExtra("brand", brand);
                        service.putExtra("task_id", task_id);
                        service.putExtra("isNormal", true);
                        startService(service);
                    }
                } else {
                    taskitmrecodill_button.setBackgroundResource(R.drawable.identitysure);
                    taskitmrecodill_button.setOnClickListener(this);
                    taskitmrecodill_img.setImageResource(R.mipmap.keep_tape);
                    Intent intent = RecordService.getIntent();//获取录音服务数据
                    String usermobile = intent.getStringExtra("usermobile");
                    String fileName = intent.getStringExtra("fileName");
                    File mRecordFile = new File(FileCache.getDirForRecord(this, usermobile + "/" + store_id),
                            fileName + ".amr");
                    if (!appDBHelper.havPhotoUrlRecord(usermobile, project_id, store_id, task_id, mRecordFile.getPath(), null)) {
                        boolean isOffline = intent.getBooleanExtra("isOffline", true);
                        String task_id = intent.getStringExtra("task_id");
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("province", intent.getStringExtra("province"));
                        map.put("usermobile", usermobile);
                        map.put("projectname", project_name);
                        map.put("store_name", intent.getStringExtra("store_name"));
                        map.put("store_num", intent.getStringExtra("store_num"));
                        map.put("city", intent.getStringExtra("city"));
                        String key = "video";
                        updataDBHelper.addUpdataTask(usermobile, project_id, project_name,
                                intent.getStringExtra("code"), intent.getStringExtra("brand"), store_id, map.get("store_name"), null,
                                null, "jcly", null, null, null, null, null,
                                Tools.getToken() + project_id + usermobile + Tools.getTimeSS(), null, key,
                                mRecordFile.getPath(), UpdataDBHelper.Updata_file_type_video, map, null, false, null, null,
                                isOffline);
                        appDBHelper.addPhotoUrlRecord(usermobile, project_id, store_id, task_id, mRecordFile.getPath(), null);
                        int num = appDBHelper.getAllRecordNumber(usermobile, project_id, store_id, task_id);
                        appDBHelper.setFileNum(mRecordFile.getPath(), num + "");
                    }
                    Intent service = new Intent(this, RecordService.class);
                    stopService(service);
                    new MyAsyncTask().execute();
                }
            }
            break;
        }
    }


    class MyAsyncTask extends AsyncTask<Void, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            ArrayList<String> list = appDBHelper.getTapePath(AppInfo.getName(TaskitemRecodillustrateActivity.this),
                    project_id, store_id, task_id);
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<String> list) {
            if (!list.isEmpty()) {
                pathList.clear();
                for (int i = 0; i < list.size(); i++) {
                    Mp3Model mp3Model = new Mp3Model(list.get(i));
                    pathList.add(mp3Model);
                }
                int size = pathList.size();
                int height = Tools.dipToPx(TaskitemRecodillustrateActivity.this, 30 + 7) * size;
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) taskitmrecodill_listview.getLayoutParams();
                lp.height = height;
                taskitmrecodill_listview.setLayoutParams(lp);
                myAdapter.notifyDataSetChanged();
                taskitmrecodill_layout.setVisibility(View.VISIBLE);
                taskitmrecodill_button.setBackgroundResource(R.drawable.identitysure);
                taskitmrecodill_button.setOnClickListener(TaskitemRecodillustrateActivity.this);
            }
        }
    }

    //显示录音条数
    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return pathList.size();
        }

        @Override
        public Object getItem(int position) {
            return pathList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RecodePlayView recodePlayView;
            if (convertView == null) {
                recodePlayView = new RecodePlayView(TaskitemRecodillustrateActivity.this);
            } else {
                recodePlayView = (RecodePlayView) convertView;
            }
            recodePlayView.setOnRecodePlayerListener(onRecodePlayerListener);
            Mp3Model mp3Model = pathList.get(position).setRecodePlayView(recodePlayView);
            recodePlayView.settingREC(mp3Model.getPath());
            return recodePlayView;
        }
    }

    private RecodePlayView.OnRecodePlayerListener onRecodePlayerListener = new RecodePlayView.OnRecodePlayerListener() {
        @Override
        public void play(RecodePlayView recodePlayView) {
            RecodePlayView.closeAllRecodeplay(recodePlayView.hashCode());
        }

        @Override
        public void stop(RecodePlayView recodePlayView) {

        }
    };

    private void Soundtaskup() {
        Soundtaskup.sendPostRequest(Urls.Soundtaskup, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200 || code == 2) {
                        String executeid = jsonObject.getString("executeid");
                        String username = AppInfo.getName(TaskitemRecodillustrateActivity.this);
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("usermobile", username);
                        map.put("task_pack_id", task_pack_id);
                        map.put("task_id", task_id);
                        map.put("executeid", executeid);
                        map.put("storeid", store_id);
                        map.put("note", taskitmrecodill_edittext.getText().toString().trim());
                        map.put("category1", category1);
                        map.put("category2", category2);
                        map.put("category3", category3);
                        map.put("outlet_batch", outlet_batch);
                        map.put("p_batch", p_batch);
                        map.put("batch", batch);
                        updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, brand,
                                store_id, store_name, task_pack_id,
                                task_pack_name, "111", task_id, task_name, category1, category2, category3,
                                username + project_id +
                                        store_id + task_pack_id + category1 + category2 + category3 + task_id, Urls
                                        .Filecomplete, null, null, UpdataDBHelper
                                        .Updata_file_type_video, map, null,
                                true, Urls.Soundtaskup, paramsToString(), false);
                        Intent service = new Intent("com.orange.oy.UpdataNewService");
                        service.setPackage("com.orange.oy");
                        startService(service);
                        TaskitemDetailActivity.isRefresh = true;
                        TaskitemDetailActivity.taskid = task_id;
                        TaskitemDetailActivity_12.isRefresh = true;
                        TaskitemDetailActivity_12.taskid = task_id;
                        TaskFinishActivity.isRefresh = true;
                        TaskitemListActivity.isRefresh = true;
                        TaskitemListActivity_12.isRefresh = true;
                        if (code == 2) {
                            ConfirmDialog.showDialog(TaskitemRecodillustrateActivity.this, null, jsonObject.getString("msg"), null,
                                    "确定", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                                        @Override
                                        public void leftClick(Object object) {

                                        }

                                        @Override
                                        public void rightClick(Object object) {
                                            baseFinish();
                                        }
                                    }).goneLeft();
                        } else if (code == 200) {
                            if ("1".equals(newtask)) {//新手任务
                                ArrayList<TaskNewInfo> list = (ArrayList<TaskNewInfo>) getIntent().getBundleExtra("data").getSerializable("list");
                                if (list != null) {
                                    if (!list.isEmpty()) {
                                        TaskNewInfo taskNewInfo = list.remove(0);
                                        String type = taskNewInfo.getTask_type();
                                        Intent intent = new Intent();
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("list", list);
                                        intent.putExtra("data", bundle);
                                        intent.putExtra("project_id", taskNewInfo.getProjectid());
                                        intent.putExtra("project_name", taskNewInfo.getProject_name());
                                        intent.putExtra("task_pack_id", "");
                                        intent.putExtra("task_pack_name", "");
                                        intent.putExtra("task_id", taskNewInfo.getTask_id());
                                        intent.putExtra("task_name", taskNewInfo.getTask_name());
                                        intent.putExtra("store_id", taskNewInfo.getStore_id());
                                        intent.putExtra("store_num", taskNewInfo.getStore_num());
                                        intent.putExtra("store_name", taskNewInfo.getStore_name());
                                        intent.putExtra("category1", "");
                                        intent.putExtra("category2", "");
                                        intent.putExtra("category3", "");
                                        intent.putExtra("is_desc", "");
                                        intent.putExtra("code", taskNewInfo.getCode());
                                        intent.putExtra("brand", taskNewInfo.getBrand());
                                        intent.putExtra("outlet_batch", taskNewInfo.getOutlet_batch());
                                        intent.putExtra("p_batch", taskNewInfo.getP_batch());
                                        intent.putExtra("newtask", "1");//判断是否是新手任务 1是0否
                                        intent.putExtra("photo_compression", taskNewInfo.getPhoto_compression());
                                        if ("1".equals(type) || "8".equals(type)) {//拍照任务
                                            intent.setClass(TaskitemRecodillustrateActivity.this, TaskitemPhotographyNextYActivity.class);
                                            startActivity(intent);
                                        } else if ("2".equals(type)) {//视频任务
                                            intent.setClass(TaskitemRecodillustrateActivity.this, TaskitemShotActivity.class);
                                            startActivity(intent);
                                        } else if ("3".equals(type)) {//记录任务
                                            intent.setClass(TaskitemRecodillustrateActivity.this, TaskitemEditActivity.class);
                                            startActivity(intent);
                                        } else if ("4".equals(type)) {//定位任务
                                            intent.setClass(TaskitemRecodillustrateActivity.this, TaskitemMapActivity.class);
                                            startActivity(intent);
                                        } else if ("5".equals(type)) {//录音任务
                                            intent.setClass(TaskitemRecodillustrateActivity.this, TaskitemRecodillustrateActivity.class);
                                            startActivity(intent);
                                        } else if ("6".equals(type)) {//扫码任务
                                            intent.setClass(TaskitemRecodillustrateActivity.this, ScanTaskNewActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                }
                            }
                            baseFinish();
                        }
                    } else {
                        Tools.showToast(TaskitemRecodillustrateActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskitemRecodillustrateActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskitemRecodillustrateActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    public boolean isVoicePermission() {
        try {
            AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.MIC, 22050, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, AudioRecord.getMinBufferSize(22050, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT));
            record.startRecording();
            int recordingState = record.getRecordingState();
            if (recordingState == AudioRecord.RECORDSTATE_STOPPED) {
                return false;
            }
            record.release();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String paramsToString() {
        Map<String, String> parames = new HashMap<>();
        parames.put("usermobile", AppInfo.getName(this));
        parames.put("pid", task_pack_id);
        parames.put("task_id", task_id);
        parames.put("note", taskitmrecodill_edittext.getText().toString().trim());
        parames.put("storeid", store_id);
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
                try {
                    data = key + "=" + URLEncoder.encode(parames.get(key).trim(), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    data = key + "=" + parames.get(key).trim();
                }
            } else {
                data = data + "&" + key + "=" + parames.get(key).trim();
            }
        }
        return data;
    }

    public void onError(MediaRecorder mr, int what, int extra) {
        try {
            if (mr != null)
                mr.reset();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
    }
}
