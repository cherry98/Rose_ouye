package com.orange.oy.activity.mycorps_315;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.BrowserActivity;
import com.orange.oy.activity.TaskFinishActivity;
import com.orange.oy.activity.TaskillustratesActivity;
import com.orange.oy.activity.TaskitemDetailActivity_12;
import com.orange.oy.activity.mycorps_314.TeammemberActivity;
import com.orange.oy.adapter.mycorps_314.TeamtaskprojectAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.mycorps.ProjectStateInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.PullToRefreshLayout;
import com.orange.oy.view.TasknumberTreeView;
import com.sobot.chat.SobotApi;
import com.sobot.chat.api.model.Information;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/***
 * 队长 and 队员的  *******项目状态*********  有价格  and  无价格
 */
public class TeamtaskProjectStateActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener,
        TeamtaskprojectAdapter.AbandonUnpass {

    private AppTitle taskILL_title;
    private PullToRefreshLayout refreshLayout;
    private TeamtaskprojectAdapter teamtaskprojectAdapter;
    private ListView statelistview;
    // private NetworkView lin_Nodata;
    private NetworkConnection outletStateList, abandonUnpassOutlet, singleDistribution;
    private String package_team_id;
    private Intent data;
    private ArrayList<ProjectStateInfo> projectStateInfoList = new ArrayList<>();
    private int page = 1;
    private String storeid, team_id, identity;

    @Override
    protected void onStop() {
        super.onStop();
        if (outletStateList != null) {
            outletStateList.stop(Urls.OutLetState);
        }
        if (abandonUnpassOutlet != null) {
            abandonUnpassOutlet.stop(Urls.AbandonUnpassOutlet);
        }
        if (singleDistribution != null) {
            singleDistribution.stop(Urls.SingleDistribution);
        }

    }

    private void initNetworkConnection() {
        outletStateList = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(TeamtaskProjectStateActivity.this));
                params.put("package_team_id", package_team_id);    //  战队和网点包关系id【必传】
                Tools.d(params.toString());
                return params;
            }
        };

        abandonUnpassOutlet = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(TeamtaskProjectStateActivity.this));
                params.put("storeid", storeid);    //  网点id
                Tools.d(params.toString());
                return params;
            }
        };
        singleDistribution = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(TeamtaskProjectStateActivity.this));
                params.put("storeid", storeid);
                params.put("accessed_num", accessed_num);
                params.put("team_id", team_id);
                return params;
            }
        };
    }

    public void onBack() {
        baseFinish();
    }

    private void initTitle() {

        taskILL_title = (AppTitle) findViewById(R.id.taskILL_title);
        taskILL_title.settingName(getResources().getString(R.string.taskILL));
        taskILL_title.showBack(this);
        taskILL_title.showIllustrate(R.mipmap.kefu, new AppTitle.OnExitClickForAppTitle() {
            @Override
            public void onExit() {
                Information info = new Information();
                info.setAppkey(Urls.ZHICHI_KEY);
                info.setColor("#FFFFFF");
                if (TextUtils.isEmpty(AppInfo.getKey(TeamtaskProjectStateActivity.this))) {
                    info.setUname("游客");
                } else {
                    String netHeadPath = AppInfo.getUserImagurl(TeamtaskProjectStateActivity.this);
                    info.setFace(netHeadPath);
                    info.setUid(AppInfo.getKey(TeamtaskProjectStateActivity.this));
                    info.setUname(AppInfo.getUserName(TeamtaskProjectStateActivity.this));
                }
                SobotApi.startSobotChat(TeamtaskProjectStateActivity.this, info);
            }
        });
    }

    private TasknumberTreeView tasknumview;
    private String total_outlet, distribution_outlet, wait_exe_outlet, execution_outlet, check_outlet, unpass_outlet, pass_outlet;
    private String accessed_num;
    private boolean IsTop; //是否为队长

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teamtask_project_state);
        data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        //  team_id = data.getStringExtra("team_id");
        package_team_id = getIntent().getStringExtra("package_team_id");
        identity = getIntent().getStringExtra("identity");
        initTitle();
        initNetworkConnection();
        initData();
        onItemClick();
        if ("1".equals(identity)) {//队长才能看任务协议
            tv_btn_task.setVisibility(View.VISIBLE);
        }
        teamtaskprojectAdapter = new TeamtaskprojectAdapter(this, projectStateInfoList, type, identity);
        statelistview.setAdapter(teamtaskprojectAdapter);
        teamtaskprojectAdapter.setAbandonButtonListener(this);
        findViewById(R.id.corpsprice_bounty).setOnClickListener(this);
        refreshLayoutListener();

    }

    /**
     * @param listView 动态测量listview的高度
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {

        if (teamtaskprojectAdapter == null) {
            return;
        }

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;

        int totalHeight = 0;
        int listViewWidth = w_screen - dip2px(this, 16);                                         //listView在布局时的宽度
        int widthSpec = View.MeasureSpec.makeMeasureSpec(listViewWidth, View.MeasureSpec.AT_MOST);
        for (int i = 0; i < teamtaskprojectAdapter.getCount(); i++) {
            View listItem = teamtaskprojectAdapter.getView(i, null, listView);
            listItem.measure(widthSpec, 0);

            int itemHeight = listItem.getMeasuredHeight();
            totalHeight += itemHeight;
        }
        // 减掉底部分割线的高度
        int historyHeight = totalHeight
                + (listView.getDividerHeight() * teamtaskprojectAdapter.getCount() - 1);


        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = historyHeight
                + (listView.getDividerHeight() * (teamtaskprojectAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    protected void onResume() {
        getData();
        super.onResume();
    }

    private TextView tv_totalmoney, tv_GiveMemberMoney, tv_name, tv_terrace, tv_time, tv_Auditcycle, tv_btn_task;
    private ImageView iv_standard;
    private View tasknumview_line;
    private String type, package_id;

    private void initData() {
        package_id = getIntent().getStringExtra("package_id");
        type = data.getStringExtra("type"); // "type":"类型，1为众包显示金额，2为分包不显示金额",
        refreshLayout = (PullToRefreshLayout) findViewById(R.id.refresh_view);
        tasknumview = (TasknumberTreeView) findViewById(R.id.tasknumview);
        tasknumview_line = findViewById(R.id.tasknumview_line);
        statelistview = (ListView) findViewById(R.id.statelistview);
        tv_totalmoney = (TextView) findViewById(R.id.tv_totalmoney);
        tv_GiveMemberMoney = (TextView) findViewById(R.id.tv_GiveMemberMoney);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_terrace = (TextView) findViewById(R.id.tv_terrace);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_Auditcycle = (TextView) findViewById(R.id.tv_Auditcycle);
        iv_standard = (ImageView) findViewById(R.id.iv_standard);
        tv_btn_task = (TextView) findViewById(R.id.tv_btn_task);
        tv_btn_task.setOnClickListener(this);
        tasknumview.setOnClickListener(this);
        findViewById(R.id.tv_preview).setOnClickListener(this);
        if (!TextUtils.isEmpty(identity)) {
            if (identity.equals("1")) {
                if ("1".equals(type)) {
                    tasknumview.setVisibility(View.VISIBLE);
                    tasknumview_line.setVisibility(View.VISIBLE);
                } else {
                    tasknumview.setVisibility(View.GONE);
                    tasknumview_line.setVisibility(View.GONE);
                }
                findViewById(R.id.teamtaskproject_headmoney).setVisibility(View.VISIBLE);
            } else {
                tasknumview.setVisibility(View.GONE);
                tasknumview_line.setVisibility(View.GONE);
                findViewById(R.id.teamtaskproject_headmoney).setVisibility(View.GONE);
            }
        }
    }

    private void refreshLayoutListener() {
        refreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                refreshData();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                //  page++;
                // getData();
                refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            }
        });
        refreshLayout.setCompleteListener(new PullToRefreshLayout.OnRetreshComplentListener() {
            @Override
            public void OnComplete() {
            }
        });
    }

    private void refreshData() {
        page = 1;
        getData();
    }

    private String projectid, project_name, standard_state;

    private void getData() {
        outletStateList.sendPostRequest(Urls.OutLetState, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (page == 1) {
                            if (!projectStateInfoList.isEmpty()) {
                                projectStateInfoList.clear();

                            }
                        }
                        if (refreshLayout != null) {
                            refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                            refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                        }

                        if (!jsonObject.isNull("data")) {
                            jsonObject = jsonObject.optJSONObject("data");

                            JSONObject object = jsonObject.optJSONObject("project_info");
                            projectid = object.optString("project_id");
                            team_id = object.optString("team_id");
                            project_name = object.optString("project_name");
                            standard_state = object.optString("standard_state");  // 是否有项目说明，1为有，0为没有

                            tv_name.setText(project_name);
                            tv_terrace.setText("商家：" + object.optString("project_person"));  // 发布商家
                            tv_time.setText(object.optString("begin_date") + "~" + object.optString("end_date"));
                            tv_Auditcycle.setText("审核周期：" + object.optString("check_time") + "天");  // 审核周期

                            Tools.d(projectid);
                            String project_person = object.optString("project_person");
                            String begin_date = object.optString("begin_date");
                            String end_date = object.optString("end_date");
                            String check_time = object.optString("check_time");
                            String project_type = object.optString("project_type");

                            if (standard_state.equals("0")) {
                                iv_standard.setVisibility(View.GONE);
                            } else {
                                iv_standard.setVisibility(View.VISIBLE);
                                iv_standard.setOnClickListener(TeamtaskProjectStateActivity.this);
                            }
                            JSONObject object2 = jsonObject.optJSONObject("exe_info");
                            String is_record = object2.optString("is_record");
                            String photo_compression = object2.optString("photo_compression");
                            String is_watermark = object2.optString("is_watermark");
                            String code = object2.optString("code");
                            String brand = object2.optString("brand");
                            String is_takephoto = object2.optString("is_takephoto");
                            String position_limit = object2.optString("position_limit");
                            String limit_province = object2.optString("limit_province");
                            String limit_city = object2.optString("limit_city");
                            String limit_county = object2.optString("limit_county");

                            JSONObject object3 = jsonObject.optJSONObject("num_tree");


                            total_outlet = object3.optString("total_outlet"); // 总的网点数量
                            distribution_outlet = object3.optString("distribution_outlet");  // 待分配网点数
                            wait_exe_outlet = object3.optString("wait_exe_outlet");  // 待执行网点数
                            execution_outlet = object3.optString("execution_outlet");  // 执行中网点数
                            check_outlet = object3.optString("check_outlet");  // 审核中网点数
                            unpass_outlet = object3.optString("unpass_outlet"); // 未通过网点数
                            pass_outlet = object3.optString("pass_outlet");  //已通过网点数
                            tv_totalmoney.setText(object3.optString("total_money"));
                            tv_GiveMemberMoney.setText(object3.optString("total_adjust_money"));//分给队员的总金额

                            // * @param n5   已通过
                            // * @param n6   未通过
                            tasknumview.setting(2, total_outlet, distribution_outlet, wait_exe_outlet, execution_outlet, check_outlet,
                                    pass_outlet, unpass_outlet);
                            JSONArray jsonArray = jsonObject.optJSONArray("list");
                            if (jsonArray != null) {
                                statelistview.setVisibility(View.VISIBLE);
                                //  lin_Nodata.setVisibility(View.GONE);
                                int length = jsonArray.length();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object4 = jsonArray.optJSONObject(i);
                                    ProjectStateInfo projectStateInfo = new ProjectStateInfo();
                                    projectStateInfo.setProjectid(projectid);
                                    projectStateInfo.setProject_name(project_name);
                                    projectStateInfo.setState(standard_state);
                                    projectStateInfo.setProject_person(project_person);
                                    projectStateInfo.setBegin_date(begin_date);
                                    projectStateInfo.setEnd_date(end_date);
                                    projectStateInfo.setCheck_time(check_time);
                                    projectStateInfo.setProject_type(project_type); // 项目类型，1为有网点，5为无网点

                                    projectStateInfo.setIs_record(is_record);   //是否全程录音（1为是，0为否）
                                    projectStateInfo.setPhoto_compression(photo_compression); // 照片清晰度
                                    projectStateInfo.setIs_watermark(is_watermark);// 是否添加照片水印
                                    projectStateInfo.setCode(code);   //  项目代号
                                    projectStateInfo.setBrand(brand);
                                    projectStateInfo.setIs_takephoto(is_takephoto);  //是否支持连续拍照（1为是，0为否）"
                                    projectStateInfo.setPosition_limit(position_limit); //  无店单项目是否有定位限制，1为有，0为无
                                    projectStateInfo.setLimit_province(limit_province);//  定位限制省份
                                    projectStateInfo.setLimit_city(limit_city);
                                    projectStateInfo.setLimit_county(limit_county);// 定位限制区域

                                    projectStateInfo.setUnpass_reason(object4.getString("unpass_reason"));
                                    projectStateInfo.setOutlet_id(object4.optString("outlet_id"));
                                    projectStateInfo.setOutlet_name(object4.optString("outlet_name"));
                                    projectStateInfo.setOutlet_num(object4.optString("outlet_num"));
                                    projectStateInfo.setOutlet_address(object4.optString("outlet_address"));
                                    projectStateInfo.setState(object4.optString("state")); //"状态，-1为上传中，0为审核中，2为未通过，3为已通过
                                    projectStateInfo.setAccessed_num(object4.optString("accessed_num"));//执行人员账号

                                    projectStateInfo.setAccessed_name(object4.optString("accessed_name")); //执行人员昵称
                                    projectStateInfo.setIs_exe(object4.optString("is_exe"));//是否可执行，1为可执行，0为不可执行
                                    projectStateInfo.setIs_abandon(object4.optString("is_abandon")); //是否放弃了，1为是，0为否
                                    projectStateInfo.setIs_distribute(object4.optString("is_distribute")); //是否可分配，1为可以，0为不可以
                                    projectStateInfo.setIs_desc(object4.optString("is_desc")); // 是否有网点说明，1为有，0为无
                                    projectStateInfo.setMoney(object4.optString("money"));
                                    projectStateInfo.setConfirm_time(object4.optString("confirm_time"));//等待确认时间，单位为分钟
                                    String isdetail = object4.getString("isdetail");
                                    String timeDetail = "";
                                    if ("0".equals(isdetail)) {
                                        String[] datelist = object4.getString("datelist").replaceAll("\\[\"", "").replaceAll("\"]",
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
                                            String date = object4.getString("date" + index);
                                            if (!TextUtils.isEmpty(date) && !"null".equals(date)) {
                                                String detailtemp = object4.getString("details" + index);
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
                                    projectStateInfo.setTimedetail(timeDetail);  // 1为有排程日期及时间，值为0时使用datelist中的时间
                                    projectStateInfoList.add(projectStateInfo);
                                }


                                if (teamtaskprojectAdapter != null) {
                                    teamtaskprojectAdapter.notifyDataSetChanged();
                                }
                                //动态测量listview的高度
                                setListViewHeightBasedOnChildren(statelistview);
                            } else {
                                statelistview.setVisibility(View.GONE);
                                // lin_Nodata.setVisibility(View.VISIBLE);
                                //  lin_Nodata.NoSearch("没有网点!");
                            }
                        }
                    } else {
                        Tools.showToast(TeamtaskProjectStateActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.d(e.toString());
                    Tools.showToast(TeamtaskProjectStateActivity.this, getResources().getString(R.string.network_error));
                }
            }

        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                //  lin_Nodata.NoNetwork();
                //  lin_Nodata.setVisibility(View.VISIBLE);
                if (refreshLayout != null) {
                    refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
                Tools.d(volleyError.toString());
                Tools.showToast(TeamtaskProjectStateActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.corpsprice_bounty: {//奖励金额页面说明
                String content = Urls.RewardInstructions + "team_id=" + team_id + "&usermobile=" + AppInfo.getName(this) +
                        "&project_id=" + projectid + "&package_id=" + package_id + "&token=" + Tools.getToken();
                Intent intent = new Intent(this, BrowserActivity.class);
                intent.putExtra("title", "奖励金额说明");
                intent.putExtra("flag", BrowserActivity.flag_question);
                intent.putExtra("content", content);
                startActivity(intent);
            }
            break;
            case R.id.iv_standard: {
                Intent intent = new Intent(TeamtaskProjectStateActivity.this, TaskillustratesActivity.class);
                intent.putExtra("projectid", projectid);
                intent.putExtra("projectname", project_name);
                intent.putExtra("isShow", "0");//是否显示不再显示开始执行按钮
                startActivity(intent);
            }
            break;
            case R.id.tv_btn_task: {  //任务协议
                Intent intent = new Intent(this, TaskProtocolActivity.class);
                intent.putExtra("team_id", team_id);
                intent.putExtra("project_id", projectid);
                intent.putExtra("package_id", package_id);
                intent.putExtra("type", "3");//从页面跳转
                intent.putExtra("package_team_id", package_team_id);
                startActivity(intent);
            }
            break;
            case R.id.tv_preview: {
                Intent intent = new Intent(this, TaskitemDetailActivity_12.class);
                intent.putExtra("id", projectid);//网点id
                intent.putExtra("projectname", project_name);
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
            case R.id.tasknumview: {  //队员执行明细
                Intent intent = new Intent(this, TeamExecuteDetailsActivity.class);
                intent.putExtra("package_team_id", package_team_id);
                startActivity(intent);
            }
            break;
        }
    }

    //单个网点人员分配
    private void singleDistribution() {
        singleDistribution.sendPostRequest(Urls.SingleDistribution, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Tools.showToast(TeamtaskProjectStateActivity.this, "操作成功");
                        getData();
                    } else {
                        Tools.showToast(TeamtaskProjectStateActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TeamtaskProjectStateActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(TeamtaskProjectStateActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private void abandonUnpass() {
        abandonUnpassOutlet.sendPostRequest(Urls.AbandonUnpassOutlet, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        getData();
                        Tools.showToast(TeamtaskProjectStateActivity.this, jsonObject.getString("msg"));
                    } else {
                        Tools.showToast(TeamtaskProjectStateActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TeamtaskProjectStateActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(TeamtaskProjectStateActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    @Override
    public void onAnondonclick(int position, String s) {
        storeid = s;
        ConfirmDialog.showDialog(this, "您是否要放弃任务？", true, new ConfirmDialog.OnSystemDialogClickListener() {
            @Override
            public void leftClick(Object object) {

            }

            @Override
            public void rightClick(Object object) {
                abandonUnpass();
            }
        });
    }

    @Override
    public void onDistribute(int position, String s) {
        ProjectStateInfo projectStateInfo = projectStateInfoList.get(position);
        storeid = s;
        Intent intent = new Intent(TeamtaskProjectStateActivity.this, TeammemberActivity.class);
        intent.putExtra("state", 3);
        intent.putExtra("team_id", team_id);
        intent.putExtra("package_team_id", package_team_id);
        intent.putExtra("project_id", projectid);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onRest(int position, String s) {
        storeid = s;
        ProjectStateInfo projectStateInfo = projectStateInfoList.get(position);
        Intent intent = new Intent(TeamtaskProjectStateActivity.this, TaskitemDetailActivity_12.class);
        intent.putExtra("id", projectStateInfo.getOutlet_id());
        intent.putExtra("projectname", projectStateInfo.getProject_name());
        intent.putExtra("store_name", projectStateInfo.getOutlet_name());
        intent.putExtra("store_num", projectStateInfo.getOutlet_num());
        intent.putExtra("project_id", projectStateInfo.getProjectid());
        intent.putExtra("photo_compression", projectStateInfo.getPhoto_compression());
        intent.putExtra("is_record", projectStateInfo.getIs_record());
        intent.putExtra("is_watermark", projectStateInfo.getIs_watermark() + "");//int
        intent.putExtra("code", projectStateInfo.getCode());
        intent.putExtra("project_type", projectStateInfo.getProject_type());
        intent.putExtra("brand", projectStateInfo.getBrand());
        intent.putExtra("is_takephoto", projectStateInfo.getIs_takephoto());//String
        startActivity(intent);
    }

    @Override
    public void onInfo(int position) {
        //“战队队员基本信息”页面；
        ProjectStateInfo projectStateInfo = projectStateInfoList.get(position);
        Intent intent = new Intent(this, TeamMemberInfoActivity.class);
        intent.putExtra("accessed_num", projectStateInfo.getAccessed_num());
        startActivity(intent);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && data != null) {
            if (resultCode == RESULT_OK) {
                accessed_num = data.getStringExtra("accessed_num");
                singleDistribution();
            }
        }
    }

    // ************** 3、点击店 / 网点 整行，系统跳转至查看任务详情页面；
    public void onItemClick() {
        statelistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TeamtaskProjectStateActivity.this, TaskFinishActivity.class);
                ProjectStateInfo projectStateInfo = projectStateInfoList.get(position);
                intent.putExtra("projectname", project_name);
                intent.putExtra("store_name", projectStateInfo.getOutlet_name());
                intent.putExtra("store_num", projectStateInfo.getOutlet_num());
                intent.putExtra("project_id", projectStateInfo.getProjectid());
                intent.putExtra("photo_compression", projectStateInfo.getPhoto_compression());
                intent.putExtra("store_id", projectStateInfo.getOutlet_id());
                intent.putExtra("state", "2");
                intent.putExtra("is_watermark", projectStateInfo.getIs_watermark());
                intent.putExtra("code", projectStateInfo.getCode());
                intent.putExtra("brand", projectStateInfo.getBrand());
                intent.putExtra("isAgain", false);
                startActivity(intent);
            }
        });
    }
}
