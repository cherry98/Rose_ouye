package com.orange.oy.network;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.allinterface.OnCheckVersionResult;
import com.orange.oy.base.MyApplication;
import com.orange.oy.base.ScreenManager;
import com.orange.oy.base.Tools;
import com.orange.oy.util.FileCache;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * 版本检查
 */
public class CheckVersion {
    private static CheckVersion checkVersion;

    //外部调用方法
    public static void check(Context context, OnCheckVersionResult listener) {
        if (checkVersion == null) {
            checkVersion = new CheckVersion(context, listener);
        } else {
            Tools.showToast(MyApplication.getInstance(), "正在更新...");
        }
    }

    private Context mContext;
    private String app_path;
    private OnCheckVersionResult onCheckVersionResult;

    private CheckVersion(Context context, OnCheckVersionResult listener) {
        mContext = context;
        onCheckVersionResult = listener;
        NetworkConnection check = new NetworkConnection(context) {
            public Map<String, String> getNetworkParams() {
                return null;
            }
        };
        check.sendGetRequest(Urls.checkversionUrl, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String ver = jsonObject.getString("ver");
                    if (ver != null && !ver.equals(Tools.getVersionName(mContext))) {
                        app_path = jsonObject.getString("app_path");
                        if (!TextUtils.isEmpty(app_path)) {
                            onCheckVersionResult.checkversion(ver);
                            new downloadAPK().execute();
                        }
                    } else {
                        onCheckVersionResult.checkversion(null);
                        checkVersion = null;
                    }
                } catch (JSONException e) {
                    checkVersion = null;
                    onCheckVersionResult.checkversion(null);
                } catch (PackageManager.NameNotFoundException e) {
                    Tools.showToast(mContext, "版本号获取失败！");
                    checkVersion = null;
                    onCheckVersionResult.checkversion(null);
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
//                Tools.showToast(mContext, mContext.getResources().getString(R.string.network_volleyerror));
                checkVersion = null;
                onCheckVersionResult.checkversion(null);
            }
        });
    }

    //下载APK
    class downloadAPK extends AsyncTask {
        protected Object doInBackground(Object[] params) {
            URL url;
            HttpURLConnection conn;
            InputStream ins = null;
            FileOutputStream outStream = null;
            boolean isSuccess = false;
            try {
                url = new URL(app_path);
                conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                ins = conn.getInputStream();
                File dir = FileCache.getDirForAPK(mContext);
                File ApkFile = new File(dir, "ouye.apk");
                outStream = new FileOutputStream(ApkFile);
                byte buf[] = new byte[1024];
                int numread;
                while ((numread = ins.read(buf)) != -1) {
                    outStream.write(buf, 0, numread);
                }
                outStream.flush();
                isSuccess = true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ins != null) {
                        ins.close();
                    }
                    if (outStream != null) {
                        outStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return isSuccess;
        }

        protected void onPostExecute(Object o) {
            if ((boolean) o) {
                File ApkFile = new File(FileCache.getDirForAPK(mContext), "ouye.apk");
                if (!ApkFile.exists()) {
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.parse("file://" + ApkFile.toString()), "application/vnd.android" +
                        ".package-archive");
                mContext.startActivity(intent);
                ScreenManager.AppExit(mContext);
            }
            checkVersion = null;
        }
    }
}
