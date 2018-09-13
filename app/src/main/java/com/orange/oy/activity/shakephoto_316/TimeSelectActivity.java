package com.orange.oy.activity.shakephoto_316;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.view.AppTitle;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeSelectActivity extends BaseActivity implements View.OnClickListener, AppTitle.OnBackClickForAppTitle {


    private TextView timeselect_begin, timeselect_end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_select);
        initTitle();
        timeselect_begin = (TextView) findViewById(R.id.timeselect_begin);
        timeselect_end = (TextView) findViewById(R.id.timeselect_end);
        String begin_date = getIntent().getStringExtra("begin_date");
        String end_date = getIntent().getStringExtra("end_date");
        if (!Tools.isEmpty(begin_date)) {
            timeselect_begin.setText(begin_date);
            time_year = Tools.StringToInt(begin_date.substring(0, 4));
            time_month = Tools.StringToInt(begin_date.substring(5, 7)) - 1;
            time_day = Tools.StringToInt(begin_date.substring(8, 10));
        } else {
            Calendar calendar = Calendar.getInstance();
            time_year = calendar.get(Calendar.YEAR);
            time_month = calendar.get(Calendar.MONTH);
            time_day = calendar.get(Calendar.DAY_OF_MONTH);
            if (time_month > 0 && time_month < 9 && time_day > 0 && time_day < 10) {
                timeselect_begin.setText(time_year + "-0" + (time_month + 1) + "-0" + time_day);
            } else if (time_month > 0 && time_month < 9 && time_day >= 10) {
                timeselect_begin.setText(time_year + "-0" + (time_month + 1) + "-" + time_day);
            } else if (time_month >= 9 && time_day > 0 && time_day < 10) {
                timeselect_begin.setText(time_year + "-" + (time_month + 1) + "-0" + time_day);
            } else {
                timeselect_begin.setText(time_year + "-" + (time_month + 1) + "-" + time_day);
            }
        }
        if (!Tools.isEmpty(end_date)) {
            timeselect_end.setText(end_date);
        }
        if (getIntent().getBooleanExtra("isTask", false)) {
            TextView timeselect_begin1 = (TextView) findViewById(R.id.timeselect_begin1);
            TextView timeselect_end1 = (TextView) findViewById(R.id.timeselect_end1);
            timeselect_begin1.setText("任务开始日期");
            timeselect_end1.setText("任务结束日期");
            findViewById(R.id.timeselect_des).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.timeselect_des).setVisibility(View.GONE);
        }
        findViewById(R.id.timeselect_begin_ly).setOnClickListener(this);
        findViewById(R.id.timeselect_end_ly).setOnClickListener(this);
        findViewById(R.id.timeselect_button).setOnClickListener(this);
    }

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.timeselect_title);
        appTitle.settingName("活动起止日期");
        appTitle.showBack(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.timeselect_begin_ly: {
                timeSelect(1);
            }
            break;
            case R.id.timeselect_end_ly: {
                if (TextUtils.isEmpty(timeselect_begin.getText().toString())) {
                    Tools.showToast(this, "请先选择起始日期");
                    return;
                }
                timeSelect(2);
            }
            break;
            case R.id.timeselect_button: {
                if (TextUtils.isEmpty(timeselect_begin.getText().toString())) {
                    Tools.showToast(this, "请选择起始日期");
                    return;
                }
                if (TextUtils.isEmpty(timeselect_end.getText().toString())) {
                    Tools.showToast(this, "请选择结束日期");
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("begin_time", timeselect_begin.getText().toString());
                intent.putExtra("end_time", timeselect_end.getText().toString());
                setResult(AppInfo.REQUEST_CODE_COLLECT, intent);
                baseFinish();
            }
            break;
        }
    }

    private TimePickerView pvCustomTime;
    private int time_year, time_month, time_day;

    public void timeSelect(final int type) {
        Calendar selectedDate = Calendar.getInstance();//系统当前时间

        int year = selectedDate.get(Calendar.YEAR);
        int month = selectedDate.get(Calendar.MONTH);
        int day = selectedDate.get(Calendar.DAY_OF_MONTH);
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        if (type == 1) {
            //起始日期
            startDate.set(year, month, day);
            //结束日期
            endDate.set(2099, 12, 31);
        } else {
            //起始日期
            startDate.set(time_year, time_month, time_day + 1);
            //结束日期
            endDate.set(time_year + 1, time_month, time_day);
        }

        pvCustomTime = new TimePickerBuilder(TimeSelectActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                if (type == 1) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    time_year = calendar.get(Calendar.YEAR);
                    time_month = calendar.get(Calendar.MONTH);
                    time_day = calendar.get(Calendar.DAY_OF_MONTH);
                    if (time_month > 0 && time_month < 9 && time_day > 0 && time_day < 10) {
                        timeselect_begin.setText(time_year + "-0" + (time_month + 1) + "-0" + time_day);
                    } else if (time_month > 0 && time_month < 9 && time_day >= 10) {
                        timeselect_begin.setText(time_year + "-0" + (time_month + 1) + "-" + time_day);
                    } else if (time_month >= 9 && time_day > 0 && time_day < 10) {
                        timeselect_begin.setText(time_year + "-" + (time_month + 1) + "-0" + time_day);
                    } else {
                        timeselect_begin.setText(time_year + "-" + (time_month + 1) + "-" + time_day);
                    }
                    timeselect_end.setText("");
                } else if (type == 2) {
                    timeselect_end.setText(sdf.format(date));
                }
            }
        })
                .setBgColor(0xFFF6F6F6)
                .setTextColorCenter(0xFF231916)
                .setTextColorOut(0xFFA0A0A0)
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setDividerColor(0xFFEEEEEE)
                .setContentTextSize(18)
                .setLineSpacingMultiplier(2.3f)
                .setLayoutRes(R.layout.pickerview_custom_time, new CustomListener() {

                    @Override
                    public void customLayout(View v) {
                        final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
//                        View ivCancel = v.findViewById(R.id.iv_cancel);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                pvCustomTime.returnData();
                                pvCustomTime.dismiss();
                            }
                        });
//                        ivCancel.setOnClickListener(new View.OnClickListener() {
//                            public void onClick(View v) {
//                                pvCustomTime.dismiss();
//                            }
//                        });
                    }
                })
                .setContentTextSize(18)
                .setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("年", "月", "日", "时", "分", "秒")
//                .setTextXOffset(0, 0, 0, 40, 0, -40)
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .build();
        pvCustomTime.show();
    }

    @Override
    public void onBack() {
        baseFinish();
    }
}
