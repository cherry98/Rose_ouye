package com.orange.oy.info.mycorps;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/5/14.
 */

public class CheckNewMemberInfo {

    /**
     * code : 200
     * msg : 查询成功
     * data : {"list":[{"user_name":"用户昵称","user_img":"/a.jpg","user_level":4,"applicant":"167676767656","mobile":"15454565456","city":"城市","task_num":145,"create_time":"2018-03-12","inviter":"邀请人","reason":"申请时填写的内容","reply":[{"sender":"16765676565","receiver":"12343234343","text":"这个群不能加了","type":1},{"sender":"12343234343","receiver":"16765676565","text":"这个群不能加了","type":0}]}]}
     */


    /**
     * user_name : 用户昵称
     * user_img : /a.jpg
     * user_level : 4
     * applicant : 167676767656
     * mobile : 15454565456
     * city : 城市
     * task_num : 145
     * create_time : 2018-03-12
     * inviter : 邀请人
     * reason : 申请时填写的内容
     * reply : [{"sender":"16765676565","receiver":"12343234343","text":"这个群不能加了","type":1},{"sender":"12343234343","receiver":"16765676565","text":"这个群不能加了","type":0}]
     */
    private String apply_id;
    private String user_name;
    private String user_img;
    private int user_level;
    private String applicant;
    private String mobile;
    private String city;
    private int task_num;
    private String create_time;
    private String inviter;
    private String reason;
    private ArrayList<ReplyBean> reply;

    public String getApply_id() {
        return apply_id;
    }

    public void setApply_id(String apply_id) {
        this.apply_id = apply_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_img() {
        return user_img;
    }

    public void setUser_img(String user_img) {
        this.user_img = user_img;
    }

    public int getUser_level() {
        return user_level;
    }

    public void setUser_level(int user_level) {
        this.user_level = user_level;
    }

    public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getTask_num() {
        return task_num;
    }

    public void setTask_num(int task_num) {
        this.task_num = task_num;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getInviter() {
        return inviter;
    }

    public void setInviter(String inviter) {
        this.inviter = inviter;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ArrayList<ReplyBean> getReply() {
        return reply;
    }

    public void setReply(ArrayList<ReplyBean> reply) {
        this.reply = reply;
    }

    public static class ReplyBean {
        /**
         * sender : 16765676565
         * receiver : 12343234343
         * text : 这个群不能加了
         * type : 1
         */

        private String sender;
        private String receiver;
        private String text;
        private int type;
        private String username;
        private String reason;


        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }


        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public String getReceiver() {
            return receiver;
        }

        public void setReceiver(String receiver) {
            this.receiver = receiver;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

    }
}
