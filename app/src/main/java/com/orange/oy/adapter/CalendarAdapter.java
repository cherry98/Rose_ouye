package com.orange.oy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.orange.oy.info.CalendarItem;
import com.orange.oy.view.CalendarView;

public class CalendarAdapter extends BaseAdapter {
    private CalendarItem[] list;
    private Context context;

    public CalendarAdapter(Context context, CalendarItem[] list) {
        this.context = context;
        this.list = list;
    }

    public int getCount() {
        return list.length;
    }

    public Object getItem(int position) {
        return list[position];
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        CalendarView calendarView;
        if (convertView == null) {
            calendarView = new CalendarView(context);
        } else {
            calendarView = (CalendarView) convertView;
        }
        CalendarItem item = list[position];
        if (item == null) {
            calendarView.clear();
        } else {
            calendarView.settingDay(item.getYear(), item.getMonth(), item.getDay(), (item.getWeek() == 1 || item
                    .getWeek() == 7), item.isSelect(), item.getSchedule());
        }
        return calendarView;
    }
}
