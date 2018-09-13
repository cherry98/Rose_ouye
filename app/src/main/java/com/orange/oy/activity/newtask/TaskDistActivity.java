package com.orange.oy.activity.newtask;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
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
import com.orange.oy.activity.TaskillustratesActivity;
import com.orange.oy.activity.TaskitemDetailActivity_12;
import com.orange.oy.adapter.TaskDistAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.OfflineDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.DataUploadDialog;
import com.orange.oy.dialog.TaskChangeDialog;
import com.orange.oy.info.MyteamNewfdInfo;
import com.orange.oy.info.TaskDetailLeftInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.CharacterParser;
import com.orange.oy.util.FileCache;
import com.orange.oy.util.PinyinComparatorForMyteam;
import com.orange.oy.view.AppTitle;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * 分派任务页||演练任务
 */
public class TaskDistActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener,
        TaskChangeDialog.OnItemClickListener, TaskDistAdapter.OnShowItemClickListener {
    private AppTitle appTitle;

    private void initTitle() {
        appTitle = (AppTitle) findViewById(R.id.taskdist_title);
        showSearch();
        appTitle.settingName(getResources().getString(R.string.taskschdetail));
        appTitle.showBack(this);
    }

    private String searchStr = "";

    @Override
    protected void onDestroy() {
        if (mLocationClient != null)
            mLocationClient.stop();
        if (isShow) {
            for (TaskDetailLeftInfo taskDetailLeftInfo : list_left) {
                taskDetailLeftInfo.setChecked(false);
                taskDetailLeftInfo.setShow(false);
            }
            taskDistAdapterLeft.notifyDataSetChanged();
            isShow = false;
            taskdist_listview_left.setLongClickable(true);
            appTitle.hideExit();
        }
        super.onDestroy();
    }

    public static boolean isRefresh;

    protected void onResume() {
        super.onResume();
        if (isRefresh) {
            isRefresh = false;
            refreshLeft();
        }
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
        if (getTeamData != null) {
            getTeamData.stop(Urls.Myteam);
        }
        if (changeAccessed != null) {
            changeAccessed.stop(Urls.Changeaccessed);
        }
        if (Selectprojectwcjd != null) {
            Selectprojectwcjd.stop(Urls.Selectprojectwcjd);
        }
        if (checkIsselect != null) {
            checkIsselect.stop(Urls.CheckIsselect);
        }
        if (checkinvalid != null) {
            checkinvalid.stop(Urls.CheckInvalid);
        }
    }

    private void initNetworkConnection() {
        Dzxstartlist = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("page", page_left + "");
                params.put("project_id", project_id);
                params.put("city", city);
                params.put("user_mobile", AppInfo.getName(TaskDistActivity.this));
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
                params.put("user_mobile", AppInfo.getName(TaskDistActivity.this));
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
                params.put("user_mobile", AppInfo.getName(TaskDistActivity.this));
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
                params.put("user_mobile", AppInfo.getName(TaskDistActivity.this));
                return params;
            }
        };
        getTeamData = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(TaskDistActivity.this));
                return params;
            }
        };
        getTeamData.setIsShowDialog(true);
        changeAccessed = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("storeidlist", storeidlist);
                params.put("accessednum", accessednum);
                params.put("projectid", project_id);
                params.put("usermobile", AppInfo.getName(TaskDistActivity.this));
                params.put("invisible", invisible);
                return params;
            }
        };
        changeAccessed.setIsShowDialog(true);
        checkIsselect = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("outletid", storeid);
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(TaskDistActivity.this));
                params.put("longitude", longitude + "");
                params.put("latitude", latitude + "");
                return params;
            }
        };
        checkIsselect.setIsShowDialog(true);
        checkinvalid = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(TaskDistActivity.this));
                params.put("token", Tools.getToken());
                params.put("outletid", storeid);
                params.put("lon", longitude + "");
                params.put("lat", latitude + "");
                params.put("address", address);
                return params;
            }
        };
    }

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
                    if (taskdist_listview_left.getVisibility() == View.VISIBLE) {
                        Dzxstartlist.setIsShowDialog(true);
                        refreshLeft();
                    } else if (taskdist_listview_middle.getVisibility() == View.VISIBLE) {
                        Zxwcstartlist.setIsShowDialog(true);
                        refreshMiddle();
                    } else if (taskdist_listview_right.getVisibility() == View.VISIBLE) {
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

    private void initView() {
        taskdist_listview_left = (PullToRefreshListView) findViewById(R.id.taskdist_listview_left);
        taskdist_listview_middle = (PullToRefreshListView) findViewById(R.id.taskdist_listview_middle);
        taskdist_listview_right = (PullToRefreshListView) findViewById(R.id.taskdist_listview_right);
        taskdist_tab_left = (TextView) findViewById(R.id.taskdist_tab_left);
        taskdist_tab_middle = (TextView) findViewById(R.id.taskdist_tab_middle);
        taskdist_tab_right = (TextView) findViewById(R.id.taskdist_tab_right);
        taskdist_tab_left_line = findViewById(R.id.taskdist_tab_left_line);
        taskdist_tab_middle_line = findViewById(R.id.taskdist_tab_middle_line);
        taskdist_tab_right_line = findViewById(R.id.taskdist_tab_right_line);
        taskdist_name = (TextView) findViewById(R.id.taskdist_name);
        taskdist_time = (TextView) findViewById(R.id.taskdist_time);
        taskdist_period = (TextView) findViewById(R.id.taskdist_period);
    }

    private PullToRefreshListView taskdist_listview_left, taskdist_listview_middle, taskdist_listview_right;
    private TaskDistAdapter taskDistAdapterLeft, taskDistAdapterMiddle, taskDistAdapterRight;
    private ArrayList<TaskDetailLeftInfo> list_left, list_middle, list_right;
    private int page_left, page_middle, page_right;
    private NetworkConnection Dzxstartlist, Zxwcstartlist, Zlyhsstartlist, Selectprojectwcjd, getTeamData, changeAccessed,
            checkIsselect, checkinvalid;
    private Intent data;
    private String project_id, city, type;
    private TextView taskdist_tab_left, taskdist_tab_middle, taskdist_tab_right;
    private View taskdist_tab_left_line, taskdist_tab_middle_line, taskdist_tab_right_line;
    private TextView taskdist_name, taskdist_time, taskdist_period;
    private String projectname, photo_compression, is_watermark, code, brand, is_takephoto, storenum;
    private CharacterParser characterParser;
    private PinyinComparatorForMyteam pinyinComparatorForMyteam;
    private OfflineDBHelper offlineDBHelper;
    private double longitude, latitude;//经纬度
    public LocationClient mLocationClient = null;
    private String project_property, address, storeid;
    public MyLocationListenner myListener = new MyLocationListenner();
    private int locType;
    private static boolean isShow; // 是否显示CheckBox标识
    private String storeidlist;//拼接的网点id
    private String invisible = "0", isRelZBproject;//0为屏蔽

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_dist);
        data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        list_left = new ArrayList<>();
        list_middle = new ArrayList<>();
        list_right = new ArrayList<>();
        initTitle();
        initNetworkConnection();
        initView();
        offlineDBHelper = new OfflineDBHelper(this);
        characterParser = CharacterParser.getInstance();
        pinyinComparatorForMyteam = new PinyinComparatorForMyteam();
        project_id = data.getStringExtra("id");
        city = data.getStringExtra("city");
        project_property = data.getStringExtra("project_property");
        if (city == null) {
            city = "";
        }
        String standard_state = data.getStringExtra("standard_state");
        if ("1".equals(standard_state)) {
            findViewById(R.id.taskdist_standard).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.taskdist_standard).setVisibility(View.GONE);
        }
        type = data.getStringExtra("type");
        projectname = data.getStringExtra("project_name");
        photo_compression = data.getStringExtra("photo_compression");
        is_watermark = data.getStringExtra("is_watermark");
        code = data.getStringExtra("code");
        brand = data.getStringExtra("brand");
        is_takephoto = data.getStringExtra("is_takephoto");
        initListview(taskdist_listview_left);
        initListview(taskdist_listview_middle);
        initListview(taskdist_listview_right);
        onRefreshListView();

        taskdist_time.setText("起止时间:" + data.getStringExtra("begin_date") + "~" + data.getStringExtra("end_date"));
        taskdist_name.setText(data.getStringExtra("project_name"));
        taskdist_period.setText("审核周期:" + data.getStringExtra("check_time") + "天");
        ((TextView) findViewById(R.id.taskdist_person)).setText("发布商家:【" + data.getStringExtra("project_person") + "】");
        taskDistAdapterLeft = new TaskDistAdapter(this, list_left);
        taskdist_listview_left.setAdapter(taskDistAdapterLeft);
        taskDistAdapterLeft.setOnShowItemClickListener(this);
        taskDistAdapterMiddle = new TaskDistAdapter(this, list_middle);
        taskdist_listview_middle.setAdapter(taskDistAdapterMiddle);
        taskDistAdapterRight = new TaskDistAdapter(this, list_right);
        taskdist_listview_right.setAdapter(taskDistAdapterRight);
        onItemClickListView1();
        onItemClickListView2();
        onItemClickListView3();
        onItemLongClick();
        taskdist_tab_left.setOnClickListener(this);
        taskdist_tab_middle.setOnClickListener(this);
        taskdist_tab_right.setOnClickListener(this);
        findViewById(R.id.taskdist_standard).setOnClickListener(this);
        onClick(taskdist_tab_left);
        checkPermission();
        initLocation();
    }

    private void onItemLongClick() {
        taskdist_listview_left.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TaskDetailLeftInfo taskDetailLeftInf = list_left.get(position - 1);
                storeidlist = null;
                String fynum = taskDetailLeftInf.getIdentity();
                if (!TextUtils.isEmpty(fynum) && !"null".equals(fynum) && fynum.equals(AppInfo.getName(TaskDistActivity.this))) {
                    if (isShow) {
                        return false;
                    } else {
                        isShow = true;
                        for (TaskDetailLeftInfo taskDetailLeftInfo : list_left) {
                            taskDetailLeftInfo.setShow(true);
                        }
                        taskDistAdapterLeft.notifyDataSetChanged();
                        appTitle.settingExit("分配", new AppTitle.OnExitClickForAppTitle() {
                            @Override
                            public void onExit() {
                                getTeamData();
                            }
                        });
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void onRefreshListView() {
        taskdist_listview_left.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
        taskdist_listview_middle.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
        taskdist_listview_right.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
    }


    private void initListview(PullToRefreshListView listview) {
        listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listview.setPullLabel(getResources().getString(R.string.listview_down));
        listview.setRefreshingLabel(getResources().getString(R.string.listview_refush));
        listview.setReleaseLabel(getResources().getString(R.string.listview_down2));
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


    private void onItemClickListView1() {
        taskdist_listview_left.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final TaskDetailLeftInfo taskDetailLeftInfo = list_left.get(position - 1);
                final String fynum = taskDetailLeftInfo.getNumber();
                if (!TextUtils.isEmpty(fynum) && !"null".equals(fynum) && fynum.equals(AppInfo.getName(TaskDistActivity.this))) {
                    if (isShow) {
                        boolean isChecked = taskDetailLeftInfo.isChecked();
                        if (isChecked) {
                            taskDetailLeftInfo.setChecked(false);
                        } else {
                            taskDetailLeftInfo.setChecked(true);
                        }
                        taskDistAdapterLeft.notifyDataSetChanged();
                    } else {
                        storeid = taskDetailLeftInfo.getId();
                        if (taskDistAdapterLeft != null) {
                            if (locType == 61 || locType == 161) {
                                checkinvalid.sendPostRequest(Urls.CheckInvalid, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String s) {
                                        Tools.d(s);
                                        try {
                                            JSONObject jsonObject = new JSONObject(s);
                                            int code = jsonObject.getInt("code");
                                            if (code == 200) {
                                                checkInvalid(taskDetailLeftInfo, fynum);
                                            } else if (code == 2) {
                                                ConfirmDialog.showDialog(TaskDistActivity.this, null, jsonObject.getString("msg"), null,
                                                        "确定", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                                            @Override
                                                            public void leftClick(Object object) {

                                                            }

                                                            @Override
                                                            public void rightClick(Object object) {
                                                            }
                                                        }).goneLeft();
                                            } else if (code == 3) {
                                                ConfirmDialog.showDialog(TaskDistActivity.this, null, jsonObject.getString("msg"), "取消",
                                                        "继续执行", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                                                            @Override
                                                            public void leftClick(Object object) {

                                                            }

                                                            @Override
                                                            public void rightClick(Object object) {
                                                                checkInvalid(taskDetailLeftInfo, fynum);
                                                            }
                                                        });
                                            } else {
                                                Tools.showToast(TaskDistActivity.this, jsonObject.getString("msg"));
                                            }
                                        } catch (JSONException e) {
                                            Tools.showToast(TaskDistActivity.this, getResources().getString(R.string.network_error));
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {
                                        Tools.showToast(TaskDistActivity.this, getResources().getString(R.string
                                                .network_volleyerror));
                                    }
                                }, null);
                            } else if (locType == 167) {
                                Tools.showToast2(TaskDistActivity.this, "请您检查是否开启权限，尝试重新请求定位");
                            } else {
                                Tools.showToast2(TaskDistActivity.this, "请您检查网络是否通畅或是否允许访问位置信息,尝试重新请求定位");
                            }
                        }
                    }
                } else {
                    Tools.showToast2(TaskDistActivity.this, "长按网点分配给自己后方可执行");
                }
            }
        });
    }

    private void checkInvalid(TaskDetailLeftInfo taskDetailLeftInfo, String fynum) {
        if (!TextUtils.isEmpty(fynum) && !"null".equals(fynum) && fynum.equals(AppInfo.getName
                (TaskDistActivity.this))) {
            if (taskDetailLeftInfo.getIs_exe().equals("1")) {
                if (taskDetailLeftInfo.getIsOffline() == 1) {
                    Intent intent = new Intent(TaskDistActivity.this, OfflinePackageActivity.class);
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
                        Intent intent = new Intent(TaskDistActivity.this, StoreDescActivity.class);
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
                        Intent intent = new Intent(TaskDistActivity.this, TaskitemDetailActivity_12
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
                Tools.showToast(TaskDistActivity.this, "未到执行时间");
            }
        } else {
            Tools.showToast(TaskDistActivity.this, "您不是访员！");
        }
    }


    private void onItemClickListView3() {
        taskdist_listview_right.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (taskDistAdapterRight != null) {
                    TaskDetailLeftInfo taskDetailLeftInfo = list_right.get(position - 1);
                    Intent intent = new Intent(TaskDistActivity.this, TaskFinishActivity.class);
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
            }
        });
    }

    private void onItemClickListView2() {
        taskdist_listview_middle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (taskDistAdapterMiddle != null) {
                    TaskDetailLeftInfo taskDetailLeftInfo = list_middle.get(position - 1);
                    Intent intent = new Intent(TaskDistActivity.this, TaskFinishActivity.class);
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
            }
        });
    }


    private ArrayList<MyteamNewfdInfo> listForTeam;

    private void getTeamData() {
        getTeamData.sendPostRequest(Urls.Myteam, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (listForTeam == null) {
                        listForTeam = new ArrayList<MyteamNewfdInfo>();
                    } else {
                        listForTeam.clear();
                    }
                    if (code == 200) {
                        JSONArray jsonArray = jsonObject.getJSONArray("datas");
                        int length = jsonArray.length();
                        String name, note;
                        for (int i = 0; i < length; i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            MyteamNewfdInfo myteamNewfdInfo = new MyteamNewfdInfo();
                            name = jsonObject.getString("user_name");
                            myteamNewfdInfo.setName(name);
                            myteamNewfdInfo.setImg(Urls.ImgIp + jsonObject.getString("img_url"));
                            myteamNewfdInfo.setId(jsonObject.getString("user_mobile"));
                            note = jsonObject.getString("note");
                            if (!TextUtils.isEmpty(note) && !note.equals("null")) {
                                myteamNewfdInfo.setNote(note);
                                name = note;
                            }
                            //汉字转换成拼音
                            String pinyin = characterParser.getSelling(name);
                            String sortString = pinyin.substring(0, 1).toUpperCase();
                            // 正则表达式，判断首字母是否是英文字母
                            if (sortString.matches("[A-Z]")) {
                                myteamNewfdInfo.setSortLetters(sortString.toUpperCase());
                            } else {
                                myteamNewfdInfo.setSortLetters("#");
                            }
                            listForTeam.add(myteamNewfdInfo);
                        }
                        Collections.sort(listForTeam, pinyinComparatorForMyteam);
                        TaskChangeDialog.showDialog(TaskDistActivity.this, listForTeam, TaskDistActivity.this);
                    } else {
                        Tools.showToast(TaskDistActivity.this, jsonObject.getString("msg"));
                        TaskChangeDialog.showDialog(TaskDistActivity.this, listForTeam,
                                TaskDistActivity.this);
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskDistActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskDistActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
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
                        isRelZBproject = jsonObject.getString("isRelZBproject");
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
                            taskDetailLeftInfo.setMoney(jsonObject.getString("money"));
                            taskDetailLeftInfo.setExe_time(jsonObject.getString("exe_time"));
                            taskDetailLeftInfo.setMoney_unit(data.getStringExtra("money_unit"));
                            taskDetailLeftInfo.setHavetime(jsonObject.getString("havetime"));
                            taskDetailLeftInfo.setTimedetail(timeDetail);
                            list_left.add(taskDetailLeftInfo);
                        }
                        try {
//                            offlineDBHelper.isCompletedForStore(AppInfo.getName(TaskDistActivity.this), project_id,
//                                    list_left);//TODO
                        } catch (Exception e) {
                            e.printStackTrace();
                            MobclickAgent.reportError(TaskDistActivity.this,
                                    "taskscheduledetailactivity error1:" + e.getMessage());
                        }
                        taskdist_listview_left.onRefreshComplete();
                        if (length < 15) {
                            taskdist_listview_left.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            taskdist_listview_left.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                        taskDistAdapterLeft.notifyDataSetChanged();
                    } else {
                        Tools.showToast(TaskDistActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    MobclickAgent.reportError(TaskDistActivity.this,
                            "TaskscheduleDetailActivity getData ParseJson:" + e.getMessage());
                    Tools.showToast(TaskDistActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
                taskdist_listview_left.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                searchStr = "";
                Dzxstartlist.setIsShowDialog(false);
                taskdist_listview_left.onRefreshComplete();
                Tools.showToast(TaskDistActivity.this, getResources().getString(R.string
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
                            taskDetailLeftInfo.setNickname(jsonObject.getString("accessed_name"));
                            taskDetailLeftInfo.setAgain("0".equals(jsonObject.getString("is_upload")));
                            taskDetailLeftInfo.setMoney(jsonObject.getString("money"));
                            taskDetailLeftInfo.setMoney_unit(data.getStringExtra("money_unit"));
                            list_middle.add(taskDetailLeftInfo);
                        }
                        taskdist_listview_middle.onRefreshComplete();
                        if (length < 15) {
                            taskdist_listview_middle.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            taskdist_listview_middle.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                        taskDistAdapterMiddle.notifyDataSetChanged();
                    } else {
                        Tools.showToast(TaskDistActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskDistActivity.this, getResources().getString(R.string.network_error));
                }
                taskdist_listview_middle.onRefreshComplete();
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                searchStr = "";
                Zxwcstartlist.setIsShowDialog(false);
                taskdist_listview_middle.onRefreshComplete();
                Tools.showToast(TaskDistActivity.this, getResources().getString(R.string
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
                            taskDetailLeftInfo.setNickname(jsonObject.getString("accessed_name"));
                            taskDetailLeftInfo.setMoney(jsonObject.getString("money"));
                            taskDetailLeftInfo.setMoney_unit(data.getStringExtra("money_unit"));
                            list_right.add(taskDetailLeftInfo);
                        }
                        taskdist_listview_right.onRefreshComplete();
                        if (length < 15) {
                            taskdist_listview_right.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            taskdist_listview_right.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                        taskDistAdapterRight.notifyDataSetChanged();
                    } else {
                        Tools.showToast(TaskDistActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskDistActivity.this, getResources().getString(R.string.network_error));
                }
                taskdist_listview_right.onRefreshComplete();
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                searchStr = "";
                Zlyhsstartlist.setIsShowDialog(false);
                taskdist_listview_right.onRefreshComplete();
                Tools.showToast(TaskDistActivity.this, getResources().getString(R.string
                        .network_volleyerror));
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taskdist_tab_left: {
                taskdist_tab_left.setTextColor(getResources().getColor(R.color.changetext));
                taskdist_tab_left_line.setVisibility(View.VISIBLE);
                taskdist_tab_middle.setTextColor(getResources().getColor(R.color.myreward_two));
                taskdist_tab_middle_line.setVisibility(View.INVISIBLE);
                taskdist_tab_right.setTextColor(getResources().getColor(R.color.myreward_two));
                taskdist_tab_right_line.setVisibility(View.INVISIBLE);
                taskdist_listview_left.setVisibility(View.VISIBLE);
                taskdist_listview_middle.setVisibility(View.GONE);
                taskdist_listview_right.setVisibility(View.GONE);
                if (list_left == null || list_left.isEmpty()) {
                    refreshLeft();
                }
            }
            break;
            case R.id.taskdist_tab_middle: {
                taskdist_tab_left.setTextColor(getResources().getColor(R.color.myreward_two));
                taskdist_tab_left_line.setVisibility(View.INVISIBLE);
                taskdist_tab_middle.setTextColor(getResources().getColor(R.color.changetext));
                taskdist_tab_middle_line.setVisibility(View.VISIBLE);
                taskdist_tab_right.setTextColor(getResources().getColor(R.color.myreward_two));
                taskdist_tab_right_line.setVisibility(View.INVISIBLE);
                taskdist_listview_left.setVisibility(View.GONE);
                taskdist_listview_middle.setVisibility(View.VISIBLE);
                taskdist_listview_right.setVisibility(View.GONE);
                if (list_middle == null || list_middle.isEmpty()) {
                    refreshMiddle();
                }
            }
            break;
            case R.id.taskdist_tab_right: {
                taskdist_tab_left.setTextColor(getResources().getColor(R.color.myreward_two));
                taskdist_tab_left_line.setVisibility(View.INVISIBLE);
                taskdist_tab_middle.setTextColor(getResources().getColor(R.color.myreward_two));
                taskdist_tab_middle_line.setVisibility(View.INVISIBLE);
                taskdist_tab_right.setTextColor(getResources().getColor(R.color.changetext));
                taskdist_tab_right_line.setVisibility(View.VISIBLE);
                taskdist_listview_left.setVisibility(View.GONE);
                taskdist_listview_middle.setVisibility(View.GONE);
                taskdist_listview_right.setVisibility(View.VISIBLE);
                if (list_right == null || list_right.isEmpty()) {
                    refreshRight();
                }
            }
            break;
            case R.id.taskdist_standard: {
                Intent intent = new Intent(this, TaskillustratesActivity.class);
                intent.putExtra("projectid", project_id);
                intent.putExtra("projectname", data.getStringExtra("project_name"));
                intent.putExtra("isShow", "0");//是否显示不再显示复选框
                startActivity(intent);
            }
            break;
        }
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    private String accessednum;

    @Override
    public void onItemClick(MyteamNewfdInfo myteamNewfdInfo) {//换人
        isShow = false;
        accessednum = myteamNewfdInfo.getId();
        new ClearCache().executeOnExecutor(Executors.newCachedThreadPool());
    }

    @Override
    public void oneself() {//换给自己
        accessednum = AppInfo.getName(this);
        changeAccessed();
    }

    //拼接网点id
    @Override
    public void onShowItemClick(TaskDetailLeftInfo taskDetailLeftInfo) {
        if (taskDetailLeftInfo.isChecked()) {
            if (storeidlist == null) {
                storeidlist = taskDetailLeftInfo.getId();
            } else {
                storeidlist = storeidlist + "," + taskDetailLeftInfo.getId();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isShow) {
            for (TaskDetailLeftInfo taskDetailLeftInfo : list_left) {
                taskDetailLeftInfo.setChecked(false);
                taskDetailLeftInfo.setShow(false);
            }
            taskDistAdapterLeft.notifyDataSetChanged();
            isShow = false;
            taskdist_listview_left.setLongClickable(true);
            appTitle.hideExit();
        } else {
            super.onBackPressed();
        }
    }

    class ClearCache extends AsyncTask {
        protected void onPreExecute() {
            if (offlineDBHelper == null) {
                offlineDBHelper = new OfflineDBHelper(TaskDistActivity.this);
            }
            CustomProgressDialog.showProgressDialog(TaskDistActivity.this, "清理缓存");
        }


        protected Object doInBackground(Object[] params) {
            offlineDBHelper.clearCache();
            recurDelete(new File(FileCache.getCacheDir(TaskDistActivity.this).getPath() + "/download"));
            AppInfo.clearCachesize(TaskDistActivity.this);
            return null;
        }

        public void recurDelete(File f) {
            if (f == null || !f.exists()) {
                return;
            }
            for (File fi : f.listFiles()) {
                if (fi.isDirectory()) {
                    recurDelete(fi);
                } else {
                    fi.delete();
                }
            }
        }

        protected void onPostExecute(Object o) {
            CustomProgressDialog.Dissmiss();
            changeAccessed();
        }
    }

    /**
     * 换人接口
     */
    private void changeAccessed() {
        if ("1".equals(isRelZBproject)) {
            DataUploadDialog.showDialog(this, false, new DataUploadDialog.OnDataUploadClickListener() {
                        @Override
                        public void firstClick() {
                            invisible = "1";
                        }

                        @Override
                        public void secondClick() {
                            invisible = "0";
                        }

                        @Override
                        public void thirdClick() {

                        }
                    }, getResources().getString(R.string.upload_permisson),
                    getResources().getString(R.string.upload_permisson2),
                    getResources().getString(R.string.upload_permisson3),
                    getResources().getString(R.string.upload_permisson4), null);
        }
        if (storeidlist == null) {
            Tools.showToast(this, "请至少选择一个网点");
            return;
        }
        changeAccessed.sendPostRequest(Urls.Changeaccessed, new Response.Listener<String>() {
            public void onResponse(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        isShow = false;
                        refreshLeft();
                        appTitle.hideExit();
                    } else {
                        Tools.showToast(TaskDistActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskDistActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskDistActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
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
                Tools.showToast(TaskDistActivity.this, getResources().getString(R.string.location_fail));
                return;
            }
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            address = location.getAddrStr();
            locType = location.getLocType();
        }

        public void onConnectHotSpotMessage(String s, int i) {

        }

        public void onReceivePoi(BDLocation arg0) {
        }
    }

}
