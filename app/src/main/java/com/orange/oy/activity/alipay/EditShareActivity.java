package com.orange.oy.activity.alipay;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class EditShareActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {

    private void initTitle() {
        AppTitle editshare_title = (AppTitle) findViewById(R.id.editshare_title);
        editshare_title.settingName("分享");
        editshare_title.showBack(this);
        editshare_title.setIllustrate(R.mipmap.share2, new AppTitle.OnExitClickForAppTitle() {
            @Override
            public void onExit() {//点击分享朋友圈等~~
                mShareAction.open();
            }
        });
    }

    private void initNetworkConnection() {
        Sign = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("key", key);
                return params;
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Sign != null) {
            Sign.stop(Urls.Sign);
        }
    }

    private String sign;
    private EditText editshare_content;
    private TextView editshare_count;
    private String projectid, key;
    private NetworkConnection Sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_share);
        initTitle();
        initNetworkConnection();
        projectid = getIntent().getStringExtra("projectid");
        key = "projectid=" + projectid + "&usermobile=" + AppInfo.getName(this);
        editshare_content = (EditText) findViewById(R.id.editshare_content);
        editshare_count = (TextView) findViewById(R.id.editshare_count);
        editshare_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                editshare_count.setText(editshare_content.getText().toString().length() + "/" + 500);
                configPlatforms();
            }
        });
        WebView editshare_webview = (WebView) findViewById(R.id.editshare_webview);
//        editshare_webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//        editshare_webview.getSettings().setSupportMultipleWindows(true);
//        editshare_webview.setWebViewClient(new WebViewClient());
//        editshare_webview.setWebChromeClient(new WebChromeClient());
//        editshare_webview.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            editshare_webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        editshare_webview.getSettings().setBlockNetworkImage(false);
        editshare_webview.loadUrl(Urls.Standard + "?projectid=" + projectid);
        Sign();
    }

    private ShareAction mShareAction;

    private void configPlatforms() {
        PlatformConfig.setQQZone(getResources().getString(R.string.qq_appid), getResources().getString(R.string.qq_appkey));
        UMImage umImage = new UMImage(this, BitmapFactory.decodeResource(getResources(), R.mipmap.login_icon));
        String str = null;
        String content = editshare_content.getText().toString();
        if (content == null || TextUtils.isEmpty(content)) {
            content = "进店体验，还能得现金，快来参加，一起赚钱。";
        }
        try {
            str = Urls.ShareProject + "?content=" + URLEncoder.encode(content, "utf-8") +
                    "&projectid=" + projectid + "&usermobile=" + AppInfo.getName(this) + "&sign=" + sign;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Tools.d("str:=" + str);
        UMWeb web = new UMWeb(str);
        web.setTitle(getResources().getString(R.string.share_title));//标题
        web.setThumb(umImage);
        web.setDescription("下载偶业 领取奖励金");//描述
        String wx_appid = getResources().getString(R.string.wx_appid);
        String wx_appsecret = getResources().getString(R.string.wx_appsecret);
        if (TextUtils.isEmpty(wx_appid) || TextUtils.isEmpty(wx_appsecret)) {
            mShareAction = new ShareAction(this).setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE).withMedia(web).setCallback(umShareListener);
        } else {
            PlatformConfig.setWeixin(wx_appid, wx_appsecret);
            mShareAction = new ShareAction(this).setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE)
                    .withMedia(web).setCallback(umShareListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    private UMShareListener umShareListener = new UMShareListener() {
        public void onStart(SHARE_MEDIA share_media) {
        }

        //分享成功的回调
        public void onResult(SHARE_MEDIA platform) {
            Tools.d(platform.toString());
            ConfirmDialog.showDialogForHint(EditShareActivity.this, "恭喜您分享成功");
        }

        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            ConfirmDialog.showDialogForHint(EditShareActivity.this, "分享失败,您需要重新分享");
        }

        public void onCancel(SHARE_MEDIA share_media) {
        }
    };

    private void Sign() {
        Sign.sendPostRequest(Urls.Sign, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        sign = jsonObject.getString("msg");
                        configPlatforms();
                    } else {
                        Tools.showToast(EditShareActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(EditShareActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(EditShareActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }


    @Override
    public void onBack() {
        baseFinish();
    }
}
