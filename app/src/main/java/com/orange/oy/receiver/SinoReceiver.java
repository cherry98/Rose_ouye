package com.orange.oy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.orange.oy.base.AppInfo;
import com.orange.oy.base.Tools;
import com.orange.oy.db.AppDBHelper;
import com.orange.oy.db.OfflineDBHelper;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.network.Urls;
import com.orange.oy.service.RecordService;
import com.orange.oy.util.FileCache;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SinoReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    static final String ACTION2 = "service.UpdataService.destory";
    static final String ACTION3 = "service.RecordService.destory";
    static final String ACTION4 = "android.net.conn.CONNECTIVITY_CHANGE";

    private boolean isHavNetwork(Context context) {
//        String network = Tools.GetNetworkType(context);//网络状态
//        switch (AppInfo.getOpen4GUpdata(context)) {
//            case AppInfo.netSetting_1: {
//                return !TextUtils.isEmpty(network);
//            }
//            case AppInfo.netSetting_2: {
//                return "WIFI".equals(network);
//            }
//            case AppInfo.netSetting_3: {
//                return !TextUtils.isEmpty(network) && network.endsWith("G");
//            }
//        }
        return Tools.isNetworkConnected(context);
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Tools.d("SinoReceiver action>>>>>>>>>>>>>>>>" + action);
        if (action.equals(ACTION) && !TextUtils.isEmpty(Tools.GetNetworkType(context))) {
            UpdataDBHelper updataDBHelper = new UpdataDBHelper(context);
            OfflineDBHelper offlineDBHelper = new OfflineDBHelper(context);
            if (updataDBHelper.isHave() && isHavNetwork(context)) {
                Intent service = new Intent("com.orange.oy.UpdataNewService");
                service.setPackage("com.orange.oy");
                context.startService(service);
            }
            if (offlineDBHelper.isHadDownList()) {
                Intent service = new Intent("com.orange.oy.DownloadDataService");
                service.setPackage("com.orange.oy");
                context.startService(service);
            }
        } else if (action.equals(ACTION2)) {
            UpdataDBHelper updataDBHelper = new UpdataDBHelper(context);
            if (updataDBHelper.isHave() && isHavNetwork(context)) {
                Intent service = new Intent("com.orange.oy.UpdataNewService");
                service.setPackage("com.orange.oy");
                context.startService(service);
            }
        } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            UpdataDBHelper updataDBHelper = new UpdataDBHelper(context);
            OfflineDBHelper offlineDBHelper = new OfflineDBHelper(context);
            if (isHavNetwork(context)) {
                if (updataDBHelper.isHave()) {
                    Intent service = new Intent("com.orange.oy.UpdataNewService");
                    service.setPackage("com.orange.oy");
                    context.startService(service);
                }
            } else {
                Intent service = new Intent("com.orange.oy.UpdataNewService");
                service.setPackage("com.orange.oy");
                context.stopService(service);
//                Intent service1 = new Intent("com.orange.oy.DownloadDataService");
//                service1.setPackage("com.orange.oy");
//                context.stopService(service1);
            }
            if (offlineDBHelper.isHadDownList()) {
                Intent service = new Intent("com.orange.oy.DownloadDataService");
                service.setPackage("com.orange.oy");
                context.startService(service);
            }
        } else if (action.equals(ACTION3)) {
            try {
                UpdataDBHelper updataDBHelper = new UpdataDBHelper(context);
                AppDBHelper appDBHelper = new AppDBHelper(context);
                String storeid = intent.getStringExtra("storeid");
                String usermobile = intent.getStringExtra("usermobile");
                String fileName = intent.getStringExtra("fileName");
                String project_id = intent.getStringExtra("project_id");
                String projectname = intent.getStringExtra("projectname");
                boolean isOffline = intent.getBooleanExtra("isOffline", true);
                boolean isExperience = intent.getBooleanExtra("isExperience", false);
                boolean isNormal = intent.getBooleanExtra("isNormal", false);
                String task_id = intent.getStringExtra("task_id");
                Map<String, String> map = new HashMap<String, String>();
                map.put("province", intent.getStringExtra("province"));
                map.put("usermobile", usermobile);
                map.put("projectname", projectname);
                map.put("store_name", intent.getStringExtra("store_name"));
                map.put("store_num", intent.getStringExtra("store_num"));
                map.put("city", intent.getStringExtra("city"));
                String key = "video";
                File mRecordFile = new File(FileCache.getDirForRecord(context, usermobile + "/" + storeid),
                        fileName + ".amr");
                if (mRecordFile.isFile()) {
                    if (isExperience) {
                        updataDBHelper.addUpdataTask(usermobile, project_id, projectname,
                                intent.getStringExtra("code"), intent.getStringExtra("brand"), storeid, map.get("store_name"), null,
                                null, "tyly",
                                null, null, null, null, null, Tools.getToken() + project_id + usermobile + Tools.getTimeSS(), Urls
                                        .ExperienceInsertFileInfo, key,
                                mRecordFile.getPath(), UpdataDBHelper.Updata_file_type_video, map, null, false, null, null,
                                isOffline);
                    } else {
                        if (isNormal) {
                            if (!appDBHelper.havPhotoUrlRecord(usermobile, project_id, storeid, task_id, mRecordFile.getPath(), null)) {
                                updataDBHelper.addUpdataTask(usermobile, project_id, projectname,
                                        intent.getStringExtra("code"), intent.getStringExtra("brand"), storeid, map.get("store_name"), null,
                                        null, "jcly", null, null, null, null, null,
                                        Tools.getToken() + project_id + usermobile + Tools.getTimeSS(), null, key,
                                        mRecordFile.getPath(), UpdataDBHelper.Updata_file_type_video, map, null, false, null, null,
                                        isOffline);
                                appDBHelper.addPhotoUrlRecord(usermobile, project_id, storeid, task_id, mRecordFile.getPath(), null);
                                int num = appDBHelper.getAllRecordNumber(usermobile, project_id, storeid, task_id);
                                appDBHelper.setFileNum(mRecordFile.getPath(), num + "");
                            }
                        } else {
                            updataDBHelper.addUpdataTask(usermobile, project_id, projectname,
                                    intent.getStringExtra("code"), intent.getStringExtra("brand"), storeid, map.get("store_name"), null,
                                    null, "-3",
                                    null, null, null, null, null, Tools.getToken() + project_id + usermobile + Tools.getTimeSS(), Urls
                                            .Soundup, key,
                                    mRecordFile.getPath(), UpdataDBHelper.Updata_file_type_video, map, null, false, null, null,
                                    isOffline);
                        }
                    }
                }
                Intent service = new Intent("com.orange.oy.UpdataNewService");
                service.setPackage("com.orange.oy");
                context.startService(service);
            } catch (Exception e) {
                e.printStackTrace();
            }
            RecordService.clearIntent();
        } else if (action.equals(ACTION4)) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeInfo = manager.getActiveNetworkInfo();
            if (activeInfo != null && activeInfo.isAvailable()) {
                Intent service = new Intent("com.orange.oy.UpdataNewService");
                service.setPackage("com.orange.oy");
                context.startService(service);
            }
        }
    }
}
