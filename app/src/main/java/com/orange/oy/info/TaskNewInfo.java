package com.orange.oy.info;

import android.text.TextUtils;

import com.orange.oy.base.Tools;

import java.io.Serializable;

/**
 * Created by xiedongyan on 2017/3/13.
 */

public class TaskNewInfo implements Serializable {

    private static final long serialVersionUID = -5626951898232573210L;
    /**
     * id : 488
     * project_name : 217理想测试项目
     * project_code : 217
     * project_type : 众包项目
     * is_record : 0
     * photo_compression : 2
     * begin_date : 2017-03-13
     * end_date : 2017-04-08
     * is_download : 0
     * is_watermark : 0
     * code : 理想
     * brand : 联想
     * is_takephoto : 1
     * type : 1
     * show_type : null
     * check_time : 11
     * min_reward : 1
     * max_reward : 14
     * "reward_type":"奖励类型，1为现金，2为礼品，3为现金+礼品",
     * "gift_url":"礼品奖励图片地址"
     */
    private String gift_url;
    private String reward_type;
    private String rob_state;
    private String id;
    private String project_name;
    private String project_code;
    private String project_type;
    private int is_record;
    private String photo_compression;
    private String begin_date;
    private String end_date;
    private int is_download;
    private int is_watermark;
    private String code;
    private String brand;
    private int is_takephoto;
    private String type;
    private String show_type;
    private String check_time;
    private String min_reward;
    private String max_reward;
    private String project_property;//1为分包项目，2为众包项目，3为新手演练项目
    private String anonymous_state;



    public String getGift_url() {
        return gift_url;
    }

    public void setGift_url(String gift_url) {
        this.gift_url = gift_url;
    }

    public String getReward_type() {
        return reward_type;
    }

    public void setReward_type(String reward_type) {
        this.reward_type = reward_type;
    }

    public String getAnonymous_state() {
        return anonymous_state;
    }

    public void setAnonymous_state(String anonymous_state) {
        this.anonymous_state = anonymous_state;
    }

    public String getRob_state() {
        return rob_state;
    }

    public void setRob_state(String rob_state) {
        this.rob_state = rob_state;
    }

    /**
     * projectid : 488
     * picurl : /file/task/911F06A5128B399C756301DC425F142B.png
     */

    private String projectid;
    private String picurl;
    private String title;
    private String project_person;//发布人
    private String publish_time;//发布时间
    private String money_unit;//金额单位
    /**
     * project_id : 497
     * photo_compression : 2
     * store_id : 128680
     * store_num : YL1
     * store_address :
     * store_name : 新手演练任务
     * accessed_num : 17801093295
     * p_id : null
     * p_name : null
     * p_desc : null
     * package_attribute : null
     * category1_name : null
     * category1_content : null
     * category2_name : null
     * category2_content : null
     * category3_name : null
     * category3_content : null
     * p_is_invalid : null
     * task_id : 2518
     * task_name : 视频任务
     * task_type : 2
     * task_note : 录制视频
     * is_package : 0
     * task_detail : {"batch":1,"taskpackid":"","taskid":"2518","taskName":"","url":"","note":""}
     * task_content : null
     * batch : 1
     * p_batch : 1
     * outlet_batch : 1
     * is_package_task : 0
     * invalid_type : null
     */

    private String store_id;//新手任务增加的参数
    private String store_num;
    private String store_address;
    private String store_name;
    private String accessed_num;
    private String p_id;
    private String p_name;
    private String p_desc;
    private String package_attribute;
    private String category1_name;
    private String category1_content;
    private String category2_name;
    private String category2_content;
    private String category3_name;
    private String category3_content;
    private String p_is_invalid;
    private String task_id;
    private String task_name;
    private String task_type;
    private String task_note;
    private String is_package;
    private String task_detail;
    private String task_content;
    private String batch;
    private String p_batch;
    private String outlet_batch;
    private String is_package_task;
    private String invalid_type;
    private String certification;//企业认证状态 1代表已认证，0为未认证
    private String standard_state;//是否配标准说明
    private String photo_url;//轮播图片
    private String is_project;
    private String link_url;

    /**
     * 3.15
     *
     * @return
     */
    private String project_model;//项目投放类型，1为个人项目，2为战队+个人项目，3为战队项目

    public String getProject_model() {
        return project_model;
    }

    public void setProject_model(String project_model) {
        this.project_model = project_model;
    }

    public String getIs_project() {
        return is_project;
    }

    public void setIs_project(String is_project) {
        this.is_project = is_project;
    }

    public String getLink_url() {
        return link_url;
    }

