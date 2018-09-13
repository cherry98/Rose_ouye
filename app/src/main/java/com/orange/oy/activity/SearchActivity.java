package com.orange.oy.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextPaint;
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
import android.widget.LinearLayout;
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
import com.orange.oy.R;
import com.orange.oy.activity.black.BlackDZXListActivity;
import com.orange.oy.activity.experience.ExperienceLocationActivity;
import com.orange.oy.activity.newtask.NoOutletsActivity;
import com.orange.oy.activity.newtask.ProjectRecruitmentActivity;
import com.orange.oy.activity.newtask.TaskDistActivity;
import com.orange.oy.activity.newtask.TaskGrabActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.AppDBHelper;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.TaskNewInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.NetworkView;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zhangpengfei on 2018/4/12.
 * 首页-搜索页面
 */
public class SearchActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {
    private LinearLayout search_hottab_layout;//热门搜索根布局
    private ArrayList<TaskNewInfo> historyList = new ArrayList<>();
    private ArrayList<SearchInfo> searchList = new ArrayList<>();
    private ListView searchListview;
    private MyHistoryAdapter myHistoryAdapter;
    private MySearchAdapter mySearchAdapter;
    private EditText search_main_edit;
    private ListView historyListView;
    private View search_main_layout;//热门搜索根布局
    private NetworkConnection SearchProject;
    private NetworkConnection HistoricalAndHotSerach;
    private NetworkConnection GetProjectInfo;
    private NetworkConnection checkapply;
    private AppDBHelper appDBHelper;
    private String searchKey = "";
    private String address = "";
    private String longitude = "";
    private String latitude = "";
    private String projectid = "";
    private NetworkView lin_Nodata;

