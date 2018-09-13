package com.orange.oy.info.mycorps;

import android.text.TextUtils;

import com.orange.oy.base.Tools;

/**
 * Created by Administrator on 2018/5/30.
 */

public class ProjectStateInfo {


    /**
     * outlet_id : 网点id
     * outlet_name : 网点名称
     * outlet_num : 网点编号
     * outlet_address : 网点地址
     * state : 状态，-1为上传中，0为审核中，2为未通过，3为已通过
     * accessed_num : 执行人员账号
     * accessed_name : 执行人员昵称
     * is_exe : 是否可执行，1为可执行，0为不可执行
     * is_abandon : 是否放弃了，1为是，0为否
     * is_distribute : 是否可分配，1为可以，0为不可以
     * is_desc : 是否有网点说明，1为有，0为无
     * money : 网点金额
     * confirm_time : 等待确认时间，单位为分钟
     * isdetail : 执行时间类型，0为没有排程时间，以项目执行日期为执行时间或只有排程日期没有时间，1为有排程日期及时间，值为0时使用datelist中的时间
     * datelist : []
     * date1 : 2018-05-03
     * details1 : ["09:00:00-12:00:00"]
     * date2 : 2018-05-04
     * details2 : ["09:00:00-21:00:00"]
     * date3 : 2018-05-05
     * details3 : ["09:00:00-21:00:00"]
     * date4 : 2018-05-07
     * details4 : ["10:05:00-22:00:00"]
     * date5 : 2018-05-08
     * details5 : ["08:00:00-20:00:00"]
     * date6 : 2018-05-09
     * details6 : ["08:00:00-21:00:00"]
     * date7 : 2018-05-24
     * details7 : ["07:00:00-11:00:00","14:00:00-19:00:00"]
     */

    private String outlet_id;
    private String outlet_name;
    private String outlet_num;
    private String outlet_address;
    private String state;
    private String accessed_num;
    private String accessed_name;
    private String is_exe;
    private String is_abandon;

    private String is_distribute;
    private String is_desc;
    private String money;
    private String confirm_time;
    private String isdetail;
    private String unpass_reason;
    //执行时间类型，0为没有排程时间，以项目执行日期为执行时间或只有排程日期没有时间，
    // 1为有排程日期及时间，值为0时使用datelist中的时间",

    private String timedetail;//执行时间
    /**
     * projectid : 1
     * project_name : 项目名称
     * project_person : 发布商家
     * begin_date : 开始时间
     * end_date : 结束时间
     * check_time : 审核周期
     * standard_state : 是否有项目说明，1为有，0为没有
     * project_type : 项目类型，1为有网点，5为无网点
     */

    private String projectid;
    private String project_name;
    private String project_person;
    private String begin_date;
    private String end_date;
    private String check_time;
    private String standard_state;
    private String project_type;
    /**
     * is_record : 是否全程录音（1为是，0为否）
     * photo_compression : 照片清晰度
     * is_watermark : 是否添加照片水印
     * code : 项目代号
     * brand : 代号对应的品牌
     * is_takephoto : 是否支持连续拍照（1为是，0为否）
     * position_limit : 无店单项目是否有定位限制，1为有，0为无
     * limit_province : 定位限制省份
     * limit_city : 定位限制城市
     * limit_county : 定位限制区域
     */

    private String is_record;
    private String photo_compression;
    private String is_watermark;
    private String code;
    private String brand;
    private String is_takephoto;
    private String position_limit;
    private String limit_province;
    private String limit_city;
    private String limit_county;


    public String getUnpass_reason() {
        return unpass_reason;
    }

    public void setUnpass_reason(String unpass_reason) {
        this.unpass_reason = unpass_reason;
    }

    public String getTimedetail() {
        if (TextUtils.isEmpty(timedetail) || timedetail.equals("null")) {
            timedetail = "";
        }
        return timedetail;
    }

    public void setTimedetail(String timedetail) {
        this.timedetail = timedetail;
    }

    public String getOutlet_id() {
        return outlet_id;
    }

    public void setOutlet_id(String outlet_id) {
        this.outlet_id = outlet_id;
    }

    public String getOutlet_name() {
        return outlet_name;
    }

