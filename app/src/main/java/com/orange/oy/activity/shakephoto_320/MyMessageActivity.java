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
import com.orange.oy.adapter.mycorps_314.MyMessageAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.info.shakephoto.MyMessageInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.NetworkView;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 我的-> 消息页面 V3.20
 */
public class MyMessageActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, AdapterView.OnItemClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.mymessage_title);
        appTitle.settingName("消息");
        appTitle.showBack(this);

    }

    private void initNetworkConnection() {
        messageList = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(MyMessageActivity.this));
                params.put("page", page + "");
                return params;
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (messageList != null) {
            messageList.stop(Urls.MessageList);
        }
    }

    private PullToRefreshListView mymessage_listview;
    private NetworkConnection messageList;
    private int page;
    private ArrayList<MyMessageInfo> list;
    private NetworkView lin_Nodata;
    private MyMessageAdapter myMessageAdapter;
    private Intent data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_message);
        data = getIntent();
        list = new ArrayList<>();
        initTitle();
        initNetworkConnection();
        mymessage_listview = (PullToRefreshListView) findViewById(R.id.mymessage_listview);
        mymessage_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        lin_Nodata = (NetworkView) findViewById(R.id.lin_Nodata);
        myMessageAdapter = new MyMessageAdapter(this, list);
        mymessage_listview.setAdapter(myMessageAdapter);
        mymessage_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                getData();
            }
        });
        getData();
        mymessage_listview.setOnItemClickListener(this);
    }

    private void refreshData() {
        page = 1;
        getData();
    }

    private void getData() {
        messageList.sendPostRequest(Urls.MessageList, new Response.Listener<String>() {
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
                        JSONArray jsonArray = jsonObject.optJSONArray("message_list");
                        if (jsonArray != null && jsonArray.length() > 0) {
                            lin_Nodata.setVisibility(View.GONE);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                MyMessageInfo myMessageInfo = new MyMessageInfo();
                                myMessageInfo.setUser_id(object.getString("user_id"));
                                myMessageInfo.setUser_name(object.getString("user_name"));
                                myMessageInfo.setUser_mobile(object.getString("user_mobile"));
                                myMessageInfo.setUser_img(object.getString("user_img"));
                                myMessageInfo.setIs_ouye(object.getString("is_ouye"));
                                myMessageInfo.setCreate_time(object.getString("create_time"));
                                myMessageInfo.setMessage(object.getString("message"));
                                list.add(myMessageInfo);
                            }
                            mymessage_listview.onRefreshComplete();
                            if (jsonArray.length() < 15) {
                                mymessage_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            } else {
                                mymessage_listview.setMode(PullToRefreshBase.Mode.BOTH);
                            }
                            if (myMessageAdapter != null) {
                                myMessageAdapter.notifyDataSetChanged();
                            }
                        } else {
                            lin_Nodata.setVisibility(View.VISIBLE);
                            lin_Nodata.NoSearch("没有消息噢~");
                        }
                    } else {
                        Tools.showToast(MyMessageActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(MyMessageActivity.this, getResources().getString(R.string.network_error));
                }
                mymessage_listview.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                lin_Nodata.NoNetwork();
                lin_Nodata.setVisibility(View.VISIBLE);
                mymessage_listview.onRefreshComplete();
                Tools.showToast(MyMessageActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (myMessageAdapter != null) {
            MyMessageInfo myMessageInfo = list.get(position - 1);
            Intent intent = new Intent(this, MyMessageDetailActivity.class);
            intent.putExtra("is_ouye", myMessageInfo.getIs_ouye());
            intent.putExtra("user_id", myMessageInfo.getUser_id());
            String user_name = myMessageInfo.getUser_name();
            if ("1".equals(myMessageInfo.getIs_ouye())) {
                user_name = "偶业小秘";
            }
            intent.putExtra("user_name", user_name);
            intent.putExtra("latitude", data.getStringExtra("latitude"));
            intent.putExtra("longitude", data.getStringExtra("longitude"));
            intent.putExtra("address", data.getStringExtra("address"));
            intent.putExtra("province", data.getStringExtra("province"));
            intent.putExtra("city", data.getStringExtra("city"));
            startActivity(intent);
        }
    }
}
