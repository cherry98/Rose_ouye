package com.orange.oy.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;

/**
 * 时间选择题
 */
public class TaskTimeSelNoEditView extends LinearLayout {

    public TaskTimeSelNoEditView(Context context, String title, String value, boolean isRequired) {
        super(context);
        Tools.loadLayout(this, R.layout.view_task_question_timesel);
        if (TextUtils.isEmpty(value)) {
            return;
        }
        String[] str = value.split(" ");
        if (str.length == 2) {
            if (!"日期".equals(str[0])) {
                ((TextView) findViewById(R.id.task_question_timesel_edit)).setText(str[0]);
            } else {
                ((TextView) findViewById(R.id.task_question_timesel_edit)).setText("");
            }
            if (!"时间".equals(str[1])) {
                ((TextView) findViewById(R.id.task_question_timesel_edit2)).setText(str[1]);
            } else {
                ((TextView) findViewById(R.id.task_question_timesel_edit2)).setText("");
            }
        }
        ((TextView) findViewById(R.id.task_question_timesel_name)).setText(title);
        if (isRequired) {
            findViewById(R.id.task_question_timesel_img).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.task_question_timesel_img).setVisibility(View.GONE);
        }
    }
}
