package com.orange.oy.view;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;


/**
 * 属性选择外壳
 */
public class AttributeShellView extends LinearLayout {
    private View attributeshell_left, attributeshell_right;
    private LinearLayout attributshell_layout;
    private TextView textView;

    public AttributeShellView(Context context) {
        super(context);
        Tools.loadLayout(this, R.layout.view_attributeshell);
        attributeshell_left = findViewById(R.id.attributeshell_left);
        attributeshell_right = findViewById(R.id.attributeshell_right);
        attributshell_layout = (LinearLayout) findViewById(R.id.attributshell_layout);
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public void addTextView() {
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.weight = 1;
        attributshell_layout.addView(textView, 1, lp);
    }

    public TextView getTextView() {
        return textView;
    }

    public void settingTextBg(int resid) {
        if (textView != null) {
            if (resid == 0) {
                textView.setTextColor(getResources().getColor(R.color.colorPrimaryDark2));
            } else {
                textView.setTextColor(Color.WHITE);
            }
            textView.setBackgroundResource(resid);
        }
    }

    public void hideBg() {
        attributeshell_left.setVisibility(INVISIBLE);
        attributeshell_right.setVisibility(INVISIBLE);
    }

    public void showBg() {
        attributeshell_left.setVisibility(View.VISIBLE);
        attributeshell_right.setVisibility(View.VISIBLE);
    }
}
