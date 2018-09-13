package com.orange.oy.adapter.mycorps_314;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.shakephoto.PrizeCardInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/8/23.
 * 我的礼品页面 V3.20
 */

public class MyGiftAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<PrizeCardInfo> list;
    private ImageLoader imageLoader;

    public MyGiftAdapter(Context context, ArrayList<PrizeCardInfo> list) {
        this.context = context;
        this.list = list;
        imageLoader = new ImageLoader(context);
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
            convertView = Tools.loadLayout(context, R.layout.item_mygift);
            viewHolder.itemmygift_name = (TextView) convertView.findViewById(R.id.itemmygift_name);
            viewHolder.itemmygift_source = (TextView) convertView.findViewById(R.id.itemmygift_source);
            viewHolder.itemmygift_number = (TextView) convertView.findViewById(R.id.itemmygift_number);
            viewHolder.itemmygift_phone = (TextView) convertView.findViewById(R.id.itemmygift_phone);
            viewHolder.itemmygift_state = (TextView) convertView.findViewById(R.id.itemmygift_state);
            viewHolder.itemmygift_img = (ImageView) convertView.findViewById(R.id.itemmygift_img);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        PrizeCardInfo prizeCardInfo = list.get(position);
        viewHolder.itemmygift_name.setText(prizeCardInfo.getGift_name());
        String order_no = prizeCardInfo.getOrder_no();
        if (!Tools.isEmpty(order_no)) {
            viewHolder.itemmygift_source.setText("订单编号：" + order_no);
        }
        String consignee_name = prizeCardInfo.getConsignee_name();
        if (!Tools.isEmpty(consignee_name)) {
            viewHolder.itemmygift_number.setText("收件姓名：" + consignee_name);
        }
        String asconsignee_phone = prizeCardInfo.getAsconsignee_phone();
        if (!Tools.isEmpty(asconsignee_phone)) {
            viewHolder.itemmygift_phone.setText("收件电话：" + asconsignee_phone);
        }
        String state = prizeCardInfo.getDelivery_state();
        if ("1".equals(state)) {
            state = "已发货";
        } else {
            state = "未发货";
        }
        viewHolder.itemmygift_state.setText(state);
        imageLoader.setShowWH(200).DisplayImage(Urls.Endpoint3 + prizeCardInfo.getImg_url(), viewHolder.itemmygift_img);
        return convertView;
    }

    class ViewHolder {
        private TextView itemmygift_name, itemmygift_source, itemmygift_number, itemmygift_phone, itemmygift_state;
        private ImageView itemmygift_img;
    }
}
