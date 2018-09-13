package com.orange.oy.adapter.mycorps_314;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.shakephoto.MyMessageDetailInfo;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/8/23.
 * 我的消息详情 V3.20 个人用户
 */

public class MyMessageDetailAdapter2 extends BaseAdapter {
    private Context context;
    private ArrayList<MyMessageDetailInfo> list;

    public MyMessageDetailAdapter2(Context context, ArrayList<MyMessageDetailInfo> list) {
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
            convertView = Tools.loadLayout(context, R.layout.item_mymessage2);
            viewHolder.itemessage2_title = (TextView) convertView.findViewById(R.id.itemessage2_title);
            viewHolder.itemessage2_name = (TextView) convertView.findViewById(R.id.itemessage2_name);
            viewHolder.itemessage2_nickname = (TextView) convertView.findViewById(R.id.itemessage2_nickname);
            viewHolder.itemessage2_time = (TextView) convertView.findViewById(R.id.itemessage2_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MyMessageDetailInfo detailInfo = list.get(position);
        viewHolder.itemessage2_title.setText(detailInfo.getProject_name());
        viewHolder.itemessage2_name.setText(detailInfo.getOutlet_name());
        viewHolder.itemessage2_time.setText("执行时间：" + detailInfo.getBegin_date() + "~" + detailInfo.getEnd_date());
        viewHolder.itemessage2_nickname.setText("用户昵称：" + detailInfo.getUser_name());
        return convertView;
    }

    class ViewHolder {
        private TextView itemessage2_title, itemessage2_name, itemessage2_nickname, itemessage2_time;
    }
}
