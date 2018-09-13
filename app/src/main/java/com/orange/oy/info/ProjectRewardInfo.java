package com.orange.oy.info;

/**
 * Created by Administrator on 2018/8/21.
 */

public class ProjectRewardInfo {

    /**
     * reward_type : 奖励类型，1为现金，2为礼品
     * money : 奖励金额
     * gift_url : 礼品图片url
     * merchant : 商家
     * gift_name : 礼品名称
     */
    private String reward_type;
    private String money;
    private String gift_url;
    private String merchant;
    private String gift_name;

    public String getReward_type() {
        return reward_type;
    }

    public void setReward_type(String reward_type) {
        this.reward_type = reward_type;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getGift_url() {
        return gift_url;
    }

    public void setGift_url(String gift_url) {
        this.gift_url = gift_url;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getGift_name() {
        return gift_name;
    }

    public void setGift_name(String gift_name) {
        this.gift_name = gift_name;
    }
}
