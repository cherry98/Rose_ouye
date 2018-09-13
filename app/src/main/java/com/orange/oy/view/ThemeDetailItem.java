package com.orange.oy.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Administrator on 2018/7/19.
 */

public class ThemeDetailItem extends GifImageView {
    private double mHeightRatio;

    public ThemeDetailItem(Context context) {
        super(context);
    }

    public ThemeDetailItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ThemeDetailItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setHeightRatio(double ratio) {
        if (ratio != mHeightRatio) {
            mHeightRatio = ratio;
            requestLayout();
        }
    }

    public double getHeightRatio() {
        return mHeightRatio;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mHeightRatio > 0) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = (int) (width * mHeightRatio);
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
