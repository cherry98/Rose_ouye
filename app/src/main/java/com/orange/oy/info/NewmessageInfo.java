package com.orange.oy.info;

/**
 * Created by Administrator on 2018/4/17.
 * <p>
 * 消息列表
 */

public class NewmessageInfo {
    /**
     * photo_url : /file/task/8F956668DCB49B0088D207A04F88BB95.jpg
     * begin_date : 2017-03-16 00:00
     * end_data : 2018-07-05 00:00
     * projectid : 492
     * state : 0
     * message_id : 2
     */

    public String photo_url;
    public String begin_date;  //项目周期开始时间
    public String end_data;  //项目周期结束时间
    public int projectid;   //项目id
    public String state;    //状态，0为为评价，1为喜欢，2为不喜欢
    public int message_id;  // 信息id，评价接口要用到
    /**
     * state : 0
     * create_time : 创建时间
     * type : 1为带图片的，2为纯文字的推送消息
     * title : 消息标题
     * content : 消息内容
     */

    private String create_time;
    private String type;
    private String title;
    private String content;
    private String project_id;
    private String messageid;

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getMessageid() {
        return messageid;
    }

    public void setMessageid(String messageid) {
        this.messageid = messageid;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getBegin_date() {
        return begin_date;
    }

    public void setBegin_date(String begin_date) {
        this.begin_date = begin_date;
    }

    public String getEnd_data() {
        return end_data;
    }

    public void setEnd_data(String end_data) {
        this.end_data = end_data;
    }

    public int getProjectid() {
        return projectid;
    }

    public void setProjectid(int projectid) {
        this.projectid = projectid;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getMessage_id() {
        return message_id;
    }

    public void setMessage_id(int message_id) {
        this.message_id = message_id;

    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
