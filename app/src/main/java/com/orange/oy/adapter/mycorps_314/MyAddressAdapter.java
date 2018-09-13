package com.orange.oy.adapter.mycorps_314;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.shakephoto.ReceiveAddressInfo;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/8/23.
 * 我的->收货地址 V3.20
 */

public class MyAddressAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ReceiveAddressInfo> list;
    private boolean isDelete;
    private boolean isClick;
    private int delWidth;

    public MyAddressAdapter(Context context, ArrayList<ReceiveAddressInfo> list) {
        this.context = context;
        this.list = list;
        delWidth = (int) context.getResources().getDimension(R.dimen.apptitle_height);
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean click) {
        isClick = click;
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
            convertView = Tools.loadLayout(context, R.layout.item_myaddress);
            viewHolder.itemmyaddr_name = (TextView) convertView.findViewById(R.id.itemmyaddr_name);
            viewHolder.itemmyaddr_phone = (TextView) convertView.findViewById(R.id.itemmyaddr_phone);
            viewHolder.itemmyaddr_default = (TextView) convertView.findViewById(R.id.itemmyaddr_default);
            viewHolder.itemmyaddr_addr = (TextView) convertView.findViewById(R.id.itemmyaddr_addr);
            viewHolder.itemmyaddr_del = (TextView) convertView.findViewById(R.id.itemmyaddr_del);
            viewHolder.main = convertView.findViewById(R.id.main);
            viewHolder.itemmyaddr_add = convertView.findViewById(R.id.itemmyaddr_add);
            viewHolder.itemmyaddr_ly = convertView.findViewById(R.id.itemmyaddr_ly);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.main.scrollTo(0, 0);
        ReceiveAddressInfo receiveAddressInfo = list.get(position);
        if ("-1".equals(receiveAddressInfo.getAddress_id())) {
            viewHolder.itemmyaddr_add.setVisibility(View.VISIBLE);
            viewHolder.main.setVisibility(View.GONE);
        } else {
            viewHolder.itemmyaddr_add.setVisibility(View.GONE);
            viewHolder.main.setVisibility(View.VISIBLE);
            String default_state = receiveAddressInfo.getDefault_state();
            if ("1".equals(default_state)) {
                viewHolder.itemmyaddr_default.setVisibility(View.VISIBLE);
            } else {
                viewHolder.itemmyaddr_default.setVisibility(View.GONE);
            }
            viewHolder.itemmyaddr_name.setText(receiveAddressInfo.getConsignee_name());
            viewHolder.itemmyaddr_phone.setText(receiveAddressInfo.getConsignee_phone());
            viewHolder.itemmyaddr_addr.setText(receiveAddressInfo.getConsignee_address());
        }
        if (isDelete) {
            viewHolder.main.scrollTo(delWidth, 0);
            viewHolder.itemmyaddr_ly.setBackgroundResource(R.drawable.itemcorpsnotice_bg2);
            viewHolder.itemmyaddr_del.setVisibility(View.VISIBLE);
            viewHolder.itemmyaddr_del.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    isClick = true;
                    return false;
                }
            });
        } else {
            viewHolder.main.scrollTo(0, 0);
            viewHolder.itemmyaddr_del.setVisibility(View.GONE);
            viewHolder.itemmyaddr_ly.setBackgroundResource(R.drawable.itemcorpsnotice_bg1);
        }
        return convertView;
    }

    class ViewHolder {
        private TextView itemmyaddr_name, itemmyaddr_phone, itemmyaddr_default, itemmyaddr_addr, itemmyaddr_del;
        private View itemmyaddr_add, itemmyaddr_ly, main;
    }

}
