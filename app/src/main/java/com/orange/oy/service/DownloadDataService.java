package com.orange.oy.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;

import com.orange.oy.R;
import com.orange.oy.activity.MainActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.Tools;
import com.orange.oy.db.OfflineDBHelper;
import com.orange.oy.info.DownloadDataInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * 离线示例文件下载服务
 */
public class DownloadDataService extends Service implements Runnable {
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static Thread thread;
    private OfflineDBHelper offlineDBHelper;

    public int onStartCommand(Intent intent, int flags, int startId) {
        startNotification();
        Tools.d("onStartCommand");
        if (thread == null) {
            Tools.d("creat thread");
            offlineDBHelper = new OfflineDBHelper(this);
            thread = new Thread(this);
            thread.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private boolean isHavNetwork() {
        String network = Tools.GetNetworkType(this);//网络状态
//        if (AppInfo.isOpen4GUpdata(this)) {
//            if (!TextUtils.isEmpty(network)) {
//                return true;
//            }
//            return false;
//        } else if ("WIFI".equals(network)) {
//            return true;
//        } else {
//            return false;
//        }
        return !TextUtils.isEmpty(network);
    }

    public void run() {
        contentTitle = "文件下载中....";
        Tools.d("通知显示");
        startNotification();
        ArrayList<DownloadDataInfo> list;
        while (thread != null && isHavNetwork()) {
            list = offlineDBHelper.getDownloadList();
            int size = list.size();
            if (size == 0) {
                break;
            }
            for (int i = 0; i < size && isHavNetwork() && thread != null; i++) {
                DownloadDataInfo downloadDataInfo = list.get(i);
//                File downfile = FileCache.getDirForDownload(this, downloadDataInfo.getUsername() + "/" +
//                        downloadDataInfo.getPackageid() + downloadDataInfo.getProjectid() + downloadDataInfo.getStoreid() +
//                        downloadDataInfo.getTaskid());
                String urlstr = downloadDataInfo.getUrl();
//                File sf = new File(downfile, urlstr.substring(urlstr.lastIndexOf("/"), urlstr.length()));
                File sf = new File(downloadDataInfo.getPath());
                URL url;
                InputStream is = null;
                OutputStream os = null;
                HttpURLConnection conn = null;
                try {
                    url = new URL(urlstr);
                    conn = (HttpURLConnection) url.openConnection();
                    is = conn.getInputStream();
                    os = new FileOutputStream(sf);
                    byte[] bytes = new byte[1024];
                    int count;
                    while ((count = is.read(bytes, 0, 1024)) != -1) {
                        os.write(bytes, 0, count);
                    }
                    os.flush();
                    Tools.d("success");
                    offlineDBHelper.removeDownloadData(downloadDataInfo.getUsername(), downloadDataInfo.getProjectid(),
                            downloadDataInfo.getStoreid(), downloadDataInfo.getPackageid(), downloadDataInfo.getTaskid(),
                            downloadDataInfo.getUrl());//此为逻辑删除
                    AppInfo.addCachesize(this, sf.length());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Tools.d("download FileNotFoundException");
                    offlineDBHelper.removeDownloadData(downloadDataInfo.getUsername(), downloadDataInfo.getProjectid(),
                            downloadDataInfo.getStoreid(), downloadDataInfo.getPackageid(), downloadDataInfo.getTaskid(),
                            downloadDataInfo.getUrl());//此为逻辑删除
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (os != null) {
                            os.close();
                        }
                        if (conn != null) {
                            conn.disconnect();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        stopSelf();
    }

    public void onDestroy() {
        super.onDestroy();
        thread = null;
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
}
