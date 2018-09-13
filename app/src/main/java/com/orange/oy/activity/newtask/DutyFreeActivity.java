package com.orange.oy.activity.newtask;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.adapter.DutyFreeAdapter;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.DutyFreeInfo;
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
 * 提现800==免税额度页面
 */
public class DutyFreeActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.dutyfree_title);
        appTitle.settingName("免税额度");
        appTitle.showBack(this);
        appTitle.settingExit("查看好友明细", new AppTitle.OnExitClickForAppTitle() {
            @Override
            public void onExit() {
                startActivity(new Intent(DutyFreeActivity.this, FriendsDetailedActivity.class));
            }
        });
    }

    private void initNetworkConnection() {
        getDutyFreeObtainLog = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("page", page + "");
                return params;
            }
        };
        getDutyFreeObtainLog.setIsShowDialog(true);
    }

    private NetworkConnection getDutyFreeObtainLog;
    private int page = 1;
    private ArrayList<DutyFreeInfo> list;
    private PullToRefreshListView dutyfree_listview;
    private DutyFreeAdapter dutyFreeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duty_free);
        list = new ArrayList<>();
        initTitle();
        initNetworkConnection();
        dutyfree_listview = (PullToRefreshListView) findViewById(R.id.dutyfree_listview);
        dutyFreeAdapter = new DutyFreeAdapter(this, list);
        dutyfree_listview.setAdapter(dutyFreeAdapter);
        dutyfree_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
        findViewById(R.id.dutyfree_more).setOnClickListener(this);
        getData();
    }


    @Override
    public void onBack() {
        baseFinish();
    }

    public void getData() {
        getDutyFreeObtainLog.sendPostRequest(Urls.GetDutyFreeObtainLog, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    if (list == null) {
                        list = new ArrayList<DutyFreeInfo>();
                    } else if (page == 1) {
                        list.clear();
                    }
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        JSONArray jsonArray = jsonObject.optJSONArray("logs");
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                DutyFreeInfo dutyFreeInfo = new DutyFreeInfo();
                                dutyFreeInfo.setId(object.getString("id"));
                                dutyFreeInfo.setMoney(object.getString("money"));
                                dutyFreeInfo.setObtainTime(object.getString("obtainTime"));
                                dutyFreeInfo.setRemark(object.getString("remark"));
                                dutyFreeInfo.setType(object.getString("type"));
                                dutyFreeInfo.setUsersMobileId(object.getString("usersMobileId"));
                                list.add(dutyFreeInfo);
                            }
                            if (dutyFreeAdapter != null) {
                                dutyFreeAdapter.notifyDataSetChanged();
                            }
                            if (jsonArray.length() < 15) {
                                dutyfree_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            } else {
                                dutyfree_listview.setMode(PullToRefreshBase.Mode.BOTH);
                            }
                        }
                        dutyfree_listview.onRefreshComplete();
                    } else {
                        Tools.showToast(DutyFreeActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(DutyFreeActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
                dutyfree_listview.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(DutyFreeActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
                dutyfree_listview.onRefreshComplete();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dutyfree_more) {
            startActivity(new Intent(DutyFreeActivity.this, ObtainDutyFreeActivity.class));
        }
    }
}
