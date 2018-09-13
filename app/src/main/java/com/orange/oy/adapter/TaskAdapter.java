package com.orange.oy.adapter;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.TaskInfo;

import java.util.ArrayList;

public class TaskAdapter extends BaseAdapter implements View.OnTouchListener {
    private Context context;
    private ArrayList<TaskInfo> list;

    public TaskAdapter(Context context, ArrayList<TaskInfo> list) {
        this.context = context;
        this.list = list;
    }

    public void resetList(ArrayList<TaskInfo> list) {
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
            convertView = Tools.loadLayout(context, R.layout.item_task);
            viewHolder.name = (TextView) convertView.findViewById(R.id.item_task_name);
            viewHolder.item1 = convertView.findViewById(R.id.item_task_item1);
            viewHolder.item2 = convertView.findViewById(R.id.item_task_item2);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        TaskInfo taskInfo = list.get(position);
        viewHolder.name.setText(taskInfo.getName());
        viewHolder.item1.setOnTouchListener(this);
        viewHolder.item2.setOnTouchListener(this);
        return convertView;
    }

    private boolean isSelectItem1, isSelectItem2;

    public void clearSelect() {
        isSelectItem1 = false;
        isSelectItem2 = false;
    }

    public boolean isSelectItem1() {
        return isSelectItem1;
    }

    public boolean isSelectItem2() {
        return isSelectItem2;
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.item_task_item1: {
                isSelectItem1 = true;
                isSelectItem2 = false;
            }
            break;
            case R.id.item_task_item2: {
                isSelectItem1 = false;
                isSelectItem2 = true;
            }
            break;
        }
        return false;
    }

    class ViewHolder {
        TextView name;
        View item1, item2;
    }
}
