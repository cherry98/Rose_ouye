package com.orange.oy.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.common.utils.IOUtils;
import com.alibaba.sdk.android.oss.model.CompleteMultipartUploadRequest;
import com.alibaba.sdk.android.oss.model.CompleteMultipartUploadResult;
import com.alibaba.sdk.android.oss.model.InitiateMultipartUploadRequest;
import com.alibaba.sdk.android.oss.model.InitiateMultipartUploadResult;
import com.alibaba.sdk.android.oss.model.ListPartsRequest;
import com.alibaba.sdk.android.oss.model.ListPartsResult;
import com.alibaba.sdk.android.oss.model.PartETag;
import com.alibaba.sdk.android.oss.model.UploadPartRequest;
import com.alibaba.sdk.android.oss.model.UploadPartResult;
import com.orange.oy.R;
import com.orange.oy.activity.MainActivity;
import com.orange.oy.activity.MyUpdataTaskActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.Tools;
import com.orange.oy.db.AppDBHelper;
import com.orange.oy.db.OfflineDBHelper;
import com.orange.oy.db.SystemDBHelper;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.info.UpdataInfo;
import com.orange.oy.info.WebpagetaskDBInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.receiver.SinoReceiver;
import com.orange.oy.util.FileCache;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 上传服务  tyly==体验录音 typz==体验拍照
 */
