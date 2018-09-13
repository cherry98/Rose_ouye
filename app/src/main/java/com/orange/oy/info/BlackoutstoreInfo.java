package com.orange.oy.info;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;

public class BlackoutstoreInfo implements Serializable {
    private static final long serialVersionUID = -7060210544600464481L;
    private String projectid;
    private String projectname;
    private String stroeid;
    private String storenum;
    private String storename;
    private String taskid;
    private String batch;
    private String taskbatch;
    private String taskname;
    private String tasktype;//3:记录任务；5:录音任务
    private String datas;
    private String note;
    private String questionnaire_type;//记录任务模式
    private String num;
    private String wuxiao;
    private String photo_type;
    private String sta_location;
    private String isphoto;
    private int is_watermark;
    private int min_num;
    private String pics;//说明页实例图片
    private String photo_compression;
    private String address;
    private String province;
    private String city;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoto_compression() {
        return photo_compression;
    }

    public void setPhoto_compression(String photo_compression) {
        this.photo_compression = photo_compression;
    }

    public String getPics() {
        return pics;
    }

    public void setPics(String pics) {
        this.pics = pics;
    }

    public int getIs_watermark() {
        return is_watermark;
    }

    public void setIs_watermark(int is_watermark) {
        this.is_watermark = is_watermark;
    }

    public int getMin_num() {
        return min_num;
    }

    public void setMin_num(int min_num) {
        this.min_num = min_num;
    }

    public String getIsphoto() {
        return isphoto;
    }

    public void setIsphoto(String isphoto) {
        this.isphoto = isphoto;
    }

    public String getSta_location() {
        return sta_location;
    }

    public void setSta_location(String sta_location) {
        this.sta_location = sta_location;
    }

    public String getPhoto_type() {
        return photo_type;
    }

    public void setPhoto_type(String photo_type) {
        this.photo_type = photo_type;
    }

    public String getWuxiao() {
        return wuxiao;
    }

    public void setWuxiao(String wuxiao) {
        this.wuxiao = wuxiao;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
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

    public String getStroeid() {
        return stroeid;
    }

    public void setStroeid(String stroeid) {
        this.stroeid = stroeid;
    }

    public String getStorenum() {
        return storenum;
    }

    public void setStorenum(String storenum) {
        this.storenum = storenum;
    }

    public String getStorename() {
        return storename;
    }

    public void setStorename(String storename) {
        this.storename = storename;
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
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
}
