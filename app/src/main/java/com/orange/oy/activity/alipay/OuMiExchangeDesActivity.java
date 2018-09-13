package com.orange.oy.activity.alipay;

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

import static com.orange.oy.R.id.teammember_listview;

/**
 * 偶米兑换明细
 */
public class OuMiExchangeDesActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.oumidetail_title);
        appTitle.settingName("兑换偶米明细");
        appTitle.showBack(this);

    }

    private void initNetworkConnection() {
        omDetail = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(OuMiExchangeDesActivity.this));
                params.put("page", page + "");
                return params;
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (omDetail != null) {
            omDetail.stop(Urls.OmDetail);
        }
    }

    private PullToRefreshListView oumidetail_listview;
    private MyAdapter myAdapter;
    private NetworkConnection omDetail;
    private int page;
    private ArrayList<OuMiInfo> list;
    private NetworkView lin_Nodata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ou_mi_detail);
        lin_Nodata = (NetworkView) findViewById(R.id.lin_Nodata);
        list = new ArrayList<>();
        initTitle();
        initNetworkConnection();
        oumidetail_listview = (PullToRefreshListView) findViewById(R.id.oumidetail_listview);
        myAdapter = new MyAdapter();
        oumidetail_listview.setAdapter(myAdapter);
        oumidetail_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
    }

    private void refreshData() {
        page = 1;
        getData();
    }

    private void getData() {
        omDetail.sendPostRequest(Urls.OmExchangeInfo, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (list == null) {
                            list = new ArrayList<OuMiInfo>();
                        } else {
                            if (page == 1) {
                                list.clear();
                            }
                        }
                        if (!jsonObject.isNull("datas")) {
                            JSONArray jsonArray = jsonObject.optJSONArray("datas");
                            if (jsonArray != null) {
                                oumidetail_listview.setVisibility(View.VISIBLE);
                                lin_Nodata.setVisibility(View.GONE);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    OuMiInfo ouMiInfo = new OuMiInfo();
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    ouMiInfo.setMoney(object.getString("money"));
                                    ouMiInfo.setNum(object.getInt("num"));
                                    ouMiInfo.setTime(object.getString("time"));
                                    ouMiInfo.setType(object.getString("type"));
                                    list.add(ouMiInfo);
                                }

                                if (jsonArray.length() < 15) {
                                    oumidetail_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                } else {
                                    oumidetail_listview.setMode(PullToRefreshBase.Mode.BOTH);
                                }
                                if (myAdapter != null) {
                                    myAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            oumidetail_listview.setVisibility(View.GONE);
                            lin_Nodata.setVisibility(View.VISIBLE);
                            lin_Nodata.NoSearch("没有兑换明细哦，赶快去兑换吧~");
                        }

                        oumidetail_listview.onRefreshComplete();


                    } else {
                        Tools.showToast(OuMiExchangeDesActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(OuMiExchangeDesActivity.this, getResources().getString(R.string.network_error));
                }
                oumidetail_listview.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                oumidetail_listview.onRefreshComplete();
                oumidetail_listview.setVisibility(View.GONE);
                lin_Nodata.NoNetwork();
                lin_Nodata.setVisibility(View.VISIBLE);
                Tools.showToast(OuMiExchangeDesActivity.this, getResources().getString(R.string.network_volleyerror));
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
                convertView = Tools.loadLayout(OuMiExchangeDesActivity.this, R.layout.item_oumiexchange2);
                viewHolder.item_project = (TextView) convertView.findViewById(R.id.item_project);
                viewHolder.itemomexchange_money = (TextView) convertView.findViewById(R.id.itemomexchange_money);
                viewHolder.item_time = (TextView) convertView.findViewById(R.id.itemomexchange_time);
//                viewHolder.item_share = (ImageView) convertView.findViewById(R.id.item_share);
                viewHolder.itemomexchange_money2 = (TextView) convertView.findViewById(R.id.itemomexchange_money2);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            OuMiInfo ouMiInfo = list.get(position);

            viewHolder.itemomexchange_money2.setText("+ "+ouMiInfo.getMoney());
//            viewHolder.item_share.setVisibility(View.GONE);
            viewHolder.item_time.setText(ouMiInfo.getTime());
            viewHolder.itemomexchange_money.setText(ouMiInfo.getNum() + "");
            return convertView;
        }

        class ViewHolder {
            TextView item_project, item_time, itemomexchange_money, itemomexchange_money2;
            ImageView item_share;
        }
    }

    class OuMiInfo {

        /**
         * name : 134xxxx3432用户执行任务
         * type : 1
         * num : 10
         * time : 2017-12-20
         */

        private String money;
        private String type;
        private int num;
        private String time;

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}
