package com.orange.oy.activity.bright;

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
import com.orange.oy.adapter.BrightBallotAdapter;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.BrightBallotInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BrightBallotActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {

    private void initTitle() {
        AppTitle taskitemlist_title = (AppTitle) findViewById(R.id.bright_title_ballot);
        taskitemlist_title.settingName("抽签结果");
        taskitemlist_title.showBack(this);
    }

    private void initNetworkConnection() {
        selectresult = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> parmas = new HashMap<>();
                parmas.put("token", Tools.getToken());
                parmas.put("outletid", outletid);
                return parmas;
            }
        };
        selectresult.setIsShowDialog(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (selectresult != null) {
            selectresult.stop(Urls.SelectResult);
        }
    }

    private String outletid;
    private PullToRefreshListView pullToRefreshListView;
    private ListView listView;//添加头布局转换
    private NetworkConnection selectresult;
    private ArrayList<BrightBallotInfo> list;
    private BrightBallotAdapter brightBallotAdapter;
    private Intent data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bright_ballot);
        initTitle();
        data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        initNetworkConnection();
        outletid = data.getStringExtra("outletid");
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.brightballot_listview);
        initListView(pullToRefreshListView);
        list = new ArrayList<>();
        brightBallotAdapter = new BrightBallotAdapter(this, list);
        listView = pullToRefreshListView.getRefreshableView();
        View headerView = View.inflate(this, R.layout.item_brightballot_header, null);
        listView.addHeaderView(headerView);
        pullToRefreshListView.setAdapter(brightBallotAdapter);
        getData();
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getData();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 1) {
                    BrightBallotInfo brightBallotInfo = list.get(position - 2);
                    Intent intent = new Intent(BrightBallotActivity.this, BrightBallotResultActivity.class);
                    intent.putExtra("type", brightBallotInfo.getType());
                    intent.putExtra("num", brightBallotInfo.getNum());
                    intent.putExtra("selectid", brightBallotInfo.getSelectid());
                    intent.putExtra("outletid", outletid);
                    intent.putExtra("project_id", data.getStringExtra("project_id"));
                    intent.putExtra("projectname", data.getStringExtra("projectname"));
                    intent.putExtra("code", data.getStringExtra("code"));
                    intent.putExtra("brand", data.getStringExtra("brand"));
                    intent.putExtra("store_name", data.getStringExtra("store_name"));
                    intent.putExtra("store_num", data.getStringExtra("store_num"));
                    intent.putExtra("photo_compression", data.getStringExtra("photo_compression"));
                    startActivity(intent);
                }
            }
        });
    }

    public static boolean isRefresh = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (isRefresh) {
            getData();
            isRefresh = false;
        }
    }

    public void initListView(PullToRefreshListView listView) {
        listView.setMode(PullToRefreshListView.Mode.PULL_FROM_START);
        listView.setPullLabel("下拉刷新");
        listView.setRefreshingLabel("正在刷新");
        listView.setReleaseLabel("释放刷新");
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    public void getData() {
        selectresult.sendPostRequest(Urls.SelectResult, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    list.clear();
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        JSONArray jsonArray = jsonObject.getJSONArray("datas");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            BrightBallotInfo brightBallot = new BrightBallotInfo();
                            brightBallot.setType(object.getString("type"));
                            brightBallot.setComplete(Tools.StringToInt(object.getString("complete")));
                            brightBallot.setNum(Tools.StringToInt(object.getString("num")));
                            brightBallot.setSelectid(Tools.StringToInt(object.getString("selectid")));
                            list.add(brightBallot);
                        }
                        pullToRefreshListView.onRefreshComplete();
                        if (jsonArray.length() < 15) {
                            pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                        brightBallotAdapter.notifyDataSetChanged();
                    } else {
                        Tools.showToast(BrightBallotActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(BrightBallotActivity.this, getResources().getString(R.string.network_error));
                }
                pullToRefreshListView.onRefreshComplete();
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                pullToRefreshListView.onRefreshComplete();
                Tools.showToast(BrightBallotActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }
}
