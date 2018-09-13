package com.orange.oy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import java.util.HashMap;
import java.util.Map;

/**
 * 网点说明
 */
public class StoreDescActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {

    public void onBack() {
        baseFinish();
    }

    private void initTitle() {
        AppTitle taskILL_title = (AppTitle) findViewById(R.id.taskILL_title);
        taskILL_title.settingName("说明");
        taskILL_title.showBack(this);
        if (!data.getBooleanExtra("is_task", false)) {
            taskILL_title.settingExit("确定", new AppTitle.OnExitClickForAppTitle() {
                public void onExit() {
                    if (isOffline) {
                        data.setClass(StoreDescActivity.this, OfflinePackageActivity.class);
                        startActivity(data);
                        baseFinish();
                    } else {
                        data.setClass(StoreDescActivity.this, TaskitemDetailActivity_12.class);
                        startActivity(data);
                        baseFinish();
                    }
                }
            });
        }
    }

    private WebView taskILL_webview;
    private String store_id;
    private Intent data;
    private boolean isOffline;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storedesc);
        data = getIntent();
        if (data == null) {
            Tools.showToast(this, "缺少参数");
            baseFinish();
        } else {
            initTitle();
            String store_name = data.getStringExtra("store_name");
            store_id = data.getStringExtra("id");
            isOffline = data.getBooleanExtra("isOffline", false);
            TextView taskILL_name = (TextView) findViewById(R.id.taskILL_name);
            taskILL_name.setText(store_name);
            taskILL_webview = (WebView) findViewById(R.id.taskILL_webview);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                taskILL_webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
            if (isOffline) {
                taskILL_webview.loadData(data.getStringExtra("outletnote"), "text/html; charset=UTF-8", null);
            } else {
                taskILL_webview.loadUrl(Urls.Outletdesc + "?token=" + Tools.getToken() + "&storeid=" + store_id);
            }
        }
    }
}
