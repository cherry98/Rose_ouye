package com.orange.oy.info;

/**
 * Created by Administrator on 2018/8/17.
 */

public class GiftInfo {

    /**
     * gift_id : 礼品id
     * gift_name : 礼品名称
     * gift_money : 礼品价值金额
     * img_url : 图片地址
     */

    private String gift_id;
    private String gift_name;
    private String gift_money;
    private String img_url;

    public String getGift_id() {
        return gift_id;
    }

    public void setGift_id(String gift_id) {
        this.gift_id = gift_id;
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

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
}
