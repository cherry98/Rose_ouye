package com.orange.oy.activity.mycorps_314;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.TelephonelistActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.MyUMShareUtils;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.UMShareDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.UMShareUtils;
import com.orange.oy.view.AppTitle;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 邀请加入战队==添加战队
 */
public class AddPlayersActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.addplayers_title);
        appTitle.settingName("添加战队");
        appTitle.showBack(this);
    }

    private String team_id;
    private NetworkConnection Sign;
    private String sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_players);
        team_id = getIntent().getStringExtra("team_id");
        initTitle();
        initNetwork();
        findViewById(R.id.addplayers_local).setOnClickListener(this);
        findViewById(R.id.addplayers_invite).setOnClickListener(this);
        sign();
    }

    private void sign() {
        Sign.sendPostRequest(Urls.Sign, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        sign = jsonObject.getString("msg");
                    } else {
                        Tools.showToast(AddPlayersActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(AddPlayersActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(AddPlayersActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private void initNetwork() {
        Sign = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("key", "team_id=" + team_id + "&usermobile=" + AppInfo.getName(AddPlayersActivity.this));
                return params;
            }
        };
    }


    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addplayers_local: {
                Intent intent = new Intent(this, TelephonelistActivity.class);
                intent.putExtra("team_id", team_id);
                startActivity(intent);
            }
            break;
            case R.id.addplayers_invite: {//分享
                UMShareDialog.showDialog(AddPlayersActivity.this, false, new UMShareDialog.UMShareListener() {
                    @Override
                    public void shareOnclick(int type) {
                        String webUrl = Urls.InviteToTeam + "?&team_id=" + team_id + "&usermobile=" + AppInfo.getName(AddPlayersActivity.this) + "&sign=" + sign;
                        MyUMShareUtils.umShare(AddPlayersActivity.this, type, webUrl);
                    }
                });
            }
            break;
        }
    }

}
