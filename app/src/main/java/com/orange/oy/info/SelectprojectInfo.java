package com.orange.oy.info;

import android.text.TextUtils;

import com.orange.oy.base.Tools;

/**
 * 提现-选择项目
 */
public class SelectprojectInfo {
    private String id;
    private String type;
    private String projectName;
    private int outletNum;
    private String money;
    private String exechangeTime;

    public String getExechangeTime() {
        return exechangeTime;
    }

    public void setExechangeTime(String exechangeTime) {
        this.exechangeTime = exechangeTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getOutletNum() {
        return outletNum;
    }

    public void setOutletNum(int outletNum) {
        this.outletNum = outletNum;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        if (TextUtils.isEmpty(money)) {
            money = "-";
        } else {
            double d = Tools.StringToDouble(money);
            if (d - (int) d > 0) {
                money = String.valueOf(d);
            } else {
                money = String.valueOf((int) d);
            }
        }
        this.money = money;
    }

    @Override
    public String toString() {
        return "SelectprojectInfo{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", projectName='" + projectName + '\'' +
                ", outletNum=" + outletNum +
                ", money='" + money + '\'' +
                '}';
    }
}
