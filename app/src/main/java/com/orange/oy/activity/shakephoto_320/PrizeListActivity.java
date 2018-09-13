package com.orange.oy.activity.shakephoto_320;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.adapter.mycorps_314.PrizeListAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.info.shakephoto.PrizeListInfo;
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
 * 礼品奖励列表 V3.20
 */
public class PrizeListActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, AdapterView.OnItemClickListener {

    private void initTilte() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.prizelist_title);
        appTitle.settingName("领取礼品");
        appTitle.showBack(this);
    }

    private void initNetwork() {
        giftList = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(PrizeListActivity.this));
                params.put("page", page + "");
                return params;
            }
        };
    }

    private PrizeListAdapter prizeListAdapter;
    private PullToRefreshListView prizelist_listview;
    private NetworkConnection giftList;
    private ArrayList<PrizeListInfo> list;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prize_list);
        list = new ArrayList<>();
        initTilte();
        initNetwork();
        prizelist_listview = (PullToRefreshListView) findViewById(R.id.prizelist_listview);
        prizeListAdapter = new PrizeListAdapter(this, list);
        prizelist_listview.setAdapter(prizeListAdapter);
        prizelist_listview.setOnItemClickListener(this);
        prizelist_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                getData();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        giftList.sendPostRequest(Urls.GiftList, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (page == 1) {
                            list.clear();
                        }
                        jsonObject = jsonObject.getJSONObject("data");
                        JSONArray jsonArray = jsonObject.optJSONArray("gift_list");
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.optJSONObject(i);
                                PrizeListInfo prizeListInfo = new PrizeListInfo();
                                prizeListInfo.setUser_gift_id(object.getString("user_gift_id"));
                                prizeListInfo.setProject_id(object.getString("project_id"));
                                prizeListInfo.setProject_name(object.getString("project_name"));
                                prizeListInfo.setOutlet_id(object.getString("outlet_id"));
                                prizeListInfo.setOutlet_name(object.getString("outlet_name"));
                                prizeListInfo.setGift_url(object.getString("gift_url"));
                                prizeListInfo.setGift_name(object.getString("gift_name"));
                                prizeListInfo.setGift_money(object.getString("gift_money"));
                                prizeListInfo.setMerchant(object.getString("merchant"));
                                prizeListInfo.setExpired(object.getString("expired"));
                                list.add(prizeListInfo);
                            }
                            if (prizeListAdapter != null) {
                                prizeListAdapter.notifyDataSetChanged();
                            }
                            prizelist_listview.onRefreshComplete();
                            if (jsonArray.length() < 15) {
                                prizelist_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            } else {
                                prizelist_listview.setMode(PullToRefreshBase.Mode.BOTH);
                            }
                        }
                    } else {
                        Tools.showToast(PrizeListActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(PrizeListActivity.this, getResources().getString(R.string.network_error));
                }
                prizelist_listview.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(PrizeListActivity.this, getResources().getString(R.string.network_volleyerror));
                prizelist_listview.onRefreshComplete();
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PrizeListInfo prizeListInfo = list.get(position - 1);
        if (prizeListAdapter != null) {
            if ("0".equals(prizeListInfo.getExpired())) {
                Intent intent = new Intent(this, DrawPrizeActivity.class);
                intent.putExtra("user_gift_id", prizeListInfo.getUser_gift_id());
                intent.putExtra("gift_url", prizeListInfo.getGift_url());
                intent.putExtra("gift_name", prizeListInfo.getGift_name());
                startActivity(intent);
            }
        }
    }
}
