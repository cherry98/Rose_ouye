package com.orange.oy.info;

import java.util.ArrayList;

public class MyupdataInfo {
    private String storeid;
    private String storename;
    private String projectid;
    private String projectname;
    private String code;

    private ArrayList<MyupdataPackage> MyupdataPackageList;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProjectname() {
        return projectname;
    }

    public void setProjectname(String projectname) {
        this.projectname = projectname;
    }

    public String getProjectid() {
        return projectid;
    }

    public void setProjectid(String projectid) {
        this.projectid = projectid;
    }

    public String getStoreid() {
        return storeid;
    }

    public void setStoreid(String storeid) {
        this.storeid = storeid;
    }

    public String getStorename() {
        return storename;
    }

    public void setStorename(String storename) {
        this.storename = storename;
    }

    public ArrayList<MyupdataPackage> getMyupdataPackageList() {
        if (MyupdataPackageList == null) {
            MyupdataPackageList = new ArrayList<>();
        }
        return MyupdataPackageList;
    }

    public void setMyupdataPackageList(ArrayList<MyupdataPackage> myupdataPackageList) {
        MyupdataPackageList = myupdataPackageList;
    }

    @Override
    public String toString() {
        return "MyupdataInfo{" +
                "storeid='" + storeid + '\'' +
                ", storename='" + storename + '\'' +
                ", projectid='" + projectid + '\'' +
                ", projectname='" + projectname + '\'' +
                ", code='" + code + '\'' +
                ", MyupdataPackageList=" + MyupdataPackageList +
                '}';
    }
}
