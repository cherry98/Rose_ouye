package com.orange.oy.activity.guide;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

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
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.orange.oy.R;
import com.orange.oy.activity.mycorps_314.IdentifycodeLoginActivity;
import com.orange.oy.activity.StoreDescActivity;
import com.orange.oy.activity.TaskitemDetailActivity_12;
import com.orange.oy.activity.black.BlackillustrateActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.clusterutil.clustering.Cluster;
import com.orange.oy.clusterutil.clustering.ClusterManager;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.fragment.CitysearchFragment;
import com.orange.oy.fragment.TaskNewFragment;
import com.orange.oy.info.LocationInfo;
import com.orange.oy.info.TaskDetailLeftInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.MapMarkView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 地图页面
 */
public class TaskLocationActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle,
        BaiduMap.OnMapLoadedCallback, View.OnClickListener, CitysearchFragment.OnCitysearchExitClickListener, CitysearchFragment.OnCitysearchItemClickListener {
    public void initTile() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.tasklocation_title);
        appTitle.settingName("任务地图");
        appTitle.showBack(this);
    }

    public void initNetworkConnection() {
        mapOutletList = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                if (!TextUtils.isEmpty(AppInfo.getKey(TaskLocationActivity.this))) {
                    params.put("token", Tools.getToken());
                    params.put("usermobile", AppInfo.getName(TaskLocationActivity.this));
                }
                switch (zoomLV) {//0：区，1：省，2：市，3：街道
                    case 0: {
                        params.put("city", city);
                        params.put("province", province);
                        params.put("county", county);
                    }
                    break;
                    case 1: {
                        params.put("province", province);
                        if (TextUtils.isEmpty(province)) {// 可能有直辖市
                            params.put("city", city);
                        }
                    }
                    break;
                    case 2: {
                        params.put("city", city);
                        params.put("province", province);
                    }
                    break;
                    case 3: {
                    }
                    break;
                }
                params.put("lon", longitude + "");
                params.put("lat", latitude + "");
                return params;
            }
        };
        mapOutletList.setIsShowDialog(true);
        checkapply = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                if (!TextUtils.isEmpty(AppInfo.getKey(TaskLocationActivity.this))) {
                    params.put("usermobile", AppInfo.getName(TaskLocationActivity.this));
                    params.put("token", Tools.getToken());
                }
                params.put("projectid", projectid);
                return params;
            }
        };
        checkapply.setIsShowDialog(true);
        checkinvalid = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(TaskLocationActivity.this));
                params.put("token", Tools.getToken());
                params.put("outletid", storeid);
                params.put("lon", longitude + "");
                params.put("lat", latitude + "");
                params.put("address", address);
                return params;
            }
        };
        checkinvalid.setIsShowDialog(true);
        rob = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(TaskLocationActivity.this));
                params.put("storeid", storeid);
                params.put("token", Tools.getToken());
                return params;
            }
        };
        rob.setIsShowDialog(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mapOutletList != null) {
            mapOutletList.stop(Urls.MapOutletList);
        }
        if (rob != null) {
            rob.stop(Urls.rob);
        }
        if (checkinvalid != null) {
            checkinvalid.stop(Urls.CheckInvalid);
        }
        if (checkapply != null) {
            checkapply.stop(Urls.CheckApply);
        }
        CustomProgressDialog.Dissmiss();
    }

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private NetworkConnection mapOutletList;
    private String city = "", province = "", county = "";
    private double longitude, latitude;
    private ArrayList<LocationInfo> list = new ArrayList<>();
    private PoiSearch poiSearch;
    private TextView tasklocation_location;
    private MyHandler myHandler = new MyHandler();
    private NetworkConnection checkapply, checkinvalid, rob;
    private String storeid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_location);
        initNetworkConnection();
        checkPermission();
        initTile();
        findViewById(R.id.tasklocation_location_layout).setOnClickListener(this);
        tasklocation_location = (TextView) findViewById(R.id.tasklocation_location);
        mMapView = (MapView) findViewById(R.id.tasklocation_mapview);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);//普通地图
        poiSearch = PoiSearch.newInstance();
