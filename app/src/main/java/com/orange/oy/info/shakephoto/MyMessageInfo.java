package com.orange.oy.info.shakephoto;

/**
 * Created by Lenovo on 2018/8/23.
 * 我的-> 消息页面 V3.20
 */

public class MyMessageInfo {

    /**
     * user_id : 用户id
     * user_name : 用户昵称
     * user_mobile : 用户账号
     * user_img : 用户头像
     * is_ouye : 是否是偶业小秘，1为是，0为否
     * create_time : 创建时间
     * message : 消息内容
     */

    private String user_id;
    private String user_name;
    private String user_mobile;
    private String user_img;
    private String is_ouye;
    private String create_time;
    private String message;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_mobile() {
        return user_mobile;
    }

    public void setUser_mobile(String user_mobile) {
        this.user_mobile = user_mobile;
    }

    public String getUser_img() {
        return user_img;
    }

    public void setUser_img(String user_img) {
        this.user_img = user_img;
    }

    public String getIs_ouye() {
        return is_ouye;
    }

    public void setIs_ouye(String is_ouye) {
        this.is_ouye = is_ouye;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
