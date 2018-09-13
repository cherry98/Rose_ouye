package com.orange.oy.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.orange.oy.R;
import com.orange.oy.activity.scan.ScanTaskNewActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.NewConfirmDialog;
import com.orange.oy.info.TaskNewInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.FileCache;
import com.orange.oy.util.SurfaceControl;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.CollapsibleTextView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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
 * 任务列表-地图定位
 */
public class TaskitemMapActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {

    private void initTitle() {
        AppTitle taskitemmap_title = (AppTitle) findViewById(R.id.taskitemmap_title);
        if (index != null && "0".equals(index)) {
            taskitemmap_title.settingName("定位任务（预览）");
        } else {
            taskitemmap_title.settingName("定位任务");
        }
        if (!"1".equals(newtask)) {//不是新手
            taskitemmap_title.showBack(this);
        }
        if ("1".equals(is_desc)) {
            taskitemmap_title.setIllustrate(new AppTitle.OnExitClickForAppTitle() {
                public void onExit() {
                    Intent intent = new Intent(TaskitemMapActivity.this, StoreDescActivity.class);
                    intent.putExtra("id", store_id);
                    intent.putExtra("store_name", store_name);
                    intent.putExtra("is_task", true);
                    startActivity(intent);
                }
            });
        }
    }

    public void onBack() {
        baseFinish();
    }

    @Override
    public void onBackPressed() {
        if (!"1".equals(newtask)) {
            super.onBackPressed();
        }
    }

    protected void onStop() {
        super.onStop();
        if (Addlocationtask != null) {
            Addlocationtask.stop(Urls.Addlocationtask);
        }
        if (selectprojectrw != null) {
            selectprojectrw.stop(Urls.Selectprojectrw);
        }
        NewConfirmDialog.dissmisDialog();
    }

    private String task_pack_id, task_id, store_id, store_name, task_pack_name, task_name, is_desc;
    private String newtask;//判断是否是新手任务 1是0否

