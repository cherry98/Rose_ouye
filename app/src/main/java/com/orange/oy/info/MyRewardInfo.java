package com.orange.oy.info;

import android.text.TextUtils;

import com.orange.oy.base.Tools;

/**
 * Created by xiedongyan on 2017/3/16.
 */

public class MyRewardInfo {


    /**
     * id : 3
     * outletId : 6
     * personId : 15901440271
     * personName : null
     * outletDesc : null
     * state : 3
     * createDate : 1489473302000
     * outletName : 张鹏飞测试网点
     * outletState : 0
     * projectName : 张鹏飞测试项目
     * money : null
     * exeTime : null
     * project_code : 项目编号
     * is_record : 1
     * photo_compression : 照片清晰度
     * begin_date : 开始时间
     * end_date : 结束时间
     * is_watermark : 1
     * code : 项目代号
     * brand : 项目品牌
     * is_takephoto : 1
     * type : 1
     * check_time : 审核周期
     * project_property :
     * is_exe :   //未通过的网点有可以重做的， 0为不可执行，1为可执行
     * is_desc :
     * is_upload :
     * "project_id":368,
     * "shade":0,
     * "size":0,
     * "outlet_address":"北京市朝阳区东三环中路20号",
     * "outletNum":"35",
     * "money_unit":"元",
     * "is_money":1,
     * "standard_state":1,
     * <p>
     * "unpass_state":2
     * "reward_type":"奖励类型，1为现金，2为礼品，3为现金+礼品",
     * " gift_url":"礼品奖励图片地址"
     */

    private String reward_type;
    private String gift_url;
    private String isclose;
    private String is_money;
    private String standard_state;
    private String unpass_state;//未通过的状态，0为超时，2为已放弃
    private String id;
    private String outletId;
    private String personId;
    private String project_id;
    private String personName;
    private String outletDesc;
    private String state;
    private long createDate;
    private String outletName;
    private String outletState;
    private String projectName;
    private String money;
    private String money2;
    private String exeTime;
    private String project_code;
    private String is_record;
    private String photo_compression;
    private String begin_date;
    private String end_date;
    private String is_watermark;
    private String code;
    private String brand;
    private String is_takephoto;
    private String type;
    private String check_time;
    private String project_property;
    private String is_exe;
    private String is_desc;
    private String is_upload;
    private String money_unit;
    private String outletNum;//网点编号
    private String outlet_address;//网点地址
    private String position_limit;
    private String limit_province;
    private String limit_city;
    public String getReward_type() {
        return reward_type;
    }

    public void setReward_type(String reward_type) {
        this.reward_type = reward_type;
    }

    public String getGift_url() {
        return gift_url;
    }

    public void setGift_url(String gift_url) {
        this.gift_url = gift_url;
    }
    public String getLimit_city() {
        return limit_city;
    }

    public void setLimit_city(String limit_city) {
        this.limit_city = limit_city;
    }

    public String getLimit_province() {
        return limit_province;
    }

    public void setLimit_province(String limit_province) {
        this.limit_province = limit_province;
    }

    public String getPosition_limit() {
        return position_limit;
    }

    public void setPosition_limit(String position_limit) {
        this.position_limit = position_limit;
    }

    public String getIs_money() {
        return is_money;
    }

    public void setIs_money(String is_money) {
        this.is_money = is_money;
    }

    public String getStandard_state() {
        return standard_state;
    }

    public void setStandard_state(String standard_state) {
        this.standard_state = standard_state;
    }

    public String getUnpass_state() {
        return unpass_state;
    }

    public void setUnpass_state(String unpass_state) {
        this.unpass_state = unpass_state;
    }


    public String getIsclose() {
        return isclose;
    }

    public void setIsclose(String isclose) {
        this.isclose = isclose;
    }

    public String getOutlet_address() {
        return outlet_address;
    }

    public void setOutlet_address(String outlet_address) {
        this.outlet_address = outlet_address;
    }

    public String getOutletNum() {
        return outletNum;
    }

    public void setOutletNum(String outletNum) {
        this.outletNum = outletNum;
    }

    public String getMoney_unit() {
        return money_unit;
    }

    public void setMoney_unit(String money_unit) {
        this.money_unit = money_unit;
    }

    public String getMoney2() {
        return money2;
    }

    public void setMoney2(String money2) {
        this.money2 = money2;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOutletId() {
        return outletId;
    }

    public void setOutletId(String outletId) {
        this.outletId = outletId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getOutletDesc() {
        return outletDesc;
    }

    public void setOutletDesc(String outletDesc) {
        //</br>替换成\n 2017.10.25 zhangpengfei
        this.outletDesc = outletDesc.replaceAll("</br>", "\n");
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public String getOutletName() {
        return outletName;
    }

    public void setOutletName(String outletName) {
        this.outletName = outletName;
    }

    public String getOutletState() {
        return outletState;
    }

    public void setOutletState(String outletState) {
        this.outletState = outletState;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        if (TextUtils.isEmpty(money)) {
            money = "-";
        } else {
            double d = Tools.StringToDouble(money);
            if (d - (int) d > 0) {
                money = String.valueOf(d);
            } else {
                money = String.valueOf((int) d);
            }
        }
        this.money = money;
    }

    public String getExeTime() {
        return exeTime;
    }

    public void setExeTime(String exeTime) {
        if (!TextUtils.isEmpty(exeTime)) {
            int time = Tools.StringToInt(exeTime);
            if (time > 0) {
                if (time < 60) {
                    this.exeTime = time + "分";
                } else if (time < 1440 && time >= 60) {
                    int hour = time / 60;
                    int min = time % 60;
                    this.exeTime = hour + "时" + min + "分";
                } else if (time >= 1440) {
                    int day = time / 1440;
                    int hour = (time % 1440) / 60;
                    int min = time % 60;
                    this.exeTime = day + "天" + hour + "时" + min + "分";
                }
            }
        }
    }

    public String getProject_code() {
        return project_code;
    }

    public void setProject_code(String project_code) {
        this.project_code = project_code;
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

    public String getProject_property() {
        return project_property;
    }

    public void setProject_property(String project_property) {
        this.project_property = project_property;
    }

    public String getIs_exe() {
        return is_exe;
    }

    public void setIs_exe(String is_exe) {
        this.is_exe = is_exe;
    }

    public String getIs_desc() {
        return is_desc;
    }

    public void setIs_desc(String is_desc) {
        this.is_desc = is_desc;
    }

    public String getIs_upload() {
        return is_upload;
    }

    public void setIs_upload(String is_upload) {
        this.is_upload = is_upload;
    }
}
