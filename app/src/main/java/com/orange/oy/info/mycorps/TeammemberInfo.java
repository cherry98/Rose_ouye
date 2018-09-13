package com.orange.oy.info.mycorps;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/5/14.
 */

public class TeammemberInfo implements Serializable {
    private String userId;//id
    private String ico;//头像
    private String name;//昵称
    private String phone;//联系电话
    private String identity;//身份 1 为队长 2为队副 3为普通成员
    private String level;//等级
    private String address;//区域
    private String province;
    private String city;
    private String completemissionNumber;//完成任务数量
    private String completePercentage;//完成率
    private String completeaheadPercentage;//提前完成率
    private String isdel;//是否可踢出，1为是，0为否
    //v3.15
    private String sex;//-1未填；0男；1女
    private String age;
    private String address2;//常去地址
    private String freetime;//空闲时间
    private String remark;//队长的备注
    private String[] specialtys = new String[5];//特长-最多五个
    private String state;//是否可分配 1为可分配，0为不可分配
    private String agephone;//年龄+电话
    private String user_mobile;//用户帐号

    public String getUser_mobile() {
        return user_mobile;
    }

    public void setUser_mobile(String user_mobile) {
        this.user_mobile = user_mobile;
    }

    public String getAgephone() {
        return agephone;
    }

    public void setAgephone(String agephone) {
        this.agephone = agephone;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getFreetime() {
        return freetime;
    }

    public void setFreetime(String freetime) {
        this.freetime = freetime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String[] getSpecialtys() {
        return specialtys;
    }

    public void setSpecialtys(String[] specialtys) {
        this.specialtys = specialtys;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIsdel() {
        return isdel;
    }

    public void setIsdel(String isdel) {
        this.isdel = isdel;
    }

    public String getIco() {
        return ico;
    }

    public void setIco(String ico) {
        this.ico = ico;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

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

    public String getCompletemissionNumber() {
        return completemissionNumber;
    }

    public void setCompletemissionNumber(String completemissionNumber) {
        this.completemissionNumber = completemissionNumber;
    }

    public String getCompletePercentage() {
        return completePercentage;
    }

    public void setCompletePercentage(String completePercentage) {
        this.completePercentage = completePercentage;
    }

    public String getCompleteaheadPercentage() {
        return completeaheadPercentage;
    }

    public void setCompleteaheadPercentage(String completeaheadPercentage) {
        this.completeaheadPercentage = completeaheadPercentage;
    }
}
