package com.orange.oy.activity;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.scan.ScanTaskNewActivity;
import com.orange.oy.activity.scan.ScanTaskillustrateActivity;
import com.orange.oy.adapter.Video2Adapter;
import com.orange.oy.adapter.VideoAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.AppDBHelper;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.DataUploadDialog;
import com.orange.oy.info.TaskNewInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.Utility;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.CollapsibleTextView;
import com.orange.oy.view.MyGridView;
import com.orange.oy.view.MyImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.orange.oy.R.id.hour;
import static com.orange.oy.R.id.taskitemedit_desc;
import static com.orange.oy.R.id.taskitempgnexty_gridview1;
import static com.orange.oy.R.id.taskitemshot_text_reset;
import static com.orange.oy.base.Tools.getSDFreeSize;


/**
 * 任务列表-录制任务页
 */
public class TaskitemShotActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View
        .OnClickListener {

    private void initTitle() {
        AppTitle taskitemshot_title = (AppTitle) findViewById(R.id.taskitemshot_title);
        if (index != null && "0".equals(index)) {
            taskitemshot_title.settingName("视频任务（预览）");
        } else {
            taskitemshot_title.settingName("视频任务");
        }
        if (!"1".equals(newtask)) {//不是新手
            taskitemshot_title.showBack(this);
        }
        if ("1".equals(is_desc)) {
            taskitemshot_title.setIllustrate(new AppTitle.OnExitClickForAppTitle() {
                public void onExit() {
                    Intent intent = new Intent(TaskitemShotActivity.this, StoreDescActivity.class);
                    intent.putExtra("id", store_id);
                    intent.putExtra("store_name", store_name);
                    intent.putExtra("is_task", true);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if ("1".equals(newtask)) {
                return true;
            } else {
                if (!isBackEnable) {
                    returnTips();
                    //Tools.showToast(TaskitemShotActivity.this, "请提交资料，稍后返回");
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onBack() {
        if (isBackEnable) {
            baseFinish();
        } else {

            returnTips();
            //Tools.showToast(TaskitemShotActivity.this, "请提交资料，稍后返回");
        }
    }

    private void returnTips() {
        ConfirmDialog.showDialog(TaskitemShotActivity.this, "提示！", 3, "您的视频将会被清空。",
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

    protected void onStop() {
        super.onStop();
    }

    private void initNetworkConnection() {
        Selectvideo = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("task_pack_id", task_pack_id);
                params.put("task_id", task_id);
                params.put("token", Tools.getToken());
                return params;
            }
        };
        Selectvideo.setIsShowDialog(true);
        selectprojectrw = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("task_pack_id", task_pack_id);
                params.put("task_id", task_id);
                params.put("store_id", store_id);
                return params;
            }
        };
        selectprojectrw.setIsShowDialog(true);
        Videoup = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(TaskitemShotActivity.this));
                params.put("token", Tools.getToken());
                params.put("pid", task_pack_id);
                params.put("task_id", task_id);
                params.put("storeid", store_id);
                params.put("note", taskitemshot_edit.getText().toString().trim());
                params.put("category1", category1);
                params.put("category2", category2);
                params.put("category3", category3);
                params.put("batch", batch);
                params.put("outlet_batch", outlet_batch);
                params.put("p_batch", p_batch);
                params.put("flag", (isHad) ? "1" : "0");
                return params;
            }
        };
        Videoup.setIsShowDialog(true);
    }

    private MyImageView taskitemshot_video1;
    private ImageView taskitemshot_shotimg;
    private String task_pack_id, task_id, store_id, task_name, task_pack_name, store_name, project_id, project_name, store_num;
    private NetworkConnection Selectvideo, selectprojectrw, Videoup;
    private TextView taskitemshot_name, taskitemshot_video_title, tv_nowsave, tv_savetime, itemapplyone_runnow;
    private EditText taskitemshot_edit;
    private String category1 = "", category2 = "", category3 = "";
    private String categoryPath;
    private boolean isHad;
    private String is_desc;
    private String batch, codeStr, brand;
    private String outlet_batch, p_batch;
    private View taskitemshot_video_layout;
    //   private CollapsibleTextView taskitemshot_desc;
    private TextView taskitemshot_desc;  //// TODO: 2018/3/29 视频上面的话
    private ImageView spread_button;
    private Intent data;
    private String newtask;//判断是否是新手任务 1是0否
    private String index;//扫码任务预览
    private AppDBHelper appDBHelper;
    private boolean isBackEnable = true;//是否可返回上一页
    private ArrayList<String> uniqueList;//存储上传记录的唯一标识（用于页面返回的清空未上传记录）
    private String allsize;
    private LinearLayout lin_alls, lin_edit, lin_bottom;
    private MyGridView taskphoto_gridview;
    private Video2Adapter video2Adapter;

    private int totoll = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitemshot);
        uniqueList = new ArrayList<>();
        appDBHelper = new AppDBHelper(this);
        initNetworkConnection();
        updataDBHelper = new UpdataDBHelper(this);
        registerReceiver(this);
        data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        index = data.getStringExtra("index");
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
        is_desc = data.getStringExtra("is_desc");
        batch = data.getStringExtra("batch");
        codeStr = data.getStringExtra("code");
        brand = data.getStringExtra("brand");
        outlet_batch = data.getStringExtra("outlet_batch");
        p_batch = data.getStringExtra("p_batch");
        newtask = data.getStringExtra("newtask");
        appDBHelper.deletePhotoUrl(project_id, store_id, task_id);
        if (TextUtils.isEmpty(batch) || batch.equals("null")) {
            batch = "1";
        }
        initTitle();
        categoryPath = Tools.toByte(category1 + category2 + category3 + project_id);
        taskitemshot_video1 = (MyImageView) findViewById(R.id.taskitemshot_video1);
        taskitemshot_video1.setImageResource(R.mipmap.tianjiayuxi);
        taskitemshot_video1.setScaleType();
        taskitemshot_name = (TextView) findViewById(R.id.taskitemshot_name);
        taskitemshot_video_title = (TextView) findViewById(R.id.taskitemshot_video_title);
        taskitemshot_shotimg = (ImageView) findViewById(R.id.taskitemshot_shotimg);
        taskitemshot_name = (TextView) findViewById(R.id.taskitemshot_name);
        taskitemshot_edit = (EditText) findViewById(R.id.taskitemshot_edit);
        taskitemshot_video_layout = findViewById(R.id.taskitemshot_video_layout);
        taskphoto_gridview = (MyGridView) findViewById(R.id.taskphoto_gridview);
        taskitemshot_desc = (TextView) findViewById(R.id.taskitemshot_desc);
        spread_button = (ImageView) findViewById(R.id.spread_button);
        tv_nowsave = (TextView) findViewById(R.id.tv_nowsave);
        itemapplyone_runnow = (TextView) findViewById(R.id.itemapplyone_runnow);
        tv_savetime = (TextView) findViewById(R.id.tv_savetime);
        lin_alls = (LinearLayout) findViewById(R.id.lin_alls);
        lin_edit = (LinearLayout) findViewById(R.id.lin_edit);
        lin_bottom = (LinearLayout) findViewById(R.id.lin_bottom);
        taskitemshot_video1.setOnClickListener(this);
        findViewById(R.id.taskitemshot_button).setOnClickListener(this);
        findViewById(R.id.taskitemshot_shot_play).setOnClickListener(this);
        findViewById(R.id.taskitemshot_button2).setOnClickListener(this);
        findViewById(R.id.spread_button_layout).setOnClickListener(this);

        if (index != null && "0".equals(index)) {
            lin_alls.setVisibility(View.GONE);
            lin_edit.setVisibility(View.GONE);
            lin_bottom.setVisibility(View.GONE);
        } else {
            lin_alls.setVisibility(View.VISIBLE);
            lin_edit.setVisibility(View.VISIBLE);
            lin_bottom.setVisibility(View.VISIBLE);
        }
        Selectvideo();
        getAvailMemory();

        itemapplyone_runnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_nowsave.setVisibility(View.VISIBLE);
                tv_savetime.setVisibility(View.VISIBLE);


                Tools.d("tag", "getInternalToatalSpace  ：" + getSDFreeSize());  // 单位MB
                String AvailMemory = String.valueOf(getSDFreeSize());
                Tools.d("tag", "AvailMemory  ：" + AvailMemory);  // 单位MB
                if (getSDFreeSize() >= 1024) {
                    allsize = Tools.getSDFreeSize() / 1024 + "G";
                } else {
                    allsize = Tools.getSDFreeSize() + "MB";
                }
                tv_nowsave.setText("手机现有存储空间: " + allsize);
                //*****************预计可拍摄时长算法：手机现有空间-偶业待上传资料空间-预留100M / 50M = 可拍摄时长（分钟）

                String a = String.valueOf(getSDFreeSize());
                double b = Double.parseDouble(a);
                double save = (b - 100) / 50; //可拍摄时长
                // String times = String.valueOf(save);
                tv_savetime.setText("预计最长可拍摄：" + minConverDayHourMin(save));
            }
        });


    }

    /**
     * 分钟转化为天时分
     */
    public static String minConverDayHourMin(Double min) {
        String htm = "0";
        if (null != min) {
            Double m = min;
            String format;
            Object[] array;
            Integer days = (int) (m / (60 * 24));
            Integer hours = (int) (m / (60) - (days * 24));
            Integer minutes = (int) (m - hours * 60 - days * 60 * 24);
            if (days > 0) {
                format = "%1$,d天%2$,d时%3$,d分";
                array = new Object[]{days, hours, minutes};
            } else if (hour > 0) {
                format = "%1$,d时%2$,d分";
                array = new Object[]{hours, minutes};

            } else {
                format = "%1$,d分";
                array = new Object[]{minutes};
            }
            htm = String.format(format, array);
        }
        return htm;
    }

    public String setExeTime(String times) {
        String exeTime = "0分钟";
        if (!TextUtils.isEmpty(exeTime)) {
            int time = Tools.StringToInt(exeTime);
            if (time > 0) {
                if (time < 60) {
                    exeTime = time + "分";
                } else if (time < 1440 && time >= 60) {
                    int hour = time / 60;
                    int min = time % 60;
                    exeTime = hour + "时" + min + "分";
                } else if (time >= 1440) {
                    int day = time / 1440;
                    int hour = (time % 1440) / 60;
                    int min = time % 60;
                    exeTime = day + "天" + hour + "时" + min + "分";
                }
            }
        }
        return exeTime;
    }

    private boolean isSpread = false;//说明是否展开
    private String url;

    private void Selectvideo() {
        Selectvideo.sendPostRequest(Urls.Selectvideo, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        jsonObject = jsonObject.getJSONObject("datas");
                        taskitemshot_name.setText(jsonObject.getString("taskName"));
                        batch = jsonObject.getString("batch");
                        url = jsonObject.getString("url");
                        if (TextUtils.isEmpty(url) || url.equals("null")) {
                            taskitemshot_video_title.setVisibility(View.GONE);
                            taskitemshot_video_layout.setVisibility(View.GONE);
                            taskitemshot_shotimg.setTag("");
                        } else {
                            // taskitemshot_shotimg.setTag(jsonObject.getString("url"));
                            taskitemshot_video_title.setVisibility(View.VISIBLE);
                            String[] arr = url.split(",");
                            List list = Arrays.asList(arr);
                            video2Adapter = new Video2Adapter(TaskitemShotActivity.this, list);
                            taskphoto_gridview.setAdapter(video2Adapter);
                        }
                        String noteType = jsonObject.getString("noteType");
                        if ("1".equals(noteType)) {
                            findViewById(R.id.taskitemshot_edit).setVisibility(View.VISIBLE);
                            findViewById(R.id.text).setVisibility(View.VISIBLE);
                        } else {
                            findViewById(R.id.taskitemshot_edit).setVisibility(View.GONE);
                            findViewById(R.id.text).setVisibility(View.GONE);
                        }
                        // new getVideoThumbnail().execute(new Object[]{taskitemshot_shotimg.getTag()});
                        taskitemshot_desc.setText(jsonObject.getString("note"));
                        //  taskitemshot_desc.setText("该片讲述了一对男女在过年回家的火车上相识，从那之后，二人的命运便纠缠在一起，历经恋爱、分手、错过、重逢的故事该片讲述了一对男女在过年回家的火车上相识，从那之后，二人的命运便纠缠在一起，历经恋爱、分手、错过、重逢的故事该片讲述了一对男女在过年回家的火车上相识，从那之后，二人的命运便纠缠在一起，历经恋爱、分手、错过、重逢的故事该片讲述了一对男女在过年回家的火车上相识，从那之后，二人的命运便纠缠在一起，历经恋爱、分手、错过、重逢的故事");
                        Tools.d("tag", "taskitemshot_desc.getLineCount() ===>>" + taskitemshot_desc.getLineCount());
                        if (taskitemshot_desc.getLineCount() > 1) {
                            taskitemshot_desc.setSingleLine(true);
                            isSpread = false;
                            findViewById(R.id.spread_button_layout).setOnClickListener(TaskitemShotActivity.this);
                            findViewById(R.id.spread_button_layout).setVisibility(View.VISIBLE);
                            findViewById(R.id.taskitemshot_video_title).setVisibility(View.VISIBLE);
                            findViewById(R.id.taskitemshot_video_layout).setVisibility(View.GONE);
                            onClick(findViewById(R.id.spread_button_layout));
                            if (index != null && "0".equals(index)) {
                                onClick(findViewById(R.id.spread_button_layout));
                            }

                        } else {
                            findViewById(R.id.spread_button_layout).setVisibility(View.GONE);
                            findViewById(R.id.taskitemshot_video_title).setVisibility(View.VISIBLE);
                            findViewById(R.id.taskitemshot_video_layout).setVisibility(View.GONE);
                        }
                    } else {
                        Tools.showToast(TaskitemShotActivity.this, jsonObject.getString("msg"));
                    }
                    CustomProgressDialog.Dissmiss();
                } catch (JSONException e) {
                    CustomProgressDialog.Dissmiss();
                    Tools.showToast(TaskitemShotActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskitemShotActivity.this, getResources().getString(R.string.network_error));
            }
        });
    }

   /* class getVideoThumbnail extends AsyncTask {
        protected Object doInBackground(Object[] params) {
            try {
                String url = params[0].toString();
                return Tools.createVideoThumbnail(url, 1);
            } catch (Exception e) {
                return null;
            }
        }

        protected void onPostExecute(Object o) {
            if (o != null) {
                if (taskitemshot_shotimg != null) {
                    taskitemshot_shotimg.setImageBitmap((Bitmap) o);
                }
            }
        }
    }*/


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taskitemshot_video1: {
                if (index != null && "0".equals(index)) {
                    Tools.showToast(TaskitemShotActivity.this, "抱歉，预览时任务无法执行。");
                    return;
                }

                ConfirmDialog.showDialog(TaskitemShotActivity.this, "提示!", 2,
                        "为保证您的任务质量，请尽量不要接打电话(注：开启飞行模式并连接WIFI会增加任务审核通过率)",
                        "不设置", "去设置", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                            @Override
                            public void leftClick(Object object) {
                                Intent intent = new Intent(TaskitemShotActivity.this, ShotActivity.class);
                                intent.putExtra("index", 1);
                                intent.putExtra("fileName", Tools.getTimeSS() + Tools.getDeviceId(TaskitemShotActivity.this) + task_id);
                                intent.putExtra("dirName", AppInfo.getName(TaskitemShotActivity.this) + "/" + project_id + store_id + task_pack_id + task_id +
                                        categoryPath);
                                startActivityForResult(intent, AppInfo.TaskitemShotRequestCodeForShot);
                            }

                            @Override
                            public void rightClick(Object object) {
                                Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                                startActivity(intent);
                            }
                        });


            }
            break;
            case R.id.taskitemshot_button: {
                if (index != null && "0".equals(index)) {
                    Tools.showToast(TaskitemShotActivity.this, "抱歉，预览时任务无法执行。");
                    return;
                }
                if (taskitemshot_video1.getTag() != null) {
                    String tag = taskitemshot_video1.getTag().toString();
                    if (tag != null && !"null".equals(tag)) {
                        sendData();
                    } else {
                        Tools.showToast(TaskitemShotActivity.this, "视频录制失败，请重新录制！");
                    }
                } else {
                    Tools.showToast(TaskitemShotActivity.this, "请录制视频");
                }
            }
            break;
            case R.id.taskitemshot_button2: {
                data.setClass(this, TaskitemShotActivity.class);
                data.putExtra("isHad", false);
                data.putExtra("batch", batch);
                startActivity(data);
                baseFinish();
            }
            break;
            case R.id.taskitemshot_shot_play: {
                Intent intent = new Intent(this, VideoViewActivity.class);
                intent.putExtra("path", taskitemshot_shotimg.getTag().toString());
                startActivity(intent);
            }
            break;

            case R.id.spread_button_layout:
                //初始状态,  totoll == 1是展开
                if (totoll == 1) {
                    isSpread = true;
                    spread_button.setImageResource(R.mipmap.spread_button_up);
                    taskitemshot_desc.setSingleLine(false);
                    if (!TextUtils.isEmpty(taskitemshot_shotimg.getTag().toString())) {
                        findViewById(R.id.taskitemshot_video_title).setVisibility(View.VISIBLE);
                        findViewById(R.id.taskitemshot_video_layout).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.taskitemshot_video_title).setVisibility(View.GONE);
                        findViewById(R.id.taskitemshot_video_layout).setVisibility(View.GONE);
                    }
                    totoll = 2;
                } else {
                    //收缩
                    totoll = 1;
                    if (taskitemshot_desc.getLineCount() > 1) {
                        spread_button.setImageResource(R.mipmap.spread_button_down);
                        taskitemshot_desc.setSingleLine(true);
                        findViewById(R.id.taskitemshot_video_title).setVisibility(View.GONE);
                        findViewById(R.id.taskitemshot_video_layout).setVisibility(View.GONE);
                    }
                }

                break;
        }

    }

    private UpdataDBHelper updataDBHelper;

    private void sendData() {
        if (isComplete) {
            goStep();
            baseFinish();
            return;
        }
        if (!isLoading) {
            Videoup.sendPostRequest(Urls.Videoup, new Response.Listener<String>() {
                public void onResponse(String s) {
                    Tools.d(s);
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        int code = jsonObject.getInt("code");
                        if (code == 200 || code == 2) {
                            isLoading = true;
                            String executeid = jsonObject.getString("executeid");
                            String username = AppInfo.getName(TaskitemShotActivity.this);
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("user_mobile", username);
                            map.put("usermobile", username);
                            map.put("task_pack_id", task_pack_id);//特别修改
                            map.put("task_id", task_id);
                            map.put("token", Tools.getToken());
                            map.put("storeid", store_id);
                            map.put("note", taskitemshot_edit.getText().toString().trim());
                            map.put("executeid", executeid);
                            map.put("category1", category1);
                            map.put("category2", category2);
                            map.put("category3", category3);
                            map.put("outlet_batch", outlet_batch);
                            map.put("p_batch", p_batch);
                            map.put("batch", batch);
                            map.put("flag", (isHad) ? "1" : "0");

                            String uniquelyNum = username + project_id +
                                    store_id + task_pack_id + category1 + category2 + category3 + task_id;

                            updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, brand,
                                    store_id, store_name, task_pack_id,
                                    task_pack_name, "111", task_id, task_name, category1, category2, category3,
                                    uniquelyNum, Urls.Videocomplete,
                                    null, null, UpdataDBHelper.Updata_file_type_video,
                                    map, null, true, Urls.Videoup, paramsToString(), false);
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
//                            if (code == 2) {
//                                ConfirmDialog.showDialog(TaskitemShotActivity.this, null, jsonObject.getString("msg"), null,
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
                            String fileurl = appDBHelper.getAllPhotoUrl(username, project_id, store_id, task_id);
                            if (fileurl != null && (appDBHelper.getPhotoUrlIsCompete(fileurl, project_id, store_id, task_id))) {
                                goStep();
                                baseFinish();
                            } else {
                                selectUploadMode();
                            }
//                            }
                        } else {
                            Tools.showToast(TaskitemShotActivity.this, jsonObject.getString("msg"));
                        }
                    } catch (JSONException e) {
                        Tools.showToast(TaskitemShotActivity.this, getResources().getString(R.string.network_error));
                    }
                    CustomProgressDialog.Dissmiss();
                }
            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError volleyError) {
                    CustomProgressDialog.Dissmiss();
                    Tools.showToast(TaskitemShotActivity.this, getResources().getString(R.string.network_volleyerror));
                }
            }, "执行中...");
        } else {
            selectUploadMode();
        }
    }

    private void goStep() {
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
                        intent.setClass(TaskitemShotActivity.this, TaskitemPhotographyNextYActivity.class);
                        startActivity(intent);
                    } else if ("2".equals(type)) {//视频任务
                        intent.setClass(TaskitemShotActivity.this, TaskitemShotActivity.class);
                        startActivity(intent);
                    } else if ("3".equals(type)) {//记录任务
                        intent.setClass(TaskitemShotActivity.this, TaskitemEditActivity.class);
                        startActivity(intent);
                    } else if ("4".equals(type)) {//定位任务
                        intent.setClass(TaskitemShotActivity.this, TaskitemMapActivity.class);
                        startActivity(intent);
                    } else if ("5".equals(type)) {//录音任务
                        intent.setClass(TaskitemShotActivity.this, TaskitemRecodillustrateActivity.class);
                        startActivity(intent);
                    } else if ("6".equals(type)) {//扫码任务
                        intent.setClass(TaskitemShotActivity.this, ScanTaskNewActivity.class);
                        startActivity(intent);
                    }
                }
            }
        }
    }

    private void selectUploadMode() {
        if (!isComplete) {
            String mode = appDBHelper.getDataUploadMode(store_id);
            Tools.d("上传模式....." + mode);
            if ("1".equals(mode)) {//弹框选择==1
                DataUploadDialog.showDialog(TaskitemShotActivity.this, false, new DataUploadDialog.OnDataUploadClickListener() {
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
                DataUploadDialog.showDialog(TaskitemShotActivity.this, false, new DataUploadDialog.OnDataUploadClickListener() {
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
        parames.put("usermobile", AppInfo.getName(this));
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
                    isBackEnable = false;
                    taskitemshot_video1.setOnClickListener(null);
                    int index = data.getIntExtra("index", 0);
                    String path = data.getStringExtra("path");
                    if (index == 1) {
                        taskitemshot_video1.setAlpha(0.4f);
                        taskitemshot_video1.setmImageThumbnail(path);
                        taskitemshot_video1.setTag(path);
                        upDataVideo(path);
                    }
                }
            }
            break;
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
                String path = in.getStringExtra("path");
                if (taskitemshot_video1.getTag() != null) {
                    if (taskitemshot_video1.getTag().toString().equals(path)) {
                        String rate = in.getStringExtra("rate");
                        if ("0".equals(rate)) {
                            taskitemshot_video1.setText(rate + "%" + "\n等待上传");
                        } else if ("100".equals(rate)) {
                            taskitemshot_video1.setText(rate + "%" + "\n上传完成");
                        } else {
                            taskitemshot_video1.setText(rate + "%" + "\n正在上传");
                        }
                    }
                }
            } else if ("2".equals(type) && !isBackEnable) {//资料回收完成
                isComplete = true;
            }
        }
    };

    private void upDataVideo(String path) {
        String username = AppInfo.getName(TaskitemShotActivity.this);
        String uniquelyNum = username + project_id +
                store_id + task_pack_id + category1 + category2 + category3 + task_id + "1";
        uniqueList.add(uniquelyNum);
        updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, brand,
                store_id, store_name, task_pack_id,
                task_pack_name, "11", task_id, task_name, category1, category2, category3,
                uniquelyNum, null,
                "key", path, UpdataDBHelper.Updata_file_type_video,
                null, null, false, null, null, false);
        appDBHelper.addPhotoUrlRecord(username, project_id, store_id, task_id, path, null);
        appDBHelper.setFileNum(path, "1");
        Intent service = new Intent("com.orange.oy.UpdataNewService");
        service.setPackage("com.orange.oy");
        startService(service);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataUploadDialog.dissmisDialog();
        unregisterReceiver(this);
    }


    /**
     * 得到内置存储空间的总容量
     *
     * @param context
     * @return
     */
    public static String getInternalToatalSpace(Context context) {
        String path = Environment.getDataDirectory().getPath();
        Tools.d("tag", "root path is " + path);
        StatFs statFs = new StatFs(path);
        long blockSize = statFs.getBlockSize();
        long totalBlocks = statFs.getBlockCount();
        long availableBlocks = statFs.getAvailableBlocks();
        long useBlocks = totalBlocks - availableBlocks;

        long rom_length = totalBlocks * blockSize;
        Tools.d("tag", "rom_length======》》》》 " + Formatter.formatFileSize(context, rom_length));
        return Formatter.formatFileSize(context, rom_length);
    }

    // 获取android当前可用内存大小
    private String getAvailMemory() {

        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        //mi.availMem; 当前系统的可用内存
        return Formatter.formatFileSize(getBaseContext(), mi.availMem);// 将获取的内存大小规格化

    }

    //手机总内存
    private String getTotalMemory() {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

            arrayOfString = str2.split("\\s+");
            for (String num : arrayOfString) {
                Tools.d(str2, num + "\t");
            }

            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close();

        } catch (IOException e) {
        }
        return Formatter.formatFileSize(getBaseContext(), initial_memory);// Byte转换为KB或者MB，内存大小规格化
    }
}
