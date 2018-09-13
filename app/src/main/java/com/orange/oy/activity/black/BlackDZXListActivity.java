package com.orange.oy.activity.black;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.orange.oy.activity.TaskillustratesActivity;
import com.orange.oy.adapter.TaskDistAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.OfflineDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
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
 * 暗访任务网点列表
 */
public class BlackDZXListActivity extends BaseActivity implements TaskChangeDialog.OnItemClickListener, OnClickListener
        , TaskDistAdapter.OnShowItemClickListener {
    private void initTitle() {
        blackdzx_title = (AppTitle) findViewById(R.id.blackdzx_title);
        blackdzx_title.settingName("网点列表");
        blackdzx_title.showBack(new AppTitle.OnBackClickForAppTitle() {
            public void onBack() {
                baseFinish();
            }
        });
        showSearch(blackdzx_title);
    }

    public void showSearch(AppTitle appTitle) {
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
                    Dzxstartlist.setIsShowDialog(true);
                    refreshLeft();
                    v.setText("");
                    return true;
                }
                return false;
            }
        });
    }

    private NetworkConnection getTeamData, changeAccessed, checkinvalid;

    private void initNetworkConnection() {
        getTeamData = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(BlackDZXListActivity.this));
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
                params.put("usermobile", AppInfo.getName(BlackDZXListActivity.this));
                return params;
            }
        };
        changeAccessed.setIsShowDialog(true);
        Dzxstartlist = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("page", page_left + "");
                params.put("project_id", project_id);
                params.put("city", city);
                params.put("user_mobile", AppInfo.getName(BlackDZXListActivity.this));
                if (!TextUtils.isEmpty(searchStr)) {
                    params.put("keyword", searchStr);
                }
                return params;
            }
        };
        checkinvalid = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(BlackDZXListActivity.this));
                params.put("token", Tools.getToken());
                params.put("outletid", storeid);
                params.put("lon", longitude + "");
                params.put("lat", latitude + "");
                params.put("address", address);
                return params;
            }
        };
        Zxwcstartlist = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("page", page_middle + "");
                params.put("project_id", project_id);
                params.put("user_mobile", AppInfo.getName(BlackDZXListActivity.this));
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
                params.put("user_mobile", AppInfo.getName(BlackDZXListActivity.this));
                if (!TextUtils.isEmpty(searchStr)) {
                    params.put("keyword", searchStr);
                }
                return params;
            }
        };
    }

    protected void onStop() {
        super.onStop();
        if (getTeamData != null) {
            getTeamData.stop(Urls.Myteam);
        }
        if (changeAccessed != null) {
            changeAccessed.stop(Urls.Changeaccessed);
        }
        if (Dzxstartlist != null) {
            Dzxstartlist.stop(Urls.Blackdzxstartlist);
        }
        if (checkinvalid != null) {
            checkinvalid.stop(Urls.CheckInvalid);
        }
    }

    private PullToRefreshListView blackdzx_listview_left, blackdzx_listview_middle, blackdzx_listview_right;
    private AppTitle blackdzx_title;
    private TaskDistAdapter taskDistAdapter, taskDistAdapterMiddle, taskDistAdapterRight;
    private ArrayList<TaskDetailLeftInfo> list_left, list_middle, list_right;
    private NetworkConnection Dzxstartlist, Zxwcstartlist, Zlyhsstartlist;
    private CharacterParser characterParser;
    private PinyinComparatorForMyteam pinyinComparatorForMyteam;
    private OfflineDBHelper offlineDBHelper;
    private String storeid, address;
    private double longitude, latitude;
    private LocationClient mLocationClient = null;
    private BDLocationListener myListener = new MyLocationListener();
    private int locType;
    private int page_left = 1, page_middle = 1, page_right = 1;
    private String project_id, projectname;
    public static boolean isRefresh;
    private String city;
    private String searchStr = "";
    private String photo_compression;
    private Intent data;
    private static boolean isShow; // 是否显示CheckBox标识
    private String storeidlist;
    private TextView blackdzx_tab_left, blackdzx_tab_middle, blackdzx_tab_right;
    private View blackdzx_tab_left_line, blackdzx_tab_middle_line, blackdzx_tab_right_line;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blackdzx);
        offlineDBHelper = new OfflineDBHelper(this);
        initTitle();
        initNetworkConnection();
        data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        initLocation();
        project_id = data.getStringExtra("id");
        projectname = data.getStringExtra("project_name");
        if (city != null) {
            city = data.getStringExtra("city");
        } else {
            city = "";
        }
        String standard_state = data.getStringExtra("standard_state");
        if ("1".equals(standard_state)) {
            findViewById(R.id.blackdzx_standard).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.blackdzx_standard).setVisibility(View.GONE);
        }
        photo_compression = data.getStringExtra("photo_compression");
        characterParser = CharacterParser.getInstance();
        pinyinComparatorForMyteam = new PinyinComparatorForMyteam();
        ((TextView) findViewById(R.id.blackdzx_name)).setText(projectname);
        ((TextView) findViewById(R.id.blackdzx_time)).setText("起止时间:" + data.getStringExtra("begin_date") + "~" + data.getStringExtra("end_date"));
        ((TextView) findViewById(R.id.blackdzx_period)).setText("审核周期:" + data.getStringExtra("check_time") + "天");
        ((TextView) findViewById(R.id.blackdzx_person)).setText("发布商家:【" + data.getStringExtra("project_person") + "】");
        blackdzx_tab_left = (TextView) findViewById(R.id.blackdzx_tab_left);
        blackdzx_tab_middle = (TextView) findViewById(R.id.blackdzx_tab_middle);
        blackdzx_tab_right = (TextView) findViewById(R.id.blackdzx_tab_right);
        blackdzx_tab_left_line = findViewById(R.id.blackdzx_tab_left_line);
        blackdzx_tab_middle_line = findViewById(R.id.blackdzx_tab_middle_line);
        blackdzx_tab_right_line = findViewById(R.id.blackdzx_tab_right_line);
        blackdzx_listview_left = (PullToRefreshListView) findViewById(R.id.blackdzx_listview_left);
        blackdzx_listview_middle = (PullToRefreshListView) findViewById(R.id.blackdzx_listview_middle);
        blackdzx_listview_right = (PullToRefreshListView) findViewById(R.id.blackdzx_listview_right);
        findViewById(R.id.blackdzx_standard).setOnClickListener(this);
        list_left = new ArrayList<>();
        list_middle = new ArrayList<>();
        list_right = new ArrayList<>();
        taskDistAdapter = new TaskDistAdapter(this, list_left);
