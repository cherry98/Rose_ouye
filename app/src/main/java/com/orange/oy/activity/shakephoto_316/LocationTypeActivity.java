package com.orange.oy.activity.shakephoto_316;

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
import com.orange.oy.adapter.mycorps_314.LocalAlbumAdapter;
import com.orange.oy.adapter.mycorps_314.LocationTypeAdapter;
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
 * 模糊位置->位置选择 V3.16
 */
public class LocationTypeActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, AdapterView.OnItemClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.locationtype_title);
        appTitle.settingName("场景类型");
        appTitle.showBack(this);
    }

    private void initNetwork() {
        placeInfo = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(LocationTypeActivity.this));
                params.put("token", Tools.getToken());
                return params;
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (placeInfo != null) {
            placeInfo.stop(Urls.PlaceInfo);
        }
    }

    private LocationTypeAdapter locationTypeAdapter;
    private ArrayList<MyPlaceInfo> list;
    private NetworkConnection placeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_type);
        list = new ArrayList<>();
        initTitle();
        initNetwork();
        PullToRefreshListView locationtype_listview = (PullToRefreshListView) findViewById(R.id.locationtype_listview);
        locationTypeAdapter = new LocationTypeAdapter(this, list);
        locationtype_listview.setAdapter(locationTypeAdapter);
        locationtype_listview.setOnItemClickListener(this);
        getData();
        locationtype_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getData();
            }
        });
    }

    private void getData() {
        placeInfo.sendPostRequest(Urls.PlaceInfo, new Response.Listener<String>() {
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
                                MyPlaceInfo myPlaceInfo = new MyPlaceInfo();
                                myPlaceInfo.setCap_id(object.getString("cap_id"));
                                myPlaceInfo.setPlace_name(object.getString("place_name"));
                                list.add(myPlaceInfo);
                            }
                            MyPlaceInfo myPlaceInfo = new MyPlaceInfo();
                            myPlaceInfo.setCap_id("-1");
                            myPlaceInfo.setPlace_name("不限");
                            list.add(0, myPlaceInfo);
                            if (locationTypeAdapter != null) {
                                locationTypeAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Tools.showToast(LocationTypeActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(LocationTypeActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(LocationTypeActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.putExtra("place_name", list.get(position - 1).getPlace_name());
        intent.putExtra("cap_id", list.get(position - 1).getCap_id());
        setResult(AppInfo.REQUEST_CODE_THEME, intent);
        baseFinish();
    }

    public class MyPlaceInfo {

        /**
         * cap_id : 场所类别id
         * place_name : 场所类别名称
         */

        private String cap_id;
        private String place_name;

        public String getCap_id() {
            return cap_id;
        }

        public void setCap_id(String cap_id) {
            this.cap_id = cap_id;
        }

        public String getPlace_name() {
            return place_name;
        }

        public void setPlace_name(String place_name) {
            this.place_name = place_name;
        }
    }
}
