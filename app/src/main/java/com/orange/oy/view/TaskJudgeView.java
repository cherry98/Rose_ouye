package com.orange.oy.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.allinterface.OnTaskQuestionSumbitListener;
import com.orange.oy.allinterface.TaskEditClearListener;
import com.orange.oy.base.Tools;
import com.orange.oy.info.TaskQuestionInfo;

/**
 * 判断题
 */
public class TaskJudgeView extends LinearLayout implements TaskEditClearListener, View.OnClickListener {
    private int isRight;//1:是，0:错误
    private OnTaskQuestionSumbitListener onTaskQuestionSumbitListener;
    private View task_question_judge_right, task_question_judge_left;
    private ImageView task_question_judge_right_ico, task_question_judge_left_ico;

    public TaskJudgeView(Context context, String title, String answer, boolean isrequired) {
        super(context);
        Tools.loadLayout(this, R.layout.view_task_question_judge);
        task_question_judge_right = findViewById(R.id.task_question_judge_right);
        task_question_judge_left = findViewById(R.id.task_question_judge_left);
        task_question_judge_right_ico = (ImageView) findViewById(R.id.task_question_judge_right_ico);
        task_question_judge_left_ico = (ImageView) findViewById(R.id.task_question_judge_left_ico);
        task_question_judge_right.setTag(false);
        task_question_judge_left.setTag(false);
        task_question_judge_right.setOnClickListener(this);
        task_question_judge_left.setOnClickListener(this);
        ((TextView) findViewById(R.id.task_question_judge_name)).setText(title);
        isRight = -1;
        if (isrequired) {
            findViewById(R.id.task_question_judge_img).setVisibility(VISIBLE);
        }
        if (!TextUtils.isEmpty(answer) && !answer.equals("null")) {
            if (answer.equals("1")) {
                onClick(task_question_judge_right);
            } else {
                onClick(task_question_judge_left);
            }
        }
    }

    public TaskJudgeView(Context context, String title, boolean isrequired) {
        super(context);
        Tools.loadLayout(this, R.layout.view_task_question_judge);
        task_question_judge_right = findViewById(R.id.task_question_judge_right);
        task_question_judge_left = findViewById(R.id.task_question_judge_left);
        task_question_judge_right_ico = (ImageView) findViewById(R.id.task_question_judge_right_ico);
        task_question_judge_left_ico = (ImageView) findViewById(R.id.task_question_judge_left_ico);
        task_question_judge_right.setTag(false);
        task_question_judge_left.setTag(false);
        task_question_judge_right.setOnClickListener(this);
        task_question_judge_left.setOnClickListener(this);
        ((TextView) findViewById(R.id.task_question_judge_name)).setText(title);
        isRight = -1;
        if (isrequired) {
            findViewById(R.id.task_question_judge_img).setVisibility(VISIBLE);
        }
    }

    public void setSubmitText(String text) {
        ((TextView) findViewById(R.id.task_question_judge_sumbit)).setText(text);
    }

    public void setOnTaskQuestionSumbitListener(OnTaskQuestionSumbitListener listener) {
        onTaskQuestionSumbitListener = listener;
        View task_question_judge_sumbit = findViewById(R.id.task_question_judge_sumbit);
        task_question_judge_sumbit.setVisibility(View.VISIBLE);
        task_question_judge_sumbit.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (onTaskQuestionSumbitListener != null) {
                    TaskQuestionInfo taskQuestionInfo = new TaskQuestionInfo();
                    if (isRight != -1)
                        taskQuestionInfo.setId(isRight + "");
                    onTaskQuestionSumbitListener.sumbit(new TaskQuestionInfo[]{taskQuestionInfo}, null);
                }
            }
        });
    }

    public int isRight() {
        return isRight;
    }

    public void isSelect(boolean isSelect) {
        if (isSelect) {
            task_question_judge_right.setOnClickListener(this);
            task_question_judge_left.setOnClickListener(this);
        } else {
            task_question_judge_right.setOnClickListener(null);
            task_question_judge_left.setOnClickListener(null);
        }
    }

    @Override
    public void dataClear() {
        task_question_judge_right.setBackgroundResource(R.drawable.questionradio_nos_bg);
        task_question_judge_left.setBackgroundResource(R.drawable.questionradio_nos_bg);
        task_question_judge_left_ico.setImageResource(R.mipmap.single_notselect);
        task_question_judge_right_ico.setImageResource(R.mipmap.single_notselect);
        isRight = -1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.task_question_judge_right: {
                task_question_judge_right.setBackgroundResource(R.drawable.questionradio_s_bg);
                task_question_judge_left.setBackgroundResource(R.drawable.questionradio_nos_bg);
                task_question_judge_left_ico.setImageResource(R.mipmap.single_notselect);
                task_question_judge_right_ico.setImageResource(R.mipmap.single_selected);
                isRight = 1;
            }
            break;
            case R.id.task_question_judge_left: {
                task_question_judge_right.setBackgroundResource(R.drawable.questionradio_nos_bg);
                task_question_judge_left.setBackgroundResource(R.drawable.questionradio_s_bg);
                task_question_judge_right_ico.setImageResource(R.mipmap.single_notselect);
                task_question_judge_left_ico.setImageResource(R.mipmap.single_selected);
                isRight = 0;
            }
            break;
        }
    }
}
