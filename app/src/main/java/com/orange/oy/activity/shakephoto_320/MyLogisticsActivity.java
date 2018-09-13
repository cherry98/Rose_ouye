package com.orange.oy.activity.shakephoto_320;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.orange.oy.R;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.view.AppTitle;

/**
 * 我的物流信息详情 V3.20
 */
public class MyLogisticsActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {

    private String url = "https://m.kuaidi100.com/index_all.html?";
    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_logistics);
        AppTitle appTitle = (AppTitle) findViewById(R.id.mylogistics_title);
        appTitle.settingName("物流详情");
        appTitle.showBack(this);
        webView = (WebView) findViewById(R.id.mylogistics_webview);
        TextView mylogistics_name = (TextView) findViewById(R.id.mylogistics_name);
        TextView mylogistics_source = (TextView) findViewById(R.id.mylogistics_source);
        TextView mylogistics_number = (TextView) findViewById(R.id.mylogistics_number);
        TextView mylogistics_phone = (TextView) findViewById(R.id.mylogistics_phone);

        Intent data = getIntent();
        String type = data.getStringExtra("type");
        String gift_name = data.getStringExtra("gift_name");
        String express_number = data.getStringExtra("express_number");
        mylogistics_name.setText(gift_name);
        mylogistics_source.setText("承运来源：" + data.getStringExtra("express_company"));
        mylogistics_number.setText("运单编号：" + express_number);
        mylogistics_phone.setText("官方电话：" + data.getStringExtra("official_phone"));
        url = url + "type=" + type + "&postid=" + express_number;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        //支持javascript
        webView.getSettings().setJavaScriptEnabled(true);
        // 设置可以支持缩放
        webView.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
        webView.getSettings().setBuiltInZoomControls(true);
        //扩大比例的缩放
        webView.getSettings().setUseWideViewPort(true);
        //控制WebView 自适应屏幕
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new MyWebView());
        webView.loadUrl(url);
    }

    public class MyWebView extends WebViewClient {

        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            baseFinish();//无效果
            return true;
        }

    }

    @Override
    public void onBack() {
        baseFinish();
    }
}
