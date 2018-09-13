package com.orange.oy.info;

import android.view.View;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/8/22.
 */

public class AllmodelCategoryInfo {
    private String category_name;
    private String is_recommend;
    private ArrayList<AllmodelTemplateInfo> template_list = new ArrayList<>();
    private boolean isSelect;
    private View bindView;

    public View getBindView() {
        return bindView;
    }

    public void setBindView(View bindView) {
        this.bindView = bindView;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getIs_recommend() {
        return is_recommend;
    }

    public void setIs_recommend(String is_recommend) {
        this.is_recommend = is_recommend;
    }

    public ArrayList<AllmodelTemplateInfo> getTemplate_list() {
        return template_list;
    }

    public void setTemplate_list(ArrayList<AllmodelTemplateInfo> template_list) {
        this.template_list = template_list;
    }
}
