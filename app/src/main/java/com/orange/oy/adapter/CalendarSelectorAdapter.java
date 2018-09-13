package com.orange.oy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.CalendarSelectInfo;

import java.util.ArrayList;

public class CalendarSelectorAdapter extends BaseAdapter {
    private ArrayList<CalendarSelectInfo> list;
    private Context context;

    public CalendarSelectorAdapter(Context context, ArrayList<CalendarSelectInfo> list) {
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
            convertView = Tools.loadLayout(context, R.layout.item_calendar);
            viewHolder.title = (TextView) convertView.findViewById(R.id.item_calendar_title);
            viewHolder.detail = (TextView) convertView.findViewById(R.id.item_calendar_value);
            viewHolder.layout = convertView.findViewById(R.id.item_calendar_child);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        CalendarSelectInfo calendarSelectInfo = list.get(position);
        if (calendarSelectInfo.getFlag() == 0) {
            viewHolder.layout.setVisibility(View.GONE);
            viewHolder.title.setVisibility(View.VISIBLE);
            viewHolder.title.setText(calendarSelectInfo.getParentName());
        } else {
            viewHolder.layout.setVisibility(View.VISIBLE);
            viewHolder.title.setVisibility(View.GONE);
            viewHolder.detail.setText(calendarSelectInfo.getChildNum() + " " + calendarSelectInfo.getChildNAme() +
                    "\n" + calendarSelectInfo.getChildDetail());
        }
        return convertView;
    }

    class ViewHolder {
        TextView title, detail;
        View layout;
    }
}
