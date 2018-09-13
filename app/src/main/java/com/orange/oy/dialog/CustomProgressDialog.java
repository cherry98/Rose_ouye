package com.orange.oy.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;


/**
 * @author http://blog.csdn.net/finddreams
 */
public class CustomProgressDialog extends ProgressDialog {
    private AnimationDrawable mAnimation;
    private Context mContext;
    private ImageView mImageView;
    private String mLoadingTip;
    private TextView mLoadingTv;

    private CustomProgressDialog(Context context, String content, int theme) {
        super(context, theme);
        this.mContext = context;
        this.mLoadingTip = content;
//        setCanceledOnTouchOutside(true);
    }

    private CustomProgressDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
        this.mLoadingTip = context.getResources().getString(R.string.network_dialog_message);
//        setCanceledOnTouchOutside(true);
    }

    private static CustomProgressDialog intance;

    public static CustomProgressDialog showProgressDialog(Context context, String msg) {
        if (intance != null && intance.isShowing()) {
            try {
                intance.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            intance = null;
        }
        if (msg == null) {
            intance = new CustomProgressDialog(context, R.style.loading_dialog);
        } else {
            intance = new CustomProgressDialog(context, msg, R.style.loading_dialog);
        }
        try {
            intance.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intance;
    }

    public static void Dissmiss() {
        if (intance != null) {
            try {
                intance.dismiss();
            } catch (Exception e) {
            }
            intance = null;
        }
    }

    public void setText(String content) {
        this.mLoadingTip = content;
        mLoadingTv.setText(content);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setCancelable(false);
        initView();
        initData();
    }

    private void initData() {
        // mImageView.setBackgroundResource(mResid);
        // 通过ImageView对象拿到背景显示的AnimationDrawable
        mAnimation = (AnimationDrawable) mImageView.getBackground();
        // 为了防止在onCreate方法中只显示第一帧的解决方案之一
        mImageView.post(new Runnable() {
            @Override
            public void run() {
                mAnimation.start();

            }
        });
        mLoadingTv.setText(mLoadingTip);
    }

    public void setContent(String str) {
        mLoadingTv.setText(str);
    }

    private void initView() {
        setContentView(R.layout.progress_dialog);
        mLoadingTv = (TextView) findViewById(R.id.id_tv_loadingmsg);
        mImageView = (ImageView) findViewById(R.id.loadingImageView);
        if (Build.VERSION.SDK_INT >= 20) {
            mLoadingTv.setTextColor(Color.argb(159, 0, 0, 0));
        }
    }

	/*
     * @Override public void onWindowFocusChanged(boolean hasFocus) {  mAnimation.start();
	 * super.onWindowFocusChanged(hasFocus); }
	 */
}
