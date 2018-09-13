package com.orange.oy.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.alibaba.sdk.android.oss.common.utils.IOUtils;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IdentitycardParseTask extends AsyncTask<Bitmap, Void, Void> {
    private String TESSBASE_PATH = null;
    private String TESSDATA_PATH = null;
    private Context context;
    private String text = "";

    public IdentitycardParseTask(Context context) {
        this.context = context;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        TESSBASE_PATH = FileCache.getCacheDir(context).getPath() + "/tesseract";
        TESSDATA_PATH = TESSBASE_PATH + "/tessdata";
        write();
    }

    protected Void doInBackground(Bitmap... params) {
        TessBaseAPI baseAPI = new TessBaseAPI();
        baseAPI.init(TESSBASE_PATH, "id");
        baseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO);
        if (params[0] != null) {
            baseAPI.setImage(params[0]);
        }
        text = baseAPI.getUTF8Text();
        baseAPI.end();
        return null;
    }

    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        text.replaceAll("\n", "");
        if (text.length() > 18) {
            text = text.substring(text.length() - 18, text.length());
        }
    }

    private void write() {
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            inputStream = context.getResources().getAssets().open("id.traineddata");
            File file = new File(TESSDATA_PATH);
            if (!file.exists()) {
                file.mkdirs();
            }
            fileOutputStream = new FileOutputStream(TESSDATA_PATH + "/id.traineddata");
            byte[] buffer = new byte[512];
            int count = 0;
            while ((count = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, count);
            }
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.safeClose(fileOutputStream);
            IOUtils.safeClose(inputStream);
        }
    }
}
