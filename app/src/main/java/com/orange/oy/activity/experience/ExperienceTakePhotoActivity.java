package com.orange.oy.activity.experience;

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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
import com.orange.oy.activity.AlbumActivity;
import com.orange.oy.activity.Camerase;
import com.orange.oy.activity.TaskitemPhotographyNextNActivity;
import com.orange.oy.adapter.GridImageAdapter;
import com.orange.oy.adapter.TaskitemReqPgAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.SelecterDialog;
import com.orange.oy.base.Tools;
import com.orange.oy.db.AppDBHelper;
import com.orange.oy.db.SystemDBHelper;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.DataUploadDialog;
import com.orange.oy.dialog.SelectPhotoDialog;
import com.orange.oy.info.TaskEditoptionsInfo;
import com.orange.oy.info.TaskQuestionInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.CollapsibleTextView;
import com.orange.oy.view.MyGridView;
import com.orange.oy.view.TaskCheckView;
import com.orange.oy.view.TaskRadioView;
import com.orange.oy.view.photoview.PhotoView;

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

/**
 * 体验项目中进店定位不准备的拍照任务
 */
public class ExperienceTakePhotoActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener, AppTitle.OnExitClickForAppTitle {

    private AppTitle taskitempgnext_title;

    private void initTitle() {
        taskitempgnext_title = (AppTitle) findViewById(R.id.taskitempgnexty_title);
        taskitempgnext_title.settingName("拍照任务");
        taskitempgnext_title.showBack(new AppTitle.OnBackClickForAppTitle() {
            @Override
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
        });
    }

