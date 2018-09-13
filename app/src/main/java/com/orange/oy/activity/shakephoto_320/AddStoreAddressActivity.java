package com.orange.oy.activity.shakephoto_320;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
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
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.orange.oy.R;
import com.orange.oy.activity.shakephoto_316.CreateLocationclassActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.fragment.CitysearchFragment;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.view.AppTitle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.orange.oy.network.Urls.AddMerchantOutlet;

/**
 * Created by Administrator on 2018/6/13.
 * 添加分店地址
 */

public class AddStoreAddressActivity extends BaseActivity implements View.OnClickListener,
        CitysearchFragment.OnCitysearchExitClickListener, CitysearchFragment.OnCitysearchItemClickListener {
    protected void onStop() {
        super.onStop();
        if (addMerchantOutlet != null) {
            addMerchantOutlet.stop(AddMerchantOutlet);
        }
    }

    private TextView createlocation_item2;
    private TextView createlocation_item3;
    private TextView createlocation_item1;
    private EditText createlocation_item5;
    private TextView createlocation_item4;
    private View fragmentRoot, main;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private ReverseGeoCodeResult reverseGeoCodeResult;
    private NetworkConnection addMerchantOutlet;
    private View createlocation_map_title;
    private String merchant_id;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addstore_address);
        merchant_id = getIntent().getStringExtra("merchant_id");
        addMerchantOutlet = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(AddStoreAddressActivity.this));
                params.put("merchant_id", merchant_id); //商户id
                params.put("outlet_name", createlocation_item1.getText().toString().trim());  //店铺名称
                params.put("outlet_num", createlocation_item5.getText().toString().trim());  //店铺编号
                params.put("province", reverseGeoCodeResult.getAddressDetail().province);
                params.put("city", reverseGeoCodeResult.getAddressDetail().city);
                params.put("town", reverseGeoCodeResult.getAddressDetail().district);
                params.put("outlet_address", createlocation_item3.getText().toString().trim());  //详细地址
                String item4 = createlocation_item4.getText().toString();
                params.put("place_id", createlocation_item4.getTag() + "");  // 场所类型id
                params.put("place_name", item4);  //场所类型名称
                //  params.put("outlet_address", reverseGeoCodeResult.getAddressDetail().street);
                // params.put("longitude", reverseGeoCodeResult.getLocation().longitude + "");
                // params.put("latitude", reverseGeoCodeResult.getLocation().latitude + "");
                return params;
            }
        };
        main = findViewById(R.id.main);
        fragmentRoot = findViewById(R.id.fragmentRoot);
        AppTitle createlocation_title = (AppTitle) findViewById(R.id.createlocation_title);
        createlocation_title.settingName("添加分店");
        createlocation_title.showBack(new AppTitle.OnBackClickForAppTitle() {
            public void onBack() {
                baseFinish();
            }
        });
        createlocation_map_title = findViewById(R.id.createlocation_map_title);
        createlocation_item1 = (TextView) findViewById(R.id.createlocation_item1);
        createlocation_item2 = (TextView) findViewById(R.id.createlocation_item2);
        createlocation_item3 = (TextView) findViewById(R.id.createlocation_item3);
        createlocation_item4 = (TextView) findViewById(R.id.createlocation_item4);
        createlocation_item5 = (EditText) findViewById(R.id.createlocation_item5);
