package com.orange.oy.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;

/**
 * 判断题
 */
public class TaskJudgeNoEditView extends LinearLayout {

    public TaskJudgeNoEditView(Context context, String title, String answer, boolean isRequired) {
        super(context);
        Tools.loadLayout(this, R.layout.view_task_question_judge);
        View task_question_judge_right = findViewById(R.id.task_question_judge_right);
        View task_question_judge_left = findViewById(R.id.task_question_judge_left);
        ImageView task_question_judge_right_ico = (ImageView) findViewById(R.id.task_question_judge_right_ico);
        ImageView task_question_judge_left_ico = (ImageView) findViewById(R.id.task_question_judge_left_ico);
        ((TextView) findViewById(R.id.task_question_judge_name)).setText(title);
        if (!TextUtils.isEmpty(answer) && !answer.equals("null")) {
            if (answer.equals("1")) {
                task_question_judge_right.setBackgroundResource(R.drawable.questionradio_s_bg);
                task_question_judge_left.setBackgroundResource(R.drawable.questionradio_nos_bg);
                task_question_judge_left_ico.setImageResource(R.mipmap.single_notselect);
                task_question_judge_right_ico.setImageResource(R.mipmap.single_selected);
            } else {
                task_question_judge_right.setBackgroundResource(R.drawable.questionradio_nos_bg);
                task_question_judge_left.setBackgroundResource(R.drawable.questionradio_s_bg);
                task_question_judge_right_ico.setImageResource(R.mipmap.single_notselect);
                task_question_judge_left_ico.setImageResource(R.mipmap.single_selected);
            }
        }
        findViewById(R.id.task_question_judge_right).setEnabled(false);
        findViewById(R.id.task_question_judge_left).setEnabled(false);
        if (isRequired) {
            findViewById(R.id.task_question_judge_img).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.task_question_judge_img).setVisibility(View.GONE);
        }
    }

}
