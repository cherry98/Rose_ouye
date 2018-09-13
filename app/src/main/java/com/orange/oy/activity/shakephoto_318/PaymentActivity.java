package com.orange.oy.activity.shakephoto_318;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.createtask_317.TaskMouldActivity;
import com.orange.oy.activity.shakephoto_316.PutInTaskActivity;
import com.orange.oy.activity.shakephoto_316.ShakeAlbumActivity;
import com.orange.oy.aliapi.PayResult;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.ScreenManager;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * V3.18  支付
 */

public class PaymentActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, CompoundButton.OnCheckedChangeListener {

    private CheckBox cb_moneyEnough, cb_zhifubao;
    private String type;
    private TextView tvmoneyEnough, myaccount_button;
    private NetworkConnection sponsorshipPay;
    private String esfi_id;

    private void initNetworkConnection() {
        sponsorshipPay = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(PaymentActivity.this));
                params.put("esfi_id", esfi_id);  //赞助id
                params.put("type", type);  //缴纳方式，1 为账户余额，2 为支付宝
                return params;
            }
        };
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_case);
        initTitle();
        String account_money = getIntent().getStringExtra("account_money");
        String total_money = getIntent().getStringExtra("total_money");
        esfi_id = getIntent().getStringExtra("esfi_id");
        cb_zhifubao = (CheckBox) findViewById(R.id.cb_zhifubao);
        cb_moneyEnough = (CheckBox) findViewById(R.id.cb_moneyEnough);
        tvmoneyEnough = (TextView) findViewById(R.id.moneyEnough);
        if (Tools.StringToDouble(total_money) > Tools.StringToDouble(account_money)) {
            tvmoneyEnough.setText(account_money + "元  不足");
            cb_moneyEnough.setVisibility(View.GONE);
        } else {
            cb_moneyEnough.setVisibility(View.VISIBLE);
            tvmoneyEnough.setText(account_money);
        }
        ((TextView) findViewById(R.id.total_money)).setText("支付总额 " + total_money + "元");
        findViewById(R.id.myaccount_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //立即申请
                if (!cb_moneyEnough.isChecked() && !cb_zhifubao.isChecked()) {
                    Tools.showToast(PaymentActivity.this, "请选择支付方式");
                    return;
                }
                //选择哪种方式
                if (cb_moneyEnough.isChecked()) {
                    type = "1";
                    sponsorshipPay();
                } else if (cb_zhifubao.isChecked()) {
                    type = "2";
                    sponsorshipPay();
                }
            }
        });
        initNetworkConnection();
        cb_moneyEnough.setOnCheckedChangeListener(this);
        cb_zhifubao.setOnCheckedChangeListener(this);
    }

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.mydetail_title);
        appTitle.settingName("赞助红包");
        appTitle.showBack(this);
    }

    public void onStop() {
        super.onStop();
        if (sponsorshipPay != null) {
            sponsorshipPay.stop(Urls.SponsorPayConfirm);
        }
    }

    private String key;

    private void sponsorshipPay() {
        sponsorshipPay.sendPostRequest(Urls.SponsorPayConfirm, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                ConfirmDialog.dissmisDialog();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (type.equals("1")) {
                            Tools.showToast(getBaseContext(), "支付成功！");
                            ScreenManager.getScreenManager().finishActivity(SponsorActivity.class);
                            baseFinish();
                        } else {
                            JSONObject object = jsonObject.optJSONObject("data");
                            key = object.optString("key");  //支付宝支付需要用到的码"
                            aliPay(key);
                        }
                    } else {
                        Tools.showToast(getBaseContext(), jsonObject.optString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.d(e.toString());
                    Tools.showToast(PaymentActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(PaymentActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, false);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void aliPay(final String orderInfo) {
        Runnable payRunnable = new Runnable() {
            public void run() {
                PayTask alipay = new PayTask(PaymentActivity.this);
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_zhifubao: {
                if (cb_zhifubao.isChecked()) {
                    cb_moneyEnough.setChecked(false);
                }
            }
            break;
            case R.id.cb_moneyEnough: {
                if (cb_moneyEnough.isChecked()) {
                    cb_zhifubao.setChecked(false);
                }
            }
            break;
        }
    }

    @Override
    public void onBack() {
        baseFinish();
    }
}
