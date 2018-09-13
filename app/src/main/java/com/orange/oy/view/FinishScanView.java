package com.orange.oy.view;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.adapter.ScanTaskAdapter;
import com.orange.oy.allinterface.FinishTaskProgressRefresh;
import com.orange.oy.base.BaseView;
import com.orange.oy.base.Tools;
import com.orange.oy.info.ScanTaskInfo;
import com.orange.oy.info.TaskFinishInfo;

import java.util.ArrayList;

/**
 * Created by xiedongyan on 2017/2/21.
 * 扫码任务查看详情页
 */

public class FinishScanView extends LinearLayout implements View.OnClickListener, FinishTaskProgressRefresh, BaseView {
    private TextView name;
    private ImageView right;
    private TaskFinishInfo taskFinishInfo;
    private LinearLayout layout;
    private TextView viewfdt_scan_num, viewfdt_scan_list;
    private MyListView listView;
    private ScanTaskAdapter scanTaskAdapter;
    private TextView viewfdt_scan_progressvalue;
    private ProgressBar viewfdt_scan_progress;
    private boolean isProgress;

    public FinishScanView(Context context, TaskFinishInfo taskFinishInfo, boolean isAgain) {
        super(context);
        Tools.loadLayout(this, R.layout.view_finishdt_scan);
        init(isAgain);
        this.taskFinishInfo = taskFinishInfo;
    }

    private void init(boolean isAgain) {
        name = (TextView) findViewById(R.id.viewfdt_scan_name);
        right = (ImageView) findViewById(R.id.viewfdt_scan_right);
        layout = (LinearLayout) findViewById(R.id.viewfdt_scan_layout2);
        listView = (MyListView) findViewById(R.id.viewfdt_scan_listview);
        viewfdt_scan_num = (TextView) findViewById(R.id.viewfdt_scan_num);
        viewfdt_scan_list = (TextView) findViewById(R.id.viewfdt_scan_list);
        viewfdt_scan_progressvalue = (TextView) findViewById(R.id.viewfdt_scan_progressvalue);
        viewfdt_scan_progress = (ProgressBar) findViewById(R.id.viewfdt_scan_progress);
        findViewById(R.id.viewfdt_scan_layout).setOnClickListener(this);
        if (isAgain) {
            findViewById(R.id.viewfdt_scan_reset).setOnClickListener(this);
        } else {
            findViewById(R.id.viewfdt_scan_reset).setVisibility(View.GONE);
        }
    }

    public void setIsProgress(boolean isshow) {
        isProgress = isshow;
        if (isProgress) {
            viewfdt_scan_progressvalue.setVisibility(VISIBLE);
            viewfdt_scan_progress.setVisibility(VISIBLE);
            findViewById(R.id.viewfdt_scan_layout).setOnClickListener(null);
        }
    }

    public void setData(Context context, String scannum, String unscannum, ArrayList<ScanTaskInfo> list) {
        if ("null".equals(scannum)) {
            scannum = "0";
        }
        if ("null".equals(unscannum)) {
            unscannum = "0";
        }
        viewfdt_scan_num.setText("已扫描的数量：" + scannum);
        if (Tools.StringToInt(unscannum) > 0) {
            viewfdt_scan_list.setText("未扫码成功商品：" + unscannum);
            scanTaskAdapter = new ScanTaskAdapter(context, list);
            listView.setAdapter(scanTaskAdapter);
        } else {
            viewfdt_scan_list.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
        }
    }

    public void settingValue(String name) {
        this.name.setText(name);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.viewfdt_scan_layout) {
            if (layout.getVisibility() == View.VISIBLE) {
                layout.setVisibility(View.GONE);
                right.setImageResource(R.mipmap.text_spread);
            } else if (layout.getVisibility() == View.GONE) {
                layout.setVisibility(View.VISIBLE);
                right.setImageResource(R.mipmap.text_shrinkup);
            }
        }
    }

    @Override
    public void setProgress(int progress) {
        if (isProgress) {
            viewfdt_scan_progress.setProgress(progress);
            if (progress < 100) {
                viewfdt_scan_progressvalue.setText(progress + "%");
            } else {
                viewfdt_scan_progressvalue.setVisibility(GONE);
                viewfdt_scan_progress.setVisibility(GONE);
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