//        taskDistAdapter.setShowTime(true);
        blackdzx_listview_left.setAdapter(taskDistAdapter);
        taskDistAdapterMiddle = new TaskDistAdapter(this, list_middle);
        blackdzx_listview_middle.setAdapter(taskDistAdapterMiddle);
        taskDistAdapterRight = new TaskDistAdapter(this, list_right);
        blackdzx_listview_right.setAdapter(taskDistAdapterRight);
        taskDistAdapter.setOnShowItemClickListener(this);
        blackdzx_listview_left.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshLeft();
            }

            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page_left++;
                getDataForLeft();
            }
        });
        blackdzx_listview_middle.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
        blackdzx_listview_right.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
        onItemClickListener();
        onItemLongClickListener();
        isRefresh = false;
        blackdzx_tab_left.setOnClickListener(this);
        blackdzx_tab_middle.setOnClickListener(this);
        blackdzx_tab_right.setOnClickListener(this);
        onClick(blackdzx_tab_left);
    }

    private void onItemLongClickListener() {
        blackdzx_listview_left.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TaskDetailLeftInfo taskDetailLeftInf = list_left.get(position - 1);
                String fynum = taskDetailLeftInf.getNumber();
                if (TextUtils.isEmpty(fynum) || "null".equals(fynum) || !fynum.equals(AppInfo.getName(BlackDZXListActivity.this))) {
                    if (isShow) {
                        Tools.d("++++" + isShow);
                        return false;
                    } else {
                        isShow = true;
                        for (TaskDetailLeftInfo taskDetailLeftInfo : list_left) {
                            taskDetailLeftInfo.setShow(true);
                        }
                        taskDistAdapter.notifyDataSetChanged();
                        blackdzx_title.settingExit("分配", new AppTitle.OnExitClickForAppTitle() {
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
            taskDistAdapter.notifyDataSetChanged();
            isShow = false;
            blackdzx_listview_left.setLongClickable(true);
            blackdzx_title.hideExit();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isShow) {
            for (TaskDetailLeftInfo taskDetailLeftInfo : list_left) {
                taskDetailLeftInfo.setChecked(false);
                taskDetailLeftInfo.setShow(false);
            }
            taskDistAdapter.notifyDataSetChanged();
            isShow = false;
            blackdzx_listview_left.setLongClickable(true);
            blackdzx_title.hideExit();
        }
    }

    private void onItemClickListener() {
        blackdzx_listview_left.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final TaskDetailLeftInfo taskDetailLeftInfo = list_left.get(position - 1);
                final String fynum = taskDetailLeftInfo.getNumber();
                if (!TextUtils.isEmpty(fynum) && !"null".equals(fynum) && fynum.equals(AppInfo.getName(BlackDZXListActivity.this))) {
                    if (isShow) {
                        boolean isChecked = taskDetailLeftInfo.isChecked();
                        if (isChecked) {
                            taskDetailLeftInfo.setChecked(false);
                        } else {
                            taskDetailLeftInfo.setChecked(true);
                        }
                        taskDistAdapter.notifyDataSetChanged();
                    } else {
                        storeid = taskDetailLeftInfo.getId();
                        if (locType == 61 || locType == 161) {
                            checkinvalid.sendPostRequest(Urls.CheckInvalid, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String s) {
                                    Tools.d(s);
                                    try {
                                        JSONObject jsonObject = new JSONObject(s);
                                        int code = jsonObject.getInt("code");
                                        if (code == 200) {
                                            checkInvalid(taskDetailLeftInfo);
                                        } else if (code == 2) {
                                            ConfirmDialog.showDialog(BlackDZXListActivity.this, null, jsonObject.getString("msg"), null,
                                                    "确定", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                                        @Override
                                                        public void leftClick(Object object) {

                                                        }

                                                        @Override
                                                        public void rightClick(Object object) {
                                                        }
                                                    }).goneLeft();
                                        } else if (code == 3) {
                                            ConfirmDialog.showDialog(BlackDZXListActivity.this, null, jsonObject.getString("msg"), "取消",
                                                    "继续执行", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                                                        @Override
                                                        public void leftClick(Object object) {

                                                        }

                                                        @Override
                                                        public void rightClick(Object object) {
                                                            checkInvalid(taskDetailLeftInfo);
                                                        }
                                                    });
                                        } else {
                                            Tools.showToast(BlackDZXListActivity.this, jsonObject.getString("msg"));
                                        }
                                    } catch (JSONException e) {
                                        Tools.showToast(BlackDZXListActivity.this, getResources().getString(R.string.network_error));
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    Tools.showToast(BlackDZXListActivity.this, getResources().getString(R.string
                                            .network_volleyerror));
                                }
                            }, null);
                        } else if (locType == 167) {
                            Tools.showToast2(BlackDZXListActivity.this, "请您检查是否开启权限，尝试重新请求定位");
                        } else {
                            Tools.showToast2(BlackDZXListActivity.this, "请您检查网络是否通畅或是否允许访问位置信息,尝试重新请求定位");
                        }
                    }
                } else {
                    Tools.showToast2(BlackDZXListActivity.this, "长按网点分配给自己后方可执行");
                }
            }
        });
        blackdzx_listview_middle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tools.showToast(BlackDZXListActivity.this, "此项目类型不支持查看详情");
            }
        });
        blackdzx_listview_right.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tools.showToast(BlackDZXListActivity.this, "此项目类型不支持查看详情");
            }
        });
    }

    private void checkInvalid(TaskDetailLeftInfo taskDetailLeftInfo) {
        Intent intent = new Intent(BlackDZXListActivity.this, BlackillustrateActivity.class);
        intent.putExtra("project_id", project_id);
        intent.putExtra("project_name", projectname);
        intent.putExtra("store_id", taskDetailLeftInfo.getId());
        intent.putExtra("store_name", taskDetailLeftInfo.getName());
        intent.putExtra("store_num", taskDetailLeftInfo.getCode());
        intent.putExtra("photo_compression", photo_compression);
        intent.putExtra("isUpdata", taskDetailLeftInfo.getIsUpdata());
        intent.putExtra("province", taskDetailLeftInfo.getCity());
        intent.putExtra("city", taskDetailLeftInfo.getCity2());
        intent.putExtra("address", taskDetailLeftInfo.getCity3());
        intent.putExtra("isNormal", true);
        startActivity(intent);
    }

    protected void onResume() {
        super.onResume();
        if (isRefresh) {
            isRefresh = false;
            refreshLeft();
        }
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
                        TaskChangeDialog.showDialog(BlackDZXListActivity.this, listForTeam, BlackDZXListActivity.this);
                    } else {
                        Tools.showToast(BlackDZXListActivity.this, jsonObject.getString("msg"));
                        TaskChangeDialog.showDialog(BlackDZXListActivity.this, listForTeam, BlackDZXListActivity.this);
                    }
                } catch (JSONException e) {
                    Tools.showToast(BlackDZXListActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(BlackDZXListActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    /**
     * 换人接口
     */
    private void changeAccessed() {
        changeAccessed.sendPostRequest(Urls.Changeaccessed, new Response.Listener<String>() {
            public void onResponse(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        isShow = false;
                        refreshLeft();
                        blackdzx_title.hideExit();
                    } else {
                        Tools.showToast(BlackDZXListActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(BlackDZXListActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(BlackDZXListActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    private String accessednum;

    /**
     * 换人
     *
     * @param myteamNewfdInfo
     */
    public void onItemClick(MyteamNewfdInfo myteamNewfdInfo) {
        isShow = false;
        accessednum = myteamNewfdInfo.getId();
        new ClearCache().executeOnExecutor(Executors.newCachedThreadPool());
    }

    /**
     * 换成自己
     */
    public void oneself() {
        accessednum = AppInfo.getName(this);
        changeAccessed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.blackdzx_standard: {
                Intent intent = new Intent(this, TaskillustratesActivity.class);
                intent.putExtra("projectid", project_id);
                intent.putExtra("projectname", projectname);
                intent.putExtra("isShow", "0");//是否显示不再显示复选框
                startActivity(intent);
            }
            break;
            case R.id.blackdzx_tab_left: {
                blackdzx_tab_left.setTextColor(getResources().getColor(R.color.changetext));
                blackdzx_tab_left_line.setVisibility(View.VISIBLE);
                blackdzx_tab_middle.setTextColor(getResources().getColor(R.color.myreward_two));
                blackdzx_tab_middle_line.setVisibility(View.INVISIBLE);
                blackdzx_tab_right.setTextColor(getResources().getColor(R.color.myreward_two));
                blackdzx_tab_right_line.setVisibility(View.INVISIBLE);
                blackdzx_listview_left.setVisibility(View.VISIBLE);
                blackdzx_listview_middle.setVisibility(View.GONE);
                blackdzx_listview_right.setVisibility(View.GONE);
                if (list_left == null || list_left.isEmpty()) {
                    refreshLeft();
                }
            }
            break;
            case R.id.blackdzx_tab_middle: {
                blackdzx_tab_left.setTextColor(getResources().getColor(R.color.myreward_two));
                blackdzx_tab_left_line.setVisibility(View.INVISIBLE);
                blackdzx_tab_middle.setTextColor(getResources().getColor(R.color.changetext));
                blackdzx_tab_middle_line.setVisibility(View.VISIBLE);
                blackdzx_tab_right.setTextColor(getResources().getColor(R.color.myreward_two));
                blackdzx_tab_right_line.setVisibility(View.INVISIBLE);
                blackdzx_listview_left.setVisibility(View.GONE);
                blackdzx_listview_middle.setVisibility(View.VISIBLE);
                blackdzx_listview_right.setVisibility(View.GONE);
                if (list_middle == null || list_middle.isEmpty()) {
                    refreshMiddle();
                }
            }
            break;
            case R.id.blackdzx_tab_right: {
                blackdzx_tab_left.setTextColor(getResources().getColor(R.color.myreward_two));
                blackdzx_tab_left_line.setVisibility(View.INVISIBLE);
                blackdzx_tab_middle.setTextColor(getResources().getColor(R.color.myreward_two));
                blackdzx_tab_middle_line.setVisibility(View.INVISIBLE);
                blackdzx_tab_right.setTextColor(getResources().getColor(R.color.changetext));
                blackdzx_tab_right_line.setVisibility(View.VISIBLE);
                blackdzx_listview_left.setVisibility(View.GONE);
                blackdzx_listview_middle.setVisibility(View.GONE);
                blackdzx_listview_right.setVisibility(View.VISIBLE);
                if (list_right == null || list_right.isEmpty()) {
                    refreshRight();
                }
            }
            break;
        }
    }

    class ClearCache extends AsyncTask {
        protected void onPreExecute() {
            if (offlineDBHelper == null) {
                offlineDBHelper = new OfflineDBHelper(BlackDZXListActivity.this);
            }
            CustomProgressDialog.showProgressDialog(BlackDZXListActivity.this, "清理缓存");
        }


        protected Object doInBackground(Object[] params) {
            offlineDBHelper.clearCache();
            recurDelete(new File(FileCache.getCacheDir(BlackDZXListActivity.this).getPath() + "/download"));
            AppInfo.clearCachesize(BlackDZXListActivity.this);
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

    private void refreshLeft() {
        page_left = 1;
        getDataForLeft();
    }

    private void refreshRight() {
        page_right = 1;
        getDataForRight();
    }

    private void refreshMiddle() {
        page_middle = 1;
        getDataForMiddle();
    }

    private void getDataForLeft() {
        Dzxstartlist.sendPostRequest(Urls.Blackdzxstartlist, new Response.Listener<String>() {
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
                            taskDetailLeftInfo.setIsUpdata(jsonObject.getString("is_upload"));
                            taskDetailLeftInfo.setNickname(jsonObject.getString("accessed_name"));
                            taskDetailLeftInfo.setExe_time(jsonObject.getString("exe_time"));
                            taskDetailLeftInfo.setMoney(jsonObject.getString("money"));
                            taskDetailLeftInfo.setTimedetail(timeDetail);
                            taskDetailLeftInfo.setHavetime(jsonObject.getString("havetime"));
                            taskDetailLeftInfo.setMoney_unit(data.getStringExtra("money_unit"));
                            taskDetailLeftInfo.setIsUpdata(jsonObject.getString("is_upload"));
                            list_left.add(taskDetailLeftInfo);
                        }
                        blackdzx_listview_left.onRefreshComplete();
                        if (length < 15) {
                            blackdzx_listview_left.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            blackdzx_listview_left.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                        if (taskDistAdapter != null) {
                            taskDistAdapter.notifyDataSetChanged();
                        } else {
                            taskDistAdapter = new TaskDistAdapter(BlackDZXListActivity.this, list_left);
                            blackdzx_listview_left.setAdapter(taskDistAdapter);
                        }
                    } else {
                        Tools.showToast(BlackDZXListActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    MobclickAgent.reportError(BlackDZXListActivity.this,
                            "BlackDZXListActivity getData ParseJson:" + e.getMessage());
                    Tools.showToast(BlackDZXListActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
                blackdzx_listview_left.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                searchStr = "";
                Dzxstartlist.setIsShowDialog(false);
                blackdzx_listview_left.onRefreshComplete();
                Tools.showToast(BlackDZXListActivity.this, getResources().getString(R.string
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
                        blackdzx_listview_middle.onRefreshComplete();
                        if (length < 15) {
                            blackdzx_listview_middle.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            blackdzx_listview_middle.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                        taskDistAdapterMiddle.notifyDataSetChanged();
                    } else {
                        Tools.showToast(BlackDZXListActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(BlackDZXListActivity.this, getResources().getString(R.string.network_error));
                }
                blackdzx_listview_middle.onRefreshComplete();
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                searchStr = "";
                Zxwcstartlist.setIsShowDialog(false);
                blackdzx_listview_middle.onRefreshComplete();
                Tools.showToast(BlackDZXListActivity.this, getResources().getString(R.string
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
                        blackdzx_listview_right.onRefreshComplete();
                        if (length < 15) {
                            blackdzx_listview_right.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            blackdzx_listview_right.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                        taskDistAdapterRight.notifyDataSetChanged();
                    } else {
                        Tools.showToast(BlackDZXListActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(BlackDZXListActivity.this, getResources().getString(R.string.network_error));
                }
                blackdzx_listview_right.onRefreshComplete();
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                searchStr = "";
                Zlyhsstartlist.setIsShowDialog(false);
                blackdzx_listview_right.onRefreshComplete();
                Tools.showToast(BlackDZXListActivity.this, getResources().getString(R.string
                        .network_volleyerror));
            }
        });
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
                Tools.showToast(BlackDZXListActivity.this, getResources().getString(R.string.location_fail));
                return;
            }
            latitude = bdLocation.getLatitude();
            longitude = bdLocation.getLongitude();
            address = bdLocation.getAddrStr();
            locType = bdLocation.getLocType();
            Tools.d(bdLocation.getAddrStr());
        }

        public void onConnectHotSpotMessage(String s, int i) {

        }
    }
}
