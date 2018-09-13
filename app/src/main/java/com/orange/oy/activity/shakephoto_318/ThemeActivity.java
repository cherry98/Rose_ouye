package com.orange.oy.activity.shakephoto_318;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import java.util.List;
import java.util.Map;

/**
 * V3.18  甩图相册------- 活动主题
 */
public class ThemeActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, AdapterView.OnItemClickListener {
    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.themeclassify_title);
        appTitle.settingName("活动主题");
        appTitle.showBack(this);
    }

    private void initNetwork() {
        activityListByThemeAndPhoto = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(ThemeActivity.this));
                params.put("token", Tools.getToken());
                params.put("type", "1");    // App传1，小程序传2【必传】
                params.put("photo_list", photo_list); // 照片信息【必传】
                params.put("cat_id", cat_id);
                params.put("local_photo", local_photo);
                return params;
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (activityListByThemeAndPhoto != null) {
            activityListByThemeAndPhoto.stop(Urls.ActivityListByThemeAndPhoto);
        }
    }

    private MyAdapter myAdapter;
    private NetworkConnection activityListByThemeAndPhoto;
    private ArrayList<MyThemeInfo> list;
    private PullToRefreshListView themeclassify_listview;
    private String photo_list, cat_id, local_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_classify);
        photo_list = getIntent().getStringExtra("photo_list");
        local_photo = getIntent().getStringExtra("local_photo");
        cat_id = getIntent().getStringExtra("cat_id");
        initTitle();
        list = new ArrayList<>();
        initNetwork();
        themeclassify_listview = (PullToRefreshListView) findViewById(R.id.themeclassify_listview);
        myAdapter = new MyAdapter();
        themeclassify_listview.setAdapter(myAdapter);
        themeclassify_listview.setOnItemClickListener(this);
        themeclassify_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getData();
            }
        });
        getData();
    }

    private void getData() {
        activityListByThemeAndPhoto.sendPostRequest(Urls.ActivityListByThemeAndPhoto, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (!list.isEmpty()) {
                            list.clear();
                        }
                        jsonObject = jsonObject.optJSONObject("data");
                        JSONArray jsonArray = jsonObject.optJSONArray("activity_list");
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.optJSONObject(i);
                                MyThemeInfo myThemeInfo = new MyThemeInfo();
                                  /*     "ai_id":"活动id",
                                        "activity_name":"活动主题名称",
                                        "location_type":"投放类型（1：精准位置；2：模糊位置）",
                                        "place_name":"场所类型名称",
                                        "province":"省份",
                                        "city":"城市",
                                        "county":"区域",
                                        "address":"地址",
                                        "key_cencent":["标签1", "标签2"]*/
                                myThemeInfo.setAi_id(object.getString("ai_id"));
                                myThemeInfo.setActivity_name(object.getString("activity_name"));
                                myThemeInfo.setKey_cencent(object.getString("key_cencent"));
                                list.add(myThemeInfo);
                            }
                            if (myAdapter != null) {
                                myAdapter.notifyDataSetChanged();
                            }
                        }
                        themeclassify_listview.onRefreshComplete();
                    } else {
                        Tools.showToast(ThemeActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(ThemeActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(ThemeActivity.this, getResources().getString(R.string.network_volleyerror));
                themeclassify_listview.onRefreshComplete();
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MyThemeInfo myThemeInfo = list.get(position - 1);
        Intent intent = new Intent();
        intent.putExtra("ai_id", myThemeInfo.getAi_id());
        intent.putExtra("activity_name", myThemeInfo.getActivity_name());
        intent.putExtra("key_cencent", myThemeInfo.getKey_cencent());
        setResult(AppInfo.REQUEST_CODE_UPLOAD_PICTURES, intent);
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
                convertView = Tools.loadLayout(ThemeActivity.this, R.layout.itemselect_text);
                viewHolder.tv_text = (TextView) convertView.findViewById(R.id.text);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tv_text.setText(list.get(position).getActivity_name());
            return convertView;
        }

        private class ViewHolder {
            TextView tv_text;
        }
    }

    public class MyThemeInfo {
        /**
         * ai_id : 活动id
         * cat_id : 主题分类id
         * theme_name : 主题分类名称
         * activity_name : 活动主题名称
         * location_type : 投放类型（1：精准位置；2：模糊位置）
         * place_name : 场所类型名称
         * province : 省份
         * city : 城市
         * county : 区域
         * address : 地址
         * key_cencent : ["标签1","标签2"]
         */

        private String ai_id;
        private String cat_id;
        private String theme_name;
        private String activity_name;
        private String location_type;
        private String place_name;
        private String province;
        private String city;
        private String county;
        private String address;
        private String key_cencent;

        public String getAi_id() {
            return ai_id;
        }

        public void setAi_id(String ai_id) {
            this.ai_id = ai_id;
        }

        public String getCat_id() {
            return cat_id;
        }

        public void setCat_id(String cat_id) {
            this.cat_id = cat_id;
        }

        public String getTheme_name() {
            return theme_name;
        }

        public void setTheme_name(String theme_name) {
            this.theme_name = theme_name;
        }

        public String getActivity_name() {
            return activity_name;
        }

        public void setActivity_name(String activity_name) {
            this.activity_name = activity_name;
        }

        public String getLocation_type() {
            return location_type;
        }

        public void setLocation_type(String location_type) {
            this.location_type = location_type;
        }

        public String getPlace_name() {
            return place_name;
        }

        public void setPlace_name(String place_name) {
            this.place_name = place_name;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCounty() {
            return county;
        }

        public void setCounty(String county) {
            this.county = county;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getKey_cencent() {
            return key_cencent;
        }

        public void setKey_cencent(String key_cencent) {
            key_cencent = key_cencent.replaceAll("\\\"", "").replaceAll("\\[", "").replaceAll("\\]", "");
            this.key_cencent = key_cencent;
        }
    }
}
