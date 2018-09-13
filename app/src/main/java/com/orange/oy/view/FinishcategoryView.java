package com.orange.oy.view;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;

public class FinishcategoryView extends LinearLayout implements View.OnClickListener {
    public FinishcategoryView(Context context, String categoryName) {
        super(context);
        Tools.loadLayout(this, R.layout.view_finishdt_category);
        init();
        viewfdt_category.setText(categoryName);
    }

    private View viewfdt_category_line;
    private TextView viewfdt_category;
    private LinearLayout viewfdt_category_other_layout;
    private ImageView viewfdt_category_right;

    private void init() {
        viewfdt_category_line = findViewById(R.id.viewfdt_category_line);
        viewfdt_category = (TextView) findViewById(R.id.viewfdt_category);
        viewfdt_category_right = (ImageView) findViewById(R.id.viewfdt_category_right);
        viewfdt_category_other_layout = (LinearLayout) findViewById(R.id.viewfdt_category_other_layout);
        findViewById(R.id.viewfdt_category_layout).setOnClickListener(this);
    }

    public void addView(View view) {
        viewfdt_category_other_layout.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    public void hideView() {
        viewfdt_category_other_layout.setVisibility(GONE);
        viewfdt_category_right.setImageResource(R.mipmap.text_spread);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.viewfdt_category_layout: {
                if (viewfdt_category_other_layout.getVisibility() == VISIBLE) {
                    viewfdt_category_other_layout.setVisibility(GONE);
                    viewfdt_category_right.setImageResource(R.mipmap.text_spread);
                } else {
                    viewfdt_category_other_layout.setVisibility(VISIBLE);
                    viewfdt_category_right.setImageResource(R.mipmap.text_shrinkup);
                }
            }
            break;
        }
    }
}
