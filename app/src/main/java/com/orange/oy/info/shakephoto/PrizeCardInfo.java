package com.orange.oy.info.shakephoto;

import com.orange.oy.info.MyGiftDetailInfo;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/8/23.
 * 礼品卡券 V3.20
 */

public class PrizeCardInfo {

    /**
     * gift_user_id : 用户礼品关系id
     * gift_name : 礼品名称
     * gift_money : 礼品价值金额
     * img_url : 礼品图片url
     * merchant : 商家
     */

    private String gift_user_id;
    private String gift_name;
    private String gift_money;
    private String img_url;
    private String merchant;
    private ArrayList<MyGiftDetailInfo> expressInfos;
    private boolean isSecond;//true 为跳转第二页（多个物流信息） false只有一个物流信息
    private String delivery_state;

    public boolean isSecond() {
        return isSecond;
    }

    public void setSecond(boolean second) {
        isSecond = second;
    }

    public ArrayList<MyGiftDetailInfo> getExpressInfos() {
        return expressInfos;
    }

    public void setExpressInfos(ArrayList<MyGiftDetailInfo> expressInfos) {
        this.expressInfos = expressInfos;
    }

    /**
     * express_company : 快递公司
     * express_number : 运单编号
     * official_phone : 官方电话
     * delivery_state : 发货状态（0：未发货；1已发货）
     */

    private String consignee_name;
    private String asconsignee_phone;
    private String order_no;


    public String getGift_user_id() {
        return gift_user_id;
    }

    public void setGift_user_id(String gift_user_id) {
        this.gift_user_id = gift_user_id;
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

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getConsignee_name() {
        return consignee_name;
    }

    public void setConsignee_name(String consignee_name) {
        this.consignee_name = consignee_name;
    }

    public String getAsconsignee_phone() {
        return asconsignee_phone;
    }

    public void setAsconsignee_phone(String asconsignee_phone) {
        this.asconsignee_phone = asconsignee_phone;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getDelivery_state() {
        return delivery_state;
    }

    public void setDelivery_state(String delivery_state) {
        this.delivery_state = delivery_state;
    }
}
