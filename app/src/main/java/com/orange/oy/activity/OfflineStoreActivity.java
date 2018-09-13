package com.orange.oy.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.adapter.OfflineStoreRightAdapter;
import com.orange.oy.adapter.OfflinestoreAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.OfflineDBHelper;
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
import java.util.concurrent.Executors;

/**
 * 离线项目页
 */
public class OfflineStoreActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {
    public void onBack() {
        baseFinish();
    }

    protected void onStop() {
        super.onStop();
        if (Dzxstartlist != null) {
            Dzxstartlist.stop(Urls.Downloadlist);
        }
    }

    private void initNetworkConnection() {
        Dzxstartlist = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("page", page_left + "");
                params.put("project_id", projectid);
                params.put("city", city);
                params.put("user_mobile", AppInfo.getName(OfflineStoreActivity.this));
                if (!TextUtils.isEmpty(searchStr)) {
                    params.put("keyword", searchStr);
                }
                return params;
            }
        };
        Downloadtasklist = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("store_id", selStoreid);
                return params;
            }
        };
        Downloadtasklist.setIsShowDialog(true);
    }

    public void showSearch() {
        offlineproject_title.showSearch(new TextWatcher() {
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
                    if (offlineproject_listview.getVisibility() == View.VISIBLE) {
                        Dzxstartlist.setIsShowDialog(true);
                        refreshLeft();
                    } else if (offlineproject_listview2.getVisibility() == View.VISIBLE) {
                        CustomProgressDialog.showProgressDialog(OfflineStoreActivity.this, "正在加载...");
                        getRightData();
                    }
                    v.setText("");
                    return true;
                }
                return false;
            }
        });
    }

    private String selStoreid;
    private ArrayList<TaskDetailLeftInfo> list_right;
    private TextView offlineproject_tab_left, offlineproject_tab_right;
    private View offlineproject_tab_left_line, offlineproject_tab_right_line;
    private PullToRefreshListView offlineproject_listview, offlineproject_listview2;
    private OfflineDBHelper offlineDBHelper;
    private String projectid;
    private OfflineStoreRightAdapter offlineStoreRightAdapter;
    private NetworkConnection Dzxstartlist, Downloadtasklist;
    private int page_left;
    private String city;
    private ArrayList<TaskDetailLeftInfo> list_left = new ArrayList<>();
    private OfflinestoreAdapter taskscheduleDetailAdapterLeft;
    private AppTitle offlineproject_title;
    private String searchStr = "";
    private String is_taskphoto;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offlinestore);
        offlineDBHelper = new OfflineDBHelper(this);
        initNetworkConnection();
        offlineproject_title = (AppTitle) findViewById(R.id.offlineproject_title);
        showSearch();
        offlineproject_title.settingName("网点列表");
        offlineproject_title.showBack(this);
        Intent data = getIntent();
        projectid = data.getStringExtra("projectid");
        city = data.getStringExtra("city");
        is_taskphoto = data.getStringExtra("is_taskphoto");
        offlineproject_tab_left = (TextView) findViewById(R.id.offlineproject_tab_left);
        offlineproject_tab_right = (TextView) findViewById(R.id.offlineproject_tab_right);
        offlineproject_tab_left_line = findViewById(R.id.offlineproject_tab_left_line);
        offlineproject_tab_right_line = findViewById(R.id.offlineproject_tab_right_line);
        offlineproject_listview = (PullToRefreshListView) findViewById(R.id.offlineproject_listview);//可下载
        offlineproject_listview2 = (PullToRefreshListView) findViewById(R.id.offlineproject_listview2);//已下载
        initListview(offlineproject_listview);
        initListview(offlineproject_listview2);
        offlineproject_listview.setMode(PullToRefreshBase.Mode.DISABLED);
        list_right = new ArrayList<>();
        offlineStoreRightAdapter = new OfflineStoreRightAdapter(this, offlineproject_listview2, list_right);
        offlineproject_listview2.setCanDelete(true);
        offlineproject_listview2.setAdapter(offlineStoreRightAdapter);
        offlineproject_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (taskscheduleDetailAdapterLeft.getSelect() == 1) {
                    TaskDetailLeftInfo taskDetailLeftInfo = list_left.get(position - 1);
                    selStoreid = taskDetailLeftInfo.getId();
                    Downloadtasklist(taskDetailLeftInfo.getTimedetail());
                }
            }
        });
        offlineproject_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshLeft();
            }

            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page_left++;
                getDataForLeft();
            }
        });
        offlineproject_listview2.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getRightData();
            }

            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            }
        });
        taskscheduleDetailAdapterLeft = new OfflinestoreAdapter(this, list_left);
        offlineproject_listview.setAdapter(taskscheduleDetailAdapterLeft);
        offlineproject_tab_left.setOnClickListener(this);
        offlineproject_tab_right.setOnClickListener(this);
        isRefresh = false;
        refreshLeft();
    }

    public static boolean isRefresh = false;

    protected void onResume() {
        super.onResume();
        if (isRefresh) {
            isRefresh = false;
            getRightData();
        }
    }

    private String username = "";

    public void getRightData() {
        new getRightData().executeOnExecutor(Executors.newCachedThreadPool());
    }

    class getRightData extends AsyncTask {
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Object doInBackground(Object[] params) {
            username = AppInfo.getName(OfflineStoreActivity.this);
            list_right = offlineDBHelper.getStoreList(username, projectid, searchStr);
            offlineDBHelper.isCompletedForStore(username, projectid, list_right);
            offlineDBHelper.settingOutletNote(username, list_right);
            offlineStoreRightAdapter.resetList(list_right);
            return null;
        }

        protected void onPostExecute(Object o) {
            searchStr = "";
            offlineStoreRightAdapter.notifyDataSetChanged();
            offlineproject_listview2.onRefreshComplete();
            CustomProgressDialog.Dissmiss();
        }
    }

    /**
     * 刷新左侧列表
     */
    private void refreshLeft() {
        page_left = 1;
        getDataForLeft();
    }

    private void getDataForLeft() {
        Dzxstartlist.sendPostRequest(Urls.Downloadlist, new Response.Listener<String>() {
            public void onResponse(String s) {
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
                            list_left.add(taskDetailLeftInfo);
                        }
                        offlineDBHelper.isCompletedForOfflineStore(AppInfo.getName(OfflineStoreActivity.this), projectid,
                                list_left);
                        offlineproject_listview.onRefreshComplete();
                        if (list_left.isEmpty() && length >= 15) {//如果一页全部下载完并且这不是最后一页，自动翻页
                            page_left++;
                            getDataForLeft();
                        } else {
                            if (length < 15) {
                                offlineproject_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            } else {
                                offlineproject_listview.setMode(PullToRefreshBase.Mode.BOTH);
                            }
                            taskscheduleDetailAdapterLeft.notifyDataSetChanged();
                        }
                    } else {
                        Tools.showToast(OfflineStoreActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Tools.showToast(OfflineStoreActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
                offlineproject_listview.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                offlineproject_listview.onRefreshComplete();
                Tools.showToast(OfflineStoreActivity.this, getResources().getString(R.string
                        .network_volleyerror));
            }
        });
    }

    /**
     * 下载离线文件
     */
    private void Downloadtasklist(final String time) {
        Downloadtasklist.sendPostRequest(Urls.Downloadtasklist, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                CustomProgressDialog.Dissmiss();
                try {
                    AppInfo.addCachesize(OfflineStoreActivity.this, s.getBytes().length);
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        if ("1".equals(jsonObject.getString("is_exe"))) {
                            ConfirmDialog.showDialog(OfflineStoreActivity.this, "提示", "该网点已做过任务，下载离线再做会覆盖之前的任务，是否继续？", "否",
                                    "继续", jsonObject, true, new ConfirmDialog.OnSystemDialogClickListener() {
                                        public void leftClick(Object object) {
                                        }

                                        public void rightClick(Object object) {
                                            if (object != null) {
                                                try {
                                                    parseData(((JSONObject) object).getJSONArray("datas"),
                                                            ((JSONObject) object).getString("outletNote"), time);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    });
                        } else {
                            parseData(jsonObject.getJSONArray("datas"), jsonObject.getString("outletNote"), time);
                        }
                    } else if (code == 1) {
                        Tools.showToast(OfflineStoreActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Tools.showToast(OfflineStoreActivity.this, getResources().getString(R.string
                            .network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(OfflineStoreActivity.this, getResources().getString(R.string
                        .network_volleyerror));
            }
        }, "正在下载离线....");
    }

    private void parseData(JSONArray jsonArray, String outltenote, String time) throws JSONException {
        Tools.d("解析存储离线数据");
        if (offlineDBHelper == null) return;
        new parseDataAsyncTask(jsonArray, outltenote, time).executeOnExecutor(Executors.newCachedThreadPool());
    }

    class parseDataAsyncTask extends AsyncTask {
        JSONArray jsonArray;
        String time;
        String projectid;
        String storeid;
        String outltenote;

        parseDataAsyncTask(JSONArray jsonArray, String outltenote, String time) {
            this.jsonArray = jsonArray;
            this.time = time;
            this.outltenote = outltenote;
        }

        protected void onPreExecute() {
            CustomProgressDialog.showProgressDialog(OfflineStoreActivity.this, "正在离线数据");
        }


        protected Object doInBackground(Object[] params) {
            int length = jsonArray.length();
            boolean isCheck = false;
            String username = AppInfo.getName(OfflineStoreActivity.this);
            boolean issuccess = false;
            try {
                for (int i = 0; i < length; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (!isCheck) {
                        offlineDBHelper.checkClear(username, jsonObject.getString("project_id"), jsonObject.getString
                                ("store_id"));
                        isCheck = true;
                    }
                    projectid = jsonObject.getString("project_id");
                    storeid = jsonObject.getString("store_id");
                    String taskid = jsonObject.getString("task_id");
                    String packageid = jsonObject.getString("p_id");
                    String tasktype = jsonObject.getString("task_type");
                    String taskdetail = jsonObject.getString("task_detail");
                    issuccess = offlineDBHelper.insertOfflinedata(username, projectid,
                            jsonObject.getString("project_name"), jsonObject.getString("code"), jsonObject.getString("brand"),
                            storeid, jsonObject.getString("store_name"),
                            time, jsonObject.getString("store_num"), jsonObject.getString("store_address"),
                            jsonObject.getString("accessed_num"), jsonObject.getString("photo_compression"),
                            ("1".equals(jsonObject.getString("is_watermark"))), ("1".equals(jsonObject.getString("is_record"))),
                            packageid, jsonObject.getString("p_name"), jsonObject.getString("category1_name"),
                            jsonObject.getString("category2_name"), jsonObject.getString("category3_name"),
                            jsonObject.getString("category1_content"), jsonObject.getString("category2_content"),
                            jsonObject.getString("category3_content"), ("1".equals(jsonObject.getString("p_is_invalid"))),
                            ("1".equals(jsonObject.getString("package_attribute"))),
                            ("1".equals(jsonObject.getString("is_package"))),
                            ("1".equals(jsonObject.getString("is_package_task"))), jsonObject.getString("invalid_type"),
                            is_taskphoto,
                            taskid, jsonObject.getString("task_name"), tasktype, jsonObject.getString("outlet_batch"),
                            jsonObject.getString("p_batch"), taskdetail, jsonObject.getString("task_content"));
                    if ("1".equals(tasktype)) {
                        JSONObject jsonObject1 = new JSONObject(taskdetail);
                        String picStr = jsonObject1.getString("pics");
                        picStr = picStr.substring(1, picStr.length() - 1);
                        String[] pics = picStr.split(",");
                        for (int index = 0; index < pics.length; index++) {
                            String temp = pics[index].replaceAll("\"", "").replaceAll("\\\\", "");
                            if (!TextUtils.isEmpty(temp) && temp.length() > 1)
                                offlineDBHelper.insertOfflinedownload(username, projectid, storeid, packageid, taskid, Urls
                                        .ImgIp + temp);
                        }
                    } else if ("2".equals(tasktype)) {
                        JSONObject jsonObject1 = new JSONObject(taskdetail);
                        String url = jsonObject1.getString("url");
                        if (!TextUtils.isEmpty(url) && !url.equals("null")) {
                            offlineDBHelper.insertOfflinedownload(username, projectid, storeid, packageid, taskid, url);
                        }
                    } else if ("3".equals(tasktype)) {
                        JSONObject jsonObject1 = new JSONObject(taskdetail);
                        String picStr = jsonObject1.getString("pics");
                        picStr = picStr.substring(1, picStr.length() - 1);
                        String[] pics = picStr.split(",");
                        for (int index = 0; index < pics.length; index++) {
                            String temp = pics[index].replaceAll("\"", "").replaceAll("\\\\", "");
                            if (!TextUtils.isEmpty(temp) && temp.length() > 1)
                                offlineDBHelper.insertOfflinedownload(username, projectid, storeid, packageid, taskid, Urls
                                        .ImgIp + temp);
                        }
                    }
                    if (!issuccess) {
                        break;
                    }
                }
                if (!TextUtils.isEmpty(outltenote) || !"null".equals(outltenote)) {
                    offlineDBHelper.insertOutletNote(username, projectid, storeid, outltenote);
                }
            } catch (JSONException e) {
                issuccess = false;
                e.printStackTrace();
            }
            return issuccess;
        }

        protected void onPostExecute(Object o) {
            CustomProgressDialog.Dissmiss();
            if ((boolean) o) {
                Intent service = new Intent("com.orange.oy.DownloadDataService");
                service.setPackage("com.orange.oy");
                startService(service);
                refreshLeft();
            } else {
                Tools.showToast(OfflineStoreActivity.this, "数据存储失败！");
                offlineDBHelper.deleteStore(username, projectid, storeid);
            }
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.offlineproject_tab_left: {
                offlineproject_tab_left.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap
                        .offlines_down), null, null);
                offlineproject_tab_left.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                offlineproject_tab_left_line.setVisibility(View.VISIBLE);
                offlineproject_tab_right.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap
                        .offlines_downed_no), null, null);
                offlineproject_tab_right.setTextColor(getResources().getColor(R.color.colorPrimaryDark2));
                offlineproject_tab_right_line.setVisibility(View.INVISIBLE);
                offlineproject_listview.setVisibility(View.VISIBLE);
                offlineproject_listview2.setVisibility(View.GONE);
                offlineproject_title.settingHint("可搜索网点名称、网点编号、省份、城市");
                if (list_left == null || list_left.isEmpty()) {
                    refreshLeft();
                }
            }
            break;
            case R.id.offlineproject_tab_right: {
                offlineproject_tab_left.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap
                        .offlines_down_no), null, null);
                offlineproject_tab_left.setTextColor(getResources().getColor(R.color.colorPrimaryDark2));
                offlineproject_tab_left_line.setVisibility(View.INVISIBLE);
                offlineproject_tab_right.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap
                        .offlines_downed), null, null);
                offlineproject_tab_right.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                offlineproject_tab_right_line.setVisibility(View.VISIBLE);
                offlineproject_listview.setVisibility(View.GONE);
                offlineproject_listview2.setVisibility(View.VISIBLE);
                offlineproject_title.settingHint("可搜索网点名称、网点编号");
                if (list_right == null || list_right.isEmpty()) {
                    getRightData();
                }
            }
            break;
        }
    }

    private boolean isSelect1, isSelect2;

    private void initListview(PullToRefreshListView listview) {
        listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listview.setPullLabel(getResources().getString(R.string.listview_down));
        listview.setRefreshingLabel(getResources().getString(R.string.listview_refush));
        listview.setReleaseLabel(getResources().getString(R.string.listview_down2));
    }
}
