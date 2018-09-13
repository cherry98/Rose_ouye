package com.orange.oy.info;

import android.view.View;

/**
 * Created by Administrator on 2018/7/6.
 */

public class TaskPhotoInfo {
    private String path;
    private boolean isSelect;
    private boolean isUped;
    private String upUrl;
    private View bindView;
    private boolean isLocal;

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public View getBindView() {
        return bindView;
    }

    public void setBindView(View bindView) {
        this.bindView = bindView;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public boolean isUped() {
        return isUped;
    }

    public void setUped(boolean uped) {
        isUped = uped;
    }

    public String getUpUrl() {
        return upUrl;
    }

    public void setUpUrl(String upUrl) {
        this.upUrl = upUrl;
    }
}
