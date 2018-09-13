package com.orange.oy.activity.mycorps_315;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.TaskillustratesActivity;
import com.orange.oy.activity.TaskitemDetailActivity_12;
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
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 众包项目申请==战队 无网点 ===详情 V3.15
 */
public class ProjectDetailActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {

    private void initTitle(String name) {
        AppTitle appTitle = (AppTitle) findViewById(R.id.projectdetail_title);
        appTitle.settingName(name);
        appTitle.showBack(this);
        appTitle.showIllustrate(R.mipmap.share2, new AppTitle.OnExitClickForAppTitle() {
            @Override
            public void onExit() {
                UMShareDialog.showDialog(ProjectDetailActivity.this, false, new UMShareDialog.UMShareListener() {
                    @Override
                    public void shareOnclick(int type) {
                        String webUrl = Urls.ShareProject + "?&projectid=" + projectid + "&usermobile=" +
                                AppInfo.getName(ProjectDetailActivity.this) + "&sign=" + sign;
                        MyUMShareUtils.umShare(ProjectDetailActivity.this, type, webUrl);
                    }
                });
            }
        });
    }

    private void initNetwork() {
        noOutletPackageDetail = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(ProjectDetailActivity.this));
                params.put("token", Tools.getToken());
                params.put("projectid", projectid);
                params.put("package_id", package_id);
                return params;
            }
        };
        selectTeam = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(ProjectDetailActivity.this));
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
                params.put("usermobile", AppInfo.getName(ProjectDetailActivity.this));
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
                params.put("usermobile", AppInfo.getName(ProjectDetailActivity.this));
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
                String key = "projectid=" + projectid + "&usermobile=" + AppInfo.getName(ProjectDetailActivity.this);
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
                        Tools.showToast(ProjectDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(ProjectDetailActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(ProjectDetailActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private String projectname, projectid;
    private Intent data;
    private TextView projectdetail_money, projectdetail_account, projectdetail_district, projectdetail_ptime;
    private NetworkConnection noOutletPackageDetail, selectTeam, robConfirm, robSubmit, Sign;
    private String team_id, package_id, sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);
        data = getIntent();
        projectname = data.getStringExtra("projectname");
        projectid = data.getStringExtra("projectid");
        team_id = data.getStringExtra("team_id");
        package_id = data.getStringExtra("package_id");
        initTitle(projectname);
        initNetwork();
        ((TextView) findViewById(R.id.projectdetail_name)).setText(projectname);
        projectdetail_money = (TextView) findViewById(R.id.projectdetail_money);
        projectdetail_account = (TextView) findViewById(R.id.projectdetail_account);
        projectdetail_district = (TextView) findViewById(R.id.projectdetail_district);
        projectdetail_ptime = (TextView) findViewById(R.id.projectdetail_ptime);
        ((ScrollView) findViewById(R.id.scrollview)).smoothScrollTo(0, 20);
        findViewById(R.id.projectdetail_preview).setOnClickListener(this);
        findViewById(R.id.projectdetail_standard).setOnClickListener(this);
        findViewById(R.id.projectdetail_button).setOnClickListener(this);
        getData();
    }

    private void getData() {
        noOutletPackageDetail.sendPostRequest(Urls.NoOutletPackageDetail, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        jsonObject = jsonObject.getJSONObject("data");
                        JSONObject project_info = jsonObject.getJSONObject("project_info");
                        ((TextView) findViewById(R.id.projectdetail_person)).setText(project_info.getString("project_person"));
                        ((TextView) findViewById(R.id.projectdetail_time)).setText(project_info.getString("begin_date") + "-"
                                + project_info.getString("end_date") + "可执行");
                        ((TextView) findViewById(R.id.projectdetail_period)).setText("审核周期：" + project_info.getString("check_time") + "天");
                        if ("1".equals(project_info.getString("certification"))) {//是否企业认证
                            findViewById(R.id.projectdetail_identity).setVisibility(View.VISIBLE);
                        } else {
                            findViewById(R.id.projectdetail_identity).setVisibility(View.GONE);
                        }
                        if ("1".equals(project_info.getString("standard_state"))) {
                            findViewById(R.id.projectdetail_standard).setVisibility(View.VISIBLE);
                            findViewById(R.id.projectdetail_standard).setOnClickListener(ProjectDetailActivity.this);
                        } else {
                            findViewById(R.id.projectdetail_standard).setVisibility(View.GONE);
                        }
                        projectdetail_money.setText("¥" + jsonObject.getString("total_money"));
                        projectdetail_account.setText("每人限领" + jsonObject.getString("limit_num") + "份");
                        if ("0".equals(jsonObject.getString("limit_position"))) {//不受限制
                            projectdetail_district.setText("不受限制");
                        } else {
                            String province = jsonObject.getString("limit_province");
                            String city = jsonObject.getString("limit_city");
                            String county = jsonObject.getString("limit_county");
                            if (!Tools.isEmpty(province) && !Tools.isEmpty(city) && !Tools.isEmpty(county)) {
                                projectdetail_district.setText(province + "-" + city + "-" + county);
                            } else if (!Tools.isEmpty(province) && !Tools.isEmpty(city) && Tools.isEmpty(county)) {
                                projectdetail_district.setText(province + "-" + city);
                            } else if (!Tools.isEmpty(province) && Tools.isEmpty(city) && Tools.isEmpty(county)) {
                                projectdetail_district.setText(province);
                            }
                        }
                        Sign();
                        projectdetail_ptime.setText(jsonObject.getString("begin_date") + "~" + jsonObject.getString("end_date"));
                    } else {
                        Tools.showToast(ProjectDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(ProjectDetailActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(ProjectDetailActivity.this, getResources().getString(R.string.network_volleyerror));
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
            case R.id.projectdetail_standard: {
                Intent intent = new Intent(this, TaskillustratesActivity.class);
                intent.putExtra("projectid", projectid);
                intent.putExtra("projectname", projectname);
                intent.putExtra("isShow", "0");//是否显示不再显示开始执行按钮
                startActivity(intent);
            }
            break;
            case R.id.projectdetail_preview: {
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
            case R.id.projectdetail_button: {//确认领取
                selectTeam();
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
                                SelectCorpDialog.showDialog(ProjectDetailActivity.this, jsonArray.getJSONObject(0).getString("team_name")
                                        , jsonArray.getJSONObject(0).getString("team_id"), jsonArray.getJSONObject(1).getString("team_name")
                                        , jsonArray.getJSONObject(1).getString("team_id"), jsonArray.getJSONObject(2).getString("team_name")
                                        , jsonArray.getJSONObject(2).getString("team_id"), new SelectCorpDialog.SelectCorpListener() {
                                            @Override
                                            public void selectCorp(String select_id) {
                                                allReceiver(select_id);
                                            }
                                        });
                            } else if (jsonArray.length() == 2) {
                                SelectCorpDialog.showDialog(ProjectDetailActivity.this, jsonArray.getJSONObject(0).getString("team_name")
                                        , jsonArray.getJSONObject(0).getString("team_id"), jsonArray.getJSONObject(1).getString("team_name")
                                        , jsonArray.getJSONObject(1).getString("team_id"), null, null, new SelectCorpDialog.SelectCorpListener() {
                                            @Override
                                            public void selectCorp(String select_id) {
                                                allReceiver(select_id);
                                            }
                                        });
                            } else if (jsonArray.length() == 1) {
                                SelectCorpDialog.showDialog(ProjectDetailActivity.this, jsonArray.getJSONObject(0).getString("team_name")
                                        , jsonArray.getJSONObject(0).getString("team_id"), null, null, null, null, new SelectCorpDialog.SelectCorpListener() {
                                            @Override
                                            public void selectCorp(String select_id) {
                                                allReceiver(select_id);
                                            }
                                        });
                            }
                        }
                    } else {
                        Tools.showToast(ProjectDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(ProjectDetailActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(ProjectDetailActivity.this, getResources().getString(R.string.network_volleyerror));
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
                        String msg3 = "如您的团队在 " + ahead_date + " 之前完成，并合格率达到" + pass_percent + "，您将额外获得" + extra_award + "%（" + extra_money + "元）的奖励金。否则您将得不到奖励金。";
                        String msg3_1 = "额外获得" + extra_award + "%（" + extra_money + "元）";
                        CorpApplyDialog.showDialog(ProjectDetailActivity.this, "您确认领取以下任务吗？", msg1, msg2, total_outlet, person_num
                                , msg3, msg3_1, team_id, projectid, package_id, true, new CorpApplyDialog.CorpApplyListenter() {
                                    @Override
                                    public void corpApply_cancel() {

                                    }

                                    @Override
                                    public void corpApply_confirm() {
                                        robSubmit();
                                    }
                                });
                    } else {
                        Tools.showToast(ProjectDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(ProjectDetailActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(ProjectDetailActivity.this, getResources().getString(R.string.network_volleyerror));
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
                            ConfirmDialog.showDialog(ProjectDetailActivity.this, "很遗憾，稍慢了一步！", 3, "任务被别人抢走了，您可以到广场领取其它任务。",
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
                        Tools.showToast(ProjectDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(ProjectDetailActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(ProjectDetailActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
