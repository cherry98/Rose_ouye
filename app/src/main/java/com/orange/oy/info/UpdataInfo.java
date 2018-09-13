package com.orange.oy.info;

import android.text.TextUtils;

import java.util.Map;

public class UpdataInfo {
    private String uniquelyNum;
    private String url;
    private String type;
    private String paths;
    private Map<String, String> fileParame;
    private Map<String, String> parame;
    private String compression;

    private int is_completed;//0：不需要，1：需要
    private String completed_url;
    private String completed_parameter;
    private String taskType;
    private String fileType;//文件类型

    private String username;
    private String projectid;
    private String projecname;
    private String stroeid;
    private String storename;
    private String packageId;
    private String packageName;
    private String taskId;
    private String taskName;
    private String category1;
    private String category2;
    private String category3;
    private String taskTime;
    private String code;
    private String brand;

    private int fileNum;

    private String taskBatch;//任务批次，上传时临时插入

    private boolean isBlack;

    private String question_id;//记录任务录音用，上传时临时插入

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    public boolean isBlack() {
        return isBlack;
    }

    public void setBlack(boolean black) {
        isBlack = black;
    }

    public String getTaskBatch() {
        if (TextUtils.isEmpty(taskBatch)) {
            return "";
        }
        return taskBatch;
    }

    public void setTaskBatch(String taskBatch) {
        this.taskBatch = taskBatch;
    }

    public int getFileNum() {
        return fileNum;
    }

    public void setFileNum(int fileNum) {
        this.fileNum = fileNum;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(String taskTime) {
        this.taskTime = taskTime;
    }

    public String getTaskId() {
        if (TextUtils.isEmpty(taskId)) {
            return "";
        }
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getCategory1() {
        return category1;
    }

    public void setCategory1(String category1) {
        this.category1 = category1;
    }

    public String getCategory2() {
        return category2;
    }

    public void setCategory2(String category2) {
        this.category2 = category2;
    }

    public String getCategory3() {
        return category3;
    }

    public void setCategory3(String category3) {
        this.category3 = category3;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProjectid() {
        return projectid;
    }

    public void setProjectid(String projectid) {
        this.projectid = projectid;
    }

    public String getProjecname() {
        return projecname;
    }

    public void setProjecname(String projecname) {
        this.projecname = projecname;
    }

    public String getStroeid() {
        return stroeid;
    }

    public void setStroeid(String stroeid) {
        this.stroeid = stroeid;
    }

    public String getStorename() {
        if (TextUtils.isEmpty(storename)) {
            storename = "";
        }
        return storename;
    }

    public void setStorename(String storename) {
        this.storename = storename;
    }

    public String getTaskType() {
        if (taskType == null) {
            taskType = "";
        }
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public int getIs_completed() {
        return is_completed;
    }

    public void setIs_completed(int is_completed) {
        this.is_completed = is_completed;
    }

    public String getCompleted_url() {
        return completed_url;
    }

    public void setCompleted_url(String completed_url) {
        this.completed_url = completed_url;
    }

    public String getCompleted_parameter() {
        return completed_parameter;
    }

    public void setCompleted_parameter(String completed_parameter) {
        this.completed_parameter = completed_parameter;
    }

    public String getUniquelyNum() {
        return uniquelyNum;
    }

    public void setUniquelyNum(String uniquelyNum) {
        this.uniquelyNum = uniquelyNum;
    }

    public String getCompression() {
        return compression;
    }

    public void setCompression(String compression) {
        this.compression = compression;
    }

    public String getPaths() {
        return paths;
    }

    public void setPaths(String paths) {
        this.paths = paths;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getFileParame() {
        return fileParame;
    }

    public void setFileParame(Map<String, String> fileParame) {
        this.fileParame = fileParame;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getParame() {
        return parame;
    }

    public void setParame(Map<String, String> parame) {
        this.parame = parame;
    }

}
