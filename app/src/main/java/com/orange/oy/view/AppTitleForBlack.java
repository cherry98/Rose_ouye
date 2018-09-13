package com.orange.oy.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;


public class AppTitleForBlack extends RelativeLayout implements View.OnClickListener {
    private TextView name;
    private View back;
    private ImageView search;
    private OnBackClickForAppTitle listener;
    private OnSearchClickForAppTitle searchListener;

    public void onClick(View v) {
        if (v.getId() == R.id.titleforblack_back && listener != null) {
            listener.onBack();
        } else if (v.getId() == R.id.titleforblack_search && searchListener != null) {
            searchListener.onSearch();
        }
    }

    public interface OnBackClickForAppTitle {
        void onBack();
    }

    public interface OnSearchClickForAppTitle {
        void onSearch();
    }

    public interface OnExitClickForAppTitle {
        void onExit();
    }

    public AppTitleForBlack(Context context, AttributeSet attrs) {
        super(context, attrs);
        Tools.loadLayout(this, R.layout.view_titleforblack);
        init();
    }

    private void init() {
        name = (TextView) findViewById(R.id.titleforblack_name);
        back = findViewById(R.id.titleforblack_back);
        search = (ImageView) findViewById(R.id.titleforblack_search);
    }

//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
//            RelativeLayout title_layout = (RelativeLayout) findViewById(R.id.titleforblack_layout);
//            int height = (int) getResources().getDimension(R.dimen.apptitle_height);
//            if (title_layout.getHeight() != height) {
//                LayoutParams lp = (LayoutParams) title_layout.getLayoutParams();
//                lp.height = height;
//                title_layout.setLayoutParams(lp);
//                title_layout.setPadding(0, 0, 0, 0);
//            }
//        } else {
//            RelativeLayout title_layout = (RelativeLayout) findViewById(R.id.titleforblack_layout);
//            int height = (int) getResources().getDimension(R.dimen.apptitle_height);
//            int toph = getStatusHeight(getContext());
//            if (toph == -1) {
//                toph = height / 2;
//            }
//            if (title_layout.getHeight() != height) {
//                LayoutParams lp = (LayoutParams) title_layout.getLayoutParams();
//                lp.height = height + toph;
//                title_layout.setLayoutParams(lp);
//                title_layout.setPadding(0, 0, 0, 0);
//            }
//        }
//    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    public void settingName(String name) {
        this.name.setText(name);
    }

    public void showBack(OnBackClickForAppTitle listener) {
        back.setVisibility(View.VISIBLE);
        this.listener = listener;
        back.setOnClickListener(this);
    }

    public void showSearch(OnSearchClickForAppTitle listener) {
        search.setVisibility(View.VISIBLE);
        this.searchListener = listener;
        search.setOnClickListener(this);
    }

    public static int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            Class clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }
}
