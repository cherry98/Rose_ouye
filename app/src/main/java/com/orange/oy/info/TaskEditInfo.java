package com.orange.oy.info;

import android.text.TextUtils;
import android.view.View;

import java.util.ArrayList;

/**
 * 题目信息集合
 */
public class TaskEditInfo {
    private String id;
    private String question_type;//问题类型，1为单选，2为多选，3为判断，4为填空,5为时间选择，6为语音题
    private String question_name;//题目
    private String prompt;//提示
    private String max_option;//最多选项
    private String min_option;//最少选项
    private String max_word_num;//最多填写字数
    private String min_word_num;//最少填写字数
    private String isrequired;//是否必填
    private String forced_jump;//是否强制跳转
    private String jump_question;//跳题
    private ArrayList<TaskEditoptionsInfo> options;//选项
    private int question_num;//顺序
    private String answers;//答案（单题模式用）
    private String answersNote;//选项备注 分隔符：&&
    private View view;
    private String is_scan;
    private String switch_to_voice;//填空题是否可切换到语音题，1为是，0为否

    private String[] answers_;
    private String[] notes;

    private String answers_url;

    public String getAnswers_url() {
        return answers_url;
    }

    public void setAnswers_url(String answers_url) {
        this.answers_url = answers_url;
    }

    public String getSwitch_to_voice() {
        return switch_to_voice;
    }

    public void setSwitch_to_voice(String switch_to_voice) {
        this.switch_to_voice = switch_to_voice;
    }

    public String[] getAnswers_() {
        return answers_;
    }

    public void setAnswers_(String[] answers_) {
        this.answers_ = answers_;
    }

    public String[] getNotes() {
        return notes;
    }

    public void setNotes(String[] notes) {
        this.notes = notes;
    }

    private boolean isGone = false;//是否隐藏

    public boolean isGone() {
        return isGone;
    }

    public void setGone(boolean gone) {
        isGone = gone;
    }

    public String getIs_scan() {
        if (is_scan == null) {
            is_scan = "";
        }
        return is_scan;
    }

    public void setIs_scan(String is_scan) {
        this.is_scan = is_scan;
    }

    public String getAnswersNote() {
        if (TextUtils.isEmpty(answersNote)) {
            return "";
        }
        return answersNote;
    }

    public void setAnswersNote(String answersNote) {
        this.answersNote = answersNote;
    }

    public String getAnswers() {
        if (TextUtils.isEmpty(answers)) {
            return "";
        }
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public int getQuestion_num() {
        return question_num;
    }

    public void setQuestion_num(int question_num) {
        this.question_num = question_num;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public String getForced_jump() {
        return forced_jump;
    }

    public void setForced_jump(String forced_jump) {
        this.forced_jump = forced_jump;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsrequired() {
        return isrequired;
    }

    public void setIsrequired(String isrequired) {
        this.isrequired = isrequired;
    }

    public String getJump_question() {
        return jump_question;
    }

    public void setJump_question(String jump_question) {
        this.jump_question = jump_question;
    }

    public String getMax_option() {
        return max_option;
    }

    public void setMax_option(String max_option) {
        this.max_option = max_option;
    }

    public String getMax_word_num() {
        return max_word_num;
    }

    public void setMax_word_num(String max_word_num) {
        this.max_word_num = max_word_num;
    }

    public String getMin_option() {
        return min_option;
    }

    public void setMin_option(String min_option) {
        this.min_option = min_option;
    }

    public String getMin_word_num() {
        return min_word_num;
    }

    public void setMin_word_num(String min_word_num) {
        this.min_word_num = min_word_num;
    }

    public ArrayList<TaskEditoptionsInfo> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<TaskEditoptionsInfo> options) {
        this.options = options;
    }

    public String getPrompt() {
        if (TextUtils.isEmpty(prompt) || prompt.equals("null")) {
            return "";
        } else {
            return prompt;
        }
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getQuestion_name() {
        return question_name;
    }

    public void setQuestion_name(String question_name) {
        this.question_name = question_name;
    }

    public String getQuestion_type() {
        return question_type;
    }

    public void setQuestion_type(String question_type) {
        this.question_type = question_type;
    }

    @Override
    public String toString() {
        return "TaskEditInfo{" +
                "id='" + id + '\'' +
                ", question_type='" + question_type + '\'' +
                ", question_name='" + question_name + '\'' +
                ", prompt='" + prompt + '\'' +
                ", max_option='" + max_option + '\'' +
                ", min_option='" + min_option + '\'' +
                ", max_word_num='" + max_word_num + '\'' +
                ", min_word_num='" + min_word_num + '\'' +
                ", isrequired='" + isrequired + '\'' +
                ", forced_jump='" + forced_jump + '\'' +
                ", jump_question='" + jump_question + '\'' +
                ", options=" + options +
                ", question_num=" + question_num +
                ", answers='" + answers + '\'' +
                ", answersNote='" + answersNote + '\'' +
                ", view=" + view +
                '}';
    }
}
