package com.orange.oy.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.orange.oy.activity.experience.ExperiencePointActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.Tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by xiedongyan on 2017/12/27.
 */

public class TimerService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Timer timer = null;
    private Intent timeIntent = null;

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        timeIntent = new Intent();
        //定时器发送广播
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //发送广播
                sendTimeChangedBroadcast();
            }
        }, 1000, 1000);
    }

    /**
     * 发送广播，通知UI层时间已改变
     */
    private void sendTimeChangedBroadcast() {
        try {
            timeIntent.putExtra("time", getTime());
            timeIntent.setAction(ExperiencePointActivity.TIME_CHANGED_ACTION);
            //发送广播，通知UI层时间改变了
            sendBroadcast(timeIntent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取最新时间
     *
     * @return
     */
    private String getTime() throws ParseException {
        String time;
        time = getsubtract(AppInfo.getStartTime(this));
        return time;
    }

    //时间相减  得到计时时间
    public String getsubtract(String starttime) throws ParseException {
        SimpleDateFormat myFormatter = new SimpleDateFormat("hh:mm:ss");
        String newtime = Tools.gettime();
        Date date = myFormatter.parse(newtime);
        Date mydate = myFormatter.parse(starttime);
        int sec = (int) ((date.getTime() - mydate.getTime()) / 1000);
        return sec + "";
    }

    @Override
    public ComponentName startService(Intent service) {
        Tools.d("TimeService->startService");
        return super.startService(service);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Tools.d("TimeService->onDestroy");
    }
}
