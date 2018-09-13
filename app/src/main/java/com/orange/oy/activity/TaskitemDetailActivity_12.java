package com.orange.oy.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.ExifInterface;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.activity.bright.BrightDZXListActivity;
import com.orange.oy.activity.bright.BrightPersonInfoActivity;
import com.orange.oy.activity.calltask.CallTaskActivity;
import com.orange.oy.activity.calltask.CallTaskResetActivity;
import com.orange.oy.activity.createtask_321.ScreenshotActivity;
import com.orange.oy.activity.createtask_321.TaskExperienceActivity;
import com.orange.oy.activity.mycorps_315.TeamMemberTodoActivity;
import com.orange.oy.activity.newtask.MyTaskDetailActivity;
import com.orange.oy.activity.newtask.MyTaskListActivity;
import com.orange.oy.activity.newtask.TaskDistActivity;
import com.orange.oy.activity.scan.ScanTaskNewActivity;
import com.orange.oy.activity.scan.ScanTaskResetActivity;
import com.orange.oy.adapter.TaskitemDetailNewAdapter_12;
import com.orange.oy.allinterface.NewOnItemClickListener;
import com.orange.oy.allinterface.OnRefreshListener;
import com.orange.oy.allinterface.OnRightClickListener;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.MyUMShareUtils;
import com.orange.oy.base.Tools;
import com.orange.oy.db.OfflineDBHelper;
import com.orange.oy.db.SystemDBHelper;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.ChangeshopDialog;
import com.orange.oy.dialog.CloseTaskDialog;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.UMShareDialog;
import com.orange.oy.info.TaskitemDetailNewInfo;
import com.orange.oy.info.UpdataInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.service.RecordService;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * 任务包列表
 */
public class TaskitemDetailActivity_12 extends BaseActivity implements AppTitle.OnBackClickForAppTitle,
        View.OnClickListener {
    private AppTitle taskitdt_title;
    private String searchStr;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

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
                    refreshDataLeft();
                    v.setText("");
                    return true;
                }
                return false;
            }
        });
    }

    private void settingExit(boolean isRight) {
        if (isRight) {
            if (!"3".equals(type)) {
                taskitdt_title.settingExit("全部重做", exitRight);
            } else {
                taskitdt_title.settingExit("考试任务", exitRight);
            }
            taskitdt_title.hideIllustrate();
        } else {
            taskitdt_title.hideExit2();
            taskitdt_title.setIllustrate(R.mipmap.share2, exitLeft);
        }
    }

    private AppTitle.OnExitClickForAppTitle exitLeft = new AppTitle.OnExitClickForAppTitle() {
        public void onExit() {
            UMShareDialog.showDialog(TaskitemDetailActivity_12.this, false, new UMShareDialog.UMShareListener() {
                @Override
                public void shareOnclick(int type) {
                    String webUrl = Urls.ShareProject + "?&projectid=" + project_id + "&usermobile=" + AppInfo.getName(TaskitemDetailActivity_12.this) + "&sign=" + sign;
                    MyUMShareUtils.umShare(TaskitemDetailActivity_12.this, type, webUrl);
                }
            });
        }
    };

    private AppTitle.OnExitClickForAppTitle exitRight = new AppTitle.OnExitClickForAppTitle() {
        public void onExit() {
            if ("0".equals(is_redo)) {
                Tools.showToast(TaskitemDetailActivity_12.this, "还有任务没有完成，不可以点击重做");
                return;
            }
            ConfirmDialog.showDialog(TaskitemDetailActivity_12.this, "确定重做吗？", null, null, null, null
                    , true, new ConfirmDialog.OnSystemDialogClickListener() {
                        public void leftClick(Object object) {
                        }

                        public void rightClick(Object object) {
                            Redo();
                        }
                    });
        }
    };

    private ChangeshopDialog.OnItemClickListener onItemClickListener = new ChangeshopDialog.OnItemClickListener() {
        public void itemClickForChangeshop(int position, Object object) {
            storeid = storeList.get(position);
            page = 1;
            getData();
        }
    };

    public void onBack() {
        if (RecordService.isStart() && taskitdt_recode_layout.getVisibility() == View.VISIBLE) {
            showDialog();
            return;
        }
        if (backPage == 0) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("pageindex", 1);
            startActivity(intent);
        }
        baseFinish();
    }

    public void onBackPressed() {
        if (backPage == 0) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("pageindex", 1);
            startActivity(intent);
        }
        super.onBackPressed();
    }

    protected void onStop() {
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        if (taskitemDetailAdapter != null) {
            taskitemDetailAdapter.stopUpdata();
        }
        if (Taskindex != null) {
            Taskindex.stop(dataUrl);
        }
        if (Changestore != null) {
            Changestore.stop(Urls.Changestore);
        }
        if (Taskindexcomplete != null) {
            Taskindexcomplete.stop(Urls.Taskindexcomplete);
        }
        if (Redo != null) {
            Redo.stop(Urls.Redo);
        }
        if (Startupload != null) {
            Startupload.stop(Urls.Startupload);
        }
        if (sign != null) {
            Sign.stop(Urls.Sign);
        }
        TaskListDetailActivity.isRefresh = true;
        TaskscheduleDetailActivity.isRefresh = true;
        BrightDZXListActivity.isRefresh = true;
        TaskDistActivity.isRefresh = true;
        MyTaskDetailActivity.isRefresh = true;
        MyTaskListActivity.isRefresh = true;
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    private String storeid;
    private int page, pageRight;

    private void initNetworkConnection() {
        Taskindex = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                if ("0".equals(index)) {//项目预览
                    params.put("projectId", project_id);
                } else {
                    params.put("storeid", storeid);
                }
                params.put("page", page + "");
                params.put("token", Tools.getToken());
                if (!TextUtils.isEmpty(searchStr)) {
                    params.put("keyword", searchStr);
                }
                return params;
            }
        };
        Taskindexcomplete = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("storeid", storeid);
                params.put("page", pageRight + "");
                params.put("token", Tools.getToken());
                if (!TextUtils.isEmpty(searchStr)) {
                    params.put("keyword", searchStr);
                }
                return params;
            }
        };
        Changestore = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("project_id", project_id);
                params.put("user_mobile", AppInfo.getName(TaskitemDetailActivity_12.this));
                params.put("token", Tools.getToken());
                return params;
            }
        };
        Changestore.setIsShowDialog(true);
        Redo = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("storeid", storeid);
                params.put("usermobile", AppInfo.getName(TaskitemDetailActivity_12.this));
                return params;
            }
        };
        Redo.setIsShowDialog(true);
        Startupload = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("storeid", storeid);
                params.put("usermobile", AppInfo.getName(TaskitemDetailActivity_12.this));
                return params;
            }
        };
        Startupload.setIsShowDialog(true);
        taskFinish = new NetworkConnection(this) {//单个任务查看详情
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("storeid", storeid);
                params.put("token", Tools.getToken());
                params.put("pid", "");
                params.put("p_batch", p_batch);
                params.put("outlet_batch", outlet_batch);
                params.put("taskid", task_id);
                return params;
            }
        };
        Sign = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                String key = "projectid=" + project_id + "&usermobile=" + AppInfo.getName(TaskitemDetailActivity_12.this);
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("key", key);
                return params;
            }
        };
    }

    private boolean isRightRefreshing = false;//待提交列表是否在刷新
    private BroadcastReceiver UpProgressbarBroadcast = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(AppInfo.BroadcastReceiver_TAKEPHOTO)) {
                String username = intent.getStringExtra("username");
                String projectid = intent.getStringExtra("projectid");
                String storeid = intent.getStringExtra("storeid");
                String taskpackid = intent.getStringExtra("taskpackid");
                String taskid = intent.getStringExtra("taskid");
                int progress = intent.getIntExtra("size", 0);
                if (!isRightRefreshing && TextUtils.isEmpty(taskpackid) && !TextUtils.isEmpty(taskid) &&
                        TaskitemDetailActivity_12.this.username.equals(username) &&
                        !TextUtils.isEmpty(projectid) && projectid.equals(project_id) &&
                        !TextUtils.isEmpty(storeid) && storeid.equals(TaskitemDetailActivity_12.this.storeid)) {
                    boolean isRefresh = taskitdt_listview_right != null && taskitdt_listview_right.getVisibility() == View.VISIBLE;
                    for (TaskitemDetailNewInfo temp : list_right) {
                        if (isRightRefreshing) {
                            break;
                        }
                        if (taskid.equals(temp.getId())) {
                            temp.setProgress(progress);
                            if (isRefresh) {
                                if (temp.getTaskitemDetail_12View() != null)
                                    temp.getTaskitemDetail_12View().settingProgressbar(progress);
                            }
                            break;
                        }
                    }
                } else if ("-3".equals(intent.getStringExtra("tasktype")) && intent.getBooleanExtra("isSuccess", false)
                        && storeid.equals(intent.getStringExtra("storeid")) && "1".equals(is_complete)) {
                    //全程录音资料回收
                    taskitdt_recode_state.setText("完成");
                    taskitdt_recode_switch.setVisibility(View.GONE);
                }
            }
        }
    };

    private void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppInfo.BroadcastReceiver_TAKEPHOTO);
        context.registerReceiver(UpProgressbarBroadcast, filter);
    }

    private void unregisterReceiver(Context context) {
        context.unregisterReceiver(UpProgressbarBroadcast);
    }

    private PullToRefreshListView taskitdt_listview_left, taskitdt_listview_right;
    private View taskitdt_recode_layout;
    private TaskitemDetailNewAdapter_12 taskitemDetailAdapter;
    private TaskitemDetailNewAdapter_12 taskitemDetailAdapter2;
    private ArrayList<TaskitemDetailNewInfo> list;
    private ArrayList<TaskitemDetailNewInfo> list_right;
    private NetworkConnection Taskindex, Changestore, Taskindexcomplete, Redo, Startupload, taskFinish, Sign;
    private String project_id;
    private String photo_compression;
    private String is_desc;
    private String codeStr, brand, task_pack_id;
    private String is_watermark;
    private SystemDBHelper systemDBHelper;
    private final static int TakeRequest = 0x100;
    private String username;
    private UpdataDBHelper updataDBHelper;
    private ArrayList<String> selectImgList = new ArrayList<>();
    private ArrayList<String> originalImgList = new ArrayList<>();
    private String task_id, outlet_batch, p_batch, task_name, type, addr, code, project_type;
    private TextView taskitdt_recode_switch;
    private String index, dataUrl;//加载数据url
    private TextView taskitdt_stroe_ablum, taskitdt_upload_store, taskitdt_recode_state;
    private boolean is_takephoto;
    private int backPage = -1;//0:直接返回首页的个人任务列表

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitemdetail_12);
        offlineDBHelper = new OfflineDBHelper(this);
        updataDBHelper = new UpdataDBHelper(this);
        systemDBHelper = new SystemDBHelper(this);
        registerReceiver(this);
        username = AppInfo.getName(this);
        initNetworkConnection();
        Intent data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        backPage = data.getIntExtra("backpage", -1);
        is_takephoto = "1".equals(data.getStringExtra("is_takephoto"));
        storeid = data.getStringExtra("id");
        project_type = data.getStringExtra("project_type");
        projectname = data.getStringExtra("projectname");
        project_id = data.getStringExtra("project_id");
        store_name = data.getStringExtra("store_name");
        store_num = data.getStringExtra("store_num");
