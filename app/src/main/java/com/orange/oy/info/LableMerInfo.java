package com.orange.oy.info;

import java.util.List;

/**
 * Created by Administrator on 2018/7/3.
 */

public class LableMerInfo {


    /**
     * label_id : 标签id
     * label_name : 标签名称
     * usermobile_list : ["手机号1","手机号2"]
     */

    private String label_id;
    private String label_name;
    private String usermobile_list;

    public String getLabel_id() {
        return label_id;
    }

    public void setLabel_id(String label_id) {

        this.label_id = label_id;
    }

    public String getLabel_name() {
        return label_name;
    }

    public void setLabel_name(String label_name) {
        this.label_name = label_name;
    }

    public String getUsermobile_list() {
        return usermobile_list;
    }

    public void setUsermobile_list(String usermobile_list) {
        usermobile_list = usermobile_list.replaceAll("\\\"", "").replaceAll("\\[", "").replaceAll("\\]", "");
        this.usermobile_list = usermobile_list;
    }
}
