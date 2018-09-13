package com.orange.oy.info;

import android.text.TextUtils;

public class TaskFinishInfo {
    private String pid;
    private String package_name;
    private String storeid;
    private String storenum;
    private String storename;
    private String projectid;
    private String projectname;
    private String taskid;
    private String category1;
    private String category2;
    private String category3;
    private String compression;
    private String name;
    private int is_watermark;
    private String code;
    private String brand;

    private String outlet_batch;
    private String p_batch;

    public String getOutlet_batch() {
        return outlet_batch;
    }

    public void setOutlet_batch(String outlet_batch) {
        this.outlet_batch = outlet_batch;
    }

    public String getP_batch() {
        return p_batch;
    }

    public void setP_batch(String p_batch) {
        this.p_batch = p_batch;
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

    public int getIs_watermark() {
        return is_watermark;
    }

    public void setIs_watermark(int is_watermark) {
        this.is_watermark = is_watermark;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public String getStorenum() {
        return storenum;
    }

    public void setStorenum(String storenum) {
        this.storenum = storenum;
    }

    public String getStorename() {
        return storename;
    }

    public void setStorename(String storename) {
        this.storename = storename;
    }

    public String getProjectid() {
        return projectid;
    }

    public void setProjectid(String projectid) {
        this.projectid = projectid;
    }

    public String getProjectname() {
        return projectname;
    }

    public void setProjectname(String projectname) {
        this.projectname = projectname;
    }

    public String getCompression() {
        return compression;
    }

    public void setCompression(String compression) {
        if ("1".equals(compression)) {
            this.compression = "100";
        } else if ("2".equals(compression)) {
            this.compression = "300";
        } else if ("3".equals(compression)) {
            this.compression = "500";
        } else {
            this.compression = "300";
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPid() {
        if (TextUtils.isEmpty(pid)) {
            pid = "";
        }
        return pid;
    }

    public void setPid(String pid) {
        if (TextUtils.isEmpty(pid) || pid.equals("null")) {
            this.pid = "";
        } else {
            this.pid = pid;
        }
    }

    public String getStoreid() {
        return storeid;
    }

    public void setStoreid(String storeid) {
        this.storeid = storeid;
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getCategory1() {
        if (TextUtils.isEmpty(category1)) {
            category1 = "";
        }
        return category1;
    }

    public void setCategory1(String category1) {
        if (TextUtils.isEmpty(category1) || category1.equals("null")) {
            this.category1 = "";
        } else {
            this.category1 = category1;
        }
    }

    public String getCategory2() {
        if (TextUtils.isEmpty(category2)) {
            category2 = "";
        }
        return category2;
    }

    public void setCategory2(String category2) {
        if (TextUtils.isEmpty(category2) || category2.equals("null")) {
            this.category2 = "";
        } else {
            this.category2 = category2;
        }
    }

    public String getCategory3() {
        if (TextUtils.isEmpty(category3)) {
            category3 = "";
        }
        return category3;
    }

    public void setCategory3(String category3) {
        if (TextUtils.isEmpty(category3) || category3.equals("null")) {
            this.category3 = "";
        } else {
            this.category3 = category3;
        }
    }
}
