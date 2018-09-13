package com.orange.oy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.adapter.NewFriendsAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.AppDBHelper;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.MyteamNewfdInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 新的好友
 */
public class NewFriendsActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, AdapterView
        .OnItemClickListener, View.OnClickListener {
    private AppTitle newfriends_title;

    private void initTitle() {
        newfriends_title = (AppTitle) findViewById(R.id.newfriends_title);
        newfriends_title.settingName(getResources().getString(R.string.newfriends));
        newfriends_title.showBack(this);
    }

    public void onBack() {
        baseFinish();
    }

    protected void onStop() {
        super.onStop();
        if (sendData != null) {
            sendData.stop(Urls.Accepttoteam);
        }
    }

    private void initNetworkConnection() {
        sendData = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(NewFriendsActivity.this));
                params.put("inviteuserid", inviteuserid);
                params.put("inviteusermobile", inviteusermobile);
                return params;
            }
        };
        sendData.setIsShowDialog(true);
    }

    private ListView newfriends_listview;
    private NewFriendsAdapter newFriendsAdapter;
    private ArrayList<MyteamNewfdInfo> list;
    private AppDBHelper appDBHelper;
    private NetworkConnection sendData;
    private String inviteuserid, inviteusermobile;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newfriends);
        initNetworkConnection();
        initTitle();
        appDBHelper = new AppDBHelper(this);
        newfriends_listview = (ListView) findViewById(R.id.newfriends_listview);
        list = appDBHelper.getAllNewfriends(AppInfo.getName(this));
        newFriendsAdapter = new NewFriendsAdapter(this, list);
        newfriends_listview.setAdapter(newFriendsAdapter);
        newfriends_listview.setOnItemClickListener(this);
        findViewById(R.id.newfriends_top).setOnClickListener(this);
    }

    private void sendData() {
        sendData.sendPostRequest(Urls.Accepttoteam, new Response.Listener<String>() {
            public void onResponse(String s) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        Tools.showToast(NewFriendsActivity.this, jsonObject.getString("msg"));
                        appDBHelper.upState(AppInfo.getName(NewFriendsActivity.this), inviteusermobile);
                        list = appDBHelper.getAllNewfriends(AppInfo.getName(NewFriendsActivity.this));
                        newFriendsAdapter.upList(list);
                        MyTeamActivity.isGetdata = true;
                    } else if (code == 2) {
                        Tools.showToast(NewFriendsActivity.this, jsonObject.getString("msg"));
                        appDBHelper.upState(AppInfo.getName(NewFriendsActivity.this), inviteusermobile);
                        list = appDBHelper.getAllNewfriends(AppInfo.getName(NewFriendsActivity.this));
                        newFriendsAdapter.upList(list);
                        MyTeamActivity.isGetdata = true;
                    } else {
                        Tools.showToast(NewFriendsActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(NewFriendsActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(NewFriendsActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (newFriendsAdapter.isSelect()) {
            newFriendsAdapter.clearSelect();
            MyteamNewfdInfo temp = list.get(position);
            inviteuserid = temp.get_id();
            inviteusermobile = temp.getId();
            if (!TextUtils.isEmpty(inviteuserid) && !inviteuserid.equals("null") && !TextUtils.isEmpty
                    (inviteusermobile) && !inviteusermobile.equals("null")) {
                sendData();
            } else {
                Tools.showToast(this, "数据异常");
            }
        } else {
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.newfriends_top: {
                startActivity(new Intent(this, TelephonelistActivity.class));
            }
            break;
        }
    }
}
