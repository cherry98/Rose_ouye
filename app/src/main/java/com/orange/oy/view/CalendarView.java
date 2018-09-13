package com.orange.oy.view;

import android.content.Context;
import android.graphics.Color;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.CalendarItem;
import com.orange.oy.util.ChinaDate;

import java.util.Calendar;

public class CalendarView extends FrameLayout {
    //    public static final int orange = R.mipmap.u174;
//    public static final int lightblue = R.mipmap.u176;
//    public static final int green = R.mipmap.u178;
//    public static final int gray = R.mipmap.u180;
//    public static final int blue = R.mipmap.u182;
//    public static final int purple = R.mipmap.u217;
//    public static final int redPoint = R.mipmap.u168;
    public static int layoutHeight = 0;
    private static Calendar calendar;
    private FrameLayout layout;
    private TextView view_calendar_time;
    private TextView view_calendar_years;
    //    private ImageView view_calendar_bg;
    private LinearLayout view_calendar_tabs;

//    private int getColor(int index) {
//        switch (index) {
//            case 0:
//                return orange;
//            case 1:
//                return lightblue;
//            case 2:
//                return green;
//            case 3:
//                return blue;
//            case 4:
//                return gray;
//            case 5:
//                return purple;
//            default:
//                return orange;
//        }
//    }

    public CalendarView(Context context) {
        super(context);
        Tools.loadLayout(this, R.layout.view_calendar);
        if (layoutHeight == 0) {
//            layoutHeight = getHeight(context);
            layoutHeight = (int) context.getResources().getDimension(R.dimen.calendar_height);
        }
        view_calendar_years = (TextView) findViewById(R.id.view_calendar_years);
        view_calendar_time = (TextView) findViewById(R.id.view_calendar_time);
        view_calendar_tabs = (LinearLayout) findViewById(R.id.view_calendar_tabs);
//        view_calendar_bg = (ImageView) findViewById(R.id.view_calendar_bg);
        layout = (FrameLayout) findViewById(R.id.view_calendar_layout);
        LayoutParams lp = (LayoutParams) layout.getLayoutParams();
        lp.height = layoutHeight;
        layout.setLayoutParams(lp);
    }

    private Calendar getCalendar() {
        if (calendar == null)
            calendar = Calendar.getInstance();
        return calendar;
    }

    public void settingDay(String year, String month, String day) {
        view_calendar_time.setText(day);
        getCalendar();
        view_calendar_years.setText(ChinaDate.oneDay(Tools.StringToInt(year), Tools.StringToInt(month), Tools
                .StringToInt(day)));
    }

    public void settingDay(String year, String month, String day, boolean isFree, boolean isSelect, int[] schedule) {
        settingDay(year, month, day);
        isFree(isFree);
        isSelect(isSelect);
        settingTab(schedule);
    }

    public void settingTab(int[] schedule) {
//        view_calendar_tabs.removeAllViews();
//        int count = view_calendar_tabs.getChildCount();
//        int j = 0;
        view_calendar_years.setBackgroundResource(android.R.color.transparent);
        view_calendar_years.setTextColor(getResources().getColor(R.color.calendar_years));
        for (int i = 0; i < CalendarItem.scheduleLenght; i++) {
            if (schedule[i] == 1) {
                view_calendar_years.setBackgroundColor(Color.RED);
                view_calendar_years.setTextColor(Color.WHITE);
//                View view;
//                if (j < count) {
//                    view = view_calendar_tabs.getChildAt(j++);
//                    view.setBackgroundResource(getColor(i));
//                } else {
//                    view = new View(getContext());
//                    view.setBackgroundResource(getColor(i));
//                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams
//                            .MATCH_PARENT, (int) getResources().getDimension(R.dimen.calendartab_height));
//                    view_calendar_tabs.addView(view, lp);
//                }
            }
        }
//        if (j < count) {
//            int result = count - j;
//            for (int i = count - result; i < count; i++) {
//                view_calendar_tabs.removeViewAt(i);
//            }
//        }
    }

    public void clear() {
        view_calendar_years.setText("");
        view_calendar_time.setText("");
        view_calendar_tabs.removeAllViews();
        layout.setBackgroundColor(Color.WHITE);
    }

    public void isFree(boolean isFree) {
        if (isFree)
            view_calendar_time.setTextColor(getResources().getColor(R.color.calendar_free));
        else
            view_calendar_time.setTextColor(getResources().getColor(R.color.app_textcolor));
    }

    public void isSelect(boolean isSelect) {
        if (isSelect)
            layout.setBackgroundResource(R.drawable.calendar_sel);
        else
            layout.setBackgroundColor(Color.WHITE);
    }

    public static int getHeight(Context context) {
        int height = (Tools.getScreeInfoWidth(context) - 6 * (int) context.getResources().getDimension(R.dimen
                .calendar_margin)) / 7;
        return height + height / 5;
    }
}
