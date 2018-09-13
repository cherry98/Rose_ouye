package com.orange.oy.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.activity.TaskitemEditillustrateActivity;
import com.orange.oy.allinterface.FinishTaskProgressRefresh;
import com.orange.oy.base.Tools;
import com.orange.oy.info.TaskFinishInfo;


public class FinisheditView extends LinearLayout implements View.OnClickListener, FinishTaskProgressRefresh {
    public FinisheditView(Context context, TaskFinishInfo taskFinishInfo, boolean isAgain) {
        super(context);
        Tools.loadLayout(this, R.layout.view_finishdt_edit);
        init(isAgain);
        this.taskFinishInfo = taskFinishInfo;
    }

    private TextView name;
    private LinearLayout layout;
    private ImageView right;
    private TaskFinishInfo taskFinishInfo;
    private TextView viewfdt_edit_state;
    private ProgressBar viewfdt_edit_rate;

    private void init(boolean isAgain) {
        viewfdt_edit_rate = (ProgressBar) findViewById(R.id.viewfdt_edit_rate);
        viewfdt_edit_state = (TextView) findViewById(R.id.viewfdt_edit_state);
        name = (TextView) findViewById(R.id.viewfdt_edit_name);
        layout = (LinearLayout) findViewById(R.id.viewfdt_edit_value);
        right = (ImageView) findViewById(R.id.viewfdt_edit_right);
        if (isAgain)
            findViewById(R.id.viewfdt_edit_reset).setOnClickListener(this);
        else
            findViewById(R.id.viewfdt_edit_reset).setVisibility(GONE);
        findViewById(R.id.viewfdt_edit_layout).setOnClickListener(this);
//        onClick(right);
    }

    public void setIsProgress(boolean isshow) {
        if (isshow) {
            viewfdt_edit_rate.setVisibility(VISIBLE);
            viewfdt_edit_state.setVisibility(VISIBLE);
            findViewById(R.id.viewfdt_edit_layout).setOnClickListener(null);
        }
    }

    public void settingValue(String name) {
        this.name.setText(name);
    }

    public void addChildView(View view) {
        LinearLayout.LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = Tools.dipToPx((Activity) getContext(), 8);
        layout.addView(view, lp);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.viewfdt_edit_layout: {
                if (layout.getVisibility() == View.VISIBLE) {
                    layout.setVisibility(View.GONE);
                    right.setImageResource(R.mipmap.text_spread);
                } else {
                    layout.setVisibility(View.VISIBLE);
                    right.setImageResource(R.mipmap.text_shrinkup);
                }
            }
            break;
            case R.id.viewfdt_edit_reset: {
                Intent intent = new Intent(getContext(), TaskitemEditillustrateActivity.class);
                intent.putExtra("project_id", taskFinishInfo.getProjectid());
                intent.putExtra("project_name", taskFinishInfo.getProjectname());
                intent.putExtra("task_pack_id", taskFinishInfo.getPid());
                intent.putExtra("taskid", taskFinishInfo.getTaskid());
                intent.putExtra("tasktype", "3");
                intent.putExtra("store_id", taskFinishInfo.getStoreid());
                intent.putExtra("category1", taskFinishInfo.getCategory1());
                intent.putExtra("category2", taskFinishInfo.getCategory2());
                intent.putExtra("category3", taskFinishInfo.getCategory3());
                intent.putExtra("outlet_batch", taskFinishInfo.getOutlet_batch());
                intent.putExtra("p_batch", taskFinishInfo.getP_batch());
                getContext().startActivity(intent);
            }
            break;
        }
    }

    @Override
    public void setProgress(int progress) {
        viewfdt_edit_rate.setProgress(progress);
        if (progress < 100) {
            viewfdt_edit_state.setText(progress + "%");
        } else {
            viewfdt_edit_rate.setVisibility(GONE);
            viewfdt_edit_state.setVisibility(GONE);
        }
    }

    @Override
    public Object getInfo() {
        return taskFinishInfo;
    }
}
