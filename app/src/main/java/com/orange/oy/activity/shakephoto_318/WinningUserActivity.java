package com.orange.oy.activity.shakephoto_318;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 获奖用户信息 V3.18
 */
public class WinningUserActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.winninguser_title);
        appTitle.settingName("获奖用户");
        appTitle.showBack(this);
    }

    private void initNetwork() {
        prizeUserInfo = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(WinningUserActivity.this));
                params.put("token", Tools.getToken());
                params.put("ai_id", ai_id);
                params.put("pur_id", pur_id);
                return params;
            }
        };
    }

    private NetworkConnection prizeUserInfo;
    private String pur_id, ai_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winning_user);
        Intent data = getIntent();
        pur_id = data.getStringExtra("pur_id");
        ai_id = data.getStringExtra("ai_id");
        initTitle();
        initNetwork();
        getData();
    }

    private void getData() {
        prizeUserInfo.sendPostRequest(Urls.PrizeUserInfo, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        jsonObject = jsonObject.optJSONObject("data");
                        if (jsonObject != null) {
                            ((TextView) findViewById(R.id.winninguser_theme)).setText("昵称：" + jsonObject.getString("user_name"));
                            ((TextView) findViewById(R.id.winninguser_num)).setText("票数：" + jsonObject.getString("votes"));
                            String prize = jsonObject.getString("prize_type");
                            if ("1".equals(prize)) {
                                prize = "一等奖";
                            } else if ("2".equals(prize)) {
                                prize = "二等奖";
                            } else if ("3".equals(prize)) {
                                prize = "三等奖";
                            }
                            ((TextView) findViewById(R.id.winninguser_spize)).setText(prize);
                            ImageView winninguser_img = (ImageView) findViewById(R.id.winninguser_img);
                            ImageLoader imageLoader = new ImageLoader(WinningUserActivity.this);
                            imageLoader.DisplayImage(jsonObject.getString("prize_image_url"), winninguser_img);
                            ((TextView) findViewById(R.id.winninguser_desc)).setText(jsonObject.getString("prize_name"));
                            ((TextView) findViewById(R.id.winninguser_name)).setText(jsonObject.getString("consignee_name"));
                            ((TextView) findViewById(R.id.winninguser_phone)).setText(jsonObject.getString("consignee_phone"));
                            ((TextView) findViewById(R.id.winninguser_address)).setText(jsonObject.getString("consignee_address"));
                        }

                    } else {
                        Tools.showToast(WinningUserActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(WinningUserActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(WinningUserActivity.this, getResources().getString(R.string.network_batch_error));
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }
}
