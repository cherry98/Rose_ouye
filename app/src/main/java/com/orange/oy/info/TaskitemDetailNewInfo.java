package com.orange.oy.info;

import android.text.TextUtils;
import android.widget.ProgressBar;

import com.orange.oy.view.TaskitemDetail_12View;

public class TaskitemDetailNewInfo {
    private String id;//如果是任务包就是包id，任务就是任务id
    private String name;//同id
    private String task_type;
    private String isClose;//如果是1任务包可以执行，2任务包关闭
    private String isPackage;//是否是任务包
    private String storeid;
    private String storename;
    private String is_invalid;//是否可以关闭，1是可以关闭，0是不可以关闭
    private boolean isCategory;//是否有分类

    private String projectid;
    private String projectname;
    private String storeNum;
    private String code;//代号
    private String brand;//品牌

    private String outlet_batch;//网点任务批次
    private String p_batch;//任务包批次

    //关闭任务包的任务信息
    private String closeTaskid;
    private String closeTaskname;
    private String closeTasktype;//task_type为关闭任务包时需要执行的任务类型，1为拍照任务，2为视频任务
    private String closeInvalidtype;//关闭任务包需要执行的任务类型，1为仅备注，2为拍照任务，3为视频任务

    private int maxTask;//最大拍照数量
    private int fill_num;//已经补拍的数量
    private String is_close;//拍照任务是否是无效，1：无效，0：正常
    private String photo_compression;

    private TaskitemDetail_12View taskitemDetail_12View;//数据源展示界面的句柄
    private int progress;//进度
    private String state;//完成状态 1为已完成,2为资料回收
    private boolean is_Record;//表示录音任务状态
    private String project_type;

    public String getProject_type() {
        return project_type;
    }

    public void setProject_type(String project_type) {
        this.project_type = project_type;
    }

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

    public String getPhoto_compression() {
        return photo_compression;
    }

    public void setPhoto_compression(String photo_compression) {
        this.photo_compression = photo_compression;
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

    public String getCloseTaskid() {
        return closeTaskid;
    }

    public void setCloseTaskid(String closeTaskid) {
        this.closeTaskid = closeTaskid;
    }

    public String getCloseTaskname() {
        return closeTaskname;
    }

    public void setCloseTaskname(String closeTaskname) {
        this.closeTaskname = closeTaskname;
    }

    public String getCloseTasktype() {
        return closeTasktype;
    }

    public void setCloseTasktype(String closeTasktype) {
        this.closeTasktype = closeTasktype;
    }

    public String getCloseInvalidtype() {
        return closeInvalidtype;
    }

    public void setCloseInvalidtype(String closeInvalidtype) {
        this.closeInvalidtype = closeInvalidtype;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getStoreNum() {
        return storeNum;
    }

    public void setStoreNum(String storeNum) {
        this.storeNum = storeNum;
    }

    public String getProjectid() {
        return projectid;
    }

    public void setProjectid(String projectid) {
        this.projectid = projectid;
    }

    public String getProjectname() {
        return projectname;
    }

    public void setProjectname(String projectname) {
        this.projectname = projectname;
    }

    public boolean isCategory() {
        return isCategory;
    }

    public void setIsCategory(boolean isCategory) {
        this.isCategory = isCategory;
    }

    public String getIs_invalid() {
        return is_invalid;
    }

    public void setIs_invalid(String is_invalid) {
        this.is_invalid = is_invalid;
    }

    public String getStoreid() {
        return storeid;
    }

    public void setStoreid(String storeid) {
        this.storeid = storeid;
    }

    public String getIsPackage() {
        return isPackage + "";
    }

    public void setIsPackage(String isPackage) {
        this.isPackage = isPackage;
    }

    public String getIsClose() {
        return isClose;
    }

    public void setIsClose(String isClose) {
        this.isClose = isClose;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTask_type() {
        return task_type;
    }

    public void setTask_type(String task_type) {
        this.task_type = task_type;
    }

    public String getStorename() {
        return storename;
    }

    public void setStorename(String storename) {
        this.storename = storename;
    }
}
