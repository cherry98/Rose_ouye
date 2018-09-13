package com.orange.oy.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.PutInTaskInfo;

import java.util.ArrayList;

/**
 *
 *
 */
public class PutInTaskAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<PutInTaskInfo> list;
    private View.OnClickListener onClickListener;
    private String state;//1：草稿箱未发布；2：投放中；3：已结束

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    public PutInTaskAdapter(Context context, ArrayList<PutInTaskInfo> list, String state) {
        this.context = context;
        this.list = list;
        this.state = state;
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
            convertView = Tools.loadLayout(context, R.layout.item_putin_task);
            viewHolder = new ViewHolder();
            viewHolder.itemalltask_name = (TextView) convertView.findViewById(R.id.itemalltask_name);
            viewHolder.itemalltask_target_num = (TextView) convertView.findViewById(R.id.itemalltask_target_num);
            viewHolder.itemalltask_time = (TextView) convertView.findViewById(R.id.itemalltask_time);
            viewHolder.itemalltask_get_num = (TextView) convertView.findViewById(R.id.itemalltask_get_num);
            viewHolder.itemalltask_get_num2 = (TextView) convertView.findViewById(R.id.itemalltask_get_num2);
            viewHolder.item_task_edit = (TextView) convertView.findViewById(R.id.item_task_edit);
            viewHolder.item_task_putin = (TextView) convertView.findViewById(R.id.item_task_putin);
            viewHolder.item_look = (TextView) convertView.findViewById(R.id.item_look);
            viewHolder.item_looked = (TextView) convertView.findViewById(R.id.item_looked);
            viewHolder.item_lin_putin = (LinearLayout) convertView.findViewById(R.id.item_lin_putin);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (list != null && !list.isEmpty()) {
            PutInTaskInfo putInTaskInfo = list.get(position);
            if (!Tools.isEmpty(putInTaskInfo.getActivity_status())) { //1：草稿箱未发布；2：投放中；3：已结束

                if (putInTaskInfo.getActivity_status().equals("2")) {
                    viewHolder.item_task_putin.setVisibility(View.GONE);
                    viewHolder.item_look.setVisibility(View.GONE);
                    viewHolder.item_lin_putin.setVisibility(View.VISIBLE);
                    viewHolder.itemalltask_get_num2.setVisibility(View.GONE);
                } else if (putInTaskInfo.getActivity_status().equals("1")) {
                    viewHolder.item_task_putin.setVisibility(View.VISIBLE);
                    viewHolder.item_look.setVisibility(View.GONE);
                    viewHolder.item_lin_putin.setVisibility(View.GONE);
                    viewHolder.itemalltask_get_num2.setVisibility(View.GONE);
                } else if (putInTaskInfo.getActivity_status().equals("3")) {
                    viewHolder.itemalltask_get_num2.setVisibility(View.VISIBLE);
                    viewHolder.item_task_putin.setVisibility(View.GONE);
                    viewHolder.item_look.setVisibility(View.VISIBLE);
                    viewHolder.item_lin_putin.setVisibility(View.GONE);
                }
            }

            if (!Tools.isEmpty(putInTaskInfo.getActivity_name())) {
                viewHolder.itemalltask_name.setText(putInTaskInfo.getActivity_name());
            }

            if (!Tools.isEmpty(putInTaskInfo.getTarget_num())) {
                viewHolder.itemalltask_target_num.setText("目标参与人数：" + putInTaskInfo.getTarget_num() + "人");
            }
            if (!Tools.isEmpty(putInTaskInfo.getBegin_date()) && !Tools.isEmpty(putInTaskInfo.getEnd_date())) {
                viewHolder.itemalltask_time.setText("活动起止日期：" + putInTaskInfo.getBegin_date() + "~" + putInTaskInfo.getEnd_date());
            }
            if (!Tools.isEmpty(putInTaskInfo.getGet_num())) {
                viewHolder.itemalltask_get_num.setText("已参与人数/人：" + putInTaskInfo.getGet_num() + "");
                viewHolder.itemalltask_get_num2.setText("已参与人数/人" + putInTaskInfo.getGet_num() + "");
            }

            viewHolder.item_task_putin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //投放
                    iPutInTask.PutIntask(position);
                }
            });

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
            viewHolder.item_task_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iPutInTask.Edit(position);
                }
            });
        }
        viewHolder.item_look.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iPutInTask.Look(position);
            }
        });
        return convertView;
    }

    private IPutInTask iPutInTask;

    public interface IPutInTask {
        void PutIntask(int position);

        void Look(int position);

        void Edit(int position);
    }

    public void setiPutInTask(IPutInTask iPutInTask) {
        this.iPutInTask = iPutInTask;
    }

    class ViewHolder {
        TextView itemalltask_name, itemalltask_target_num, itemalltask_time, itemalltask_get_num, itemalltask_get_num2;
        TextView item_task_edit, item_task_putin, item_look, item_looked;
        ImageView itemcorpstask_ico;
        LinearLayout item_lin_putin;
    }
}
