package com.orange.oy.info;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/6/29.
 */

public class LargeImagePageInfo implements Serializable {
    private String ai_id;
    private String file_url;
    private String aitivity_name;
    private String key_concent;
    private String address;
    private String create_time;
    private String fi_id;
    private String IsHaveDelete;
    private String show_address;
    private String user_name;
    private String IsHaveShare;

    public String getIsHaveShare() {
        return IsHaveShare;
    }

    public void setIsHaveShare(String isHaveShare) {
        IsHaveShare = isHaveShare;
    }

    public String getAi_id() {
        return ai_id;
    }

    public void setAi_id(String ai_id) {
        this.ai_id = ai_id;
    }

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }

    public String getAitivity_name() {
        return aitivity_name;
    }

    public void setAitivity_name(String aitivity_name) {
        this.aitivity_name = aitivity_name;
    }

    public String getKey_concent() {
        return key_concent;
    }

    public void setKey_concent(String key_concent) {
        this.key_concent = key_concent;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getFi_id() {
        return fi_id;
    }

    public void setFi_id(String fi_id) {
        this.fi_id = fi_id;
    }

    public String getIsHaveDelete() {
        return IsHaveDelete;
    }

    public void setIsHaveDelete(String isHaveDelete) {
        IsHaveDelete = isHaveDelete;
    }

    public String getShow_address() {
        return show_address;
    }

    public void setShow_address(String show_address) {
        this.show_address = show_address;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
