package com.orange.oy.view;

import android.content.Context;
import android.widget.LinearLayout;

import com.orange.oy.R;
import com.orange.oy.base.Tools;

public class FindLeftlistviewItem extends LinearLayout {
    public FindLeftlistviewItem(Context context) {
        super(context);
        Tools.loadLayout(this, R.layout.view_item_findleft);
    }
}
