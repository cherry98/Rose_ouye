package com.orange.oy.activity.newtask;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.TaskitemDetailActivity_12;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.fragment.TaskNewFragment;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.sobot.chat.SobotApi;
import com.sobot.chat.api.model.Information;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.orange.oy.R.id.nooutlets_preview;

/**
 * 无店单项目申请页面
 */
public class NoOutletsActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.nooutlets_title);
        appTitle.settingName("项目说明");
        appTitle.showBack(this);
        appTitle.showIllustrate(R.mipmap.kefu, new AppTitle.OnExitClickForAppTitle() {
            @Override
            public void onExit() {
                Information info = new Information();
                info.setAppkey(Urls.ZHICHI_KEY);
                info.setColor("#FFFFFF");
                if (TextUtils.isEmpty(AppInfo.getKey(NoOutletsActivity.this))) {
                    info.setUname("游客");
                } else {
                    String netHeadPath = AppInfo.getUserImagurl(NoOutletsActivity.this);
                    info.setFace(netHeadPath);
                    info.setUid(AppInfo.getKey(NoOutletsActivity.this));
                    info.setUname(AppInfo.getUserName(NoOutletsActivity.this));
                }
                SobotApi.startSobotChat(NoOutletsActivity.this, info);
            }
        });
    }

    private void initNetworkConnection() {
        checkapply = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                if (!TextUtils.isEmpty(AppInfo.getKey(NoOutletsActivity.this))) {
                    params.put("usermobile", AppInfo.getName(NoOutletsActivity.this));
                    params.put("token", Tools.getToken());
                }
                params.put("projectid", projectid);
                return params;
            }
        };
        applyNoOutletsProject = new NetworkConnection(NoOutletsActivity.this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(NoOutletsActivity.this));
                params.put("projectId", projectid);
                return params;
            }
        };
        applyNoOutletsProject.setIsShowDialog(true);
        checkinvalid = new NetworkConnection(NoOutletsActivity.this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(NoOutletsActivity.this));
                params.put("token", Tools.getToken());
                params.put("outletid", outletId);
                params.put("lon", longitude);
                params.put("lat", latitude);
                params.put("address", address);
                return params;
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (checkinvalid != null) {
            checkinvalid.stop(Urls.CheckInvalid);
        }
        if (applyNoOutletsProject != null) {
            applyNoOutletsProject.stop(Urls.ApplyNoOutletsProject);
        }
        if (checkapply != null) {
            checkapply.stop(Urls.CheckApply);
        }
    }

    private Intent data;
    private String projectid;
    private NetworkConnection checkapply, applyNoOutletsProject, checkinvalid;
    private String address, latitude, longitude, outletId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_outlets);
        initTitle();
        data = getIntent();
        if (data == null) {
            Tools.showToast(this, "缺少参数");
            return;
        }
        String preview = getIntent().getStringExtra("preview");
        if (TextUtils.isEmpty(preview)) {
            findViewById(R.id.nooutlets_preview).setVisibility(View.GONE);
        }
        address = data.getStringExtra("address");
        latitude = data.getStringExtra("latitude");
        longitude = data.getStringExtra("longitude");
        initNetworkConnection();
        projectid = data.getStringExtra("projectid");
        ((TextView) findViewById(R.id.nooutlets_name)).setText(data.getStringExtra("projectname"));
        WebView nooutlets_webview = (WebView) findViewById(R.id.nooutlets_webview);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            nooutlets_webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        nooutlets_webview.loadUrl(Urls.Standard + "?projectid=" + projectid);
        findViewById(R.id.nooutlets_button).setOnClickListener(this);
        findViewById(nooutlets_preview).setOnClickListener(this);
        boolean isEdit = data.getBooleanExtra("isEdit", false);
        if (isEdit) {
            ConfirmDialog.showDialog(this, "恭喜您！", 2, "报名成功，您可以申请任务啦！", null, "立即申请", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                @Override
                public void leftClick(Object object) {

                }

                @Override
                public void rightClick(Object object) {

                }
            }).goneLeft();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ConfirmDialog.dissmisDialog();
                }
            }, 3000);
        }
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.nooutlets_button) {
            checkapply();
        } else if (v.getId() == nooutlets_preview) {
            Intent intent = new Intent(this, TaskitemDetailActivity_12.class);
            intent.putExtra("id", data.getStringExtra("id"));
            intent.putExtra("projectname", data.getStringExtra("projectname"));
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
            intent.putExtra("project_type", "5");
            intent.putExtra("is_desc", "");
            intent.putExtra("index", "0");
            startActivity(intent);
            baseFinish();
        }
    }

    private void applyNoOutletsProject() {
        applyNoOutletsProject.sendPostRequest(Urls.ApplyNoOutletsProject, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    String msg = jsonObject.getString("msg");
                    outletId = jsonObject.getString("outletId");
                    if (code == 200) {
                        ConfirmDialog.showDialog(NoOutletsActivity.this, "申请成功", 2, msg, "继续申请", "现在去做", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                            @Override
                            public void leftClick(Object object) {
                                TaskNewFragment.isRefresh = true;
                                baseFinish();
                            }

                            @Override
                            public void rightClick(Object object) {
                                doSelectType();
                            }
                        });
                    } else if (code == 3) {
                        ConfirmDialog.showDialog(NoOutletsActivity.this, "申请成功", 2, msg, null, "现在去做", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                            @Override
                            public void leftClick(Object object) {

                            }

                            @Override
                            public void rightClick(Object object) {
                                doSelectType();
                            }
                        }).goneLeft();
                    } else if (code == 2) {
                        ConfirmDialog.showDialog(NoOutletsActivity.this, "申请失败", msg, null, "确定", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                            @Override
                            public void leftClick(Object object) {

                            }

                            @Override
                            public void rightClick(Object object) {
                            }
                        }).goneLeft();
                    } else {
                        Tools.showToast(NoOutletsActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(NoOutletsActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(NoOutletsActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private void doSelectType() {
        checkinvalid.sendPostRequest(Urls.CheckInvalid, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        doExecute();
                    } else if (code == 2) {
                        ConfirmDialog.showDialog(NoOutletsActivity.this, null, jsonObject.getString("msg"), null,
                                "确定", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                    @Override
                                    public void leftClick(Object object) {

                                    }

                                    @Override
                                    public void rightClick(Object object) {
                                    }
                                }).goneLeft();
                    } else if (code == 3) {
                        ConfirmDialog.showDialog(NoOutletsActivity.this, null, jsonObject.getString("msg"), "取消",
                                "继续执行", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                                    @Override
                                    public void leftClick(Object object) {

                                    }

                                    @Override
                                    public void rightClick(Object object) {
                                        doExecute();
                                    }
                                });
                    } else {
                        Tools.showToast(NoOutletsActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(NoOutletsActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(NoOutletsActivity.this, getResources().getString(R.string
                        .network_volleyerror));
            }
        }, null);
    }

    private void doExecute() {
        data.setClass(NoOutletsActivity.this, TaskitemDetailActivity_12.class);
        data.putExtra("id", outletId);
        data.putExtra("project_id", projectid);
        data.putExtra("store_name", "网点名称");
        data.putExtra("store_num", "网点编号");
        data.putExtra("project_type", "5");
        startActivity(data);
        baseFinish();
    }

    private void checkapply() {
        checkapply.sendPostRequest(Urls.CheckApply, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (!TextUtils.isEmpty(AppInfo.getKey(NoOutletsActivity.this))) {
                        ConfirmDialog.showDialog(NoOutletsActivity.this, "是否确认申请该网点？", true, new ConfirmDialog.OnSystemDialogClickListener() {
                            @Override
                            public void leftClick(Object object) {

                            }

                            @Override
                            public void rightClick(Object object) {
                                applyNoOutletsProject();
                            }
                        });
                    } else if (jsonObject.getInt("code") == 1) {//点击进入招募令
                        data.setClass(NoOutletsActivity.this, ProjectRecruitmentActivity.class);
                        startActivity(data);
                        baseFinish();
                    }
                } catch (JSONException e) {
                    Tools.showToast(NoOutletsActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(NoOutletsActivity.this, getResources().getString(R.string.network_error));
            }
        }, null);
    }
}
