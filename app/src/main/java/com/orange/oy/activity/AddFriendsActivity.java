package com.orange.oy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.adapter.AddFriendsAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.MyteamNewfdInfo;
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
 * 添加成员
 */
public class AddFriendsActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View
        .OnClickListener, AdapterView.OnItemClickListener {
    private AppTitle addfriends_title;

    private void initTitle() {
        addfriends_title = (AppTitle) findViewById(R.id.addfriends_title);
        addfriends_title.settingName(getResources().getString(R.string.addfriends));
        addfriends_title.showBack(this);
    }

    public void onBack() {
        baseFinish();
    }

    protected void onStop() {
        super.onStop();
        if (searchData != null) {
            searchData.stop(Urls.Search);
        }
    }

    private void initNetworkConnection() {
        searchData = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(AddFriendsActivity.this));
                params.put("keyword", keyword);
                return params;
            }
        };
        searchData.setIsShowDialog(true);
    }

    private EditText addfriends_search;
    private ArrayList<MyteamNewfdInfo> list;
    private ListView addfriends_listview;
    private AddFriendsAdapter addFriendsAdapter;
    private NetworkConnection searchData;
    private String keyword;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriends);
        initTitle();
        initNetworkConnection();
        addfriends_listview = (ListView) findViewById(R.id.addfriends_listview);
        addfriends_search = (EditText) findViewById(R.id.addfriends_search);
        addfriends_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    keyword = v.getText().toString().trim();
                    if (!TextUtils.isEmpty(keyword)) {
                        searchData();
                    } else {
                        Tools.showToast(AddFriendsActivity.this, "您还没输入搜索条件哦~");
                    }
                    return true;
                }
                return false;
            }
        });
        addfriends_listview.setOnItemClickListener(this);
        findViewById(R.id.addfriends_top).setOnClickListener(this);
    }

    private void searchData() {
        searchData.sendPostRequest(Urls.Search, new Response.Listener<String>() {
            public void onResponse(String s) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        JSONArray jsonArray = jsonObject.getJSONArray("datas");
                        if (list == null) {
                            list = new ArrayList<MyteamNewfdInfo>();
                        } else {
                            list.clear();
                        }
                        int length = jsonArray.length();
                        for (int i = 0; i < length; i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            MyteamNewfdInfo myteamNewfdInfo = new MyteamNewfdInfo();
                            myteamNewfdInfo.set_id(jsonObject.getString("id"));
                            myteamNewfdInfo.setId(jsonObject.getString("user_mobile"));
                            String name = jsonObject.getString("note");
                            if (!TextUtils.isEmpty(name) && !name.equals("null")) {
                                myteamNewfdInfo.setName(name);
                            } else {
                                myteamNewfdInfo.setName(jsonObject.getString("user_name"));
                            }
                            myteamNewfdInfo.setImg(jsonObject.getString("img_url"));
                            list.add(myteamNewfdInfo);
                        }
                        if (addFriendsAdapter == null) {
                            addFriendsAdapter = new AddFriendsAdapter(AddFriendsActivity.this, list);
                            addfriends_listview.setAdapter(addFriendsAdapter);
                        } else {
                            addFriendsAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Tools.showToast(AddFriendsActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(AddFriendsActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(AddFriendsActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addfriends_top: {
                startActivity(new Intent(this, TelephonelistActivity.class));
            }
            break;
        }
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, AddFriendsDetailActivity.class);
        intent.putExtra("data", list.get(position));
        startActivity(intent);
    }
}
