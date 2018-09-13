package com.orange.oy.activity.alipay;

import android.os.Bundle;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.adapter.NewestWithdrawsAdapter;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.info.NewestWithdrawsInfo;
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
 * 最新提现页面
 */

public class NewestWithdrawsActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.newest_title);
        appTitle.settingName("实时红包");
        appTitle.showBack(this);
    }

    private NetworkConnection withdrawalInfo;
    private ArrayList<NewestWithdrawsInfo> list;
    private NewestWithdrawsAdapter adapter;
    private PullToRefreshListView newest_listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newest_withdraws);
        list = new ArrayList<>();
        initTitle();
        getData();
        newest_listview = (PullToRefreshListView) findViewById(R.id.newest_listview);
        adapter = new NewestWithdrawsAdapter(this, list);
        newest_listview.setAdapter(adapter);
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    public void getData() {
        withdrawalInfo = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                return params;
            }
        };
        withdrawalInfo.sendPostRequest(Urls.WithdrawalInfo, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (!list.isEmpty()) {
                        list.clear();
                    }
                    if (jsonObject.getInt("code") == 200) {
                        JSONArray jsonArray = jsonObject.getJSONArray("datas");
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                NewestWithdrawsInfo newestWithdrawsInfo = new NewestWithdrawsInfo();
                                newestWithdrawsInfo.setDate(object.getString("date"));
                                newestWithdrawsInfo.setImg_url(object.getString("img_url"));
                                newestWithdrawsInfo.setRecord(object.getString("record"));
                                list.add(newestWithdrawsInfo);
                            }
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Tools.showToast(NewestWithdrawsActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(NewestWithdrawsActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(NewestWithdrawsActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }
}
