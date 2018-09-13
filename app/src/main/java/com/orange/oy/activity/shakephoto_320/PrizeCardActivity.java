package com.orange.oy.activity.shakephoto_320;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.adapter.mycorps_314.PrizeCardAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.info.shakephoto.PrizeCardInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 礼品卡券 V3.20
 */
public class PrizeCardActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.prizecard_title);
        appTitle.settingName("券包");
        appTitle.showBack(this);
    }

    private void initNetwork() {
        myGift = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(PrizeCardActivity.this));
                return params;
            }
        };
    }

    private PullToRefreshListView prizecard_listview;
    private ArrayList<PrizeCardInfo> list;
    private PrizeCardAdapter prizeCardAdapter;
    private NetworkConnection myGift;
    private TextView prizecard_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prize_card);
        list = new ArrayList<>();
        initTitle();
        initNetwork();
        prizecard_listview = (PullToRefreshListView) findViewById(R.id.prizecard_listview);
        prizeCardAdapter = new PrizeCardAdapter(this, list);
        prizecard_listview.setAdapter(prizeCardAdapter);
        prizecard_num = (TextView) findViewById(R.id.prizecard_num);
        prizecard_num.setOnClickListener(PrizeCardActivity.this);
        getData();
    }

    private void getData() {
        myGift.sendPostRequest(Urls.MyGift, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (!list.isEmpty()) {
                            list.clear();
                        }
                        jsonObject = jsonObject.getJSONObject("data");
                        String gift_num = jsonObject.getString("gift_num");
                        prizecard_num.setText("共" + gift_num + "张");
                        JSONArray jsonArray = jsonObject.optJSONArray("gift_list");
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                PrizeCardInfo prizeCardInfo = new PrizeCardInfo();
                                prizeCardInfo.setGift_user_id(object.getString("gift_user_id"));
                                prizeCardInfo.setGift_money(object.getString("gift_money"));
                                prizeCardInfo.setGift_name(object.getString("gift_name"));
                                prizeCardInfo.setImg_url(object.getString("img_url"));
                                prizeCardInfo.setMerchant(object.getString("merchant"));
                                list.add(prizeCardInfo);
                            }
                            if (prizeCardAdapter != null) {
                                prizeCardAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Tools.showToast(PrizeCardActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(PrizeCardActivity.this, getResources().getString(R.string.network_error));
                }
                prizecard_listview.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(PrizeCardActivity.this, getResources().getString(R.string.network_volleyerror));
                prizecard_listview.onRefreshComplete();
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, MyGiftActivity.class);
        startActivity(intent);
    }
}
