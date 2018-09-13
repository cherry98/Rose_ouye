package com.orange.oy.info.shakephoto;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/9/4.
 * 题目 info V3.21
 */

public class QuestionListInfo implements Serializable {

    private static final long serialVersionUID = 354138206562946476L;
    /**
     * question_id : 5316
     * question_type : 问题类型，1为单选，2为多选，4为填空
     * question_name : 题目名称
     * options : [{"option_id":"8672","option_name":"选项名字","option_num":"选项序号"}]
     * max_option : 最多选择选项
     * min_option : 最少选择选型
     * isrequired : 是否必填,1为是，0为否
     * question_num : 问题编号
     */

    private String question_id;
    private String question_type;
    private String question_name;
    private String max_option;
    private String min_option;
    private String isrequired;
    private String question_num;
    private ArrayList<OptionsListInfo> options;

    public ArrayList<OptionsListInfo> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<OptionsListInfo> options) {
        this.options = options;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    public String getQuestion_type() {
        return question_type;
    }

    public void setQuestion_type(String question_type) {
        this.question_type = question_type;
    }

    public String getQuestion_name() {
        return question_name;
    }

    public void setQuestion_name(String question_name) {
        this.question_name = question_name;
    }

    public String getMax_option() {
        return max_option;
    }

    public void setMax_option(String max_option) {
        this.max_option = max_option;
    }

    public String getMin_option() {
        return min_option;
    }

    public void setMin_option(String min_option) {
        this.min_option = min_option;
    }

    public String getIsrequired() {
        return isrequired;
    }

    public void setIsrequired(String isrequired) {
        this.isrequired = isrequired;
    }

    public String getQuestion_num() {
        return question_num;
    }

    public void setQuestion_num(String question_num) {
        this.question_num = question_num;
    }

}
