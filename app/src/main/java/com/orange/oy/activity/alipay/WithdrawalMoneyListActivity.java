package com.orange.oy.activity.alipay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.info.WithdrawalMoneyListInfo;
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
 * 提现明细列表
 */
public class WithdrawalMoneyListActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, AdapterView.OnItemClickListener {
    private NetworkConnection getMoneyOrders;
    private int pageNum;
    private PullToRefreshListView listView;
    private ArrayList<WithdrawalMoneyListInfo> list = new ArrayList<>();
    private MyAdapter adapter;

    private void initNetworkConnection() {
        getMoneyOrders = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("pageNum", pageNum + "");
                return params;
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (getMoneyOrders != null) {
            getMoneyOrders.stop(Urls.GetMoneyOrders);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawalmoneylist);
        pageNum = 1;
        initNetworkConnection();
        AppTitle title = (AppTitle) findViewById(R.id.withdrawalmoney_apptitle);
        title.showBack(this);
        title.settingName("提现明细");
        listView = (PullToRefreshListView) findViewById(R.id.withdrawalmoney_listview);
        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listView.setPullLabel(getResources().getString(R.string.listview_down));
        listView.setRefreshingLabel(getResources().getString(R.string.listview_refush));
        listView.setReleaseLabel(getResources().getString(R.string.listview_down2));
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                pageNum = 1;
                getData();
            }

            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                pageNum++;
                getData();
            }
        });
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        getData();
    }

    /**
     * 获取数据
     */
    private void getData() {
        getMoneyOrders.sendPostRequest(Urls.GetMoneyOrders, new Response.Listener<String>() {
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
                        JSONArray datas = jsonObject.optJSONArray("orders");
                        if (datas != null) {
                            int length = datas.length();
                            JSONObject jsonObject1;
                            if (length > 0) {
                                for (int i = 0; i < length; i++) {
                                    jsonObject1 = datas.getJSONObject(i);
                                    WithdrawalMoneyListInfo withdrawalMoneyListInfo = new WithdrawalMoneyListInfo();
                                    withdrawalMoneyListInfo.setWithdrawaCode("编号:" + jsonObject1.getString("orderCode"));
                                    withdrawalMoneyListInfo.setAccount("支付宝账户:" + jsonObject1.getString("payAccount"));
                                    withdrawalMoneyListInfo.setCreatDate("创建日期:" + jsonObject1.getString("createTime"));
                                    withdrawalMoneyListInfo.setMoney("¥ " + jsonObject1.getString("withdrawDeposit"));
                                    withdrawalMoneyListInfo.setRealMoney("实际到帐金额:" + jsonObject1.getString("actualAmount") + "元");
                                    withdrawalMoneyListInfo.setTaxMoney("收税金额:" + jsonObject1.getString("tax") + "元");
                                    withdrawalMoneyListInfo.setType(jsonObject1.getString("orderState"));
                                    withdrawalMoneyListInfo.setFriends(jsonObject1.optString("friends"));
                                    withdrawalMoneyListInfo.setPayType(jsonObject1.getString("payType"));
                                    list.add(withdrawalMoneyListInfo);
                                }
                                if (length < 15) {
                                    listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                } else {
                                    listView.setMode(PullToRefreshBase.Mode.BOTH);
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                Tools.showToast(WithdrawalMoneyListActivity.this, "暂无提现明细");
                            }
                        }
                    } else {
                        Tools.showToast(WithdrawalMoneyListActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(WithdrawalMoneyListActivity.this, getResources().getString(R.string.network_error));
                }
                listView.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(WithdrawalMoneyListActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position--;
        if (list != null && !list.isEmpty()) {
            if ("2".equals(list.get(position).getType())) {
                Intent intent = new Intent(this, WithdrawalMoneyDetailActivity.class);
                intent.putExtra("data", list.get(position));
                startActivity(intent);
            }
        }
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Item item = null;
            if (convertView == null) {
                convertView = Tools.loadLayout(WithdrawalMoneyListActivity.this, R.layout.item_withdrawlmoneylist);
                item = new Item();
                item.code = (TextView) convertView.findViewById(R.id.code);
                item.tax = (TextView) convertView.findViewById(R.id.tax);
                item.realmoney = (TextView) convertView.findViewById(R.id.realmoney);
                item.account = (TextView) convertView.findViewById(R.id.account);
                item.creattime = (TextView) convertView.findViewById(R.id.creattime);
                item.tip = (TextView) convertView.findViewById(R.id.tip);
                item.money = (TextView) convertView.findViewById(R.id.money);
                item.type = (TextView) convertView.findViewById(R.id.type);
                convertView.setTag(item);
            } else {
                item = (Item) convertView.getTag();
            }
            if (!list.isEmpty()) {
                WithdrawalMoneyListInfo withdrawalMoneyListInfo = list.get(position);
                item.code.setText(withdrawalMoneyListInfo.getWithdrawaCode());
                String payType = withdrawalMoneyListInfo.getPayType();
                if ("0".equals(payType)) {//个人缴税显示
                    if (withdrawalMoneyListInfo.getTaxMoney() != null) {
                        item.tax.setText(withdrawalMoneyListInfo.getTaxMoney());
                        item.tax.setVisibility(View.VISIBLE);
                    } else {
                        item.tax.setVisibility(View.GONE);
                    }
                    if (withdrawalMoneyListInfo.getRealMoney() != null) {
                        item.realmoney.setVisibility(View.VISIBLE);
                        item.realmoney.setText(withdrawalMoneyListInfo.getRealMoney());
                    } else {
                        item.realmoney.setVisibility(View.GONE);
                    }
                } else {
                    item.tax.setVisibility(View.GONE);
                    item.realmoney.setVisibility(View.GONE);
                }
                item.account.setText(withdrawalMoneyListInfo.getAccount());
                item.money.setText(withdrawalMoneyListInfo.getMoney());
                if (withdrawalMoneyListInfo.getCreatDate() != null) {
                    item.creattime.setText(withdrawalMoneyListInfo.getCreatDate());
                    item.creattime.setVisibility(View.VISIBLE);
                } else {
                    item.creattime.setVisibility(View.GONE);
                }
                if ("0".equals(withdrawalMoneyListInfo.getType())) {//审核中
                    item.tip.setVisibility(View.GONE);
                    item.type.setText("审核中");
                } else if ("1".equals(withdrawalMoneyListInfo.getType())) {//已提现
                    item.tip.setVisibility(View.GONE);
                    item.type.setText("已提现");
                } else if ("2".equals(withdrawalMoneyListInfo.getType())) {//失效
                    item.tip.setVisibility(View.VISIBLE);
                    item.type.setText("提现失败");
                } else {
                    item.tip.setVisibility(View.VISIBLE);
                    item.type.setText("异常");
                }
            }
            return convertView;
        }

        class Item {
            TextView code, account, money, type, tax, realmoney, creattime, tip;
        }
    }
}
