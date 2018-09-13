package com.orange.oy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import java.util.HashMap;
import java.util.Map;

//上传网络设置页
public class NetworkSettingActivity extends BaseActivity implements View.OnClickListener {
    private ImageView netsetting_1, netsetting_2, netsetting_3;
    private NetworkConnection Dataconnection;
    private int flag = 0;

    private void initNetworkConnection() {
        Dataconnection = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> map = new HashMap<>();
                map.put("usermobile", AppInfo.getName(NetworkSettingActivity.this));
                map.put("flag", flag + "");
                return map;
            }
        };
    }

    private boolean isHavNetwork() {
        String network = Tools.GetNetworkType(this);//网络状态
        Tools.d("network--" + network);
        switch (flag) {
            case AppInfo.netSetting_1: {
                return !TextUtils.isEmpty(network);
            }
            case AppInfo.netSetting_2: {
                return "WIFI".equals(network);
            }
            case AppInfo.netSetting_3: {
                return !TextUtils.isEmpty(network) && network.endsWith("G");
            }
        }
        return false;
    }

    private UpdataDBHelper updataDBHelper;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_networksetting);
        updataDBHelper = new UpdataDBHelper(this);
        initNetworkConnection();
        AppTitle appTitle = (AppTitle) findViewById(R.id.netsetting_title);
        appTitle.showBack(new AppTitle.OnBackClickForAppTitle() {
            public void onBack() {
                baseFinish();
            }
        });
        appTitle.settingName("设置上传网络环境");
        appTitle.settingExit("完成", new AppTitle.OnExitClickForAppTitle() {
            public void onExit() {
                if (flag != 0) {
                    Tools.d("network change flag---" + flag);
                    AppInfo.setOpen4GUpdata(NetworkSettingActivity.this, flag);
                    if (isHavNetwork()) {
                        if (updataDBHelper.isHave()) {
                            Intent service = new Intent("com.orange.oy.UpdataNewService");
                            service.setPackage("com.orange.oy");
                            startService(service);
                        }
                    }
                    sendData();
                }
                baseFinish();
            }
        });
        netsetting_1 = (ImageView) findViewById(R.id.netsetting_1);
        netsetting_2 = (ImageView) findViewById(R.id.netsetting_2);
        netsetting_3 = (ImageView) findViewById(R.id.netsetting_3);
        switch (AppInfo.getOpen4GUpdata(this)) {
            case AppInfo.netSetting_1: {
                flag = 1;
                netsetting_1.setVisibility(View.VISIBLE);
                netsetting_2.setVisibility(View.INVISIBLE);
                netsetting_3.setVisibility(View.INVISIBLE);
            }
            break;
            case AppInfo.netSetting_2: {
                flag = 2;
                netsetting_1.setVisibility(View.INVISIBLE);
                netsetting_2.setVisibility(View.VISIBLE);
                netsetting_3.setVisibility(View.INVISIBLE);
            }
            break;
            case AppInfo.netSetting_3: {
                flag = 3;
                netsetting_1.setVisibility(View.INVISIBLE);
                netsetting_2.setVisibility(View.INVISIBLE);
                netsetting_3.setVisibility(View.VISIBLE);
            }
            break;
        }
        findViewById(R.id.netsetting_layout1).setOnClickListener(this);
        findViewById(R.id.netsetting_layout2).setOnClickListener(this);
        findViewById(R.id.netsetting_layout3).setOnClickListener(this);
    }

    private void sendData() {
        Dataconnection.sendPostRequest(Urls.Dataconnection, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(NetworkSettingActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.netsetting_layout1: {
                flag = 1;
                netsetting_1.setVisibility(View.VISIBLE);
                netsetting_2.setVisibility(View.INVISIBLE);
                netsetting_3.setVisibility(View.INVISIBLE);
            }
            break;
            case R.id.netsetting_layout2: {
                flag = 2;
                netsetting_1.setVisibility(View.INVISIBLE);
                netsetting_2.setVisibility(View.VISIBLE);
                netsetting_3.setVisibility(View.INVISIBLE);
            }
            break;
            case R.id.netsetting_layout3: {
                flag = 3;
                netsetting_1.setVisibility(View.INVISIBLE);
                netsetting_2.setVisibility(View.INVISIBLE);
                netsetting_3.setVisibility(View.VISIBLE);
            }
            break;
        }
    }
}
