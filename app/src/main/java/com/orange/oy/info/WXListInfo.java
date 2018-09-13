package com.orange.oy.info;

import java.util.Arrays;

/**
 * 微信页数据集
 */
public class WXListInfo {
    private String id;
    private String type;
    private String num;
    private String[] name;
    private WXOptionInfo option1;
    private WXOptionInfo option2;
    private int time;//距离录音开始时间

    private boolean isVideo;
    private int videoTime;

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public int getVideoTime() {
        return videoTime;
    }

    public void setVideoTime(int videoTime) {
        if (videoTime == -1) {
            videoTime = 180;
        }
        this.videoTime = videoTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String[] getName() {
        return name;
    }

    public void setName(String[] name) {
        this.name = name;
    }

    public WXOptionInfo getOption1() {
        return option1;
    }

    public void setOption1(WXOptionInfo option1) {
        this.option1 = option1;
    }

    public WXOptionInfo getOption2() {
        return option2;
    }

    public void setOption2(WXOptionInfo option2) {
        this.option2 = option2;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "WXListInfo{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", num='" + num + '\'' +
                ", name=" + Arrays.toString(name) +
                ", option1=" + option1 +
                ", option2=" + option2 +
                ", time=" + time +
                ", isVideo=" + isVideo +
                ", videoTime=" + videoTime +
                '}';
    }
}