//        createlocation_item2.setOnClickListener(this);
        createlocation_item4.setOnClickListener(this);

        initMap();
        findViewById(R.id.createlocation_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (reverseGeoCodeResult == null) {
                    Tools.showToast(AddStoreAddressActivity.this, "还没获取到位置");
                    return;
                }
                if (TextUtils.isEmpty(createlocation_item1.getText().toString())) {
                    Tools.showToast(AddStoreAddressActivity.this, "请填写位置名称");
                    return;
                }
                if (TextUtils.isEmpty(createlocation_item4.getText().toString())) {
                    Tools.showToast(AddStoreAddressActivity.this, "请输入所属类型");
                    return;
                }
                if (TextUtils.isEmpty(createlocation_item5.getText().toString())) {
                    Tools.showToast(AddStoreAddressActivity.this, "请输入分店编号");
                    return;
                }
                sendData();
            }
        });
    }

    private void sendData() {
        addMerchantOutlet.sendPostRequest(AddMerchantOutlet, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (200 == jsonObject.getInt("code")) {

                        StoreAddressActivity.isRefresh = true;
                        baseFinish();

                    } else {
                        Tools.showToast(AddStoreAddressActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(AddStoreAddressActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(AddStoreAddressActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private void initMap() {
        // 地图初始化
        // 获取地图控件引用
        mMapView = (MapView) findViewById(R.id.createlocation_map);
        mMapView.showZoomControls(true);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode
                .NORMAL, true, null));
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode
                .NORMAL, true, null, getResources().getColor(R.color.aplumMapcolor), getResources().getColor(R.color.aplumMapcolor)));
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            public void onMapClick(LatLng point) {
                settingOverlay(point);
            }

            public boolean onMapPoiClick(MapPoi mapPoi) {
                settingOverlay(mapPoi.getPosition());
                return false;
            }
        });
        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            public void onTouch(MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE &&
                        createlocation_map_title.getVisibility() == View.VISIBLE) {
                    createlocation_map_title.setVisibility(View.GONE);
                }
            }
        });
        initLocation();
    }

    private void settingOverlay(LatLng point) {
        // 构建Marker图标
        if (bitmap == null)
            bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.createlocation_ico);
        mBaiduMap.clear();
        // 构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
        // 在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
        //在地图上添加Marker，并显示
        MapStatus mMapStatus = new MapStatus.Builder().target(point).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        mBaiduMap.addOverlay(option);
        mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(point));
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createlocation_item2: {//选择地区
                main.setVisibility(View.GONE);
                fragmentRoot.setVisibility(View.VISIBLE);
                creatCitysearchFragment();
            }
            break;
            case R.id.createlocation_item4: {
                Intent intent = new Intent(AddStoreAddressActivity.this, CreateLocationclassActivity.class);
                startActivityForResult(intent, 1);
            }
            break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1: {
                if (resultCode == RESULT_OK) {
                    createlocation_item4.setTag(data.getStringExtra("cap_id"));
                    createlocation_item4.setText(data.getStringExtra("place_name"));
                }
            }
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private FragmentManager fMgr;
    private CitysearchFragment citysearchFragment;

    /**
     * 城市定位页面
     */
    private void creatCitysearchFragment() {
        if (fMgr == null) {
            fMgr = getSupportFragmentManager();
        }
        FragmentTransaction ft = fMgr.beginTransaction();
        citysearchFragment = (CitysearchFragment) fMgr.findFragmentByTag("citysearchFragment");
        if (citysearchFragment == null) {
            citysearchFragment = new CitysearchFragment();
            citysearchFragment.setOnCitysearchExitClickListener(this);
            citysearchFragment.setOnCitysearchItemClickListener(this);
            ft.replace(R.id.fragmentRoot, citysearchFragment, "citysearchFragment");
            ft.addToBackStack("citysearchFragment");
        } else {
            ft.show(citysearchFragment);
        }
        ft.commit();
    }

    public void exitClick() {
        if (citysearchFragment != null) {
            fragmentRoot.setVisibility(View.GONE);
            main.setVisibility(View.VISIBLE);
            FragmentTransaction ft = fMgr.beginTransaction();
            ft.hide(citysearchFragment);
            ft.commit();
        }
    }

    public void ItemClick(Map<String, String> map) {
        if (TextUtils.isEmpty(map.get("county")) || "null".equals(map.get("county"))) {
            createlocation_item2.setText(map.get("name"));
        } else {
            createlocation_item2.setText(map.get("name") + "-" + map.get("county"));
        }
        fragmentRoot.setVisibility(View.GONE);
        main.setVisibility(View.VISIBLE);
        FragmentTransaction ft = fMgr.beginTransaction();
        ft.hide(citysearchFragment);
        ft.commit();
    }

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
            Tools.showToast(AddStoreAddressActivity.this, "正在定位...");
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
        mLocationClient.setLocOption(option);
        Tools.showToast(AddStoreAddressActivity.this, "正在定位...");
    }

    private BitmapDescriptor bitmap;

    private class MyLocationListenner implements BDLocationListener {
        public void onReceiveLocation(BDLocation location) {
            mLocationClient.stop();
            if (location == null) {
                Tools.showToast(AddStoreAddressActivity.this, getResources().getString(R.string.location_fail));
                return;
            }
            location_latitude = location.getLatitude();
            location_longitude = location.getLongitude();
            LatLng point = new LatLng(location_latitude, location_longitude);
            settingOverlay(point);
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
            } else {
                String string, string1;
                AddStoreAddressActivity.this.reverseGeoCodeResult = reverseGeoCodeResult;
                ReverseGeoCodeResult.AddressComponent addressComponent = reverseGeoCodeResult.getAddressDetail();
//                int index = addressComponent.streetNumber.lastIndexOf("号");
//                if (index > 0) {
//                    try {
//                        String str = String.valueOf(addressComponent.streetNumber.charAt(index - 1));
//                        Tools.d(str);
//                        if (Tools.StringToInt(str) != -1) {
//                            addressComponent.streetNumber = addressComponent.streetNumber.substring(0, index + 1);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
                if (addressComponent.province.endsWith("市")) {
                    string = addressComponent.city + addressComponent.district +
                            addressComponent.street + addressComponent.streetNumber;
                    string1 = addressComponent.city + " " + addressComponent.district;
                } else {
                    string = addressComponent.province + addressComponent.city +
                            addressComponent.district + addressComponent.street + addressComponent.streetNumber;
                    string1 = addressComponent.province + " " + addressComponent.city + " " + addressComponent.district;
                }
                createlocation_item2.setText(string1);
                createlocation_item3.setText(string);
                if (reverseGeoCodeResult.getPoiList() != null && !reverseGeoCodeResult.getPoiList().isEmpty()) {
                    createlocation_item1.setText(reverseGeoCodeResult.getPoiList().get(0).name);
                }
            }
        }
    };

}
