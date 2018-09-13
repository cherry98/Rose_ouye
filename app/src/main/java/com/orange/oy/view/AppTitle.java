package com.orange.oy.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;


public class AppTitle extends RelativeLayout implements View.OnClickListener {
    private TextView name, title_exit, title_back_text;
    private ImageView title_name_img;
    private View title_back, title_search_layout;
    private EditText title_search_edittext;
    private ImageView search;
    private OnBackClickForAppTitle listener;
    private OnSearchClickForAppTitle searchListener;
    private OnExitClickForAppTitle exitListener;
    private OnRightClickForAppTitle rightClickListener;
    private ImageView title_illustrate;
    private ImageView title_back_img;
    private View title_right;

    public void onClick(View v) {
        if (v.getId() == R.id.title_back && listener != null) {
            listener.onBack();
        } else if (v.getId() == R.id.title_search && searchListener != null) {
            searchListener.onSearch();
        } else if (v.getId() == R.id.title_exit && exitListener != null) {
            exitListener.onExit();
        } else if (v.getId() == R.id.title_illustrate && exitListener != null) {
            exitListener.onExit();
        } else if (v.getId() == R.id.title_right && rightClickListener != null) {
            rightClickListener.onRightClick();
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

    public interface OnRightClickForAppTitle {
        void onRightClick();
    }

    public AppTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        Tools.loadLayout(this, R.layout.view_title);
        init();
    }

    public final void settingExitSize(int size) {
        if (title_exit != null) {
            title_exit.setTextSize(size);
        }
    }

    public final void showSearch(TextWatcher textWatcher, TextView.OnEditorActionListener onEditorActionListener) {
        title_search_layout.setVisibility(View.VISIBLE);
        name.setVisibility(View.GONE);
        title_search_edittext.addTextChangedListener(textWatcher);
        title_search_edittext.setOnEditorActionListener(onEditorActionListener);
    }

    public void hideSearchEditText() {
        title_search_layout.setVisibility(View.GONE);
        name.setVisibility(View.VISIBLE);
    }

    public final void settingHint(String hint) {
        title_search_edittext.setHint(hint);
    }

    public String getSearchText() {
        return title_search_edittext.getText().toString();
    }

    public final void setSearchText(String str) {
        title_search_edittext.setText(str);
    }

    public ImageView getTitle_name_img() {
        return title_name_img;
    }

    public void setImageTitle(int resid) {
        name.setVisibility(GONE);
        title_name_img.setVisibility(VISIBLE);
        title_name_img.setImageResource(resid);
    }

    private void init() {
        title_name_img = (ImageView) findViewById(R.id.title_name_img);
        name = (TextView) findViewById(R.id.title_name);
        title_search_layout = findViewById(R.id.title_search_layout);
        title_search_edittext = (EditText) findViewById(R.id.title_search_edittext);
        title_back = findViewById(R.id.title_back);
        search = (ImageView) findViewById(R.id.title_search);
        title_exit = (TextView) findViewById(R.id.title_exit);
        title_back_text = (TextView) findViewById(R.id.title_back_text);
        title_illustrate = (ImageView) findViewById(R.id.title_illustrate);
        title_back_img = (ImageView) findViewById(R.id.title_back_img);
        title_right = findViewById(R.id.title_right);
    }

    public final void settingRightListener(OnRightClickForAppTitle onRightClickForAppTitle) {
        rightClickListener = onRightClickForAppTitle;
        if (onRightClickForAppTitle != null) {
            title_right.setOnClickListener(this);
        } else {
            title_right.setClickable(false);
        }
    }

    public void transparentbg() {
        findViewById(R.id.title_layout).setBackgroundColor(Color.TRANSPARENT);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            RelativeLayout title_layout = (RelativeLayout) findViewById(R.id.title_layout);
            int height = (int) getResources().getDimension(R.dimen.apptitle_height);
            if (title_layout.getHeight() != height) {
                LayoutParams lp = (LayoutParams) title_layout.getLayoutParams();
                lp.height = height;
                title_layout.setLayoutParams(lp);
                title_layout.setPadding(0, 0, 0, 0);
            }
        }
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    public final void settingName(String name) {
        this.name.setText(name);
    }

    public final void settingName(String name, int color) {
        title_name_img.setVisibility(GONE);
        this.name.setVisibility(VISIBLE);
        this.name.setText(name);
        this.name.setTextColor(color);
    }

    public final void settingName(Spannable name) {
        this.name.setText(name);
    }

    public final void showBack(OnBackClickForAppTitle listener) {
        title_back.setVisibility(View.VISIBLE);
        this.listener = listener;
        title_back.setOnClickListener(this);
    }

    public final void showBack(OnBackClickForAppTitle listener, String text) {
        title_back.setVisibility(View.VISIBLE);
        this.listener = listener;
        title_back.setOnClickListener(this);
        title_back_img.setVisibility(View.GONE);
        title_back_text.setVisibility(View.VISIBLE);
        title_back_text.setText(text);
    }

    public final void showSearch(OnSearchClickForAppTitle listener) {
        search.setVisibility(View.VISIBLE);
        this.searchListener = listener;
        if (listener != null) {
            search.setOnClickListener(this);
        } else {
            search.setClickable(false);
        }
    }

    public final void hideSearch() {
        search.setVisibility(View.INVISIBLE);
        this.searchListener = null;
        search.setOnClickListener(null);
    }

    public final void settingSearch(int resid, OnSearchClickForAppTitle listener) {
        search.setImageResource(resid);
        showSearch(listener);
    }

    public final void showExit(OnExitClickForAppTitle listener) {
        title_exit.setVisibility(View.VISIBLE);
        this.exitListener = listener;
        if (listener != null) {
            title_exit.setOnClickListener(this);
        } else {
            title_exit.setClickable(false);
        }
    }

    public final void settingExit(String str, OnExitClickForAppTitle listener) {
        title_exit.setText(str);
        showExit(listener);
    }

    public final void settingExit(String str, int color, OnExitClickForAppTitle listener) {
        title_exit.setText(str);
        title_exit.setTextColor(color);
        showExit(listener);
    }

    public final void settingExit(String str) {
        title_exit.setVisibility(View.VISIBLE);
        title_exit.setText(str);
    }

    public final void settingExitColor(int color) {
        title_exit.setTextColor(color);
    }

    public final void setIllustrate(OnExitClickForAppTitle listener) {
        showIllustrate(listener);
    }

    public final void setIllustrate(int imgId, OnExitClickForAppTitle listener) {
        showIllustrate(imgId, listener);
    }

    public final void showIllustrate(OnExitClickForAppTitle listener) {
        showIllustrate(-1, listener);
    }

    public final void showIllustrate(int imgId, OnExitClickForAppTitle listener) {
        if (imgId != -1) {
            title_illustrate.setImageResource(imgId);
        }
        title_illustrate.setVisibility(View.VISIBLE);
        this.exitListener = listener;
        if (listener != null) {
            title_illustrate.setOnClickListener(this);
        } else {
            title_illustrate.setOnClickListener(null);
        }
    }

    public final void hideIllustrate() {
        title_illustrate.setVisibility(View.GONE);
    }

    public final void hideExit() {
        title_exit.setVisibility(View.INVISIBLE);
        this.exitListener = null;
        title_exit.setOnClickListener(null);
    }

    public final void hideExit2() {
        title_exit.setVisibility(View.GONE);
        this.exitListener = null;
        title_exit.setOnClickListener(null);
    }

    public ImageView getTitle_illustrate() {
        return title_illustrate;
    }
}
