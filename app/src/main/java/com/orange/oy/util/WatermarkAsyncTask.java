package com.orange.oy.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;

import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;

import java.io.FileOutputStream;
import java.io.IOException;

public class WatermarkAsyncTask extends AsyncTask {
    private Context context;
    private String msg;
    private String filePath;
    private String outPath;
    private OnWatermarkListener onWatermarkListener;
    private final static int paintSize = 60;

    //回调接口
    public interface OnWatermarkListener {
        void success();

        void failed();
    }

    public WatermarkAsyncTask(Context context, String waterMessage, String filePath, String outPath,
                              OnWatermarkListener onWatermarkListener, boolean isShowDialog) {
        this.filePath = filePath;
        this.outPath = outPath;
        this.context = context;
        this.onWatermarkListener = onWatermarkListener;
        msg = waterMessage;
        if (isShowDialog) {
            CustomProgressDialog.showProgressDialog(context, "正在处理图片");
        }
    }

    protected Object doInBackground(Object[] params) {
//        String path = Environment.getExternalStorageDirectory().getPath() + "/test1.png";
        Bitmap bitmap = Tools.getBitmap(filePath, 1024, 1024);
        Bitmap newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        bitmap.recycle();
        int width = newBitmap.getWidth();
        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint();
        paint.setAlpha(90);
        paint.setColor(Color.WHITE);
        paint.setDither(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setTextSize(paintSize);
        int xNum = width / paintSize;
        String[] msgs = msg.split("\n");
        int xN = 1;
        for (String str : msgs) {
            if (xNum < str.length()) {
                int yNum = (int) Math.ceil(str.length() * 1d / xNum);
                int yb = 0;
                for (int i = 1; i <= yNum; i++) {
                    int temp = yb + xNum;
                    if (temp > str.length()) {
                        temp = str.length();
                    }
                    canvas.drawText(str, yb, temp, 0, paintSize * xN++, paint);
                    yb = temp;
                }
            } else {
                canvas.drawText(str, 0, paintSize * xN++, paint);
            }
        }
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();//存储
        return newBitmap;
    }

    protected void onPostExecute(Object o) {
        if (o != null && o instanceof Bitmap) {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(outPath);
                ((Bitmap) o).compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                if (onWatermarkListener != null) {
                    onWatermarkListener.success();
                }
                ((Bitmap) o).recycle();
            } catch (IOException e) {
                e.printStackTrace();
                if (onWatermarkListener != null) {
                    onWatermarkListener.failed();
                }
            } finally {
                CustomProgressDialog.Dissmiss();
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            CustomProgressDialog.Dissmiss();
            if (onWatermarkListener != null) {
                onWatermarkListener.failed();
            }
        }
    }
}