package com.orange.oy.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.allinterface.OnTaskEditRefreshListener;
import com.orange.oy.allinterface.OnTaskQuestionSumbitListener;
import com.orange.oy.allinterface.TaskEditClearListener;
import com.orange.oy.base.Tools;
import com.orange.oy.info.TaskEditInfo;
import com.orange.oy.info.TaskQuestionInfo;

import java.util.ArrayList;

/**
 * 单选
 */
public class TaskRadioView extends LinearLayout implements AdapterView.OnItemClickListener, TaskEditClearListener {
    private TextView task_question_radio_name;
    private OnTaskQuestionSumbitListener onTaskQuestionSumbitListener;
    private OnTaskEditRefreshListener onTaskEditRefreshListener;
    private MyListView myListView;
    private MyListViewAdapter adapter;
    private ArrayList<TaskQuestionInfo> list = new ArrayList<>();
    private int selectPosition, oldSelectPosition;
    private EditText editText;
    private int questionNum = -1;

    public TaskRadioView(Context context) {
        super(context);
        Tools.loadLayout(this, R.layout.view_task_question_radio);
        task_question_radio_name = (TextView) findViewById(R.id.task_question_radio_name);
        selectPosition = -1;
        oldSelectPosition = -1;
        myListView = (MyListView) findViewById(R.id.mylistview);
        adapter = new MyListViewAdapter(context, list, false);
        myListView.setAdapter(adapter);
        myListView.setOnItemClickListener(this);
    }

    public void setReset(boolean isReset) {
        adapter.setReset(isReset);
    }

    public void isSelect(boolean isSelect) {
        setReset(!isSelect);
        if (isSelect) {
            myListView.setOnItemClickListener(this);
        } else {
            myListView.setOnItemClickListener(null);
        }
    }

    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    public void settingData(TaskEditInfo taskEditInfo) {
        questionNum = taskEditInfo.getQuestion_num();
    }

    public void setTitle(String title, boolean isrequired) {
        task_question_radio_name.setText(title);
        if (isrequired) {
            findViewById(R.id.task_question_radio_img).setVisibility(VISIBLE);
        }
    }

    public void setSubmitText(String text) {
        ((TextView) findViewById(R.id.task_question_radio_sumbit)).setText(text);
    }

    public void setOnTaskEditRefreshListener(OnTaskEditRefreshListener onTaskEditRefreshListener) {
        this.onTaskEditRefreshListener = onTaskEditRefreshListener;
    }

