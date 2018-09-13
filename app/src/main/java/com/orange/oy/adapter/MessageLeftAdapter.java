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

/**
 * 我的消息左侧列表
 */
public class MessageLeftAdapter extends BaseAdapter {
    private ArrayList<MessageLeftInfo> list;
    private Context context;
    private boolean isDelete;
//    private Set<Integer> selectSet;

    public MessageLeftAdapter(Context context, ArrayList<MessageLeftInfo> list) {
        this.context = context;
        this.list = list;
//        selectSet = new HashSet<>();
        isDelete = false;
    }

    public void setDelete(boolean isDelete) {
        this.isDelete = isDelete;
//        if (!isDelete) {
//            selectSet.clear();
//        }
        notifyDataSetChanged();
    }

//    public Set<Integer> getSelectSet() {
//        return selectSet;
//    }

    public boolean isDelete() {
        return isDelete;
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
            convertView = Tools.loadLayout(context, R.layout.item_message_left);
            viewHolder.title = (TextView) convertView.findViewById(R.id.item_mesleft_title);
            viewHolder.message = (TextView) convertView.findViewById(R.id.item_mesleft_message);
            viewHolder.message2 = (TextView) convertView.findViewById(R.id.item_mesleft_message2);
            viewHolder.select = (ImageView) convertView.findViewById(R.id.item_mesleft_select);
            viewHolder.time = (TextView) convertView.findViewById(R.id.item_mesleft_time);
            viewHolder.updown = (ImageView) convertView.findViewById(R.id.item_mesleft_updown);
            viewHolder.layout = convertView.findViewById(R.id.item_mesleft_layout);
//            viewHolder.settingListener();
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MessageLeftInfo messageLeftInfo = list.get(position);
        if (isDelete) {
            viewHolder.select.setVisibility(View.VISIBLE);
            if (messageLeftInfo.isSelect()) {
                viewHolder.select.setImageResource(R.mipmap.message_del_yes);
//                selectSet.add(position);
            } else {
                viewHolder.select.setImageResource(R.mipmap.message_del_no);
//                if (selectSet.contains(position)) {
//                    selectSet.remove(position);
//                }
            }
        } else {
            viewHolder.select.setVisibility(View.GONE);
        }
        viewHolder.title.setText(messageLeftInfo.getTitle());
        viewHolder.time.setText(messageLeftInfo.getTime());
        String message = messageLeftInfo.getMessage();
        String message2 = messageLeftInfo.getMessage2();
        viewHolder.message.setText(message);
        if ("6".equals(messageLeftInfo.getFlag())) {
            viewHolder.message.setSingleLine(false);
            viewHolder.message.setMaxLines(2);
        } else {
            viewHolder.message.setSingleLine(true);
        }
        if (TextUtils.isEmpty(message2)) {//如果没有扩展消息
            messageLeftInfo.setIsOpen(false);
            viewHolder.updown.setVisibility(View.GONE);
            viewHolder.message2.setVisibility(View.GONE);
        } else {
            viewHolder.updown.setVisibility(View.VISIBLE);
            viewHolder.message2.setText(message2);
            if (messageLeftInfo.isOpen()) {
                viewHolder.message2.setVisibility(View.VISIBLE);
                viewHolder.updown.setImageResource(R.mipmap.finishdt2);
            } else {
                viewHolder.message2.setVisibility(View.GONE);
                viewHolder.updown.setImageResource(R.mipmap.finishdt1);
            }
        }
        return convertView;
    }

    class ViewHolder {
        TextView title, message, message2, time;
        ImageView select, updown;
        View layout;

//        void settingListener() {
//            select.setOnClickListener(this);
//        }
//
//        public void onClick(View v) {
//            switch (v.getId()) {
//                case R.id.item_mesleft_select: {
//                    int position = (int) select.getTag();
//                    if (selectSet.contains(position)) {
//                        selectSet.remove(position);
//                        select.setImageResource(R.mipmap.message_del_no);
//                    } else {
//                        selectSet.add(position);
//                        select.setImageResource(R.mipmap.message_del_yes);
//                    }
//                }
//                break;
//            }
//        }
    }
}
