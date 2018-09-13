package com.orange.oy.adapter;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.TaskDetailLeftInfo;

import java.util.ArrayList;

public class TaskDetailRightAdapter extends BaseAdapter implements View.OnTouchListener {
    private ArrayList<TaskDetailLeftInfo> list;
    private Context context;

    public TaskDetailRightAdapter(Context context, ArrayList<TaskDetailLeftInfo> list) {
        this.context = context;
        this.list = list;
    }

    public void upData(ArrayList<TaskDetailLeftInfo> list) {
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
            convertView = Tools.loadLayout(context, R.layout.item_taskdetailright);
            viewHolder.name = (TextView) convertView.findViewById(R.id.item_taskdetailright_name);
            viewHolder.button1 = convertView.findViewById(R.id.item_taskdetailright_button1);
            viewHolder.button2 = convertView.findViewById(R.id.item_taskdetailright_button2);
            viewHolder.city4 = (TextView) convertView.findViewById(R.id.item_taskdetailright_city4);
            viewHolder.number = (TextView) convertView.findViewById(R.id.item_taskdetailright_number);
            viewHolder.button1.setOnTouchListener(this);
            viewHolder.button2.setOnTouchListener(this);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        TaskDetailLeftInfo taskDetailLeftInfo = list.get(position);
        viewHolder.name.setText(taskDetailLeftInfo.getCode() + " " + taskDetailLeftInfo.getName());
        viewHolder.city4.setText(taskDetailLeftInfo.getIdentity() + " " + taskDetailLeftInfo.getCity() + " " +
                taskDetailLeftInfo.getCity2() + " " + taskDetailLeftInfo.getCity3() + " " + taskDetailLeftInfo
                .getCitydetail());
        viewHolder.number.setText(taskDetailLeftInfo.getNumber());
        return convertView;
    }

    private boolean isSelect1, isSelect2;

    public void clear() {
        isSelect1 = false;
        isSelect2 = false;
    }

    /**
     * @return 0:左侧button
     * 1:右侧button
     * -1:没有button
     */
    public int selectButton() {
        if (isSelect2 && isSelect1) {
            return -1;
        } else {
            if (isSelect1) {
                return 0;
            } else if (isSelect2) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            switch (v.getId()) {
                case R.id.item_taskdetailright_button1: {
                    isSelect1 = true;
                    isSelect2 = false;
                }
                break;
                case R.id.item_taskdetailright_button2: {
                    isSelect1 = false;
                    isSelect2 = true;
                }
                break;
            }
        }
        return false;
    }

    class ViewHolder {
        private TextView name;
        private TextView city4;
        private TextView number;
        private View button1, button2;
    }
}
