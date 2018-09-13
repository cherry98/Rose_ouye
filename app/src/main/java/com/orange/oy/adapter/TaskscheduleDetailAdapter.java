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

public class TaskscheduleDetailAdapter extends BaseAdapter implements View.OnTouchListener {
    private ArrayList<TaskDetailLeftInfo> list;
    private Context context;
    private String usermobile;
    private boolean showButton2;
    private boolean isOffline = false;

    public void setShowButton2(boolean showButton2) {
        this.showButton2 = showButton2;
    }

    public TaskscheduleDetailAdapter(Context context, ArrayList<TaskDetailLeftInfo> list) {
        this.context = context;
        this.list = list;
        isClickButton = false;
        isShow = true;
        usermobile = AppInfo.getName(context);
        showButton2 = false;
    }

    public TaskscheduleDetailAdapter(Context context, ArrayList<TaskDetailLeftInfo> list, boolean isOffline) {
        this.context = context;
        this.list = list;
        isClickButton = false;
        isShow = true;
        usermobile = AppInfo.getName(context);
        showButton2 = false;
        this.isOffline = isOffline;
    }

    private boolean isShow;

    public void isShowButton(boolean isShow) {
        this.isShow = isShow;
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
            if (isOffline) {
                viewHolder.item_taskscheduledetail_looktime_top.setVisibility(View.VISIBLE);
            } else {
                viewHolder.item_taskscheduledetail_looktime_top.setVisibility(View.GONE);
                viewHolder.item_taskscheduledetail_looktime.setVisibility(View.GONE);
                viewHolder.item_taskscheduledetail_button2.setBackgroundResource(R.drawable.taskdetail_button3);
                viewHolder.item_taskscheduledetail_button2.setText("详情");
                viewHolder.item_taskscheduledetail_button2.setTextColor(Color.GREEN);
            }
            if (showButton2) {
                viewHolder.item_taskscheduledetail_button.setVisibility(View.VISIBLE);
                viewHolder.item_taskscheduledetail_button.setBackgroundResource(R.drawable.taskdetail_button);
                viewHolder.item_taskscheduledetail_button.setText("重做");
                viewHolder.item_taskscheduledetail_button.setTextColor(context.getResources().getColor(R.color
                        .colorPrimaryDark));
            } else {
                if (!isOffline)
                    viewHolder.item_taskscheduledetail_button.setVisibility(View.GONE);
            }
            viewHolder.item_taskscheduledetail_button.setOnTouchListener(this);
            viewHolder.item_taskscheduledetail_button2.setOnTouchListener(this);
            viewHolder.item_taskscheduledetail_looktime.setOnTouchListener(this);
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
//            if (showButton2)
//                viewHolder.item_taskscheduledetail_button2.setVisibility(View.VISIBLE);
//            if (isOffline) {
//                viewHolder.item_taskscheduledetail_offline.setVisibility(View.VISIBLE);
//                if (taskDetailLeftInfo.getIsOffline() == 1) {
//                    viewHolder.item_taskscheduledetail_offline.setOnTouchListener(null);
//                    viewHolder.item_taskscheduledetail_offline.setText("任务已下载");
//                    viewHolder.item_taskscheduledetail_offline.setTextColor(context.getResources().getColor(R.color
//                            .myteam_search_text));
//                    isClickOffline = false;
//                } else {
//                    viewHolder.item_taskscheduledetail_offline.setOnTouchListener(this);
//                    viewHolder.item_taskscheduledetail_offline.setText("离线下载");
//                    viewHolder.item_taskscheduledetail_offline.setTextColor(Color.BLUE);
//                }
//            }
            if (isOffline) {
                viewHolder.item_taskscheduledetail_button.setVisibility(View.VISIBLE);
                if (taskDetailLeftInfo.getIs_exe().equals("1")) {//可执行
                    if (taskDetailLeftInfo.getIsCompleted() == 1) {
                        viewHolder.item_taskscheduledetail_button.setText("已完成");
                        viewHolder.item_taskscheduledetail_button.setOnTouchListener(null);
                    } else {
                        viewHolder.item_taskscheduledetail_button.setText("执行");
                        viewHolder.item_taskscheduledetail_button.setOnTouchListener(this);
                    }
                } else {
                    viewHolder.item_taskscheduledetail_button.setText("不可执行");
                    viewHolder.item_taskscheduledetail_button.setOnTouchListener(null);
                }
            }
        } else {
            if (isOffline)
                viewHolder.item_taskscheduledetail_button.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(proxy) && !"null".equals(proxy) && proxy.equals(usermobile)) {
            identity = "代理 " + identity;
            if (isOffline) {
                viewHolder.item_taskscheduledetail_button2.setVisibility(View.VISIBLE);
            }
        } else {
            if (isOffline) {
                viewHolder.item_taskscheduledetail_button2.setVisibility(View.GONE);
            }
        }
        viewHolder.item_taskscheduledetail_code_name.setText(taskDetailLeftInfo.getCode());
        viewHolder.item_taskscheduledetail_time.setText(taskDetailLeftInfo.getName());
        if (isOffline) {
            viewHolder.item_taskscheduledetail_looktime.setText(taskDetailLeftInfo.getTimedetail());
        }
//        viewHolder.item_taskscheduledetail_address.setText(identity + taskDetailLeftInfo.getCity() + " " +
//                taskDetailLeftInfo.getCity2() + " " + taskDetailLeftInfo.getCity3());
//        viewHolder.item_taskscheduledetail_address.setText(taskDetailLeftInfo.getCity() + " " +
//                taskDetailLeftInfo.getCity2() + " " + taskDetailLeftInfo.getCity3());
        viewHolder.item_taskscheduledetail_address.setText(taskDetailLeftInfo.getCity3());
        viewHolder.item_taskscheduledetail_number.setText(taskDetailLeftInfo.getNumber());
        return convertView;
    }

    private boolean isClickButton, isClickButton2, isClickLooktime, isClickOffline;

    public void clearClickButton() {
        isClickButton = false;
        isClickButton2 = false;
        isClickLooktime = false;
        isClickOffline = false;
    }

    public boolean isClickButton() {
        return isClickButton;
    }

    public boolean isClickButton2() {
        return isClickButton2;
    }

    public int getSelect() {
        if (isClickLooktime) {
            return 3;
        } else if (isClickOffline) {
            return 4;
        }
        return -1;
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.item_taskscheduledetail_button) {
            isClickButton = true;
            isClickButton2 = false;
        } else if (v.getId() == R.id.item_taskscheduledetail_button2) {
            isClickButton2 = true;
            isClickButton = false;
        } else if (v.getId() == R.id.item_taskscheduledetail_looktime) {
            clearClickButton();
            isClickLooktime = true;
        } else if (v.getId() == R.id.item_taskscheduledetail_offline) {
            clearClickButton();
            isClickOffline = true;
        }
        return false;
    }

    class ViewHolder {
        private TextView item_taskscheduledetail_code_name, item_taskscheduledetail_time,
                item_taskscheduledetail_address, item_taskscheduledetail_number, item_taskscheduledetail_offline,
                item_taskscheduledetail_looktime, item_taskscheduledetail_button, item_taskscheduledetail_button2;
        private View item_taskscheduledetail_looktime_top;
    }
}
