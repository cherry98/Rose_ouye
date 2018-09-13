package com.orange.oy.info.shakephoto;

import android.view.View;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/7/30.
 */
public class PhotoListBean implements Serializable {
    /**
     * file_url : 原图文件地址
     * province : 省份
     * city : 城市
     * county : 区域
     * address : 详细地址
     * create_time : 创建时间，格式为2018-05-29
     * longitude : 经度
     * latitude : 纬度
     * key_concent : ["关键词1","关键词2"]
     */

    private String file_url;  //原图文件地址
    private String file_url2;//缩略图路径
    private String province;
    private String city;
    private String county;
    private String address;
    private String longitude;
    private String latitude;
    private boolean isShow;
    private boolean isCheck;
    private String area;//不详细的街道地址，用于照片按地址分类【必填】
    private String create_time;
    private String upUrl; //完整路径
    private boolean isUped; //是否上传
    private View bindView;

    /**
     * oss_name : Oss存储的名称
     * show_name : 显示名
     * file_type : 文件格式
     * dai_id : 用户自定义地址ID，当地址为自己创建的时候需要有
     * show_address : 是否显示地址（0：不显示；1：显示）
     */


    private String oss_name;
    private String show_name;
    private String file_type;
    private String dai_id;
    private String show_address;


    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
    public String getUpUrl() {
        return upUrl;
    }

    public void setUpUrl(String upUrl) {
        this.upUrl = upUrl;
    }

    public boolean isUped() {
        return isUped;
    }

    public void setUped(boolean uped) {
        isUped = uped;
    }

    public View getBindView() {
        return bindView;
    }

    public void setBindView(View bindView) {
        this.bindView = bindView;
    }


    public String getOss_name() {
        return oss_name;
    }

    public void setOss_name(String oss_name) {
        this.oss_name = oss_name;
    }

    public String getShow_name() {
        return show_name;
    }

    public void setShow_name(String show_name) {
        this.show_name = show_name;
    }

    public String getFile_type() {
        return file_type;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    public String getDai_id() {
        return dai_id;
    }

    public void setDai_id(String dai_id) {
        this.dai_id = dai_id;
    }

    public String getShow_address() {
        return show_address;
    }

    public void setShow_address(String show_address) {
        this.show_address = show_address;
    }

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }

    public String getFile_url2() {
        return file_url2;
    }

    public void setFile_url2(String file_url2) {
        this.file_url2 = file_url2;
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

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
