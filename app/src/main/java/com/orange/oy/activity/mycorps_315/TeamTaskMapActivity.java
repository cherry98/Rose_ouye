package com.orange.oy.activity.mycorps_315;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.clusterutil.clustering.Cluster;
import com.orange.oy.clusterutil.clustering.ClusterManager;
import com.orange.oy.info.LocationInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.MapMarkView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/5/29.
 * 战队任务查看地图
 */

public class TeamTaskMapActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle,
        BaiduMap.OnMapLoadedCallback {
    public void initTile() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.tasklocation_title);
        appTitle.settingName("任务地图");
        appTitle.showBack(this);
    }

    public void initNetworkConnection() {
        OutletPackageMap = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(TeamTaskMapActivity.this));
                params.put("projectid", project_id);
                params.put("package_id", package_id);
                if (location_longitude != 0)
                    params.put("lon", location_longitude + "");
                if (location_latitude != 0)
                    params.put("lat", location_latitude + "");
                return params;
            }
        };
    }

    protected void onStop() {
        super.onStop();
        if (OutletPackageMap != null) {
            OutletPackageMap.stop(Urls.MapOutletList);
        }
    }

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private NetworkConnection OutletPackageMap;
    private ArrayList<LocationInfo> list = new ArrayList<>();
    private String package_id = "";
    private String project_id = "";
    private MyHandler myHandler = new MyHandler();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_location);
        initNetworkConnection();
        checkPermission();
        initTile();
        findViewById(R.id.tasklocation_location_layout).setVisibility(View.GONE);
        project_id = getIntent().getStringExtra("project_id");
        package_id = getIntent().getStringExtra("package_id");
        mMapView = (MapView) findViewById(R.id.tasklocation_mapview);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);//普通地图
        initLocation();
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

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        switch (requestCode) {
            case AppInfo.REQUEST_CODE_ASK_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                } else {
                    Tools.showToast(TeamTaskMapActivity.this, "定位权限获取失败");
                    AppInfo.setOpenLocation(TeamTaskMapActivity.this, false);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void onBack() {
        baseFinish();
    }

    ClusterManager<LocationInfo> mClusterManager;

    public void getData() {
        OutletPackageMap.sendPostRequest(Urls.OutletPackageMap, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (!list.isEmpty()) {
                        list.clear();
                    }
                    if (jsonObject.optInt("code") == 200) {
                        JSONArray projectList = jsonObject.getJSONArray("data");
                        int length = projectList.length();
                        JSONObject tempJson;
                        for (int i = 0; i < length; i++) {
                            tempJson = projectList.getJSONObject(i);
                            LocationInfo locationInfo4 = new LocationInfo();
                            locationInfo4.setLatitude(Double.parseDouble(tempJson.optString("latitude")));
                            locationInfo4.setLongtitude(Double.parseDouble(tempJson.optString("longtitude")));
                            MapMarkView mapMarkView = new MapMarkView(TeamTaskMapActivity.this, true);
                            locationInfo4.setBitmapDescriptor(BitmapDescriptorFactory.fromView(mapMarkView));
                            list.add(locationInfo4);
                        }
                        settingOverly(list);
                    } else {
                        Tools.showToast(TeamTaskMapActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TeamTaskMapActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(TeamTaskMapActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    public void onMapLoaded() {
//        ms = new MapStatus.Builder().zoom(22 - switchZoolLevel + 1).build();
//        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
    }

    /**
     * 设置地图点
     */
    private void settingOverly(ArrayList<LocationInfo> list) {
        if (mClusterManager == null) {
            mClusterManager = new ClusterManager<LocationInfo>(TeamTaskMapActivity.this, mBaiduMap);
            mBaiduMap.setOnMapLoadedCallback(TeamTaskMapActivity.this);
            // 设置地图监听，当地图状态发生改变时，进行点聚合运算
            mBaiduMap.setOnMapStatusChangeListener(mClusterManager);
            // 设置maker点击时的响应
            mBaiduMap.setOnMarkerClickListener(mClusterManager);
            mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<LocationInfo>() {
                @Override
                public boolean onClusterClick(Cluster<LocationInfo> cluster) {
                    int settingZoom = 18;
                    if (nowZoom < 7) {
                        settingZoom = 8;
                    } else if (nowZoom < 10) {
                        settingZoom = 13;
                    } else if (nowZoom < 14) {
                        settingZoom = 16;
                    }
                    MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(cluster.getPosition(), settingZoom);
                    mBaiduMap.setMapStatus(u);
                    return false;
                }
            });
            mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<LocationInfo>() {
                public boolean onClusterItemClick(LocationInfo tempMarker) {
                    return true;
                }
            });
            mClusterManager.setHandler(myHandler, MAP_STATUS_CHANGE); //设置handler
        } else {
            mClusterManager.clearItems();
        }
        mClusterManager.addItems(list);
    }


    private static final int MAP_STATUS_CHANGE = 100;

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MAP_STATUS_CHANGE) {
                MapStatus mapStatus = (MapStatus) msg.obj;
                if (mapStatus != null) {
                    refurbishInfo((int) mapStatus.zoom);
                }
            }
        }
    }

    private int nowZoom = 0;//当前地图级别

    private void refurbishInfo(int zoom) {
        nowZoom = zoom;
    }

    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        if (mMapView != null)
            mMapView.onDestroy();
        if (mBaiduMap != null)
            mBaiduMap.setMyLocationEnabled(false);
        if (mLocationClient != null)
            mLocationClient.stop();
    }

    public static boolean isRefresh;

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        if (mMapView != null)
            mMapView.onResume();
        if (isRefresh) {
            isRefresh = false;
            getData();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        if (mMapView != null)
            mMapView.onPause();
    }

    /**
     * 定位客户端
     */
    public LocationClient mLocationClient = null;
    public TeamTaskMapActivity.MyLocationListenner myListener = new TeamTaskMapActivity.MyLocationListenner();
    public static double location_latitude = 0, location_longitude = 0;

    /**
     * 初始化定位
     */
    private void initLocation() {
        if (mLocationClient != null && mLocationClient.isStarted()) {
            return;
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
//        option.setScanSpan(10000);
        mLocationClient.setLocOption(option);
    }

    private class MyLocationListenner implements BDLocationListener {
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                getData();
                return;
            }
            location_latitude = location.getLatitude();
            location_longitude = location.getLongitude();
            getData();
        }

        public void onConnectHotSpotMessage(String s, int i) {
        }

        public void onReceivePoi() {
        }
    }

}
