package com.orange.oy.activity.bright;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
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
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.activity.OfflinePackageActivity;
import com.orange.oy.activity.StoreDescActivity;
import com.orange.oy.activity.TaskFinishActivity;
import com.orange.oy.activity.TaskitemDetailActivity_12;
import com.orange.oy.adapter.BrightListAdapter;
import com.orange.oy.adapter.TaskscheduleDetailMiddleAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.OfflineDBHelper;
import com.orange.oy.db.SystemDBHelper;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.MyDialog;
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
 * 明访任务网点列表
 */
public class BrightDZXListActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle,
        View.OnClickListener {

    private AppTitle brightdzx_title;
    private String searchStr = "";

    private void initTitle() {
        brightdzx_title = (AppTitle) findViewById(R.id.brightdzx_title);
        showSearch();
        brightdzx_title.settingName(getResources().getString(R.string.taskschdetail));
        brightdzx_title.showBack(this);
    }

    public void showSearch() {
        brightdzx_title.settingHint("可搜索网点名称、网点编号、省份、城市");
        brightdzx_title.showSearch(new TextWatcher() {
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
                    if (brightdzx_listview_left.getVisibility() == View.VISIBLE) {
                        Dzxstartlist.setIsShowDialog(true);
                        refreshLeft();
                    } else if (brightdzx_listview_middle.getVisibility() == View.VISIBLE) {
                        Zxwcstartlist.setIsShowDialog(true);
                        refreshMiddle();
                    } else if (brightdzx_listview_right.getVisibility() == View.VISIBLE) {
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

    @Override
    protected void onDestroy() {
        if (mLocationClient != null)
            mLocationClient.stop();
        super.onDestroy();
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
        if (checkIsselect != null) {
            checkIsselect.stop(Urls.CheckIsselect);
        }
        if (Redo != null) {
            Redo.stop(Urls.Redo);
        }
        if (Selectprojectwcjd != null) {
            Selectprojectwcjd.stop(Urls.Selectprojectwcjd);
        }
        if (Startupload != null) {
            Startupload.stop(Urls.Startupload);
        }
    }

    public void initNetworkConnection() {
        Dzxstartlist = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("page", page_left + "");
                params.put("project_id", project_id);
                params.put("city", city);
                params.put("user_mobile", AppInfo.getName(BrightDZXListActivity.this));
                if (!TextUtils.isEmpty(searchStr)) {
                    params.put("keyword", searchStr);
                }
                return params;
            }
        };
        Dzxstartlist.setIsShowDialog(true);
        Zxwcstartlist = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("page", page_middle + "");
                params.put("project_id", project_id);
                params.put("user_mobile", AppInfo.getName(BrightDZXListActivity.this));
                if (!TextUtils.isEmpty(searchStr)) {
                    params.put("keyword", searchStr);
                }
                return params;
            }
        };
        Zxwcstartlist.setIsShowDialog(true);
        Zlyhsstartlist = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("page", page_right + "");
                params.put("project_id", project_id);
                params.put("user_mobile", AppInfo.getName(BrightDZXListActivity.this));
                if (!TextUtils.isEmpty(searchStr)) {
                    params.put("keyword", searchStr);
                }
                return params;
            }
        };
        Zlyhsstartlist.setIsShowDialog(true);
        Selectprojectwcjd = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("project_id", project_id);
                params.put("user_mobile", AppInfo.getName(BrightDZXListActivity.this));
                return params;
            }
        };
        Selectprojectwcjd.setIsShowDialog(true);
        checkIsselect = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("outletid", outletid);
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(BrightDZXListActivity.this));
                params.put("longitude", longitude + "");
                params.put("latitude", latitude + "");
                return params;
            }
        };
        checkIsselect.setIsShowDialog(true);
        Redo = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("storeid", selStoreid);
                params.put("usermobile", AppInfo.getName(BrightDZXListActivity.this));
                return params;
            }
        };
        Redo.setIsShowDialog(true);
        Startupload = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("storeid", selStoreid);
                params.put("usermobile", AppInfo.getName(BrightDZXListActivity.this));
                return params;
            }
        };
        Startupload.setIsShowDialog(true);
    }

    private int page_left, page_middle, page_right;
    private PullToRefreshListView brightdzx_listview_left, brightdzx_listview_middle, brightdzx_listview_right;
    private TextView brightdzx_name, brightdzx_tableft_text, brightdzx_tableft_small, brightdzx_tabmiddle_text,
            brightdzx_tabmiddle_small, brightdzx_tabright_text, brightdzx_tabright_small;
    private ImageView brightdzx_tableft_ico, brightdzx_tabmiddle_ico, brightdzx_tabright_ico;
    private View brightdzx_tableft_line, brightdzx_tabmiddle_line, brightdzx_tabright_line;
    private ArrayList<TaskDetailLeftInfo> list_left, list_middle, list_right;
    private String project_id, projectname, city, photo_compression, is_record, code, brand, is_takephoto, outletid;
    private int is_watermark;
    private NetworkConnection Dzxstartlist, Zxwcstartlist, Zlyhsstartlist, Selectprojectwcjd, checkIsselect, Redo, Startupload;
    private OfflineDBHelper offlineDBHelper;
    private BrightListAdapter adapterLeft, adapterRight;
    private TaskscheduleDetailMiddleAdapter adapterMiddle;
    public static boolean isRefresh;
    private String type;//任务类型
    private int selMiddlePosition = 0;
    private String selStoreid;
    private UpdataDBHelper updataDBHelper;
    private SystemDBHelper systemDBHelper;
    private double longitude, latitude;//经纬度
    public LocationClient mLocationClient = null;
    public MyLocationListenner myListener = new MyLocationListenner();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bright_dzxlist);
        initTitle();
        initNetworkConnection();
        offlineDBHelper = new OfflineDBHelper(this);
        Intent data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        updataDBHelper = new UpdataDBHelper(this);
        systemDBHelper = new SystemDBHelper(this);
        project_id = data.getStringExtra("project_id");
        projectname = data.getStringExtra("projectname");
        city = data.getStringExtra("city");
        photo_compression = data.getStringExtra("photo_compression");
        is_record = data.getStringExtra("is_record");
        is_watermark = data.getIntExtra("is_watermark", 0);
        code = data.getStringExtra("code");
        brand = data.getStringExtra("brand");
        is_takephoto = data.getStringExtra("is_takephoto");
        type = data.getStringExtra("type");
        initView();
        brightdzx_name.setText(projectname);
        Selectprojectwcjd();
        checkPermission();
        isRefresh = false;
        initLocation();
    }

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

    private class MyLocationListenner implements BDLocationListener {
        public void onReceiveLocation(BDLocation location) {
            mLocationClient.stop();
            if (location == null) {
                Tools.showToast(BrightDZXListActivity.this, getResources().getString(R.string.location_fail));
                return;
            }
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }

        public void onConnectHotSpotMessage(String s, int i) {

        }

        public void onReceivePoi(BDLocation arg0) {
        }
    }

    private void initView() {
        brightdzx_listview_left = (PullToRefreshListView) findViewById(R.id.brightdzx_listview_left);
        brightdzx_listview_middle = (PullToRefreshListView) findViewById(R.id.brightdzx_listview_middle);
        brightdzx_listview_right = (PullToRefreshListView) findViewById(R.id.brightdzx_listview_right);
        brightdzx_name = (TextView) findViewById(R.id.brightdzx_name);//projectname
        brightdzx_tableft_text = (TextView) findViewById(R.id.brightdzx_tableft_text);//待执行 0
        brightdzx_tabmiddle_text = (TextView) findViewById(R.id.brightdzx_tabmiddle_text);
        brightdzx_tabright_text = (TextView) findViewById(R.id.brightdzx_tabright_text);
        brightdzx_tableft_small = (TextView) findViewById(R.id.brightdzx_tableft_small);
        brightdzx_tabmiddle_small = (TextView) findViewById(R.id.brightdzx_tabmiddle_small);
        brightdzx_tabright_small = (TextView) findViewById(R.id.brightdzx_tabright_small);
        brightdzx_tableft_ico = (ImageView) findViewById(R.id.brightdzx_tableft_ico);//图片
        brightdzx_tabmiddle_ico = (ImageView) findViewById(R.id.brightdzx_tabmiddle_ico);
        brightdzx_tabright_ico = (ImageView) findViewById(R.id.brightdzx_tabright_ico);
        brightdzx_tableft_line = findViewById(R.id.brightdzx_tableft_line);//上三角
        brightdzx_tabmiddle_line = findViewById(R.id.brightdzx_tabmiddle_line);
        brightdzx_tabright_line = findViewById(R.id.brightdzx_tabright_line);
        initListView(brightdzx_listview_left);
        initListView(brightdzx_listview_middle);
        initListView(brightdzx_listview_right);
        brightdzx_listview_left.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
        brightdzx_listview_middle.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
        brightdzx_listview_right.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
        brightdzx_listview_left.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final TaskDetailLeftInfo taskDetailLeftInfo = list_left.get(position - 1);
                final String fynum = taskDetailLeftInfo.getNumber();
                outletid = taskDetailLeftInfo.getId();
//                addr = taskDetailLeftInfo.getCity3();
//                store_num = taskDetailLeftInfo.getCode();
//                store_name = taskDetailLeftInfo.getName();
//                longtitude = taskDetailLeftInfo.getLongtitude();
//                latitude = taskDetailLeftInfo.getLatitude();
                switch (adapterLeft.getSelect()) {
                    case 3: {
                        TextView textView = new TextView(BrightDZXListActivity.this);
                        textView.setBackgroundColor(Color.WHITE);
                        textView.setTextColor(Color.BLACK);
                        textView.setTextSize(15);
                        textView.setGravity(Gravity.CENTER_HORIZONTAL);
                        textView.setText("\n可执行时间\n\n" + taskDetailLeftInfo.getTimedetail());
                        textView.setHeight(Tools.getScreeInfoHeight(BrightDZXListActivity.this) / 2);
                        MyDialog myDialog = new MyDialog(BrightDZXListActivity.this, textView, false, 0);
                        myDialog.setMyDialogWidth(Tools.getScreeInfoWidth(BrightDZXListActivity.this) - 40);
                        myDialog.showAtLocation((BrightDZXListActivity.this.findViewById(R.id.main)),
                                Gravity.CENTER_VERTICAL, 0, 0); //设置layout在PopupWindow中显示的位置
                    }
                    case -1: {//考试任务和普通任务
                        if (adapterLeft.isClickButton()) {//考试任务
                            if ("1".equals(taskDetailLeftInfo.getIs_exe())) {
                                checkIsselect.sendPostRequest(Urls.CheckIsselect, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String s) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(s);
                                            if (jsonObject.getInt("code") == 200) {
                                                if ("1".equals(jsonObject.getString("msg"))) {
                                                    Intent intent = new Intent(BrightDZXListActivity.this, BrightPersonInfoActivity.class);
                                                    intent.putExtra("store_num", taskDetailLeftInfo.getCode());
                                                    intent.putExtra("city", taskDetailLeftInfo.getCity3());
                                                    intent.putExtra("outletid", outletid);
                                                    intent.putExtra("project_id", project_id);
                                                    intent.putExtra("projectname", projectname);
                                                    intent.putExtra("code", code);
                                                    intent.putExtra("photo_compression", photo_compression);
                                                    intent.putExtra("brand", brand);
                                                    intent.putExtra("store_name", taskDetailLeftInfo.getName());
                                                    startActivity(intent);
                                                } else if ("0".equals(jsonObject.getString("msg"))) {
                                                    Intent intent = new Intent(BrightDZXListActivity.this, BrightTwoCodeActivity.class);
                                                    intent.putExtra("city3", taskDetailLeftInfo.getCity3());
                                                    intent.putExtra("outletid", outletid);
                                                    intent.putExtra("project_id", project_id);
                                                    intent.putExtra("mytype", "1");
                                                    intent.putExtra("id", taskDetailLeftInfo.getId());
                                                    intent.putExtra("projectname", projectname);
                                                    intent.putExtra("store_name", taskDetailLeftInfo.getName());
                                                    intent.putExtra("store_num", taskDetailLeftInfo.getCode());
                                                    intent.putExtra("province", taskDetailLeftInfo.getCity());
                                                    intent.putExtra("city", taskDetailLeftInfo.getCity2());
                                                    intent.putExtra("longtitude", taskDetailLeftInfo.getLongtitude());
                                                    intent.putExtra("latitude", taskDetailLeftInfo.getLatitude());
                                                    intent.putExtra("photo_compression", photo_compression);
                                                    intent.putExtra("is_watermark", is_watermark);
                                                    intent.putExtra("code", code);
                                                    intent.putExtra("brand", brand);
                                                    intent.putExtra("is_takephoto", is_takephoto);
                                                    intent.putExtra("type", type);
                                                    intent.putExtra("is_exe", taskDetailLeftInfo.getIs_exe());
                                                    intent.putExtra("is_desc", taskDetailLeftInfo.getIs_desc());
                                                    intent.putExtra("number", taskDetailLeftInfo.getNumber());
                                                    intent.putExtra("isOffline", taskDetailLeftInfo.getIsOffline());
                                                    startActivity(intent);
                                                }
                                            } else {
                                                Tools.showToast(BrightDZXListActivity.this, jsonObject.getString("msg"));
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        CustomProgressDialog.Dissmiss();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {
                                        CustomProgressDialog.Dissmiss();
                                        Tools.showToast(BrightDZXListActivity.this, getResources().getString(R.string.network_volleyerror));
                                    }
                                }, null);
                            } else {
                                Tools.showToast(BrightDZXListActivity.this, "未到执行时间");
                            }
                        } else if (adapterLeft.isClickButton2()) {//普通任务
                            if (taskDetailLeftInfo.getIs_exe().equals("1")) {
                                //首先判断是否抽签完毕
                                checkIsselect.sendPostRequest(Urls.CheckIsselect, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String s) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(s);
                                            if (jsonObject.getInt("code") == 200) {
                                                if ("1".equals(jsonObject.getString("msg"))) {//抽签完毕
                                                    if (!TextUtils.isEmpty(fynum) && !"null".equals(fynum) && fynum.equals(AppInfo.getName
                                                            (BrightDZXListActivity.this))) {
                                                        if (taskDetailLeftInfo.getIsOffline() == 1) {
                                                            Intent intent = new Intent(BrightDZXListActivity.this, OfflinePackageActivity.class);
                                                            intent.putExtra("id", taskDetailLeftInfo.getId());
                                                            intent.putExtra("projectname", projectname);
                                                            intent.putExtra("store_name", taskDetailLeftInfo.getName());
                                                            intent.putExtra("store_num", taskDetailLeftInfo.getCode());
                                                            intent.putExtra("province", taskDetailLeftInfo.getCity());
                                                            intent.putExtra("city", taskDetailLeftInfo.getCity2());
                                                            intent.putExtra("longtitude", taskDetailLeftInfo.getLongtitude());
                                                            intent.putExtra("latitude", taskDetailLeftInfo.getLatitude());
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
                                                                Intent intent = new Intent(BrightDZXListActivity.this, StoreDescActivity.class);
                                                                intent.putExtra("id", taskDetailLeftInfo.getId());
                                                                intent.putExtra("projectname", projectname);
                                                                intent.putExtra("store_name", taskDetailLeftInfo.getName());
                                                                intent.putExtra("store_num", taskDetailLeftInfo.getCode());
                                                                intent.putExtra("province", taskDetailLeftInfo.getCity());
                                                                intent.putExtra("city", taskDetailLeftInfo.getCity2());
                                                                intent.putExtra("longtitude", taskDetailLeftInfo.getLongtitude());
                                                                intent.putExtra("latitude", taskDetailLeftInfo.getLatitude());
                                                                intent.putExtra("project_id", project_id);
                                                                intent.putExtra("photo_compression", photo_compression);
                                                                intent.putExtra("is_desc", "1");
                                                                intent.putExtra("is_watermark", is_watermark);
                                                                intent.putExtra("code", code);
                                                                intent.putExtra("brand", brand);
                                                                intent.putExtra("is_takephoto", is_takephoto);
                                                                intent.putExtra("type", type);
                                                                startActivity(intent);
                                                            } else {
                                                                Intent intent = new Intent(BrightDZXListActivity.this, TaskitemDetailActivity_12
                                                                        .class);
                                                                intent.putExtra("id", taskDetailLeftInfo.getId());
                                                                intent.putExtra("projectname", projectname);
                                                                intent.putExtra("store_name", taskDetailLeftInfo.getName());
                                                                intent.putExtra("store_num", taskDetailLeftInfo.getCode());
                                                                intent.putExtra("province", taskDetailLeftInfo.getCity());
                                                                intent.putExtra("city", taskDetailLeftInfo.getCity2());
                                                                intent.putExtra("longtitude", taskDetailLeftInfo.getLongtitude());
                                                                intent.putExtra("latitude", taskDetailLeftInfo.getLatitude());
                                                                intent.putExtra("project_id", project_id);
                                                                intent.putExtra("photo_compression", photo_compression);
                                                                intent.putExtra("is_desc", "0");
                                                                intent.putExtra("is_watermark", is_watermark);
                                                                intent.putExtra("project_type",taskDetailLeftInfo.getProject_type());
                                                                intent.putExtra("code", code);
                                                                intent.putExtra("brand", brand);
                                                                intent.putExtra("is_takephoto", is_takephoto);
                                                                intent.putExtra("type", type);
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    } else {
                                                        Tools.showToast(BrightDZXListActivity.this, "您不是访员！");
                                                    }
                                                } else if ("0".equals(jsonObject.getString("msg"))) {
                                                    Intent intent = new Intent(BrightDZXListActivity.this, BrightTwoCodeActivity.class);
                                                    intent.putExtra("city3", taskDetailLeftInfo.getCity3());
                                                    intent.putExtra("outletid", outletid);
                                                    intent.putExtra("project_id", project_id);
                                                    intent.putExtra("mytype", "1");
                                                    intent.putExtra("id", taskDetailLeftInfo.getId());
                                                    intent.putExtra("projectname", projectname);
                                                    intent.putExtra("store_name", taskDetailLeftInfo.getName());
                                                    intent.putExtra("store_num", taskDetailLeftInfo.getCode());
                                                    intent.putExtra("province", taskDetailLeftInfo.getCity());
                                                    intent.putExtra("city", taskDetailLeftInfo.getCity2());
                                                    intent.putExtra("longtitude", taskDetailLeftInfo.getLongtitude());
                                                    intent.putExtra("latitude", taskDetailLeftInfo.getLatitude());
                                                    intent.putExtra("photo_compression", photo_compression);
                                                    intent.putExtra("is_watermark", is_watermark);
                                                    intent.putExtra("code", code);
                                                    intent.putExtra("brand", brand);
                                                    intent.putExtra("is_takephoto", is_takephoto);
                                                    intent.putExtra("type", type);
                                                    intent.putExtra("is_exe", taskDetailLeftInfo.getIs_exe());
                                                    intent.putExtra("is_desc", taskDetailLeftInfo.getIs_desc());
                                                    intent.putExtra("number", taskDetailLeftInfo.getNumber());
                                                    intent.putExtra("isOffline", taskDetailLeftInfo.getIsOffline());
                                                    startActivity(intent);
                                                }
                                            } else {
                                                Tools.showToast(BrightDZXListActivity.this, jsonObject.getString("msg"));
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        CustomProgressDialog.Dissmiss();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {
                                        CustomProgressDialog.Dissmiss();
                                        Tools.showToast(BrightDZXListActivity.this, getResources().getString(R.string.network_volleyerror));
                                    }
                                }, null);
                            } else {
                                Tools.showToast(BrightDZXListActivity.this, "未到执行时间");
                            }
                        }
                    }
                    break;
                }
                adapterLeft.clearClickButton();
            }
        });
        brightdzx_listview_middle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selMiddlePosition = position - 1;
                if (adapterMiddle.isClickButton2()) {
                    TaskDetailLeftInfo taskDetailLeftInfo = list_middle.get(selMiddlePosition);
                    Intent intent = new Intent(BrightDZXListActivity.this, TaskFinishActivity.class);
                    intent.putExtra("projectname", projectname);
                    intent.putExtra("store_name", taskDetailLeftInfo.getName());
                    intent.putExtra("store_num", taskDetailLeftInfo.getCode());
                    intent.putExtra("project_id", project_id);
                    intent.putExtra("photo_compression", photo_compression);
                    intent.putExtra("store_id", taskDetailLeftInfo.getId());
                    intent.putExtra("state", "1");
                    intent.putExtra("is_watermark", is_watermark);
                    intent.putExtra("code", code);
                    intent.putExtra("brand", brand);
                    intent.putExtra("isAgain", false);
                    startActivity(intent);
                } else if (adapterMiddle.isClickButton()) {
                    ConfirmDialog.showDialog(BrightDZXListActivity.this, "确定重做吗？", null, null, null, list_middle
                            .get(selMiddlePosition), true, new ConfirmDialog.OnSystemDialogClickListener() {
                        public void leftClick(Object object) {
                        }

                        public void rightClick(Object object) {
                            if (object instanceof TaskDetailLeftInfo) {// 重做
                                TaskDetailLeftInfo taskDetailLeftInfo = (TaskDetailLeftInfo) object;
                                selStoreid = taskDetailLeftInfo.getId();
                                offlineDBHelper.deleteOfflineForRedo(AppInfo.getName(BrightDZXListActivity.this),
                                        project_id, selStoreid);
                                Redo();
                            }
                        }
                    });
                } else if (adapterMiddle.isClickButton3()) {//整店上传 TODO
                    ConfirmDialog.showDialog(BrightDZXListActivity.this, "确定上传吗？", null, null, null, list_middle
                            .get(selMiddlePosition), true, new ConfirmDialog.OnSystemDialogClickListener() {
                        public void leftClick(Object object) {
                        }

                        public void rightClick(Object object) {
                            if (object instanceof TaskDetailLeftInfo) {// 整店上传
                                TaskDetailLeftInfo taskDetailLeftInfo = (TaskDetailLeftInfo) object;
                                selStoreid = taskDetailLeftInfo.getId();
                                if (updataDBHelper.startUp(AppInfo.getName(BrightDZXListActivity.this), project_id,
                                        selStoreid)) {
                                    sendStartUpload();
                                } else {
                                    Tools.showToast(BrightDZXListActivity.this, "数据更新失败！");
                                }
                            }
                        }
                    });
                }
                adapterMiddle.clearClickButton();
            }
        });
        brightdzx_listview_right.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapterRight.isClickButton2()) {
                    TaskDetailLeftInfo taskDetailLeftInfo = list_right.get(position - 1);
                    Intent intent = new Intent(BrightDZXListActivity.this, TaskFinishActivity.class);
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
                adapterRight.clearClickButton();
            }
        });
        list_left = new ArrayList<>();
        adapterLeft = new BrightListAdapter(this, list_left, true);
        brightdzx_listview_left.setAdapter(adapterLeft);
        list_middle = new ArrayList<>();
        adapterMiddle = new TaskscheduleDetailMiddleAdapter(this, list_middle);
        brightdzx_listview_middle.setAdapter(adapterMiddle);
        list_right = new ArrayList<>();
        adapterRight = new BrightListAdapter(this, list_right);
        brightdzx_listview_right.setAdapter(adapterRight);
        View view = findViewById(R.id.brightdzx_tableft_layout);
        view.setOnClickListener(this);
        findViewById(R.id.brightdzx_tabmiddle_layout).setOnClickListener(this);
        findViewById(R.id.brightdzx_tabright_layout).setOnClickListener(this);
        onClick(view);
    }

