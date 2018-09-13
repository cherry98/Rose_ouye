package com.orange.oy.info.shakephoto;

/**
 * Created by Lenovo on 2018/6/12.
 * 我参与的活动列表
 */

public class ShakeAlbumInfo {

    /**
     * ai_id : 活动id
     * photo_url : 照片地址
     * activity_name : 活动名称
     * initiator : 活动发起方
     * left_target : 还需邀请人数
     * left_time : 剩余拆红包天数
     * prize : 活动大奖
     * sponsor_num : 赞助商数量
     * is_join : 是否是参与的活动，1为是，0为否
     * redpack_state : 红包状态，0为不可拆红包，1为可以拆红包，2为已拆红包
     */

    private String ai_id;
    private String photo_url;
    private String activity_name;
    private String initiator;
    private String left_target;
    private String left_time;
    private String prize;
    private String sponsor_num;
    private String is_join;
    private String redpack_state;
    private String sponsor_money;
    private String activity_status;

    public String getSponsor_money() {
        return sponsor_money;
    }

    public void setSponsor_money(String sponsor_money) {
        this.sponsor_money = sponsor_money;
    }

    public String getActivity_status() {
        return activity_status;
    }

    public void setActivity_status(String activity_status) {
        this.activity_status = activity_status;
    }

    public String getAi_id() {
        return ai_id;
    }

    public void setAi_id(String ai_id) {
        this.ai_id = ai_id;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getActivity_name() {
        return activity_name;
    }

    public void setActivity_name(String activity_name) {
        this.activity_name = activity_name;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public String getLeft_target() {
        return left_target;
    }

    public void setLeft_target(String left_target) {
        this.left_target = left_target;
    }

    public String getLeft_time() {
        return left_time;
    }

    public void setLeft_time(String left_time) {
        this.left_time = left_time;
    }

    public String getPrize() {
        return prize;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }

    public String getSponsor_num() {
        return sponsor_num;
    }

    public void setSponsor_num(String sponsor_num) {
        this.sponsor_num = sponsor_num;
    }

    public String getIs_join() {
        return is_join;
    }

    public void setIs_join(String is_join) {
        this.is_join = is_join;
    }

    public String getRedpack_state() {
        return redpack_state;
    }

    public void setRedpack_state(String redpack_state) {
        this.redpack_state = redpack_state;
    }
}
