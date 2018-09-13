package com.orange.oy.adapter;

import android.content.Context;
import android.graphics.Color;
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

public class TaskscheduleDetailMiddleAdapter extends BaseAdapter implements View.OnTouchListener {
    private ArrayList<TaskDetailLeftInfo> list;
    private Context context;
    private String usermobile;

    public TaskscheduleDetailMiddleAdapter(Context context, ArrayList<TaskDetailLeftInfo> list) {
        this.context = context;
        this.list = list;
        usermobile = AppInfo.getName(context);
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
            convertView = Tools.loadLayout(context, R.layout.item_taskscheduledetailoffline);
            viewHolder.item_taskscheduledetail_looktime = (TextView) convertView.findViewById(R.id
                    .item_taskscheduledetail_looktime);
            viewHolder.item_taskscheduledetail_number = (TextView) convertView.findViewById(R.id
                    .item_taskscheduledetail_number);
            viewHolder.item_taskscheduledetail_code_name = (TextView) convertView.findViewById(R.id
                    .item_taskscheduledetail_code_name);
            viewHolder.item_taskscheduledetail_time = (TextView) convertView.findViewById(R.id
                    .item_taskscheduledetail_time);
            viewHolder.item_taskscheduledetail_address = (TextView) convertView.findViewById(R.id
                    .item_taskscheduledetail_address);
            viewHolder.item_taskscheduledetail_button = (TextView) convertView.findViewById(R.id.item_taskscheduledetail_button);
            viewHolder.item_taskscheduledetail_button2 = (TextView) convertView.findViewById(R.id
                    .item_taskscheduledetail_button2);
            viewHolder.item_taskscheduledetail_looktime_top = convertView.findViewById(R.id.item_taskscheduledetail_looktime_top);
            viewHolder.item_taskscheduledetail_button1_2 = convertView.findViewById(R.id.item_taskscheduledetail_button1_2);
//            viewHolder.item_taskscheduledetail_button.setOnTouchListener(this);
            viewHolder.item_taskscheduledetail_button2.setOnTouchListener(this);
//            viewHolder.item_taskscheduledetail_button1_2.setOnTouchListener(this);

            viewHolder.item_taskscheduledetail_looktime_top.setVisibility(View.GONE);
            viewHolder.item_taskscheduledetail_looktime.setVisibility(View.GONE);
            viewHolder.item_taskscheduledetail_button2.setBackgroundResource(R.drawable.taskdetail_button3);
            viewHolder.item_taskscheduledetail_button2.setText("详情");
            viewHolder.item_taskscheduledetail_button2.setTextColor(Color.GREEN);
//            viewHolder.item_taskscheduledetail_button.setBackgroundResource(R.drawable.taskdetail_button);
//            viewHolder.item_taskscheduledetail_button.setText("重做");
//            viewHolder.item_taskscheduledetail_button.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            viewHolder.item_taskscheduledetail_button.setVisibility(View.GONE);
            viewHolder.item_taskscheduledetail_button1_2.setVisibility(View.GONE);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        TaskDetailLeftInfo taskDetailLeftInfo = list.get(position);
        String proxy = taskDetailLeftInfo.getIdentity();
        String fynum = taskDetailLeftInfo.getNumber();
        String identity = "";
        if (!TextUtils.isEmpty(fynum) && !"null".equals(fynum) && fynum.equals(usermobile)) {
            identity = "访员 ";
//            if (taskDetailLeftInfo.isAgain())
//                viewHolder.item_taskscheduledetail_button1_2.setVisibility(View.VISIBLE);
//            else
//                viewHolder.item_taskscheduledetail_button1_2.setVisibility(View.GONE);
//            if (taskDetailLeftInfo.isAgain()) {
//                viewHolder.item_taskscheduledetail_button.setVisibility(View.VISIBLE);
//            } else {
//                viewHolder.item_taskscheduledetail_button.setVisibility(View.GONE);
//            }
        } else {
//            viewHolder.item_taskscheduledetail_button1_2.setVisibility(View.GONE);
//            viewHolder.item_taskscheduledetail_button.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(proxy) && !"null".equals(proxy) && proxy.equals(usermobile)) {
            identity = "代理 " + identity;
        }
        viewHolder.item_taskscheduledetail_code_name.setText(taskDetailLeftInfo.getCode());
        viewHolder.item_taskscheduledetail_time.setText(taskDetailLeftInfo.getName());
        viewHolder.item_taskscheduledetail_address.setText(taskDetailLeftInfo.getCity3());
        viewHolder.item_taskscheduledetail_number.setText(taskDetailLeftInfo.getNumber());
        return convertView;
    }

    private boolean isClickButton, isClickButton2, isClickButton3;

    public void clearClickButton() {
        isClickButton = false;
        isClickButton2 = false;
        isClickButton3 = false;
    }

    public boolean isClickButton() {
        return isClickButton;
    }

    public boolean isClickButton2() {
        return isClickButton2;
    }

    public boolean isClickButton3() {
        return isClickButton3;
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.item_taskscheduledetail_button) {
            isClickButton = true;
            isClickButton2 = false;
            isClickButton3 = false;
        } else if (v.getId() == R.id.item_taskscheduledetail_button2) {
            isClickButton2 = true;
            isClickButton = false;
            isClickButton3 = false;
        } else if (v.getId() == R.id.item_taskscheduledetail_button1_2) {
            isClickButton3 = true;
            isClickButton = false;
            isClickButton2 = false;
        }
        return false;
    }

    class ViewHolder {
        private TextView item_taskscheduledetail_code_name, item_taskscheduledetail_time,
                item_taskscheduledetail_address, item_taskscheduledetail_number, item_taskscheduledetail_offline,
                item_taskscheduledetail_looktime, item_taskscheduledetail_button, item_taskscheduledetail_button2;
        private View item_taskscheduledetail_looktime_top, item_taskscheduledetail_button1_2;
    }
}
