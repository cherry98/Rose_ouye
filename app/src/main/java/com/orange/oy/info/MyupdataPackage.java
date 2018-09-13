package com.orange.oy.info;

import android.text.TextUtils;
import android.widget.ProgressBar;

import java.util.ArrayList;

public class MyupdataPackage {
    //    private String packageid;
//    private String packagename;
//    private String taskid;
//    private String taskname;
    private String id;
    private String name;
    private boolean isPackage;
    private String tasktype;
    private String uniquelyNum;
    private String category1;
    private String category2;
    private String category3;
    private long fileSize;
    private ProgressBar view;
    private ArrayList<MyupdataCategory> MyupdataCategoryList;

    public ProgressBar getView() {
        return view;
    }

    public void setView(ProgressBar view) {
        this.view = view;
    }

    /**
     * 搜索是否有这个分类
     *
     * @param category1 分类1
     * @param category2 分类2
     * @param category3 分类3
     * @return 分类所在List下标
     */
    public int searchCategory(String category1, String category2, String category3) {
        if (MyupdataCategoryList != null) {
            if (TextUtils.isEmpty(category1)) {
                category1 = "";
            }
            if (TextUtils.isEmpty(category2)) {
                category2 = "";
            }
            if (TextUtils.isEmpty(category3)) {
                category3 = "";
            }
            int size = MyupdataCategoryList.size();
            for (int i = 0; i < size; i++) {
                MyupdataCategory myupdataCategory = MyupdataCategoryList.get(i);
                if ((category1 + category2 + category3).equals(myupdataCategory.getCategory1() + myupdataCategory
                        .getCategory2() + myupdataCategory.getCategory3())) {
                    return i;
                }
            }
        }
        return -1;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getUniquelyNum() {
        return uniquelyNum;
    }

    public void setUniquelyNum(String uniquelyNum) {
        this.uniquelyNum = uniquelyNum;
    }

    public boolean isPackage() {
        return isPackage;
    }

    public void setIsPackage(boolean isPackage) {
        this.isPackage = isPackage;
    }

    public String getCategory1() {
        return category1;
    }

    public void setCategory1(String category1) {
        this.category1 = category1;
    }

    public String getCategory2() {
        return category2;
    }

    public void setCategory2(String category2) {
        this.category2 = category2;
    }

    public String getCategory3() {
        return category3;
    }

    public void setCategory3(String category3) {
        this.category3 = category3;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTasktype() {
        return tasktype;
    }

    public void setTasktype(String tasktype) {
        this.tasktype = tasktype;
    }

    public ArrayList<MyupdataCategory> getMyupdataCategoryList() {
        return MyupdataCategoryList;
    }

    public void setMyupdataCategoryList(ArrayList<MyupdataCategory> myupdataCategoryList) {
        MyupdataCategoryList = myupdataCategoryList;
    }
}