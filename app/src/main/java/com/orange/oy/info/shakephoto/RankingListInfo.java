package com.orange.oy.info.shakephoto;


import java.util.List;

/**
 * Created by Lenovo on 2018/7/19.
 * 用户排名参数
 */

public class RankingListInfo {

    /**
     * fi_id : 文件ID
     * file_url : 原图文件地址
     * create_time : 创建时间，格式为2018-05-29
     * key_concent : ["关键词1","关键词2"]
     * ranking : 排名
     * comment_num : 评论数
     * praise_num : 点赞数
     * share_num : 分享数
     * user_img : 用户头像
     * user_mobile : 用户账号
     * praise_state : 是否点赞过（0：未点赞，1：已点赞）
     */

    private String fi_id;
    private String file_url;
    private String create_time;
    private String ranking;
    private String comment_num;
    private String praise_num;
    private String share_num;
    private String user_img;
    private String user_mobile;
    private String praise_state;
    private String key_concent;
    private String user_name;
    private String comment_state;// 是否评论过（0：未评论，1：已评论

    private boolean isSelect;//是否被选择

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getComment_state() {
        return comment_state;
    }

    public void setComment_state(String comment_state) {
        this.comment_state = comment_state;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
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

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
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

    public String getKey_concent() {
        return key_concent;
    }

    public void setKey_concent(String key_concent) {
        this.key_concent = key_concent;
    }
}
