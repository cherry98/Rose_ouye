package com.orange.oy.info;

import java.io.Serializable;

/**
 * Created by xiedongyan on 2017/2/17.
 */

public class ScanTaskInfo implements Serializable {

    private static final long serialVersionUID = -2334773607489980794L;
    /**
     * barcode : 2321234
     * name : 测试产品
     * size : 1ml
     * picurl : /file/task/37492AD0C9397AEB486D91AD86EE908F.jpg
     */

    private String barcode;
    private String name;
    private String size;
    private String picurl;
    private String state;//标记商品状态 1扫描成功 2 扫描失败可置无效 3扫描失败不可置无效
    private String taskScanId;//id,如果置无效回调需要用到

    public String getTaskScanId() {
        return taskScanId;
    }

    public void setTaskScanId(String taskScanId) {
        this.taskScanId = taskScanId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPicurl() {
        return picurl;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }

    @Override
    public String toString() {
        return "ScanTaskInfo{" +
                "barcode='" + barcode + '\'' +
                ", name='" + name + '\'' +
                ", size='" + size + '\'' +
                ", picurl='" + picurl + '\'' +
                '}';
    }
}