//        province = data.getStringExtra("province");
//        city = data.getStringExtra("city");
        is_desc = data.getStringExtra("is_desc");
//        photo_compression = data.getStringExtra("photo_compression");
        codeStr = data.getStringExtra("code");
        brand = data.getStringExtra("brand");
        is_watermark = data.getStringExtra("is_watermark");
        type = data.getStringExtra("type");
        addr = data.getStringExtra("addr");
        code = data.getStringExtra("code");
        initTitle(projectname);
        index = data.getStringExtra("index");//任务预览
        taskitdt_recode_layout = findViewById(R.id.taskitdt_recode_layout);
        if (index != null && ("0".equals(index) || "2".equals(index))) {
            if ("2".equals(index)) {
                dataUrl = Urls.Taskindex;
            } else {
                dataUrl = Urls.CheckPreview;
            }
            taskitdt_title.hideExit();
            findViewById(R.id.carry_layout).setVisibility(View.GONE);
            findViewById(R.id.tabline_layout).setVisibility(View.GONE);
            findViewById(R.id.bottomlayout).setVisibility(View.GONE);
            taskitdt_recode_layout.setVisibility(View.GONE);
        } else {
            dataUrl = Urls.Taskindex;
            photoNumber();
            taskitdt_recode_layout.setVisibility(View.VISIBLE);
        }
        list = new ArrayList<>();
        list_right = new ArrayList<>();
        taskitdt_stroe_ablum = (TextView) findViewById(R.id.taskitdt_stroe_ablum);
        taskitdt_recode_state = (TextView) findViewById(R.id.taskitdt_recode_state);
        taskitdt_upload_store = (TextView) findViewById(R.id.taskitdt_upload_store);
        if (!is_takephoto) {
            taskitdt_stroe_ablum.setVisibility(View.GONE);
        } else {
            taskitdt_stroe_ablum.setVisibility(View.VISIBLE);
            taskitdt_stroe_ablum.setOnClickListener(this);
        }
        taskitdt_recode_switch = (TextView) findViewById(R.id.taskitdt_recode_switch);
        taskitemdetail_tab_left = (TextView) findViewById(R.id.taskitemdetail_tab_left);
        taskitemdetail_tab_right = (TextView) findViewById(R.id.taskitemdetail_tab_right);
        taskitemdetail_tab_left_line = findViewById(R.id.taskitemdetail_tab_left_line);
        taskitemdetail_tab_right_line = findViewById(R.id.taskitemdetail_tab_right_line);
        taskitdt_listview_left = (PullToRefreshListView) findViewById(R.id.taskitdt_listview_left);
        taskitdt_listview_right = (PullToRefreshListView) findViewById(R.id.taskitdt_listview_right);
        taskitdt_listview_left.setCanDelete(true);
        taskitdt_listview_right.setCanDelete(true);
        taskitdt_listview_left.setMode(PullToRefreshBase.Mode.BOTH);
        taskitdt_listview_left.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshDataLeft();
            }

            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                getData();
            }
        });
        taskitdt_listview_right.setMode(PullToRefreshBase.Mode.BOTH);
        taskitdt_listview_right.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshDataRight();
            }

            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                pageRight++;
                getDataRight();
            }
        });
        taskitemDetailAdapter = new TaskitemDetailNewAdapter_12(this, taskitdt_listview_left, list, index);
        taskitemDetailAdapter.settingRightText("连拍");
        taskitemDetailAdapter.setPhoto_compression(photo_compression, is_watermark);
        taskitemDetailAdapter.setOnRefushListener(onRefreshListener);
        taskitemDetailAdapter.setNewOnItemClickListener(newOnItemClickListener);
        taskitemDetailAdapter.setOnRightClickListener(new OnRightClickListener() {
            public void onRightClick(Object object) {//连拍按钮
                if (object == null) {
                    Tools.showToast(TaskitemDetailActivity_12.this, "异常，请清理内存后重新打开页面");
                    return;
                }
                if (!RecordService.isStart() && taskitdt_recode_layout.getVisibility() == View.VISIBLE) {
                    Tools.showToast(TaskitemDetailActivity_12.this, "请先点击开始进店按钮");
                    return;
                }
                TaskitemDetailNewInfo taskitemDetailNewInfo = (TaskitemDetailNewInfo) object;
                int size = systemDBHelper.getPictureNumForprivate(AppInfo.getName(TaskitemDetailActivity_12.this),
                        taskitemDetailNewInfo.getProjectid(), taskitemDetailNewInfo.getStoreid(), null, taskitemDetailNewInfo
                                .getId());
                if (size < taskitemDetailNewInfo.getMaxTask()) {
                    Intent intent = new Intent(TaskitemDetailActivity_12.this, Camerase.class);
                    intent.putExtra("projectid", taskitemDetailNewInfo.getProjectid());
                    intent.putExtra("storeid", taskitemDetailNewInfo.getStoreid());
                    intent.putExtra("storecode", taskitemDetailNewInfo.getStoreNum());
                    intent.putExtra("taskid", taskitemDetailNewInfo.getId());
                    Tools.d(taskitemDetailNewInfo.getMaxTask() + "");
                    intent.putExtra("maxTake", taskitemDetailNewInfo.getMaxTask() - size);
                    intent.putExtra("state", 1);
                    startActivity(intent);
                } else {
                    Tools.showToast(TaskitemDetailActivity_12.this, "拍照数量已达最大值！");
                }
            }
        });
        taskitemDetailAdapter2 = new TaskitemDetailNewAdapter_12(this, taskitdt_listview_right, list_right, index);
        taskitemDetailAdapter2.settingRightText("补拍");
        taskitemDetailAdapter2.isShowProgressbar(true);
        taskitemDetailAdapter2.setPhoto_compression(photo_compression, is_watermark);
        taskitemDetailAdapter2.setOnRefushListener(onRefreshListener);
        taskitemDetailAdapter2.setNewOnItemClickListener(newOnItemClickListener2);
        taskitemDetailAdapter2.setOnRightClickListener(new OnRightClickListener() {
            public void onRightClick(Object object) {//补拍按钮
                if (object == null) {
                    Tools.showToast(TaskitemDetailActivity_12.this, "异常，请清理内存后重新打开页面");
                    return;
                }
                TaskitemDetailNewInfo taskitemDetailNewInfo = (TaskitemDetailNewInfo) object;
                task_id = taskitemDetailNewInfo.getId();
                outlet_batch = taskitemDetailNewInfo.getOutlet_batch();
                p_batch = taskitemDetailNewInfo.getP_batch();
                task_name = taskitemDetailNewInfo.getName();
                int size = systemDBHelper.getPhotoNumFortaskstate4(username, project_id, storeid, "", task_id);
                if (size < taskitemDetailNewInfo.getMaxTask() - taskitemDetailNewInfo.getFill_num()) {
                    Intent intent = new Intent(TaskitemDetailActivity_12.this, Camerase.class);
                    intent.putExtra("projectid", taskitemDetailNewInfo.getProjectid());
                    intent.putExtra("storeid", taskitemDetailNewInfo.getStoreid());
                    intent.putExtra("storecode", taskitemDetailNewInfo.getStoreNum());
                    intent.putExtra("taskid", task_id);
                    Tools.d(taskitemDetailNewInfo.getMaxTask() + "");
                    intent.putExtra("maxTake", taskitemDetailNewInfo.getMaxTask() - size - taskitemDetailNewInfo.getFill_num());
                    intent.putExtra("state", 4);
                    startActivityForResult(intent, TakeRequest);
                } else {
                    Tools.showToast(TaskitemDetailActivity_12.this, "拍照数量已达最大值！");
                }
            }
        });
        taskitdt_listview_left.setAdapter(taskitemDetailAdapter);
        taskitdt_listview_right.setAdapter(taskitemDetailAdapter2);
//        taskitdt_start_store.setOnClickListener(this);
//        taskitdt_stop_store.setOnClickListener(this);
        if (RecordService.isStart()) {
//            taskitdt_start_store.setVisibility(View.INVISIBLE);
//            taskitdt_stop_store.setVisibility(View.VISIBLE);
        } else {
//            taskitdt_start_store.setVisibility(View.VISIBLE);
//            taskitdt_stop_store.setVisibility(View.INVISIBLE);
        }
