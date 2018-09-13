package com.orange.oy.activity.experience;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.service.RecordService;
import com.orange.oy.service.TimerService;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 体验项目---网点说明~~~~
 */
public class ExperienceillActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.experienceill_title);
        appTitle.settingName("网点");
        appTitle.showBack(this);
    }

    private void initNetworkConnection() {
        experienceOutlet = new NetworkConnection(this) {
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
                params.put("usermobile", AppInfo.getName(ExperienceillActivity.this));
                params.put("storeid", store_id);
                params.put("task_id", taskid);
                params.put("outlet_batch", outlet_batch);
                params.put("lon", longtitude2 + "");
                params.put("lat", latitude2 + "");
                params.put("address", addr);
                params.put("type", "1");//类型（1为开始体验，2为结束体验，传1或2）
                return params;
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (experienceOutlet != null) {
            experienceOutlet.stop(Urls.ExperienceOutlet);
        }
        if (experienceLocation != null) {
            experienceLocation.stop(Urls.ExperienceLocation);
        }
    }

    private String photourl, address, projectName, money;
    private ImageLoader imageLoader;
    private NetworkConnection experienceOutlet, experienceLocation;
    private double longtitude1, latitude1, longtitude2, latitude2;//1店铺 2定位
    private String addr, store_id, taskid, outlet_batch;
    private WebView experienceill_webview;
    private LocationClient locationClient = null;
    private BDLocationListener myListener = new MyLocationListener();
    private double distance;//店铺经纬度和定位经纬度距离
    private Intent data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experienceill);
        imageLoader = new ImageLoader(this);
        initTitle();
        data = getIntent();
        if (data == null) {
            return;
        }
        store_id = getIntent().getStringExtra("store_id");
        projectName = getIntent().getStringExtra("projectName");
        experienceill_webview = (WebView) findViewById(R.id.experienceill_webview);
