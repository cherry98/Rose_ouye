package com.orange.oy.view;

import android.content.Context;
import android.util.AttributeSet;

import com.orange.oy.base.Tools;
import com.tencent.smtt.sdk.WebView;


/**
 * Created by Administrator on 2018/9/3.
 */

public class MyWebView extends WebView {
    public interface OnMyScrollChanged {
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }

    private OnMyScrollChanged onMyScrollChanged;

    public void setOnMyScrollChanged(OnMyScrollChanged onMyScrollChanged) {
        this.onMyScrollChanged = onMyScrollChanged;
    }

    public MyWebView(Context context) {
        super(context);
    }

    public MyWebView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public MyWebView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public MyWebView(Context context, AttributeSet attributeSet, int i, boolean b) {
        super(context, attributeSet, i, b);
    }

    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onMyScrollChanged != null) {
            onMyScrollChanged.onScrollChanged(l, t, oldl, oldt);
        }
    }
}
