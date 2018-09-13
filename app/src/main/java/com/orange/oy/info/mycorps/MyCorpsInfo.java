package com.orange.oy.info.mycorps;

/**
 * Created by Lenovo on 2018/5/9.
 * 我的战队信息
 */

public class MyCorpsInfo {

    /**
     * team_id : 1
     * team_name : 战狼队
     * user_num : 16
     */

    private String team_id;
    private String team_name;
    private String user_num;
    private String team_img;
    private String apply_user_num;
    private boolean isSelect;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getApply_user_num() {
        return apply_user_num;
    }

    public void setApply_user_num(String apply_user_num) {
        this.apply_user_num = apply_user_num;
    }

    public String getTeam_img() {
        return team_img;
    }

    public void setTeam_img(String team_img) {
        this.team_img = team_img;
    }

    public String getTeam_id() {
        return team_id;
    }

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
    }

    public String getTeam_name() {
        return team_name;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public String getUser_num() {
        return user_num;
    }

    public void setUser_num(String user_num) {
        this.user_num = user_num;
    }
}
