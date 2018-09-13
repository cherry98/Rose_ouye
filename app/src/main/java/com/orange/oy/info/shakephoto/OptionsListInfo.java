package com.orange.oy.info.shakephoto;

import java.io.Serializable;

/**
 * Created by Lenovo on 2018/9/4.
 * 选项info V3.21
 */

public class OptionsListInfo implements Serializable {
    private static final long serialVersionUID = 3467351732987822793L;
    /**
     * option_id : 8672
     * option_name : 选项名字
     * option_num : 选项序号
     */

    private String option_id;
    private String option_name;
    private String option_num;
    private String photo_url;
    private String path;
    private boolean isUped;

    public boolean isUped() {
        return isUped;
    }

    public void setUped(boolean uped) {
        isUped = uped;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getOption_id() {
        return option_id;
    }

    public void setOption_id(String option_id) {
        this.option_id = option_id;
    }

    public String getOption_name() {
        return option_name;
    }

    public void setOption_name(String option_name) {
        this.option_name = option_name;
    }

    public String getOption_num() {
        return option_num;
    }

    public void setOption_num(String option_num) {
        this.option_num = option_num;
    }
}
