package com.orange.oy.activity.shakephoto_320;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.activity.createtask_317.TaskMouldActivity;
import com.orange.oy.activity.shakephoto_316.CollectPhotoActivity;
import com.orange.oy.activity.shakephoto_318.ThemeDetailActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.info.MySponsorshipInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/8/22.
 * 我的赞助 投放中/已完成
 */

public class MySponsorshipActivity extends BaseActivity {
    private void initNetwork() {
        SponsorshipActivityList = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(MySponsorshipActivity.this));
                params.put("activity_status", activity_status);//2：投放中；3：已结束
                params.put("page", page + "");
                return params;
            }
        };
    }

    private PullToRefreshListView mysponsp_listview;
    private ArrayList<MySponsorshipInfo> mySponsorshipInfos = new ArrayList<>();
    private MyAdapter myAdapter;
    private String activity_status;
    private NetworkConnection SponsorshipActivityList;
    private int page = 1;
    private ImageLoader imageLoader;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mysponsorship);
        imageLoader = new ImageLoader(this);
        activity_status = getIntent().getStringExtra("activity_status");//活动状态2：投放中；3：已结束
        initNetwork();
        AppTitle mysponsp_title = (AppTitle) findViewById(R.id.mysponsp_title);
        if ("2".equals(activity_status)) {
            mysponsp_title.settingName("投放中");
        } else {
            mysponsp_title.settingName("已完成");
        }
        mysponsp_title.showBack(new AppTitle.OnBackClickForAppTitle() {
            public void onBack() {
                baseFinish();
            }
        });
        mysponsp_listview = (PullToRefreshListView) findViewById(R.id.mysponsp_listview);
        mysponsp_listview.setMode(PullToRefreshBase.Mode.BOTH);
        mysponsp_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                getData();
            }

            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                getData();
            }
        });
        View view = new View(this);
        view.setMinimumHeight(Tools.dipToPx(this, 10));
        mysponsp_listview.getRefreshableView().addFooterView(view);
        mysponsp_listview.setOnItemClickListener(onItemClickListener);
        myAdapter = new MyAdapter();
        mysponsp_listview.setAdapter(myAdapter);
        page = 1;
        getData();
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MySponsorshipInfo mySponsorshipInfo = mySponsorshipInfos.get(position - 1);
            Intent intent = new Intent(MySponsorshipActivity.this, ThemeDetailActivity.class);
            intent.putExtra("ai_id", mySponsorshipInfo.getAi_id());
            intent.putExtra("acname", mySponsorshipInfo.getActivity_name());
            startActivity(intent);
        }
    };

    private void getData() {
        SponsorshipActivityList.sendPostRequest(Urls.SponsorshipActivityList, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        jsonObject = jsonObject.optJSONObject("data");
                        if (jsonObject != null) {
                            JSONArray list = jsonObject.optJSONArray("list");
                            if (list != null) {
                                if (page == 1) {
                                    mySponsorshipInfos.clear();
                                    myAdapter.notifyDataSetChanged();
                                }
                                int length = list.length();
                                for (int i = 0; i < length; i++) {
                                    JSONObject temp = list.getJSONObject(i);
                                    MySponsorshipInfo mySponsorshipInfo = new MySponsorshipInfo();
                                    mySponsorshipInfo.setActivity_name(temp.optString("activity_name"));
                                    mySponsorshipInfo.setAi_id(temp.optString("ai_id"));
                                    mySponsorshipInfo.setTarget_num(temp.optString("target_num"));
                                    mySponsorshipInfo.setGet_num(temp.optString("get_num"));
                                    mySponsorshipInfo.setBegin_date(temp.optString("begin_date"));
                                    mySponsorshipInfo.setEnd_date(temp.optString("end_date"));
                                    mySponsorshipInfo.setActivity_status(temp.optString("activity_status"));
                                    mySponsorshipInfo.setActivity_type(temp.optString("activity_type"));
                                    mySponsorshipInfo.setProject_id(temp.optString("project_id"));
                                    mySponsorshipInfo.setTemplate_img(temp.optString("template_img"));
                                    mySponsorshipInfo.setSponsorship_money(temp.optString("sponsorship_money"));
                                    mySponsorshipInfo.setAd_show_num(temp.optString("ad_show_num"));
                                    mySponsorshipInfo.setAd_click_num(temp.optString("ad_click_num"));
                                    mySponsorshipInfos.add(mySponsorshipInfo);
                                }
                                myAdapter.notifyDataSetChanged();
                                mysponsp_listview.onRefreshComplete();
                                if (length < 15) {
                                    mysponsp_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                } else {
                                    mysponsp_listview.setMode(PullToRefreshBase.Mode.BOTH);
                                }
                            }
                        }
                    } else {
                        Tools.showToast(MySponsorshipActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(MySponsorshipActivity.this, getResources().getString(R.string.network_error));
                }
                mysponsp_listview.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                mysponsp_listview.onRefreshComplete();
                Tools.showToast(MySponsorshipActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private class MyAdapter extends BaseAdapter {

        public int getCount() {
            return mySponsorshipInfos.size();
        }

        public Object getItem(int position) {
            return mySponsorshipInfos.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHold viewHold;
            if (convertView == null) {
                viewHold = new ViewHold();
                if ("2".equals(activity_status)) {//活动状态2：投放中；3：已结束
                    convertView = Tools.loadLayout(MySponsorshipActivity.this, R.layout.item_mysponsorship1);
                } else {
                    convertView = Tools.loadLayout(MySponsorshipActivity.this, R.layout.item_mysponsorship2);
                }
                viewHold.itemmysponsp_img = (ImageView) convertView.findViewById(R.id.itemmysponsp_img);
                viewHold.itemmysponsp_title = (TextView) convertView.findViewById(R.id.itemmysponsp_title);
                viewHold.itemmysponsp_item1 = (TextView) convertView.findViewById(R.id.itemmysponsp_item1);
                viewHold.itemmysponsp_item2 = (TextView) convertView.findViewById(R.id.itemmysponsp_item2);
                viewHold.itemmysponsp_item3 = (TextView) convertView.findViewById(R.id.itemmysponsp_item3);
                viewHold.itemmysponsp_b2 = convertView.findViewById(R.id.itemmysponsp_b2);
                viewHold.itemmysponsp_num1 = (TextView) convertView.findViewById(R.id.itemmysponsp_num1);
                viewHold.itemmysponsp_num2 = (TextView) convertView.findViewById(R.id.itemmysponsp_num2);
                viewHold.itemmysponsp_num3 = (TextView) convertView.findViewById(R.id.itemmysponsp_num3);
                convertView.setTag(viewHold);
            } else {
                viewHold = (ViewHold) convertView.getTag();
            }
            MySponsorshipInfo mySponsorshipInfo = mySponsorshipInfos.get(position);
            viewHold.itemmysponsp_title.setText(mySponsorshipInfo.getActivity_name());
            viewHold.itemmysponsp_item1.setText(mySponsorshipInfo.getBegin_date() + "~" + mySponsorshipInfo.getEnd_date());
            if ("2".equals(activity_status)) {//活动状态2：投放中；3：已结束
                viewHold.itemmysponsp_item2.setText("目标参与人数:" + mySponsorshipInfo.getTarget_num() + "人");
                viewHold.itemmysponsp_item3.setText("赞助总金额:" + mySponsorshipInfo.getSponsorship_money() + "元");
            } else {
                viewHold.itemmysponsp_item2.setText("赞助总金额:" + mySponsorshipInfo.getSponsorship_money() + "人");
                viewHold.itemmysponsp_num1.setText(mySponsorshipInfo.getGet_num());
                viewHold.itemmysponsp_num2.setText(mySponsorshipInfo.getAd_show_num());
                viewHold.itemmysponsp_num3.setText(mySponsorshipInfo.getAd_click_num());
                viewHold.itemmysponsp_b2.setTag(position);
                viewHold.itemmysponsp_b2.setOnClickListener(onClickListener);
            }
//            if ("1".equals(mySponsorshipInfo.getActivity_type())) {
            if ("null".equals(mySponsorshipInfo.getTemplate_img()) || TextUtils.isEmpty(mySponsorshipInfo.getTemplate_img())) {
                viewHold.itemmysponsp_img.setImageResource(R.mipmap.ssfrw_button_ji);
            } else {
                imageLoader.DisplayImage(Urls.ImgIp + mySponsorshipInfo.getTemplate_img(), viewHold.itemmysponsp_img,
                        R.mipmap.ssfrw_button_ji);
            }
//            } else {
//                if ("null".equals(mySponsorshipInfo.getTemplate_img()) || TextUtils.isEmpty(mySponsorshipInfo.getTemplate_img())) {
//                    viewHold.itemmysponsp_img.setImageResource(R.mipmap.round_pai);
//                } else {
//                    imageLoader.DisplayImage(Urls.ImgIp + mySponsorshipInfo.getTemplate_img(), viewHold.itemmysponsp_img,
//                            R.mipmap.round_pai);
//                }
//            }
            return convertView;
        }

        class ViewHold {
            ImageView itemmysponsp_img;
            TextView itemmysponsp_title, itemmysponsp_item1, itemmysponsp_item2, itemmysponsp_item3;
            View itemmysponsp_b2;
            TextView itemmysponsp_num1, itemmysponsp_num2, itemmysponsp_num3;
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            int position = (int) v.getTag();
            MySponsorshipInfo mySponsorshipInfo = mySponsorshipInfos.get(position);
//            if ("1".equals(mySponsorshipInfo.getActivity_type())) {
            Intent intent = new Intent(MySponsorshipActivity.this, CollectPhotoActivity.class);
            intent.putExtra("ai_id", mySponsorshipInfo.getAi_id());
            intent.putExtra("which_page", "2");
            intent.putExtra("putagain", true);
            startActivity(intent);
//            } else {
//                Intent intent = new Intent(MySponsorshipActivity.this, TaskMouldActivity.class);
//                intent.putExtra("project_id", mySponsorshipInfo.getProject_id());
//                intent.putExtra("which_page", "1");
//                startActivity(intent);
//            }
        }
    };
}
