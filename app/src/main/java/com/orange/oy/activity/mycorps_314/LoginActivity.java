package com.orange.oy.activity.mycorps_314;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.LoginCheckActivity;
import com.orange.oy.activity.MainActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.ScreenManager;
import com.orange.oy.base.Tools;
import com.orange.oy.db.AppDBHelper;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.encryption.AESException;
import com.orange.oy.encryption.EncryptTool;
import com.orange.oy.fragment.MyFragment;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

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
 * 密码登录页面 V3.14
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, AppTitle.OnBackClickForAppTitle {

    private void initNetwork() {
        connection = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {//TODO
                Map<String, String> params = new HashMap<>();
                params.put("mobile", mobile);
                params.put("pwd", pwd);
                params.put("client", Tools.getDeviceType());
                params.put("token", Tools.getToken());
                params.put("name", getResources().getString(R.string.app_name));
                try {
                    params.put("versionnum", Tools.getVersionName(LoginActivity.this));
                } catch (PackageManager.NameNotFoundException e) {
                    params.put("versionnum", "not found");
                }
                params.put("logintime", Tools.getTimeByPattern("yyyy-MM-dd HH-mm-ss"));
                params.put("comname", Tools.getDeviceType());
                params.put("phonemodle", Tools.getDeviceModel());
                params.put("sysversion", Tools.getSystemVersion() + "");
                params.put("operator", Tools.getCarrieroperator(LoginActivity.this));
                params.put("resolution", Tools.getScreeInfoWidth(LoginActivity.this) + "*" + Tools
                        .getScreeInfoHeight(LoginActivity.this));
                params.put("mac", Tools.getLocalMacAddress(LoginActivity.this));
                params.put("imei", Tools.getDeviceId(LoginActivity.this));
                params.put("aeskey", aeskey);
                return params;
            }
        };
        connection.setIsShowDialog(true);
    }

    private TextView login;
    private EditText login_name, login_password;
    private NetworkConnection connection;
    private AppDBHelper appDBHelper;
    private int flag = 0;
    private String aeskey;
    private EncryptTool encryptTool;
    String mobile, pwd;//加密过后的账号密码

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initTitle();
        encryptTool = new EncryptTool(this);
        Intent data = getIntent();
        if (data != null) {
            flag = data.getIntExtra("flag", 0);
        }
        appDBHelper = new AppDBHelper(this);
        login = (TextView) findViewById(R.id.login_button);
        login_name = (EditText) findViewById(R.id.login_name);
        login_password = (EditText) findViewById(R.id.login_password);
        login.setOnClickListener(this);
        login_name.setText(AppInfo.getName(this));
        initNetwork();
        findViewById(R.id.login_forgetpw_ico).setOnClickListener(this);
        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.login_rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
    }

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.login_title);
        appTitle.settingName("密码登录");
        appTitle.showBack(this);
    }

    private Animation operatingAnim;

    protected void onStop() {
        super.onStop();
        if (connection != null) {
            connection.stop(Urls.Login);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button: {
                if (TextUtils.isEmpty(login_name.getText())) {
                    Tools.showToast(this, "请输入账号");
                    return;
                }
                if (TextUtils.isEmpty(login_password.getText())) {
                    Tools.showToast(this, "请输入密码");
                    return;
                }
                login();
            }
            break;
            case R.id.login_forgetpw_ico: {//忘记密码
                startActivity(new Intent(this, ForgetPasswordActivity.class));
            }
            break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RegisterActivity.resultCode && data != null) {
            try {
                CustomProgressDialog.showProgressDialog(LoginActivity.this, "正在登录...");
                login_name.setText(data.getStringExtra("name"));
                login_password.setText(data.getStringExtra("password"));
                login();
            } catch (Exception e) {
            }
        } else if (resultCode == AppInfo.LoginCheckResultCode && data != null) {
            try {
                CustomProgressDialog.showProgressDialog(this, "正在登录...");
                loginSuccess(new JSONObject(data.getStringExtra("datas")));
            } catch (JSONException e) {
                Tools.showToast(LoginActivity.this, getResources().getString(R.string.network_error));
            }
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
        bindaccount = datas.getString("bindaccount");
        bindidcard = datas.getString("bindidcard");
        AppInfo.isbindaccount(this, bindaccount);
        AppInfo.isbindidcard(this, bindidcard);
        AppInfo.setName(LoginActivity.this, mobile);
        AppInfo.setKey(LoginActivity.this, key);
        if (JPushInterface.isPushStopped(this)) {//开启极光推送服务
            JPushInterface.resumePush(this);
            Tools.d("resumePush");
        }
        JPushInterface.setAlias(LoginActivity.this, key, mAliasCallback);
        MyFragment.isRefresh = true;
    }

    private String bindaccount, bindidcard;

    private void login() {
        try {
            mobile = encryptTool.AESencode(login_name.getText().toString());
            pwd = encryptTool.AESencode(login_password.getText().toString());
            aeskey = encryptTool.getAeskey();
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
        } catch (AESException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        connection.sendPostRequest(Urls.Login, new Response.Listener<String>() {
            public void onResponse(String response) {
                Tools.d(response);
                try {
                    JSONObject job = new JSONObject(response);
                    int code = job.getInt("code");
                    int need_update = job.getInt("need_update");
                    AppInfo.setIsNeedUpdata(LoginActivity.this, need_update);
                    if (code == 200) {
                        JSONObject datas = job.getJSONObject("datas");
                        ScreenManager.getScreenManager().finishActivity(IdentifycodeLoginActivity.class);
                        loginSuccess(datas);
                    } else if (code == 2) {
                        Intent intent = new Intent(LoginActivity.this, LoginCheckActivity.class);
                        intent.putExtra("mobile", login_name.getText().toString());
                        startActivityForResult(intent, 20);
                        CustomProgressDialog.Dissmiss();
                    } else {
                        Tools.showToast(LoginActivity.this, job.getString("msg"));
                        CustomProgressDialog.Dissmiss();
                    }
                } catch (Exception e) {
                    CustomProgressDialog.Dissmiss();
                    Tools.showToast(LoginActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(LoginActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, getResources().getString(R.string.login_dialog_message));
    }

    /**
     * 生成极光alias
     */
    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            ScreenManager.getScreenManager().finishActivity(IdentifycodeLoginActivity.class);
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Tools.d(logs);
                    CustomProgressDialog.Dissmiss();
                    if (flag == 1) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                    setResult(RESULT_OK);
                    baseFinish();
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Tools.d(logs);
                    CustomProgressDialog.Dissmiss();
                    if (flag == 1) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                    setResult(RESULT_OK);
                    baseFinish();
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
                    Tools.d(logs);
                    CustomProgressDialog.Dissmiss();
                    if (flag == 1) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                    setResult(RESULT_OK);
                    baseFinish();
            }
        }
    };

    @Override
    public void onBack() {
        baseFinish();
    }
}
