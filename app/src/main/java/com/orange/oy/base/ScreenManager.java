package com.orange.oy.base;

import android.app.Activity;
import android.content.Context;

import com.orange.oy.activity.TaskitemDetailActivity;
import com.orange.oy.network.NetworkConnection;

import java.util.Stack;

public class ScreenManager {
    private static Stack<Activity> activityStack;
    private static ScreenManager instance;

    private ScreenManager() {
    }

    public static ScreenManager getScreenManager() {
        if (instance == null) {
            instance = new ScreenManager();
        }
        return instance;
    }

    /**
     * add Activity 添加Activity到栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * get current Activity 获取当前Activity（栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity（栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null && activityStack != null) {
            activityStack.remove(activity);
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        int size = activityStack.size();
        for (int i = 0; i < size; i++) {
            Activity activity = activityStack.get(i);
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
                size--;
                i--;
            }
        }
    }

    /**
     * 结束所有Activity
     */
    private static void finishAllActivity() throws Exception {
        if (activityStack == null) return;
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public static void AppExit(Context context) {
        try {
            finishAllActivity();
            NetworkConnection.stopNetwork();
        } catch (Exception e) {
        }
    }

    /**
     * 退出指定页面
     */
    public static void AppExitTaskitemDetailActivity() {
        try {
            if (activityStack == null) return;
            for (int i = 0, size = activityStack.size(); i < size; i++) {
                if (null != activityStack.get(i) && activityStack.get(i) instanceof TaskitemDetailActivity) {
                    activityStack.get(i).finish();
                    activityStack.remove(i);
                    i--;
                    size--;
                }
            }
        } catch (Exception e) {
        }
    }
}
