package com.orange.oy.activity.shakephoto_318;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.MyListView;
import com.orange.oy.view.PullToRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 活动详情 V3.18
 */
public class NewActivityDetailActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, PullToRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, PullToRefreshLayout.OnRetreshComplentListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.activitydetail_title);
        appTitle.settingName("活动详情");
        appTitle.showBack(this);
    }

    private void initNetwork() {
        newActivityDetail = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(NewActivityDetailActivity.this));
                params.put("token", Tools.getToken());
                params.put("ai_id", ai_id);
                params.put("activity_status", activity_status);
                return params;
            }
        };
    }

    private NetworkConnection newActivityDetail;
    private String activity_status, ai_id;
    private PullToRefreshLayout refreshLayout;
    private ArrayList<ProvinceInfo> list;
    private ArrayList<ProvinceInfo> list2;
    private MyAdapter myAdapter;
    private MyAdapter2 myAdapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_detail);
        list = new ArrayList<>();
        list2 = new ArrayList<>();
        Intent data = getIntent();
        activity_status = data.getStringExtra("activity_status");
        ai_id = data.getStringExtra("ai_id");
        String template_img = data.getStringExtra("template_img");

        ImageView activitydetail_img = (ImageView) findViewById(R.id.activitydetail_img);
        ImageLoader imageLoader = new ImageLoader(NewActivityDetailActivity.this);
        imageLoader.DisplayImage(Urls.ImgIp + template_img, activitydetail_img, R.mipmap.ssfrw_button_ji);
        initTitle();
        initNetwork();
        refreshLayout = (PullToRefreshLayout) findViewById(R.id.refresh_view);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setCompleteListener(this);
        myAdapter = new MyAdapter();
        MyListView activitydetail_listview = (MyListView) findViewById(R.id.activitydetail_listview);
        activitydetail_listview.setAdapter(myAdapter);
        if ("2".equals(activity_status)) {//投放中
            findViewById(R.id.activitydetail_ly).setVisibility(View.GONE);
        } else {
            findViewById(R.id.activitydetail_ly).setVisibility(View.VISIBLE);
            MyListView activitydetail_listview2 = (MyListView) findViewById(R.id.activitydetail_listview2);
            myAdapter2 = new MyAdapter2();
            activitydetail_listview2.setAdapter(myAdapter2);
            activitydetail_listview2.setOnItemClickListener(this);
        }
        getData();
    }

    private void getData() {
        newActivityDetail.sendPostRequest(Urls.NewActivityDetail, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (refreshLayout != null) {
                            refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                            refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                        }
                        if (!list.isEmpty()) {
                            list.clear();
                        }
                        if (!list2.isEmpty()) {
                            list2.clear();
                        }
                        jsonObject = jsonObject.getJSONObject("data");
                        JSONObject activity_info = jsonObject.optJSONObject("activity_info");
                        if (activity_info != null) {
                            ((TextView) findViewById(R.id.activitydetail_theme)).setText(activity_info.getString("activity_name"));
                            ((TextView) findViewById(R.id.activitydetail_num)).setText("目标照片数量:" + activity_info.getString("target_num") + "人");
                            ((TextView) findViewById(R.id.activitydetail_time)).setText("活动起止日期:" + activity_info.getString("begin_date") +
                                    "~" + activity_info.getString("end_date"));
                        }
                        ((TextView) findViewById(R.id.activitydetail_joinnum)).setText(jsonObject.getString("join_num"));
                        ((TextView) findViewById(R.id.activitydetail_shownum)).setText(jsonObject.getString("ad_show_num"));
                        ((TextView) findViewById(R.id.activitydetail_clicknum)).setText(jsonObject.getString("ad_click_num"));
                        ((TextView) findViewById(R.id.activitydetail_totalpro)).setText(jsonObject.getString("total_province"));
                        JSONArray province_list = jsonObject.optJSONArray("province_list");
                        if (province_list != null) {
                            for (int i = 0; i < province_list.length(); i++) {
                                JSONObject object = province_list.getJSONObject(i);
                                ProvinceInfo provinceInfo = new ProvinceInfo();
                                provinceInfo.setNum(object.getString("num"));
                                provinceInfo.setProvince(object.getString("province"));
                                list.add(provinceInfo);
                            }
                            if (myAdapter != null) {
                                myAdapter.notifyDataSetChanged();
                            }
                        }
                        if ("3".equals(activity_status)) {//已结束有领奖信息
                            JSONArray prize_info = jsonObject.optJSONArray("prize_info");
                            if (prize_info != null && !"[]".equals(prize_info.toString())) {
                                findViewById(R.id.activitydetail_ly).setVisibility(View.VISIBLE);
                                for (int i = 0; i < prize_info.length(); i++) {
                                    ProvinceInfo provinceInfo = new ProvinceInfo();
                                    JSONObject object = prize_info.getJSONObject(i);
                                    provinceInfo.setPur_id(object.getString("pur_id"));
                                    provinceInfo.setUser_mobile(object.getString("user_mobile"));
                                    provinceInfo.setUser_name(object.getString("user_name"));
                                    provinceInfo.setPrize_type(object.getString("prize_type"));
                                    list2.add(provinceInfo);
                                }
                                if (myAdapter2 != null) {
                                    myAdapter2.notifyDataSetChanged();
                                }
                            } else {
                                findViewById(R.id.activitydetail_ly).setVisibility(View.GONE);
                            }
                        }
                    } else {
                        Tools.showToast(NewActivityDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(NewActivityDetailActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(NewActivityDetailActivity.this, getResources().getString(R.string.network_batch_error));
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
        ProvinceInfo provinceInfo = list2.get(position);
        Intent intent = new Intent(this, WinningUserActivity.class);
        intent.putExtra("pur_id", provinceInfo.getPur_id());
        intent.putExtra("ai_id", ai_id);
        startActivity(intent);
    }

    @Override
    public void OnComplete() {

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
            Item item;
            if (convertView == null) {
                item = new Item();
                convertView = Tools.loadLayout(NewActivityDetailActivity.this, R.layout.item_province);
                item.itemprovince_name = (TextView) convertView.findViewById(R.id.itemprovince_name);
                item.itemprovince_num = (TextView) convertView.findViewById(R.id.itemprovince_num);
                convertView.setTag(item);
            } else {
                item = (Item) convertView.getTag();
            }
            ProvinceInfo provinceInfo = list.get(position);
            item.itemprovince_name.setText(provinceInfo.getProvince());
            item.itemprovince_num.setText(provinceInfo.getNum());
            return convertView;
        }

        class Item {
            private TextView itemprovince_name, itemprovince_num;
        }
    }

    class ProvinceInfo {

        /**
         * province : 省份
         * num : 数量
         */

        private String province;
        private String num;
        /**
         * pur_id : 领奖id
         * user_name : 用户昵称
         * user_mobile : 用户账号
         * prize_type : 奖项类型，返回1或2或3
         */

        private String pur_id;
        private String user_name;
        private String user_mobile;
        private String prize_type;


        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }

        public String getPur_id() {
            return pur_id;
        }

        public void setPur_id(String pur_id) {
            this.pur_id = pur_id;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getUser_mobile() {
            return user_mobile;
        }

        public void setUser_mobile(String user_mobile) {
            this.user_mobile = user_mobile;
        }

        public String getPrize_type() {
            return prize_type;
        }

        public void setPrize_type(String prize_type) {
            this.prize_type = prize_type;
        }
    }

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

    class MyAdapter2 extends BaseAdapter {
        @Override
        public int getCount() {
            return list2.size();
        }

        @Override
        public Object getItem(int position) {
            return list2.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Item item;
            if (convertView == null) {
                item = new Item();
                convertView = Tools.loadLayout(NewActivityDetailActivity.this, R.layout.item_activityprize);
                item.itemprize_nickname = (TextView) convertView.findViewById(R.id.itemprize_nickname);
                item.itemprize_rank = (TextView) convertView.findViewById(R.id.itemprize_rank);
                convertView.setTag(item);
            } else {
                item = (Item) convertView.getTag();
            }
            ProvinceInfo provinceInfo = list2.get(position);
            item.itemprize_nickname.setText(provinceInfo.getUser_name());
            String prize = provinceInfo.getPrize_type();
            if ("1".equals(prize)) {
                item.itemprize_rank.setText("一等奖");
            } else if ("2".equals(prize)) {
                item.itemprize_rank.setText("二等奖");
            } else if ("3".equals(prize)) {
                item.itemprize_rank.setText("三等奖");
            }
            return convertView;
        }

        class Item {
            private TextView itemprize_nickname, itemprize_rank;
        }
    }
}
