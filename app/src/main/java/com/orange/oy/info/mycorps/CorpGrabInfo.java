package com.orange.oy.info.mycorps;

/**
 * Created by Lenovo on 2018/5/25.
 * 众包任务批量申领==战队(任务包)
 */

public class CorpGrabInfo {

    /**
     * package_id : 包id
     * province : 省份
     * city : 城市
     * num : 数量
     * total_money : 总金额
     * is_certification : 是否有进行了认证的战队，1为已认证，0为未认证
     * jump_select_team : 是否要弹出选择战队，1为弹出，0为不弹出,当is_certification值为0时并且有好几个未认证的战队时该值会为1，值为1时弹出需要去认证的战队让用户选择
     * team_id : 战队id，当is_certification值为0，并且jump_select_team值为0时需要用到，引导用户对该战队进行认证
     */

    private String package_id;
    private String province;
    private String city;
    private String num;
    private String total_money;
    private String is_certification;
    private String jump_select_team;
    private String team_id;
    private String type;//是省份的包还是城市的包，1为省份的包，2为城市的包
    private String project_type;//项目类型，1为有网点，5为无网点
    private String certification;

    public String getCertification() {
        return certification;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }

    public String getProject_type() {
        return project_type;
    }

    public void setProject_type(String project_type) {
        this.project_type = project_type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPackage_id() {
        return package_id;
    }

    public void setPackage_id(String package_id) {
        this.package_id = package_id;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getTotal_money() {
        return total_money;
    }

    public void setTotal_money(String total_money) {
        this.total_money = total_money;
    }

    public String getIs_certification() {
        return is_certification;
    }

    public void setIs_certification(String is_certification) {
        this.is_certification = is_certification;
    }

    public String getJump_select_team() {
        return jump_select_team;
    }

    public void setJump_select_team(String jump_select_team) {
        this.jump_select_team = jump_select_team;
    }

    public String getTeam_id() {
        return team_id;
    }

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
    }
}
