package com.orange.oy.activity.shakephoto_316;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.orange.oy.R;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.view.AppTitle;

import java.util.List;

/**
 * Created by Administrator on 2018/6/13.
 * 甩图点击定位-所在位置
 */

public class ShakeSelectLocationActivity extends BaseActivity implements View.OnClickListener {
    private ListView shakesl_listview;
    private boolean isPrecise;
    private String province, county;
    private boolean isShowLocation = true;

    /**
     * 此方法与SearchLocationActivity相同
     * 如有修改注意同步修改
     *
     * @param province
     * @param city
     * @param county
     * @param address
     * @param longitude
     * @param latitude
     */
    private void returnResult(String name, String province, String city, String county, String address, String longitude, String latitude) {
        Intent intent = new Intent();
        intent.putExtra("item1", name);
        intent.putExtra("province", province);
        intent.putExtra("city", city);
        intent.putExtra("county", county);
        intent.putExtra("address", address);
        intent.putExtra("longitude", longitude);
        intent.putExtra("latitude", latitude);
        intent.putExtra("isShowLocation", isShowLocation);
        setResult(RESULT_OK, intent);
        baseFinish();
    }

    private View shakesl_item1, shakesl_item2, shakesl_item3;
    private ImageView shakesl_item4;
    private TextView shakesl_item1_2, shakesl_item2_2, shakesl_item3_2;
    private View shakesl_item4_layout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shakeselectlocation);
        AppTitle shakesl_title = (AppTitle) findViewById(R.id.shakesl_title);
        shakesl_title.settingName("所在位置");
        shakesl_title.showBack(new AppTitle.OnBackClickForAppTitle() {
            public void onBack() {
                Intent intent = new Intent();
                intent.putExtra("isShowLocation", isShowLocation);
                setResult(RESULT_OK, intent);
                baseFinish();
            }
        });
        isPrecise = getIntent().getBooleanExtra("isPrecise", true);
        isShowLocation = getIntent().getBooleanExtra("isShowLocation", true);
        shakesl_item1 = findViewById(R.id.shakesl_item1);
        shakesl_item2 = findViewById(R.id.shakesl_item2);
        shakesl_item3 = findViewById(R.id.shakesl_item3);
        shakesl_item4_layout = findViewById(R.id.shakesl_item4_layout);
        shakesl_item4 = (ImageView) findViewById(R.id.shakesl_item4);
        shakesl_item1_2 = (TextView) findViewById(R.id.shakesl_item1_2);
        shakesl_item2_2 = (TextView) findViewById(R.id.shakesl_item2_2);
        shakesl_item3_2 = (TextView) findViewById(R.id.shakesl_item3_2);
        shakesl_listview = (ListView) findViewById(R.id.shakesl_listview);
        View search_main_edit = findViewById(R.id.search_main_edit);
        search_main_edit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ShakeSelectLocationActivity.this, SearchLocationActivity.class);
                intent.putExtra("isPrecise", isPrecise);
                intent.putExtra("title", "城市定位");
                startActivityForResult(intent, 1);
            }
        });
        shakesl_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view.getTag() != null && view.getTag() instanceof PoiInfo) {
                    PoiInfo poiInfo = (PoiInfo) view.getTag();
                    returnResult(poiInfo.name, province, poiInfo.city, county, poiInfo.address, poiInfo.location.longitude + "",
                            poiInfo.location.latitude + "");
                }
            }
        });
        initLocation();
        if (isPrecise) {//精准定位
            shakesl_item1.setVisibility(View.GONE);
            shakesl_item2.setVisibility(View.GONE);
            shakesl_item3.setVisibility(View.VISIBLE);
            shakesl_item4_layout.setVisibility(View.GONE);
            String province = getIntent().getStringExtra("province");
            String city = getIntent().getStringExtra("city");
            String result;
            if (province.equals(city)) {
                result = city;
            } else {
                result = province + " " + city;
            }
            result = result + " " + getIntent().getStringExtra("address");
            shakesl_item3_2.setText(result);
        } else {//模糊定位
            shakesl_item1.setVisibility(View.VISIBLE);
            shakesl_item2.setVisibility(View.VISIBLE);
            shakesl_item3.setVisibility(View.GONE);
            shakesl_item4_layout.setVisibility(View.VISIBLE);
            shakesl_item1_2.setText(getIntent().getStringExtra("place_name"));
            String province = getIntent().getStringExtra("province");
            String city = getIntent().getStringExtra("city");
            String result;
            if (province.equals(city)) {
                result = city;
            } else {
                result = province + " " + city;
            }
            shakesl_item2_2.setText(result);
        }
        shakesl_item4.setOnClickListener(this);
        if (isShowLocation) {
            shakesl_item4.setImageResource(R.mipmap.round_notselect);
        } else {
            shakesl_item4.setImageResource(R.mipmap.round_selected);
        }
    }

    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("isShowLocation", isShowLocation);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shakesl_item4: {//不显示位置
                isShowLocation = !isShowLocation;
                if (isShowLocation) {
                    shakesl_item4.setImageResource(R.mipmap.round_notselect);
                } else {
                    shakesl_item4.setImageResource(R.mipmap.round_selected);
                }
            }
            break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1: {
                if (resultCode == RESULT_OK) {
                    data.putExtra("isShowLocation", isShowLocation);
                    setResult(RESULT_OK, data);
                    baseFinish();
                }
            }
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class MyAdapter extends BaseAdapter {
        List<PoiInfo> list;

        MyAdapter(List<PoiInfo> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            if (convertView == null) {
                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        Tools.dipToPx(ShakeSelectLocationActivity.this, 45));
                convertView = textView = new TextView(ShakeSelectLocationActivity.this);
                textView.setTextSize(14);
                textView.setTextColor(0xFF231916);
                textView.setPadding(Tools.dipToPx(ShakeSelectLocationActivity.this, 15), 0, 0, 0);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                textView.setLayoutParams(lp);
            } else {
                textView = (TextView) convertView;
            }
            PoiInfo poiInfo = list.get(position);
            textView.setText(poiInfo.name);
            convertView.setTag(poiInfo);
            return convertView;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (mLocationClient != null && !mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
        if (geoCoder != null) {
            geoCoder.destroy();
        }
    }

    /**
     * 定位客户端
     */
    public LocationClient mLocationClient = null;
    public MyLocationListenner myListener = new MyLocationListenner();
    private GeoCoder geoCoder = null;
    public static double location_latitude, location_longitude;
    private LatLng MyLatLng;

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
        mLocationClient.setLocOption(option);
    }

    private class MyLocationListenner implements BDLocationListener {
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
//                Tools.showToast(TaskLocationActivity.this, getResources().getString(R.string.location_fail2));
                return;
            }
            location_latitude = location.getLatitude();
            location_longitude = location.getLongitude();
            MyLatLng = new LatLng(location_latitude, location_longitude);
            geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(MyLatLng));
        }

        public void onConnectHotSpotMessage(String s, int i) {
        }

        public void onReceivePoi() {
        }
    }

    private OnGetGeoCoderResultListener onGetGeoCoderResultListener = new OnGetGeoCoderResultListener() {
        public void onGetGeoCodeResult(GeoCodeResult result) {
        }

        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
            if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                Tools.showToast(ShakeSelectLocationActivity.this, "位置查找失败");
            } else {
                province = reverseGeoCodeResult.getAddressDetail().province;
                county = reverseGeoCodeResult.getAddressDetail().district;
                MyAdapter myAdapter = new MyAdapter(reverseGeoCodeResult.getPoiList());
                shakesl_listview.setAdapter(myAdapter);
            }
        }
    };
}