//        isRefresh = false;
        checkPermission();
        long sdsize = Tools.getSDFreeSize();
        if (sdsize < 51) {
            ConfirmDialog.showDialog(this, "提示", getResources().getString(R.string.sdcardsize), null, null, null, true,
                    new ConfirmDialog.OnSystemDialogClickListener() {
                        public void leftClick(Object object) {
                        }

                        public void rightClick(Object object) {
                        }
                    });
        }
        taskitemdetail_tab_left.setOnClickListener(this);
        taskitemdetail_tab_right.setOnClickListener(this);
        taskitdt_recode_switch.setOnClickListener(this);
        findViewById(R.id.taskitdt_upload_store).setOnClickListener(this);
        onClick(taskitemdetail_tab_left);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        Sign();
    }

    public void photoNumber() {//计算照片数量
        ArrayList<String> list;
        if (TextUtils.isEmpty(taskid)) {
            list = systemDBHelper.getPictureThumbnail(AppInfo.getName(this), project_id, storeid);
        } else {
            list = systemDBHelper.getPictureThumbnail(AppInfo.getName(this), project_id,
                    storeid, packageid, taskid);
        }
        if (originalImgList.size() != 0) {
            originalImgList.clear();
        }
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                String path = systemDBHelper.searchForOriginalpath(list.get(i));
                File file = new File(path);
                if (file.exists() && file.isFile()) {
                    originalImgList.add(path);
                }
            }
        }
        if (taskitdt_stroe_ablum != null) {
            taskitdt_stroe_ablum.setText("临时相册(" + list.size() + ")");
        }
    }

    private String taskid_Record = "";

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TakeRequest:
                    String path = data.getStringExtra("path");
                    String[] paths;
                    if (!TextUtils.isEmpty(path)) {
                        paths = path.split(",");
                        for (int i = 0; i < paths.length; i++) {
                            File file = new File(paths[i]);
                            if (!file.isFile()) {
                                Tools.showToast(this, "第" + (i + 1) + "张异常，已去除");
                            }
                        }
                        Collections.addAll(selectImgList, paths);
                    }
                    new zoomImageAsyncTask().executeOnExecutor(Executors.newCachedThreadPool());
                    break;
                case 0: {//录音任务
                    taskid_Record = data.getStringExtra("task_id");
                    int size = list.size();
                    for (int i = 0; i < size; i++) {
                        TaskitemDetailNewInfo taskitemDetailNewInfo = list.get(i);
                        if (taskitemDetailNewInfo == null) {
                            continue;
                        }
                        if (!TextUtils.isEmpty(taskid_Record) && (!taskitemDetailNewInfo.getIsPackage().equals("1")) && taskitemDetailNewInfo.getId().equals
                                (taskid_Record)) {//是任务
                            taskitemDetailNewInfo.setIs_Record(true);
                        } else {
                            taskitemDetailNewInfo.setIs_Record(false);
                        }
                    }
                    if (taskitemDetailAdapter != null)
                        taskitemDetailAdapter.notifyDataSetChanged();
                }
                break;
            }
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("TaskitemDetailActivity_12 Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    class zoomImageAsyncTask extends AsyncTask {
        String msg = "图片压缩失败！";

        protected void onPreExecute() {
            if (photo_compression.equals("-1")) {
                CustomProgressDialog.showProgressDialog(TaskitemDetailActivity_12.this, "图片处理中...");
            } else {
                CustomProgressDialog.showProgressDialog(TaskitemDetailActivity_12.this, "图片压缩中...");
            }
            super.onPreExecute();
        }

        private File getTempFile(String oPath) throws FileNotFoundException {
            File returnvalue = null;
            FileInputStream fis = null;
            FileOutputStream fos = null;
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            try {
                if (!isLegal(oPath)) {
                    return null;
                }
                File oldfile = new File(oPath);
                if (!oldfile.exists()) {
                    return null;
                }
                if (!oldfile.isFile()) {
                    return null;
                }
                if (!oldfile.canRead()) {
                    return null;
                }
                File f = new File(oPath + "temp");
                fis = new FileInputStream(oldfile);
                bis = new BufferedInputStream(fis);
                fos = new FileOutputStream(f);
                bos = new BufferedOutputStream(fos);
                byte[] b = new byte[1024];
                while (bis.read(b) != -1) {
                    for (int i = 0; i < b.length; i++) {
                        b[i] = (byte) (255 - b[i]);
                    }
                    bos.write(b);
                }
                bos.flush();
                if (isLegal(oPath + "temp")) {
                    returnvalue = f;
                } else {
                    returnvalue = null;
                    f.delete();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new FileNotFoundException();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                throw new OutOfMemoryError();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bis != null) {
                        bis.close();
                    }
                    if (fis != null) {
                        fis.close();
                    }
                    if (bos != null) {
                        bos.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return returnvalue;
        }

        protected Object doInBackground(Object[] params) {
            try {
                int size = selectImgList.size();
                for (int i = 0; i < size; i++) {
                    String tPath = selectImgList.get(i);
                    if ("camera_default".equals(tPath)) {
                        continue;
                    }
                    String oPath = systemDBHelper.searchForOriginalpath(tPath);
                    if (!TextUtils.isEmpty(oPath) && isLegal(oPath) && checkPicture(oPath)) {
                        if (photo_compression.equals("-1")) {
                            if (systemDBHelper.bindTaskForPicture(oPath, "", task_id)) {
                                if (!originalImgList.contains(oPath)) {
                                    originalImgList.add(oPath);
                                } else {
                                    msg = "发现重复照片，已自动去重，请重新提交";
                                    selectImgList.remove(i);
                                    i--;
                                    size--;
                                    isHadUnlegal = true;
                                }
                            } else {
                                selectImgList.remove(i);
                                i--;
                                size--;
                                isHadUnlegal = true;
                            }
                        } else {//加水印
                            File tempFile = getTempFile(oPath);//生成临时文件
                            if (tempFile == null) {
                                selectImgList.remove(i);
                                i--;
                                size--;
                                isHadUnlegal = true;
                                continue;
                            }
                            if (saveBitmap(imageZoom(Tools.getBitmap(tempFile.getPath(), 1024, 1024),
                                    Tools.StringToInt(photo_compression)), tempFile.getPath(), oPath) != null) {
                                Tools.d(oPath);
                                if (systemDBHelper.bindTaskForPicture(oPath, "", task_id)) {
                                    if (!originalImgList.contains(oPath)) {
                                        originalImgList.add(oPath);
                                    } else {
                                        msg = "发现重复照片，已自动去重，请重新提交";
                                        selectImgList.remove(i);
                                        i--;
                                        size--;
                                        isHadUnlegal = true;
                                    }
                                } else {
                                    selectImgList.remove(i);
                                    i--;
                                    size--;
                                    isHadUnlegal = true;
                                }
                            } else {
                                selectImgList.remove(i);
                                i--;
                                size--;
                                isHadUnlegal = true;
                            }
                            if (tempFile.exists()) {
                                tempFile.delete();
                            }
                        }
                    } else {
                        selectImgList.remove(i);
                        i--;
                        size--;
                        if (!TextUtils.isEmpty(oPath)) {//判定为异常图片，执行删除操作
                            new File(oPath).delete();
                            new File(tPath).delete();
                            systemDBHelper.deletePicture(oPath);
                        }
                        msg = "有图片异常，已自动删除异常图片,请重新提交";
                        isHadUnlegal = true;
                    }
                }
            } catch (OutOfMemoryError e) {
                msg = "内存不足，请清理内存或重启手机";
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        private boolean isHadUnlegal = false;

        protected void onPostExecute(Object o) {
            if (o == null || !(boolean) o || isHadUnlegal) {
                Tools.showToast(TaskitemDetailActivity_12.this, msg);
                if (originalImgList != null) {
                    for (String temp : originalImgList) {
                        systemDBHelper.deletePicture(temp);
                        File file = new File(temp);
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                    originalImgList.clear();
                }
                if (selectImgList != null) {
                    for (String temp : selectImgList) {
                        systemDBHelper.deletePictureForThum(temp);
                    }
                    selectImgList.clear();
                }
                CustomProgressDialog.Dissmiss();
            } else {
                CustomProgressDialog.Dissmiss();
//                if (taskitempgnexty_bg2_reset.getVisibility() != View.VISIBLE) {
//                    list_single.addAll(selectImgList);
//                    adapter.notifyDataSetChanged();
//                } else {
//                    for (String temp : selectImgList) {
//                        list_double.add(new String[]{temp, ""});
//                    }
            }
//                refreshUI();
            sendData();
//            }
        }
    }

    private boolean isLegal(String path) {
        File file = new File(path);
        return file.length() > 51200;
    }

    private void sendData() {
        Map<String, String> params = new HashMap<>();
        params.put("task_id", task_id);
        params.put("user_mobile", username);
        params.put("storeid", storeid);
        params.put("outlet_batch", outlet_batch);
        params.put("p_batch", p_batch);
        String imgs = "";
        int size;
        String key = "";
        size = originalImgList.size();
        for (int i = 0; i < size; i++) {
            String path = originalImgList.get(i);
            if (path.equals("camera_default")) {
                continue;
            }
            if (TextUtils.isEmpty(imgs)) {
                imgs = originalImgList.get(i);
            } else {
                imgs = imgs + "," + originalImgList.get(i);
            }
            if (TextUtils.isEmpty(key)) {
                key = "img" + (i + 1);
            } else {
                key = key + ",img" + (i + 1);
            }
        }
        updataDBHelper.addUpdataTask(username, project_id, projectname, store_num, brand,
                storeid, store_name, "",
                "", "1", task_id, task_name, "", "", "",
                username + project_id + storeid + task_id + Tools.getTimeSS() + "bp",
                Urls.Filecomplete,
                key, imgs, UpdataDBHelper.Updata_file_type_img, params, photo_compression,
                true, Urls.Taskphotoup, paramsToString(), false);
        selectImgList.clear();
        originalImgList.clear();
        Intent service = new Intent("com.orange.oy.UpdataNewService");
        service.setPackage("com.orange.oy");
        startService(service);
        refreshDataRight();
    }

    private String paramsToString() {
        Map<String, String> parames = new HashMap<>();
        parames.put("task_id", task_id);
        parames.put("user_mobile", AppInfo.getName(this));
        parames.put("storeid", storeid);
        parames.put("token", Tools.getToken());
        parames.put("outlet_batch", outlet_batch);
        parames.put("p_batch", p_batch);
        parames.put("is_fill", "1");
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

    private Bitmap imageZoom(Bitmap bitMap, double maxSize) throws OutOfMemoryError {
        boolean isOutOfMemoryError = false;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            double mid = b.length / 1024;
            if (mid > maxSize) {
                double i = mid / maxSize;
                bitMap = zoomImage(bitMap, bitMap.getWidth() / Math.sqrt(i), bitMap.getHeight() / Math.sqrt(i));
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            isOutOfMemoryError = true;
            throw new OutOfMemoryError();
        } finally {
            if (isOutOfMemoryError && bitMap != null && !bitMap.isRecycled()) {
                bitMap.recycle();
            }
        }
        return bitMap;
    }

    private Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
        if (newBitmap != null) {
            bgimage.recycle();
        }
        return newBitmap;
    }

    private File saveBitmap(Bitmap bm, String tempPath, String oPath) throws FileNotFoundException,
            OutOfMemoryError {
        File returnvalue = null;
        FileOutputStream out = null;
        try {
            File f = new File(tempPath);
            out = new FileOutputStream(f);
            ExifInterface exifInterface = new ExifInterface(oPath);
            bm = addWatermark(bm, systemDBHelper.searchForWatermark(oPath));
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            ExifInterface exifInterface1 = new ExifInterface(f.getPath());
            exifInterface1.setAttribute(ExifInterface.TAG_ORIENTATION, exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION));
            exifInterface1.saveAttributes();
            if (isLegal(f.getPath())) {
                if (encryptPicture(tempPath, oPath)) {
                    systemDBHelper.updataIswater(oPath);
                    returnvalue = new File(oPath);
                    f.delete();
                } else {
                    f.delete();
                    returnvalue = null;
                }
            } else {
                f.delete();
                returnvalue = null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new FileNotFoundException();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            throw new OutOfMemoryError();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (bm != null) {
                    bm.recycle();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return returnvalue;
    }

    private boolean encryptPicture(String oldPath, String newPath) {
        boolean returnvalue = false;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            if (!isLegal(oldPath)) {
                return false;
            }
            File oldfile = new File(oldPath);
            if (!oldfile.exists()) {
                return false;
            }
            if (!oldfile.isFile()) {
                return false;
            }
            if (!oldfile.canRead()) {
                return false;
            }
            File f = new File(newPath);
            fis = new FileInputStream(oldfile);
            bis = new BufferedInputStream(fis);
            fos = new FileOutputStream(f);
            bos = new BufferedOutputStream(fos);
            byte[] b = new byte[1024];
            while (bis.read(b) != -1) {
                for (int i = 0; i < b.length; i++) {
                    b[i] = (byte) (255 - b[i]);
                }
                bos.write(b);
            }
            bos.flush();
            if (isLegal(f.getPath())) {
                returnvalue = true;
            } else {
                if (f.exists())
                    f.delete();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (fis != null) {
                    fis.close();
                }
                if (bos != null) {
                    bos.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return returnvalue;
    }

    private Bitmap addWatermark(Bitmap bitmap, String msg) {
        Bitmap newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        bitmap.recycle();
        int width = newBitmap.getWidth();
        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint();
        paint.setAlpha(100);
        paint.setColor(Color.RED);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setTextSize(AppInfo.PaintSize);
        int xNum = width / AppInfo.PaintSize;
        String[] msgs = msg.split("\n");
        int xN = 1;
        for (String str : msgs) {
            if (paint.measureText(str) <= width) {
                canvas.drawText(str, 0, AppInfo.PaintSize * xN++, paint);
            } else {
                int yNum = (int) Math.ceil(str.length() * 1d / xNum);
                int yb = 0;
                for (int i = 1; i <= yNum; i++) {
                    int temp = yb + xNum;
                    if (temp > str.length()) {
                        temp = str.length();
                    }
                    canvas.drawText(str, yb, temp, 0, AppInfo.PaintSize * xN++, paint);
                    yb = temp;
                }
            }
        }
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();//存储
        return newBitmap;
    }

    private boolean checkPicture(String path) {
        FileInputStream is = null;
        String value = null;
        try {
            is = new FileInputStream(path);
            byte[] b = new byte[4];
            is.read(b, 0, b.length);
            for (int i = 0; i < b.length; i++) {
                b[i] = (byte) (255 - b[i]);
            }
            value = bytesToHexString(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return value != null && (value.equals("FFD8FFE1") || value.equals("89504E47"));
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (byte aSrc : src) {
            hv = Integer.toHexString(aSrc & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        return builder.toString();
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

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        switch (requestCode) {
            case AppInfo.REQUEST_CODE_ASK_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                } else {
                    Tools.showToast(TaskitemDetailActivity_12.this, "拍照权限获取失败");
                    baseFinish();
                }
                break;
            case AppInfo.REQUEST_CODE_ASK_RECORD_AUDIO:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                } else {
                    Tools.showToast(TaskitemDetailActivity_12.this, "录音权限获取失败");
                    baseFinish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public static boolean isRefresh;
    public static String taskid;
    public static String packageid;

    protected void onResume() {
        if (!(index != null && ("0".equals(index) || "2".equals(index)))) {
            photoNumber();
        }
        if (isRefresh) {
            isRefresh = false;
            if (list == null) {
                return;
            }
            int size = list.size();
            boolean r = false;
            for (int i = 0; i < size; i++) {
                TaskitemDetailNewInfo taskitemDetailNewInfo = list.get(i);
                if (taskitemDetailNewInfo == null) {
                    continue;
                }
                if (taskid != null && (!taskitemDetailNewInfo.getIsPackage().equals("1")) && taskitemDetailNewInfo.getId().equals
                        (taskid)) {//是任务

                    list.remove(i);
                    size--;
                    i--;
                    taskitemDetailAdapter.notifyDataSetChanged();
                    taskid = null;
                    break;
                } else if (packageid != null && taskitemDetailNewInfo.getIsPackage().equals("1") && packageid.equals
                        (taskitemDetailNewInfo.getId())) {
                    list.remove(i);
                    size--;
                    i--;
                    taskitemDetailAdapter.notifyDataSetChanged();
                    packageid = null;
                    break;
                } else {
                    r = true;
                    break;
                }
            }
            if (r) {
                refreshDataRight();
                refreshDataLeft();
            } else {
                if (list.isEmpty()) {
                    onClick(taskitemdetail_tab_right);
                }
            }
        }
        super.onResume();
    }

    private OnRefreshListener onRefreshListener = new OnRefreshListener() {
        public void refresh(String packageid) {
            isRefresh = false;
            if (list == null) return;
            int size = list.size();
            for (int i = 0; i < size; i++) {
                TaskitemDetailNewInfo taskitemDetailNewInfo = list.get(i);
                if (taskitemDetailNewInfo == null) {
                    continue;
                }
                if (taskitemDetailNewInfo.getIsPackage().equals("1") && packageid.equals(taskitemDetailNewInfo.getId())) {//是任务包
                    list.remove(i);
                    taskitemDetailAdapter.notifyDataSetChanged();
                    break;
                }
            }
//            TaskitemDetailActivity.this.refresh();
        }
    };

    private void refreshDataLeft() {
        if (list != null) {
            list.clear();
            taskitemDetailAdapter.notifyDataSetChanged();
        }
        page = 1;
        getData();
    }

    private boolean isRecord = false;

    private void getData() {
        Taskindex.sendPostRequest(dataUrl, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                searchStr = "";
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    String redo = jsonObject.optString("is_redo");
                    String outletId = jsonObject.getString("outletId");
                    if ("0".equals(index)) {
                        storeid = outletId;
                    }
                    if (!TextUtils.isEmpty(redo)) {
                        is_redo = redo;
                    }
                    String complete = jsonObject.optString("is_complete");
                    if (!TextUtils.isEmpty(complete)) {
                        is_complete = complete;
                    }
                    isRecord = jsonObject.getString("record").equals("1");
                    if (isRecord) {
                        if (index != null && ("0".equals(index) || "2".equals(index))) {
                            taskitdt_recode_layout.setVisibility(View.GONE);
                        } else {
                            taskitdt_recode_layout.setVisibility(View.VISIBLE);
                        }
                    } else {
                        taskitdt_recode_layout.setVisibility(View.GONE);
                    }
                    if (photo_compression == null || "null".equals(photo_compression) || TextUtils.isEmpty(photo_compression)) {
                        photo_compression = jsonObject.getString("photo_compression");
                        if (photo_compression.equals("1")) {
                            photo_compression = "300";
                        } else if (photo_compression.equals("2")) {
                            photo_compression = "500";
                        } else if (photo_compression.equals("3")) {
                            photo_compression = "1024";
                        } else if (photo_compression.equals("4")) {
                            photo_compression = "-1";
                        } else {
                            photo_compression = "500";
                        }
                    }
                    if (code == 200) {
                        JSONArray jsonArray = jsonObject.optJSONArray("datas");
                        if (jsonArray != null) {
                            if (list == null) {
                                list = new ArrayList<>();
                                page = 1;
                                taskitemDetailAdapter.resetList(list);
                            } else {
                                if (page == 1) {
                                    list.clear();
                                }
                            }
                            int length = jsonArray.length();
                            for (int i = 0; i < length; i++) {
                                jsonObject = jsonArray.getJSONObject(i);
                                TaskitemDetailNewInfo taskitemDetailNewInfo = new TaskitemDetailNewInfo();
                                taskitemDetailNewInfo.setIsPackage(jsonObject.getString("is_package"));
                                taskitemDetailNewInfo.setOutlet_batch(jsonObject.getString("outlet_batch"));
                                taskitemDetailNewInfo.setP_batch(jsonObject.getString("p_batch"));
                                taskitemDetailNewInfo.setStoreid(storeid);
                                taskitemDetailNewInfo.setStoreNum(store_num);
                                taskitemDetailNewInfo.setStorename(store_name);
                                taskitemDetailNewInfo.setProjectid(project_id);
                                taskitemDetailNewInfo.setProjectname(projectname);
                                taskitemDetailNewInfo.setCode(codeStr);
                                taskitemDetailNewInfo.setBrand(brand);
                                taskitemDetailNewInfo.setPhoto_compression(photo_compression);
                                if (taskitemDetailNewInfo.getIsPackage().equals("1")) {//任务包
                                    taskitemDetailNewInfo.setName(jsonObject.getString("p_name"));
                                    taskitemDetailNewInfo.setId(jsonObject.getString("p_id"));
                                    taskitemDetailNewInfo.setIsClose(jsonObject.getString("is_close"));
                                    taskitemDetailNewInfo.setIs_invalid(jsonObject.getString("is_invalid"));
                                    taskitemDetailNewInfo.setCloseInvalidtype(jsonObject.getString("invalid_type"));
                                    taskitemDetailNewInfo.setCloseTaskname(jsonObject.getString("task_name"));
                                    taskitemDetailNewInfo.setCloseTaskid(jsonObject.getString("task_id"));
                                    taskitemDetailNewInfo.setCloseTasktype(jsonObject.getString("task_type"));
                                    String category = jsonObject.getString("category1_name");
                                    if (TextUtils.isEmpty(category) || category.equals("null")) {
                                        taskitemDetailNewInfo.setIsCategory(false);
                                    } else {
                                        taskitemDetailNewInfo.setIsCategory(true);
                                    }
                                } else {//任务
                                    taskitemDetailNewInfo.setName(jsonObject.getString("task_name"));
                                    taskitemDetailNewInfo.setId(jsonObject.getString("task_id"));
                                    taskitemDetailNewInfo.setTask_type(jsonObject.getString("task_type"));
                                    taskitemDetailNewInfo.setMaxTask(Tools.StringToInt(jsonObject.getString("max_num")));
                                    if (taskitemDetailNewInfo.getId().equals(taskid_Record)) {
                                        taskitemDetailNewInfo.setIs_Record(true);
                                    }
                                }
                                list.add(taskitemDetailNewInfo);
                            }
                            taskitdt_listview_left.onRefreshComplete();
                            if (length < 15) {
                                taskitdt_listview_left.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            } else {
                                taskitdt_listview_left.setMode(PullToRefreshBase.Mode.BOTH);
                            }
                            if (list.isEmpty()) {
                                is_complete = "1";
                            }
                            taskitemDetailAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Tools.showToast(TaskitemDetailActivity_12.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskitemDetailActivity_12.this, getResources().getString(R.string.network_error));
                }
                taskitdt_listview_left.onRefreshComplete();
                if ("1".equals(is_complete)) {
                    onClick(taskitemdetail_tab_right);
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                taskitdt_listview_left.onRefreshComplete();
                Tools.showToast(TaskitemDetailActivity_12.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    private String sign = "";

    private void Sign() {
        Sign.sendPostRequest(Urls.Sign, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        sign = jsonObject.getString("msg");
                    } else {
                        Tools.showToast(TaskitemDetailActivity_12.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(TaskitemDetailActivity_12.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private void refreshDataRight() {
        if (list_right != null) {
            list_right.clear();
            taskitemDetailAdapter2.notifyDataSetChanged();
        }
        pageRight = 1;
        getDataRight();
    }

    private String is_complete = "0";//是否可以点击整店上传，1：可以；0：不可以
    private String is_redo = "0";

    private void getDataRight() {
        isRightRefreshing = true;
        Taskindexcomplete.sendPostRequest(Urls.Taskindexcomplete, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                searchStr = "";
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    String redo = jsonObject.optString("is_redo");
                    if (!TextUtils.isEmpty(redo)) {
                        is_redo = redo;
                    }
                    String complete = jsonObject.optString("is_complete");
                    if (!TextUtils.isEmpty(complete)) {
                        is_complete = complete;
                    }
                    if (code == 200) {
                        JSONArray jsonArray = jsonObject.optJSONArray("datas");
                        if (jsonArray != null) {
                            if (list_right == null) {
                                list_right = new ArrayList<>();
                                pageRight = 1;
                                taskitemDetailAdapter2.resetList(list_right);
                            } else {
                                if (pageRight == 1) {
                                    list_right.clear();
                                }
                            }
                            int length = jsonArray.length();
                            for (int i = 0; i < length; i++) {
                                jsonObject = jsonArray.getJSONObject(i);
                                TaskitemDetailNewInfo taskitemDetailNewInfo = new TaskitemDetailNewInfo();
                                taskitemDetailNewInfo.setIsPackage(jsonObject.getString("is_package"));
                                taskitemDetailNewInfo.setOutlet_batch(jsonObject.getString("outlet_batch"));
                                taskitemDetailNewInfo.setP_batch(jsonObject.getString("p_batch"));
                                taskitemDetailNewInfo.setStoreid(storeid);
                                taskitemDetailNewInfo.setStoreNum(store_num);
                                taskitemDetailNewInfo.setStorename(store_name);
                                taskitemDetailNewInfo.setProjectid(project_id);
                                taskitemDetailNewInfo.setProjectname(projectname);
                                taskitemDetailNewInfo.setCode(codeStr);
                                taskitemDetailNewInfo.setBrand(brand);
                                taskitemDetailNewInfo.setPhoto_compression(photo_compression);
                                if (taskitemDetailNewInfo.getIsPackage().equals("1")) {//任务包
                                    taskitemDetailNewInfo.setName(jsonObject.getString("p_name"));
                                    taskitemDetailNewInfo.setId(jsonObject.getString("p_id"));
                                    taskitemDetailNewInfo.setIsClose(jsonObject.getString("is_close"));
                                    taskitemDetailNewInfo.setIs_invalid(jsonObject.getString("is_invalid"));
                                    taskitemDetailNewInfo.setCloseInvalidtype(jsonObject.getString("invalid_type"));
                                    taskitemDetailNewInfo.setCloseTaskname(jsonObject.getString("task_name"));
                                    taskitemDetailNewInfo.setCloseTaskid(jsonObject.getString("task_id"));
                                    taskitemDetailNewInfo.setCloseTasktype(jsonObject.getString("task_type"));
                                    String category = jsonObject.getString("category1_name");
                                    if (TextUtils.isEmpty(category) || category.equals("null")) {
                                        taskitemDetailNewInfo.setIsCategory(false);
                                    } else {
                                        taskitemDetailNewInfo.setIsCategory(true);
                                    }
                                } else {//任务
                                    taskitemDetailNewInfo.setName(jsonObject.getString("task_name"));
                                    taskitemDetailNewInfo.setId(jsonObject.getString("task_id"));
                                    taskitemDetailNewInfo.setTask_type(jsonObject.getString("task_type"));
                                    taskitemDetailNewInfo.setMaxTask(Tools.StringToInt(jsonObject.getString("max_num")));
                                    taskitemDetailNewInfo.setFill_num(Tools.StringToInt(jsonObject.getString("fill_num")));
                                    taskitemDetailNewInfo.setIs_close(jsonObject.getString("is_close"));
                                    taskitemDetailNewInfo.setState(jsonObject.getString("state"));
                                }
                                list_right.add(taskitemDetailNewInfo);
                            }
                            taskitdt_listview_right.onRefreshComplete();
                            if (length < 15) {
                                taskitdt_listview_right.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            } else {
                                taskitdt_listview_right.setMode(PullToRefreshBase.Mode.BOTH);
                            }
                            taskitemDetailAdapter2.notifyDataSetChanged();
                        }
                    } else {
                        Tools.showToast(TaskitemDetailActivity_12.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskitemDetailActivity_12.this, getResources().getString(R.string.network_error));
                }
                isRightRefreshing = false;
                taskitdt_listview_right.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                isRightRefreshing = false;
                taskitdt_listview_right.onRefreshComplete();
                Tools.showToast(TaskitemDetailActivity_12.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    private ArrayList<String> storeList;

    private void Changestore() {
        Changestore.sendPostRequest(Urls.Changestore, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        JSONArray jsonArray = jsonObject.getJSONArray("datas");
                        if (storeList == null) {
                            storeList = new ArrayList<>();
                        } else {
                            storeList.clear();
                        }
                        int length = jsonArray.length();
                        ArrayList<Map<String, String>> list = new ArrayList<>();
                        for (int i = 0; i < length; i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            Map<String, String> map = new HashMap<>();
                            map.put(jsonObject.getString("store_code"), jsonObject.getString("si_name"));
                            list.add(map);
                            storeList.add(jsonObject.getString("id"));
                        }
                        if (!"3".equals(type)) {
                            ChangeshopDialog.showDialog(TaskitemDetailActivity_12.this, list, onItemClickListener);
                        } else {
                            Intent intent = new Intent(TaskitemDetailActivity_12.this, BrightPersonInfoActivity.class);
                            intent.putExtra("store_num", store_num);
                            intent.putExtra("city", addr);
                            intent.putExtra("outletid", storeid);
                            intent.putExtra("project_id", project_id);
                            intent.putExtra("projectname", projectname);
                            intent.putExtra("code", code);
                            intent.putExtra("brand", brand);
                            intent.putExtra("store_name", store_name);
                            startActivity(intent);
                        }
                    } else {
                        String msg = jsonObject.getString("msg");
                        if (TextUtils.isEmpty(msg) || "null".equals(msg)) {
                            Tools.showToast(TaskitemDetailActivity_12.this, "无店铺");
                        } else {
                            Tools.showToast(TaskitemDetailActivity_12.this, jsonObject.getString("msg"));
                        }
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskitemDetailActivity_12.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskitemDetailActivity_12.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    private OfflineDBHelper offlineDBHelper;

    private void Redo() {
        Redo.sendPostRequest(Urls.Redo, new Response.Listener<String>() {
                    public void onResponse(String s) {
                        Tools.d(s);
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            int code = jsonObject.getInt("code");
                            if (code == 200) {
                                try {
                                    if (RecordService.isStart()) {
                                        Intent service = new Intent(TaskitemDetailActivity_12.this, RecordService.class);
                                        stopService(service);
                                    }
                                } catch (Exception e) {
                                }
                                String username = AppInfo.getName(TaskitemDetailActivity_12.this);
                                systemDBHelper.updataAllstateTo3(username, project_id, storeid);
                                offlineDBHelper.deleteOfflineForRedo(username, project_id, storeid);
                                updataDBHelper.deleteStoreTask(username, project_id, storeid);
                                offlineDBHelper.deleteTraffic(username, project_id, storeid);
                                list_right.clear();
                                taskitemDetailAdapter2.notifyDataSetChanged();
                                refreshDataRight();
                                refreshDataLeft();
                                Tools.showToast(TaskitemDetailActivity_12.this, jsonObject.getString("msg"));
                            } else {
                                Tools.showToast(TaskitemDetailActivity_12.this, jsonObject.getString("msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Tools.showToast(TaskitemDetailActivity_12.this, getResources().getString(R.string
                                    .network_error));
                        }
                        CustomProgressDialog.Dissmiss();
                    }
                }, new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError volleyError) {
                        CustomProgressDialog.Dissmiss();
                        Tools.showToast(TaskitemDetailActivity_12.this, getResources().getString(R.string
                                .network_volleyerror));
                    }
                }
        );
    }

    private void sendStartUpload() {
        Startupload.sendPostRequest(Urls.Startupload, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        refreshDataLeft();
                        Intent service = new Intent("com.orange.oy.UpdataNewService");
                        service.setPackage("com.orange.oy");
                        startService(service);
                        String msg = jsonObject.getString("msg");
                        if ("1".equals(msg)) {
//                            ConfirmDialog.showDialog(TaskitemDetailActivity_12.this, null, "新手项目已执行完毕，可申请推荐项目。您可以在【我的】页面查看【任务进度】", null,
//                                    "确定", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
//                                        @Override
//                                        public void leftClick(Object object) {
//
//                                        }
//
//                                        @Override
//                                        public void rightClick(Object object) {
//                                            baseFinish();
//                                        }
//                                    }).goneLeft();
                        } else {
                            ArrayList<UpdataInfo> list = updataDBHelper.getTask();
                            if (!list.isEmpty()) {
                                ConfirmDialog.showDialog(TaskitemDetailActivity_12.this, "提示!", "亲，您的任务资料尚未上传完成，请确保资料上传完成，以免影响到您下个任务的执行。", null, "我知道了", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                    @Override
                                    public void leftClick(Object object) {

                                    }

                                    @Override
                                    public void rightClick(Object object) {
                                        //刷新
                                        TeamMemberTodoActivity.isfreshing = true;
                                        baseFinish();


                                    }
                                }).goneLeft();
                            } else {
                                TeamMemberTodoActivity.isfreshing = true;
                                baseFinish();

                            }
                        }


                    } else {
                        Tools.showToast(TaskitemDetailActivity_12.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Tools.showToast(TaskitemDetailActivity_12.this, getResources().getString(R.string.network_error));
                } finally {
                    CustomProgressDialog.Dissmiss();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskitemDetailActivity_12.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private NewOnItemClickListener newOnItemClickListener = new NewOnItemClickListener() {
        public void onItemClick(Object object) {
            if (!("0".equals(index) || "2".equals(index))) {
                if (!RecordService.isStart() && taskitdt_recode_layout.getVisibility() == View.VISIBLE) {
                    ConfirmDialog.showDialog(TaskitemDetailActivity_12.this, "提示！", 2, "执行任务前请点击任务执行后面的“开始”按钮开启全程录音", null, "知道了"
                            , null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                @Override
                                public void leftClick(Object object) {

                                }

                                @Override
                                public void rightClick(Object object) {
                                }
                            }).goneLeft();
                    return;
                }
            }
            String index2;
            if ("2".equals(index)) {
                index2 = "0";
            } else {
                index2 = index;
            }
            TaskitemDetailNewInfo taskitemDetailNewInfo = (TaskitemDetailNewInfo) object;
            if (taskitemDetailNewInfo.getIsPackage().equals("1")) {
                if (taskitemDetailNewInfo.getIsClose().equals("1")) {
                    Intent intent = new Intent(TaskitemDetailActivity_12.this, TaskitemListActivity_12.class);
                    intent.putExtra("project_id", project_id);
                    intent.putExtra("project_name", projectname);
                    intent.putExtra("task_pack_id", taskitemDetailNewInfo.getId());
                    intent.putExtra("pack_name", taskitemDetailNewInfo.getName());
                    intent.putExtra("store_id", taskitemDetailNewInfo.getStoreid());
                    intent.putExtra("store_num", store_num);
                    intent.putExtra("store_name", taskitemDetailNewInfo.getStorename());
                    intent.putExtra("isCategory", taskitemDetailNewInfo.isCategory());
                    intent.putExtra("photo_compression", taskitemDetailNewInfo.getPhoto_compression());
                    intent.putExtra("is_desc", is_desc);
                    intent.putExtra("is_watermark", is_watermark);
                    intent.putExtra("code", codeStr);
                    intent.putExtra("brand", brand);
                    intent.putExtra("outlet_batch", taskitemDetailNewInfo.getOutlet_batch());
                    intent.putExtra("p_batch", taskitemDetailNewInfo.getP_batch());
                    intent.putExtra("index", index2);
                    intent.putExtra("project_type", project_type);
                    intent.putExtra("taskid_Record", taskid_Record);
                    startActivityForResult(intent, 0);
                }
            } else {
                if (taskitemDetailNewInfo.getTask_type().equals("1") || taskitemDetailNewInfo.getTask_type().equals("8")) {
                    Intent intent = new Intent(TaskitemDetailActivity_12.this, TaskitemPhotographyNextYActivity.class);
                    intent.putExtra("project_id", project_id);
                    intent.putExtra("project_name", projectname);
                    intent.putExtra("task_pack_id", "");
                    intent.putExtra("task_pack_name", "");
                    intent.putExtra("task_id", taskitemDetailNewInfo.getId());
                    intent.putExtra("task_type", taskitemDetailNewInfo.getTask_type());
                    intent.putExtra("task_name", taskitemDetailNewInfo.getName());
                    intent.putExtra("store_id", taskitemDetailNewInfo.getStoreid());
                    intent.putExtra("store_num", store_num);
                    intent.putExtra("store_name", taskitemDetailNewInfo.getStorename());
                    intent.putExtra("photo_compression", taskitemDetailNewInfo.getPhoto_compression());
                    intent.putExtra("is_watermark", is_watermark);
                    intent.putExtra("category1", "");
                    intent.putExtra("category2", "");
                    intent.putExtra("category3", "");
                    intent.putExtra("is_desc", is_desc);
                    intent.putExtra("code", codeStr);
                    intent.putExtra("brand", brand);
                    intent.putExtra("outlet_batch", taskitemDetailNewInfo.getOutlet_batch());
                    intent.putExtra("p_batch", taskitemDetailNewInfo.getP_batch());
                    intent.putExtra("newtask", "0");//判断是否是新手任务 1是0否
                    intent.putExtra("index", index2);
                    startActivity(intent);
                } else if (taskitemDetailNewInfo.getTask_type().equals("2")) {
                    Intent intent = new Intent(TaskitemDetailActivity_12.this, TaskitemShotActivity.class);
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
                    intent.putExtra("is_desc", is_desc);
                    intent.putExtra("code", codeStr);
                    intent.putExtra("brand", brand);
                    intent.putExtra("outlet_batch", taskitemDetailNewInfo.getOutlet_batch());
                    intent.putExtra("p_batch", taskitemDetailNewInfo.getP_batch());
                    intent.putExtra("newtask", "0");//判断是否是新手任务 1是0否
                    intent.putExtra("index", index2);
                    startActivity(intent);
                } else if (taskitemDetailNewInfo.getTask_type().equals("3")) {
                    Intent intent = new Intent(TaskitemDetailActivity_12.this, TaskitemEditActivity.class);
                    intent.putExtra("project_id", project_id);
                    intent.putExtra("project_name", projectname);
                    intent.putExtra("task_pack_id", "");
                    intent.putExtra("task_pack_name", "");
                    intent.putExtra("task_id", taskitemDetailNewInfo.getId());
                    intent.putExtra("task_name", taskitemDetailNewInfo.getName());
                    intent.putExtra("tasktype", "3");
                    intent.putExtra("store_id", taskitemDetailNewInfo.getStoreid());
                    intent.putExtra("store_num", store_num);
                    intent.putExtra("store_name", taskitemDetailNewInfo.getStorename());
                    intent.putExtra("category1", "");
                    intent.putExtra("category2", "");
                    intent.putExtra("category3", "");
                    intent.putExtra("is_desc", is_desc);
                    intent.putExtra("outlet_batch", taskitemDetailNewInfo.getOutlet_batch());
                    intent.putExtra("p_batch", taskitemDetailNewInfo.getP_batch());
                    intent.putExtra("newtask", "0");//判断是否是新手任务 1是0否
                    intent.putExtra("index", index2);
                    startActivity(intent);
                } else if (taskitemDetailNewInfo.getTask_type().equals("4")) {
                    Intent intent = new Intent(TaskitemDetailActivity_12.this, TaskitemMapActivity.class);
                    intent.putExtra("project_id", project_id);
                    intent.putExtra("project_name", projectname);
                    intent.putExtra("task_pack_id", "");
                    intent.putExtra("task_pack_name", "");
                    intent.putExtra("task_id", taskitemDetailNewInfo.getId());
                    intent.putExtra("task_name", taskitemDetailNewInfo.getName());
                    intent.putExtra("store_id", taskitemDetailNewInfo.getStoreid());
                    intent.putExtra("store_num", store_num);
                    intent.putExtra("store_name", taskitemDetailNewInfo.getStorename());
                    intent.putExtra("project_type", project_type);
                    intent.putExtra("category1", "");
                    intent.putExtra("category2", "");
                    intent.putExtra("category3", "");
                    intent.putExtra("is_desc", is_desc);
                    intent.putExtra("code", codeStr);
                    intent.putExtra("brand", brand);
                    intent.putExtra("outlet_batch", taskitemDetailNewInfo.getOutlet_batch());
                    intent.putExtra("p_batch", taskitemDetailNewInfo.getP_batch());
                    intent.putExtra("newtask", "0");//判断是否是新手任务 1是0否
                    intent.putExtra("index", index2);
                    startActivity(intent);
                } else if (taskitemDetailNewInfo.getTask_type().equals("5")) {
                    if (!TextUtils.isEmpty(taskid_Record) && RecordService.isStart() && !taskitemDetailNewInfo.getId().equals(taskid_Record)) {
                        ConfirmDialog.showDialog(TaskitemDetailActivity_12.this, "提示！", 2, "您的录音任务还没有结束，请先提交后开始下一个任务~", null, "我知道了"
                                , null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                    @Override
                                    public void leftClick(Object object) {

                                    }

                                    @Override
                                    public void rightClick(Object object) {
                                    }
                                }).goneLeft();
                        return;
                    }
                    Intent intent = new Intent(TaskitemDetailActivity_12.this, TaskitemRecodillustrateActivity.class);
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
                    intent.putExtra("is_desc", is_desc);
                    intent.putExtra("code", codeStr);
                    intent.putExtra("brand", brand);
                    intent.putExtra("outlet_batch", taskitemDetailNewInfo.getOutlet_batch());
                    intent.putExtra("p_batch", taskitemDetailNewInfo.getP_batch());
                    intent.putExtra("newtask", "0");//判断是否是新手任务 1是0否
                    intent.putExtra("index", index2);
                    startActivityForResult(intent, 0);
                } else if (taskitemDetailNewInfo.getTask_type().equals("6")) {
                    Intent intent = new Intent(TaskitemDetailActivity_12.this, ScanTaskNewActivity.class);
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
                    intent.putExtra("is_desc", is_desc);
                    intent.putExtra("code", codeStr);
                    intent.putExtra("brand", brand);
                    intent.putExtra("outlet_batch", taskitemDetailNewInfo.getOutlet_batch());
                    intent.putExtra("p_batch", taskitemDetailNewInfo.getP_batch());
                    intent.putExtra("newtask", "0");//判断是否是新手任务 1是0否
                    intent.putExtra("index", index2);
                    startActivity(intent);
                } else if (taskitemDetailNewInfo.getTask_type().equals("7")) {//电话任务
                    Intent intent = new Intent(TaskitemDetailActivity_12.this, CallTaskActivity.class);
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
                    intent.putExtra("is_desc", is_desc);
                    intent.putExtra("code", codeStr);
                    intent.putExtra("brand", brand);
                    intent.putExtra("outlet_batch", taskitemDetailNewInfo.getOutlet_batch());
                    intent.putExtra("p_batch", taskitemDetailNewInfo.getP_batch());
                    intent.putExtra("newtask", "0");//判断是否是新手任务 1是0否
                    intent.putExtra("index", index2);
                    startActivity(intent);
                } else if (taskitemDetailNewInfo.getTask_type().equals("9")) {//体验任务
                    Intent intent = new Intent(TaskitemDetailActivity_12.this, TaskExperienceActivity.class);
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
                    intent.putExtra("is_desc", is_desc);
                    intent.putExtra("code", codeStr);
                    intent.putExtra("brand", brand);
                    intent.putExtra("outlet_batch", taskitemDetailNewInfo.getOutlet_batch());
                    intent.putExtra("p_batch", taskitemDetailNewInfo.getP_batch());
                    intent.putExtra("newtask", "0");//判断是否是新手任务 1是0否
                    intent.putExtra("index", index2);
                    startActivity(intent);
                }
            }
        }
    };

    private NewOnItemClickListener newOnItemClickListener2 = new NewOnItemClickListener() {
        public void onItemClick(Object object) {
            if (!RecordService.isStart() && taskitdt_recode_layout.getVisibility() == View.VISIBLE) {
                ConfirmDialog.showDialog(TaskitemDetailActivity_12.this, "提示！", 2, "执行任务前请点击任务执行后面的“开始”按钮开启全程录音", null, "知道了"
                        , null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                            @Override
                            public void leftClick(Object object) {

                            }

                            @Override
                            public void rightClick(Object object) {
                            }
                        }).goneLeft();
                return;
            }
            final TaskitemDetailNewInfo taskitemDetailNewInfo = (TaskitemDetailNewInfo) object;
            task_id = taskitemDetailNewInfo.getId();
            outlet_batch = taskitemDetailNewInfo.getOutlet_batch();
            p_batch = taskitemDetailNewInfo.getP_batch();
            if (taskitemDetailNewInfo.getIsPackage().equals("1")) {
                if (taskitemDetailNewInfo.getIsClose().equals("1")) {
                    Intent intent = new Intent(TaskitemDetailActivity_12.this, TaskitemListexecutedActivity_12.class);
                    intent.putExtra("project_id", project_id);
                    intent.putExtra("project_name", projectname);
                    intent.putExtra("task_pack_id", taskitemDetailNewInfo.getId());
                    intent.putExtra("pack_name", taskitemDetailNewInfo.getName());
                    intent.putExtra("store_id", taskitemDetailNewInfo.getStoreid());
                    intent.putExtra("store_num", store_num);
                    intent.putExtra("store_name", taskitemDetailNewInfo.getStorename());
                    intent.putExtra("isCategory", taskitemDetailNewInfo.isCategory());
                    intent.putExtra("photo_compression", taskitemDetailNewInfo.getPhoto_compression());
                    intent.putExtra("is_desc", is_desc);
                    intent.putExtra("is_watermark", is_watermark);
                    intent.putExtra("code", codeStr);
                    intent.putExtra("brand", brand);
                    intent.putExtra("outlet_batch", taskitemDetailNewInfo.getOutlet_batch());
                    intent.putExtra("p_batch", taskitemDetailNewInfo.getP_batch());
                    intent.putExtra("fill_num", taskitemDetailNewInfo.getFill_num());
                    intent.putExtra("max_num", taskitemDetailNewInfo.getMaxTask());
                    intent.putExtra("is_close", taskitemDetailNewInfo.getIs_close());
                    startActivity(intent);
                }
            } else {
                if (!"2".equals(taskitemDetailNewInfo.getState())) {
                    Tools.showToast(TaskitemDetailActivity_12.this, "亲，要等资料传完才能看的哦～");
                    return;
                }
                if (taskitemDetailNewInfo.getTask_type().equals("1") || taskitemDetailNewInfo.getTask_type().equals("8")) {
                    Intent intent = new Intent();
                    intent.putExtra("project_id", project_id);
                    intent.putExtra("project_name", projectname);
                    intent.putExtra("task_pack_id", "");
                    intent.putExtra("task_pack_name", "");
                    intent.putExtra("task_id", taskitemDetailNewInfo.getId());
                    intent.putExtra("task_name", taskitemDetailNewInfo.getName());
                    intent.putExtra("task_type", taskitemDetailNewInfo.getTask_type());
                    intent.putExtra("store_id", taskitemDetailNewInfo.getStoreid());
                    intent.putExtra("store_num", store_num);
                    intent.putExtra("store_name", taskitemDetailNewInfo.getStorename());
                    intent.putExtra("photo_compression", taskitemDetailNewInfo.getPhoto_compression());
                    intent.putExtra("is_watermark", is_watermark);
                    intent.putExtra("category1", "");
                    intent.putExtra("category2", "");
                    intent.putExtra("category3", "");
                    intent.putExtra("is_desc", is_desc);
                    intent.putExtra("code", codeStr);
                    intent.putExtra("brand", brand);
                    intent.putExtra("outlet_batch", taskitemDetailNewInfo.getOutlet_batch());
                    intent.putExtra("p_batch", taskitemDetailNewInfo.getP_batch());
                    intent.putExtra("fill_num", taskitemDetailNewInfo.getFill_num());
                    intent.putExtra("max_num", taskitemDetailNewInfo.getMaxTask());
                    if ("1".equals(taskitemDetailNewInfo.getIs_close())) {//无效
                        intent.setClass(TaskitemDetailActivity_12.this, TaskitemPhotographyResetcloseActivity.class);
                    } else {//正常
                        intent.setClass(TaskitemDetailActivity_12.this, TaskitemPhotographyResetActivity.class);
                    }
                    startActivity(intent);
                } else if (taskitemDetailNewInfo.getTask_type().equals("2")) {
                    Intent intent = new Intent(TaskitemDetailActivity_12.this, TaskitemShotResetActivity.class);
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
                    intent.putExtra("is_desc", is_desc);
                    intent.putExtra("code", codeStr);
                    intent.putExtra("brand", brand);
                    intent.putExtra("outlet_batch", taskitemDetailNewInfo.getOutlet_batch());
                    intent.putExtra("p_batch", taskitemDetailNewInfo.getP_batch());
                    startActivity(intent);
                } else if (taskitemDetailNewInfo.getTask_type().equals("3")) {
                    Intent intent = new Intent(TaskitemDetailActivity_12.this, TaskitemEditReset2Activity.class);
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
                    intent.putExtra("is_desc", is_desc);
                    intent.putExtra("outlet_batch", taskitemDetailNewInfo.getOutlet_batch());
                    intent.putExtra("p_batch", taskitemDetailNewInfo.getP_batch());
                    startActivity(intent);
                } else if (taskitemDetailNewInfo.getTask_type().equals("4")) {
                    Intent intent = new Intent(TaskitemDetailActivity_12.this, TaskitemMapResetActivity.class);
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
                    intent.putExtra("is_desc", is_desc);
                    intent.putExtra("code", codeStr);
                    intent.putExtra("brand", brand);
                    intent.putExtra("outlet_batch", taskitemDetailNewInfo.getOutlet_batch());
                    intent.putExtra("p_batch", taskitemDetailNewInfo.getP_batch());
                    startActivity(intent);
                } else if (taskitemDetailNewInfo.getTask_type().equals("5")) {
                    Intent intent = new Intent(TaskitemDetailActivity_12.this, TaskitemRecodResetActivity.class);
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
                    intent.putExtra("is_desc", is_desc);
                    intent.putExtra("code", codeStr);
                    intent.putExtra("brand", brand);
                    intent.putExtra("outlet_batch", taskitemDetailNewInfo.getOutlet_batch());
                    intent.putExtra("p_batch", taskitemDetailNewInfo.getP_batch());
                    startActivity(intent);
                } else if (taskitemDetailNewInfo.getTask_type().equals("6")) {
                    Intent intent = new Intent(TaskitemDetailActivity_12.this, ScanTaskResetActivity.class);
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
                    intent.putExtra("is_desc", is_desc);
                    intent.putExtra("code", codeStr);
                    intent.putExtra("brand", brand);
                    intent.putExtra("outlet_batch", taskitemDetailNewInfo.getOutlet_batch());
                    intent.putExtra("p_batch", taskitemDetailNewInfo.getP_batch());
                    startActivity(intent);
                } else if (taskitemDetailNewInfo.getTask_type().equals("7")) {//电话任务
                    taskFinish.sendPostRequest(Urls.TaskFinish, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            Tools.d(s);
                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                if (jsonObject.getInt("code") == 200) {
                                    if ("1".equals(jsonObject.getString("wuxiao"))) {
                                        Tools.showToast(TaskitemDetailActivity_12.this, "此任务已关闭");
                                    } else {
                                        Intent intent = new Intent(TaskitemDetailActivity_12.this, CallTaskResetActivity.class);
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
                                        intent.putExtra("is_desc", is_desc);
                                        intent.putExtra("code", codeStr);
                                        intent.putExtra("brand", brand);
                                        intent.putExtra("outlet_batch", taskitemDetailNewInfo.getOutlet_batch());
                                        intent.putExtra("p_batch", taskitemDetailNewInfo.getP_batch());
                                        startActivity(intent);
                                    }
                                }
                            } catch (JSONException e) {
                                Tools.showToast(TaskitemDetailActivity_12.this, getString(R.string.network_error));
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Tools.showToast(TaskitemDetailActivity_12.this, getString(R.string.network_volleyerror));
                        }
                    });
                } else if (taskitemDetailNewInfo.getTask_type().equals("9")) {//体验任务
                    Intent intent = new Intent(TaskitemDetailActivity_12.this, ScreenshotActivity.class);
                    intent.putExtra("task_id", taskitemDetailNewInfo.getId());
                    intent.putExtra("storeid", taskitemDetailNewInfo.getStoreid());
                    intent.putExtra("outlet_batch", taskitemDetailNewInfo.getOutlet_batch());
                    intent.putExtra("which_page", "1");//查看详情
                    startActivity(intent);
                }
            }
        }

    };


    protected void onDestroy() {
//        if (RecordService.isStart()) { TODO
//            unbindService(conn);
//        }
        unregisterReceiver(this);
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

    private TextView taskitemdetail_tab_left, taskitemdetail_tab_right;
    private View taskitemdetail_tab_left_line, taskitemdetail_tab_right_line;

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taskitdt_upload_store: {//整店上传
                if ("0".equals(is_complete)) {
                    Tools.showToast(this, "还有任务没有完成，不可以点击上传");
                    return;
                }
                try {
                    if (RecordService.isStart()) {
                        ConfirmDialog.showDialog(TaskitemDetailActivity_12.this, "提示！", 2, "请点击任务执行后的结束按钮,结束全程录音", null, "我知道了"
                                , null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                    @Override
                                    public void leftClick(Object object) {

                                    }

                                    @Override
                                    public void rightClick(Object object) {
                                    }
                                }).goneLeft();
                        return;
                    }
                    ConfirmDialog.showDialog(TaskitemDetailActivity_12.this, "\n提交后将不可修改，是否确认？", null, "取消", "确定", null
                            , true, new ConfirmDialog.OnSystemDialogClickListener() {
                                public void leftClick(Object object) {
                                }

                                public void rightClick(Object object) {
                                    try {
                                        if (RecordService.isStart()) {
                                            Intent service = new Intent(TaskitemDetailActivity_12.this, RecordService.class);
                                            stopService(service);
//                                            taskitdt_start_store.setVisibility(View.VISIBLE);
//                                            taskitdt_stop_store.setVisibility(View.INVISIBLE);
                                        }
                                    } catch (Exception e) {
                                    }
                                    systemDBHelper.packPhotoUpload(TaskitemDetailActivity_12.this, AppInfo.getName
                                            (TaskitemDetailActivity_12.this), project_id, storeid);
                                    sendStartUpload();
                                }
                            });
                } catch (Exception e) {
                    Tools.showToast(TaskitemDetailActivity_12.this, "异常");
                }
            }
            break;
            case R.id.taskitdt_stroe_ablum: {
                Intent intent = new Intent(TaskitemDetailActivity_12.this, AlbumNewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("projectid", project_id);
                bundle.putString("storeid", storeid);
                bundle.putInt("onlyShow", 1);
                bundle.putString("storecode", store_num);
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
            }
            break;
            case R.id.taskitemdetail_tab_left: {
                settingExit(false);
                taskitemdetail_tab_left.setTextColor(getResources().getColor(R.color.changetext));
                taskitemdetail_tab_left_line.setVisibility(View.VISIBLE);
                taskitemdetail_tab_right.setTextColor(getResources().getColor(R.color.myreward_two));
                taskitemdetail_tab_right_line.setVisibility(View.INVISIBLE);
                taskitdt_listview_left.setVisibility(View.VISIBLE);
                taskitdt_listview_right.setVisibility(View.GONE);
                if (is_takephoto) {
                    taskitdt_stroe_ablum.setVisibility(View.VISIBLE);
                }
                findViewById(R.id.tabline_shadow).setVisibility(View.VISIBLE);
                taskitdt_upload_store.setVisibility(View.GONE);
                if (isRecord) {
                    taskitdt_recode_layout.setVisibility(View.VISIBLE);
                } else {
                    taskitdt_recode_layout.setVisibility(View.GONE);
                }
                if (!"完成".equals(taskitdt_recode_state.getText().toString().trim())) {
                    if (RecordService.isStart()) {
                        taskitdt_recode_state.setVisibility(View.VISIBLE);
                        taskitdt_recode_switch.setVisibility(View.GONE);
                    } else {
                        taskitdt_recode_state.setVisibility(View.INVISIBLE);
                        taskitdt_recode_switch.setVisibility(View.VISIBLE);
                    }
                }
                if (list == null || list.isEmpty()) {
                    refreshDataLeft();
                }
            }
            break;
            case R.id.taskitemdetail_tab_right: {
                settingExit(true);
                taskitemdetail_tab_left.setTextColor(getResources().getColor(R.color.myreward_two));
                taskitemdetail_tab_left_line.setVisibility(View.INVISIBLE);
                taskitemdetail_tab_right.setTextColor(getResources().getColor(R.color.changetext));
                taskitemdetail_tab_right_line.setVisibility(View.VISIBLE);
                taskitdt_listview_left.setVisibility(View.GONE);
                taskitdt_listview_right.setVisibility(View.VISIBLE);
                taskitdt_recode_layout.setVisibility(View.GONE);
                taskitdt_stroe_ablum.setVisibility(View.GONE);
                findViewById(R.id.tabline_shadow).setVisibility(View.GONE);
                taskitdt_upload_store.setVisibility(View.VISIBLE);
                if (isRecord) {
                    taskitdt_recode_layout.setVisibility(View.VISIBLE);
                } else {
                    taskitdt_recode_layout.setVisibility(View.GONE);
                }
                if (!"完成".equals(taskitdt_recode_state.getText().toString().trim())) {
                    if (RecordService.isStart()) {
                        taskitdt_recode_state.setVisibility(View.VISIBLE);
                        taskitdt_recode_switch.setVisibility(View.VISIBLE);
                    } else {
                        taskitdt_recode_state.setVisibility(View.INVISIBLE);
                        taskitdt_recode_switch.setVisibility(View.GONE);
                    }
                }
                if (list_right == null || list_right.isEmpty()) {
                    refreshDataRight();
                }
            }
            break;
            case R.id.taskitdt_recode_switch: {
                if (!RecordService.isStart()) {
//                    Intent service = new Intent(this, RecordService.class);
                    if (!isVoicePermission()) {
                        Tools.showToast(this, "录音无法正常启动，请检查权限设置！");
                    } else {
                        ConfirmDialog.showDialog(TaskitemDetailActivity_12.this, "提示!", 2,
                                "为保证您的任务质量，请尽量不要接打电话(注：开启飞行模式并连接WIFI会增加任务审核通过率)",
                                "不设置", "去设置", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                    @Override
                                    public void leftClick(Object object) {

                                    }

                                    @Override
                                    public void rightClick(Object object) {
                                        Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                                        startActivity(intent);
                                    }
                                });
                        taskitdt_recode_switch.setText("结束");
                        taskitdt_recode_state.setVisibility(View.VISIBLE);
                        taskitdt_recode_switch.setVisibility(View.GONE);
                        Intent service = new Intent("com.orange.oy.recordservice").setPackage("com.orange.oy");
                        service.putExtra("usermobile", AppInfo.getName(TaskitemDetailActivity_12.this));
                        service.putExtra("project_id", project_id);
                        service.putExtra("projectname", projectname);
                        service.putExtra("store_name", store_name);
                        service.putExtra("store_num", store_num);
                        service.putExtra("storeid", storeid);
                        service.putExtra("dirName", AppInfo.getName(TaskitemDetailActivity_12.this) + "/" + storeid);
                        service.putExtra("fileName", Tools.getTimeSS() + Tools.getDeviceId(TaskitemDetailActivity_12.this) +
                                storeid);
                        service.putExtra("isOffline", false);
                        service.putExtra("code", codeStr);
                        service.putExtra("brand", brand);
                        startService(service);
                    }
                } else {
                    if ("0".equals(is_redo)) {
                        Tools.showToast(this, "还有任务没有完成，不可以结束录音");
                        return;
                    }
                    taskitdt_recode_state.setVisibility(View.VISIBLE);
                    taskitdt_recode_state.setText("传输中...");
                    taskitdt_recode_switch.setVisibility(View.GONE);
                    taskitdt_recode_switch.setOnClickListener(null);
                    Intent service = new Intent(this, RecordService.class);
                    stopService(service);
                }
            }
            break;
        }
    }

    private String projectname, store_name, store_num;

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
        ConfirmDialog.showDialog(this, "提示", 2, "继续返回则会清除您已执行完成的所有任务，您是否确定返回?", "确认返回", "我再想想", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
            @Override
            public void leftClick(Object object) {
                Redo();
                baseFinish();
            }

            @Override
            public void rightClick(Object object) {

            }
        });
    }

}
