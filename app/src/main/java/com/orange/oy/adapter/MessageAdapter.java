package com.orange.oy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.MessageLeftInfo;
import com.orange.oy.view.CollapsibleTextView;

import java.util.ArrayList;

/**
 * Created by xiedongyan on 2017/9/9.
 */

/**
 * 消息列表适配器
 */
public class MessageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<MessageLeftInfo> list;
    private OnShowItemClickListener onShowItemClickListener;

    public MessageAdapter(Context context, ArrayList<MessageLeftInfo> list) {
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
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = Tools.loadLayout(context, R.layout.item_message);
            viewHolder.itemmessage_img = (ImageView) convertView.findViewById(R.id.itemmessage_img);
            viewHolder.itemmessage_spread = (CollapsibleTextView) convertView.findViewById(R.id.itemmessage_spread);
            viewHolder.itemmessage_time = (TextView) convertView.findViewById(R.id.itemmessage_time);
            viewHolder.itemmessage_title = (TextView) convertView.findViewById(R.id.itemmessage_title);
            viewHolder.itemmessage_checkbox = (CheckBox) convertView.findViewById(R.id.itemmessage_checkbox);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //设置数据
        final MessageLeftInfo messageLeftInfo = list.get(position);
        viewHolder.itemmessage_title.setText(messageLeftInfo.getTitle());
        viewHolder.itemmessage_time.setText(messageLeftInfo.getTime());
        String type = messageLeftInfo.getCode();
        if ("3".equals(type)) {//资料已回收
            viewHolder.itemmessage_img.setImageResource(R.mipmap.message_recovery);
        } else if ("6".equals(type)) {//支付成功
            viewHolder.itemmessage_img.setImageResource(R.mipmap.message_success);
        } else if ("7".equals(type)) {//支付失败
            viewHolder.itemmessage_img.setImageResource(R.mipmap.message_fail);
        } else if ("8".equals(type)) {//偶米兑换
            viewHolder.itemmessage_img.setImageResource(R.mipmap.oumi_money);
        } else {
            viewHolder.itemmessage_img.setImageResource(R.mipmap.message_system);
        }
        viewHolder.itemmessage_spread.setDesc(messageLeftInfo.getMessage(), TextView.BufferType.NORMAL, true);
        boolean isShow = messageLeftInfo.isShow();//是否显示复选框
        if (isShow) {
            viewHolder.itemmessage_checkbox.setVisibility(View.VISIBLE);
            viewHolder.itemmessage_img.setVisibility(View.GONE);
        } else {
            viewHolder.itemmessage_checkbox.setVisibility(View.GONE);
            viewHolder.itemmessage_img.setVisibility(View.VISIBLE);
        }
        viewHolder.itemmessage_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    messageLeftInfo.setChecked(true);
                } else {
                    messageLeftInfo.setChecked(false);
                }
                onShowItemClickListener.onShowItemClick(messageLeftInfo);
            }
        });
        viewHolder.itemmessage_checkbox.setChecked(messageLeftInfo.isChecked());
        return convertView;
    }

    class ViewHolder {
        private CollapsibleTextView itemmessage_spread;
        private ImageView itemmessage_img;
        private TextView itemmessage_title, itemmessage_time;
        private CheckBox itemmessage_checkbox;
    }

    public interface OnShowItemClickListener {
        void onShowItemClick(MessageLeftInfo messageLeftInfo);
    }

    public void setOnShowItemClickListener(OnShowItemClickListener onShowItemClickListener) {
        this.onShowItemClickListener = onShowItemClickListener;
    }
}