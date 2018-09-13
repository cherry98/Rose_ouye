package com.orange.oy.activity.mycorps_314;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.adapter.mycorps_314.JoinCorpAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CloseTaskDialog;
import com.orange.oy.info.mycorps.JoinCorpInfo;
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
 * 加入战队 V3.14
 */
public class JoinCorpActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, AdapterView.OnItemClickListener, JoinCorpAdapter.OnRefreshListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.joincorp_title);
        appTitle.settingName("加入战队");
        appTitle.showBack(this);
        appTitle.showIllustrate(R.mipmap.join_filter, new AppTitle.OnExitClickForAppTitle() {
            @Override
            public void onExit() {//战队筛选条件
                startActivityForResult(new Intent(JoinCorpActivity.this, CorpsFilterActivity.class), AppInfo.REQUEST_CODE_FILTER);
            }
        });
    }

    private void initNetwork() {
        applyToTeam = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(JoinCorpActivity.this));
                params.put("token", Tools.getToken());
                params.put("team_id", team_id);
                params.put("reason", reason);
                return params;
            }
        };
        teamlist = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(JoinCorpActivity.this));
                params.put("token", Tools.getToken());
                if (!TextUtils.isEmpty(keword)) {
                    params.put("keword", keword);
                }
                if (!TextUtils.isEmpty(team_speciality)) {
                    params.put("team_speciality", team_speciality);
                }
                if (!TextUtils.isEmpty(team_state)) {
                    params.put("team_state", team_state);
                }
                if (!TextUtils.isEmpty(user_num)) {
                    params.put("user_num", user_num);
                }
                if (!TextUtils.isEmpty(pvince)) {
                    params.put("pvince", pvince);
                }
                params.put("page", pageNum + "");
                return params;
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (applyToTeam != null) {
            applyToTeam.stop(Urls.ApplyToTeam);
        }
        if (teamlist != null) {
            teamlist.stop(Urls.Teamlist);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private TextView joincorp_result;//筛选结果
    private PullToRefreshListView joincorp_listview;
    private JoinCorpAdapter joinCorpAdapter;
    private NetworkConnection teamlist, applyToTeam;
    private String keword, team_speciality, user_num, team_state, pvince;
    private ArrayList<JoinCorpInfo> list;
    private String team_id, reason;
    private String text, apply_id;
    private int pageNum = 1;
    public static boolean isRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_corp);
        list = new ArrayList<>();
        initTitle();
        initNetwork();
        joincorp_result = (TextView) findViewById(R.id.joincorp_result);
        joincorp_listview = (PullToRefreshListView) findViewById(R.id.joincorp_listview);
        joinCorpAdapter = new JoinCorpAdapter(this, list);
        joincorp_listview.setAdapter(joinCorpAdapter);
        joincorp_listview.setOnItemClickListener(this);
        joincorp_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                keword = "";
                team_speciality = "";
                team_state = "";
                user_num = "";
                pvince = "";
                pageNum = 1;
                getTeamlist();
            }

            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                pageNum++;
                getTeamlist();
            }
        });
        joinCorpAdapter.setOnRefreshListener(this);
        getTeamlist();
    }

    private void getTeamlist() {
        teamlist.sendPostRequest(Urls.Teamlist, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (pageNum == 1) {
                            list.clear();
                        }
                        jsonObject = jsonObject.optJSONObject("data");
                        JSONArray jsonArray = jsonObject.optJSONArray("list");
                        int search_result = Tools.StringToInt(jsonObject.optString("search_result"));
                        if (jsonArray != null) {
                            int length = jsonArray.length();
                            if (length > 0) {
                                for (int i = 0; i < length; i++) {
                                    joincorp_result.setVisibility(View.GONE);
                                    JSONObject object = jsonArray.optJSONObject(i);
                                    JoinCorpInfo joinCorpInfo = new JoinCorpInfo();
                                    joinCorpInfo.setTeam_id(object.getString("team_id"));
                                    joinCorpInfo.setApply_id(object.getString("apply_id"));
                                    joinCorpInfo.setUser_num(object.getString("user_num"));
                                    joinCorpInfo.setCaptain(object.getString("captain"));
                                    joinCorpInfo.setTeam_name(object.getString("team_name"));
                                    joinCorpInfo.setTeam_code(object.getString("team_code"));
                                    joinCorpInfo.setTeam_img(object.getString("team_img"));
                                    joinCorpInfo.setMobile(object.getString("mobile"));
                                    joinCorpInfo.setTask_num(object.getString("task_num"));
                                    joinCorpInfo.setProvince(object.getString("province"));
                                    joinCorpInfo.setState(object.getString("state"));
                                    joinCorpInfo.setRefuse_num(object.getString("refuse_num"));
                                    joinCorpInfo.setSpeciality(object.getJSONArray("speciality"));
                                    joinCorpInfo.setChatInfo(object.getJSONArray("reply"));
                                    joinCorpInfo.setAuth_status(object.getString("auth_status"));
                                    list.add(joinCorpInfo);
                                }
                                joincorp_listview.onRefreshComplete();
                                if (length < 15) {
                                    joincorp_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                } else {
                                    joincorp_listview.setMode(PullToRefreshBase.Mode.BOTH);
                                }
                                if (joinCorpAdapter != null) {
                                    joinCorpAdapter.notifyDataSetChanged();
                                }
                                joincorp_listview.onRefreshComplete();
                                if (search_result == 1) {
                                    joincorp_result.setVisibility(View.GONE);
                                } else {
                                    if (pageNum == 1)
                                        joincorp_result.setVisibility(View.VISIBLE);
                                }
                            } else {
                                if (pageNum == 1)
                                    joincorp_result.setVisibility(View.VISIBLE);
                            }
                        } else {
                            if (pageNum == 1)
                                joincorp_result.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Tools.showToast(JoinCorpActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(JoinCorpActivity.this, getResources().getString(R.string.network_error));
                }
                joincorp_listview.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                joincorp_listview.onRefreshComplete();
                Tools.showToast(JoinCorpActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AppInfo.REQUEST_CODE_FILTER) {
            keword = data.getStringExtra("keword");
            team_speciality = data.getStringExtra("team_speciality");
            team_state = data.getStringExtra("team_state");
            user_num = data.getStringExtra("user_num");
            pvince = data.getStringExtra("pvince");
            pageNum = 1;
            getTeamlist();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (joinCorpAdapter != null) {
            if (joinCorpAdapter.isClick()) {
                JoinCorpInfo joinCorpInfo = list.get(--position);
                team_id = joinCorpInfo.getTeam_id();
                CloseTaskDialog.showDialog(this, "您需要发送验证申请，等对方通过", "发送", "你好！我是 " +
                        AppInfo.getName(this) + "...", new CloseTaskDialog.OnCloseTaskDialogListener() {
                    @Override
                    public void sumbit(String edittext) {
                        reason = edittext;
                        applyToTeam();
                    }
                });
            }
        }
    }

    private void applyToTeam() {
        applyToTeam.sendPostRequest(Urls.ApplyToTeam, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Tools.showToast(JoinCorpActivity.this, "申请成功");
                        getTeamlist();
                    } else {
                        Tools.showToast(JoinCorpActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(JoinCorpActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(JoinCorpActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    @Override
    public void onRefresh() {
        getTeamlist();
    }
}