//    private String addr, store_name, store_num, latitude, longtitude;

//    private void checkIsselect() {
//
//    }

    private void initListView(PullToRefreshListView listView) {
        listView.setMode(PullToRefreshListView.Mode.PULL_FROM_START);
        listView.setPullLabel("下拉刷新");
        listView.setRefreshingLabel("正在刷新");
        listView.setReleaseLabel("释放刷新");
    }

    @Override
    public void onBack() {
        baseFinish();
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

    private void sendStartUpload() {
        Startupload.sendPostRequest(Urls.Startupload, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        if (selMiddlePosition >= 0 && selMiddlePosition < list_middle.size()) {
                            list_middle.get(selMiddlePosition).setAgain(false);
                            adapterMiddle.notifyDataSetChanged();
                            systemDBHelper.packPhotoUpload(BrightDZXListActivity.this, AppInfo.getName
                                    (BrightDZXListActivity.this), project_id, selStoreid);
                            Intent service = new Intent("com.orange.oy.UpdataNewService");
                            service.setPackage("com.orange.oy");
                            startService(service);
                        }
                    } else {
                        Tools.showToast(BrightDZXListActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Tools.showToast(BrightDZXListActivity.this, getResources().getString(R.string.network_error));
                } finally {
                    CustomProgressDialog.Dissmiss();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(BrightDZXListActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private void Redo() {
        Redo.sendPostRequest(Urls.Redo, new Response.Listener<String>() {
                    public void onResponse(String s) {
                        Tools.d(s);
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            int code = jsonObject.getInt("code");
                            if (code == 200) {
                                String username = AppInfo.getName(BrightDZXListActivity.this);
                                offlineDBHelper.deleteTraffic(username, project_id, selStoreid);
                                updataDBHelper.removeTask(username, project_id, selStoreid);
                                int temp = Tools.StringToInt(brightdzx_tableft_small.getText().toString());
                                if (temp == -1) {
                                    temp = 0;
                                }
                                temp++;
                                brightdzx_tableft_small.setText((temp > 999) ? "..." : temp + "");
                                temp = Tools.StringToInt(brightdzx_tabmiddle_small.getText().toString());
                                temp--;
                                if (temp == -1) {
                                    temp = 0;
                                }
                                brightdzx_tabmiddle_small.setText((temp > 999) ? "..." : temp + "");
                                refreshMiddle();
                                Tools.showToast(BrightDZXListActivity.this, jsonObject.getString("msg"));
                            } else {
                                Tools.showToast(BrightDZXListActivity.this, jsonObject.getString("msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Tools.showToast(BrightDZXListActivity.this, getResources().getString(R.string
                                    .network_error));
                        }
                        CustomProgressDialog.Dissmiss();
                    }
                }, new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError volleyError) {
                        CustomProgressDialog.Dissmiss();
                        Tools.showToast(BrightDZXListActivity.this, getResources().getString(R.string
                                .network_volleyerror));
                    }
                }
        );
    }

    private void Selectprojectwcjd() {
        Selectprojectwcjd.sendPostRequest(Urls.Selectprojectwcjd, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        jsonObject = jsonObject.getJSONObject("datas");
                        int temp = Tools.StringToInt(jsonObject.getString("dzxnum"));
                        if (temp == -1) {
                            temp = 0;
                        }
                        brightdzx_tableft_small.setText((temp > 999) ? "..." : temp + "");
                        temp = Tools.StringToInt(jsonObject.getString("zlhsnum"));
                        if (temp == -1) {
                            temp = 0;
                        }
                        brightdzx_tabright_small.setText((temp > 999) ? "..." : temp + "");
                        temp = Tools.StringToInt(jsonObject.getString("wcnum"));
                        if (temp == -1) {
                            temp = 0;
                        }
                        brightdzx_tabmiddle_small.setText((temp > 999) ? "..." : temp + "");
                    } else {
                        Tools.showToast(BrightDZXListActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(BrightDZXListActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(BrightDZXListActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    private void getDataForLeft() {
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
                            taskDetailLeftInfo.setTimedetail(timeDetail);
                            taskDetailLeftInfo.setLatitude(jsonObject.getString("latitude"));
                            taskDetailLeftInfo.setLongtitude(jsonObject.getString("longtitude"));
                            list_left.add(taskDetailLeftInfo);
                        }
                        try {
                            offlineDBHelper.isCompletedForStore(AppInfo.getName(BrightDZXListActivity.this), project_id,
                                    list_left);
                        } catch (Exception e) {
                            e.printStackTrace();
                            MobclickAgent.reportError(BrightDZXListActivity.this,
                                    "taskscheduledetailactivity error1:" + e.getMessage());
                        }
                        brightdzx_listview_left.onRefreshComplete();
                        if (length < 15) {
                            brightdzx_listview_left.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            brightdzx_listview_left.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                        adapterLeft.notifyDataSetChanged();
                    } else {
                        Tools.showToast(BrightDZXListActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    MobclickAgent.reportError(BrightDZXListActivity.this,
                            "TaskscheduleDetailActivity getData ParseJson:" + e.getMessage());
                    Tools.showToast(BrightDZXListActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
                brightdzx_listview_left.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                searchStr = "";
                Dzxstartlist.setIsShowDialog(false);
                brightdzx_listview_left.onRefreshComplete();
                Tools.showToast(BrightDZXListActivity.this, getResources().getString(R.string
                        .network_volleyerror));
            }
        });
    }

    private void getDataForMiddle() {
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
                            list_middle.add(taskDetailLeftInfo);
                        }
                        brightdzx_listview_middle.onRefreshComplete();
                        if (length < 15) {
                            brightdzx_listview_middle.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            brightdzx_listview_middle.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                        adapterMiddle.notifyDataSetChanged();
                    } else {
                        Tools.showToast(BrightDZXListActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(BrightDZXListActivity.this, getResources().getString(R.string.network_error));
                }
                brightdzx_listview_middle.onRefreshComplete();
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                searchStr = "";
                Zxwcstartlist.setIsShowDialog(false);
                brightdzx_listview_middle.onRefreshComplete();
                Tools.showToast(BrightDZXListActivity.this, getResources().getString(R.string
                        .network_volleyerror));
            }
        });
    }

    private void getDataForRight() {
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
                            list_right.add(taskDetailLeftInfo);
                        }
                        brightdzx_listview_right.onRefreshComplete();
                        if (length < 15) {
                            brightdzx_listview_right.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            brightdzx_listview_right.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                        adapterRight.notifyDataSetChanged();
                    } else {
                        Tools.showToast(BrightDZXListActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(BrightDZXListActivity.this, getResources().getString(R.string.network_error));
                }
                brightdzx_listview_right.onRefreshComplete();
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                searchStr = "";
                Zlyhsstartlist.setIsShowDialog(false);
                brightdzx_listview_right.onRefreshComplete();
                Tools.showToast(BrightDZXListActivity.this, getResources().getString(R.string
                        .network_volleyerror));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isRefresh) {
            isRefresh = false;
            Selectprojectwcjd();
            refreshLeft();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.brightdzx_tableft_layout:
                brightdzx_tableft_ico.setImageResource(R.mipmap.wangdian_dzx_1);
                brightdzx_tableft_text.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                brightdzx_tableft_line.setVisibility(View.VISIBLE);
                brightdzx_tabmiddle_ico.setImageResource(R.mipmap.bright_upload_1);
                brightdzx_tabmiddle_text.setTextColor(getResources().getColor(R.color.colorPrimaryDark2));
                brightdzx_tabmiddle_line.setVisibility(View.INVISIBLE);
                brightdzx_tabright_ico.setImageResource(R.mipmap.wangdian_zlyhs_2);
                brightdzx_tabright_text.setTextColor(getResources().getColor(R.color.colorPrimaryDark2));
                brightdzx_tabright_line.setVisibility(View.INVISIBLE);
                brightdzx_listview_left.setVisibility(View.VISIBLE);
                brightdzx_listview_middle.setVisibility(View.GONE);
                brightdzx_listview_right.setVisibility(View.GONE);
                if (list_left == null || list_left.isEmpty()) {
                    refreshLeft();
                }
                break;
            case R.id.brightdzx_tabmiddle_layout:
                brightdzx_tableft_ico.setImageResource(R.mipmap.wangdian_dzx_2);
                brightdzx_tableft_text.setTextColor(getResources().getColor(R.color.colorPrimaryDark2));
                brightdzx_tableft_line.setVisibility(View.INVISIBLE);
                brightdzx_tabmiddle_ico.setImageResource(R.mipmap.bright_upload_2);
                brightdzx_tabmiddle_text.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                brightdzx_tabmiddle_line.setVisibility(View.VISIBLE);
                brightdzx_tabright_ico.setImageResource(R.mipmap.wangdian_zlyhs_2);
                brightdzx_tabright_text.setTextColor(getResources().getColor(R.color.colorPrimaryDark2));
                brightdzx_tabright_line.setVisibility(View.INVISIBLE);
                brightdzx_listview_left.setVisibility(View.GONE);
                brightdzx_listview_middle.setVisibility(View.VISIBLE);
                brightdzx_listview_right.setVisibility(View.GONE);
                if (list_middle == null || list_middle.isEmpty()) {
                    refreshMiddle();
                }
                break;
            case R.id.brightdzx_tabright_layout:
                brightdzx_tableft_ico.setImageResource(R.mipmap.wangdian_dzx_2);
                brightdzx_tableft_text.setTextColor(getResources().getColor(R.color.colorPrimaryDark2));
                brightdzx_tableft_line.setVisibility(View.INVISIBLE);
                brightdzx_tabmiddle_ico.setImageResource(R.mipmap.bright_upload_1);
                brightdzx_tabmiddle_text.setTextColor(getResources().getColor(R.color.colorPrimaryDark2));
                brightdzx_tabmiddle_line.setVisibility(View.INVISIBLE);
                brightdzx_tabright_ico.setImageResource(R.mipmap.wangdian_zlyhs_1);
                brightdzx_tabright_text.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                brightdzx_tabright_line.setVisibility(View.VISIBLE);
                brightdzx_listview_left.setVisibility(View.GONE);
                brightdzx_listview_middle.setVisibility(View.GONE);
                brightdzx_listview_right.setVisibility(View.VISIBLE);
                if (list_right == null || list_right.isEmpty()) {
                    refreshRight();
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
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}