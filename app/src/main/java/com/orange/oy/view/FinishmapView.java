package com.orange.oy.view;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.activity.TaskitemMapActivity;
import com.orange.oy.allinterface.FinishTaskProgressRefresh;
import com.orange.oy.base.BaseView;
import com.orange.oy.base.Tools;
import com.orange.oy.info.TaskFinishInfo;
import com.orange.oy.util.ImageLoader;


public class FinishmapView extends LinearLayout implements View.OnClickListener, FinishTaskProgressRefresh, BaseView {
    public FinishmapView(Context context, TaskFinishInfo taskFinishInfo, boolean isAgain) {
        super(context);
        Tools.loadLayout(this, R.layout.view_finishdt_map);
        init(isAgain);
        imageLoader = new ImageLoader(context);
        this.taskFinishInfo = taskFinishInfo;
    }

    private TextView name;
    private ImageView right;
    private View value;
    private ImageView viewfdt_map_img;
    private ImageLoader imageLoader;
    private TaskFinishInfo taskFinishInfo;
    private ProgressBar viewfdt_map_progress;
    private TextView viewfdt_map_progressvalue;

    private void init(boolean isAgain) {
        viewfdt_map_progress = (ProgressBar) findViewById(R.id.viewfdt_map_progress);
        viewfdt_map_progressvalue = (TextView) findViewById(R.id.viewfdt_map_progressvalue);
        name = (TextView) findViewById(R.id.viewfdt_map_name);
        value = findViewById(R.id.viewfdt_map_value);
        right = (ImageView) findViewById(R.id.viewfdt_map_right);
        viewfdt_map_img = (ImageView) findViewById(R.id.viewfdt_map_img);
        if (isAgain)
            findViewById(R.id.viewfdt_map_reset).setOnClickListener(this);
        else
            findViewById(R.id.viewfdt_map_reset).setVisibility(GONE);
        findViewById(R.id.viewfdt_map_layout).setOnClickListener(this);
//        onClick(right);
    }

    private boolean isProgress;

    public void setIsprogress(boolean isshow) {
        isProgress = isshow;
        if (isProgress) {
            viewfdt_map_progressvalue.setVisibility(VISIBLE);
            viewfdt_map_progress.setVisibility(VISIBLE);
            findViewById(R.id.viewfdt_map_layout).setOnClickListener(null);
        }
    }

    public void settingValue(String name, String value) {
        this.name.setText(name);
        imageLoader.DisplayImage(value.replaceAll("\\\\", ""), viewfdt_map_img);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.viewfdt_map_layout: {
                if (value.getVisibility() == View.VISIBLE) {
                    value.setVisibility(View.GONE);
                    right.setImageResource(R.mipmap.text_spread);
                } else {
                    value.setVisibility(View.VISIBLE);
                    right.setImageResource(R.mipmap.text_shrinkup);
                }
            }
            break;
            case R.id.viewfdt_map_reset: {//重做
                if (taskFinishInfo == null) return;
                Intent intent = new Intent(getContext(), TaskitemMapActivity.class);
                intent.putExtra("task_pack_id", taskFinishInfo.getPid());
                intent.putExtra("task_id", taskFinishInfo.getTaskid());
                intent.putExtra("store_id", taskFinishInfo.getStoreid());
                intent.putExtra("category1", taskFinishInfo.getCategory1());
                intent.putExtra("category2", taskFinishInfo.getCategory2());
                intent.putExtra("category3", taskFinishInfo.getCategory3());
                intent.putExtra("project_id", taskFinishInfo.getProjectid());
                intent.putExtra("project_name", taskFinishInfo.getProjectname());
                intent.putExtra("task_pack_name", taskFinishInfo.getPackage_name());
                intent.putExtra("task_name", taskFinishInfo.getName());
                intent.putExtra("store_num", taskFinishInfo.getStorenum());
                intent.putExtra("store_name", taskFinishInfo.getStorename());
                intent.putExtra("outlet_batch", taskFinishInfo.getOutlet_batch());
                intent.putExtra("p_batch", taskFinishInfo.getP_batch());
                getContext().startActivity(intent);
            }
            break;
        }
    }

    @Override
    public void setProgress(int progress) {
        if (isProgress) {
            viewfdt_map_progress.setProgress(progress);
            if (progress < 100) {
                viewfdt_map_progressvalue.setText(progress + "%");
            } else {
                viewfdt_map_progressvalue.setVisibility(GONE);
                viewfdt_map_progress.setVisibility(GONE);
            }
        }
    }

    @Override
    public Object getInfo() {
        return taskFinishInfo;
    }

    @Override
    public void onResume(Object object) {

    }

    @Override
    public void onPause(Object object) {

    }

    @Override
    public void onStop(Object object) {

    }

    @Override
    public void onDestory(Object object) {

    }

    @Override
    public Object getBaseData() {
        return taskFinishInfo;
    }
}
