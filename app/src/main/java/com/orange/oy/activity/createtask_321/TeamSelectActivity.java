package com.orange.oy.activity.createtask_321;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.activity.createtask_317.AddPhoneActivity;
import com.orange.oy.activity.createtask_317.AddTagActivity;
import com.orange.oy.activity.mycorps_314.CorpsFilterActivity;
import com.orange.oy.activity.mycorps_314.JoinCorpActivity;
import com.orange.oy.activity.mycorps_314.MyCorpsActivity;
import com.orange.oy.adapter.TeamSelectAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.DotagDialog;
import com.orange.oy.info.MyteamNewfdInfo;
import com.orange.oy.info.mycorps.JoinCorpInfo;
import com.orange.oy.info.mycorps.MyCorpsInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.MyListView;
import com.orange.oy.view.PullToRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.orange.oy.R.id.apply_listview;
import static com.orange.oy.R.id.create_listview;
import static com.orange.oy.R.id.joincorp_listview;
import static com.orange.oy.R.id.joincorp_result;
import static com.orange.oy.R.id.mycrops_apply;
import static com.orange.oy.R.id.mycrops_apply_ly;
import static com.orange.oy.R.id.mycrops_create;
import static com.orange.oy.R.id.mycrops_create_ly;
import static com.orange.oy.R.id.teamtaskproject_headmoney;

/***
 *   V3.21  按战队挑选  and 筛选之后的结果
 */
public class TeamSelectActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {

    private AppTitle appTitle;

    private void initTitle() {
        appTitle = (AppTitle) findViewById(R.id.whom_title);
        appTitle.settingName("按战队挑选");
        appTitle.showBack(this);
        isFromCorp = getIntent().getBooleanExtra("isFromCorp", false);
        if (!isFromCorp) {
            appTitle.settingSearch(R.mipmap.round_notselect, null);
            appTitle.settingExit("全选", Color.parseColor("#A0A0A0"), null);
            appTitle.settingRightListener(onRightClickForAppTitle);
        }
    }

    public boolean isFromCorp;//
    private boolean isAll = false;
    private AppTitle.OnRightClickForAppTitle onRightClickForAppTitle = new AppTitle.OnRightClickForAppTitle() {
        public void onRightClick() {
            if (isAll) {
                appTitle.settingSearch(R.mipmap.round_notselect, null);
                for (int i = 0; i < list.size(); i++) {
                    MyCorpsInfo myCorpsInfo = list.get(i);
                    myCorpsInfo.setSelect(false);
                }
                teamSelectAdapter2.notifyDataSetChanged();
            } else {
                appTitle.settingSearch(R.mipmap.round_selected, null);
                for (int i = 0; i < list.size(); i++) {
                    MyCorpsInfo myCorpsInfo = list.get(i);
                    myCorpsInfo.setSelect(true);
                }
                teamSelectAdapter2.notifyDataSetChanged();
            }
            isAll = !isAll;
        }
    };

