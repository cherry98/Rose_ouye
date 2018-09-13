package com.orange.oy.activity.alipay;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import com.orange.oy.info.BillListDetailInfo;
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
 * 历史账单页面 (V3.15)
 */
public class BillListDetailActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {
    private MyAdapter myAdapter;
    private ArrayList<BillListDetailInfo> list = new ArrayList<>();
    private PullToRefreshListView enchashment_listview;
    private NetworkConnection billList;
    private int pageNum;

    protected void onStop() {
        super.onStop();
        if (billList != null) {
            billList.stop(Urls.BillList);
        }
    }

    private void initNetworkConnection() {
        billList = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(BillListDetailActivity.this));
                params.put("pagenum", pageNum + "");
                params.put("pagesize", "15");
                return params;
            }
        };
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billlistdetail);
        pageNum = 1;
        initNetworkConnection();
        AppTitle enchashment_title = (AppTitle) findViewById(R.id.enchashment_title);
        enchashment_title.showBack(this);
        enchashment_title.settingName("历史账单");
        enchashment_listview = (PullToRefreshListView) findViewById(R.id.enchashment_listview);
        enchashment_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        enchashment_listview.setPullLabel(getResources().getString(R.string.listview_down));
        enchashment_listview.setRefreshingLabel(getResources().getString(R.string.listview_refush));
        enchashment_listview.setReleaseLabel(getResources().getString(R.string.listview_down2));
        enchashment_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                pageNum = 1;
                getData();
            }

            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                pageNum++;
                getData();
            }
        });
        myAdapter = new MyAdapter();
        enchashment_listview.setAdapter(myAdapter);
        getData();
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    public void onBack() {
        baseFinish();
    }

    private void getData() {
        billList.sendPostRequest(Urls.BillList, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (list == null) {
                            list = new ArrayList<BillListDetailInfo>();
                        } else if (pageNum == 1) {
                            list.clear();
                        }
                        jsonObject = jsonObject.optJSONObject("data");

                        ((TextView) findViewById(R.id.billlist_pay)).setText(jsonObject.getString("total_expenditures")); //总支出
                        ((TextView) findViewById(R.id.billlist_income)).setText(jsonObject.getString("total_income"));  //：总收入
                        ((TextView) findViewById(R.id.billlist_frozenCapital)).setText(jsonObject.getString("frozen_money"));   //：冻结金额


                        JSONArray jsonArray = jsonObject.optJSONArray("list");
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                BillListDetailInfo billListDetailInfo = new BillListDetailInfo();
                                billListDetailInfo.setMoney(object.getString("money"));
                                billListDetailInfo.setBill_type(object.getString("type"));
                                billListDetailInfo.setTitle(object.getString("title"));
                                billListDetailInfo.setExchange_time(object.getString("createDateStr"));

                                list.add(billListDetailInfo);
                            }
                            if (jsonArray.length() < 15) {
                                enchashment_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            } else {
                                enchashment_listview.setMode(PullToRefreshBase.Mode.BOTH);
                            }
                            if (myAdapter != null) {
                                myAdapter.notifyDataSetChanged();
                            }
                        }
                        enchashment_listview.onRefreshComplete();
                    } else {
                        Tools.showToast(BillListDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(BillListDetailActivity.this, getResources().getString(R.string.network_error));
                }
                enchashment_listview.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                enchashment_listview.onRefreshComplete();
                Tools.showToast(BillListDetailActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private class MyAdapter extends BaseAdapter {

        public int getCount() {
            return list.size();
        }

        public Object getItem(int position) {
            return list.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = Tools.loadLayout(BillListDetailActivity.this, R.layout.item_billlistdetail);
                viewHolder.item_project = (TextView) convertView.findViewById(R.id.item_project);
                viewHolder.item_time = (TextView) convertView.findViewById(R.id.item_time);
                viewHolder.item_money = (TextView) convertView.findViewById(R.id.item_money);
                viewHolder.item_state = (TextView) convertView.findViewById(R.id.item_state); //收入和支出
                //   viewHolder.item_img = (ImageView) convertView.findViewById(R.id.item_img);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            BillListDetailInfo billListDetailInfo = list.get(position);
            String type = billListDetailInfo.getBill_type();
            if (!TextUtils.isEmpty(type)) {
                viewHolder.item_state.setText("[" + type + "]");
                String str = "";
                if ("收入".equals(type)) {  //bill_type：账单类型（1：收入；2：支出）
                    str = "+";
                } else if ("支出".equals(type)) {
                    str = "-";
                } else if ("解冻".equals(type)) {
                    str = "+";
                } else if ("冻结".equals(type)) {
                    str = "-";
                }
                viewHolder.item_money.setText(str + " " + billListDetailInfo.getMoney());
            } else {
                viewHolder.item_state.setText("");
                viewHolder.item_money.setText("");
            }
            viewHolder.item_project.setText(billListDetailInfo.getTitle());
            viewHolder.item_time.setText(billListDetailInfo.getExchange_time());
            return convertView;
        }

        private class ViewHolder {
            TextView item_project, item_time, item_money, item_state;
            //ImageView item_img;
        }
    }
}
