package com.orange.oy.base;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDex;
import android.view.WindowManager;

import com.baidu.mapapi.SDKInitializer;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.orange.oy.network.Urls;
import com.sobot.chat.SobotApi;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import cn.jpush.android.api.JPushInterface;

public class MyApplication extends Application {
    private static MyApplication instance;
    private WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

    public WindowManager.LayoutParams getMywmParams() {
        return wmParams;
    }

    public void onCreate() {
        super.onCreate();
        instance = this;
        SDKInitializer.initialize(this);
        Fresco.initialize(this);
        JPushInterface.init(this);
        UMShareAPI.get(this);//初始化sdk
        PlatformConfig.setWeixin("wx87bba3c32217c786", "ed7e20211af83c3f96eac780ae0ede2a");
        //开启debug模式，方便定位错误，具体错误检查方式可以查看http://dev.umeng.com/social/android/quick-integration的报错必看
        // 正式发布，请关闭该模式
//        Config.DEBUG = true;
        SobotApi.initSobotSDK(this, Urls.ZHICHI_KEY, "");
        ApplicationInfo appInfo = null;
        String msg = "ouye";
        try {
            appInfo = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            msg = appInfo.metaData.getString("UMENG_CHANNEL");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        UMConfigure.init(this, "58c8df3807fe653c4200073e", msg, UMConfigure.DEVICE_TYPE_PHONE, "");
//        SobotApi.setNotificationFlag(this, true, R.mipmap.ic_launcher, R.mipmap.ic_launcher);
    }

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * 配置分享平台参数
     */ {
        //微信
        PlatformConfig.setWeixin("wx3360e320aef00538", "127c1be79b70a2d7d2be55573f210107");
        //QQ
        PlatformConfig.setQQZone("1106044214", "UdTwoFWFDntSc7Aj");
        //新浪微博
        PlatformConfig.setSinaWeibo("3809022358", "3b09b44dad6914751477a9be8365d109", "http://open.weibo.com/apps/3809022358/privilege/oauth");
    }
}
