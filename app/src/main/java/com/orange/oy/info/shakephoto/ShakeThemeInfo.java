package com.orange.oy.info.shakephoto;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/6/14.
 */

public class ShakeThemeInfo implements Serializable {
    private String ai_id;//活动id
    private String cat_id;//分类id
    private String theme_name;//分类名称
    private String activity_name;//主题名称
    private String location_type;//投放类型（1：精准位置；2：模糊位置）
    private String place_name;//场景类型名称
    private String province;//省份
    private String city;//城市
    private String county;//区域
    private String address;//地址
    private String[] key_cencent;//标签
    private String latitude;
    private String longitude;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAi_id() {
        return ai_id;
    }

    public void setAi_id(String ai_id) {
        this.ai_id = ai_id;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getTheme_name() {
        return theme_name;
    }

    public void setTheme_name(String theme_name) {
        this.theme_name = theme_name;
    }

    public String getActivity_name() {
        return activity_name;
    }

    public void setActivity_name(String activity_name) {
        this.activity_name = activity_name;
    }

    public String getLocation_type() {
        return location_type;
    }

    public void setLocation_type(String location_type) {
        this.location_type = location_type;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
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

    public String[] getKey_cencent() {
        return key_cencent;
    }

    public void setKey_cencent(String[] key_cencent) {
        this.key_cencent = key_cencent;
    }
}
