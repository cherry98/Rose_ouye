package com.orange.oy.activity.shakephoto_316;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiAddrInfo;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.orange.oy.R.id.createlocation_item1;
import static com.orange.oy.R.id.light;

/**
 * Created by Administrator on 2018/6/12.
 * 搜索位置
 */

public class SearchLocationActivity extends BaseActivity {

    private ListView searchloc_listview;
    private PoiSearch poiSearch;
    private MyHandler myHandler = new MyHandler();
    private String province, county;
    private EditText search_main_edit;

    private void returnResult(String name, String province, String city, String county, String address,
                              String longitude, String latitude) {
        Intent intent = new Intent();
        intent.putExtra("item1", name);
        intent.putExtra("province", province);
        intent.putExtra("city", city);
        intent.putExtra("county", county);
        intent.putExtra("address", address);
        intent.putExtra("longitude", longitude);
        intent.putExtra("latitude", latitude);
        setResult(RESULT_OK, intent);
        baseFinish();
    }

    private void createAddress(final String province, final String city, final String county,
                               final String address, final String address2, final String longitude, final String latitude) {
        createAddress = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(SearchLocationActivity.this));
                params.put("province", province);
                params.put("city", city);
                params.put("county", county);
                params.put("address", address2);
                params.put("longitude", longitude);
                params.put("latitude", latitude + "");
                params.put("address_name", address);
                String which_page = getIntent().getStringExtra("which_page");
                if (which_page != null && "0".equals(which_page)) {//任务模板需要
                    params.put("template_id", getIntent().getStringExtra("template_id"));
                }
                return params;
            }
        };
        createAddress.sendPostRequest(Urls.CreateAddress, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        returnResult(address, province, city, county, address2, longitude, latitude);
                    } else {
                        Tools.showToast(SearchLocationActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(SearchLocationActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(SearchLocationActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private View search_main_edit_cancel;
    private String template_id, which_page;
    private NetworkConnection createAddress;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchlocation);
        boolean isPrecise = getIntent().getBooleanExtra("isPrecise", false);//是否精准定位
        String title = getIntent().getStringExtra("title");//title
        which_page = getIntent().getStringExtra("which_page");
        template_id = getIntent().getStringExtra("template_id");
        View searchloc_tip1_creat = findViewById(R.id.searchloc_tip1_creat);
        searchloc_tip1_creat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SearchLocationActivity.this, CreateLocationActivity.class);
                if (which_page != null && "0".equals(which_page)) {//任务模板需要
                    intent.putExtra("which_page", which_page);
                    intent.putExtra("template_id", template_id);
                }
                startActivityForResult(intent, 1);
            }
        });
        View searchloc_tip1 = findViewById(R.id.searchloc_tip1);
        AppTitle appTitle = (AppTitle) findViewById(R.id.searchloc_title);
        poiSearch = PoiSearch.newInstance();
        poiSearch.setOnGetPoiSearchResultListener(poiListener);
        searchloc_listview = (ListView) findViewById(R.id.searchloc_listview);
        search_main_edit_cancel = findViewById(R.id.search_main_edit_cancel);
        search_main_edit = (EditText) findViewById(R.id.search_main_edit);
        search_main_edit.addTextChangedListener(textWatcher);
        search_main_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Message message = Message.obtain();
                    message.what = 1;
                    message.obj = v.getText().toString();
                    myHandler.sendMessageDelayed(message, 100);
                    return true;
                }
                return false;
            }
        });
        searchloc_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (view.getTag() != null && view.getTag() instanceof PoiInfo) {
                PoiInfo poiInfo = poiInfos.get(position);
                if (which_page != null && "0".equals(which_page)) {
                    createAddress(province, poiInfo.city, county, poiInfo.name, poiInfo.address, poiInfo.location.longitude + "",
                            poiInfo.location.latitude + "");
                } else {
                    returnResult(poiInfo.name, province, poiInfo.city, county, poiInfo.address, poiInfo.location.longitude + "",
                            poiInfo.location.latitude + "");
                }
//                }
            }
        });
        if (isPrecise) {
            appTitle.setVisibility(View.GONE);
            searchloc_tip1.setVisibility(View.GONE);
            searchloc_tip1_creat.setVisibility(View.GONE);
            poiInfos = getIntent().getParcelableArrayListExtra("poiInfos");
            if (poiInfos == null) {
                initLocation();
            } else {
                MyAdapter myAdapter = new MyAdapter(poiInfos);
                searchloc_listview.setAdapter(myAdapter);
            }
        } else {
            initLocation();
            searchloc_tip1.setVisibility(View.VISIBLE);
            searchloc_tip1_creat.setVisibility(View.VISIBLE);
            appTitle.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(title)) {
                appTitle.settingName("任务位置");
            } else {
                appTitle.settingName(title);
            }
            appTitle.showBack(new AppTitle.OnBackClickForAppTitle() {
                public void onBack() {
                    baseFinish();
                }
            });
        }
        search_main_edit_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                search_main_edit.setText("");
                Message message = Message.obtain();
                message.what = 1;
                message.obj = "";
                myHandler.sendMessage(message);
            }
        });
    }

    private TextWatcher textWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (TextUtils.isEmpty(s)) {
                search_main_edit_cancel.setVisibility(View.INVISIBLE);
            } else {
                search_main_edit_cancel.setVisibility(View.VISIBLE);
            }
        }

        public void afterTextChanged(Editable s) {

        }
    };

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1: {
                if (RESULT_OK == resultCode) {
                    setResult(RESULT_OK, data);
                    baseFinish();
                }
            }
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {

        public void onGetPoiResult(PoiResult result) {
            if (result == null) {
                Tools.d("检索失败");
                return;
            }
            if (result.getAllPoi() != null) {
                poiInfos = result.getAllPoi();
                MyAdapter myAdapter = new MyAdapter(poiInfos);
                searchloc_listview.setAdapter(myAdapter);
            } else if (result.getAllAddr() != null) {
                for (PoiAddrInfo poiAddrInfo : result.getAllAddr()) {
                    Tools.d(poiAddrInfo.address);
                }
            } else if (result.getSuggestCityList() != null) {
                for (CityInfo poiAddrInfo : result.getSuggestCityList()) {
                    Tools.d(poiAddrInfo.city);
                }
            }
        }

        public void onGetPoiDetailResult(PoiDetailResult result) {
            //获取Place详情页检索结果
        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    };
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
                Tools.showToast(SearchLocationActivity.this, "位置查找失败");
            } else {
                province = reverseGeoCodeResult.getAddressDetail().province;
                county = reverseGeoCodeResult.getAddressDetail().district;
                if (reverseGeoCodeResult.getPoiList() != null) {
                    poiInfos = reverseGeoCodeResult.getPoiList();
                    MyAdapter myAdapter = new MyAdapter(poiInfos);
                    searchloc_listview.setAdapter(myAdapter);
                }
            }
        }
    };
    private List<PoiInfo> poiInfos;

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
//            TextView textView;
            ViewHolder viewHolder;
            if (convertView == null) {
//                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                        Tools.dipToPx(SearchLocationActivity.this, 45));
//                convertView = textView = new TextView(SearchLocationActivity.this);
//                textView.setTextSize(14);
//                textView.setTextColor(0xFF231916);
//                textView.setPadding(Tools.dipToPx(SearchLocationActivity.this, 15), 0, 0, 0);
//                textView.setGravity(Gravity.CENTER_VERTICAL);
//                textView.setLayoutParams(lp);
                viewHolder = new ViewHolder();
                convertView = Tools.loadLayout(SearchLocationActivity.this, R.layout.item_locationlist);
                viewHolder.itemlist_placename = (TextView) convertView.findViewById(R.id.itemlist_placename);
                viewHolder.itemlist_address = (TextView) convertView.findViewById(R.id.itemlist_address);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            PoiInfo poiInfo = list.get(position);
            viewHolder.itemlist_placename.setText(poiInfo.name);
            viewHolder.itemlist_address.setText(poiInfo.address);
            return convertView;
        }
    }

    class ViewHolder {
        private TextView itemlist_placename, itemlist_address;
    }

    private LatLng MyLatLng;

    private class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    if (MyLatLng != null) {
//                        poiSearch.searchNearby(new PoiNearbySearchOption()
//                                .keyword(msg.obj + "")
//                                .sortType(PoiSortType.distance_from_near_to_far)
//                                .location(MyLatLng)
//                                .radius(3000)
//                                .pageNum(30));
                        List<PoiInfo> searchPoiInfos = new ArrayList<>();
                        for (PoiInfo poiInfo : poiInfos) {
                            if (poiInfo.address.contains(msg.obj + "") || poiInfo.name.contains(msg.obj + "")) {
                                searchPoiInfos.add(poiInfo);
                                MyAdapter myAdapter = new MyAdapter(searchPoiInfos);
                                searchloc_listview.setAdapter(myAdapter);
                            }
                        }
                    }
                }
                break;
            }
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (poiSearch != null) {
            poiSearch.destroy();
        }
        if (mLocationClient != null) {
            mLocationClient.stop();
        }
        if (geoCoder != null) {
            geoCoder.destroy();
        }
    }
}