    public void setOutlet_name(String outlet_name) {
        this.outlet_name = outlet_name;
    }

    public String getOutlet_num() {
        return outlet_num;
    }

    public void setOutlet_num(String outlet_num) {
        this.outlet_num = outlet_num;
    }

    public String getOutlet_address() {
        return outlet_address;
    }

    public void setOutlet_address(String outlet_address) {
        this.outlet_address = outlet_address;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAccessed_num() {
        return accessed_num;
    }

    public void setAccessed_num(String accessed_num) {
        this.accessed_num = accessed_num;
    }

    public String getAccessed_name() {
        return accessed_name;
    }

    public void setAccessed_name(String accessed_name) {
        this.accessed_name = accessed_name;
    }

    public String getIs_exe() {
        return is_exe;
    }

    public void setIs_exe(String is_exe) {
        this.is_exe = is_exe;
    }

    public String getIs_abandon() {
        return is_abandon;
    }

    public void setIs_abandon(String is_abandon) {
        this.is_abandon = is_abandon;
    }

    public String getIs_distribute() {
        return is_distribute;
    }

    public void setIs_distribute(String is_distribute) {
        this.is_distribute = is_distribute;
    }

    public String getIs_desc() {
        return is_desc;
    }

    public void setIs_desc(String is_desc) {
        this.is_desc = is_desc;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getConfirm_time() {
        return confirm_time;
    }

    public void setConfirm_time(String confirm_time) {
        if (!TextUtils.isEmpty(confirm_time) && !"null".equals(confirm_time)) {
            int time = Tools.StringToInt(confirm_time);
            if (time > 0) {
                if (time < 60) {
                    this.confirm_time = time + "分";
                } else if (time < 1440 && time >= 60) {
                    int hour = time / 60;
                    int min = time % 60;
                    this.confirm_time = hour + "时" + min + "分";
                } else if (time >= 1440) {
                    int day = time / 1440;
                    int hour = (time % 1440) / 60;
                    int min = time % 60;
                    this.confirm_time = day + "天" + hour + "小时" + min + "分钟";
                }
            }
        } else {
            this.confirm_time = confirm_time;
        }
    }

    public String getIsdetail() {
        return isdetail;
    }

    public void setIsdetail(String isdetail) {
        this.isdetail = isdetail;
    }


    public String getProjectid() {
        return projectid;
    }

    public void setProjectid(String projectid) {
        this.projectid = projectid;
    }

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public String getProject_person() {
        return project_person;
    }

    public void setProject_person(String project_person) {
        this.project_person = project_person;
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

    public String getCheck_time() {
        return check_time;
    }

    public void setCheck_time(String check_time) {
        this.check_time = check_time;
    }

    public String getStandard_state() {
        return standard_state;
    }

    public void setStandard_state(String standard_state) {
        this.standard_state = standard_state;
    }

    public String getProject_type() {
        return project_type;
    }

    public void setProject_type(String project_type) {
        this.project_type = project_type;
    }

    public String getIs_record() {
        return is_record;
    }

    public void setIs_record(String is_record) {
        this.is_record = is_record;
    }

    public String getPhoto_compression() {
        return photo_compression;
    }

    public void setPhoto_compression(String photo_compression) {
        this.photo_compression = photo_compression;
    }

    public String getIs_watermark() {
        return is_watermark;
    }

    public void setIs_watermark(String is_watermark) {
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

    public String getIs_takephoto() {
        return is_takephoto;
    }

    public void setIs_takephoto(String is_takephoto) {
        this.is_takephoto = is_takephoto;
    }

    public String getPosition_limit() {
        return position_limit;
    }

    public void setPosition_limit(String position_limit) {
        this.position_limit = position_limit;
    }

    public String getLimit_province() {
        return limit_province;
    }

    public void setLimit_province(String limit_province) {
        this.limit_province = limit_province;
    }

    public String getLimit_city() {
        return limit_city;
    }

    public void setLimit_city(String limit_city) {
        this.limit_city = limit_city;
    }

    public String getLimit_county() {
        return limit_county;
    }

    public void setLimit_county(String limit_county) {
        this.limit_county = limit_county;
    }
}
