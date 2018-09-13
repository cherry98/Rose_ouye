package com.orange.oy.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.io.ByteArrayOutputStream;

/**
 * Created by xiedongyan on 2017/11/20.
 */

public class UMShareUtils {

    public static void shareWeb(Activity activity, String WebUrl, SHARE_MEDIA platform) {
        shareWeb(activity, WebUrl, platform, activity.getResources().getString(R.string.share_title), "下载偶业 领取奖励金");
    }

    public static void shareWeb_shake(Activity activity, String WebUrl, SHARE_MEDIA platform) {
        shareWeb(activity, WebUrl, platform, "一起来晒图，瓜分现金红包", "【偶业】一款晒图就得红包的app");
    }

    /**
     * 分享链接
     */
    public static void shareWeb(final Activity activity, String WebUrl, SHARE_MEDIA platform, String title, String msg) {
        UMImage umImage = new UMImage(activity, BitmapFactory.decodeResource(activity.getResources(), R.mipmap.login_icon_share));
        UMWeb web = new UMWeb(WebUrl);//连接地址
        web.setTitle(title);//标题
        web.setDescription(msg);//描述
        web.setThumb(umImage);
        new ShareAction(activity)
                .setPlatform(platform)
                .withMedia(web)
                .setCallback(new UMShareListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {

                    }

                    @Override
                    public void onResult(final SHARE_MEDIA share_media) {
                        if (!(share_media == SHARE_MEDIA.WEIXIN || share_media == SHARE_MEDIA.WEIXIN_CIRCLE)) {
                            Tools.showToast(activity, "分享成功");
                        }
                    }

                    @Override
                    public void onError(final SHARE_MEDIA share_media, final Throwable throwable) {
                        Tools.showToast(activity, "分享失败");
                    }

                    @Override
                    public void onCancel(final SHARE_MEDIA share_media) {
                        Tools.showToast(activity, "分享取消");
                    }
                })
                .share();
    }

    /**
     * 分享微信小程序
     */
    public static void shareMin(final Activity context, String weburl, String path) {
        IWXAPI api = WXAPIFactory.createWXAPI(context, "wx87bba3c32217c786");
        WXMiniProgramObject miniProgramObj = new WXMiniProgramObject();
        miniProgramObj.webpageUrl = weburl; // 兼容低版本的网页链接
        miniProgramObj.miniprogramType = WXMiniProgramObject.MINIPTOGRAM_TYPE_RELEASE;// 正式版:0，测试版:1，体验版:2
        miniProgramObj.userName = "gh_1944a85b173c";     // 小程序原始id
        miniProgramObj.path = path;            //小程序页面路径
        WXMediaMessage msg = new WXMediaMessage(miniProgramObj);
        msg.title = "一起来晒图，刮分现金红包";                    // 小程序消息title
        msg.description = "【偶业】一款晒图就得红包的app";               // 小程序消息desc
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BitmapFactory.decodeResource(context.getResources(), R.drawable.wxmin_pic).compress(Bitmap.CompressFormat.JPEG, 80, baos);
        msg.thumbData = baos.toByteArray();                      // 小程序消息封面图片，小于128k

        SendMessageToWX.Req req = new SendMessageToWX.Req();
//        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;  // 目前支持会话
        api.sendReq(req);
//        PlatformConfig.setWeixin("gh_7dce52467190", "c9a16af762465dc7ed7528053e35c7bb");
//        UMMin umMin = new UMMin(weburl);
//        //兼容低版本的网页链接
//        umMin.setThumb(new UMImage(context, R.mipmap.ic_launcher));
//        // 小程序消息封面图片
//        umMin.setTitle(context.getResources().getString(R.string.share_title));
//        // 小程序消息title
//        umMin.setDescription("");
//        // 小程序消息描述
//        umMin.setPath(path);
//        //小程序页面路径
//        umMin.setUserName(username);
//        // 小程序原始id,在微信平台查询
//        new ShareAction(context)
//                .withMedia(umMin)
//                .setPlatform(SHARE_MEDIA.WEIXIN)
//                .setCallback(new UMShareListener() {
//                    public void onStart(SHARE_MEDIA share_media) {
//                    }
//
//                    public void onResult(final SHARE_MEDIA share_media) {
//                        Tools.showToast(context, "分享成功");
//                    }
//
//                    public void onError(final SHARE_MEDIA share_media, final Throwable throwable) {
//                        Tools.showToast(context, "分享失败");
//                    }
//
//                    public void onCancel(final SHARE_MEDIA share_media) {
//                        Tools.showToast(context, "分享取消");
//                    }
//                }).share();
    }

}
