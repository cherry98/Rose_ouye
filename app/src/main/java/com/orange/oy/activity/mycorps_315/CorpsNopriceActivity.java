package com.orange.oy.activity.mycorps_315;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.TaskillustratesActivity;
import com.orange.oy.activity.TaskitemDetailActivity_12;
import com.orange.oy.activity.mycorps_314.TeammemberActivity;
import com.orange.oy.adapter.mycorps_314.CorpNopriceAdapter;
import com.orange.oy.allinterface.OnItemCheckListener;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CorpApplyDialog;
import com.orange.oy.info.mycorps.CorpGrabDetailInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.MyListView;
import com.orange.oy.view.ObservableScrollView;
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

/**
 * 战队任务执行===无价格
 */
public class CorpsNopriceActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener, OnItemCheckListener, AdapterView.OnItemClickListener {
    private void initTitle(String name) {
        AppTitle appTitle = (AppTitle) findViewById(R.id.corpsnoprice_title);
        appTitle.settingName(name);
        appTitle.showBack(this);
        appTitle.showIllustrate(R.mipmap.kefu, new AppTitle.OnExitClickForAppTitle() {
            @Override
            public void onExit() {
                Information info = new Information();
                info.setAppkey(Urls.ZHICHI_KEY);
                info.setColor("#FFFFFF");
                if (TextUtils.isEmpty(AppInfo.getKey(CorpsNopriceActivity.this))) {
                    info.setUname("游客");
                } else {
                    String netHeadPath = AppInfo.getUserImagurl(CorpsNopriceActivity.this);
                    info.setFace(netHeadPath);
                    info.setUid(AppInfo.getKey(CorpsNopriceActivity.this));
                    info.setUname(AppInfo.getUserName(CorpsNopriceActivity.this));
                }
                SobotApi.startSobotChat(CorpsNopriceActivity.this, info);
            }
        });
    }

