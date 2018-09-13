package com.orange.oy.activity.bright;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.adapter.BrightPersonInfoAdapter;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.BrightPersonInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BrightPersonInfoActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle,
        View.OnClickListener {

    private void initTitle() {
        AppTitle taskitemlist_title = (AppTitle) findViewById(R.id.bright_title_personinfo);
        taskitemlist_title.settingName("人员信息");
        taskitemlist_title.showBack(this);
    }

    public void initNetworkConnection() {
        assistantInfo = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("outletid", outletid);
                params.put("state", state + "");
                return params;
            }
        };
        assistantInfo.setIsShowDialog(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (assistantInfo != null) {
            assistantInfo.stop(Urls.AssistantInfo);
        }
    }

    private int state;
    private NetworkConnection assistantInfo;
    private String outletid, store_name, addr;
    private PullToRefreshListView brightinfo_listview_left, brightinfo_listview_right;
    private BrightPersonInfoAdapter adapterLeft;
    private ArrayList<BrightPersonInfo> list;
    private TextView bright_name_personinfo, bright_addr_personinfo, bright_info_showall_circle, bright_info_showpart_circle;
    private Intent data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bright_person_info);
        initTitle();
        initNetworkConnection();
        data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        list = new ArrayList<>();
        outletid = data.getStringExtra("outletid");
        store_name = data.getStringExtra("store_name");
        addr = data.getStringExtra("city");
        brightinfo_listview_left = (PullToRefreshListView) findViewById(R.id.brightinfo_listview_left);
        brightinfo_listview_right = (PullToRefreshListView) findViewById(R.id.brightinfo_listview_right);
        initListView(brightinfo_listview_left);
        initListView(brightinfo_listview_right);
        brightinfo_listview_left.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshLeft();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                state = 0;
                getData();
            }
        });
        brightinfo_listview_right.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshRight();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                state = 1;
                getData();
            }
        });
        bright_name_personinfo = (TextView) findViewById(R.id.bright_name_personinfo);
        bright_addr_personinfo = (TextView) findViewById(R.id.bright_addr_personinfo);
        bright_info_showall_circle = (TextView) findViewById(R.id.bright_info_showall_circle);
        bright_info_showpart_circle = (TextView) findViewById(R.id.bright_info_showpart_circle);
        bright_name_personinfo.setText(store_name);
        bright_addr_personinfo.setText(addr);
        adapterLeft = new BrightPersonInfoAdapter(this, list);
        brightinfo_listview_left.setAdapter(adapterLeft);
        brightinfo_listview_right.setAdapter(adapterLeft);
        View view = findViewById(R.id.bright_info_showall);
        view.setOnClickListener(this);
        onClick(view);
        findViewById(R.id.bright_info_showpart).setOnClickListener(this);
        findViewById(R.id.bright_button_personinfo).setOnClickListener(this);
    }

    private void refreshLeft() {
        state = 0;
        getData();
    }

    private void refreshRight() {
        state = 1;
        getData();
    }

    public void initListView(PullToRefreshListView listView) {
        listView.setMode(PullToRefreshListView.Mode.PULL_FROM_START);
        listView.setPullLabel("下拉刷新");
        listView.setRefreshingLabel("正在刷新");
        listView.setReleaseLabel("释放刷新");
    }

    @Override
    public void onBack() {
        BrightTwoCodeActivity.isRefresh = true;
        baseFinish();
    }

    public static boolean isRefresh = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (isRefresh) {
            state = 0;
            getData();
        }
    }

    public void getData() {
        assistantInfo.sendPostRequest(Urls.AssistantInfo, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                if (list == null) {
                    list = new ArrayList<BrightPersonInfo>();
                } else {
                    list.clear();
                }
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        JSONArray jsonArray = jsonObject.getJSONArray("datas");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            BrightPersonInfo brightPersonInfo = new BrightPersonInfo();
                            JSONObject object = jsonArray.getJSONObject(i);
                            brightPersonInfo.setName(object.getString("name"));
                            brightPersonInfo.setSex(object.getString("sex"));
                            brightPersonInfo.setMobile(object.getString("mobile"));
                            brightPersonInfo.setEmail(object.getString("email"));
                            brightPersonInfo.setDealer(object.getString("dealer"));
                            brightPersonInfo.setIdcardnum(object.getString("idcardnum"));
                            brightPersonInfo.setState(object.getString("state"));
                            brightPersonInfo.setNote(object.getString("note"));
                            brightPersonInfo.setJob(object.getString("job"));
                            brightPersonInfo.setIs_note(Tools.StringToInt(object.getString("is_note")));
                            brightPersonInfo.setIscomplete(Tools.StringToInt(object.getString("iscomplete")));
                            list.add(brightPersonInfo);
                        }
                        if (state == 0) {
                            brightinfo_listview_left.onRefreshComplete();
                            if (jsonArray.length() < 15) {
                                brightinfo_listview_left.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            } else {
                                brightinfo_listview_left.setMode(PullToRefreshBase.Mode.BOTH);
                            }
                        } else {
                            brightinfo_listview_right.onRefreshComplete();
                            if (jsonArray.length() < 15) {
                                brightinfo_listview_right.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            } else {
                                brightinfo_listview_right.setMode(PullToRefreshBase.Mode.BOTH);
                            }
                        }
                        adapterLeft.notifyDataSetChanged();
                    } else {
                        Tools.showToast(BrightPersonInfoActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(BrightPersonInfoActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
                if (state == 0) {
                    brightinfo_listview_left.onRefreshComplete();
                } else {
                    brightinfo_listview_right.onRefreshComplete();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                if (state == 0) {
                    brightinfo_listview_left.onRefreshComplete();
                } else {
                    brightinfo_listview_right.onRefreshComplete();
                }
                Tools.showToast(BrightPersonInfoActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bright_info_showall: {
                state = 0;
                getData();
                bright_info_showall_circle.setBackgroundResource(R.drawable.bright_info_showblue);
                bright_info_showpart_circle.setBackgroundResource(R.drawable.bright_info_showgrey);
                brightinfo_listview_left.setVisibility(View.VISIBLE);
                brightinfo_listview_right.setVisibility(View.GONE);
            }
            break;
            case R.id.bright_info_showpart: {
                state = 1;
                getData();
                bright_info_showall_circle.setBackgroundResource(R.drawable.bright_info_showgrey);
                bright_info_showpart_circle.setBackgroundResource(R.drawable.bright_info_showblue);
                brightinfo_listview_left.setVisibility(View.GONE);
                brightinfo_listview_right.setVisibility(View.VISIBLE);
            }
            break;
            case R.id.bright_button_personinfo:
                Intent intent = new Intent(BrightPersonInfoActivity.this, BrightBallotActivity.class);
                intent.putExtra("store_num", data.getStringExtra("store_num"));
                intent.putExtra("city", addr);
                intent.putExtra("outletid", outletid);
                intent.putExtra("project_id", data.getStringExtra("project_id"));
                intent.putExtra("projectname", data.getStringExtra("projectname"));
                intent.putExtra("code", data.getStringExtra("code"));
                intent.putExtra("brand", data.getStringExtra("brand"));
                intent.putExtra("store_name", data.getStringExtra("store_name"));
                intent.putExtra("photo_compression", data.getStringExtra("photo_compression"));
                startActivity(intent);
                break;
        }
    }
}
