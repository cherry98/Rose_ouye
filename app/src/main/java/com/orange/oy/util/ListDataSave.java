package com.orange.oy.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orange.oy.info.TransferInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiedongyan on 2017/2/13.
 */

public class ListDataSave {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public ListDataSave(Context mContext, String preferenceName) {
        sharedPreferences = mContext.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    //保存list
    public void setDataList(String tag, List<TransferInfo> dataList) {
        if (null == dataList || dataList.size() <= 0) {
            return;
        }
        Gson gson = new Gson();
        //转换成json数据再保存
        String strJson = gson.toJson(dataList);
        editor.clear();
        editor.putString(tag, strJson);
        editor.commit();
    }

    //获取list
    public List<TransferInfo> getDataList(String tag) {
        ArrayList<TransferInfo> dataList = new ArrayList<>(0);
        String strJson = sharedPreferences.getString(tag, null);
        if (null == strJson) {
            return dataList;
        }
        Gson gson = new Gson();
        dataList = gson.fromJson(strJson, new TypeToken<ArrayList<TransferInfo>>() {
        }.getType());
        return dataList;
    }
}
