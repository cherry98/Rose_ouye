package com.orange.oy.info;

import android.view.View;

/**
 * Created by Administrator on 2018/5/10.
 */

public class TeamSpecialtyInfo {
    private String id;
    private String name;
    private boolean isSelect;
    private boolean isCustom;//是否自定义
    private View view;

    public boolean isCustom() {
        return isCustom;
    }

    public void setCustom(boolean custom) {
        isCustom = custom;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