//        poiSearch.setOnGetGeoCodeResultListener(onGetGeoCoderResultListener);
        poiSearch.setOnGetPoiSearchResultListener(poiListener);
        myHandler.sendEmptyMessageDelayed(0, 1000);
        String[] s = AppInfo.getAddress(this);
        province = s[0];
        city = s[1];
        county = s[2];
        if (TextUtils.isEmpty(s[2])) {
            tasklocation_location.setText(s[1]);
        } else {
            tasklocation_location.setText(s[1] + "-" + s[2]);
        }
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
                    Tools.showToast(TaskLocationActivity.this, "定位权限获取失败");
                    AppInfo.setOpenLocation(TaskLocationActivity.this, false);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void onBack() {
        TaskNewFragment.isRefresh = true;
        baseFinish();
    }

    ClusterManager<LocationInfo> mClusterManager;

    public void getData() {
        mapOutletList.sendPostRequest(Urls.MapOutletList, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (!list.isEmpty()) {
                        list.clear();
                    }
                    if (jsonObject.optInt("code") == 200) {
                        JSONArray projectList = jsonObject.getJSONArray("datas");
                        int length = projectList.length();
                        JSONObject tempJson;
                        int bigNum = 0;
                        boolean isBig = false;
                        for (int i = 0; i < length; i++) {
                            tempJson = projectList.getJSONObject(i);
                            LocationInfo locationInfo4 = new LocationInfo();
                            TaskDetailLeftInfo taskDetailLeftInfo = new TaskDetailLeftInfo();
                            taskDetailLeftInfo.setIs_exe(tempJson.optString("is_exe"));
                            taskDetailLeftInfo.setIs_desc(tempJson.optString("is_desc"));
                            taskDetailLeftInfo.setId(tempJson.optString("storeid"));
                            taskDetailLeftInfo.setName(tempJson.optString("storeName"));
                            taskDetailLeftInfo.setCode(tempJson.optString("storeNum"));
                            taskDetailLeftInfo.setIdentity(tempJson.optString("proxy_num"));
                            taskDetailLeftInfo.setCity(tempJson.optString("province"));
                            taskDetailLeftInfo.setCity2(tempJson.optString("city"));
                            taskDetailLeftInfo.setCity3(tempJson.optString("address"));
                            taskDetailLeftInfo.setNumber(tempJson.optString("accessed_num"));
                            taskDetailLeftInfo.setMoney(tempJson.optString("outletMoney"));
                            taskDetailLeftInfo.setMoney_unit(tempJson.optString("money_unit"));
                            taskDetailLeftInfo.setProjectid(tempJson.optString("projectid"));
                            taskDetailLeftInfo.setProjectname(tempJson.optString("projectName"));
                            taskDetailLeftInfo.setIs_taskphoto(tempJson.optString("is_takephoto"));
                            taskDetailLeftInfo.setBrand(tempJson.optString("brand"));
                            taskDetailLeftInfo.setIsUpdata(tempJson.optString("is_upload"));
                            locationInfo4.setOutletMoney(((int) Tools.StringToDouble(taskDetailLeftInfo.getMoney())) + "");
                            locationInfo4.setPhoto_compression(tempJson.optString("photo_compression"));
                            locationInfo4.setAddress(tempJson.optString("address"));
                            locationInfo4.setLatitude(Double.parseDouble(tempJson.optString("latitude")));
                            locationInfo4.setLongtitude(Double.parseDouble(tempJson.optString("longtitude")));
                            locationInfo4.setTaskDetailLeftInfo(taskDetailLeftInfo);
                            boolean isBreak = false;
                            for (LocationInfo temp : list) {
                                if (temp.getLatitude() == locationInfo4.getLatitude() &&
                                        temp.getLongtitude() == locationInfo4.getLongtitude()) {//如果经纬度相同，就合并
                                    ArrayList<LocationInfo> locationInfos = temp.getLocationInfos();
                                    if (locationInfos == null) {
                                        locationInfos = new ArrayList<LocationInfo>();
                                        locationInfos.add(temp);
                                        locationInfos.add(locationInfo4);
                                        temp.setLocationInfos(locationInfos);
                                    } else {
                                        locationInfos.add(locationInfo4);
                                    }
                                    isBreak = true;
                                    break;
                                }
                            }
                            if (!isBreak) {
                                list.add(locationInfo4);
                            }
                        }
                        int size = list.size();
                        if (size <= 10) {
                            isBig = true;
                        }
                        for (int i = 0; i < size; i++) {
                            LocationInfo temp = list.get(i);
                            if (temp.getLocationInfos() != null && !temp.getLocationInfos().isEmpty()) {
                                bigNum++;
                                MapMarkView mapMarkView = new MapMarkView(TaskLocationActivity.this, false);
                                mapMarkView.setNumber(temp.getLocationInfos().size() + "");
                                temp.setBitmapDescriptor(BitmapDescriptorFactory.fromView(mapMarkView));
                            } else {
                                if (isBig) {
                                    MapMarkView mapMarkView = new MapMarkView(TaskLocationActivity.this, false);
                                    mapMarkView.setMoney(temp.getOutletMoney());
                                    temp.setBitmapDescriptor(BitmapDescriptorFactory.fromView(mapMarkView));
                                } else {
                                    if ((size - i - 1 + bigNum) > 10 && Math.random() < 0.5) {//小
                                        MapMarkView mapMarkView = new MapMarkView(TaskLocationActivity.this, true);
                                        temp.setBitmapDescriptor(BitmapDescriptorFactory.fromView(mapMarkView));
                                    } else {//大
                                        MapMarkView mapMarkView = new MapMarkView(TaskLocationActivity.this, false);
                                        mapMarkView.setMoney(temp.getOutletMoney());
                                        temp.setBitmapDescriptor(BitmapDescriptorFactory.fromView(mapMarkView));
                                    }
                                }
                            }
                        }
                        settingOverly(list);
                    } else {
                        Tools.showToast(TaskLocationActivity.this, jsonObject.getString("msg"));
                    }
                    CustomProgressDialog.Dissmiss();
                } catch (JSONException e) {
                    Tools.showToast(TaskLocationActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskLocationActivity.this, getResources().getString(R.string.network_volleyerror));
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
            mClusterManager = new ClusterManager<LocationInfo>(TaskLocationActivity.this, mBaiduMap);
            mBaiduMap.setOnMapLoadedCallback(TaskLocationActivity.this);
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
                @Override
                public boolean onClusterItemClick(LocationInfo tempMarker) {
                    if (tempMarker.getLocationInfos() != null && !tempMarker.getLocationInfos().isEmpty()) {
                        showDialog2(tempMarker.getLocationInfos());
                    } else {
                        showDialog1(tempMarker);
                    }
                    return true;
                }
            });
            mClusterManager.setHandler(myHandler, MAP_STATUS_CHANGE); //设置handler
        } else {
            mClusterManager.clearItems();
        }
        mClusterManager.addItems(list);
    }

    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        if (mMapView != null)
            mMapView.onDestroy();
        if (mBaiduMap != null)
            mBaiduMap.setMyLocationEnabled(false);
        if (poiSearch != null) {
            poiSearch.destroy();
        }
        if (mLocationClient != null)
            mLocationClient.stop();
        if (geoCoder != null) {
            geoCoder.destroy();
        }
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

    private String address = "";
    private OnGetGeoCoderResultListener onGetGeoCoderResultListener = new OnGetGeoCoderResultListener() {
        public void onGetGeoCodeResult(GeoCodeResult result) {
        }

        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
            if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                Tools.showToast(TaskLocationActivity.this, getResources().getString(R.string.location_fail3));
            } else {
                longitude = reverseGeoCodeResult.getLocation().longitude;
                latitude = reverseGeoCodeResult.getLocation().latitude;
                address = reverseGeoCodeResult.getAddress();
            }
        }
    };
    private OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {

        public void onGetPoiResult(PoiResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                Tools.d("获取城市位置失败");
                return;
            }
            List<PoiInfo> list = result.getAllPoi();
            if (list != null && !list.isEmpty()) {
                PoiInfo poiAddrInfo = list.get(0);
                latitude = poiAddrInfo.location.latitude;
                longitude = poiAddrInfo.location.longitude;
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(poiAddrInfo.location, 13);
                mBaiduMap.setMapStatus(u);
                getData();
            } else {
                Tools.d("获取城市位置失败");
            }
        }

        public void onGetPoiDetailResult(PoiDetailResult result) {
            //获取Place详情页检索结果
        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    };

    private void showDialog1(LocationInfo locationInfo) {
        View view = Tools.loadLayout(this, R.layout.view_dialog_tasklocation);
        ((TextView) view.findViewById(R.id.vdtasklocation_name)).setText(locationInfo.getTaskDetailLeftInfo().getName());
        ((TextView) view.findViewById(R.id.vdtasklocation_money)).setText("¥ " + locationInfo.getOutletMoney());
        ((TextView) view.findViewById(R.id.vdtasklocation_address)).setText(locationInfo.getAddress());
        view.setTag(locationInfo);
        view.setOnClickListener(onClickListener);
        PopupWindow popupWindow = new PopupWindow();
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.setContentView(view);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        int cy = Tools.getScreeInfoHeight(this) / 2;
        int cx = Tools.getScreeInfoWidth(this) / 2;
        popupWindow.showAtLocation(mMapView, Gravity.CENTER, (int) nowX - cx, (int) nowY - cy - 50); //设置layout在PopupWindow中显示的位置
    }

    private ArrayList<LocationInfo> dialog2LocationInfos;

    private void showDialog2(ArrayList<LocationInfo> locationInfos) {
        View view = Tools.loadLayout(this, R.layout.view_dialog_tasklocation2);
        ListView listView = (ListView) view.findViewById(R.id.vdtasklocation_listview);
        MyAdapter myAdapter = new MyAdapter(locationInfos);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(onItemClickListener);
        PopupWindow popupWindow = new PopupWindow();
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.setContentView(view);
        popupWindow.setWidth(Tools.dipToPx(this, 280));
        popupWindow.setHeight(Tools.dipToPx(this, 460));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(mMapView, Gravity.CENTER, 0, 0); //设置layout在PopupWindow中显示的位置
        dialog2LocationInfos = locationInfos;
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (dialog2LocationInfos == null) {
                Tools.showToast(TaskLocationActivity.this, "数据异常，请重新打开");
            }
            dialongClick(dialog2LocationInfos.get(position));
        }
    };
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            dialongClick((LocationInfo) v.getTag());
        }
    };

    /**
     * 地图气泡点击
     *
     * @param locationInfo
     */
    private void dialongClick(LocationInfo locationInfo) {
        if (TextUtils.isEmpty(AppInfo.getKey(TaskLocationActivity.this))) {
            ConfirmDialog.showDialog(TaskLocationActivity.this, null, 2,
                    getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                        @Override
                        public void leftClick(Object object) {
                        }

                        @Override
                        public void rightClick(Object object) {
                            Intent intent = new Intent(TaskLocationActivity.this, IdentifycodeLoginActivity.class);
                            startActivity(intent);
                        }
                    });
        } else {
            ConfirmDialog.showDialog(TaskLocationActivity.this, "是否确认申请该网点？", locationInfo,
                    true, new ConfirmDialog.OnSystemDialogClickListener() {
                        @Override
                        public void leftClick(Object object) {

                        }

                        @Override
                        public void rightClick(Object object) {
                            rob((LocationInfo) object);
                        }
                    });
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tasklocation_location_layout: {
                creatCitysearchFragment();
            }
            break;
        }
    }

    private CitysearchFragment citysearchFragment;
    private FragmentManager fMgr;

    private void creatCitysearchFragment() {
        if (fMgr == null) {
            fMgr = getSupportFragmentManager();
        }
        FragmentTransaction ft = fMgr.beginTransaction();
        citysearchFragment = (CitysearchFragment) fMgr.findFragmentByTag("citysearchFragment");
        if (citysearchFragment == null) {
            citysearchFragment = new CitysearchFragment();
        }
        citysearchFragment.setOnCitysearchExitClickListener(this);
        citysearchFragment.setOnCitysearchItemClickListener(this);
        ft.replace(R.id.fragmentRoot, citysearchFragment, "citysearchFragment");
        ft.commit();
    }

    public void exitClick() {
        citysearchFragment = (CitysearchFragment) fMgr.findFragmentByTag("citysearchFragment");
        if (citysearchFragment != null) {
            FragmentTransaction ft = fMgr.beginTransaction();
            ft.remove(citysearchFragment).commit();
        }
    }

    public void ItemClick(Map<String, String> map) {
        province = map.get("province");
        city = map.get("name");
        county = map.get("county");
        if (TextUtils.isEmpty(county)) {
            tasklocation_location.setText(city);
        } else {
            tasklocation_location.setText(city + "-" + county);
        }
        AppInfo.setCityName(this, province, city, county);
        myHandler.sendEmptyMessage(0);
        citysearchFragment = (CitysearchFragment) fMgr.findFragmentByTag("citysearchFragment");
        if (citysearchFragment != null) {
            FragmentTransaction ft = fMgr.beginTransaction();
            ft.remove(citysearchFragment).commit();
        }
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
            } else {
                Tools.d("pro:" + province + ",city:" + city + ",county:" + county);
                if (TextUtils.isEmpty(county)) {
                    poiSearch.searchInCity(new PoiCitySearchOption().city(city).keyword("市政府").pageNum(1));
                } else {
                    poiSearch.searchInCity(new PoiCitySearchOption().city(city).keyword(county + "政府").pageNum(1));
                }
            }
        }
    }

    private float nowX;
    private float nowY;

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            nowX = event.getX();
            nowY = event.getY();
        }
        return super.onTouchEvent(event);
    }

    private class MyAdapter extends BaseAdapter {
        private ArrayList<LocationInfo> locationInfos;

        private MyAdapter(ArrayList<LocationInfo> locationInfos) {
            this.locationInfos = locationInfos;
        }

        @Override
        public int getCount() {
            return locationInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return locationInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHold viewHold;
            if (convertView == null) {
                convertView = Tools.loadLayout(TaskLocationActivity.this, R.layout.item_dialog_tasklocation);
                viewHold = new ViewHold();
                viewHold.name = (TextView) convertView.findViewById(R.id.item_dtasklocation_name);
                viewHold.money = (TextView) convertView.findViewById(R.id.item_dtasklocation_money);
                viewHold.address = (TextView) convertView.findViewById(R.id.item_dtasklocation_address);
                convertView.setTag(viewHold);
            } else {
                viewHold = (ViewHold) convertView.getTag();
            }
            LocationInfo locationInfo = locationInfos.get(position);
            viewHold.name.setText(locationInfo.getStoreName());
            viewHold.address.setText(locationInfo.getAddress());
            viewHold.money.setText("¥ " + locationInfo.getOutletMoney());
            return convertView;
        }
    }

    private class ViewHold {
        TextView name, money, address;
    }

    private int zoomLV = 0;//0：区，1：省，2：市，3：街道
    private int nowZoom = 0;//当前地图级别

    private void refurbishInfo(int zoom) {
        if (zoom < 7) {//省
            if (nowZoom >= 7) {
                zoomLV = 1;
                getData();
            }
        } else if (zoom < 10) {//市
            if (nowZoom >= 10) {
                zoomLV = 2;
                getData();
            }
        }
        nowZoom = zoom;
//        else if (zoom < 14) {//区
//            zoomLV = 0;
//        } else {//街道
//            zoomLV = 3;
//        }
    }

    private String projectid = "";

    public void rob(final LocationInfo locationInfo) {
        storeid = locationInfo.getTaskDetailLeftInfo().getId();
        rob.sendPostRequest(Urls.rob, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String msg = jsonObject.getString("msg");
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        ConfirmDialog.showDialog(TaskLocationActivity.this, "恭喜您，申请成功！", msg, "继续申请", "现在去做", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                            @Override
                            public void leftClick(Object object) {
                                mClusterManager.removeItem(locationInfo);
                            }

                            @Override
                            public void rightClick(Object object) {
                                doSelectType(locationInfo, locationInfo.getTaskDetailLeftInfo().getType());
                            }
                        });
                    } else if (code == 3) {
                        ConfirmDialog.showDialog(TaskLocationActivity.this, "恭喜您，申请成功！", msg, null, "现在去做", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                            @Override
                            public void leftClick(Object object) {

                            }

                            @Override
                            public void rightClick(Object object) {
                                doSelectType(locationInfo, locationInfo.getTaskDetailLeftInfo().getType());
                            }
                        }).goneLeft();
                    } else if (code == 2) {
                        ConfirmDialog.showDialog(TaskLocationActivity.this, "申请失败！", msg, null, "我知道了", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                            @Override
                            public void leftClick(Object object) {

                            }

                            @Override
                            public void rightClick(Object object) {
                            }
                        }).goneLeft();
                    } else if (code == 1) {
                        ConfirmDialog.showDialog(TaskLocationActivity.this, "很遗憾，稍慢了一步！", msg, null, "继续申请", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                            @Override
                            public void leftClick(Object object) {
                            }

                            @Override
                            public void rightClick(Object object) {
                                mClusterManager.removeItem(locationInfo);
                            }
                        }).goneLeft();
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskLocationActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskLocationActivity.this, getResources().getString(R.string.network_error));
            }
        }, null);
    }

    //判断是否可以执行
    private void doSelectType(final LocationInfo locationInfo, final String tasktype) {
        if (locType == 61 || locType == 161) {
            checkinvalid.sendPostRequest(Urls.CheckInvalid, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    Tools.d(s);
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        int code = jsonObject.getInt("code");
                        if (code == 200) {
                            doExecute(locationInfo, tasktype);
                        } else if (code == 2) {
                            ConfirmDialog.showDialog(TaskLocationActivity.this, null, jsonObject.getString("msg"), null,
                                    "确定", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                        @Override
                                        public void leftClick(Object object) {

                                        }

                                        @Override
                                        public void rightClick(Object object) {
                                        }
                                    }).goneLeft();
                        } else if (code == 3) {
                            ConfirmDialog.showDialog(TaskLocationActivity.this, null, jsonObject.getString("msg"), "取消",
                                    "继续执行", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                                        @Override
                                        public void leftClick(Object object) {

                                        }

                                        @Override
                                        public void rightClick(Object object) {
                                            doExecute(locationInfo, tasktype);
                                        }
                                    });
                        } else {
                            Tools.showToast(TaskLocationActivity.this, jsonObject.getString("msg"));
                        }
                    } catch (JSONException e) {
                        Tools.showToast(TaskLocationActivity.this, getResources().getString(R.string.network_error));
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Tools.showToast(TaskLocationActivity.this, getResources().getString(R.string
                            .network_volleyerror));
                }
            }, null);
        } else if (locType == 167) {
            Tools.showToast2(this, "请您检查是否开启权限，尝试重新请求定位");
        } else {
            Tools.showToast2(this, "请您检查网络是否通畅或是否允许访问位置信息,尝试重新请求定位");
        }
    }

    private void doExecute(LocationInfo locationInfo, String tasktype) {
        TaskDetailLeftInfo taskDetailLeftInfo = locationInfo.getTaskDetailLeftInfo();
        if (tasktype != null) {
            if ("1".equals(tasktype)) {//正常任务
                if (taskDetailLeftInfo.getIs_desc().equals("1")) {//有网点说明
                    Intent intent = new Intent(this, StoreDescActivity.class);
                    intent.putExtra("id", taskDetailLeftInfo.getId());
                    intent.putExtra("projectname", taskDetailLeftInfo.getProjectname());
                    intent.putExtra("store_name", taskDetailLeftInfo.getName());
                    intent.putExtra("store_num", taskDetailLeftInfo.getCode());
                    intent.putExtra("province", taskDetailLeftInfo.getCity());
                    intent.putExtra("city", taskDetailLeftInfo.getCity2());
                    intent.putExtra("project_id", taskDetailLeftInfo.getProjectid());
                    intent.putExtra("photo_compression", taskDetailLeftInfo.getPhoto_compression());
                    intent.putExtra("is_record", taskDetailLeftInfo.getIs_record());
                    intent.putExtra("is_watermark", taskDetailLeftInfo.getIs_watermark());//int
                    intent.putExtra("code", taskDetailLeftInfo.getCode());
                    intent.putExtra("brand", taskDetailLeftInfo.getBrand());
                    intent.putExtra("is_takephoto", taskDetailLeftInfo.getIs_taskphoto());//String
                    intent.putExtra("is_desc", taskDetailLeftInfo.getIs_desc());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, TaskitemDetailActivity_12.class);
                    intent.putExtra("id", taskDetailLeftInfo.getId());
                    intent.putExtra("projectname", taskDetailLeftInfo.getProjectname());
                    intent.putExtra("store_name", taskDetailLeftInfo.getName());
                    intent.putExtra("store_num", taskDetailLeftInfo.getCode());
                    intent.putExtra("province", taskDetailLeftInfo.getCity());
                    intent.putExtra("city", taskDetailLeftInfo.getCity2());
                    intent.putExtra("project_id", taskDetailLeftInfo.getProjectid());
                    intent.putExtra("photo_compression", taskDetailLeftInfo.getPhoto_compression());
                    intent.putExtra("is_record", taskDetailLeftInfo.getIs_record());
                    intent.putExtra("is_watermark", taskDetailLeftInfo.getIs_watermark() + "");//int
                    intent.putExtra("code", taskDetailLeftInfo.getCode());
                    intent.putExtra("brand", taskDetailLeftInfo.getBrand());
                    intent.putExtra("is_takephoto", taskDetailLeftInfo.getIs_taskphoto());//String
                    intent.putExtra("is_desc", taskDetailLeftInfo.getIs_desc());
                    startActivity(intent);
                }
            } else if ("2".equals(tasktype)) {//暗访任务
                Intent intent = new Intent(this, BlackillustrateActivity.class);
                intent.putExtra("project_id", taskDetailLeftInfo.getProjectid());
                intent.putExtra("project_name", taskDetailLeftInfo.getProjectname());
                intent.putExtra("store_id", taskDetailLeftInfo.getId());
                intent.putExtra("store_name", taskDetailLeftInfo.getName());
                intent.putExtra("store_num", taskDetailLeftInfo.getCode());
                intent.putExtra("photo_compression", locationInfo.getPhoto_compression());
                intent.putExtra("isUpdata", taskDetailLeftInfo.getIsUpdata());
                intent.putExtra("province", taskDetailLeftInfo.getCity());
                intent.putExtra("city", taskDetailLeftInfo.getCity2());
                intent.putExtra("address", taskDetailLeftInfo.getCity3());
                intent.putExtra("is_desc", taskDetailLeftInfo.getIs_desc());
                intent.putExtra("isNormal", true);
                startActivity(intent);
            }
        } else {
            if (taskDetailLeftInfo.getIs_desc().equals("1")) {//有网点说明
                Intent intent = new Intent(this, StoreDescActivity.class);
                intent.putExtra("id", taskDetailLeftInfo.getId());
                intent.putExtra("projectname", taskDetailLeftInfo.getProjectname());
                intent.putExtra("store_name", taskDetailLeftInfo.getName());
                intent.putExtra("store_num", taskDetailLeftInfo.getCode());
                intent.putExtra("province", taskDetailLeftInfo.getCity());
                intent.putExtra("city", taskDetailLeftInfo.getCity2());
                intent.putExtra("project_id", taskDetailLeftInfo.getProjectid());
                intent.putExtra("photo_compression", taskDetailLeftInfo.getPhoto_compression());
                intent.putExtra("is_record", taskDetailLeftInfo.getIs_record());
                intent.putExtra("is_watermark", taskDetailLeftInfo.getIs_watermark());//int
                intent.putExtra("code", taskDetailLeftInfo.getCode());
                intent.putExtra("brand", taskDetailLeftInfo.getBrand());
                intent.putExtra("is_takephoto", taskDetailLeftInfo.getIs_taskphoto());//String
                intent.putExtra("is_desc", taskDetailLeftInfo.getIs_desc());
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, TaskitemDetailActivity_12.class);
                intent.putExtra("id", taskDetailLeftInfo.getId());
                intent.putExtra("projectname", taskDetailLeftInfo.getProjectname());
                intent.putExtra("store_name", taskDetailLeftInfo.getName());
                intent.putExtra("store_num", taskDetailLeftInfo.getCode());
                intent.putExtra("province", taskDetailLeftInfo.getCity());
                intent.putExtra("city", taskDetailLeftInfo.getCity2());
                intent.putExtra("project_id", taskDetailLeftInfo.getProjectid());
                intent.putExtra("photo_compression", taskDetailLeftInfo.getPhoto_compression());
                intent.putExtra("is_record", taskDetailLeftInfo.getIs_record());
                intent.putExtra("is_watermark", taskDetailLeftInfo.getIs_watermark() + "");//int
                intent.putExtra("code", taskDetailLeftInfo.getCode());
                intent.putExtra("brand", taskDetailLeftInfo.getBrand());
                intent.putExtra("is_takephoto", taskDetailLeftInfo.getIs_taskphoto());//String
                intent.putExtra("is_desc", taskDetailLeftInfo.getIs_desc());
                startActivity(intent);
            }
        }
    }

    /**
     * 定位客户端
     */
    public LocationClient mLocationClient = null;
    public MyLocationListenner myListener = new MyLocationListenner();
    private GeoCoder geoCoder = null;
    public static double location_latitude, location_longitude;

    /**
     * 初始化定位
     */
    private void initLocation() {
        if (mLocationClient != null && mLocationClient.isStarted()) {
            return;
        }
        if (geoCoder == null) {
            geoCoder = GeoCoder.newInstance();
            geoCoder.setOnGetGeoCodeResultListener(onGetGeoCoderResultListener);
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

    private int locType;

    private class MyLocationListenner implements BDLocationListener {
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
//                Tools.showToast(TaskLocationActivity.this, getResources().getString(R.string.location_fail2));
                return;
            }
            location_latitude = location.getLatitude();
            location_longitude = location.getLongitude();
            locType = location.getLocType();
            LatLng point = new LatLng(location_latitude, location_longitude);
            geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(point));
        }

        public void onConnectHotSpotMessage(String s, int i) {
        }

        public void onReceivePoi() {
        }
    }

}
