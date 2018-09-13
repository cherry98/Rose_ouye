package com.orange.oy.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.allinterface.OnTaskEditRefreshListener;
import com.orange.oy.allinterface.OnTaskQuestionSumbitListener;
import com.orange.oy.allinterface.TaskEditClearListener;
import com.orange.oy.base.Tools;
import com.orange.oy.info.TaskEditInfo;
import com.orange.oy.info.TaskEditoptionsInfo;
import com.orange.oy.info.TaskQuestionInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;

import java.util.ArrayList;

/**
 * 多选
 */
public class TaskCheckView extends LinearLayout implements TaskcheckImageView.OnCheckedChangeListener, TaskEditClearListener {
    private TextView task_question_check_name;
    private LinearLayout task_question_check_layout;
    private OnTaskQuestionSumbitListener onTaskQuestionSumbitListener;
    private ImageLoader imageLoader;
    private int questionNum;
    private String jumpQuestion;
    private OnTaskEditRefreshListener onTaskEditRefreshListener;

    public TaskCheckView(Context context) {
        super(context);
        imageLoader = new ImageLoader(context);
        Tools.loadLayout(this, R.layout.view_task_question_check);
        task_question_check_name = (TextView) findViewById(R.id.task_question_check_name);
        task_question_check_layout = (LinearLayout) findViewById(R.id.task_question_check_layout);
    }

    public void settingData(TaskEditInfo taskEditInfo) {
        questionNum = taskEditInfo.getQuestion_num();
        jumpQuestion = taskEditInfo.getJump_question();
    }

    public void setOnTaskEditRefreshListener(OnTaskEditRefreshListener onTaskEditRefreshListener) {
        this.onTaskEditRefreshListener = onTaskEditRefreshListener;
    }

    public void setSubmitText(String text) {
        ((TextView) findViewById(R.id.task_question_check_sumbit)).setText(text);
    }