    private void returnTips() {
        ConfirmDialog.showDialog(ExperienceTakePhotoActivity.this, "提示！", 3, "您已上传的照片会保留,未上传完成的照片照片将会被清空。",
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (isLoading) {
                selectUploadMode();
            } else if (!isBackEnable) {
                returnTips();
            }
        }
        return super.onKeyDown(keyCode, event);
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
        ExperienceTaskPhotoUp = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("task_id", taskid);
                params.put("user_mobile", AppInfo.getName(ExperienceTakePhotoActivity.this));
                params.put("storeid", store_id);
                params.put("token", Tools.getToken());
                params.put("batch", batch);
                params.put("outlet_batch", outlet_batch);
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
                params.put("lon", location_longitude + "");
                params.put("lat", location_latitude + "");
                params.put("address", address);
                params.put("type", carrytype);
                return params;
            }
        };
        ExperienceTaskPhotoUp.setIsShowDialog(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Takephoto != null) {
            Takephoto.stop(Urls.Takephoto);
        }
        if (ExperienceTaskPhotoUp != null) {
            ExperienceTaskPhotoUp.stop(Urls.ExperienceTaskPhotoUp);
        }
    }

    private NetworkConnection Takephoto, ExperienceTaskPhotoUp;
    private TextView taskitemedit_desc;
    private String id, num;
    private String isphoto, photo_type, taskid, task_pack_id = "", store_id, task_name, store_name,
            project_id, project_name, store_num, batch, outlet_batch;// photo_type 是否需要填写备注 0 不需要 1需要
    private int maxSelect, minSelect;
    private MyGridView taskitempgnexty_gridview1;
    private GridView checkreqpgnext_gridview;
    private ArrayList<String> picList = new ArrayList<>();
    private TaskitemReqPgAdapter adapter2;
    private EditText taskitempgnexty_edit;
    private ArrayList<String> selectImgList = new ArrayList<>();
    private ArrayList<String> originalImgList = new ArrayList<>();//原图路径
    private static int selectIndex;
    private GridImageAdapter adapter;
    private UpdataDBHelper updataDBHelper;
    private SystemDBHelper systemDBHelper;
    private LinearLayout taskitempgnexty_questionlayout;
    private ListView taskitempgnexty_bg2;//wu
    private Intent data;
    private int is_watermark;
    private String brand, photo_compression;
    private int gridViewItemHeigth;
    private String carrytype;//1为开始体验 2我已离店
    private ImageLoader imageLoader;
    private String address;
    private AppDBHelper appDBHelper;
    private boolean isBackEnable = true;//是否可返回上一页
    public static ExperienceTakePhotoActivity experienceTakePhotoActivity = null;
    private ArrayList<String> uniqueList;//存储上传记录的唯一标识（用于页面返回的清空未上传记录）
    private ImageView spread_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_take_photo);
        appDBHelper = new AppDBHelper(this);
        experienceTakePhotoActivity = this;
        data = getIntent();
        if (data == null) {
            return;
        }
        uniqueList = new ArrayList<>();
        updataDBHelper = new UpdataDBHelper(this);
        systemDBHelper = new SystemDBHelper(this);
        imageLoader = new ImageLoader(this);
        registerReceiver(this);
        outlet_batch = data.getStringExtra("outlet_batch");
        photo_compression = data.getStringExtra("photo_compression");
        is_watermark = data.getIntExtra("is_watermark", 0);
        brand = data.getStringExtra("brand");
        taskid = data.getStringExtra("taskid");
        store_id = data.getStringExtra("store_id");
        project_id = data.getStringExtra("id");
        carrytype = data.getStringExtra("carrytype");
        store_name = data.getStringExtra("storeName");
        project_name = data.getStringExtra("projectName");
        store_num = data.getStringExtra("storeNum");
        initTitle();
        initNetworkConnection();
        selectImgList.add("camera_default");
        spread_button = (ImageView) findViewById(R.id.spread_button);
        gridViewItemHeigth = (Tools.getScreeInfoWidth(this) - Tools.dipToPx(this, 60)) / 3 + Tools.dipToPx(this, 10);
        taskitemedit_desc = (TextView) findViewById(R.id.taskitempgnexty_desc);
        taskitempgnexty_gridview1 = (MyGridView) findViewById(R.id.taskitempgnexty_gridview1);
        taskitempgnexty_questionlayout = (LinearLayout) findViewById(R.id.taskitempgnexty_questionlayout);
        adapter2 = new TaskitemReqPgAdapter(this, picList);
        taskitempgnexty_gridview1.setAdapter(adapter2);
        taskitempgnexty_gridview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (imageLoader == null) {
                    imageLoader = new ImageLoader(ExperienceTakePhotoActivity.this);
                }
                PhotoView imageView = new PhotoView(ExperienceTakePhotoActivity.this);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageLoader.DisplayImage(picList.get(position), imageView);
                SelecterDialog.showView(ExperienceTakePhotoActivity.this, imageView);
            }
        });
        findViewById(R.id.taskitempgnexty_button).setOnClickListener(this);
        initLocation();
        checkPermission();
        getData();
    }

    private String picStr;//示例图片
    private boolean isSpread = false;//说明是否展开

    private void getData() {
        Takephoto.sendPostRequest(Urls.Takephoto, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200 || code == 100) {
                        task_name = jsonObject.getString("name");
                        ((TextView) findViewById(R.id.taskitempgnexty_name)).setText(task_name);
                        taskitemedit_desc.setText(jsonObject.getString("desc"));
                        if (taskitemedit_desc.getLineCount() > 1) {
                            taskitemedit_desc.setSingleLine(true);
                            isSpread = false;
                            findViewById(R.id.spread_button_layout).setOnClickListener(ExperienceTakePhotoActivity.this);
                            findViewById(R.id.spread_button_layout).setVisibility(View.VISIBLE);
                        } else {
                            findViewById(R.id.spread_button_layout).setVisibility(View.GONE);
                        }
                        num = jsonObject.getString("num");
                        String wuxiao = jsonObject.getString("wuxiao");
                        if ("1".equals(wuxiao)) {
                            taskitempgnext_title.settingExit("无法执行", getResources().getColor(R.color.homepage_select), ExperienceTakePhotoActivity.this);
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
                        findViewById(R.id.shili).setVisibility(View.GONE);
                        taskitempgnexty_gridview1.setVisibility(View.GONE);
                        if (TextUtils.isEmpty(picStr) || "null".equals(picStr)) {
                        } else {
                            findViewById(R.id.spread_button_layout).setOnClickListener(ExperienceTakePhotoActivity.this);
                            findViewById(R.id.spread_button_layout).setVisibility(View.VISIBLE);
                            String[] pics = picStr.split(",");
                            for (int i = 0; i < pics.length; i++) {
                                picList.add(Urls.ImgIp + pics[i].replaceAll("\"", "").replaceAll("\\\\", ""));
                            }
                            adapter2.notifyDataSetChanged();
                        }
                        checkreqpgnext_gridview = (GridView) findViewById(R.id.taskitempgnexty_gridview);
                        taskitempgnexty_edit = (EditText) findViewById(R.id.taskitempgnexty_edit);
                        adapter = new GridImageAdapter(ExperienceTakePhotoActivity.this, selectImgList);
                        checkreqpgnext_gridview.setAdapter(adapter);
                        checkreqpgnext_gridview.setOnItemClickListener(ExperienceTakePhotoActivity.this);
                        if ("1".equals(photo_type)) {
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
                        Tools.showToast(ExperienceTakePhotoActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Tools.showToast(ExperienceTakePhotoActivity.this, getResources().getString(R.string
                            .network_error));
                }
//                ArrayList<String> tempList = systemDBHelper.getPictureThumbnailForTask(AppInfo.getName
//                        (ExperienceTakePhotoActivity.this), project_id, store_id, task_pack_id, taskid);
//                if (tempList != null && !tempList.isEmpty()) {
//                    selectImgList.clear();
//                    selectImgList.addAll(tempList);
//                    if (selectImgList.size() < maxSelect) {
//                        selectImgList.add("camera_default");
//                    }
//                    refreshUI();
//                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(ExperienceTakePhotoActivity.this, getResources().getString(R.string
                        .network_volleyerror));
            }
        }, null);
    }

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

    private MyAdapter myAdapter;

    @Override
    public void onExit() {
        data.setClass(this, TaskitemPhotographyNextNActivity.class);
        data.putExtra("tasktype", "1");
        data.putExtra("photo_type", photo_type);
        data.putExtra("num", num);
        data.putExtra("isphoto", isphoto);
        data.putExtra("batch", batch);
        data.putExtra("is_watermark", is_watermark);
        data.putExtra("carrytype", carrytype);
        data.putExtra("task_name", task_name);
        data.putExtra("task_pack_id", "");
        data.putExtra("task_id", taskid);
        data.putExtra("store_id", store_id);
        data.putExtra("project_id", project_id);
        startActivity(data);
    }

    class MyAdapter extends BaseAdapter {

        public int getCount() {
            return selectImgList.size();
        }

        public Object getItem(int position) {
            return selectImgList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                convertView = Tools.loadLayout(ExperienceTakePhotoActivity.this, R.layout.view_checkreqpgnext_add);
                imageView = (ImageView) convertView.findViewById(R.id.view_checkreqpgnext_img);
                imageView.setOnClickListener(onClickListener);
                convertView.setTag(imageView);
            } else {
                imageView = (ImageView) convertView.getTag();
            }
            imageView.setTag(position);
            String path = selectImgList.get(position);
            if (path.equals("camera_default")) {
                imageView.setImageResource(R.mipmap.camera_default);
            } else {
                imageView.setImageBitmap(Tools.getBitmap(path, 200, 200));
            }
            return convertView;
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                selectIndex = Integer.parseInt(v.getTag().toString());
                if ("0".equals(isphoto)) {//不可用相册
                    takeListener.onClick(null);
                } else {
                    SelectPhotoDialog.showPhotoSelecter(ExperienceTakePhotoActivity.this, takeListener, pickListener);
                }
            } catch (NumberFormatException exception) {
                selectIndex = 0;
                Tools.showToast(ExperienceTakePhotoActivity.this, "应用异常");
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
            if ("1".equals(isrequired) && taskQuestionInfo == null) {//必填
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
            if ("1".equals(isrequired) && taskQuestionInfos.isEmpty()) {//必填
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

    private void sendData() {
        if (isComplete) {//资料回收完成
            goship();
            baseFinish();
            return;
        }
        if (!isLoading) {
            if (TextUtils.isEmpty(batch) || batch.equals("null")) {
                batch = "1";
            }
            ExperienceTaskPhotoUp.sendPostRequest(Urls.ExperienceTaskPhotoUp, new Response.Listener<String>() {
                public void onResponse(String s) {
                    Tools.d("体验定位拍照执行完成" + s);
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        if (jsonObject.getInt("code") == 200) {
                            isLoading = true;
                            String executeid = jsonObject.getString("executeid");
                            String username = AppInfo.getName(ExperienceTakePhotoActivity.this);
                            Map<String, String> params = new HashMap<>();
                            params.put("user_mobile", username);
                            params.put("executeid", executeid);
                            params.put("storeid", store_id);
                            params.put("outlet_batch", outlet_batch);
                            params.put("type", carrytype);
//                        }//8-8体验任务==定位不准确拍照任务
//                        Tools.d("定位拍照imgs：" + imgs);
                            updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, brand,
                                    store_id, store_name, task_pack_id,
                                    "", "111", taskid, task_name, null, null, null,
                                    username + project_id +
                                            store_id + task_pack_id + taskid,
                                    Urls.ExperienceFileComplete,
                                    null, null, UpdataDBHelper.Updata_file_type_img, params, photo_compression,
                                    true, Urls.ExperienceTaskPhotoUp, paramsToString(), false);
                            Intent service = new Intent("com.orange.oy.UpdataNewService");
                            service.setPackage("com.orange.oy");
                            startService(service);
                            String fileurl = appDBHelper.getAllPhotoUrl(username, project_id, store_id, taskid);
                            if (fileurl != null && (appDBHelper.getPhotoUrlIsCompete(fileurl, project_id, store_id, taskid))) {
                                goship();
                                baseFinish();
                            } else {
                                selectUploadMode();
                            }
                        } else {
                            Tools.showToast(ExperienceTakePhotoActivity.this, jsonObject.getString("msg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Tools.showToast(ExperienceTakePhotoActivity.this, getResources().getString(R.string
                                .network_error));
                    }
                    CustomProgressDialog.Dissmiss();
                }
            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError volleyError) {
                    CustomProgressDialog.Dissmiss();
                    Tools.showToast(ExperienceTakePhotoActivity.this, getResources().getString(R.string
                            .network_volleyerror));
                }
            }, null);
        } else {
            selectUploadMode();
        }
    }

    private void goship() {
        if ("1".equals(carrytype)) {//开始体验
            Intent intent = new Intent(ExperienceTakePhotoActivity.this, ExperiencePointActivity.class);
            intent.putExtra("projectid", project_id);
            intent.putExtra("storeid", store_id);
            intent.putExtra("packageid", task_pack_id);
            intent.putExtra("taskid", taskid);
            intent.putExtra("storecode", store_num);
            intent.putExtra("photo_compression", photo_compression);
            intent.putExtra("projectName", project_name);
            intent.putExtra("brand", brand);
            intent.putExtra("storeName", store_name);
            intent.putExtra("outlet_batch", outlet_batch);
            intent.putExtra("is_watermark", is_watermark);
            startActivity(intent);
        } else {//我已离店
            Intent intent = new Intent(ExperienceTakePhotoActivity.this, ExperienceEditActivity.class);
            intent.putExtra("project_id", project_id);
            intent.putExtra("project_name", project_name);
            intent.putExtra("task_pack_id", "");
            intent.putExtra("task_pack_name", "");
            intent.putExtra("task_id", getIntent().getStringExtra("record_taskid"));
            intent.putExtra("task_name", "");
            intent.putExtra("tasktype", "3");
            intent.putExtra("store_id", store_id);
            intent.putExtra("store_num", store_num);
            intent.putExtra("store_name", getIntent().getStringExtra("storeName"));
            intent.putExtra("category1", "");
            intent.putExtra("category2", "");
            intent.putExtra("category3", "");
            intent.putExtra("outlet_batch", getIntent().getStringExtra("outlet_batch"));
            intent.putExtra("brand", getIntent().getStringExtra("brand"));
            startActivity(intent);
        }
    }

    private boolean isClick = false;//是否勾选本店下所有任务

    private void selectUploadMode() {
        if (!isComplete) {
            String mode = appDBHelper.getDataUploadMode(store_id);
            Tools.d("上传模式....." + mode);
            if ("1".equals(mode)) {//弹框选择==1
                DataUploadDialog.showDialog(ExperienceTakePhotoActivity.this, false, new DataUploadDialog.OnDataUploadClickListener() {
                    @Override
                    public void firstClick() {
                        appDBHelper.addDataUploadRecord(store_id, "1");
                    }

                    @Override
                    public void secondClick() {
                        appDBHelper.addDataUploadRecord(store_id, "2");
                        goship();
                        baseFinish();
                    }

                    @Override
                    public void thirdClick() {
                        appDBHelper.addDataUploadRecord(store_id, "3");
                        goship();
                        baseFinish();
                    }
                });
            } else if ("2".equals(mode)) {//弹框选择===2
                DataUploadDialog.showDialog(ExperienceTakePhotoActivity.this, false, new DataUploadDialog.OnDataUploadClickListener() {
                    @Override
                    public void firstClick() {
                        appDBHelper.addDataUploadRecord(store_id, "1");
                    }

                    @Override
                    public void secondClick() {
                        appDBHelper.addDataUploadRecord(store_id, "2");
                        goship();
                        baseFinish();
                    }

                    @Override
                    public void thirdClick() {
                        appDBHelper.addDataUploadRecord(store_id, "3");
                        goship();
                        baseFinish();
                    }
                });
            } else if ("3".equals(mode)) {//直接关闭
                appDBHelper.addDataUploadRecord(store_id, "3");
                goship();
                baseFinish();
            }
        }
    }

    private String paramsToString() {
        Map<String, String> params = new HashMap<>();
        params.put("task_id", taskid);
        params.put("user_mobile", AppInfo.getName(ExperienceTakePhotoActivity.this));
        params.put("storeid", store_id);
        params.put("token", Tools.getToken());
        params.put("batch", batch);
        params.put("outlet_batch", outlet_batch);
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
        params.put("type", carrytype);
        params.put("lon", location_longitude + "");
        params.put("lat", location_latitude + "");
        params.put("address", address);
        String data = "";
        Iterator<String> iterator = params.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (TextUtils.isEmpty(data)) {
                try {
                    data = key + "=" + URLEncoder.encode(params.get(key).trim(), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    data = key + "=" + params.get(key).trim();
                }
            } else {
                try {
                    data = data + "&" + key + "=" + URLEncoder.encode(params.get(key).trim(), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    data = data + "&" + key + "=" + params.get(key).trim();
                }
            }
        }
        return data;
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
                if (!checkQuestion()) {//校验题目
                    answers = null;
                    notes = null;
                    return;
                }
                if (selectImgList == null || selectImgList.isEmpty() || selectImgList.get(0).equals("camera_default")) {
                    Tools.showToast(ExperienceTakePhotoActivity.this, "请拍照");
                    return;
                }
                int size = selectImgList.size();
                if (selectImgList.get(size - 1).contains("default")) {
                    size--;
                }
                if (size < minSelect) {
                    if (selectImgList.size() - 1 < minSelect) {
                        Tools.showToast(ExperienceTakePhotoActivity.this, "拍照数量不足" + minSelect + "张");
                        return;
                    }
                }
                sendData();
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
                Intent intent = new Intent(ExperienceTakePhotoActivity.this, Camerase.class);
                intent.putExtra("projectid", project_id);
                intent.putExtra("storeid", store_id);
                intent.putExtra("packageid", task_pack_id);
                intent.putExtra("taskid", taskid);
                intent.putExtra("storecode", store_num);
                intent.putExtra("maxTake", 1);
                intent.putExtra("state", 1);
                startActivityForResult(intent, TakeRequest);
            } else {
                Tools.showToast(ExperienceTakePhotoActivity.this, "已到拍照上限！");
            }
        }
    };

    //相册选取
    private View.OnClickListener pickListener = new View.OnClickListener() {
        public void onClick(View v) {
            SelectPhotoDialog.dissmisDialog();
            Intent intent = new Intent(ExperienceTakePhotoActivity.this, AlbumActivity.class);
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

    private void settingImgPath(String path) {
        File file = new File(path);
        if (!file.isFile()) {
            Tools.showToast(this, "拍照方式错误");
            return;
        }
        startZoomImageAsyncTask(path, "");
    }

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

        public zoomImageAsyncTask(String path) {
            this.path = path;
        }

        public zoomImageAsyncTask(boolean isAblum) {
            this.isAblum = isAblum;
        }

        protected void onPreExecute() {
            if (photo_compression.equals("-1")) {
                CustomProgressDialog.showProgressDialog(ExperienceTakePhotoActivity.this, "图片处理中...");
            } else {
                CustomProgressDialog.showProgressDialog(ExperienceTakePhotoActivity.this, "图片压缩中...");
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
                                        Tools.StringToInt(photo_compression)), tempFile.getPath(), oPath) != null) {
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
                                    Tools.StringToInt(photo_compression)), tempFile.getPath(), oPath) != null) {
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
                Tools.showToast(ExperienceTakePhotoActivity.this, msg);
                if (originalImgList != null) {
                    originalImgList.clear();
                }
                CustomProgressDialog.Dissmiss();
            } else {
                if (isAblum) {
                    String key = "", imgs = "";
                    int size = originalImgList.size();
                    String username = AppInfo.getName(ExperienceTakePhotoActivity.this);
                    for (int i = 0; i < size; i++) {
                        appDBHelper.addPhotoUrlRecord(username, project_id, store_id, taskid, originalImgList.get(i), selectImgList.get(i));
                        appDBHelper.setFileNum(originalImgList.get(i), originalImgList.size() + "");
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
                    String uniquelyNum = username + project_id + store_id + task_pack_id + taskid + size;
                    boolean isSuccess = updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, brand,
                            store_id, store_name, task_pack_id,
                            null, "11", taskid, task_name, null, null, null,
                            uniquelyNum, null, key, imgs, UpdataDBHelper.Updata_file_type_img, null, photo_compression,
                            false, null, null, false);
                    if (isSuccess) {
                        CustomProgressDialog.Dissmiss();
                    }
                } else {
                    String username = AppInfo.getName(ExperienceTakePhotoActivity.this);
                    String uniquelyNum = username + project_id + store_id + task_pack_id + taskid + originalImgList.size();
                    boolean isSuccess1 = updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, brand,
                            store_id, store_name, task_pack_id,
                            null, "11", taskid, task_name, null, null, null,
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

    private void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppInfo.BroadcastReceiver_TAKEPHOTO);
        context.registerReceiver(takePhotoBroadcastReceiver, filter);
    }

    private void unregisterReceiver(Context context) {
        context.unregisterReceiver(takePhotoBroadcastReceiver);
    }

    private boolean isComplete = false;
    private boolean isLoading = false;
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

    private void refreshUI() {
        if (taskitempgnexty_bg2 != null) {
            int size = selectImgList.size();
            if (size >= 0) {
                if (size < maxSelect && (size == 0 || !selectImgList.get(size - 1).equals("camera_default"))) {
                    selectImgList.add("camera_default");
                }
            } else {
                selectImgList.add("camera_default");
            }
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) taskitempgnexty_bg2.getLayoutParams();
            lp.height = (int) (selectImgList.size() * (getResources().getDimension(R.dimen.taskitemphotoY_item_height) +
                    Tools.dipToPx(ExperienceTakePhotoActivity.this, 10)));
            taskitempgnexty_bg2.setLayoutParams(lp);
            myAdapter.notifyDataSetChanged();
        } else {
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
        return file.length() > 51200;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TakeRequest: {
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
                case 0: {
                    isBackEnable = false;
                    Bundle bundle = data.getExtras();
                    ArrayList<String> tDataList = (ArrayList<String>) bundle.getSerializable("dataList");
                    startZoomImageAsyncTask(Tools.getTimeSS() + Tools.getDeviceId(ExperienceTakePhotoActivity.this) +
                            taskid, tDataList);
                }
                break;
            }
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

    private File saveBitmap(Bitmap bm, String tempPath, String oPath) throws FileNotFoundException,
            OutOfMemoryError {
        File returnvalue = null;
        FileOutputStream out = null;
        try {
            File f = new File(tempPath);
            out = new FileOutputStream(f);
            ExifInterface exif = null;
            int pointIndex = oPath.lastIndexOf(".");
            exif = new ExifInterface(oPath.substring(0, pointIndex) + "_2.ouye");
            if (is_watermark == 1 && !systemDBHelper.isBindForPicture(oPath)) {
                if (locationStr == null) {
                    locationStr = "";
                } else {
                    locationStr = "\n" + locationStr;
                }
                bm = addWatermark(bm, systemDBHelper.searchForWatermark(oPath));
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
            address = location.getAddrStr();
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
        int size = selectImgList.size();
        if (position < size) {
            selectIndex = position;
            if ("0".equals(isphoto)) {//不可用相册
                if ("camera_default".equals(selectImgList.get(position))) {
                    takeListener.onClick(null);
                }
            } else {
                SelectPhotoDialog.showPhotoSelecter(ExperienceTakePhotoActivity.this, takeListener, pickListener);
            }
        }
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