    private void initNetwork() {
        myTeams = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(TeamSelectActivity.this));
                params.put("token", Tools.getToken());
                return params;
            }
        };
        teamlist = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(TeamSelectActivity.this));
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

    private ScrollView scrollview;
    private MyListView list_view;
    private LinearLayout lin_team;
    private TeamSelectAdapter teamSelectAdapter, teamSelectAdapter2;
    private NetworkConnection myTeams, teamlist;
    private ArrayList<MyCorpsInfo> list;

    private TextView joincorp_result, tv_commit;
    private PullToRefreshListView joincorp_listview;
    private int pageNum = 1;
    private String Isvisible;  //1可见  2 不可见
    private String ischart; // 1是集图活动   2是任务模板
    private String invisible_type;
    private String invisible_team, outlet_package_type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_select);
        Isvisible = getIntent().getStringExtra("Isvisible");
        ischart = getIntent().getStringExtra("ischart");
        initTitle();
        initNetwork();
        list = new ArrayList<>();
        tv_commit = (TextView) findViewById(R.id.tv_commit);
        scrollview = (ScrollView) findViewById(R.id.scrollview);
        joincorp_result = (TextView) findViewById(R.id.joincorp_result);
        joincorp_listview = (PullToRefreshListView) findViewById(R.id.joincorp_listview);

        list_view = (MyListView) findViewById(R.id.list_view);
        lin_team = (LinearLayout) findViewById(R.id.lin_team);

        //2为谁不可见红包，3为谁可见红包
        if (!Tools.isEmpty(Isvisible)) {
            if (!Tools.isEmpty(ischart)) {
                if ("1".equals(ischart)) {
                    if (Isvisible.equals("1")) { //1可见  2 不可见
                        invisible_type = "3";
                    } else {
                        invisible_type = "2";
                    }
                } else {  //invisible_type	对谁可见的类型【必传】1为全部，2为仅自己可见，3为谁不可见任务，4为谁可见任务
                    if (Isvisible.equals("1")) { //1可见  2 不可见
                        invisible_type = "4";
                    } else {
                        invisible_type = "3";
                    }
                }
            }
        }
        lin_team.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //  系统跳转至“筛选”页面
                Intent intent = new Intent(TeamSelectActivity.this, CorpsFilterActivity.class);
                intent.putExtra("isTeam",1);
                startActivityForResult(intent, AppInfo.REQUEST_CODE_FILTER);
            }
        });
        teamSelectAdapter = new TeamSelectAdapter(this, list);
        list_view.setAdapter(teamSelectAdapter);
        getData();


        teamSelectAdapter2 = new TeamSelectAdapter(this, list);
        joincorp_listview.setAdapter(teamSelectAdapter2);


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

        onclick();
    }

    private String temp = "";

    private void onclick() {
        tv_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //提交按钮，区分是否从筛选过来的 ,isFromCorp是false的时候，是从筛选过来的
                // if (isFromCorp) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isSelect()) {
                        temp += list.get(i).getTeam_id() + ",";
                    }
                }
                if (!Tools.isEmpty(temp)) {
                    invisible_team = temp.substring(0, temp.length() - 1);
                }
                if (Tools.isEmpty(invisible_team)) {
                    Tools.showToast(TeamSelectActivity.this, "请选择战队~");
                    return;
                }


                //ischart  1是集图   2是任务模板  集图活动中没有按省份打包弹框
                if (!Tools.isEmpty(ischart)) {

                    if ("1".equals(ischart)) {  //直接提交
                        Intent intent = new Intent();
                        intent.putExtra("invisible_type", invisible_type);
                        intent.putExtra("invisible_team", invisible_team); //战队的id
                        setResult(AppInfo.REQUEST_CODE_ISVISIBLE, intent);
                        baseFinish();
                    } else {
                        DotagDialog.showDialog2(TeamSelectActivity.this, "保存为标签，下次可直接选用", false, "按省份打包", "按城市打包", new DotagDialog.OnDataUploadClickListener() {
                            @Override
                            public void firstClick() {
                                //按省份打包
                                outlet_package_type = "1";
                                //提交返回
                                Intent intent = new Intent();
                                intent.putExtra("invisible_type", invisible_type);
                                intent.putExtra("invisible_team", invisible_team); //战队的id
                                intent.putExtra("outlet_package_type", outlet_package_type);  // 网点打包类型（1：按省份打包；2：按城市打包）
                                setResult(AppInfo.REQUEST_CODE_ISVISIBLE, intent);
                                baseFinish();
                            }

                            @Override
                            public void secondClick() {
                                //按城市打包
                                outlet_package_type = "2";
                                //提交返回
                                Intent intent = new Intent();
                                intent.putExtra("invisible_type", invisible_type);
                                intent.putExtra("invisible_team", invisible_team); //战队的id
                                intent.putExtra("outlet_package_type", outlet_package_type);  // 网点打包类型（1：按省份打包；2：按城市打包）
                                setResult(AppInfo.REQUEST_CODE_ISVISIBLE, intent);
                                baseFinish();
                            }

                        });
                    }
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (myTeams != null) {
            myTeams.stop(Urls.MyTeams);
        }
        if (teamlist != null) {
            teamlist.stop(Urls.Teamlist);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBack() {
        baseFinish();
    }

    private String keword, team_speciality, team_state, user_num, pvince;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AppInfo.REQUEST_CODE_FILTER) {

            isFromCorp = false;
            appTitle.settingSearch(R.mipmap.round_notselect, null);
            appTitle.settingExit("全选", Color.parseColor("#A0A0A0"), null);
            appTitle.settingRightListener(onRightClickForAppTitle);

            keword = data.getStringExtra("keword");
            team_speciality = data.getStringExtra("team_speciality");
            team_state = data.getStringExtra("team_state");
            user_num = data.getStringExtra("user_num");
            pvince = data.getStringExtra("pvince");
            pageNum = 1;
            getTeamlist();
            onclick();
        }
    }

    private void getTeamlist() {
        scrollview.setVisibility(View.GONE);
        joincorp_listview.setVisibility(View.VISIBLE);
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
                                    MyCorpsInfo joinCorpInfo = new MyCorpsInfo();
                                    joinCorpInfo.setTeam_id(object.getString("team_id"));
                                    joinCorpInfo.setUser_num(object.getString("user_num"));
                                    joinCorpInfo.setTeam_name(object.getString("team_name"));
                                    joinCorpInfo.setTeam_img(object.getString("team_img"));
                                    if (isAll) {
                                        joinCorpInfo.setSelect(true);
                                    }
                                    list.add(joinCorpInfo);
                                }
                                joincorp_listview.onRefreshComplete();
                                if (length < 15) {
                                    joincorp_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                } else {
                                    joincorp_listview.setMode(PullToRefreshBase.Mode.BOTH);
                                }
                                if (teamSelectAdapter2 != null) {
                                    teamSelectAdapter2.notifyDataSetChanged();
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
                        Tools.showToast(TeamSelectActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TeamSelectActivity.this, getResources().getString(R.string.network_error));
                }
                joincorp_listview.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                joincorp_listview.onRefreshComplete();
                Tools.showToast(TeamSelectActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private void getData() {
        scrollview.setVisibility(View.VISIBLE);
        joincorp_listview.setVisibility(View.GONE);

        myTeams.sendPostRequest(Urls.MyTeams, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (!list.isEmpty()) {
                            list.clear();
                        }
                        jsonObject = jsonObject.optJSONObject("data");

                        JSONArray jsonArray = jsonObject.optJSONArray("build_list");//组建的战队信息
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                MyCorpsInfo myCorpsInfo = new MyCorpsInfo();
                                myCorpsInfo.setTeam_id(object.getString("team_id"));
                                myCorpsInfo.setTeam_img(object.getString("team_img"));
                                myCorpsInfo.setTeam_name(object.getString("team_name"));
                                myCorpsInfo.setUser_num(object.getString("user_num"));
                                myCorpsInfo.setApply_user_num(object.getString("apply_user_num"));
                                list.add(myCorpsInfo);
                            }

                        }
                        JSONArray jsonArray1 = jsonObject.optJSONArray("join_list");//加入的战队信息
                        if (jsonArray1 != null) {
                            for (int i = 0; i < jsonArray1.length(); i++) {
                                JSONObject object = jsonArray1.getJSONObject(i);
                                MyCorpsInfo myCorpsInfo = new MyCorpsInfo();
                                myCorpsInfo.setTeam_id(object.getString("team_id"));
                                myCorpsInfo.setTeam_img(object.getString("team_img"));
                                myCorpsInfo.setTeam_name(object.getString("team_name"));
                                myCorpsInfo.setUser_num(object.getString("user_num"));
                                list.add(myCorpsInfo);
                            }
                        }

                        if (list != null && list.size() > 0) {

                        } else {

                        }

                        if (teamSelectAdapter != null) {
                            teamSelectAdapter.notifyDataSetChanged();
                        }

                    } else {
                        Tools.showToast(TeamSelectActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TeamSelectActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(TeamSelectActivity.this, getResources().getString(R.string.network_volleyerror));

            }
        });
    }

}
