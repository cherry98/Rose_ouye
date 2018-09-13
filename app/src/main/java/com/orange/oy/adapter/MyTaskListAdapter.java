package com.orange.oy.adapter;

import android.content.Context;
import android.text.TextUtils;
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
 * Created by xiedongyan on 2017/3/7.
 */

/**
 * 我的任务列表适配器
 */
public class MyTaskListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<TaskNewInfo> list;

    public MyTaskListAdapter(Context context, ArrayList<TaskNewInfo> list) {
        this.context = context;
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
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = Tools.loadLayout(context, R.layout.item_tasknew);
            viewHolder.itemtasknew_layout = (LinearLayout) convertView.findViewById(R.id.itemtasknew_layout);
            viewHolder.itemtasknew_name = (TextView) convertView.findViewById(R.id.itemtasknew_name);
            viewHolder.itemtasknew_price = (TextView) convertView.findViewById(R.id.itemtasknew_price);
            viewHolder.itemtasknew_titletype = (TextView) convertView.findViewById(R.id.itemtasknew_titletype);
            viewHolder.itemtasknew_time = (TextView) convertView.findViewById(R.id.itemtasknew_time);
            viewHolder.itemtasknew_pname = (TextView) convertView.findViewById(R.id.itemtasknew_pname);
            viewHolder.itemtasknew_yuan2 = (TextView) convertView.findViewById(R.id.itemtasknew_yuan2);
            viewHolder.itemtasknew_certification = (ImageView) convertView.findViewById(R.id.itemtasknew_certification);
//            viewHolder.itemtasknew_splitline = convertView.findViewById(R.id.itemtasknew_splitline);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (position == 0) {
            viewHolder.itemtasknew_layout.setVisibility(View.VISIBLE);
        } else {
            viewHolder.itemtasknew_layout.setVisibility(View.GONE);
        }
        if (position == list.size() - 1) {
//            viewHolder.itemtasknew_splitline.setVisibility(View.GONE);
        } else {
//            viewHolder.itemtasknew_splitline.setVisibility(View.VISIBLE);
        }
        TaskNewInfo taskNewInfo = list.get(position);
        viewHolder.itemtasknew_name.setText(taskNewInfo.getProject_name());
        viewHolder.itemtasknew_price.setText(taskNewInfo.getMin_reward() + "~" + taskNewInfo.getMax_reward());
        viewHolder.itemtasknew_yuan2.setText(taskNewInfo.getMoney_unit());
        viewHolder.itemtasknew_titletype.setText("全部项目");
        if ("null".equals(taskNewInfo.getPublish_time()) || TextUtils.isEmpty(taskNewInfo.getPublish_time())) {
            viewHolder.itemtasknew_time.setVisibility(View.GONE);
        } else {
            viewHolder.itemtasknew_time.setText("发布时间：" + taskNewInfo.getPublish_time());
        }
        viewHolder.itemtasknew_pname.setText("发布商家：" + "【" + taskNewInfo.getProject_person() + "】");
        String certification = taskNewInfo.getCertification();
        if ("1".equals(certification)) {//已认证
            viewHolder.itemtasknew_certification.setImageResource(R.mipmap.identification);
        }
        return convertView;
    }

    class ViewHolder {
        private LinearLayout itemtasknew_layout;
        private TextView itemtasknew_name, itemtasknew_price, itemtasknew_titletype,
                itemtasknew_pname, itemtasknew_time, itemtasknew_yuan2;
        private ImageView itemtasknew_certification;
//        private View itemtasknew_splitline;
    }
}
