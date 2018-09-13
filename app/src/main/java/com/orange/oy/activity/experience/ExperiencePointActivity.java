package com.orange.oy.activity.experience;

import android.Manifest;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.adapter.ExperiencePointAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.MyApplication;
import com.orange.oy.base.Tools;
import com.orange.oy.db.SystemDBHelper;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.ExperienceCommentInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.service.TimerService;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.FloatWindowView;
import com.orange.oy.view.MarqueeView;
import com.sobot.chat.SobotApi;
import com.sobot.chat.api.model.Information;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 体验项目---项目体验执行~~~
 */
public class ExperiencePointActivity extends BaseActivity implements View.OnClickListener {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private AppTitle appTitle;

    private void initTitle() {
        appTitle = (AppTitle) findViewById(R.id.epoint_title);
        appTitle.settingName("进店体验");
        appTitle.showBack(new AppTitle.OnBackClickForAppTitle() {
            @Override
            public void onBack() {
                ConfirmDialog.showDialog(ExperiencePointActivity.this, "返回首页后您已填写的内容将丢失，请谨慎操作", true, new ConfirmDialog.OnSystemDialogClickListener() {
                    @Override
                    public void leftClick(Object object) {

                    }

                    @Override
                    public void rightClick(Object object) {
                        stopService(new Intent("com.orange.oy.recordservice").setPackage("com.orange.oy"));
                        stopService(new Intent(ExperiencePointActivity.this, TimerService.class));
                        baseFinish();
                    }
                });
            }
        }, "首页");
        appTitle.showIllustrate(R.mipmap.kefu, new AppTitle.OnExitClickForAppTitle() {
            @Override
            public void onExit() {
                Information info = new Information();
                info.setAppkey(Urls.ZHICHI_KEY);
                info.setColor("#FFFFFF");
                if (TextUtils.isEmpty(AppInfo.getKey(ExperiencePointActivity.this))) {
                    info.setUname("游客");
                } else {
                    String netHeadPath = AppInfo.getUserImagurl(ExperiencePointActivity.this);
                    info.setFace(netHeadPath);
                    info.setUid(AppInfo.getKey(ExperiencePointActivity.this));
                    info.setUname(AppInfo.getUserName(ExperiencePointActivity.this));
                }
                SobotApi.startSobotChat(ExperiencePointActivity.this, info);
            }
        });
    }

    @Override
    public void onBackPressed() {
        ConfirmDialog.showDialog(ExperiencePointActivity.this, "返回首页后您已填写的内容将丢失，请谨慎操作", true, new ConfirmDialog.OnSystemDialogClickListener() {
            @Override
            public void leftClick(Object object) {

            }

            @Override
            public void rightClick(Object object) {
                stopService(new Intent("com.orange.oy.recordservice").setPackage("com.orange.oy"));
                stopService(new Intent(ExperiencePointActivity.this, TimerService.class));
                ExperiencePointActivity.super.onBackPressed();
            }
        });
    }

