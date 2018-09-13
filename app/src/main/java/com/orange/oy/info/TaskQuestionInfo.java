package com.orange.oy.info;

import android.view.View;
import android.widget.EditText;

import java.util.Arrays;

public class TaskQuestionInfo {
    private String id;//如果是填空题或时间题，此参数代表填入的值；如果是判断则是1或者0；多选或单选则是选项的id
    private int num;
    private String name;
    private String[] mutexId;//互斥
    private String jump;//是否强制跳转
    private String jumpquestion;//跳转题的题号
    private View view;//多选用
    private EditText noteEditext;//备注
    private boolean isRequired;//备注是否必填
    private String photo_url;
    private boolean isClick;
    private String isforcedfill;//isforcedfill 是否必填 0否、1是
    private boolean isShowEdit;//显示edittext
    private String note;//备注
    private String url;//录音文件路径

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "TaskQuestionInfo{" +
                "id='" + id + '\'' +
                ", num=" + num +
                ", name='" + name + '\'' +
                ", mutexId=" + Arrays.toString(mutexId) +
                ", jump='" + jump + '\'' +
                ", jumpquestion='" + jumpquestion + '\'' +
                ", view=" + view +
                ", noteEditext=" + noteEditext +
                ", isRequired=" + isRequired +
                ", photo_url='" + photo_url + '\'' +
                ", isClick=" + isClick +
                ", isforcedfill='" + isforcedfill + '\'' +
                ", isShowEdit=" + isShowEdit +
                ", note='" + note + '\'' +
                '}';
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isShowEdit() {
        return isShowEdit;
    }

    public void setShowEdit(boolean showEdit) {
        isShowEdit = showEdit;
    }

    public String getIsforcedfill() {
        return isforcedfill;
    }

    public void setIsforcedfill(String isforcedfill) {
        this.isforcedfill = isforcedfill;
    }

    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean click) {
        isClick = click;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getJump() {
        return jump;
    }

    public void setJump(String jump) {
        this.jump = jump;
    }

    public String getJumpquestion() {
        return jumpquestion;
    }

    public void setJumpquestion(String jumpquestion) {
        this.jumpquestion = jumpquestion;
    }

    public void clear() {
        id = null;
        name = null;
        mutexId = null;
        view = null;
        noteEditext = null;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setIsRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    public EditText getNoteEditext() {
        return noteEditext;
    }

    public void setNoteEditext(EditText noteEditext) {
        this.noteEditext = noteEditext;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getMutexId() {
        return mutexId;
    }

    public void setMutexId(String[] mutexId) {
        this.mutexId = mutexId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
