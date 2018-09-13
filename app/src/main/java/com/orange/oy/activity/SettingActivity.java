package com.orange.oy.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.guide.TaskGuideActivity;
import com.orange.oy.activity.mycorps_314.IdentifycodeLoginActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.OfflineDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.fragment.MyFragment;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.FileCache;
import com.orange.oy.view.AppTitle;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import cn.jpush.android.api.JPushInterface;

/**
 * 设置页面
 */
public class SettingActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {
    public void onBack() {
        baseFinish();
    }

    private ImageView my_switch;
    private View setting_network_switch;
    private TextView my_cachesize_text;
    private NetworkConnection Addstatistout;
    private String mobile;

    private void initNetworkConnection() {
        Addstatistout = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_mobile", mobile);
                params.put("token", Tools.getToken());
                params.put("name", getResources().getString(R.string.app_name));
                try {
                    params.put("versionnum", Tools.getVersionName(SettingActivity.this));
                } catch (PackageManager.NameNotFoundException e) {
                    params.put("versionnum", "not found");
                }
                params.put("comname", Tools.getDeviceType());
                params.put("phonemodle", Tools.getDeviceModel());
                params.put("sysversion", Tools.getSystemVersion() + "");
                params.put("operator", Tools.getCarrieroperator(SettingActivity.this));
                params.put("resolution", Tools.getScreeInfoWidth(SettingActivity.this) + "*" + Tools.getScreeInfoHeight
                        (SettingActivity.this));
                params.put("outtime", Tools.getTimeByPattern("yyyy-MM-dd HH:mm:ss"));
                params.put("mac", Tools.getLocalMacAddress(SettingActivity.this));
                params.put("imei", Tools.getDeviceId(SettingActivity.this));
                return params;
            }
        };
    }

    private View setting_loginout;
    private TextView textView;
    private boolean isagent;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initNetworkConnection();
        AppTitle feedback_title = (AppTitle) findViewById(R.id.setting_title);
        feedback_title.settingName("设置");
        feedback_title.showBack(this);
        isagent = getIntent().getBooleanExtra("isagent", false);
        if (isagent) {
            findViewById(R.id.setting_proxyapply_text).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.setting_proxyapply_text)).setText("已通过申通");
        } else {
            findViewById(R.id.setting_proxyapply_text).setVisibility(View.GONE);
        }
        my_switch = (ImageView) findViewById(R.id.setting_switch);
        setting_network_switch = findViewById(R.id.setting_network_switch);
        setting_loginout = findViewById(R.id.setting_loginout);
        if (TextUtils.isEmpty(AppInfo.getKey(SettingActivity.this))) {
            setting_loginout.setVisibility(View.GONE);
        } else {
            setting_loginout.setVisibility(View.VISIBLE);
        }
        my_cachesize_text = (TextView) findViewById(R.id.setting_cachesize_text);
        double size = new BigDecimal(AppInfo.getCachesize(this) / 1024d / 1024d).
                setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        my_cachesize_text.setText(size + "M");
        if (AppInfo.isOpenLocation(this)) {
            my_switch.setImageResource(R.mipmap.switch_open);
        } else {
            my_switch.setImageResource(R.mipmap.switch_close);
        }
        textView = (TextView) findViewById(R.id.setting_network_text);
        my_switch.setOnClickListener(this);
        setting_network_switch.setOnClickListener(this);
        setting_loginout.setOnClickListener(this);
        findViewById(R.id.setting_offline).setOnClickListener(this);
        findViewById(R.id.setting_aboutus).setOnClickListener(this);
        findViewById(R.id.setting_appversion).setOnClickListener(this);
        findViewById(R.id.setting_cachesize).setOnClickListener(this);
        findViewById(R.id.setting_traffic).setOnClickListener(this);
        findViewById(R.id.setting_proxyapply).setOnClickListener(this);
        findViewById(R.id.setting_myteam).setOnClickListener(this);
        findViewById(R.id.setting_guide).setOnClickListener(this);
    }

    protected void onResume() {
        super.onResume();
        switch (AppInfo.getOpen4GUpdata(this)) {
            case AppInfo.netSetting_1: {
                textView.setText("wifi、4G、3G、2G均可上传");
            }
            break;
            case AppInfo.netSetting_2: {
                textView.setText("仅wifi时上传");
            }
            break;
            case AppInfo.netSetting_3: {
                textView.setText("仅4G、3G、2G时上传");
            }
            break;
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_switch: {
                boolean isOpenLocation = !AppInfo.isOpenLocation(this);
                AppInfo.setOpenLocation(this, isOpenLocation);
                if (isOpenLocation) {
                    my_switch.setImageResource(R.mipmap.switch_open);
                } else {
                    my_switch.setImageResource(R.mipmap.switch_close);
                }
                Intent intent = new Intent();
                intent.putExtra("isOpenLocation", isOpenLocation);
                setResult(AppInfo.SettingLocationResultCode, intent);
            }
            break;
            case R.id.setting_network_switch: {
//                boolean isOpen4GUpdata = !AppInfo.isOpen4GUpdata(this);
//                AppInfo.setOpen4GUpdata(this, isOpen4GUpdata);
//                if (isOpen4GUpdata) {
//                    setting_network_switch.setImageResource(R.mipmap.switch_on);
//                } else {
//                    setting_network_switch.setImageResource(R.mipmap.switch_off);
//                }
                Intent intent = new Intent(this, NetworkSettingActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.setting_offline: {
                Intent intent = new Intent(this, OfflineProjectActivity.class);
                intent.putExtra("city", getIntent().getStringExtra("city"));
                startActivity(intent);

            }
            break;
            case R.id.setting_aboutus: {
                Intent intent = new Intent(this, BrowserActivity.class);
                intent.putExtra("flag", BrowserActivity.flag_about);
                startActivity(intent);
            }
            break;
            case R.id.setting_appversion: {
                startActivity(new Intent(this, AboutUsActivity.class));
            }
            break;
            case R.id.setting_cachesize: {
                ConfirmDialog.showDialog(this, "确定清理吗？", true, new ConfirmDialog.OnSystemDialogClickListener() {
                    public void leftClick(Object object) {
                    }

                    public void rightClick(Object object) {
                        new ClearCache().executeOnExecutor(Executors.newCachedThreadPool());
                    }
                });
            }
            break;
            case R.id.setting_traffic: {
                startActivity(new Intent(this, TrafficSumProjectActivity.class));
            }
            break;
            case R.id.setting_loginout: {
                MyFragment.isRefresh = true;
                mobile = AppInfo.getName(this);
                Addstatistout.sendPostRequest(Urls.Addstatistout, new Response.Listener<String>() {
                    public void onResponse(String s) {
                    }
                }, new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                });
                AppInfo.clearKey(this);
                JPushInterface.clearAllNotifications(this);
                JPushInterface.setAlias(this, "", null);
                JPushInterface.stopPush(this);
                upLoginico();
                Intent intent = new Intent("com.orange.oy.VRService");
                intent.setPackage("com.orange.oy");
                stopService(intent);
                baseFinish();
            }
            break;
            case R.id.setting_proxyapply: {
                if (!isagent) {
                    if (TextUtils.isEmpty(AppInfo.getKey(SettingActivity.this))) {
                        ConfirmDialog.showDialog(SettingActivity.this, null, 2,
                                getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                    @Override
                                    public void leftClick(Object object) {
                                    }

                                    @Override
                                    public void rightClick(Object object) {
                                        Intent intent = new Intent(SettingActivity.this, IdentifycodeLoginActivity.class);
                                        startActivity(intent);
                                    }
                                });
                        return;
                    }
                    startActivity(new Intent(SettingActivity.this, ProxyapplyActivity.class));
                }
            }
            break;
            case R.id.setting_myteam: {
                if (TextUtils.isEmpty(AppInfo.getKey(SettingActivity.this))) {
                    ConfirmDialog.showDialog(SettingActivity.this, null, 2,
                            getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                @Override
                                public void leftClick(Object object) {
                                }

                                @Override
                                public void rightClick(Object object) {
                                    Intent intent = new Intent(SettingActivity.this, IdentifycodeLoginActivity.class);
                                    intent.putExtra("nologin", "1");
                                    startActivity(intent);
                                }
                            });
                    return;
                }
                startActivity(new Intent(SettingActivity.this, MyTeamActivity.class));
            }
            break;
            case R.id.setting_guide: {
                Intent intent = new Intent(SettingActivity.this, TaskGuideActivity.class);
                intent.putExtra("type", "0");//登录页面跳转为1 设置页面跳转为0
                startActivity(intent);
            }
            break;
        }
    }

    public void upLoginico() {
        findViewById(R.id.setting_offline).setOnClickListener(null);
        findViewById(R.id.setting_cachesize).setOnClickListener(null);
        findViewById(R.id.setting_traffic).setOnClickListener(null);
        setting_loginout.setVisibility(View.GONE);
    }

    private OfflineDBHelper offlineDBHelper;

    class ClearCache extends AsyncTask {
        protected void onPreExecute() {
            if (offlineDBHelper == null) {
                offlineDBHelper = new OfflineDBHelper(SettingActivity.this);
            }
            CustomProgressDialog.showProgressDialog(SettingActivity.this, "清理缓存");
        }


        protected Object doInBackground(Object[] params) {
            offlineDBHelper.clearCache();
            recurDelete(new File(FileCache.getCacheDir(SettingActivity.this).getPath() + "/download"));
            AppInfo.clearCachesize(SettingActivity.this);
            return null;
        }

        public void recurDelete(File f) {
            if (f == null || !f.exists()) {
                return;
            }
            for (File fi : f.listFiles()) {
                if (fi.isDirectory()) {
                    recurDelete(fi);
                } else {
                    fi.delete();
                }
            }
        }

        protected void onPostExecute(Object o) {
            my_cachesize_text.setText("(0)");
            CustomProgressDialog.Dissmiss();
        }
    }
}
