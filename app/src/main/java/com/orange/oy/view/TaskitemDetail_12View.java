package com.orange.oy.view;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.internal.SlideTouchEventListener;
import com.orange.oy.R;
import com.orange.oy.allinterface.OfflineStoreClickViewListener;
import com.orange.oy.allinterface.PullToRefreshDeleteListener;
import com.orange.oy.base.Tools;
import com.orange.oy.info.TaskitemDetailNewInfo;
import com.orange.oy.info.TaskitemListInfo;

public class TaskitemDetail_12View extends LinearLayout implements SlideTouchEventListener {
    private int rightWidth;
    private View item_taskitemdetaill_left_layout;
    private TextView item_taskitemdetaill_right;
    private OfflineStoreClickViewListener offlineStoreClickViewListener;
    private boolean isDelete = false;
    private PullToRefreshDeleteListener pullToRefreshDeleteListener;
    private ImageView item_taskitemdetaill_ico, item_taskitemdetaill_package_switch;
    private TextView item_taskitemdetaill_name, item_taskitemdetaill_package_name;
    private View item_taskitemdetaill_tasklayout, item_taskitemdetaill_packagelayout;
    private boolean isSlide = false;
    private ProgressBar item_taskitemdetaill_rate;
    private TextView item_taskitemdetaill_state;
    private View item_taskitemdetail_right;

    public void setPullToRefreshDeleteListener(PullToRefreshDeleteListener listener) {
        pullToRefreshDeleteListener = listener;
    }

    public void setOfflineStoreClickViewListener(OfflineStoreClickViewListener listener) {
        offlineStoreClickViewListener = listener;
    }

