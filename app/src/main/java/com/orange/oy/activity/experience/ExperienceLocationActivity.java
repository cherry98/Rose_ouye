package com.orange.oy.activity.experience;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.orange.oy.R;
import com.orange.oy.activity.guide.TaskLocationActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.clusterutil.clustering.Cluster;
import com.orange.oy.clusterutil.clustering.ClusterItem;
import com.orange.oy.clusterutil.clustering.ClusterManager;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.LocationInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 体验项目---项目地点地图项目
 */
public class ExperienceLocationActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, BaiduMap.OnMapLoadedCallback {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.eplocation_title);
        appTitle.settingName("任务地图");
        appTitle.showBack(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (experienceOutletList != null) {
            experienceOutletList.stop(Urls.ExperienceOutletList);
        }
        if (checkinvalid != null) {
            checkinvalid.stop(Urls.CheckInvalid);
        }
        if (rob != null) {
            rob.stop(Urls.rob);
        }
    }

    private void initNetworkConnection() {
        experienceOutletList = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(ExperienceLocationActivity.this));
                params.put("token", Tools.getToken());
                params.put("projectid", projectid);
                params.put("lon", longitude + "");
                params.put("lat", latitude + "");
                params.put("city", city);
                return params;
            }
        };
        experienceOutletList.setIsShowDialog(true);
        rob = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(ExperienceLocationActivity.this));
                params.put("storeid", storeid);
                params.put("token", Tools.getToken());
                return params;
            }
        };
        rob.setIsShowDialog(true);
        checkinvalid = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(ExperienceLocationActivity.this));
                params.put("token", Tools.getToken());
                params.put("outletid", storeid);
                params.put("lon", longitude + "");
                params.put("lat", latitude + "");
                params.put("address", address);
                return params;
            }
        };
    }

    private MapView mapView;
    private BaiduMap mBaiduMap;
    private LocationClient locationClient = null;
    private BDLocationListener myListener = new MyLocationListener();
    private double longitude, latitude;
    private String city, projectid, storeid, address;
    private NetworkConnection experienceOutletList, checkinvalid, rob;
    private ArrayList<LocationInfo> list = new ArrayList<>();
    private int switchZoolLevel;
    private MapStatus ms;
    private int locType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_location);
        initTitle();
        initNetworkConnection();
        city = getIntent().getStringExtra("city");
        projectid = getIntent().getStringExtra("id");
        mapView = (MapView) findViewById(R.id.eplocation_mapview);
        mBaiduMap = mapView.getMap();
        //开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);//普通地图
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null));
        checkPermission();
        initLocation();
    }

    private boolean isRefresh;

    private void getData() {
        experienceOutletList.sendPostRequest(Urls.ExperienceOutletList, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (!list.isEmpty()) {
                        list.clear();
                    }
                    if (isRefresh) {
                        mBaiduMap.clear();
                        mBaiduMap = mapView.getMap();
                        //开启定位图层
                        mBaiduMap.setMyLocationEnabled(true);
                        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);//普通地图
                        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null));
                        initLocation();
                        isRefresh = false;
                    }
                    if (jsonObject.getInt("code") == 200) {
                        JSONArray jsonArray = jsonObject.getJSONArray("datas");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            LocationInfo locationInfo = new LocationInfo();
                            locationInfo.setProjectid(object.getString("projectid"));
                            locationInfo.setProjectName(object.getString("projectName"));
                            locationInfo.setBegin_date(object.getString("begin_date"));
                            locationInfo.setEnd_date(object.getString("end_date"));
                            locationInfo.setCheck_time(object.getString("check_time"));
                            locationInfo.setMoney_unit(object.getString("money_unit"));
                            locationInfo.setStoreid(object.getString("storeid"));
                            locationInfo.setStoreName(object.getString("storeName"));
                            locationInfo.setStoreNum(object.getString("storeNum"));
                            locationInfo.setProvince(object.getString("province"));
                            locationInfo.setCity(object.getString("city"));
                            locationInfo.setAddress(object.getString("address"));
                            locationInfo.setLongtitude(object.getDouble("longtitude"));
                            locationInfo.setLatitude(object.getDouble("latitude"));
                            locationInfo.setDist(object.getDouble("dist"));
                            locationInfo.setStandard_state(object.getString("standard_state"));
                            locationInfo.setOutletMoney(object.getString("outletMoney"));
                            locationInfo.setProject_person(object.getString("project_person"));
                            locationInfo.setPhotoUrl(object.getString("photoUrl"));
                            locationInfo.setIs_watermark(object.getString("is_watermark"));
                            locationInfo.setPhoto_compression(object.getString("photo_compression"));
                            locationInfo.setIs_takephoto(object.getString("is_takephoto"));
                            locationInfo.setCode(object.getString("code"));
                            locationInfo.setBrand(object.getString("brand"));
                            list.add(locationInfo);
                        }
                        if (!list.isEmpty()) {
                            Tools.d("长度" + list.size());
                            MyLocationData locData = new MyLocationData.Builder().latitude(latitude).longitude
                                    (longitude).build();
                            mBaiduMap.setMyLocationData(locData);
                            ClusterManager<MyItem> mClusterManager = new ClusterManager<MyItem>(ExperienceLocationActivity.this, mBaiduMap);
                            ArrayList<MyItem> items = new ArrayList<MyItem>();
                            for (int i = 0; i < list.size(); i++) {
                                LatLng latLng = new LatLng(list.get(i).getLatitude(), list.get(i).getLongtitude());
                                items.add(new MyItem(latLng, i, list.get(i).getOutletMoney()));
                            }
                            mClusterManager.addItems(items);
                            int dist = (int) list.get(0).getDist();
                            setZoomLevel(dist);
                            mBaiduMap.setOnMapLoadedCallback(ExperienceLocationActivity.this);
                            // 设置地图监听，当地图状态发生改变时，进行点聚合运算
                            mBaiduMap.setOnMapStatusChangeListener(mClusterManager);
                            // 设置maker点击时的响应
                            mBaiduMap.setOnMarkerClickListener(mClusterManager);
                            mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyItem>() {
                                @Override
                                public boolean onClusterClick(Cluster<MyItem> cluster) {
                                    LatLngBounds.Builder builder2 = new LatLngBounds.Builder();
                                    int i = 0;
                                    List<MyItem> items = (List<MyItem>) cluster.getItems();
                                    List list1 = new ArrayList();
                                    for (MyItem myItem : items) {
                                        builder2 = builder2.include(myItem.getPosition());
                                        list1.add(myItem.getPositionItem());
                                        Log.i("map", "log: i=" + i++ + " pos=" + myItem.getPosition().toString() + "posti" + myItem.getPositionItem());
                                    }
                                    LatLngBounds latlngBounds = builder2.build();
                                    String la, lo, la_1, lo_1;
                                    la = latlngBounds.northeast.latitude + "";
                                    la_1 = latlngBounds.southwest.latitude + "";
                                    lo = latlngBounds.northeast.longitude + "";
                                    lo_1 = latlngBounds.southwest.longitude + "";
                                    boolean a = la.equals(la_1);
                                    boolean b = lo.equals(lo_1);
                                    Tools.d("la:" + la + "la_1:" + la_1 + "lo:" + lo + "lo_1:" + lo_1 + "\n" + "la---0：" + a + "lo-----" + b);
                                    MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(latlngBounds, mapView.getWidth(), mapView.getHeight());
                                    mBaiduMap.animateMapStatus(u);
                                    Random random = new Random();
                                    final int position = (int) list1.get(random.nextInt(list1.size()));
                                    if (!list.isEmpty() && a && b) {
                                        ConfirmDialog.showDialog(ExperienceLocationActivity.this, "是否确认申请该网点？", true, new ConfirmDialog.OnSystemDialogClickListener() {
                                            @Override
                                            public void leftClick(Object object) {

                                            }

                                            @Override
                                            public void rightClick(Object object) {
                                                rob(list.get(position));
                                            }
                                        });
                                    }
                                    return false;
                                }
                            });
                            mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
                                @Override
                                public boolean onClusterItemClick(MyItem item) {
                                    Tools.d("item" + item.getPosition() + "+++" + item.getPositionItem());
                                    final int position = item.getPositionItem();
                                    if (!list.isEmpty()) {
                                        ConfirmDialog.showDialog(ExperienceLocationActivity.this, "是否确认申请该网点？", true, new ConfirmDialog.OnSystemDialogClickListener() {
                                            @Override
                                            public void leftClick(Object object) {

                                            }

                                            @Override
                                            public void rightClick(Object object) {
                                                rob(list.get(position));
                                            }
                                        });
                                    }
                                    return false;
                                }
                            });
                            mClusterManager.setHandler(handler, MAP_STATUS_CHANGE); //设置handler
                        }
                    } else {
                        Tools.showToast(ExperienceLocationActivity.this, jsonObject.getString("msg"));
                    }
                    CustomProgressDialog.Dissmiss();
                } catch (JSONException e) {
                    Tools.showToast(ExperienceLocationActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(ExperienceLocationActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    private void rob(final LocationInfo locationInfo) {
        storeid = locationInfo.getStoreid();
        rob.sendPostRequest(Urls.rob, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String msg = jsonObject.getString("msg");
                    int code = jsonObject.getInt("code");
                    final String max_num = jsonObject.getString("max_num");
                    if (code == 200) {
                        ConfirmDialog.showDialog(ExperienceLocationActivity.this, "申请成功", msg, "继续申请", "开始执行", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                            @Override
                            public void leftClick(Object object) {
                                if ("1".equals(max_num)) {
                                    baseFinish();
                                } else {
                                    isRefresh = true;
                                    getData();
                                }
                            }

                            @Override
                            public void rightClick(Object object) {
                                doSelectType(locationInfo);
                            }
                        });
                    } else if (code == 3) {
                        ConfirmDialog.showDialog(ExperienceLocationActivity.this, "申请成功", msg, null, "开始执行", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                            @Override
                            public void leftClick(Object object) {

                            }

                            @Override
                            public void rightClick(Object object) {
                                doSelectType(locationInfo);
                            }
                        }).goneLeft();
                    } else if (code == 2) {
                        ConfirmDialog.showDialog(ExperienceLocationActivity.this, "申请失败", msg, null, "确定", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                            @Override
                            public void leftClick(Object object) {

                            }

                            @Override
                            public void rightClick(Object object) {
                            }
                        }).goneLeft();
                    } else {
                        Tools.showToast(ExperienceLocationActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(ExperienceLocationActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(ExperienceLocationActivity.this, getResources().getString(R.string.network_error));
            }
        }, null);
    }

    private void doSelectType(final LocationInfo locationInfo) {
        if (locType == 61 || locType == 161) {
            checkinvalid.sendPostRequest(Urls.CheckInvalid, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    Tools.d(s);
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        int code = jsonObject.getInt("code");
                        if (code == 200) {
                            doExecute(locationInfo);
                        } else if (code == 2) {
                            ConfirmDialog.showDialog(ExperienceLocationActivity.this, null, jsonObject.getString("msg"), null,
                                    "确定", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                        @Override
                                        public void leftClick(Object object) {

                                        }

                                        @Override
                                        public void rightClick(Object object) {
                                        }
                                    }).goneLeft();
                        } else if (code == 3) {
                            ConfirmDialog.showDialog(ExperienceLocationActivity.this, null, jsonObject.getString("msg"), "取消",
                                    "继续执行", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                                        @Override
                                        public void leftClick(Object object) {

                                        }

                                        @Override
                                        public void rightClick(Object object) {
                                            doExecute(locationInfo);
                                        }
                                    });
                        } else {
                            Tools.showToast(ExperienceLocationActivity.this, jsonObject.getString("msg"));
                        }
                    } catch (JSONException e) {
                        Tools.showToast(ExperienceLocationActivity.this, getResources().getString(R.string.network_error));
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Tools.showToast(ExperienceLocationActivity.this, getResources().getString(R.string
                            .network_volleyerror));
                }
            }, null);
        } else if (locType == 167) {
            Tools.showToast2(this, "请您检查是否开启权限，尝试重新请求定位");
        } else {
            Tools.showToast2(this, "请您检查网络是否通畅或是否允许访问位置信息,尝试重新请求定位");
        }
    }

    private void doExecute(LocationInfo locationInfo) {
        Intent intent = new Intent(ExperienceLocationActivity.this, ExperienceillActivity.class);
        intent.putExtra("id", locationInfo.getProjectid());
        intent.putExtra("projectName", locationInfo.getProjectName());
        intent.putExtra("storeNum", locationInfo.getStoreNum());
        intent.putExtra("storeName", locationInfo.getStoreName());
        intent.putExtra("store_id", locationInfo.getStoreid());
        intent.putExtra("city", locationInfo.getCity());
        intent.putExtra("money_unit", locationInfo.getMoney_unit());
        intent.putExtra("end_date", locationInfo.getEnd_date());
        intent.putExtra("check_time", locationInfo.getCheck_time());
        intent.putExtra("begin_date", locationInfo.getBegin_date());
        intent.putExtra("project_name", locationInfo.getProjectName());
        intent.putExtra("longitude", locationInfo.getLongtitude());
        intent.putExtra("latitude", locationInfo.getLatitude());
        intent.putExtra("project_person", locationInfo.getProject_person());
        intent.putExtra("standard_state", locationInfo.getStandard_state());
        intent.putExtra("is_watermark", locationInfo.getIs_watermark());
        intent.putExtra("photo_compression", locationInfo.getPhoto_compression());
        intent.putExtra("brand", locationInfo.getBrand());
        intent.putExtra("code", locationInfo.getCode());
        startActivity(intent);
        baseFinish();
    }

    @Override
    public void onMapLoaded() {
        // TODO Auto-generated method stub
        ms = new MapStatus.Builder().zoom(22 - switchZoolLevel + 1).build();
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
    }

    private final int MAP_STATUS_CHANGE = 100;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MAP_STATUS_CHANGE:
                    MapStatus mapStatus = (MapStatus) msg.obj;
                    if (mapStatus != null) {
                        Log.i("MarkerClusterDemo", "mapStatus=" + mapStatus.toString());
                        // to do :  判断地图状态，进行相应处理
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public class MyItem implements ClusterItem {
        private final LatLng mPosition;
        private Bundle mBundle;
        private int position;
        private String money;

        public MyItem(LatLng latLng, int position, String money) {
            mPosition = latLng;
            mBundle = null;
            this.position = position;
            this.money = money;
        }

        public int getPositionItem() {
            return position;
        }

        public String getMoney() {
            return money;
        }

        public MyItem(LatLng latLng, Bundle bundle) {
            mPosition = latLng;
            mBundle = bundle;
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }

        @Override
        public BitmapDescriptor getBitmapDescriptor() {
            View view = LayoutInflater.from(ExperienceLocationActivity.this).inflate(R.layout.location_money, null);
            TextView location_money = (TextView) view.findViewById(R.id.location_money);
            location_money.setText("¥" + money);
            return BitmapDescriptorFactory
                    .fromView(view);//R.drawable.icon_gcoding);
        }

        public Bundle getBundle() {
            return mBundle;
        }

    }

    /**
     * 初始化定位
     */

    private void initLocation() {
        if (locationClient != null && locationClient.isStarted()) {
            Tools.showToast(ExperienceLocationActivity.this, "正在定位...");
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
                Tools.showToast(ExperienceLocationActivity.this, getResources().getString(R.string.location_fail));
                return;
            }
            latitude = bdLocation.getLatitude();
            longitude = bdLocation.getLongitude();
            locType = bdLocation.getLocType();
            address = bdLocation.getAddrStr();
            Tools.d(bdLocation.getAddrStr());
            getData();
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
                    Tools.showToast(ExperienceLocationActivity.this, "定位权限获取失败");
                    AppInfo.setOpenLocation(ExperienceLocationActivity.this, false);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mapView.onPause();
    }

    private void setZoomLevel(int dist) {
        if (dist <= 10) {
            switchZoolLevel = 1;
        } else if (dist <= 20 && dist > 10) {
            switchZoolLevel = 2;
        } else if (dist <= 50 && dist > 20) {
            switchZoolLevel = 3;
        } else if (dist <= 100 && dist > 50) {
            switchZoolLevel = 4;
        } else if (dist <= 200 && dist > 100) {
            switchZoolLevel = 5;
        } else if (dist <= 500 && dist > 200) {
            switchZoolLevel = 6;
        } else if (dist <= 1000 && dist > 500) {
            switchZoolLevel = 7;
        } else if (dist <= 2000 && dist > 1000) {
            switchZoolLevel = 8;
        } else if (dist <= 5000 && dist > 2000) {
            switchZoolLevel = 9;
        } else if (dist <= 10000 && dist > 5000) {
            switchZoolLevel = 10;
        } else if (dist <= 20000 && dist > 10000) {
            switchZoolLevel = 11;
        } else if (dist <= 100000 && dist > 20000) {
            switchZoolLevel = 12;
        } else if (dist <= 100000 && dist > 50000) {
            switchZoolLevel = 13;
        } else {
            switchZoolLevel = 16;
        }
        if (switchZoolLevel == 0) {
            ms = new MapStatus.Builder().target(new LatLng(latitude, longitude)).zoom(12).build();
        } else {
            ms = new MapStatus.Builder().target(new LatLng(latitude, longitude)).zoom(22 - switchZoolLevel).build();
        }
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
    }
}
