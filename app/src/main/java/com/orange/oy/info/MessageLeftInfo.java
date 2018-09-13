package com.orange.oy.info;

import android.widget.ToggleButton;

/**
 * 消息左侧列表信息
 */
public class MessageLeftInfo {
    private String id;
    private String title;
    private String time;
    private String message;
    private String message2;
    private String flag;
    private boolean isOpen;
    private boolean isSelect;
    private String code;
    private boolean isShow;//是否显示复选框
    private boolean isChecked;//是否选中Item



    public MessageLeftInfo() {
    }

    public MessageLeftInfo(boolean isChecked, boolean isShow) {
        this.isChecked = isChecked;
        this.isShow = isShow;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public String getMessage2() {
        return message2;
    }

    public void setMessage2(String message2) {
        this.message2 = message2;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
