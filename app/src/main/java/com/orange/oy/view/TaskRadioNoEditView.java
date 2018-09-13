package com.orange.oy.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.TaskQuestionInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.util.Utility;

import java.util.ArrayList;

/**
 * 单选
 */
public class TaskRadioNoEditView extends LinearLayout {
    private TextView task_question_radio_name;
    private ArrayList<TaskQuestionInfo> list = new ArrayList<>();
    private MyListView mylistview;
    private MyListViewAdapter adapter;

    public TaskRadioNoEditView(Context context) {
        super(context);
        Tools.loadLayout(this, R.layout.view_task_question_radio);
        task_question_radio_name = (TextView) findViewById(R.id.task_question_radio_name);
        mylistview = (MyListView) findViewById(R.id.mylistview);
        adapter = new MyListViewAdapter(context, list, true);
//        Utility.setListViewHeightBasedOnChildren(mylistview);
        mylistview.setAdapter(adapter);
    }


    public void setTitle(String title, boolean isRequired) {
        task_question_radio_name.setText(title);
        if (isRequired) {
            findViewById(R.id.task_question_radio_img).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.task_question_radio_img).setVisibility(View.GONE);
        }
    }

    public void addRadioButton(String id, String text, boolean ishav, String url) {//单选（无备注）
        if (list == null) {
            list = new ArrayList<>();
        }
        TaskQuestionInfo taskQuestionInfo = new TaskQuestionInfo();
        taskQuestionInfo.setPhoto_url(url);
        taskQuestionInfo.setId(id);
        taskQuestionInfo.setName(text);
        taskQuestionInfo.setClick(ishav);
        list.add(taskQuestionInfo);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            mylistview.setAdapter(adapter);
        }
    }

    //  isforcedfill 是否必填 0否、1是   单题模式下单选（有备注）

    public void addRadioButtonForFill(String id, String text, String isforcedfill, String note, boolean ishav, String url) {
        if (list == null) {
            list = new ArrayList<>();
        }
        if ("null".equals(note)) {
            note = "";
        }
        TaskQuestionInfo taskQuestionInfo = new TaskQuestionInfo();
        taskQuestionInfo.setId(id);
        taskQuestionInfo.setName(text);
        taskQuestionInfo.setPhoto_url(url);
        taskQuestionInfo.setIsforcedfill(isforcedfill);
        taskQuestionInfo.setClick(ishav);
        taskQuestionInfo.setNote(note);
        taskQuestionInfo.setShowEdit(true);
        list.add(taskQuestionInfo);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            mylistview.setAdapter(adapter);
        }
    }
}
