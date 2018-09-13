package com.orange.oy.activity.mycorps_314;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.MainActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.encryption.AESException;
import com.orange.oy.encryption.EncryptTool;
import com.orange.oy.fragment.MyFragment;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * 偶业登录界面 V3.14
 */

public class IdentifycodeLoginActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {
    private AppTitle identifycode_title;

    private void initTitle() {
        identifycode_title = (AppTitle) findViewById(R.id.identifycode_title);
        identifycode_title.settingName("偶业");
        identifycode_title.showBack(this);
    }

    private NetworkConnection sendsms, identifyLogin, WechatLogin;

    private EditText identifycode_phone, identifycode_code;
    private TextView identifycode_obtain;
    private EncryptTool encryptTool;
    private String aeskey;
    private UMShareAPI mShareAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identifycode_login);
        encryptTool = new EncryptTool(this);
        UMShareConfig config = new UMShareConfig();
        config.isNeedAuthOnGetUserInfo(true);
        mShareAPI = UMShareAPI.get(this);
        mShareAPI.setShareConfig(config);
        initTitle();
        initNetwork();
        identifycode_code = (EditText) findViewById(R.id.identifycode_code);
        identifycode_phone = (EditText) findViewById(R.id.identifycode_phone1);
        identifycode_obtain = (TextView) findViewById(R.id.identifycode_obtain);
        identifycode_obtain.setOnClickListener(this);
        findViewById(R.id.identifycode_next).setOnClickListener(this);
        findViewById(R.id.identifycode_login).setOnClickListener(this);
        findViewById(R.id.identifycode_nocode).setOnClickListener(this);
        findViewById(R.id.login_wx).setOnClickListener(this);
    }

    private void WXlogin() {
        mShareAPI.getPlatformInfo(this, SHARE_MEDIA.WEIXIN, authListener);
    }

    private UMAuthListener authListener = new UMAuthListener() {
        /**
         * @desc 授权开始的回调
         * @param platform 平台名称
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {
        }

        /**
         * @desc 授权成功的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param data 用户资料返回
         */
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            Tools.d("成功了");
            String openid = data.get("openid");
            if (openid == null) {
                Tools.showToast(IdentifycodeLoginActivity.this, "微信登录失败，请使用账号登录");
            } else {
                Tools.d("openid:" + openid);
                wechat_account = openid;
                checkWX();
            }
        }

        /**
         * @desc 授权失败的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Tools.d("失败：" + t.getMessage());
        }

        /**
         * @desc 授权取消的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         */
        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Tools.d("取消了");
        }
    };

