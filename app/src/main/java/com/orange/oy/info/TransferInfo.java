package com.orange.oy.info;


import java.io.Serializable;

/**
 * Created by xiedongyan on 2017/2/9.
 */

public class TransferInfo implements Serializable {
    private static final long serialVersionUID = -6190456288286524003L;
    private String store_num;
    private String city3;
    private String outletid;
    private String project_id;
    private String projectname;
    private String code;
    private String brand;
    private String store_name;
    private String mytype;//上面是考试任务下面普通任务（有重合）
    private String id;
    private String province;
    private String city;
    private String longtitude;
    private String latitude;
    private String photo_compression;
    private int is_watermark;
    private String is_takephoto;
    private String type;//任务类型
    private String is_exe;
    private String is_desc;
    private String number;
    private int isOffline;
    private int is_record;

    public int getIs_record() {
        return is_record;
    }

    public void setIs_record(int is_record) {
        this.is_record = is_record;
    }

    public int getIsOffline() {
        return isOffline;
    }

    public void setIsOffline(int isOffline) {
        this.isOffline = isOffline;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(String longtitude) {
        this.longtitude = longtitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getPhoto_compression() {
        return photo_compression;
    }

    public void setPhoto_compression(String photo_compression) {
        this.photo_compression = photo_compression;
    }

    public int getIs_watermark() {
        return is_watermark;
    }

    public void setIs_watermark(int is_watermark) {
        this.is_watermark = is_watermark;
    }

    public String getIs_takephoto() {
        return is_takephoto;
    }

    public void setIs_takephoto(String is_takephoto) {
        this.is_takephoto = is_takephoto;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIs_exe() {
        return is_exe;
    }

    public void setIs_exe(String is_exe) {
        this.is_exe = is_exe;
    }

    public String getIs_desc() {
        return is_desc;
    }

    public void setIs_desc(String is_desc) {
        this.is_desc = is_desc;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCity3() {
        return city3;
    }

    public void setCity3(String city3) {
        this.city3 = city3;
    }

    public String getMytype() {
        return mytype;
    }

    public void setMytype(String mytype) {
        this.mytype = mytype;
    }

    public String getStore_num() {
        return store_num;
    }

    public void setStore_num(String store_num) {
        this.store_num = store_num;
    }


    public String getOutletid() {
        return outletid;
    }

    public void setOutletid(String outletid) {
        this.outletid = outletid;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getProjectname() {
        return projectname;
    }

    public void setProjectname(String projectname) {
        this.projectname = projectname;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    @Override
    public String toString() {
        return "TransferInfo{" +
                "store_num='" + store_num + '\'' +
                ", city3='" + city3 + '\'' +
                ", outletid='" + outletid + '\'' +
                ", project_id='" + project_id + '\'' +
                ", projectname='" + projectname + '\'' +
                ", code='" + code + '\'' +
                ", brand='" + brand + '\'' +
                ", store_name='" + store_name + '\'' +
                ", mytype='" + mytype + '\'' +
                ", id='" + id + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", longtitude='" + longtitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", photo_compression='" + photo_compression + '\'' +
                ", is_watermark=" + is_watermark +
                ", is_takephoto='" + is_takephoto + '\'' +
                ", type='" + type + '\'' +
                ", is_exe='" + is_exe + '\'' +
                ", is_desc='" + is_desc + '\'' +
                ", number='" + number + '\'' +
                ", isOffline=" + isOffline +
                ", is_record=" + is_record +
                '}';
    }
}
