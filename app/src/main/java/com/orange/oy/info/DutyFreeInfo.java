package com.orange.oy.info;

/**
 * Created by Lenovo on 2018/3/6.
 * 免税额度info
 */

public class DutyFreeInfo {

    /**
     * id : 1
     * usersMobileId : 1322
     * obtainTime : 2018-03-05 14:44:07.0
     * remark : 获取免税额度800元
     * type : 1   type值为1时为额度增加，0为额度减少
     * money : 800  Money为额度增加或减少的数值，值为-1时为额度增加到无限制
     */

    private String id;
    private String usersMobileId;
    private String obtainTime;
    private String remark;
    private String type;
    private String money;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsersMobileId() {
        return usersMobileId;
    }

    public void setUsersMobileId(String usersMobileId) {
        this.usersMobileId = usersMobileId;
    }

    public String getObtainTime() {
        return obtainTime;
    }

    public void setObtainTime(String obtainTime) {
        this.obtainTime = obtainTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }
}