package com.orange.oy.dialog;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.ScreenManager;

import cn.jpush.android.api.JPushInterface;

/**
 * 悬浮窗dialog
 */
public class SuspendDialog {
    //定义浮动窗口布局
    private RelativeLayout mFloatLayout;
    //创建浮动窗口设置布局参数的对象
    private WindowManager mWindowManager;
    private Context context;

    public void createFloatView(Context mContext) {
        this.context = mContext;
        int type;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            type = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wmParams.type = type;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        wmParams.gravity = Gravity.CENTER;
        wmParams.x = 0;
        wmParams.y = 0;
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = (int) context.getResources().getDimension(R.dimen.dialog_logoff_height);
        LayoutInflater inflater = LayoutInflater.from(context);
        mFloatLayout = (RelativeLayout) inflater.inflate(R.layout.dialog_logoff, null);
        mWindowManager.addView(mFloatLayout, wmParams);
        View mFloatView = mFloatLayout.findViewById(R.id.logoff_button);
        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        mFloatView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mFloatLayout != null) {
                    mWindowManager.removeView(mFloatLayout);
                }
            }
        });
    }

}
