package com.orange.oy.activity.newtask;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.activity.TaskillustratesActivity;
import com.orange.oy.activity.black.BlackDZXListActivity;
import com.orange.oy.adapter.MyTaskListAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.AppDBHelper;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.TaskNewInfo;
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
 * 我的任务列表页
 */
public class MyTaskListActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, AdapterView.OnItemClickListener {

    public void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.mytasklist_title);
        appTitle.settingName("我的任务");
        appTitle.showBack(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (myTaskProjectList != null) {
            myTaskProjectList.stop(Urls.MyTaskProjectList);
        }
    }

    public void initNetworkConnection() {
        myTaskProjectList = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(MyTaskListActivity.this));
                params.put("token", Tools.getToken());
                return params;
            }
        };
        myTaskProjectList.setIsShowDialog(true);
    }

    private NetworkConnection myTaskProjectList;
    private PullToRefreshListView listView;
    private MyTaskListAdapter myTaskListAdapter;
    private ArrayList<TaskNewInfo> list;
    private AppDBHelper appDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_task_list);
        appDBHelper = new AppDBHelper(this);
        initTitle();
        initNetworkConnection();
        list = new ArrayList<>();
        listView = (PullToRefreshListView) findViewById(R.id.mytasklist_listview);
        listView.setCanDelete(true);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getData();
            }
        });
        listView.setOnItemClickListener(this);
        myTaskListAdapter = new MyTaskListAdapter(this, list);
        listView.setAdapter(myTaskListAdapter);
        getData();
    }

    public static boolean isRefresh;

    protected void onResume() {
        super.onResume();
        if (isRefresh) {
            isRefresh = false;
            getData();
        }
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TaskNewInfo taskNewInfo = list.get(position - 1);
        if (taskNewInfo.getType().equals("2")) {
            if (appDBHelper.getIsShow(taskNewInfo.getId()) && "1".equals(taskNewInfo.getStandard_state())) {
                Intent intent = new Intent(this, TaskillustratesActivity.class);
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
//        intent.putExtra("city", tasknew_distric.getText().toString());
                intent.putExtra("type", taskNewInfo.getType());
                intent.putExtra("money_unit", taskNewInfo.getMoney_unit());
                intent.putExtra("isShow", "1");//是否显示不再显示复选框
                intent.putExtra("standard_state", taskNewInfo.getStandard_state());
                intent.putExtra("isHomePage", "0");//是否是首页传过来的
                startActivity(intent);
            } else {
                Intent intent = new Intent(MyTaskListActivity.this, BlackDZXListActivity.class);
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
//            intent.putExtra("city", tasknew_distric.getText().toString());
                intent.putExtra("type", taskNewInfo.getType());
                intent.putExtra("mytype", "1");
                intent.putExtra("money_unit", taskNewInfo.getMoney_unit());
                intent.putExtra("standard_state", taskNewInfo.getStandard_state());
                startActivity(intent);
            }
        } else if (taskNewInfo.getType().equals("1")) {
            if (appDBHelper.getIsShow(taskNewInfo.getId()) && "1".equals(taskNewInfo.getStandard_state())) {
                Intent intent = new Intent(this, TaskillustratesActivity.class);
                intent.putExtra("projectid", taskNewInfo.getId());
                intent.putExtra("projectname", taskNewInfo.getProject_name());
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
//        intent.putExtra("city", tasknew_distric.getText().toString());
                intent.putExtra("type", taskNewInfo.getType());
                intent.putExtra("money_unit", taskNewInfo.getMoney_unit());
                intent.putExtra("isShow", "1");//是否显示不再显示复选框
                intent.putExtra("standard_state", taskNewInfo.getStandard_state());
                intent.putExtra("isHomePage", "0");//是否是首页传过来的
                startActivity(intent);
            } else {
                Intent intent = new Intent(MyTaskListActivity.this, MyTaskDetailActivity.class);
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
//        intent.putExtra("city", tasknew_distric.getText().toString());
                intent.putExtra("type", taskNewInfo.getType());
                intent.putExtra("money_unit", taskNewInfo.getMoney_unit());
                intent.putExtra("standard_state", taskNewInfo.getStandard_state());
                startActivity(intent);
            }
        }
    }

    public void getData() {
        myTaskProjectList.sendPostRequest(Urls.MyTaskProjectList, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (list == null) {
                            list = new ArrayList<TaskNewInfo>();
                        } else {
                            list.clear();
                        }
                        JSONArray jsonArray = jsonObject.getJSONArray("datas");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            TaskNewInfo taskNewInfo = new TaskNewInfo();
                            taskNewInfo.setId(object.getString("id"));
                            taskNewInfo.setProject_name(object.getString("project_name"));
                            taskNewInfo.setProject_code(object.getString("project_code"));
                            taskNewInfo.setProject_type(object.getString("project_type"));
                            taskNewInfo.setIs_record(Tools.StringToInt(object.getString("is_record")));
                            taskNewInfo.setPhoto_compression(object.getString("photo_compression"));
                            taskNewInfo.setBegin_date(object.getString("begin_date"));
                            taskNewInfo.setEnd_date(object.getString("end_date"));
                            taskNewInfo.setIs_download(Tools.StringToInt(object.getString("is_download")));
                            taskNewInfo.setIs_watermark(Tools.StringToInt(object.getString("is_watermark")));
                            taskNewInfo.setCode(object.getString("code"));
                            taskNewInfo.setBrand(object.getString("brand"));
                            taskNewInfo.setIs_takephoto(Tools.StringToInt(object.getString("is_takephoto")));
                            taskNewInfo.setType(object.getString("type"));
                            taskNewInfo.setShow_type(object.getString("show_type"));
                            taskNewInfo.setCheck_time(object.getString("check_time") + "");
                            taskNewInfo.setMin_reward(object.getString("min_reward"));
                            taskNewInfo.setMax_reward(object.getString("max_reward"));
                            taskNewInfo.setProject_property(object.getString("project_property"));
                            taskNewInfo.setPublish_time(object.optString("publish_time"));
                            taskNewInfo.setProject_person(object.optString("project_person"));
                            taskNewInfo.setMoney_unit(object.getString("money_unit"));
                            taskNewInfo.setCertification(object.getString("certification"));
                            taskNewInfo.setStandard_state(object.getString("standard_state"));
                            list.add(taskNewInfo);
                        }
                        if (myTaskListAdapter != null) {
                            myTaskListAdapter.notifyDataSetChanged();
                        }
                        listView.onRefreshComplete();
                    } else {
                        Tools.showToast(MyTaskListActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(MyTaskListActivity.this, getResources().getString(R.string.network_error));
                }
                listView.onRefreshComplete();
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                listView.onRefreshComplete();
                Tools.showToast(MyTaskListActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }
}
