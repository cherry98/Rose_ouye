package com.orange.oy.activity;

import android.content.DialogInterface;
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
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 代理申请
 */
public class ProxyapplyActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {
    private AppTitle proxyp_title;

    private void initTitle() {
        proxyp_title = (AppTitle) findViewById(R.id.proxyp_title);
        proxyp_title.settingName(getResources().getString(R.string.proxyapple));
        proxyp_title.showBack(this);
    }

    protected void onStop() {
        super.onStop();
        if (sendData != null) {
            sendData.stop(Urls.Applyagent);
        }
    }

    private void initNetworkConnection() {
        sendData = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(ProxyapplyActivity.this));
                params.put("agentname", proxyap_name.getText().toString().trim());
                params.put("agentphone", proxyap_moblie.getText().toString().trim());
                params.put("agentemail", proxyap_mail.getText().toString().trim());
                params.put("agentqq", proxyap_qq.getText().toString().trim());
                params.put("agentprovince", proxyap_province.getText().toString());
                params.put("agentcity", proxyap_city.getText().toString());
                params.put("name", getResources().getString(R.string.app_name));
                try {
                    params.put("versionnum", Tools.getVersionName(ProxyapplyActivity.this));
                } catch (PackageManager.NameNotFoundException e) {
                    params.put("versionnum", "not found");
                }
                params.put("logintime", Tools.getTimeByPattern("yyyy-MM-dd HH-mm-ss"));
                params.put("comname", Tools.getDeviceType());
                params.put("phonemodle", Tools.getDeviceModel());
                params.put("sysversion", Tools.getSystemVersion() + "");
                params.put("operator", Tools.getCarrieroperator(ProxyapplyActivity.this));
                params.put("resolution", Tools.getScreeInfoWidth(ProxyapplyActivity.this) + "*" + Tools
                        .getScreeInfoHeight(ProxyapplyActivity.this));
                params.put("mac", Tools.getLocalMacAddress(ProxyapplyActivity.this));
                params.put("imei", Tools.getDeviceId(ProxyapplyActivity.this));
                return params;
            }
        };
        sendData.setIsShowDialog(true);
    }

    private EditText proxyap_name, proxyap_moblie, proxyap_mail, proxyap_qq;
    private TextView proxyap_province, proxyap_city;
    private NetworkConnection sendData;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxyapply);
        initNetworkConnection();
        initTitle();
        proxyap_name = (EditText) findViewById(R.id.proxyap_name);
        proxyap_moblie = (EditText) findViewById(R.id.proxyap_moblie);
        proxyap_mail = (EditText) findViewById(R.id.proxyap_mail);
        proxyap_qq = (EditText) findViewById(R.id.proxyap_qq);
        proxyap_province = (TextView) findViewById(R.id.proxyap_province);
        proxyap_city = (TextView) findViewById(R.id.proxyap_city);
        findViewById(R.id.proxyap_submit).setOnClickListener(this);
        proxyap_province.setOnClickListener(this);
        proxyap_city.setOnClickListener(this);
    }

    private void sendData() {
        sendData.sendPostRequest(Urls.Applyagent, new Response.Listener<String>() {
            public void onResponse(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = 0;
                    code = jsonObject.getInt("code");
                    if (code == 200) {
//                        Tools.showToast(ProxyapplyActivity.this, jsonObject.getString("msg"));
                        ConfirmDialog.showDialog(ProxyapplyActivity.this, "您的申请已成功提交",
                                "我们会在您提交申请后的3个工作日内，给予审核结果，请您耐心等待！谢谢您的支持！", null, null, null,
                                true, null);
                        if (ConfirmDialog.getDialog() != null) {
                            ConfirmDialog.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
                                public void onDismiss(DialogInterface dialog) {
                                    baseFinish();
                                }
                            });
                        }
                    } else {
                        Tools.showToast(ProxyapplyActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(ProxyapplyActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(ProxyapplyActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, "正在提交...");
    }

    public void onBack() {
        baseFinish();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.proxyap_submit: {
                if (TextUtils.isEmpty(proxyap_name.getText())) {
                    Tools.showToast(this, getResources().getString(R.string.proxyapple_error1));
                    return;
                }
                if (TextUtils.isEmpty(proxyap_moblie.getText())) {
                    Tools.showToast(this, getResources().getString(R.string.proxyapple_error2));
                    return;
                }
                if (TextUtils.isEmpty(proxyap_province.getText())) {
                    Tools.showToast(this, getResources().getString(R.string.proxyapple_error4));
                    return;
                }
                if (TextUtils.isEmpty(proxyap_city.getText())) {
                    Tools.showToast(this, getResources().getString(R.string.proxyapple_error5));
                    return;
                }
                sendData();
            }
            break;
            case R.id.proxyap_province: {
                Intent intent = new Intent(this, SelectDistrictActivity.class);
                intent.putExtra("flag", 0);
                startActivityForResult(intent, 200);
            }
            break;
            case R.id.proxyap_city: {
                if (proxyap_province.getTag() == null) {
                    Tools.showToast(this, "请先选择省份");
                    return;
                }
                Intent intent = new Intent(this, SelectDistrictActivity.class);
                intent.putExtra("flag", 1);
                intent.putExtra("provinceId", proxyap_province.getTag().toString());
                startActivityForResult(intent, 200);
            }
            break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 200) {
            switch (resultCode) {
                case AppInfo.SelectDistrictResultCode1: {
                    if (data != null) {
                        proxyap_province.setTag(data.getStringExtra("id"));
                        proxyap_province.setText(data.getStringExtra("name"));
                        proxyap_city.setTag(null);
                        proxyap_city.setText("");
                    }
                }
                break;
                case AppInfo.SelectDistrictResultCode2: {
                    if (data != null) {
                        proxyap_city.setTag(data.getStringExtra("id"));
                        proxyap_city.setText(data.getStringExtra("name"));
                    }
                }
                break;
            }
        }
    }
}
