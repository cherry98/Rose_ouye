package com.orange.oy.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;

/**
 * TextView展开收起的自定义View
 */
public class CollapsibleTextView extends LinearLayout implements
        View.OnClickListener {

    /**
     * default text show max lines
     */
    private static final int DEFAULT_MAX_LINE_COUNT = 3;

    private static final int COLLAPSIBLE_STATE_NONE = 0;
    private static final int COLLAPSIBLE_STATE_SHRINKUP = 1;
    private static final int COLLAPSIBLE_STATE_SPREAD = 2;

    private TextView desc;
    private TextView descOp;

    private String shrinkup;
    private String spread;
    private int mState;
    private boolean flag;
    private boolean isShow;//是否显示箭头的样式
    private ImageView desc_img;

    public CollapsibleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        shrinkup = "收起";
        spread = "展开";
        View view = inflate(context, R.layout.textview_collapsible, this);
        view.setPadding(0, -1, 0, 0);
        desc = (TextView) view.findViewById(R.id.desc_tv);
        descOp = (TextView) view.findViewById(R.id.desc_op_tv);
        desc_img = (ImageView) findViewById(R.id.desc_img);
        findViewById(R.id.desc_layout).setOnClickListener(this);
    }

    public CollapsibleTextView(Context context) {
        this(context, null);
    }

    private int lineCount;

    public void setDesc(CharSequence charSequence, TextView.BufferType bufferType) {
        desc.setText(charSequence, bufferType);
        mState = COLLAPSIBLE_STATE_SPREAD;
        post(new Runnable() {
            @Override
            public void run() {
                lineCount = desc.getLineCount();
                carry();
            }
        });
    }

    public void setTextColor(CharSequence charSequence, TextView.BufferType bufferType, int color,int textsize) {
        desc.setText(charSequence, bufferType);
        desc.setTextColor(color);
        desc.setTextSize(TypedValue.COMPLEX_UNIT_SP,textsize);
        mState = COLLAPSIBLE_STATE_SPREAD;
        post(new Runnable() {
            @Override
            public void run() {
                lineCount = desc.getLineCount();
                carry();
            }
        });
    }

    public void setDesc(CharSequence charSequence, TextView.BufferType bufferType, boolean isShow) {
        this.isShow = isShow;
        desc.setText(charSequence, bufferType);
        mState = COLLAPSIBLE_STATE_SPREAD;
        post(new Runnable() {
            @Override
            public void run() {
                lineCount = desc.getLineCount();
                carry();
            }
        });
    }

    @Override
    public void onClick(View v) {
        flag = false;
        carry();
    }

    public void carry() {
        if (!flag) {
            flag = true;
            if (lineCount <= DEFAULT_MAX_LINE_COUNT) {
                mState = COLLAPSIBLE_STATE_NONE;
                descOp.setVisibility(View.GONE);
                desc.setMaxLines(DEFAULT_MAX_LINE_COUNT + 1);
                if (isShow) {
                    desc_img.setVisibility(View.GONE);
                }
            } else {
                post(new InnerRunnable());
            }
        }
    }

    class InnerRunnable implements Runnable {
        @Override
        public void run() {
            if (mState == COLLAPSIBLE_STATE_SPREAD) {
                desc.setMaxLines(DEFAULT_MAX_LINE_COUNT);
                descOp.setVisibility(View.VISIBLE);
                descOp.setText(spread);
                mState = COLLAPSIBLE_STATE_SHRINKUP;
                if (isShow) {
                    desc_img.setImageResource(R.mipmap.text_spread);
                    desc_img.setVisibility(View.VISIBLE);
                }
            } else if (mState == COLLAPSIBLE_STATE_SHRINKUP) {
                desc.setMaxLines(Integer.MAX_VALUE);
                descOp.setVisibility(View.VISIBLE);
                descOp.setText(shrinkup);
                mState = COLLAPSIBLE_STATE_SPREAD;
                if (isShow) {
                    desc_img.setImageResource(R.mipmap.text_shrinkup);
                    desc_img.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