    public void setOnTaskQuestionSumbitListener(OnTaskQuestionSumbitListener listener) {
        onTaskQuestionSumbitListener = listener;
        View task_question_check_sumbit = findViewById(R.id.task_question_check_sumbit);
        task_question_check_sumbit.setVisibility(View.VISIBLE);
        task_question_check_sumbit.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (onTaskQuestionSumbitListener != null) {
                    ArrayList<TaskQuestionInfo> list = getSelectAnswer();
                    int size = list.size();
                    TaskQuestionInfo[] anwers = new TaskQuestionInfo[size];
                    String[] notes = new String[size];
                    for (int i = 0; i < size; i++) {
                        anwers[i] = list.get(i);
                        if (anwers[i].getNoteEditext() != null) {
                            notes[i] = anwers[i].getNoteEditext().getText().toString().trim().replaceAll("&&", "");
                        }
                    }
                    onTaskQuestionSumbitListener.sumbit(anwers, notes);
                }
            }
        });
    }

    public void setTitle(String str, boolean isrequired) {
        task_question_check_name.setText(str);
        if (isrequired) {
            findViewById(R.id.task_question_check_img).setVisibility(VISIBLE);
        }
    }

    /**
     * 拍照任务用
     */
    public ArrayList<String> getSelectId() {
        ArrayList<String> returnList = new ArrayList<>();
        if (list == null) return returnList;
        int size = list.size();
        TaskQuestionInfo taskQuestionInfo;
        TaskcheckImageView checkBox;
        for (int i = 0; i < size; i++) {
            String str;
            taskQuestionInfo = list.get(i);
            checkBox = (TaskcheckImageView) taskQuestionInfo.getView();
            if (checkBox.isChecked()) {
                str = taskQuestionInfo.getId();
                returnList.add(str);
            }
        }
        return returnList;
    }

    public ArrayList<TaskQuestionInfo> getSelectAnswer() {
        ArrayList<TaskQuestionInfo> returnList = new ArrayList<>();
        if (list == null) return returnList;
        int size = list.size();
        TaskQuestionInfo taskQuestionInfo;
        TaskcheckImageView checkBox;
        for (int i = 0; i < size; i++) {
            taskQuestionInfo = list.get(i);
            checkBox = (TaskcheckImageView) taskQuestionInfo.getView();
            if (checkBox.isChecked()) {
                returnList.add(taskQuestionInfo);
            }
        }
        return returnList;
    }

    private ArrayList<TaskQuestionInfo> list;

    public void addCheckBox(TaskEditoptionsInfo taskEditoptionsInfo) {
        addCheckBox(taskEditoptionsInfo, false);
    }

    public void addCheckBox(TaskEditoptionsInfo taskEditoptionsInfo, boolean ishav) {
        if (list == null) {
            list = new ArrayList<>();
        }
        int position = list.size();
        TaskQuestionInfo taskQuestionInfo = new TaskQuestionInfo();
        View view = Tools.loadLayout(getContext(), R.layout.view_task_question_checkbox);
        TaskcheckImageView checkBox = (TaskcheckImageView) view.findViewById(R.id.checkbox);
        checkBox.settingLayout((LinearLayout) view, false);
        ImageView img = (ImageView) view.findViewById(R.id.img);
        TextView text = (TextView) view.findViewById(R.id.text);
        if (taskEditoptionsInfo.getOption_name() == null || "null".equals(taskEditoptionsInfo.getOption_name())) {
            text.setText("");
        } else {
            text.setText(taskEditoptionsInfo.getOption_name());
        }
        String url = taskEditoptionsInfo.getPhoto_url();
        if (url == null || url.equals("null") || TextUtils.isEmpty(url)) {
            img.setVisibility(View.GONE);
        } else {
            img.setVisibility(View.VISIBLE);
            url = url.replaceAll("\"", "").replaceAll("\\\\", "");
            if (!url.startsWith("http")) {
                if (url.startsWith("GZB/")) {
                    url = Urls.Endpoint3 + url + "?x-oss-process=image/resize,l_350";
                } else {
                    url = Urls.ImgIp + url;
                }
            }
//            url = Urls.ImgIp + url.replaceAll("\"", "").replaceAll("\\\\", "");
            imageLoader.DisplayImage(url, img);
        }
        checkBox.setChecked(ishav);
        checkBox.setId(position);
//        checkBox.setText(taskEditoptionsInfo.getOption_name());
        checkBox.setOnCheckedChangeListener(this);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 7, 0, 7);
        view.setLayoutParams(lp);
        task_question_check_layout.addView(view, lp);
        taskQuestionInfo.setView(checkBox);
        taskQuestionInfo.setId(taskEditoptionsInfo.getId());
        taskQuestionInfo.setName(taskEditoptionsInfo.getOption_name());
        taskQuestionInfo.setNum(taskEditoptionsInfo.getOption_num());
        taskQuestionInfo.setJump(taskEditoptionsInfo.getJump());
        taskQuestionInfo.setJumpquestion(taskEditoptionsInfo.getJumpquestion());
        taskQuestionInfo.setPhoto_url(taskEditoptionsInfo.getPhoto_url());
        String mutex_id = taskEditoptionsInfo.getMutex_id();
        if (!TextUtils.isEmpty(mutex_id) && !mutex_id.equals("null")) {
            String[] strs = mutex_id.split(",");
            taskQuestionInfo.setMutexId(strs);
        }
        list.add(taskQuestionInfo);
    }

    /**
     * 此方法要在addCheckBox全部调用完成后设置
     *
     * @param isSelect
     */
    public void isSelect(boolean isSelect) {
        for (TaskQuestionInfo taskQuestionInfo : list) {
            ((TaskcheckImageView) taskQuestionInfo.getView()).isSelect(isSelect);
        }
    }

    public void addCheckBoxForFill(String optionname, String isforcedfill, String id, int optionnum, String mutexid) {
        TaskEditoptionsInfo taskEditoptionsInfo = new TaskEditoptionsInfo();
        taskEditoptionsInfo.setIsforcedfill(isforcedfill);
        taskEditoptionsInfo.setOption_name(optionname);
        taskEditoptionsInfo.setOption_num(optionnum);
        taskEditoptionsInfo.setMutex_id(mutexid);
        taskEditoptionsInfo.setId(id);
        addCheckBoxForFill(taskEditoptionsInfo);
    }

    public void addCheckBoxForFill(TaskEditoptionsInfo taskEditoptionsInfo) {
        addCheckBoxForFill(taskEditoptionsInfo, false, null);
    }

    public void addCheckBoxForFill(TaskEditoptionsInfo taskEditoptionsInfo, boolean ishav, String note) {
        if (list == null) {
            list = new ArrayList<>();
        }
        int position = list.size();
        TaskQuestionInfo taskQuestionInfo = new TaskQuestionInfo();
        View view = Tools.loadLayout(getContext(), R.layout.view_task_question_checkbox);
        TaskcheckImageView checkBox = (TaskcheckImageView) view.findViewById(R.id.checkbox);
        checkBox.settingLayout((LinearLayout) view, false);
        ImageView img = (ImageView) view.findViewById(R.id.img);
        TextView text = (TextView) view.findViewById(R.id.text);
        if (taskEditoptionsInfo.getOption_name() == null || "null".equals(taskEditoptionsInfo.getOption_name())) {
            text.setText("");
        } else {
            text.setText(taskEditoptionsInfo.getOption_name());
        }
        String url = taskEditoptionsInfo.getPhoto_url();
        if (url == null || url.equals("null") || TextUtils.isEmpty(url)) {
            img.setVisibility(View.GONE);
        } else {
            img.setVisibility(View.VISIBLE);
            url = url.replaceAll("\"", "").replaceAll("\\\\", "");
            if (!url.startsWith("http")) {
                if (url.startsWith("GZB/")) {
                    url = Urls.Endpoint3 + url + "?x-oss-process=image/resize,l_350";
                } else {
                    url = Urls.ImgIp + url;
                }
            }
//            url = Urls.ImgIp + url.replaceAll("\"", "").replaceAll("\\\\", "");
            imageLoader.DisplayImage(url, img);
        }
        EditText ed = (EditText) Tools.loadLayout(getContext(), R.layout.view_task_question_radiobutton_fill);
        if (!TextUtils.isEmpty(note)) {
            ed.setText(note);
        } else {
            if ("1".equals(taskEditoptionsInfo.getIsforcedfill())) {
                ed.setHint("备注（必填）");
                taskQuestionInfo.setIsRequired(true);
            } else {
                ed.setHint("备注");
                taskQuestionInfo.setIsRequired(false);
            }
        }
        checkBox.setChecked(ishav);
        checkBox.setId(position);
        checkBox.setOnCheckedChangeListener(this);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 7, 0, 7);
        task_question_check_layout.addView(view, lp);
        task_question_check_layout.addView(ed, lp);
        taskQuestionInfo.setView(checkBox);
        taskQuestionInfo.setNoteEditext(ed);
        taskQuestionInfo.setId(taskEditoptionsInfo.getId());
        taskQuestionInfo.setName(taskEditoptionsInfo.getOption_name());
        taskQuestionInfo.setNum(taskEditoptionsInfo.getOption_num());
        taskQuestionInfo.setJump(taskEditoptionsInfo.getJump());
        taskQuestionInfo.setJumpquestion(taskEditoptionsInfo.getJumpquestion());
        String mutex_id = taskEditoptionsInfo.getMutex_id();
        if (!TextUtils.isEmpty(mutex_id) && !mutex_id.equals("null")) {
            String[] strs = mutex_id.split(",");
            taskQuestionInfo.setMutexId(strs);
        }
        list.add(taskQuestionInfo);
    }

    private int selectNum = 0;

    /**
     * 判断互斥
     */
    @Override
    public void onCheckedChanged(TaskcheckImageView buttonView, boolean isChecked) {
        if (isChecked) {
            selectNum++;
        } else {
            selectNum--;
        }
        if (onTaskEditRefreshListener != null) {
            onTaskEditRefreshListener.changeView(this, questionNum, (selectNum == 0) ? "0" : jumpQuestion, null);
        }
        if (!isChecked) return;
        int id = buttonView.getId();
        if (list != null && id >= 0 && id < list.size()) {
            TaskQuestionInfo taskQuestionInfo = list.get(id);
            String[] mutexs = taskQuestionInfo.getMutexId();
            if (mutexs != null) {
                int size = list.size();
                for (int j = 0; j < mutexs.length; j++) {
                    for (int i = 0; i < size; i++) {
                        taskQuestionInfo = list.get(i);
                        if (mutexs[j].equals(taskQuestionInfo.getNum() + "")) {
                            TaskcheckImageView checkBox = (TaskcheckImageView) taskQuestionInfo.getView();
                            if (checkBox.isChecked()) {
                                checkBox.setChecked(false);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void dataClear() {
        for (TaskQuestionInfo taskQuestionInfo : list) {
            ((TaskcheckImageView) taskQuestionInfo.getView()).setChecked(false);
            if (taskQuestionInfo.getNoteEditext() != null) {
                taskQuestionInfo.getNoteEditext().setText("");
            }
        }
        selectNum = 0;
    }

}
