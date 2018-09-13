package com.orange.oy.info.mycorps;

/**
 * Created by Lenovo on 2018/5/15.
 */

public class CorpsNoticeInfo {

    /**
     * notice_id : 1
     * title : 公告1
     * text :
     * create_time : 2018-4-12
     */

    private String notice_id;
    private String title;
    private String text;
    private String create_time;
    private String is_read;
    private String head_img;

    public String getHead_img() {
        return head_img;
    }

    public void setHead_img(String head_img) {
        this.head_img = head_img;
    }

    public String getIs_read() {
        return is_read;
    }

    public void setIs_read(String is_read) {
        this.is_read = is_read;
    }

    public String getNotice_id() {
        return notice_id;
    }

    public void setNotice_id(String notice_id) {
        this.notice_id = notice_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
