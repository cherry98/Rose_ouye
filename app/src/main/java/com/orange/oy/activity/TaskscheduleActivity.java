package com.orange.oy.activity;

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
import com.orange.oy.adapter.TaskscheduleAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.TaskInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 完成进度
 */
public class TaskscheduleActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {
    private AppTitle tasksch_title;

    public void onBack() {
        baseFinish();
    }

    private void initTitle() {
        tasksch_title = (AppTitle) findViewById(R.id.tasksch_title);
        tasksch_title.settingName(getResources().getString(R.string.taskschedule));
        tasksch_title.showBack(this);
    }

    protected void onStop() {
        super.onStop();
        if (Selectprojectwcjd != null) {
            Selectprojectwcjd.stop(Urls.Selectprojectwcjd);
        }
    }

    private String project_id;

    private void initNetworkConnection() {
        Selectprojectwcjd = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("project_id", project_id);
                params.put("user_mobile", AppInfo.getName(TaskscheduleActivity.this));
                return params;
            }
        };
        Selectprojectwcjd.setIsShowDialog(true);
    }

    private PullToRefreshListView tasksch_listview;
    private TaskscheduleAdapter taskscheduleAdapter;
    private ArrayList<TaskInfo> list;
    private NetworkConnection Selectprojectwcjd;
    private String projectname;
    private String city;
    private String photo_compression;
    private String is_record;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskschedule);
        initNetworkConnection();
        initTitle();
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
        tasksch_listview = (PullToRefreshListView) findViewById(R.id.tasksch_listview);
        tasksch_listview.setMode(PullToRefreshBase.Mode.DISABLED);
        tasksch_listview.setPullLabel(getResources().getString(R.string.listview_down));
        tasksch_listview.setRefreshingLabel(getResources().getString(R.string.listview_refush));
        tasksch_listview.setReleaseLabel(getResources().getString(R.string.listview_down2));
        tasksch_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            }

            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            }
        });
        tasksch_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TaskInfo taskInfo = list.get(position - 1);
                Intent intent = new Intent(TaskscheduleActivity.this, TaskscheduleDetailActivity.class);
                intent.putExtra("project_id", taskInfo.getId());
                intent.putExtra("projectname", projectname);
                intent.putExtra("city", city);
                intent.putExtra("title", taskInfo.getName());
                intent.putExtra("photo_compression", photo_compression);
                intent.putExtra("is_record", is_record);
                startActivity(intent);
            }
        });
        list = new ArrayList<>();
        taskscheduleAdapter = new TaskscheduleAdapter(this, list);
        tasksch_listview.setAdapter(taskscheduleAdapter);
        getData();
    }

    private void getData() {
        Selectprojectwcjd.sendPostRequest(Urls.Selectprojectwcjd, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        if (list == null) {
                            list = new ArrayList<TaskInfo>();
                        } else {
                            list.clear();
                        }
                        jsonObject = jsonObject.getJSONObject("datas");
                        TaskInfo taskInfo = new TaskInfo();
                        taskInfo.setName(jsonObject.getString("projectName"));
                        taskInfo.setId(jsonObject.getString("projectid"));
                        taskInfo.setItem1Num(jsonObject.getString("storesum"));
                        taskInfo.setItem2Num(jsonObject.getString("wcnum"));
                        taskInfo.setItem3Num(jsonObject.getString("zlhsnum"));
                        list.add(taskInfo);
                        taskscheduleAdapter.notifyDataSetChanged();
                    } else {
                        Tools.showToast(TaskscheduleActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TaskscheduleActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TaskscheduleActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }
}
