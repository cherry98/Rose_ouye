package com.orange.oy.activity.scan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
 * 身份证信息验证页--后改成支付宝认证页面
 */
public class IdentityActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle,
        View.OnClickListener {

    public void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.identity_title);
        appTitle.settingName("绑卡领取奖励金");
        appTitle.showBack(this);
    }

    public void initNetworkConnection() {
        bindPayAccount = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(IdentityActivity.this));
                params.put("payaccount", alipay_id);
                return params;
            }
        };
        bindPayAccount.setIsShowDialog(true);
    }

    private EditText identitytext_alipay;
    private NetworkConnection bindPayAccount;
    private String alipay_id, payaccount, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity);
        Intent data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        name = data.getStringExtra("name");
        ((TextView) findViewById(R.id.identity_name)).setText(name);
        identitytext_alipay = (EditText) findViewById(R.id.identitytext_alipay);
        payaccount = data.getStringExtra("payaccount");
        if (!TextUtils.isEmpty(payaccount) && !"null".equals(payaccount)) {
            identitytext_alipay.setText(payaccount);
            identitytext_alipay.setFocusable(false);
            identitytext_alipay.setEnabled(false);
            findViewById(R.id.identitytext_edit).setVisibility(View.VISIBLE);
        } else {
            identitytext_alipay.setFocusable(true);
            identitytext_alipay.setEnabled(true);
            identitytext_alipay.setFocusableInTouchMode(true);
            findViewById(R.id.identitytext_edit).setVisibility(View.GONE);
        }
        initTitle();
        initNetworkConnection();
        findViewById(R.id.identity_button).setOnClickListener(this);
        findViewById(R.id.identity_main).setOnClickListener(this);
        findViewById(R.id.identitytext_edit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.identity_button: {
                alipay_id = identitytext_alipay.getText().toString();
                if (payaccount != null) {
                    if (payaccount.equals(alipay_id)) {
                        Tools.showToast(this, "请先修改您的支付宝账号");
                        return;
                    }
                }
                if (alipay_id == null || "".equals(alipay_id)) {
                    Tools.showToast(this, "请填写您的支付宝账号");
                    return;
                }
                sendData();
            }
            break;
            case R.id.identity_main: {
                InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            break;
            case R.id.identitytext_edit: {
                identitytext_alipay.setFocusable(true);
                identitytext_alipay.setEnabled(true);
                identitytext_alipay.setFocusableInTouchMode(true);
                identitytext_alipay.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
            }
            break;
        }
    }

    private void sendData() {//支付宝页面绑定成功
        bindPayAccount.sendPostRequest(Urls.BindPayAccount, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        ConfirmDialog.showDialog(IdentityActivity.this, null, 1, "恭喜您，绑定成功！", null, "我知道了", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                            @Override
                            public void leftClick(Object object) {

                            }

                            @Override
                            public void rightClick(Object object) {
                                baseFinish();
                            }
                        }).goneLeft();
                    } else {
                        Tools.showToast(IdentityActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(IdentityActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(IdentityActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        }, null);
    }

    @Override
    public void onBack() {
        baseFinish();
    }
}
