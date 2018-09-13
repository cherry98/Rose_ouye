package com.orange.oy.view.photoview;

import java.io.Serializable;

/**
 * Created by ntl on 2016/12/13.
 */
public class PhotoMode implements Serializable {
    private String path = "";

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public PhotoMode(String path) {

        this.path = path;
    }
}
