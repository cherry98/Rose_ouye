package com.orange.oy.info.shakephoto;

/**
 * Created by Lenovo on 2018/8/20.
 * 礼品奖励列表 V3.20
 */

public class PrizeListInfo {

    /**
     * user_gift_id : 礼品id
     * project_id : 项目id
     * project_name : 项目名称
     * outlet_id : 网点ID
     * outlet_name : 网点名称
     * gift_url : 礼品图片url
     * gift_name : 礼品名称
     * gift_money : 礼品价值金额
     * merchant : 商家名称
     * expired : 是否过期，0为未过期，1为已过期
     */

    private String user_gift_id;
    private String project_id;
    private String project_name;
    private String outlet_id;
    private String outlet_name;
    private String gift_url;
    private String gift_name;
    private String gift_money;
    private String merchant;
    private String expired;

    public String getUser_gift_id() {
        return user_gift_id;
    }

    public void setUser_gift_id(String user_gift_id) {
        this.user_gift_id = user_gift_id;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
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

    public String getGift_url() {
        return gift_url;
    }

    public void setGift_url(String gift_url) {
        this.gift_url = gift_url;
    }

    public String getGift_name() {
        return gift_name;
    }

    public void setGift_name(String gift_name) {
        this.gift_name = gift_name;
    }

    public String getGift_money() {
        return gift_money;
    }

    public void setGift_money(String gift_money) {
        this.gift_money = gift_money;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getExpired() {
        return expired;
    }

    public void setExpired(String expired) {
        this.expired = expired;
    }
}
