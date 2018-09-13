package com.orange.oy.adapter;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.TaskDetailLeftInfo;

import java.util.ArrayList;

/**
 * Created by xiedongyan on 2017/3/7.
 */

/**
 * 我的任务执行界面适配器
 */
public class MyTaskDetailAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<TaskDetailLeftInfo> list;
    private boolean isRight = false;

    public MyTaskDetailAdapter(Context context, ArrayList<TaskDetailLeftInfo> list) {
        this.context = context;
        this.list = list;
    }

    public MyTaskDetailAdapter(Context context, ArrayList<TaskDetailLeftInfo> list, boolean isRight) {
        this.context = context;
        this.list = list;
        this.isRight = isRight;
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

    private boolean isClick = false;

    public boolean isClick() {
        return isClick;
    }

    public void clearClick() {
        isClick = false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = Tools.loadLayout(context, R.layout.item_taskgrab);
            viewHolder.itemtaskgrab_addr = (TextView) convertView.findViewById(R.id.itemtaskgrab_addr);
            viewHolder.itemtaskgrab_num = (TextView) convertView.findViewById(R.id.itemtaskgrab_num);
            viewHolder.itemtaskgrab_name = (TextView) convertView.findViewById(R.id.itemtaskgrab_name);
            viewHolder.itemtaskgrab_looktime = (TextView) convertView.findViewById(R.id.itemtaskgrab_looktime);
            viewHolder.itemtaskgrab_grab = (TextView) convertView.findViewById(R.id.itemtaskgrab_grab);
            viewHolder.itemtaskgrab_price = (TextView) convertView.findViewById(R.id.itemtaskgrab_price);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        TaskDetailLeftInfo taskDetailLeftInfo = list.get(position);
        viewHolder.itemtaskgrab_addr.setText(taskDetailLeftInfo.getCity3());
        viewHolder.itemtaskgrab_name.setText(taskDetailLeftInfo.getName());
        viewHolder.itemtaskgrab_looktime.setText(taskDetailLeftInfo.getTimedetail());
        viewHolder.itemtaskgrab_num.setText(taskDetailLeftInfo.getCode());
        viewHolder.itemtaskgrab_price.setText(taskDetailLeftInfo.getMoney());
        return convertView;
    }

    class ViewHolder {
        private TextView itemtaskgrab_addr, itemtaskgrab_num, itemtaskgrab_name, itemtaskgrab_looktime,
                itemtaskgrab_grab, itemtaskgrab_price;
    }
}
