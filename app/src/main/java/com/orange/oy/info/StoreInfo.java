package com.orange.oy.info;

/**
 * Created by xiedongyan on 2017/12/28.
 */

public class StoreInfo {

    /**
     * storeid : 128712
     * storeName : 1
     * photoUrl : /file/task/3F5FB8B2C1809C50034E92BA0C26EB00.jpg
     */

    private String storeid;
    private String storeName;
    private String photoUrl;
    public boolean isSelect;

    public String getStoreid() {
        return storeid;
    }

    public void setStoreid(String storeid) {
        this.storeid = storeid;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
