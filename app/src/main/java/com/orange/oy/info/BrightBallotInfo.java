package com.orange.oy.info;

/**
 * Created by xiedongyan on 2017/1/17.
 */

public class BrightBallotInfo {

    /**
     * selectid : 1
     * type : 类别一
     * num : 1
     * complete : 0
     */

    private int selectid;
    private String type;
    private int num;
    private int complete;

    public int getSelectid() {
        return selectid;
    }

    public void setSelectid(int selectid) {
        this.selectid = selectid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getComplete() {
        return complete;
    }

    public void setComplete(int complete) {
        this.complete = complete;
    }

    /**
     * name : 木木
     * sex : 女
     * mobile : 1211222
     * email : 1222@qq.com
     * dealer : null
     * idcardnum : 130245276787654000
     * state : null
     * note : null
     * is_note : null
     * taskid : 2360
     * executeid : 1
     * iscomplete : 0
     */

    private String name;
    private String sex;
    private String mobile;
    private String email;
    private Object dealer;
    private String idcardnum;
    private Object state;
    private Object note;
    private Object is_note;
    private int taskid;
    private int executeid;
    private int iscomplete;
    private String tasktype;

    public String getTasktype() {
        return tasktype;
    }

    public void setTasktype(String tasktype) {
        this.tasktype = tasktype;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Object getDealer() {
        return dealer;
    }

    public void setDealer(Object dealer) {
        this.dealer = dealer;
    }

    public String getIdcardnum() {
        return idcardnum;
    }

    public void setIdcardnum(String idcardnum) {
        this.idcardnum = idcardnum;
    }

    public Object getState() {
        return state;
    }

    public void setState(Object state) {
        this.state = state;
    }

    public Object getNote() {
        return note;
    }

    public void setNote(Object note) {
        this.note = note;
    }

    public Object getIs_note() {
        return is_note;
    }

    public void setIs_note(Object is_note) {
        this.is_note = is_note;
    }

    public int getTaskid() {
        return taskid;
    }

    public void setTaskid(int taskid) {
        this.taskid = taskid;
    }

    public int getExecuteid() {
        return executeid;
    }

    public void setExecuteid(int executeid) {
        this.executeid = executeid;
    }

    public int getIscomplete() {
        return iscomplete;
    }

    public void setIscomplete(int iscomplete) {
        this.iscomplete = iscomplete;
    }
}
