package com.orange.oy.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.MessageLeftInfo;

import java.util.ArrayList;

public class MessageMiddleAdapter extends BaseAdapter {
    private ArrayList<MessageLeftInfo> list;
    private Context context;

    public MessageMiddleAdapter(Context context, ArrayList<MessageLeftInfo> list) {
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

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = Tools.loadLayout(context, R.layout.item_message_middle);
            viewHolder.item_msgmiddle_title = (TextView) convertView.findViewById(R.id.item_mesmiddle_title);
            viewHolder.item_msgmiddle_message = (TextView) convertView.findViewById(R.id.item_mesmiddle_message);
            viewHolder.item_mesmiddle_time = (TextView) convertView.findViewById(R.id.item_mesmiddle_time);
//            viewHolder.item_mesmiddle_img = (ImageView) convertView.findViewById(R.id.item_mesmiddle_img);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MessageLeftInfo messageLeftInfo = list.get(position);
        viewHolder.item_msgmiddle_title.setText(messageLeftInfo.getTitle());
        if (TextUtils.isEmpty(messageLeftInfo.getMessage())) {
            viewHolder.item_msgmiddle_message.setVisibility(View.GONE);
        } else {
            viewHolder.item_msgmiddle_message.setVisibility(View.VISIBLE);
            viewHolder.item_msgmiddle_message.setText(messageLeftInfo.getMessage());
        }
        viewHolder.item_mesmiddle_time.setText(messageLeftInfo.getTime());
//        String flag = messageLeftInfo.getFlag();
//        if (flag.equals("公告")) {
//            viewHolder.item_mesmiddle_img.setImageResource(R.mipmap.message_left_ico1);
//        } else {
//            viewHolder.item_mesmiddle_img.setImageResource(R.mipmap.message_gonggao);
//        }
        return convertView;
    }

    class ViewHolder {
        TextView item_msgmiddle_title, item_msgmiddle_message, item_mesmiddle_time;
        ImageView item_mesmiddle_img;
    }
}
