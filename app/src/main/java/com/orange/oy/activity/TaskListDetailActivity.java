package com.orange.oy.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
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
import com.orange.oy.adapter.TaskDetailLeftAdapter;
import com.orange.oy.adapter.TaskDetailRightAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.OfflineDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.MyDialog;
import com.orange.oy.dialog.TaskChangeDialog;
import com.orange.oy.info.MyteamNewfdInfo;
import com.orange.oy.info.TaskDetailLeftInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.CharacterParser;
import com.orange.oy.util.FileCache;
import com.orange.oy.util.PinyinComparatorForMyteam;
import com.orange.oy.view.AppTitle;

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
 * 任务列表-查看任务列表
 */
public class TaskListDetailActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View
        .OnClickListener, TaskChangeDialog.OnItemClickListener {
    private AppTitle taskdetail_title;

    public void onBack() {
        baseFinish();
    }

    private String searchStr;

    private void initTitle() {
        taskdetail_title = (AppTitle) findViewById(R.id.taskdetail_title);
        taskdetail_title.settingHint("可搜索网点名称、网点编号、省份、城市");
        taskdetail_title.showSearch(new TextWatcher() {
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
                    refreshLeft();
                    v.setText("");
                    return true;
                }
                return false;
            }
        });
        taskdetail_title.showBack(this);
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
            Dzxstartlist.stop(Urls.Dzxstartlist);
        }
        if (Zxwcstartlist != null) {
            Zxwcstartlist.stop(Urls.Zxwcstartlist);
        }
        if (Redo != null) {
            Redo.stop(Urls.Redo);
        }
        if (Downloadtasklist != null) {
            Downloadtasklist.stop(Urls.Downloadtasklist);
        }
    }

    /**
     * 换人
     *
     * @param myteamNewfdInfo
     */
    public void onItemClick(MyteamNewfdInfo myteamNewfdInfo) {
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

    class ClearCache extends AsyncTask {
        protected void onPreExecute() {
            if (offlineDBHelper == null) {
                offlineDBHelper = new OfflineDBHelper(TaskListDetailActivity.this);
            }
            CustomProgressDialog.showProgressDialog(TaskListDetailActivity.this, "清理缓存");
        }


        protected Object doInBackground(Object[] params) {
            offlineDBHelper.clearCache();
            recurDelete(new File(FileCache.getCacheDir(TaskListDetailActivity.this).getPath() + "/download"));
            AppInfo.clearCachesize(TaskListDetailActivity.this);
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

    private String project_id, projectname;
    private int page_left, page_right;
    private String storenum, accessednum, flag;
    private String selStoreid;

    private void initNetworkConnection() {
        getTeamData = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(TaskListDetailActivity.this));
                return params;
            }
        };
        getTeamData.setIsShowDialog(true);
        changeAccessed = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("storeid", storenum);
                params.put("accessednum", accessednum);
                params.put("usermobile", AppInfo.getName(TaskListDetailActivity.this));
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
                params.put("user_mobile", AppInfo.getName(TaskListDetailActivity.this));
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
                params.put("page", page_right + "");
                params.put("project_id", project_id);
                params.put("user_mobile", AppInfo.getName(TaskListDetailActivity.this));
                return params;
            }
        };
        Redo = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("storeid", selStoreid);
                params.put("usermobile", AppInfo.getName(TaskListDetailActivity.this));
                return params;
            }
        };
        Redo.setIsShowDialog(true);
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

    private TextView taskdetail_tab_left, taskdetail_tab_right;
    private View taskdetail_tab_left_line, taskdetail_tab_right_line;
    private PullToRefreshListView taskdetail_listview_left, taskdetail_listview_right;
    private TaskDetailLeftAdapter taskDetailLeftAdapter;
    private TaskDetailRightAdapter taskDetailRightAdapter;
    private NetworkConnection getTeamData, changeAccessed, Dzxstartlist, Zxwcstartlist, Redo, Downloadtasklist;
    private CharacterParser characterParser;
    private PinyinComparatorForMyteam pinyinComparatorForMyteam;
    private ArrayList<MyteamNewfdInfo> listForTeam;
    private ArrayList<TaskDetailLeftInfo> list_left, list_right;
    public static boolean isRefresh;
    private String city;
    private String photo_compression;
    private OfflineDBHelper offlineDBHelper;
    private String is_record;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskdetail);
        offlineDBHelper = new OfflineDBHelper(this);
        initTitle();
        initNetworkConnection();
        Intent data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        project_id = data.getStringExtra("project_id");
        projectname = data.getStringExtra("projectname");
        city = data.getStringExtra("city");
        is_record = data.getStringExtra("is_record");
        photo_compression = data.getStringExtra("photo_compression");
        characterParser = CharacterParser.getInstance();
        pinyinComparatorForMyteam = new PinyinComparatorForMyteam();
        taskdetail_tab_left = (TextView) findViewById(R.id.taskdetail_tab_left);
        taskdetail_tab_right = (TextView) findViewById(R.id.taskdetail_tab_right);
        taskdetail_tab_left_line = findViewById(R.id.taskdetail_tab_left_line);
        taskdetail_tab_right_line = findViewById(R.id.taskdetail_tab_right_line);
        taskdetail_listview_left = (PullToRefreshListView) findViewById(R.id.taskdetail_listview_left);
        taskdetail_listview_right = (PullToRefreshListView) findViewById(R.id.taskdetail_listview_right);
        taskdetail_listview_right.setVisibility(View.GONE);
        initListview(taskdetail_listview_left);
        initListview(taskdetail_listview_right);
        taskdetail_listview_left.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshLeft();
            }

            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page_left++;
                getDataForLeft();
            }
        });
        taskdetail_listview_right.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshRight();
            }

            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page_right++;
                getDataForRight();
            }
        });
        taskdetail_listview_left.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TaskDetailLeftInfo taskDetailLeftInfo = list_left.get(position - 1);
                if (taskDetailLeftAdapter != null) {
                    int selectButton = taskDetailLeftAdapter.selectButton();
                    switch (selectButton) {
                        case 0: {//换人
                            storenum = taskDetailLeftInfo.getId();
                            getTeamData();
                        }
                        break;
                        case 1: {//开始执行
                            if (taskDetailLeftInfo.getIsOffline() == 1) {
                                Intent intent = new Intent(TaskListDetailActivity.this, OfflinePackageActivity.class);
                                intent.putExtra("id", taskDetailLeftInfo.getId());
                                intent.putExtra("isstore", taskDetailLeftInfo.getIsstore());
                                intent.putExtra("projectname", projectname);
                                intent.putExtra("store_name", taskDetailLeftInfo.getName());
                                intent.putExtra("store_num", taskDetailLeftInfo.getCode());
                                intent.putExtra("province", taskDetailLeftInfo.getCity());
                                intent.putExtra("city", taskDetailLeftInfo.getCity2());
                                intent.putExtra("project_id", project_id);
                                intent.putExtra("photo_compression", photo_compression);
                                intent.putExtra("is_record", is_record);
                                startActivity(intent);
                            } else {
                                if (taskDetailLeftInfo.getIs_desc().equals("1")) {//有网点说明
                                    Intent intent = new Intent(TaskListDetailActivity.this, StoreDescActivity.class);
                                    intent.putExtra("id", taskDetailLeftInfo.getId());
                                    intent.putExtra("isstore", taskDetailLeftInfo.getIsstore());
                                    intent.putExtra("projectname", projectname);
                                    intent.putExtra("store_name", taskDetailLeftInfo.getName());
                                    intent.putExtra("store_num", taskDetailLeftInfo.getCode());
                                    intent.putExtra("province", taskDetailLeftInfo.getCity());
                                    intent.putExtra("city", taskDetailLeftInfo.getCity2());
                                    intent.putExtra("project_id", project_id);
                                    intent.putExtra("photo_compression", photo_compression);
                                    intent.putExtra("is_desc", "1");
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(TaskListDetailActivity.this, TaskitemDetailActivity.class);
                                    intent.putExtra("id", taskDetailLeftInfo.getId());
                                    intent.putExtra("isstore", taskDetailLeftInfo.getIsstore());
                                    intent.putExtra("projectname", projectname);
                                    intent.putExtra("store_name", taskDetailLeftInfo.getName());
                                    intent.putExtra("store_num", taskDetailLeftInfo.getCode());
                                    intent.putExtra("province", taskDetailLeftInfo.getCity());
                                    intent.putExtra("city", taskDetailLeftInfo.getCity2());
                                    intent.putExtra("project_id", project_id);
                                    intent.putExtra("photo_compression", photo_compression);
                                    intent.putExtra("is_desc", "0");
                                    startActivity(intent);
                                }
                            }
                        }
                        break;
                        case 2: {//查看执行时间
                            Tools.d("执行时间");
                            TextView textView = new TextView(TaskListDetailActivity.this);
                            textView.setBackgroundColor(Color.WHITE);
                            textView.setTextColor(Color.BLACK);
                            textView.setTextSize(15);
                            textView.setGravity(Gravity.CENTER_HORIZONTAL);
                            textView.setText("\n可执行时间\n\n" + taskDetailLeftInfo.getTimedetail());
                            textView.setHeight(Tools.getScreeInfoHeight(TaskListDetailActivity.this) / 2);
                            MyDialog myDialog = new MyDialog(TaskListDetailActivity.this, textView, false, 0);
                            myDialog.setMyDialogWidth(Tools.getScreeInfoWidth(TaskListDetailActivity.this) - 40);
                            myDialog.showAtLocation((TaskListDetailActivity.this.findViewById(R.id.main)),
                                    Gravity.CENTER_VERTICAL, 0, 0); //设置layout在PopupWindow中显示的位置
                        }
                        break;
                        case 3: {//离线下载
                            Tools.d("离线下载");
                            selStoreid = taskDetailLeftInfo.getId();
                            Downloadtasklist(taskDetailLeftInfo.getTimedetail());
                        }
                        break;
                    }
                    taskDetailLeftAdapter.clear();
                }
            }
        });
        taskdetail_listview_right.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TaskDetailLeftInfo taskDetailLeftInfo = list_right.get(position - 1);
                if (taskDetailRightAdapter != null) {
                    int selectButton = taskDetailRightAdapter.selectButton();
                    switch (selectButton) {
                        case 0: {//查看详情
                            Intent intent = new Intent(TaskListDetailActivity.this, TaskFinishActivity.class);
                            intent.putExtra("store_id", taskDetailLeftInfo.getId());
                            intent.putExtra("is_store", taskDetailLeftInfo.getIsstore());
                            intent.putExtra("state", "1");
                            startActivity(intent);
                        }
                        break;
                        case 1: {//返回重做 TODO
                            selStoreid = taskDetailLeftInfo.getId();
                            Redo();
                        }
                        break;
                    }
                    taskDetailLeftAdapter.clear();
                }
            }
        });
        list_left = new ArrayList<>();
        taskDetailLeftAdapter = new TaskDetailLeftAdapter(this, list_left);
        taskdetail_listview_left.setAdapter(taskDetailLeftAdapter);
        list_right = new ArrayList<>();
        taskDetailRightAdapter = new TaskDetailRightAdapter(this, list_right);
        taskdetail_listview_right.setAdapter(taskDetailRightAdapter);
        taskdetail_tab_left.setOnClickListener(this);
        taskdetail_tab_right.setOnClickListener(this);
        onClick(taskdetail_tab_left);
        isRefresh = false;
    }

    protected void onResume() {
        super.onResume();
        if (isRefresh) {
            isRefresh = false;
            refreshLeft();
        }
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
                    AppInfo.addCachesize(TaskListDetailActivity.this, s.getBytes().length);
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        if ("1".equals(jsonObject.getString("is_exe"))) {
                            ConfirmDialog.showDialog(TaskListDetailActivity.this, "提示", "该网点已做过任务，下载离线再做会覆盖之前的任务，是否继续？", "否",
                                    "继续", jsonObject, true, new ConfirmDialog.OnSystemDialogClickListener() {
                                        public void leftClick(Object object) {
                                        }

                                        public void rightClick(Object object) {
                                            if (object != null) {
                                                try {
                                                    parseData(((JSONObject) object).getJSONArray("datas"), time);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    });
                        } else {
                            parseData(jsonObject.getJSONArray("datas"), time);
                        }
                    } else if (code == 1) {
                        Tools.showToast(TaskListDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Tools.showToast(TaskListDetailActivity.this, getResources().getString(R.string
                            .network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskListDetailActivity.this, getResources().getString(R.string
                        .network_volleyerror));
            }
        }, "正在下载离线....");
    }

    private void parseData(JSONArray jsonArray, String time) throws JSONException {
        Tools.d("解析存储离线数据");
        if (offlineDBHelper == null) return;
        new parseDataAsyncTask(jsonArray, time).executeOnExecutor(Executors.newCachedThreadPool());
    }

    class parseDataAsyncTask extends AsyncTask {
        JSONArray jsonArray;
        String time;

        parseDataAsyncTask(JSONArray jsonArray, String time) {
            this.jsonArray = jsonArray;
            this.time = time;
        }

        protected void onPreExecute() {
            CustomProgressDialog.showProgressDialog(TaskListDetailActivity.this, "正在离线数据");
        }


        protected Object doInBackground(Object[] params) {
            int length = jsonArray.length();
            boolean isCheck = false;
            String username = AppInfo.getName(TaskListDetailActivity.this);
            boolean issuccess = false;
            try {
                for (int i = 0; i < length; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (!isCheck) {
                        offlineDBHelper.checkClear(username, jsonObject.getString("project_id"), jsonObject.getString
                                ("store_id"));
                        isCheck = true;
                    }
                    String projectid = jsonObject.getString("project_id");
                    String storeid = jsonObject.getString("store_id");
                    String taskid = jsonObject.getString("task_id");
                    String packageid = jsonObject.getString("p_id");
                    String tasktype = jsonObject.getString("task_type");
                    String taskdetail = jsonObject.getString("task_detail");
//                    issuccess = offlineDBHelper.insertOfflinedata(username, projectid,
//                            jsonObject.getString("project_name"), jsonObject.getString("code"), jsonObject.getString("brand"),
//                            storeid, jsonObject.getString("store_name"),
//                            time, jsonObject.getString("store_num"), jsonObject.getString("store_address"),
//                            jsonObject.getString("accessed_num"), jsonObject.getString("photo_compression"),
//                            ("1".equals(jsonObject.getString("is_watermark"))),
//                            ("1".equals(jsonObject.getString("is_record"))), packageid,
//                            jsonObject.getString("p_name"), jsonObject.getString("category1_name"),
//                            jsonObject.getString("category2_name"), jsonObject.getString("category3_name"),
//                            jsonObject.getString("category1_content"), jsonObject.getString("category2_content"),
//                            jsonObject.getString("category3_content"), ("1".equals(jsonObject.getString("p_is_invalid"))),
//                            ("1".equals(jsonObject.getString("package_attribute"))),
//                            ("1".equals(jsonObject.getString("is_package"))), taskid, jsonObject.getString("task_name"),
//                            tasktype, jsonObject.getString("outlet_batch"), jsonObject.getString("p_batch"),
//                            taskdetail, jsonObject.getString("task_content"));
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
                    }
                    if (!issuccess) {
                        break;
                    }
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
                Tools.showToast(TaskListDetailActivity.this, "数据存储失败！");
            }
        }
    }

    /**
     * 刷新左侧列表
     */
    private void refreshLeft() {
        page_left = 1;
        getDataForLeft();
    }

    /**
     * 刷新中间列表
     */
    private void refreshRight() {
        page_right = 1;
        getDataForRight();
    }

    private void Redo() {
        Redo.sendPostRequest(Urls.Redo, new Response.Listener<String>() {
                    public void onResponse(String s) {
                        Tools.d(s);
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            int code = jsonObject.getInt("code");
                            if (code == 200) {
                                refreshRight();
                                Tools.showToast(TaskListDetailActivity.this, jsonObject.getString("msg"));
                            } else {
                                Tools.showToast(TaskListDetailActivity.this, jsonObject.getString("msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Tools.showToast(TaskListDetailActivity.this, getResources().getString(R.string
                                    .network_error));
                        }
                        CustomProgressDialog.Dissmiss();
                    }
                }, new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError volleyError) {
                        CustomProgressDialog.Dissmiss();
                        Tools.showToast(TaskListDetailActivity.this, getResources().getString(R.string
                                .network_volleyerror));
                    }
                }
        );
    }

    private void getDataForLeft() {
        Dzxstartlist.sendPostRequest(Urls.Dzxstartlist, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                searchStr = "";
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        if (list_left == null) {
                            list_left = new ArrayList<TaskDetailLeftInfo>();
                            taskDetailLeftAdapter.upData(list_left);
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
                                        timeDetail = timeDetail + "\n\n" + str;
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
                                            timeDetail = timeDetail + "\n\n" + date;
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
                        offlineDBHelper.isCompletedForStore(AppInfo.getName(TaskListDetailActivity.this), project_id,
                                list_left);
                        taskdetail_listview_left.onRefreshComplete();
                        if (length == 0) {
                            taskdetail_listview_left.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            taskdetail_listview_left.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                        taskDetailLeftAdapter.notifyDataSetChanged();
                    } else {
                        Tools.showToast(TaskListDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Tools.showToast(TaskListDetailActivity.this, getResources().getString(R.string.network_error));
                }
                taskdetail_listview_left.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                taskdetail_listview_left.onRefreshComplete();
                Tools.showToast(TaskListDetailActivity.this, getResources().getString(R.string
                        .network_volleyerror));
            }
        });
    }

    private void getDataForRight() {
        Zxwcstartlist.sendPostRequest(Urls.Zxwcstartlist, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        if (list_right == null) {
                            list_right = new ArrayList<TaskDetailLeftInfo>();
                            taskDetailRightAdapter.upData(list_right);
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
                        if (length < 15) {
                            taskdetail_listview_right.onRefreshComplete();
                            taskdetail_listview_right.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            taskdetail_listview_right.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                        taskDetailRightAdapter.notifyDataSetChanged();
                    } else {
                        Tools.showToast(TaskListDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskListDetailActivity.this, getResources().getString(R.string.network_error));
                }
                taskdetail_listview_right.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                taskdetail_listview_right.onRefreshComplete();
                Tools.showToast(TaskListDetailActivity.this, getResources().getString(R.string
                        .network_volleyerror));
            }
        });
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
                        refreshLeft();
                    } else {
                        Tools.showToast(TaskListDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskListDetailActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskListDetailActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

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
                        TaskChangeDialog.showDialog(TaskListDetailActivity.this, listForTeam, TaskListDetailActivity.this);
                    } else {
                        Tools.showToast(TaskListDetailActivity.this, jsonObject.getString("msg"));
                        TaskChangeDialog.showDialog(TaskListDetailActivity.this, listForTeam, TaskListDetailActivity.this);
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskListDetailActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskListDetailActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taskdetail_tab_left: {
                if (taskdetail_listview_left.getVisibility() != View.VISIBLE) {
                    taskdetail_listview_left.setVisibility(View.VISIBLE);
                    taskdetail_listview_right.setVisibility(View.GONE);
                    changeTab();
                }
                if (list_left == null || list_left.isEmpty()) {
                    refreshLeft();
                }
            }
            break;
            case R.id.taskdetail_tab_right: {
                if (taskdetail_listview_right.getVisibility() != View.VISIBLE) {
                    taskdetail_listview_left.setVisibility(View.GONE);
                    taskdetail_listview_right.setVisibility(View.VISIBLE);
                    changeTab();
                }
                if (list_right == null || list_right.isEmpty()) {
                    refreshRight();
                }
            }
            break;
        }
    }

    private void changeTab() {
        if (taskdetail_listview_left.getVisibility() == View.VISIBLE) {
            if (taskdetail_listview_right.getVisibility() == View.VISIBLE) {
                taskdetail_listview_right.setVisibility(View.GONE);
            }
            taskdetail_tab_left.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            taskdetail_tab_left_line.setVisibility(View.VISIBLE);
            taskdetail_tab_right.setTextColor(Color.BLACK);
            taskdetail_tab_right_line.setVisibility(View.GONE);
        } else if (taskdetail_listview_right.getVisibility() == View.VISIBLE) {
            taskdetail_tab_right.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            taskdetail_tab_right_line.setVisibility(View.VISIBLE);
            taskdetail_tab_left.setTextColor(Color.BLACK);
            taskdetail_tab_left_line.setVisibility(View.GONE);
        } else {
            taskdetail_listview_left.setVisibility(View.VISIBLE);
            changeTab();
        }
    }

    private void initListview(PullToRefreshListView listview) {
        listview.setMode(PullToRefreshBase.Mode.BOTH);
        listview.setPullLabel(getResources().getString(R.string.listview_down));// 刚下拉时，显示的提示
        listview.setRefreshingLabel(getResources().getString(R.string.listview_refush));// 刷新时
        listview.setReleaseLabel(getResources().getString(R.string.listview_down2));// 下来达到一定距离时，显示的提示
    }

    protected void onDestroy() {
        super.onDestroy();
        offlineDBHelper = null;
    }
}
