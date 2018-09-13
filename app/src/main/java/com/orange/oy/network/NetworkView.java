package com.orange.oy.network;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;

/**
 * Created by Administrator on 2018/5/16.
 * 网络请求展示图
 */

public class NetworkView extends LinearLayout {
    public NetworkView(Context context) {
        this(context, null, 0);
    }

    public NetworkView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NetworkView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private ImageView lin_Nodata_img;
    private TextView lin_Nodata_prompt;

    private void initView() {
        Tools.loadLayout(this, R.layout.view_network);
        lin_Nodata_img = (ImageView) findViewById(R.id.lin_Nodata_img);
        lin_Nodata_prompt = (TextView) findViewById(R.id.lin_Nodata_prompt);
    }

    /**
     * 请求失败
     */
    public void NoNetwork() {
        lin_Nodata_img.setImageResource(R.mipmap.grrw_image2);
        lin_Nodata_prompt.setText("网络连接中断，\n请检查下您的网络吧！");
    }

    public void NoNetwork(String msg) {
        lin_Nodata_img.setImageResource(R.mipmap.grrw_image2);
        lin_Nodata_prompt.setText(msg);
    }

    /**
     * 自定义图片和展示文字
     *
     * @param resId
     * @param msg
     */
    public void SettingMSG(int resId, String msg) {
        lin_Nodata_img.setImageResource(resId);
        lin_Nodata_prompt.setText(msg);
    }

    /**
     * 未搜索到内容
     */
    public void NoSearch() {
        lin_Nodata_img.setImageResource(R.mipmap.grrw_image);
        lin_Nodata_prompt.setText("没有与您搜索内容相匹配的信息");
    }

    public void NoSearch(String msg) {
        lin_Nodata_img.setImageResource(R.mipmap.grrw_image);
        lin_Nodata_prompt.setText(msg);
    }

    public ImageView getLin_Nodata_img() {
        return lin_Nodata_img;
    }

    public TextView getLin_Nodata_prompt() {
        return lin_Nodata_prompt;
    }
}
