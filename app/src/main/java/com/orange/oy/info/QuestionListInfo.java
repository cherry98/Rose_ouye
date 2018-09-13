package com.orange.oy.info;

import com.orange.oy.base.Tools;

import java.util.ArrayList;

/**
 * Created by xiedongyan on 2017/12/28.
 */

public class QuestionListInfo {
    /**
     * selectionId : 2
     * num : 2
     * question : 您认为以下那个店环境最好？
     */

    private String selectionId;
    private String num;
    private String question;
    private ArrayList<StoreInfo> storeinfos;//题目里的答案list

    public void setStoreinfos(ArrayList<StoreInfo> storeinfos){
        this.storeinfos = new ArrayList<>();
        for(StoreInfo temp:storeinfos){
            StoreInfo storeInfo = new StoreInfo();
            storeInfo.setPhotoUrl(temp.getPhotoUrl());
            storeInfo.setStoreid(temp.getStoreid());
            storeInfo.setStoreName(temp.getStoreName());
            this.storeinfos.add(storeInfo);
        }
        this.storeinfos.get(0).isSelect = true;
    }

    public ArrayList<StoreInfo> getStoreinfos(){
        return storeinfos;
    }
    public void clearSelect(){
        for(StoreInfo temp:storeinfos){
            temp.isSelect = false;
        }
    }
    public String getSelectionId() {
        return selectionId;
    }

    public void setSelectionId(String selectionId) {
        this.selectionId = selectionId;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
