package com.orange.oy.info;

/**
 * Created by Administrator on 2018/8/22.
 */

public class AllmodelTemplateInfo {
    /**
     * "template_id":"模板id",
     * "template_name":"模板名称",
     * "template_img":"模板图标",
     * "template_type":"类型，1为集图活动，2为甩投任务"
     */
    private String template_id;
    private String template_name;
    private String template_img;
    private String template_type;

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getTemplate_name() {
        return template_name;
    }

    public void setTemplate_name(String template_name) {
        this.template_name = template_name;
    }

    public String getTemplate_img() {
        return template_img;
    }

    public void setTemplate_img(String template_img) {
        this.template_img = template_img;
    }

    public String getTemplate_type() {
        return template_type;
    }

    public void setTemplate_type(String template_type) {
        this.template_type = template_type;
    }
}
