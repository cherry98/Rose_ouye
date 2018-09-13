package com.orange.oy.activity.newtask;

import android.os.Bundle;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.adapter.FriendsDetailedAdapter;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.FriendsDetailedInfo;
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
 * 提现800==好友明细页面
 */
public class FriendsDetailedActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.friendsdetailed_title);
        appTitle.settingName("好友明细");
        appTitle.showBack(this);
    }

    private void initNetworkConnection() {
        getDutyFreeFriends = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("page", page + "");
                return params;
            }
        };
        getDutyFreeFriends.setIsShowDialog(true);
    }

    private NetworkConnection getDutyFreeFriends;
    private int page = 1;
    private ArrayList<FriendsDetailedInfo> list;
    private FriendsDetailedAdapter friendsDetailedAdapter;
    private PullToRefreshListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_detailed);
        list = new ArrayList<>();
        initTitle();
        initNetworkConnection();
        listView = (PullToRefreshListView) findViewById(R.id.friendsdetailed_listview);
        friendsDetailedAdapter = new FriendsDetailedAdapter(this, list);
        listView.setAdapter(friendsDetailedAdapter);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
        getData();
    }

    private void getData() {
        getDutyFreeFriends.sendPostRequest(Urls.GetDutyFreeFriends, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    if (list == null) {
                        list = new ArrayList<FriendsDetailedInfo>();
                    } else if (page == 1) {
                        list.clear();
                    }
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        JSONArray jsonArray = jsonObject.optJSONArray("friends");
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                FriendsDetailedInfo friendsDetailedInfo = new FriendsDetailedInfo();
                                friendsDetailedInfo.setCreateTime(object.getString("createTime"));
                                friendsDetailedInfo.setFriendMobile(object.getString("friendMobile"));
                                friendsDetailedInfo.setUsersMobileId(object.getString("usersMobileId"));
                                friendsDetailedInfo.setState(object.getString("state"));
                                friendsDetailedInfo.setId(object.getString("id"));
                                friendsDetailedInfo.setStateStr(object.getString("stateStr"));
                                list.add(friendsDetailedInfo);
                            }
                            if (friendsDetailedAdapter != null) {
                                friendsDetailedAdapter.notifyDataSetChanged();
                            }
                            if (jsonArray.length() < 15) {
                                listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            } else {
                                listView.setMode(PullToRefreshBase.Mode.BOTH);
                            }
                        }
                        listView.onRefreshComplete();
                    } else {
                        Tools.showToast(FriendsDetailedActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(FriendsDetailedActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listView.onRefreshComplete();
                Tools.showToast(FriendsDetailedActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }
}
