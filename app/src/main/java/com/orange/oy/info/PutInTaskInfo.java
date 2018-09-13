package com.orange.oy.info;

/**
 * Created by Administrator on 2018/6/8.
 */

public class PutInTaskInfo {

    /**
     * ai_id : 活动id
     * activity_name : 活动名称
     * target_num : 目标照片数
     * get_num : 收到的照片数
     * begin_date : 开始日期
     * end_date : 结束日期
     * activity_status : 活动状态1：草稿箱未发布；2：投放中；3：已结束
     */

    private String ai_id;
    private String activity_name;
    private String target_num;
    private String get_num;
    private String begin_date;
    private String end_date;
    private String activity_status;
    /**
     * activity_type : 活动类型（1：集图活动；2：偶业项目）
     * project_id : 项目id
     * template_img : 图标
     * project_total_money : 任务总金额
     * money : 执行单价
     * total_num : 执行总量
     * gettask_num : 已领数量
     * done_num : 已做数量
     * check_num : 待审核数量
     * complete_num : 已完成数量
     * pass_num : 已通过数量
     * unpass_num : 未通过数量
     * reward_money : 发放奖励金额
     */

    private String activity_type;
    private String project_id;
    private String template_img;
    private String project_total_money;
    private String money;
    private String total_num;
    private String gettask_num;
    private String done_num;
    private String check_num;
    private String complete_num;
    private String pass_num;
    private String unpass_num;
    private String reward_money;
    /**
     * "ad_show_num":"广告浏览数量",
     * "ad_click_num":"广告点击数",
     * "sponsor_num":"赞助商数量"
     */
    private String ad_show_num;
    private String ad_click_num;
    private String sponsor_num;

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

    public String getSponsor_num() {
        return sponsor_num;
    }

    public void setSponsor_num(String sponsor_num) {
        this.sponsor_num = sponsor_num;
    }

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

    public String getProject_total_money() {
        return project_total_money;
    }

    public void setProject_total_money(String project_total_money) {
        this.project_total_money = project_total_money;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getTotal_num() {
        return total_num;
    }

    public void setTotal_num(String total_num) {
        this.total_num = total_num;
    }

    public String getGettask_num() {
        return gettask_num;
    }

    public void setGettask_num(String gettask_num) {
        this.gettask_num = gettask_num;
    }

    public String getDone_num() {
        return done_num;
    }

    public void setDone_num(String done_num) {
        this.done_num = done_num;
    }

    public String getCheck_num() {
        return check_num;
    }

    public void setCheck_num(String check_num) {
        this.check_num = check_num;
    }

    public String getComplete_num() {
        return complete_num;
    }

    public void setComplete_num(String complete_num) {
        this.complete_num = complete_num;
    }

    public String getPass_num() {
        return pass_num;
    }

    public void setPass_num(String pass_num) {
        this.pass_num = pass_num;
    }

    public String getUnpass_num() {
        return unpass_num;
    }

    public void setUnpass_num(String unpass_num) {
        this.unpass_num = unpass_num;
    }

    public String getReward_money() {
        return reward_money;
    }

    public void setReward_money(String reward_money) {
        this.reward_money = reward_money;
    }
}
