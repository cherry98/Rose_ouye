package com.orange.oy.info.mycorps;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/5/28.
 */

public class CorpsTaskInfo implements Serializable {
    private String project_id;
    private String package_id;
    private String team_id;
    private String package_team_id;
    private String project_name;
    private String company_credit;//商家信誉
    private String type;//类型，1为众包显示金额，2为分包不显示金额
    private String identity;//对于该项目的身份，1 队长 2队副 3普通成员
    private String total_money;//总金额
    private String total_outlet;//总网点数
    private String distribution_outlet;//待分配网点数
    private String confirm_outlet;//确认中（带领取）网点数
    private String wait_exe_outlet;//待执行网点数
    private String execution_outlet;//执行中网点数
    private String begin_date;//项目开始时间
    private String end_date;//项目结束时间
    private String company_abbreviation;//商家简称
    private String captain_name;//队长名称
    private String team_name;//战队名称

    private String user_get_outlet;//已领取网点数
    private String user_upload_outlet;//已提交网点数
    private String pass_outlet;//已通过网点数
    private String unpass_outlet;//未通过网点数
    private String check_outlet;//审核中网点数
    private String project_type;//项目类型，1为有网点，5为无网点"

    public String getProject_type() {
        return project_type;
    }

    public void setProject_type(String project_type) {
        this.project_type = project_type;
    }

    public String getPass_outlet() {
        return pass_outlet;
    }

    public void setPass_outlet(String pass_outlet) {
        this.pass_outlet = pass_outlet;
    }

    public String getUnpass_outlet() {
        return unpass_outlet;
    }

    public void setUnpass_outlet(String unpass_outlet) {
        this.unpass_outlet = unpass_outlet;
    }

    public String getCheck_outlet() {
        return check_outlet;
    }

    public void setCheck_outlet(String check_outlet) {
        this.check_outlet = check_outlet;
    }

    public String getUser_get_outlet() {
        return user_get_outlet;
    }

    public void setUser_get_outlet(String user_get_outlet) {
        this.user_get_outlet = user_get_outlet;
    }

    public String getUser_upload_outlet() {
        return user_upload_outlet;
    }

    public void setUser_upload_outlet(String user_upload_outlet) {
        this.user_upload_outlet = user_upload_outlet;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getPackage_id() {
        return package_id;
    }

    public void setPackage_id(String package_id) {
        this.package_id = package_id;
    }

    public String getTeam_id() {
        return team_id;
    }

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
    }

    public String getPackage_team_id() {
        return package_team_id;
    }

    public void setPackage_team_id(String package_team_id) {
        this.package_team_id = package_team_id;
    }

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public String getCompany_credit() {
        return company_credit;
    }

    public void setCompany_credit(String company_credit) {
        this.company_credit = company_credit;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getTotal_money() {
        return total_money;
    }

    public void setTotal_money(String total_money) {
        this.total_money = total_money;
    }

    public String getTotal_outlet() {
        return total_outlet;
    }

    public void setTotal_outlet(String total_outlet) {
        this.total_outlet = total_outlet;
    }

    public String getDistribution_outlet() {
        return distribution_outlet;
    }

    public void setDistribution_outlet(String distribution_outlet) {
        this.distribution_outlet = distribution_outlet;
    }

    public String getConfirm_outlet() {
        return confirm_outlet;
    }

    public void setConfirm_outlet(String confirm_outlet) {
        this.confirm_outlet = confirm_outlet;
    }

    public String getWait_exe_outlet() {
        return wait_exe_outlet;
    }

    public void setWait_exe_outlet(String wait_exe_outlet) {
        this.wait_exe_outlet = wait_exe_outlet;
    }

    public String getExecution_outlet() {
        return execution_outlet;
    }

    public void setExecution_outlet(String execution_outlet) {
        this.execution_outlet = execution_outlet;
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

    public String getCompany_abbreviation() {
        return company_abbreviation;
    }

    public void setCompany_abbreviation(String company_abbreviation) {
        this.company_abbreviation = company_abbreviation;
    }

    public String getCaptain_name() {
        return captain_name;
    }

    public void setCaptain_name(String captain_name) {
        this.captain_name = captain_name;
    }

    public String getTeam_name() {
        return team_name;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }
}
