package com.orange.oy.view;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.activity.FinishpackageActivity;
import com.orange.oy.base.Tools;

public class FinishpackageView extends LinearLayout implements View.OnClickListener {
    public FinishpackageView(Context context, String packageNmae) {
        super(context);
        Tools.loadLayout(this, R.layout.view_finishdt_package);
        init();
        viewfdt_package.setText(packageNmae);
    }

    private TextView viewfdt_package;
    private LinearLayout viewfdt_package_category_layout;
    private ImageView viewfdt_package_right;
    private View viewfdt_package_note_layout, viewfdt_package_layout;
    private TextView viewfdt_package_note;

    private void init() {
        viewfdt_package_note_layout = findViewById(R.id.viewfdt_package_note_layout);
        viewfdt_package_note = (TextView) findViewById(R.id.viewfdt_package_note);
        viewfdt_package = (TextView) findViewById(R.id.viewfdt_package);
        viewfdt_package_right = (ImageView) findViewById(R.id.viewfdt_package_right);
        viewfdt_package_category_layout = (LinearLayout) findViewById(R.id.viewfdt_package_category_layout);
        viewfdt_package_layout = findViewById(R.id.viewfdt_package_layout);
        setIsClick(true);
    }

    public void settingNote(String note) {
        viewfdt_package_note_layout.setVisibility(VISIBLE);
        viewfdt_package_note.setVisibility(VISIBLE);
        viewfdt_package_note.setText(note);
    }

    public void setIsClick(boolean isClick) {
        viewfdt_package_layout.setOnClickListener((isClick) ? this : null);
        if (!isClick) {
            viewfdt_package_layout.setVisibility(GONE);
        } else {
            viewfdt_package_layout.setVisibility(VISIBLE);
        }
    }

    /**
     * 添加子布局
     *
     * @param view 要添加的布局
     */
    public void addView(View view) {
        viewfdt_package_category_layout.addView(view, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup
                .LayoutParams.WRAP_CONTENT));
    }

    private Intent intent;

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    private String pid;

    public void setPackid(String packid) {
        pid = packid;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.viewfdt_package_layout: {
                if (this.getTag() == null) {
                    Tools.showToast(getContext(), "打开失败");
                    return;
                }
                intent.setClass(getContext(), FinishpackageActivity.class);
                intent.putExtra("pid", pid);
                intent.putExtra("data", this.getTag() + "");
                getContext().startActivity(intent);
//                if (viewfdt_package_category_layout.getVisibility() == VISIBLE) {
//                    int count = viewfdt_package_category_layout.getChildCount();
//                    View view;
//                    for (int i = 0; i < count; i++) {
//                        view = viewfdt_package_category_layout.getChildAt(i);
//                        if (view instanceof FinishcategoryView) {
//                            ((FinishcategoryView) view).hideView();
//                        }
//                    }
//                    viewfdt_package_category_layout.setVisibility(GONE);
//                    viewfdt_package_right.setImageResource(R.mipmap.text_spread);
//                } else {
//                    viewfdt_package_category_layout.setVisibility(VISIBLE);
//                    viewfdt_package_right.setImageResource(R.mipmap.text_shrinkup);
//                }

            }
            break;
        }
    }
}
