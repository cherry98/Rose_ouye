package com.orange.oy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.MessageLeftInfo;

import java.util.ArrayList;

public class MessageRightAdapter extends BaseAdapter {
    private ArrayList<MessageLeftInfo> list;
    private Context context;

    public MessageRightAdapter(Context context, ArrayList<MessageLeftInfo> list) {
        this.context = context;
        this.list = list;
    }

    public void resetList(ArrayList<MessageLeftInfo> list) {
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
            convertView = Tools.loadLayout(context, R.layout.item_message_right);
            viewHolder.item_msgright_title = (TextView) convertView.findViewById(R.id.item_msgright_title);
//            viewHolder.item_msgright_message = (TextView) convertView.findViewById(R.id.item_msgright_message);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MessageLeftInfo messageLeftInfo = list.get(position);
        viewHolder.item_msgright_title.setText(messageLeftInfo.getTitle());
//        viewHolder.item_msgright_message.setText(String.format(context.getResources().getString(R.string
//                .message_right_msg), messageLeftInfo.getMessage()));
        return convertView;
    }

    class ViewHolder {
        TextView item_msgright_title, item_msgright_message;
    }
}
