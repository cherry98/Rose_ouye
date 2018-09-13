package com.orange.oy.info;

/**
 * Created by Administrator on 2018/6/11.
 */

public class DetailsInfo {

    /**
     * fi_id : 文件ID
     * aitivity_name : 活动名称
     * file_type : 原图文件地址
     * province : 省份
     * city : 城市
     * county : 区域
     * address : 地址
     * create_time : 创建时间，格式为2018-05-29
     * longitude : 经度
     * latitude : 纬度
     * key_concent : ["关键词1","关键词2"]
     * show_address":"是否显示地址（0：不显示；1：显示）"
     */
    private String get_num; //共收到照片数
    private String fi_id;
    private String aitivity_name;
    private String file_url;
    private String province;
    private String city;
    private String county;
    private String address;
    private String create_time;
    private String longitude;
    private String latitude;
    private String key_concent;
    private String show_address;
    private String user_name;

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getShow_address() {
        return show_address;
    }

    public void setShow_address(String show_address) {
        this.show_address = show_address;
    }

    public String getFi_id() {
        return fi_id;
    }

    public void setFi_id(String fi_id) {
        this.fi_id = fi_id;
    }

    public String getAitivity_name() {
        return aitivity_name;
    }

    public void setAitivity_name(String aitivity_name) {
        this.aitivity_name = aitivity_name;
    }

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_type) {
        this.file_url = file_type;
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

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
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

    public String getKey_concent() {
        return key_concent;
    }

    public void setKey_concent(String key_concent) {
        key_concent = key_concent.replaceAll("\\\"", "").replaceAll("\\[", "").replaceAll("\\]", "");
        this.key_concent = key_concent;
    }

    public String getGet_num() {
        return get_num;
    }

    public void setGet_num(String get_num) {
        this.get_num = get_num;
    }
}
