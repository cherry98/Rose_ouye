package com.orange.oy.activity.mycorps_315;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.BrowserActivity;
import com.orange.oy.activity.alipay.OuMiDetailActivity;
import com.orange.oy.activity.alipay.OuMiExchangeActivity;
import com.orange.oy.activity.alipay.OuMiExchangeDesActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.orange.oy.base.AppInfo.getName;

/**
 * 我的偶米
 * /*  1、可兑换偶米数：显示用户全部可兑换的偶米，点击跳转至“兑换偶米
 * 2、兑换偶米明细：显示用户全部已兑换的偶米总数，点击跳转至“兑换偶米明细”页面；
 * 3、偶米获得明细：显示用户全部得到的偶米总数，点击跳转至“偶米获得明细”页面；
 */

public class MyoumiActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle,
        View.OnClickListener {

    private TextView oumi_money, oumi_totalmoney, oumi_num;


    private void initTitle() {
        AppTitle titleview = (AppTitle) findViewById(R.id.titleview);
        titleview.showBack(this);
        titleview.settingName("我的偶米");
//        titleview.settingExit("规则", new AppTitle.OnExitClickForAppTitle() {
//            @Override
//            public void onExit() {
//                Intent intent = new Intent(MyoumiActivity.this, BrowserActivity.class);
//                intent.putExtra("title", "规则");
//                intent.putExtra("content", urls);
//                intent.putExtra("flag", "9");
//                startActivity(intent);
//            }
//        });
    }

    private NetworkConnection MYOM;

    public void onBack() {
        baseFinish();
    }

    protected void onStop() {
        super.onStop();
        if (MYOM != null) {
            MYOM.stop(Urls.MYOM);
        }
    }

    private void initNetworkConnection() {

        MYOM = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(MyoumiActivity.this));
                return params;
            }
        };

    }

    String urls;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oumi);
        urls = Urls.API + "omRule?token=" + Tools.getToken() + "&usermobile=" + AppInfo.getName(MyoumiActivity.this);
        initTitle();
        initNetworkConnection();
        ScrollView myaccount_scrollview = (ScrollView) findViewById(R.id.myaccount_scrollview);
        myaccount_scrollview.smoothScrollTo(0, 20);
        oumi_money = (TextView) findViewById(R.id.oumi_money);
        oumi_totalmoney = (TextView) findViewById(R.id.oumi_totalmoney);
        oumi_num = (TextView) findViewById(R.id.oumi_num);

        findViewById(R.id.myaccount_button).setOnClickListener(this);
        findViewById(R.id.oumi_duihuan).setOnClickListener(this);
        findViewById(R.id.oumi_huode).setOnClickListener(this);
        findViewById(R.id.lin_oumiNum).setOnClickListener(this);

    }

    protected void onResume() {
        getData();
        super.onResume();
    }

    private String convertible_num, total_exchange_num, total_num;

    private void getData() {
        MYOM.sendPostRequest(Urls.MYOM, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {


                        if (!jsonObject.isNull("data")) {
                            JSONObject object = jsonObject.getJSONObject("data");

                            convertible_num = object.optString("convertible_num"); //可兑换偶米数
                            total_exchange_num = object.optString("total_exchange_num");// 已兑换的偶米数
                            total_num = object.optString("total_num"); //偶米总数

                            oumi_money.setText(String.format(getResources().getString(R.string.account_money), convertible_num));
                            oumi_totalmoney.setText(String.format(getResources().getString(R.string.account_money),
                                    total_exchange_num));
                            oumi_num.setText(String.format(getResources().getString(R.string.account_money),
                                    total_num));
                       /* if (convertible_num != null) {
                            if (TextUtils.isEmpty(convertible_num)) {
                                convertible_num = "-";
                            } else {
                                double d = Tools.StringToDouble(convertible_num);
                                if (d - (int) d > 0) {
                                    convertible_num = String.valueOf(d);
                                } else {
                                    convertible_num = String.valueOf((int) d);
                                }
                            }
                           *//* oumi_money.setText(String.format(getResources().getString(R.string.account_money),
                                    "¥" + withdrawalmoney));*//*
                            oumi_money.setText(convertible_num);
                        } else {
                            oumi_money.setText("-");
                        }*/
                        }
                    } else {
                        Tools.showToast(MyoumiActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(MyoumiActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(MyoumiActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.myaccount_button: {   //兑换：跳转至兑换偶米页面；
                startActivity(new Intent(this, OuMiExchangeActivity.class));
            }
            break;

            case R.id.oumi_huode: {// “偶米获得明细”页面；

                startActivity(new Intent(this, OuMiDetailActivity.class));
            }
            break;
            case R.id.oumi_duihuan: {//兑换偶米明细”页面；
                startActivity(new Intent(this, OuMiExchangeDesActivity.class));
            }
            break;
            case R.id.lin_oumiNum: {
                startActivity(new Intent(this, OuMiExchangeActivity.class));
            }
            break;
        }
    }
}
