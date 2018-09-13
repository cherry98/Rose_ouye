package com.orange.oy.activity.bigchange;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.activity.BrowserActivity;
import com.orange.oy.adapter.MessageMiddleAdapter;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.info.MessageLeftInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BroadcastActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.broadcast_title);
        appTitle.settingName("广播");
        appTitle.showBack(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (announcementlist != null) {
            announcementlist.stop(Urls.Announcementlist);
        }
    }

    private void initNetworkConnection() {
        announcementlist = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("page", page + "");
                return params;
            }
        };
    }

    private PullToRefreshListView broadcast_listview;
    private NetworkConnection announcementlist;
    private int page = 1;
    private ArrayList<MessageLeftInfo> list;
    private MessageMiddleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);
        initTitle();
        initNetworkConnection();
        list = new ArrayList<>();
        broadcast_listview = (PullToRefreshListView) findViewById(R.id.broadcast_listview);
        initListview(broadcast_listview);
        adapter = new MessageMiddleAdapter(this, list);
        broadcast_listview.setAdapter(adapter);
        broadcast_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshListView();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                getData();
            }
        });
        broadcast_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    MessageLeftInfo messageLeftInfo = list.get(position - 1);
                    Intent intent = new Intent(BroadcastActivity.this, BrowserActivity.class);
                    intent.putExtra("flag", BrowserActivity.flag_broadcast);
                    intent.putExtra("title", messageLeftInfo.getTitle());
                    intent.putExtra("content", messageLeftInfo.getMessage2());
                    intent.putExtra("id", messageLeftInfo.getId());
                    startActivity(intent);
                } catch (Exception e) {
                    Tools.showToast(BroadcastActivity.this, "数据异常，退出应用试试吧");
                }
            }
        });
        getData();
    }

    private void initListview(PullToRefreshListView listview) {
        listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listview.setPullLabel(getResources().getString(R.string.listview_down));// 刚下拉时，显示的提示
        listview.setRefreshingLabel(getResources().getString(R.string.listview_refush));// 刷新时
        listview.setReleaseLabel(getResources().getString(R.string.listview_down2));// 下来达到一定距离时，显示的提示
    }

    /**
     * 刷新列表
     */
    private void refreshListView() {
        page = 1;
        getData();
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    private void getData() {
        announcementlist.sendPostRequest(Urls.Announcementlist, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        if (list == null) {
                            list = new ArrayList<MessageLeftInfo>();
                        } else {
                            if (page == 1)
                                list.clear();
                        }
                        JSONArray jsonArray = jsonObject.getJSONArray("datas");
                        if (jsonArray != null) {
                            int lenght = jsonArray.length();
                            for (int i = 0; i < lenght; i++) {
                                jsonObject = jsonArray.getJSONObject(i);
                                MessageLeftInfo messageLeftInfo = new MessageLeftInfo();
                                messageLeftInfo.setId(jsonObject.getString("id"));
                                messageLeftInfo.setTitle(jsonObject.getString("title"));
                                String message = jsonObject.getString("brief");
                                if (TextUtils.isEmpty(message) || "null".equals(message)) {
                                    message = "";
                                }
                                messageLeftInfo.setMessage(message);
                                messageLeftInfo.setMessage2(jsonObject.getString("content"));
                                messageLeftInfo.setTime(jsonObject.getString("date"));
                                messageLeftInfo.setFlag(jsonObject.getString("type"));
                                list.add(messageLeftInfo);
                            }
                            broadcast_listview.onRefreshComplete();
                            if (lenght < 15) {
                                broadcast_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            } else {
                                broadcast_listview.setMode(PullToRefreshBase.Mode.BOTH);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        broadcast_listview.onRefreshComplete();
                        Tools.showToast(BroadcastActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    broadcast_listview.onRefreshComplete();
                    Tools.showToast(BroadcastActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                broadcast_listview.onRefreshComplete();
                Tools.showToast(BroadcastActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }
}
