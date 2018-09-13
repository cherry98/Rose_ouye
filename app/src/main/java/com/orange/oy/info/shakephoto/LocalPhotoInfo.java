package com.orange.oy.info.shakephoto;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/6/13.
 * 本地相册列表
 */

public class LocalPhotoInfo {

    /**
     * area : 不详细的地址
     * create_time : 创建时间，格式为2018-05-29
     * photo_num : 照片数
     * photo_list : [{"fi_id":"文件ID","aitivity_name":"活动名称","file_type":"原图文件地址","province":"省份","city":"城市",
     * "county":"区域","address":"详细地址","create_time":"创建时间，格式为2018-05-29","longitude":"经度","latitude":"纬度",
     * "key_concent":["关键词1","关键词2"]}]
     */

    private String area;
    private String create_time;
    private String photo_num;
    private String total_photo_num;
    private boolean isShowMap;

    public boolean isShowMap() {
        return isShowMap;
    }

    public void setShowMap(boolean showMap) {
        isShowMap = showMap;
    }

    private ArrayList<PhotoListBean> photo_list;

    public String getTotal_photo_num() {
        return total_photo_num;
    }

    public void setTotal_photo_num(String total_photo_num) {
        this.total_photo_num = total_photo_num;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getPhoto_num() {
        return photo_num;
    }

    public void setPhoto_num(String photo_num) {
        this.photo_num = photo_num;
    }

    public ArrayList<PhotoListBean> getPhoto_list() {
        return photo_list;
    }

    public void setPhoto_list(ArrayList<PhotoListBean> photo_list) {
        this.photo_list = photo_list;
    }

    public static class PhotoListBean {
        /**
         * fi_id : 文件ID
         * aitivity_name : 活动名称
         * file_type : 原图文件地址
         * province : 省份
         * city : 城市
         * county : 区域
         * address : 详细地址
         * create_time : 创建时间，格式为2018-05-29
         * longitude : 经度
         * latitude : 纬度
         * key_concent : ["关键词1","关键词2"]
         */

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
        private boolean isShow;
        private boolean isCheck;

        public boolean isCheck() {
            return isCheck;
        }

        public void setCheck(boolean check) {
            isCheck = check;
        }

        public boolean isShow() {
            return isShow;
        }

        public void setShow(boolean show) {
            isShow = show;
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

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getFile_url() {
            return file_url;
        }

        public void setFile_url(String file_url) {
            this.file_url = file_url;
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
    }
}
