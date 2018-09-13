package com.orange.oy.activity.black;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
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
import com.orange.oy.info.BlackoutstoreInfo;
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
 * 定位任务（出店）
 */
public class OutSurveyMapActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {
    private void initTitle() {
        AppTitle taskitemmap_title = (AppTitle) findViewById(R.id.blackmap_title_outsurvey);
        taskitemmap_title.settingName("定位任务");
        taskitemmap_title.showBack(this);
    }

    private void initNetworkConnection() {
        blackMapFinish = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("taskid", task_id);
                params.put("storeid", store_id);
                params.put("taskbatch", taskbatch);
                params.put("batch", batch);
                params.put("address", province + " " + city + " " + address);
                params.put("longitude", location_longitude + "");
                params.put("latitude", location_latitude + "");
                params.put("usermobile", username);
                return params;
            }
        };
        blackMapFinish.setIsShowDialog(true);
    }

    protected void onStop() {
        super.onStop();
        if (blackMapFinish != null) {
            blackMapFinish.stop(Urls.BlackMapFinish);
        }
        ConfirmDialog.dissmisDialog();
    }

    private NetworkConnection blackMapFinish;
    private String username, task_id, store_id, batch, store_num, storename, project_id, categoryPath,
            project_name, task_name, taskbatch, note;
    private String province, city, address;
    private UpdataDBHelper updataDBHelper;
    private ArrayList<BlackoutstoreInfo> list;
    private BlackoutstoreInfo blackoutstoreInfo;
    private TextView blackmap_name_outsurvey;
    private CollapsibleTextView taskitemmap_value_outsurvey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_survey_map);
        initTitle();
        username = AppInfo.getName(this);
        initNetworkConnection();
        updataDBHelper = new UpdataDBHelper(this);
        list = (ArrayList<BlackoutstoreInfo>) getIntent().getBundleExtra("data").getSerializable("list");
        blackoutstoreInfo = list.remove(0);
        task_id = blackoutstoreInfo.getTaskid();
        store_id = blackoutstoreInfo.getStroeid();
        batch = blackoutstoreInfo.getBatch();
        store_num = blackoutstoreInfo.getStorenum();
        storename = blackoutstoreInfo.getStorename();
        project_id = blackoutstoreInfo.getProjectid();
        project_name = blackoutstoreInfo.getProjectname();
        task_name = blackoutstoreInfo.getTaskname();
        taskbatch = blackoutstoreInfo.getTaskbatch();
        province = blackoutstoreInfo.getProvince();
        city = blackoutstoreInfo.getCity();
        address = blackoutstoreInfo.getAddress();
        note = blackoutstoreInfo.getNote();
        categoryPath = project_id;
        blackmap_name_outsurvey = (TextView) findViewById(R.id.blackmap_name_outsurvey);
        blackmap_name_outsurvey.setText(task_name);
        taskitemmap_value_outsurvey = (CollapsibleTextView) findViewById(R.id.blackmap_value_outsurvey);
        taskitemmap_value_outsurvey.setDesc(note, TextView.BufferType.NORMAL);
        findViewById(R.id.blackmap_local_outsurvey).setOnClickListener(this);
        initMap();
        initLocation();
    }

    @Override
    public void onBack() {
        BlackDZXListActivity.isRefresh = true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void sendData(final String path) {
        if (TextUtils.isEmpty(batch) || batch.equals("null")) {
            batch = "1";
        }
        blackMapFinish.sendPostRequest(Urls.BlackMapFinish, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200 || code == 2) {
                        String executeid = jsonObject.getString("executeid");
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("taskid", task_id);
                        map.put("storeid", store_id);
                        map.put("taskbatch", taskbatch);
                        map.put("batch", batch);
                        map.put("executeid", executeid);
                        map.put("usermobile", username);
                        String key = "img1";
                        updataDBHelper.addUpdataTask(username, project_id, project_name, store_num, null,
                                store_id, storename, null, null, "4", task_id, task_name,
                                null, null, null, username + project_id + store_id + task_id
                                , Urls.BlackMapComplete, key, path, UpdataDBHelper.Updata_file_type_img, map, null,
                                true, Urls.BlackMapFinish, paramsToString(), true);
                        Intent service = new Intent("com.orange.oy.UpdataNewService");
                        service.setPackage("com.orange.oy");
                        startService(service);
                        if (list != null && !list.isEmpty()) {
                            String tasktype = list.get(0).getTasktype();
                            if (tasktype.equals("3")) {
                                Intent intent = new Intent(OutSurveyMapActivity.this, OutSurveyEditActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("list", list);
                                intent.putExtra("data", bundle);
                                startActivity(intent);
                            } else if (tasktype.equals("5")) {
                                Intent intent = new Intent(OutSurveyMapActivity.this, OutSurveyRecordillustrateActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("list", list);
                                intent.putExtra("data", bundle);
                                startActivity(intent);
                            } else if (tasktype.equals("4")) {
                                Intent intent = new Intent(OutSurveyMapActivity.this, OutSurveyMapActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("list", list);
                                intent.putExtra("data", bundle);
                                startActivity(intent);
                            } else if (tasktype.equals("1")) {//tasktype为1的时候是拍照任务
                                Intent intent = new Intent(OutSurveyMapActivity.this,
                                        OutSurveyTakephotoActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("list", list);
                                intent.putExtra("data", bundle);
                                intent.putExtra("tasktype", tasktype);
                                startActivity(intent);
                            } else if (tasktype.equals("8")) {//tasktype为1的时候是防翻拍-拍照任务
                                Intent intent = new Intent(OutSurveyMapActivity.this,
                                        OutSurveyTakephotoActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("list", list);
                                intent.putExtra("data", bundle);
                                intent.putExtra("tasktype", tasktype);
                                startActivity(intent);
                            }
                        } else {
                            BlackDZXListActivity.isRefresh = true;
                        }
                        if (code == 2) {
                            ConfirmDialog.showDialog(OutSurveyMapActivity.this, null, jsonObject.getString("msg"), null,
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
                            baseFinish();
                        }
                        CustomProgressDialog.Dissmiss();
                    } else {
                        CustomProgressDialog.Dissmiss();
                        Tools.showToast(OutSurveyMapActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    CustomProgressDialog.Dissmiss();
                    Tools.showToast(OutSurveyMapActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(OutSurveyMapActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, "执行中...");
    }

    private String paramsToString() {
        Map<String, String> parames = new HashMap<>();
        parames.put("taskid", task_id);
        parames.put("storeid", store_id);
        parames.put("taskbatch", taskbatch);
        parames.put("batch", batch);
        parames.put("address", province + " " + city + " " + address);
        parames.put("longitude", location_longitude + "");
        parames.put("latitude", location_latitude + "");
        parames.put("usermobile", username);
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

    class ScreenshotAsyncTask extends AsyncTask {
        private View view;
        private Bitmap bitmap;

        ScreenshotAsyncTask(View view, Bitmap bitmap) {
            this.view = view;
            this.bitmap = bitmap;
        }

        protected void onPreExecute() {
            CustomProgressDialog.showProgressDialog(OutSurveyMapActivity.this, "正在截图...");
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
                File path = FileCache.getDirForPhoto(OutSurveyMapActivity.this, username + "/" + project_id + store_id +
                        task_id + categoryPath);
                file = new File(path, Tools.getTimeSS() + Tools.getDeviceId(OutSurveyMapActivity.this) + task_id + ".png");
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
                MobclickAgent.reportError(OutSurveyMapActivity.this,
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
                Tools.showToast(OutSurveyMapActivity.this, "截图失败");
            }
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.blackmap_local_outsurvey) {
            initLocation();
        }
    }

    private BaiduMap mBaiduMap;
    private MapView mMapView;

    private void screenshot() {
        Tools.showToast(this, "截图");
        mBaiduMap.snapshotScope(null, new BaiduMap.SnapshotReadyCallback() {
            public void onSnapshotReady(Bitmap bitmap) {
                if (bitmap != null) {
                    new ScreenshotAsyncTask(ConfirmDialog.getDialogView(), bitmap).executeOnExecutor(Executors
                            .newCachedThreadPool());
                } else {
                    Tools.showToast(OutSurveyMapActivity.this, "截图失败");
                }
            }
        });
    }

    private void initMap() {
        // 地图初始化
        // 获取地图控件引用
        mMapView = (MapView) findViewById(R.id.blackmap_mapview_outsurvey);
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
            Tools.showToast(OutSurveyMapActivity.this, "正在定位...");
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

    private void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll");
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setIsNeedAddress(true);
//        option.setScanSpan(6000);
        mLocationClient.setLocOption(option);
        Tools.showToast(OutSurveyMapActivity.this, "正在定位...");
    }

    private class MyLocationListenner implements BDLocationListener {
        public void onReceiveLocation(BDLocation location) {
            mLocationClient.stop();
            if (location == null) {
                Tools.showToast(OutSurveyMapActivity.this, getResources().getString(R.string.location_fail));
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
                ConfirmDialog.showDialogForMap(OutSurveyMapActivity.this, null, String.format(getResources().getString(R
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
                ConfirmDialog.showDialogForMap(OutSurveyMapActivity.this, null, String.format(getResources().getString(R
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
