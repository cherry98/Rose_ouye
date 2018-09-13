package com.orange.oy.activity.mycorps_315;

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
import com.orange.oy.activity.TaskillustratesActivity;
import com.orange.oy.activity.TaskitemDetailActivity_12;
import com.orange.oy.activity.mycorps_314.TeamShallActivity;
import com.orange.oy.adapter.mycorps_314.CorpGrabAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.MyUMShareUtils;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.SelectCorpDialog;
import com.orange.oy.dialog.UMShareDialog;
import com.orange.oy.info.mycorps.CorpGrabInfo;
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
 * 众包任务批量申领==战队（任务包） V3.15
 */
public class CorpGrabActivity extends BaseActivity implements View.OnClickListener, AppTitle.OnBackClickForAppTitle, AdapterView.OnItemClickListener {

    private void initTitle(String name) {
        AppTitle appTitle = (AppTitle) findViewById(R.id.corpgrab_title);
        appTitle.settingName(name);
        appTitle.showBack(this);
        appTitle.showIllustrate(R.mipmap.share2, new AppTitle.OnExitClickForAppTitle() {
            @Override
            public void onExit() {
                UMShareDialog.showDialog(CorpGrabActivity.this, false, new UMShareDialog.UMShareListener() {
                    @Override
                    public void shareOnclick(int type) {
                        String webUrl = Urls.ShareProject + "?&projectid=" + projectid +
                                "&usermobile=" + AppInfo.getName(CorpGrabActivity.this) + "&sign=" + sign;
                        MyUMShareUtils.umShare(CorpGrabActivity.this, type, webUrl);
                    }
                });
            }
        });
    }

