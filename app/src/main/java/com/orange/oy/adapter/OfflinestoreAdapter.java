package com.orange.oy.adapter;

import android.content.Context;
import android.text.TextUtils;
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

public class OfflinestoreAdapter extends BaseAdapter implements View.OnTouchListener {
    private boolean isSelect1;
    private ArrayList<TaskDetailLeftInfo> list_right;
    private Context context;
    private String username;

    public OfflinestoreAdapter(Context context, ArrayList<TaskDetailLeftInfo> list) {
        this.context = context;
        this.list_right = list;
        username = AppInfo.getName(context);
    }

    public int getCount() {
        return list_right.size();
    }

    public Object getItem(int position) {
        return list_right.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = Tools.loadLayout(context, R.layout.item_offlinestore);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.item_offlinestore_time);
            viewHolder.code = (TextView) convertView.findViewById(R.id.item_offlinestore_code_name);
            viewHolder.button1 = (TextView) convertView.findViewById(R.id.item_offlinestore_button2);
            viewHolder.city1 = (TextView) convertView.findViewById(R.id.item_offlinestore_address);
            viewHolder.item_offlinestore_number = (TextView) convertView.findViewById(R.id.item_offlinestore_number);
            viewHolder.item_offlinestore_time = (TextView) convertView.findViewById(R.id.item_offlinestore_looktime);
            viewHolder.button1.setText("下载");
            viewHolder.button1.setOnTouchListener(this);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        TaskDetailLeftInfo taskDetailLeftInfo = list_right.get(position);
        String fynum = taskDetailLeftInfo.getNumber();
        if (!TextUtils.isEmpty(fynum) && !"null".equals(fynum) && fynum.equals(username)) {
            viewHolder.button1.setVisibility(View.VISIBLE);
        } else {
            viewHolder.button1.setVisibility(View.GONE);
        }
        viewHolder.item_offlinestore_time.setText(taskDetailLeftInfo.getTimedetail());
        viewHolder.name.setText(taskDetailLeftInfo.getName());
        viewHolder.code.setText(taskDetailLeftInfo.getCode());
        viewHolder.city1.setText(taskDetailLeftInfo.getCity3());
        viewHolder.item_offlinestore_number.setText(fynum);
        return convertView;
    }

    public int getSelect() {
        int result = -1;
        if (isSelect1) {
            result = 1;
        }
        isSelect1 = false;
        return result;
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            switch (v.getId()) {
                case R.id.item_offlinestore_button2: {
                    isSelect1 = true;
                }
                break;
            }
        }
        return false;
    }

    class ViewHolder {
        TextView name, code, city1, button1, item_offlinestore_number;
        TextView item_offlinestore_time;
    }
}
