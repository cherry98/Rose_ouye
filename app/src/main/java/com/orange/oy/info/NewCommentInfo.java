package com.orange.oy.info;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/8/9.
 */

public class NewCommentInfo implements Serializable {


    /**
     * ai_id : 活动id
     * activity_name : 活动名称
     * comments : [{"fi_id":"文件ID","file_url":"文件地址","create_time":"评论时间","comment":"评论内容","user_img":"用户头像","user_name":"用户昵称","comment_id ":"评论的id","praise_num":"图片被赞的数量","praise_user":"赞图片的用户昵称，多个以逗号分隔"}]
     */

    private String ai_id;
    private String activity_name;
    private ArrayList<CommentsBean> comments;

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

    public ArrayList<CommentsBean> getComments() {
        return comments;
    }

    public void setComments(ArrayList<CommentsBean> comments) {
        this.comments = comments;
    }

    public static class CommentsBean implements Serializable {
        /**
         * fi_id : 文件ID
         * file_url : 文件地址
         * create_time : 评论时间
         * comment : 评论内容
         * user_img : 用户头像
         * user_name : 用户昵称
         * comment_id  : 评论的id
         * praise_num : 图片被赞的数量
         * praise_user : 赞图片的用户昵称，多个以逗号分隔
         */
        private String activity_name;
        private String fi_id;
        private String file_url;
        private String create_time;
        private String comment;
        private String user_img;
        private String user_name;
        private String comment_id;
        private String praise_num;
        private String praise_user;
        private ArrayList<NewCommentInfo.CommentsBean> commentList = new ArrayList<>();

        public ArrayList<CommentsBean> getCommentList() {
            return commentList;
        }

        public void setCommentList(ArrayList<CommentsBean> commentList) {
            this.commentList = commentList;
        }

        public String getActivity_name() {
            return activity_name;
        }

        public void setActivity_name(String activity_name) {
            this.activity_name = activity_name;
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

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
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

        public String getComment_id() {
            return comment_id;
        }

        public void setComment_id(String comment_id) {
            this.comment_id = comment_id;
        }

        public String getPraise_num() {
            return praise_num;
        }

        public void setPraise_num(String praise_num) {
            this.praise_num = praise_num;
        }

        public String getPraise_user() {
            return praise_user;
        }

        public void setPraise_user(String praise_user) {
            this.praise_user = praise_user;
        }
    }
}
