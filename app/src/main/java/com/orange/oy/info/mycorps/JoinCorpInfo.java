package com.orange.oy.info.mycorps;

import org.json.JSONArray;

/**
 * Created by Lenovo on 2018/5/14.
 */

public class JoinCorpInfo {

    /**
     * team_id : 1
     * apply_id : 1
     * team_name : 战狼队
     * team_code : 123456
     * team_img : /a.jpg
     * team_credit : null
     * captain : 张三
     * mobile : 17898980987
     * task_num : 145
     * user_num : 12
     * province : 河北省
     * speciality : ["特长1","特长2"]
     * state : 0
     * refuse_num : 2
     * reply : [{"sender":"16765676565","receiver":"12343234343","text":"这个群不能加了","type":1},{"sender":"12343234343","receiver":"16765676565","text":"这个群不能加了","type":0}]
     */

    private String team_id;
    private String apply_id;
    private String team_name;
    private String team_code;
    private String team_img;
    private Object team_credit;
    private String captain;
    private String mobile;
    private String task_num;
    private String user_num;
    private String province;
    private String state;
    private String refuse_num;
    private JSONArray speciality;
    private JSONArray chatInfo;
    private String auth_status;//认证状态

    public String getAuth_status() {
        return auth_status;
    }

    public void setAuth_status(String auth_status) {
        this.auth_status = auth_status;
    }

    public JSONArray getChatInfo() {
        return chatInfo;
    }

    public void setChatInfo(JSONArray chatInfo) {
        this.chatInfo = chatInfo;
    }

    public JSONArray getSpeciality() {
        return speciality;
    }

    public void setSpeciality(JSONArray speciality) {
        this.speciality = speciality;
    }

    public String getTeam_id() {
        return team_id;
    }

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
    }

    public String getApply_id() {
        return apply_id;
    }

    public void setApply_id(String apply_id) {
        this.apply_id = apply_id;
    }

    public String getTeam_name() {
        return team_name;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public String getTeam_code() {
        return team_code;
    }

    public void setTeam_code(String team_code) {
        this.team_code = team_code;
    }

    public String getTeam_img() {
        return team_img;
    }

    public void setTeam_img(String team_img) {
        this.team_img = team_img;
    }

    public Object getTeam_credit() {
        return team_credit;
    }

    public void setTeam_credit(Object team_credit) {
        this.team_credit = team_credit;
    }

    public String getCaptain() {
        return captain;
    }

    public void setCaptain(String captain) {
        this.captain = captain;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTask_num() {
        return task_num;
    }

    public void setTask_num(String task_num) {
        this.task_num = task_num;
    }

    public String getUser_num() {
        return user_num;
    }

    public void setUser_num(String user_num) {
        this.user_num = user_num;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getRefuse_num() {
        return refuse_num;
    }

    public void setRefuse_num(String refuse_num) {
        this.refuse_num = refuse_num;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