    private void initNetwork() {
        waitExecuteOutlet = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(CorpsNopriceActivity.this));
                params.put("token", Tools.getToken());
                params.put("package_team_id", package_team_id);
                return params;
            }
        };
        singleDistribution = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(CorpsNopriceActivity.this));
                params.put("storeid", storeid);
                params.put("accessed_num", accessed_num);
                params.put("team_id", team_id);
                return params;
            }
        };
        multipleDistribution = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(CorpsNopriceActivity.this));
                params.put("storeidlist", storeidlist);
                params.put("accessed_num", accessed_num);
                params.put("team_id", team_id);
                return params;
            }
        };
    }

    private String projectname, projectid, package_team_id;
    private CorpNopriceAdapter corpNopriceAdapter;
    private MyListView corpsnoprice_listview;
    private NetworkConnection waitExecuteOutlet, singleDistribution, multipleDistribution;
    private TextView corpsnoprice_person, corpsnoprice_time, corpsnoprice_period;
    private TasknumberTreeView corpsnoprice_tree;
    private String storeid, storeidlist, accessed_num, team_id;//分配人员传参
    private PullToRefreshLayout refreshLayout;
    private ArrayList<CorpGrabDetailInfo> list;
    private ArrayList<CorpGrabDetailInfo> list_selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corps_noprice);
        list_selected = new ArrayList<>();
        list = new ArrayList<>();
        Intent data = getIntent();
        projectname = data.getStringExtra("projectname");
        team_id = data.getStringExtra("team_id");
        projectid = data.getStringExtra("projectid");
        package_team_id = data.getStringExtra("package_team_id");
        initTitle(projectname);
        initNetwork();
        initView();
        corpNopriceAdapter = new CorpNopriceAdapter(this, list);
        corpsnoprice_listview.setAdapter(corpNopriceAdapter);
        corpNopriceAdapter.setOnItemCheckListener(this);
        corpsnoprice_listview.setOnItemClickListener(this);
        findViewById(R.id.corpsnoprice_dis).setOnClickListener(this);
        findViewById(R.id.corpsnoprice_selected).setOnClickListener(this);
        findViewById(R.id.corpsnoprice_standard).setOnClickListener(this);
        findViewById(R.id.corpsnoprice_preview).setOnClickListener(this);
        ((ObservableScrollView) findViewById(R.id.scv)).smoothScrollTo(0, 20);
        corpsnoprice_tree.setOnClickListener(this);
        refreshLayoutListener();
    }

    protected void onResume() {
        super.onResume();
        getData();
    }

    private void initView() {
        ((TextView) findViewById(R.id.corpsnoprice_name)).setText(projectname);
        corpsnoprice_listview = (MyListView) findViewById(R.id.corpsnoprice_listview);
        corpsnoprice_person = (TextView) findViewById(R.id.corpsnoprice_person);
        corpsnoprice_time = (TextView) findViewById(R.id.corpsnoprice_time);
        corpsnoprice_period = (TextView) findViewById(R.id.corpsnoprice_period);
        corpsnoprice_tree = (TasknumberTreeView) findViewById(R.id.corpsnoprice_tree);
        refreshLayout = (PullToRefreshLayout) findViewById(R.id.refresh_view);
    }

    private void refreshLayoutListener() {
        refreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                getData();
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

    private void getData() {
        waitExecuteOutlet.sendPostRequest(Urls.WaitExecuteOutlet, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                storeidlist = null;
                if (!list_selected.isEmpty()) {
                    list_selected.clear();
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
                        findViewById(R.id.corpsnoprice_dis).setVisibility(View.VISIBLE);
                        findViewById(R.id.corpsnoprice_selected).setVisibility(View.GONE);
                        if (jsonObject.isNull("data")) {
                            baseFinish();
                        } else {
                            jsonObject = jsonObject.getJSONObject("data");
                            JSONObject project_info = jsonObject.getJSONObject("project_info");
                            corpsnoprice_time.setText(project_info.getString("begin_date") + "-" + project_info.getString("end_date") + "可执行");
                            corpsnoprice_period.setText("审核周期：" + project_info.getString("check_time") + "天");
                            corpsnoprice_person.setText(project_info.getString("project_person"));
                            if ("1".equals(project_info.getString("standard_state"))) {
                                findViewById(R.id.corpsnoprice_standard).setVisibility(View.VISIBLE);
                                findViewById(R.id.corpsnoprice_standard).setOnClickListener(CorpsNopriceActivity.this);
                            } else {
                                findViewById(R.id.corpsnoprice_standard).setVisibility(View.GONE);
                            }
                            String project_type = project_info.getString("project_type");
                            JSONObject num_tree = jsonObject.getJSONObject("num_tree");
                            String total_outlet = num_tree.getString("total_outlet");//总的网点数量
                            String distribution_outlet = num_tree.getString("distribution_outlet");//待分配网点数
                            String wait_exe_outlet = num_tree.getString("wait_exe_outlet");//待执行网点数
                            String execution_outlet = num_tree.getString("execution_outlet");//执行中网点数
                            String check_outlet = num_tree.getString("check_outlet");//审核中网点数
                            String unpass_outlet = num_tree.getString("unpass_outlet");//未通过网点数
                            String pass_outlet = num_tree.getString("pass_outlet");//已通过网点数
                            corpsnoprice_tree.setting(1, total_outlet, distribution_outlet, wait_exe_outlet, execution_outlet,
                                    check_outlet, pass_outlet, unpass_outlet);
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
                                    corpGrabDetailInfo.setMoney(object.getString("money"));
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
                            }
                        }
                        if (corpNopriceAdapter != null) {
                            corpNopriceAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Tools.showToast(CorpsNopriceActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(CorpsNopriceActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(CorpsNopriceActivity.this, getResources().getString(R.string.network_volleyerror));
                if (refreshLayout != null) {
                    refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.corpsnoprice_dis: {//批量分配
                list_selected.clear();
                findViewById(R.id.corpsnoprice_dis).setVisibility(View.GONE);
                findViewById(R.id.corpsnoprice_selected).setVisibility(View.VISIBLE);
                if (!list.isEmpty()) {
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).setShowCheck(true);
                    }
                    corpNopriceAdapter.notifyDataSetChanged();
                }
            }
            break;
            case R.id.corpsnoprice_selected: {//选中
                if (!list_selected.isEmpty()) {
                    String msg = "";
                    for (int i = 0; i < list_selected.size(); i++) {
                        CorpGrabDetailInfo corpGrabDetailInfo = list_selected.get(i);
                        if (storeidlist == null) {
                            storeidlist = corpGrabDetailInfo.getOutlet_id();
                        } else {
                            storeidlist = storeidlist + "," + corpGrabDetailInfo.getOutlet_id();
                        }
                        if ("10".equals(corpGrabDetailInfo.getExe_state())) {
                            msg = msg + corpGrabDetailInfo.getOutlet_name() + "(确认中)\n";
                        } else if ("2".equals(corpGrabDetailInfo.getExe_state())) {
                            msg = msg + corpGrabDetailInfo.getOutlet_name() + "(待执行)\n";
                        } else if ("3".equals(corpGrabDetailInfo.getExe_state())) {
                            msg = msg + corpGrabDetailInfo.getOutlet_name() + "(执行中)\n";
                        }
                    }
                    if (TextUtils.isEmpty(msg)) {//直接跳转分配人员页面
                        Intent intent = new Intent(CorpsNopriceActivity.this, TeammemberActivity.class);
                        intent.putExtra("state", 3);
                        intent.putExtra("team_id", team_id);
                        intent.putExtra("package_team_id", package_team_id);
                        intent.putExtra("project_id", projectid);
                        startActivityForResult(intent, 2);
                    } else {
                        msg = msg + "以上任务是否需要重新分配？";
                        CorpApplyDialog.showDialog(this, "提示", msg, new CorpApplyDialog.CorpApplyListenter() {
                            @Override
                            public void corpApply_cancel() {
                                getData();
                            }

                            @Override
                            public void corpApply_confirm() {
                                Intent intent = new Intent(CorpsNopriceActivity.this, TeammemberActivity.class);
                                intent.putExtra("state", 3);
                                intent.putExtra("team_id", team_id);
                                intent.putExtra("package_team_id", package_team_id);
                                intent.putExtra("project_id", projectid);
                                startActivityForResult(intent, 2);
                            }
                        });
                    }
                } else {
                    Tools.showToast(this, "请至少选中一个选项");
                }
            }
            break;
            case R.id.corpsnoprice_tree: {//队员执行明细
                Intent intent = new Intent(this, TeamExecuteDetailsActivity.class);
                intent.putExtra("package_team_id", package_team_id);
                startActivity(intent);

            }
            break;
            case R.id.corpsnoprice_standard: {//任务说明
                Intent intent = new Intent(this, TaskillustratesActivity.class);
                intent.putExtra("projectid", projectid);
                intent.putExtra("projectname", projectname);
                intent.putExtra("isShow", "0");//是否显示不再显示开始执行按钮
                startActivity(intent);

            }
            break;
            case R.id.corpsnoprice_preview: {
                Intent intent = new Intent(this, TaskitemDetailActivity_12.class);
                intent.putExtra("id", projectid);
                intent.putExtra("projectname", projectname);
                intent.putExtra("store_name", "网点名称");
                intent.putExtra("store_num", "网点编号");
                intent.putExtra("province", "");
                intent.putExtra("city", "");
                intent.putExtra("project_id", projectid);
                intent.putExtra("project_type", "1");
                intent.putExtra("is_desc", "");
                intent.putExtra("index", "0");
                startActivity(intent);
            }
            break;
        }
    }

    //拼接选中的信息
    @Override
    public void onItemCheck(CorpGrabDetailInfo corpGrabDetailInfo) {
        if (corpGrabDetailInfo.isCheck()) {
            list_selected.add(corpGrabDetailInfo);
        } else {
            if (list_selected.contains(corpGrabDetailInfo)) {
                list_selected.remove(corpGrabDetailInfo);
            }
        }
    }

    //批量网点人员分配
    private void multipleDistribution() {
        multipleDistribution.sendPostRequest(Urls.MultipleDistribution, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Tools.showToast(CorpsNopriceActivity.this, "操作成功");
                        getData();
                    } else {
                        Tools.showToast(CorpsNopriceActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(CorpsNopriceActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(CorpsNopriceActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
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
                        Tools.showToast(CorpsNopriceActivity.this, "操作成功");
                        getData();
                    } else {
                        Tools.showToast(CorpsNopriceActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(CorpsNopriceActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(CorpsNopriceActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (corpNopriceAdapter != null) {
            CorpGrabDetailInfo corpGrabDetailInfo = list.get(position);
            storeid = corpGrabDetailInfo.getOutlet_id();
            if (corpNopriceAdapter.isClick1()) {//立即执行
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
            } else if (corpNopriceAdapter.isClick2()) {//分配
                String exe_state = corpGrabDetailInfo.getExe_state();
                if ("9".equals(exe_state)) {//待分配可直接分配
                    Intent intent = new Intent(CorpsNopriceActivity.this, TeammemberActivity.class);
                    intent.putExtra("state", 3);
                    intent.putExtra("team_id", team_id);
                    intent.putExtra("package_team_id", package_team_id);
                    intent.putExtra("project_id", projectid);
                    startActivityForResult(intent, 1);
                } else {
                    String msg = "";
                    if ("10".equals(exe_state)) {//确认中
                        msg = corpGrabDetailInfo.getOutlet_name() + ",此任务已被队员确认领取，是否需要重新分配？";
                    } else if ("2".equals(exe_state)) {//待执行
                        msg = corpGrabDetailInfo.getOutlet_name() + ",此任务已有队员正在执行中，是否需要重新分配？";
                    } else if ("3".equals(exe_state)) {//执行中
                        msg = corpGrabDetailInfo.getOutlet_name() + ",此任务正在等待队员确认，是否需要重新分配？";
                    }
                    CorpApplyDialog.showDialog(CorpsNopriceActivity.this, "提示", msg, new CorpApplyDialog.CorpApplyListenter() {
                        @Override
                        public void corpApply_cancel() {

                        }

                        @Override
                        public void corpApply_confirm() {
                            Intent intent = new Intent(CorpsNopriceActivity.this, TeammemberActivity.class);
                            intent.putExtra("state", 3);
                            intent.putExtra("team_id", team_id);
                            intent.putExtra("package_team_id", package_team_id);
                            intent.putExtra("project_id", projectid);
                            startActivityForResult(intent, 1);
                        }
                    });
                }
            }
            corpNopriceAdapter.clearClick();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && data != null) {
            if (resultCode == RESULT_OK) {
                accessed_num = data.getStringExtra("accessed_num");
                singleDistribution();
            }
        } else if (requestCode == 2 && data != null) {
            if (resultCode == RESULT_OK) {
                accessed_num = data.getStringExtra("accessed_num");
                multipleDistribution();
            }
        }
    }
}