    public void setLink_url(String link_url) {
        this.link_url = link_url;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getStandard_state() {
        return standard_state;
    }

    public void setStandard_state(String standard_state) {
        this.standard_state = standard_state;
    }

    public String getCertification() {
        return certification;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }

    public String getMoney_unit() {
        return money_unit;
    }

    public void setMoney_unit(String money_unit) {
        this.money_unit = money_unit;
    }

    public String getProject_person() {
        return project_person;
    }

    public void setProject_person(String project_person) {
        this.project_person = project_person;
    }

    public String getPublish_time() {
        return publish_time;
    }

    public void setPublish_time(String publish_time) {
        this.publish_time = publish_time;
    }

    public String getProject_property() {
        return project_property;
    }

    public void setProject_property(String project_property) {
        this.project_property = project_property;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShow_type() {
        return show_type;
    }

    public void setShow_type(String show_type) {
        this.show_type = show_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public String getProject_code() {
        return project_code;
    }

    public void setProject_code(String project_code) {
        this.project_code = project_code;
    }

    public String getProject_type() {
        return project_type;
    }

    public void setProject_type(String project_type) {
        this.project_type = project_type;
    }

    public int getIs_record() {
        return is_record;
    }

    public void setIs_record(int is_record) {
        this.is_record = is_record;
    }

    public String getPhoto_compression() {
        return photo_compression;
    }

    public void setPhoto_compression(String photo_compression) {
        if (photo_compression.equals("1")) {
            this.photo_compression = "300";
        } else if (photo_compression.equals("2")) {
            this.photo_compression = "500";
        } else if (photo_compression.equals("3")) {
            this.photo_compression = "1024";
        } else if (photo_compression.equals("4")) {
            this.photo_compression = "-1";
        } else {
            this.photo_compression = "500";
        }
    }

    public String getBegin_date() {
        return begin_date;
    }

    public void setBegin_date(String begin_date) {
        this.begin_date = begin_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public int getIs_download() {
        return is_download;
    }

    public void setIs_download(int is_download) {
        this.is_download = is_download;
    }

    public int getIs_watermark() {
        return is_watermark;
    }

    public void setIs_watermark(int is_watermark) {
        this.is_watermark = is_watermark;
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

    public int getIs_takephoto() {
        return is_takephoto;
    }

    public void setIs_takephoto(int is_takephoto) {
        this.is_takephoto = is_takephoto;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCheck_time() {
        return check_time;
    }

    public void setCheck_time(String check_time) {
        this.check_time = check_time;
    }

    public String getMin_reward() {
        return min_reward;
    }

    public void setMin_reward(String min_reward) {
        if (TextUtils.isEmpty(min_reward)) {
            min_reward = "-";
        } else {
            double d = Tools.StringToDouble(min_reward);
            if (d - (int) d > 0) {
                min_reward = String.valueOf(d);
            } else {
                min_reward = String.valueOf((int) d);
            }
        }
        this.min_reward = min_reward;
    }

    public String getMax_reward() {
        return max_reward;
    }

    public void setMax_reward(String max_reward) {
        if (TextUtils.isEmpty(max_reward)) {
            max_reward = "-";
        } else {
            double d = Tools.StringToDouble(max_reward);
            if (d - (int) d > 0) {
                max_reward = String.valueOf(d);
            } else {
                max_reward = String.valueOf((int) d);
            }
        }
        this.max_reward = max_reward;
    }

    public String getProjectid() {
        return projectid;
    }

    public void setProjectid(String projectid) {
        this.projectid = projectid;
    }

    public String getPicurl() {
        return picurl;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getStore_num() {
        return store_num;
    }

    public void setStore_num(String store_num) {
        this.store_num = store_num;
    }

    public String getStore_address() {
        return store_address;
    }

    public void setStore_address(String store_address) {
        this.store_address = store_address;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getAccessed_num() {
        return accessed_num;
    }

    public void setAccessed_num(String accessed_num) {
        this.accessed_num = accessed_num;
    }

    public String getP_id() {
        return p_id;
    }

    public void setP_id(String p_id) {
        this.p_id = p_id;
    }

    public String getP_name() {
        return p_name;
    }

    public void setP_name(String p_name) {
        this.p_name = p_name;
    }

    public String getP_desc() {
        return p_desc;
    }

    public void setP_desc(String p_desc) {
        this.p_desc = p_desc;
    }

    public String getPackage_attribute() {
        return package_attribute;
    }

    public void setPackage_attribute(String package_attribute) {
        this.package_attribute = package_attribute;
    }

    public String getCategory1_name() {
        return category1_name;
    }

    public void setCategory1_name(String category1_name) {
        this.category1_name = category1_name;
    }

    public String getCategory1_content() {
        return category1_content;
    }

    public void setCategory1_content(String category1_content) {
        this.category1_content = category1_content;
    }

    public String getCategory2_name() {
        return category2_name;
    }

    public void setCategory2_name(String category2_name) {
        this.category2_name = category2_name;
    }

    public String getCategory2_content() {
        return category2_content;
    }

    public void setCategory2_content(String category2_content) {
        this.category2_content = category2_content;
    }

    public String getCategory3_name() {
        return category3_name;
    }

    public void setCategory3_name(String category3_name) {
        this.category3_name = category3_name;
    }

    public String getCategory3_content() {
        return category3_content;
    }

    public void setCategory3_content(String category3_content) {
        this.category3_content = category3_content;
    }

    public String getP_is_invalid() {
        return p_is_invalid;
    }

    public void setP_is_invalid(String p_is_invalid) {
        this.p_is_invalid = p_is_invalid;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public String getTask_type() {
        return task_type;
    }

    public void setTask_type(String task_type) {
        this.task_type = task_type;
    }

    public String getTask_note() {
        return task_note;
    }

    public void setTask_note(String task_note) {
        this.task_note = task_note;
    }

    public String getIs_package() {
        return is_package;
    }

    public void setIs_package(String is_package) {
        this.is_package = is_package;
    }

    public String getTask_detail() {
        return task_detail;
    }

    public void setTask_detail(String task_detail) {
        this.task_detail = task_detail;
    }

    public String getTask_content() {
        return task_content;
    }

    public void setTask_content(String task_content) {
        this.task_content = task_content;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getP_batch() {
        return p_batch;
    }

    public void setP_batch(String p_batch) {
        this.p_batch = p_batch;
    }

    public String getOutlet_batch() {
        return outlet_batch;
    }

    public void setOutlet_batch(String outlet_batch) {
        this.outlet_batch = outlet_batch;
    }

    public String getIs_package_task() {
        return is_package_task;
    }

    public void setIs_package_task(String is_package_task) {
        this.is_package_task = is_package_task;
    }

    public String getInvalid_type() {
        return invalid_type;
    }

    public void setInvalid_type(String invalid_type) {
        this.invalid_type = invalid_type;
    }
}
