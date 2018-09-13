package com.orange.oy.info;

import java.util.ArrayList;

public class TaskitemDetailInfo {
    private String id;//p_id
    private String id2;//task_id
    private String store_id;
    private String isstore;
    private String name;
    private String sign;//判断项目类型
    private String isClose;//如果是1任务包可以执行，2任务包关闭
    private ArrayList<TaskitemDetailInfo> childrens;

    public String getIsstore() {
        return isstore;
    }

    public void setIsstore(String isstore) {
        this.isstore = isstore;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getIsClose() {
        return isClose;
    }

    public void setIsClose(String isClose) {
        this.isClose = isClose;
    }

    public String getId2() {
        return id2;
    }

    public void setId2(String id2) {
        this.id2 = id2;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<TaskitemDetailInfo> getChildrens() {
        return childrens;
    }

    public void setChildrens(ArrayList<TaskitemDetailInfo> childrens) {
        this.childrens = childrens;
    }

    public TaskitemDetailInfo testData(int n) {
        setName("test" + n);
        childrens = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            TaskitemDetailInfo item = new TaskitemDetailInfo();
            item.setName(i + "");
            item.setSign((i / 2) + "");
            childrens.add(item);
        }
        return this;
    }
}
