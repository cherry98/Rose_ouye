package com.orange.oy.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.adapter.TaskitemDetailNewAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.OfflineDBHelper;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.CloseTaskDialog;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.TaskitemDetailNewInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.service.RecordService;
import com.orange.oy.view.AppTitle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * 任务包列表
 */
public class OfflinePackageActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, AdapterView
        .OnItemClickListener, View.OnClickListener {
    private AppTitle taskitdt_title;
    private String searchStr;

    private void initTitle(String title) {
        taskitdt_title = (AppTitle) findViewById(R.id.taskitdt_title);
        taskitdt_title.showBack(this);
        taskitdt_title.settingHint("可搜索任务名称、任务包名称");
        taskitdt_title.showSearch(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
            }
        }, new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchStr = v.getText().toString().trim();
                    refreshData();
                    v.setText(searchStr);
                    return true;
                }
                return false;
            }
        });
        taskitdt_title.settingExit("清空条件", new AppTitle.OnExitClickForAppTitle() {
            public void onExit() {
                searchStr = "";
                taskitdt_title.setSearchText("");
                refreshData();
            }
        });
    }

    public void onBack() {
        if (RecordService.isStart() && taskitdt_recode_layout.getVisibility() == View.VISIBLE) {
            showDialog();
        } else {
            baseFinish();
        }
    }

    protected void onStop() {
        super.onStop();
        if (taskitemDetailAdapter != null) {
            taskitemDetailAdapter.stopUpdata();
        }
        TaskListDetailActivity.isRefresh = true;
        TaskscheduleDetailActivity.isRefresh = true;
    }

    private String storeid;
    private int page;

    private PullToRefreshListView taskitdt_listview;
    private View taskitdt_recode_layout, taskitdt_recode_layout_line;
    private TaskitemDetailNewAdapter taskitemDetailAdapter;
    private ArrayList<TaskitemDetailNewInfo> list;
    private View taskitdt_start_store, taskitdt_stop_store;
    private String project_id;
    private String photo_compression;
    private OfflineDBHelper offlineDBHelper;
    private int is_watermark;
    private String code, brand;
    private boolean is_taskphoto;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitemdetail);
        offlineDBHelper = new OfflineDBHelper(this);
        updataDBHelper = new UpdataDBHelper(this);
        Intent data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        //TODO 此页面可能会在后台隐藏很长时间，要手动添加变量保存！
        storeid = data.getStringExtra("id");
        projectname = data.getStringExtra("projectname");
        project_id = data.getStringExtra("project_id");
        store_name = data.getStringExtra("store_name");
        store_num = data.getStringExtra("store_num");
        province = data.getStringExtra("province");
        city = data.getStringExtra("city");
        is_watermark = data.getIntExtra("is_watermark", 0);
        code = data.getStringExtra("code");
        brand = data.getStringExtra("brand");
        is_taskphoto = "1".equals(data.getStringExtra("is_takephoto"));
        taskitdt_recode_layout = findViewById(R.id.taskitdt_recode_layout);
        taskitdt_recode_layout_line = findViewById(R.id.taskitdt_recode_layout_line);
        if (data.getIntExtra("is_record", 0) == 1) {
            taskitdt_recode_layout.setVisibility(View.VISIBLE);
            taskitdt_recode_layout_line.setVisibility(View.VISIBLE);
        }
        photo_compression = data.getStringExtra("photo_compression");
        initTitle(projectname);
        list = new ArrayList<>();
        if (is_taskphoto) {
            list.add(null);
            list.add(null);
        }
        taskitemDetailAdapter = new TaskitemDetailNewAdapter(this, list);
        taskitemDetailAdapter.setShowTitle(true);
        taskitemDetailAdapter.setIs_takephoto(is_taskphoto);
        taskitemDetailAdapter.setPhoto_compression(photo_compression);
        taskitemDetailAdapter.setOnRefushListener(onRefreshListener);
        taskitemDetailAdapter.isOffline(true);
        taskitdt_listview = (PullToRefreshListView) findViewById(R.id.taskitdt_listview);
        taskitdt_start_store = findViewById(R.id.taskitdt_start_store);
        taskitdt_stop_store = findViewById(R.id.taskitdt_stop_store);
        taskitdt_listview.setMode(PullToRefreshBase.Mode.DISABLED);
        taskitdt_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshData();
            }

            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                getData();
            }
        });
        taskitdt_listview.setOnItemClickListener(this);
        taskitdt_listview.setAdapter(taskitemDetailAdapter);
        taskitdt_start_store.setOnClickListener(this);
        taskitdt_stop_store.setOnClickListener(this);
        if (RecordService.isStart()) {
            taskitdt_start_store.setVisibility(View.INVISIBLE);
            taskitdt_stop_store.setVisibility(View.VISIBLE);
        } else {
            taskitdt_start_store.setVisibility(View.VISIBLE);
            taskitdt_stop_store.setVisibility(View.INVISIBLE);
        }
        isRefresh = false;
        checkPermission();
        long sdsize = Tools.getSDFreeSize();
        if (sdsize < 11) {
            ConfirmDialog.showDialog(this, "提示", getResources().getString(R.string.sdcardsize), null, null, null, true,
                    new ConfirmDialog.OnSystemDialogClickListener() {
                        public void leftClick(Object object) {
                        }

                        public void rightClick(Object object) {
                        }
                    });
        }
        if (offlineDBHelper.isHadDownList()) {
            String msg;
            if (TextUtils.isEmpty(Tools.GetNetworkType(this))) {
                msg = "有示例文件未下载完成，请开启网络！";
            } else {
                msg = "有示例文件未下载完成";
            }
            ConfirmDialog.showDialog(this, "提示", msg, null, null, null, true,
                    new ConfirmDialog.OnSystemDialogClickListener() {
                        public void leftClick(Object object) {
                        }

                        public void rightClick(Object object) {
                        }
                    });
        }
        getData();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, AppInfo
                        .REQUEST_CODE_ASK_RECORD_AUDIO);
                return;
            }
            checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, AppInfo
                        .REQUEST_CODE_ASK_CAMERA);
                return;
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        switch (requestCode) {
            case AppInfo.REQUEST_CODE_ASK_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                } else {
                    Tools.showToast(OfflinePackageActivity.this, "权限获取失败");
                }
                break;
            case AppInfo.REQUEST_CODE_ASK_RECORD_AUDIO:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                } else {
                    Tools.showToast(OfflinePackageActivity.this, "权限获取失败");
                    baseFinish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public static boolean isRefresh;

    protected void onResume() {
        super.onResume();
        if (isRefresh) {
            isRefresh = false;
            getData();
        }
    }

    private TaskitemDetailActivity.OnRefreshListener onRefreshListener = new TaskitemDetailActivity.OnRefreshListener() {
        public void refresh(String packageid) {
            isRefresh = false;
            if (list == null) return;
            int size = list.size();
            for (int i = 0; i < size; i++) {
                TaskitemDetailNewInfo taskitemDetailNewInfo = list.get(i);
                if (taskitemDetailNewInfo == null) {
                    continue;
                }
                if (taskitemDetailNewInfo != null && taskitemDetailNewInfo.getIsPackage().equals("1") && packageid.equals
                        (taskitemDetailNewInfo.getId())) {//是任务包
                    list.remove(i);
                    taskitemDetailAdapter.notifyDataSetChanged();
                    break;
                }
            }
//            TaskitemDetailActivity.this.refresh();
        }
    };

    private void refreshData() {
        if (list != null) {
            list.clear();
            list.add(null);
            list.add(null);
            taskitemDetailAdapter.notifyDataSetChanged();
        }
        page = 1;
        getData();
    }

    private void getData() {
        new getDataAsyncTask().executeOnExecutor(Executors.newCachedThreadPool());
    }

    private UpdataDBHelper updataDBHelper;

    class getDataAsyncTask extends AsyncTask {
        protected void onPreExecute() {
            CustomProgressDialog.showProgressDialog(OfflinePackageActivity.this, "正在加载...");
        }

        protected Object doInBackground(Object[] params) {
            String username = AppInfo.getName(OfflinePackageActivity.this);
            list = offlineDBHelper.getTaskPackage(username, project_id, storeid, searchStr);
            if (TextUtils.isEmpty(searchStr) && list.isEmpty()) {
                Map<String, String> map = new HashMap<>();
                map.put("token", Tools.getToken());
                map.put("storeid", storeid);
                map.put("usermobile", username);
                updataDBHelper.addUpdataTask(username, project_id, projectname, store_num, null, storeid, store_name,
                        null, null, "-5", null, null, null, null, null, Tools.getTimeSS() + "-5",
                        Urls.Startupload,
                        null, null, UpdataDBHelper.Updata_file_type_video, map, null, false, null, null, true);
            }
            if (is_taskphoto) {
                list.add(0, null);
                list.add(1, null);
            }
            return null;
        }

        protected void onPostExecute(Object o) {
            taskitemDetailAdapter.resetList(list);
            taskitemDetailAdapter.notifyDataSetChanged();
            CustomProgressDialog.Dissmiss();
        }

    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!RecordService.isStart() && taskitdt_recode_layout.getVisibility() == View.VISIBLE) {
            Tools.showToast(this, "请先点击开始进店按钮");
            return;
        }
        if (is_taskphoto && position < 3) {
            if (position == 1) {
                Intent intent = new Intent(OfflinePackageActivity.this,
                        Camerase.class);
                intent.putExtra("projectid", project_id);
                intent.putExtra("storeid", storeid);
                intent.putExtra("storecode", store_num);
                startActivity(intent);
            } else {
                Intent intent = new Intent(OfflinePackageActivity.this, AlbumActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("projectid", project_id);
                bundle.putString("storeid", storeid);
                bundle.putInt("onlyShow", 1);
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
            }
            return;
        }
        TaskitemDetailNewInfo taskitemDetailNewInfo = list.get(position - 1);
        if (taskitemDetailNewInfo.getIsPackage().equals("1")) {
            if (taskitemDetailNewInfo.getIsClose().equals("1")) {
                Intent intent = new Intent(this, OfflineTaskActivity.class);
                intent.putExtra("project_id", project_id);
                intent.putExtra("project_name", projectname);
                intent.putExtra("task_pack_id", taskitemDetailNewInfo.getId());
                intent.putExtra("pack_name", taskitemDetailNewInfo.getName());
                intent.putExtra("store_id", taskitemDetailNewInfo.getStoreid());
                intent.putExtra("store_num", store_num);
                intent.putExtra("store_name", taskitemDetailNewInfo.getStorename());
                intent.putExtra("isCategory", taskitemDetailNewInfo.isCategory());
                intent.putExtra("photo_compression", photo_compression);
                intent.putExtra("is_watermark", is_watermark);
                intent.putExtra("code", code);
                intent.putExtra("brand", brand);
                startActivity(intent);
            }
        } else {
            if (taskitemDetailNewInfo.getTask_type().equals("1")) {
                Intent intent = new Intent(this, OfflineTaskitemPhotographyActivity.class);
                intent.putExtra("project_id", project_id);
                intent.putExtra("project_name", projectname);
                intent.putExtra("task_pack_id", "");
                intent.putExtra("task_pack_name", "");
                intent.putExtra("task_id", taskitemDetailNewInfo.getId());
                intent.putExtra("task_name", taskitemDetailNewInfo.getName());
                intent.putExtra("store_id", taskitemDetailNewInfo.getStoreid());
                intent.putExtra("store_num", store_num);
                intent.putExtra("store_name", taskitemDetailNewInfo.getStorename());
                intent.putExtra("photo_compression", photo_compression);
                intent.putExtra("is_watermark", is_watermark);
                intent.putExtra("category1", "");
                intent.putExtra("category2", "");
                intent.putExtra("category3", "");
                intent.putExtra("code", code);
                intent.putExtra("brand", brand);
                startActivity(intent);
            } else if (taskitemDetailNewInfo.getTask_type().equals("2")) {
                Intent intent = new Intent(this, OfflineTaskitemShotillustrateActivity.class);
                intent.putExtra("project_id", project_id);
                intent.putExtra("project_name", projectname);
                intent.putExtra("task_pack_id", "");
                intent.putExtra("task_pack_name", "");
                intent.putExtra("task_id", taskitemDetailNewInfo.getId());
                intent.putExtra("task_name", taskitemDetailNewInfo.getName());
                intent.putExtra("store_id", taskitemDetailNewInfo.getStoreid());
                intent.putExtra("store_num", store_num);
                intent.putExtra("store_name", taskitemDetailNewInfo.getStorename());
                intent.putExtra("category1", "");
                intent.putExtra("category2", "");
                intent.putExtra("category3", "");
                intent.putExtra("code", code);
                intent.putExtra("brand", brand);
                startActivity(intent);
            } else if (taskitemDetailNewInfo.getTask_type().equals("3")) {
                Intent intent = new Intent(this, OfflineTaskitemEditillustrateActivity.class);
                intent.putExtra("project_id", project_id);
                intent.putExtra("project_name", projectname);
                intent.putExtra("task_pack_id", "");
                intent.putExtra("task_pack_name", "");
                intent.putExtra("taskid", taskitemDetailNewInfo.getId());
                intent.putExtra("task_name", taskitemDetailNewInfo.getName());
                intent.putExtra("tasktype", "3");
                intent.putExtra("store_id", taskitemDetailNewInfo.getStoreid());
                intent.putExtra("store_num", store_num);
                intent.putExtra("store_name", taskitemDetailNewInfo.getStorename());
                intent.putExtra("category1", "");
                intent.putExtra("category2", "");
                intent.putExtra("category3", "");
                startActivity(intent);
            } else if (taskitemDetailNewInfo.getTask_type().equals("4")) {
                Intent intent = new Intent(this, OfflineTaskitemMapActivity.class);
                intent.putExtra("project_id", project_id);
                intent.putExtra("project_name", projectname);
                intent.putExtra("task_pack_id", "");
                intent.putExtra("task_pack_name", "");
                intent.putExtra("task_id", taskitemDetailNewInfo.getId());
                intent.putExtra("task_name", taskitemDetailNewInfo.getName());
                intent.putExtra("store_id", taskitemDetailNewInfo.getStoreid());
                intent.putExtra("store_num", store_num);
                intent.putExtra("store_name", taskitemDetailNewInfo.getStorename());
                intent.putExtra("category1", "");
                intent.putExtra("category2", "");
                intent.putExtra("category3", "");
                intent.putExtra("code", code);
                intent.putExtra("brand", brand);
                startActivity(intent);
            } else if (taskitemDetailNewInfo.getTask_type().equals("5")) {
                Intent intent = new Intent(this, OfflineTaskitemRecodillustrateActivity.class);
                intent.putExtra("project_id", project_id);
                intent.putExtra("project_name", projectname);
                intent.putExtra("task_pack_id", "");
                intent.putExtra("task_pack_name", "");
                intent.putExtra("task_id", taskitemDetailNewInfo.getId());
                intent.putExtra("task_name", taskitemDetailNewInfo.getName());
                intent.putExtra("store_id", taskitemDetailNewInfo.getStoreid());
                intent.putExtra("store_num", store_num);
                intent.putExtra("store_name", taskitemDetailNewInfo.getStorename());
                intent.putExtra("category1", "");
                intent.putExtra("category2", "");
                intent.putExtra("category3", "");
                intent.putExtra("code", code);
                intent.putExtra("brand", brand);
                startActivity(intent);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppInfo.TaskitemDetailRequestCodeForTake: {
                if (resultCode == AppInfo.ShotSuccessResultCode && CloseTaskDialog.isShow()) {
                    int index = data.getIntExtra("index", 0);
                    String path = data.getStringExtra("path");
                    if (index != 0) {
                    }
                }
            }
            break;
        }
    }

    protected void onDestroy() {
//        if (RecordService.isStart()) { TODO
//            unbindService(conn);
//        }
        if (RecordService.isStart()) {
            stopService(new Intent(this, RecordService.class));
        }
        CustomProgressDialog.Dissmiss();
        super.onDestroy();
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

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taskitdt_start_store: {
                if (!RecordService.isStart()) {
                    if (!isVoicePermission()) {
                        Tools.showToast(this, "录音无法正常启动，请检查权限设置！");
                    } else {
//                    Intent service = new Intent(this, RecordService.class);
                        Intent service = new Intent("com.orange.oy.recordservice").setPackage("com.orange.oy");
                        service.putExtra("province", province);
                        service.putExtra("usermobile", AppInfo.getName(OfflinePackageActivity.this));
                        service.putExtra("project_id", project_id);
                        service.putExtra("projectname", projectname);
                        service.putExtra("store_name", store_name);
                        service.putExtra("store_num", store_num);
                        service.putExtra("city", city);
                        service.putExtra("storeid", storeid);
                        service.putExtra("isOffline", true);
                        service.putExtra("dirName", AppInfo.getName(OfflinePackageActivity.this) + "/" + storeid);
                        service.putExtra("fileName", Tools.getTimeSS() + Tools.getDeviceId(OfflinePackageActivity.this) +
                                storeid);
                        startService(service);
                        taskitdt_start_store.setVisibility(View.INVISIBLE);
                        taskitdt_stop_store.setVisibility(View.VISIBLE);
//                    bindService(service, conn, Context.BIND_AUTO_CREATE);
//
                    }
                } else {
                    Tools.showToast(this, "录音已经启动");
                }
            }
            break;
            case R.id.taskitdt_stop_store: {
                try {
                    if (RecordService.isStart()) {
//                        unbindService(conn);
                        Intent service = new Intent(this, RecordService.class);
                        stopService(service);
                        taskitdt_start_store.setVisibility(View.VISIBLE);
                        taskitdt_stop_store.setVisibility(View.INVISIBLE);
                    }
                } catch (Exception e) {
                }
            }
            break;
        }
    }

    private String projectname, store_name, store_num, province, city;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (CloseTaskDialog.isOpen()) {
                return true;
            } else if (RecordService.isStart() && taskitdt_recode_layout.getVisibility() == View.VISIBLE) {
                showDialog();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showDialog() {
        ConfirmDialog.showDialog(this, "提示", "退出页面会中止录音，确定退出吗？", null, null, null, true, new ConfirmDialog
                .OnSystemDialogClickListener() {
            public void leftClick(Object object) {
            }

            public void rightClick(Object object) {
                baseFinish();
            }
        });
    }

    private ServiceConnection conn = new ServiceConnection() {
        public void onServiceDisconnected(ComponentName name) {
            Tools.d("onServiceDisconnected");
            taskitdt_start_store.setVisibility(View.VISIBLE);
            taskitdt_stop_store.setVisibility(View.GONE);
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            RecordService.MyBinder binder = (RecordService.MyBinder) service;
            RecordService bindService = binder.getService();
            Intent intent = new Intent();
            intent.putExtra("province", province);
            intent.putExtra("usermobile", AppInfo.getName(OfflinePackageActivity.this));
            intent.putExtra("projectname", projectname);
            intent.putExtra("store_name", store_name);
            intent.putExtra("store_num", store_num);
            intent.putExtra("city", city);
            intent.putExtra("storeid", storeid);
            bindService.MyMethod(AppInfo.getName(OfflinePackageActivity.this) + "/" + storeid, "record"
                    , false, intent);
            taskitdt_start_store.setVisibility(View.GONE);
            taskitdt_stop_store.setVisibility(View.VISIBLE);
        }
    };
}
