package com.orange.oy.info;

/**
 * Created by Administrator on 2018/7/25.
 */

public class CommentListInfo {

    /**
     * comment_id : 评论的id
     * user_img : 用户头像
     * user_name : 用户昵称
     * content : 评论的内容
     * create_time : 评论的时间
     * praise_num : 赞的数量
     * is_praise  : 是否点过赞，1为点过，0为没点过
     * "comment_username ":"@的用户昵称"
     */

    private String comment_id;
    private String user_img;
    private String user_name;
    private String content;
    private String create_time;
    private int praise_num;
    private String is_praise;
    private String comment_username;

    public String getComment_username() {
        return comment_username;
    }

    public void setComment_username(String comment_username) {
        this.comment_username = comment_username;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }


    public String getUser_img() {
        return user_img;
    }

    public void setUser_img(String user_img) {
        this.user_img = user_img;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public int getPraise_num() {
        return praise_num;
    }

    public void setPraise_num(int praise_num) {
        this.praise_num = praise_num;
    }

    public String getIs_praise() {
        return is_praise;
    }

    public void setIs_praise(String is_praise) {
        this.is_praise = is_praise;
    }
}
