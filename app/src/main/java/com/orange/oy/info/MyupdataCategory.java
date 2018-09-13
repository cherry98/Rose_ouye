package com.orange.oy.info;

import android.text.TextUtils;

import java.util.ArrayList;


public class MyupdataCategory {
    private String category1;
    private String category2;
    private String category3;
    private ArrayList<MyupdataPackage> MyupdataPackageList;

    public String getCategory1() {
        return category1;
    }

    public void setCategory1(String category1) {
        if (TextUtils.isEmpty(category1)) {
            this.category1 = "";
        } else {
            this.category1 = category1;
        }
    }

    public String getCategory2() {
        return category2;
    }

    public void setCategory2(String category2) {
        if (TextUtils.isEmpty(category2)) {
            this.category2 = "";
        } else {
            this.category2 = category2;
        }
    }

    public String getCategory3() {
        return category3;
    }

    public void setCategory3(String category3) {
        if (TextUtils.isEmpty(category3)) {
            this.category3 = "";
        } else {
            this.category3 = category3;
        }
    }

    public ArrayList<MyupdataPackage> getMyupdataPackageList() {
        return MyupdataPackageList;
    }

    public void setMyupdataPackageList(ArrayList<MyupdataPackage> myupdataPackageList) {
        MyupdataPackageList = myupdataPackageList;
    }
}
