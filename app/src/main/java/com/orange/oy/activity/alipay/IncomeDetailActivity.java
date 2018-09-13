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
 * 触发点：我的-->我的奖金-->总金额
 * 总金额收入明细
 */
public class IncomeDetailActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {
    private void initNetWork() {
        incomeDetails = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(IncomeDetailActivity.this));
                params.put("token", Tools.getToken());
                params.put("page", page + "");
                return params;
            }
        };
    }

    private NetworkConnection incomeDetails;
    private int page;
    private ArrayList<IncomeDetailInfo> list;
    private MyAdapter myAdapter;
    private PullToRefreshListView incomedetail_listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_detail);
        list = new ArrayList<>();
        AppTitle appTitle = (AppTitle) findViewById(R.id.incomedetail_title);
        appTitle.settingName("收入明细");
        appTitle.showBack(this);
        initNetWork();
        incomedetail_listview = (PullToRefreshListView) findViewById(R.id.incomedetail_listview);
        myAdapter = new MyAdapter();
        incomedetail_listview.setAdapter(myAdapter);
        incomedetail_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
        incomeDetails.sendPostRequest(Urls.IncomeDetails, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (list == null) {
                            list = new ArrayList<IncomeDetailInfo>();
                        } else {
                            if (page == 1) {
                                list.clear();
                            }
                        }
                        JSONArray jsonArray = jsonObject.optJSONArray("datas");
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                IncomeDetailInfo incomeDetailInfo = new IncomeDetailInfo();
                                incomeDetailInfo.setCreate_time(object.getString("create_time"));
                                incomeDetailInfo.setMoney(object.getString("money"));
                                incomeDetailInfo.setProject_name(object.getString("project_name"));
                                list.add(incomeDetailInfo);
                            }
                            if (jsonArray.length() < 15) {
                                incomedetail_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            } else {
                                incomedetail_listview.setMode(PullToRefreshBase.Mode.BOTH);
                            }
                        }
                        if (myAdapter != null) {
                            myAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Tools.showToast(IncomeDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(IncomeDetailActivity.this, getResources().getString(R.string.network_error));
                }
                incomedetail_listview.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(IncomeDetailActivity.this, getResources().getString(R.string.network_volleyerror));
                incomedetail_listview.onRefreshComplete();
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
                convertView = Tools.loadLayout(IncomeDetailActivity.this, R.layout.item_incomedetail);
                viewHolder.itemincome_name = (TextView) convertView.findViewById(R.id.itemincome_name);
                viewHolder.itemincome_time = (TextView) convertView.findViewById(R.id.itemincome_time);
                viewHolder.itemincome_money = (TextView) convertView.findViewById(R.id.itemincome_money);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            IncomeDetailInfo incomeDetailInfo = list.get(position);
            viewHolder.itemincome_time.setText(incomeDetailInfo.getCreate_time());
            viewHolder.itemincome_name.setText(incomeDetailInfo.getProject_name());
            String withdrawalmoney = incomeDetailInfo.getMoney();
            if (withdrawalmoney != null) {
                if (TextUtils.isEmpty(withdrawalmoney)) {
                    withdrawalmoney = "-";
                } else {
                    double d = Tools.StringToDouble(withdrawalmoney);
                    if (d - (int) d > 0) {
                        withdrawalmoney = String.valueOf(d);
                    } else {
                        withdrawalmoney = String.valueOf((int) d);
                    }
                }
                viewHolder.itemincome_money.setText(String.format(getResources().getString(R.string.account_money),
                        "+ ¥" + withdrawalmoney));
            }
            return convertView;
        }

        class ViewHolder {
            private TextView itemincome_name, itemincome_time, itemincome_money;
        }
    }

    class IncomeDetailInfo {

        /**
         * project_name : 0411全程录音无网点测试项目
         * create_time : 2018-04-17 19:43:21
         * money : 400.00
         */

        private String project_name;
        private String create_time;
        private String money;

        public String getProject_name() {
            return project_name;
        }

        public void setProject_name(String project_name) {
            this.project_name = project_name;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }
    }
}
