package com.orange.oy.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.util.ImageLoader;

/**
 * Created by Lenovo on 2018/3/14.
 */

public class MyImageView extends FrameLayout {
    private ImageView mImageView = null;
    private TextView mTextView = null;
    private ImageLoader imageLoader;

    public MyImageView(Context context) {
        this(context, null);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        this.setOrientation(LinearLayout.VERTICAL);//设置垂直排序
//        this.setGravity(Gravity.CENTER);//设置居中
        if (mImageView == null) {
            mImageView = new ImageView(context);
        }
        if (mTextView == null) {
            mTextView = new TextView(context);
        }
        imageLoader = new ImageLoader(context);
        init();
    }

    public ImageView getmImageView() {
        return mImageView;
    }

    /**
     * 初始化状态
     */
    private void init() {
        this.setText("");
        mTextView.setGravity(Gravity.CENTER);//字体居中
        this.mTextView.setTextColor(getResources().getColor(R.color.app_background2));
        this.mTextView.setTextSize(18);
        addView(mImageView);//将图片控件加入到布局中
        addView(mTextView);//将文字控件加入到布局中
    }

    public void setText(String text) {
        this.mTextView.setText(text);
    }

    public void setAdjustViewBounds() {
        mImageView.setAdjustViewBounds(true);
    }

    public void setScaleType() {
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    public void setScaleType2() {
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }


    public void setImageBitmap(String path) {
        imageLoader.setShowWH(200).DisplayImage(path, mImageView, -2);
    }

    public void setImageBitmap2(String path) {
        mImageView.setImageBitmap(Tools.getBitmap2(path, 200, 200));
    }

    public void setImageBitmap3(String url) {
        setScaleType();
        imageLoader.setShowWH(200).DisplayImage(url, mImageView, -2);
    }

    public void setImageBitmap4(String url) {
        setScaleType2();
        if (url.startsWith("http") || url.startsWith("https")) {
            imageLoader.setShowWH(200).DisplayImage(url + "?x-oss-process=image/resize,l_250", mImageView, -2);
        } else {
            imageLoader.setShowWH(200).DisplayImage(url, mImageView, -2);
        }
    }

    public void setmImageThumbnail(String path) {
        mImageView.setImageBitmap(Tools.createVideoThumbnail(path, 1));
    }


    public void setImageResource(int imgId) {
        mImageView.setImageResource(imgId);
    }
}
