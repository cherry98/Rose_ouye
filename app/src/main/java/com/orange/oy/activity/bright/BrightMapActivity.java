package com.orange.oy.activity.bright;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.network.NetworkConnection;
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
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;

public class BrightMapActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle,
        View.OnClickListener {

    private void initTitle() {
        AppTitle taskitemmap_title = (AppTitle) findViewById(R.id.taskitemmap_title);
        taskitemmap_title.settingName("定位任务");
        taskitemmap_title.showBack(this);
    }

    private void initNetworkConnection() {
        selectprojectrw = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("task_pack_id", "");
                params.put("task_id", taskid);
                params.put("store_id", store_id);
                return params;
            }
        };
        selectprojectrw.setIsShowDialog(true);
        assistantTaskLocation = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(BrightMapActivity.this));
                params.put("executeid", executeid);
                params.put("taskbatch", batch);
                params.put("clienttime", date);
                params.put("address", province + " " + city + " " + address);
                params.put("longitude", location_longitude + "");
                params.put("latitude", location_latitude + "");
                return params;
            }
        };
    }

    private String project_id, project_name, codeStr, brand, store_num, store_id,
            batch, executeid, taskid, store_name, categoryPath, date;
    private NetworkConnection selectprojectrw, assistantTaskLocation;
    private TextView taskitemmap_name, taskitemmap_value;
    private String province;
    private String city;
    private String address;
    protected UpdataDBHelper updataDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskitemmap);
        initTitle();
        Intent data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        updataDBHelper = new UpdataDBHelper(this);
        initNetworkConnection();
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        date = sDateFormat.format(new java.util.Date());
        executeid = data.getIntExtra("executeid", 0) + "";
        taskid = data.getIntExtra("taskid", 0) + "";
        project_id = data.getStringExtra("project_id");
        project_name = data.getStringExtra("projectname");
        codeStr = data.getStringExtra("code");
        brand = data.getStringExtra("brand");
        store_num = data.getStringExtra("store_num");
        store_id = data.getStringExtra("outletid");
        store_name = data.getStringExtra("store_name");
        categoryPath = Tools.toByte(project_id);
        taskitemmap_name = (TextView) findViewById(R.id.taskitemmap_name);
        taskitemmap_value = (TextView) findViewById(R.id.taskitemmap_value);
        initMap();
        findViewById(R.id.taskitemmap_local).setOnClickListener(this);
        getData();
    }

    private String note, taskname;

    private void getData() {
        selectprojectrw.sendPostRequest(Urls.Selectprojectrw, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        taskname = jsonObject.getString("taskname");
                        taskitemmap_name.setText(taskname);
                        note = jsonObject.getString("note");
                        if ("null".equals(note)) {
                            note = "";
                        }
                        taskitemmap_value.setText(note);
                        province = jsonObject.getString("province");
                        city = jsonObject.getString("city");
                        address = jsonObject.getString("address");
                        batch = jsonObject.getString("batch");
                        initLocation();
                    } else {
                        Tools.showToast(BrightMapActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(BrightMapActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(BrightMapActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taskitemmap_local: {
                initLocation();
            }
            break;
        }
    }

    private void screenshot() {
        Tools.showToast(this, "截图");
        mBaiduMap.snapshotScope(null, new BaiduMap.SnapshotReadyCallback() {
            public void onSnapshotReady(Bitmap bitmap) {
                if (bitmap != null) {
                    new ScreenshotAsyncTask(ConfirmDialog.getDialogView(), bitmap).executeOnExecutor(Executors
                            .newCachedThreadPool());
                } else {
                    Tools.showToast(BrightMapActivity.this, "截图失败");
                }
            }
        });
    }

    //图像合成线程
    class ScreenshotAsyncTask extends AsyncTask {
        private View view;
        private Bitmap bitmap;

        ScreenshotAsyncTask(View view, Bitmap bitmap) {
            this.view = view;
            this.bitmap = bitmap;
        }

        protected void onPreExecute() {
            CustomProgressDialog.showProgressDialog(BrightMapActivity.this, "正在截图...");
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
                File path = FileCache.getDirForPhoto(BrightMapActivity.this, AppInfo.getName(BrightMapActivity
                        .this) + "/" + project_id + store_id + taskid + categoryPath);
                file = new File(path, Tools.getTimeSS() + Tools.getDeviceId(BrightMapActivity.this) + taskid + ".png");
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
                MobclickAgent.reportError(BrightMapActivity.this,
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
                Tools.showToast(BrightMapActivity.this, "截图失败");
            }
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
        }
    }

    private void sendData(final String path) {
        assistantTaskLocation.sendPostRequest(Urls.AssistantTaskLocation, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        String username = AppInfo.getName(BrightMapActivity.this);
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("usermobile", username);
                        map.put("executeid", executeid);
                        map.put("note", note);
                        String key = "img1";
                        updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, brand,
                                store_id, store_name, null,
                                null, "4-4", taskid, taskname, null, null,
                                null, username + project_id + store_id + taskid
                                , Urls.AssistantTaskComplete, key, path, UpdataDBHelper.Updata_file_type_img, map, null,
                                true, Urls.AssistantTaskLocation, paramsToString(), false);
                        Intent service = new Intent("com.orange.oy.UpdataNewService");
                        service.setPackage("com.orange.oy");
                        startService(service);
                        BrightBallotResultActivity.isRefresh = true;
                        BrightBallotActivity.isRefresh = true;
                        BrightPersonInfoActivity.isRefresh = true;
                        baseFinish();
                    } else {
                        Tools.showToast(BrightMapActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(BrightMapActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(BrightMapActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    private String paramsToString() {
        Map<String, String> parames = new HashMap<>();
        parames.put("usermobile", AppInfo.getName(BrightMapActivity.this));
        parames.put("executeid", executeid);
        parames.put("taskbatch", batch);
        parames.put("clienttime", date);
        parames.put("address", address);
        parames.put("longitude", location_longitude + "");
        parames.put("latitude", location_latitude + "");
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

    private MapView mMapView;
    private BaiduMap mBaiduMap;

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
            Tools.showToast(BrightMapActivity.this, "正在定位...");
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
        Tools.showToast(BrightMapActivity.this, "正在定位...");
    }

    private class MyLocationListenner implements BDLocationListener {
        public void onReceiveLocation(BDLocation location) {
            mLocationClient.stop();
            if (location == null) {
                Tools.showToast(BrightMapActivity.this, getResources().getString(R.string.location_fail));
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
                ConfirmDialog.showDialogForMap(BrightMapActivity.this, null, String.format(getResources().getString(R
                                .string.taskitemmap_show), store_num, store_name, province + " " + city + " " + address,
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
                ConfirmDialog.showDialogForMap(BrightMapActivity.this, null, String.format(getResources().getString(R
                                .string.taskitemmap_show), store_num, store_name, province + " " + city + " " + address,
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

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (assistantTaskLocation != null) {
            assistantTaskLocation.stop(Urls.AssistantTaskLocation);
        }
        if (selectprojectrw != null) {
            selectprojectrw.stop(Urls.Selectprojectrw);
        }
        ConfirmDialog.dissmisDialog();
    }
}
