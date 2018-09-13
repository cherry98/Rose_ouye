package com.orange.oy.info;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiedongyan on 2017/12/18.
 */

public class ExperienceCommentInfo {

    /**
     * imgurl : /file/task/3E89D40AD12194515DC235488F5BB0B5.jpg
     * score : 4
     * multiselect : ["服务好","服务人员着装整齐"]
     * photourl : ["/file/task/968DFE09262B1BC98D5D3D39FFE8D6E2.jpg","/file/task/D613E9898F06032E1C555DBBD969C2C1.jpg"]
     * comment : 请求去去去群群群群群群群群群
     * date : 2017-12-07
     * type : 0
     */

    private String imgurl;
    private String score;
    private String comment;
    private String date;
    private String type;
    private JSONArray multiselect;
    private JSONArray photourl;

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JSONArray getMultiselect() {
        return multiselect;
    }

    public void setMultiselect(JSONArray multiselect) {
        this.multiselect = multiselect;
    }

    public JSONArray getPhotourl() {
        return photourl;
    }

    public void setPhotourl(JSONArray photourl) {
        this.photourl = photourl;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
