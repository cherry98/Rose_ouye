package com.orange.oy.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
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
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.OfflineDBHelper;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.network.Urls;
import com.orange.oy.util.FileCache;
import com.orange.oy.util.SurfaceControl;
import com.orange.oy.view.AppTitle;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * 任务列表-地图定位
 */
public class OfflineTaskitemMapActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {
    private AppTitle taskitemmap_title;

    private void initTitle() {
        taskitemmap_title = (AppTitle) findViewById(R.id.taskitemmap_title);
        taskitemmap_title.settingName("定位任务");
        taskitemmap_title.showBack(this);
    }

    public void onBack() {
        baseFinish();
    }

    protected void onStop() {
        super.onStop();
    }

    private String task_pack_id, task_id, store_id, store_name, task_pack_name, task_name;

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private TextView taskitemmap_name, taskitemmap_value;
    private String category1 = "", category2 = "", category3 = "";
    private String categoryPath;
    private String project_id, project_name;
    private OfflineDBHelper offlineDBHelper;
    private String code, brand;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitemmap);
        offlineDBHelper = new OfflineDBHelper(this);
        initTitle();
        updataDBHelper = new UpdataDBHelper(this);
        Intent data = getIntent();
        if (data == null) {
            baseFinish();
            return;
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
        code = data.getStringExtra("code");
        brand = data.getStringExtra("brand");
        categoryPath = Tools.toByte(category1 + category2 + category3 + project_id);
        taskitemmap_name = (TextView) findViewById(R.id.taskitemmap_name);
        taskitemmap_value = (TextView) findViewById(R.id.taskitemmap_value);
        initMap();
        findViewById(R.id.taskitemmap_local).setOnClickListener(this);
        getData();
    }

    private void sendData(final String path) throws UnsupportedEncodingException {
        if (TextUtils.isEmpty(batch) || batch.equals("null")) {
            batch = "1";
        }
        String username = AppInfo.getName(OfflineTaskitemMapActivity.this);
        if (TextUtils.isEmpty(task_pack_id)) {
            offlineDBHelper.completedTask(username, project_id, store_id, task_id);
        } else {
            offlineDBHelper.insertOfflineCompleted(username, project_id, store_id, task_pack_id, task_id, category1,
                    category2, category3);
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_mobile", username);
        map.put("task_pack_id", task_pack_id);
        map.put("task_id", task_id);
        map.put("storeid", store_id);
        map.put("address", province + " " + city + " " + address);
        map.put("longitude", location_longitude + "");
        map.put("latitude", location_latitude + "");
        map.put("category1", category1);
        map.put("category2", category2);
        map.put("category3", category3);
        map.put("outlet_batch", outlet_batch);
        map.put("p_batch", p_batch);
        map.put("batch", batch);
        String key = "img1";
        updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, brand, store_id, store_name, task_pack_id,
                task_pack_name, "4", task_id, task_name, category1, category2,
                category3, username + project_id + store_id + task_pack_id + category1 + category2 + category3 + task_id
                , Urls.Filecomplete, key, path, UpdataDBHelper.Updata_file_type_img, map, null, true, Urls.Addlocationtask,
                paramsToString(), true);
        Intent service = new Intent("com.orange.oy.UpdataNewService");
        service.setPackage("com.orange.oy");
        startService(service);
        OfflinePackageActivity.isRefresh = true;
        OfflineTaskActivity.isRefresh = true;
        baseFinish();
    }

    private String batch;
    private String outlet_batch = null, p_batch = null;

    private void getData() {
        String username = AppInfo.getName(this);
        String result = offlineDBHelper.getTaskConetnt(username, project_id, store_id, task_pack_id, task_id);
        String result2 = offlineDBHelper.getTaskDetail(username, project_id, store_id, task_pack_id, task_id);
        outlet_batch = offlineDBHelper.getTaskOutletBatch(username, project_id, store_id, task_pack_id, task_id);
        p_batch = offlineDBHelper.getTaskPBatch(username, project_id, store_id, task_pack_id, task_id);
        try {
            JSONObject jsonObject = new JSONObject(result);
            taskitemmap_name.setText(jsonObject.getString("taskname"));
            String note = jsonObject.getString("note");
            if ("null".equals(note)) {
                note = "";
            }
            taskitemmap_value.setText(note);
            taskpackid = jsonObject.getString("taskpackid");
            storename = jsonObject.getString("storename");
            province = jsonObject.getString("province");
            city = jsonObject.getString("city");
            address = jsonObject.getString("address");
            storeid = jsonObject.getString("storeid");
            batch = new JSONObject(result2).getString("batch");
            initLocation();
        } catch (JSONException e) {
            Tools.showToast(this, "离线数据异常！");
            baseFinish();
        }
    }

    private String paramsToString() throws UnsupportedEncodingException {
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
                data = key + "=" + URLEncoder.encode(parames.get(key).trim(), "utf-8");
            } else {
                data = data + "&" + key + "=" + URLEncoder.encode(parames.get(key).trim(), "utf-8");
            }
        }
        return data;
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
                    new screenshotAsyncTask(ConfirmDialog.getDialogView(), bitmap).executeOnExecutor(Executors
                            .newCachedThreadPool());
                } else {
                    Tools.showToast(OfflineTaskitemMapActivity.this, "截图失败");
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
            CustomProgressDialog.showProgressDialog(OfflineTaskitemMapActivity.this, "正在截图...");
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
                File path = FileCache.getDirForPhoto(OfflineTaskitemMapActivity.this, AppInfo.getName(OfflineTaskitemMapActivity
                        .this) + "/" + project_id + store_id + task_pack_id + task_id + categoryPath);
                file = new File(path, Tools.getTimeSS() + Tools.getDeviceId(OfflineTaskitemMapActivity.this) + task_id + ".png");
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
                MobclickAgent.reportError(OfflineTaskitemMapActivity.this,
                        "OfflineTaskitemMapActivity save photo error:" + e.getMessage());
                e.printStackTrace();
            }
            return file;
        }

        protected void onPostExecute(Object o) {
            CustomProgressDialog.Dissmiss();
            if (o != null && ((File) o).exists()) {
                try {
                    if (outlet_batch != null && p_batch != null) {
                        sendData(((File) o).getPath());
                    } else {
                        Tools.showToast(OfflineTaskitemMapActivity.this, getResources().getString(R.string.batch_error));
                    }
                } catch (UnsupportedEncodingException e) {
                    Tools.showToast(OfflineTaskitemMapActivity.this, "存储失败，未知异常！");
                    MobclickAgent.reportError(OfflineTaskitemMapActivity.this, "offline map y:" + e.getMessage());
                }
            } else {
                Tools.showToast(OfflineTaskitemMapActivity.this, "截图失败");
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
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            public void onMapClick(LatLng latLng) {
                if (mSearch == null) {
                    mSearch = GeoCoder.newInstance();
                    mSearch.setOnGetGeoCodeResultListener(onGetGeoCoderResultListener);
                }
                location_latitude = latLng.latitude;
                location_longitude = latLng.longitude;
                LatLng point = new LatLng(location_latitude, location_longitude);
                MyLocationData locData = new MyLocationData.Builder().latitude(location_latitude).longitude
                        (location_longitude).build();
                mBaiduMap.setMyLocationData(locData);
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(point, 15);
                mBaiduMap.setMapStatus(u);
                mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(point));
            }

            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
    }

    private ConfirmDialog.OnSystemDialogClickListener onSystemDialogClickListener = new ConfirmDialog
            .OnSystemDialogClickListener() {
        public void leftClick(Object object) {
            ConfirmDialog.dissmisDialog();
        }

        public void rightClick(Object object) {
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

    /**
     * 初始化定位
     */
    private void initLocation() {
        if (mLocationClient != null && mLocationClient.isStarted()) {
            Tools.showToast(OfflineTaskitemMapActivity.this, "正在定位...");
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
        Tools.showToast(OfflineTaskitemMapActivity.this, "正在定位...");
    }

    public class MyLocationListenner implements BDLocationListener {
        public void onReceiveLocation(BDLocation location) {
            mLocationClient.stop();
            if (location == null) {
                Tools.showToast(OfflineTaskitemMapActivity.this, getResources().getString(R.string.location_fail));
                return;
            }
            location_latitude = location.getLatitude();
            location_longitude = location.getLongitude();
            LatLng point = new LatLng(location_latitude, location_longitude);
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
                ConfirmDialog.showDialogForMap(OfflineTaskitemMapActivity.this, null, String.format(getResources().getString(R
                                .string.taskitemmap_show), store_num, storename, province + " " + city + " " + address,
                        location_longitude + "", location_latitude + "", "获取街道信息失败"), "取消", "确定", null, true,
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
                ConfirmDialog.showDialogForMap(OfflineTaskitemMapActivity.this, null, String.format(getResources().getString(R
                                .string.taskitemmap_show), store_num, storename, province + " " + city + " " + address,
                        location_longitude + "", location_latitude + "", string), "取消", "确定", null, true,
                        onSystemDialogClickListener);
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
        if (mMapView != null)
            mMapView.onDestroy();
        mMapView = null;
        if (mLocationClient != null)
            mLocationClient.stop();
        if (mSearch != null) {
            mSearch.destroy();
        }
        super.onDestroy();
    }
}
