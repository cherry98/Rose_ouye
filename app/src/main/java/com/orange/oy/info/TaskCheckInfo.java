package com.orange.oy.info;

/**
 * Created by Administrator on 2018/7/2.
 */

public class TaskCheckInfo {

    /**
     * outlet_id : 网点id
     * outlet_name : 网点名称
     * user_name : 用户昵称
     * user_mobile : 用户账号
     * complete_time : 完成时间
     * pass_state : 通过状态，1为通过，0为不通过
     * money : 网点金额
     * address : 位置地址
     */

    private String outlet_id;
    private String outlet_name;
    private String user_name;
    private String user_mobile;
    private String complete_time;
    private String pass_state;
    private String money;
    private String address;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_mobile() {
        return user_mobile;
    }

    public void setUser_mobile(String user_mobile) {
        this.user_mobile = user_mobile;
    }

    public String getComplete_time() {
        return complete_time;
    }

    public void setComplete_time(String complete_time) {
        this.complete_time = complete_time;
    }

    public String getPass_state() {
        return pass_state;
    }

    public void setPass_state(String pass_state) {
        this.pass_state = pass_state;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
