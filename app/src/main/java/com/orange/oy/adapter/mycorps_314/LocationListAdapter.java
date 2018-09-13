package com.orange.oy.adapter.mycorps_314;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.LocationListInfo;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/6/27.
 * 位置列表
 */

public class LocationListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<LocationListInfo> list;
    private boolean isDelete;
    private boolean isClick1;
    private boolean isClick2;

    public LocationListAdapter(Context context, ArrayList<LocationListInfo> list) {
        this.context = context;
        this.list = list;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public boolean isClick1() {
        return isClick1;
    }

    public boolean isClick2() {
        return isClick2;
    }

    public void clearClick() {
        isClick1 = false;
        isClick2 = false;
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
            convertView = Tools.loadLayout(context, R.layout.item_locationlist);
            viewHolder.itemlist_placename = (TextView) convertView.findViewById(R.id.itemlist_placename);
            viewHolder.itemlist_address = (TextView) convertView.findViewById(R.id.itemlist_address);
            viewHolder.itemlist_delete = (TextView) convertView.findViewById(R.id.itemlist_delete);
            viewHolder.itemlist_ly = convertView.findViewById(R.id.itemlist_ly);
            viewHolder.itemlist_move = convertView.findViewById(R.id.itemlist_move);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        LocationListInfo locationListInfo = list.get(position);
        if (isDelete) {
            viewHolder.itemlist_move.scrollTo(150, 0);
        } else {
            viewHolder.itemlist_move.scrollTo(0, 0);
        }
        viewHolder.itemlist_address.setText(locationListInfo.getAddress());
        viewHolder.itemlist_placename.setText(locationListInfo.getAddress_name());
        if ("-1".equals(locationListInfo.getDai_id())) {
            viewHolder.itemlist_ly.setVisibility(View.VISIBLE);
            viewHolder.itemlist_move.setVisibility(View.GONE);
        } else {
            viewHolder.itemlist_ly.setVisibility(View.GONE);
            viewHolder.itemlist_move.setVisibility(View.VISIBLE);
        }
        viewHolder.itemlist_delete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isClick1 = true;
                return false;
            }
        });
        viewHolder.itemlist_ly.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isClick2 = true;
                return false;
            }
        });
        return convertView;
    }

    class ViewHolder {
        private TextView itemlist_placename, itemlist_address, itemlist_delete;
        private View itemlist_ly, itemlist_move;
    }
}
