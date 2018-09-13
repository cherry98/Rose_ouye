package com.orange.oy.activity.mycorps_315;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.TaskillustratesActivity;
import com.orange.oy.activity.TaskitemDetailActivity_12;
import com.orange.oy.adapter.mycorps_314.CorpGrabDetailAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.MyUMShareUtils;
import com.orange.oy.base.ScreenManager;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CorpApplyDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.SelectCorpDialog;
import com.orange.oy.dialog.UMShareDialog;
import com.orange.oy.fragment.TaskNewFragment;
import com.orange.oy.info.mycorps.CorpGrabDetailInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.MyListView;
import com.orange.oy.view.PullToRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 网点分布的明细==战队 V3.15
 */
public class CorpGrabDetailActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {

    private void initTitle(String name) {
        AppTitle appTitle = (AppTitle) findViewById(R.id.corpgrabdetail_title);
        appTitle.settingName(name);
        appTitle.showBack(this);
        appTitle.showIllustrate(R.mipmap.share2, new AppTitle.OnExitClickForAppTitle() {
            @Override
            public void onExit() {
                UMShareDialog.showDialog(CorpGrabDetailActivity.this, false, new UMShareDialog.UMShareListener() {
                    @Override
                    public void shareOnclick(int type) {
                        String webUrl = Urls.ShareProject + "?&projectid=" + projectid + "&usermobile=" +
                                AppInfo.getName(CorpGrabDetailActivity.this) + "&sign=" + sign;
                        MyUMShareUtils.umShare(CorpGrabDetailActivity.this, type, webUrl);
                    }
                });
            }
        });
    }

