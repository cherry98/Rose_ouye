package com.orange.oy.activity.alipay;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
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
import com.orange.oy.dialog.CustomProgressDialog;
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

import static com.orange.oy.R.id.oumidetail_listview;

/**
 * 偶米兑换页面
 */
public class OuMiExchangeActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {

    private void initTitle() {
        AppTitle oumiexchange_title = (AppTitle) findViewById(R.id.oumiexchange_title);
        oumiexchange_title.settingName("偶米兑换");
        oumiexchange_title.showBack(this);
    }

    private void initNetworkConnection() {
        omExchangeInfo = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(OuMiExchangeActivity.this));
                params.put("page", page + "");
                return params;
            }
        };
        omExchange = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(OuMiExchangeActivity.this));
                params.put("num", num + "");
                return params;
            }
        };
        omExchange.setIsShowDialog(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (omExchange != null) {
            omExchange.stop(Urls.OmExchange);
        }
        if (omExchangeInfo != null) {
            omExchangeInfo.stop(Urls.OmExchangeInfo);
        }
    }

    private PullToRefreshListView oumiexchange_listview;
    private MyAdapter myAdapter;
    private NetworkConnection omExchangeInfo, omExchange;
    private int page;
    private ArrayList<OmExchangeInfo> list;
    private TextView oumiexchange_exchange;
    private int num;
    private EditText oumiexchange_totalom;
    private int totalom;
    private NetworkView lin_Nodata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ou_mi_exchange);
        list = new ArrayList<>();
        initTitle();
        initNetworkConnection();
        lin_Nodata = (NetworkView) findViewById(R.id.lin_Nodata);
        oumiexchange_listview = (PullToRefreshListView) findViewById(R.id.oumiexchange_listview);
        oumiexchange_exchange = (TextView) findViewById(R.id.oumiexchange_exchange);
        oumiexchange_totalom = (EditText) findViewById(R.id.oumiexchange_totalom);
        myAdapter = new MyAdapter();
        oumiexchange_listview.setAdapter(myAdapter);
        oumiexchange_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
        oumiexchange_exchange.setOnClickListener(this);
    }

    private void refreshData() {
        page = 1;
        getData();
    }

    private void getData() {
        omExchangeInfo.sendPostRequest(Urls.OmExchangeInfo, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (list == null) {
                            list = new ArrayList<OmExchangeInfo>();
                        } else {
                            if (page == 1) {
                                list.clear();
                            }
                        }
                        totalom = jsonObject.getInt("totalom");
                        oumiexchange_totalom.setHint("总余额：" + totalom + "偶米");
                        oumiexchange_totalom.setHintTextColor(Color.parseColor("#FFA0A0A0"));
                        if (!jsonObject.isNull("datas")) {
                            JSONArray jsonArray = jsonObject.optJSONArray("datas");
                            if (null != jsonArray && jsonArray.length() > 0) {
                                lin_Nodata.setVisibility(View.GONE);
                                oumiexchange_listview.setVisibility(View.VISIBLE);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    OmExchangeInfo omExchangeInfo = new OmExchangeInfo();
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    omExchangeInfo.setNum(object.getInt("num"));
                                    omExchangeInfo.setTime(object.getString("time"));
                                    omExchangeInfo.setType(object.getString("type"));
                                    list.add(omExchangeInfo);
                                }
                                if (jsonArray.length() < 15) {
                                    oumiexchange_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                } else {
                                    oumiexchange_listview.setMode(PullToRefreshBase.Mode.BOTH);
                                }
                                if (myAdapter != null) {
                                    myAdapter.notifyDataSetChanged();
                                }
                            } else {
                                lin_Nodata.setVisibility(View.VISIBLE);
                                oumiexchange_listview.setVisibility(View.GONE);
                                lin_Nodata.SettingMSG(R.mipmap.grrw_image, "没有兑换记录哦~");

                            }
                        }
                        oumiexchange_listview.onRefreshComplete();

                    } else {
                        Tools.showToast(OuMiExchangeActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(OuMiExchangeActivity.this, getResources().getString(R.string.network_error));
                }
                oumiexchange_listview.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                oumiexchange_listview.onRefreshComplete();
                Tools.showToast(OuMiExchangeActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.oumiexchange_exchange) {
            String count = oumiexchange_totalom.getText().toString();
            if ("".equals(count)) {
                Tools.showToast(OuMiExchangeActivity.this, "请输入您要兑换的偶米数~");
                return;
            }
            num = Tools.StringToInt(count);
            num = ((int) (Tools.StringToInt(count) / 100)) * 100;
            if (num <= 0) {
                Tools.showToast(OuMiExchangeActivity.this, "请输入大于100的偶米数哦~");
                return;
            }
            if (num > totalom) {
                Tools.showToast(OuMiExchangeActivity.this, "您没有足够的偶米可以兑换哦~");
                return;
            }
            omExchange();
        }
    }

    private void omExchange() {//取整兑换
        omExchange.sendPostRequest(Urls.OmExchange, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    oumiexchange_totalom.setText("");
                    Tools.showToast(OuMiExchangeActivity.this, jsonObject.getString("msg"));
                } catch (JSONException e) {
                    Tools.showToast(OuMiExchangeActivity.this, getResources().getString(R.string.network_error));
                }
                refreshData();
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(OuMiExchangeActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        }, false);
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
                convertView = Tools.loadLayout(OuMiExchangeActivity.this, R.layout.item_oumiexchange);
                viewHolder.itemomexchange_time = (TextView) convertView.findViewById(R.id.itemomexchange_time);
                viewHolder.itemomexchange_type = (TextView) convertView.findViewById(R.id.itemomexchange_type);
                viewHolder.itemomexchange_money = (TextView) convertView.findViewById(R.id.itemomexchange_money);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            OmExchangeInfo omExchangeInfo = list.get(position);
            viewHolder.itemomexchange_time.setText(omExchangeInfo.getTime());
            String type = "";//0为未提现，1为处理中，2为已提现  "type": 0,
            if ("0".equals(omExchangeInfo.getType())) {
                type = "未提现";
            } else if ("1".equals(omExchangeInfo.getType())) {
                type = "处理中";
            } else if ("2".equals(omExchangeInfo.getType())) {
                type = "已提现";
            }
            viewHolder.itemomexchange_type.setText(type);
            viewHolder.itemomexchange_money.setText(omExchangeInfo.getNum() + "");
            return convertView;
        }

        class ViewHolder {
            private TextView itemomexchange_type, itemomexchange_time, itemomexchange_money;
        }
    }

    class OmExchangeInfo {

        /**
         * num : 100
         * type : 0
         * time : 2017-11-28
         */

        private int num;
        private String type;
        private String time;

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}