    private void initNetworkConnection() {
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
        Addlocationtask = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("user_mobile", AppInfo.getName(TaskitemMapActivity.this));
                params.put("task_pack_id", task_pack_id);
                params.put("task_id", task_id);
                params.put("storeid", store_id);
                params.put("address", province + " " + city + " " + address);
                params.put("longitude", location_longitude + "");
                params.put("latitude", location_latitude + "");
                params.put("category1", category1);
                params.put("category2", category2);
                params.put("category3", category3);
                params.put("batch", batch);
                params.put("outlet_batch", outlet_batch);
                params.put("p_batch", p_batch);
                return params;
            }
        };
        Addlocationtask.setIsShowDialog(true);
    }

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private NetworkConnection selectprojectrw, Addlocationtask;
    private TextView taskitemmap_name;
    private CollapsibleTextView taskitemmap_value;
    private String category1 = "", category2 = "", category3 = "";
    private String categoryPath;
    private String project_id, project_name;
    private String batch;
    private String codeStr, brand;
    private String outlet_batch, p_batch, project_type;
    private String index;//扫码任务预览

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitemmap);
        initNetworkConnection();
        updataDBHelper = new UpdataDBHelper(this);
        Intent data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        index = data.getStringExtra("index");
        project_type = data.getStringExtra("project_type");
        if (project_type == null) {
            project_type = "";
        }
        project_id = data.getStringExtra("project_id");
        project_name = data.getStringExtra("project_name");
        task_pack_id = data.getStringExtra("task_pack_id");
        task_pack_name = data.getStringExtra("task_pack_name");
        task_id = data.getStringExtra("task_id");
        task_name = data.getStringExtra("task_name");
        store_id = data.getStringExtra("store_id");
        store_name = data.getStringExtra("store_name");
        store_num = data.getStringExtra("store_num");
        category1 = data.getStringExtra("category1");
        category2 = data.getStringExtra("category2");
        category3 = data.getStringExtra("category3");
        is_desc = data.getStringExtra("is_desc");
        codeStr = data.getStringExtra("code");
        brand = data.getStringExtra("brand");
        outlet_batch = data.getStringExtra("outlet_batch");
        p_batch = data.getStringExtra("p_batch");
        newtask = data.getStringExtra("newtask");
        initTitle();
        if (index != null && "0".equals(index)) {
            findViewById(R.id.taskitemmap_local).setVisibility(View.GONE);
        } else {
            findViewById(R.id.taskitemmap_local).setVisibility(View.VISIBLE);
        }
        categoryPath = Tools.toByte(category1 + category2 + category3 + project_id);
        taskitemmap_name = (TextView) findViewById(R.id.taskitemmap_name);
        taskitemmap_value = (CollapsibleTextView) findViewById(R.id.taskitemmap_value);
        initMap();
        findViewById(R.id.taskitemmap_local).setOnClickListener(this);
        getData();
        checkPermission();

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        switch (requestCode) {
            case AppInfo.REQUEST_CODE_ASK_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                } else {
                    Tools.showToast(TaskitemMapActivity.this, "定位权限获取失败");
                    AppInfo.setOpenLocation(TaskitemMapActivity.this, false);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void sendData(final String path) {
        if (TextUtils.isEmpty(batch) || batch.equals("null")) {
            batch = "1";
        }
        Addlocationtask.sendPostRequest(Urls.Addlocationtask, new Response.Listener<String>() {
            public void onResponse(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200 || code == 2) {
                        String username = AppInfo.getName(TaskitemMapActivity.this);
                        String executeid = jsonObject.getString("executeid");
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("user_mobile", username);
                        map.put("task_pack_id", task_pack_id);
                        map.put("task_id", task_id);
                        map.put("storeid", store_id);
                        map.put("address", province + " " + city + " " + address);
                        map.put("longitude", location_longitude + "");
                        map.put("latitude", location_latitude + "");
                        map.put("executeid", executeid);
                        map.put("category1", category1);
                        map.put("category2", category2);
                        map.put("category3", category3);
                        map.put("outlet_batch", outlet_batch);
                        map.put("p_batch", p_batch);
                        map.put("batch", batch);
                        String key = "img1";
                        updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, brand,
                                store_id, store_name, task_pack_id,
                                task_pack_name, "4", task_id, task_name, category1, category2,
                                category3, username + project_id + store_id + task_pack_id +
                                        category1 + category2 + category3 + task_id
                                , Urls.Filecomplete, key, path, UpdataDBHelper.Updata_file_type_img, map, null,
                                true, Urls.Addlocationtask, paramsToString(), false);
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
                        CustomProgressDialog.Dissmiss();
                        if (code == 2) {
                            NewConfirmDialog.showDialog(TaskitemMapActivity.this, null, jsonObject.getString("msg"), null,
                                    "确定", null, true, new NewConfirmDialog.OnSystemDialogClickListener() {
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
                                            intent.setClass(TaskitemMapActivity.this, TaskitemPhotographyNextYActivity.class);
                                            startActivity(intent);
                                        } else if ("2".equals(type)) {//视频任务
                                            intent.setClass(TaskitemMapActivity.this, TaskitemShotActivity.class);
                                            startActivity(intent);
                                        } else if ("3".equals(type)) {//记录任务
                                            intent.setClass(TaskitemMapActivity.this, TaskitemEditActivity.class);
                                            startActivity(intent);
                                        } else if ("4".equals(type)) {//定位任务
                                            intent.setClass(TaskitemMapActivity.this, TaskitemMapActivity.class);
                                            startActivity(intent);
                                        } else if ("5".equals(type)) {//录音任务
                                            intent.setClass(TaskitemMapActivity.this, TaskitemRecodillustrateActivity.class);
                                            startActivity(intent);
                                        } else if ("6".equals(type)) {//扫码任务
                                            intent.setClass(TaskitemMapActivity.this, ScanTaskNewActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                }
                            }
                            baseFinish();
                        }
                    } else {
                        CustomProgressDialog.Dissmiss();
                        Tools.showToast(TaskitemMapActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    CustomProgressDialog.Dissmiss();
                    Tools.showToast(TaskitemMapActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskitemMapActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, "执行中...");
    }

    private String paramsToString() {
        Map<String, String> parames = new HashMap<>();
        parames.put("token", Tools.getToken());
        parames.put("user_mobile", AppInfo.getName(this));
        parames.put("task_pack_id", task_pack_id);
        parames.put("task_id", task_id);
        parames.put("storeid", store_id);
        parames.put("address", province + " " + city + " " + address);
        parames.put("longitude", location_longitude + "");
        parames.put("latitude", location_latitude + "");
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
                try {
                    data = data + "&" + key + "=" + URLEncoder.encode(parames.get(key).trim(), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    data = data + "&" + key + "=" + parames.get(key).trim();
                }
            }
        }
        return data;
    }

    private void getData() {
        selectprojectrw.sendPostRequest(Urls.Selectprojectrw, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        taskitemmap_name.setText(jsonObject.getString("taskname"));
                        String note = jsonObject.getString("note");
                        if ("null".equals(note)) {
                            note = "";
                        }
                        taskitemmap_value.setTextColor(note, TextView.BufferType.NORMAL, Color.parseColor("#A0A0A0"), 14);
                        taskpackid = jsonObject.getString("taskpackid");
                        storename = jsonObject.getString("storename");
                        if (store_num == null) {
                            store_num = jsonObject.getString("storenum");
                        }
                        province = jsonObject.getString("province");
                        city = jsonObject.getString("city");
                        address = jsonObject.getString("address");
                        storeid = jsonObject.getString("storeid");
                        batch = jsonObject.getString("batch");

                        if (index != null && "0".equals(index)) {

                        } else {
                            initLocation();
                        }

                    } else {
                        Tools.showToast(TaskitemMapActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskitemMapActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskitemMapActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taskitemmap_local: {
                initLocation();
            }
            break;
        }
    }

    private UpdataDBHelper updataDBHelper;

    private void screenshot() {
//        GlobalScreenshot screenshot = new GlobalScreenshot(this);
//        screenshot.setOnSaveFinishListener(new GlobalScreenshot.OnSaveFinishListener() {
//            public void finish(String path) {
//                sendData(path);
//            }
//        });
//        screenshot.takeScreenshot(ConfirmDialog.getDialogView(), AppInfo.getName(this) + "/" + store_id + isstore +
//                task_pack_id + task_id, new Runnable() {
//            public void run() {
//            }
//        }, true, true);
        Tools.showToast(this, "截图");
        mBaiduMap.snapshotScope(null, new BaiduMap.SnapshotReadyCallback() {
            public void onSnapshotReady(Bitmap bitmap) {
                if (bitmap != null) {
//                    Bitmap bitmap2 = SurfaceControl.screenshot(ConfirmDialog.getDialogView());
//                    int b1w = bitmap.getWidth();
//                    int b1h = bitmap.getHeight();
//                    int b2w = bitmap2.getWidth();
//                    int b2h = bitmap2.getHeight();
//                    int bgwidth = (b1w > b2w) ? b1w : b2w;
//                    int bgheight = b1h + b2h;
//                    Bitmap newbmp = Bitmap.createBitmap(bgwidth, bgheight, Bitmap.Config.ARGB_8888);
//                    Canvas cv = new Canvas(newbmp);
//                    cv.drawBitmap(bitmap2, 0, 0, null);
//                    cv.drawBitmap(bitmap, 0, b2h, null);
//                    cv.save(Canvas.ALL_SAVE_FLAG);
//                    cv.restore();//存储
                    new screenshotAsyncTask(NewConfirmDialog.getDialogView(), bitmap).executeOnExecutor(Executors
                            .newCachedThreadPool());
                } else {
                    Tools.showToast(TaskitemMapActivity.this, "截图失败");
                }
            }
        });
    }

    //图像合成线程
    class screenshotAsyncTask extends AsyncTask {
        private View view;
        private Bitmap bitmap;

        screenshotAsyncTask(View view, Bitmap bitmap) {
            this.view = view;
            this.bitmap = bitmap;
        }

        protected void onPreExecute() {
            CustomProgressDialog.showProgressDialog(TaskitemMapActivity.this, "正在截图...");
        }

        protected Object doInBackground(Object[] params) {
            Bitmap bitmap2 = SurfaceControl.screenshot(view);
            int b1w = bitmap.getWidth();
            int b1h = bitmap.getHeight();
            int b2w = bitmap2.getWidth();
            int b2h = bitmap2.getHeight();
            int bgwidth = (b1w > b2w) ? b1w : b2w;
            int bgheight = b1h + b2h;
            Bitmap newbmp = Bitmap.createBitmap(bgwidth, bgheight, Bitmap.Config.ARGB_8888);
            Canvas cv = new Canvas(newbmp);
            cv.drawBitmap(bitmap2, 0, 0, null);
            cv.drawBitmap(bitmap, 0, b2h, null);
            cv.save(Canvas.ALL_SAVE_FLAG);
            cv.restore();//存储
            File file = null;
            try {
                File path = FileCache.getDirForPhoto(TaskitemMapActivity.this, AppInfo.getName(TaskitemMapActivity
                        .this) + "/" + project_id + store_id + task_pack_id + task_id + categoryPath);
                file = new File(path, Tools.getTimeSS() + Tools.getDeviceId(TaskitemMapActivity.this) + task_id + ".png");
                if (!path.exists()) {
                    path.mkdirs();
                }
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(file);
                newbmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
                Tools.d("save ok");
            } catch (IOException e) {
                file = null;
                MobclickAgent.reportError(TaskitemMapActivity.this,
                        "TaskitemMapActivity save photo error:" + e.getMessage());
                e.printStackTrace();
            }
            return file;
        }

        protected void onPostExecute(Object o) {
            CustomProgressDialog.Dissmiss();
            if (o != null && ((File) o).exists()) {
                sendData(((File) o).getPath());
            } else {
                Tools.showToast(TaskitemMapActivity.this, "截图失败");
            }
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
        }
    }

    private String taskpackid;
    private String store_num;
    private String storename;
    private String province;
    private String city;
    private String address;
    private String storeid;

    private void initMap() {
        // 地图初始化
        // 获取地图控件引用
        mMapView = (MapView) findViewById(R.id.taskitemmap_mapview);
        mMapView.showZoomControls(false);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode
                .NORMAL, true, null));
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode
                .NORMAL, true, null, getResources().getColor(R.color.aplumMapcolor), getResources().getColor(R.color.aplumMapcolor)));
//        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {//点击获取当前位置信息
//            public void onMapClick(LatLng latLng) {
//                if (mSearch == null) {
//                    mSearch = GeoCoder.newInstance();
//                    mSearch.setOnGetGeoCodeResultListener(onGetGeoCoderResultListener);
//                }
//                location_latitude = latLng.latitude;
//                location_longitude = latLng.longitude;
//                LatLng point = new LatLng(location_latitude, location_longitude);
//                MyLocationData locData = new MyLocationData.Builder().latitude(location_latitude).longitude
//                        (location_longitude).build();
//                mBaiduMap.setMyLocationData(locData);
//                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(point, 15);
//                mBaiduMap.setMapStatus(u);
//                mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(point));
//            }
//
//            public boolean onMapPoiClick(MapPoi mapPoi) {
//                return false;
//            }
//        });
    }

    private NewConfirmDialog.OnSystemDialogClickListener onSystemDialogClickListener = new NewConfirmDialog
            .OnSystemDialogClickListener() {
        public void leftClick(Object object) {
            NewConfirmDialog.dissmisDialog();
        }

        public void rightClick(Object object) {
            if (index != null && "0".equals(index)) {
                Tools.showToast(TaskitemMapActivity.this, "抱歉，预览时任务无法执行。");
                return;
            }
            screenshot();
        }
    };

    /**
     * 定位客户端
     */
    public LocationClient mLocationClient = null;
    public MyLocationListenner myListener = new MyLocationListenner();
    private GeoCoder mSearch = null;
    public static double location_latitude, location_longitude;
    private String location_myadr;

    /**
     * 初始化定位
     */
    private void initLocation() {
        if (mLocationClient != null && mLocationClient.isStarted()) {
            Tools.showToast(TaskitemMapActivity.this, "正在定位...");
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
//        option.setScanSpan(6000);
        mLocationClient.setLocOption(option);
        Tools.showToast(TaskitemMapActivity.this, "正在定位...");
    }

    private class MyLocationListenner implements BDLocationListener {
        public void onReceiveLocation(BDLocation location) {
            mLocationClient.stop();
            if (location == null) {
                Tools.showToast(TaskitemMapActivity.this, getResources().getString(R.string.location_fail));
                return;
            }
            location_latitude = location.getLatitude();
            location_longitude = location.getLongitude();
            location_myadr = location.getAddrStr();
            LatLng point = new LatLng(location_latitude, location_longitude);

  /*          // 构建Marker图标

            BitmapDescriptor bitmap = BitmapDescriptorFactory

                    .fromResource(R.mipmap.dwrw_icon_dingwei);

            // 构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);

            // 在地图上添加Marker，并显示
            mBaiduMap.addOverlay(option);
            //在地图上添加Marker，并显示
            MapStatus mMapStatus = new MapStatus.Builder().target(point).zoom(5)//图层大小
                    .build();
            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
            mBaiduMap.setMapStatus(mMapStatusUpdate);
            mBaiduMap.addOverlay(option);*/


            mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(point));
            MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius()).direction(100)
                    .latitude(location_latitude).longitude(location_longitude).build();
            mBaiduMap.setMyLocationData(locData);
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(point, 15);
            mBaiduMap.setMapStatus(u);
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
                String temp;
                if (!Tools.isEmpty(province) && !Tools.isEmpty(city) && !Tools.isEmpty(address)) {
                    if (province.equals(city) && city.equals(address)) {
                        temp = address;
                    } else if (province.equals(city) && !city.equals(address)) {
                        temp = city + " " + address;
                    } else if (!province.equals(city) && city.equals(address)) {
                        temp = province + " " + city;
                    } else {
                        temp = province + " " + city + " " + address;
                    }
                } else {
                    temp = address;
                }
                NewConfirmDialog.showDialogForMap(TaskitemMapActivity.this, null, project_type,
                        getResources().getString(R.string.taskitemmap_show),
                        store_num,
                        storename,
                        temp,
                        location_longitude + "",
                        location_latitude + "",
                        "获取街道信息失败",
                        "取消", "确定", null, true,
                        onSystemDialogClickListener);
            } else {
                String string;
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
                    string = addressComponent.city + addressComponent.district +
                            addressComponent.street + addressComponent.streetNumber;
                } else {
                    string = addressComponent.province + addressComponent.city +
                            addressComponent.district + addressComponent.street + addressComponent.streetNumber;
                }
                if ("1".equals(newtask)) {//新手任务
                    NewConfirmDialog.showDialog(TaskitemMapActivity.this, null, String.format(getResources().getString(R
                                    .string.taskitemmap_show2), string), "取消", "确定", null, true,
                            onSystemDialogClickListener);
                } else {
                    String temp;
                    if (!Tools.isEmpty(province) && !Tools.isEmpty(city) && !Tools.isEmpty(address)) {
                        if (province.equals(city) && city.equals(address)) {
                            temp = address;
                        } else if (province.equals(city) && !city.equals(address)) {
                            temp = city + " " + address;
                        } else if (!province.equals(city) && city.equals(address)) {
                            temp = province + " " + city;
                        } else {
                            temp = province + " " + city + " " + address;
                        }
                    } else {
                        temp = address;
                    }
                    NewConfirmDialog.showDialogForMap(TaskitemMapActivity.this, null, project_type, getResources().getString(R
                                    .string.taskitemmap_show),
                            store_num, storename,
                            temp,
                            location_longitude + "",
                            location_latitude + "", location_myadr,
                            "取消", "确定", null, true,
                            onSystemDialogClickListener);
                }
            }
        }
    };

    protected void onPause() {
        if (mMapView != null)
            mMapView.onPause();
        super.onPause();
    }

    protected void onResume() {
        if (mMapView != null)
            mMapView.onResume();
        super.onResume();
    }

    protected void onDestroy() {
        if (mBaiduMap != null)
            mBaiduMap.setMyLocationEnabled(false);
        if (mMapView != null) {
            try {
                mMapView.onDestroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mMapView = null;
        if (mLocationClient != null)
            mLocationClient.stop();
        if (mSearch != null) {
            mSearch.destroy();
        }
        super.onDestroy();
    }
}
