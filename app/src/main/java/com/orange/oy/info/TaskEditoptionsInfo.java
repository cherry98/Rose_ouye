package com.orange.oy.info;

import com.orange.oy.network.Urls;

/**
 * 任务选项
 */
public class TaskEditoptionsInfo {
    private String id;
    private String option_name;//选项名字
    private int option_num;//选项序号
    private String isfill;//是否可填
    private String isforcedfill;//是否必填
    private String mutex_id;//互斥选项
    private String jump;//是否强制跳转，0为否，1为是
    private String jumpquestion;//跳转的题的题号
    private String photo_url;

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public int getOption_num() {
        return option_num;
    }

    public void setOption_num(int option_num) {
        this.option_num = option_num;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsfill() {
        return isfill;
    }

    public void setIsfill(String isfill) {
        this.isfill = isfill;
    }

    public String getIsforcedfill() {
        return isforcedfill;
    }

    public void setIsforcedfill(String isforcedfill) {
        this.isforcedfill = isforcedfill;
    }

    public String getJump() {
        return jump;
    }

    public void setJump(String jump) {
        this.jump = jump;
    }

    public String getJumpquestion() {
        return jumpquestion;
    }

    public void setJumpquestion(String jumpquestion) {
        this.jumpquestion = jumpquestion;
    }

    public String getMutex_id() {
        return mutex_id;
    }

    public void setMutex_id(String mutex_id) {
        this.mutex_id = mutex_id;
    }

    public String getOption_name() {
        return option_name;
    }

    public void setOption_name(String option_name) {
        this.option_name = option_name;
    }

}