    public TaskitemDetail_12View(Context context) {
        super(context);
        Tools.loadLayout(this, R.layout.item_taskdetail_12);
        rightWidth = (int) getResources().getDimension(R.dimen.view_offlinestore_right_width);
        item_taskitemdetail_right = findViewById(R.id.item_taskitemdetail_right);
        item_taskitemdetaill_state = (TextView) findViewById(R.id.item_taskitemdetaill_state);
        View item_taskitemdetaill_layout = findViewById(R.id.item_taskitemdetaill_layout);
        item_taskitemdetaill_right = (TextView) findViewById(R.id.item_taskitemdetaill_right);
        item_taskitemdetaill_ico = (ImageView) findViewById(R.id.item_taskitemdetaill_ico);
        item_taskitemdetaill_package_switch = (ImageView) findViewById(R.id.item_taskitemdetaill_package_switch);
        item_taskitemdetaill_name = (TextView) findViewById(R.id.item_taskitemdetaill_name);
        item_taskitemdetaill_package_name = (TextView) findViewById(R.id.item_taskitemdetaill_package_name);
        item_taskitemdetaill_packagelayout = findViewById(R.id.item_taskitemdetaill_packagelayout);
        item_taskitemdetaill_tasklayout = findViewById(R.id.item_taskitemdetaill_tasklayout);
        item_taskitemdetaill_left_layout = findViewById(R.id.item_taskitemdetaill_left_layout);
        item_taskitemdetaill_rate = (ProgressBar) findViewById(R.id.item_taskitemdetaill_rate);
        LayoutParams lp = (LayoutParams) item_taskitemdetaill_left_layout.getLayoutParams();
        lp.width = Tools.getScreeInfoWidth(context);
        item_taskitemdetaill_left_layout.setLayoutParams(lp);
        OnTouchListener onTouchListener = new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        switch (v.getId()) {
                            case R.id.item_taskitemdetaill_layout: {
                                if (offlineStoreClickViewListener != null) {
                                    offlineStoreClickViewListener.select(TaskitemDetail_12View.this);
                                }
                            }
                            break;
                            case R.id.item_taskitemdetaill_left_layout: {
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
        item_taskitemdetaill_layout.setOnTouchListener(onTouchListener);
        item_taskitemdetaill_left_layout.setOnTouchListener(onTouchListener);
    }

    /**
     * 控制进度条显示隐藏
     *
     * @param isShow
     */
    public void isShowProgressbar(boolean isShow, boolean is_Record) {
        if (isShow) {
            item_taskitemdetaill_rate.setVisibility(VISIBLE);
            item_taskitemdetaill_state.setVisibility(VISIBLE);
        } else {
            item_taskitemdetaill_rate.setVisibility(GONE);
        }
        if (is_Record) {
            item_taskitemdetaill_state.setText("录音中");
            item_taskitemdetaill_state.setVisibility(VISIBLE);
        } else {
            item_taskitemdetaill_state.setVisibility(INVISIBLE);
        }
    }

    /**
     * 设置进度条进度
     *
     * @param progress
     */
    public void settingProgressbar(int progress) {
        item_taskitemdetaill_rate.setProgress(progress);
        item_taskitemdetaill_state.setVisibility(VISIBLE);
        if (progress >= 100) {
            item_taskitemdetaill_rate.setVisibility(GONE);
            item_taskitemdetaill_state.setText("完成");
            item_taskitemdetail_right.setVisibility(VISIBLE);
        } else {
            item_taskitemdetaill_state.setText(progress + "%");
            item_taskitemdetail_right.setVisibility(INVISIBLE);
        }
    }

    public void settingRightText(String text) {
        if (item_taskitemdetaill_right != null) {
            item_taskitemdetaill_right.setText(text);
        }
    }

    private Object object;

    /**
     * 设置数据
     *
     * @param taskitemDetailNewInfo 数据
     */
    public void setting(TaskitemDetailNewInfo taskitemDetailNewInfo, OnClickListener closeTaskClickListener, int position) {
        this.object = taskitemDetailNewInfo;
        if (taskitemDetailNewInfo.getIsPackage().equals("1")) {//任务包
            isSlide = false;
            if (item_taskitemdetaill_tasklayout != null) {
                item_taskitemdetaill_tasklayout.setVisibility(View.GONE);
                item_taskitemdetaill_packagelayout.setVisibility(View.VISIBLE);
            }
            item_taskitemdetaill_package_name.setText(taskitemDetailNewInfo.getName());
            if (taskitemDetailNewInfo.getIs_invalid().equals("1")) {//可以关闭
                item_taskitemdetaill_package_switch.setVisibility(View.VISIBLE);
                if (taskitemDetailNewInfo.getIsClose().equals("1")) {//1开
                    item_taskitemdetaill_package_switch.setImageResource(R.mipmap.switch_open2);
                    item_taskitemdetaill_package_switch.setTag(position);
                    item_taskitemdetaill_package_switch.setOnClickListener(closeTaskClickListener);
                } else {//2关
                    item_taskitemdetaill_package_switch.setImageResource(R.mipmap.switch_off2);
                    item_taskitemdetaill_package_switch.setTag(null);
                    item_taskitemdetaill_package_switch.setOnClickListener(null);
                }
            } else {//不可以关闭
                item_taskitemdetaill_package_switch.setVisibility(View.GONE);
                item_taskitemdetaill_package_switch.setTag(null);
                item_taskitemdetaill_package_switch.setOnClickListener(null);
            }
        } else {//任务
            if (item_taskitemdetaill_tasklayout != null) {
                item_taskitemdetaill_tasklayout.setVisibility(View.VISIBLE);
                item_taskitemdetaill_packagelayout.setVisibility(View.GONE);
            }
            item_taskitemdetaill_name.setText(taskitemDetailNewInfo.getName());
            if (taskitemDetailNewInfo.getTask_type().equals("1")) {
                item_taskitemdetaill_ico.setImageResource(R.mipmap.take_photo);
                isSlide = false;
            } else if (taskitemDetailNewInfo.getTask_type().equals("2")) {
                item_taskitemdetaill_ico.setImageResource(R.mipmap.take_viedo);
                isSlide = false;
            } else if (taskitemDetailNewInfo.getTask_type().equals("3")) {
                item_taskitemdetaill_ico.setImageResource(R.mipmap.take_record);
                isSlide = false;
            } else if (taskitemDetailNewInfo.getTask_type().equals("4")) {
                item_taskitemdetaill_ico.setImageResource(R.mipmap.take_location);
                isSlide = false;
            } else if (taskitemDetailNewInfo.getTask_type().equals("5")) {
                item_taskitemdetaill_ico.setImageResource(R.mipmap.take_tape);
                isSlide = false;
            } else if (taskitemDetailNewInfo.getTask_type().equals("6")) {
                item_taskitemdetaill_ico.setImageResource(R.mipmap.take_scan);
                isSlide = false;
            } else if (taskitemDetailNewInfo.getTask_type().equals("7")) {
                item_taskitemdetaill_ico.setImageResource(R.mipmap.task_phone);
                isSlide = false;
            } else if (taskitemDetailNewInfo.getTask_type().equals("8")) {
                item_taskitemdetaill_ico.setImageResource(R.mipmap.take_photo);
                isSlide = false;
            } else if (taskitemDetailNewInfo.getTask_type().equals("9")) {
                item_taskitemdetaill_ico.setImageResource(R.mipmap.take_exp);
                isSlide = false;
            } else {
                isSlide = false;
            }
        }
    }

    /**
     * 设置数据
     *
     * @param taskitemDetailNewInfo 数据
     */
    public void settingForTask(TaskitemListInfo taskitemDetailNewInfo) {
        this.object = taskitemDetailNewInfo;
        if (item_taskitemdetaill_tasklayout != null) {
            item_taskitemdetaill_tasklayout.setVisibility(View.VISIBLE);
            item_taskitemdetaill_packagelayout.setVisibility(View.GONE);
        }
        item_taskitemdetaill_name.setText(taskitemDetailNewInfo.getTaskname());
        if (taskitemDetailNewInfo.getType().equals("1")) {
            item_taskitemdetaill_ico.setImageResource(R.mipmap.take_photo);
            isSlide = false;
        } else if (taskitemDetailNewInfo.getType().equals("2")) {
            item_taskitemdetaill_ico.setImageResource(R.mipmap.take_viedo);
            isSlide = false;
        } else if (taskitemDetailNewInfo.getType().equals("3")) {
            item_taskitemdetaill_ico.setImageResource(R.mipmap.take_record);
            isSlide = false;
        } else if (taskitemDetailNewInfo.getType().equals("4")) {
            item_taskitemdetaill_ico.setImageResource(R.mipmap.take_location);
            isSlide = false;
        } else if (taskitemDetailNewInfo.getType().equals("5")) {
            item_taskitemdetaill_ico.setImageResource(R.mipmap.take_tape);
            isSlide = false;
        } else if (taskitemDetailNewInfo.getType().equals("6")) {
            item_taskitemdetaill_ico.setImageResource(R.mipmap.take_scan);
            isSlide = false;
        } else if (taskitemDetailNewInfo.getType().equals("7")) {
            item_taskitemdetaill_ico.setImageResource(R.mipmap.task_phone);
            isSlide = false;
        } else if (taskitemDetailNewInfo.getType().equals("8")) {
            item_taskitemdetaill_ico.setImageResource(R.mipmap.take_photo);
            isSlide = false;
        } else if (taskitemDetailNewInfo.getType().equals("9")) {
            item_taskitemdetaill_ico.setImageResource(R.mipmap.take_exp);
            isSlide = false;
        } else {
            isSlide = false;
        }
    }

    private boolean isClickButton = false;

    /***
     * 只有当setOnTouchListener中的onTouch方法返回值是false（事件未被消费，向下传递）时，onTouchEvent方法才被执行。
     *
     * @param slide
     */
    public void onTouchEvent(int slide) {
        isClickButton = false;
        if (!isSlide) {
            return;
        }
        LayoutParams lp = (LayoutParams) item_taskitemdetaill_left_layout.getLayoutParams();
        int temp = lp.leftMargin - slide;
        if (Math.abs(temp) > rightWidth) {
            lp.leftMargin = -rightWidth;
        } else if (temp > 0) {
            lp.leftMargin = 0;
        } else {
            lp.leftMargin = temp;
        }
        item_taskitemdetaill_left_layout.setLayoutParams(lp);
    }

    public void onTouchUp() {
        if (isDelete && pullToRefreshDeleteListener != null) {
            Tools.d("删除");
            isDelete = false;
            LayoutParams lp = (LayoutParams) item_taskitemdetaill_left_layout.getLayoutParams();
            if (lp.leftMargin != 0) {
                lp.leftMargin = 0;
                item_taskitemdetaill_left_layout.setLayoutParams(lp);
            }
            pullToRefreshDeleteListener.delete(object);
        } else if (isClickButton && pullToRefreshDeleteListener != null) {
            Tools.d("执行");
            isClickButton = false;
            LayoutParams lp = (LayoutParams) item_taskitemdetaill_left_layout.getLayoutParams();
            if (lp.leftMargin != 0) {
                lp.leftMargin = 0;
                item_taskitemdetaill_left_layout.setLayoutParams(lp);
            } else {
                pullToRefreshDeleteListener.click(object);
            }
        } else {
            LayoutParams lp = (LayoutParams) item_taskitemdetaill_left_layout.getLayoutParams();
            if (lp.leftMargin != 0 || lp.leftMargin != -rightWidth) {
                if (lp.leftMargin <= -rightWidth / 9) {
                    lp.leftMargin = -rightWidth;
                } else {
                    lp.leftMargin = 0;
                }
                item_taskitemdetaill_left_layout.setLayoutParams(lp);
            }
        }
    }

    public void onTouchMove(int sx, int sy) {
        Tools.d("sx:" + sx + ",sy:" + sy);
        LayoutParams lp = (LayoutParams) item_taskitemdetaill_left_layout.getLayoutParams();
        if (lp.leftMargin != 0) {
            lp.leftMargin = 0;
            item_taskitemdetaill_left_layout.setLayoutParams(lp);
        }
        isClickButton = false;
        isDelete = false;
    }

    public void onTouchDown(int x, int y) {
        int[] location = new int[2];
        item_taskitemdetaill_right.getLocationOnScreen(location);
        int width = item_taskitemdetaill_right.getWidth();
        int height = item_taskitemdetaill_right.getHeight();
        if (x > location[0] && x < location[0] + width && y < location[1] + height && y > location[1]) {
            Tools.d("删除");
            isDelete = true;
        } else {
            LayoutParams lp = (LayoutParams) item_taskitemdetaill_left_layout.getLayoutParams();
            lp.leftMargin = 0;
            item_taskitemdetaill_left_layout.setLayoutParams(lp);
        }
    }
}