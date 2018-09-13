package com.orange.oy.info.shakephoto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2018/9/7.
 * 体验任务 查看截图 V3.21
 */

public class ScreenshotInfo {

    /**
     * page_name : 页面名称
     * page_url : 页面地址
     * praise_num : 赞的数量
     * printscreen_list : [{"fi_id":"文件id","file_url":"文件路径","comment_type":"评论的类型，，1：吻， 2：花，3：鸡蛋，4：板砖，5：粑粑","comment_content":"评论的内容"}]
     */

    private String page_name;
    private String page_url;
    private String praise_num;
    private ArrayList<PrintscreenListBean> printscreen_list;

    public ArrayList<PrintscreenListBean> getPrintscreen_list() {
        return printscreen_list;
    }

    public void setPrintscreen_list(ArrayList<PrintscreenListBean> printscreen_list) {
        this.printscreen_list = printscreen_list;
    }

    public String getPage_name() {
        return page_name;
    }

    public void setPage_name(String page_name) {
        this.page_name = page_name;
    }

    public String getPage_url() {
        return page_url;
    }

    public void setPage_url(String page_url) {
        this.page_url = page_url;
    }

    public String getPraise_num() {
        return praise_num;
    }

    public void setPraise_num(String praise_num) {
        this.praise_num = praise_num;
    }

    public static class PrintscreenListBean {
        /**
         * fi_id : 文件id
         * file_url : 文件路径
         * comment_type : 评论的类型，，1：吻， 2：花，3：鸡蛋，4：板砖，5：粑粑
         * comment_content : 评论的内容
         */

        private String fi_id;
        private String file_url;
        private String comment_type;
        private String comment_content;

        public String getFi_id() {
            return fi_id;
        }

        public void setFi_id(String fi_id) {
            this.fi_id = fi_id;
        }

        public String getFile_url() {
            return file_url;
        }

        public void setFile_url(String file_url) {
            this.file_url = file_url;
        }

        public String getComment_type() {
            return comment_type;
        }

        public void setComment_type(String comment_type) {
            this.comment_type = comment_type;
        }

        public String getComment_content() {
            return comment_content;
        }

        public void setComment_content(String comment_content) {
            this.comment_content = comment_content;
        }
    }

    /**
     * GridView数据
     */
    private String fi_id;
    private String file_url;
    private String comment_type;
    private String comment_content;

    public String getFi_id() {
        return fi_id;
    }

    public void setFi_id(String fi_id) {
        this.fi_id = fi_id;
    }

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }

    public String getComment_type() {
        return comment_type;
    }

    public void setComment_type(String comment_type) {
        this.comment_type = comment_type;
    }

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }
}
