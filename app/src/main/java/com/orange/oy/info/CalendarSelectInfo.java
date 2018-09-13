package com.orange.oy.info;

public class CalendarSelectInfo {
    private String parentId;
    private String parentCode;
    private String parentName;
    private int flag;//0:父项，1:子项
    private String childId;
    private String childNAme;
    private String childNum;
    private String childDetail;
    private String province;
    private String city;
    private String isstore;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getChildDetail() {
        return childDetail;
    }

    public void setChildDetail(String childDetail) {
        this.childDetail = childDetail;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public String getChildNAme() {
        return childNAme;
    }

    public void setChildNAme(String childNAme) {
        this.childNAme = childNAme;
    }

    public String getChildNum() {
        return childNum;
    }

    public void setChildNum(String childNum) {
        this.childNum = childNum;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getIsstore() {
        return isstore;
    }

    public void setIsstore(String isstore) {
        this.isstore = isstore;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }
}
