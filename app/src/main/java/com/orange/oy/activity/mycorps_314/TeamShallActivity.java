package com.orange.oy.activity.mycorps_314;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * V3.14 战队认证
 */
public class TeamShallActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {
    private Context context;
    private AppTitle aboutus_title;
    private NetworkConnection teamAuth;
    private String team_id;
    private String psersonalAuth;
    private String enterprise_auth;
    private TextView company_identify_state, my_identify_state;

    private void initTitle() {
        team_id = getIntent().getStringExtra("team_id");
        aboutus_title = (AppTitle) findViewById(R.id.aboutus_title);
        aboutus_title.settingName("战队认证");
        aboutus_title.showBack(this);
    }

    private void initNetworkConnection() {
        teamAuth = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(TeamShallActivity.this));
                params.put("team_id", team_id);//战队id
                return params;
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_shall);
        company_identify_state = (TextView) findViewById(R.id.company_identify_state);
        my_identify_state = (TextView) findViewById(R.id.my_identify_state);
        findViewById(R.id.company_identify).setOnClickListener(this);
        findViewById(R.id.my_identify).setOnClickListener(this);
        initTitle();
        initNetworkConnection();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //企业认证
            case R.id.company_identify:

                if (!TextUtils.isEmpty(enterprise_auth)) {
                    if (enterprise_auth.equals("2")) {
                        Tools.showToast(context, "已经认证过了哦！");
                        return;
                    } else if (enterprise_auth.equals("1")) {
                        Tools.showToast(context, "正在审核中，请耐心等待！");
                        return;
                    } else {
                        if (enterprise_auth.equals("0") || enterprise_auth.equals("3")) {
                            Intent intent = new Intent(this, IdentityCompanyActivity.class);
                            intent.putExtra("teamId", team_id);
                            startActivity(intent);
                        }
                    }
                }
                break;
            case R.id.my_identify:

                if (!TextUtils.isEmpty(psersonalAuth)) {
                    if (psersonalAuth.equals("0")) {
                        Intent intent = new Intent(this, IdentityMyActivity.class);
                        intent.putExtra("teamId", team_id);
                        startActivity(intent);
                    } else if (psersonalAuth.equals("1")) {
                        Tools.showToast(context, "正在审核中，请耐心等待！");
                        return;
                    } else if (psersonalAuth.equals("2")) {
                        Tools.showToast(context, "已经认证过了哦！");
                        return;
                    }

                    if (!TextUtils.isEmpty(enterprise_auth)) {
                        if (enterprise_auth.equals("2")) {
                            Tools.showToast(context, "已经进行了企业认证哦,不能再进行个人认证！");
                            return;
                        } else if (enterprise_auth.equals("1")) {
                            Tools.showToast(context, "企业认证正在审核中,不能再进行个人认证！");
                            return;
                        }
                    }

                }

                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        teamAuth.sendPostRequest(Urls.TEAMAUTH, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {

                        JSONObject object = jsonObject.optJSONObject("data");
                        psersonalAuth = object.optString("psersonal_auth"); //是否进行了个人认证，1为是，0为否
                        enterprise_auth = object.optString("enterprise_auth"); //是否进行了企业认证，1为是，0为否
                        if (psersonalAuth.equals("0")) {
                            company_identify_state.setText("未认证");
                        } else if (psersonalAuth.equals("1")) {
                            my_identify_state.setText("审核中");
                        } else if (psersonalAuth.equals("2")) {
                            my_identify_state.setText("已认证");
                        }
                        if (enterprise_auth.equals("0")) {
                            company_identify_state.setText("未认证");
                        } else if (enterprise_auth.equals("1")) {
                            company_identify_state.setText("审核中");
                        } else if (enterprise_auth.equals("2")) {
                            company_identify_state.setText("已认证");
                        } else if (enterprise_auth.equals("3")) {
                            company_identify_state.setText("审核未通过");
                        }
                    } else {
                        Tools.showToast(TeamShallActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(TeamShallActivity.this, getResources().getString(R.string.network_error));
                }
            }

        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(TeamShallActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }
}
