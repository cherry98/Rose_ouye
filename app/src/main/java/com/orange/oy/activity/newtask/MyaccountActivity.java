package com.orange.oy.activity.newtask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.alipay.BillListDetailActivity;
import com.orange.oy.activity.alipay.IncomeDetailActivity;
import com.orange.oy.activity.alipay.WithdrawActivity;
import com.orange.oy.activity.alipay.WithdrawalMoneyListActivity;
import com.orange.oy.activity.scan.IdentityActivity;
import com.orange.oy.activity.scan.IdentityVerActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.UMShareDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.CircularImageView;
import com.umeng.socialize.ShareAction;
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
 * 我的账户页(我的奖励金)
 */
public class MyaccountActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle,
        View.OnClickListener {

    private void initTitle() {
        AppTitle titleview = (AppTitle) findViewById(R.id.titleview);
        titleview.showBack(this);
        titleview.settingName("我的奖金");
        titleview.showIllustrate(R.mipmap.share2, new AppTitle.OnExitClickForAppTitle() {
            @Override
            public void onExit() {
                UMShareDialog.showDialog(MyaccountActivity.this, false, new UMShareDialog.UMShareListener() {
                    @Override
                    public void shareOnclick(int type) {
                        umShare(type);
                    }
                });
            }
        });
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

    private void umShare(int type) {
        String url = Urls.Bonus + "?usermobile=" + AppInfo.getName(this);
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

    private UMShareListener umShareListener = new UMShareListener() {
        public void onStart(SHARE_MEDIA share_media) {
        }

        public void onResult(SHARE_MEDIA platform) {
            Tools.showToast(MyaccountActivity.this, "分享成功");
            Tools.d(platform.toString());
        }

        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            Tools.showToast(MyaccountActivity.this, "分享失败");
        }

        public void onCancel(SHARE_MEDIA share_media) {
            Tools.showToast(MyaccountActivity.this, "分享取消");
        }
    };

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

    private NetworkConnection Myaccount, Getmoney;
    private String money;

    public void onBack() {
        baseFinish();
    }

    protected void onStop() {
        super.onStop();
        if (Myaccount != null) {
            Myaccount.stop(Urls.Myaccount);
        }
    }

    private void initNetworkConnection() {
        Myaccount = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(MyaccountActivity.this));
                return params;
            }
        };
        Getmoney = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("money", money);
                params.put("type", "0");
                return params;
            }
        };
        Getmoney.setIsShowDialog(true);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myaccount);
        ImageLoader imageLoader = new ImageLoader(this);
        initTitle();
        initNetworkConnection();
        ScrollView myaccount_scrollview = (ScrollView) findViewById(R.id.myaccount_scrollview);
        myaccount_scrollview.smoothScrollTo(0, 20);
        CircularImageView myaccount_headimg = (CircularImageView) findViewById(R.id.myaccount_headimg);
        if (!TextUtils.isEmpty(AppInfo.getKey(this))) {
            String imgUrl = AppInfo.getUserImagurl(this);
            if (!TextUtils.isEmpty(imgUrl)) {
                imageLoader.DisplayImage(imgUrl, myaccount_headimg, R.mipmap.grxx_icon_mrtx);
            } else {
                myaccount_headimg.setImageResource(R.mipmap.grxx_icon_mrtx);
            }
        } else {
            myaccount_headimg.setImageResource(R.mipmap.grxx_icon_mrtx);
        }
    }

    protected void onResume() {
        getData();
        super.onResume();
    }

    private String getmoney, duty_free;
    private String withdrawalmoney, true_name, payaccount;

    private void getData() {
        Myaccount.sendPostRequest(Urls.Myaccount, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        withdrawalmoney = jsonObject.optString("withdrawalmoney");//可提现金额
                        TextView myaccount_money1 = (TextView) findViewById(R.id.myaccount_money1);
                        if (withdrawalmoney != null) {
                            if (TextUtils.isEmpty(withdrawalmoney)) {
                                withdrawalmoney = "-";
                            } else {
                                double d = Tools.StringToDouble(withdrawalmoney);
                                if (d - (int) d > 0) {
                                    withdrawalmoney = String.valueOf(d);
                                } else {
                                    withdrawalmoney = String.valueOf((int) d);
                                }
                            }
                            myaccount_money1.setText(String.format(getResources().getString(R.string.account_money),
                                    "¥" + withdrawalmoney));
                        } else {
                            myaccount_money1.setText("-");
                        }

                        String total_money = jsonObject.optString("totalmoney");//已提现金额
                        TextView myaccount_total = (TextView) findViewById(R.id.myaccount_total);
                        if (total_money != null) {
                            if (TextUtils.isEmpty(total_money)) {
                                total_money = "-";
                            } else {
                                double d = Tools.StringToDouble(total_money);
                                if (d - (int) d > 0) {
                                    total_money = String.valueOf(d);
                                } else {
                                    total_money = String.valueOf((int) d);
                                }
                            }
                            myaccount_total.setText(String.format(getResources().getString(R.string.account_money),
                                    total_money));
                        } else {
                            myaccount_total.setText("-");
                        }

                        getmoney = jsonObject.optString("getmoney");//已提现金额
                        TextView myaccount_getmoney = (TextView) findViewById(R.id.myaccount_getmoney);
                        if (getmoney != null) {
                            if (TextUtils.isEmpty(getmoney)) {
                                getmoney = "-";
                            } else {
                                double d = Tools.StringToDouble(getmoney);
                                if (d - (int) d > 0) {
                                    getmoney = String.valueOf(d);
                                } else {
                                    getmoney = String.valueOf((int) d);
                                }
                            }
                            myaccount_getmoney.setText(String.format(getResources().getString(R.string.account_money),
                                    getmoney));
                        } else {
                            myaccount_getmoney.setText("-");
                        }
                        ((TextView) findViewById(R.id.myaccount_taskcount)).setText(jsonObject.optString("outlet_number"));

                        int percentage = jsonObject.optInt("percentage");//用户获得总金额在所有用户中的排序位置
                        TextView myaccount_percent1 = (TextView) findViewById(R.id.myaccount_percent1);
                        myaccount_percent1.setText(String.format(getResources().getString(R.string.account_money2), percentage + "%"));
                        ProgressBar myaccount_percent2 = (ProgressBar) findViewById(R.id.myaccount_percent2);
                        myaccount_percent2.setProgress(percentage);
                        ((TextView) findViewById(R.id.myaccount_percent3)).setText(percentage + "%");
                        true_name = jsonObject.getString("true_name");

                        TextView myaccount_identifyver_state = (TextView) findViewById(R.id.myaccount_identifyver_state);
                        String bindidcard = jsonObject.getString("bindidcard");
                        AppInfo.isbindidcard(MyaccountActivity.this, bindidcard);
                        if ("1".equals(bindidcard)) {//已绑定 身份证
                            myaccount_identifyver_state.setText("已认证");
                            myaccount_identifyver_state.setTextColor(getResources().getColor(R.color.homepage_notselect));
                        } else {
                            myaccount_identifyver_state.setText("未认证");
                            myaccount_identifyver_state.setTextColor(getResources().getColor(R.color.homepage_select));
                        }
                        TextView myaccount_identify_state = (TextView) findViewById(R.id.myaccount_identify_state);
                        String bindaccount = jsonObject.getString("bindaccount");
                        AppInfo.isbindaccount(MyaccountActivity.this, bindaccount);
                        if ("1".equals(bindaccount)) {//已绑定 支付宝
                            myaccount_identify_state.setText("已绑定");
                            myaccount_identify_state.setTextColor(getResources().getColor(R.color.homepage_notselect));
                        } else {
                            myaccount_identify_state.setText("未绑定");
                            myaccount_identify_state.setTextColor(getResources().getColor(R.color.homepage_select));
                        }
                        payaccount = jsonObject.getString("payaccount");
                        duty_free = jsonObject.getString("duty_free");//免税额度
                    } else {
                        Tools.showToast(MyaccountActivity.this, jsonObject.getString("msg"));
                    }
                    findViewById(R.id.myaccount_button).setOnClickListener(MyaccountActivity.this);
                    findViewById(R.id.myaccount_identify).setOnClickListener(MyaccountActivity.this);
                    findViewById(R.id.myaccount_identifyver).setOnClickListener(MyaccountActivity.this);
                    findViewById(R.id.myaccount_money1_ly).setOnClickListener(MyaccountActivity.this);
                    findViewById(R.id.myaccount_total).setOnClickListener(MyaccountActivity.this);
                    findViewById(R.id.myaccount_getmoney).setOnClickListener(MyaccountActivity.this);
                } catch (JSONException e) {
                    Tools.showToast(MyaccountActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(MyaccountActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.myaccount_button: {
                if (!AppInfo.isBindidCard(MyaccountActivity.this)) {//已绑定身份证
                    if (!AppInfo.isBindAccount(MyaccountActivity.this)) {//已绑定支付宝
                        if ("-".equals(withdrawalmoney) || "0".equals(withdrawalmoney)) {
                            ConfirmDialog.showDialog(MyaccountActivity.this, "您无可提现金额", true, null).goneLeft();
                            return;
                        }
                        Intent intent = new Intent(MyaccountActivity.this, WithdrawActivity.class);
                        intent.putExtra("withdrawalmoney", withdrawalmoney);
                        intent.putExtra("duty_free", duty_free);
                        startActivity(intent);
                    } else {//未绑定支付宝
                        Intent intent = new Intent(MyaccountActivity.this, IdentityActivity.class);
                        startActivity(intent);
                    }
                } else {//未绑定身份证
                    startActivity(new Intent(MyaccountActivity.this, IdentityVerActivity.class));
                }
            }
            break;
            case R.id.myaccount_money1_ly: {//我的奖金-提现
                if (!AppInfo.isBindidCard(MyaccountActivity.this)) {//已绑定身份证
                    if (!AppInfo.isBindAccount(MyaccountActivity.this)) {//已绑定支付宝
                        Intent intent = new Intent(MyaccountActivity.this, BillListDetailActivity.class);
                        intent.putExtra("duty_free", duty_free);
                        intent.putExtra("withdrawalmoney", withdrawalmoney);
                        startActivity(intent);
                    } else {//未绑定支付宝
                        Intent intent = new Intent(MyaccountActivity.this, IdentityActivity.class);
                        startActivity(intent);
                    }
                } else {//未绑定身份证
                    startActivity(new Intent(MyaccountActivity.this, IdentityVerActivity.class));
                }
            }
            break;
            case R.id.myaccount_identify: {//绑定支付宝
                if (AppInfo.isBindidCard(this)) {//绑定支付宝之前先要判定是否绑定身份证
                    Intent intent = new Intent(this, IdentityVerActivity.class);
                    intent.putExtra("isJudge", true);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, IdentityActivity.class);
                    intent.putExtra("name", true_name);
                    intent.putExtra("payaccount", payaccount);
                    startActivity(intent);
                }
            }
            break;
            case R.id.myaccount_identifyver: {//绑定身份证
                startActivity(new Intent(this, IdentityVerActivity.class));
            }
            break;
            case R.id.myaccount_total: {//累计总金额
                startActivity(new Intent(MyaccountActivity.this, IncomeDetailActivity.class));
            }
            break;
            case R.id.myaccount_getmoney: {//已提现==提现明细
                startActivity(new Intent(MyaccountActivity.this, WithdrawalMoneyListActivity.class));
            }
            break;
        }
    }
}