    public void setOnTaskQuestionSumbitListener(OnTaskQuestionSumbitListener listener) {
        onTaskQuestionSumbitListener = listener;
        View task_question_radio_sumbit = findViewById(R.id.task_question_radio_sumbit);
        task_question_radio_sumbit.setVisibility(View.VISIBLE);
        task_question_radio_sumbit.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (onTaskQuestionSumbitListener != null) {
                    TaskQuestionInfo taskQuestionInfo = getSelectAnswers();
                    if (taskQuestionInfo != null && taskQuestionInfo.getNoteEditext() != null) {
                        onTaskQuestionSumbitListener.sumbit(new TaskQuestionInfo[]{taskQuestionInfo}, new
                                String[]{taskQuestionInfo.getNoteEditext().getText().toString().trim()});
                    } else {
                        onTaskQuestionSumbitListener.sumbit(new TaskQuestionInfo[]{taskQuestionInfo}, null);
                    }
                }
            }
        });
    }

    public TaskQuestionInfo getSelectAnswers() {
        if (list == null || selectPosition == -1) return null;
        try {
            return list.get(selectPosition);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 拍照任务用
     *
     * @return
     */
    public String getSelectId() {
        if (list == null) return null;
        try {
            return list.get(selectPosition).getId();
        } catch (Exception e) {
            return null;
        }
    }

    //  isforcedfill 是否必填 0否、1是   多题模式下单选（有备注）

    public void addRadioButtonForFill(String id, String text, String isforcedfill, String url) {
        addRadioButtonForFill(id, text, isforcedfill, null, false, url);
    }

    public void addRadioButtonForFill(String id, String text, String isforcedfill, String note, boolean issel, String url) {
        if (list == null) {
            list = new ArrayList<>();
        }
        if ("null".equals(note)) {
            note = "";
        }
        TaskQuestionInfo taskQuestionInfo = new TaskQuestionInfo();
        if (!TextUtils.isEmpty(note)) {
            taskQuestionInfo.setNote(note);
        }
        taskQuestionInfo.setId(id);
        taskQuestionInfo.setName(text);
        taskQuestionInfo.setPhoto_url(url);
        taskQuestionInfo.setIsforcedfill(isforcedfill);
        taskQuestionInfo.setClick(issel);
        if (taskQuestionInfo.isClick()) {
            selectPosition = list.size();
        }
        taskQuestionInfo.setIsRequired("1".equals(isforcedfill));
        taskQuestionInfo.setShowEdit(true);
        list.add(taskQuestionInfo);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
//            myListView.setAdapter(adapter);
        }
    }
    //  isforcedfill 是否必填 0否、1是   单题模式下单选（有备注）---->3.11：多题模式也用此方法

    public void addRadioButtonForFill(String id, String text, String isforcedfill, String jump, String jumpquestion, String url) {
        addRadioButtonForFill(id, text, isforcedfill, jump, jumpquestion, null, false, url);
    }

    public void addRadioButtonForFill(String id, String text, String isforcedfill, String jump, String jumpquestion, String note, boolean isssel, String url) {
        if (list == null) {
            list = new ArrayList<>();
        }
        if ("null".equals(note)) {
            note = "";
        }
        TaskQuestionInfo taskQuestionInfo = new TaskQuestionInfo();
        if (!TextUtils.isEmpty(note)) {
            taskQuestionInfo.setNote(note);
        }
        taskQuestionInfo.setId(id);
        taskQuestionInfo.setName(text);
        taskQuestionInfo.setPhoto_url(url);
        taskQuestionInfo.setJump(jump);
        taskQuestionInfo.setJumpquestion(jumpquestion);
        taskQuestionInfo.setIsforcedfill(isforcedfill);
        taskQuestionInfo.setClick(isssel);
        if (taskQuestionInfo.isClick()) {
            selectPosition = list.size();
        }
        taskQuestionInfo.setIsRequired("1".equals(isforcedfill));
        taskQuestionInfo.setShowEdit(true);
        list.add(taskQuestionInfo);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
//            myListView.setAdapter(adapter);
        }
    }

    public void addRadioButton(String id, String text, String jump, String jumpquestion, String url) {//单题模式下单选（无备注）---->3.11：多题模式也用此方法
        addRadioButton(id, text, jump, jumpquestion, false, url);
    }

    public void addRadioButton(String id, String text, String jump, String jumpquestion, boolean issel, String url) {//单题模式下单选（无备注）---->3.11：多题模式也用此方法
        if (list == null) {
            list = new ArrayList<>();
        }
        TaskQuestionInfo taskQuestionInfo = new TaskQuestionInfo();
        taskQuestionInfo.setId(id);
        taskQuestionInfo.setName(text);
        taskQuestionInfo.setPhoto_url(url);
        taskQuestionInfo.setJump(jump);
        taskQuestionInfo.setJumpquestion(jumpquestion);
        taskQuestionInfo.setClick(issel);
        if (taskQuestionInfo.isClick()) {
            selectPosition = list.size();
        }
        taskQuestionInfo.setShowEdit(false);
        list.add(taskQuestionInfo);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            myListView.setAdapter(adapter);
        }
    }

    public void addRadioButton(String id, String text, String url) {//多题模式下单选（无备注）
        addRadioButton(id, text, false, url);
    }

    public void addRadioButton(String id, String text, boolean issel, String url) {//多题模式下单选（无备注）
        if (list == null) {
            list = new ArrayList<>();
        }
        TaskQuestionInfo taskQuestionInfo = new TaskQuestionInfo();
        taskQuestionInfo.setId(id);
        taskQuestionInfo.setName(text);
        taskQuestionInfo.setPhoto_url(url);
        taskQuestionInfo.setClick(issel);
        taskQuestionInfo.setShowEdit(false);
        list.add(taskQuestionInfo);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            myListView.setAdapter(adapter);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == adapter.getSelectPosition()) {
            adapter.setSelectPosition(-1);
        } else {
            adapter.setSelectPosition(position);
        }
        oldSelectPosition = selectPosition;
        if (selectPosition == position) {
            selectPosition = -1;
        } else {
            selectPosition = position;
        }
        View view1 = adapter.getView(position, view, parent);
        EditText editText = (EditText) view1.findViewById(R.id.edittext);
        this.editText = editText;
        TaskQuestionInfo taskQuestionInfo = list.get(position);
        taskQuestionInfo.setNoteEditext(editText);
        if (onTaskEditRefreshListener != null) {
            onTaskEditRefreshListener.changeView(this, questionNum, adapter.getSelectPosition() == -1 ? "0" : taskQuestionInfo.getJumpquestion(),
                    (oldSelectPosition == -1 ? null : (oldSelectPosition + "")));
        }
        adapter.notifyDataSetChanged();
    }

    public void settingOldPosition() {
        adapter.setSelectPosition(oldSelectPosition);
        selectPosition = oldSelectPosition;
        oldSelectPosition = -1;
        adapter.notifyDataSetChanged();
    }

    public TaskQuestionInfo getQuestionInfo(int index) {
        return list.get(index);
    }

    @Override
    public void dataClear() {//清除选择的数据
        if (adapter != null) {
            adapter.setSelectPosition(-1);
            selectPosition = -1;
            oldSelectPosition = -1;
            adapter.notifyDataSetChanged();
        }
    }
}
