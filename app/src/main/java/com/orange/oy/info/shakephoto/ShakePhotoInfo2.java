package com.orange.oy.info.shakephoto;

import android.view.View;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/7/12.
 * 本地图片保存到数据库所存信息
 */

public class ShakePhotoInfo2 implements Serializable {
    private String file_url;//原图路径
    private String file_url2;//缩略图路径
    private String province;//省份【必传】
    private String city;//城市【必传】
    private String county;//区/县【必传】
    private String address;//详细地址【必传】
    private String longitude;//经度【必传】
    private String latitude;//纬度【必传】
    private String area;//不详细的街道地址，用于照片按地址分类【必填】
    private String time;
    public ArrayList<PhotoListBean> list;

    public ArrayList<PhotoListBean> getList() {
        return list;
    }

    public void setList(ArrayList<PhotoListBean> list) {
        this.list = list;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    @Override
    public String toString() {
        return "ShakePhotoInfo2{" +
                "file_url='" + file_url + '\'' +
                ", file_url2='" + file_url2 + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", county='" + county + '\'' +
                ", address='" + address + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", area='" + area + '\'' +
                '}';
    }
}
