package com.orange.oy.info;

import android.text.TextUtils;

import com.orange.oy.base.Tools;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TaskDetailLeftInfo {

    private String reward_type;  // 奖励类型，1为现金，2为礼品，3为现金+礼品
    private String gift_url;  // 礼品奖励图片地址
    private String project_name;
    private String project_type;  // project_type值为5时为无店单的项目，无店单的项目不需要显示网点信息（接口中返回的数据和其他的项目的一样，网点信息不显示就行）
    private String id;
    private String code;
    private String name;
    private String identity;
    private String city;
    private String city2;
    private String city3;
    private String citydetail;
    private String number;
    private String time;
    private String isstore;//店铺or活动 区分
    private String is_exe;
    private String isclose;//值为1时代表体验项目拍摄门头照任务置无效了不需要查看详情，其他情况值为0

    private int isOffline;//是否离线了，0：没离线；1：离线了
    private int isCompleted;//离线是否完成了，0：没完成；1：完成了
    private int isEdit;//是否编辑过这条数据，给数据库做识别用
    private String timedetail;//执行时间

    private String projectid;
    private String projectname;
    private String photo_compression;
    private int is_record;
    private int is_watermark;
    private String codeStr;//代号
    private String brand;//品牌

    private String outletnote;//网点说明
    private String longtitude;//经度
    private String latitude;//纬度
    private String money;//抢领钱数
    private String nickname;
    private String exe_time;//剩余时间
    private String havetime;//有无倒计时时间
    private String money_unit;//金额单位
    private String type;//任务类型
    private boolean isShow;//是否显示复选框
    private boolean isChecked;//是否选中Item
    private int exe_type;
    //0为待执行【从头开始】，1是已离店未答体验调查问卷【进答体验问卷页面】，2是执行完成未评论【进评论页面】，3是已评价完【不能点进去】）
    private String experience_state;
    private String check_time;
    private String project_person;
    private String standard_state;
    private String end_date;
    private String begin_date;
    private String outlet_batch;
    private String record_taskid;
    private String abandon;
    private String position_limit;//无店单项目是否有定位限制，1为有，0为无
    private String limit_province;
    private String limit_city;
    private String rob_state;   // "rob_state":"是否可领取，1为可以领取，0为已抢完"

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

    public String getRob_state() {
        return rob_state;
    }

    public void setRob_state(String rob_state) {
        this.rob_state = rob_state;
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

    public String getPosition_limit() {
        return position_limit;
    }

    public void setPosition_limit(String position_limit) {
        this.position_limit = position_limit;
    }


    public String getProject_type() {
        return project_type;
    }

    public void setProject_type(String project_type) {
        this.project_type = project_type;
    }

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public String getIsclose() {
        return isclose;
    }

    public void setIsclose(String isclose) {
        this.isclose = isclose;
    }

    public String getAbandon() {
        return abandon;
    }

    public void setAbandon(String abandon) {
        this.abandon = abandon;
    }

    public String getOutlet_batch() {
        return outlet_batch;
    }

    public void setOutlet_batch(String outlet_batch) {
        this.outlet_batch = outlet_batch;
    }

    public String getRecord_taskid() {
        return record_taskid;
    }

    public void setRecord_taskid(String record_taskid) {
        this.record_taskid = record_taskid;
    }

    public String getProject_person() {
        return project_person;
    }

    public void setProject_person(String project_person) {
        this.project_person = project_person;
    }

    public String getStandard_state() {
        return standard_state;
    }

    public void setStandard_state(String standard_state) {
        this.standard_state = standard_state;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getBegin_date() {
        return begin_date;
    }

    public void setBegin_date(String begin_date) {
        this.begin_date = begin_date;
    }

    public String getCheck_time() {
        return check_time;
    }

    public void setCheck_time(String check_time) {
        this.check_time = check_time;
    }

    public String getExperience_state() {
        return experience_state;
    }

    public void setExperience_state(String experience_state) {
        this.experience_state = experience_state;
    }

    public int getExe_type() {
        return exe_type;
    }

    public void setExe_type(int exe_type) {
        this.exe_type = exe_type;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMoney_unit() {
        return money_unit;
    }

    public void setMoney_unit(String money_unit) {
        this.money_unit = money_unit;
    }

    public String getHavetime() {
        return havetime;
    }

    public void setHavetime(String havetime) {
        this.havetime = havetime;
    }

    public String getExe_time() {
        return exe_time;
    }

    public void setExe_time(String exe_time) {
        if (!TextUtils.isEmpty(exe_time) && !"null".equals(exe_time)) {
            int time = Tools.StringToInt(exe_time);
            if (time < 60) {
                // this.exe_time = time + "分";
                if (time >= 10) {
                    this.exe_time = time + "分钟";
                } else {
                    this.exe_time = time + "分钟";
                }
            } else if (time < 1440 && time >= 60) {
                int hour = time / 60;
                int min = time % 60;
                this.exe_time = hour + "小时" + min + "分钟";
//                if (hour >= 10) {
//                    if (min >= 10) {
//                        this.exe_time = hour + "小时" + min + "分钟";
//                    } else {
//                        this.exe_time = hour + "小时0" + min + "分钟";
//                    }
//                } else {
//                    if (min >= 10) {
//                        this.exe_time = "0" + hour + "小时" + min + "分钟";
//                    } else {
//                        this.exe_time = "0" + hour + "小时0" + min + "分钟";
//                    }
//                }
            } else if (time >= 1440) {
                int day = time / 1440;
                int hour = (time % 1440) / 60;
                int min = time % 60;
                this.exe_time = day + "天" + hour + "小时" + min + "分钟";
            }
        } else {
            this.exe_time = exe_time;
        }
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(String longtitude) {
        this.longtitude = longtitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    //离线项目列表用
    private String is_taskphoto;//是否允许连续拍照

    private String isUpdata;//1为出店，0为未出店,暗访用

    public String getIsUpdata() {
        return isUpdata;
    }

    public void setIsUpdata(String isUpdata) {
        this.isUpdata = isUpdata;
    }

    public String getIs_taskphoto() {
        return is_taskphoto;
    }

    public void setIs_taskphoto(String is_taskphoto) {
        this.is_taskphoto = is_taskphoto;
    }

    public String getOutletnote() {
        return outletnote;
    }

    public void setOutletnote(String outletnote) {
        this.outletnote = outletnote;
    }

    private boolean isAgain = false;

    public boolean isAgain() {
        return isAgain;
    }

    public void setAgain(boolean again) {
        isAgain = again;
    }

    public String getCodeStr() {
        return codeStr;
    }

    public void setCodeStr(String codeStr) {
        this.codeStr = codeStr;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getIs_watermark() {
        return is_watermark;
    }

    public void setIs_watermark(int is_watermark) {
        this.is_watermark = is_watermark;
    }

    private String is_desc;//是否有网点说明，值为1时有网点说明，值为0时没有网点说明

    public String getIs_desc() {
        return is_desc;
    }

    public void setIs_desc(String is_desc) {
        this.is_desc = is_desc;
    }

    public String getIs_exe() {
        return is_exe;
    }

    public void setIs_exe(String is_exe) {
        this.is_exe = is_exe;
    }

    public int getIs_record() {
        return is_record;
    }

    public void setIs_record(int is_record) {
        this.is_record = is_record;
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
            this.photo_compression = photo_compression;
        }
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

    public int getIsEdit() {
        return isEdit;
    }

    public void setIsEdit(int isEdit) {
        this.isEdit = isEdit;
    }

    public int getIsOffline() {
        return isOffline;
    }

    public void setIsOffline(int isOffline) {
        this.isOffline = isOffline;
    }

    public int getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(int isCompleted) {
        this.isCompleted = isCompleted;
    }

    public String getIsstore() {
        return isstore;
    }

    public void setIsstore(String isstore) {
        this.isstore = isstore;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCity2() {
        return city2;
    }

    public void setCity2(String city2) {
        this.city2 = city2;
    }

    public String getCity3() {
        return city3;
    }

    public void setCity3(String city3) {
        this.city3 = city3;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCitydetail() {
        return citydetail;
    }

    public void setCitydetail(String citydetail) {
        this.citydetail = citydetail;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
