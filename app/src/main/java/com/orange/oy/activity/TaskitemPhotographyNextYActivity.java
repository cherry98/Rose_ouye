package com.orange.oy.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.common.utils.IOUtils;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.orange.oy.R;
import com.orange.oy.activity.scan.ScanTaskNewActivity;
import com.orange.oy.activity.shakephoto_316.LargeImagePageActivity;
import com.orange.oy.adapter.GridImageAdapter;
import com.orange.oy.adapter.TaskitemReqPgAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.AppDBHelper;
import com.orange.oy.db.SystemDBHelper;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.DataUploadDialog;
import com.orange.oy.dialog.SelectPhotoDialog;
import com.orange.oy.info.LargeImagePageInfo;
import com.orange.oy.info.TaskEditoptionsInfo;
import com.orange.oy.info.TaskNewInfo;
import com.orange.oy.info.TaskQuestionInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.FileCache;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.MyGridView;
import com.orange.oy.view.TaskCheckView;
import com.orange.oy.view.TaskRadioView;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;

import static com.orange.oy.R.id.taskitempgnexty_gridview;

/**
 * 任务分类-拍照任务-任务说明页-信息录入页
 */
public class TaskitemPhotographyNextYActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View
        .OnClickListener, AdapterView.OnItemClickListener, AppTitle.OnExitClickForAppTitle {

    private AppTitle taskitempgnext_title;

    private void initTitle() {
        taskitempgnext_title = (AppTitle) findViewById(R.id.taskitempgnexty_title);
        if (index != null && "0".equals(index)) {
            taskitempgnext_title.settingName("拍照任务（预览）");
        } else {
            taskitempgnext_title.settingName("拍照任务");
        }
        if (!"1".equals(newtask)) {//不是新手
            taskitempgnext_title.showBack(this);
        }
    }

    @Override
    public void onExit() {
        if (index != null && "0".equals(index)) {
            Tools.showToast(TaskitemPhotographyNextYActivity.this, "抱歉，预览时任务无法执行。");
            return;
        }
        data.setClass(this, TaskitemPhotographyNextNActivity.class);
        data.putExtra("tasktype", "1");
        data.putExtra("photo_type", photo_type);
        data.putExtra("num", num);
        data.putExtra("isphoto", isphoto);
        data.putExtra("batch", batch);
        data.putExtra("is_watermark", is_watermark);
        startActivity(data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if ("1".equals(newtask)) {
                return true;
            } else {
                if (isLoading) {
                    selectUploadMode();
                } else {
                    if (!isBackEnable) {
                        returnTips();
                    }
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void returnTips() {
        ConfirmDialog.showDialog(TaskitemPhotographyNextYActivity.this, "提示！", 3, "您的照片将会被清空。",
                "继续返回", "等待上传", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                    @Override
                    public void leftClick(Object object) {
                        for (int i = 0; i < uniqueList.size(); i++) {
                            updataDBHelper.removeTask(project_id + uniqueList.get(i));
                        }
                        baseFinish();
                    }

                    @Override
                    public void rightClick(Object object) {

                    }
                });
    }

    public void onBack() {
        if (isBackEnable) {
            baseFinish();
        } else {
            if (isLoading) {//如果已经进行资料回收
                selectUploadMode();
            } else {
                returnTips();
            }
        }
    }

    protected void onStop() {
        super.onStop();
    }

    private void initNetworkConnection() {
        Takephoto = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("taskid", taskid);
                params.put("token", Tools.getToken());
                params.put("storeid", store_id);
                return params;
            }
        };
        Takephoto.setIsShowDialog(true);
        Taskphotoup = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("task_id", taskid);
                params.put("user_mobile", AppInfo.getName(TaskitemPhotographyNextYActivity.this));
                params.put("status", "0");
                params.put("task_pack_id", task_pack_id);
                params.put("storeid", store_id);
                params.put("token", Tools.getToken());
                params.put("category1", category1);
                params.put("category2", category2);
                params.put("category3", category3);
                params.put("batch", batch);
                params.put("outlet_batch", outlet_batch);
                params.put("p_batch", p_batch);
                params.put("is_fill", "0");
                if (taskRadioView != null && taskRadioView.getSelectId() != null) {//单选
                    params.put("question_id", id);
                    params.put("answers", answers);
                    params.put("note", notes);
                }
                if (taskCheckView != null && taskCheckView.getSelectId() != null) {//多选
                    params.put("question_id", id);
                    params.put("answers", answers);
                    params.put("note", notes);
                }
                params.put("txt1", Tools.filterEmoji(taskitempgnexty_edit.getText().toString().trim()));
                return params;
            }
        };
        Taskphotoup.setIsShowDialog(true);
    }

    private String isphoto, photo_type, taskid, tasktype, task_pack_id, store_id, task_name, task_pack_name, store_name,
            project_id, project_name, store_num;// photo_type 是否需要填写备注 0 不需要 1需要
    private String id, num;
    private LinearLayout taskitempgnexty_questionlayout;
    private EditText taskitempgnexty_edit;
    private GridImageAdapter adapter;
    private ArrayList<String> selectImgList = new ArrayList<>();
    private ArrayList<String> originalImgList = new ArrayList<>();//原图路径
    private static int selectIndex;
    private NetworkConnection Takephoto, Taskphotoup;
    private int maxSelect, minSelect;
    private String category1 = "", category2 = "", category3 = "";
    private String photo_compression;
    private GridView checkreqpgnext_gridview;
    private int gridViewItemHeigth;
    private int is_watermark;
    private String codeStr, brand;
    private String outlet_batch, p_batch;
    private String is_takephoto;
    private TextView taskitemedit_desc;
    private MyGridView taskitempgnexty_gridview1;
    private TaskitemReqPgAdapter adapter2;
    private ArrayList<String> picList = new ArrayList<>();
    private ImageLoader imageLoader;
    private Intent data;
    private String newtask;//判断是否是新手任务 1是0否
    private String index;//扫码任务预览
    private AppDBHelper appDBHelper;
    private boolean isBackEnable = true;//是否可返回上一页
    public static TaskitemPhotographyNextYActivity activity = null;
    private ArrayList<String> uniqueList;//存储上传记录的唯一标识（用于页面返回的清空未上传记录）
    private ImageView spread_button;

    private String batch, picStr;//示例图片
    private boolean isSpread = false;//说明是否展开

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitempgnext_y);
        activity = this;
        uniqueList = new ArrayList<>();
        updataDBHelper = new UpdataDBHelper(this);
        systemDBHelper = new SystemDBHelper(this);
        appDBHelper = new AppDBHelper(this);
        initNetworkConnection();
        selectIndex = 0;
        data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        index = data.getStringExtra("index");

        gridViewItemHeigth = (Tools.getScreeInfoWidth(this) - Tools.dipToPx(this, 60)) / 3 + Tools.dipToPx(this, 10);
        tasktype = data.getStringExtra("task_type");
        if (TextUtils.isEmpty(tasktype)) {
            tasktype = "1";
        }
        registerReceiver(this);
        project_id = data.getStringExtra("project_id");
        project_name = data.getStringExtra("project_name");
        taskid = data.getStringExtra("task_id");
        task_pack_id = data.getStringExtra("task_pack_id");
        task_pack_name = data.getStringExtra("task_pack_name");
        store_name = data.getStringExtra("store_name");
        task_name = data.getStringExtra("task_name");
        store_id = data.getStringExtra("store_id");
        store_num = data.getStringExtra("store_num");
        category1 = data.getStringExtra("category1");
        category2 = data.getStringExtra("category2");
        category3 = data.getStringExtra("category3");
        outlet_batch = data.getStringExtra("outlet_batch");
        p_batch = data.getStringExtra("p_batch");
        is_watermark = Tools.StringToInt(data.getStringExtra("is_watermark"));
        codeStr = data.getStringExtra("code");
        brand = data.getStringExtra("brand");
        newtask = data.getStringExtra("newtask");
        initTitle();
        appDBHelper.deletePhotoUrl(project_id, store_id, taskid);
        photo_compression = data.getStringExtra("photo_compression");
        selectImgList.add("camera_default");
        spread_button = (ImageView) findViewById(R.id.spread_button);
        taskitempgnexty_gridview1 = (MyGridView) findViewById(R.id.taskitempgnexty_gridview1);
        taskitemedit_desc = (TextView) findViewById(R.id.taskitempgnexty_desc);
        taskitempgnexty_questionlayout = (LinearLayout) findViewById(R.id.taskitempgnexty_questionlayout);
        checkreqpgnext_gridview = (GridView) findViewById(taskitempgnexty_gridview);
        taskitempgnexty_edit = (EditText) findViewById(R.id.taskitempgnexty_edit);

        if (index != null && "0".equals(index)) {
            checkreqpgnext_gridview.setVisibility(View.GONE);
            findViewById(R.id.taskitempgnexty_txt).setVisibility(View.GONE);
            taskitempgnexty_edit.setVisibility(View.GONE);
            taskitempgnexty_questionlayout.setVisibility(View.GONE);
            findViewById(R.id.taskitempgnexty_button).setVisibility(View.GONE);
        } else {
            checkreqpgnext_gridview.setVisibility(View.VISIBLE);
            findViewById(R.id.taskitempgnexty_txt).setVisibility(View.VISIBLE);
            taskitempgnexty_edit.setVisibility(View.VISIBLE);
            taskitempgnexty_questionlayout.setVisibility(View.VISIBLE);
            findViewById(R.id.taskitempgnexty_button).setVisibility(View.VISIBLE);
            taskitempgnext_title.setVisibility(View.VISIBLE);
        }

        adapter2 = new TaskitemReqPgAdapter(this, picList);
        taskitempgnexty_gridview1.setAdapter(adapter2);
        taskitempgnexty_gridview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (imageLoader == null) {
//                    imageLoader = new ImageLoader(TaskitemPhotographyNextYActivity.this);
//                }
//                PhotoView imageView = new PhotoView(TaskitemPhotographyNextYActivity.this);
//                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//                imageLoader.DisplayImage(picList.get(position), imageView);
//                SelecterDialog.showView(TaskitemPhotographyNextYActivity.this, imageView);

                if (picList != null && !picList.isEmpty()) {
                    if (largeImagePageInfos == null) {
                        largeImagePageInfos = new ArrayList<>();
                        for (String string : picList) {
                            LargeImagePageInfo largeImagePageInfo = new LargeImagePageInfo();
                            largeImagePageInfo.setFile_url(string);
                            largeImagePageInfos.add(largeImagePageInfo);
                        }
                    }
                    Intent intent = new Intent(TaskitemPhotographyNextYActivity.this, LargeImagePageActivity.class);
                    intent.putExtra("isList", true);
                    intent.putExtra("list", largeImagePageInfos);
                    intent.putExtra("position", position);
                    intent.putExtra("state", 1);
                    startActivity(intent);
                }
            }
        });
        findViewById(R.id.taskitempgnexty_button).setOnClickListener(this);
        initLocation();
        checkPermission();
        getData();
    }

    private ArrayList<LargeImagePageInfo> largeImagePageInfos;

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, AppInfo
                        .REQUEST_CODE_ASK_CAMERA);
                return;
            }
        }
    }

    private void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppInfo.BroadcastReceiver_TAKEPHOTO);
        context.registerReceiver(takePhotoBroadcastReceiver, filter);
    }

    private boolean isComplete = false;
    private boolean isLoading = false;

    private void unregisterReceiver(Context context) {
        context.unregisterReceiver(takePhotoBroadcastReceiver);
    }

    private BroadcastReceiver takePhotoBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent in) {
            String type = in.getStringExtra("type");
            if ("1".equals(type)) {//可更新UI
                String rate = in.getStringExtra("rate");
                String thumbnailPath = in.getStringExtra("thumbnailPath");
                if (adapter != null && rate != null && thumbnailPath != null) {
                    adapter.setRateData(rate, thumbnailPath);
                    refreshUI();
                }
            } else if ("2".equals(type) && !isBackEnable) {//资料回收完成
                isComplete = true;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        switch (requestCode) {
            case AppInfo.REQUEST_CODE_ASK_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                } else {
                    Tools.showToast(this, "摄像头权限获取失败");
                    baseFinish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                selectIndex = Integer.parseInt(v.getTag().toString());
                if ("0".equals(isphoto)) {//不可用相册
                    takeListener.onClick(null);
                } else {
                    SelectPhotoDialog.showPhotoSelecter(TaskitemPhotographyNextYActivity.this, takeListener, pickListener);
                }
            } catch (NumberFormatException exception) {
                selectIndex = 0;
                Tools.showToast(TaskitemPhotographyNextYActivity.this, "应用异常");
            }
        }
    };
    private String answers = null, notes = null;
    private boolean isrequired;
    private int min_option, max_option;

    /**
     * 题目校验&赋值
     */
    private boolean checkQuestion() {
        if (taskRadioView != null) {//单选
            TaskQuestionInfo taskQuestionInfo = taskRadioView.getSelectAnswers();
            if (isrequired && taskQuestionInfo == null) {//必填
                Tools.showToast(this, "请完成题目！");
                return false;
            } else {
                if (taskQuestionInfo != null) {
                    notes = " ";
                    if (taskQuestionInfo.getNoteEditext() != null && !TextUtils.isEmpty(taskQuestionInfo
                            .getNoteEditext().getText().toString().trim())) {//判断备注
                        notes = taskQuestionInfo.getNoteEditext().getText().toString().trim();
                    } else if (taskQuestionInfo.isRequired()) {
                        Tools.showToast(this, "被选项备注必填！");
                        return false;
                    }
                    answers = taskQuestionInfo.getId();
                }
                return true;
            }
        }
        if (taskCheckView != null) {//多选
            ArrayList<TaskQuestionInfo> taskQuestionInfos = taskCheckView.getSelectAnswer();
            if (isrequired && taskQuestionInfos.isEmpty()) {//必填
                Tools.showToast(this, "请完成题目！");
                return false;
            } else {
                notes = "";
                int taskQuestionInfosSize = taskQuestionInfos.size();
                if (taskQuestionInfosSize < min_option || taskQuestionInfosSize > max_option) {
                    if (max_option == min_option) {
                        Tools.showToast(this, "请选择" + min_option + "个选项");
                    } else {
                        Tools.showToast(this, "选择的选项应该大于" + min_option + "，小于" + max_option);
                    }
                    return false;
                } else {
                    /*判断选项备注*/
                    for (int j = 0; j < taskQuestionInfosSize; j++) {
                        TaskQuestionInfo taskQuestionInfo = taskQuestionInfos.get(j);
                        if (taskQuestionInfo.getNoteEditext() != null && !TextUtils.isEmpty
                                (taskQuestionInfo.getNoteEditext().getText().toString().trim())) {//判断备注
                            String temp = taskQuestionInfo.getNoteEditext().getText().toString().trim().replaceAll
                                    ("&&", "");
                            if (TextUtils.isEmpty(temp)) {
                                temp = " ";
                            }
                            if (TextUtils.isEmpty(notes)) {
                                notes = temp;
                            } else {
                                notes = notes + "&&" + temp;
                            }
                        } else if (taskQuestionInfo.isRequired()) {
                            Tools.showToast(this, "选项备注必填！");
                            return false;
                        } else {//如果没有备注也要用分隔符分隔
                            if (notes == null) {
                                notes = " ";
                            } else {
                                notes = notes + "&& ";
                            }
                        }
                        if (answers == null) {
                            answers = taskQuestionInfo.getId();
                        } else {
                            answers = answers + "," + taskQuestionInfo.getId();
                        }
                    }
                }
            }
            return true;
        }
        return true;
    }

    private UpdataDBHelper updataDBHelper;

    public void goStep() {
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
                    intent.putExtra("photo_compression", photo_compression);
                    if ("1".equals(type) || "8".equals(type)) {//拍照任务
                        intent.setClass(TaskitemPhotographyNextYActivity.this, TaskitemPhotographyNextYActivity.class);
                        startActivity(intent);
                    } else if ("2".equals(type)) {//视频任务
                        intent.setClass(TaskitemPhotographyNextYActivity.this, TaskitemShotActivity.class);
                        startActivity(intent);
                    } else if ("3".equals(type)) {//记录任务
                        intent.setClass(TaskitemPhotographyNextYActivity.this, TaskitemEditActivity.class);
                        startActivity(intent);
                    } else if ("4".equals(type)) {//定位任务
                        intent.setClass(TaskitemPhotographyNextYActivity.this, TaskitemMapActivity.class);
                        startActivity(intent);
                    } else if ("5".equals(type)) {//录音任务
                        intent.setClass(TaskitemPhotographyNextYActivity.this, TaskitemRecodillustrateActivity.class);
                        startActivity(intent);
                    } else if ("6".equals(type)) {//扫码任务
                        intent.setClass(TaskitemPhotographyNextYActivity.this, ScanTaskNewActivity.class);
                        startActivity(intent);
                    }
                }
            }
        }
    }

    private void sendData() {
        if (isComplete) {//资料回收完成
            goStep();
            baseFinish();
            return;
        }
        if (!isLoading) {//是否已经执行资料回收
            if (TextUtils.isEmpty(batch) || batch.equals("null")) {
                batch = "1";
            }
            Taskphotoup.sendPostRequest(Urls.Taskphotoup, new Response.Listener<String>() {
                public void onResponse(String s) {
                    Tools.d(s);
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        int code = jsonObject.getInt("code");
                        if (code == 200 || code == 2) {
                            isLoading = true;
                            String executeid = jsonObject.getString("executeid");
                            String username = AppInfo.getName(TaskitemPhotographyNextYActivity.this);
                            Map<String, String> params = new HashMap<>();
                            params.put("task_id", taskid);
                            params.put("user_mobile", username);
                            params.put("status", "1");
                            params.put("executeid", executeid);
                            params.put("task_pack_id", task_pack_id);
                            params.put("storeid", store_id);
                            params.put("category1", category1);
                            params.put("category2", category2);
                            params.put("category3", category3);
                            params.put("outlet_batch", outlet_batch);
                            params.put("p_batch", p_batch);
                            params.put("batch", batch);
                            updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, brand,
                                    store_id, store_name, task_pack_id,
                                    task_pack_name, "111", taskid, task_name, category1, category2, category3,
                                    username + project_id +
                                            store_id + task_pack_id + category1 + category2 + category3 + taskid,
                                    Urls.Filecomplete,
                                    null, null, UpdataDBHelper.Updata_file_type_img, params, photo_compression,
                                    true, Urls.Taskphotoup, paramsToString(), false);
                            Intent service = new Intent("com.orange.oy.UpdataNewService");
                            service.setPackage("com.orange.oy");
                            startService(service);
                            TaskitemDetailActivity_12.isRefresh = true;
                            TaskitemDetailActivity.taskid = taskid;
                            TaskFinishActivity.isRefresh = true;
                            TaskitemListActivity.isRefresh = true;
                            TaskitemListActivity_12.isRefresh = true;
//                            if (code == 2) {
//                                ConfirmDialog.showDialog(TaskitemPhotographyNextYActivity.this, null, jsonObject.getString("msg"), null,
//                                        "确定", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
//                                            @Override
//                                            public void leftClick(Object object) {
//
//                                            }
//
//                                            @Override
//                                            public void rightClick(Object object) {
//                                                baseFinish();
//                                            }
//                                        }).goneLeft();
//                            } else if (code == 200) {
                            String fileurl = appDBHelper.getAllPhotoUrl(username, project_id, store_id, taskid);
                            if (fileurl != null && (appDBHelper.getPhotoUrlIsCompete(fileurl, project_id, store_id, taskid))) {
                                goStep();
                                baseFinish();
                            } else {
                                selectUploadMode();
                            }
//                            }
                        } else {
                            Tools.showToast(TaskitemPhotographyNextYActivity.this, jsonObject.getString("msg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Tools.showToast(TaskitemPhotographyNextYActivity.this, getResources().getString(R.string
                                .network_error));
                    }
                    CustomProgressDialog.Dissmiss();
                }
            }, new Response.ErrorListener()

            {
                public void onErrorResponse(VolleyError volleyError) {
                    CustomProgressDialog.Dissmiss();
                    Tools.showToast(TaskitemPhotographyNextYActivity.this, getResources().getString(R.string
                            .network_volleyerror));
                }
            }, null);
        } else {
            selectUploadMode();
        }
    }

    private void selectUploadMode() {
        if (!isComplete) {
            String mode = appDBHelper.getDataUploadMode(store_id);
            Tools.d("上传模式....." + mode);
            if ("1".equals(mode)) {//弹框选择==1
                DataUploadDialog.showDialog(TaskitemPhotographyNextYActivity.this, false, new DataUploadDialog.OnDataUploadClickListener() {
                    @Override
                    public void firstClick() {
                        appDBHelper.addDataUploadRecord(store_id, "1");
                    }

                    @Override
                    public void secondClick() {
                        appDBHelper.addDataUploadRecord(store_id, "2");
                        goStep();
                        baseFinish();
                    }

                    @Override
                    public void thirdClick() {
                        appDBHelper.addDataUploadRecord(store_id, "3");
                        goStep();
                        baseFinish();
                    }
                });
            } else if ("2".equals(mode)) {//弹框选择===2
                DataUploadDialog.showDialog(TaskitemPhotographyNextYActivity.this, false, new DataUploadDialog.OnDataUploadClickListener() {
                    @Override
                    public void firstClick() {
                        appDBHelper.addDataUploadRecord(store_id, "1");
                    }

                    @Override
                    public void secondClick() {
                        appDBHelper.addDataUploadRecord(store_id, "2");
                        goStep();
                        baseFinish();
                    }

                    @Override
                    public void thirdClick() {
                        appDBHelper.addDataUploadRecord(store_id, "3");
                        goStep();
                        baseFinish();
                    }
                });
            } else if ("3".equals(mode)) {//直接关闭
                appDBHelper.addDataUploadRecord(store_id, "3");
                goStep();
                baseFinish();
            }
        }
    }

    private String paramsToString() {
        Map<String, String> parames = new HashMap<>();
        parames.put("task_id", taskid);
        parames.put("user_mobile", AppInfo.getName(this));
        parames.put("status", "0");
        parames.put("task_pack_id", task_pack_id);
        parames.put("storeid", store_id);
        parames.put("token", Tools.getToken());
        parames.put("category1", category1);
        parames.put("category2", category2);
        parames.put("category3", category3);
        parames.put("batch", batch);
        parames.put("outlet_batch", outlet_batch);
        parames.put("p_batch", p_batch);
        parames.put("is_fill", "0");
        int size;
        if (taskRadioView != null && taskRadioView.getSelectId() != null) {//单选
            parames.put("question_id", id);
            parames.put("answers", answers);
            parames.put("note", notes);
        }
        if (taskCheckView != null && taskCheckView.getSelectId() != null) {//多选
            parames.put("question_id", id);
            parames.put("answers", answers);
            parames.put("note", notes);
        }
        parames.put("txt1", Tools.filterEmoji(taskitempgnexty_edit.getText().toString().trim()));
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

    private boolean local_photo = false;

    private void getData() {
        Takephoto.sendPostRequest(Urls.Takephoto, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200 || code == 100) {
                        ((TextView) findViewById(R.id.taskitempgnexty_name)).setText(jsonObject.getString("name"));
                        taskitemedit_desc.setText(jsonObject.getString("desc"));
                        if (taskitemedit_desc.getLineCount() > 1) {
                            taskitemedit_desc.setSingleLine(true);
                            isSpread = false;
                            findViewById(R.id.spread_button_layout).setOnClickListener(TaskitemPhotographyNextYActivity.this);
                            findViewById(R.id.spread_button_layout).setVisibility(View.VISIBLE);
                            if (index != null && "0".equals(index))
                                onClick(findViewById(R.id.spread_button_layout));
                        } else {
                            findViewById(R.id.spread_button_layout).setVisibility(View.GONE);
                        }
                        num = jsonObject.getString("num");
                        local_photo = "1".equals(jsonObject.getString("local_photo"));
                        String wuxiao = jsonObject.getString("wuxiao");
                        Tools.d("tag", "wuxiao====>>" + wuxiao);
                        Tools.d("tag", "index====>>" + index);
                        if ("1".equals(wuxiao) && !(index != null && "0".equals(index))) {
                            taskitempgnext_title.settingExit("无法执行", getResources().getColor(R.color.homepage_select), TaskitemPhotographyNextYActivity.this);

                        }
                        String min_num = jsonObject.getString("min_num");
                        isphoto = jsonObject.getString("isphoto");
                        photo_type = jsonObject.getString("photo_type");
                        batch = jsonObject.getString("batch");
                        maxSelect = Tools.StringToInt(num);
                        minSelect = Tools.StringToInt(min_num);
                        if (maxSelect == -1) {
                            maxSelect = 9;
                        }
                        picStr = jsonObject.getString("pics");
                        picStr = picStr.replaceAll("\\\"", "").replaceAll("\\[", "").replaceAll("\\]", "");
                        findViewById(R.id.shili).setVisibility(View.GONE);
                        taskitempgnexty_gridview1.setVisibility(View.GONE);
                        if (TextUtils.isEmpty(picStr) || "null".equals(picStr)) {
                        } else {
                            findViewById(R.id.spread_button_layout).setOnClickListener(TaskitemPhotographyNextYActivity.this);
                            findViewById(R.id.spread_button_layout).setVisibility(View.VISIBLE);
                            if (index != null && "0".equals(index))
                                onClick(findViewById(R.id.spread_button_layout));
//                            picStr = picStr.substring(1, picStr.length() - 1);
                            String[] pics = picStr.split(",");
                            for (int i = 0; i < pics.length; i++) {
                                String url = pics[i].replaceAll("\"", "").replaceAll("\\\\", "");
                                if (!(url.startsWith("http://") || url.startsWith("https://"))) {
                                    url = Urls.ImgIp + url;
                                }
                                picList.add(url);
                            }
//                            if (pics.length > 0) {
//                                int t = (int) Math.ceil(pics.length / 3d);
//                                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) taskitempgnexty_gridview1.getLayoutParams();
//                                lp.height = (int) ((Tools.getScreeInfoWidth(TaskitemPhotographyNextYActivity.this) -
//                                        getResources().getDimension(R.dimen.taskphoto_gridview_mar) * 2 -
//                                        getResources().getDimension(R.dimen.taskphoto_gridview_item_mar) * 2) / 3) * t;
//                                taskitempgnexty_gridview1.setLayoutParams(lp);
//                            }
                            adapter2.notifyDataSetChanged();
                        }
                        adapter = new GridImageAdapter(TaskitemPhotographyNextYActivity.this, selectImgList);
                        checkreqpgnext_gridview.setAdapter(adapter);
                        checkreqpgnext_gridview.setOnItemClickListener(TaskitemPhotographyNextYActivity.this);
                        if ("1".equals(photo_type) && !(index != null && "0".equals(index))) {
                            findViewById(R.id.taskitempgnexty_txt).setVisibility(View.VISIBLE);
                            taskitempgnexty_edit.setVisibility(View.VISIBLE);
                        } else {
                            findViewById(R.id.taskitempgnexty_txt).setVisibility(View.GONE);
                            taskitempgnexty_edit.setVisibility(View.GONE);
                        }
                        if (code == 200) {
                            String question_type = jsonObject.getString("question_type");
                            id = jsonObject.getString("id");
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                                    .MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            isrequired = "1".equals(jsonObject.getString("isrequired"));
                            max_option = jsonObject.getInt("max_option");
                            min_option = jsonObject.getInt("min_option");
                            String prompt = jsonObject.getString("prompt");
                            if (TextUtils.isEmpty(prompt) || prompt.equals("null")) {
                                prompt = "";
                            } else {
                                prompt = "(" + prompt + ")";
                            }
                            if ("1".equals(question_type)) {
                                findViewById(R.id.taskitempgnexty_questionhint).setVisibility(View.VISIBLE);
                                addQuestionRadioView(jsonObject.getString("question_name") + prompt, jsonObject.getJSONArray
                                        ("options"), isrequired);
                                taskRadioView.setTag(jsonObject.getString("id"));
                                taskitempgnexty_questionlayout.addView(taskRadioView, lp);
                            } else if ("2".equals(question_type)) {
                                findViewById(R.id.taskitempgnexty_questionhint).setVisibility(View.VISIBLE);
                                addQuestionCheckView(jsonObject.getString("question_name") + prompt, jsonObject.getJSONArray
                                        ("options"), isrequired);
                                taskCheckView.setTag(jsonObject.getString("id"));
                                taskitempgnexty_questionlayout.addView(taskCheckView, lp);
                            }
                        }
                    } else {
                        Tools.showToast(TaskitemPhotographyNextYActivity.this, jsonObject.getString("msg"));
                    }
                } catch (
                        JSONException e)

                {
                    e.printStackTrace();
                    Tools.showToast(TaskitemPhotographyNextYActivity.this, getResources().getString(R.string
                            .network_error));
                }
//                if (!(index != null && "0".equals(index))) {
//                    ArrayList<String> tempList = systemDBHelper.getPictureThumbnailForTask(AppInfo.getName
//                            (TaskitemPhotographyNextYActivity.this), project_id, store_id, task_pack_id, taskid);
//                    if (tempList != null && !tempList.isEmpty()) {
//                        selectImgList.clear();
//                        selectImgList.addAll(tempList);
//                        if (selectImgList.size() < maxSelect) {
//                            selectImgList.add("camera_default");
//                        }
//                        refreshUI();
//                    }
//                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener()

        {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskitemPhotographyNextYActivity.this, getResources().getString(R.string
                        .network_volleyerror));
            }
        }, null);
    }

    private TaskRadioView taskRadioView;
    private TaskCheckView taskCheckView;
    private String questionInfo = "";

    /**
     * 添加单选
     *
     * @throws JSONException
     */
    private void addQuestionRadioView(String title, JSONArray jsonArray, boolean isrequired) throws JSONException {
        taskRadioView = new TaskRadioView(this);
        taskRadioView.setTitle(title, isrequired);
        questionInfo += title;
        int length = jsonArray.length();
        JSONObject jsonObject;
        for (int i = 0; i < length; i++) {
            jsonObject = jsonArray.getJSONObject(i);
            String optionName = jsonObject.getString("option_name");
            if ("1".equals(jsonObject.getString("isfill"))) {
                taskRadioView.addRadioButtonForFill(jsonObject.getString("id"), optionName, jsonObject.getString("isforcedfill"), null);
            } else {
                taskRadioView.addRadioButton(jsonObject.getString("id"), optionName, null);
            }
            questionInfo = questionInfo + "_" + optionName;
        }
    }

    /**
     * 添加多选
     *
     * @throws JSONException
     */
    private void addQuestionCheckView(String title, JSONArray jsonArray, boolean isrequired) throws JSONException {
        taskCheckView = new TaskCheckView(this);
        taskCheckView.setTitle(title, isrequired);
        int length = jsonArray.length();
        JSONObject jsonObject;
        for (int i = 0; i < length; i++) {
            jsonObject = jsonArray.getJSONObject(i);
            if ("1".equals(jsonObject.getString("isfill"))) {
                taskCheckView.addCheckBoxForFill(jsonObject.getString("option_name"), jsonObject.getString
                        ("isforcedfill"), jsonObject.getString("id"), jsonObject.getInt("option_num"), jsonObject
                        .getString("mutex_id"));
            } else {
                TaskEditoptionsInfo taskEditoptionsInfo = new TaskEditoptionsInfo();
                taskEditoptionsInfo.setOption_name(jsonObject.getString("option_name"));
                taskEditoptionsInfo.setId(jsonObject.getString("id"));
                taskEditoptionsInfo.setOption_num(jsonObject.getInt("option_num"));
                taskEditoptionsInfo.setMutex_id(jsonObject.getString("mutex_id"));
                taskCheckView.addCheckBox(taskEditoptionsInfo);
            }
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taskitempgnexty_button: {
                if (index != null && "0".equals(index)) {
                    Tools.showToast(TaskitemPhotographyNextYActivity.this, "抱歉，预览时任务无法执行。");
                    return;
                }
                if (!checkQuestion()) {//校验题目
                    answers = null;
                    notes = null;
                    return;
                }
                if (selectImgList == null || selectImgList.isEmpty() || selectImgList.get(0).equals("camera_default")) {
                    Tools.showToast(TaskitemPhotographyNextYActivity.this, "请拍照");
                    return;
                }
                int size = selectImgList.size();
                if (selectImgList.get(size - 1).contains("default")) {
                    size--;
                }
                if (size < minSelect) {
                    if (selectImgList.size() - 1 < minSelect) {
                        Tools.showToast(TaskitemPhotographyNextYActivity.this, "拍照数量不足" + minSelect + "张");
                        return;
                    }
                }
                sendData();
//                new zoomImageAsyncTask().executeOnExecutor(Executors.newCachedThreadPool());
            }
            break;
            case R.id.spread_button_layout: {
                if (!TextUtils.isEmpty(picStr) && !"null".equals(picStr)) {
                    if (taskitempgnexty_gridview1.getVisibility() == View.VISIBLE) {
                        spread_button.setImageResource(R.mipmap.spread_button_down);
                        findViewById(R.id.shili).setVisibility(View.GONE);
                        taskitempgnexty_gridview1.setVisibility(View.GONE);
                        taskitemedit_desc.setSingleLine(true);
                    } else {
                        spread_button.setImageResource(R.mipmap.spread_button_up);
                        taskitemedit_desc.setSingleLine(false);
                        findViewById(R.id.shili).setVisibility(View.VISIBLE);
                        taskitempgnexty_gridview1.setVisibility(View.VISIBLE);
                    }
                } else {
                    findViewById(R.id.shili).setVisibility(View.GONE);
                    taskitempgnexty_gridview1.setVisibility(View.GONE);
                    if (isSpread) {//说明展开
                        isSpread = false;
                        spread_button.setImageResource(R.mipmap.spread_button_down);
                        taskitemedit_desc.setSingleLine(true);
                    } else {
                        isSpread = true;
                        spread_button.setImageResource(R.mipmap.spread_button_up);
                        taskitemedit_desc.setSingleLine(false);
                    }
                }
            }
            break;
        }
    }

    public static final int TakeRequest = 0x100;
    public static final int PickRequest = 0x101;
    //拍照
    private View.OnClickListener takeListener = new View.OnClickListener() {
        public void onClick(View v) {
            SelectPhotoDialog.dissmisDialog();
            int temp = maxSelect - selectIndex;
            if (temp > 0) {
                Intent intent = new Intent(TaskitemPhotographyNextYActivity.this, Camerase.class);
                intent.putExtra("projectid", project_id);
                intent.putExtra("storeid", store_id);
                intent.putExtra("packageid", task_pack_id);
                intent.putExtra("taskid", taskid);
                intent.putExtra("storecode", store_num);
                intent.putExtra("maxTake", 1);
                intent.putExtra("state", 1);
                if ("8".equals(tasktype)) {
                    intent.putExtra("isCFouce", true);
                }
                startActivityForResult(intent, TakeRequest);
            } else {
                Tools.showToast(TaskitemPhotographyNextYActivity.this, "已到拍照上限！");
            }
        }
    };

    //相册选取
    private View.OnClickListener pickListener = new View.OnClickListener() {
        public void onClick(View v) {
            SelectPhotoDialog.dissmisDialog();
            Intent intent = new Intent(TaskitemPhotographyNextYActivity.this, AlbumActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("maxsize", maxSelect - selectIndex);
            bundle.putString("projectid", project_id);
            bundle.putString("storeid", store_id);
            bundle.putString("packetid", task_pack_id);
            bundle.putString("taskid", taskid);
            intent.putExtras(bundle);
            startActivityForResult(intent, 0);
        }
    };

    //手机相册选取
    private View.OnClickListener photoPickListener = new View.OnClickListener() {
        public void onClick(View v) {
            SelectPhotoDialog.dissmisDialog();
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, AppInfo.phototaskNYRequestCodeForPick);
        }
    };

    private void settingImgPath(String path) {
        File file = new File(path);
        if (!file.isFile()) {
            Tools.showToast(this, "拍照方式错误");
            return;
        }
        startZoomImageAsyncTask(path, "");
    }

    private void settingImgPath(String path, String opath) {
        if (selectIndex < selectImgList.size()) {
            String temp = selectImgList.remove(selectIndex);
            if (!temp.contains("default")) {
                systemDBHelper.updataStateTo2(new String[]{temp});
            }
            if (selectIndex == selectImgList.size()) {
                selectImgList.add(path);
            } else {
                selectImgList.add(selectIndex, path);
            }
        } else {
            selectImgList.add(path);
        }
        new zoomImageAsyncTask(path, opath, true).executeOnExecutor(Executors.newCachedThreadPool());
    }

    private SystemDBHelper systemDBHelper;

    public void startZoomImageAsyncTask(String path, String name) {
        if (selectIndex < selectImgList.size()) {
            String temp = selectImgList.remove(selectIndex);
            if (!temp.contains("default")) {
                systemDBHelper.updataStateTo2(new String[]{temp});
            }
            if (selectIndex == selectImgList.size()) {
                selectImgList.add(path);
            } else {
                selectImgList.add(selectIndex, path);
            }
        } else {
            selectImgList.add(path);
        }
        new zoomImageAsyncTask(path).executeOnExecutor(Executors.newCachedThreadPool());
    }

    private int current;

    public void startZoomImageAsyncTask(String name, ArrayList<String> list) {
        current = selectImgList.size() - 1;
        int size = list.size();
        if (size > 0) {
            if (size <= maxSelect - selectIndex) {
                int oldSize = selectImgList.size();
                for (int i = selectIndex, j = 0; j < size; j++, i++) {
                    if (i < oldSize) {
                        systemDBHelper.updataStateTo2(new String[]{selectImgList.get(i)});
                        selectImgList.set(i, list.get(j));
                    } else {
                        selectImgList.add(list.get(j));
                    }
                }
                systemDBHelper.updataStateTo1(list.toArray(new String[size]));
                new zoomImageAsyncTask(true).executeOnExecutor(Executors.newCachedThreadPool());
            } else {
                Tools.showToast(this, "选择异常，请重新选择图片！");
            }
        }
    }

    class zoomImageAsyncTask extends AsyncTask {
        String msg = "图片压缩失败！";
        String path, oPath;
        boolean isAblum;
        boolean isLocation;

        public zoomImageAsyncTask(String path) {
            this.path = path;
        }

        public zoomImageAsyncTask(boolean isAblum) {
            this.isAblum = isAblum;
        }

        public zoomImageAsyncTask(String path, String opath, boolean isLocation) {
            this.path = path;
            this.oPath = opath;
            this.isLocation = isLocation;
        }

        protected void onPreExecute() {
            if (photo_compression.equals("-1")) {
                CustomProgressDialog.showProgressDialog(TaskitemPhotographyNextYActivity.this, "图片处理中...");
            } else {
                CustomProgressDialog.showProgressDialog(TaskitemPhotographyNextYActivity.this, "图片压缩中...");
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
                if (isLocation) {//本地相册
                    if (!TextUtils.isEmpty(oPath)) {
                        if (photo_compression.equals("-1")) {
                            if (!originalImgList.contains(oPath)) {
                                originalImgList.add(oPath);
                            } else {
                                msg = "发现重复照片，已自动去重，请重新提交";
                                selectImgList.remove(path);
                                isHadUnlegal = true;
                            }
                        } else {//加水印
                            File tempFile = getTempFile(oPath);//生成临时文件
                            if (tempFile == null) {
                                selectImgList.remove(path);
                                isHadUnlegal = true;
                            }
                            if (saveBitmap(imageZoom(Tools.getBitmap(tempFile.getPath(), 1024, 1024),
                                    Tools.StringToInt(photo_compression)), tempFile.getPath(), oPath, isLocation) != null) {
                                if (!originalImgList.contains(oPath)) {
                                    originalImgList.add(oPath);
                                } else {
                                    msg = "发现重复照片，已自动去重，请重新提交";
                                    selectImgList.remove(path);
                                    isHadUnlegal = true;
                                }
                            } else {
                                selectImgList.remove(path);
                                isHadUnlegal = true;
                            }
                            if (tempFile.exists()) {
                                tempFile.delete();
                            }
                        }
                    } else {
                        selectImgList.remove(path);
                        if (!TextUtils.isEmpty(oPath)) {//判定为异常图片，执行删除操作
                            new File(oPath).delete();
                            new File(path).delete();
                            systemDBHelper.deletePicture(oPath);
                        }
                        msg = "有图片异常，已自动删除异常图片,请重新提交";
                        isHadUnlegal = true;
                    }
                } else {
                    if (isAblum) {
                        int size = selectImgList.size();
                        for (int i = current; i < size; i++) {
                            String tPath = selectImgList.get(i);
                            if ("camera_default".equals(tPath)) {
                                continue;
                            }
                            String oPath = systemDBHelper.searchForOriginalpath(tPath);
                            Tools.d("opath照片1：" + oPath);
                            if (!TextUtils.isEmpty(oPath) && isLegal(oPath) && checkPicture(oPath)) {
                                if (photo_compression.equals("-1")) {
                                    if (systemDBHelper.bindTaskForPicture(oPath, task_pack_id, taskid)) {
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
                                            Tools.StringToInt(photo_compression)), tempFile.getPath(), oPath, isLocation) != null) {
                                        Tools.d(oPath);
                                        if (systemDBHelper.bindTaskForPicture(oPath, task_pack_id, taskid)) {
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
                    } else {
                        oPath = systemDBHelper.searchForOriginalpath(path);
                        Tools.d("opath照片2：" + oPath);
                        if (!TextUtils.isEmpty(oPath) && isLegal(oPath) && checkPicture(oPath)) {
                            if (photo_compression.equals("-1")) {
                                if (systemDBHelper.bindTaskForPicture(oPath, task_pack_id, taskid)) {
                                    if (!originalImgList.contains(oPath)) {
                                        originalImgList.add(oPath);
                                    } else {
                                        msg = "发现重复照片，已自动去重，请重新提交";
                                        selectImgList.remove(path);
                                        isHadUnlegal = true;
                                    }
                                } else {
                                    selectImgList.remove(path);
                                    isHadUnlegal = true;
                                }
                            } else {//加水印
                                File tempFile = getTempFile(oPath);//生成临时文件
                                if (tempFile == null) {
                                    selectImgList.remove(path);
                                    isHadUnlegal = true;
                                }
                                if (saveBitmap(imageZoom(Tools.getBitmap(tempFile.getPath(), 1024, 1024),
                                        Tools.StringToInt(photo_compression)), tempFile.getPath(), oPath, isLocation) != null) {
                                    if (systemDBHelper.bindTaskForPicture(oPath, task_pack_id, taskid)) {
                                        if (!originalImgList.contains(oPath)) {
                                            originalImgList.add(oPath);
                                        } else {
                                            msg = "发现重复照片，已自动去重，请重新提交";
                                            selectImgList.remove(path);
                                            isHadUnlegal = true;
                                        }
                                    } else {
                                        selectImgList.remove(path);
                                        isHadUnlegal = true;
                                    }
                                } else {
                                    selectImgList.remove(path);
                                    isHadUnlegal = true;
                                }
                                if (tempFile.exists()) {
                                    tempFile.delete();
                                }
                            }
                        } else {
                            selectImgList.remove(path);
                            if (!TextUtils.isEmpty(oPath)) {//判定为异常图片，执行删除操作
                                new File(oPath).delete();
                                new File(path).delete();
                                systemDBHelper.deletePicture(oPath);
                            }
                            msg = "有图片异常，已自动删除异常图片,请重新提交";
                            isHadUnlegal = true;
                        }
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
                Tools.showToast(TaskitemPhotographyNextYActivity.this, msg);
                if (originalImgList != null) {
                    originalImgList.clear();
                }
                CustomProgressDialog.Dissmiss();
            } else {
                if (isAblum) {
                    String key = "", imgs = "";
                    int size = originalImgList.size();
                    String username = AppInfo.getName(TaskitemPhotographyNextYActivity.this);
                    for (int i = 0; i < size; i++) {
                        appDBHelper.addPhotoUrlRecord(username, project_id, store_id, taskid, originalImgList.get(i), selectImgList.get(i));
                        appDBHelper.setFileNum(originalImgList.get(i), size + "");
                        systemDBHelper.updataStateOPathTo3_2(originalImgList.get(i));
                        String path2 = originalImgList.get(i);
                        if (path2.equals("camera_default")) {
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
                    String uniquelyNum = username + project_id + store_id + task_pack_id + category1 + category2 + category3 + taskid + size;
                    uniqueList.add(uniquelyNum);
                    boolean isSuccess = updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, brand,
                            store_id, store_name, task_pack_id,
                            task_pack_name, "11", taskid, task_name, category1, category2, category3,
                            uniquelyNum, null, key, imgs, UpdataDBHelper.Updata_file_type_img, null, photo_compression,
                            false, null, null, false);
                    if (isSuccess) {
                        CustomProgressDialog.Dissmiss();
                    }
                } else {
                    String username = AppInfo.getName(TaskitemPhotographyNextYActivity.this);
                    String uniquelyNum = username + project_id + store_id + task_pack_id + category1 + category2 + category3 + taskid + originalImgList.size();
                    uniqueList.add(uniquelyNum);
                    boolean isSuccess1 = updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, brand,
                            store_id, store_name, task_pack_id,
                            task_pack_name, "11", taskid, task_name, category1, category2, category3,
                            uniquelyNum, null, "img", oPath, UpdataDBHelper.Updata_file_type_img, null, photo_compression,
                            false, null, null, false);
                    boolean isSuccess2 = appDBHelper.addPhotoUrlRecord(username, project_id, store_id, taskid, oPath, path);
                    if (isSuccess1 && isSuccess2) {
                        appDBHelper.setFileNum(oPath, originalImgList.size() + "");
                        CustomProgressDialog.Dissmiss();
                    }
                    systemDBHelper.updataStateOPathTo3_2(oPath);
                }
                Intent service = new Intent("com.orange.oy.UpdataNewService");
                service.setPackage("com.orange.oy");
                startService(service);
            }
            refreshUI();
        }
    }

    private void refreshUI() {
        int size = selectImgList.size();
        if (size > 0) {
            if (size < maxSelect && !selectImgList.get(size - 1).equals("camera_default")) {
                selectImgList.add("camera_default");
            }
        } else {
            selectImgList.add("camera_default");
        }
        int t = (int) Math.ceil(selectImgList.size() / 3d);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) checkreqpgnext_gridview.getLayoutParams();
        lp.height = t * gridViewItemHeigth;
        checkreqpgnext_gridview.setLayoutParams(lp);

        adapter.notifyDataSetChanged();
    }

    //检测加密图片是否正常
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

    private boolean isLegal(String path) {
        File file = new File(path);
//        return file.length() > 51200;
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TakeRequest: {//拍照
                    isBackEnable = false;
                    settingImgPath(data.getStringExtra("path"));
                }
                break;
                case PickRequest: {
                    Uri selectedImage = data.getData();
                    String filePath = null;
                    try {
                        if (selectedImage.toString().startsWith("content")) {
                            String[] filePathColumn = {MediaStore.Images.Media.DATA};
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor.moveToFirst()) {
                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                filePath = cursor.getString(columnIndex);
                            }
                            cursor.close();
                        } else {
                            filePath = selectedImage.getPath();
                        }
                    } catch (Exception e) {
                    }
                    if (filePath == null) {
                        Tools.showToast(this, "图片读取失败");
                        return;
                    }
                    settingImgPath(filePath);
                }
                break;
                case 0: {//从相册选取
                    isBackEnable = false;
                    Bundle bundle = data.getExtras();
                    ArrayList<String> tDataList = (ArrayList<String>) bundle.getSerializable("dataList");
                    startZoomImageAsyncTask(Tools.getTimeSS() + Tools.getDeviceId(TaskitemPhotographyNextYActivity.this) +
                            taskid, tDataList);
                }
                break;
                case AppInfo.phototaskNYRequestCodeForPick: {
                    Uri uri = data.getData();
                    String path = getPath(uri);
                    if (path != null) {
                        new copyPhoto(path).executeOnExecutor(Executors.newCachedThreadPool());
                    }
                }
                break;
            }
        }
    }

    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private class copyPhoto extends AsyncTask {
        String path;

        copyPhoto(String path) {
            this.path = path;
            CustomProgressDialog.showProgressDialog(TaskitemPhotographyNextYActivity.this, "图片处理中");
        }

        protected Object doInBackground(Object[] params) {
            String[] strs = new String[2];
            FileInputStream fis = null;
            FileOutputStream fos = null;
            FileOutputStream fos2 = null;
            try {
                fis = new FileInputStream(path);
                String url1 = FileCache.getDirForCamerase(TaskitemPhotographyNextYActivity.this).getPath()
                        + "/" + Tools.getTimeSS() + "_2.ouye";
                fos = new FileOutputStream(url1);
                int index = 0;
                byte[] bytes = new byte[1024 * 1024];
                while ((index = fis.read(bytes)) > -1) {
                    fos.write(bytes, 0, index);
                }
                fos.flush();
                strs[0] = url1;
                String url2 = FileCache.getDirForCamerase(TaskitemPhotographyNextYActivity.this).getPath()
                        + "/" + Tools.getTimeSS() + ".ouye";
                fos2 = new FileOutputStream(url2);
                index = 0;
                IOUtils.safeClose(fis);
                fis = new FileInputStream(path);
                byte[] bytes1 = new byte[1024 * 1024];
                while ((index = fis.read(bytes1)) > -1) {
                    for (int i = 0; i < index; i++) {
                        bytes1[i] = (byte) (255 - bytes1[i]);
                    }
                    fos2.write(bytes1, 0, index);
                }
                fos2.flush();
                strs[1] = url2;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.safeClose(fis);
                IOUtils.safeClose(fos);
                IOUtils.safeClose(fos2);
            }
            return strs;
        }

        protected void onPostExecute(Object object) {
            CustomProgressDialog.Dissmiss();
            String[] strs = (String[]) object;
            settingImgPath(strs[0], strs[1]);
        }
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

    //加密图片
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

    private File saveBitmap(Bitmap bm, String tempPath, String oPath, boolean isLocation) throws FileNotFoundException,
            OutOfMemoryError {
        File returnvalue = null;
        FileOutputStream out = null;
        try {
            File f = new File(tempPath);
            out = new FileOutputStream(f);
            ExifInterface exif = null;
            int pointIndex = oPath.lastIndexOf(".");
            exif = new ExifInterface(oPath.substring(0, pointIndex) + "_2.ouye");
            if (is_watermark == 1 && (!systemDBHelper.isBindForPicture(oPath) || isLocation)) {
                if (locationStr == null) {
                    locationStr = "";
                } else {
                    locationStr = "\n" + locationStr;
                }
                if (isLocation) {
                    bm = addWatermark(bm, locationStr);
                } else {
                    bm = addWatermark(bm, systemDBHelper.searchForWatermark(oPath));
                }
            }
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            if (exif != null) {
                ExifInterface exif2 = new ExifInterface(f.getPath());
                exif2.setAttribute(ExifInterface.TAG_ORIENTATION, exif.getAttribute(ExifInterface.TAG_ORIENTATION));
                exif2.saveAttributes();
            }
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

    /**
     * 定位客户端
     */
    public LocationClient mLocationClient = null;
    public MyLocationListenner myListener = new MyLocationListenner();
    private GeoCoder mSearch = null;
    public static double location_latitude, location_longitude;
    public String locationStr = "";

    /**
     * 初始化定位
     */
    private void initLocation() {
        if (mLocationClient != null && mLocationClient.isStarted()) {
            return;
        }
        if (mSearch == null) {
            mSearch = GeoCoder.newInstance();
            mSearch.setOnGetGeoCodeResultListener(onGetGeoCoderResultListener);
        }
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(myListener);
        setLocationOption();
        mLocationClient.start();
    }

    // 设置相关参数
    private void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll");
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setIsNeedAddress(true);
        option.setScanSpan(10000);
        mLocationClient.setLocOption(option);
    }

    private class MyLocationListenner implements BDLocationListener {
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }
            location_latitude = location.getLatitude();
            location_longitude = location.getLongitude();
            LatLng point = new LatLng(location_latitude, location_longitude);
            if (mSearch != null)
                mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(point));
        }

        public void onConnectHotSpotMessage(String s, int i) {

        }

        public void onReceivePoi(BDLocation arg0) {
        }

    }

    private OnGetGeoCoderResultListener onGetGeoCoderResultListener = new OnGetGeoCoderResultListener() {

        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
        }

        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
            if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                return;
            }
            ReverseGeoCodeResult.AddressComponent addressComponent = reverseGeoCodeResult.getAddressDetail();
            int index = addressComponent.streetNumber.lastIndexOf("号");
            if (index > 0) {
                try {
                    String str = String.valueOf(addressComponent.streetNumber.charAt(index - 1));
                    Tools.d(str);
                    if (Tools.StringToInt(str) != -1) {
                        addressComponent.streetNumber = addressComponent.streetNumber.substring(0, index + 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (addressComponent.province.endsWith("市")) {
                locationStr = addressComponent.city + addressComponent.district +
                        addressComponent.street + addressComponent.streetNumber;
            } else {
                locationStr = addressComponent.province + addressComponent.city +
                        addressComponent.district + addressComponent.street + addressComponent.streetNumber;
            }
        }
    };

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (index != null && "0".equals(index)) {
            Tools.showToast(TaskitemPhotographyNextYActivity.this, "抱歉，预览时任务无法执行。");
            return;
        }
        int size = selectImgList.size();
        if (position < size) {
            selectIndex = position;
            if ("0".equals(isphoto)) {//不可用相册
                if ("camera_default".equals(selectImgList.get(position))) {
                    takeListener.onClick(null);
                }
            } else {
                refreshData();
            }
        }
    }

    private boolean isCheck = false;

    private void refreshData() {

        new AsyncTask<Void, Void, ArrayList<String>>() {

            protected void onPreExecute() {
                isCheck = true;
                super.onPreExecute();
            }

            protected ArrayList<String> doInBackground(Void... params) {
                ArrayList<String> list;
                list = systemDBHelper.getPictureThumbnail(AppInfo.getName(TaskitemPhotographyNextYActivity.this), project_id,
                        store_id, task_pack_id, taskid);
                int size = list.size();
                String temp;
                for (int i = 0; i < size; i++) {
                    temp = list.get(i);
                    File file = new File(systemDBHelper.searchForOriginalpath(temp));
                    if (!file.exists() || !file.isFile()) {
                        systemDBHelper.deletePicture(temp);
                        file = new File(temp);
                        if (file.exists()) {
                            file.delete();
                        }
                        list.remove(i);
                        i--;
                        size--;
                    }
                }
                return list;
            }

            protected void onPostExecute(ArrayList<String> tmpList) {
                if (TaskitemPhotographyNextYActivity.this == null || TaskitemPhotographyNextYActivity.this.isFinishing()) {
                    return;
                }
                if (tmpList != null && !tmpList.isEmpty()) {
                    if (local_photo) {
                        SelectPhotoDialog.showPhotoSelecterAll(TaskitemPhotographyNextYActivity.this, takeListener,
                                pickListener, photoPickListener);
                    } else {
                        SelectPhotoDialog.showPhotoSelecter(TaskitemPhotographyNextYActivity.this, takeListener, pickListener);
                    }
                } else {
                    if (local_photo) {
                        SelectPhotoDialog.showPhotoSelecterAll(TaskitemPhotographyNextYActivity.this, takeListener,
                                pickListener, photoPickListener).goneItem2();
                    } else {
                        takeListener.onClick(null);
                    }
                }
                isCheck = false;
                return;
            }

        }.execute();

    }

    protected void onDestroy() {
        super.onDestroy();
        if (mSearch != null) {
            mSearch.destroy();
            mSearch = null;
        }
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
        DataUploadDialog.dissmisDialog();
        unregisterReceiver(this);
    }
}
