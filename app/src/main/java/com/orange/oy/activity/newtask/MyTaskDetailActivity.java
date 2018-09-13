package com.orange.oy.activity.newtask;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.activity.OfflinePackageActivity;
import com.orange.oy.activity.StoreDescActivity;
import com.orange.oy.activity.TaskFinishActivity;
import com.orange.oy.activity.TaskillustratesActivity;
import com.orange.oy.activity.TaskitemDetailActivity_12;
import com.orange.oy.activity.guide.TaskLocationActivity;
import com.orange.oy.adapter.MyTaskDetailAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.TaskDetailLeftInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 我的任务详情页
 */
public class MyTaskDetailActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {

    private AppTitle appTitle;

    private void initTitle() {
        appTitle = (AppTitle) findViewById(R.id.mytask_title);
        showSearch();
        appTitle.settingName(getResources().getString(R.string.taskschdetail));
        appTitle.showBack(this);
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
                    if (mytask_listview_left.getVisibility() == View.VISIBLE) {
                        Dzxstartlist.setIsShowDialog(true);
                        refreshLeft();
                    } else if (mytask_listview_middle.getVisibility() == View.VISIBLE) {
                        Zxwcstartlist.setIsShowDialog(true);
                        refreshMiddle();
                    } else if (mytask_listview_right.getVisibility() == View.VISIBLE) {
                        Zlyhsstartlist.setIsShowDialog(true);
                        refreshRight();
                    }
                    v.setText("");
                    return true;
                }
                return false;
            }
        });
    }

    public static boolean isRefresh;

    protected void onResume() {
        super.onResume();
        if (isRefresh) {
            isRefresh = false;
            refreshLeft();
        }
    }

    public void initNetworkConnection() {
        Dzxstartlist = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("page", page_left + "");
                params.put("project_id", project_id);
//                params.put("city", city);
                params.put("user_mobile", AppInfo.getName(MyTaskDetailActivity.this));
                if (!TextUtils.isEmpty(searchStr)) {
                    params.put("keyword", searchStr);
                }
                return params;
            }
        };
        Zxwcstartlist = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("page", page_middle + "");
                params.put("project_id", project_id);
                params.put("user_mobile", AppInfo.getName(MyTaskDetailActivity.this));
                if (!TextUtils.isEmpty(searchStr)) {
                    params.put("keyword", searchStr);
                }
                return params;
            }
        };
        Zlyhsstartlist = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("page", page_right + "");
                params.put("project_id", project_id);
                params.put("user_mobile", AppInfo.getName(MyTaskDetailActivity.this));
                if (!TextUtils.isEmpty(searchStr)) {
                    params.put("keyword", searchStr);
                }
                return params;
            }
        };
        Selectprojectwcjd = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("project_id", project_id);
                params.put("user_mobile", AppInfo.getName(MyTaskDetailActivity.this));
                return params;
            }
        };
        checkinvalid = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(MyTaskDetailActivity.this));
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
        if (Dzxstartlist != null) {
            Dzxstartlist.stop(Urls.Dzxstartlist);
        }
        if (Zxwcstartlist != null) {
            Zxwcstartlist.stop(Urls.Zxwcstartlist);
        }
        if (Zlyhsstartlist != null) {
            Zlyhsstartlist.stop(Urls.Zlyhsstartlist);
        }
        if (Selectprojectwcjd != null) {
            Selectprojectwcjd.stop(Urls.Selectprojectwcjd);
        }
        if (checkinvalid != null) {
            checkinvalid.stop(Urls.CheckInvalid);
        }
    }

    private PullToRefreshListView mytask_listview_left, mytask_listview_middle, mytask_listview_right;
    private MyTaskDetailAdapter myTaskDetailAdapterLeft, myTaskDetailAdapterMiddle, myTaskDetailAdapterRight;
    private NetworkConnection Dzxstartlist, Zxwcstartlist, Zlyhsstartlist, Selectprojectwcjd, checkinvalid;
    private int page_left, page_middle, page_right;
    private Intent data;
    private String project_id, city, projectname, photo_compression, is_watermark, code, brand, is_takephoto;
    private ArrayList<TaskDetailLeftInfo> list_left, list_middle, list_right;
    private TextView mytask_tab_left, mytask_tab_middle, mytask_tab_right;
    private View mytask_tab_left_line, mytask_tab_middle_line, mytask_tab_right_line;
    private TextView mytaskdetail_name, mytaskdetail_time, mytaskdetail_period;
    private String storeid, address;
    private double longitude, latitude;
    private LocationClient mLocationClient = null;
    private BDLocationListener myListener = new MyLocationListener();
    private int locType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_task);
        initTitle();
        initView();
        initNetworkConnection();
        list_left = new ArrayList<>();
        list_middle = new ArrayList<>();
        list_right = new ArrayList<>();
        data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        String standard_state = data.getStringExtra("standard_state");
        if ("1".equals(standard_state)) {
            findViewById(R.id.mytaskdetail_standard).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.mytaskdetail_standard).setVisibility(View.GONE);
        }
        project_id = data.getStringExtra("id");
        projectname = data.getStringExtra("project_name");
        photo_compression = data.getStringExtra("photo_compression");
        is_watermark = data.getStringExtra("is_watermark");
        code = data.getStringExtra("code");
        brand = data.getStringExtra("brand");
        is_takephoto = data.getStringExtra("is_takephoto");
        mytaskdetail_time.setText("起止时间:" + data.getStringExtra("begin_date") + "~" + data.getStringExtra("end_date"));
        mytaskdetail_name.setText(data.getStringExtra("project_name"));
        mytaskdetail_period.setText("审核周期:" + data.getStringExtra("check_time") + "天");
        initListview(mytask_listview_left);
        ((TextView) findViewById(R.id.mytaskdetail_person)).setText("发布商家:【" + data.getStringExtra("project_person") + "】");
        initListview(mytask_listview_middle);
        initListview(mytask_listview_right);
        myTaskDetailAdapterLeft = new MyTaskDetailAdapter(this, list_left);
        mytask_listview_left.setAdapter(myTaskDetailAdapterLeft);
        myTaskDetailAdapterMiddle = new MyTaskDetailAdapter(this, list_middle, true);
        mytask_listview_middle.setAdapter(myTaskDetailAdapterMiddle);
        myTaskDetailAdapterRight = new MyTaskDetailAdapter(this, list_right, true);
        mytask_listview_right.setAdapter(myTaskDetailAdapterRight);
        mytask_listview_left.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshLeft();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page_left++;
                getDataForLeft();
            }
        });
        mytask_listview_middle.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshMiddle();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page_middle++;
                getDataForMiddle();
            }
        });
        mytask_listview_right.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshRight();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page_right++;
                getDataForRight();
            }
        });
        OnItemClickListView1();
        OnItemClickListView2();
        OnItemClickListView3();
        View view = findViewById(R.id.mytask_tab_left);
        view.setOnClickListener(this);
        findViewById(R.id.mytask_tab_middle).setOnClickListener(this);
        findViewById(R.id.mytask_tab_right).setOnClickListener(this);
        findViewById(R.id.mytaskdetail_standard).setOnClickListener(this);
        onClick(view);
        checkPermission();
        initLocation();
    }

    private void OnItemClickListView1() {
        mytask_listview_left.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final TaskDetailLeftInfo taskDetailLeftInfo = list_left.get(position - 1);
                storeid = taskDetailLeftInfo.getId();
                if (myTaskDetailAdapterLeft != null) {
                    if (locType == 61 || locType == 161) {
                        checkinvalid.sendPostRequest(Urls.CheckInvalid, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                Tools.d(s);
                                try {
                                    JSONObject jsonObject = new JSONObject(s);
                                    int code = jsonObject.getInt("code");
                                    if (code == 200) {
                                        doExecute(taskDetailLeftInfo);
                                    } else if (code == 2) {
                                        ConfirmDialog.showDialog(MyTaskDetailActivity.this, null, jsonObject.getString("msg"), null,
                                                "确定", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                                    @Override
                                                    public void leftClick(Object object) {

                                                    }

                                                    @Override
                                                    public void rightClick(Object object) {
                                                    }
                                                }).goneLeft();
                                    } else if (code == 3) {
                                        ConfirmDialog.showDialog(MyTaskDetailActivity.this, null, jsonObject.getString("msg"), "取消",
                                                "继续执行", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                                                    @Override
                                                    public void leftClick(Object object) {

                                                    }

                                                    @Override
                                                    public void rightClick(Object object) {
                                                        doExecute(taskDetailLeftInfo);
                                                    }
                                                });
                                    } else {
                                        Tools.showToast(MyTaskDetailActivity.this, jsonObject.getString("msg"));
                                    }
                                } catch (JSONException e) {
                                    Tools.showToast(MyTaskDetailActivity.this, getResources().getString(R.string.network_error));
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Tools.showToast(MyTaskDetailActivity.this, getResources().getString(R.string
                                        .network_volleyerror));
                            }
                        }, null);
                    } else if (locType == 167) {
                        Tools.showToast2(MyTaskDetailActivity.this, "请您检查是否开启权限，尝试重新请求定位");
                    } else {
                        Tools.showToast2(MyTaskDetailActivity.this, "请您检查网络是否通畅或是否允许访问位置信息,尝试重新请求定位");
                    }
                }
                myTaskDetailAdapterLeft.clearClick();
            }
        });
    }

    private void doExecute(TaskDetailLeftInfo taskDetailLeftInfo) {
        String fynum = taskDetailLeftInfo.getNumber();
        if (!TextUtils.isEmpty(fynum) && !"null".equals(fynum) && fynum.equals(AppInfo.getName
                (MyTaskDetailActivity.this))) {
            if (taskDetailLeftInfo.getIs_exe().equals("1")) {
                if (taskDetailLeftInfo.getIsOffline() == 1) {
                    Intent intent = new Intent(MyTaskDetailActivity.this, OfflinePackageActivity.class);
                    intent.putExtra("id", taskDetailLeftInfo.getId());
                    intent.putExtra("projectname", projectname);
                    intent.putExtra("store_name", taskDetailLeftInfo.getName());
                    intent.putExtra("store_num", taskDetailLeftInfo.getCode());
                    intent.putExtra("province", taskDetailLeftInfo.getCity());
                    intent.putExtra("city", taskDetailLeftInfo.getCity2());
                    intent.putExtra("project_id", project_id);
                    intent.putExtra("photo_compression", photo_compression);
                    intent.putExtra("is_record", taskDetailLeftInfo.getIs_record());
                    intent.putExtra("is_watermark", is_watermark);
                    intent.putExtra("code", code);
                    intent.putExtra("brand", brand);
                    intent.putExtra("is_takephoto", is_takephoto);
                    startActivity(intent);
                } else {
                    if (taskDetailLeftInfo.getIs_desc().equals("1")) {//有网点说明
                        Intent intent = new Intent(MyTaskDetailActivity.this, StoreDescActivity.class);
                        intent.putExtra("id", taskDetailLeftInfo.getId());
                        intent.putExtra("projectname", projectname);
                        intent.putExtra("store_name", taskDetailLeftInfo.getName());
                        intent.putExtra("store_num", taskDetailLeftInfo.getCode());
                        intent.putExtra("province", taskDetailLeftInfo.getCity());
                        intent.putExtra("city", taskDetailLeftInfo.getCity2());
                        intent.putExtra("project_id", project_id);
                        intent.putExtra("photo_compression", photo_compression);
                        intent.putExtra("is_desc", "1");
                        intent.putExtra("is_watermark", is_watermark);
                        intent.putExtra("code", code);
                        intent.putExtra("brand", brand);
                        intent.putExtra("is_takephoto", is_takephoto);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(MyTaskDetailActivity.this, TaskitemDetailActivity_12
                                .class);
                        intent.putExtra("id", taskDetailLeftInfo.getId());
                        intent.putExtra("projectname", projectname);
                        intent.putExtra("store_name", taskDetailLeftInfo.getName());
                        intent.putExtra("store_num", taskDetailLeftInfo.getCode());
                        intent.putExtra("province", taskDetailLeftInfo.getCity());
                        intent.putExtra("city", taskDetailLeftInfo.getCity2());
                        intent.putExtra("project_id", project_id);
                        intent.putExtra("photo_compression", photo_compression);
                        intent.putExtra("is_desc", "0");
                        intent.putExtra("is_watermark", is_watermark);
                        intent.putExtra("code", code);
                        intent.putExtra("brand", brand);
                        intent.putExtra("is_takephoto", is_takephoto);
                        startActivity(intent);
                    }
                }
            } else {
                Tools.showToast(MyTaskDetailActivity.this, "未到执行时间");
            }
        } else {
            Tools.showToast(MyTaskDetailActivity.this, "您不是访员！");
        }
    }

    private void OnItemClickListView2() {
        mytask_listview_middle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TaskDetailLeftInfo taskDetailLeftInfo = list_middle.get(position - 1);
                if (myTaskDetailAdapterMiddle != null) {
                    Intent intent = new Intent(MyTaskDetailActivity.this, TaskFinishActivity.class);
                    intent.putExtra("projectname", projectname);
                    intent.putExtra("store_name", taskDetailLeftInfo.getName());
                    intent.putExtra("store_num", taskDetailLeftInfo.getCode());
                    intent.putExtra("project_id", project_id);
                    intent.putExtra("photo_compression", photo_compression);
                    intent.putExtra("store_id", taskDetailLeftInfo.getId());
                    intent.putExtra("state", "2");
                    intent.putExtra("is_watermark", is_watermark);
                    intent.putExtra("code", code);
                    intent.putExtra("brand", brand);
                    intent.putExtra("isAgain", false);
                    startActivity(intent);
                }
                myTaskDetailAdapterMiddle.clearClick();
            }
        });
    }

    private void OnItemClickListView3() {
        mytask_listview_right.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TaskDetailLeftInfo taskDetailLeftInfo = list_right.get(position - 1);
                if (myTaskDetailAdapterRight != null) {
                    Intent intent = new Intent(MyTaskDetailActivity.this, TaskFinishActivity.class);
                    intent.putExtra("projectname", projectname);
                    intent.putExtra("store_name", taskDetailLeftInfo.getName());
                    intent.putExtra("store_num", taskDetailLeftInfo.getCode());
                    intent.putExtra("project_id", project_id);
                    intent.putExtra("photo_compression", photo_compression);
                    intent.putExtra("store_id", taskDetailLeftInfo.getId());
                    intent.putExtra("state", "2");
                    intent.putExtra("is_watermark", is_watermark);
                    intent.putExtra("code", code);
                    intent.putExtra("brand", brand);
                    intent.putExtra("isAgain", false);
                    startActivity(intent);
                }
                myTaskDetailAdapterRight.clearClick();
            }
        });
    }

    private void refreshLeft() {
        page_left = 1;
        getDataForLeft();
    }

    private void refreshMiddle() {
        page_middle = 1;
        getDataForMiddle();
    }

    private void refreshRight() {
        page_right = 1;
        getDataForRight();
    }

    private void initListview(PullToRefreshListView listview) {
        listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listview.setPullLabel(getResources().getString(R.string.listview_down));
        listview.setRefreshingLabel(getResources().getString(R.string.listview_refush));
        listview.setReleaseLabel(getResources().getString(R.string.listview_down2));
    }

    private void getDataForLeft() {//待执行
        Dzxstartlist.sendPostRequest(Urls.Dzxstartlist, new Response.Listener<String>() {
            public void onResponse(String s) {
                Dzxstartlist.setIsShowDialog(false);
                Tools.d(s);
                searchStr = "";
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        if (list_left == null) {
                            list_left = new ArrayList<TaskDetailLeftInfo>();
                        } else {
                            if (page_left == 1) {
                                list_left.clear();
                            }
                        }
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
                            taskDetailLeftInfo.setName(jsonObject.getString("storeName"));
                            taskDetailLeftInfo.setCode(jsonObject.getString("storeNum"));
                            taskDetailLeftInfo.setIdentity(jsonObject.getString("proxy_num"));
                            taskDetailLeftInfo.setCity(jsonObject.getString("province"));
                            taskDetailLeftInfo.setCity2(jsonObject.getString("city"));
                            taskDetailLeftInfo.setCity3(jsonObject.getString("address"));
                            taskDetailLeftInfo.setNumber(jsonObject.getString("accessed_num"));
                            taskDetailLeftInfo.setNickname(jsonObject.getString("accessed_name"));
                            taskDetailLeftInfo.setExe_time(jsonObject.getString("exe_time"));
                            taskDetailLeftInfo.setMoney(jsonObject.getString("money"));
                            taskDetailLeftInfo.setTimedetail(timeDetail);
                            taskDetailLeftInfo.setHavetime(jsonObject.getString("havetime"));
                            taskDetailLeftInfo.setMoney_unit(data.getStringExtra("money_unit"));
                            list_left.add(taskDetailLeftInfo);
                        }
                        try {
//                            offlineDBHelper.isCompletedForStore(AppInfo.getName(TaskDistActivity.this), project_id,
//                                    list_left);//TODO
                        } catch (Exception e) {
                            e.printStackTrace();
                            MobclickAgent.reportError(MyTaskDetailActivity.this,
                                    "taskscheduledetailactivity error1:" + e.getMessage());
                        }
                        mytask_listview_left.onRefreshComplete();
                        if (length < 15) {
                            mytask_listview_left.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            mytask_listview_left.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                        myTaskDetailAdapterLeft.notifyDataSetChanged();
                    } else {
                        Tools.showToast(MyTaskDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    MobclickAgent.reportError(MyTaskDetailActivity.this,
                            "TaskscheduleDetailActivity getData ParseJson:" + e.getMessage());
                    Tools.showToast(MyTaskDetailActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
                mytask_listview_left.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                searchStr = "";
                Dzxstartlist.setIsShowDialog(false);
                mytask_listview_left.onRefreshComplete();
                Tools.showToast(MyTaskDetailActivity.this, getResources().getString(R.string
                        .network_volleyerror));
            }
        });
    }

    private void getDataForMiddle() {//待上传
        Zxwcstartlist.sendPostRequest(Urls.Zxwcstartlist, new Response.Listener<String>() {
            public void onResponse(String s) {
                Zxwcstartlist.setIsShowDialog(false);
                Tools.d(s);
                searchStr = "";
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        if (list_middle == null) {
                            list_middle = new ArrayList<TaskDetailLeftInfo>();
                        } else {
                            if (page_middle == 1) {
                                list_middle.clear();
                            }
                        }
                        JSONArray jsonArray = jsonObject.getJSONArray("datas");
                        int length = jsonArray.length();
                        for (int i = 0; i < length; i++) {
                            TaskDetailLeftInfo taskDetailLeftInfo = new TaskDetailLeftInfo();
                            jsonObject = jsonArray.getJSONObject(i);
                            taskDetailLeftInfo.setId(jsonObject.getString("storeid"));
                            taskDetailLeftInfo.setName(jsonObject.getString("storeName"));
                            taskDetailLeftInfo.setCode(jsonObject.getString("storeNum"));
                            taskDetailLeftInfo.setIdentity(jsonObject.getString("proxy_num"));
                            taskDetailLeftInfo.setCity(jsonObject.getString("province"));
                            taskDetailLeftInfo.setCity2(jsonObject.getString("city"));
                            taskDetailLeftInfo.setCity3(jsonObject.getString("address"));
                            taskDetailLeftInfo.setNumber(jsonObject.getString("accessed_num"));
                            taskDetailLeftInfo.setAgain("0".equals(jsonObject.getString("is_upload")));
                            taskDetailLeftInfo.setMoney(jsonObject.getString("money"));
                            taskDetailLeftInfo.setMoney_unit(data.getStringExtra("money_unit"));
                            list_middle.add(taskDetailLeftInfo);
                        }
                        mytask_listview_middle.onRefreshComplete();
                        if (length < 15) {
                            mytask_listview_middle.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            mytask_listview_middle.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                        myTaskDetailAdapterMiddle.notifyDataSetChanged();
                    } else {
                        Tools.showToast(MyTaskDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(MyTaskDetailActivity.this, getResources().getString(R.string.network_error));
                }
                mytask_listview_middle.onRefreshComplete();
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                searchStr = "";
                Zxwcstartlist.setIsShowDialog(false);
                mytask_listview_middle.onRefreshComplete();
                Tools.showToast(MyTaskDetailActivity.this, getResources().getString(R.string
                        .network_volleyerror));
            }
        });
    }

    private void getDataForRight() {//已上传
        Zlyhsstartlist.sendPostRequest(Urls.Zlyhsstartlist, new Response.Listener<String>() {
            public void onResponse(String s) {
                Zlyhsstartlist.setIsShowDialog(false);
                Tools.d(s);
                searchStr = "";
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        if (list_right == null) {
                            list_right = new ArrayList<TaskDetailLeftInfo>();
                        } else {
                            if (page_right == 1) {
                                list_right.clear();
                            }
                        }
                        JSONArray jsonArray = jsonObject.getJSONArray("datas");
                        int length = jsonArray.length();
                        for (int i = 0; i < length; i++) {
                            TaskDetailLeftInfo taskDetailLeftInfo = new TaskDetailLeftInfo();
                            jsonObject = jsonArray.getJSONObject(i);
                            taskDetailLeftInfo.setId(jsonObject.getString("storeid"));
                            taskDetailLeftInfo.setName(jsonObject.getString("storeName"));
                            taskDetailLeftInfo.setCode(jsonObject.getString("storeNum"));
                            taskDetailLeftInfo.setIdentity(jsonObject.getString("proxy_num"));
                            taskDetailLeftInfo.setCity(jsonObject.getString("province"));
                            taskDetailLeftInfo.setCity2(jsonObject.getString("city"));
                            taskDetailLeftInfo.setCity3(jsonObject.getString("address"));
                            taskDetailLeftInfo.setNumber(jsonObject.getString("accessed_num"));
                            taskDetailLeftInfo.setMoney(jsonObject.getString("money"));
                            taskDetailLeftInfo.setMoney_unit(data.getStringExtra("money_unit"));
                            list_right.add(taskDetailLeftInfo);
                        }
                        mytask_listview_right.onRefreshComplete();
                        if (length < 15) {
                            mytask_listview_right.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            mytask_listview_right.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                        myTaskDetailAdapterRight.notifyDataSetChanged();
                    } else {
                        Tools.showToast(MyTaskDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(MyTaskDetailActivity.this, getResources().getString(R.string.network_error));
                }
                mytask_listview_right.onRefreshComplete();
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                searchStr = "";
                Zlyhsstartlist.setIsShowDialog(false);
                mytask_listview_right.onRefreshComplete();
                Tools.showToast(MyTaskDetailActivity.this, getResources().getString(R.string
                        .network_volleyerror));
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    private void initView() {
        mytask_listview_left = (PullToRefreshListView) findViewById(R.id.mytask_listview_left);
        mytask_listview_middle = (PullToRefreshListView) findViewById(R.id.mytask_listview_middle);
        mytask_listview_right = (PullToRefreshListView) findViewById(R.id.mytask_listview_right);
        mytask_tab_left = (TextView) findViewById(R.id.mytask_tab_left);
        mytask_tab_middle = (TextView) findViewById(R.id.mytask_tab_middle);
        mytask_tab_right = (TextView) findViewById(R.id.mytask_tab_right);
        mytask_tab_left_line = findViewById(R.id.mytask_tab_left_line);
        mytask_tab_middle_line = findViewById(R.id.mytask_tab_middle_line);
        mytask_tab_right_line = findViewById(R.id.mytask_tab_right_line);
        mytaskdetail_name = (TextView) findViewById(R.id.mytaskdetail_name);
        mytaskdetail_time = (TextView) findViewById(R.id.mytaskdetail_time);
        mytaskdetail_period = (TextView) findViewById(R.id.mytaskdetail_period);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mytask_tab_left: {
                mytask_tab_left.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                mytask_tab_left_line.setVisibility(View.VISIBLE);
                mytask_tab_middle.setTextColor(getResources().getColor(R.color.colorPrimaryDark2));
                mytask_tab_middle_line.setVisibility(View.INVISIBLE);
                mytask_tab_right.setTextColor(getResources().getColor(R.color.colorPrimaryDark2));
                mytask_tab_right_line.setVisibility(View.INVISIBLE);
                mytask_listview_left.setVisibility(View.VISIBLE);
                mytask_listview_middle.setVisibility(View.GONE);
                mytask_listview_right.setVisibility(View.GONE);
                if (list_left == null || list_left.isEmpty()) {
                    refreshLeft();
                }
            }
            break;
            case R.id.mytask_tab_middle: {
                mytask_tab_left.setTextColor(getResources().getColor(R.color.colorPrimaryDark2));
                mytask_tab_left_line.setVisibility(View.INVISIBLE);
                mytask_tab_middle.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                mytask_tab_middle_line.setVisibility(View.VISIBLE);
                mytask_tab_right.setTextColor(getResources().getColor(R.color.colorPrimaryDark2));
                mytask_tab_right_line.setVisibility(View.INVISIBLE);
                mytask_listview_left.setVisibility(View.GONE);
                mytask_listview_middle.setVisibility(View.VISIBLE);
                mytask_listview_right.setVisibility(View.GONE);
                if (list_middle == null || list_middle.isEmpty()) {
                    refreshMiddle();
                }
            }
            break;
            case R.id.mytask_tab_right: {
                mytask_tab_left.setTextColor(getResources().getColor(R.color.colorPrimaryDark2));
                mytask_tab_left_line.setVisibility(View.INVISIBLE);
                mytask_tab_middle.setTextColor(getResources().getColor(R.color.colorPrimaryDark2));
                mytask_tab_middle_line.setVisibility(View.INVISIBLE);
                mytask_tab_right.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                mytask_tab_right_line.setVisibility(View.VISIBLE);
                mytask_listview_left.setVisibility(View.GONE);
                mytask_listview_middle.setVisibility(View.GONE);
                mytask_listview_right.setVisibility(View.VISIBLE);
                if (list_right == null || list_right.isEmpty()) {
                    refreshRight();
                }
            }
            break;
            case R.id.mytaskdetail_standard: {
                Intent intent = new Intent(this, TaskillustratesActivity.class);
                intent.putExtra("projectid", project_id);
                intent.putExtra("projectname", data.getStringExtra("project_name"));
                intent.putExtra("isShow", "0");//是否显示不再显示复选框
                startActivity(intent);
            }
            break;
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, AppInfo
                        .REQUEST_CODE_ASK_RECORD_AUDIO);
                return;
            }
            checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, AppInfo
                        .REQUEST_CODE_ASK_CAMERA);
                return;
            }
            if (Build.VERSION.SDK_INT >= 23) {
                checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
                if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, AppInfo
                            .REQUEST_CODE_ASK_LOCATION);
                    return;
                }
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        switch (requestCode) {
            case AppInfo.REQUEST_CODE_ASK_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                } else {
                    Tools.showToast(this, "拍照权限获取失败");
                    baseFinish();
                }
                break;
            case AppInfo.REQUEST_CODE_ASK_RECORD_AUDIO:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                } else {
                    Tools.showToast(this, "录音权限获取失败");
                    baseFinish();
                }
                break;
            case AppInfo.REQUEST_CODE_ASK_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                } else {
                    Tools.showToast(this, "定位权限获取失败");
                    AppInfo.setOpenLocation(this, false);
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
                Tools.showToast(MyTaskDetailActivity.this, getResources().getString(R.string.location_fail));
                return;
            }
            latitude = bdLocation.getLatitude();
            longitude = bdLocation.getLongitude();
            address = bdLocation.getAddrStr();
            locType = bdLocation.getLocType();
            //61:GPS定位结果，GPS定位成功。
            //62:无法获取有效定位依据，定位失败，请检查运营商网络或者wifi网络是否正常开启，尝试重新请求定位。
            Tools.d(bdLocation.getAddrStr() + "locType:" + locType);
        }

        public void onConnectHotSpotMessage(String s, int i) {

        }
    }
}
