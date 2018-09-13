package com.orange.oy.info;

/**
 * Created by xiedongyan on 2017/11/29.
 */

public class MyRecommendInfo {

    private String usermobile;//注册账号
    private String time;//注册时间
    private String isreward;//是否奖励，1为有，0为没有
    private String omnum;//奖励的偶米数

    public String getUsermobile() {
        return usermobile;
    }

    public void setUsermobile(String usermobile) {
        this.usermobile = usermobile;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIsreward() {
        return isreward;
    }

    public void setIsreward(String isreward) {
        this.isreward = isreward;
    }

    public String getOmnum() {
        return omnum;
    }

    public void setOmnum(String omnum) {
        this.omnum = omnum;
    }
}
