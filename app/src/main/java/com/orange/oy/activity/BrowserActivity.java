package com.orange.oy.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.fragment.MyFragment;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * html数据显示总页
 */
public class BrowserActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {
    public static final String flag_about = "1";
    public static final String flag_broadcast = "2";
    public static final String flag_question = "3";
    public static final String flag_protocol = "4";
    public static final String flag_fold = "5";
    public static final String flag_custom = "6";
    public static final String flag_loading = "7";
    public static final String flag_phonepay = "8";//手机充值
    public static final String flag_rule = "9";
    public static final String flag_readurl = "10";//h5页面，只读没有其他操作

    private void initTitle(String title) {
        AppTitle browser_title = (AppTitle) findViewById(R.id.browser_title);
        browser_title.setVisibility(View.VISIBLE);
        browser_title.settingName(title);
        browser_title.showBack(this);
    }

    protected void onStop() {
        super.onStop();
        if (getDownInfoForBroadcast != null) {
            getDownInfoForBroadcast.stop(Urls.Announcement);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.destroy();
        }
    }

    public void onBack() {
        baseFinish();
    }

    private void initNetworkConnectionForBroadcast() {
        getDownInfoForBroadcast = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("id", id);
                return params;
            }
        };
        getDownInfoForBroadcast.setIsShowDialog(true);
    }

    private WebView webView;
    private NetworkConnection getDownInfoForBroadcast;
    private String id;
    private ProgressBar browser_progressbar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        browser_progressbar = (ProgressBar) findViewById(R.id.browser_progressbar);
        Intent data = getIntent();
        if (data == null) {
            baseFinish();
        } else {
            webView = (WebView) findViewById(R.id.browser_webview);
            webView.getSettings().setDefaultTextEncodingName("UTF-8");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
            String flag = data.getStringExtra("flag");
            String title = data.getStringExtra("title");
            String content = data.getStringExtra("content");
            id = data.getStringExtra("id");
            if (TextUtils.isEmpty(flag)) {
                baseFinish();
                return;
            }
            switch (flag) {
                case flag_readurl: {
                    if (!TextUtils.isEmpty(title)) {
                        initTitle(title);
                    }
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                    webView.getSettings().setDefaultTextEncodingName("UTF-8");
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.getSettings().setAppCacheEnabled(true);
                    // 开启 DOM storage API 功能
                    webView.getSettings().setDomStorageEnabled(true);
                    webView.setWebViewClient(new WebViewClient() {
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            view.loadUrl(url);
                            return true;
                        }


                    });
                    webView.loadUrl(content);
                }
                break;
                case flag_rule: //偶米，里面的规则
                    if (!TextUtils.isEmpty(title)) {
                        initTitle(title);
                    }
                    webView.loadUrl(content);
                    break;
                case flag_about: //关于页跳转
//                initNetworkConnectionForAbout();
                    if (TextUtils.isEmpty(title)) {
                        initTitle(getResources().getString(R.string.aboutus));

                    } else {
                        initTitle(title);
                    }
//                getDataForAbout();
                    findViewById(R.id.aboutus_version_ly).setVisibility(View.VISIBLE);
                    TextView textView = (TextView) findViewById(R.id.aboutus_version);
                    try {
                        textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        textView.setText("偶业 v" + Tools.getVersionName(this));
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    webView.loadUrl(Urls.API + "about");
                    break;
                case flag_broadcast: //消息-广播跳转
                    initNetworkConnectionForBroadcast();
                    if (!TextUtils.isEmpty(title)) {
                        initTitle(title);
                    }
//                if (!TextUtils.isEmpty(content) && !content.equals("null")) {
//                    webView.loadData(content, "text/html; charset=UTF-8", null);
//                }
                    if (!TextUtils.isEmpty(id)) {
//                    View browser_down_layout = findViewById(R.id.browser_down_layout);
//                    View browser_down_item1 = findViewById(R.id.browser_down_item1);
//                    View browser_down_item2 = findViewById(R.id.browser_down_item2);
//                    TextView browser_down_item1_msg = (TextView) findViewById(R.id.browser_down_item1_msg);
//                    TextView browser_down_item2_msg = (TextView) findViewById(R.id.browser_down_item2_msg);
                        getDataForBroadcast();
                    }
                    break;
                case flag_question: //常见问题跳转
                    if (!TextUtils.isEmpty(title)) {
                        initTitle(title);
                    }
                    if (!TextUtils.isEmpty(content) && !content.equals("null")) {
                        webView.loadUrl(content);
                    }
                    break;
                case flag_protocol: //注册协议
                    initTitle("注册协议");
                    webView.loadUrl("http://www.oyearn.com/mobile/index.html");
                    break;
                case flag_fold:
                    if (!TextUtils.isEmpty(title)) {
                        initTitle(title);
                    }
                    webView.loadData(content, "text/html; charset=UTF-8", null);
                    break;
                case flag_custom: {
                    initTitle(title);
                    webView.setWebViewClient(new WebViewClient() {
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            view.loadUrl(url);
                            return true;
                        }
                    });
                    webView.setWebChromeClient(new WebChromeClient() {
                        @Override
                        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                            return super.onJsAlert(view, url, message, result);
                        }

                        @Override
                        public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
                            return super.onJsBeforeUnload(view, url, message, result);
                        }

                        @Override
                        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                            return super.onJsConfirm(view, url, message, result);
                        }

                        @Override
                        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult
                                result) {
                            return super.onJsPrompt(view, url, message, defaultValue, result);
                        }
                    });
                    settingWebView();
                    webView.loadUrl(content);
                }
                break;
                case flag_loading: {
                    webView.setWebViewClient(new WebViewClient() {
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            view.loadUrl(url);
                            return true;
                        }
                    });
                    webView.setWebChromeClient(new WebChromeClient() {
                        @Override
                        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                            return super.onJsAlert(view, url, message, result);
                        }

                        @Override
                        public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
                            return super.onJsBeforeUnload(view, url, message, result);
                        }

                        @Override
                        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                            return super.onJsConfirm(view, url, message, result);
                        }

                        @Override
                        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult
                                result) {
                            return super.onJsPrompt(view, url, message, defaultValue, result);
                        }
                    });
                    settingWebView();
                    webView.loadUrl(content);
                }
                break;
                case flag_phonepay: {
                    MyFragment.isRefresh = true;
                    initTitle("手机充值");
                    browser_progressbar.setVisibility(View.VISIBLE);
                    webView.setWebViewClient(new WebViewClient() {
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            view.loadUrl(url);
                            return true;
                        }
                    });
                    webView.setWebChromeClient(new WebChromeClient() {
                        public void onProgressChanged(WebView view, int newProgress) {
                            super.onProgressChanged(view, newProgress);
                            browser_progressbar.setProgress(newProgress);
                            if (newProgress == 100) {
                                browser_progressbar.setVisibility(View.GONE);
                                if (!TextUtils.isEmpty(userPhoneNum))
                                    webView.loadUrl("javascript:setTelphone('" + userPhoneNum + "')");
                            }
                        }

                        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                            return super.onJsAlert(view, url, message, result);
                        }

                        public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
                            return super.onJsBeforeUnload(view, url, message, result);
                        }

                        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                            return super.onJsConfirm(view, url, message, result);
                        }

                        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult
                                result) {
                            return super.onJsPrompt(view, url, message, defaultValue, result);
                        }
                    });
                    settingWebView();
                    webView.addJavascriptInterface(new PhonePayContact() {
                        @JavascriptInterface
                        public void getPhoneContact(String phone) {
                            if (Build.VERSION.SDK_INT >= 23) {
                                int checkCallPhonePermission = ContextCompat.checkSelfPermission(BrowserActivity.this, Manifest.permission.READ_CONTACTS);
                                if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(BrowserActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, AppInfo
                                            .REQUEST_CODE_ASK_READ_PHONE_STATE);
                                } else {
                                    startActivityForResult(new Intent(
                                            Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), Integer.parseInt(flag_phonepay));
                                }
                            } else {
                                startActivityForResult(new Intent(
                                        Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), Integer.parseInt(flag_phonepay));
                            }
                        }
                    }, "ouYe_phone_js");
                    String url = Urls.API + "rechargePhone?usermobile=" + AppInfo.getName(this) + "&token=" + Tools.getToken();
                    webView.loadUrl(url);
                    userPhoneNum = data.getStringExtra("userPhoneNum");
                }
                break;
            }
        }
    }

    private String userPhoneNum;

    private interface PhonePayContact {//js调取接口

        @JavascriptInterface
        void getPhoneContact(String phone);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        switch (requestCode) {
            case AppInfo.REQUEST_CODE_ASK_READ_PHONE_STATE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(new Intent(
                            Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), Integer.parseInt(flag_phonepay));
                } else {
                    Tools.showToast(BrowserActivity.this, "权限获取失败");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 8: {//获取手机联系人
                    if (data == null) {
                        return;
                    }
                    ContentResolver reContentResolverol = getContentResolver();
                    Uri contactData = data.getData();
                    @SuppressWarnings("deprecation")
                    Cursor cursor = managedQuery(contactData, null, null, null, null);
                    cursor.moveToFirst();
                    String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    Cursor phone = reContentResolverol.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                            null,
                            null);
                    if (phone != null) {
                        if (phone.moveToNext()) {
                            String usernumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            webView.loadUrl("javascript:setTelphone('" + usernumber + "')");
                        }
                        phone.close();
                    }
                }
                break;
            }
        }
    }

    private void settingWebView() {
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setDefaultTextEncodingName("UTF-8");
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setDomStorageEnabled(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    private void getDataForBroadcast() {
        getDownInfoForBroadcast.sendPostRequest(Urls.Announcement, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        String content = jsonObject.getString("content");
                        if (!TextUtils.isEmpty(content)) {
                            webView.loadData(content, "text/html; charset=UTF-8", null);
                        }
//                        JSONArray jsonArray = jsonObject.getJSONArray("filelist");
//                        int length = jsonArray.length();
//                        if (length > 0) {
//                            browser_down_layout.setVisibility(View.VISIBLE);
//                            if (length == 1) {
//                                browser_down_item1.setVisibility(View.VISIBLE);
//                                jsonObject = jsonArray.getJSONObject(0);
//                                browser_down_item1_msg.setText(jsonObject.getString("profile"));
//                                browser_down_item1.setTag(Urls.ip + jsonObject.getString("filePath") + "," + jsonObject
//                                        .getString("originalFileName"));
//                            } else if (length == 2) {
//                                browser_down_item1.setVisibility(View.VISIBLE);
//                                browser_down_item2.setVisibility(View.VISIBLE);
//                                jsonObject = jsonArray.getJSONObject(0);
//                                browser_down_item1_msg.setText(jsonObject.getString("profile"));
//                                browser_down_item1.setTag(Urls.ip + jsonObject.getString("filePath") + "," + jsonObject
//                                        .getString("originalFileName"));
//                                jsonObject = jsonArray.getJSONObject(1);
//                                browser_down_item2_msg.setText(jsonObject.getString("profile"));
//                                browser_down_item2.setTag(Urls.ip + jsonObject.getString("filePath") + "," + jsonObject
//                                        .getString("originalFileName"));
//                            }
//                        }
                    } else {
                        Tools.showToast(BrowserActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(BrowserActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(BrowserActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.browser_down_item1: {
            }
            case R.id.browser_down_item2: {//TODO 下载去掉
                Object object = v.getTag();
//                if (http != null && object != null) {
                String[] strs = object.toString().split(",");
                if (strs.length != 2) return;
//                    handler = http.download(strs[0] + strs[1], new File(FileCache.getDirForDownload(BrowserActivity
//                            .this), strs[1]).getPath(), false, true, new
//                            RequestCallBack<File>() {
//                                public void onStart() {
//                                    CustomProgressDialog.showProgressDialog(BrowserActivity.this, "正在下载...");
//                                }
//
//                                public void onLoading(long total, long current, boolean isUploading) {
////                                    testTextView.setText(current + "/" + total);
//                                }
//
//                                public void onSuccess(ResponseInfo<File> responseInfo) {
//                                    Tools.showToast(BrowserActivity.this, "下载完成：" + responseInfo.result.getPath());
//                                    CustomProgressDialog.Dissmiss();
//                                }
//
//                                public void onFailure(HttpException error, String msg) {
//                                    CustomProgressDialog.Dissmiss();
//                                }
//                            });
//                }
            }
            break;
        }
    }
}
