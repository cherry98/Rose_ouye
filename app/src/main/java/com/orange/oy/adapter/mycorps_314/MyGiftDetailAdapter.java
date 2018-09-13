package com.orange.oy.adapter.mycorps_314;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.MyGiftDetailInfo;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/8/28.
 * 我的礼品物流详情 V3.20(多个物流信息时使用)
 */

public class MyGiftDetailAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<MyGiftDetailInfo> list;

    public MyGiftDetailAdapter(Context context, ArrayList<MyGiftDetailInfo> list) {
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
            convertView = Tools.loadLayout(context, R.layout.item_giftdetail);
            viewHolder.itemgiftdetail_name = (TextView) convertView.findViewById(R.id.itemgiftdetail_name);
            viewHolder.itemgiftdetail_source = (TextView) convertView.findViewById(R.id.itemgiftdetail_source);
            viewHolder.itemgiftdetail_number = (TextView) convertView.findViewById(R.id.itemgiftdetail_number);
            viewHolder.itemgiftdetail_phone = (TextView) convertView.findViewById(R.id.itemgiftdetail_phone);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MyGiftDetailInfo myGiftDetailInfo = list.get(position);
        viewHolder.itemgiftdetail_name.setText(myGiftDetailInfo.getGift_name());
        viewHolder.itemgiftdetail_source.setText("承运来源：" + myGiftDetailInfo.getExpress_company());
        viewHolder.itemgiftdetail_number.setText("运单编号：" + myGiftDetailInfo.getExpress_number());
        viewHolder.itemgiftdetail_phone.setText("官方电话：" + myGiftDetailInfo.getOfficial_phone());
        return convertView;
    }

    class ViewHolder {
        private TextView itemgiftdetail_name, itemgiftdetail_source, itemgiftdetail_number, itemgiftdetail_phone;
    }
}
