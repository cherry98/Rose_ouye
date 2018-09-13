package com.orange.oy.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.TaskNewInfo;

import java.util.ArrayList;

/**
 * Created by xiedongyan on 2017/3/3.
 */

/**
 * 新的任务页面适配器
 */
public class TaskNewAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<TaskNewInfo> list;
    private boolean isAssign;//是否是指派的任务页面列表
    private int number, number2;//推荐项目，体验项目数量

    public void setNumber(int number, int number2) {
        this.number = number;
        this.number2 = number2;
    }

    public TaskNewAdapter(Context context, ArrayList<TaskNewInfo> list) {//演练，指派
        this.context = context;
        this.list = list;
    }

    public TaskNewAdapter(Context context, ArrayList<TaskNewInfo> list, boolean isAssign) {
        this.context = context;
        this.list = list;
        this.isAssign = isAssign;
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
            convertView = Tools.loadLayout(context, R.layout.item_tasknew);
            viewHolder.item_tasknew = (LinearLayout) convertView.findViewById(R.id.item_tasknew);
            viewHolder.itemtasknew_layout = (LinearLayout) convertView.findViewById(R.id.itemtasknew_layout);
            viewHolder.itemtasknew_name = (TextView) convertView.findViewById(R.id.itemtasknew_name);
            viewHolder.itemtasknew_price = (TextView) convertView.findViewById(R.id.itemtasknew_price);
            viewHolder.itemtasknew_time = (TextView) convertView.findViewById(R.id.itemtasknew_time);
            viewHolder.itemtasknew_pname = (TextView) convertView.findViewById(R.id.itemtasknew_pname);
            viewHolder.itemtasknew_certification = (ImageView) convertView.findViewById(R.id.itemtasknew_certification);
            viewHolder.itemtasknew_yuan2 = (TextView) convertView.findViewById(R.id.itemtasknew_yuan2);
            viewHolder.itemtasknew_titletype = (TextView) convertView.findViewById(R.id.itemtasknew_titletype);
            viewHolder.itemtasknew_splitline = convertView.findViewById(R.id.itemtasknew_splitline);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        TaskNewInfo taskNewInfo = list.get(position);
        if (!isAssign) {
            if (number != 0 && number2 != 0) {
                if (position == 0) {
                    viewHolder.itemtasknew_titletype.setText("推荐任务");
                    viewHolder.itemtasknew_layout.setVisibility(View.VISIBLE);
                } else if (position == number) {
                    viewHolder.itemtasknew_layout.setVisibility(View.VISIBLE);
                    viewHolder.itemtasknew_titletype.setText("体验任务");
                } else {
                    viewHolder.itemtasknew_layout.setVisibility(View.GONE);
                }
            } else if (number == 0 && number2 != 0) {
                if (position == 0) {
                    viewHolder.itemtasknew_layout.setVisibility(View.VISIBLE);
                    viewHolder.itemtasknew_titletype.setText("体验任务");
                } else {
                    viewHolder.itemtasknew_layout.setVisibility(View.GONE);
                }
            } else if (number != 0 && number2 == 0) {
                if (position == 0) {
                    viewHolder.itemtasknew_layout.setVisibility(View.VISIBLE);
                    viewHolder.itemtasknew_titletype.setText("推荐任务");
                } else {
                    viewHolder.itemtasknew_layout.setVisibility(View.GONE);
                }
            } else {
                viewHolder.itemtasknew_layout.setVisibility(View.GONE);
            }
        } else {
            viewHolder.itemtasknew_layout.setVisibility(View.GONE);
        }
        if (!isAssign) {
            if (position == number - 1 || position == list.size() - 1) {
                viewHolder.itemtasknew_splitline.setVisibility(View.GONE);
            }
            viewHolder.itemtasknew_price.setVisibility(View.VISIBLE);
            viewHolder.itemtasknew_yuan2.setVisibility(View.VISIBLE);
        } else {
            viewHolder.item_tasknew.setBackgroundResource(R.color.app_background2);
            viewHolder.itemtasknew_price.setVisibility(View.GONE);
            viewHolder.itemtasknew_yuan2.setVisibility(View.GONE);
        }
        viewHolder.itemtasknew_price.setText(taskNewInfo.getMin_reward() + "~" + taskNewInfo.getMax_reward());
        viewHolder.itemtasknew_yuan2.setText(taskNewInfo.getMoney_unit());
        viewHolder.itemtasknew_name.setText(taskNewInfo.getProject_name());
        if ("null".equals(taskNewInfo.getPublish_time()) || TextUtils.isEmpty(taskNewInfo.getPublish_time())) {
            viewHolder.itemtasknew_time.setVisibility(View.GONE);
        } else {
            viewHolder.itemtasknew_time.setText("发布时间：" + taskNewInfo.getPublish_time());
        }
        viewHolder.itemtasknew_pname.setText("发布商家：" + "【" + taskNewInfo.getProject_person() + "】");
        String certification = taskNewInfo.getCertification();
        if ("1".equals(certification)) {//已认证
            viewHolder.itemtasknew_certification.setImageResource(R.mipmap.identification);
        } else {
            viewHolder.itemtasknew_certification.setVisibility(View.GONE);
        }
        Tools.d("number:" + number);
        return convertView;
    }

    class ViewHolder {
        private LinearLayout itemtasknew_layout, item_tasknew;
        private TextView itemtasknew_name, itemtasknew_price,
                itemtasknew_pname, itemtasknew_time, itemtasknew_yuan2, itemtasknew_titletype;
        private ImageView itemtasknew_certification;
        private View itemtasknew_splitline;
    }
}
