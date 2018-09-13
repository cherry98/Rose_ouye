package com.orange.oy.activity.shakephoto_318;

import android.content.Intent;
import android.os.Bundle;
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
import com.orange.oy.activity.shakephoto_316.LargeImagePageActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.info.DetailsInfo;
import com.orange.oy.info.LargeImageInfo;
import com.orange.oy.info.LargeImagePageInfo;
import com.orange.oy.info.SeconderDesInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * V3.18 赞助人详情
 */
public class SeconderDesActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, AdapterView.OnItemClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.seconderdes_title);
        appTitle.settingName("赞助人详情");
        appTitle.showBack(this);
    }

    private void initNetworkConnection() {
        sponsorInfo = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(SeconderDesActivity.this));
                params.put("ai_id", ai_id); //活动id
                params.put("page", page + "");
                return params;
            }
        };
    }

    private String ai_id;
    private int page;
    private MyAdapter seconderDesAdapter;
    private PullToRefreshListView seconderdes_listview;
    private TextView tv_theme;
    private ArrayList<SeconderDesInfo> list = new ArrayList<>();
    private NetworkConnection sponsorInfo;
    private ImageLoader imageLoader;
    private ArrayList<LargeImageInfo> largeImagePageInfos;
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seconder_des);
        ai_id = getIntent().getStringExtra("ai_id");
        initTitle();
        initNetworkConnection();
        imageLoader = new ImageLoader(this);
        seconderdes_listview = (PullToRefreshListView) findViewById(R.id.seconderdes_listview);
        tv_theme = (TextView) findViewById(R.id.tv_theme);
        seconderDesAdapter = new MyAdapter();
        seconderdes_listview.setAdapter(seconderDesAdapter);
        seconderdes_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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

    @Override
    protected void onStop() {
        super.onStop();
        if (sponsorInfo != null) {
            sponsorInfo.stop(Urls.SponsorInfo);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private String activity_name;

    private void getData() {
        sponsorInfo.sendPostRequest(Urls.SponsorInfo, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (list == null) {
                            list = new ArrayList<SeconderDesInfo>();
                        } else {
                            if (page == 1) {
                                list.clear();
                            }
                        }
                        if (!jsonObject.isNull("data")) {
                            jsonObject = jsonObject.getJSONObject("data");
                            activity_name = jsonObject.optJSONObject("activity_info").getString("activity_name");
                            if (!Tools.isEmpty(activity_name)) {
                                tv_theme.setText(activity_name);
                            }
                            JSONArray jsonArray = jsonObject.getJSONArray("sponsor_list");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                SeconderDesInfo seconderDesInfo = new SeconderDesInfo();
                                JSONObject object = jsonArray.getJSONObject(i);
                                seconderDesInfo.setAd_links(object.getString("ad_links")); // 广告链接
                                seconderDesInfo.setAd_url(object.getString("ad_url"));// 广告图oss路径
                                seconderDesInfo.setComment_num(object.getString("comment_num")); // 评论数
                                seconderDesInfo.setPraise_num(object.getString("praise_num")); // 点赞数
                                seconderDesInfo.setShare_num(object.getString("share_num")); // 分享数
                                seconderDesInfo.setSponsor_name(object.getString("sponsor_name")); //赞助商名称
                                seconderDesInfo.setSponsorship_fee(object.getString("sponsorship_fee"));// 赞助总金额
                                seconderDesInfo.setPraise_state(object.getString("praise_state"));
                                seconderDesInfo.setSai_id(object.getString("sai_id"));
                                seconderDesInfo.setComment_state(object.getString("comment_state"));
                                list.add(seconderDesInfo);
                            }
                            seconderdes_listview.onRefreshComplete();
                            if (jsonArray.length() < 15) {
                                seconderdes_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            } else {
                                seconderdes_listview.setMode(PullToRefreshBase.Mode.BOTH);
                            }
                            if (seconderDesAdapter != null) {
                                seconderDesAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Tools.showToast(SeconderDesActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(SeconderDesActivity.this, getResources().getString(R.string.network_error));
                }
                seconderdes_listview.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                seconderdes_listview.onRefreshComplete();
                Tools.showToast(SeconderDesActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onBack() {
        baseFinish();
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

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = Tools.loadLayout(SeconderDesActivity.this, R.layout.item_seconder);
                viewHolder.tv_names = (TextView) convertView.findViewById(R.id.tv_names);
                viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.tv_moneys = (TextView) convertView.findViewById(R.id.tv_moneys);
                viewHolder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
                viewHolder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            SeconderDesInfo seconderDesInfo = list.get(position);
            if (!Tools.isEmpty(seconderDesInfo.getSponsor_name())) {
                viewHolder.tv_name.setText(seconderDesInfo.getSponsor_name());
            }
            if (!Tools.isEmpty(seconderDesInfo.getSponsorship_fee())) {
                viewHolder.tv_money.setText(seconderDesInfo.getSponsorship_fee());
            }
            String url = seconderDesInfo.getAd_url();
            if (!(url.startsWith("http://") || url.startsWith("https://"))) {
                if (!new File(url).exists()) {
                    url = Urls.Endpoint3 + url;
                }
            }
            imageLoader.DisplayImage(url, viewHolder.iv_pic);
            viewHolder.iv_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //查看广告大图

                    if (list != null && !list.isEmpty()) {
                        if (largeImagePageInfos == null) {
                            largeImagePageInfos = new ArrayList<>();
                            for (SeconderDesInfo desInfo : list) {
                                LargeImageInfo largeImageInfo = new LargeImageInfo();
                                largeImageInfo.setFile_url(desInfo.getAd_url());
                                largeImageInfo.setAd_links(desInfo.getAd_links());
                                largeImageInfo.setAi_id(ai_id);
                                largeImageInfo.setComment_num(desInfo.getComment_num());
                                largeImageInfo.setShare_num(desInfo.getShare_num());
                                largeImageInfo.setPraise_num(desInfo.getPraise_num());
                                largeImageInfo.setSai_id(desInfo.getSai_id());
                                largeImageInfo.setPraise_state(desInfo.getPraise_state());
                                largeImageInfo.setIs_advertisement("1"); ////是否是广告图，1为是，0为否
                                largeImageInfo.setComment_state(desInfo.getComment_state());
                                largeImagePageInfos.add(largeImageInfo);
                            }
                        }
                        Intent intent = new Intent(SeconderDesActivity.this, LargeImageActivity.class);
                        intent.putExtra("list", largeImagePageInfos);
                        intent.putExtra("position", position);
                        startActivity(intent);
                    }

                }
            });
            return convertView;
        }

        private class ViewHolder {
            TextView tv_names, tv_name, tv_moneys, tv_money;
            ImageView iv_pic;
        }
    }
}
