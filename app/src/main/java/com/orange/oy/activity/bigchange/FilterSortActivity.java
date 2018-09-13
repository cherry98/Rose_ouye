package com.orange.oy.activity.bigchange;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.activity.StoreDescActivity;
import com.orange.oy.activity.TaskitemDetailActivity_12;
import com.orange.oy.activity.black.BlackillustrateActivity;
import com.orange.oy.activity.guide.TaskLocationActivity;
import com.orange.oy.activity.newtask.MyTaskListActivity;
import com.orange.oy.adapter.TaskGrabAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
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
 * 主页的筛选排序页面
 */

public class FilterSortActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {

    public void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.filtersort_title);
        appTitle.settingName("筛选");
        appTitle.showBack(this);
    }

    public void initNetworkConnection() {
        selectOutletList = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(FilterSortActivity.this));
                params.put("type", type);//搜素类型
                params.put("lon", longitude + "");
                params.put("lat", latitude + "");
                params.put("city", city);
                String keyword = filtersort_search.getText().toString().trim();
                if (!TextUtils.isEmpty(keyword)) {
                    params.put("keyword", keyword);
                }
                params.put("page", page + "");
                return params;
            }
        };
        selectOutletList.setIsShowDialog(true);
        rob = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(FilterSortActivity.this));
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
                params.put("usermobile", AppInfo.getName(FilterSortActivity.this));
                params.put("token", Tools.getToken());
                params.put("outletid", storeid);
                params.put("lon", longitude + "");
                params.put("lat", latitude + "");
                params.put("address", address);
                return params;
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (selectOutletList != null) {
            selectOutletList.stop(Urls.SelectOutletList);
        }
        if (rob != null) {
            rob.stop(Urls.rob);
        }
        if (checkinvalid != null) {
            checkinvalid.stop(Urls.CheckInvalid);
        }
    }

    public PullToRefreshListView filtersort_listview;
    private EditText filtersort_search;//搜索内容
    private TextView filtersort_money, filtersort_newest, filtersort_lately;
    private NetworkConnection selectOutletList, rob, checkinvalid;
    private double longitude, latitude;
    private String city, type;//1为奖励金最高，2为最新发布，3为离我最近
    private int page = 1;
    private LocationClient mLocationClient = null;
    private BDLocationListener myListener = new MyLocationListener();
    private ArrayList<TaskDetailLeftInfo> list;
    private TaskGrabAdapter adapter;
    private String storeid;
    private String address;
    private int locType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_sort);
        initTitle();
        initNetworkConnection();
        list = new ArrayList<>();
        city = getIntent().getStringExtra("city");
        filtersort_search = (EditText) findViewById(R.id.filtersort_search);
        filtersort_money = (TextView) findViewById(R.id.filtersort_money);
        filtersort_newest = (TextView) findViewById(R.id.filtersort_newest);
        filtersort_lately = (TextView) findViewById(R.id.filtersort_lately);
        filtersort_listview = (PullToRefreshListView) findViewById(R.id.filtersort_listview);
        filtersort_money.setOnClickListener(this);
        filtersort_newest.setOnClickListener(this);
        filtersort_lately.setOnClickListener(this);
        initListview(filtersort_listview);
        checkLocation();
        initLocation();
        adapter = new TaskGrabAdapter(this, true, list);
        filtersort_listview.setAdapter(adapter);
        filtersort_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                getData();
            }
        });
        filtersort_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TaskDetailLeftInfo taskDetailLeftInfo = list.get(position - 1);
                storeid = taskDetailLeftInfo.getId();
                if (adapter != null) {
                    rob(taskDetailLeftInfo);
                }
            }
        });
    }

    public void rob(final TaskDetailLeftInfo taskDetailLeftInfo) {
        rob.sendPostRequest(Urls.rob, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String msg = jsonObject.getString("msg");
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        ConfirmDialog.showDialog(FilterSortActivity.this, "申请成功", msg, "继续申请", "开始执行", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                            @Override
                            public void leftClick(Object object) {
                                if ("1".equals(type)) {
                                    TaskLocationActivity.isRefresh = true;
                                    baseFinish();
                                } else {
                                    if (adapter != null) {
                                        adapter.notifyDataSetChanged();
                                        refreshData();
                                    }
                                }
                            }

                            @Override
                            public void rightClick(Object object) {
                                doSelectType(taskDetailLeftInfo, taskDetailLeftInfo.getType());
                            }
                        });
                    } else if (code == 3) {
                        ConfirmDialog.showDialog(FilterSortActivity.this, "申请成功", msg, null, "开始执行", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                            @Override
                            public void leftClick(Object object) {

                            }

                            @Override
                            public void rightClick(Object object) {
                                doSelectType(taskDetailLeftInfo, taskDetailLeftInfo.getType());
                            }
                        }).goneLeft();
                    } else if (code == 2) {
                        ConfirmDialog.showDialog(FilterSortActivity.this, "申请失败", msg, null, "确定", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                            @Override
                            public void leftClick(Object object) {

                            }

                            @Override
                            public void rightClick(Object object) {
                            }
                        }).goneLeft();
                    } else {
                        Tools.showToast(FilterSortActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(FilterSortActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(FilterSortActivity.this, getResources().getString(R.string.network_error));
            }
        }, null);
    }

    //判断是否可以执行
    private void doSelectType(final TaskDetailLeftInfo taskDetailLeftInfo, final String tasktype) {
        if (locType == 61 || locType == 161) {
            checkinvalid.sendPostRequest(Urls.CheckInvalid, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    Tools.d(s);
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        int code = jsonObject.getInt("code");
                        if (code == 200) {
                            doExecute(taskDetailLeftInfo, tasktype);
                        } else if (code == 2) {
                            ConfirmDialog.showDialog(FilterSortActivity.this, null, jsonObject.getString("msg"), null,
                                    "确定", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                        @Override
                                        public void leftClick(Object object) {

                                        }

                                        @Override
                                        public void rightClick(Object object) {
                                        }
                                    }).goneLeft();
                        } else if (code == 3) {
                            ConfirmDialog.showDialog(FilterSortActivity.this, null, jsonObject.getString("msg"), "取消",
                                    "继续执行", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                                        @Override
                                        public void leftClick(Object object) {

                                        }

                                        @Override
                                        public void rightClick(Object object) {
                                            doExecute(taskDetailLeftInfo, tasktype);
                                        }
                                    });
                        } else {
                            Tools.showToast(FilterSortActivity.this, jsonObject.getString("msg"));
                        }
                    } catch (JSONException e) {
                        Tools.showToast(FilterSortActivity.this, getResources().getString(R.string.network_error));
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Tools.showToast(FilterSortActivity.this, getResources().getString(R.string
                            .network_volleyerror));
                }
            }, null);
        } else if (locType == 167) {
            Tools.showToast2(this, "请您检查是否开启权限，尝试重新请求定位");
        } else {
            Tools.showToast2(this, "请您检查网络是否通畅或是否允许访问位置信息,尝试重新请求定位");
        }
    }


    private void doExecute(TaskDetailLeftInfo taskDetailLeftInfo, String tasktype) {
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
                intent.putExtra("project_type",taskDetailLeftInfo.getProject_type());
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
            intent.putExtra("store_num", taskDetailLeftInfo.getNumber());
            intent.putExtra("photo_compression", taskDetailLeftInfo.getPhoto_compression());
            intent.putExtra("isUpdata", taskDetailLeftInfo.getIsUpdata());
            intent.putExtra("province", taskDetailLeftInfo.getCity());
            intent.putExtra("city", taskDetailLeftInfo.getCity2());
            intent.putExtra("address", taskDetailLeftInfo.getCity3());
            intent.putExtra("is_desc", taskDetailLeftInfo.getIs_desc());
            intent.putExtra("isNormal", true);
            startActivity(intent);
        }
    }

    private void refreshData() {
        page = 1;
        getData();
    }

    private void initListview(PullToRefreshListView listview) {
        listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listview.setPullLabel(getResources().getString(R.string.listview_down));// 刚下拉时，显示的提示
        listview.setRefreshingLabel(getResources().getString(R.string.listview_refush));// 刷新时
        listview.setReleaseLabel(getResources().getString(R.string.listview_down2));// 下来达到一定距离时，显示的提示
    }

    public void getData() {
        selectOutletList.sendPostRequest(Urls.SelectOutletList, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (list == null) {
                            list = new ArrayList<TaskDetailLeftInfo>();
                        } else {
                            if (page == 1) {
                                list.clear();
                            }
                        }
                        JSONArray jsonArray = jsonObject.getJSONArray("datas");
                        if (jsonArray != null) {
                            int length = jsonArray.length();
                            for (int i = 0; i < length; i++) {
                                TaskDetailLeftInfo taskDetailLeftInfo = new TaskDetailLeftInfo();
                                jsonObject = jsonArray.getJSONObject(i);
                                String isdetail = jsonObject.getString("isdetail");
                                String timeDetail = "";
                                if ("0".equals(isdetail)) {
                                    String[] datelist = jsonObject.getString("datelist").replaceAll("\\[\"", "").replaceAll("\"]",
                                            "").split("\",\"");
                                    for (String str : datelist) {
                                        if (TextUtils.isEmpty(timeDetail)) {
                                            timeDetail += str;
                                        } else {
                                            timeDetail = timeDetail + "\n" + str;
                                        }
                                    }
                                } else {
                                    for (int index = 1; index < 8; index++) {
                                        String date = jsonObject.getString("date" + index);
                                        if (!TextUtils.isEmpty(date) && !"null".equals(date)) {
                                            String detailtemp = jsonObject.getString("details" + index);
                                            if (!"null".equals(detailtemp)) {
                                                String[] ss = detailtemp.replaceAll("\\[\"", "").replaceAll("\"]", "").split("\",\"");
                                                for (int j = 0; j < ss.length; j++) {
                                                    date = date + " " + ((TextUtils.isEmpty(ss[j])) ? "" : ss[j]);
                                                }
                                            }
                                            if (TextUtils.isEmpty(timeDetail)) {
                                                timeDetail = date;
                                            } else {
                                                timeDetail = timeDetail + "\n" + date;
                                            }
                                        }
                                    }
                                }
                                taskDetailLeftInfo.setId(jsonObject.getString("storeid"));
                                taskDetailLeftInfo.setName(jsonObject.getString("storeName"));
                                taskDetailLeftInfo.setCode(jsonObject.getString("storeNum"));
                                taskDetailLeftInfo.setCity(jsonObject.getString("province"));
                                taskDetailLeftInfo.setCity2(jsonObject.getString("city"));
                                taskDetailLeftInfo.setCity3(jsonObject.getString("address"));
                                taskDetailLeftInfo.setTimedetail(timeDetail);
                                taskDetailLeftInfo.setMoney(jsonObject.getString("money"));
                                taskDetailLeftInfo.setMoney_unit(jsonObject.getString("money_unit"));
                                list.add(taskDetailLeftInfo);
                            }
                            filtersort_listview.onRefreshComplete();
                            if (length < 15) {
                                filtersort_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            } else {
                                filtersort_listview.setMode(PullToRefreshBase.Mode.BOTH);
                            }
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            } else {
                                adapter = new TaskGrabAdapter(FilterSortActivity.this, true, list);
                                filtersort_listview.setAdapter(adapter);
                            }
                        }
                    } else {
                        Tools.showToast(FilterSortActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(FilterSortActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                filtersort_listview.onRefreshComplete();
                Tools.showToast(FilterSortActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //1为奖励金最高，2为最新发布，3为离我最近
            case R.id.filtersort_newest: {//按照最新上线搜素
                filtersort_newest.setTextColor(getResources().getColor(R.color.makesure));
                filtersort_money.setTextColor(getResources().getColor(R.color.citysearch_text));
                filtersort_lately.setTextColor(getResources().getColor(R.color.citysearch_text));
                type = "2";
                getData();
            }
            break;
            case R.id.filtersort_money: {//按照奖励金最高搜素
                filtersort_newest.setTextColor(getResources().getColor(R.color.citysearch_text));
                filtersort_money.setTextColor(getResources().getColor(R.color.makesure));
                filtersort_lately.setTextColor(getResources().getColor(R.color.citysearch_text));
                type = "1";
                getData();
            }
            break;
            case R.id.filtersort_lately: {//按照据我最近搜素
                filtersort_newest.setTextColor(getResources().getColor(R.color.citysearch_text));
                filtersort_money.setTextColor(getResources().getColor(R.color.citysearch_text));
                filtersort_lately.setTextColor(getResources().getColor(R.color.makesure));
                type = "3";
                getData();
            }
            break;
        }
    }

    /**
     * 定位权限
     */
    private void checkLocation() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, AppInfo
                        .REQUEST_CODE_ASK_LOCATION);
                return;
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        switch (requestCode) {
            case AppInfo.REQUEST_CODE_ASK_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkLocation();
                } else {
                    Tools.showToast(FilterSortActivity.this, "定位权限获取失败");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 定位
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

    private void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll");
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            mLocationClient.stop();
            if (bdLocation == null) {
                Tools.showToast(FilterSortActivity.this, getResources().getString(R.string.location_fail));
                return;
            }
            latitude = bdLocation.getLatitude();
            longitude = bdLocation.getLongitude();
            address = bdLocation.getAddrStr();
            locType = bdLocation.getLocType();
        }

        public void onConnectHotSpotMessage(String s, int i) {

        }
    }
}
