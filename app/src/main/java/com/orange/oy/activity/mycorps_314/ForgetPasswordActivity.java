package com.orange.oy.activity.mycorps_314;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

/**
 * 忘记密码页
 */
public class ForgetPasswordActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View
        .OnClickListener {
    private AppTitle forgetpw_title;

    private void initTitle() {
        forgetpw_title = (AppTitle) findViewById(R.id.forgetpw_title);
        forgetpw_title.settingName(getResources().getString(R.string.forgetpw_submit), Color.BLACK);
        forgetpw_title.transparentbg();
        forgetpw_title.showBack(this);
    }

    protected void onStop() {
        super.onStop();
        if (timerThread != null)
            timerThread.isStop = true;
        if (sendData != null) {
            sendData.stop(Urls.Findpassword);
        }
        if (getSMS != null) {
            getSMS.stop(Urls.RegisterSendSMS);
        }
    }

    public void onBack() {
        baseFinish();
    }

    private void initNetworkConnection() {
        sendData = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("mobile", forgetpw_moblie.getText().toString());
                params.put("pwd", forgetpw_newpw.getText().toString());
                params.put("client", Tools.getDeviceType());
                params.put("vcode", forgetpw_captcha.getText().toString());
                params.put("token", Tools.getToken());
                return params;
            }
        };
        sendData.setIsShowDialog(true);
        getSMS = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("mobile", forgetpw_moblie.getText().toString());
                params.put("ident", "0");
                params.put("token", Tools.getToken());
                return params;
            }
        };
        getSMS.setIsShowDialog(true);
    }

    private EditText forgetpw_moblie, forgetpw_newpw, forgetpw_captcha;
    private static TextView forgetpw_getcaptcha;
    private View forgetpw_submit;
    private NetworkConnection sendData, getSMS;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword);
        initTitle();
        initNetworkConnection();
        forgetpw_moblie = (EditText) findViewById(R.id.forgetpw_moblie);
        forgetpw_newpw = (EditText) findViewById(R.id.forgetpw_newpw);
        forgetpw_captcha = (EditText) findViewById(R.id.forgetpw_captcha);
        forgetpw_getcaptcha = (TextView) findViewById(R.id.forgetpw_getcaptcha);
        forgetpw_submit = findViewById(R.id.forgetpw_submit);
        findViewById(R.id.forgetpw_submit).setOnClickListener(this);
        forgetpw_getcaptcha.setOnClickListener(this);
        forgetpw_submit.setOnClickListener(this);
    }

    private void sendData() {
        sendData.sendPostRequest(Urls.Findpassword, new Response.Listener<String>() {
            public void onResponse(String s) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        Tools.showToast(ForgetPasswordActivity.this, getResources().getString(R.string.forgetpw_toast));
                        baseFinish();
                    } else {
                        Tools.showToast(ForgetPasswordActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(ForgetPasswordActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(ForgetPasswordActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    private void sendSMS() {
        getSMS.sendPostRequest(Urls.RegisterSendSMS, new Response.Listener<String>() {
            public void onResponse(String s) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        if (timerThread == null || !timerThread.isAlive()) {
                            timerThread = new TimerThread();
                            timerThread.start();
                        }
                    } else {
                        Tools.showToast(ForgetPasswordActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(ForgetPasswordActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(ForgetPasswordActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    private MyHandler handler = new MyHandler(this);

    static class MyHandler extends Handler {
        Context context;

        MyHandler(Context context) {
            this.context = context;
        }

        public void handleMessage(Message msg) {
            Tools.d(maxTime + "");
            switch (msg.what) {
                case 0: { //倒计时
                    if (forgetpw_getcaptcha != null)
                        forgetpw_getcaptcha.setText(maxTime + "");
                }
                break;
                case 1: {//倒计时结束
                    if (forgetpw_getcaptcha != null)
                        forgetpw_getcaptcha.setText(context.getResources().getString(R.string.register_getcaptcha));
                }
                break;
            }
        }
    }

    private static int maxTime;

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forgetpw_getcaptcha: {
                if (!Tools.isMobile(forgetpw_moblie.getText().toString())) {
                    Tools.showToast(this, getResources().getString(R.string.register_check_mobile));
                } else {
                    sendSMS();
                }
            }
            break;
            case R.id.forgetpw_submit: {
                if (checkMoblie()) {
                    sendData();
                }
            }
            break;
        }
    }

    /**
     * 提交前验证
     *
     * @return
     */
    private boolean checkMoblie() {
        String str = forgetpw_moblie.getText().toString();
        if (TextUtils.isEmpty(str)) {
            Tools.showToast(this, getResources().getString(R.string.register_check_mobile2));
            return false;
        }
        if (!Tools.isMobile(str)) {
            Tools.showToast(this, getResources().getString(R.string.register_check_mobile));
            return false;
        }
        if (TextUtils.isEmpty(forgetpw_captcha.getText())) {
            Tools.showToast(this, getResources().getString(R.string.register_check_captcha));
            return false;
        }
        str = forgetpw_newpw.getText().toString();
        if (str == null || str.equals("") || str.length() < 6) {
            Tools.showToast(this, getResources().getString(R.string.register_check_pw));
            return false;
        }
        return true;
    }

    private TimerThread timerThread;

    class TimerThread extends Thread {
        boolean isStop = false;

        public void run() {
            if (maxTime != AppInfo.CAPTCHATIME)
                maxTime = AppInfo.CAPTCHATIME;
            while (maxTime != 0 && !isStop) {
                if (handler != null)
                    handler.sendEmptyMessage(0);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                maxTime--;
            }
            if (handler != null)
                handler.sendEmptyMessage(1);
        }
    }
}
