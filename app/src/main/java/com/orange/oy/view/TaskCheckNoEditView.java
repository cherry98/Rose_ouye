package com.orange.oy.view;

import android.content.Context;
import android.speech.tts.Voice;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.TaskEditoptionsInfo;
import com.orange.oy.info.TaskQuestionInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;

/**
 * 多选
 */
public class TaskCheckNoEditView extends LinearLayout {
    private TextView task_question_check_name;
    private LinearLayout task_question_check_layout;
    private ImageLoader imageLoader;

    public TaskCheckNoEditView(Context context) {
        super(context);
        imageLoader = new ImageLoader(context);
        Tools.loadLayout(this, R.layout.view_task_question_check);
        task_question_check_name = (TextView) findViewById(R.id.task_question_check_name);
        task_question_check_layout = (LinearLayout) findViewById(R.id.task_question_check_layout);
    }

    public void setTitle(String str, boolean isRequired) {
        task_question_check_name.setText(str);
        if (isRequired) {
            findViewById(R.id.task_question_check_img).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.task_question_check_img).setVisibility(View.GONE);
        }
    }

    public void addCheckBox(String id, String text, boolean ishad, String url) {
        TaskQuestionInfo taskQuestionInfo = new TaskQuestionInfo();
        View view = Tools.loadLayout(getContext(), R.layout.view_task_question_checkbox);
        TaskcheckImageView checkBox = (TaskcheckImageView) view.findViewById(R.id.checkbox);
        ImageView img = (ImageView) view.findViewById(R.id.img);
        TextView textview = (TextView) view.findViewById(R.id.text);
        if (url == null || url.equals("null") || TextUtils.isEmpty(url)) {
            img.setVisibility(View.GONE);
        } else {
            img.setVisibility(View.VISIBLE);
            url = Urls.ImgIp + url.replaceAll("\"", "").replaceAll("\\\\", "");
            imageLoader.DisplayImage(url, img);
        }
        if ("null".equals(text)) {
            text = "";
        }
        textview.setText(text);
        checkBox.setEnabled(false);
        checkBox.setChecked(ishad);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 20, 0, 20);
        view.setLayoutParams(lp);
        task_question_check_layout.addView(view, lp);
        taskQuestionInfo.setView(checkBox);
        taskQuestionInfo.setId(id);
        taskQuestionInfo.setName(text);
    }

    public void addCheckBoxForFill(String optionname, String isforcedfill, String id, int optionnum, String note,
                                   boolean ishad, String url) {
        if ("null".equals(optionname)) {
            optionname = "";
        }
        TaskEditoptionsInfo taskEditoptionsInfo = new TaskEditoptionsInfo();
        taskEditoptionsInfo.setIsforcedfill(isforcedfill);
        taskEditoptionsInfo.setOption_name(optionname);
        taskEditoptionsInfo.setOption_num(optionnum);
        taskEditoptionsInfo.setId(id);
        addCheckBoxForFill(taskEditoptionsInfo, note, ishad, url);
    }

    public void addCheckBoxForFill(TaskEditoptionsInfo taskEditoptionsInfo, String note, boolean ishad, String url) {
        TaskQuestionInfo taskQuestionInfo = new TaskQuestionInfo();
        View view = Tools.loadLayout(getContext(), R.layout.view_task_question_checkbox);
        TaskcheckImageView checkBox = (TaskcheckImageView) view.findViewById(R.id.checkbox);
        ImageView img = (ImageView) view.findViewById(R.id.img);
        TextView textview = (TextView) view.findViewById(R.id.text);
        EditText ed = (EditText) Tools.loadLayout(getContext(), R.layout.view_task_question_radiobutton_fill);
        textview.setText(taskEditoptionsInfo.getOption_name());
        if (url == null || url.equals("null") || TextUtils.isEmpty(url)) {
            img.setVisibility(View.GONE);
        } else {
            img.setVisibility(View.VISIBLE);
            url = Urls.ImgIp + url.replaceAll("\"", "").replaceAll("\\\\", "");
            imageLoader.DisplayImage(url, img);
        }
        checkBox.setEnabled(false);
        checkBox.setChecked(ishad);
        if (!TextUtils.isEmpty(note)) {
            ed.setText(note);
        }
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 20, 0, 20);
        view.setLayoutParams(lp);
        task_question_check_layout.addView(view, lp);
        task_question_check_layout.addView(ed, lp);
        taskQuestionInfo.setView(checkBox);
        taskQuestionInfo.setNoteEditext(ed);
        taskQuestionInfo.setId(taskEditoptionsInfo.getId());
        taskQuestionInfo.setName(taskEditoptionsInfo.getOption_name());
        taskQuestionInfo.setNum(taskEditoptionsInfo.getOption_num());
    }

}
