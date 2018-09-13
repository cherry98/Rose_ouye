package com.orange.oy.activity.newtask;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
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
import com.orange.oy.activity.StoreDescActivity;
import com.orange.oy.activity.TaskillustratesActivity;
import com.orange.oy.activity.TaskitemDetailActivity_12;
import com.orange.oy.activity.black.BlackillustrateActivity;
import com.orange.oy.activity.guide.TaskLocationActivity;
import com.orange.oy.activity.mycorps_314.IdentifycodeLoginActivity;
import com.orange.oy.adapter.TaskGrabAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.MyUMShareUtils;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.UMShareDialog;
import com.orange.oy.fragment.CitysearchFragment;
import com.orange.oy.fragment.TaskNewFragment;
import com.orange.oy.info.TaskDetailLeftInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.umeng.socialize.UMShareAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 抢领任务页
 */
public class TaskGrabActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle,
        View.OnClickListener, AdapterView.OnItemClickListener, OnGetGeoCoderResultListener, CitysearchFragment.OnCitysearchExitClickListener, CitysearchFragment.OnCitysearchItemClickListener {

    private AppTitle appTitle;

    private void initTitle() {
        appTitle = (AppTitle) findViewById(R.id.taskgrab_title);
        appTitle.settingName(project_name);
        appTitle.showBack(this);
        appTitle.showIllustrate(R.mipmap.share2, new AppTitle.OnExitClickForAppTitle() {
            @Override
            public void onExit() {
                UMShareDialog.showDialog(TaskGrabActivity.this, false, new UMShareDialog.UMShareListener() {
                    @Override
                    public void shareOnclick(int type) {
                        String webUrl = Urls.ShareProject + "?&projectid=" + projectid + "&usermobile=" + AppInfo.getName(TaskGrabActivity.this) + "&sign=" + sign;
                        MyUMShareUtils.umShare(TaskGrabActivity.this, type, webUrl);
                    }
                });
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (outletList != null) {
            outletList.stop(Urls.OutletList);
        }
        if (rob != null) {
            rob.stop(Urls.rob);
        }
        if (Sign != null) {
            Sign.stop(Urls.Sign);
        }
        if (checkinvalid != null) {
            checkinvalid.stop(Urls.CheckInvalid);
        }
        getCityNameState = null;
    }

    public void initNetworkConnection() {
        outletList = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                if (!TextUtils.isEmpty(AppInfo.getKey(TaskGrabActivity.this))) {
                    params.put("token", Tools.getToken());
                    params.put("user_mobile", AppInfo.getName(TaskGrabActivity.this));
                }
                params.put("project_id", projectid);
                params.put("page", page + "");
                String city = taskgrab_city.getText().toString().trim();
                if (city.contains("-")) {
                    int index = city.indexOf("-");
                    city = city.substring(0, index);
                }
                params.put("city", city);
                params.put("province", province);
                if (!TextUtils.isEmpty(searchStr)) {
                    params.put("keyword", searchStr);
                }
                if ("1".equals(type)) {
                    params.put("store_id", data.getStringExtra("store_id"));
                    params.put("lon", longitude + "");
                    params.put("lat", latitude + "");
                }
                if (isLocation) {
                    params.put("lon", longitude + "");
                    params.put("lat", latitude + "");
                }
                return params;
            }
        };
        outletList.setIsShowDialog(true);
        rob = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(TaskGrabActivity.this));
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
                params.put("usermobile", AppInfo.getName(TaskGrabActivity.this));
                params.put("token", Tools.getToken());
                params.put("outletid", storeid);
                params.put("lon", longitude + "");
                params.put("lat", latitude + "");
                params.put("address", address);
                return params;
            }
        };
        Sign = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                String key = "projectid=" + projectid + "&usermobile=" + AppInfo.getName(TaskGrabActivity.this);
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("key", key);
                return params;
            }
        };
    }

    private String searchStr = "";

    public void showSearch() {
        appTitle.settingHint("可搜索网点名称、网点编号、省份、城市");
        appTitle.showSearch(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void afterTextChanged(Editable s) {

            }
        }, new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchStr = v.getText().toString().trim();
                    outletList.setIsShowDialog(true);
                    refreshData();
                    v.setText("");
                    return true;
                }
                return false;
            }
        });
    }

    private PullToRefreshListView taskgrab_listview;
    private TaskGrabAdapter taskGrabAdapter;
    private NetworkConnection outletList, rob, checkinvalid, Sign;
    private Intent data;
    private String projectid, city, storeid, money_unit;
    private int page;
    private TextView taskgrab_time, taskgrab_name, taskgrab_period, taskgrab_person;
    private ArrayList<TaskDetailLeftInfo> list;
    private double longitude, latitude;//经纬度
    private int locType;
    public LocationClient mLocationClient = null;
    public MyLocationListenner myListener = new MyLocationListenner();
    private GeoCoder mSearch = null;
    private String type, address, province;
    private boolean flag;//是否需要排序 true是代表点击排序 false代表申请的时候的需要获取定位
    private String project_name;
    private FragmentManager fMgr;
    private CitysearchFragment citysearchFragment;
    public static String getCityNameState = null;//城市选择的多种情况
    private TextView taskgrab_city;
    private String sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_grab);
        data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        list = new ArrayList<>();
        initNetworkConnection();
        type = data.getStringExtra("type1");
        if (!TextUtils.isEmpty(type) && type.equals("1")) {//地图页跳转
            searchStr = data.getStringExtra("storeNum");
        }
        province = data.getStringExtra("province");
        taskgrab_time = (TextView) findViewById(R.id.taskgrab_time);
        taskgrab_name = (TextView) findViewById(R.id.taskgrab_name);
        taskgrab_period = (TextView) findViewById(R.id.taskgrab_period);
        taskgrab_person = (TextView) findViewById(R.id.taskgrab_person);
        taskgrab_city = (TextView) findViewById(R.id.taskgrab_city);
        String[] s = AppInfo.getAddress(this);
        if (TextUtils.isEmpty(s[2])) {
            taskgrab_city.setText(s[1]);
        } else {
            taskgrab_city.setText(s[1] + "-" + s[2]);
        }
        projectid = data.getStringExtra("id");
        money_unit = data.getStringExtra("money_unit");
        taskgrab_time.setText("起止时间:" + data.getStringExtra("begin_date") + "~" + data.getStringExtra("end_date"));
        project_name = data.getStringExtra("project_name");
        taskgrab_name.setText(project_name);
        initTitle();
        taskgrab_period.setText("审核周期:" + data.getStringExtra("check_time") + "天");
        taskgrab_person.setText("发布商家:【" + data.getStringExtra("project_person") + "】");
        String standard_state = data.getStringExtra("standard_state");
        if ("1".equals(standard_state)) {
            findViewById(R.id.taskgrab_standard).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.taskgrab_standard).setVisibility(View.GONE);
        }
        findViewById(R.id.taskgrab_standard).setOnClickListener(this);
        findViewById(R.id.taskgrab_preview).setOnClickListener(this);
        taskgrab_listview = (PullToRefreshListView) findViewById(R.id.taskgrab_listview);
        taskgrab_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getData();
                page++;
            }
        });
        taskGrabAdapter = new TaskGrabAdapter(this, true, list);
        taskgrab_listview.setAdapter(taskGrabAdapter);
        taskgrab_listview.setOnItemClickListener(this);
        findViewById(R.id.taskgrab_location).setOnClickListener(this);
        getData();
        flag = false;
        initLocation();
        checkLocation();
        boolean isEdit = data.getBooleanExtra("isEdit", false);
        if (isEdit) {
            ConfirmDialog.showDialog(this, "恭喜您！", 2, "报名成功，您可以申请任务啦！", null, "立即申请", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                @Override
                public void leftClick(Object object) {

                }

                @Override
                public void rightClick(Object object) {

                }
            }).goneLeft();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ConfirmDialog.dissmisDialog();
                }
            }, 3000);
        }
    }

    @Override
    public void onBackPressed() {
        findViewById(R.id.fragmentRoot).setVisibility(View.GONE);
        if ("1".equals(type)) {
            TaskLocationActivity.isRefresh = true;
        }
        TaskNewFragment.isRefresh = true;
        super.onBackPressed();
    }

    @Override
    public void onBack() {
        if ("1".equals(type)) {
            TaskLocationActivity.isRefresh = true;
        }
        TaskNewFragment.isRefresh = true;
        baseFinish();
    }

    public void refreshData() {
        if (isLocation && isFirst) {
            flag = true;
            initLocation();
        }
        page = 1;
        getData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    public void getData() {
        outletList.sendPostRequest(Urls.OutletList, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                searchStr = "";
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
                        Sign();
                        JSONArray jsonArray = jsonObject.getJSONArray("datas");
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
                            taskDetailLeftInfo.setIs_exe(jsonObject.getString("is_exe"));
                            taskDetailLeftInfo.setIs_desc(jsonObject.getString("is_desc"));
                            taskDetailLeftInfo.setId(jsonObject.getString("storeid"));
                            taskDetailLeftInfo.setRob_state(jsonObject.getString("rob_state"));
                            taskDetailLeftInfo.setName(jsonObject.getString("storeName"));
                            taskDetailLeftInfo.setCode(jsonObject.getString("storeNum"));
                            taskDetailLeftInfo.setIdentity(jsonObject.getString("proxy_num"));
                            taskDetailLeftInfo.setCity(jsonObject.getString("province"));
                            taskDetailLeftInfo.setCity2(jsonObject.getString("city"));
                            taskDetailLeftInfo.setCity3(jsonObject.getString("address"));
                            taskDetailLeftInfo.setNumber(jsonObject.getString("accessed_num"));
                            taskDetailLeftInfo.setTimedetail(timeDetail);
                            taskDetailLeftInfo.setMoney(jsonObject.getString("money"));
                            taskDetailLeftInfo.setMoney_unit(money_unit);
                            taskDetailLeftInfo.setType(data.getStringExtra("type"));
                            taskDetailLeftInfo.setProjectid(projectid);
                            taskDetailLeftInfo.setProjectname(project_name);
                            taskDetailLeftInfo.setIs_taskphoto(data.getStringExtra("is_takephoto"));
                            taskDetailLeftInfo.setBrand(data.getStringExtra("brand"));
                            taskDetailLeftInfo.setIsUpdata(jsonObject.getString("is_upload"));
                            taskDetailLeftInfo.setReward_type(jsonObject.getString("reward_type"));
                            taskDetailLeftInfo.setGift_url(jsonObject.getString("gift_url"));
                            list.add(taskDetailLeftInfo);
                        }
                        taskgrab_listview.onRefreshComplete();
                        if (length < 15) {
                            taskgrab_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            taskgrab_listview.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                        if (taskGrabAdapter != null) {
                            taskGrabAdapter.notifyDataSetChanged();
                        } else {
                            taskGrabAdapter = new TaskGrabAdapter(TaskGrabActivity.this, true, list);
                            taskgrab_listview.setAdapter(taskGrabAdapter);
                        }
                    } else {
                        Tools.showToast(TaskGrabActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskGrabActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                searchStr = "";
                outletList.setIsShowDialog(false);
                taskgrab_listview.onRefreshComplete();
                Tools.showToast(TaskGrabActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    private void Sign() {
        Sign.sendPostRequest(Urls.Sign, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        sign = jsonObject.getString("msg");
                    } else {
                        Tools.showToast(TaskGrabActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskGrabActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskGrabActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private boolean isLocation = false;
    private boolean isFirst = true;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taskgrab_standard: {
                Intent intent = new Intent(this, TaskillustratesActivity.class);
                intent.putExtra("projectid", projectid);
                intent.putExtra("projectname", data.getStringExtra("project_name"));
                intent.putExtra("isShow", "0");//是否显示不再显示开始执行按钮
                startActivity(intent);
            }
            break;
            case R.id.taskgrab_location: {
                findViewById(R.id.fragmentRoot).setVisibility(View.VISIBLE);
                creatCitysearchFragment();
            }
            break;
            case R.id.taskgrab_preview: {
                Intent intent = new Intent(this, TaskitemDetailActivity_12.class);
                intent.putExtra("id", data.getStringExtra("id"));
                intent.putExtra("projectname", data.getStringExtra("projectname"));
                intent.putExtra("store_name", "网点名称");
                intent.putExtra("store_num", "网点编号");
                intent.putExtra("province", "");
                intent.putExtra("city", "");
                intent.putExtra("project_id", projectid);
                intent.putExtra("photo_compression", data.getStringExtra("photo_compression"));
                intent.putExtra("is_record", data.getStringExtra("is_record"));
                intent.putExtra("is_watermark", data.getStringExtra("is_watermark"));//int
                intent.putExtra("code", data.getStringExtra("code"));
                intent.putExtra("brand", data.getStringExtra("brand"));
                intent.putExtra("is_takephoto", data.getStringExtra("is_takephoto"));//String
                intent.putExtra("project_type", "1");
                intent.putExtra("is_desc", "");
                intent.putExtra("index", "0");
                startActivity(intent);
            }
            break;
        }
    }

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
        }
        citysearchFragment.setOnCitysearchExitClickListener(this);
        citysearchFragment.setOnCitysearchItemClickListener(this);
        ft.replace(R.id.fragmentRoot, citysearchFragment, "citysearchFragment");
        ft.addToBackStack("citysearchFragment");
        ft.commit();
    }

    public void rob(final TaskDetailLeftInfo taskDetailLeftInfo) {
        rob.sendPostRequest(Urls.rob, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String msg = jsonObject.getString("msg");
                    int code = jsonObject.getInt("code");
                    final String max_num = jsonObject.getString("max_num");
                    if (code == 200) {
                        ConfirmDialog.showDialog(TaskGrabActivity.this, "恭喜您，申请成功！", msg, "继续申请", "现在去做", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                            @Override
                            public void leftClick(Object object) {
                                if ("1".equals(type)) {
                                    TaskLocationActivity.isRefresh = true;
                                    baseFinish();
                                } else {
                                    if ("1".equals(max_num)) {
                                        TaskNewFragment.isRefresh = true;
                                        baseFinish();
                                    } else {
                                        if (taskGrabAdapter != null) {
                                            taskGrabAdapter.notifyDataSetChanged();
                                            refreshData();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void rightClick(Object object) {
                                doSelectType(taskDetailLeftInfo, taskDetailLeftInfo.getType());
                            }
                        });
                    } else if (code == 3) {
                        ConfirmDialog.showDialog(TaskGrabActivity.this, "恭喜您，申请成功！", msg, null, "现在去做", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                            @Override
                            public void leftClick(Object object) {

                            }

                            @Override
                            public void rightClick(Object object) {
                                doSelectType(taskDetailLeftInfo, taskDetailLeftInfo.getType());
                            }
                        }).goneLeft();
                    } else if (code == 2) {
                        ConfirmDialog.showDialog(TaskGrabActivity.this, "申请失败！", msg, null, "我知道了", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                            @Override
                            public void leftClick(Object object) {

                            }

                            @Override
                            public void rightClick(Object object) {
                            }
                        }).goneLeft();
                    } else if (code == 1) {
                        ConfirmDialog.showDialog(TaskGrabActivity.this, "很遗憾，稍慢了一步！", msg, null, "继续申请", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                            @Override
                            public void leftClick(Object object) {
                                if ("1".equals(type)) {
                                    TaskLocationActivity.isRefresh = true;
                                    baseFinish();
                                } else {
                                    if ("1".equals(max_num)) {
                                        TaskNewFragment.isRefresh = true;
                                        baseFinish();
                                    } else {
                                        if (taskGrabAdapter != null) {
                                            taskGrabAdapter.notifyDataSetChanged();
                                            refreshData();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void rightClick(Object object) {
                            }
                        }).goneLeft();
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskGrabActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskGrabActivity.this, getResources().getString(R.string.network_error));
            }
        }, null);
    }

    private String rob_state;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final TaskDetailLeftInfo taskDetailLeftInfo = list.get(position - 1);
        storeid = taskDetailLeftInfo.getId();
        rob_state = taskDetailLeftInfo.getRob_state();  //  // "rob_state":"是否可领取，1为可以领取，0为已抢完"
        if (TextUtils.isEmpty(AppInfo.getKey(TaskGrabActivity.this))) {
            ConfirmDialog.showDialog(TaskGrabActivity.this, null, 2,
                    getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                        @Override
                        public void leftClick(Object object) {
                        }

                        @Override
                        public void rightClick(Object object) {
                            Intent intent = new Intent(TaskGrabActivity.this, IdentifycodeLoginActivity.class);
                            startActivity(intent);
                        }
                    });
            return;
        }
        if (taskGrabAdapter != null) {
            if (!Tools.isEmpty(rob_state) && "1".equals(rob_state)) {
                ConfirmDialog.showDialog(this, "是否确认申请该网点？", true, new ConfirmDialog.OnSystemDialogClickListener() {
                    @Override
                    public void leftClick(Object object) {

                    }

                    @Override
                    public void rightClick(Object object) {
                        rob(taskDetailLeftInfo);
                    }
                });
            }
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
                    Tools.showToast(TaskGrabActivity.this, "定位权限获取失败");
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
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
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

    //城市关闭监听
    @Override
    public void exitClick() {
        if (citysearchFragment != null) {
            findViewById(R.id.fragmentRoot).setVisibility(View.GONE);
            FragmentTransaction ft = fMgr.beginTransaction();
            ft.hide(citysearchFragment);
        }
    }

    //城市选择监听
    @Override
    public void ItemClick(Map<String, String> map) {
        if (TextUtils.isEmpty(getCityNameState)) {
            if (citysearchFragment != null) {
                province = map.get("province");
                if (TextUtils.isEmpty(map.get("county")) || "null".equals(map.get("county"))) {
                    taskgrab_city.setText(map.get("name"));
                } else {
                    taskgrab_city.setText(map.get("name") + "-" + map.get("county"));
                }
                if (province == null) {
                    province = map.get("name");
                }
                findViewById(R.id.fragmentRoot).setVisibility(View.GONE);
                FragmentTransaction ft = fMgr.beginTransaction();
                ft.hide(citysearchFragment);
                refreshData();
            }
        } else {
            getCityNameState = map.get("name");
            if (citysearchFragment != null) {
                findViewById(R.id.fragmentRoot).setVisibility(View.GONE);
                FragmentTransaction ft = fMgr.beginTransaction();
                ft.hide(citysearchFragment);
            }
        }
    }

    private class MyLocationListenner implements BDLocationListener {
        public void onReceiveLocation(BDLocation location) {
            mLocationClient.stop();
            if (location == null) {
                Tools.showToast(TaskGrabActivity.this, getResources().getString(R.string.location_fail));
                return;
            }
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            locType = location.getLocType();
            address = location.getAddrStr();
            LatLng ptCenter = new LatLng(latitude, longitude);
            mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ptCenter));
        }

        public void onConnectHotSpotMessage(String s, int i) {

        }

        public void onReceivePoi(BDLocation arg0) {
        }
    }


    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Tools.showToast(TaskGrabActivity.this, getResources().getString(R.string.location_fail));
            return;
        }
        if (flag) {
            boolean isEmpty = !TextUtils.isEmpty(reverseGeoCodeResult.getAddressDetail().city);
            if (isEmpty) {
//            isLocationSuccess = true;
                if (isFirst) {
                    isLocation = true;
                    isFirst = false;
                } else {
                    isLocation = false;
                    isFirst = true;
                }
                refreshData();
            } else {
                Tools.showToast(TaskGrabActivity.this, getResources().getString(R.string.location_fail));
            }
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (mSearch != null) {
            mSearch.destroy();
        }
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
        UMShareAPI.get(TaskGrabActivity.this).release();
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
                            ConfirmDialog.showDialog(TaskGrabActivity.this, null, jsonObject.getString("msg"), null,
                                    "确定", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                        @Override
                                        public void leftClick(Object object) {

                                        }

                                        @Override
                                        public void rightClick(Object object) {
                                        }
                                    }).goneLeft();
                        } else if (code == 3) {
                            ConfirmDialog.showDialog(TaskGrabActivity.this, null, jsonObject.getString("msg"), "取消",
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
                            Tools.showToast(TaskGrabActivity.this, jsonObject.getString("msg"));
                        }
                    } catch (JSONException e) {
                        Tools.showToast(TaskGrabActivity.this, getResources().getString(R.string.network_error));
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Tools.showToast(TaskGrabActivity.this, getResources().getString(R.string
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
        if (tasktype != null) {
            if ("1".equals(tasktype) || "6".equals(tasktype)) {//正常任务/到店红包
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
                    intent.putExtra("backpage", 0);
                    startActivity(intent);
                    baseFinish();
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
                    intent.putExtra("project_type", "1");
                    intent.putExtra("backpage", 0);
                    startActivity(intent);
                    baseFinish();
                }
            } else if ("2".equals(tasktype)) {//暗访任务
                Intent intent = new Intent(this, BlackillustrateActivity.class);
                intent.putExtra("project_id", taskDetailLeftInfo.getProjectid());
                intent.putExtra("project_name", taskDetailLeftInfo.getProjectname());
                intent.putExtra("store_id", taskDetailLeftInfo.getId());
                intent.putExtra("store_name", taskDetailLeftInfo.getName());
                intent.putExtra("store_num", taskDetailLeftInfo.getCode());
                intent.putExtra("photo_compression", data.getStringExtra("photo_compression"));
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
                intent.putExtra("project_type", "1");
                intent.putExtra("is_desc", taskDetailLeftInfo.getIs_desc());
                startActivity(intent);
            }
        }
    }
}
