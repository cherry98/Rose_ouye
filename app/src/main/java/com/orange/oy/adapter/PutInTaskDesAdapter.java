package com.orange.oy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.TaskCheckInfo;

import java.util.ArrayList;


/**
 *
 *
 */
public class PutInTaskDesAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<TaskCheckInfo> list;
    private View.OnClickListener onClickListener;

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    public PutInTaskDesAdapter(Context context, ArrayList<TaskCheckInfo> list) {
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

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = Tools.loadLayout(context, R.layout.item_task_ischeck);
            viewHolder = new ViewHolder();

            TextView item_check, item_looked, item_look;
            LinearLayout lin_task_state;

            viewHolder.tv_taskname = (TextView) convertView.findViewById(R.id.tv_taskname);
            viewHolder.tv_username = (TextView) convertView.findViewById(R.id.tv_username);
            viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.tv_task_state = (TextView) convertView.findViewById(R.id.tv_task_state);

            viewHolder.item_check = (TextView) convertView.findViewById(R.id.item_check);
            viewHolder.item_look = (TextView) convertView.findViewById(R.id.item_look);
            viewHolder.item_looked = (TextView) convertView.findViewById(R.id.item_looked);
            viewHolder.lin_task_state = (LinearLayout) convertView.findViewById(R.id.lin_task_state);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (list != null && !list.isEmpty()) {
            TaskCheckInfo taskCheckInfo = list.get(position);

            if (!Tools.isEmpty(taskCheckInfo.getOutlet_name())) {
                viewHolder.tv_taskname.setText(taskCheckInfo.getOutlet_name());
            }
            if (!Tools.isEmpty(taskCheckInfo.getUser_name())) {
                viewHolder.tv_username.setText("用户昵称：" + taskCheckInfo.getUser_name());
            }

            if (!Tools.isEmpty(taskCheckInfo.getComplete_time())) {
                viewHolder.tv_time.setText("完成时间：" + taskCheckInfo.getComplete_time());
            }
            /****************** //  状态0为待验收，1为已验收,2为通过，3为不通过      *****/
            String type = taskCheckInfo.getType();
            if ("0".equals(type)) {
                viewHolder.item_looked.setVisibility(View.GONE);
                viewHolder.item_check.setVisibility(View.VISIBLE);
                viewHolder.lin_task_state.setVisibility(View.GONE);
            } else if ("1".equals(type)) {
                viewHolder.item_looked.setVisibility(View.GONE);
                viewHolder.item_check.setVisibility(View.GONE);
                viewHolder.lin_task_state.setVisibility(View.VISIBLE);
                if (!Tools.isEmpty(taskCheckInfo.getPass_state())) {
                    if (taskCheckInfo.getPass_state().equals("1")) { //通过状态，1为通过，0为不通过
                        viewHolder.tv_task_state.setText("任务状态：审核通过 ");
                    } else {
                        viewHolder.tv_task_state.setText("任务状态：审核不通过 ");
                    }
                }

            } else if ("2".equals(type) || "3".equals(type)) {

                if (!Tools.isEmpty(taskCheckInfo.getComplete_time())) {
                    viewHolder.tv_time.setText("执行时间：" + taskCheckInfo.getComplete_time());
                }
                viewHolder.item_check.setVisibility(View.GONE);
                viewHolder.item_looked.setVisibility(View.VISIBLE); //查看
                viewHolder.lin_task_state.setVisibility(View.GONE);
            }

            viewHolder.item_look.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iPutInTask.Look(position);
                }
            });
            viewHolder.item_looked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iPutInTask.Look(position);
                }
            });
            viewHolder.item_check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iPutInTask.check(position);
                }
            });

        }
        return convertView;
    }

    private IPutInTask iPutInTask;

    public interface IPutInTask {
        void check(int position);

        void Look(int position);

    }

    public void setiPutInTask(IPutInTask iPutInTask) {
        this.iPutInTask = iPutInTask;
    }

    class ViewHolder {

        TextView tv_taskname, tv_username, tv_time, tv_task_state;
        TextView item_check, item_looked, item_look;
        LinearLayout lin_task_state;
    }
}