    private void initNetwork() {
        outletPackage = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(CorpGrabActivity.this));
                params.put("token", Tools.getToken());
                params.put("projectid", projectid);
                return params;
            }
        };
        selectTeam = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(CorpGrabActivity.this));
                params.put("token", Tools.getToken());
                params.put("projectid", projectid);
                params.put("type", "1");
                params.put("package_id", package_id);
                return params;
            }
        };
        Sign = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                String key = "projectid=" + projectid + "&usermobile=" + AppInfo.getName(CorpGrabActivity.this);
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
                        Tools.showToast(CorpGrabActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(CorpGrabActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(CorpGrabActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private PullToRefreshListView corpgrab_listview;
    private CorpGrabAdapter corpGrabAdapter;
    private String projectname, projectid, package_id;
    private Intent data;
    private NetworkConnection outletPackage, selectTeam, Sign;
    private TextView corpgrab_name, corpgrab_person, corpgrab_time, corpgrab_period;
    private ArrayList<CorpGrabInfo> list;
    private String sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corp_grab);
        list = new ArrayList<>();
        data = getIntent();
        projectname = data.getStringExtra("projectname");
        projectid = data.getStringExtra("projectid");
        initTitle(projectname);
        initView();
        initNetwork();
        corpgrab_name.setText(projectname);
        corpGrabAdapter = new CorpGrabAdapter(this, list);
        corpgrab_listview.setAdapter(corpGrabAdapter);
        findViewById(R.id.corpgrab_preview).setOnClickListener(this);
        getData();
        corpgrab_listview.setOnItemClickListener(this);
        corpgrab_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getData();
            }
        });
    }

    private void initView() {
        corpgrab_listview = (PullToRefreshListView) findViewById(R.id.corpgrab_listview);
        corpgrab_name = (TextView) findViewById(R.id.corpgrab_name);
        corpgrab_person = (TextView) findViewById(R.id.corpgrab_person);
        corpgrab_time = (TextView) findViewById(R.id.corpgrab_time);
        corpgrab_period = (TextView) findViewById(R.id.corpgrab_period);
    }

    private void getData() {
        outletPackage.sendPostRequest(Urls.OutletPackage, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (!list.isEmpty()) {
                            list.clear();
                        }
                        jsonObject = jsonObject.getJSONObject("data");
                        JSONObject project_info = jsonObject.getJSONObject("project_info");
                        corpgrab_person.setText(project_info.getString("project_person"));
                        String certification = project_info.getString("certification");
                        if ("1".equals(certification)) {//是否企业认证
                            findViewById(R.id.corpgrab_identity).setVisibility(View.VISIBLE);
                        } else {
                            findViewById(R.id.corpgrab_identity).setVisibility(View.GONE);
                        }
                        corpgrab_time.setText(project_info.getString("begin_date") + "-" + project_info.getString("end_date") + "可执行");
                        corpgrab_period.setText("审核周期：" + project_info.getString("check_time") + "天");
                        if ("1".equals(project_info.getString("standard_state"))) {
                            findViewById(R.id.corpgrab_standard).setVisibility(View.VISIBLE);
                            findViewById(R.id.corpgrab_standard).setOnClickListener(CorpGrabActivity.this);
                        } else {
                            findViewById(R.id.corpgrab_standard).setVisibility(View.GONE);
                        }
                        String project_type = project_info.getString("project_type");
                        JSONObject package_info = jsonObject.getJSONObject("package_info");
                        String type = package_info.getString("type");
                        if ("1".equals(type)) {
                            findViewById(R.id.corpgrab_hint).setVisibility(View.VISIBLE);
                        } else {
                            findViewById(R.id.corpgrab_hint).setVisibility(View.GONE);
                        }
                        JSONArray jsonArray = package_info.optJSONArray("list");
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                CorpGrabInfo corpGrabInfo = new CorpGrabInfo();
                                corpGrabInfo.setProject_type(project_type);
                                corpGrabInfo.setType(type);
                                corpGrabInfo.setPackage_id(object.getString("package_id"));
                                corpGrabInfo.setProvince(object.getString("province"));
                                corpGrabInfo.setCity(object.getString("city"));
                                corpGrabInfo.setNum(object.getString("num"));
                                corpGrabInfo.setTotal_money(object.getString("total_money"));
                                corpGrabInfo.setIs_certification(object.getString("is_certification"));
                                corpGrabInfo.setJump_select_team(object.getString("jump_select_team"));
                                corpGrabInfo.setTeam_id(object.getString("team_id"));
                                corpGrabInfo.setCertification(certification);
                                list.add(corpGrabInfo);
                            }
                            if (corpGrabAdapter != null) {
                                corpGrabAdapter.notifyDataSetChanged();
                            }
                        }
                        corpgrab_listview.onRefreshComplete();
                        Sign();
                    } else {
                        Tools.showToast(CorpGrabActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(CorpGrabActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                corpgrab_listview.onRefreshComplete();
                Tools.showToast(CorpGrabActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.corpgrab_standard: {//任务说明
                Intent intent = new Intent(this, TaskillustratesActivity.class);
                intent.putExtra("projectid", projectid);
                intent.putExtra("projectname", projectname);
                intent.putExtra("isShow", "0");//是否显示不再显示开始执行按钮
                startActivity(intent);
            }
            break;
            case R.id.corpgrab_preview: {//任务预览
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
        }
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CorpGrabInfo corpGrabInfo = list.get(position - 1);
        package_id = corpGrabInfo.getPackage_id();
        if ("0".equals(corpGrabInfo.getIs_certification())) {//需要认证
            if ("0".equals(corpGrabInfo.getJump_select_team()) && !TextUtils.isEmpty(corpGrabInfo.getTeam_id())) {//单个战队认证
                Intent intent = new Intent(CorpGrabActivity.this, TeamShallActivity.class);
                intent.putExtra("team_id", corpGrabInfo.getTeam_id());
                startActivity(intent);
            } else {
                selectTeam();
            }
        } else {//无需认证 跳转网点分布明细
            if ("1".equals(corpGrabInfo.getProject_type())) {//有网点
                Intent intent = new Intent(this, CorpGrabDetailActivity.class);
                intent.putExtra("projectid", projectid);
                intent.putExtra("projectname", projectname);
                intent.putExtra("package_id", package_id);
                intent.putExtra("certification", corpGrabInfo.getCertification());
                startActivityForResult(intent, 0);
            } else {//无网点
                Intent intent = new Intent(this, ProjectDetailActivity.class);
                intent.putExtra("projectid", projectid);
                intent.putExtra("projectname", projectname);
                intent.putExtra("package_id", package_id);
                intent.putExtra("team_id", corpGrabInfo.getTeam_id());
                intent.putExtra("certification", corpGrabInfo.getCertification());
                startActivityForResult(intent, 0);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == AppInfo.RESULT_ACTIVITY_FINISH_FOR_DATA) {
                setResult(AppInfo.RESULT_MAIN_SHOWMIDDLE_RIGHT, data);
                baseFinish();
            }
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
                                SelectCorpDialog.showDialog(CorpGrabActivity.this, jsonArray.getJSONObject(0).getString("team_name")
                                        , jsonArray.getJSONObject(0).getString("team_id"), jsonArray.getJSONObject(1).getString("team_name")
                                        , jsonArray.getJSONObject(1).getString("team_id"), jsonArray.getJSONObject(2).getString("team_name")
                                        , jsonArray.getJSONObject(2).getString("team_id"), new SelectCorpDialog.SelectCorpListener() {
                                            @Override
                                            public void selectCorp(String select_id) {
                                                Intent intent = new Intent(CorpGrabActivity.this, TeamShallActivity.class);
                                                intent.putExtra("team_id", select_id);
                                                startActivity(intent);
                                            }
                                        });
                            } else if (jsonArray.length() == 2) {
                                SelectCorpDialog.showDialog(CorpGrabActivity.this, jsonArray.getJSONObject(0).getString("team_name")
                                        , jsonArray.getJSONObject(0).getString("team_id"), jsonArray.getJSONObject(1).getString("team_name")
                                        , jsonArray.getJSONObject(1).getString("team_id"), null, null, new SelectCorpDialog.SelectCorpListener() {
                                            @Override
                                            public void selectCorp(String select_id) {
                                                Intent intent = new Intent(CorpGrabActivity.this, TeamShallActivity.class);
                                                intent.putExtra("team_id", select_id);
                                                startActivity(intent);
                                            }
                                        });
                            }
                        }
                    } else {
                        Tools.showToast(CorpGrabActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(CorpGrabActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(CorpGrabActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }
}
