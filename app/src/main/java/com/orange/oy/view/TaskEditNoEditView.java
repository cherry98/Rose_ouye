package com.orange.oy.view;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;

/**
 * 填空题
 */
public class TaskEditNoEditView extends LinearLayout {

    public TaskEditNoEditView(Context context, String title, String value, boolean isRequired) {
        super(context);
        Tools.loadLayout(this, R.layout.view_task_question_edit);
        EditText editText = (EditText) findViewById(R.id.task_question_edit_edit);
        editText.setEnabled(false);
        editText.setHint("");
        editText.setText(value);
        ((TextView) findViewById(R.id.task_question_edit_name)).setText(title);
        if (isRequired) {
            findViewById(R.id.task_question_edit_img).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.task_question_edit_img).setVisibility(View.GONE);
        }
    }

}
