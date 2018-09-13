package com.orange.oy.activity.shakephoto_320;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.aliapi.PayResult;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.orange.oy.R.string.account_money;


/**
 * 商户账号
 */
public class ShopaccountActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle,
        View.OnClickListener {

    private void initTitle() {
        AppTitle titleview = (AppTitle) findViewById(R.id.titleview);
        titleview.showBack(this);
        titleview.settingName("商户账号");
    }


    public void onBack() {
        baseFinish();
    }

    protected void onStop() {
        super.onStop();
        if (merchantAccount != null) {
            merchantAccount.stop(Urls.MerchantAccount);
        }
    }

    private void initNetworkConnection() {
        merchantAccount = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(ShopaccountActivity.this));
                params.put("merchant_id", merchant_id);
                return params;
            }
        };
        merchantRecharge = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(ShopaccountActivity.this));
                params.put("merchant_id", merchant_id);  //商户id【必传】
                params.put("money", money);  //充值金额
                return params;
            }
        };
    }

    private LinearLayout lin_accountlist, lin_bill;
    private TextView myaccount_money, tv_recharge;
    private String merchant_id;
    private String account_moneys;
    private NetworkConnection merchantAccount, merchantRecharge;
    private String money;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopaccount);
        merchant_id = getIntent().getStringExtra("merchant_id");
        initTitle();
        initNetworkConnection();
        ScrollView myaccount_scrollview = (ScrollView) findViewById(R.id.myaccount_scrollview);
        myaccount_scrollview.smoothScrollTo(0, 20);

        lin_accountlist = (LinearLayout) findViewById(R.id.lin_accountlist);
        lin_bill = (LinearLayout) findViewById(R.id.lin_bill);
        tv_recharge = (TextView) findViewById(R.id.tv_recharge);
        myaccount_money = (TextView) findViewById(R.id.myaccount_money);

        tv_recharge.setOnClickListener(this);
        lin_accountlist.setOnClickListener(this);
        myaccount_money.setOnClickListener(this);
        lin_bill.setOnClickListener(this);
    }

    protected void onResume() {
        getData();
        super.onResume();
    }

    private String key;

    private void Pay() {
        merchantRecharge.sendPostRequest(Urls.MerchantRecharge, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                ConfirmDialog.dissmisDialog();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        JSONObject object = jsonObject.optJSONObject("data");
                        key = object.optString("key");  //支付宝支付需要用到的码"
                        aliPay(key);
                    } else {
                        Tools.showToast(getBaseContext(), jsonObject.optString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.d(e.toString());
                    Tools.showToast(ShopaccountActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(ShopaccountActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, false);
    }

    private void getData() {
        merchantAccount.sendPostRequest(Urls.MerchantAccount, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        jsonObject = jsonObject.getJSONObject("data");
                        account_moneys = jsonObject.getString("account_money"); // 账户余额

                        if (account_moneys != null) {
                            if (TextUtils.isEmpty(account_moneys)) {
                                account_moneys = "-";
                            } else {
                                double d = Tools.StringToDouble(account_moneys);
                                if (d - (int) d > 0) {
                                    account_moneys = String.valueOf(d);
                                } else {
                                    account_moneys = String.valueOf((int) d);
                                }
                            }
                            myaccount_money.setText(String.format(getResources().getString(account_money),
                                    "¥" + account_moneys));
                        } else {
                            myaccount_money.setText("-");
                        }

                    } else {
                        Tools.showToast(ShopaccountActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(ShopaccountActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(ShopaccountActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_recharge: {//充值
                //调起支付宝
                Intent intent = new Intent(this, PayCaseActivity.class);
                intent.putExtra("merchant_id", merchant_id);
                startActivity(intent);
            }
            break;
            case R.id.lin_accountlist: { //账号明细
                Intent intent = new Intent(this, AccountActivity.class);
                intent.putExtra("merchant_id", merchant_id);
                startActivity(intent);
            }
            break;


            case R.id.lin_bill: {//开发票

            }
            break;

        }
    }

    public void aliPay(final String orderInfo) {
        Runnable payRunnable = new Runnable() {
            public void run() {
                PayTask alipay = new PayTask(ShopaccountActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    private static final int SDK_PAY_FLAG = 1;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult(
                            (Map<String, String>) msg.obj);
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。

                        Tools.showToast(getBaseContext(), "支付成功");
                        baseFinish();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Tools.showToast(getBaseContext(), "支付失败！");
                        Tools.d(resultStatus);
                        // Tools.showToast(getBaseContext(), "支付失败！" + resultStatus);

                    }
                    break;
                }
                default:
                    break;
            }
        }

    };

}
