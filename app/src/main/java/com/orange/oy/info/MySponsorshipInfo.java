package com.orange.oy.info;

/**
 * Created by Administrator on 2018/8/22.
 */

public class MySponsorshipInfo {
    /**
     * "ai_id":"活动id",
     * "activity_name":"活动名称",
     * "target_num":"目标人数",
     * "get_num":"收到的照片人数",
     * "begin_date":"开始日期",
     * "end_date":"结束日期",
     * "activity_status":"活动状态2：投放中；3：已结束",
     * "activity_type":"活动类型（1：集图活动；2：偶业项目）",
     * "project_id":"项目id",
     * "template_img":"图标",
     * "sponsorship_money":"赞助总金额",
     * "ad_show_num":"广告浏览数量",
     * "ad_click_num":"广告点击数"
     */
    private String ai_id;
    private String activity_name;
    private String target_num;
    private String get_num;
    private String begin_date;
    private String end_date;
    private String activity_status;
    private String activity_type;
    private String project_id;
    private String template_img;
    private String sponsorship_money;
    private String ad_show_num;
    private String ad_click_num;

    public String getAi_id() {
        return ai_id;
    }

    public void setAi_id(String ai_id) {
        this.ai_id = ai_id;
    }

    public String getActivity_name() {
        return activity_name;
    }

    public void setActivity_name(String activity_name) {
        this.activity_name = activity_name;
    }

    public String getTarget_num() {
        return target_num;
    }

    public void setTarget_num(String target_num) {
        this.target_num = target_num;
    }

    public String getGet_num() {
        return get_num;
    }

    public void setGet_num(String get_num) {
        this.get_num = get_num;
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

    public String getActivity_status() {
        return activity_status;
    }

    public void setActivity_status(String activity_status) {
        this.activity_status = activity_status;
    }

    public String getActivity_type() {
        return activity_type;
    }

    public void setActivity_type(String activity_type) {
        this.activity_type = activity_type;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getTemplate_img() {
        return template_img;
    }

    public void setTemplate_img(String template_img) {
        this.template_img = template_img;
    }

    public String getSponsorship_money() {
        return sponsorship_money;
    }

    public void setSponsorship_money(String sponsorship_money) {
        this.sponsorship_money = sponsorship_money;
    }

    public String getAd_show_num() {
        return ad_show_num;
    }

    public void setAd_show_num(String ad_show_num) {
        this.ad_show_num = ad_show_num;
    }

    public String getAd_click_num() {
        return ad_click_num;
    }

    public void setAd_click_num(String ad_click_num) {
        this.ad_click_num = ad_click_num;
    }
}
