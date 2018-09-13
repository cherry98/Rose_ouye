package com.orange.oy.view;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.activity.createtask_321.ScreenshotActivity;
import com.orange.oy.allinterface.FinishTaskProgressRefresh;
import com.orange.oy.base.Tools;
import com.orange.oy.info.TaskFinishInfo;

/**
 * 网页体验任务完成
 */
public class WebpageTaskView extends LinearLayout implements View.OnClickListener, FinishTaskProgressRefresh {
    private TextView viewfdt_wbpgt_state, name;
    private ProgressBar viewfdt_wbpgt_rate;
    private ImageView right;
    private TaskFinishInfo taskFinishInfo;

    public WebpageTaskView(Context context, TaskFinishInfo taskFinishInfo, boolean isAgain) {
        super(context);
        Tools.loadLayout(this, R.layout.view_finishdt_webpagetask);
        this.taskFinishInfo = taskFinishInfo;
        init(isAgain);
    }

    public void setTaskName(String name) {
        ((TextView) findViewById(R.id.viewfdt_wbpgt_name)).setText(name);
    }

    private void init(boolean isAgain) {
        viewfdt_wbpgt_state = (TextView) findViewById(R.id.viewfdt_wbpgt_state);
        viewfdt_wbpgt_rate = (ProgressBar) findViewById(R.id.viewfdt_wbpgt_rate);
        name = (TextView) findViewById(R.id.viewfdt_wbpgt_name);
        right = (ImageView) findViewById(R.id.viewfdt_wbpgt_right);
        findViewById(R.id.viewfdt_wbpgt_layout).setOnClickListener(this);
        if (isAgain) {
            findViewById(R.id.viewfdt_wbpgt_reset).setOnClickListener(this);
        } else {
            findViewById(R.id.viewfdt_wbpgt_reset).setVisibility(View.GONE);
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.viewfdt_wbpgt_layout: {
                Intent intent = new Intent(getContext(), ScreenshotActivity.class);
                intent.putExtra("task_id", taskFinishInfo.getTaskid());
                intent.putExtra("storeid", taskFinishInfo.getStoreid());
                intent.putExtra("outlet_batch", taskFinishInfo.getOutlet_batch());
                intent.putExtra("p_batch", taskFinishInfo.getP_batch());
                intent.putExtra("pid", taskFinishInfo.getPid());
                intent.putExtra("which_page", "1");//查看详情
                getContext().startActivity(intent);
            }
            break;
        }
    }

    private boolean isProgress;

    public void setIsProgress(boolean isshow) {
        isProgress = isshow;
        if (isProgress) {
            viewfdt_wbpgt_rate.setVisibility(VISIBLE);
            viewfdt_wbpgt_state.setVisibility(VISIBLE);
            findViewById(R.id.viewfdt_wbpgt_layout).setOnClickListener(null);
        }
    }

    public void setProgress(int progress) {
        if (isProgress) {
            viewfdt_wbpgt_rate.setProgress(progress);
            if (progress < 100) {
                viewfdt_wbpgt_state.setText(progress + "%");
            } else {
                viewfdt_wbpgt_rate.setVisibility(GONE);
                viewfdt_wbpgt_state.setVisibility(GONE);
                findViewById(R.id.viewfdt_wbpgt_layout).setOnClickListener(this);
            }
        }
    }

    public Object getInfo() {
        return taskFinishInfo;
    }
}
