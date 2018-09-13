package com.orange.oy.activity.shakephoto_320;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
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
 * V3.20账户明细
 */
public class AccountActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.oumidetail_title);
        appTitle.settingName("账户明细");
        appTitle.showBack(this);

    }

    private void initNetworkConnection() {
        merchantAccountDetail = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(AccountActivity.this));
                params.put("merchant_id", merchant_id);
                // params.put("page", page + "");
                return params;
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (merchantAccountDetail != null) {
            merchantAccountDetail.stop(Urls.MerchantAccountDetail);
        }
    }

    private PullToRefreshListView oumidetail_listview;
    private MyAdapter myAdapter;
    private NetworkConnection merchantAccountDetail;
    private int page;
    private ArrayList<AccountDetailInfo> list;
    private String merchant_id;
    private NetworkView lin_Nodata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ou_mi_detail);
        merchant_id = getIntent().getStringExtra("merchant_id");
        list = new ArrayList<>();
        initTitle();
        initNetworkConnection();
        lin_Nodata = (NetworkView) findViewById(R.id.lin_Nodata);
        oumidetail_listview = (PullToRefreshListView) findViewById(R.id.oumidetail_listview);
        myAdapter = new MyAdapter();
        oumidetail_listview.setAdapter(myAdapter);
        oumidetail_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
        merchantAccountDetail.sendPostRequest(Urls.MerchantAccountDetail, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (list == null) {
                            list = new ArrayList<>();
                        } else {
                            if (page == 1) {
                                list.clear();
                            }
                        }
                        if (!jsonObject.isNull("data")) {
                            lin_Nodata.setVisibility(View.GONE);
                            oumidetail_listview.setVisibility(View.VISIBLE);
                            jsonObject = jsonObject.getJSONObject("data");
                            JSONArray jsonArray = jsonObject.getJSONArray("list");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                AccountDetailInfo detailInfo = new AccountDetailInfo();
                                JSONObject object = jsonArray.getJSONObject(i);
                                detailInfo.setMoney(object.getString("money"));
                                detailInfo.setTitle(object.getString("title"));
                                detailInfo.setCreateDateStr(object.getString("createDateStr"));
                                detailInfo.setType(object.getString("type"));
                                list.add(detailInfo);
                            }
                            oumidetail_listview.onRefreshComplete();
                            if (jsonArray.length() < 15) {
                                oumidetail_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            } else {
                                oumidetail_listview.setMode(PullToRefreshBase.Mode.BOTH);
                            }
                            if (myAdapter != null) {
                                myAdapter.notifyDataSetChanged();
                            }
                        }
                        if (list.isEmpty()) {
                            if (lin_Nodata.getVisibility() == View.GONE) {
                                lin_Nodata.setVisibility(View.VISIBLE);
                                lin_Nodata.NoSearch("还没有明细哦，点击刷新");
                                lin_Nodata.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        page = 1;
                                        getData();
                                        lin_Nodata.NoSearch("正在刷新...");
                                        lin_Nodata.setOnClickListener(null);
                                    }
                                });
                                oumidetail_listview.setVisibility(View.GONE);
                            }
                        } else {
                            if (oumidetail_listview.getVisibility() == View.GONE) {
                                lin_Nodata.setVisibility(View.GONE);
                                oumidetail_listview.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        lin_Nodata.setVisibility(View.GONE);
                        Tools.showToast(AccountActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(AccountActivity.this, getResources().getString(R.string.network_error));
                }
                oumidetail_listview.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                oumidetail_listview.onRefreshComplete();
                lin_Nodata.setVisibility(View.VISIBLE);
                lin_Nodata.NoNetwork(getResources().getString(R.string.network_fail) + "点击重试");
                lin_Nodata.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        lin_Nodata.NoNetwork("正在重试...");
                        lin_Nodata.setOnClickListener(null);
                        page = 1;
                        getData();
                    }
                });
                oumidetail_listview.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    class MyAdapter extends BaseAdapter {
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
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = Tools.loadLayout(AccountActivity.this, R.layout.item_enchashmentdetail);
                viewHolder.item_project = (TextView) convertView.findViewById(R.id.item_project);
                viewHolder.item_money = (TextView) convertView.findViewById(R.id.item_money);
                viewHolder.item_time = (TextView) convertView.findViewById(R.id.item_time);
                viewHolder.item_share = (ImageView) convertView.findViewById(R.id.item_share);
                viewHolder.item_state = (TextView) convertView.findViewById(R.id.item_state);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            AccountDetailInfo detailInfo = list.get(position);
            //      "type":"类型，1为充值，2为支出", 冻结 解冻
            if (!Tools.isEmpty(detailInfo.getType())) {
                if ("充值".equals(detailInfo.getType())) {
                    viewHolder.item_state.setText("[充值]");
                    viewHolder.item_money.setText("+ " + detailInfo.getMoney() + "");
                } else if ("支出".equals(detailInfo.getType())) {
                    viewHolder.item_state.setText("[支出]");
                    viewHolder.item_money.setText("- " + detailInfo.getMoney() + "");
                } else if ("冻结".equals(detailInfo.getType())) {
                    viewHolder.item_state.setText("[冻结]");
                    viewHolder.item_money.setText("- " + detailInfo.getMoney() + "");
                } else if ("解冻".equals(detailInfo.getType())) {
                    viewHolder.item_state.setText("[解冻]");
                    viewHolder.item_money.setText("- " + detailInfo.getMoney() + "");
                }
            }
            viewHolder.item_share.setVisibility(View.GONE);
            viewHolder.item_project.setText(detailInfo.getTitle());
            viewHolder.item_time.setText(detailInfo.getCreateDateStr());

            return convertView;
        }

        class ViewHolder {
            TextView item_project, item_time, item_money, item_state;
            ImageView item_img, item_share;
        }
    }

    class AccountDetailInfo {
        /**
         * balance : 999916.5
         * billType : 消费
         * title : 平台服务费82.50元
         * accountPk : 3e9687fa661f488ca3b484c8b254204c
         * orderNo : 201808291840121360011777672588
         * remark : 平台服务费82.50元
         * money : 82.5
         * state : 交易成功
         * billInfo : 16e38b86f5c54c2a861bc58477a5373a
         * type : 内部调账
         * createDate : 1535539212000
         * createDateStr : 2018-08-29 18:40:12
         */

        private double balance;
        private String billType;
        private String title;
        private String accountPk;
        private String orderNo;
        private String remark;
        private String money;
        private String state;
        private String billInfo;
        private String type;
        private String createDate;
        private String createDateStr;

        public double getBalance() {
            return balance;
        }

        public void setBalance(double balance) {
            this.balance = balance;
        }

        public String getBillType() {
            return billType;
        }

        public void setBillType(String billType) {
            this.billType = billType;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAccountPk() {
            return accountPk;
        }

        public void setAccountPk(String accountPk) {
            this.accountPk = accountPk;
        }

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getBillInfo() {
            return billInfo;
        }

        public void setBillInfo(String billInfo) {
            this.billInfo = billInfo;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getMoney() {
            return money;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getCreateDateStr() {
            return createDateStr;
        }

        public void setCreateDateStr(String createDateStr) {
            this.createDateStr = createDateStr;
        }

    }
}
