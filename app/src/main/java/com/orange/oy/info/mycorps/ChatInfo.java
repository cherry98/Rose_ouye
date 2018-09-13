package com.orange.oy.info.mycorps;

/**
 * Created by Lenovo on 2018/5/11.
 */

public class ChatInfo {

    /**
     * sender : 16765676565
     * receiver : 12343234343
     * text : 这个群不能加了
     * type : 1
     */

    private String sender;
    private String receiver;
    private String text;
    private String type;
    private String apply_id;
    private String team_id;

    public String getTeam_id() {
        return team_id;
    }

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
    }

    public String getApply_id() {
        return apply_id;
    }

    public void setApply_id(String apply_id) {
        this.apply_id = apply_id;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
