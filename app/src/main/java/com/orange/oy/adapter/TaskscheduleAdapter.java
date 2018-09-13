package com.orange.oy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.TaskInfo;

import java.util.ArrayList;

public class TaskscheduleAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<TaskInfo> list;

    public TaskscheduleAdapter(Context context, ArrayList<TaskInfo> list) {
        this.context = context;
        this.list = list;
    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = Tools.loadLayout(context, R.layout.item_taskschedule);
            viewHolder.name = (TextView) convertView.findViewById(R.id.item_tasksch_name);
            viewHolder.item1Num = (TextView) convertView.findViewById(R.id.item_tasksch_item1);
            viewHolder.item2Num = (TextView) convertView.findViewById(R.id.item_tasksch_item2);
            viewHolder.item3Num = (TextView) convertView.findViewById(R.id.item_tasksch_item3);
//            viewHolder.time = (TextView) convertView.findViewById(R.id.item_tasksch_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        TaskInfo taskInfo = list.get(position);
        viewHolder.name.setText(taskInfo.getName());
        viewHolder.item1Num.setText(context.getResources().getString(R.string.taskschedule_item1) + taskInfo
                .getItem1Num());
        viewHolder.item2Num.setText(context.getResources().getString(R.string.taskschedule_item2) + taskInfo
                .getItem2Num());
        viewHolder.item3Num.setText(context.getResources().getString(R.string.taskschedule_item3) + taskInfo
                .getItem3Num());
//        viewHolder.time.setText(taskInfo.getFinishTime());
        return convertView;
    }

    class ViewHolder {
        TextView name, item1Num, item2Num, item3Num, time;
    }
}
