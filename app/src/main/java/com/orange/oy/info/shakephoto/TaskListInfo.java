package com.orange.oy.info.shakephoto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2018/6/28.
 */

public class TaskListInfo implements Serializable {

    /**
     * task_id : 任务id
     * task_type : 任务类型（1：拍照任务；2：视频任务；3：记录/问卷任务；4：定位任务；5录音任务；6：扫码任务；7：电话任务；8：防止翻拍任务）
     * task_name : 任务名称
     * note : 任务说明
     * is_watermark : 获取任务执行位置，1为获取（即添加水印），0为不获取（不加水印）
     * local_photo : 是否可以调用本地相册，1为可以，0为不可以
     * photourl : ["实例图片地址1","示例图片地址2"]
     * "online_store_name": "体验任务的网店名称",
     * "online_store_url": "体验任务的网店网址",
     * <p>
     * videourl
     */
    private ArrayList<String> videourl; //视频
    private String online_store_name; //"体验任务的网店名称",
    private String online_store_url;//"体验任务的网店网址",
    private String task_id;
    private String task_type;
    private String task_name;
    private String note;
    private String is_watermark;
    private String local_photo;
    private ArrayList<String> photourl;
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

    private String sta_location;
    private ArrayList<QuestionListInfo> question_list;

    public String getOnline_store_url() {
        return online_store_url;
    }

    public void setOnline_store_url(String online_store_url) {
        this.online_store_url = online_store_url;
    }

    public String getOnline_store_name() {
        return online_store_name;
    }

    public void setOnline_store_name(String online_store_name) {
        this.online_store_name = online_store_name;
    }

    public ArrayList<QuestionListInfo> getQuestion_list() {
        return question_list;
    }

    public void setQuestion_list(ArrayList<QuestionListInfo> question_list) {
        this.question_list = question_list;
    }

    public ArrayList<String> getVideourl() {
        return videourl;
    }

    public void setVideourl(ArrayList<String> videourl) {
        this.videourl = videourl;
    }

    public ArrayList<String> getPhotourl() {
        return photourl;
    }

    public void setPhotourl(ArrayList<String> photourl) {
        this.photourl = photourl;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getTask_type() {
        return task_type;
    }

    public void setTask_type(String task_type) {
        this.task_type = task_type;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getIs_watermark() {
        return is_watermark;
    }

    public void setIs_watermark(String is_watermark) {
        this.is_watermark = is_watermark;
    }

    public String getLocal_photo() {
        return local_photo;
    }

    public void setLocal_photo(String local_photo) {
        this.local_photo = local_photo;
    }

    public String getSta_location() {
        return sta_location;
    }

    public void setSta_location(String sta_location) {
        this.sta_location = sta_location;
    }
}
