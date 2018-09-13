package com.orange.oy.activity.mycorps_314;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.aliapi.PayResult;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.AppDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.ConfirmPayDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.sobot.chat.SobotApi;
import com.sobot.chat.api.model.Information;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 个人认证
 * <p>
 * V3.14
 */
public class IdentityMyActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle
        , View.OnClickListener {
    private AppTitle taskILL_title;
    private String teamId;

    public void onBack() {
        baseFinish();
    }

    private void initTitle() {
        teamId = getIntent().getStringExtra("teamId");  //战队id
        taskILL_title = (AppTitle) findViewById(R.id.taskILL_title);
        taskILL_title.settingName(getResources().getString(R.string.identityMy));
        taskILL_title.showBack(this);
        taskILL_title.showIllustrate(R.mipmap.kefu, new AppTitle.OnExitClickForAppTitle() {
            public void onExit() {
                Information info = new Information();
                info.setAppkey(Urls.ZHICHI_KEY);
                info.setColor("#FFFFFF");
                if (TextUtils.isEmpty(AppInfo.getKey(IdentityMyActivity.this))) {
                    info.setUname("游客");
                } else {
                    String netHeadPath = AppInfo.getUserImagurl(IdentityMyActivity.this);
                    info.setFace(netHeadPath);
                    info.setUid(AppInfo.getKey(IdentityMyActivity.this));
                    info.setUname(AppInfo.getUserName(IdentityMyActivity.this));
                }
                SobotApi.startSobotChat(IdentityMyActivity.this, info);
            }
        });
    }

    private void initNetworkConnection() {

        psersonalMoney = new NetworkConnection(IdentityMyActivity.this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(IdentityMyActivity.this));
                params.put("team_id", teamId);
                params.put("type", type);//type	缴纳方式，1为账户余额，2为支付宝
                return params;
            }
        };

        psersonalAuth = new NetworkConnection(IdentityMyActivity.this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(IdentityMyActivity.this));
                params.put("token", Tools.getToken());
                return params;
            }
        };
    }

    private Button taskILL_button;
    private TextView taskILL_name, tv_CashDeposit;
    private WebView taskILL_webview;
    private AppDBHelper appDBHelper;
    private Intent data;
    private String msg;
    private String type;
    private NetworkConnection psersonalMoney, psersonalAuth;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity_myself);
        tv_CashDeposit = (TextView) findViewById(R.id.tv_CashDeposit);
        taskILL_button = (Button) findViewById(R.id.taskILL_button);
        taskILL_button.setOnClickListener(this);
        initTitle();
        data = getIntent();
        if (data == null) {
            Tools.showToast(this, "缺少参数");
            return;
        }

        initNetworkConnection();
        getpsersonalAuth();

        taskILL_webview = (WebView) findViewById(R.id.taskILL_webview);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            taskILL_webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        taskILL_webview.loadUrl(Urls.margin);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.taskILL_button) {
            //=========弹框

            ConfirmPayDialog.showDialogForPay(IdentityMyActivity.this, "请选择缴纳方式！", msg, null,
                    true, new ConfirmPayDialog.OnSystemDialogClickListener() {
                        @Override
                        public void rightClick(Object object, boolean moneyEnough, boolean zhifubao) {
                            Tools.d("tag", "moneyEnough======>>>>" + moneyEnough);
                            Tools.d("tag", "==================================>>>>");
                            Tools.d("tag", "zhifubao======>>>>" + zhifubao);
                            //选择哪种方式
                            if (moneyEnough == true) {
                                type = "1";
                                getPsersonalMoney();
                            } else if (zhifubao == true) {
                                type = "2";
                                getPsersonalMoney();
                            }

                        }
                    });
        }
    }

    private String key;

    private void getPsersonalMoney() {
        psersonalMoney.sendPostRequest(Urls.PSERSONALMoney, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                ConfirmDialog.dissmisDialog();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (!TextUtils.isEmpty(AppInfo.getKey(IdentityMyActivity.this))) {
                        if (jsonObject.getInt("code") == 200) {

                            JSONObject object = jsonObject.optJSONObject("data");
                            key = object.optString("key");  //支付宝支付需要用到的码"
                            if (TextUtils.isEmpty(key) || "null".equals(key)) {
                                Tools.showToast(getBaseContext(), "支付成功！");
                                finish();
                            } else {
                                //去第三方支付
                                aliPay(key, mHandler);
                            }
                        } else {
                            Tools.showToast(getBaseContext(), jsonObject.optString("msg"));
                        }
                    }

                } catch (JSONException e) {
                    Tools.d(e.toString());
                    Tools.showToast(IdentityMyActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(IdentityMyActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    private void getpsersonalAuth() {
        psersonalAuth.sendPostRequest(Urls.PSERSONALAUTH, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (!TextUtils.isEmpty(AppInfo.getKey(IdentityMyActivity.this))) {
                        if (jsonObject.getInt("code") == 200) {
                            JSONObject object = jsonObject.optJSONObject("data");
                            Double money, accountMoney;
                            accountMoney = object.optDouble("user_account"); ////余额
                            money = object.optDouble("money"); //保证金
                            tv_CashDeposit.setText(object.getString("money") + "元");
                            if (accountMoney < money) {
                                msg = accountMoney + "  不足";
                            }
                        }
                    }

                } catch (JSONException e) {
                    Tools.showToast(IdentityMyActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(IdentityMyActivity.this, getResources().getString(R.string.network_error));
            }
        }, null);
    }


    public void aliPay(final String orderInfo, final Handler mHandler) {
        Runnable payRunnable = new Runnable() {
            public void run() {
                PayTask alipay = new PayTask(IdentityMyActivity.this);
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
                        finish();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Tools.showToast(getBaseContext(), "支付失败！");

                    }
                    break;
                }
                default:
                    break;
            }
        }

    };


}