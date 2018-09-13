package com.orange.oy.activity.mycorps_314;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.ScreenManager;
import com.orange.oy.base.Tools;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 个人战队解散
 */
public class PersonalDissolveActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {

    private NetworkConnection dissolveTeam;
    private String team_id;
    private NetworkConnection psersonalAuth;

    protected void onStop() {
        super.onStop();
        if (dissolveTeam != null) {
            dissolveTeam.stop(Urls.DissolveTeam);
        }
        if (psersonalAuth != null) {
            psersonalAuth.stop(Urls.PSERSONALAUTH);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_dissolve);
        dissolveTeam = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(PersonalDissolveActivity.this));
                params.put("token", Tools.getToken());
                params.put("team_id", team_id);
                return params;
            }
        };
        psersonalAuth = new NetworkConnection(PersonalDissolveActivity.this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(PersonalDissolveActivity.this));
                params.put("token", Tools.getToken());
                return params;
            }
        };
        team_id = getIntent().getStringExtra("team_id");
        AppTitle appTitle = (AppTitle) findViewById(R.id.perdiss_title);
        appTitle.settingName("解散战队");
        appTitle.showBack(this);
        findViewById(R.id.perdiss_dissolve).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dissolveTeam();
            }
        });
        getpsersonalAuth();
    }

    private void getpsersonalAuth() {
        psersonalAuth.sendPostRequest(Urls.PSERSONALAUTH, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (!TextUtils.isEmpty(AppInfo.getKey(PersonalDissolveActivity.this))) {
                        if (jsonObject.getInt("code") == 200) {
                            JSONObject object = jsonObject.optJSONObject("data");
                            ((TextView) findViewById(R.id.tv_CashDeposit)).setText(object.getString("money") + "元");
                        }
                    }
                } catch (JSONException e) {
                    Tools.showToast(PersonalDissolveActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(PersonalDissolveActivity.this, getResources().getString(R.string.network_error));
            }
        }, null);
    }

    private void dissolveTeam() {
        dissolveTeam.sendPostRequest(Urls.DissolveTeam, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Tools.showToast(PersonalDissolveActivity.this, "战队已解散");
                        ScreenManager.getScreenManager().finishActivity(SetCorpsActivity.class);
                        ScreenManager.getScreenManager().finishActivity(TeamInformationActivity.class);
                        baseFinish();
                    } else {
                        Tools.showToast(PersonalDissolveActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(PersonalDissolveActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(PersonalDissolveActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    public void onBack() {
        baseFinish();
    }
}
