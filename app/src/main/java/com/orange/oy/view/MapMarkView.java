package com.orange.oy.view;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;

/**
 * Created by Zhangpengfei on 2018/4/13.
 * 地图标记view
 */

public class MapMarkView extends LinearLayout {
    private TextView view_mapmark_money;
    private TextView view_mapmark_bottom;

    public MapMarkView(Context context) {
        super(context);
    }

    /**
     * @param context 上下文
     * @param isSmall 是否为小图标
     */
    public MapMarkView(Context context, boolean isSmall) {
        this(context);
        if (isSmall) {
            Tools.loadLayout(this, R.layout.view_mapmark_small);
        } else {
            Tools.loadLayout(this, R.layout.view_mapmark_big);
            view_mapmark_money = (TextView) findViewById(R.id.view_mapmark_money);
            view_mapmark_bottom = (TextView) findViewById(R.id.view_mapmark_bottom);
        }
    }

    public void setMoney(String money) {
        if (view_mapmark_money != null) {
            view_mapmark_money.setText(money);
            view_mapmark_bottom.setTextSize(16);
            view_mapmark_bottom.setText("¥");
        }
    }

    public void setNumber(String number) {
        if (view_mapmark_money != null) {
            view_mapmark_money.setText(number);
            view_mapmark_bottom.setTextSize(12);
            view_mapmark_bottom.setText("个");
        }
    }
}
