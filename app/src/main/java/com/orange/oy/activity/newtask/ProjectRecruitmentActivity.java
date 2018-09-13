package com.orange.oy.activity.newtask;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.mycorps_314.IdentifycodeLoginActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.info.ProjectRecListInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProjectRecruitmentActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {

    public void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.projectrec_title);
        appTitle.settingName("项目招募");
        appTitle.showBack(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (selectquestionnaire != null) {
            selectquestionnaire.stop(Urls.SelectQuestionnaire);
        }
        if (recruitmentInfo != null) {
            recruitmentInfo.stop(Urls.RecruitmentInfo);
        }
    }

    private WebView webView;
    private String projectid;
    private NetworkConnection selectquestionnaire, recruitmentInfo;
    private ArrayList<ProjectRecListInfo> list;
    private Intent data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_recruitment);
        initTitle();
        initNetworkConnection();
        list = new ArrayList<>();
        data = getIntent();
        if (data == null) {
            Tools.showToast(this, "缺少参数");
            baseFinish();
        } else {
            projectid = data.getStringExtra("projectid");
        }
        getData();
        webView = (WebView) findViewById(R.id.projectrec_webview);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.getSettings().setBlockNetworkImage(false);
        webView.loadUrl(Urls.Recruitment + "?projectid=" + projectid);
        findViewById(R.id.projectrec_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(AppInfo.getKey(ProjectRecruitmentActivity.this))) {
                    ConfirmDialog.showDialog(ProjectRecruitmentActivity.this, null, 2,
                            getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                @Override
                                public void leftClick(Object object) {
                                }

                                @Override
                                public void rightClick(Object object) {
                                    Intent intent = new Intent(ProjectRecruitmentActivity.this, IdentifycodeLoginActivity.class);
                                    startActivity(intent);
                                }
                            });
                    return;
                }
                goData();
            }
        });

    }

    private void initNetworkConnection() {
        selectquestionnaire = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(ProjectRecruitmentActivity.this));
                params.put("projectid", projectid);
                return params;
            }
        };
        recruitmentInfo = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("projectid", projectid);
                return params;
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBack() {
        baseFinish();
    }


    public void goData() {
        selectquestionnaire.sendPostRequest(Urls.SelectQuestionnaire, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    final int code = jsonObject.getInt("code");
                    final String msg = jsonObject.getString("msg");
                    if (code == 200) {
                        JSONArray jsonArray = jsonObject.getJSONArray("datas");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            ProjectRecListInfo projectRecListInfo = new ProjectRecListInfo();
                            projectRecListInfo.setTaskid(object.getString("taskid"));
                            projectRecListInfo.setTaskbatch(object.getString("taskbatch"));
                            projectRecListInfo.setTaskname(object.getString("taskname"));
                            String tasktype = object.getString("tasktype");
                            projectRecListInfo.setTasktype(tasktype);
                            projectRecListInfo.setDatas(object.getString("datas"));
                            projectRecListInfo.setNote(object.getString("note"));
                            projectRecListInfo.setQuestionnaire_type(object.getString("questionnaire_type"));
                            projectRecListInfo.setPics(object.getString("pics"));
                            list.add(projectRecListInfo);
                        }
                        String relbatch = jsonObject.getString("relbatch");
                        if (!list.isEmpty()) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("list", list);
                            data.putExtra("data", bundle);
                            data.putExtra("projectid", projectid);
                            data.putExtra("relbatch", relbatch);
                            if ("5".equals(list.get(0).getTasktype())) {
                                data.setClass(ProjectRecruitmentActivity.this, TaskNewRecordillustrateActivity.class);
                                startActivity(data);
                                baseFinish();
                            } else if ("3".equals(list.get(0).getTasktype())) {
                                data.setClass(ProjectRecruitmentActivity.this, TaskNewEditActivity.class);
                                startActivity(data);
                                baseFinish();
                            }
                        }
                    } else {
                        Tools.showToast(ProjectRecruitmentActivity.this, msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        }, null);
    }

    public void getData() {
        recruitmentInfo.sendPostRequest(Urls.RecruitmentInfo, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        ((TextView) findViewById(R.id.projectrec_name)).setText(jsonObject.getString("title"));
                        ((TextView) findViewById(R.id.projectrec_period)).setText("招募周期：" + jsonObject.getString("begin_date") + "至" + jsonObject.getString("end_date"));
                        ((TextView) findViewById(R.id.projectrec_person)).setText("商家" + jsonObject.getString("company_name"));
                    } else {
                        Tools.showToast(ProjectRecruitmentActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }, null);
    }
}
