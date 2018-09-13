package com.orange.oy.info.mycorps;

import android.text.TextUtils;

import com.orange.oy.base.Tools;

import java.io.Serializable;

/**
 * Created by Lenovo on 2018/5/28.
 * 网点分布的明细==战队 V3.15（等待执行有价格无价格）
 */

public class CorpGrabDetailInfo implements Serializable {

    /**
     * outlet_id : 网点id
     * outlet_name : 网点名称
     * outlet_num : 网点编号
     * outlet_address : 网点地址
     * isdetail : 执行时间类型，0为没有排程时间，以项目执行日期为执行时间或只有排程日期没有时间，1为有排程日期及时间，值为0时使用datelist中的时间
     * timeDetail:执行时间列表
     */

    private String outlet_id;
    private String outlet_name;
    private String outlet_num;
    private String outlet_address;
    private String timeDetail;
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
    /**
     * exe_state : 网点状态，（9待分配, 10确认中, 2为待执行，3为执行中 ）
     * accessed_num : 执行人员账号
     * accessed_name : 执行人员昵称
     * is_exe : 当前时间是否可执行，1为可执行，0为不可执行
     * is_desc : 是否有网点说明，1为有，0为无
     * money : 网点金额
     * confirm_time : 等待确认时间，单位为分钟
     */

    private String exe_state;
    private String accessed_num;
    private String accessed_name;
    private String is_exe;
    private String is_desc;
    private String money;
    private String confirm_time;
    /**
     * user_name : 拒绝人昵称
     * create_time : 拒绝的时间
     * reason : 填写的原因
     */

    private String user_name;
    private String create_time;
    private String reason;
    private boolean is_haveReason;
    private boolean isShowCheck;//是否显示复选框
    private boolean isCheck;//是否选中
    private String project_type;

    private String primary;//价格调整前的金额
    private String current;//调整的当前金额
    private boolean isMax;//是否达到最大金额
    private boolean isMin;//是否达到最小金额

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public boolean isMax() {
        return isMax;
    }

    public void setMax(boolean max) {
        isMax = max;
    }

    public boolean isMin() {
        return isMin;
    }

    public void setMin(boolean min) {
        isMin = min;
    }

    public String getProject_type() {
        return project_type;
    }

    public void setProject_type(String project_type) {
        this.project_type = project_type;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public boolean isShowCheck() {
        return isShowCheck;
    }

    public void setShowCheck(boolean showCheck) {
        isShowCheck = showCheck;
    }

    public boolean is_haveReason() {
        return is_haveReason;
    }

    public void setIs_haveReason(boolean is_haveReason) {
        this.is_haveReason = is_haveReason;
    }

    public String getTimeDetail() {
        return timeDetail;
    }

    public void setTimeDetail(String timeDetail) {
        this.timeDetail = timeDetail;
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

    public String getExe_state() {
        return exe_state;
    }

    public void setExe_state(String exe_state) {
        this.exe_state = exe_state;
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

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
