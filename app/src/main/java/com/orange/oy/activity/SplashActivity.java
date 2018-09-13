package com.orange.oy.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.mobstat.StatService;
import com.baidu.soleagencysdk.api.CheckCompletion;
import com.baidu.soleagencysdk.api.SoleAgencySDK;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * app启动页面
 */
public class SplashActivity extends BaseActivity implements CheckCompletion {

    private String h5projectid, h5usermobile;
    private NetworkConnection advertisement;

    protected void onStop() {
        super.onStop();
        if (advertisement != null) {
            advertisement.stop(Urls.Advertisement);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        SoleAgencySDK.startToCheckShouzhu(this, this);
        initData();
    }

    private void skip() {
        advertisement.sendPostRequest(Urls.Advertisement, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String photo_url = jsonObject.getString("photo_url");
                    String link_url = jsonObject.getString("link_url");
                    int need_update = jsonObject.getInt("need_update");//是否需要更新
                    AppInfo.setIsNeedUpdata(SplashActivity.this, need_update);
                    int time = jsonObject.optInt("time");
                    if (photo_url == null) {//
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    } else {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        intent.putExtra("photo_url", photo_url);
                        intent.putExtra("link_url", link_url);
                        intent.putExtra("time", time);
                        startActivity(intent);
                        baseFinish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
//                Tools.showToast(SplashActivity.this, getResources().getString(R.string.network_volleyerror));
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                baseFinish();
            }
        }, 2000);
    }

    private void init() {
        advertisement = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(SplashActivity.this));
                return params;
            }
        };
    }

    public void checkDidComplete() {
        initData();
    }

    private void initData() {
//        BDAutoUpdateSDK.silenceUpdateAction(this, false);
        Uri uri = getIntent().getData();//打开h5界拿到的参数
        if (uri != null) {
            h5projectid = uri.getQueryParameter("h5projectid");
            h5usermobile = uri.getQueryParameter("h5usermobile");
        }
        AppInfo.setH5Data(this, h5projectid, h5usermobile);
        init();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                skip();
            }
        }, 2000);
        StatService.start(this);
    }

    public void doStartToCheckShouzhu(View view) {
        SoleAgencySDK.startToCheckShouzhu(this, this);
    }

    protected void onDestroy() {
        super.onDestroy();
        SoleAgencySDK.onDestryActivity(this);
    }
}
