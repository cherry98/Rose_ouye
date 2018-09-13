package com.orange.oy.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.orange.oy.R;
import com.orange.oy.base.Tools;

/**
 * Created by Administrator on 2018/3/29.
 */

public class TaskcheckImageView extends android.support.v7.widget.AppCompatImageView implements View.OnClickListener {


    private boolean checked;

    public TaskcheckImageView(Context context) {
        super(context);
        setOnClickListener(this);
    }

    public TaskcheckImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this);
    }

    public TaskcheckImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(this);
    }

    public void isSelect(boolean isSelect) {
        if (isSelect) {
            setOnClickListener(this);
            if (layout != null) {
                layout.setOnClickListener(this);
            }
        } else {
            setOnClickListener(null);
            if (layout != null) {
                layout.setOnClickListener(null);
            }
        }
    }

    private LinearLayout layout;

    public void settingLayout(LinearLayout layout, boolean onlyclick) {
        this.layout = layout;
        layout.setOnClickListener(this);
        if (onlyclick) {
            setOnClickListener(null);
        }
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        if (checked) {
            setImageResource(R.mipmap.checkbox2311);
            if (layout != null) {
                layout.setBackgroundResource(R.drawable.questionradio_s_bg);
            }
        } else {
            setImageResource(R.mipmap.checkbox1311);
            if (layout != null) {
                layout.setBackgroundResource(R.drawable.questionradio_nos_bg);
            }
        }
    }

    public boolean isChecked() {
        return checked;
    }

    private OnCheckedChangeListener onCheckedChangeListener;

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    @Override
    public void onClick(View v) {
        Tools.d("click:" + (v instanceof TaskcheckImageView));
        setChecked(!checked);
        if (onCheckedChangeListener != null) {
            onCheckedChangeListener.onCheckedChanged(this, checked);
        }
    }

    interface OnCheckedChangeListener {
        void onCheckedChanged(TaskcheckImageView imageView, boolean ischecked);
    }
}