public class UpdataNewService extends Service implements Runnable {
    /**
     * 判断服务是否正在运行
     *
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName();
//            Tools.d(mName);
            if (serviceName.equals(mName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        Tools.d("onCreateNew");
    }

    private static Thread thread;

    public int onStartCommand(Intent intent, int flags, int startId) {
        startNotification();
        Tools.d("onStartCommand");
        if (thread == null) {
            Tools.d("creat thread");
            updataDBHelper = new UpdataDBHelper(this);
            offlineDBHelper = new OfflineDBHelper(this);
            systemDBHelper = new SystemDBHelper(this);
            appDBHelper = new AppDBHelper(this);
            thread = new Thread(this);
            thread.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private boolean isHavNetwork() {//1是WiFi、4G都可以 2仅WiFi 3仅4G
//        String network = Tools.GetNetworkType(this);//网络状态
//        Tools.d("当前网络状态network--" + network);
//        int flag = AppInfo.getOpen4GUpdata(this);
//        Tools.d("设置上传网络状态network flag--" + flag);
//        switch (flag) {
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
        return Tools.isNetworkConnected(this);
    }

    private UpdataDBHelper updataDBHelper;
    private OfflineDBHelper offlineDBHelper;
    private SystemDBHelper systemDBHelper;
    private AppDBHelper appDBHelper;
    private ArrayList<File> deleteFileList;

    /**
     * 111执行完成创建调资料回收接口记录 11单张照片上传记录
     * 222执行完成创建调扫码资料回收接口记录 22扫码置无效视频上传记录
     * jcly检查项目的录音任务
     */
    public void run() {
        contentTitle = "资料回收中...";
        Tools.d("通知显示");
        startNotification();
        ArrayList<UpdataInfo> list;
        int size = 0;
        deleteFileList = new ArrayList<>();
        while (thread != null && isHavNetwork()) {
            list = updataDBHelper.getTask();
            size = list.size();
            Tools.d("size----" + size);
            if (size == 0) {
                if (updataDBHelper.isHave() && updataDBHelper.getTask().size() == 0) {//数据库剩下的都是脏数据
                    updataDBHelper.clearTable();
                }
                contentTitle = "资料清理中...";
                startNotification();
                MainActivity.deleteFiles(FileCache.getDirForPhoto(this));
                break;
            }
            int i = 0;
            while (!list.isEmpty() && thread != null && isHavNetwork()) {
                UpdataInfo updataInfo = list.remove(0);
                if ("tyly".equals(updataInfo.getTaskType()) || "8-9".equals(updataInfo.getTaskType()) ||
                        "8-8".equals(updataInfo.getTaskType()) || "3".equals(updataInfo.getTaskType()) || isHavNetwork()) {
                    if (updataInfo.getTaskType().equals("wx3")) {//暗访微信任务
                        boolean issuccess = UpdataCompleted2(updataInfo.getCompleted_url(), updataInfo.getCompleted_parameter(),
                                updataInfo.getUsername(), updataInfo.getProjectid(), updataInfo.getProjecname(),
                                updataInfo.getStroeid(), updataInfo.getStorename(), updataInfo.getTaskType());
                        if (issuccess) {
                            updataDBHelper.removeTask(updataInfo.getUniquelyNum(), "");
                        }
                        continue;
                    } else if (updataInfo.getTaskType().equals("3") || updataInfo.getTaskType().equals("3-3") || updataInfo
                            .getTaskType().equals("6") || updataInfo.getTaskType().equals("333") || updataInfo.getTaskType().equals("111")
                            || updataInfo.getTaskType().equals("222")) {//正常记录任务
                        String data = "";
                        Map<String, String> parames = updataInfo.getParame();
                        if (updataInfo.getTaskType().equals("111") || updataInfo.getTaskType().equals("222")) {
                            //扫码任务使用category1判断是否可置无效（0不需要拼接fileurl，1需要拼接fileurl）
                            if (!("222".equals(updataInfo.getTaskType()) && "0".equals(updataInfo.getCategory1()))) {
                                String fileurl = appDBHelper.getAllPhotoUrl(updataInfo.getUsername(), updataInfo.getProjectid(), updataInfo.getStroeid(), updataInfo.getTaskId());
                                Tools.d("新增拍照or视频：" + fileurl);
                                if (fileurl != null && (appDBHelper.getPhotoUrlIsCompete(fileurl, updataInfo.getProjectid(), updataInfo.getStroeid(), updataInfo.getTaskId()))) {
                                    Tools.d("新增拍照or视频是否可资料回收：" + appDBHelper.getPhotoUrlIsCompete(fileurl, updataInfo.getProjectid(), updataInfo.getStroeid(), updataInfo.getTaskId()));
                                    parames.put("filelist", fileurl);
                                } else {
                                    if (fileurl != null)
                                        continue;
                                }
                            }
                        }
                        try {
                            Iterator<String> iterator = parames.keySet().iterator();
                            while (iterator.hasNext()) {
                                String key = iterator.next();
                                if (TextUtils.isEmpty(data)) {
                                    data = key + "=" + URLEncoder.encode(parames.get(key).trim(), "utf-8");
                                } else {
                                    data = data + "&" + key + "=" + URLEncoder.encode(parames.get(key).trim(), "utf-8");
                                }
                            }
                        } catch (UnsupportedEncodingException e) {
                            MobclickAgent.reportError(this, "updataservice UnsupportedEncodingException1:" + e.getMessage());
                            continue;
                        }
                        Tools.d("data:==" + data);
                        String executeid = UpdataCompleted(updataInfo.getUrl(), data,
                                updataInfo.getUsername(), updataInfo.getProjectid(), updataInfo.getProjecname(),
                                updataInfo.getStroeid(), updataInfo.getStorename(), updataInfo.getTaskType(), updataInfo.getTaskId());
                        Tools.d("executeid1:" + executeid);
                        if (!updataInfo.getTaskType().equals("6")) {
                            if (updataInfo.getTaskType().equals("111") || updataInfo.getTaskType().equals("222")) {
                                updataDBHelper.removeTask(updataInfo.getUniquelyNum(), "");
                                continue;
                            } else if (executeid == null) {
                                continue;
                            }
                        }
                        boolean issuccess;
                        if (updataInfo.getTaskType().equals("6")) {
                            issuccess = UpdataCompleted2(updataInfo.getCompleted_url(), updataInfo.getCompleted_parameter(),
                                    updataInfo.getUsername(), updataInfo.getProjectid(), updataInfo.getProjecname(),
                                    updataInfo.getStroeid(), updataInfo.getStorename(), updataInfo.getTaskType());
                        } else {
                            issuccess = UpdataCompleted2(updataInfo.getCompleted_url(), updataInfo.getCompleted_parameter() +
                                            "&executeid=" + executeid,
                                    updataInfo.getUsername(), updataInfo.getProjectid(), updataInfo.getProjecname(),
                                    updataInfo.getStroeid(), updataInfo.getStorename(), updataInfo.getTaskType());
                        }
                        if (issuccess) {
                            updataDBHelper.removeTask(updataInfo.getUniquelyNum(), "");
                        }
                        continue;
                    } else if ("-5".equals(updataInfo.getTaskType())) {//整店上传接口
                        String data = "";
                        try {
                            Map<String, String> parames = updataInfo.getParame();
                            Iterator<String> iterator = parames.keySet().iterator();
                            while (iterator.hasNext()) {
                                String key = iterator.next();
                                if (TextUtils.isEmpty(data)) {
                                    data = key + "=" + URLEncoder.encode(parames.get(key).trim(), "utf-8");
                                } else {
                                    data = data + "&" + key + "=" + URLEncoder.encode(parames.get(key).trim(), "utf-8");
                                }
                            }
                        } catch (UnsupportedEncodingException e) {
                            MobclickAgent.reportError(this, "updataservice UnsupportedEncodingException2:" + e.getMessage());
                            continue;
                        }
                        if (UpdataCompleted2(updataInfo.getUrl(), data, null, null, null, null, null, updataInfo.getTaskType())) {
                            updataDBHelper.removeTask(updataInfo.getUniquelyNum(), "");
                        }
                        continue;
                    } else if (updataInfo.getTaskType().equals("-2")) {//-2要最后执行，不要问为什么！
                        int index = 0;
                        for (UpdataInfo updataInfo1 : list) {
                            if (!updataInfo1.getTaskType().equals("-2") && !updataInfo1.getTaskType().equals("-4")) {
                                index++;
                                break;
                            }
                        }
                        if (index == 0) {
                            if (TextUtils.isEmpty(updataInfo.getCompleted_url())) {
                                updataInfo.setCompleted_url(Urls.Packagecomplete);
                            }
                            Tools.d("完成按钮接口开始执行！----" + updataInfo.getCompleted_url());
                            boolean issuccess = UpdataCompleted2(updataInfo.getCompleted_url(), updataInfo.getCompleted_parameter(),
                                    updataInfo.getUsername(), updataInfo.getProjectid(), updataInfo.getProjecname(),
                                    updataInfo.getStroeid(), updataInfo.getStorename(), updataInfo.getTaskType());
                            if (issuccess) {
                                updataDBHelper.removeTask(updataInfo.getUniquelyNum(), "");
                            }
                        }
                        continue;
                    } else if (updataInfo.getTaskType().equals("-4")) {//上传多余文件
                        int index1 = 0;
                        for (UpdataInfo updataInfo1 : list) {
                            if (!updataInfo1.getTaskType().equals("-4")) {
                                index1++;
                                break;
                            }
                        }
                        if (index1 == 0) {
                            String[] paths = updataInfo.getPaths().split(",");
                            ArrayList<String> fileList = new ArrayList<>();
                            ArrayList<File> tempList = new ArrayList<>();
                            Collections.addAll(fileList, paths);
                            int index = 0;
                            long fileNowNum = 0;
                            int xh = 0;
                            Tools.d("多余文件开始上传----");
                            for (; !fileList.isEmpty() && ("tyly".equals(updataInfo.getTaskType()) || "8-9".equals(updataInfo.getTaskType()) ||
                                    "8-8".equals(updataInfo.getTaskType()) || "3".equals(updataInfo.getTaskType()) || isHavNetwork()); index++) {
                                Tools.d(fileList.size() + "");
                                if (index == fileList.size()) {
                                    xh++;
                                    index = 0;
                                }
                                if (xh > 1) {
                                    break;
                                }
                                Tools.d("index:" + index + " >>>><<<< xh:" + xh);
                                File tempInfo = new File(fileList.get(index));
                                if (!tempInfo.exists() || !tempInfo.isFile()) {
                                    fileList.remove(index);
                                    index--;
                                    continue;
                                }
                                fileNowNum = tempInfo.length();
//                                uniquely_num = updataInfo.getUniquelyNum();
                                boolean isSuccess = OSSMultipartTest(tempInfo.getName(), tempInfo, updataInfo, "");
                                if (isSuccess) {
//                                    fileNowSize = fileNowSize + fileNowNum;
//                                    fileNowNum2 = 0;
                                    tempList.add(tempInfo);
                                    fileList.remove(index);
                                    index--;
                                }
                            }
                            if (fileList.isEmpty()) {
                                updataDBHelper.removeTask(updataInfo.getUniquelyNum(), updataInfo.getPaths());
                                for (File tempInfo : tempList) {
                                    systemDBHelper.deletePicture(tempInfo.getPath());
                                    tempInfo.delete();
                                }
//                                fileNowSize = 0;
//                                fileNowNum2 = 0;
                            }
                        }
                        continue;
                    }
                    String executeid = null;
                    taskNum = 1;//文件区分用，防止时间戳重复
                    if (updataInfo.getIs_completed() == 1) {//先执行执行完成接口
                        executeid = UpdataCompleted(updataInfo.getCompleted_url(), updataInfo.getCompleted_parameter(),
                                updataInfo.getUsername(), updataInfo.getProjectid(), updataInfo.getProjecname(),
                                updataInfo.getStroeid(), updataInfo.getStorename(), updataInfo.getTaskType(), updataInfo.getTaskId());
                        if (!"2-2".equals(updataInfo.getTaskType()) && !"1-1".equals(updataInfo.getTaskType()) && !"3-3".equals
                                (updataInfo.getTaskType())
                                && !"4-4".equals(updataInfo.getTaskType()) && !"5-5".equals(updataInfo.getTaskType()) && !"6"
                                .equals(updataInfo.getTaskType()) &&
                                !"333".equals(updataInfo.getTaskType()) && !"555".equals(updataInfo.getTaskType())) {
                            if ((TextUtils.isEmpty(executeid) || "null".equals(executeid)) && !"01".equals(updataInfo.getTaskType()
                            ) &&
                                    !"02".equals(updataInfo.getTaskType()) && !"03".equals(updataInfo.getTaskType()))
                                continue;
                        }
                    }
                    String filelistStr = "";
                    Map<String, String> parameMap = updataInfo.getParame();//普通参数
                    if (!"2-2".equals(updataInfo.getTaskType()) && !"1-1".equals(updataInfo.getTaskType()) && !"3-3".equals
                            (updataInfo.getTaskType())
                            && !"4-4".equals(updataInfo.getTaskType()) && !"5-5".equals(updataInfo.getTaskType()) && !"6".equals
                            (updataInfo.getTaskType())
                            && !"333".equals(updataInfo.getTaskType()) && !"555".equals(updataInfo.getTaskType())) {
                        if (executeid != null)
                            parameMap.put("executeid", executeid);
                    }
                    ArrayList<TempInfo> fileList = new ArrayList<>();
                    ArrayList<TempInfo> tempList = new ArrayList<>();
                    if (!"01".equals(updataInfo.getTaskType())) {//关闭任务包-仅备注
                        Map<String, String> fileMap = updataInfo.getFileParame();//文件参数
                        Iterator<String> iterator = fileMap.keySet().iterator();
                        task_sum_size = 0;
                        task_sized = 0;
                        long task_sized_save = 0;
                        while (iterator.hasNext()) {
                            String key = iterator.next();
                            File file = new File(fileMap.get(key));
                            Tools.d("文件填充");
                            task_sum_size += file.length();
                            TempInfo tempInfo = new TempInfo();
                            tempInfo.file = file;
                            tempInfo.name = key;
                            fileList.add(tempInfo);
                            Tools.d("文件填充----end");
                        }
                        if (AppInfo.TASKITEMEDIT_TASKTYPE.equals(updataInfo.getTaskType())) {//记录任务有语音题
                            updataInfo.setQuestion_id(parameMap.get("question_ids"));//上传时候用
                        }
                        int index = 0;
                        int xh = 0;
                        File foucesfile = null;
                        String foucesfilepath;
                        for (; !fileList.isEmpty() && ("tyly".equals(updataInfo.getTaskType()) || "8-9".equals(updataInfo.getTaskType()) ||
                                "8-8".equals(updataInfo.getTaskType()) || "3".equals(updataInfo.getTaskType()) || isHavNetwork()); index++) {
                            Tools.d(fileList.size() + "");
                            if (index == fileList.size()) {
                                xh++;
                                index = 0;
                            }
                            if (xh > 1) {
                                break;
                            }
                            Tools.d("index:" + index + " >>>><<<< xh:" + xh);
                            TempInfo tempInfo = fileList.get(index);
                            Tools.d("tempInfo.file:" + tempInfo.file);
                            if (!tempInfo.file.exists() || !tempInfo.file.isFile()) {
                                if (!"8-9".equals(updataInfo.getTaskType())) {
                                    while (!sendPost(updataInfo)) {
                                        try {
                                            Thread.sleep(200);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                fileList.remove(index);
                                index--;
                                continue;
                            }
//                            uniquely_num = updataInfo.getUniquelyNum();
                            updataInfo.setTaskBatch(parameMap.get("batch"));
                            foucesfilepath = systemDBHelper.searchForFoucesPicturepath(tempInfo.file.getPath());
                            boolean isFoucesSuccess = true;
                            Tools.d("tempInfo.file.getPath():" + tempInfo.file.getPath());
                            Tools.d("foucesfilepath:" + foucesfilepath);
                            if (!TextUtils.isEmpty(foucesfilepath)) {
                                Tools.d("发现取证图片");
                                foucesfile = new File(foucesfilepath);
                                if (!foucesfile.exists() || !foucesfile.isFile()) {
                                    Tools.d("文件丢失：" + foucesfilepath);
                                    while (!sendPost(updataInfo)) {
                                        try {
                                            Thread.sleep(200);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    systemDBHelper.updataFoucePicture(tempInfo.file.getPath(), "");//删除取证图片数据
                                } else {
                                    task_sum_size += foucesfile.length();
                                    updataInfo.setTaskTime("Nx_" + updataInfo.getTaskTime());
                                    task_sized_save = task_sized;
                                    isFoucesSuccess = OSSMultipartTest(foucesfile.getName(), foucesfile, updataInfo, executeid);
                                    if (isFoucesSuccess) {
                                        task_sized = task_sized_save + foucesfile.length();
                                        Tools.d("取证图片上传成功");
                                        updataInfo.setTaskTime(updataInfo.getTaskTime().replaceFirst("Nx", "1x"));
                                        systemDBHelper.updataFoucePicture(tempInfo.file.getPath(), "");//上传成功删除取证图片，防止重复上传
                                        foucesfile.delete();
                                    } else {
                                        task_sized = task_sized_save;
                                        Bundle bundle = new Bundle();
                                        bundle.putString("uniquelynum", updataInfo.getUniquelyNum());
                                        bundle.putInt("size", getNowTaskPER());//任务进度
                                        sendProgress("updatafile", bundle);
                                    }
                                }
                                foucesfilepath = null;
                            }
                            Tools.d("isFoucesSuccess:" + isFoucesSuccess);
                            if (isFoucesSuccess) {//取证图片上传成功或者没有取证图片才传正常图片
                                task_sized_save = task_sized;
                                boolean isSuccess = OSSMultipartTest(tempInfo.file.getName(), tempInfo.file, updataInfo, executeid);
                                Tools.d("OSSMultipartTest:" + isSuccess);
                                if (isSuccess) {
                                    task_sized = task_sized_save + tempInfo.file.length();
                                    if (TextUtils.isEmpty(filelistStr)) {
                                        if ("8-9".equals(updataInfo.getTaskType())) {
                                            filelistStr = Urls.Endpoint2 + "/" + tempInfo.file.getName() + ".jpg";
                                        } else {
                                            filelistStr = Urls.Endpoint2 + "/" + tempInfo.file.getName();
                                        }
                                    } else {
                                        if ("8-9".equals(updataInfo.getTaskType())) {
                                            filelistStr = filelistStr + "&&" + Urls.Endpoint2 + "/" + tempInfo.file.getName() + ".jpg";
                                        } else {

                                            filelistStr = filelistStr + "&&" + Urls.Endpoint2 + "/" + tempInfo.file.getName();
                                        }
                                    }
                                    tempList.add(tempInfo);
                                    fileList.remove(index);
                                    index--;
                                } else {
                                    task_sized = task_sized_save;
                                    Bundle bundle = new Bundle();
                                    bundle.putString("uniquelynum", updataInfo.getUniquelyNum());
                                    bundle.putInt("size", getNowTaskPER());//任务进度
                                    sendProgress("updatafile", bundle);
                                }
                                Tools.d(tempInfo.file.getName() + "文件名--------" + tempInfo.name);
                                Tools.d("文件OSS：" + Urls.Endpoint2 + "/" + tempInfo.file.getName());
                                Tools.d("文件path：" + tempInfo.file.getPath());
                                if ("11".equals(updataInfo.getTaskType()) || "22".equals(updataInfo.getTaskType()) || "jcly".equals(updataInfo.getTaskType())) {
                                    appDBHelper.setPhotoUrl(Urls.Endpoint2 + "/" + tempInfo.file.getName(), tempInfo.file.getPath());
                                }
                            }
                        }
                        if (updataInfo.getTaskType().equals("555")) {
                            parameMap.put("fileurl", filelistStr);
                        } else {
                            parameMap.put("filelist", filelistStr);
                        }
                    }
                    if (fileList.isEmpty()) {
                        Tools.d("updataInfo.getTaskType():" + updataInfo.getTaskType());
                        if ("wxsp".equals(updataInfo.getTaskType()) || "wxly".equals(updataInfo.getTaskType())
                                || "typz".equals(updataInfo.getTaskType()) || "tyly".equals(updataInfo.getTaskType())
                                || "11".equals(updataInfo.getTaskType()) || "22".equals(updataInfo.getTaskType())
                                || "jcly".equals(updataInfo.getTaskType())) {//暗访录音和视频任务没有资料完成
                            if (!tempList.isEmpty()) {
                                updataDBHelper.removeTask(updataInfo.getUniquelyNum(), updataInfo.getPaths());
                                if (!"jcly".equals(updataInfo.getTaskType())) {
                                    if (!"tyly".equals(updataInfo.getTaskType()) && !"typz".equals(updataInfo.getTaskType())) {
                                        for (TempInfo tempInfo : tempList) {
                                            tempInfo.file.delete();
                                        }
                                    } else {
                                        for (TempInfo tempInfo : tempList) {
                                            deleteFileList.add(tempInfo.file);
                                        }
                                    }
                                }
                            } else {
                                MobclickAgent.reportError(this, "Updataservice tempList is null! taskid:" + updataInfo.getTaskId() +
                                        ",packageid:" + updataInfo.getPackageId() + ",storeid:" + updataInfo.getStroeid() + "," +
                                        "projectid:" + updataInfo.getProjectid() + ",paths:" + updataInfo.getPaths());
                                updataDBHelper.removeTask(updataInfo.getUniquelyNum(), updataInfo.getPaths());
                            }
                        } else {
                            if (TextUtils.isEmpty(filelistStr) && !"01".equals(updataInfo.getTaskType())) {
                                MobclickAgent.reportError(this, "Updataservice filelist is null! taskid:" + updataInfo.getTaskId() +
                                        ",packageid:" + updataInfo.getPackageId() + ",storeid:" + updataInfo.getStroeid() + "," +
                                        "projectid:" + updataInfo.getProjectid() + ",paths:" + updataInfo.getPaths());
                                updataDBHelper.removeTask(updataInfo.getUniquelyNum(), updataInfo.getPaths());
                            } else {
                                Tools.d("filelist:" + filelistStr);
                                boolean isSuccess = UpdataEnd(updataInfo.getUrl(), parameMap, updataInfo.getUsername(),
                                        updataInfo.getProjectid(), updataInfo.getProjecname(),
                                        updataInfo.getStroeid(), updataInfo.getStorename(), updataInfo.getTaskType());
                                if (isSuccess) {
                                    if ("01".equals(updataInfo.getTaskType())) {
                                        updataDBHelper.removeTaskForClose01(updataInfo.getUniquelyNum());
                                    } else {
                                        if ("tyjt".equals(updataInfo.getTaskType())) {
                                            systemDBHelper.deleteWebpagephoto(updataInfo.getProjectid(),
                                                    updataInfo.getStroeid(), updataInfo.getTaskId(), updataInfo.getTaskBatch()
                                                    , updataInfo.getUsername());
                                        }
                                        updataDBHelper.removeTask(updataInfo.getUniquelyNum(), updataInfo.getPaths());
                                        Tools.d("——————————————>>>>" + updataInfo.getTaskName());
                                        for (TempInfo tempInfo : tempList) {
                                            systemDBHelper.deletePicture(tempInfo.file.getPath());
                                            tempInfo.file.delete();
                                        }
                                    }
                                }
                            }
                        }
                        task_sized = 0;
                        task_sum_size = 0;
                    }
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (size == 0 && thread != null && !deleteFileList.isEmpty() && isEnableDelete) {
            for (File temp : deleteFileList) {
                temp.delete();
            }
        }
        stopSelf();
    }

    //    private static long fileSumSize;//任务所有文件大小/当前文件大小
//    private static long fileNowNum2;//当前传的大小
//    private static long fileNowSize;//当前传过的文件总大小
////    private static String uniquely_num;//当前任务唯一标识码
//
    public int getNowTaskPER() {
        try {
            int r = (int) (task_sized * 1d / task_sum_size * 100);
            if (r > 100) {
                r = 100;
            }
            return r;
        } catch (Exception e) {
            return 0;
        }
    }

    public int getNowFilePER() {
        try {
            int r = (int) (file_sized * 1d / file_sum_size * 100);
            if (r > 100) {
                r = 100;
            }
            return r;
        } catch (Exception e) {
            return 0;
        }
    }
//    public static String getUniquely_num() {
//        return uniquely_num;
//    }

    class TempInfo {
        File file;
        String name;
    }

    /**
     * 设置httpurlconnection
     *
     * @param httpURLConnection
     * @param timeout
     * @throws ProtocolException
     */
    private void settingHttpURLConnection(HttpURLConnection httpURLConnection, int timeout) throws ProtocolException {
        httpURLConnection.setChunkedStreamingMode(1280 * 1024);// 128K
        httpURLConnection.setConnectTimeout(timeout);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
    }

    private void settingHttpURLConnection(HttpURLConnection httpURLConnection) throws ProtocolException {
        settingHttpURLConnection(httpURLConnection, 10000);
    }

    /**
     * 文件丢失通知后台
     *
     * @param updataInfo
     * @return
     */
    private boolean sendPost(UpdataInfo updataInfo) {
        boolean returnvalue = false;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        OutputStream os = null;
        URL url;
        HttpURLConnection httpURLConnection = null;
        try {
            url = new URL(Urls.FileLost);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            settingHttpURLConnection(httpURLConnection);
            String data;
            data = "storeid=" + URLEncoder.encode(updataInfo.getStroeid(), "utf-8") +
                    "&taskid=" + URLEncoder.encode(updataInfo.getTaskId(), "utf-8") +
                    "&usermobile=" + URLEncoder.encode(updataInfo.getUsername(), "utf-8") +
                    "&projectid=" + URLEncoder.encode(updataInfo.getProjectid(), "utf-8");
            os = httpURLConnection.getOutputStream();
            byte[] bs = data.getBytes("UTF-8");
            os.write(bs);
            os.flush();
            os.close();
            os = null;
            is = httpURLConnection.getInputStream();
            isr = new InputStreamReader(is, "utf-8");
            br = new BufferedReader(isr);
            String result = br.readLine();
            Tools.d(result);
            JSONObject jsonObject = new JSONObject(result);
            int code = jsonObject.getInt("code");
//            if (code == 200 || code == 2) {
            returnvalue = true;
//            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (isr != null)
                    isr.close();
                if (is != null)
                    is.close();
                if (os != null)
                    os.close();
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return returnvalue;
    }

    private String UpdataCompleted(String urlString, String parames, String username, String projectid, String projectname,
                                   String storeid, String storename, String tasktype, String taskid) {
        Tools.d("UpdataCompleted:tasktype:" + tasktype + ",storeid:" + storeid + ",storename" + storename);
        if (!("tyly".equals(tasktype) || "8-9".equals(tasktype) ||
                "8-8".equals(tasktype) || "3".equals(tasktype) || isHavNetwork())) {
            return null;
        }
        String executeid = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        OutputStream os = null;
        URL url;
        HttpURLConnection httpURLConnection = null;
        try {
            url = new URL(urlString);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            settingHttpURLConnection(httpURLConnection);
            String data;
            try {
                String versionnum;
                try {
                    versionnum = Tools.getVersionName(this);
                } catch (PackageManager.NameNotFoundException e) {
                    versionnum = "not found";
                }
                data = "&comname=" + URLEncoder.encode(Tools.getDeviceType(), "utf-8") +
                        "&phonemodle=" + URLEncoder.encode(Tools.getDeviceModel(), "utf-8") +
                        "&sysversion=" + URLEncoder.encode(Tools.getSystemVersion() + "", "utf-8") +
                        "&operator=" + URLEncoder.encode(Tools.getCarrieroperator(this), "utf-8") +
                        "&mac=" + URLEncoder.encode(Tools.getLocalMacAddress(this), "utf-8") +
                        "&versionnum=" + URLEncoder.encode(versionnum, "utf-8") +
                        "&resolution=" + URLEncoder.encode(Tools.getScreeInfoWidth(this) + "*" + Tools.getScreeInfoHeight(this),
                        "utf-8") +
                        "&name=" + URLEncoder.encode(getResources().getString(R.string.app_name), "utf-8") +
                        "&newusermobile=" + URLEncoder.encode(AppInfo.getName(this), "utf-8") +
                        "&imei=" + URLEncoder.encode(Tools.getDeviceId(this), "utf-8");
            } catch (Exception e) {
                data = "";
            }
            try {
                URLDecoder.decode(parames, "utf-8");
            } catch (UnsupportedEncodingException e) {
                parames = parames.replaceAll("%", "%25");
            }
            parames += data;
            Tools.d(parames);
            Log.i("------------parames", parames);
            os = httpURLConnection.getOutputStream();
            byte[] bs = parames.getBytes("UTF-8");
            long size = bs.length;
            os.write(bs);
            os.flush();
            os.close();
            os = null;
            is = httpURLConnection.getInputStream();//之后不打印了
            isr = new InputStreamReader(is, "utf-8");
            br = new BufferedReader(isr);
            String result = br.readLine();
            Tools.d(result + "=======" + url);//执行完成
            JSONObject jsonObject = new JSONObject(result);
            if ("200".equals(jsonObject.getString("code")) || "2".equals(jsonObject.getString("code"))) {
                executeid = jsonObject.optString("executeid");
                offlineDBHelper.upTrafficSum(username, projectid, projectname, storeid, storename, size);
                Intent intent = new Intent();
                intent.setAction(AppInfo.BroadcastReceiver_TAKEPHOTO);
                intent.putExtra("type", "2");//资料回收完成 可关闭页面
                sendBroadcast(intent);
                appDBHelper.deletePhotoUrl(projectid, storeid, taskid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (isr != null)
                    isr.close();
                if (is != null)
                    is.close();
                if (os != null)
                    os.close();
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return executeid;
    }

    private boolean UpdataCompleted2(String urlString, String parames, String username, String projectid, String projectname,
                                     String storeid, String storename, String tasktype) {
        if (!("tyly".equals(tasktype) || "8-9".equals(tasktype) ||
                "8-8".equals(tasktype) || "3".equals(tasktype) || isHavNetwork())) {
            return false;
        }
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        OutputStream os = null;
        URL url;
        HttpURLConnection httpURLConnection = null;
        boolean returnvalue = false;
        try {
            url = new URL(urlString);//
            httpURLConnection = (HttpURLConnection) url.openConnection();
            settingHttpURLConnection(httpURLConnection);
            String data;
            try {
                String versionnum;
                try {
                    versionnum = Tools.getVersionName(this);
                } catch (PackageManager.NameNotFoundException e) {
                    versionnum = "not found";
                }
                data = "&comname=" + URLEncoder.encode(Tools.getDeviceType(), "utf-8") +
                        "&phonemodle=" + URLEncoder.encode(Tools.getDeviceModel(), "utf-8") +
                        "&sysversion=" + URLEncoder.encode(Tools.getSystemVersion() + "", "utf-8") +
                        "&operator=" + URLEncoder.encode(Tools.getCarrieroperator(this), "utf-8") +
                        "&mac=" + URLEncoder.encode(Tools.getLocalMacAddress(this), "utf-8") +
                        "&versionnum=" + URLEncoder.encode(versionnum, "utf-8") +
                        "&resolution=" + URLEncoder.encode(Tools.getScreeInfoWidth(this) + "*" + Tools.getScreeInfoHeight(this),
                        "utf-8") +
                        "&name=" + URLEncoder.encode(getResources().getString(R.string.app_name), "utf-8") +
                        "&newusermobile=" + URLEncoder.encode(AppInfo.getName(this), "utf-8") +
                        "&imei=" + URLEncoder.encode(Tools.getDeviceId(this), "utf-8");
            } catch (Exception e) {
                data = "";
            }
            parames += data;
            Tools.d(parames);
//            Log.i("--------params", parames);
            os = httpURLConnection.getOutputStream();
            byte[] bs = parames.getBytes("UTF-8");
            long size = bs.length;
            os.write(bs);
            os.flush();
            os.close();
            os = null;
            is = httpURLConnection.getInputStream();
            isr = new InputStreamReader(is, "utf-8");
            br = new BufferedReader(isr);
            String result = br.readLine();
            Tools.d(result);
            Log.i("-----------result2", result + "==" + urlString);
            JSONObject jsonObject = new JSONObject(result);
            if ("200".equals(jsonObject.getString("code")) || "2".equals(jsonObject.getString("code"))) {
                returnvalue = true;
                if (username != null)
                    offlineDBHelper.upTrafficSum(username, projectid, projectname, storeid, storename, size);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (isr != null)
                    isr.close();
                if (is != null)
                    is.close();
                if (os != null)
                    os.close();
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return returnvalue;
    }

    private boolean isEnableDelete;

    private boolean UpdataEnd(String urlString, Map<String, String> parames, String username, String projectid,
                              String projectname, String storeid, String storename, String tasktype) {
        if (!("tyly".equals(tasktype) || "8-9".equals(tasktype) ||
                "8-8".equals(tasktype) || "3".equals(tasktype) || isHavNetwork())) {
            return false;
        }
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        OutputStream os = null;
        URL url;
        HttpURLConnection httpURLConnection = null;
        boolean returnvalue = false;
        try {
            url = new URL(urlString);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            settingHttpURLConnection(httpURLConnection, 120000);
            String data = "";
            Iterator<String> iterator = parames.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                Tools.d(key);
                if (TextUtils.isEmpty(data)) {
                    data = key + "=" + URLEncoder.encode(parames.get(key).trim(), "utf-8");
                } else {
                    data = data + "&" + key + "=" + URLEncoder.encode(parames.get(key).trim(), "utf-8");
                }
            }
            String data2;
            try {
                String versionnum;
                try {
                    versionnum = Tools.getVersionName(this);
                } catch (PackageManager.NameNotFoundException e) {
                    versionnum = "not found";
                }
                data2 = "&comname=" + URLEncoder.encode(Tools.getDeviceType(), "utf-8") +
                        "&phonemodle=" + URLEncoder.encode(Tools.getDeviceModel(), "utf-8") +
                        "&sysversion=" + URLEncoder.encode(Tools.getSystemVersion() + "", "utf-8") +
                        "&operator=" + URLEncoder.encode(Tools.getCarrieroperator(this), "utf-8") +
                        "&mac=" + URLEncoder.encode(Tools.getLocalMacAddress(this), "utf-8") +
                        "&versionnum=" + URLEncoder.encode(versionnum, "utf-8") +
                        "&resolution=" + URLEncoder.encode(Tools.getScreeInfoWidth(this) + "*" + Tools.getScreeInfoHeight(this),
                        "utf-8") +
                        "&name=" + URLEncoder.encode(getResources().getString(R.string.app_name), "utf-8") +
                        "&newusermobile=" + URLEncoder.encode(AppInfo.getName(this), "utf-8") +
                        "&imei=" + URLEncoder.encode(Tools.getDeviceId(this), "utf-8");
            } catch (Exception e) {
                data2 = "";
            }
            data += data2;
            Tools.d(data);
            os = httpURLConnection.getOutputStream();
            byte[] bs = data.getBytes("UTF-8");
            int size = bs.length;
            os.write(bs);
            os.flush();
            os.close();
            os = null;
            is = httpURLConnection.getInputStream();
            isr = new InputStreamReader(is, "utf-8");
            br = new BufferedReader(isr);
            String result = br.readLine();
            Tools.d(result);//资料回收完成
            Log.i("-----------result3", result + "==" + urlString);
            JSONObject jsonObject = new JSONObject(result);
            if ("200".equals(jsonObject.getString("code")) || "2".equals(jsonObject.getString("code"))) {
                if ("8-9".equals(tasktype)) {
                    isEnableDelete = true;
                }
                if ("-3".equals(tasktype)) {
                    Intent in = new Intent();
                    in.setAction(AppInfo.BroadcastReceiver_TAKEPHOTO);
                    in.putExtra("isSuccess", true);
                    in.putExtra("tasktype", tasktype);
                    in.putExtra("storeid", storeid);
                    sendBroadcast(in);
                }
                returnvalue = true;
                offlineDBHelper.upTrafficSum(username, projectid, projectname, storeid, storename, size);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (isr != null)
                    isr.close();
                if (is != null)
                    is.close();
                if (os != null)
                    os.close();
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return returnvalue;
    }

    public void onDestroy() {
        super.onDestroy();
        thread = null;
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        Intent intent = new Intent("service.UpdataService.destory");
        intent.setClass(this, SinoReceiver.class);
        sendBroadcast(intent);
        stopForeground(false);
    }

    private CharSequence contentTitle = "检测中...";
    private CharSequence contentText = "";

    private void startNotification() {
        Notification notification;
        int icon = R.mipmap.ic_launcher;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            CharSequence tickerText = this.getResources().getString(R.string.app_name);
            Notification.Builder builder = new Notification.Builder(this).setTicker(tickerText).setSmallIcon(icon);
            Intent i = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            notification = builder.setContentIntent(pendingIntent).setContentTitle(contentTitle).setContentText
                    (contentText).build();
            notification.flags = Notification.FLAG_NO_CLEAR;
            startForeground(2016, notification);
        } else {
            CharSequence tickerText = this.getResources().getString(R.string.app_name);
            Notification.Builder builder = new Notification.Builder(this).setTicker(tickerText).setSmallIcon(icon);
            builder.setContentTitle(contentTitle);
            builder.setContentText(contentText);
            notification = builder.getNotification();
            notification.flags = Notification.FLAG_NO_CLEAR;
            startForeground(2016, notification);
        }
    }

    private SharedPreferences sharedPreferences;
    private OSS oss = null;
    private static int taskNum = 1;

    /**
     * OSS分片上传
     */
    private boolean OSSMultipartTest(String objectKey, final File file, final UpdataInfo updataInfo, String executeid) {
        file_sum_size = 0;
        file_sized = 0;
        if (!("tyly".equals(updataInfo.getTaskType()) || "8-9".equals(updataInfo.getTaskType()) ||
                "8-8".equals(updataInfo.getTaskType()) || "3".equals(updataInfo.getTaskType()) || isHavNetwork())) {
            return false;
        }
        if (TextUtils.isEmpty(executeid)) {
            executeid = "";
        }
        final String OTemp = objectKey;
        boolean isPng = objectKey.endsWith(".ouye");
        objectKey = Urls.EndpointDir + "/" + objectKey;
        if ("8-9".equals(updataInfo.getTaskType())) {
            objectKey = objectKey + ".jpg";
        }
        Tools.d("objectKey:" + objectKey);
        boolean returnResult = false;
        if (oss == null) {
            OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider("YiMYmCHzNNO8k2l8",
                    "oPDfExOB5wAgmT0LHRA35Ts1tGzfjH");//测试
//            OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider("VuIq42mFfoyCvKNi",
//                    "BEiKNEaKzlI5k7EjIs9KuJG7Dpix5r");
            oss = new OSSClient(getApplicationContext(), Urls.Endpoint, credentialProvider);
        }
        try {
            String uploadId;
            if (sharedPreferences == null) {
                sharedPreferences = getSharedPreferences("OSSUpload_uploadId", MODE_PRIVATE);
            }
            final String username = updataInfo.getUsername();
            final String pid = updataInfo.getPackageId();
            final String storeid = updataInfo.getStroeid();
            String ossname = file.getName();
            final String executeid_str = executeid;
            final String taskid = updataInfo.getTaskId();
            final String taskbatch = updataInfo.getTaskBatch();
            final String projectid = updataInfo.getProjectid();
            final String filetype;
            if ("wxsp".equals(updataInfo.getTaskType())) {//暗访视频
                filetype = "1";
            } else if ("typz".equals(updataInfo.getTaskType())) {//体验拍照
                filetype = "3";
            } else {
                filetype = "2";
            }
            final int filenum = updataInfo.getFileNum();
            final String showname;
            String shownameStr;
            if ("".equals(pid) || "".equals(updataInfo.getPackageName())) {
                shownameStr = updataInfo.getProjecname() + "_" +
                        updataInfo.getStorename().replaceFirst(" ", "_") + "_" +

                        updataInfo.getTaskName() + "_" + taskNum++ + "_" + updataInfo.getTaskTime() +
                        ossname.substring(ossname.lastIndexOf("."));
            } else {
                shownameStr = updataInfo.getProjecname() + "_" +
                        updataInfo.getStorename().replaceFirst(" ", "_") + "_" + updataInfo.getPackageName() + "_" +
                        updataInfo.getTaskName() + "_" + taskNum++ + "_" + updataInfo.getTaskTime() +
                        ossname.substring(ossname.lastIndexOf("."));
            }
            if ("8-9".equals(updataInfo.getTaskType())) {
                ossname = ossname + ".jpg";
            }
            if (!TextUtils.isEmpty(updataInfo.getCode()) && !TextUtils.isEmpty(updataInfo.getBrand())) {
                showname = shownameStr.replaceAll(updataInfo.getCode(), updataInfo.getBrand());
            } else {
                showname = shownameStr;
            }
            Tools.d("name:" + showname);
            final String fileurl = objectKey;
            final String isinvalid;
            String tasktype = updataInfo.getTaskType();//tasktype为"333"时为招募问卷
            if (tasktype.equals("11")) {
                long[] ls = appDBHelper.getAllFilesumsize(updataInfo.getUsername(), updataInfo.getProjectid(), updataInfo.getStroeid(), updataInfo.getTaskId());
                task_sum_size = ls[0];
                task_sized = ls[1];
            }
            //值为0时为不置无效，1为拍照任务置无效，2为任务包置无效,3为全程录音,4为多余文件
            if ("1".equals(tasktype) && UpdataDBHelper.Updata_file_type_video.equals(updataInfo.getFileType())) {//拍照任务置无效
                isinvalid = "1";
            } else if ("0".equals(tasktype) || "01".equals(tasktype) || "02".equals(tasktype) || "03".equals(tasktype)) {//任务包置无效
                isinvalid = "2";
            } else if ("-3".equals(tasktype)) {//全程录音
                isinvalid = "3";
            } else if ("-4".equals(tasktype)) {//多余文件
                isinvalid = "4";
            } else if ("333".equals(tasktype) || "555".equals(tasktype)) {//招募问卷
                isinvalid = "5";
            } else if ("8-9".equals(tasktype)) {//体验评价分享上传图片回调接口调用insertfileinfo接口isinvalid传6
                isinvalid = "6";
            } else {//不置无效任务
                isinvalid = "0";
            }
            Tools.d("isinvalid:" + isinvalid);
            if (!("tyly".equals(updataInfo.getTaskType()) || "8-9".equals(updataInfo.getTaskType()) ||
                    "8-8".equals(updataInfo.getTaskType()) || "3".equals(updataInfo.getTaskType()) || isHavNetwork())) {
                return false;
            }
            try {
                if (oss.doesObjectExist(Urls.BucketName, objectKey)) {//文件已存在
                    String url;
                    if ("wxsp".equals(updataInfo.getTaskType()) || "wxly".equals(updataInfo.getTaskType())) {
                        url = Urls.InsertfileinfoForblack;
                    } else if ("typz".equals(updataInfo.getTaskType()) || "tyly".equals(updataInfo.getTaskType())) {
                        url = Urls.ExperienceInsertFileInfo;
                    } else if ("tyjt".equals(updataInfo.getTaskType())) {
                        url = Urls.CallbackExperienceFileInfo;
                    } else {
                        url = Urls.Insertfileinfo;
                    }
                    String callbackBody = "usermobile=" + URLEncoder.encode(username, "utf-8")
                            + "&pid=" + URLEncoder.encode(pid, "utf-8") +
                            "&storeid=" + URLEncoder.encode(storeid, "utf-8")
                            + "&ossname=" + URLEncoder.encode(ossname, "utf-8") +
                            "&showname=" + URLEncoder.encode(showname, "utf-8") +
                            "&fileurl=" + URLEncoder.encode(fileurl, "utf-8") +
                            "&isinvalid=" + URLEncoder.encode(isinvalid, "utf-8") +
                            "&executeid=" + URLEncoder.encode(executeid_str, "utf-8") +
                            "&filetype=" + URLEncoder.encode(filetype, "utf-8") +
                            "&filenum=" + filenum
                            + "&taskid=" + URLEncoder.encode(taskid, "utf-8") +
                            "&taskbatch=" + URLEncoder.encode(taskbatch, "utf-8") +
                            "&projectid=" + URLEncoder.encode(projectid, "utf-8")
                            + "&task_id=" + URLEncoder.encode(taskid, "utf-8") +
                            "&task_batch=" + URLEncoder.encode(taskbatch, "utf-8") +
                            "&project_id=" + URLEncoder.encode(projectid, "utf-8");
                    if ("22".equals(updataInfo.getTaskType())) {
                        String taskScanId = updataInfo.getBrand();
                        callbackBody += "&taskScanId=" + URLEncoder.encode(taskScanId, "utf-8");
                    } else if ("tyjt".equals(updataInfo.getTaskType())) {//体验截图
                        WebpagetaskDBInfo webpagetaskDBInfo = systemDBHelper.getWebpageInfo(updataInfo.getProjectid(),
                                updataInfo.getStroeid(), updataInfo.getTaskId(), updataInfo.getTaskBatch(),
                                updataInfo.getUsername(), file.getPath());
                        String str = "&comment_type=" + URLEncoder.encode(webpagetaskDBInfo.getCommentState(), "utf-8")
                                + "&comment_content=" + URLEncoder.encode(webpagetaskDBInfo.getCommentTxt(), "utf-8")
                                + "&store_title=" + URLEncoder.encode(webpagetaskDBInfo.getWebName(), "utf-8")
                                + "&store_url=" + URLEncoder.encode(webpagetaskDBInfo.getWebUrl(), "utf-8")
                                + "&praise=" + URLEncoder.encode(webpagetaskDBInfo.getIspraise(), "utf-8");
                        callbackBody += str;
                    }
                    String question_id = "";
                    if (AppInfo.TASKITEMEDIT_TASKTYPE.equals(updataInfo.getTaskType())) {
                        String[] qids = updataInfo.getQuestion_id().split(";");
                        for (String str : qids) {
                            if (str.substring(str.lastIndexOf("/"), str.length()).equals(OTemp)) {
                                question_id = str.substring(0, str.indexOf(","));
                            }
                        }
                        callbackBody += "&questionId" + URLEncoder.encode(question_id, "utf-8");
                    }
                    boolean isSuccess = sendOSSCallback(url, callbackBody);
                    if (isSuccess) {
                        Tools.d("文件已存在");
                        return true;
                    }
                }
            } catch (Exception e) {
                if (e instanceof ServiceException) {
                    MobclickAgent.reportError(this, "Updataservice doesObjectExist error:" + ((ServiceException) e)
                            .toString());
                } else {
                    MobclickAgent.reportError(this, "Updataservice doesObjectExist error0");
                }
            }
            uploadId = sharedPreferences.getString(Urls.BucketName + "/" + objectKey, "");
            Tools.d("uploadId:" + uploadId);
            if (TextUtils.isEmpty(uploadId)) {
                InitiateMultipartUploadRequest init = new InitiateMultipartUploadRequest(Urls.BucketName, objectKey);
                InitiateMultipartUploadResult initResult = oss.initMultipartUpload(init);
                uploadId = initResult.getUploadId();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Urls.BucketName + "/" + objectKey, uploadId);
                editor.apply();
            }
            Tools.d("uploadId:" + uploadId);
            ListPartsRequest listParts = new ListPartsRequest(Urls.BucketName, objectKey, uploadId);//获取已上传的分片列表
            ListPartsResult result = oss.listParts(listParts);
            long partSize = 128 * 1024; // 设置分片大小
            int currentIndex = result.getParts().size(); // 上传分片编号，从1开始
            if (currentIndex < 1) {
                currentIndex = 1;
            }
            file_sized = (currentIndex - 1) * partSize;//获取已经上传大小 ... /_\!
            Tools.d(currentIndex + "");
            InputStream input = new FileInputStream(file);
            long fileLength = file_sum_size = file.length();
            Tools.d("文件长度：" + file.length());
            long uploadedLength = 0;
            List<PartETag> partETags = new ArrayList<PartETag>(); // 保存分片上传的结果
            boolean isHaveNetwork = ("tyly".equals(updataInfo.getTaskType()) || "8-9".equals(updataInfo.getTaskType()) ||
                    "8-8".equals(updataInfo.getTaskType()) || "3".equals(updataInfo.getTaskType()) || isHavNetwork());
//            boolean isSend = isPng || file.getName().endsWith(".mp4");
            String thumbnailPath = systemDBHelper.searchForThumbnailPath(file.getPath());
            if (TextUtils.isEmpty(thumbnailPath)) {
                thumbnailPath = file.getPath();
                thumbnailPath = thumbnailPath.replaceAll(".ouye", "_2.ouye");
            }
            boolean isgoon = false;
            while (uploadedLength < fileLength && isHaveNetwork) {
                Tools.d("while");
                int partLength = (int) Math.min(partSize, fileLength - uploadedLength);
                byte[] partData = IOUtils.readStreamAsBytesArray(input, partLength); // 按照分片大小读取文件的一段内容
                if (isPng) {//解密
                    for (int i = 0; i < partData.length; i++) {
                        partData[i] = (byte) (255 - partData[i]);
                    }
                }
                UploadPartRequest uploadPart = new UploadPartRequest(Urls.BucketName, objectKey, uploadId, currentIndex);
                uploadPart.setPartContent(partData); // 设置分片内容
                UploadPartResult uploadPartResult = oss.uploadPart(uploadPart);
                partETags.add(new PartETag(currentIndex, uploadPartResult.getETag())); // 保存分片上传成功后的结果
                if (uploadedLength < file_sized) {
                    isgoon = true;
                    file_sized += 2048;
                    task_sized += 2048;
                } else {
                    if (isgoon) {
                        isgoon = false;
                        task_sized += uploadedLength - file_sized;
                        file_sized = uploadedLength;
                    }
                    file_sized += partLength;
                    task_sized += partLength;
                }
                if (!"4".equals(tasktype)) {//多余文件上传不发
                    if (tasktype.equals("11")) {
                        long[] ls = appDBHelper.getAllFilesumsize(updataInfo.getUsername(), updataInfo.getProjectid(), updataInfo.getStroeid(), updataInfo.getTaskId());
                        task_sum_size = ls[0];
                    }
                    int gtp = getNowTaskPER();
                    int ftp = getNowFilePER();
                    Tools.d(gtp + "% ===----- " + ftp + "% === " + updataInfo.getUniquelyNum() + " -- " + file_sum_size + "," + file_sized + " --- " + task_sum_size + " -- " + task_sized);
                    boolean isSuccess = false;
                    if ("-3".equals(tasktype)) {//全程录音是否上传成功（只用于待提交列表）
                        if (gtp == 100) {
                            isSuccess = true;
                        } else {
                            isSuccess = false;
                        }
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("uniquelynum", updataInfo.getUniquelyNum());
                    bundle.putInt("size", gtp);//任务进度task
                    bundle.putString("username", updataInfo.getUsername());
                    bundle.putString("projectid", updataInfo.getProjectid());
                    bundle.putString("storeid", updataInfo.getStroeid());
                    bundle.putString("taskpackid", updataInfo.getPackageId());
                    bundle.putString("taskid", updataInfo.getTaskId());
                    bundle.putString("type", "1");//可更新UI
                    bundle.putString("rate", ftp + "");//文件进度
                    bundle.putString("thumbnailPath", thumbnailPath);
                    bundle.putString("path", file.getPath());
                    sendProgress(null, bundle);
                }
                uploadedLength += partLength;
                currentIndex++;
                isHaveNetwork = ("tyly".equals(updataInfo.getTaskType()) || "8-9".equals(updataInfo.getTaskType()) ||
                        "8-8".equals(updataInfo.getTaskType()) || "3".equals(updataInfo.getTaskType()) || isHavNetwork());
            }
            Tools.d("parts upload end-----" + executeid_str);
            if (isHaveNetwork) {
                CompleteMultipartUploadRequest complete = new CompleteMultipartUploadRequest(Urls.BucketName, objectKey,
                        uploadId, partETags);
                final String finalOssname = ossname;
                complete.setCallbackParam(new HashMap<String, String>() {
                    {
                        Tools.d("资料回传：" + updataInfo.getTaskType());
                        if ("wxsp".equals(updataInfo.getTaskType()) || "wxly".equals(updataInfo.getTaskType())) {
                            put("callbackUrl", Urls.InsertfileinfoForblack);
                        } else if ("typz".equals(updataInfo.getTaskType()) || "tyly".equals(updataInfo.getTaskType())) {
                            Tools.d("体验拍照录音资料回调");
                            put("callbackUrl", Urls.ExperienceInsertFileInfo);
                        } else if ("tyjt".equals(updataInfo.getTaskType())) {
                            Tools.d("在线体验任务截图上传回调");
                            put("callbackUrl", Urls.CallbackExperienceFileInfo);
                        } else {
                            Tools.d("普通资料回调");
                            put("callbackUrl", Urls.Insertfileinfo);
                        }
                        String callbackBody = "usermobile=" + username + "&pid=" + pid + "&storeid=" + storeid
                                + "&ossname=" + finalOssname + "&showname=" + URLEncoder.encode(showname, "utf-8") +
                                "&fileurl=" + fileurl + "&isinvalid=" + isinvalid + "&executeid=" + executeid_str +
                                "&filetype=" + filetype + "&filenum=" + filenum
                                + "&taskid=" + taskid + "&task_id=" + taskid + "&taskbatch=" + taskbatch +
                                "&task_batch=" + taskbatch + "&projectid=" + projectid + "&project_id=" + projectid;
                        if ("22".equals(updataInfo.getTaskType())) {
                            String taskScanId = updataInfo.getBrand();
                            Tools.d("扫码任务多传的taskScanId：" + taskScanId);
                            callbackBody += "&taskScanId=" + taskScanId;
                        } else if ("tyjt".equals(updataInfo.getTaskType())) {//体验截图
                            Tools.d("体验截图多上传的参数");//替代参数说明在ScreenshotActivity sendData()方法说明
                            WebpagetaskDBInfo webpagetaskDBInfo = systemDBHelper.getWebpageInfo(updataInfo.getProjectid(),
                                    updataInfo.getStroeid(), updataInfo.getTaskId(), updataInfo.getTaskBatch(),
                                    updataInfo.getUsername(), file.getPath());
                            String str = "&comment_type=" + webpagetaskDBInfo.getCommentState()
                                    + "&comment_content=" + webpagetaskDBInfo.getCommentTxt()
                                    + "&store_title=" + webpagetaskDBInfo.getWebName()
                                    + "&store_url=" + webpagetaskDBInfo.getWebUrl()
                                    + "&praise=" + webpagetaskDBInfo.getIspraise();
                            callbackBody += str;
                        }
                        String question_id = "";
                        if (AppInfo.TASKITEMEDIT_TASKTYPE.equals(updataInfo.getTaskType())) {
                            String[] qids = updataInfo.getQuestion_id().split(";");
                            for (String str : qids) {
                                if (str.substring(str.lastIndexOf("/"), str.length()).equals(OTemp)) {
                                    question_id = str.substring(0, str.indexOf(","));
                                }
                            }
                            callbackBody += "&questionId" + question_id;
                        }
                        put("callbackBody", callbackBody);
                        Tools.d(callbackBody);
                        put("callbackBodyType", "application/x-www-form-urlencoded");
                    }
                });
                CompleteMultipartUploadResult completeResult = oss.completeMultipartUpload(complete);
                if (completeResult != null) {
                    String resultStr = completeResult.getServerCallbackReturnBody();
                    Tools.d("resultStr:" + resultStr);
                    if (!TextUtils.isEmpty(resultStr) && (new JSONObject(resultStr).getString("code").equals("200") || new JSONObject(resultStr).getString("code").equals("2"))) {
                        Tools.d(resultStr);//上传成功
                        returnResult = true;
                        if (!"4".equals(tasktype)) {
                            offlineDBHelper.upTrafficSum(updataInfo.getUsername(), updataInfo.getProjectid(), updataInfo
                                    .getProjecname(), updataInfo.getStroeid(), updataInfo.getStorename(), file.length());
                        }
                    }
                }
                Tools.d("end");
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(Urls.BucketName + "/" + objectKey);
                editor.apply();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                return false;
            }
        } catch (ClientException | ServiceException | IOException e) {
            if (e instanceof ServiceException) {
                Tools.d("ErrorCode:" + ((ServiceException) e).getErrorCode());
                String error = ((ServiceException) e).getErrorCode();
                if (error != null) {
                    if (error.contains("NoSuchUpload")) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove(Urls.BucketName + "/" + objectKey);
                        if (editor.commit()) {
                            returnResult = OSSMultipartTest(OTemp, file, updataInfo, executeid);
                        }
                    } else if (!((ServiceException) e).getErrorCode().contains("RequestTimeout")) {
                        MobclickAgent.reportError(this, "Updataservice OSSMultipartTest error1:" + ((ServiceException) e)
                                .toString());
                    }
                }
            } else {
                Tools.d("error2");
                MobclickAgent.reportError(this, "Updataservice OSSMultipartTest error2:" + e.getMessage());
            }
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
            MobclickAgent.reportError(this, "Updataservice OSSMultipartTest JSONException:" + e.getMessage());
        }
        return returnResult;
    }


    private boolean sendOSSCallback(String u, String par) {
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        OutputStream os = null;
        URL url;
        HttpURLConnection httpURLConnection = null;
        boolean returnvalue = false;
        try {
            url = new URL(u);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            settingHttpURLConnection(httpURLConnection);
            os = httpURLConnection.getOutputStream();
            os.write(par.getBytes("UTF-8"));
            os.flush();
            os.close();
            os = null;
            is = httpURLConnection.getInputStream();
            isr = new InputStreamReader(is, "utf-8");
            br = new BufferedReader(isr);
            String result = br.readLine();
            Tools.d(result);//上传成功
            Log.i("-----------result5", result);
            JSONObject jsonObject = new JSONObject(result);
            if ("200".equals(jsonObject.getString("code")) || "2".equals(jsonObject.getString("code"))) {
                returnvalue = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (isr != null)
                    isr.close();
                if (is != null)
                    is.close();
                if (os != null)
                    os.close();
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return returnvalue;
    }

    //个级别总大小
    private long project_sum_size, store_sum_size, task_sum_size, file_sum_size;
    //个级别已经传的大小
    private long project_sized, store_sized, task_sized, file_sized;

    private Timer mTimer;
    private Bundle message = new Bundle();

    /**
     * 发送bundle
     */
    private void sendProgress(String receive, Bundle bundle) {
        if (mTimer == null) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                public long scheduledExecutionTime() {
                    return super.scheduledExecutionTime();
                }

                public void run() {
                    if (!message.isEmpty()) {
                        Intent in = new Intent();
                        in.setAction(AppInfo.BroadcastReceiver_TAKEPHOTO);
                        in.putExtras(message);
                        sendBroadcast(in);
                        message.clear();
                    }
                }
            }, 0, 400);
        }
        if (TextUtils.isEmpty(receive)) {
            message.putAll(bundle);
        } else {
            message.putBundle(receive, bundle);
        }
    }
}
