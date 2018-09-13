package com.orange.oy.info;

import android.text.TextUtils;

import com.orange.oy.view.TaskitemDetail_12View;

public class TaskitemListInfo {
    private String p_id;
    private String task_id;
    private String storeid;
    private String taskname;
    private String type;

    private String outlet_batch;//网点任务批次
    private String p_batch;//任务包批次

    private String invalidtype;//关闭任务包任务类型
    private int fill_num;
    private int maxTask;
    private String is_close;

    private TaskitemDetail_12View taskitemDetail_12View;//数据源展示界面的句柄
    private int progress;//进度
    private String state;//完成状态 1为已完成,2为资料回收
    private boolean is_Record;//表示录音任务状态

    public boolean is_Record() {
        return is_Record;
    }

    public void setIs_Record(boolean is_Record) {
        this.is_Record = is_Record;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getProgress() {

        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public TaskitemDetail_12View getTaskitemDetail_12View() {
        return taskitemDetail_12View;
    }

    public void setTaskitemDetail_12View(TaskitemDetail_12View taskitemDetail_12View) {
        this.taskitemDetail_12View = taskitemDetail_12View;
    }

    public String getIs_close() {
        return is_close;
    }

    public void setIs_close(String is_close) {
        this.is_close = is_close;
    }

    public int getFill_num() {
        return fill_num;
    }

    public void setFill_num(int fill_num) {
        this.fill_num = fill_num;
    }

    public int getMaxTask() {
        return maxTask;
    }

    public void setMaxTask(int maxTask) {
        this.maxTask = maxTask;
    }


    public String getInvalidtype() {
        return invalidtype;
    }

    public void setInvalidtype(String invalidtype) {
        this.invalidtype = invalidtype;
    }

    public String getOutlet_batch() {
        return outlet_batch;
    }

    public void setOutlet_batch(String outlet_batch) {
        if (TextUtils.isEmpty(outlet_batch) || "null".equals(outlet_batch)) {
            this.outlet_batch = "1";
        } else {
            this.outlet_batch = outlet_batch;
        }
    }

    public String getP_batch() {
        return p_batch;
    }

    public void setP_batch(String p_batch) {
        if (TextUtils.isEmpty(p_batch) || "null".equals(p_batch)) {
            this.p_batch = "1";
        } else {
            this.p_batch = p_batch;
        }
    }

    public String getP_id() {
        return p_id;
    }

    public void setP_id(String p_id) {
        this.p_id = p_id;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getStoreid() {
        return storeid;
    }

    public void setStoreid(String storeid) {
        this.storeid = storeid;
    }

    public String getTaskname() {
        return taskname;
    }

    public void setTaskname(String taskname) {
        this.taskname = taskname;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
