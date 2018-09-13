package com.orange.oy.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.UMShareDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 邀请好友
 */
public class InvitefriendActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View
        .OnClickListener {

    private void initTitle() {
        AppTitle invitefd_title = (AppTitle) findViewById(R.id.invitefd_title);
        invitefd_title.settingName(getResources().getString(R.string.invitefd));
        invitefd_title.showBack(this);
        invitefd_title.settingExit("我的邀请", getResources().getColor(R.color.homepage_select), new AppTitle.OnExitClickForAppTitle() {
            @Override
            public void onExit() {
                startActivity(new Intent(InvitefriendActivity.this, MyRecommedActivity.class));
            }
        });
    }

    private void initNetworkConnection() {
        inviteFriendInfo = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(InvitefriendActivity.this));
                return params;
            }
        };
        sendData = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(InvitefriendActivity.this));
                params.put("shareway", platform);
                params.put("invitecode", AppInfo.getInviteCode(InvitefriendActivity.this));
                try {
                    params.put("versionnum", Tools.getVersionName(InvitefriendActivity.this));
                } catch (PackageManager.NameNotFoundException e) {
                    params.put("versionnum", "not found");
                }
                params.put("logintime", Tools.getTimeByPattern("yyyy-MM-dd HH-mm-ss"));
                params.put("comname", Tools.getDeviceType());
                params.put("phonemodle", Tools.getDeviceModel());
                params.put("sysversion", Tools.getSystemVersion() + "");
                params.put("operator", Tools.getCarrieroperator(InvitefriendActivity.this));
                params.put("resolution", Tools.getScreeInfoWidth(InvitefriendActivity.this) + "*" + Tools
                        .getScreeInfoHeight(InvitefriendActivity.this));
                params.put("mac", Tools.getLocalMacAddress(InvitefriendActivity.this));
                params.put("imei", Tools.getDeviceId(InvitefriendActivity.this));
                return params;
            }
        };
    }

    private NetworkConnection inviteFriendInfo, sendData;
    private String url;

    protected void onStop() {
        super.onStop();
        if (inviteFriendInfo != null) {
            inviteFriendInfo.stop(Urls.InviteFriendInfo);
        }
    }

    private TextView invitefd_code;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitefriend);
        initNetworkConnection();
        initTitle();
        getData();
        WebView webView = (WebView) findViewById(R.id.invitefd_webview);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.loadUrl(Urls.Invitefriend);
        findViewById(R.id.invitefd_button).setOnClickListener(this);
        invitefd_code = (TextView) findViewById(R.id.invitefd_code);
        invitefd_code.setOnClickListener(this);
    }

    private void getData() {
        inviteFriendInfo.sendPostRequest(Urls.InviteFriendInfo, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (!TextUtils.isEmpty(jsonObject.getString("invitecode")) && !"null".equals(jsonObject.getString("invitecode"))) {
                            findViewById(R.id.invitefd_ly).setVisibility(View.VISIBLE);
                            invitefd_code.setText(jsonObject.getString("invitecode"));
                        } else {
                            findViewById(R.id.invitefd_ly).setVisibility(View.GONE);
                        }
                        url = jsonObject.getString("url");
                    } else {
                        Tools.showToast(InvitefriendActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(InvitefriendActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(InvitefriendActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }


    public void onBack() {
        baseFinish();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    private String platform;
    private UMShareListener umShareListener = new UMShareListener() {
        public void onStart(SHARE_MEDIA share_media) {
        }

        public void onResult(SHARE_MEDIA platform) {
            Tools.showToast(InvitefriendActivity.this, "分享成功");
            Tools.d(platform.toString());
            InvitefriendActivity.this.platform = platform.toString();
            sendData();
        }

        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            Tools.showToast(InvitefriendActivity.this, "分享失败");
        }

        public void onCancel(SHARE_MEDIA share_media) {
            Tools.showToast(InvitefriendActivity.this, "分享取消");
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.invitefd_button: {
                UMShareDialog.showDialog(this, false, new UMShareDialog.UMShareListener() {
                    public void shareOnclick(int type) {
                        umShare(type);
                    }
                });
            }
            break;
            case R.id.invitefd_code: {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("Label", invitefd_code.getText().toString());
                cm.setPrimaryClip(mClipData);
                Tools.showToast(this, "复制成功");
            }
            break;
        }
    }

    private void sendData() {
        sendData.sendPostRequest(Urls.Statisticsinvite, new Response.Listener<String>() {
            public void onResponse(String s) {
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
    }

    private void umShare(int type) {
        if (type == 1) {//微信
            if (!isWeixinAvilible(this)) {
                Tools.showToast(this, "请先安装微信客户端~");
                return;
            }
            shareWeb(this, url, SHARE_MEDIA.WEIXIN);
        } else if (type == 2) {//朋友圈
            if (!isWeixinAvilible(this)) {
                Tools.showToast(this, "请先安装微信客户端~");
                return;
            }
            shareWeb(this, url, SHARE_MEDIA.WEIXIN_CIRCLE);
        } else if (type == 3) {//新浪微博
            shareWeb(this, url, SHARE_MEDIA.SINA);
        } else if (type == 4) {//QQ
            if (!isQQClientAvailable(this)) {
                Tools.showToast(this, "请先安装QQ客户端~");
                return;
            }
            shareWeb(this, url, SHARE_MEDIA.QQ);
        } else if (type == 5) {//QQ空间
            if (!isQQClientAvailable(this)) {
                Tools.showToast(this, "请先安装QQ客户端~");
                return;
            }
            shareWeb(this, url, SHARE_MEDIA.QZONE);
        }
    }

    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

    public void shareWeb(final Activity activity, String WebUrl, SHARE_MEDIA platform) {
        UMImage umImage = new UMImage(activity, BitmapFactory.decodeResource(activity.getResources(), R.mipmap.login_icon_share));
        UMWeb web = new UMWeb(WebUrl);//连接地址
        web.setTitle(getResources().getString(R.string.share_title));//标题
        web.setDescription("下载偶业 领取奖励金");//描述
        web.setThumb(umImage);
        new ShareAction(activity)
                .setPlatform(platform)
                .withMedia(web)
                .setCallback(umShareListener)
                .share();
    }
}
