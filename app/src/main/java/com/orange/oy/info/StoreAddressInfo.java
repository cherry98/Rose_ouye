package com.orange.oy.info;

/**
 * Created by Administrator on 2018/8/21.
 */

public class StoreAddressInfo {

    /**
     * outlet_id : 店铺id
     * outlet_name : 店铺名称
     * outlet_num : 店铺编号
     * province : 省份
     * city : 城市
     * town : 区县
     * outlet_address : 店铺详细地址
     */

    private String outlet_id;
    private String outlet_name;
    private String outlet_num;
    private String province;
    private String city;
    private String town;
    private String outlet_address;

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

    public String getOutlet_num() {
        return outlet_num;
    }

    public void setOutlet_num(String outlet_num) {
        this.outlet_num = outlet_num;
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

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getOutlet_address() {
        return outlet_address;
    }

    public void setOutlet_address(String outlet_address) {
        this.outlet_address = outlet_address;
    }
}
