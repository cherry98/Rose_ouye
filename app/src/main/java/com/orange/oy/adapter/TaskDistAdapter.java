package com.orange.oy.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.Text;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.MyDialog;
import com.orange.oy.info.TaskDetailLeftInfo;

import java.util.ArrayList;

/**
 * Created by xiedongyan on 2017/3/8.
 */

/**
 * 分配任务适配器
 */
public class TaskDistAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<TaskDetailLeftInfo> list;
    private OnShowItemClickListener onShowItemClickListener;

    public TaskDistAdapter(Context context, ArrayList<TaskDetailLeftInfo> list) {
        this.context = context;
        this.list = list;
    }

    public TaskDistAdapter(Context context, ArrayList<TaskDetailLeftInfo> list, String type, boolean isRight, String
            project_property) {
        this.context = context;
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
            convertView = Tools.loadLayout(context, R.layout.item_taskdist);
            viewHolder.itemtaskdist_addr = (TextView) convertView.findViewById(R.id.itemtaskdist_addr);
            viewHolder.itemtaskdist_num = (TextView) convertView.findViewById(R.id.itemtaskdist_num);
            viewHolder.itemtaskdist_name = (TextView) convertView.findViewById(R.id.itemtaskdist_name);
            viewHolder.itemtaskdist_nickname = (TextView) convertView.findViewById(R.id.itemtaskdist_nickname);
            viewHolder.itemtaskdist_looktime = (TextView) convertView.findViewById(R.id.itemtaskdist_looktime);
            viewHolder.itemtaskdist_img = (ImageView) convertView.findViewById(R.id.itemtaskdist_img);
            viewHolder.itemtaskdist_checkbox = (CheckBox) convertView.findViewById(R.id.itemtaskdist_checkbox);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final TaskDetailLeftInfo taskDetailLeftInfo = list.get(position);
        viewHolder.itemtaskdist_addr.setText(taskDetailLeftInfo.getCity3());
        viewHolder.itemtaskdist_name.setText(taskDetailLeftInfo.getName());
        viewHolder.itemtaskdist_num.setText(taskDetailLeftInfo.getCode());
        if (taskDetailLeftInfo.getNickname().equals("null") || taskDetailLeftInfo.getNickname() == null) {
            viewHolder.itemtaskdist_nickname.setText(taskDetailLeftInfo.getIdentity());
        } else {
            viewHolder.itemtaskdist_nickname.setText(taskDetailLeftInfo.getNickname());
        }
        if (!"".equals(taskDetailLeftInfo.getTimedetail()) && !TextUtils.isEmpty(taskDetailLeftInfo.getTimedetail())) {
            viewHolder.itemtaskdist_looktime.setVisibility(View.VISIBLE);
            viewHolder.itemtaskdist_looktime.setText(taskDetailLeftInfo.getTimedetail());
            final View finalConvertView = convertView;
            viewHolder.itemtaskdist_looktime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView textView = new TextView(context);
                    textView.setBackgroundColor(Color.WHITE);
                    textView.setTextColor(Color.BLACK);
                    textView.setTextSize(15);
                    textView.setGravity(Gravity.CENTER_HORIZONTAL);
                    textView.setText("\n可执行时间\n\n" + taskDetailLeftInfo.getTimedetail());
                    textView.setHeight(Tools.getScreeInfoHeight(context) / 2);
                    MyDialog myDialog = new MyDialog((Activity) context, textView, false, 0);
                    myDialog.setMyDialogWidth(Tools.getScreeInfoWidth(context) - 40);
                    myDialog.showAtLocation((finalConvertView.findViewById(R.id.main)),
                            Gravity.CENTER_VERTICAL, 0, 0); //设置layout在PopupWindow中显示的位置
                }
            });
        } else {
            viewHolder.itemtaskdist_looktime.setVisibility(View.GONE);
            viewHolder.itemtaskdist_looktime.setOnClickListener(null);
        }
        boolean isShow = taskDetailLeftInfo.isShow();//是否显示复选框
        if (isShow) {
            viewHolder.itemtaskdist_checkbox.setVisibility(View.VISIBLE);
            viewHolder.itemtaskdist_img.setVisibility(View.GONE);
        } else {
            viewHolder.itemtaskdist_checkbox.setVisibility(View.GONE);
            viewHolder.itemtaskdist_img.setVisibility(View.VISIBLE);
        }
        viewHolder.itemtaskdist_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    taskDetailLeftInfo.setChecked(true);
                } else {
                    taskDetailLeftInfo.setChecked(false);
                }
                onShowItemClickListener.onShowItemClick(taskDetailLeftInfo);
            }
        });
        viewHolder.itemtaskdist_checkbox.setChecked(taskDetailLeftInfo.isChecked());
        return convertView;
    }

    class ViewHolder {
        private TextView itemtaskdist_addr, itemtaskdist_num, itemtaskdist_name, itemtaskdist_nickname,
                itemtaskdist_looktime;
        private CheckBox itemtaskdist_checkbox;
        private ImageView itemtaskdist_img;
    }

    public interface OnShowItemClickListener {
        void onShowItemClick(TaskDetailLeftInfo taskDetailLeftInfo);
    }

    public void setOnShowItemClickListener(OnShowItemClickListener onShowItemClickListener) {
        this.onShowItemClickListener = onShowItemClickListener;
    }
}
