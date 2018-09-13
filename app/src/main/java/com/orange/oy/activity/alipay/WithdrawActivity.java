package com.orange.oy.activity.alipay;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.newtask.DutyFreeMethodActivity;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.GetvcodeDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 提现操作页面
 */
public class WithdrawActivity extends BaseActivity implements View.OnClickListener, AppTitle.OnBackClickForAppTitle {


    private EditText withdraw_edittext;
    private String withdrawalmoney;
    private NetworkConnection getmoney;
    private int duty_free;

    @Override
    protected void onStop() {
        super.onStop();
        if (getmoney != null) {
            getmoney.stop(Urls.NewGetMoney);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        AppTitle appTitle = (AppTitle) findViewById(R.id.withdraw_title);
        appTitle.settingName("我的奖金");
        appTitle.showBack(this);
        Intent data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        initNetWork();
        duty_free = (int) Double.parseDouble(data.getStringExtra("duty_free"));
        withdrawalmoney = data.getStringExtra("withdrawalmoney");
        ((TextView) findViewById(R.id.withdraw_money)).setText("¥" + withdrawalmoney);
        withdraw_edittext = (EditText) findViewById(R.id.withdraw_edittext);
        findViewById(R.id.withdraw_all).setOnClickListener(this);
        findViewById(R.id.withdraw_button).setOnClickListener(this);
    }

    private void initNetWork() {
        getmoney = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("money", money + "");
                params.put("type", "0");//提现方式  0个人交税，1邀请好友,此项必填。
                params.put("vcode", getvcodeDialog.getIdentifycode_code().getText().toString());
                return params;
            }
        };
        getmoney.setIsShowDialog(true);
    }

    private int money;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.withdraw_all: {
                withdraw_edittext.setText(withdrawalmoney);
            }
            break;
            case R.id.withdraw_button: {
                if (TextUtils.isEmpty(withdraw_edittext.getText().toString().trim())) {
                    Tools.showToast(this, "请先输入您要提现的金额~");
                    return;
                }
                if (!isDigits(withdraw_edittext.getText().toString().trim())) {
                    Tools.showToast(this, "请输入整数~");
                    return;
                }
                money = Tools.StringToInt(withdraw_edittext.getText().toString().trim());//输入的金额
                double price = Double.parseDouble(withdrawalmoney);
                int withdrawal = (int) price;//总金额
                if (money > withdrawal) {
                    Tools.showToast(WithdrawActivity.this, "提现金额不能大于总余额~");
                    return;
                }

                if (duty_free != -1) {
                    if (duty_free < money) {
                        Intent intent = new Intent(WithdrawActivity.this, DutyFreeMethodActivity.class);
                        intent.putExtra("money", money + "");
                        startActivity(intent);
                    } else {
                        getvcodeDialog = GetvcodeDialog.ShowGetvcodeDialog(WithdrawActivity.this);
                        getvcodeDialog.getWithdraw_button().setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                if (getvcodeDialog != null)
                                    sendData();//提现操作
                            }
                        });
                    }
                } else {
                    getvcodeDialog = GetvcodeDialog.ShowGetvcodeDialog(WithdrawActivity.this);
                    getvcodeDialog.getWithdraw_button().setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            if (getvcodeDialog != null)
                                sendData();//提现操作
                        }
                    });
                }
            }
            break;
        }
    }

    private GetvcodeDialog getvcodeDialog;

    //判断一个字符串是否为整数
    public static boolean isDigits(String str) {
        return str.matches("[-+]?[0-9]*");
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    private void sendData() {//提现操作
        getmoney.sendPostRequest(Urls.NewGetMoney, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Tools.showToast(WithdrawActivity.this, "提现成功");
                        GetvcodeDialog.dismiss();
                        baseFinish();
                    } else {
                        Tools.showToast(WithdrawActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(WithdrawActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(WithdrawActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, false);
    }
}
