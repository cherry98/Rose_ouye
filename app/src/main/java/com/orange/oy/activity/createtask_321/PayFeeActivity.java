package com.orange.oy.activity.createtask_321;

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
import com.orange.oy.activity.mycorps_315.CorpGrabActivity;
import com.orange.oy.activity.shakephoto_318.PutInTask2Activity;
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

import static com.orange.oy.R.id.total_money;

/***
 *    V3.21 追加费用------支付
 */
public class PayFeeActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, CompoundButton.OnCheckedChangeListener {

    private CheckBox cb_moneyEnough, cb_zhifubao;
    private String type;
    private TextView tvmoneyEnough, myaccount_button, tv_fee, tv_service;
    private NetworkConnection addpricePayConfirm;
    private String project_id, account_money, additional_id;
    private double Allprice;

    private void initNetworkConnection() {
        addpricePayConfirm = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(PayFeeActivity.this));
                params.put("project_id", project_id);  //活动id
                params.put("additional_id", additional_id);  //项目加价信息id
                params.put("type", type);  //缴纳方式，1 为账户余额，2 为支付宝
                return params;
            }
        };
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_fee);
        initTitle();
        account_money = getIntent().getStringExtra("account_money");   // 账户余额
        additional_id = getIntent().getStringExtra("additional_id");
        project_id = getIntent().getStringExtra("project_id");
        Allprice = getIntent().getDoubleExtra("Allprice", 0);
        String total_money = String.valueOf(Tools.add(Allprice,(Tools.mul(Allprice, 0.1))));

        cb_zhifubao = (CheckBox) findViewById(R.id.cb_zhifubao);
        cb_moneyEnough = (CheckBox) findViewById(R.id.cb_moneyEnough);
        tvmoneyEnough = (TextView) findViewById(R.id.moneyEnough);
        tv_fee = (TextView) findViewById(R.id.tv_fee); // 追加费
        tv_service = (TextView) findViewById(R.id.tv_service);  //平台服务费

        tv_fee.setText(Allprice + "元");
        tv_service.setText((Tools.mul(Allprice, 0.1)) + "元");

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
                    Tools.showToast(PayFeeActivity.this, "请选择支付方式");
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
        appTitle.settingName("支付");
        appTitle.showBack(this);
    }

    public void onStop() {
        super.onStop();
        if (addpricePayConfirm != null) {
            addpricePayConfirm.stop(Urls.AddpricePayConfirm);
        }
    }

    private String key;

    private void sponsorshipPay() {
        addpricePayConfirm.sendPostRequest(Urls.AddpricePayConfirm, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                ConfirmDialog.dissmisDialog();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        JSONObject object = jsonObject.optJSONObject("data");
                        key = object.optString("key");  //支付宝支付需要用到的码"
                        if (TextUtils.isEmpty(key) || "null".equals(key)) {
                            Tools.showToast(getBaseContext(), "支付成功!");
                            ScreenManager screenManager = ScreenManager.getScreenManager();
                            screenManager.finishActivity(AddfeeActivity.class);
                            baseFinish();
                        } else {
                            //去第三方支付
                            aliPay(key);
                        }

                    } else {
                        Tools.showToast(getBaseContext(), jsonObject.optString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.d(e.toString());
                    Tools.showToast(PayFeeActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(PayFeeActivity.this, getResources().getString(R.string.network_volleyerror));
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
                PayTask alipay = new PayTask(PayFeeActivity.this);
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

                        /*ScreenManager.getScreenManager().finishActivity(TaskMouldActivity.class);
                        Intent intent = new Intent(PayFeeActivity.this, PutInTask2Activity.class);
                        intent.putExtra("activity_status", "2");
                        startActivity(intent);
                        baseFinish();*/
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
