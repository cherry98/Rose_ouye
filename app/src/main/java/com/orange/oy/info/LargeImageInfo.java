package com.orange.oy.info;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/7/26.
 */

public class LargeImageInfo implements Serializable {
    private String ai_id;   //活动id
    private String acname;//活动name
    private String fi_id;    //文件ID
    private String file_url;  // 原图文件地址
    private String comment_num; //  评论数
    private String praise_num; //  点赞数
    private String share_num; //  分享数
    private String user_img;  //  用户头像
    private String user_mobile; //  用户账号
    private String praise_state; // 是否点赞过（0：未点赞，1：已点赞）
    private String is_advertisement;  //是否是广告图，1为是，0为否
    private String sai_id;   //广告的赞助信息id【必传】
    private String ad_links;   //广告的外部链接
    private String comment_state;// 是否评论过（0：未评论，1：已评论

    private String user_name;

    public String getAcname() {
        return acname;
    }

    public void setAcname(String acname) {
        this.acname = acname;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getComment_state() {
        return comment_state;
    }

    public void setComment_state(String comment_state) {
        this.comment_state = comment_state;
    }

    public String getAi_id() {
        return ai_id;
    }

    public void setAi_id(String ai_id) {
        this.ai_id = ai_id;
    }

    public String getFi_id() {
        return fi_id;
    }

    public void setFi_id(String fi_id) {
        this.fi_id = fi_id;
    }

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }

    public String getComment_num() {
        return comment_num;
    }

    public void setComment_num(String comment_num) {
        this.comment_num = comment_num;
    }

    public String getPraise_num() {
        return praise_num;
    }

    public void setPraise_num(String praise_num) {
        this.praise_num = praise_num;
    }

    public String getShare_num() {
        return share_num;
    }

    public void setShare_num(String share_num) {
        this.share_num = share_num;
    }

    public String getUser_img() {
        return user_img;
    }

    public void setUser_img(String user_img) {
        this.user_img = user_img;
    }

    public String getUser_mobile() {
        return user_mobile;
    }

    public void setUser_mobile(String user_mobile) {
        this.user_mobile = user_mobile;
    }

    public String getPraise_state() {
        return praise_state;
    }

    public void setPraise_state(String praise_state) {
        this.praise_state = praise_state;
    }

    public String getIs_advertisement() {
        return is_advertisement;
    }

    public void setIs_advertisement(String is_advertisement) {
        this.is_advertisement = is_advertisement;
    }

    public String getSai_id() {
        return sai_id;
    }

    public void setSai_id(String sai_id) {
        this.sai_id = sai_id;
    }

    public String getAd_links() {
        return ad_links;
    }

    public void setAd_links(String ad_links) {
        this.ad_links = ad_links;
    }
}
