package com.orange.oy.activity.mycorps_314;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

public class IssueNoticeActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.issuenotice_title);
        appTitle.settingName("发布公告");
        appTitle.showBack(this);
    }

    private void initNetwork() {
        publishNotice = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(IssueNoticeActivity.this));
                params.put("token", Tools.getToken());
                params.put("team_id", team_id);
                params.put("title", Tools.filterEmoji(issuenotice_notitle.getText().toString().trim()));
                params.put("text", Tools.filterEmoji(issuenotice_content.getText().toString().trim()));
                return params;
            }
        };
    }

    private EditText issuenotice_notitle, issuenotice_content;
    private NetworkConnection publishNotice;
    private String team_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_notice);
        team_id = getIntent().getStringExtra("team_id");
        initTitle();
        initNetwork();
        issuenotice_notitle = (EditText) findViewById(R.id.issuenotice_notitle);
        issuenotice_content = (EditText) findViewById(R.id.issuenotice_content);
        findViewById(R.id.issuenotice_sumbit).setOnClickListener(this);
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.issuenotice_sumbit) {
            publishNotice();
        }
    }

    private void publishNotice() {
        publishNotice.sendPostRequest(Urls.PublishNotice, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Tools.showToast(IssueNoticeActivity.this, "发布成功");
                        baseFinish();
                        CorpsNoticeActivity.isRefresh = true;
                    } else {
                        Tools.showToast(IssueNoticeActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(IssueNoticeActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(IssueNoticeActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }
}
