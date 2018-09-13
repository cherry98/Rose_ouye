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
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.orange.oy.R.id.item_state;

/**
 * 偶米获得明细
 */
public class OuMiDetailActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.oumidetail_title);
        appTitle.settingName("偶米获得明细");
        appTitle.showBack(this);

    }

    private void initNetworkConnection() {
        omDetail = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(OuMiDetailActivity.this));
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ou_mi_detail);
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
        omDetail.sendPostRequest(Urls.OmDetail, new Response.Listener<String>() {
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

                            JSONArray jsonArray = jsonObject.getJSONArray("datas");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                OuMiInfo ouMiInfo = new OuMiInfo();
                                JSONObject object = jsonArray.getJSONObject(i);
                                ouMiInfo.setName(object.getString("name"));
                                ouMiInfo.setNum(object.getInt("num"));
                                ouMiInfo.setTime(object.getString("time"));
                                ouMiInfo.setType(object.getString("type"));
                                list.add(ouMiInfo);
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
                    } else {
                        Tools.showToast(OuMiDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(OuMiDetailActivity.this, getResources().getString(R.string.network_error));
                }
                oumidetail_listview.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                oumidetail_listview.onRefreshComplete();
                Tools.showToast(OuMiDetailActivity.this, getResources().getString(R.string.network_volleyerror));
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
                convertView = Tools.loadLayout(OuMiDetailActivity.this, R.layout.item_enchashmentdetail);
                viewHolder.item_project = (TextView) convertView.findViewById(R.id.item_project);
                viewHolder.item_money = (TextView) convertView.findViewById(R.id.item_money);
                viewHolder.item_time = (TextView) convertView.findViewById(R.id.item_time);
                viewHolder.item_share = (ImageView) convertView.findViewById(R.id.item_share);
                viewHolder.item_state = (TextView) convertView.findViewById(R.id.item_state);
                viewHolder.item_money2 = (TextView) convertView.findViewById(R.id.item_money2);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            OuMiInfo ouMiInfo = list.get(position);
            if ("0".equals(ouMiInfo.getType()) || "1".equals(ouMiInfo.getType())) {  // "type": 1, //值为1或0时为执行任务，否则为其他
                viewHolder.item_state.setText("[执行任务]");
            } else {
                viewHolder.item_state.setText("");
            }
            viewHolder.item_share.setVisibility(View.GONE);
            viewHolder.item_project.setText(ouMiInfo.getName());
            viewHolder.item_time.setText(ouMiInfo.getTime());
            viewHolder.item_money.setText("+ " + ouMiInfo.getNum() + "");
            viewHolder.item_money2.setText("偶米");
            return convertView;
        }

        class ViewHolder {
            TextView item_project, item_time, item_money, item_state, item_money2;
            ImageView item_img, item_share;
        }
    }

    class OuMiInfo {

        /**
         * name : 134xxxx3432用户执行任务
         * type : 1
         * <p>
         * /////类型（0为自己执行的，1为别人执行的）10 登录偶业 11 连续登录 12 完成任务奖励 13 分享任务 14 完善个人信息 15 绑定支付宝 16 身份认证
         * num : 10
         * time : 2017-12-20
         */

        private String name;
        private String type;
        private int num;
        private String time;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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
