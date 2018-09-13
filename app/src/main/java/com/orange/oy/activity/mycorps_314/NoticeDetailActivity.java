package com.orange.oy.activity.mycorps_314;

import android.os.Bundle;
import android.view.KeyEvent;
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
 * 公告详情
 */
public class NoticeDetailActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.noticedetail_title);
        appTitle.settingName("公告详情");
        appTitle.showBack(this);
    }

    private NetworkConnection noticeDetail;
    private String notice_id, team_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);
        notice_id = getIntent().getStringExtra("notice_id");
        team_id = getIntent().getStringExtra("team_id");
        initTitle();
        initNetwork();
        getData();
    }

    private void getData() {
        noticeDetail.sendPostRequest(Urls.NoticeDetail, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        TextView noticedetail_name = (TextView) findViewById(R.id.noticedetail_name);
                        TextView noticedetail_time = (TextView) findViewById(R.id.noticedetail_time);
                        TextView noticedetail_content = (TextView) findViewById(R.id.noticedetail_content);
                        jsonObject = jsonObject.getJSONObject("data");
                        noticedetail_name.setText(jsonObject.getString("title"));
                        noticedetail_time.setText(jsonObject.getString("createTime"));
                        noticedetail_content.setText(jsonObject.getString("text"));
                    } else {
                        Tools.showToast(NoticeDetailActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(NoticeDetailActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(NoticeDetailActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private void initNetwork() {
        noticeDetail = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(NoticeDetailActivity.this));
                params.put("token", Tools.getToken());
                params.put("team_id", team_id);
                params.put("notice_id", notice_id);
                return params;
            }
        };
    }

    @Override
    public void onBack() {
        CorpsNoticeActivity.isRefresh = true;
        baseFinish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        CorpsNoticeActivity.isRefresh = true;
        return super.onKeyDown(keyCode, event);
    }
}
