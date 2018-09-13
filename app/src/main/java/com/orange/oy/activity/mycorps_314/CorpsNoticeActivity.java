package com.orange.oy.activity.mycorps_314;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.adapter.mycorps_314.CorpsNoticeAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.mycorps.CorpsNoticeInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.PullToRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 战队公告
 */
public class CorpsNoticeActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, AdapterView.OnItemClickListener, View.OnClickListener {
    private AppTitle corpsnotice_title;

    private void initTitle() {
        corpsnotice_title = (AppTitle) findViewById(R.id.corpsnotice_title);
        corpsnotice_title.settingName("战队公告");
        corpsnotice_title.showBack(this);
        if ("1".equals(user_identity) || "2".equals(user_identity)) {//队长或副队才能删除公告
            corpsnotice_title.showIllustrate(R.mipmap.grrw_button_shanchu, onExitClickForAppTitle1);
        }
    }

    private AppTitle.OnExitClickForAppTitle onExitClickForAppTitle1 = new AppTitle.OnExitClickForAppTitle() {
        public void onExit() {
            corpsnotice_title.hideIllustrate();
            corpsnotice_title.settingExit("完成", onExitClickForAppTitle2);
            if (corpsNoticeAdapter != null) {
                corpsNoticeAdapter.setEdit(true);
                corpsNoticeAdapter.notifyDataSetChanged();
            }
        }
    };

    private AppTitle.OnExitClickForAppTitle onExitClickForAppTitle2 = new AppTitle.OnExitClickForAppTitle() {
        public void onExit() {
            corpsnotice_title.hideExit();
            corpsnotice_title.showIllustrate(R.mipmap.grrw_button_shanchu, onExitClickForAppTitle1);
            if (corpsNoticeAdapter != null) {
                corpsNoticeAdapter.setEdit(false);
                corpsNoticeAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if (noticeList != null) {
            noticeList.stop(Urls.NoticeList);
        }
    }

    private void initNetwork() {
        noticeList = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(CorpsNoticeActivity.this));
                params.put("token", Tools.getToken());
                params.put("team_id", team_id);
                return params;
            }
        };
        delNotice = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("team_id", team_id);
                params.put("notice_id", notice_id);
                return params;
            }
        };
        delNotice.setIsShowDialog(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private CorpsNoticeAdapter corpsNoticeAdapter;
    private ListView corpsnotice_listview;
    private NetworkConnection noticeList, delNotice;
    private String team_id;
    private ArrayList<CorpsNoticeInfo> list;
    public static boolean isRefresh = false;
    private PullToRefreshLayout refreshLayout;
    private String notice_id, user_identity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corps_notice);
        team_id = getIntent().getStringExtra("team_id");
        user_identity = getIntent().getStringExtra("user_identity");
        list = new ArrayList<>();
        initTitle();
        initNetwork();
        refreshLayout = (PullToRefreshLayout) findViewById(R.id.refresh_view);
        corpsnotice_listview = (ListView) findViewById(R.id.corpsnotice_listview);
        corpsNoticeAdapter = new CorpsNoticeAdapter(this, list);
        corpsnotice_listview.setAdapter(corpsNoticeAdapter);
        corpsnotice_listview.setOnItemClickListener(this);
        if ("1".equals(user_identity) || "2".equals(user_identity)) {
            findViewById(R.id.corpsnotice_issue_ly).setVisibility(View.VISIBLE);
            findViewById(R.id.corpsnotice_issue).setOnClickListener(this);
        } else {
            findViewById(R.id.corpsnotice_issue_ly).setVisibility(View.GONE);
        }
        refreshLayoutListener();
    }

    private void refreshLayoutListener() {
        refreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                getData();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                if (refreshLayout != null) {
                    refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            }
        });
        refreshLayout.setCompleteListener(new PullToRefreshLayout.OnRetreshComplentListener() {
            @Override
            public void OnComplete() {
            }
        });
    }

    private void delNotice() {
        delNotice.sendPostRequest(Urls.DelNotice, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Tools.showToast(CorpsNoticeActivity.this, "删除成功");
                        getData();
                    } else {
                        Tools.showToast(CorpsNoticeActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(CorpsNoticeActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(CorpsNoticeActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    private void getData() {
        noticeList.sendPostRequest(Urls.NoticeList, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                if (refreshLayout != null) {
                    refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (!list.isEmpty()) {
                            list.clear();
                        }
                        jsonObject = jsonObject.getJSONObject("data");
                        JSONArray jsonArray = jsonObject.optJSONArray("list");
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                CorpsNoticeInfo corpsNoticeInfo = new CorpsNoticeInfo();
                                corpsNoticeInfo.setCreate_time(object.getString("createTime"));
                                corpsNoticeInfo.setNotice_id(object.getString("id"));
                                corpsNoticeInfo.setText(object.getString("text"));
                                corpsNoticeInfo.setTitle(object.getString("title"));
                                corpsNoticeInfo.setIs_read(object.getString("is_read"));
                                corpsNoticeInfo.setHead_img(object.getString("head_img"));
                                list.add(corpsNoticeInfo);
                            }
                            if (corpsNoticeAdapter != null) {
                                corpsNoticeAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Tools.showToast(CorpsNoticeActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(CorpsNoticeActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(CorpsNoticeActivity.this, getResources().getString(R.string.network_volleyerror));
                if (refreshLayout != null) {
                    refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (corpsNoticeAdapter != null) {
            CorpsNoticeInfo corpsNoticeInfo = list.get(position);
            notice_id = corpsNoticeInfo.getNotice_id();
            if (corpsNoticeAdapter.isClick()) {
                corpsNoticeAdapter.clearClick();
                ConfirmDialog.showDialog(CorpsNoticeActivity.this, "提示！", 3, "您确认要删除这条公告吗？", "取消", "确定",
                        null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                            @Override
                            public void leftClick(Object object) {

                            }

                            @Override
                            public void rightClick(Object object) {
                                delNotice();
                            }
                        });
            } else {
                Intent intent = new Intent(CorpsNoticeActivity.this, NoticeDetailActivity.class);
                intent.putExtra("team_id", team_id);
                intent.putExtra("notice_id", corpsNoticeInfo.getNotice_id());
                startActivity(intent);
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.corpsnotice_issue: {
                Intent intent = new Intent(CorpsNoticeActivity.this, IssueNoticeActivity.class);
                intent.putExtra("team_id", team_id);
                startActivity(intent);
            }
            break;
        }
    }
}
