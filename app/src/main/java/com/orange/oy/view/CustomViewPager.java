package com.orange.oy.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.orange.oy.base.Tools;

/**
 * Created by Administrator on 2018/8/7.
 * <p>
 * 重写onInterceptTouchEvent事件用于catch系统级别异常
 */

public class CustomViewPager extends ViewPager {

    public CustomViewPager(Context context) {
        this(context, null);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
//            e.printStackTrace();
//            Tools.d(e.getMessage() + "");
        } catch (Exception e) {
//            e.printStackTrace();
//            Tools.d(e.getMessage() + "");
        }
        return false;
    }
}