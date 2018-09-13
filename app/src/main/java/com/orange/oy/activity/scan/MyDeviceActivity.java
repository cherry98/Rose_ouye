package com.orange.oy.activity.scan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.alipay.SelectprojectActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.fragment.MyFragment;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝，VR绑定页
 */
public class MyDeviceActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {
    public void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.mydevice_title);
        if (type.equals("1")) {
            appTitle.settingName("绑定VR设备");
        } else if (type.equals("2")) {
            appTitle.settingName("支付宝绑定");
//            if ("0".equals(data.getStringExtra("isskip"))) {
//                appTitle.settingExit("跳过", this);
//            }
        }
        appTitle.showBack(this);
    }

    private void initNetworkConnection() {
        bindPayAccount = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(MyDeviceActivity.this));
                params.put("payaccount", payaccount);
                return params;
            }
        };
        bindPayAccount.setIsShowDialog(true);
        bindVR = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(MyDeviceActivity.this));
                params.put("vrid", vrid);
                return params;
            }
        };
        bindVR.setIsShowDialog(true);
    }

    private String type;
    private String payaccount, vrid;
    private EditText mydevice_edit;
    private NetworkConnection bindPayAccount, bindVR;
    private Intent data;
    public static MyDeviceActivity myDeviceActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_device);
        myDeviceActivity = this;
        data = getIntent();
        if (data == null) {
            return;
        }
        initNetworkConnection();
        type = data.getStringExtra("type");
        if (type.equals("1")) {
            ((TextView) findViewById(R.id.mydevice_desc)).setText(R.string.mydevice_vr);
        } else {
            ((TextView) findViewById(R.id.mydevice_desc)).setText(R.string.mydevice_alipay);
            if (isBindCard()) {
                Intent intent = new Intent(MyDeviceActivity.this, IdentityActivity.class);
                intent.putExtra("isback", "0");
                intent.putExtra("ismyaccount", "0");
                startActivity(intent);
            }
        }
        ((TextView) findViewById(R.id.mydevice_text)).setText(data.getStringExtra("texthint"));
        mydevice_edit = (EditText) findViewById(R.id.mydevice_edit);
        mydevice_edit.setHint(data.getStringExtra("edithint"));
        initTitle();
        findViewById(R.id.mydevicesure_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("1".equals(type)) {//VR设备状态
                    if (!TextUtils.isEmpty(mydevice_edit.getText().toString()) && !"null".equals(mydevice_edit.getText().toString())) {
                        vrid = mydevice_edit.getText().toString();
                        sendVRData();
                    } else {
                        Tools.showToast(MyDeviceActivity.this, "请输入您的VR设备ID");
                    }
                } else if ("2".equals(type)) {//绑定支付宝
                    payaccount = mydevice_edit.getText().toString();
                    if (!TextUtils.isEmpty(payaccount) && !payaccount.equals("null")) {
                        sendPayData();
                    } else {
                        Tools.showToast(MyDeviceActivity.this, "请输入您的支付宝账号");
                    }
                }
            }
        });
    }

    public boolean isBindCard() {
        boolean isAccount = AppInfo.isBindidCard(MyDeviceActivity.this);
        return isAccount;
    }

    private void sendVRData() {
        bindVR.sendPostRequest(Urls.BindVR, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                CustomProgressDialog.Dissmiss();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Intent intent = new Intent(MyDeviceActivity.this, VRDeviceillActivity.class);
                        intent.putExtra("vrid", vrid);
                        startActivity(intent);
                        MyFragment.isRefresh = true;
                        baseFinish();
                    } else {
                        Tools.showToast(MyDeviceActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(MyDeviceActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(MyDeviceActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        }, null);
    }


    private void sendPayData() {
        bindPayAccount.sendPostRequest(Urls.BindPayAccount, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                CustomProgressDialog.Dissmiss();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Tools.showToast(MyDeviceActivity.this, "绑定成功");
                        MyFragment.isRefresh = true;
                        SelectprojectActivity.isRefresh = true;
                        baseFinish();
                    } else {
                        Tools.showToast(MyDeviceActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(MyDeviceActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(MyDeviceActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        }, null);
    }

    @Override
    public void onBack() {
        baseFinish();
    }
}
