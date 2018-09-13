package com.orange.oy.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.OfflineDBHelper;
import com.orange.oy.info.TaskDetailLeftInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 离线项目页
 */
public class OfflineProjectActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, OnGetGeoCoderResultListener {
    public void onBack() {
        baseFinish();
    }

    private void initNetworkConnection() {
        getData = new NetworkConnection(OfflineProjectActivity.this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("page", page + "");
                params.put("usermobile", AppInfo.getName(OfflineProjectActivity.this));
                params.put("city", localCity);
//                String search = task_search.getText().toString().trim();
                String search = "";
                if (!TextUtils.isEmpty(search))
                    params.put("projectname", search);
                return params;
            }
        };
    }

    protected void onStop() {
        super.onStop();
        if (getData != null) {
            getData.stop(Urls.Projectlist);
        }
    }

    private ArrayList<TaskDetailLeftInfo> list;
    private NetworkConnection getData;
    private PullToRefreshListView offlineproject_listview;
    private int page;
    private MyAdapter myAdapter;
    private OfflineDBHelper offlineDBHelper;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offlineproject);
        offlineDBHelper = new OfflineDBHelper(this);
        initNetworkConnection();
        AppTitle offlineproject_title = (AppTitle) findViewById(R.id.offlineproject_title);
        offlineproject_title.settingName("下载任务");
        offlineproject_title.showBack(this);
        localCity = getIntent().getStringExtra("city");
        offlineproject_listview = (PullToRefreshListView) findViewById(R.id.offlineproject_listview);
        offlineproject_listview.setMode(PullToRefreshBase.Mode.DISABLED);
        offlineproject_listview.setPullLabel(getResources().getString(R.string.listview_down));
        offlineproject_listview.setRefreshingLabel(getResources().getString(R.string.listview_refush));
        offlineproject_listview.setReleaseLabel(getResources().getString(R.string.listview_down2));
        offlineproject_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshData();
            }

            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                getData();
            }
        });
        list = new ArrayList<TaskDetailLeftInfo>();
        myAdapter = new MyAdapter();
        offlineproject_listview.setAdapter(myAdapter);
        offlineproject_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TaskDetailLeftInfo taskDetailLeftInfo = list.get(position - 1);
                Intent intent = new Intent(OfflineProjectActivity.this, OfflineStoreActivity.class);
                intent.putExtra("projectid", taskDetailLeftInfo.getId());
                intent.putExtra("is_taskphoto", taskDetailLeftInfo.getIs_taskphoto());
                intent.putExtra("city", localCity);
                startActivity(intent);
            }
        });
        if (!TextUtils.isEmpty(localCity)) {
            refreshData();
        } else {
            Tools.showToast(OfflineProjectActivity.this, "显示已离线项目");
            list = offlineDBHelper.getProjectList(AppInfo.getName(OfflineProjectActivity.this));
            offlineproject_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            myAdapter.notifyDataSetChanged();
        }
//        checkLocation();
    }

    private void refreshData() {
        page = 1;
        getData();
    }

    private void getData() {
        getData.sendPostRequest(Urls.Projectlist, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        JSONArray jsonArray = jsonObject.getJSONArray("datas");
                        if (list == null) {
                            page = 1;
                            list = new ArrayList<TaskDetailLeftInfo>();
                        } else {
                            if (page == 1)
                                list.clear();
                        }
                        int length = jsonArray.length();
                        for (int i = 0; i < length; i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            String is_download = jsonObject.getString("is_download");
                            if (is_download.equals("0")) {
                                continue;
                            }
                            TaskDetailLeftInfo taskInfo = new TaskDetailLeftInfo();
                            taskInfo.setName(jsonObject.getString("project_name"));
                            taskInfo.setIs_taskphoto(jsonObject.getString("is_takephoto"));
                            taskInfo.setId(jsonObject.getString("id"));
                            list.add(taskInfo);
                        }
                        offlineproject_listview.onRefreshComplete();
                        if (length < 15) {
                            offlineproject_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            offlineproject_listview.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                        myAdapter.notifyDataSetChanged();
                    } else {
                        Tools.showToast(OfflineProjectActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(OfflineProjectActivity.this, getResources().getString(R.string.network_error));
                }
                offlineproject_listview.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                offlineproject_listview.onRefreshComplete();
//                Tools.showToast(OfflineProjectActivity.this, getResources().getString(R.string.network_volleyerror));
                Tools.showToast(OfflineProjectActivity.this, "显示已离线项目");
                list = offlineDBHelper.getProjectList(AppInfo.getName(OfflineProjectActivity.this));
                offlineproject_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                myAdapter.notifyDataSetChanged();
            }
        });
    }

    class MyAdapter extends BaseAdapter {

        public int getCount() {
            return list.size();
        }

        public Object getItem(int position) {
            return list.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            if (convertView == null) {
                convertView = Tools.loadLayout(OfflineProjectActivity.this, R.layout.item_listview_offlineproject);
                textView = (TextView) convertView.findViewById(R.id.item_listview_name);
                convertView.setTag(textView);
            } else {
                textView = (TextView) convertView.getTag();
            }
            textView.setText(list.get(position).getName());
            return convertView;
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        switch (requestCode) {
            case AppInfo.REQUEST_CODE_ASK_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initLocation();
                } else {
                    Tools.showToast(OfflineProjectActivity.this, "定位权限获取失败");
                    AppInfo.setOpenLocation(OfflineProjectActivity.this, false);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 定位客户端
     */
    public LocationClient mLocationClient = null;
    public MyLocationListenner myListener = new MyLocationListenner();
    private GeoCoder mSearch = null;
    public static double location_latitude, location_longitude;

    private void checkLocation() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, AppInfo
                        .REQUEST_CODE_ASK_LOCATION);
            } else {
                initLocation();
            }
        } else {
            initLocation();
        }
    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        if (mLocationClient != null && mLocationClient.isStarted()) {
            return;
        }
        location_latitude = 0;
        location_longitude = 0;
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
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
    }

    public class MyLocationListenner implements BDLocationListener {

        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                Tools.showToast(OfflineProjectActivity.this, getResources().getString(R.string.location_fail));
                return;
            }
            double location_latitude = location.getLatitude();
            double location_longitude = location.getLongitude();
            LatLng ptCenter = new LatLng(location_latitude, location_longitude);
            mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ptCenter));
        }

        public void onConnectHotSpotMessage(String s, int i) {

        }

        public void onReceivePoi(BDLocation arg0) {
        }
    }

    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
    }

    private String localCity;

    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Tools.showToast(OfflineProjectActivity.this, getResources().getString(R.string.location_fail));
            return;
        }
        localCity = reverseGeoCodeResult.getAddressDetail().city;
        if (!TextUtils.isEmpty(localCity)) {
            refreshData();
        } else {
            Tools.showToast(OfflineProjectActivity.this, getResources().getString(R.string.location_fail));
        }
    }

}
