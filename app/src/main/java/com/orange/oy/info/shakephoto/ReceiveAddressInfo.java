package com.orange.oy.info.shakephoto;

/**
 * Created by Lenovo on 2018/8/21.
 * 选择收货地址 V3.20
 */

public class ReceiveAddressInfo {

    /**
     * address_id : 地址id
     * consignee_name : 收货人姓名
     * consignee_phone : 收货人电话
     * province : 省份
     * city : 城市
     * county : 区县
     * consignee_address : 详细地址
     * default_state : 默认地址（0：非默认；1：默认地址）
     */

    private String address_id;
    private String consignee_name;
    private String consignee_phone;
    private String province;
    private String city;
    private String county;
    private String consignee_address;
    private String default_state;

    public String getAddress_id() {
        return address_id;
    }

    public void setAddress_id(String address_id) {
        this.address_id = address_id;
    }

    public String getConsignee_name() {
        return consignee_name;
    }

    public void setConsignee_name(String consignee_name) {
        this.consignee_name = consignee_name;
    }

    public String getConsignee_phone() {
        return consignee_phone;
    }

    public void setConsignee_phone(String consignee_phone) {
        this.consignee_phone = consignee_phone;
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

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getConsignee_address() {
        return consignee_address;
    }

    public void setConsignee_address(String consignee_address) {
        this.consignee_address = consignee_address;
    }

    public String getDefault_state() {
        return default_state;
    }

    public void setDefault_state(String default_state) {
        this.default_state = default_state;
    }
}
