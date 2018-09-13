package com.orange.oy.info;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;
import com.orange.oy.clusterutil.clustering.ClusterItem;

import java.util.ArrayList;

/**
 * Created by xiedongyan on 2017/6/30.
 */

public class LocationInfo implements ClusterItem {

    /**
     * projectid : 494
     * projectName : 329理想测试项目
     * begin_date : 2017-03-29
     * end_date : 2017-07-29
     * check_time : 6
     * money_unit : 元
     * storeid : 128677
     * storeName : 6
     * storeNum : 6
     * province : 北京市
     * city : 北京市
     * address : 北京市东城区体育馆路街道
     * longtitude : 116.4175910784
     * latitude : 39.8630499581
     * dist : 6296
     */

    private String projectid;
    private String projectName;
    private String begin_date;
    private String end_date;
    private String check_time;
    private String money_unit;
    private String storeid;
    private String storeName;
    private String storeNum;
    private String province;
    private String city;
    private String address;
    private double longtitude;//经度
    private double latitude;//纬度
    private double dist;
    private String standard_state;
    private String outletMoney;
    private String project_person;
    private String photoUrl;
    private String is_watermark;
    private String code;
    private String brand;
    private String is_takephoto;
    private String photo_compression;

    private ArrayList<LocationInfo> locationInfos;//多坐标数据集
    private BitmapDescriptor bitmapDescriptor;
    private boolean havChild;//是否有子项

    private TaskDetailLeftInfo taskDetailLeftInfo;

    public TaskDetailLeftInfo getTaskDetailLeftInfo() {
        return taskDetailLeftInfo;
    }

    public void setTaskDetailLeftInfo(TaskDetailLeftInfo taskDetailLeftInfo) {
        this.taskDetailLeftInfo = taskDetailLeftInfo;
    }

    public ArrayList<LocationInfo> getLocationInfos() {
        return locationInfos;
    }

    public void setLocationInfos(ArrayList<LocationInfo> locationInfos) {
        this.locationInfos = locationInfos;
    }

    public boolean isHavChild() {
        return havChild;
    }

    public void setHavChild(boolean havChild) {
        this.havChild = havChild;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(latitude, longtitude);
    }

    public BitmapDescriptor getBitmapDescriptor() {
        return bitmapDescriptor;
    }

    public void setBitmapDescriptor(BitmapDescriptor bitmapDescriptor) {
        this.bitmapDescriptor = bitmapDescriptor;
    }

    public String getIs_watermark() {
        return is_watermark;
    }

    public void setIs_watermark(String is_watermark) {
        this.is_watermark = is_watermark;
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

    public String getIs_takephoto() {
        return is_takephoto;
    }

    public void setIs_takephoto(String is_takephoto) {
        this.is_takephoto = is_takephoto;
    }

    public String getPhoto_compression() {
        return photo_compression;
    }

    public void setPhoto_compression(String photo_compression) {
        if (photo_compression.equals("1")) {
            this.photo_compression = "300";
        } else if (photo_compression.equals("2")) {
            this.photo_compression = "500";
        } else if (photo_compression.equals("3")) {
            this.photo_compression = "1024";
        } else if (photo_compression.equals("4")) {
            this.photo_compression = "-1";
        } else {
            this.photo_compression = photo_compression;
        }
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getProject_person() {
        return project_person;
    }

    public void setProject_person(String project_person) {
        this.project_person = project_person;
    }

    public String getOutletMoney() {
        return outletMoney;
    }

    public void setOutletMoney(String outletMoney) {
        this.outletMoney = outletMoney;
    }

    public String getStandard_state() {
        return standard_state;
    }

    public void setStandard_state(String standard_state) {
        this.standard_state = standard_state;
    }

    public String getProjectid() {
        return projectid;
    }

    public void setProjectid(String projectid) {
        this.projectid = projectid;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getBegin_date() {
        return begin_date;
    }

    public void setBegin_date(String begin_date) {
        this.begin_date = begin_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getCheck_time() {
        return check_time;
    }

    public void setCheck_time(String check_time) {
        this.check_time = check_time;
    }

    public String getMoney_unit() {
        return money_unit;
    }

    public void setMoney_unit(String money_unit) {
        this.money_unit = money_unit;
    }

    public String getStoreid() {
        return storeid;
    }

    public void setStoreid(String storeid) {
        this.storeid = storeid;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreNum() {
        return storeNum;
    }

    public void setStoreNum(String storeNum) {
        this.storeNum = storeNum;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getDist() {
        return dist;
    }

    public void setDist(double dist) {
        this.dist = dist;
    }
}
