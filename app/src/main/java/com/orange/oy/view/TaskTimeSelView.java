package com.orange.oy.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.orange.oy.R;
import com.orange.oy.allinterface.OnTaskQuestionSumbitListener;
import com.orange.oy.allinterface.TaskEditClearListener;
import com.orange.oy.base.Tools;
import com.orange.oy.info.TaskQuestionInfo;

import java.util.Calendar;

/**
 * 时间选择题
 */
public class TaskTimeSelView extends LinearLayout implements View.OnClickListener, TaskEditClearListener {
    private TextView showSelectValue, showSelectValue2;
    private OnTaskQuestionSumbitListener onTaskQuestionSumbitListener;

    public TaskTimeSelView(Context context, String title, String value, boolean isrequired) {
        super(context);
        Tools.loadLayout(this, R.layout.view_task_question_timesel);
        showSelectValue = (TextView) findViewById(R.id.task_question_timesel_edit);
        showSelectValue2 = (TextView) findViewById(R.id.task_question_timesel_edit2);
        ((TextView) findViewById(R.id.task_question_timesel_name)).setText(title);
        showSelectValue.setOnClickListener(this);
        showSelectValue2.setOnClickListener(this);
        if (isrequired) {
            findViewById(R.id.task_question_timesel_img).setVisibility(VISIBLE);
        }
        String[] str = value.split(" ");
        if (str.length == 2) {
            if (!"日期".equals(str[0])) {
                showSelectValue.setText(str[0]);
            } else {
                showSelectValue.setText("");
            }
            if (!"时间".equals(str[1])) {
                showSelectValue2.setText(str[1]);
            } else {
                showSelectValue2.setText("");
            }
        }
    }

    public TaskTimeSelView(Context context, String title, boolean isrequired) {
        super(context);
        Tools.loadLayout(this, R.layout.view_task_question_timesel);
        showSelectValue = (TextView) findViewById(R.id.task_question_timesel_edit);
        showSelectValue2 = (TextView) findViewById(R.id.task_question_timesel_edit2);
        ((TextView) findViewById(R.id.task_question_timesel_name)).setText(title);
        showSelectValue.setOnClickListener(this);
        showSelectValue2.setOnClickListener(this);
        if (isrequired) {
            findViewById(R.id.task_question_timesel_img).setVisibility(VISIBLE);
        }
    }

    public void isSelect(boolean isSelect) {
        if (isSelect) {
            showSelectValue.setOnClickListener(this);
            showSelectValue2.setOnClickListener(this);
        } else {
            showSelectValue.setOnClickListener(null);
            showSelectValue2.setOnClickListener(null);
        }
    }

    public void setSubmitText(String text) {
        ((TextView) findViewById(R.id.task_question_timesel_sumbit)).setText(text);
    }

    public void setOnTaskQuestionSumbitListener(OnTaskQuestionSumbitListener listener) {
        onTaskQuestionSumbitListener = listener;
        View task_question_timesel_sumbit = findViewById(R.id.task_question_timesel_sumbit);
        task_question_timesel_sumbit.setVisibility(View.VISIBLE);
        task_question_timesel_sumbit.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (onTaskQuestionSumbitListener != null) {
                    TaskQuestionInfo taskQuestionInfo = new TaskQuestionInfo();
                    String str1 = showSelectValue.getText().toString().trim();
                    String str2 = showSelectValue2.getText().toString().trim();
                    if (TextUtils.isEmpty(str1) && TextUtils.isEmpty(str2)) {
                        onTaskQuestionSumbitListener.sumbit(new TaskQuestionInfo[]{taskQuestionInfo}, null);
                    } else if (TextUtils.isEmpty(str1)) {
                        Tools.showToast(getContext(), "请选择日期");
                    } else if (TextUtils.isEmpty(str2)) {
                        Tools.showToast(getContext(), "请选择时间");
                    } else {
                        taskQuestionInfo.setId(str1 + " " + str2);
                        onTaskQuestionSumbitListener.sumbit(new TaskQuestionInfo[]{taskQuestionInfo}, null);
                    }
                }
            }
        });
    }

    public String getText() {
        String str1 = showSelectValue.getText().toString().trim();
        if (TextUtils.isEmpty(str1)) {
            return null;
        }
        String str2 = showSelectValue2.getText().toString().trim();
        if (TextUtils.isEmpty(str2)) {
            return null;
        }
        return str1 + " " + str2;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.task_question_timesel_edit: {
                MyDatePickerDialog myDatePickerDialog = new MyDatePickerDialog();
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        myDatePickerDialog, Tools.getYear(), Tools.getMonth() - 1, Tools.getCurrentMonthDay());
                datePickerDialog.show();
            }
            break;
            case R.id.task_question_timesel_edit2: {
                Calendar calendar = Calendar.getInstance();
                MyTimePickerDialog myTimePickerDialog = new MyTimePickerDialog();
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        myTimePickerDialog, calendar.getTime().getHours(), calendar.getTime().getMinutes(), true);
                timePickerDialog.show();
            }
            break;
        }
    }

    @Override
    public void dataClear() {
        showSelectValue.setText("");
        showSelectValue2.setText("");
    }

    public class MyDatePickerDialog implements DatePickerDialog.OnDateSetListener {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            monthOfYear = monthOfYear + 1;
            showSelectValue.setText(year + "-" + ((monthOfYear > 9) ? monthOfYear : ("0" + monthOfYear)) +
                    "-" + ((dayOfMonth > 9) ? dayOfMonth : ("0" + dayOfMonth)));
        }
    }

    public class MyTimePickerDialog implements TimePickerDialog.OnTimeSetListener {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            showSelectValue2.setText(((hourOfDay > 9) ? hourOfDay : ("0" + hourOfDay)) +
                    ":" + ((minute > 9) ? minute : ("0" + minute)));
        }
    }
}
