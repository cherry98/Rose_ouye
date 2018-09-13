package com.orange.oy.base;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.orange.oy.network.Urls;
import com.orange.oy.util.UMShareUtils;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.List;

/**
 * Created by Lenovo on 2018/5/23.
 * 友盟分享
 */

public class MyUMShareUtils {
    public static void umShare(Context context, int type, String webUrl) {
        umShare(context, type, webUrl, 0);
    }

    public static void umShare_shakephoto(Context context, int type, String webUrl) {
        umShare(context, type, webUrl, 1);
    }

    public static void umShare(Context context, int type, String webUrl, int state) {
        umShare(context, type, webUrl, state, null, null);
    }

    /**
     * @param context
     * @param type
     * @param webUrl
     * @param state   0:正常，1:甩图分享
     */
    public static void umShare(Context context, int type, String webUrl, int state, String title, String msg) {
        if (type == 1) {//微信
            if (!isWeixinAvilible(context)) {
                Tools.showToast(context, "请先安装微信客户端~");
                return;
            }
            if (title == null || msg == null) {
                if (state == 1) {
                    UMShareUtils.shareWeb_shake((Activity) context, webUrl, SHARE_MEDIA.WEIXIN);
                } else {
                    UMShareUtils.shareWeb((Activity) context, webUrl, SHARE_MEDIA.WEIXIN);
                }
            } else {
                UMShareUtils.shareWeb((Activity) context, webUrl, SHARE_MEDIA.WEIXIN, title, msg);
            }
        } else if (type == 2) {//朋友圈
            if (!isWeixinAvilible(context)) {
                Tools.showToast(context, "请先安装微信客户端~");
                return;
            }
            if (title == null || msg == null) {
                if (state == 1) {
                    UMShareUtils.shareWeb_shake((Activity) context, webUrl, SHARE_MEDIA.WEIXIN_CIRCLE);
                } else {
                    UMShareUtils.shareWeb((Activity) context, webUrl, SHARE_MEDIA.WEIXIN_CIRCLE);
                }
            } else {
                UMShareUtils.shareWeb((Activity) context, webUrl, SHARE_MEDIA.WEIXIN_CIRCLE, title, msg);
            }
        } else if (type == 3) {//新浪微博
            if (title == null || msg == null) {
                if (state == 1) {
                    UMShareUtils.shareWeb_shake((Activity) context, webUrl, SHARE_MEDIA.SINA);
                } else {
                    UMShareUtils.shareWeb((Activity) context, webUrl, SHARE_MEDIA.SINA);
                }
            } else {
                UMShareUtils.shareWeb((Activity) context, webUrl, SHARE_MEDIA.SINA, title, msg);
            }
        } else if (type == 4) {//QQ
            if (!isQQClientAvailable(context)) {
                Tools.showToast(context, "请先安装QQ客户端~");
                return;
            }
            if (title == null || msg == null) {
                if (state == 1) {
                    UMShareUtils.shareWeb_shake((Activity) context, webUrl, SHARE_MEDIA.QQ);
                } else {
                    UMShareUtils.shareWeb((Activity) context, webUrl, SHARE_MEDIA.QQ);
                }
            } else {
                UMShareUtils.shareWeb((Activity) context, webUrl, SHARE_MEDIA.QQ, title, msg);
            }
        } else if (type == 5) {//QQ空间
            if (!isQQClientAvailable(context)) {
                Tools.showToast(context, "请先安装QQ客户端~");
                return;
            }
            if (title == null || msg == null) {
                if (state == 1) {
                    UMShareUtils.shareWeb_shake((Activity) context, webUrl, SHARE_MEDIA.QZONE);
                } else {
                    UMShareUtils.shareWeb((Activity) context, webUrl, SHARE_MEDIA.QZONE);
                }
            } else {
                UMShareUtils.shareWeb((Activity) context, webUrl, SHARE_MEDIA.QZONE, title, msg);
            }
        } else if (type == 6) {//小程序
            if (!isWeixinAvilible(context)) {
                Tools.showToast(context, "请先安装微信客户端~");
                return;
            }
            UMShareUtils.shareMin((Activity) context,
                    Urls.ip + "ouye/h5/login.html", "pages/directory/directory");
        }
    }

    private static boolean isWeixinAvilible(Context context) {
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

    private static boolean isQQClientAvailable(Context context) {
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
}
