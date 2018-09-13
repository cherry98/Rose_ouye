package com.orange.oy.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.Tools;
import com.orange.oy.info.TaskDetailLeftInfo;

import java.util.ArrayList;

/**
 * Created by xiedongyan on 2017/1/11.
 */

public class BrightListAdapter extends BaseAdapter implements View.OnTouchListener {

    private ArrayList<TaskDetailLeftInfo> list;
    private Context context;
    private String usermobile;
    private boolean isOffline = false;
    private boolean isClickButton = false;//考试任务
    private boolean isClickButton2 = false;//普通任务/详情
    private boolean isClickLooktime = false;//true为待执行 false不可查看

    //已上传页面的构造函数
    public BrightListAdapter(Context context, ArrayList<TaskDetailLeftInfo> list) {
        this.context = context;
        this.list = list;
        usermobile = AppInfo.getName(context);
    }

    //待执行页面的构造函数
    public BrightListAdapter(Context context, ArrayList<TaskDetailLeftInfo> list, boolean isOffline) {
        this.context = context;
        this.list = list;
        this.isOffline = isOffline;
        usermobile = AppInfo.getName(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = Tools.loadLayout(context, R.layout.item_brightdetail);
            viewHolder.item_brightdetail_codename = (TextView) convertView.findViewById(R.id.item_brightdetail_codename);
            viewHolder.item_brightdetail_name = (TextView) convertView.findViewById(R.id.item_brightdetail_name);
            viewHolder.item_brightdetail_address = (TextView) convertView.findViewById(R.id.item_brightdetail_address);
            viewHolder.item_brightdetail_number = (TextView) convertView.findViewById(R.id.item_brightdetail_number);
            viewHolder.item_brightdetail_looktime = (TextView) convertView.findViewById(R.id.item_brightdetail_looktime);
            viewHolder.item_brightdetail_testtask = (TextView) convertView.findViewById(R.id.item_brightdetail_testtask);
            viewHolder.item_brightdetail_commontask = (TextView) convertView.findViewById(R.id.item_brightdetail_commontask);
            viewHolder.item_brightdetail_looktime_top = convertView.findViewById(R.id.item_brightdetail_looktime_top);
            if (isOffline) {
                viewHolder.item_brightdetail_looktime_top.setVisibility(View.VISIBLE);//待执行
            } else {
                viewHolder.item_brightdetail_looktime_top.setVisibility(View.GONE);//已上传
                viewHolder.item_brightdetail_looktime.setVisibility(View.GONE);
                viewHolder.item_brightdetail_commontask.setBackgroundResource(R.drawable.taskdetail_button3);
                viewHolder.item_brightdetail_commontask.setText("详情");
                viewHolder.item_brightdetail_commontask.setTextColor(Color.GREEN);
                viewHolder.item_brightdetail_testtask.setVisibility(View.GONE);
            }
            viewHolder.item_brightdetail_testtask.setOnTouchListener(this);
            viewHolder.item_brightdetail_commontask.setOnTouchListener(this);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        TaskDetailLeftInfo taskDetailLeftInfo = list.get(position);
        viewHolder.item_brightdetail_codename.setText(taskDetailLeftInfo.getCode());
        viewHolder.item_brightdetail_name.setText(taskDetailLeftInfo.getName());
        if (isOffline) {
            viewHolder.item_brightdetail_looktime.setText(taskDetailLeftInfo.getTimedetail());
        }
        viewHolder.item_brightdetail_address.setText(taskDetailLeftInfo.getCity3());
        viewHolder.item_brightdetail_number.setText(taskDetailLeftInfo.getNumber());
        return convertView;
    }

    public int getSelect() {
        if (isClickLooktime) {
            return 3;
        }
        return -1;
    }

    public boolean isClickButton() {
        return isClickButton;
    }

    public boolean isClickButton2() {
        return isClickButton2;
    }

    public void clearClickButton() {
        isClickButton = false;
        isClickButton2 = false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.item_brightdetail_testtask:
                isClickButton = true;
                isClickButton2 = false;
                break;
            case R.id.item_brightdetail_commontask:
                isClickButton = false;
                isClickButton2 = true;
                break;
            case R.id.item_brightdetail_looktime:
                clearClickButton();
                isClickLooktime = true;
                break;
        }
        return false;
    }

    class ViewHolder {
        private TextView item_brightdetail_codename, item_brightdetail_name, item_brightdetail_address,
                item_brightdetail_number, item_brightdetail_looktime;
        private TextView item_brightdetail_testtask, item_brightdetail_commontask;
        private View item_brightdetail_looktime_top;
    }
}
