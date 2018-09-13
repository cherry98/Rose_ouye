package com.orange.oy.activity.alipay;

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
import com.orange.oy.adapter.SelectprojectAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.SelectprojectInfo;
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
 * 提现-选择项目页
 */
public class SelectprojectActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, AdapterView
        .OnItemClickListener, View.OnClickListener {
    private SelectprojectAdapter selectprojectAdapter;
    private PullToRefreshListView selectproject_listview;
    private ArrayList<SelectprojectInfo> list = new ArrayList<>();
    private NetworkConnection Withdrawals, Getmoney;

    protected void onStop() {
        super.onStop();
        if (Withdrawals != null) {
            Withdrawals.stop(Urls.Withdrawals);
        }
        if (Getmoney != null) {
            Getmoney.stop(Urls.Getmoney);
        }
    }

    private int pageNum;

    private void initNetworkConnection() {
        Withdrawals = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(SelectprojectActivity.this));
                params.put("pageNum", pageNum + "");
                return params;
            }
        };
        Getmoney = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                SelectprojectInfo selectprojectInfo = list.remove(selectprojectAdapter.getSelectPosition());
                runOnUiThread(new Runnable() {
                    public void run() {
                        selectprojectAdapter.notifyDataSetChanged();
                    }
                });
                selectprojectAdapter.setSelectPosition(-1);
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(SelectprojectActivity.this));
                params.put("projectid", selectprojectInfo.getId());
                params.put("counts", selectprojectInfo.getOutletNum() + "");
                params.put("money", selectprojectInfo.getMoney());
                params.put("type", selectprojectInfo.getType());
                return params;
            }
        };
        Getmoney.setIsShowDialog(true);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectproject);
        pageNum = 1;
        initNetworkConnection();
        AppTitle selectproject_title = (AppTitle) findViewById(R.id.selectproject_title);
        selectproject_title.showBack(this);
        selectproject_title.settingName("选择项目");
        ((TextView) findViewById(R.id.selectproject_money)).setText(getIntent().getStringExtra("getmoney"));
        selectproject_listview = (PullToRefreshListView) findViewById(R.id.selectproject_listview);
        selectproject_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        selectproject_listview.setPullLabel(getResources().getString(R.string.listview_down));
        selectproject_listview.setRefreshingLabel(getResources().getString(R.string.listview_refush));
        selectproject_listview.setReleaseLabel(getResources().getString(R.string.listview_down2));
        selectproject_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                pageNum = 1;
                getData();
            }

            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                pageNum++;
                getData();
            }
        });
        selectprojectAdapter = new SelectprojectAdapter(this, list);
        selectproject_listview.setAdapter(selectprojectAdapter);
        selectproject_listview.setOnItemClickListener(this);
        findViewById(R.id.selectproject_button).setOnClickListener(this);
        getData();
    }

    public static boolean isRefresh = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (isRefresh) {
            getData();
        }
    }

    private void getData() {
        Withdrawals.sendPostRequest(Urls.Withdrawals, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        if (pageNum == 1) {
                            list.clear();
                        }
                        JSONArray datas = jsonObject.optJSONArray("datas");
                        if (datas != null) {
                            int length = datas.length();
                            JSONObject jsonObject1;
                            for (int i = 0; i < length; i++) {
                                jsonObject1 = datas.getJSONObject(i);
                                SelectprojectInfo selectprojectInfo = new SelectprojectInfo();
                                selectprojectInfo.setId(jsonObject1.getString("projectId"));
                                selectprojectInfo.setProjectName(jsonObject1.getString("projectName"));
                                selectprojectInfo.setMoney(jsonObject1.getString("money"));
                                selectprojectInfo.setOutletNum(Tools.StringToInt(jsonObject1.getString("counts")));
                                selectprojectInfo.setType(jsonObject1.getString("type"));
                                selectprojectInfo.setExechangeTime(jsonObject1.getString("exechangeTime"));
                                list.add(selectprojectInfo);
                            }
                            if (length < 15) {
                                selectproject_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            } else {
                                selectproject_listview.setMode(PullToRefreshBase.Mode.BOTH);
                            }
                            selectprojectAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Tools.showToast(SelectprojectActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(SelectprojectActivity.this, getResources().getString(R.string.network_error));
                }
                selectproject_listview.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                selectproject_listview.onRefreshComplete();
                Tools.showToast(SelectprojectActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private void sendData() {
        Getmoney.sendPostRequest(Urls.Getmoney, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                CustomProgressDialog.Dissmiss();
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        startActivity(new Intent(SelectprojectActivity.this, CommitsuccessActivity.class));
                    } else if (code == 2) {
//                        boolean isAccount = AppInfo.isBindAccount(SelectprojectActivity.this);
//                        if (isAccount) {//如果没绑支付宝
//                            Intent intent = new Intent(SelectprojectActivity.this, IdentityActivity.class);
////                    intent.putExtra("isskip", "1");
//                            intent.putExtra("isback", "1");
//                            intent.putExtra("ismyaccount", "0");
//                            intent.putExtra("bindaccount", "0");//是否绑定支付宝 0-未绑定 1-绑定
//                            startActivity(intent);
//                        }
                    } else {
                        Tools.showToast(SelectprojectActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(SelectprojectActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(SelectprojectActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position = position - 1;
        if (position == selectprojectAdapter.getSelectPosition()) {
            selectprojectAdapter.setSelectPosition(-1);
        } else {
            selectprojectAdapter.setSelectPosition(position);
        }
        selectprojectAdapter.notifyDataSetChanged();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selectproject_button: {//确定
                if (selectprojectAdapter.getSelectPosition() == -1) {
                    Tools.showToast(this, "请选择项目");
                    return;
                }
                sendData();
            }
            break;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    public void onBack() {
        baseFinish();
    }
}
