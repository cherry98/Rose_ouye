package com.orange.oy.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.TaskDetailLeftInfo;

import java.util.ArrayList;

import static com.orange.oy.R.id.item_img;
import static com.orange.oy.R.id.itemtaskgrab_grab;

/**
 * Created by xiedongyan on 2017/3/7.
 */

/**
 * 抢领任务适配器
 */
public class TaskGrabAdapter extends BaseAdapter {
    private Context context;
    private boolean isGrab = false;
    private ArrayList<TaskDetailLeftInfo> list;

    public TaskGrabAdapter(Context context, boolean isGrab, ArrayList<TaskDetailLeftInfo> list) {
        this.context = context;
        this.isGrab = isGrab;
        this.list = list;
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
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = Tools.loadLayout(context, R.layout.item_taskgrab);
            viewHolder.itemtaskgrab_addr = (TextView) convertView.findViewById(R.id.itemtaskgrab_addr);
            viewHolder.itemtaskgrab_num = (TextView) convertView.findViewById(R.id.itemtaskgrab_num);
            viewHolder.itemtaskgrab_name = (TextView) convertView.findViewById(R.id.itemtaskgrab_name);
            viewHolder.itemtaskgrab_looktime = (TextView) convertView.findViewById(R.id.itemtaskgrab_looktime);
            viewHolder.itemtaskgrab_grab = (TextView) convertView.findViewById(itemtaskgrab_grab);
            viewHolder.itemtaskgrab_price = (TextView) convertView.findViewById(R.id.itemtaskgrab_price);
            viewHolder.item_main = (LinearLayout) convertView.findViewById(R.id.item_main);
            viewHolder.item_right_lin = (LinearLayout) convertView.findViewById(R.id.item_right_lin);
            viewHolder.item_img = (ImageView) convertView.findViewById(R.id.item_img);
            viewHolder.iv_gift = (ImageView) convertView.findViewById(R.id.iv_gift);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (isGrab) {
            viewHolder.itemtaskgrab_grab.setVisibility(View.VISIBLE);
            viewHolder.itemtaskgrab_grab.setText("立即申请");
        }
        TaskDetailLeftInfo taskDetailLeftInfo = list.get(position);

        //"rob_state":"是否可领取，1为可以领取，0为已抢完",
        if (!Tools.isEmpty(taskDetailLeftInfo.getRob_state()) && "0".equals(taskDetailLeftInfo.getRob_state())) {
            viewHolder.item_img.setVisibility(View.VISIBLE);
            viewHolder.item_right_lin.setVisibility(View.GONE);
            viewHolder.item_main.setBackgroundResource(R.drawable.unify_input);

        } else {
            viewHolder.item_img.setVisibility(View.GONE);
            viewHolder.item_right_lin.setVisibility(View.VISIBLE);
            viewHolder.item_main.setBackgroundResource(R.drawable.itemalltask_background);
        }
        viewHolder.itemtaskgrab_addr.setText(taskDetailLeftInfo.getCity3());
        viewHolder.itemtaskgrab_num.setText(taskDetailLeftInfo.getId());
        viewHolder.itemtaskgrab_name.setText(taskDetailLeftInfo.getName());
        if (taskDetailLeftInfo.getTimedetail() != null) {
            viewHolder.itemtaskgrab_looktime.setVisibility(View.VISIBLE);
            viewHolder.itemtaskgrab_looktime.setText(taskDetailLeftInfo.getTimedetail());
        }
        // 奖励类型，1为现金，2为礼品，3为现金+礼品
        if (!Tools.isEmpty(taskDetailLeftInfo.getReward_type())) {

            if ("2".equals(taskDetailLeftInfo.getReward_type())) {
                viewHolder.itemtaskgrab_price.setVisibility(View.GONE);
                viewHolder.iv_gift.setVisibility(View.VISIBLE);
            } else if ("1".equals(taskDetailLeftInfo.getReward_type())) {
                viewHolder.iv_gift.setVisibility(View.GONE);
                viewHolder.itemtaskgrab_price.setVisibility(View.VISIBLE);
                viewHolder.itemtaskgrab_price.setText("¥" + taskDetailLeftInfo.getMoney());
            } else {
                viewHolder.iv_gift.setVisibility(View.VISIBLE);
                viewHolder.itemtaskgrab_price.setVisibility(View.VISIBLE);
                viewHolder.itemtaskgrab_price.setText("¥" + taskDetailLeftInfo.getMoney());
            }
        }

        return convertView;
    }

    class ViewHolder {
        private TextView itemtaskgrab_addr, itemtaskgrab_num, itemtaskgrab_name, itemtaskgrab_looktime,
                itemtaskgrab_grab, itemtaskgrab_price;
        private LinearLayout item_main, item_right_lin;
        private ImageView item_img, iv_gift;
    }
}
