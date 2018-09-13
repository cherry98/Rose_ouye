package com.orange.oy.activity.bright;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.adapter.BrightBallotResultAdapter;
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

public class BrightBallotResultActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {

    private void initTitle(String str) {
        AppTitle taskitemlist_title = (AppTitle) findViewById(R.id.bright_title_ballotresult);
        taskitemlist_title.settingName(str);
        taskitemlist_title.showBack(this);
    }

    public void initNetworkConnection() {
        selectassistantinfo = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> parmas = new HashMap<>();
                parmas.put("token", Tools.getToken());
                parmas.put("outletid", outletid);
                parmas.put("selectid", selectid + "");
                return parmas;
            }
        };
        selectassistantinfo.setIsShowDialog(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (selectassistantinfo != null) {
            selectassistantinfo.stop(Urls.SelectAssistantInfo);
        }
    }

    private String outletid;
    private int selectid;
    private NetworkConnection selectassistantinfo;
    private PullToRefreshListView brightresult_listview;
    private BrightBallotResultAdapter brightBallotResultAdapter;
    private TextView bright_num_ballotresult, bright_text_ballotresult;
    private ArrayList<BrightBallotInfo> list;
    private Intent data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bright_ballot_result);
        data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        list = new ArrayList<>();
        outletid = data.getStringExtra("outletid");
        selectid = data.getIntExtra("selectid", 0);
        initTitle(data.getStringExtra("type"));
        initNetworkConnection();
        bright_num_ballotresult = (TextView) findViewById(R.id.bright_num_ballotresult);
        bright_text_ballotresult = (TextView) findViewById(R.id.bright_text_ballotresult);
        brightresult_listview = (PullToRefreshListView) findViewById(R.id.brightresult_listview);
        bright_num_ballotresult.setText(data.getIntExtra("num", 0) + "");
        bright_text_ballotresult.setText(data.getStringExtra("type") + "抽签结果：");
        initListView(brightresult_listview);
        getData();
        brightBallotResultAdapter = new BrightBallotResultAdapter(this, list);
        brightresult_listview.setAdapter(brightBallotResultAdapter);
        brightresult_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getData();
            }
        });
        brightresult_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BrightBallotInfo brightBallotInfo = list.get(position - 1);
                if (brightBallotInfo.getIscomplete() == 0) {
                    if ("1".equals(brightBallotInfo.getTasktype())) {//拍照任务
                        Intent intent = new Intent(BrightBallotResultActivity.this, BrightTakephotoillustrateActivity.class);
                        intent.putExtra("executeid", brightBallotInfo.getExecuteid());
                        intent.putExtra("taskid", brightBallotInfo.getTaskid());
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
                    } else if ("2".equals(brightBallotInfo.getTasktype())) {//视频任务
                        Intent intent = new Intent(BrightBallotResultActivity.this, BrightShotillustrateActivity.class);
                        intent.putExtra("executeid", brightBallotInfo.getExecuteid());
                        intent.putExtra("taskid", brightBallotInfo.getTaskid());
                        intent.putExtra("selectid", brightBallotInfo.getSelectid());
                        intent.putExtra("outletid", outletid);
                        intent.putExtra("project_id", data.getStringExtra("project_id"));
                        intent.putExtra("projectname", data.getStringExtra("projectname"));
                        intent.putExtra("code", data.getStringExtra("code"));
                        intent.putExtra("brand", data.getStringExtra("brand"));
                        intent.putExtra("store_name", data.getStringExtra("store_name"));
                        intent.putExtra("store_num", data.getStringExtra("store_num"));
                        startActivity(intent);
                    } else if ("3".equals(brightBallotInfo.getTasktype())) {//记录任务
                        Intent intent = new Intent(BrightBallotResultActivity.this, BrightEditillustrateActivity.class);
                        intent.putExtra("executeid", brightBallotInfo.getExecuteid());
                        intent.putExtra("taskid", brightBallotInfo.getTaskid());
                        intent.putExtra("selectid", brightBallotInfo.getSelectid());
                        intent.putExtra("outletid", outletid);
                        intent.putExtra("project_id", data.getStringExtra("project_id"));
                        intent.putExtra("projectname", data.getStringExtra("projectname"));
                        intent.putExtra("code", data.getStringExtra("code"));
                        intent.putExtra("brand", data.getStringExtra("brand"));
                        intent.putExtra("store_name", data.getStringExtra("store_name"));
                        intent.putExtra("store_num", data.getStringExtra("store_num"));
                        startActivity(intent);
                    } else if ("4".equals(brightBallotInfo.getTasktype())) {//定位任务
                        Intent intent = new Intent(BrightBallotResultActivity.this, BrightMapActivity.class);
                        intent.putExtra("executeid", brightBallotInfo.getExecuteid());
                        intent.putExtra("taskid", brightBallotInfo.getTaskid());
                        intent.putExtra("selectid", brightBallotInfo.getSelectid());
                        intent.putExtra("outletid", outletid);
                        intent.putExtra("project_id", data.getStringExtra("project_id"));
                        intent.putExtra("projectname", data.getStringExtra("projectname"));
                        intent.putExtra("code", data.getStringExtra("code"));
                        intent.putExtra("brand", data.getStringExtra("brand"));
                        intent.putExtra("store_name", data.getStringExtra("store_name"));
                        intent.putExtra("store_num", data.getStringExtra("store_num"));
                        startActivity(intent);
                    } else if ("5".equals(brightBallotInfo.getTasktype())) {//录音任务
                        Intent intent = new Intent(BrightBallotResultActivity.this, BrightRecordillustrateActivity.class);
                        intent.putExtra("executeid", brightBallotInfo.getExecuteid());
                        intent.putExtra("taskid", brightBallotInfo.getTaskid());
                        intent.putExtra("selectid", brightBallotInfo.getSelectid());
                        intent.putExtra("outletid", outletid);
                        intent.putExtra("project_id", data.getStringExtra("project_id"));
                        intent.putExtra("projectname", data.getStringExtra("projectname"));
                        intent.putExtra("code", data.getStringExtra("code"));
                        intent.putExtra("brand", data.getStringExtra("brand"));
                        intent.putExtra("store_name", data.getStringExtra("store_name"));
                        intent.putExtra("store_num", data.getStringExtra("store_num"));
                        startActivity(intent);
                    }
                }
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    public void initListView(PullToRefreshListView listView) {
        listView.setMode(PullToRefreshListView.Mode.PULL_FROM_START);
        listView.setPullLabel("下拉刷新");
        listView.setRefreshingLabel("正在刷新");
        listView.setReleaseLabel("释放刷新");
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

    public void getData() {
        selectassistantinfo.sendPostRequest(Urls.SelectAssistantInfo, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    list.clear();
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        JSONArray jsonArray = jsonObject.getJSONArray("datas");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            BrightBallotInfo brightBallotInfo = new BrightBallotInfo();
                            JSONObject object = jsonArray.getJSONObject(i);
                            brightBallotInfo.setName(object.getString("name"));
                            brightBallotInfo.setSex(object.getString("sex"));
                            brightBallotInfo.setMobile(object.getString("mobile"));
                            brightBallotInfo.setEmail(object.getString("email"));
                            brightBallotInfo.setDealer(object.getString("dealer"));
                            brightBallotInfo.setIdcardnum(object.getString("idcardnum"));
                            brightBallotInfo.setState(object.getString("state"));
                            brightBallotInfo.setNote(object.getString("note"));
                            brightBallotInfo.setIs_note(object.getString("is_note"));
                            brightBallotInfo.setTaskid(Tools.StringToInt(object.getString("taskid")));
                            brightBallotInfo.setExecuteid(Tools.StringToInt(object.getString("executeid")));
                            brightBallotInfo.setIscomplete(Tools.StringToInt(object.getString("iscomplete")));
                            brightBallotInfo.setTasktype(object.getString("tasktype"));
                            list.add(brightBallotInfo);
                        }
                        brightresult_listview.onRefreshComplete();
                        if (jsonArray.length() < 15) {
                            brightresult_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            brightresult_listview.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                        brightBallotResultAdapter.notifyDataSetChanged();
                    } else {
                        Tools.showToast(BrightBallotResultActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(BrightBallotResultActivity.this, getResources().getString(R.string.network_error));
                }
                brightresult_listview.onRefreshComplete();
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                brightresult_listview.onRefreshComplete();
                Tools.showToast(BrightBallotResultActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }
}
