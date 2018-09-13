package com.orange.oy.info;

/**
 * Created by Lenovo on 2018/6/27.
 */

public class LocationListInfo {

    /**
     * dai_id : 位置id
     * province : 省份
     * city : 城市
     * county : 区域
     * address : 地址
     * address_name : 地址名称
     * longitude : 经度
     * latitude : 纬度
     */

    private String dai_id;
    private String province;
    private String city;
    private String county;
    private String address;
    private String address_name;
    private String longitude;
    private String latitude;

    public String getDai_id() {
        return dai_id;
    }

    public void setDai_id(String dai_id) {
        this.dai_id = dai_id;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress_name() {
        return address_name;
    }

    public void setAddress_name(String address_name) {
        this.address_name = address_name;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
