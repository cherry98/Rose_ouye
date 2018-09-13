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

public class TaskDetailLeftAdapter extends BaseAdapter implements View.OnTouchListener {
    private ArrayList<TaskDetailLeftInfo> list;
    private Context context;
    private String usermobile;

    public TaskDetailLeftAdapter(Context context, ArrayList<TaskDetailLeftInfo> list) {
        this.context = context;
        this.list = list;
        usermobile = AppInfo.getName(context);
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
            convertView = Tools.loadLayout(context, R.layout.item_taskdetailleft);
            viewHolder.name = (TextView) convertView.findViewById(R.id.item_taskdetail_name);
            viewHolder.button1 = convertView.findViewById(R.id.item_taskdetail_button1);
            viewHolder.button2 = (TextView) convertView.findViewById(R.id.item_taskdetail_button2);
            viewHolder.looktime = (TextView) convertView.findViewById(R.id.item_taskdetail_looktime);
            viewHolder.offline = (TextView) convertView.findViewById(R.id.item_taskdetail_offline);
            viewHolder.city1 = (TextView) convertView.findViewById(R.id.item_taskdetail_detail);
            viewHolder.number = (TextView) convertView.findViewById(R.id.item_taskdetail_number);
            viewHolder.button1.setOnTouchListener(this);
            viewHolder.button2.setOnTouchListener(this);
//            viewHolder.looktime.setOnTouchListener(this);
            viewHolder.offline.setOnTouchListener(this);
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
            viewHolder.offline.setVisibility(View.VISIBLE);
            viewHolder.button2.setVisibility(View.VISIBLE);
            if (taskDetailLeftInfo.getIsOffline() == 1) {
                viewHolder.offline.setOnTouchListener(null);
                viewHolder.offline.setText("任务已下载");
                viewHolder.offline.setTextColor(context.getResources().getColor(R.color.myteam_search_text));
                isSelect4 = false;
                if (taskDetailLeftInfo.getIsCompleted() == 1) {
                    viewHolder.button2.setText("已完成");
                    viewHolder.button2.setOnTouchListener(null);
                    isSelect2 = false;
                } else {
                    viewHolder.button2.setText("执行");
                    viewHolder.button2.setOnTouchListener(this);
                }
            } else {
                viewHolder.offline.setOnTouchListener(this);
                viewHolder.offline.setText("离线下载");
                viewHolder.offline.setTextColor(Color.BLUE);
            }
            if (taskDetailLeftInfo.getIs_exe().equals("1") && taskDetailLeftInfo.getIsCompleted() != 1) {//可执行
                viewHolder.button2.setText("执行");
                viewHolder.button2.setOnTouchListener(this);
            } else {
                viewHolder.button2.setText("不可执行");
                viewHolder.button2.setOnTouchListener(null);
            }
        } else {
            viewHolder.offline.setVisibility(View.GONE);
            viewHolder.button2.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(proxy) && !"null".equals(proxy) && proxy.equals(usermobile)) {
            identity = "代理 " + identity;
            viewHolder.button1.setVisibility(View.VISIBLE);
        } else {
            viewHolder.button1.setVisibility(View.GONE);
        }
        viewHolder.name.setText(taskDetailLeftInfo.getCode() + " " + taskDetailLeftInfo.getName());
        viewHolder.looktime.setText(taskDetailLeftInfo.getTimedetail());
//        viewHolder.city1.setText(identity + taskDetailLeftInfo.getCity() + " " +
//                taskDetailLeftInfo.getCity2() + " " + taskDetailLeftInfo.getCity3());
//        viewHolder.city1.setText(taskDetailLeftInfo.getCity() + " " + taskDetailLeftInfo.getCity2() + " " + taskDetailLeftInfo
//                .getCity3());
        viewHolder.city1.setText(taskDetailLeftInfo.getCity3());
        viewHolder.number.setText(fynum);
        return convertView;
    }

    private boolean isSelect1, isSelect2, isSelect3, isSelect4;

    public void clear() {
        isSelect1 = false;
        isSelect2 = false;
        isSelect3 = false;
        isSelect4 = false;
    }

    /**
     * @return 0:左侧button
     * 1:右侧button
     * 2:查看执行时间
     * 3:离线下载
     * -1:没有button
     */
    public int selectButton() {
        if (isSelect2 && isSelect1 && isSelect3 && isSelect4) {
            return -1;
        } else {
            if (isSelect1) {
                return 0;
            } else if (isSelect2) {
                return 1;
            } else if (isSelect3) {
                return 2;
            } else if (isSelect4) {
                return 3;
            } else {
                return -1;
            }
        }
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            switch (v.getId()) {
                case R.id.item_taskdetail_button1: {
                    isSelect1 = true;
                    isSelect2 = false;
                    isSelect3 = false;
                    isSelect4 = false;
                }
                break;
                case R.id.item_taskdetail_button2: {
                    isSelect1 = false;
                    isSelect2 = true;
                    isSelect3 = false;
                    isSelect4 = false;
                }
                break;
                case R.id.item_taskdetail_looktime: {
                    isSelect1 = false;
                    isSelect2 = false;
                    isSelect3 = true;
                    isSelect4 = false;
                }
                break;
                case R.id.item_taskdetail_offline: {
                    isSelect1 = false;
                    isSelect2 = false;
                    isSelect3 = false;
                    isSelect4 = true;
                }
                break;
            }
        }
        return false;
    }

    class ViewHolder {
        private TextView name;
        private TextView city1;
        private TextView number;
        private View button1;
        private TextView looktime, offline, button2;
    }
}
