package com.orange.oy.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginCheckActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {
    private AppTitle logincheck_title;

    private void initTitle() {
        logincheck_title = (AppTitle) findViewById(R.id.logincheck_title);
        logincheck_title.settingName(getResources().getString(R.string.app_name));
        logincheck_title.showBack(this);
    }

    protected void onStop() {
        super.onStop();
        if (Sendsms != null) {
            Sendsms.stop(Urls.Sendsms);
        }
        if (Check != null) {
            Check.stop(Urls.Check);
        }
    }

    public void onBack() {
        baseFinish();
    }

    private void initNetworkConnection() {
        Sendsms = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("mobile", mobile);
                params.put("ident", "3");
                params.put("token", Tools.getToken());
                return params;
            }
        };
        Sendsms.setIsShowDialog(true);
        Check = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("mobile", mobile);
                params.put("vcode", logincheck_ch.getText().toString());
                params.put("token", Tools.getToken());
                params.put("name", getResources().getString(R.string.app_name));
                try {
                    params.put("versionnum", Tools.getVersionName(LoginCheckActivity.this));
                } catch (PackageManager.NameNotFoundException e) {
                    params.put("versionnum", "not found");
                }
                params.put("logintime", Tools.getTimeByPattern("yyyy-MM-dd HH-mm-ss"));
                params.put("comname", Tools.getDeviceType());
                params.put("phonemodle", Tools.getDeviceModel());
                params.put("sysversion", Tools.getSystemVersion() + "");
                params.put("operator", Tools.getCarrieroperator(LoginCheckActivity.this));
                params.put("resolution", Tools.getScreeInfoWidth(LoginCheckActivity.this) + "*" + Tools
                        .getScreeInfoHeight(LoginCheckActivity.this));
                params.put("mac", Tools.getLocalMacAddress(LoginCheckActivity.this));
                params.put("imei", Tools.getDeviceId(LoginCheckActivity.this));
                return params;
            }
        };
        Check.setIsShowDialog(true);
    }

    private View logincheck_view1, logincheck_view2;
    private TextView logincheck_item1;
    private NetworkConnection Sendsms, Check;
    private EditText logincheck_mobile, logincheck_ch;
    private String mobile;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logincheck);
        initTitle();
        Intent data = getIntent();
        if (data != null) {
            initNetworkConnection();
            mobile = data.getStringExtra("mobile");
            if (mobile != null && mobile.length() >= 11) {
                logincheck_view1 = findViewById(R.id.logincheck_view1);
                logincheck_view2 = findViewById(R.id.logincheck_view2);
                logincheck_item1 = (TextView) findViewById(R.id.logincheck_item1);
                logincheck_mobile = (EditText) findViewById(R.id.logincheck_mobile);
                logincheck_ch = (EditText) findViewById(R.id.logincheck_ch);
                String showMobile = mobile.substring(0, 3) + "******" + mobile.substring(9, 11);
                logincheck_item1.setText(String.format(getResources().getString(R.string.logincheck_item1), showMobile));
                logincheck_mobile.setText(showMobile);
                findViewById(R.id.logincheck_button1).setOnClickListener(this);
                findViewById(R.id.logincheck_button2).setOnClickListener(this);
//                findViewById(R.id.logincheck_button3).setOnClickListener(this);
            } else {
                Tools.showToast(this, "请输入手机号");
                baseFinish();
            }
        } else {
            baseFinish();
        }
    }

    private void Sendsms() {
        Sendsms.sendPostRequest(Urls.Sendsms, new Response.Listener<String>() {
            public void onResponse(String s) {
                try {
                    JSONObject job = new JSONObject(s);
                    int code = job.getInt("code");
                    if (code == 200) {
                        logincheck_view1.setVisibility(View.GONE);
                        logincheck_view2.setVisibility(View.VISIBLE);
                    } else {
                        Tools.showToast(LoginCheckActivity.this, job.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(LoginCheckActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(LoginCheckActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    private void Check() {
        Check.sendPostRequest(Urls.Check, new Response.Listener<String>() {
            public void onResponse(String s) {
                try {
                    JSONObject job = new JSONObject(s);
                    int code = job.getInt("code");
                    if (code == 200) {
                        JSONObject datas = job.getJSONObject("datas");
                        Intent intent = new Intent();
                        intent.putExtra("datas", datas.toString());
                        setResult(AppInfo.LoginCheckResultCode, intent);
                    } else {
                        Tools.showToast(LoginCheckActivity.this, job.getString("msg"));
                    }
                } catch (Exception e) {
                    Tools.showToast(LoginCheckActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
                baseFinish();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(LoginCheckActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logincheck_button1: {
                Sendsms();
            }
            break;
            case R.id.logincheck_button2: {
                if (TextUtils.isEmpty(logincheck_ch.getText().toString())) {
                    Tools.showToast(this, "请输入验证码");
                    return;
                }
                Check();
            }
            break;
//            case R.id.logincheck_button3: {
//                logincheck_view1.setVisibility(View.VISIBLE);
//                logincheck_view2.setVisibility(View.GONE);
//            }
//            break;
        }
    }
}
