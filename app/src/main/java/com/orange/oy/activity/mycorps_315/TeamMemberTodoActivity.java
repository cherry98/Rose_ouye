package com.orange.oy.activity.mycorps_315;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.TaskillustratesActivity;
import com.orange.oy.activity.TaskitemDetailActivity_12;
import com.orange.oy.adapter.mycorps_314.TeamMemberTodoAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CloseTaskDialog;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.mycorps.CorpGrabDetailInfo;
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


/***
 *  战队任务 --- 队员待执行列表
 */
public class TeamMemberTodoActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener,
        TeamMemberTodoAdapter.TeammemberTodoInterface {

    private AppTitle appTitle;
    private PullToRefreshLayout refreshLayout;
    private TeamMemberTodoAdapter teamMemberTodoAdapter;
    private MyListView corpsprice_listview;
    private NetworkConnection waitExecuteOutlet, acceptTeamTask, abandonTeamTask;
    private String projectname, projectid, package_team_id, package_id;
    private String storeid, reason, storeidlist, accessed_num, team_id;//分配人员传参
    private Intent data;
    private TextView corpsprice_person, corpsprice_time, corpsprice_period, corpsprice_total, corpsprice_total_adjust;
    private ArrayList<CorpGrabDetailInfo> list;
    private String type;
    private int selPosition = -1;


    public void onBack() {
        baseFinish();
    }

    private void initTitle() {
        appTitle = (AppTitle) findViewById(R.id.taskILL_title);
        appTitle.settingName(projectname); //项目的名字
        appTitle.showBack(this);
        appTitle.showBack(new AppTitle.OnBackClickForAppTitle() {
            public void onBack() {
                baseFinish();
            }
        });
        settingDel1();
    }

    private void settingDel1() {
        appTitle.hideExit();
        if (teamMemberTodoAdapter != null) {
            teamMemberTodoAdapter.setDelet(false);
        }
        appTitle.settingExit("放弃任务", new AppTitle.OnExitClickForAppTitle() {
            public void onExit() {
                settingDel2();
            }
        });
    }

    private void settingDel2() {
        appTitle.hideIllustrate();
        if (teamMemberTodoAdapter != null) {
            teamMemberTodoAdapter.setDelet(true);
        }
        appTitle.settingExit("取消放弃", new AppTitle.OnExitClickForAppTitle() {
            public void onExit() {
                settingDel1();
            }
        });
    }

    private void initNetwork() {
        waitExecuteOutlet = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(TeamMemberTodoActivity.this));
                params.put("token", Tools.getToken());
                params.put("package_team_id", package_team_id);
                Tools.d(params.toString());
                return params;
            }
        };

        acceptTeamTask = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(TeamMemberTodoActivity.this));
                params.put("storied", storeid);    //  网点id  storied
                return params;
            }
        };
        abandonTeamTask = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(TeamMemberTodoActivity.this));
                params.put("storied", storeid);    //  网点id
                params.put("reason", reason); //放弃任务的原因【必填】
                Tools.d(params.toString());
                return params;
            }
        };

    }

    public static boolean isfreshing;

    @Override
    protected void onResume() {
        super.onResume();
        if (isfreshing) {
            getData();
            isfreshing = false;
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (waitExecuteOutlet != null) {
            waitExecuteOutlet.stop(Urls.WaitExecuteOutlet);
        }
        if (acceptTeamTask != null) {
            acceptTeamTask.stop(Urls.AcceptTeamTask);
        }
        if (abandonTeamTask != null) {
            abandonTeamTask.stop(Urls.AbandonTeamTask);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teammember_todo);

        data = getIntent();
        projectname = data.getStringExtra("projectname");
        team_id = data.getStringExtra("team_id");
        projectid = data.getStringExtra("projectid");
        package_team_id = data.getStringExtra("package_team_id");
        package_id = data.getStringExtra("package_id");
        type = data.getStringExtra("type"); // "type":"类型，1为众包显示金额，2为分包不显示金额",
        list = new ArrayList<>();
        initTitle();
        initView();
        initNetwork();
        isfreshing = true;
        refreshLayoutListener();
        teamMemberTodoAdapter = new TeamMemberTodoAdapter(this, list, type);
        corpsprice_listview.setAdapter(teamMemberTodoAdapter);
        teamMemberTodoAdapter.setTeammemberTodoListener(this);
        findViewById(R.id.corpsprice_standard).setOnClickListener(this);
        findViewById(R.id.corpsprice_preview).setOnClickListener(this);
    }


    private void refreshLayoutListener() {
        refreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                refreshData();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                if (refreshLayout != null) {
                    refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            }
        });
        refreshLayout.setCompleteListener(new PullToRefreshLayout.OnRetreshComplentListener() {
            @Override
            public void OnComplete() {
            }
        });
    }

    private void refreshData() {
        getData();
    }

    private void initView() {
        ((TextView) findViewById(R.id.corpsprice_name)).setText(projectname);
        refreshLayout = (PullToRefreshLayout) findViewById(R.id.refresh_view);
        corpsprice_listview = (MyListView) findViewById(R.id.statelistview);
        corpsprice_person = (TextView) findViewById(R.id.corpsprice_person);
        corpsprice_time = (TextView) findViewById(R.id.corpsprice_time);
        corpsprice_period = (TextView) findViewById(R.id.corpsprice_period);
        corpsprice_total = (TextView) findViewById(R.id.corpsprice_total);
        corpsprice_total_adjust = (TextView) findViewById(R.id.corpsprice_total_adjust);
        refreshLayout = (PullToRefreshLayout) findViewById(R.id.refresh_view);
    }

    private void getData() {
        waitExecuteOutlet.sendPostRequest(Urls.WaitExecuteOutlet, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                storeidlist = null;
                if (!list.isEmpty()) {
                    list.clear();
                }
                if (refreshLayout != null) {
                    refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (!list.isEmpty()) {
                            list.clear();
                        }

                        if (!jsonObject.isNull("data")) {

                            jsonObject = jsonObject.optJSONObject("data");
                            JSONObject project_info = jsonObject.getJSONObject("project_info");
                            corpsprice_time.setText(project_info.getString("begin_date") + "-" + project_info.getString("end_date") + "可执行");
                            corpsprice_period.setText("审核周期：" + project_info.getString("check_time") + "天");
                            corpsprice_person.setText(project_info.getString("project_person"));
                            //  jsonObject.is
                            String project_type = project_info.getString("project_type");
//                            JSONObject num_tree = jsonObject.getJSONObject("num_tree");
//                            String total_outlet = num_tree.getString("total_outlet");//总的网点数量
//                            String distribution_outlet = num_tree.getString("distribution_outlet");//待分配网点数
//                            String wait_exe_outlet = num_tree.getString("wait_exe_outlet");//待执行网点数
//                            String execution_outlet = num_tree.getString("execution_outlet");//执行中网点数
//                            String check_outlet = num_tree.getString("check_outlet");//审核中网点数
//                            String unpass_outlet = num_tree.getString("unpass_outlet");//未通过网点数
//                            String pass_outlet = num_tree.getString("pass_outlet");//已通过网点数
                            if ("1".equals(project_info.getString("standard_state"))) {
                                findViewById(R.id.corpsprice_standard).setVisibility(View.VISIBLE);
                                findViewById(R.id.corpsprice_standard).setOnClickListener(TeamMemberTodoActivity.this);
                            } else {
                                findViewById(R.id.corpsprice_standard).setVisibility(View.GONE);
                            }
                            JSONObject exe_info = jsonObject.getJSONObject("exe_info");
                            String is_record = exe_info.getString("is_record");
                            String photo_compression = exe_info.getString("photo_compression");
                            String is_watermark = exe_info.getString("is_watermark");
                            String code = exe_info.getString("code");
                            String brand = exe_info.getString("brand");
                            String is_takephoto = exe_info.getString("is_takephoto");
                            String position_limit = exe_info.getString("position_limit");
                            String limit_province = exe_info.getString("limit_province");
                            String limit_city = exe_info.getString("limit_city");
                            String limit_county = exe_info.getString("limit_county");
                            JSONArray jsonArray = jsonObject.optJSONArray("list");
                            if (jsonArray != null) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    CorpGrabDetailInfo corpGrabDetailInfo = new CorpGrabDetailInfo();
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String isdetail = object.getString("isdetail");
                                    String timeDetail = "";
                                    if ("0".equals(isdetail)) {
                                        String[] datelist = object.getString("datelist").replaceAll("\\[\"", "").replaceAll("\"]",
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
                                            String date = object.getString("date" + index);
                                            if (!TextUtils.isEmpty(date) && !"null".equals(date)) {
                                                String detailtemp = object.getString("details" + index);
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
                                    corpGrabDetailInfo.setTimeDetail(timeDetail);
                                    corpGrabDetailInfo.setOutlet_id(object.getString("outlet_id"));
                                    corpGrabDetailInfo.setOutlet_address(object.getString("outlet_address"));
                                    corpGrabDetailInfo.setOutlet_name(object.getString("outlet_name"));
                                    corpGrabDetailInfo.setOutlet_num(object.getString("outlet_num"));
                                    corpGrabDetailInfo.setExe_state(object.getString("exe_state"));
                                    corpGrabDetailInfo.setAccessed_name(object.getString("accessed_name"));
                                    corpGrabDetailInfo.setAccessed_num(object.getString("accessed_num"));
                                    corpGrabDetailInfo.setIs_desc(object.getString("is_desc"));
                                    corpGrabDetailInfo.setIs_exe(object.getString("is_exe"));
                                    corpGrabDetailInfo.setPrimary(object.getString("money"));
                                    corpGrabDetailInfo.setCurrent(object.getString("money"));
                                    corpGrabDetailInfo.setConfirm_time(object.getString("confirm_time"));
                                    corpGrabDetailInfo.setIs_record(is_record);
                                    corpGrabDetailInfo.setPhoto_compression(photo_compression);
                                    corpGrabDetailInfo.setIs_watermark(is_watermark);
                                    corpGrabDetailInfo.setCode(code);
                                    corpGrabDetailInfo.setBrand(brand);
                                    corpGrabDetailInfo.setIs_takephoto(is_takephoto);
                                    corpGrabDetailInfo.setPosition_limit(position_limit);
                                    corpGrabDetailInfo.setLimit_province(limit_province);
                                    corpGrabDetailInfo.setLimit_city(limit_city);
                                    corpGrabDetailInfo.setLimit_province(limit_county);
                                    corpGrabDetailInfo.setProject_type(project_type);
                                    JSONObject refuse_reason = object.optJSONObject("refuse_reason");
                                    if (refuse_reason != null) {
                                        corpGrabDetailInfo.setIs_haveReason(true);
                                        corpGrabDetailInfo.setUser_name(refuse_reason.getString("user_name"));
                                        corpGrabDetailInfo.setCreate_time(refuse_reason.getString("create_time"));
                                        corpGrabDetailInfo.setReason(refuse_reason.getString("reason"));
                                    } else {
                                        corpGrabDetailInfo.setIs_haveReason(false);
                                    }
                                    list.add(corpGrabDetailInfo);
                                }
                                if (teamMemberTodoAdapter != null) {
                                    teamMemberTodoAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    } else {
                        Tools.showToast(TeamMemberTodoActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TeamMemberTodoActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(TeamMemberTodoActivity.this, getResources().getString(R.string.network_volleyerror));
                if (refreshLayout != null) {
                    refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            }
        });
    }

    private void delete() {
        abandonTeamTask.sendPostRequest(Urls.AbandonTeamTask, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    if ("200".equals(jsonObject.getString("code"))) {
                        Tools.showToast(TeamMemberTodoActivity.this, jsonObject.getString("msg"));
                        list.remove(selPosition);
                        teamMemberTodoAdapter.notifyDataSetChanged();
                    } else {
                        Tools.showToast(TeamMemberTodoActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(TeamMemberTodoActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.corpsprice_standard: {
                Intent intent = new Intent(TeamMemberTodoActivity.this, TaskillustratesActivity.class);
                intent.putExtra("projectid", projectid);
                intent.putExtra("projectname", projectname);
                intent.putExtra("isShow", "0");//是否显示不再显示开始执行按钮
                startActivity(intent);
            }
            break;

            case R.id.corpsprice_preview: {
                Intent intent = new Intent(this, TaskitemDetailActivity_12.class);
                intent.putExtra("id", data.getStringExtra("id"));//网点id
                intent.putExtra("projectname", projectname);
                intent.putExtra("store_name", "网点名称");
                intent.putExtra("store_num", "网点编号");
                intent.putExtra("province", "");
                intent.putExtra("city", "");
                intent.putExtra("project_id", projectid);
                intent.putExtra("photo_compression", data.getStringExtra("photo_compression"));
                intent.putExtra("is_record", data.getStringExtra("is_record"));
                intent.putExtra("is_watermark", data.getStringExtra("is_watermark"));//int
                intent.putExtra("code", data.getStringExtra("code"));
                intent.putExtra("brand", data.getStringExtra("brand"));
                intent.putExtra("is_takephoto", data.getStringExtra("is_takephoto"));//String
                intent.putExtra("project_type", "1");
                intent.putExtra("is_desc", "");
                intent.putExtra("index", "0");
                startActivity(intent);
            }
            break;
            case R.id.tasknumview: {   //点击形状树
                Intent intent = new Intent(this, TeamExecuteDetailsActivity.class);
                intent.putExtra("package_team_id", package_team_id);
                startActivity(intent);
            }
            break;
        }
    }

    private void AcceptTeamTask() {
        acceptTeamTask.sendPostRequest(Urls.AcceptTeamTask, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        getData();
                        Tools.showToast(TeamMemberTodoActivity.this, jsonObject.getString("msg"));
                    } else {
                        Tools.showToast(TeamMemberTodoActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TeamMemberTodoActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(TeamMemberTodoActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    @Override
    public void onCarryclick(int position) {
        CorpGrabDetailInfo corpGrabDetailInfo = list.get(position);
        if (corpGrabDetailInfo.getIs_exe().equals("0")) {//不可执行
            ConfirmDialog.showDialog(this, "提示", 3, "还未到执行时间，不可执行", "", "我知道了", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                public void leftClick(Object object) {
                }

                public void rightClick(Object object) {
                }
            }).goneLeft();
            return;
        }
        Intent intent = new Intent(this, TaskitemDetailActivity_12.class);
        intent.putExtra("id", corpGrabDetailInfo.getOutlet_id());
        intent.putExtra("projectname", projectname);
        intent.putExtra("store_name", corpGrabDetailInfo.getOutlet_name());
        intent.putExtra("store_num", corpGrabDetailInfo.getOutlet_num());
        intent.putExtra("project_id", projectid);
        intent.putExtra("photo_compression", corpGrabDetailInfo.getPhoto_compression());
        intent.putExtra("is_record", corpGrabDetailInfo.getIs_record());
        intent.putExtra("is_watermark", corpGrabDetailInfo.getIs_watermark());
        intent.putExtra("project_type", corpGrabDetailInfo.getProject_type());
        intent.putExtra("code", corpGrabDetailInfo.getCode());
        intent.putExtra("brand", corpGrabDetailInfo.getBrand());
        intent.putExtra("is_takephoto", corpGrabDetailInfo.getIs_takephoto());//String
        startActivity(intent);
    }

    @Override
    public void onAccept(int position, String s) {
        storeid = s;
        AcceptTeamTask();
    }

    @Override
    public void Delete(int position, String s) {
        storeid = s;
        //  CorpGrabDetailInfo corpGrabDetailInfo = list.get(position);
        selPosition = position;
        CloseTaskDialog.showDialog2(this, "放弃任务原因", "完成", "请填写放弃任务原因", new CloseTaskDialog.OnCloseTaskDialogListener() {
            @Override
            public void sumbit(String edittext) {
                reason = edittext;
                delete();
            }
        });
    }
}
