package com.orange.oy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.MyteamNewfdInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 添加好友详情页
 */
public class AddFriendsDetailActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View
        .OnClickListener {
    private AppTitle addfriends_title;

    private void initTitle() {
        addfriends_title = (AppTitle) findViewById(R.id.addfriends_title);
        addfriends_title.settingName(getResources().getString(R.string.addfriendsdetail));
        addfriends_title.showBack(this);
    }

    public void onBack() {
        baseFinish();
    }

    protected void onStop() {
        super.onStop();
        if (sendData != null) {
            sendData.stop(Urls.Addtoteam);
        }
    }

    private void initNetworkConnection() {
        sendData = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(AddFriendsDetailActivity.this));
                params.put("teamuserid", myteamNewfdInfo.get_id());
                params.put("teamusermobile", myteamNewfdInfo.getId());
                String note = addfriends_note.getText().toString().trim();
                if (!TextUtils.isEmpty(note)) {
                    params.put("teamnote", note);
                }
                return params;
            }
        };
        sendData.setIsShowDialog(true);
    }

    private MyteamNewfdInfo myteamNewfdInfo;
    private TextView addfriends_name;
    private ImageView addfriends_img;
    private EditText addfriends_note;
    private ImageLoader imageLoader;
    private NetworkConnection sendData;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriendsdetail);
        initNetworkConnection();
        initTitle();
        Intent data = getIntent();
        if (data != null) {
            myteamNewfdInfo = (MyteamNewfdInfo) data.getSerializableExtra("data");
            if (myteamNewfdInfo != null) {
                imageLoader = new ImageLoader(this);
                addfriends_name = (TextView) findViewById(R.id.addfriends_name);
                addfriends_img = (ImageView) findViewById(R.id.addfriends_img);
                addfriends_note = (EditText) findViewById(R.id.addfriends_note);
                addfriends_name.setText(myteamNewfdInfo.getName());
                imageLoader.DisplayImage(myteamNewfdInfo.getImg(), addfriends_img, R.mipmap.my_img_de);
                findViewById(R.id.addfriends_sumbit).setOnClickListener(this);
            } else {
                baseFinish();
            }
        } else {
            baseFinish();
        }
    }

    private void sendData() {
        sendData.sendPostRequest(Urls.Addtoteam, new Response.Listener<String>() {
            public void onResponse(String s) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        Tools.showToast(AddFriendsDetailActivity.this, jsonObject.getString("msg"));
                        baseFinish();
                    } else {
                        Tools.showToast(AddFriendsDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(AddFriendsDetailActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(AddFriendsDetailActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, "正在添加...");
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addfriends_sumbit: {
                sendData();
            }
            break;
        }
    }
}
