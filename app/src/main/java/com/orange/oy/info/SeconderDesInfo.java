package com.orange.oy.info;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/7/18.
 */

public class SeconderDesInfo {

    /**
     * sponsor_name : 赞助商名称
     * sponsorship_fee : 赞助总金额
     * ad_url : 广告图oss路径
     * ad_links : 广告链接
     * praise_num : 点赞数
     * comment_num : 评论数
     * share_num : 分享数
     * praise_state : 0 是否点赞
     * sai_id : ad7061b106714d0f897bcad35185fce8
     */

    private String sponsor_name;
    private String sponsorship_fee;
    private String ad_url;
    private String ad_links;
    private String praise_num;
    private String comment_num;
    private String share_num;
    private String sai_id;
    private String praise_state;
    private String comment_state;   // 是否评论过（0：未评论，1：已评论

    public String getComment_state() {
        return comment_state;
    }

    public void setComment_state(String comment_state) {
        this.comment_state = comment_state;
    }

    public String getSponsor_name() {
        return sponsor_name;
    }

    public void setSponsor_name(String sponsor_name) {
        this.sponsor_name = sponsor_name;
    }

    public String getSponsorship_fee() {
        return sponsorship_fee;
    }

    public void setSponsorship_fee(String sponsorship_fee) {
        this.sponsorship_fee = sponsorship_fee;
    }

    public String getAd_url() {
        return ad_url;
    }

    public void setAd_url(String ad_url) {
        this.ad_url = ad_url;
    }

    public String getAd_links() {
        return ad_links;
    }

    public void setAd_links(String ad_links) {
        this.ad_links = ad_links;
    }

    public String getPraise_num() {
        return praise_num;
    }

    public void setPraise_num(String praise_num) {
        this.praise_num = praise_num;
    }

    public String getComment_num() {
        return comment_num;
    }

    public void setComment_num(String comment_num) {
        this.comment_num = comment_num;
    }

    public String getShare_num() {
        return share_num;
    }

    public void setShare_num(String share_num) {
        this.share_num = share_num;
    }

    public String getSai_id() {
        return sai_id;
    }

    public void setSai_id(String sai_id) {
        this.sai_id = sai_id;
    }

    public String getPraise_state() {
        return praise_state;
    }

    public void setPraise_state(String praise_state) {
        this.praise_state = praise_state;
    }
}
