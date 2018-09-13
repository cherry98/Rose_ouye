package com.orange.oy.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2018/7/25.
 */

public class AutoTextView extends LinearLayout {
    private final int SPEED = 50;
    private TextView textView1, textView2;
    private String mText;
    private boolean isSetting;
    private int moveSpeed = 10;

    public AutoTextView(Context context) {
        super(context);
        initView();
    }

    public AutoTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public AutoTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        isSetting = false;
        myHandler = new MyHandler();
        this.setOrientation(LinearLayout.HORIZONTAL);
        this.setGravity(Gravity.CENTER_VERTICAL);
        textView1 = new TextView(getContext());
        textView1.setTextSize(12);
        textView1.setTextColor(Color.WHITE);
        textView1.setLines(1);
        addView(textView1, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView2 = new TextView(getContext());
        textView2.setTextSize(12);
        textView2.setTextColor(Color.WHITE);
        textView2.setLines(1);
        addView(textView2, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void setText(String str) {
        mText = str;
        textView1.setText(mText);
        settingView();
    }

    public String getText() {
        return mText;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        settingView();
    }

    private void settingView() {
        if (!isSetting && mText != null && getWidth() > 0) {
            isSetting = true;
            int textW = getTextWidth(textView1);
            moveSpeed = textW / mText.length() / 10;
            if (moveSpeed < 1) {
                moveSpeed = 1;
            }
            if (textW >= getWidth()) {
                int rightM = getRightMargin(textW / mText.length() * 2);
                LinearLayout.LayoutParams lp = (LayoutParams) textView1.getLayoutParams();
                lp.width = textW;
                lp.rightMargin = rightM;
                textView1.setLayoutParams(lp);
                lp = (LayoutParams) textView2.getLayoutParams();
                lp.width = textW;
                lp.rightMargin = rightM;
                textView2.setLayoutParams(lp);
            } else {
                int rightM = getWidth() - textW;
                if (rightM < textW / mText.length() * 2) {
                    rightM = textW / mText.length() * 2;
                }
                rightM = getRightMargin(rightM);
                LinearLayout.LayoutParams lp = (LayoutParams) textView1.getLayoutParams();
                lp.width = textW;
                lp.rightMargin = rightM;
                textView1.setLayoutParams(lp);
                lp = (LayoutParams) textView2.getLayoutParams();
                lp.width = textW;
                lp.rightMargin = rightM;
                textView2.setLayoutParams(lp);
            }
            textView2.setText(mText);
            if (myHandler != null)
                myHandler.sendEmptyMessageDelayed(0, SPEED);
        }
    }

    private int getRightMargin(int rightmargin) {
        if (can(rightmargin)) {
            return rightmargin;
        } else {
            return getRightMargin(rightmargin + 1);
        }
    }

    private boolean can(int num) {
        return num % moveSpeed == 0;
    }

    public void startScroll() {
        if (myHandler == null) {
            myHandler = new MyHandler();
        }
        myHandler.sendEmptyMessageDelayed(0, SPEED);
    }

    public void stopScroll() {
        if (myHandler != null)
            myHandler.removeMessages(0);
        myHandler = null;
    }

    /**
     * 获取文本宽度
     */
    private int getTextWidth(TextView textView) {
        Paint paint = textView.getPaint();
        String str = textView.getText().toString();
        return (int) (paint.measureText(str));
    }

    private MyHandler myHandler = null;

    private class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            if (isSetting) {
                View view1 = getChildAt(0);
                View view2 = getChildAt(1);
                LinearLayout.LayoutParams lp = (LayoutParams) view1.getLayoutParams();
                lp.leftMargin = (int) (view1.getX() - moveSpeed);
                view1.setLayoutParams(lp);
                if (view2.getX() <= 0) {
                    removeViewAt(0);
                    LinearLayout.LayoutParams lp1 = (LayoutParams) view1.getLayoutParams();
                    lp1.leftMargin = 0;
                    view1.setLayoutParams(lp1);
                    addView(view1);
                }
                if (myHandler != null) {
                    myHandler.sendEmptyMessageDelayed(0, SPEED);
                }
            }
        }
    }
}
