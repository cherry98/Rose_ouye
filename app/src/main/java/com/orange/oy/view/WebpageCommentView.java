package com.orange.oy.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.orange.oy.R;
import com.orange.oy.base.Tools;

/**
 * Created by Administrator on 2018/9/6.
 * 体验任务评论用
 */

public class WebpageCommentView extends LinearLayout implements View.OnClickListener {
    private View webpage_i1, webpage_i2, webpage_i3, webpage_i4, webpage_i5;
    private View webpage_screenshot;
    private ImageView webpage_paint;
    private View wpcv_commentlayout;
    private View wpcv_sumbit;
    private EditText wpcv_edittext;
    private PaintView wpcv_paintview;
    private String path;
    private OnWebpageCommentViewListener onWebpageCommentViewListener;
    private int type = 0;//0:最初状态,1:画板状态,2:画板待确认状态

    public void setOnWebpageCommentViewListener(OnWebpageCommentViewListener onWebpageCommentViewListener) {
        this.onWebpageCommentViewListener = onWebpageCommentViewListener;
    }

    public interface OnWebpageCommentViewListener {
        void submit(int state, String comment);

        void screenshot();
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public WebpageCommentView(Context context) {
        this(context, null);
    }

    public WebpageCommentView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WebpageCommentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Tools.loadLayout(this, R.layout.view_webpagecommentview);
        wpcv_paintview = (PaintView) findViewById(R.id.wpcv_paintview);
        wpcv_commentlayout = findViewById(R.id.wpcv_commentlayout);
        webpage_paint = (ImageView) findViewById(R.id.webpage_paint);
        wpcv_edittext = (EditText) findViewById(R.id.wpcv_edittext);
        webpage_i1 = findViewById(R.id.webpage_i1);
        webpage_i2 = findViewById(R.id.webpage_i2);
        webpage_i3 = findViewById(R.id.webpage_i3);
        webpage_i4 = findViewById(R.id.webpage_i4);
        webpage_i5 = findViewById(R.id.webpage_i5);
        wpcv_sumbit = findViewById(R.id.wpcv_sumbit);
        webpage_screenshot = findViewById(R.id.webpage_screenshot);
        webpage_screenshot.setOnClickListener(this);
        wpcv_sumbit.setOnClickListener(this);
        webpage_i1.setOnClickListener(this);
        webpage_i2.setOnClickListener(this);
        webpage_i3.setOnClickListener(this);
        webpage_i4.setOnClickListener(this);
        webpage_i5.setOnClickListener(this);
        webpage_paint.setOnClickListener(this);
        wpcv_paintview.setOnPaintviewsavefinishListener(new PaintView.OnPaintviewsavefinishListener() {
            public void savefinish() {//图片保存完成
                if (onWebpageCommentViewListener != null) {
                    onWebpageCommentViewListener.submit(state, wpcv_edittext.getText().toString());
                }
                if (type != 2) {
                    setType(0);
                }
            }
        });
        wpcv_edittext.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String editable = wpcv_edittext.getText().toString();
                String str = Tools.stringFilter(editable);
                if (!editable.equals(str)) {
                    wpcv_edittext.setText(str);
                    wpcv_edittext.setSelection(str.length());
                }
            }

            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * @param type 0:最初状态,1:画板状态
     */
    public void setType(int type) {
        this.type = type;
        switch (type) {
            case 0: {
                wpcv_paintview.setVisibility(GONE);
                webpage_screenshot.setVisibility(VISIBLE);
                webpage_paint.setVisibility(GONE);
                wpcv_commentlayout.setVisibility(GONE);
                webpage_i2.setBackgroundColor(Color.TRANSPARENT);
                webpage_i1.setBackgroundColor(Color.TRANSPARENT);
                webpage_i3.setBackgroundColor(Color.TRANSPARENT);
                webpage_i4.setBackgroundColor(Color.TRANSPARENT);
                webpage_i5.setBackgroundColor(Color.TRANSPARENT);
                state = 0;
                wpcv_paintview.canPaint(false);
                webpage_paint.setImageResource(R.mipmap.wpcv_paint1);
                wpcv_sumbit.setVisibility(GONE);
            }
            break;
            case 1: {
                wpcv_paintview.setVisibility(VISIBLE);
                webpage_screenshot.setVisibility(GONE);
                webpage_paint.setVisibility(VISIBLE);
                wpcv_commentlayout.setVisibility(VISIBLE);
                wpcv_sumbit.setVisibility(VISIBLE);
            }
            break;
            case 2: {
                webpage_screenshot.setVisibility(GONE);
                webpage_paint.setVisibility(VISIBLE);
                wpcv_commentlayout.setVisibility(VISIBLE);
                wpcv_sumbit.setVisibility(VISIBLE);
            }
            break;
        }
    }

    public void goPaint(String path) {
        wpcv_paintview.setBitmap(Tools.getBitmap(path, 1024, 1024));
//        type = 1;
//        wpcv_paintview.setVisibility(VISIBLE);
//        webpage_screenshot.setVisibility(GONE);
//        webpage_paint.setVisibility(VISIBLE);
//        wpcv_commentlayout.setVisibility(VISIBLE);
//        wpcv_sumbit.setVisibility(VISIBLE);
        setType(1);
    }

    private int state;//1：吻， 2：花，3：鸡蛋，4：板砖，5：粑粑

