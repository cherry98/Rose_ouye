package com.orange.oy.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;

/**
 * Created by Lenovo on 2018/3/29.
 * 审核不通过网点原因展开
 */

public class SpreadTextView extends LinearLayout implements View.OnClickListener {
    /**
     * default text show max lines
     */
    private static final int DEFAULT_MAX_LINE_COUNT = 2;

    private static final int COLLAPSIBLE_STATE_NONE = 0;
    private static final int COLLAPSIBLE_STATE_SHRINKUP = 1;
    private static final int COLLAPSIBLE_STATE_SPREAD = 2;

    private TextView desc;

    private int mState;
    private boolean flag;
    private ImageView desc_img, desc_xuxian;

    public SpreadTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = inflate(context, R.layout.item_spreadtext, this);
        view.setPadding(0, -1, 0, 0);
        desc = (TextView) view.findViewById(R.id.desc_tv);
        desc_img = (ImageView) findViewById(R.id.desc_img);
        desc_xuxian = (ImageView) findViewById(R.id.desc_xuxian);
        findViewById(R.id.desc_layout).setOnClickListener(this);
    }

    public SpreadTextView(Context context) {
        this(context, null);
    }

    private int lineCount;

    public void setDesc(CharSequence charSequence) {
        desc.setText(charSequence, TextView.BufferType.NORMAL);
        mState = COLLAPSIBLE_STATE_SPREAD;
        post(new Runnable() {
            @Override
            public void run() {
                lineCount = desc.getLineCount();
                carry();
            }
        });
    }

    public void setDescType2(CharSequence charSequence, boolean flag) {
        desc.setText(charSequence, TextView.BufferType.NORMAL);
        if (flag) {
            desc_xuxian.setVisibility(VISIBLE);
        }else {
            desc_xuxian.setVisibility(GONE);
        }
        mState = COLLAPSIBLE_STATE_SPREAD;
        post(new Runnable() {
            @Override
            public void run() {
                lineCount = desc.getLineCount();
                carry();
            }
        });
    }

    /**
     * 设置默认显示隐藏
     *
     * @param b
     */
    public void setIsup(boolean b) {
        if (b) {
            mState = COLLAPSIBLE_STATE_SHRINKUP;
        }
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
                desc.setMaxLines(DEFAULT_MAX_LINE_COUNT + 1);
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
                mState = COLLAPSIBLE_STATE_SHRINKUP;
                desc_img.setImageResource(R.mipmap.spread_button_down);
                desc_img.setVisibility(View.VISIBLE);
            } else if (mState == COLLAPSIBLE_STATE_SHRINKUP) {
                desc.setMaxLines(Integer.MAX_VALUE);
                mState = COLLAPSIBLE_STATE_SPREAD;
                desc_img.setImageResource(R.mipmap.spread_button_up);
                desc_img.setVisibility(View.VISIBLE);
            }
        }
    }


}