    private void initNetworkConnection() {
        experienceOutletInfo = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("storeid", store_id);
                return params;
            }
        };
        experienceLocation = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(ExperiencePointActivity.this));
                params.put("storeid", store_id);
                params.put("task_id", taskid);
                params.put("outlet_batch", outlet_batch);
                params.put("lon", longtitude2 + "");
                params.put("lat", latitude2 + "");
                params.put("address", addr);
                params.put("type", "2");//类型（1为开始体验，2为结束体验，传1或2）
                return params;
            }
        };
        fileNum = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("storeid", store_id);
                params.put("projectid", projectid);
                params.put("filetype", filetype);
                params.put("filenum", filenum + "");
                params.put("usermobile", AppInfo.getName(ExperiencePointActivity.this));
                return params;
            }
        };
    }

    private MarqueeView epoint_marqueeview;
    private PullToRefreshListView epoint_listview;
    private ExperiencePointAdapter experiencePointAdapter;
    private WindowManager wm = null;
    private WindowManager.LayoutParams wmParams = null;
    private FloatWindowView floatWindowView = null;
    private AlertDialog dialog;
    private NetworkConnection experienceOutletInfo, experienceLocation, fileNum;
    private String store_id;
    private ArrayList<ExperienceCommentInfo> list;
    private ArrayList<MarqueeInfo> marqueeInfos;
    private ImageView epoint_img;
    private TextView epoint_name, epoint_addr;
    private String projectid, packageid = "", taskid, storecode, projectName, record_taskid, outlet_batch;
    private SystemDBHelper systemDBHelper;
    private double longtitude1, latitude1, longtitude2, latitude2;//1店铺 2定位
    private LocationClient locationClient = null;
    private BDLocationListener myListener = new MyLocationListener();
    private double distance;//店铺经纬度和定位经纬度距离
    public static int recordLength;
    private String addr;
    private String filetype;
    private int filenum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_point);
        systemDBHelper = new SystemDBHelper(this);
        updataDBHelper = new UpdataDBHelper(this);
        list = new ArrayList<>();
        marqueeInfos = new ArrayList<>();
        dialog = new AlertDialog.Builder(this)
                .setTitle("悬浮窗权限管理")
                .setMessage("是否去开启悬浮窗权限？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //打开权限设置
                        openSetting();
                    }
                })
                .setNegativeButton("否", null)
                .create();

        //创建悬浮框
        checkPermission();
        initLocation();
        Intent data = getIntent();
        if (data == null) {
            return;
        }
        projectid = data.getStringExtra("projectid");
        projectName = data.getStringExtra("projectName");
        storecode = data.getStringExtra("storecode");
        store_id = getIntent().getStringExtra("storeid");
        outlet_batch = getIntent().getStringExtra("outlet_batch");
        initTitle();
        initNetworkConnection();
        initView();

        getData();
        experiencePointAdapter = new ExperiencePointAdapter(this, list);
        epoint_listview.setAdapter(experiencePointAdapter);
        findViewById(R.id.epoint_leave).setOnClickListener(this);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void createFloatView() {
        //开启悬浮窗前选请求权限
        if ("Xiaomi".equals(Build.MANUFACTURER)) {//小米手机
            requestPermission();
        } else if ("Meizu".equals(Build.MANUFACTURER)) {//魅族手机
            requestPermission();
        } else {//其他手机
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    startActivityForResult(intent, 12);
                } else {
                }
            } else {
            }
        }
        wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        wmParams = ((MyApplication) getApplication()).getMywmParams();
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        wmParams.format = PixelFormat.RGBA_8888;//设置图片格式，效果为背景透明
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.RIGHT | Gravity.TOP;
        float ratioWidth = (float) Tools.getScreeInfoWidth(this) / 720;
        float ratioHeight = (float) Tools.getScreeInfoHeight(this) / 1080;
        float ratioMetrics = Math.min(ratioWidth, ratioHeight);
        wmParams.x = (int) (67 * ratioMetrics);
        wmParams.y = (int) (360 * ratioMetrics);
        wmParams.width = (int) (100 * ratioMetrics);
        wmParams.height = (int) (100 * ratioMetrics);
        floatWindowView = new FloatWindowView(this);
        floatWindowView.setData(projectid, packageid, taskid, storecode, projectName, store_id);
        floatWindowView.setImageResource(R.mipmap.suspend_photo);
        wm.addView(floatWindowView, wmParams);
    }

    private void getData() {
        experienceOutletInfo.sendPostRequest(Urls.ExperienceOutletInfo, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (list == null) {
                            list = new ArrayList<ExperienceCommentInfo>();
                        } else {
                            list.clear();
                        }
                        if (marqueeInfos == null) {
                            marqueeInfos = new ArrayList<MarqueeInfo>();
                        } else {
                            marqueeInfos.clear();
                        }
                        ImageLoader imageLoader = new ImageLoader(ExperiencePointActivity.this);
                        imageLoader.DisplayImage(Urls.ImgIp + jsonObject.getString("photourl"), epoint_img);
                        record_taskid = jsonObject.getString("record_taskid");
                        taskid = jsonObject.getString("taskid");
                        epoint_addr.setText(jsonObject.getString("address"));
                        epoint_name.setText(jsonObject.getString("storeName"));
                        longtitude1 = Double.parseDouble(jsonObject.getString("longtitude"));
                        latitude1 = Double.parseDouble(jsonObject.getString("latitude"));
                        JSONArray jsonArray = jsonObject.optJSONArray("comments");
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                ExperienceCommentInfo experienceCommentInfo = new ExperienceCommentInfo();
                                JSONObject object = jsonArray.getJSONObject(i);
                                experienceCommentInfo.setComment(object.getString("comment"));
                                experienceCommentInfo.setDate(object.getString("date"));
                                experienceCommentInfo.setImgurl(object.getString("imgurl"));
                                experienceCommentInfo.setMultiselect(object.optJSONArray("multiselect"));
                                experienceCommentInfo.setPhotourl(object.optJSONArray("photourl"));
                                experienceCommentInfo.setScore(object.getString("score"));
                                experienceCommentInfo.setType(object.getString("type"));
                                list.add(experienceCommentInfo);
                            }
                        }
                        JSONArray jsonArray1 = jsonObject.optJSONArray("notices");
                        if (jsonArray1 != null) {
                            for (int i = 0; i < jsonArray1.length(); i++) {
                                JSONObject object = jsonArray1.getJSONObject(i);
                                MarqueeInfo marqueeInfo = new MarqueeInfo();
                                marqueeInfo.setMultiselect(object.optJSONArray("multiselect"));
                                marqueeInfo.setStorename(object.getString("storename"));
                                marqueeInfo.setUsermobile(object.getString("usermobile"));
                                marqueeInfos.add(marqueeInfo);
                            }
                        }
                        initMarqueeView();
                        createFloatView();
                        if (experiencePointAdapter != null) {
                            experiencePointAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Tools.showToast(ExperiencePointActivity.this, jsonObject.getString("msg"));
                    }
                    CustomProgressDialog.Dissmiss();
                } catch (JSONException e) {
                    Tools.showToast(ExperiencePointActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(ExperiencePointActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    // 请求用户给予悬浮窗的权限
    private void requestPermission() {
        if (isFloatWindowOpAllowed(this)) {//已经开启
        } else {
            dialog.show();
        }
    }

    //判断悬浮窗权限
    private boolean isFloatWindowOpAllowed(Context context) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            return checkOp(context, 24);
        } else {
            if ((context.getApplicationInfo().flags & 1 << 27) == 1 << 27) {
                return true;
            } else {
                return false;
            }
        }
    }

    private boolean checkOp(Context context, int op) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Class<?> spClazz = Class.forName(manager.getClass().getName());
                Method method = manager.getClass().getDeclaredMethod("checkOp", int.class, int.class, String.class);
                int property = (Integer) method.invoke(manager, op
                        , Binder.getCallingUid(), context.getPackageName());
                Tools.d(" property: " + property);
                if (AppOpsManager.MODE_ALLOWED == property) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Tools.d("Below API 19 cannot invoke!");
        }
        return false;
    }

    private void openSetting() {
        try {
            Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            localIntent.setClassName("com.miui.securitycenter",
                    "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
            localIntent.putExtra("extra_pkgname", getPackageName());
            startActivityForResult(localIntent, 11);
        } catch (ActivityNotFoundException e) {
            Intent intent1 = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent1.setData(uri);
            startActivityForResult(intent1, 11);
        }
    }

    private void initView() {
        epoint_marqueeview = (MarqueeView) findViewById(R.id.epoint_marqueeview);
        epoint_listview = (PullToRefreshListView) findViewById(R.id.epoint_listview);
        epoint_img = (ImageView) findViewById(R.id.epoint_img);
        epoint_name = (TextView) findViewById(R.id.epoint_name);
        epoint_addr = (TextView) findViewById(R.id.epoint_addr);
    }

    //跑马灯滚动
    private void initMarqueeView() {
        for (int i = 0; i < marqueeInfos.size(); i++) {
            TextView textView = new TextView(this);
            textView.setText(marqueeInfos.get(i).getUsermobile() + "刚刚体验了" + marqueeInfos.get(i).getStorename() + "店评价");
            epoint_marqueeview.addViewInQueue(textView);
            JSONArray jsonArray = marqueeInfos.get(i).getMultiselect();
            for (int j = 0; j < jsonArray.length(); j++) {
                TextView textView1 = new TextView(this);
                try {
                    textView1.setText(jsonArray.getString(j));
                    textView1.setTextSize(12);
                    textView1.setBackgroundResource(R.drawable.shape_item3);
                    textView1.setTextColor(getResources().getColor(R.color.experience_notselect));
                    textView1.setPadding(10, 5, 10, 5);
                    epoint_marqueeview.addViewInQueue(textView1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        epoint_marqueeview.setScrollSpeed(8);
        epoint_marqueeview.setScrollDirection(MarqueeView.RIGHT_TO_LEFT);
        epoint_marqueeview.setViewMargin(15);
        epoint_marqueeview.startScroll();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11) {
            if (isFloatWindowOpAllowed(this)) {//已经开启
            } else {
                Tools.showToast2(this, "开启悬浮窗失败");
            }
        } else if (requestCode == 12) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(this)) {
                    Tools.showToast2(this, "权限授予失败,无法开启悬浮窗");
                } else {
                }
            }
        } else if (requestCode == 13) {
            startUpload(data.getStringExtra("path"));//点击完成上传一次
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        wm.addView(floatWindowView, wmParams);
    }

    @Override
    protected void onStop() {
        if (floatWindowView != null) {
            wm.removeView(floatWindowView);
        }
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
        if (experienceOutletInfo != null) {
            experienceOutletInfo.stop(Urls.ExperienceOutletInfo);
        }
        if (experienceLocation != null) {
            experienceLocation.stop(Urls.ExperienceLocation);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (floatWindowView != null) {
            wm.removeView(floatWindowView);
        }
    }

    private ArrayList<String> originalImgList = new ArrayList<>();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.epoint_leave: {//上传需开启服务
                if (originalImgList.isEmpty()) {
                    Tools.showToast(ExperiencePointActivity.this, "请点击页面浮标相机图标在店内随意拍几张照片吧~");
                    return;
                }
                if (recordLength < 5 * 60) {
                    Tools.showToast(ExperiencePointActivity.this, "亲，您在店里的时间太短，再呆一会儿吧~");
                    return;
                }
                startMemory();
                judgeLocation();
            }
            break;
        }
    }


    public void startUpload(String paths) {//点击完成上传
        ArrayList<String> upLoadImgList = new ArrayList<>();
        String[] path = paths.split(",");
        for (int i = 0; i < path.length; i++) {
            String oPath = systemDBHelper.searchForOriginalpath(path[i]);
            File file = new File(oPath);
            if (file.exists() && file.isFile()) {
                upLoadImgList.add(oPath);
                originalImgList.add(oPath);
            }
        }
        String username = AppInfo.getName(this);
        String brand = getIntent().getStringExtra("brand");
        String store_name = getIntent().getStringExtra("storeName");
        int size;
        String imgs = "";
        String key = "";
        size = upLoadImgList.size();
        for (int i = 0; i < size; i++) {
            String path2 = upLoadImgList.get(i);
            if (path2.equals("camera_default")) {
                continue;
            }
            if (TextUtils.isEmpty(imgs)) {
                imgs = upLoadImgList.get(i);
            } else {
                imgs = imgs + "," + upLoadImgList.get(i);
            }
            if (TextUtils.isEmpty(key)) {
                key = "img" + (i + 1);
            } else {
                key = key + ",img" + (i + 1);
            }
        }
        Tools.d("连续拍照imgs：" + imgs);
        systemDBHelper.updataStateOPathTo3(originalImgList.toArray(new String[size]));
        updataDBHelper.addUpdataTask(username, projectid, projectName, storecode, brand,
                store_id, store_name, "", "", "typz", taskid, "体验拍照任务", "", "", "",
                username + projectid + store_id + taskid + "typz", null,
                key, imgs, UpdataDBHelper.Updata_file_type_img, null, null, false, null, null, false);
        Intent service = new Intent("com.orange.oy.UpdataNewService");
        service.setPackage("com.orange.oy");
        startService(service);
    }

    private UpdataDBHelper updataDBHelper;
    public static String TIME_CHANGED_ACTION = "com.yy.time.TIME_CHANGED_ACTION";//录音计时action

    private void startMemory() {
        stopService(new Intent("com.orange.oy.recordservice").setPackage("com.orange.oy"));
        stopService(new Intent(ExperiencePointActivity.this, TimerService.class));
        fileNum("3", originalImgList.size());
    }

    private void fileNum(final String filetype, int filenum) {
        this.filetype = filetype;
        this.filenum = filenum;
        fileNum.sendPostRequest(Urls.FileNum, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d("文件类型：" + filetype + "体验项目文件数量：" + s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if ("3".equals(filetype)) {
                            fileNum("2", 1);
                        }
                    } else {
                        Tools.showToast(ExperiencePointActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
    }

    private void experiencelocation() {
        experienceLocation.sendPostRequest(Urls.ExperienceLocation, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Intent intent = new Intent(ExperiencePointActivity.this, ExperienceEditActivity.class);
                        intent.putExtra("project_id", projectid);
                        intent.putExtra("project_name", projectName);
                        intent.putExtra("task_pack_id", "");
                        intent.putExtra("task_pack_name", "");
                        intent.putExtra("task_id", record_taskid);
                        intent.putExtra("task_name", "");
                        intent.putExtra("tasktype", "3");
                        intent.putExtra("store_id", store_id);
                        intent.putExtra("store_num", storecode);
                        intent.putExtra("store_name", getIntent().getStringExtra("storeName"));
                        intent.putExtra("category1", "");
                        intent.putExtra("category2", "");
                        intent.putExtra("category3", "");
                        intent.putExtra("outlet_batch", getIntent().getStringExtra("outlet_batch"));
                        intent.putExtra("brand", getIntent().getStringExtra("brand"));
                        startActivity(intent);
                        baseFinish();
                    } else {
                        Tools.showToast(ExperiencePointActivity.this, jsonObject.getString("msg"));
                    }
                    CustomProgressDialog.Dissmiss();
                } catch (JSONException e) {
                    Tools.showToast(ExperiencePointActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(ExperiencePointActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }


    private void judgeLocation() {
        if (distance >= 1000) {//是否调不拍照接口
            experiencelocation();
        } else {//弹窗口
            ConfirmDialog.showDialogForHint(ExperiencePointActivity.this, "您尚未离店，无法点击“我已离店”按钮", "我已确定离店", new ConfirmDialog.OnSystemDialogClickListener() {
                @Override
                public void leftClick(Object object) {
                }

                @Override
                public void rightClick(Object object) {
                    //执行拍照任务
                    Intent intent = new Intent(ExperiencePointActivity.this, ExperienceTakePhotoActivity.class);
                    intent.putExtra("outlet_batch", getIntent().getStringExtra("outlet_batch"));
                    intent.putExtra("is_watermark", getIntent().getIntExtra("is_watermark", 0));
                    intent.putExtra("photo_compression", getIntent().getStringExtra("photo_compression"));
                    intent.putExtra("brand", getIntent().getStringExtra("brand"));
                    intent.putExtra("store_id", store_id);
                    intent.putExtra("taskid", taskid);
                    intent.putExtra("id", projectid);
                    intent.putExtra("record_taskid", record_taskid);
                    intent.putExtra("carrytype", "2");
                    intent.putExtra("storeName", getIntent().getStringExtra("storeName"));
                    intent.putExtra("projectName", projectName);
                    intent.putExtra("storeNum", storecode);
                    startActivity(intent);
                    baseFinish();
                }
            });
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("ExperiencePoint Page") // TODO: Define a title for the content shown.
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

    class MarqueeInfo {

        /**
         * usermobile : null
         * storename : 6
         * multiselect : ["介绍细致","环境好","服务人员着装整齐"]
         */

        private String usermobile;
        private String storename;
        private JSONArray multiselect;

        public String getUsermobile() {
            return usermobile;
        }

        public void setUsermobile(String usermobile) {
            this.usermobile = usermobile;
        }

        public String getStorename() {
            return storename;
        }

        public void setStorename(String storename) {
            this.storename = storename;
        }

        public JSONArray getMultiselect() {
            return multiselect;
        }

        public void setMultiselect(JSONArray multiselect) {
            this.multiselect = multiselect;
        }

    }

    private static final double EARTH_RADIUS = 6378137.0;

    //返回单位是米
    public static double getDistance(double longitude1, double latitude1,
                                     double longitude2, double latitude2) {
        double Lat1 = rad(latitude1);
        double Lat2 = rad(latitude2);
        double a = Lat1 - Lat2;
        double b = rad(longitude1) - rad(longitude2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(Lat1) * Math.cos(Lat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }


    /**
     * 初始化定位
     */

    private void initLocation() {
        if (locationClient != null && locationClient.isStarted()) {
            Tools.showToast(ExperiencePointActivity.this, "正在定位...");
            return;
        }
        locationClient = new LocationClient(this);
        locationClient.registerLocationListener(myListener);
        setLocationOption();
        locationClient.start();
    }

    public void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(1000);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps
        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        locationClient.setLocOption(option);
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            locationClient.stop();
            if (bdLocation == null) {
                Tools.showToast(ExperiencePointActivity.this, getResources().getString(R.string.location_fail));
                return;
            }
            latitude2 = bdLocation.getLatitude();
            longtitude2 = bdLocation.getLongitude();
            addr = bdLocation.getAddrStr();
            Tools.d(latitude2 + "===" + longtitude2 + "===" + bdLocation.getAddrStr());
            Tools.d(getDistance(longtitude1, latitude1, longtitude2, latitude2) + "---===");
            distance = getDistance(longtitude1, latitude1, longtitude2, latitude2);
        }

        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, AppInfo
                        .REQUEST_CODE_ASK_LOCATION);
                return;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        switch (requestCode) {
            case AppInfo.REQUEST_CODE_ASK_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                } else {
                    Tools.showToast(ExperiencePointActivity.this, "定位权限获取失败");
                    AppInfo.setOpenLocation(ExperiencePointActivity.this, false);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
