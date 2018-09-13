package com.orange.oy.activity.shakephoto_320;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.alipay.sdk.app.PayTask;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.createtask_317.TaskMouldActivity;
import com.orange.oy.activity.shakephoto_318.PutInTask2Activity;
import com.orange.oy.aliapi.PayResult;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.ScreenManager;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.orange.oy.R.id.ed_storeNum;
import static com.orange.oy.R.id.ed_storeUrl;

/***
 * V3.20  商户账号-----账户充值
 */
public class PayCaseActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {
    public void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.title);
        appTitle.settingName("充值");
        appTitle.showBack(this);
    }

    public void initNetworkConnection() {
        merchantRecharge = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(PayCaseActivity.this));
                params.put("merchant_id", merchant_id);  //商户id
                params.put("money", money); //充值金额【必传】
                Tools.d(params.toString());
                return params;
            }
        };
    }

    private NetworkConnection merchantRecharge;
    private EditText ed_name, ed_money;
    private String merchant_id, store_name, money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_pay);
        initTitle();
        initNetworkConnection();
        merchant_id = getIntent().getStringExtra("merchant_id");
        ed_name = (EditText) findViewById(R.id.ed_name);
        ed_money = (EditText) findViewById(R.id.ed_money);


        findViewById(R.id.btn_commit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                store_name = ed_name.getText().toString().trim();
                money = ed_money.getText().toString().trim();

               /* if (Tools.isEmpty(store_name)) {
                    Tools.showToast(PayCaseActivity.this, "请填写收款户名~");
                    return;
                }*/
                if (Tools.isEmpty(money)) {
                    Tools.showToast(PayCaseActivity.this, "请填写转账金额~");
                    return;
                }
                sendData();
            }
        });

    }

    private String key;

    private void sendData() {
        merchantRecharge.sendPostRequest(Urls.MerchantRecharge, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
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
                    Tools.showToast(PayCaseActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(PayCaseActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        }, null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != merchantRecharge) {
            merchantRecharge.stop(Urls.MerchantRecharge);
        }
    }

    public void aliPay(final String orderInfo) {
        Runnable payRunnable = new Runnable() {
            public void run() {
                PayTask alipay = new PayTask(PayCaseActivity.this);
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
                        // Tools.showToast(getBaseContext(), "支付失败！" + resultStatus);
                        Tools.d("resultStatus::::" + resultStatus);

                    }
                    break;
                }
                default:
                    break;
            }
        }

    };

    @Override
    public void onBack() {
        baseFinish();
    }
}