//        experienceill_webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//        experienceill_webview.getSettings().setSupportMultipleWindows(true);
//        experienceill_webview.setWebViewClient(new WebViewClient());
//        experienceill_webview.setWebChromeClient(new WebChromeClient());
//        experienceill_webview.getSettings().setJavaScriptEnabled(true);
        String money_unit = data.getStringExtra("money_unit");
        if (money_unit != null) {
            ((TextView) findViewById(R.id.experienceill_money2)).setText(money_unit);
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            experienceill_webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        experienceill_webview.getSettings().setBlockNetworkImage(false);
        experienceill_webview.loadUrl(Urls.Outletdesc + "?token=" + Tools.getToken() + "&storeid=" + store_id);
        initNetworkConnection();
        getData();
        checkPermission();
        initLocation();
        findViewById(R.id.experienceill_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开启录音服务
                if (!isVoicePermission()) {
                    Tools.showToast(ExperienceillActivity.this, "录音无法正常启动，请检查权限设置！");
                    return;
                }
                AppInfo.saveStartTime(ExperienceillActivity.this, Tools.gettime());
                startService(new Intent(ExperienceillActivity.this, TimerService.class));
                Intent service = new Intent("com.orange.oy.recordservice").setPackage("com.orange.oy");
                service.putExtra("usermobile", AppInfo.getName(ExperienceillActivity.this));
                service.putExtra("project_id", data.getStringExtra("id"));
                service.putExtra("projectname", projectName);
                service.putExtra("store_name", data.getStringExtra("storeName"));
                service.putExtra("store_num", data.getStringExtra("storeNum"));
                service.putExtra("storeid", store_id);
                service.putExtra("dirName", AppInfo.getName(ExperienceillActivity.this) + "/" + store_id);
                service.putExtra("fileName", Tools.getTimeSS() + Tools.getDeviceId(ExperienceillActivity.this) +
                        store_id);
                service.putExtra("isOffline", false);
                service.putExtra("code", data.getStringExtra("code"));
                service.putExtra("brand", data.getStringExtra("brand"));
                service.putExtra("isExperience", true);
                startService(service);
                //判断定位
                judgeLocation();
            }
        });
    }

    private void getData() {
        experienceOutlet.sendPostRequest(Urls.ExperienceOutlet, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        photourl = jsonObject.getString("photourl");
                        address = jsonObject.getString("address");
                        taskid = jsonObject.getString("taskid");
                        outlet_batch = jsonObject.getString("outlet_batch");
                        money = jsonObject.getString("money");

                        longtitude1 = Double.parseDouble(jsonObject.getString("longtitude"));
                        latitude1 = Double.parseDouble(jsonObject.getString("latitude"));
                        ImageView experienceill_img = (ImageView) findViewById(R.id.experienceill_img);
                        imageLoader.DisplayImage(Urls.ImgIp + photourl, experienceill_img);
                        ((TextView) findViewById(R.id.experienceill_addr)).setText(address);
                        if (TextUtils.isEmpty(money)) {
                            money = "-";
                        } else {
                            double d = Tools.StringToDouble(money);
                            if (d - (int) d > 0) {
                                money = String.valueOf(d);
                            } else {
                                money = String.valueOf((int) d);
                            }
                        }
                        ((TextView) findViewById(R.id.experienceill_money)).setText(money);
                        ((TextView) findViewById(R.id.experienceill_name)).setText(jsonObject.getString("storeName"));
                    } else {
                        Tools.showToast(ExperienceillActivity.this, jsonObject.getString("msg"));
                    }
                    CustomProgressDialog.Dissmiss();
                } catch (JSONException e) {
                    Tools.showToast(ExperienceillActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(ExperienceillActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    /**
     * 根据经纬度判断是否到达店内1KM以内
     */
    private void judgeLocation() {
        if (distance <= 1000) {//调不拍照接口
            experiencelocation();
        } else {//弹窗口
            ConfirmDialog.showDialogForHint(ExperienceillActivity.this, "您未到达店铺附近，无法开始体验", "我已确认到店", new ConfirmDialog.OnSystemDialogClickListener() {
                @Override
                public void leftClick(Object object) {
                }

                @Override
                public void rightClick(Object object) {
                    //执行拍照任务
                    Intent intent = new Intent(ExperienceillActivity.this, ExperienceTakePhotoActivity.class);
                    intent.putExtra("outlet_batch", outlet_batch);
                    intent.putExtra("is_watermark", data.getIntExtra("is_watermark", 0));
                    intent.putExtra("photo_compression", data.getStringExtra("photo_compression"));
                    intent.putExtra("brand", data.getStringExtra("brand"));
                    intent.putExtra("store_id", data.getStringExtra("store_id"));
                    intent.putExtra("taskid", taskid);
                    intent.putExtra("id", data.getStringExtra("id"));
                    intent.putExtra("carrytype", "1");
                    intent.putExtra("storeName", data.getStringExtra("storeName"));
                    intent.putExtra("projectName", data.getStringExtra("projectName"));
                    intent.putExtra("storeNum", data.getStringExtra("storeNum"));
                    startActivity(intent);
                    baseFinish();
                }
            });
        }
    }

    private void experiencelocation() {
        experienceLocation.sendPostRequest(Urls.ExperienceLocation, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Intent intent = new Intent(ExperienceillActivity.this, ExperiencePointActivity.class);
                        intent.putExtra("outlet_batch", outlet_batch);
                        intent.putExtra("projectName", projectName);
                        intent.putExtra("projectid", data.getStringExtra("id"));
                        intent.putExtra("storeid", store_id);
                        intent.putExtra("packageid", "");
                        intent.putExtra("taskid", taskid);
                        intent.putExtra("storecode", data.getStringExtra("storeNum"));
                        intent.putExtra("photo_compression", data.getStringExtra("photo_compression"));
                        intent.putExtra("project_name", data.getStringExtra("project_name"));
                        intent.putExtra("brand", data.getStringExtra("brand"));
                        intent.putExtra("storeName", data.getStringExtra("storeName"));
                        startActivity(intent);
                        baseFinish();
                    } else {
                        Tools.showToast(ExperienceillActivity.this, jsonObject.getString("msg"));
                    }
                    CustomProgressDialog.Dissmiss();
                } catch (JSONException e) {
                    Tools.showToast(ExperienceillActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(ExperienceillActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
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

    @Override
    public void onBack() {
        baseFinish();
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
            Tools.showToast(ExperienceillActivity.this, "正在定位...");
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
                Tools.showToast(ExperienceillActivity.this, getResources().getString(R.string.location_fail));
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
                    Tools.showToast(ExperienceillActivity.this, "定位权限获取失败");
                    AppInfo.setOpenLocation(ExperienceillActivity.this, false);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