    public int getState() {
        return state;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.webpage_screenshot: {
                if (onWebpageCommentViewListener != null) {
                    onWebpageCommentViewListener.screenshot();
                }
            }
            break;
            case R.id.webpage_i1: {
                if (onWebpageCommentViewListener != null) {
                    if (state == 0) {
                        onWebpageCommentViewListener.screenshot();
                        if (!wpcv_paintview.isCanPaint()) {
                            goPaint(path);
                        }
                    }
                    state = 1;
                    webpage_i1.setBackgroundResource(R.drawable.bg_circel_stroke_fff65d57);
                    webpage_i2.setBackgroundColor(Color.TRANSPARENT);
                    webpage_i3.setBackgroundColor(Color.TRANSPARENT);
                    webpage_i4.setBackgroundColor(Color.TRANSPARENT);
                    webpage_i5.setBackgroundColor(Color.TRANSPARENT);
                }
            }
            break;
            case R.id.webpage_i2: {
                if (onWebpageCommentViewListener != null) {
                    if (state == 0) {
                        onWebpageCommentViewListener.screenshot();
                        if (!wpcv_paintview.isCanPaint()) {

                            goPaint(path);
                        }
                    }
                    state = 2;
                    webpage_i2.setBackgroundResource(R.drawable.bg_circel_stroke_fff65d57);
                    webpage_i1.setBackgroundColor(Color.TRANSPARENT);
                    webpage_i3.setBackgroundColor(Color.TRANSPARENT);
                    webpage_i4.setBackgroundColor(Color.TRANSPARENT);
                    webpage_i5.setBackgroundColor(Color.TRANSPARENT);
                }
            }
            break;
            case R.id.webpage_i3: {
                if (onWebpageCommentViewListener != null) {
                    if (state == 0) {
                        onWebpageCommentViewListener.screenshot();
                        if (!wpcv_paintview.isCanPaint()) {
                            goPaint(path);
                        }
                    }
                    state = 3;
                    webpage_i3.setBackgroundResource(R.drawable.bg_circel_stroke_fff65d57);
                    webpage_i2.setBackgroundColor(Color.TRANSPARENT);
                    webpage_i1.setBackgroundColor(Color.TRANSPARENT);
                    webpage_i4.setBackgroundColor(Color.TRANSPARENT);
                    webpage_i5.setBackgroundColor(Color.TRANSPARENT);
                }
            }
            break;
            case R.id.webpage_i4: {
                if (onWebpageCommentViewListener != null) {
                    if (state == 0) {
                        onWebpageCommentViewListener.screenshot();
                        if (!wpcv_paintview.isCanPaint()) {
                            goPaint(path);
                        }
                    }
                    state = 4;
                    webpage_i4.setBackgroundResource(R.drawable.bg_circel_stroke_fff65d57);
                    webpage_i2.setBackgroundColor(Color.TRANSPARENT);
                    webpage_i1.setBackgroundColor(Color.TRANSPARENT);
                    webpage_i3.setBackgroundColor(Color.TRANSPARENT);
                    webpage_i5.setBackgroundColor(Color.TRANSPARENT);
                }
            }
            break;
            case R.id.webpage_i5: {
                if (onWebpageCommentViewListener != null) {
                    if (state == 0) {
                        onWebpageCommentViewListener.screenshot();
                        if (!wpcv_paintview.isCanPaint()) {
                            goPaint(path);
                        }
                    }
                    state = 5;
                    webpage_i5.setBackgroundResource(R.drawable.bg_circel_stroke_fff65d57);
                    webpage_i2.setBackgroundColor(Color.TRANSPARENT);
                    webpage_i1.setBackgroundColor(Color.TRANSPARENT);
                    webpage_i3.setBackgroundColor(Color.TRANSPARENT);
                    webpage_i4.setBackgroundColor(Color.TRANSPARENT);
                }
            }
            break;
            case R.id.wpcv_sumbit: {
                if (state == 0) {
                    Tools.showToast(getContext(), "喜欢这个页面就给个小花吧，不喜欢就给个砖头吧~");
                    return;
                }
                String editstr = wpcv_edittext.getText().toString();
                if (TextUtils.isEmpty(editstr)) {
                    Tools.showToast(getContext(), "写一下对这个页面的感受吧");
                    return;
                }
                if (wpcv_paintview.isCanPaint()) {
                    wpcv_paintview.saveView(path);
                } else {
                    if (onWebpageCommentViewListener != null) {
                        onWebpageCommentViewListener.submit(state, editstr);
                    }
                    if (type != 2) {
                        setType(0);
                    }
                }
                wpcv_edittext.setText("");
//                wpcv_paintview.setVisibility(GONE);
//                webpage_screenshot.setVisibility(VISIBLE);
//                webpage_paint.setVisibility(GONE);
//                wpcv_commentlayout.setVisibility(GONE);
//                webpage_i2.setBackgroundColor(Color.TRANSPARENT);
//                webpage_i1.setBackgroundColor(Color.TRANSPARENT);
//                webpage_i3.setBackgroundColor(Color.TRANSPARENT);
//                webpage_i4.setBackgroundColor(Color.TRANSPARENT);
//                webpage_i5.setBackgroundColor(Color.TRANSPARENT);
//                state = 0;
//                type = 0;
//                wpcv_paintview.canPaint(false);
//                webpage_paint.setImageResource(R.mipmap.wpcv_paint1);
//                wpcv_sumbit.setVisibility(GONE);
            }
            break;
            case R.id.webpage_paint: {
                if (wpcv_paintview.isCanPaint()) {
                    wpcv_paintview.revertPaint();
                } else {
                    if (type == 2) {
                        wpcv_paintview.setVisibility(VISIBLE);
                        wpcv_paintview.setBitmap(Tools.getBitmap(path, 1024, 1024));
                    }
                    wpcv_paintview.canPaint(true);
                    webpage_paint.setImageResource(R.mipmap.wpcv_paint2);
                }
            }
            break;
        }
    }
}
