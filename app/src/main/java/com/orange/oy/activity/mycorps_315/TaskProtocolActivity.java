package com.orange.oy.activity.mycorps_315;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.greenrobot.eventbus.EventBus;

/**
 * 领取任务协议==战队
 */
public class TaskProtocolActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.taskprotocol_title);
        appTitle.settingName("领取任务协议");
        appTitle.showBack(this);
    }

    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_protocol);
        initTitle();
        Intent data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        type = data.getStringExtra("type");
        WebView taskprotocol_webview = (WebView) findViewById(R.id.taskprotocol_webview);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            taskprotocol_webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        String team_id = data.getStringExtra("team_id");
        String project_id = data.getStringExtra("project_id");
        String package_id = data.getStringExtra("package_id");
        String package_team_id = data.getStringExtra("package_team_id");
        String content = Urls.RobTaskProtocol + "team_id=" + team_id + "&usermobile=" + AppInfo.getName(this)
                + "&project_id=" + project_id + "&package_id=" + package_id + "&token=" + Tools.getToken();
        if ("3".equals(type)) {
            findViewById(R.id.taskprotocol_agree).setVisibility(View.GONE);
            content = content + "&package_team_id=" + package_team_id;
        } else {
            findViewById(R.id.taskprotocol_agree).setVisibility(View.VISIBLE);
            findViewById(R.id.taskprotocol_agree).setOnClickListener(this);
        }
        taskprotocol_webview.loadUrl(content);
    }


    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.taskprotocol_agree) {
            if ("1".equals(type)) {
                EventBus.getDefault().post("1");
                baseFinish();
            } else {
                Intent intent = new Intent();
                intent.putExtra("isAgree", true);
                setResult(AppInfo.REQUEST_CODE_AGREE, intent);
                baseFinish();
            }
        }
    }
}
