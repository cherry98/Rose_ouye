package com.orange.oy.info.mycorps;

import java.util.List;

/**
 * Created by Administrator on 2018/5/10.
 */

public class TeamInformInfo {


    /**
     * notice : 2
     * team_img : /a.jpg
     * team_name : 战狼队
     * team_slogan : 战队口号
     * team_credit : null
     * personal_auth : 1
     * enterprise_auth : 0
     * captain : {"name":"张三","mobile":"13434343434"}
     * deputy : [{"name":"队副1","mobile":"17676767676"},{"name":"队副2","mobile":"18787878787"}]
     * province : 河北省
     * task_num : 128
     * total_money : 13580
     * open_total_amount : 1
     * user_num : 35
     * pass_percent : 97
     * ahead_percent : 23
     * team_speciality : ["特长1","特长2"]
     * user_city : [{"city":"沈阳市","num":2}]
     * invitation : 1
     * user_identity : 1
     */

    private int notice;
    private String team_img;
    private String team_name;
    private String team_slogan;
    private Object team_credit;
    private int personal_auth;
    private int enterprise_auth;
    private CaptainBean captain;
    private String province;
    private int task_num;
    private int total_money;
    private int open_total_amount;
    private int user_num;
    private int pass_percent;
    private int ahead_percent;
    private int invitation;
    private int user_identity;
    private List<DeputyBean> deputy;
    private List<String> team_speciality;
    private List<UserCityBean> user_city;

    public int getNotice() {
        return notice;
    }

    public void setNotice(int notice) {
        this.notice = notice;
    }

    public String getTeam_img() {
        return team_img;
    }

    public void setTeam_img(String team_img) {
        this.team_img = team_img;
    }

    public String getTeam_name() {
        return team_name;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public String getTeam_slogan() {
        return team_slogan;
    }

    public void setTeam_slogan(String team_slogan) {
        this.team_slogan = team_slogan;
    }

    public Object getTeam_credit() {
        return team_credit;
    }

    public void setTeam_credit(Object team_credit) {
        this.team_credit = team_credit;
    }

    public int getPersonal_auth() {
        return personal_auth;
    }

    public void setPersonal_auth(int personal_auth) {
        this.personal_auth = personal_auth;
    }

    public int getEnterprise_auth() {
        return enterprise_auth;
    }

    public void setEnterprise_auth(int enterprise_auth) {
        this.enterprise_auth = enterprise_auth;
    }

    public CaptainBean getCaptain() {
        return captain;
    }

    public void setCaptain(CaptainBean captain) {
        this.captain = captain;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public int getTask_num() {
        return task_num;
    }

    public void setTask_num(int task_num) {
        this.task_num = task_num;
    }

    public int getTotal_money() {
        return total_money;
    }

    public void setTotal_money(int total_money) {
        this.total_money = total_money;
    }

    public int getOpen_total_amount() {
        return open_total_amount;
    }

    public void setOpen_total_amount(int open_total_amount) {
        this.open_total_amount = open_total_amount;
    }

    public int getUser_num() {
        return user_num;
    }

    public void setUser_num(int user_num) {
        this.user_num = user_num;
    }

    public int getPass_percent() {
        return pass_percent;
    }

    public void setPass_percent(int pass_percent) {
        this.pass_percent = pass_percent;
    }

    public int getAhead_percent() {
        return ahead_percent;
    }

    public void setAhead_percent(int ahead_percent) {
        this.ahead_percent = ahead_percent;
    }

    public int getInvitation() {
        return invitation;
    }

    public void setInvitation(int invitation) {
        this.invitation = invitation;
    }

    public int getUser_identity() {
        return user_identity;
    }

    public void setUser_identity(int user_identity) {
        this.user_identity = user_identity;
    }

    public List<DeputyBean> getDeputy() {
        return deputy;
    }

    public void setDeputy(List<DeputyBean> deputy) {
        this.deputy = deputy;
    }

    public List<String> getTeam_speciality() {
        return team_speciality;
    }

    public void setTeam_speciality(List<String> team_speciality) {
        this.team_speciality = team_speciality;
    }

    public List<UserCityBean> getUser_city() {
        return user_city;
    }

    public void setUser_city(List<UserCityBean> user_city) {
        this.user_city = user_city;
    }

    public static class CaptainBean {
        /**
         * name : 张三
         * mobile : 13434343434
         */

        private String name;
        private String mobile;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }
    }

    public static class DeputyBean {
        /**
         * name : 队副1
         * mobile : 17676767676
         */

        private String name;
        private String mobile;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }
    }

    public static class UserCityBean {
        /**
         * city : 沈阳市
         * num : 2
         */

        private String city;
        private int num;

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }
    }

}
