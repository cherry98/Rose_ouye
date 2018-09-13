package com.orange.oy.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.allinterface.DiscussCallback;
import com.orange.oy.base.Tools;


/**
 * Used to task execute back dialog.
 * 评论的dialog
 */
public class DiscussPopDialog extends PopupWindow {

    private View conentView;
    //private LinearLayout main;
    private EditText ed_discuss;
    private TextView sure;

    private Activity context;
    private Window window;
    private DiscussCallback discussCallback;

    /**
     * @param context perent Activity
     */
    public DiscussPopDialog(final Activity context, DiscussCallback discussCallback) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.pop_discuss, null);
        this.discussCallback = discussCallback;

        this.context = context;
        this.window = context.getWindow();
        setPopWindow();

        setView(context);
        showPopupWindow();
    }


    /**
     * 显示popupWindow
     */
    public void showPopupWindow() {
        if (!this.isShowing()) {
//            setBackground();
            // 以下拉方式显示popupwindow 位置覆盖父控件左下定点对齐
//            this.showAsDropDown(view, 0, 2);
            //底部显示
            this.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            this.showAtLocation(context.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
        } else {
            popDismiss();
        }
        setBackground();
    }


    /**
     * Pop 关闭
     */
    private void popDismiss() {
        super.dismiss();
    }

    /**
     * 设置PopWindoe相关属性
     */
    private void setPopWindow() {

        float h = context.getWindowManager().getDefaultDisplay().getHeight();
        float w = context.getWindowManager().getDefaultDisplay().getWidth();
        float ratioWidth = w / 720;
        float ratioHeight = h / 1080;
        float ratioMetrics = Math.min(ratioWidth, ratioHeight);

        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth((int) w);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight((int) (104 * ratioMetrics));
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.popupAnimation);
    }

    /**
     * 设置背景透明度
     */
    private void setBackground() {
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.7f;
        window.setAttributes(lp);
        //消失的时候设置窗体背景变亮
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.alpha = 1.0f;
                window.setAttributes(lp);
            }
        });
    }

    /**
     * 设置展示View以及初始化适配器
     */
    private void setView(Activity activity) {
        // main = (LinearLayout) conentView.findViewById(R.id.main);
        ed_discuss = (EditText) conentView
                .findViewById(R.id.ed_discuss);

        ed_discuss.setFocusable(true);
        ed_discuss.setFocusableInTouchMode(true);
        ed_discuss.requestFocus();
        //自动弹出软键盘
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);


        sure = (TextView) conentView
                .findViewById(R.id.tv_sure);
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check()) {
                    discussCallback.onDiscuss(ed_discuss.getText().toString());
                    popDismiss();
                }
            }
        });
    }

    private boolean check() {
        if (TextUtils.isEmpty(ed_discuss.getText().toString())) {
            Tools.d("请输入回复");
            return false;
        }
        return true;
    }
}