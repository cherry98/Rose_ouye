package com.orange.oy.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.ResumableUploadRequest;
import com.alibaba.sdk.android.oss.model.ResumableUploadResult;
import com.orange.oy.R;
import com.orange.oy.activity.MainActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.AppDBHelper;
import com.orange.oy.info.ShakephotoUpdataInfo;
import com.orange.oy.network.NetworkUpForHttpURL;
import com.orange.oy.network.Urls;
import com.orange.oy.util.FileCache;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Administrator on 2018/6/11.
 * 甩图上传服务
 */

public class ShakephotoUpdataService extends Service implements Runnable {
    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
    }

    public static ShakephotoUpdataService getShakephotoUpdataService() {
        return shakephotoUpdataService;
    }

    private static ShakephotoUpdataService shakephotoUpdataService;
    private Thread thread;

    public Thread getThread() {
        return thread;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        startNotification();
        if (shakephotoUpdataService == null) {
            shakephotoUpdataService = this;
        }
        if (shakephotoUpdataService.getThread() == null) {
            Cancel();
            Tools.d("creat thread");
            thread = new Thread(this);
            thread.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        Tools.d("onDestroy");
        thread = null;
        shakephotoUpdataService = null;
        Cancel();
    }

    private AppDBHelper appDBHelper;

    private CharSequence contentTitle = "甩图上传中...";
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

    public void run() {
        boolean isSuccess = false;
        ArrayList<ShakephotoUpdataInfo> upList = new ArrayList<>();
        while (true) {
            if (appDBHelper == null) {
                appDBHelper = new AppDBHelper(this);
            }
            upList = appDBHelper.getShakePhotoUpdataList();
            if (upList.isEmpty()) {
                break;
            }
            for (ShakephotoUpdataInfo shakephotoUpdataInfo : upList) {
//            while (!isSuccess) {
                String path = shakephotoUpdataInfo.getPath();
                String parameter = shakephotoUpdataInfo.getParameter();
                if (TextUtils.isEmpty(path)) {
                    appDBHelper.removeShakePhotoUpdataItem(path);
                    resumableTask = null;
                } else {
                    File file = new File(path);
                    if (file.exists() && file.isFile()) {
                        String ossname = Tools.getTimeSS() + "_" + file.getName();
                        String fileurl = Urls.Shakephoto + "/" + ossname;
                        if (isSuccess = ResumeUpdata(fileurl, file)) {
                            Tools.d("success");
                            Object call_result = null;
                            try {
                                parameter = parameter + "&file_url=" + URLEncoder.encode(fileurl, "utf-8")
                                        + "&oss_name=" + URLEncoder.encode(ossname, "utf-8")
                                        + "&show_name=" + URLEncoder.encode(ossname, "utf-8");
                                Tools.d("shakeservice_parameter:" + parameter);
                                call_result = NetworkUpForHttpURL.getNetworkUpForHttpURL().sendPost(Urls.CallbackFileInfo, parameter);
                                if (call_result != null) {
                                    if (call_result instanceof Exception) {//异常
                                        Tools.d(((Exception) call_result).getMessage());
                                    } else {
                                        try {
                                            Tools.d(call_result + "");
                                            JSONObject jsonObject = new JSONObject(call_result + "");
                                            if (200 == jsonObject.getInt("code")) {//上传成功
                                                if (file.getPath().contains("/OY/")) {
                                                    file.delete();
                                                }
                                                appDBHelper.deleteShakePhoto(path);
                                                appDBHelper.removeShakePhotoUpdataItem(path);
                                                resumableTask = null;
                                            } else {
                                                if (!appDBHelper.havPhotoInShakeTable(path)) {
                                                    if (file.getPath().contains("/OY/")) {
                                                        file.delete();
                                                    }
                                                }
                                                appDBHelper.removeShakePhotoUpdataItem(path);
                                                resumableTask = null;
                                            }
                                        } catch (JSONException e) {//异常
                                            e.printStackTrace();
                                            if (!appDBHelper.havPhotoInShakeTable(path)) {
                                                if (file.getPath().contains("/OY/")) {
                                                    file.delete();
                                                }
                                            }
                                            appDBHelper.removeShakePhotoUpdataItem(path);
                                            resumableTask = null;
                                        }
                                    }
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                                if (!appDBHelper.havPhotoInShakeTable(path)) {
                                    if (file.getPath().contains("/OY/")) {
                                        file.delete();
                                    }
                                }
                                appDBHelper.removeShakePhotoUpdataItem(path);
                                resumableTask = null;
                            }
                        } else {
                            Tools.d("fail");
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        appDBHelper.removeShakePhotoUpdataItem(path);
                        resumableTask = null;
                    }
                }
//            }
            }
        }
        stopSelf();
    }

    private OSSClient oss = null;
    private boolean returnvalue = false;
    private OSSAsyncTask resumableTask;

    private boolean ResumeUpdata(String objectKey, File file) {
        try {
            if (oss == null) {
                OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider("YiMYmCHzNNO8k2l8",
                        "oPDfExOB5wAgmT0LHRA35Ts1tGzfjH");
                oss = new OSSClient(getApplicationContext(), "http://oss-cn-hangzhou.aliyuncs.com", credentialProvider);
            }
            if (oss.doesObjectExist(Urls.BucketName, objectKey)) {//文件已存在
                Tools.d("文件已存在");
                return true;
            }
            String recordDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/oss_record/";
            File recordDir = new File(recordDirectory);
            // 要保证目录存在，如果不存在则主动创建
            if (!recordDir.exists()) {
                recordDir.mkdirs();
            }
            ResumableUploadRequest request = new ResumableUploadRequest(Urls.BucketName, objectKey, file.getPath(),
                    recordDirectory);// 设置上传过程回调
            request.setProgressCallback(new OSSProgressCallback<ResumableUploadRequest>() {
                public void onProgress(ResumableUploadRequest request, long currentSize, long totalSize) {
                    Tools.d("currentSize:" + currentSize + " totalSize:" + totalSize);
                }
            });
            resumableTask = oss.asyncResumableUpload(request, new OSSCompletedCallback<ResumableUploadRequest, ResumableUploadResult>() {
                public void onSuccess(ResumableUploadRequest request, ResumableUploadResult result) {
                    returnvalue = true;
                }

                public void onFailure(ResumableUploadRequest request, ClientException clientExcepion, ServiceException serviceException) {
                    returnvalue = false;
                }
            });
            resumableTask.waitUntilFinished();
        } catch (ClientException | ServiceException e) {
            e.printStackTrace();
        }
        return returnvalue;
    }

    public void Cancel() {
        if (resumableTask != null) {
            resumableTask.cancel();
            Tools.d("cancel");
        }
    }
}
