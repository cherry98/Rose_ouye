package com.orange.oy.view;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.internal.SlideTouchEventListener;
import com.orange.oy.R;
import com.orange.oy.allinterface.OfflineStoreClickViewListener;
import com.orange.oy.allinterface.PullToRefreshDeleteListener;
import com.orange.oy.base.Tools;

public class OfflineStoreView extends LinearLayout implements SlideTouchEventListener {
    private TextView name, code, button, city, number, time;
    private View success;
    private int rightWidth;
    private View item_offlinestore_left_layout;
    private View item_offlinestore_right;
    private OfflineStoreClickViewListener offlineStoreClickViewListener;
    private boolean isDelete = false;
    private PullToRefreshDeleteListener pullToRefreshDeleteListener;

    public void setPullToRefreshDeleteListener(PullToRefreshDeleteListener listener) {
        pullToRefreshDeleteListener = listener;
    }

    public void setOfflineStoreClickViewListener(OfflineStoreClickViewListener listener) {
        offlineStoreClickViewListener = listener;
    }

    public OfflineStoreView(Context context) {
        super(context);
        Tools.loadLayout(this, R.layout.item_offlinestore_delete);
        rightWidth = (int) getResources().getDimension(R.dimen.view_offlinestore_right_width);
        View offlinestore_layout = findViewById(R.id.offlinestore_layout);
        item_offlinestore_right = findViewById(R.id.item_offlinestore_right);
        item_offlinestore_left_layout = findViewById(R.id.item_offlinestore_left_layout);
        LinearLayout.LayoutParams lp = (LayoutParams) item_offlinestore_left_layout.getLayoutParams();
        lp.width = Tools.getScreeInfoWidth(context);
        item_offlinestore_left_layout.setLayoutParams(lp);
        name = (TextView) findViewById(R.id.item_offlinestore_time);
        code = (TextView) findViewById(R.id.item_offlinestore_code_name);
        button = (TextView) findViewById(R.id.item_offlinestore_button2);
        city = (TextView) findViewById(R.id.item_offlinestore_address);
        number = (TextView) findViewById(R.id.item_offlinestore_number);
        time = (TextView) findViewById(R.id.item_offlinestore_looktime);
        success = findViewById(R.id.item_offlinestore_button);
        OnTouchListener onTouchListener = new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        switch (v.getId()) {
                            case R.id.offlinestore_layout: {
                                if (offlineStoreClickViewListener != null) {
                                    offlineStoreClickViewListener.select(OfflineStoreView.this);
                                }
                            }
                            break;
                            case R.id.item_offlinestore_button2: {
                                isClickButton = true;
                            }
                            break;
                        }
                    }
                    break;
                }
                return false;
            }
        };
        offlinestore_layout.setOnTouchListener(onTouchListener);
        button.setOnTouchListener(onTouchListener);
    }

    private boolean isClickButton = false;

    public void setName(String name) {
        this.name.setText(name);
    }

    public void setCode(String code) {
        this.code.setText(code);
    }

    public void setCity(String city) {
        this.city.setText(city);
    }

    public void setNumber(String number) {
        this.number.setText(number);
    }

    public void setTime(String time) {
        this.time.setText(time);
    }

    public void setVisibilityButton1(int visibilityButton1) {
        button.setVisibility(visibilityButton1);
        if (visibilityButton1 == View.VISIBLE) {
            success.setVisibility(View.GONE);
        } else {
            success.setVisibility(View.VISIBLE);
        }
    }

    public void setButton1Text(String string) {
        button.setText(string);
    }

    public void onTouchEvent(int slide) {
        isClickButton = false;
        LinearLayout.LayoutParams lp = (LayoutParams) item_offlinestore_left_layout.getLayoutParams();
        int temp = lp.leftMargin - slide;
        if (Math.abs(temp) > rightWidth) {
            lp.leftMargin = -rightWidth;
        } else if (temp > 0) {
            lp.leftMargin = 0;
        } else {
            lp.leftMargin = temp;
        }
        item_offlinestore_left_layout.setLayoutParams(lp);
    }

    public void onTouchUp() {
        if (isDelete && pullToRefreshDeleteListener != null) {
            Tools.d("删除");
            isDelete = false;
            pullToRefreshDeleteListener.delete(getTag());
            LinearLayout.LayoutParams lp = (LayoutParams) item_offlinestore_left_layout.getLayoutParams();
            if (lp.leftMargin != 0) {
                lp.leftMargin = 0;
                item_offlinestore_left_layout.setLayoutParams(lp);
            }
        } else if (isClickButton && pullToRefreshDeleteListener != null) {
            Tools.d("执行");
            isClickButton = false;
            pullToRefreshDeleteListener.click(getTag());
            LinearLayout.LayoutParams lp = (LayoutParams) item_offlinestore_left_layout.getLayoutParams();
            if (lp.leftMargin != 0) {
                lp.leftMargin = 0;
                item_offlinestore_left_layout.setLayoutParams(lp);
            }
        } else {
            LinearLayout.LayoutParams lp = (LayoutParams) item_offlinestore_left_layout.getLayoutParams();
            if (lp.leftMargin != 0 || lp.leftMargin != -rightWidth) {
                if (lp.leftMargin <= -rightWidth / 9) {
                    lp.leftMargin = -rightWidth;
                } else {
                    lp.leftMargin = 0;
                }
                item_offlinestore_left_layout.setLayoutParams(lp);
            }
        }
    }

    public void onTouchMove(int sx, int sy) {
        Tools.d("sx:" + sx + ",sy:" + sy);
        LinearLayout.LayoutParams lp = (LayoutParams) item_offlinestore_left_layout.getLayoutParams();
        if (lp.leftMargin != 0) {
            lp.leftMargin = 0;
            item_offlinestore_left_layout.setLayoutParams(lp);
        }
        isClickButton = false;
        isDelete = false;
    }


    public void onTouchDown(int x, int y) {
        int[] location = new int[2];
        item_offlinestore_right.getLocationOnScreen(location);
        int width = item_offlinestore_right.getWidth();
        int height = item_offlinestore_right.getHeight();
        if (x > location[0] && x < location[0] + width && y < location[1] + height && y > location[1]) {
            Tools.d("删除");
            isDelete = true;
        } else {
            LinearLayout.LayoutParams lp = (LayoutParams) item_offlinestore_left_layout.getLayoutParams();
            lp.leftMargin = 0;
            item_offlinestore_left_layout.setLayoutParams(lp);
        }
    }
}

























