package com.orange.oy.util;

import android.content.Context;

import com.orange.oy.R;

import java.io.File;
import java.sql.Date;

public class FileCache {

    private static File cacheDir;

    public static void InitDir(Context context) {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED))
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), context.getResources()
                    .getString(R.string.app_cachedir));
        else
            cacheDir = context.getCacheDir();
        if (!cacheDir.exists() || !cacheDir.isDirectory())
            cacheDir.mkdirs();
    }

    public static File getCacheDir(Context context) {
        if (cacheDir == null) {
            InitDir(context);
        }
        return cacheDir;
    }

    public static File getDirForPhoto(Context context) {
        if (cacheDir == null) {
            InitDir(context);
        }
        File dir = new File(cacheDir.getPath() + "/data");
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static File getDirForPhoto(Context context, String dirName) {
        if (cacheDir == null) {
            InitDir(context);
        }
        File dir = new File(cacheDir, "/data");
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }
        dir = new File(cacheDir, "data/" + dirName);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static File getDirForVideo(Context context, String dirName) {
        if (cacheDir == null) {
            InitDir(context);
        }
        File dir = new File(cacheDir, "/data");
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }
        dir = new File(cacheDir, "data/video");
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }
        dir = new File(cacheDir, "data/video/" + dirName);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static File getDirForRecord(Context context, String dirName) {
        if (cacheDir == null) {
            InitDir(context);
        }
        File dir = new File(cacheDir, "/data");
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }
        dir = new File(cacheDir, "/data/record");
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }
        dir = new File(cacheDir, "/data/record/" + dirName);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static File getDirForDownload(Context context, String dirname) {
        if (cacheDir == null) {
            InitDir(context);
        }
        File dir = new File(cacheDir.getPath() + "/download/" + dirname);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static File getDirForCamerase(Context context) {
        if (cacheDir == null) {
            InitDir(context);
        }
        File dir = new File(cacheDir.getPath() + "/camerase");
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static File getDirForShakePhoto(Context context) {
        if (cacheDir == null) {
            InitDir(context);
        }
        File dir = new File(cacheDir.getPath() + "/shakephoto");
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static File getDirForCamerase2(Context context) {
        if (cacheDir == null) {
            InitDir(context);
        }
        File dir = new File(cacheDir.getPath() + "/camerase2");
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }
        return dir;
    }

    //新版APK路径
    public static File getDirForAPK(Context context) {
        if (cacheDir == null) {
            InitDir(context);
        }
        File dir = new File(cacheDir, "apk");
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static File getFile(Context context, String url) {
        if (cacheDir == null || !cacheDir.isDirectory()) {
            InitDir(context);
        }
        String filename = String.valueOf(url.hashCode());
        File f = new File(cacheDir, filename);
        return f;
    }

    public static void clear(Context context, long clearTime) {
        if (cacheDir == null || !cacheDir.isDirectory()) {
            InitDir(context);
        }
        ClearThread ct = new ClearThread(cacheDir, clearTime);
        ct.start();
    }

}

class ClearThread extends Thread {
    private long clearTime;
    private File cacheDir;

    ClearThread(File cacheDir, long clearTime) {
        this.clearTime = clearTime;
        this.cacheDir = cacheDir;
    }

    public void run() {
        try {
            Date curDate = new Date(System.currentTimeMillis());
            long nowTime = curDate.getTime();
            File[] files = cacheDir.listFiles();
            if (files == null) {
                return;
            }
            for (File file : files) {
                Date date = new Date(file.lastModified());
                long lastTime = date.getTime();
                if (nowTime - lastTime >= clearTime) {
                    file.delete();
                }
            }
        } catch (Exception e) {
        }
    }
}