//    private class getOpenid extends AsyncTask {
//        private String code;
//        private final String url1 = "https://api.weixin.qq.com/sns/oauth2/access_token";
//
//        private getOpenid(String code) {
//            this.code = code;
//        }
//
//        protected void onPreExecute() {
//            CustomProgressDialog.showProgressDialog(IdentifycodeLoginActivity.this, "");
//        }
//
//        protected void onPostExecute(Object object) {
//            CustomProgressDialog.Dissmiss();
//        }
//
//        protected Object doInBackground(Object[] params) {
//            String parameter = "appid=wx87bba3c32217c786&secret=ed7e20211af83c3f96eac780ae0ede2a&code=" + code +
//                    "&grant_type=authorization_code";
//            Object call_result = NetworkUpForHttpURL.getNetworkUpForHttpURL().sendGet(url1 + "?" + parameter);
//            if (call_result != null) {
//                if (call_result instanceof Exception) {//异常
//                    Tools.d(((Exception) call_result).getMessage());
//                } else {
//                    Tools.d(call_result + "");
//
//                }
//            }
//            return null;
//        }
//    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }

    private void initNetwork() {
        sendsms = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("mobile", identifycode_phone.getText().toString().trim());
                params.put("ident", "4");
                params.put("token", Tools.getToken());
                return params;
            }
        };
        identifyLogin = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("mobile", identifycode_phone.getText().toString().trim());
                params.put("vcode", identifycode_code.getText().toString().trim());
                params.put("client", Tools.getDeviceType());
                params.put("name", getResources().getString(R.string.app_name));
                try {
                    params.put("versionnum", Tools.getVersionName(IdentifycodeLoginActivity.this));
                } catch (PackageManager.NameNotFoundException e) {
                    params.put("versionnum", "not found");
                }
                params.put("logintime", Tools.getTimeByPattern("yyyy-MM-dd HH-mm-ss"));
                params.put("comname", Tools.getDeviceType());
                params.put("phonemodle", Tools.getDeviceModel());
                params.put("sysversion", Tools.getSystemVersion() + "");
                params.put("operator", Tools.getCarrieroperator(IdentifycodeLoginActivity.this));
                params.put("resolution", Tools.getScreeInfoWidth(IdentifycodeLoginActivity.this) + "*" + Tools
                        .getScreeInfoHeight(IdentifycodeLoginActivity.this));
                params.put("mac", Tools.getLocalMacAddress(IdentifycodeLoginActivity.this));
                params.put("imei", Tools.getDeviceId(IdentifycodeLoginActivity.this));
                params.put("aeskey", aeskey);
                if (MainActivity.getLocalMap() != null) {
                    params.put("province", MainActivity.getLocalMap().get("province"));
                    params.put("city", MainActivity.getLocalMap().get("name"));
                    params.put("address", MainActivity.getLocalMap().get("county"));
                }
                params.put("wechat_account", wechat_account);
                return params;
            }
        };
        identifyLogin.setIsShowDialog(true);
        WechatLogin = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("wechat_account", wechat_account);
                params.put("client", Tools.getDeviceType());
                return params;
            }
        };
        WechatLogin.setIsShowDialog(true);
    }

    private String wechat_account = "";

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.identifycode_login: {//密码登录
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.identifycode_nocode: {//收不到验证码
                startActivity(new Intent(this, IdentifycodeExplainActivity.class));
            }
            break;
            case R.id.identifycode_obtain: {
                if (!timer) {
                    sendsms();
                }
            }
            break;
            case R.id.identifycode_next: {
                identifyLogin();
            }
            break;
            case R.id.login_wx: {
                WXlogin();
            }
            break;
        }
    }

    private boolean isShowRegister = false;

    private void identifyLogin() {
        try {
            aeskey = encryptTool.getAeskey();
            identifyLogin.sendPostRequest(Urls.IdentifyLogin, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    Tools.d(s);
                    isShowRegister = false;
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        int code = jsonObject.getInt("code");
                        if (code == 200) {
                            int need_update = jsonObject.getInt("need_update");//是否需要更新
                            AppInfo.setIsNeedUpdata(IdentifycodeLoginActivity.this, need_update);
                            loginSuccess(jsonObject.getJSONObject("datas"));
                        } else if (code == 2) {
                            isShowRegister = true;
                            loginSuccess(jsonObject.getJSONObject("datas"));
                        } else {
                            CustomProgressDialog.Dissmiss();
                            Tools.showToast(IdentifycodeLoginActivity.this, jsonObject.getString("msg"));
                        }
                    } catch (JSONException e) {
                        CustomProgressDialog.Dissmiss();
                        Tools.showToast(IdentifycodeLoginActivity.this, getResources().getString(R.string.network_error));
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    CustomProgressDialog.Dissmiss();
                    Tools.showToast(IdentifycodeLoginActivity.this, getResources().getString(R.string.network_volleyerror));
                }
            });
        } catch (AESException | InvalidKeySpecException | InvalidKeyException |
                BadPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    private void loginSuccess(JSONObject datas) throws JSONException {
        String key = datas.getString("key");
        if (aeskey != null) {
            try {
                key = encryptTool.AESdecode(key);
            } catch (AESException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }
        String mobile = datas.getString("user_mobile");
        AppInfo.isbindaccount(this, datas.getString("bindaccount"));
        AppInfo.isbindidcard(this, datas.getString("bindidcard"));
        AppInfo.setName(IdentifycodeLoginActivity.this, mobile);
        AppInfo.setKey(IdentifycodeLoginActivity.this, key);
        if (JPushInterface.isPushStopped(this)) {//开启极光推送服务
            JPushInterface.resumePush(this);
            Tools.d("resumePush");
        }
        JPushInterface.setAlias(IdentifycodeLoginActivity.this, key, mAliasCallback);
        MyFragment.isRefresh = true;
    }

    private void sendsms() {
        sendsms.sendPostRequest(Urls.Sendsms, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        handler.sendEmptyMessage(0);
                    } else {
                        Tools.showToast(IdentifycodeLoginActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(IdentifycodeLoginActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    private void checkWX() {
        WechatLogin.sendPostRequest(Urls.WechatLogin, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        int need_update = jsonObject.getInt("need_update");//是否需要更新
                        AppInfo.setIsNeedUpdata(IdentifycodeLoginActivity.this, need_update);
                        loginSuccess(jsonObject.getJSONObject("datas"));
                    } else {
                        findViewById(R.id.login_frogetpassword_layout).setVisibility(View.INVISIBLE);
                        findViewById(R.id.login_wx_layout).setVisibility(View.INVISIBLE);
                        identifycode_title.settingName("绑定手机号");
                        CustomProgressDialog.Dissmiss();
                    }
                } catch (JSONException e) {
                    CustomProgressDialog.Dissmiss();
                    Tools.showToast(IdentifycodeLoginActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(IdentifycodeLoginActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private MyHandler handler = new MyHandler(this);
    private boolean timer = false;

    private class MyHandler extends Handler {
        Context context;

        MyHandler(Context context) {
            this.context = context;
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: { //倒计时
                    if (maxTime > 0) {
                        timer = true;
                        if (identifycode_obtain != null)
                            identifycode_obtain.setText(maxTime-- + "");
                        sendEmptyMessageDelayed(0, 1000);
                    } else {
                        timer = false;
                        maxTime = 60;
                        if (identifycode_obtain != null)
                            identifycode_obtain.setText(context.getResources().getString(R.string.register_getcaptcha));
                    }
                }
                break;
            }
        }
    }

    private int maxTime = 60;

    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(0);
        handler = null;
    }

    /**
     * 生成极光alias
     */
    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Tools.d(logs);
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Tools.d(logs);
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
                    Tools.d(logs);
            }
            CustomProgressDialog.Dissmiss();
            if (isShowRegister) {//需要注册
                Intent intent = new Intent(IdentifycodeLoginActivity.this, RegisterActivity.class);
                intent.putExtra("mobile", identifycode_phone.getText().toString().trim());
                startActivity(intent);
            } else {
                setResult(RESULT_OK);
                baseFinish();
            }
        }
    };
}
