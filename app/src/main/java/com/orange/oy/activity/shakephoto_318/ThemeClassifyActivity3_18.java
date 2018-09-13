package com.orange.oy.activity.shakephoto_318;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.adapter.SelectTextAdapter3_17;
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
 * V3.18  甩图相册------- 活动分类
 */
public class ThemeClassifyActivity3_18 extends BaseActivity implements AppTitle.OnBackClickForAppTitle, AdapterView.OnItemClickListener {
    // ThemeClassifyActivity
    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.themeclassify_title);
        appTitle.settingName("活动分类");
        appTitle.showBack(this);
    }

    private void initNetwork() {
        themeInfoByPhoto = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(ThemeClassifyActivity3_18.this));
                params.put("token", Tools.getToken());
                params.put("type", "1");    // App传1，小程序传2【必传】
                params.put("photo_list", photo_list); // 照片信息【必传】
                params.put("local_photo", local_photo);
                Tools.d(params.toString());
                return params;
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (themeInfoByPhoto != null) {
            themeInfoByPhoto.stop(Urls.ThemeInfoByPhoto);
        }
    }

    private SelectTextAdapter3_17 selectTextAdapter;
    private NetworkConnection themeInfoByPhoto;
    private ArrayList<MyThemeInfo> list;
    private PullToRefreshListView themeclassify_listview;
    private String photo_list, local_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_classify);
        photo_list = getIntent().getStringExtra("photo_list");
        local_photo = getIntent().getStringExtra("local_photo");
        initTitle();
        list = new ArrayList<>();
        initNetwork();
        themeclassify_listview = (PullToRefreshListView) findViewById(R.id.themeclassify_listview);
        selectTextAdapter = new SelectTextAdapter3_17(this, list);
        themeclassify_listview.setAdapter(selectTextAdapter);
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
        themeInfoByPhoto.sendPostRequest(Urls.ThemeInfoByPhoto, new Response.Listener<String>() {
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
                        JSONArray jsonArray = jsonObject.optJSONArray("list");
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.optJSONObject(i);
                                MyThemeInfo myThemeInfo = new MyThemeInfo();
                                myThemeInfo.setCat_id(object.getString("cat_id"));
                                myThemeInfo.setTheme_name(object.getString("theme_name"));
                                list.add(myThemeInfo);
                            }
                            if (selectTextAdapter != null) {
                                selectTextAdapter.notifyDataSetChanged();
                            }
                        }
                        themeclassify_listview.onRefreshComplete();
                    } else {
                        Tools.showToast(ThemeClassifyActivity3_18.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(ThemeClassifyActivity3_18.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(ThemeClassifyActivity3_18.this, getResources().getString(R.string.network_volleyerror));
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
        intent.putExtra("cat_id", myThemeInfo.getCat_id());
        intent.putExtra("theme_name", myThemeInfo.getTheme_name());
        setResult(AppInfo.REQUEST_CODE_UPLOAD_PICTURES, intent);
        baseFinish();
    }

    public class MyThemeInfo {

        /**
         * cat_id : 主题分类id
         * theme_name : 主题分类名称
         */

        private String cat_id;
        private String theme_name;

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
    }
}
