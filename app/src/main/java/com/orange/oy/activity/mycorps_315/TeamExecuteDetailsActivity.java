package com.orange.oy.activity.mycorps_315;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.adapter.mycorps_314.ExecuteDetailsAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.info.mycorps.TeamExecuteDetailsInfo;
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


/***
 * 队员执行明细页面
 */
public class TeamExecuteDetailsActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, ExecuteDetailsAdapter.ExecuteDetailsAdapterCallback {
    private AppTitle taskILL_title;
    private ExecuteDetailsAdapter executeDetailsAdapter;
    private PullToRefreshListView plistview;
    private NetworkView lin_Nodata;
    private View headview; //头布局
    private TextView tv_head_name, tv_head_alltask, tv_head_time;
    private NetworkConnection teamMemberExeInfo;
    private String package_team_id;
    private int page = 1;
    private ArrayList<TeamExecuteDetailsInfo> detailsInfoArrayList = new ArrayList<>();

    private void initNetworkConnection() {
        teamMemberExeInfo = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(TeamExecuteDetailsActivity.this));
                params.put("package_team_id", package_team_id);// package_team_id	战队和网点包关系id【必传】
                return params;
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (teamMemberExeInfo != null) {
            teamMemberExeInfo.stop(Urls.APPLYUSERLIST);
        }
    }

    private void initTitle() {
        package_team_id = getIntent().getStringExtra("package_team_id");
        taskILL_title = (AppTitle) findViewById(R.id.titleview);
        taskILL_title.settingName("状态明细");
        taskILL_title.showBack(this);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_execute_details);
        initTitle();
        initNetworkConnection();
        plistview = (PullToRefreshListView) findViewById(R.id.plistview);
        lin_Nodata = (NetworkView) findViewById(R.id.lin_Nodata);

        refreshListView();
        getData();

        headview = View.inflate(TeamExecuteDetailsActivity.this, R.layout.header_team_details, null);
        tv_head_name = (TextView) headview.findViewById(R.id.tv_head_name);
        tv_head_alltask = (TextView) headview.findViewById(R.id.tv_head_alltask);
        tv_head_time = (TextView) headview.findViewById(R.id.tv_head_time);
        ListView lv = plistview.getRefreshableView();
        lv.addHeaderView(headview);
        executeDetailsAdapter = new ExecuteDetailsAdapter(this, detailsInfoArrayList);
        plistview.setAdapter(executeDetailsAdapter);
        executeDetailsAdapter.setCallback(this);

    }

    private void refreshListView() {
        plistview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                getData();
            }
        });
    }


    private void getData() {
        teamMemberExeInfo.sendPostRequest(Urls.TeamMemberExeInfo, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (page == 1) {
                            if (!detailsInfoArrayList.isEmpty()) {
                                detailsInfoArrayList.clear();

                            }
                        }
                        jsonObject = jsonObject.optJSONObject("data");
                        JSONObject object1 = jsonObject.optJSONObject("project_info");
                        tv_head_name.setText(object1.getString("project_name"));
                        if (!object1.isNull("total_outlet")) {
                            tv_head_alltask.setText("任务总量 ：" + object1.getString("total_outlet"));
                        } else {
                            tv_head_alltask.setText("任务总量 ：0");
                        }

                        tv_head_time.setText(object1.getString("begin_date") + "~" + object1.getString("end_date") + "可执行");

                        JSONArray jsonArray = jsonObject.optJSONArray("list");
                        if (jsonArray != null) {
                            plistview.setVisibility(View.VISIBLE);
                            lin_Nodata.setVisibility(View.GONE);
                            int length = jsonArray.length();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.optJSONObject(i);
                                TeamExecuteDetailsInfo teamMemberExeInfo = new TeamExecuteDetailsInfo();
                                teamMemberExeInfo.setCheck_outlet(object.optString("check_outlet"));
                                teamMemberExeInfo.setGet_outlet(object.optString("get_outlet"));
                                teamMemberExeInfo.setIdentity(object.optString("identity"));
                                teamMemberExeInfo.setMobile(object.optString("mobile"));
                                teamMemberExeInfo.setPass_outlet(object.optString("pass_outlet"));
                                teamMemberExeInfo.setUnpass_outlet(object.optString("unpass_outlet"));
                                teamMemberExeInfo.setUser_id(object.optString("user_id"));
                                teamMemberExeInfo.setUser_img(object.optString("user_img"));
                                teamMemberExeInfo.setUser_level(object.optString("user_level"));
                                teamMemberExeInfo.setUser_name(object.optString("user_name"));
                                teamMemberExeInfo.setUser_sex(object.optString("user_sex"));
                                teamMemberExeInfo.setWait_exe_outlet(object.optString("wait_exe_outlet"));

                                detailsInfoArrayList.add(teamMemberExeInfo);
                            }
                            plistview.onRefreshComplete();
                            if (length < 15) {
                                plistview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            } else {
                                plistview.setMode(PullToRefreshBase.Mode.BOTH);
                            }
                            if (executeDetailsAdapter != null) {
                                executeDetailsAdapter.notifyDataSetChanged();
                            }
                            plistview.onRefreshComplete();
                        } else {
                            plistview.setVisibility(View.GONE);
                            lin_Nodata.setVisibility(View.VISIBLE);
                            lin_Nodata.NoSearch("没有数据哦!");
                        }

                    } else {
                        plistview.onRefreshComplete();
                        Tools.showToast(TeamExecuteDetailsActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TeamExecuteDetailsActivity.this, getResources().getString(R.string.network_error));
                }
                plistview.onRefreshComplete();
            }

        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                plistview.onRefreshComplete();
                plistview.setVisibility(View.GONE);
                lin_Nodata.NoNetwork();
                lin_Nodata.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void callPhone(int pos, String tel) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + tel);
        intent.setData(data);
        startActivity(intent);
    }
}
