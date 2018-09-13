package com.orange.oy.info;

/**
 * Created by xiedongyan on 2017/1/16.
 */

public class BrightPersonInfo {

    /**
     * name : 木木
     * sex : 女
     * mobile : 1211222
     * email : 1222@qq.com
     * dealer : null
     * idcardnum : 130245276787654000
     * state : null
     * note : null
     * is_note : 0
     */

    private String name;
    private String sex;
    private String mobile;
    private String email;
    private String dealer;
    private String idcardnum;
    private String state;
    private String note;
    private int is_note;
    private String job;
    private int iscomplete;

    public int getIscomplete() {
        return iscomplete;
    }

    public void setIscomplete(int iscomplete) {
        this.iscomplete = iscomplete;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
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

    public String getDealer() {
        return dealer;
    }

    public void setDealer(String dealer) {
        this.dealer = dealer;
    }

    public String getIdcardnum() {
        return idcardnum;
    }

    public void setIdcardnum(String idcardnum) {
        this.idcardnum = idcardnum;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getIs_note() {
        return is_note;
    }

    public void setIs_note(int is_note) {
        this.is_note = is_note;
    }

    @Override
    public String toString() {
        return "BrightPersonInfo{" +
                "name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", dealer='" + dealer + '\'' +
                ", idcardnum='" + idcardnum + '\'' +
                ", state='" + state + '\'' +
                ", note='" + note + '\'' +
                ", is_note=" + is_note +
                '}';
    }
}