    private void initNetwork() {
        outletPackageDetail = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(CorpGrabDetailActivity.this));
                params.put("token", Tools.getToken());
                params.put("projectid", projectid);
                params.put("package_id", package_id);
                if (!TextUtils.isEmpty(corpgrabdetail_edit.getText().toString().trim())) {
                    params.put("keyword", corpgrabdetail_edit.getText().toString().trim());
                }
//                params.put("page",) page页码（从1开始，不传为全部）
                return params;
            }
        };
        selectTeam = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(CorpGrabDetailActivity.this));
                params.put("token", Tools.getToken());
                params.put("projectid", projectid);
                params.put("type", "2");
                params.put("package_id", package_id);
                return params;
            }
        };
        robConfirm = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(CorpGrabDetailActivity.this));
                params.put("token", Tools.getToken());
                params.put("projectid", projectid);
                params.put("package_id", package_id);
                params.put("team_id", team_id);
                return params;
            }
        };
        robSubmit = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(CorpGrabDetailActivity.this));
                params.put("token", Tools.getToken());
                params.put("projectid", projectid);
                params.put("package_id", package_id);
                params.put("team_id", team_id);
                return params;
            }
        };
        Sign = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                String key = "projectid=" + projectid + "&usermobile=" + AppInfo.getName(CorpGrabDetailActivity.this);
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("key", key);
                return params;
            }
        };
    }

    private void Sign() {
        Sign.sendPostRequest(Urls.Sign, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        sign = jsonObject.getString("msg");
                    } else {
                        Tools.showToast(CorpGrabDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(CorpGrabDetailActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(CorpGrabDetailActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private String projectname, projectid, package_id, team_id, sign;
    private Intent data;
    private CorpGrabDetailAdapter corpGrabDetailAdapter;
    private MyListView corpgrabdetail_listview;
    private TextView corpgrabdetail_district, corpgrabdetail_account, corpgrabdetail_person, corpgrabdetail_name,
            corpgrabdetail_time, corpgrabdetail_period;
    private NetworkConnection outletPackageDetail, selectTeam, robConfirm, robSubmit, Sign;
    private EditText corpgrabdetail_edit;
    private ArrayList<CorpGrabDetailInfo> list;
    private PullToRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corp_grab_detail);
        list = new ArrayList<>();
        data = getIntent();
        projectname = data.getStringExtra("projectname");
        projectid = data.getStringExtra("projectid");
        package_id = data.getStringExtra("package_id");
        String certification = data.getStringExtra("certification");
        if ("1".equals(certification)) {
            findViewById(R.id.corpgrabdetail_identity).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.corpgrabdetail_identity).setVisibility(View.GONE);
        }
        initTitle(projectname);
        initView();
        initNetwork();
        ((ScrollView) findViewById(R.id.scv)).smoothScrollTo(0, 20);
        corpgrabdetail_name.setText(projectname);
        corpGrabDetailAdapter = new CorpGrabDetailAdapter(this, list);
        corpgrabdetail_listview.setAdapter(corpGrabDetailAdapter);
        getData();
        refreshLayoutListener();
        corpgrabdetail_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    getData();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void initView() {
        refreshLayout = (PullToRefreshLayout) findViewById(R.id.refresh_view);
        corpgrabdetail_district = (TextView) findViewById(R.id.corpgrabdetail_district);
        corpgrabdetail_name = (TextView) findViewById(R.id.corpgrabdetail_name);
        corpgrabdetail_person = (TextView) findViewById(R.id.corpgrabdetail_person);
        corpgrabdetail_time = (TextView) findViewById(R.id.corpgrabdetail_time);
        corpgrabdetail_account = (TextView) findViewById(R.id.corpgrabdetail_account);
        corpgrabdetail_period = (TextView) findViewById(R.id.corpgrabdetail_period);
        corpgrabdetail_listview = (MyListView) findViewById(R.id.corpgrabdetail_listview);
        corpgrabdetail_edit = (EditText) findViewById(R.id.corpgrabdetail_edit);
        findViewById(R.id.corpgrabdetail_preview).setOnClickListener(this);
        findViewById(R.id.corpgrabdetail_standard).setOnClickListener(this);
        findViewById(R.id.corpgrabdetail_button).setOnClickListener(this);
        findViewById(R.id.corpgrabdetail_loacation).setOnClickListener(this);
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
        outletPackageDetail.sendPostRequest(Urls.OutletPackageDetail, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
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
                        jsonObject = jsonObject.getJSONObject("data");
                        JSONObject project_info = jsonObject.getJSONObject("project_info");
                        corpgrabdetail_person.setText(project_info.getString("project_person"));
                        corpgrabdetail_time.setText(project_info.getString("begin_date") + "-" + project_info.getString("end_date") + "可执行");
                        corpgrabdetail_period.setText("审核周期：" + project_info.getString("check_time") + "天");
                        if ("1".equals(project_info.getString("standard_state"))) {
                            findViewById(R.id.corpgrabdetail_standard).setVisibility(View.VISIBLE);
                            findViewById(R.id.corpgrabdetail_standard).setOnClickListener(CorpGrabDetailActivity.this);
                        } else {
                            findViewById(R.id.corpgrabdetail_standard).setVisibility(View.GONE);
                        }
                        JSONObject outlet_info = jsonObject.getJSONObject("outlet_info");
                        if ("1".equals(outlet_info.getString("type"))) {//省份
                            corpgrabdetail_district.setText(outlet_info.getString("province"));
                        } else if ("1".equals(outlet_info.getString("type"))) {//城市
                            corpgrabdetail_district.setText(outlet_info.getString("province") + "-" + outlet_info.getString("city"));
                        }
                        if ("1".equals(outlet_info.getString("show_map"))) {//是否显示地图
                            findViewById(R.id.corpgrabdetail_loacation).setVisibility(View.VISIBLE);
                            findViewById(R.id.corpgrabdetail_loacation).setOnClickListener(CorpGrabDetailActivity.this);
                        } else {
                            findViewById(R.id.corpgrabdetail_loacation).setVisibility(View.GONE);
                        }
                        corpgrabdetail_account.setText(outlet_info.getString("total_num") + "个");
                        JSONArray jsonArray = outlet_info.optJSONArray("list");
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                CorpGrabDetailInfo corpGrabDetailInfo = new CorpGrabDetailInfo();
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
                                list.add(corpGrabDetailInfo);
                            }
                            if (corpGrabDetailAdapter != null) {
                                corpGrabDetailAdapter.notifyDataSetChanged();
                            }
                            Sign();
                        }
                    } else {
                        Tools.showToast(CorpGrabDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(CorpGrabDetailActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (refreshLayout != null) {
                    refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
                Tools.showToast(CorpGrabDetailActivity.this, getResources().getString(R.string.network_volleyerror));
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
            case R.id.corpgrabdetail_preview: {//任务预览
                Intent intent = new Intent(this, TaskitemDetailActivity_12.class);
                intent.putExtra("id", data.getStringExtra("id"));
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
            case R.id.corpgrabdetail_standard: {//任务说明
                Intent intent = new Intent(this, TaskillustratesActivity.class);
                intent.putExtra("projectid", projectid);
                intent.putExtra("projectname", projectname);
                intent.putExtra("isShow", "0");//是否显示不再显示开始执行按钮
                startActivity(intent);
            }
            break;
            case R.id.corpgrabdetail_button: {//全部领取
                selectTeam();
            }
            break;
            case R.id.corpgrabdetail_loacation: {//地图查看
                Intent intent = new Intent(CorpGrabDetailActivity.this, TeamTaskMapActivity.class);
                intent.putExtra("project_id", projectid);
                intent.putExtra("package_id", package_id);
                startActivity(intent);
            }
            break;
        }
    }

    private void selectTeam() {
        selectTeam.sendPostRequest(Urls.SelectTeam, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        jsonObject = jsonObject.getJSONObject("data");
                        JSONArray jsonArray = jsonObject.optJSONArray("list");
                        if (jsonArray != null) {
                            if (jsonArray.length() == 3) {
                                SelectCorpDialog.showDialog(CorpGrabDetailActivity.this, jsonArray.getJSONObject(0).getString("team_name")
                                        , jsonArray.getJSONObject(0).getString("team_id"), jsonArray.getJSONObject(1).getString("team_name")
                                        , jsonArray.getJSONObject(1).getString("team_id"), jsonArray.getJSONObject(2).getString("team_name")
                                        , jsonArray.getJSONObject(2).getString("team_id"), new SelectCorpDialog.SelectCorpListener() {
                                            @Override
                                            public void selectCorp(String select_id) {
                                                allReceiver(select_id);
                                            }
                                        });
                            } else if (jsonArray.length() == 2) {
                                SelectCorpDialog.showDialog(CorpGrabDetailActivity.this, jsonArray.getJSONObject(0).getString("team_name")
                                        , jsonArray.getJSONObject(0).getString("team_id"), jsonArray.getJSONObject(1).getString("team_name")
                                        , jsonArray.getJSONObject(1).getString("team_id"), null, null, new SelectCorpDialog.SelectCorpListener() {
                                            @Override
                                            public void selectCorp(String select_id) {
                                                allReceiver(select_id);
                                            }
                                        });
                            } else if (jsonArray.length() == 1) {
                                SelectCorpDialog.showDialog(CorpGrabDetailActivity.this, jsonArray.getJSONObject(0).getString("team_name")
                                        , jsonArray.getJSONObject(0).getString("team_id"), null, null, null, null, new SelectCorpDialog.SelectCorpListener() {
                                            @Override
                                            public void selectCorp(String select_id) {
                                                allReceiver(select_id);
                                            }
                                        });
                            }
                        }
                    } else {
                        Tools.showToast(CorpGrabDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(CorpGrabDetailActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(CorpGrabDetailActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private void allReceiver(final String team_id) {
        this.team_id = team_id;
        robConfirm.sendPostRequest(Urls.RobConfirm, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        jsonObject = jsonObject.getJSONObject("data");
                        String province = jsonObject.getString("province");
                        String city = jsonObject.getString("city");
                        String total_outlet = jsonObject.getString("total_outlet");
                        String person_num = jsonObject.getString("person_num");
                        String ahead_date = jsonObject.getString("ahead_date");
                        String pass_percent = jsonObject.getString("pass_percent");
                        String extra_award = jsonObject.getString("extra_award");
                        String extra_money = jsonObject.getString("extra_money");
                        String msg1;
                        if (!"null".equals(city) && !TextUtils.isEmpty(city)) {
                            msg1 = province + "-" + city + "，申请成功 " + jsonObject.getString("total_outlet") + " 家店";
                        } else {
                            msg1 = province + "，申请成功" + total_outlet + "家店";
                        }
                        String msg2 = "共计申请成功 " + total_outlet + " 家店\n" + "战队中符合任务要求 " + person_num + " 人";
                        String msg3 = "如您的团队在 " + ahead_date + " 之前完成，并合格率达到" + pass_percent + "%，您将额外获得" +
                                extra_award + "%（" + extra_money + "元）的奖励金。否则您将得不到奖励金。";
                        String msg3_1 = "额外获得" + extra_award + "%（" + extra_money + "元）";
                        CorpApplyDialog.showDialog(CorpGrabDetailActivity.this, "您确认领取以下任务吗？", msg1, msg2, total_outlet, person_num
                                , msg3, msg3_1, team_id, projectid, package_id,
                                true, new CorpApplyDialog.CorpApplyListenter() {
                                    @Override
                                    public void corpApply_cancel() {

                                    }

                                    @Override
                                    public void corpApply_confirm() {
                                        robSubmit();
                                    }
                                });
                    } else {
                        Tools.showToast(CorpGrabDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(CorpGrabDetailActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(CorpGrabDetailActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private void robSubmit() {
        robSubmit.sendPostRequest(Urls.RobSubmit, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        jsonObject = jsonObject.getJSONObject("data");
                        String state = jsonObject.getString("state");
                        if ("1".equals(state)) {//申请成功==战队任务-等待执行
                            setResult(AppInfo.RESULT_ACTIVITY_FINISH_FOR_DATA);
                            baseFinish();
                        } else {//申请不成功
                            ConfirmDialog.showDialog(CorpGrabDetailActivity.this, "很遗憾，稍慢了一步！", 3, "任务被别人抢走了，您可以到广场领取其它任务。",
                                    null, "返回广场", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                        @Override
                                        public void leftClick(Object object) {

                                        }

                                        @Override
                                        public void rightClick(Object object) {
                                            ScreenManager screenManager = ScreenManager.getScreenManager();
                                            screenManager.finishActivity(CorpGrabActivity.class);
                                            TaskNewFragment.isRefresh = true;
                                            baseFinish();
                                        }
                                    });
                        }
                    } else {
                        Tools.showToast(CorpGrabDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(CorpGrabDetailActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(CorpGrabDetailActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
