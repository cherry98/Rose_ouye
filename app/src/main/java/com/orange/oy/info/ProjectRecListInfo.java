package com.orange.oy.info;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;

/**
 * Created by xiedongyan on 2017/3/14.
 */

public class ProjectRecListInfo implements Serializable {

    private static final long serialVersionUID = -540894787975114901L;
    /**
     * taskid : 3198
     * taskbatch : 1
     * taskname : 招募的录音任务
     * tasktype : 5
     * datas : null
     * note : null
     * questionnaire_type : null
     * pics : null
     */

    private String taskid;
    private String taskbatch;
    private String taskname;
    private String tasktype;
    private String datas;
    private String note;
    private String questionnaire_type;
    private String pics;

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getTaskbatch() {
        return taskbatch;
    }

    public void setTaskbatch(String taskbatch) {
        this.taskbatch = taskbatch;
    }

    public String getTaskname() {
        return taskname;
    }

    public void setTaskname(String taskname) {
        this.taskname = taskname;
    }

    public String getTasktype() {
        return tasktype;
    }

    public void setTasktype(String tasktype) {
        this.tasktype = tasktype;
    }

    public JSONArray getDatas() {
        if (TextUtils.isEmpty(datas)) {
            return null;
        }
        try {
            return new JSONArray(datas);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setDatas(String datas) {
        this.datas = datas;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getQuestionnaire_type() {
        return questionnaire_type;
    }

    public void setQuestionnaire_type(String questionnaire_type) {
        this.questionnaire_type = questionnaire_type;
    }

    public String getPics() {
        return pics;
    }

    public void setPics(String pics) {
        this.pics = pics;
    }

    @Override
    public String toString() {
        return "ProjectRecListInfo{" +
                "taskid='" + taskid + '\'' +
                ", taskbatch='" + taskbatch + '\'' +
                ", taskname='" + taskname + '\'' +
                ", tasktype='" + tasktype + '\'' +
                ", datas=" + datas +
                ", note='" + note + '\'' +
                ", questionnaire_type='" + questionnaire_type + '\'' +
                ", pics='" + pics + '\'' +
                '}';
    }
}
