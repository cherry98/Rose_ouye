package com.orange.oy.dialog;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.ViewGroup.LayoutParams;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.orange.oy.R;

/**
 * 使用popupwindow定义弹出菜单
 */
public class MyDialog extends PopupWindow {
    private Activity context;

    public MyDialog(Activity context, View mMenuView, boolean isTouch) {
        this.context = context;
        this.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        this.setContentView(mMenuView);
        this.setWidth(LayoutParams.MATCH_PARENT);
        this.setHeight(LayoutParams.WRAP_CONTENT);
        this.setOutsideTouchable(isTouch);
        this.setFocusable(true);
        this.setTouchable(true);
        this.setAnimationStyle(R.style.selecterStyle);
        this.setBackgroundDrawable(new BitmapDrawable());
        backgroundAlpha(0.4f);
        this.setOnDismissListener(new OnDismissListener() {
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
    }

    public MyDialog(Activity context, View mMenuView, boolean isTouch, int AnimationStyle) {
        this.context = context;
        this.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        this.setContentView(mMenuView);
        this.setWidth(LayoutParams.MATCH_PARENT);
        this.setHeight(LayoutParams.WRAP_CONTENT);
        this.setOutsideTouchable(isTouch);
        this.setFocusable(true);
        this.setTouchable(true);
        if (AnimationStyle != 0)
            this.setAnimationStyle(AnimationStyle);
        this.setBackgroundDrawable(new BitmapDrawable());
        backgroundAlpha(0.4f);
        this.setOnDismissListener(new OnDismissListener() {
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
    }

    public void setMyDialogWidth(int width) {
        this.setWidth(width);
    }

    public void backgroundAlpha(float bgAlpha) {
        if (context == null) return;
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        context.getWindow().setAttributes(lp);
    }
}
