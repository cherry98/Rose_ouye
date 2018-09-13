package com.orange.oy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.orange.oy.activity.experience.ExperiencePointActivity;
import com.orange.oy.base.Tools;

/**
 * Created by xiedongyan on 2017/12/27.
 */

public class ExperienceTimeReceiver extends BroadcastReceiver {
    private ExperiencePointActivity activity = new ExperiencePointActivity();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ExperiencePointActivity.TIME_CHANGED_ACTION.equals(action)) {
            String strtime = intent.getStringExtra("time");
            activity.recordLength = Tools.StringToInt(strtime);
        }
    }
}
