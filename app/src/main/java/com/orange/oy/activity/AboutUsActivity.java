package com.orange.oy.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.allinterface.OnCheckVersionResult;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.network.CheckVersion;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AboutUsActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {
    private AppTitle aboutus_title;

    private void initTitle() {
        aboutus_title = (AppTitle) findViewById(R.id.aboutus_title);
        aboutus_title.settingName("应用版本");
        aboutus_title.showBack(this);
    }

    protected void onStop() {
        super.onStop();
        if (checkVersion != null) {
            checkVersion.stop(Urls.Version);
        }
        ConfirmDialog.dissmisDialog();
    }

    private void initNetworkConnection() {
        checkVersion = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                return params;
            }
        };
    }

    private NetworkConnection checkVersion;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        initNetworkConnection();
        initTitle();
        findViewById(R.id.aboutus_item1).setOnClickListener(this);
        findViewById(R.id.aboutus_item2).setOnClickListener(this);
        try {
            ((TextView) findViewById(R.id.aboutus_version)).setText(Tools.getVersionName(this));
        } catch (PackageManager.NameNotFoundException e) {
            ((TextView) findViewById(R.id.aboutus_version)).setText("");
        }
    }

    private void checkVersion() {
        checkVersion.sendPostRequest(Urls.Version, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = 0;
                    code = jsonObject.getInt("code");
                    if (code == 200) {
                        jsonObject = jsonObject.getJSONObject("datas");
                        String version_num = jsonObject.getString("version_num");
                        String version_desc = jsonObject.getString("version_desc");
                        String verison_url = jsonObject.getString("verison_url");
                        try {
                            if (version_num != null && !version_num.equals(Tools.getVersionName(AboutUsActivity.this)
                            )) {
                                ConfirmDialog.showDialog(AboutUsActivity.this, "发现新版本！", version_desc, "稍后再说",
                                        "马上更新", verison_url, true, new ConfirmDialog.OnSystemDialogClickListener() {
                                            public void leftClick(Object object) {
                                            }

                                            public void rightClick(Object object) {
                                                if (object == null || TextUtils.isEmpty(object.toString()) || !object
                                                        .toString().startsWith("http"))
                                                    return;
                                                Intent intent = new Intent();
                                                intent.setAction("android.intent.action.VIEW");
                                                intent.setData(Uri.parse(object.toString()));
                                                startActivity(intent);
                                            }
                                        });
                            } else {
                                Tools.showToast(AboutUsActivity.this, "您的已经是最新版本啦~");
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            Tools.showToast(AboutUsActivity.this, "检查失败");
                        }
                    } else {
                        Tools.showToast(AboutUsActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(AboutUsActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(AboutUsActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    public void onBack() {
        baseFinish();
    }

    private OnCheckVersionResult onCheckVersionResult = new OnCheckVersionResult() {
        public void checkversion(String versionName) {
            if (versionName != null) {
                ConfirmDialog.showDialogForHint(AboutUsActivity.this, "发现新版！正在更新...");
            } else {
                ConfirmDialog.showDialogForHint(AboutUsActivity.this, "未发现新版");
            }
        }
    };

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.aboutus_item1: {
                Tools.showToast(AboutUsActivity.this, "正在检查更新...");
                int need_update = AppInfo.getNeedUpdata(this);
                if (need_update == 1) {
                    CheckVersion.check(getBaseContext(), onCheckVersionResult);//检查更新
                }
            }
            break;
            case R.id.aboutus_item2: {
                Intent intent = new Intent(this, BrowserActivity.class);
                intent.putExtra("flag", BrowserActivity.flag_about);
                startActivity(intent);
            }
            break;
        }
    }
}