    public void initTile() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.search_main_title);
        appTitle.settingName("搜索");
        appTitle.showBack(this);
    }

    private void initNetworkConnection() {
        SearchProject = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(SearchActivity.this));
                params.put("city", AppInfo.getCityName(SearchActivity.this));
                params.put("keyword", searchKey);
                return params;
            }
        };
        SearchProject.setIsShowDialog(false);
        HistoricalAndHotSerach = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(SearchActivity.this));
                return params;
            }
        };
        HistoricalAndHotSerach.setIsShowDialog(false);
        GetProjectInfo = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(SearchActivity.this));
                params.put("record", "1");
                params.put("projectid", projectid);
                return params;
            }
        };
        GetProjectInfo.setIsShowDialog(true);
        checkapply = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                if (!TextUtils.isEmpty(AppInfo.getKey(SearchActivity.this))) {
                    params.put("usermobile", AppInfo.getName(SearchActivity.this));
                    params.put("token", Tools.getToken());
                }
                params.put("projectid", projectid);
                return params;
            }
        };
        checkapply.setIsShowDialog(true);
    }

    protected void onStop() {
        super.onStop();
        if (SearchProject != null) {
            SearchProject.stop(Urls.SearchProject);
        }
        if (HistoricalAndHotSerach != null) {
            HistoricalAndHotSerach.stop(Urls.HistoricalAndHotSerach);
        }
        if (GetProjectInfo != null) {
            GetProjectInfo.stop(Urls.GetProjectInfo);
        }
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onDestroy() {
        super.onDestroy();
        if (mLocationClient != null)
            mLocationClient.stop();
        if (mSearch != null) {
            mSearch.destroy();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initNetworkConnection();
        initTile();
        search_main_layout = findViewById(R.id.search_main_layout);
        lin_Nodata = (NetworkView) findViewById(R.id.lin_Nodata);
        lin_Nodata.NoSearch();
        historyListView = (ListView) findViewById(R.id.search_history_listview);
        searchListview = (ListView) findViewById(R.id.search_main_listview);
        search_hottab_layout = (LinearLayout) findViewById(R.id.search_hottab_layout);
        search_main_edit = (EditText) findViewById(R.id.search_main_edit);
        myHistoryAdapter = new MyHistoryAdapter();
        historyListView.setAdapter(myHistoryAdapter);
        mySearchAdapter = new MySearchAdapter();
        searchListview.setAdapter(mySearchAdapter);
        search_main_edit.addTextChangedListener(textWatcher);
        appDBHelper = new AppDBHelper(this);
        historyListView.setOnItemClickListener(historyOnItemClickListener);
        searchListview.setOnItemClickListener(searchOnItemClickListener);
        search_main_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchKey = search_main_edit.getText().toString();
                    SearchProject.stop(Urls.SearchProject);
                    startSearch();
                    return true;
                }
                return false;
            }
        });
        initLocation();
    }

    protected void onResume() {
        super.onResume();
        getData();
    }

    public void onBackPressed() {
        if (searchListview.getVisibility() == View.VISIBLE || lin_Nodata.getVisibility() == View.VISIBLE) {
            search_main_edit.setText("");
            search_main_layout.setVisibility(View.VISIBLE);
            searchListview.setVisibility(View.GONE);
            lin_Nodata.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            if (TextUtils.isEmpty(s.toString())) {
                search_main_layout.setVisibility(View.VISIBLE);
                searchListview.setVisibility(View.GONE);
                lin_Nodata.setVisibility(View.GONE);
            } else {
                search_main_layout.setVisibility(View.GONE);
                searchListview.setVisibility(View.VISIBLE);
                lin_Nodata.setVisibility(View.GONE);
            }
            searchKey = s.toString();
            if (TextUtils.isEmpty(searchKey)) {
                search_main_layout.setVisibility(View.VISIBLE);
                searchListview.setVisibility(View.GONE);
                lin_Nodata.setVisibility(View.GONE);
            } else {
                SearchProject.stop(Urls.SearchProject);
                startSearch();
            }
        }
    };

    /**
     * 实时搜索
     */
    private void startSearch() {
        SearchProject.sendPostRequest(Urls.SearchProject, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject response = new JSONObject(s);
                    String code = response.getString("code");
                    String keyword = response.getString("keyword");
                    if ("200".equals(code)) {
                        if (searchKey.equals(keyword)) {
                            if (searchList != null) {
                                searchList.clear();
                            } else {
                                searchList = new ArrayList<SearchInfo>();
                            }
                            JSONArray datas = response.getJSONArray("datas");
                            int length = datas.length();
                            JSONObject tempJSON;
                            for (int i = 0; i < length; i++) {
                                tempJSON = datas.getJSONObject(i);
                                SearchInfo searchInfo = new SearchInfo();
                                searchInfo.id = tempJSON.getString("projectid");
                                searchInfo.name = tempJSON.getString("projectname");
                                searchList.add(searchInfo);
                            }
                            mySearchAdapter.notifyDataSetChanged();
                            search_main_layout.setVisibility(View.GONE);
                            if (searchList.size() == 0) {
                                searchListview.setVisibility(View.GONE);
                                lin_Nodata.setVisibility(View.VISIBLE);
                            } else {
                                searchListview.setVisibility(View.VISIBLE);
                                lin_Nodata.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        Tools.showToast(SearchActivity.this, response.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(SearchActivity.this, getResources().getString(R.string.network_error));
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    private void getData() {
        HistoricalAndHotSerach.sendPostRequest(Urls.HistoricalAndHotSerach, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject response = new JSONObject(s);
                    String code = response.getString("code");
                    if ("200".equals(code)) {
                        int length;
                        JSONObject tempJson;
                        if (search_hottab_layout.getChildCount() < 2) {
                            JSONArray hotSearch = response.getJSONArray("hotList");//热门搜索
                            length = hotSearch.length();
                            HotTabInfo[] hotTabInfos = new HotTabInfo[length];
                            for (int i = 0; i < length; i++) {
                                tempJson = hotSearch.getJSONObject(i);
                                HotTabInfo hotTabInfo = new HotTabInfo();
                                hotTabInfo.id = tempJson.optString("id");
                                hotTabInfo.name = tempJson.optString("name");
                                hotTabInfo.creatTime = tempJson.optString("createTime");
                                hotTabInfos[i] = hotTabInfo;
                            }
                            autoAddTab(hotTabInfos);
                        }
                        if (historyList == null) {
                            historyList = new ArrayList<TaskNewInfo>();
                        } else {
                            historyList.clear();
                        }
                        JSONArray projectList = response.getJSONArray("projectList");
                        length = projectList.length();
                        for (int i = 0; i < length; i++) {
                            tempJson = projectList.getJSONObject(i);
                            TaskNewInfo taskNewInfo = new TaskNewInfo();
                            taskNewInfo.setId(tempJson.getString("id"));
                            taskNewInfo.setProject_name(tempJson.getString("project_name"));
                            taskNewInfo.setProject_code(tempJson.getString("project_code"));
                            taskNewInfo.setProject_type(tempJson.getString("project_type"));
                            taskNewInfo.setIs_record(Tools.StringToInt(tempJson.getString("is_record")));
                            taskNewInfo.setPhoto_compression(tempJson.getString("photo_compression"));
                            taskNewInfo.setBegin_date(tempJson.getString("begin_date"));
                            taskNewInfo.setEnd_date(tempJson.getString("end_date"));
                            taskNewInfo.setIs_download(Tools.StringToInt(tempJson.getString("is_download")));
                            taskNewInfo.setIs_watermark(Tools.StringToInt(tempJson.getString("is_watermark")));
                            taskNewInfo.setCode(tempJson.getString("code"));
                            taskNewInfo.setBrand(tempJson.getString("brand"));
                            taskNewInfo.setIs_takephoto(Tools.StringToInt(tempJson.getString("is_takephoto")));
                            taskNewInfo.setType(tempJson.getString("type"));
                            taskNewInfo.setShow_type(tempJson.getString("show_type"));
                            taskNewInfo.setCheck_time(tempJson.getString("check_time"));
                            taskNewInfo.setMin_reward(tempJson.getString("min_reward"));
                            taskNewInfo.setMax_reward(tempJson.getString("max_reward"));
                            taskNewInfo.setProject_property(tempJson.getString("project_property"));
                            taskNewInfo.setPublish_time(tempJson.optString("publish_time"));
                            taskNewInfo.setProject_person(tempJson.optString("project_person"));
                            taskNewInfo.setMoney_unit(tempJson.getString("money_unit"));
                            taskNewInfo.setCertification(tempJson.getString("certification"));
                            taskNewInfo.setStandard_state(tempJson.getString("standard_state"));
                            historyList.add(taskNewInfo);
                        }
                        myHistoryAdapter.notifyDataSetChanged();
                    } else {
                        Tools.showToast(SearchActivity.this, response.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Tools.showToast(SearchActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(SearchActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private void getProjectData() {
        GetProjectInfo.sendPostRequest(Urls.GetProjectInfo, new Response.Listener<String>() {
            public void onResponse(String s) {
                try {
                    JSONObject response = new JSONObject(s);
                    String code = response.getString("code");
                    if ("200".equals(code)) {
                        JSONObject tempJson = response.getJSONObject("datas");
                        TaskNewInfo taskNewInfo = new TaskNewInfo();
                        taskNewInfo.setId(tempJson.getString("id"));
                        taskNewInfo.setProject_name(tempJson.getString("project_name"));
                        taskNewInfo.setProject_code(tempJson.getString("project_code"));
                        taskNewInfo.setProject_type(tempJson.getString("project_type"));
                        taskNewInfo.setIs_record(Tools.StringToInt(tempJson.getString("is_record")));
                        taskNewInfo.setPhoto_compression(tempJson.getString("photo_compression"));
                        taskNewInfo.setBegin_date(tempJson.getString("begin_date"));
                        taskNewInfo.setEnd_date(tempJson.getString("end_date"));
                        taskNewInfo.setIs_download(Tools.StringToInt(tempJson.getString("is_download")));
                        taskNewInfo.setIs_watermark(Tools.StringToInt(tempJson.getString("is_watermark")));
                        taskNewInfo.setCode(tempJson.getString("code"));
                        taskNewInfo.setBrand(tempJson.getString("brand"));
                        taskNewInfo.setIs_takephoto(Tools.StringToInt(tempJson.getString("is_takephoto")));
                        taskNewInfo.setType(tempJson.getString("type"));
                        taskNewInfo.setShow_type(tempJson.getString("show_type"));
                        taskNewInfo.setCheck_time(tempJson.getString("check_time"));
                        taskNewInfo.setMin_reward(tempJson.getString("min_reward"));
                        taskNewInfo.setMax_reward(tempJson.getString("max_reward"));
                        taskNewInfo.setProject_property(tempJson.getString("project_property"));
                        taskNewInfo.setPublish_time(tempJson.optString("publish_time"));
                        taskNewInfo.setProject_person(tempJson.optString("project_person"));
                        taskNewInfo.setMoney_unit(tempJson.getString("money_unit"));
                        taskNewInfo.setCertification(tempJson.getString("certification"));
                        taskNewInfo.setStandard_state(tempJson.getString("standard_state"));
                        CustomProgressDialog.Dissmiss();
                        clickProject(taskNewInfo);
                    } else {
                        Tools.showToast(SearchActivity.this, response.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    CustomProgressDialog.Dissmiss();
                    Tools.showToast(SearchActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(SearchActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private View.OnClickListener hotTabOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            search_main_edit.setText(((HotTabInfo) v.getTag()).name);
        }
    };

    private AdapterView.OnItemClickListener searchOnItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SearchInfo searchInfo = searchList.get(position);
            projectid = searchInfo.id;
            getProjectData();
        }
    };

    private AdapterView.OnItemClickListener historyOnItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TaskNewInfo taskNewInfo = historyList.get(position);
            clickProject(taskNewInfo);
        }
    };

    /**
     * 项目点击
     *
     * @param taskNewInfo
     */
    private void clickProject(final TaskNewInfo taskNewInfo) {
        search_main_edit.setText("");
        search_main_layout.setVisibility(View.VISIBLE);
        searchListview.setVisibility(View.GONE);
        lin_Nodata.setVisibility(View.GONE);
        projectid = taskNewInfo.getId();
        if (taskNewInfo.getProject_property().equals("2")) {//众包
            final String type = taskNewInfo.getType();
            if ("5".equals(type)) {//无店单的项目
                Intent intent = new Intent(SearchActivity.this, NoOutletsActivity.class);
                intent.putExtra("project_person", taskNewInfo.getProject_person());
                intent.putExtra("projectname", taskNewInfo.getProject_name());
                intent.putExtra("projectid", taskNewInfo.getId());
                intent.putExtra("id", taskNewInfo.getId());
                intent.putExtra("project_name", taskNewInfo.getProject_name());
                intent.putExtra("project_code", taskNewInfo.getProject_code());
                intent.putExtra("project_type", taskNewInfo.getProject_type());
                intent.putExtra("is_record", taskNewInfo.getIs_record() + "");
                intent.putExtra("photo_compression", taskNewInfo.getPhoto_compression());
                intent.putExtra("begin_date", taskNewInfo.getBegin_date());
                intent.putExtra("end_date", taskNewInfo.getEnd_date());
                intent.putExtra("is_download", taskNewInfo.getIs_download() + "");
                intent.putExtra("is_watermark", taskNewInfo.getIs_watermark() + "");
                intent.putExtra("code", taskNewInfo.getCode());
                intent.putExtra("brand", taskNewInfo.getBrand());
                intent.putExtra("is_takephoto", taskNewInfo.getIs_takephoto() + "");
                intent.putExtra("show_type", taskNewInfo.getShow_type());
                intent.putExtra("check_time", taskNewInfo.getCheck_time());
                intent.putExtra("project_property", taskNewInfo.getProject_property());
                intent.putExtra("city", AppInfo.getCityName(SearchActivity.this));
                intent.putExtra("type", taskNewInfo.getType());
                intent.putExtra("money_unit", taskNewInfo.getMoney_unit());
                intent.putExtra("standard_state", taskNewInfo.getStandard_state());
                intent.putExtra("longitude", longitude);
                intent.putExtra("latitude", latitude);
                intent.putExtra("address", address);
                startActivity(intent);
            } else {
                if (appDBHelper.getIsShow(taskNewInfo.getId()) && "1".equals(taskNewInfo.getStandard_state())) {
                    Intent intent = new Intent(SearchActivity.this, TaskillustratesActivity.class);
                    intent.putExtra("project_person", taskNewInfo.getProject_person());
                    intent.putExtra("projectname", taskNewInfo.getProject_name());
                    intent.putExtra("projectid", taskNewInfo.getId());
                    intent.putExtra("id", taskNewInfo.getId());
                    intent.putExtra("project_name", taskNewInfo.getProject_name());
                    intent.putExtra("project_code", taskNewInfo.getProject_code());
                    intent.putExtra("project_type", taskNewInfo.getProject_type());
                    intent.putExtra("is_record", taskNewInfo.getIs_record() + "");
                    intent.putExtra("photo_compression", taskNewInfo.getPhoto_compression());
                    intent.putExtra("begin_date", taskNewInfo.getBegin_date());
                    intent.putExtra("end_date", taskNewInfo.getEnd_date());
                    intent.putExtra("is_download", taskNewInfo.getIs_download() + "");
                    intent.putExtra("is_watermark", taskNewInfo.getIs_watermark() + "");
                    intent.putExtra("code", taskNewInfo.getCode());
                    intent.putExtra("brand", taskNewInfo.getBrand());
                    intent.putExtra("is_takephoto", taskNewInfo.getIs_takephoto() + "");
                    intent.putExtra("show_type", taskNewInfo.getShow_type());
                    intent.putExtra("check_time", taskNewInfo.getCheck_time());
                    intent.putExtra("project_property", taskNewInfo.getProject_property());
                    intent.putExtra("city", AppInfo.getCityName(SearchActivity.this));
                    intent.putExtra("type", taskNewInfo.getType());
                    intent.putExtra("money_unit", taskNewInfo.getMoney_unit());
                    intent.putExtra("standard_state", taskNewInfo.getStandard_state());
                    intent.putExtra("longitude", longitude);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("address", address);
                    intent.putExtra("province", getIntent().getStringExtra("province"));
                    intent.putExtra("isHomePage", "1");//是否是首页传过来的 1首页 0我的任务列表 2地图
                    startActivity(intent);
                } else {
                    checkapply.sendPostRequest(Urls.CheckApply, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                if (jsonObject.getInt("code") == 200) {
                                    if ("1".equals(type)) {
                                        Intent intent = new Intent(SearchActivity.this, TaskGrabActivity.class);
                                        intent.putExtra("id", taskNewInfo.getId());
                                        intent.putExtra("project_person", taskNewInfo.getProject_person());
                                        intent.putExtra("project_name", taskNewInfo.getProject_name());
                                        intent.putExtra("project_code", taskNewInfo.getProject_code());
                                        intent.putExtra("project_type", taskNewInfo.getProject_type());
                                        intent.putExtra("is_record", taskNewInfo.getIs_record() + "");
                                        intent.putExtra("photo_compression", taskNewInfo.getPhoto_compression());
                                        intent.putExtra("begin_date", taskNewInfo.getBegin_date());
                                        intent.putExtra("end_date", taskNewInfo.getEnd_date());
                                        intent.putExtra("is_download", taskNewInfo.getIs_download() + "");
                                        intent.putExtra("is_watermark", taskNewInfo.getIs_watermark() + "");
                                        intent.putExtra("code", taskNewInfo.getCode());
                                        intent.putExtra("brand", taskNewInfo.getBrand());
                                        intent.putExtra("is_takephoto", taskNewInfo.getIs_takephoto() + "");
                                        intent.putExtra("type", taskNewInfo.getType());
                                        intent.putExtra("show_type", taskNewInfo.getShow_type());
                                        intent.putExtra("check_time", taskNewInfo.getCheck_time());
                                        intent.putExtra("project_property", taskNewInfo.getProject_property());
                                        intent.putExtra("min_reward", taskNewInfo.getMin_reward() + "");
                                        intent.putExtra("max_reward", taskNewInfo.getMax_reward() + "");
                                        intent.putExtra("city", AppInfo.getCityName(SearchActivity.this));
                                        intent.putExtra("money_unit", taskNewInfo.getMoney_unit());
                                        intent.putExtra("standard_state", taskNewInfo.getStandard_state());
                                        intent.putExtra("province", getIntent().getStringExtra("province"));
                                        intent.putExtra("type1", "0");//首页跳转
                                        startActivity(intent);
                                    } else if ("4".equals(type)) {
                                        Intent intent = new Intent(SearchActivity.this, ExperienceLocationActivity.class);
                                        intent.putExtra("id", taskNewInfo.getId());
                                        intent.putExtra("project_person", taskNewInfo.getProject_person());
                                        intent.putExtra("project_name", taskNewInfo.getProject_name());
                                        intent.putExtra("project_code", taskNewInfo.getProject_code());
                                        intent.putExtra("project_type", taskNewInfo.getProject_type());
                                        intent.putExtra("is_record", taskNewInfo.getIs_record() + "");
                                        intent.putExtra("photo_compression", taskNewInfo.getPhoto_compression());
                                        intent.putExtra("begin_date", taskNewInfo.getBegin_date());
                                        intent.putExtra("end_date", taskNewInfo.getEnd_date());
                                        intent.putExtra("is_download", taskNewInfo.getIs_download() + "");
                                        intent.putExtra("is_watermark", taskNewInfo.getIs_watermark() + "");
                                        intent.putExtra("code", taskNewInfo.getCode());
                                        intent.putExtra("brand", taskNewInfo.getBrand());
                                        intent.putExtra("is_takephoto", taskNewInfo.getIs_takephoto() + "");
                                        intent.putExtra("type", taskNewInfo.getType());
                                        intent.putExtra("show_type", taskNewInfo.getShow_type());
                                        intent.putExtra("check_time", taskNewInfo.getCheck_time());
                                        intent.putExtra("project_property", taskNewInfo.getProject_property());
                                        intent.putExtra("min_reward", taskNewInfo.getMin_reward() + "");
                                        intent.putExtra("max_reward", taskNewInfo.getMax_reward() + "");
                                        intent.putExtra("city", AppInfo.getCityName(SearchActivity.this));
                                        intent.putExtra("money_unit", taskNewInfo.getMoney_unit());
                                        intent.putExtra("standard_state", taskNewInfo.getStandard_state());
                                        startActivity(intent);
                                    }
                                } else if (jsonObject.getInt("code") == 2) {//点击进入招募令
                                    Intent intent = new Intent(SearchActivity.this, ProjectRecruitmentActivity.class);
                                    intent.putExtra("projectid", taskNewInfo.getId());
                                    startActivity(intent);
                                } else {
                                    Tools.showToast(SearchActivity.this, jsonObject.getString("msg"));
                                }
                            } catch (JSONException e) {
                                Tools.showToast(SearchActivity.this, getResources().getString(R.string.network_error));
                            }
                            CustomProgressDialog.Dissmiss();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            CustomProgressDialog.Dissmiss();
                            Tools.showToast(SearchActivity.this, getResources().getString(R.string.network_error));
                        }
                    }, null);
                }
            }
        } else if (taskNewInfo.getProject_property().equals("3")) {//演练---暂时不用未做修改
            String type = taskNewInfo.getType();
            if (type.equals("1")) {//正常任务
                Intent intent = new Intent(SearchActivity.this, TaskDistActivity.class);
                intent.putExtra("id", taskNewInfo.getId());
                intent.putExtra("project_person", taskNewInfo.getProject_person());
                intent.putExtra("project_name", taskNewInfo.getProject_name());
                intent.putExtra("project_code", taskNewInfo.getProject_code());
                intent.putExtra("project_type", taskNewInfo.getProject_type());
                intent.putExtra("is_record", taskNewInfo.getIs_record() + "");
                intent.putExtra("photo_compression", taskNewInfo.getPhoto_compression());
                intent.putExtra("begin_date", taskNewInfo.getBegin_date());
                intent.putExtra("end_date", taskNewInfo.getEnd_date());
                intent.putExtra("is_download", taskNewInfo.getIs_download() + "");
                intent.putExtra("is_watermark", taskNewInfo.getIs_watermark() + "");
                intent.putExtra("code", taskNewInfo.getCode());
                intent.putExtra("brand", taskNewInfo.getBrand());
                intent.putExtra("is_takephoto", taskNewInfo.getIs_takephoto() + "");
                intent.putExtra("type", taskNewInfo.getType() + "");
                intent.putExtra("show_type", taskNewInfo.getShow_type());
                intent.putExtra("check_time", taskNewInfo.getCheck_time());
                intent.putExtra("project_property", taskNewInfo.getProject_property());
                intent.putExtra("city", AppInfo.getCityName(SearchActivity.this));
                intent.putExtra("type", type);
                intent.putExtra("money_unit", taskNewInfo.getMoney_unit());
                startActivity(intent);
            } else if (type.equals("2")) {// 暗访任务
                Intent intent = new Intent(SearchActivity.this, BlackDZXListActivity.class);
                intent.putExtra("id", taskNewInfo.getId());
                intent.putExtra("project_person", taskNewInfo.getProject_person());
                intent.putExtra("project_name", taskNewInfo.getProject_name());
                intent.putExtra("project_code", taskNewInfo.getProject_code());
                intent.putExtra("project_type", taskNewInfo.getProject_type());
                intent.putExtra("is_record", taskNewInfo.getIs_record() + "");
                intent.putExtra("photo_compression", taskNewInfo.getPhoto_compression());
                intent.putExtra("begin_date", taskNewInfo.getBegin_date());
                intent.putExtra("end_date", taskNewInfo.getEnd_date());
                intent.putExtra("is_download", taskNewInfo.getIs_download() + "");
                intent.putExtra("is_watermark", taskNewInfo.getIs_watermark() + "");
                intent.putExtra("code", taskNewInfo.getCode());
                intent.putExtra("brand", taskNewInfo.getBrand());
                intent.putExtra("is_takephoto", taskNewInfo.getIs_takephoto() + "");
                intent.putExtra("type", taskNewInfo.getType() + "");
                intent.putExtra("show_type", taskNewInfo.getShow_type());
                intent.putExtra("check_time", taskNewInfo.getCheck_time());
                intent.putExtra("project_property", taskNewInfo.getProject_property());
                intent.putExtra("city", AppInfo.getCityName(SearchActivity.this));
                intent.putExtra("money_unit", taskNewInfo.getMoney_unit());
                intent.putExtra("type", type);
                startActivity(intent);
            } else if (type.equals("3")) {//明访任务
                Intent intent = new Intent(SearchActivity.this, TaskDistActivity.class);
                intent.putExtra("id", taskNewInfo.getId());
                intent.putExtra("project_name", taskNewInfo.getProject_name());
                intent.putExtra("project_code", taskNewInfo.getProject_code());
                intent.putExtra("project_type", taskNewInfo.getProject_type());
                intent.putExtra("is_record", taskNewInfo.getIs_record() + "");
                intent.putExtra("photo_compression", taskNewInfo.getPhoto_compression());
                intent.putExtra("begin_date", taskNewInfo.getBegin_date());
                intent.putExtra("end_date", taskNewInfo.getEnd_date());
                intent.putExtra("is_download", taskNewInfo.getIs_download() + "");
                intent.putExtra("is_watermark", taskNewInfo.getIs_watermark() + "");
                intent.putExtra("code", taskNewInfo.getCode());
                intent.putExtra("brand", taskNewInfo.getBrand());
                intent.putExtra("is_takephoto", taskNewInfo.getIs_takephoto() + "");
                intent.putExtra("type", taskNewInfo.getType() + "");
                intent.putExtra("show_type", taskNewInfo.getShow_type());
                intent.putExtra("check_time", taskNewInfo.getCheck_time());
                intent.putExtra("project_property", taskNewInfo.getProject_property());
                intent.putExtra("city", AppInfo.getCityName(SearchActivity.this));
                intent.putExtra("type", type);
                intent.putExtra("money_unit", taskNewInfo.getMoney_unit());
                startActivity(intent);
            }
        }
    }

    private class HotTabInfo {
        String name, id, creatTime;
    }

    private class SearchInfo {
        String id, name;
    }

    /**
     * historyListview用
     */
    private class MyHistoryAdapter extends BaseAdapter {

        public int getCount() {
            return historyList.size();
        }

        public Object getItem(int position) {
            return historyList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHold viewHold;
            if (convertView == null) {
                convertView = Tools.loadLayout(SearchActivity.this, R.layout.item_search);
                viewHold = new ViewHold();
                viewHold.title = (TextView) convertView.findViewById(R.id.itemsearch_title);
                viewHold.time = (TextView) convertView.findViewById(R.id.itemsearch_time);
                viewHold.button = (TextView) convertView.findViewById(R.id.itemsearch_button);
                viewHold.money = (TextView) convertView.findViewById(R.id.itemsearch_money);
                viewHold.value = (TextView) convertView.findViewById(R.id.itemsearch_value);
                convertView.setTag(viewHold);
            } else {
                viewHold = (ViewHold) convertView.getTag();
            }
            if (!historyList.isEmpty()) {
                TaskNewInfo taskNewInfo = historyList.get(position);
                if ("null".equals(taskNewInfo.getPublish_time()) || TextUtils.isEmpty(taskNewInfo.getPublish_time())) {
                    viewHold.time.setVisibility(View.GONE);
                } else {
                    viewHold.time.setVisibility(View.VISIBLE);
                    viewHold.time.setText(taskNewInfo.getPublish_time());
                }
                viewHold.title.setText(taskNewInfo.getProject_name());
                viewHold.money.setText("¥" + taskNewInfo.getMin_reward() + "-" + taskNewInfo.getMax_reward());
                viewHold.value.setText("商家：" + taskNewInfo.getProject_person());
            }
            return convertView;
        }

        public void notifyDataSetChanged() {
            int height = (int) (getResources().getDimension(R.dimen.searchhistory_item_height) + getResources().getDimension(R.dimen.searchhistory_item_marbottome));
            height = height * getCount();
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) historyListView.getLayoutParams();
            lp.height = height;
            historyListView.setLayoutParams(lp);
            super.notifyDataSetChanged();
        }
    }

    private class ViewHold {
        TextView title, value, time, money, button;
    }

    private class MySearchAdapter extends BaseAdapter {
        public int getCount() {
            return searchList.size();
        }

        public Object getItem(int position) {
            return searchList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            if (convertView == null) {
                textView = new TextView(SearchActivity.this);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                textView.setTextSize(13);
                textView.setTextColor(Color.parseColor("#FFA0A0A0"));
                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.height = Tools.dipToPx(SearchActivity.this, 40);
                textView.setLayoutParams(lp);
            } else {
                textView = (TextView) convertView;
            }
            if (!searchList.isEmpty()) {
                SearchInfo searchInfo = searchList.get(position);
                textView.setText(searchInfo.name);
            }
            return textView;
        }

        public void notifyDataSetChanged() {
            searchListview.setSelection(0);
            super.notifyDataSetChanged();
        }
    }

    /**
     * 生成标签
     */
    private void autoAddTab(HotTabInfo[] tabInfos) {
        final int mar = (int) getResources().getDimension(R.dimen.searchhot_bg_marginLeftRight) + 1;//最外层左右边距
        final int tabMar = (int) getResources().getDimension(R.dimen.searchhot_tab_marginLeftRight) + 1;//标签外的右边距
        final int tabHeight = (int) getResources().getDimension(R.dimen.searchhot_tab_height);//标签高度
        final int tabTextmarg = (int) getResources().getDimension(R.dimen.searchhot_tab_text_margLeftRight) + 1;//标签内边距
        final int windowWidth = Tools.getScreeInfoWidth(this) - mar * 2;
        int layoutwidth = 0;
        LinearLayout.LayoutParams tabParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tabParams.topMargin = (int) getResources().getDimension(R.dimen.searchhot_tab_marginLeftRight) + 1;//标签上边距
        LinearLayout tempLayout = new LinearLayout(this);
        tempLayout.setOrientation(LinearLayout.HORIZONTAL);
        search_hottab_layout.addView(tempLayout, tabParams);
        boolean isAddMar;
        for (HotTabInfo temp : tabInfos) {
            int length = temp.name.length();
            //------start--------设置标签样式
            TextView tv = new TextView(this);
            tv.setTextSize(14);
            tv.setTextColor(Color.parseColor("#FF7C7C7C"));
            tv.setText(temp.name);
            tv.setGravity(Gravity.CENTER);
            tv.setBackgroundResource(R.drawable.search_hot_bg);
            tv.setTag(temp);
            tv.setOnClickListener(hotTabOnClickListener);
            //------end--------设置标签样式
            TextPaint paint = tv.getPaint();
            int minus = length - 4;
            int textWidth;
            if (minus <= 0) {
                textWidth = (int) (tv.getTextSize() * 6) + 1;
            } else {
                textWidth = (int) (paint.measureText(temp.name) + tabTextmarg * 2) + 1;
            }
            layoutwidth = layoutwidth + textWidth + tabMar;
            isAddMar = true;
            if (layoutwidth >= windowWidth) {
                layoutwidth = layoutwidth - tabMar;
                if (layoutwidth >= windowWidth) {
                    layoutwidth = textWidth + tabMar;
                    tempLayout = new LinearLayout(this);
                    tempLayout.setOrientation(LinearLayout.HORIZONTAL);
                    search_hottab_layout.addView(tempLayout, tabParams);
                } else {
                    isAddMar = false;
                }
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(textWidth, tabHeight);
            if (isAddMar) {
                params.rightMargin = tabMar;
            }
            tempLayout.addView(tv, params);
        }
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
            Tools.showToast(SearchActivity.this, "正在定位...");
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
        option.setScanSpan(3000);
        mLocationClient.setLocOption(option);
    }

    private class MyLocationListenner implements BDLocationListener {
        public void onReceiveLocation(BDLocation location) {
            mLocationClient.stop();
            if (location == null) {
                Tools.showToast(SearchActivity.this, getResources().getString(R.string.location_fail));
                return;
            }
            location_latitude = location.getLatitude();
            location_longitude = location.getLongitude();
            LatLng point = new LatLng(location_latitude, location_longitude);
            mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(point));
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
                longitude = reverseGeoCodeResult.getLocation().longitude + "";
                latitude = reverseGeoCodeResult.getLocation().latitude + "";
                address = reverseGeoCodeResult.getAddress();
            }
        }
    };
}
