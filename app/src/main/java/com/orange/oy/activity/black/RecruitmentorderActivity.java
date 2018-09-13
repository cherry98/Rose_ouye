package com.orange.oy.activity.black;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.view.AppTitle;

/**
 * 招募令页
 */
public class RecruitmentorderActivity extends BaseActivity implements View.OnClickListener {
    private View recruitmentorder_button;
    private TextView recruitmentorder_toptext, recruitmentorder_text;
    private WebView recruitmentorder_webview;
    private Intent data;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruitmentorder);
        data = getIntent();
        AppTitle recruitmentorder_title = (AppTitle) findViewById(R.id.recruitmentorder_title);
        recruitmentorder_title.settingName("招募令");
        recruitmentorder_title.showBack(new AppTitle.OnBackClickForAppTitle() {
            public void onBack() {
                baseFinish();
            }
        });
        recruitmentorder_button = findViewById(R.id.recruitmentorder_button);
        recruitmentorder_toptext = (TextView) findViewById(R.id.recruitmentorder_toptext);
        recruitmentorder_text = (TextView) findViewById(R.id.recruitmentorder_text);
        recruitmentorder_webview = (WebView) findViewById(R.id.recruitmentorder_webview);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            recruitmentorder_webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recruitmentorder_button: {
            }
            break;
        }
    }
}
