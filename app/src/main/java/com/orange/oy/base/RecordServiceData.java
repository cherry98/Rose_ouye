package com.orange.oy.base;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 录音数据临时存储，请注意数据的初始化！
 */
public class RecordServiceData {
    public static final String TempDataName = "RECORDSERVICEDATA_OUYE_NAME";

    public static SharedPreferences.Editor getEditor(Context context) {
        return context.getSharedPreferences(TempDataName, Context.MODE_PRIVATE).edit();
    }

    public static void clearData(Context context) {
        SharedPreferences.Editor e = context.getSharedPreferences(TempDataName, Context.MODE_PRIVATE).edit();
        e.clear();
        e.commit();
    }
}
