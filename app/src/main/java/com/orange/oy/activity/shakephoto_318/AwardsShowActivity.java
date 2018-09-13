package com.orange.oy.activity.shakephoto_318;

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
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
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

import static com.orange.oy.network.Urls.PrizeInfo;


/**
 * V3.18  活动奖项展示信息
 */
public class AwardsShowActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, AdapterView.OnItemClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.seconderdes_title);
        appTitle.settingName("活动奖项展示");
        appTitle.showBack(this);
    }

    private void initNetworkConnection() {
        prizeInfo = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(AwardsShowActivity.this));
                params.put("ai_id", ai_id); //活动id
                return params;
            }
        };
    }

    private String ai_id;
    private int page;
    private MyAdapter seconderDesAdapter;
    private PullToRefreshListView seconderdes_listview;
    private TextView tv_theme, tv_sponsor;
    private ArrayList<PrizeInfo> list = new ArrayList<>();
    private NetworkConnection prizeInfo;
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_award_show);
        ai_id = getIntent().getStringExtra("ai_id");
        initTitle();
        initNetworkConnection();
        imageLoader = new ImageLoader(this);
        seconderdes_listview = (PullToRefreshListView) findViewById(R.id.seconderdes_listview);
        seconderdes_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        tv_theme = (TextView) findViewById(R.id.tv_theme);
        tv_sponsor = (TextView) findViewById(R.id.tv_sponsor);
        seconderDesAdapter = new MyAdapter();
        seconderdes_listview.setAdapter(seconderDesAdapter);
        seconderdes_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
        getData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (prizeInfo != null) {
            prizeInfo.stop(PrizeInfo);
        }
    }

    private String activity_name, sponsor_name;

    private void getData() {
        prizeInfo.sendPostRequest(PrizeInfo, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (list == null) {
                            list = new ArrayList<PrizeInfo>();
                        } else {
                            if (page == 1) {
                                list.clear();
                            }
                        }
                        if (!jsonObject.isNull("data")) {
                            jsonObject = jsonObject.getJSONObject("data");
                            activity_name = jsonObject.optJSONObject("activity_info").getString("activity_name");// 活动名称
                            sponsor_name = jsonObject.optJSONObject("activity_info").getString("sponsor_name");//赞助商
                            if (!Tools.isEmpty(activity_name)) {
                                tv_theme.setText("活动主题: " + activity_name);
                            }
                            if (!Tools.isEmpty(sponsor_name)) {
                                tv_sponsor.setText("赞助商：" + sponsor_name);
                            }
                            JSONArray jsonArray = jsonObject.getJSONArray("prize_info");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                PrizeInfo prizeInfo = new PrizeInfo();
                                JSONObject object = jsonArray.getJSONObject(i);
                                prizeInfo.setPrize_image_url(object.getString("prize_image_url")); //奖品图片地址
                                prizeInfo.setPrize_name(object.getString("prize_name"));//奖品名
                                prizeInfo.setPrize_num(object.getString("prize_num"));
                                prizeInfo.setPrize_type(object.getString("prize_type")); // 奖项类型 (1:一等奖，2:二等奖，3:三等奖)
                                list.add(prizeInfo);
                            }
                            seconderdes_listview.onRefreshComplete();
                            if (seconderDesAdapter != null) {
                                seconderDesAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Tools.showToast(AwardsShowActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(AwardsShowActivity.this, getResources().getString(R.string.network_error));
                }
                seconderdes_listview.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                seconderdes_listview.onRefreshComplete();
                Tools.showToast(AwardsShowActivity.this, getResources().getString(R.string.network_volleyerror));
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

    class MyAdapter extends BaseAdapter {

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
                convertView = Tools.loadLayout(AwardsShowActivity.this, R.layout.item_awards_show);
                viewHolder.tv_text = (TextView) convertView.findViewById(R.id.tv_text);
                viewHolder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
                viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            PrizeInfo seconderDesInfo = list.get(position);
            if (!Tools.isEmpty(seconderDesInfo.getPrize_name())) {
                viewHolder.tv_name.setText(seconderDesInfo.getPrize_name());
            }

            if (!Tools.isEmpty(seconderDesInfo.getPrize_type()) && !Tools.isEmpty(seconderDesInfo.getPrize_num())) {
                //奖项类型(1:一等奖，2:二等奖，3:三等奖)
                if ("1".equals(seconderDesInfo.getPrize_type())) {
                    viewHolder.tv_text.setText("一等奖 /" + seconderDesInfo.getPrize_num() + "个");
                } else if ("2".equals(seconderDesInfo.getPrize_type())) {
                    viewHolder.tv_text.setText("二等奖/" + seconderDesInfo.getPrize_num() + "个");
                } else if ("3".equals(seconderDesInfo.getPrize_type())) {
                    viewHolder.tv_text.setText("三等奖/" + seconderDesInfo.getPrize_num() + "个");
                }
            }

            String url = seconderDesInfo.getPrize_image_url();
            if (!(url.startsWith("http://") || url.startsWith("https://"))) {
                if (!new File(url).exists()) {
                    url = Urls.Endpoint3 + url;
                }
            }
            imageLoader.DisplayImage(url, viewHolder.iv_pic);

            return convertView;
        }

        private class ViewHolder {
            TextView tv_text, tv_name;
            ImageView iv_pic;
        }
    }

    class PrizeInfo {

        /**
         * prize_type : 奖项类型 (1:一等奖，2:二等奖，3:三等奖)
         * prize_num : 奖品数量
         * prize_name : 奖品名
         * prize_image_url  : 奖品图片地址
         */

        private String prize_type;
        private String prize_num;
        private String prize_name;
        private String prize_image_url;

        public String getPrize_type() {
            return prize_type;
        }

        public void setPrize_type(String prize_type) {
            this.prize_type = prize_type;
        }

        public String getPrize_num() {
            return prize_num;
        }

        public void setPrize_num(String prize_num) {
            this.prize_num = prize_num;
        }

        public String getPrize_name() {
            return prize_name;
        }

        public void setPrize_name(String prize_name) {
            this.prize_name = prize_name;
        }

        public String getPrize_image_url() {
            return prize_image_url;
        }

        public void setPrize_image_url(String prize_image_url) {
            this.prize_image_url = prize_image_url;
        }
    }
}
