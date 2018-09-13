package com.orange.oy.adapter.mycorps_314;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.shakephoto.ReceiveAddressInfo;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/8/16.
 * 选择收货地址 V3.20
 */

public class ReceiveAddressAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ReceiveAddressInfo> list;
    private int mPosition = -1;

    public ReceiveAddressAdapter(Context context, ArrayList<ReceiveAddressInfo> list) {
        this.context = context;
        this.list = list;
    }

    public void setmPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
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
            convertView = Tools.loadLayout(context, R.layout.item_receiveaddr);
            viewHolder.itemreceive_name = (TextView) convertView.findViewById(R.id.itemreceive_name);
            viewHolder.itemreceive_phone = (TextView) convertView.findViewById(R.id.itemreceive_phone);
            viewHolder.itemreceive_default = (TextView) convertView.findViewById(R.id.itemreceive_default);
            viewHolder.itemreceive_addr = (TextView) convertView.findViewById(R.id.itemreceive_addr);
            viewHolder.itemreceive_add = convertView.findViewById(R.id.itemreceive_add);
            viewHolder.itemreceive_ly = convertView.findViewById(R.id.itemreceive_ly);
            viewHolder.itemreceive_check = (CheckBox) convertView.findViewById(R.id.itemreceive_check);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ReceiveAddressInfo receiveAddressInfo = list.get(position);
        if ("-1".equals(receiveAddressInfo.getAddress_id())) {
            viewHolder.itemreceive_add.setVisibility(View.VISIBLE);
            viewHolder.itemreceive_ly.setVisibility(View.GONE);
        } else {
            viewHolder.itemreceive_add.setVisibility(View.GONE);
            viewHolder.itemreceive_ly.setVisibility(View.VISIBLE);
            if (position == mPosition) {
                viewHolder.itemreceive_check.setChecked(true);
            } else {
                viewHolder.itemreceive_check.setChecked(false);
            }
            String default_state = receiveAddressInfo.getDefault_state();
            if ("1".equals(default_state)) {
                viewHolder.itemreceive_default.setVisibility(View.VISIBLE);
            } else {
                viewHolder.itemreceive_default.setVisibility(View.GONE);
            }
            viewHolder.itemreceive_name.setText(receiveAddressInfo.getConsignee_name());
            viewHolder.itemreceive_phone.setText(receiveAddressInfo.getConsignee_phone());
            viewHolder.itemreceive_addr.setText(receiveAddressInfo.getConsignee_address());
        }
        return convertView;
    }

    class ViewHolder {
        private TextView itemreceive_name, itemreceive_phone, itemreceive_default, itemreceive_addr;
        private CheckBox itemreceive_check;
        private View itemreceive_add, itemreceive_ly;
    }
}
