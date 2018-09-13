package com.orange.oy.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.alibaba.sdk.android.oss.common.utils.IOUtils;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2018/9/5.
 * 画图view
 */

public class PaintView extends View {
    private Bitmap bitmap;
    private int bitmapX, bitmapY;
    //    private int canvasWidth, canvasHeight;
    private boolean isInit = false;
    private boolean canPaint = false;

    public void canPaint(boolean c) {
        canPaint = c;
    }

    public boolean isCanPaint() {
        return canPaint;
    }

    public void revertPaint() {
        if (lines != null && !lines.isEmpty()) {
            lines.remove(lines.size() - 1);
            invalidate();
        }
    }

    private ArrayList<ArrayList<PointInfo>> lines = new ArrayList<>();

    public PaintView(Context context) {
        super(context);
    }

    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PaintView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setBitmap(Bitmap bitmap) {
        lines.clear();
        isInit = false;
        if (this.bitmap != null && this.bitmap != bitmap && !this.bitmap.isRecycled()) {
            this.bitmap.recycle();
        }
        this.bitmap = bitmap;
        invalidate();
    }

    public void saveView(String path) {
        if (bitmap != null)
            new saveBitmap(path).executeOnExecutor(Executors.newCachedThreadPool());
    }

    private Paint getPaint() {
        Paint mPaint = new Paint();
        mPaint.setColor(Color.YELLOW);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(9);
        return mPaint;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(mDraw(canvas));
    }

    private Canvas mDraw(Canvas canvas) {
        Paint mPaint = getPaint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mPaint);
        if (bitmap != null) {
//            canvasWidth = canvas.getWidth();
//            canvasHeight = canvas.getHeight();
            if (!isInit) {
                bitmap = zoomImg2(bitmap, canvas.getWidth(), canvas.getHeight());
//                int bitmapWidth = bitmap.getWidth();
//                int bitmapHeight = bitmap.getHeight();
                bitmapX = bitmapY = 0;
//                if (bitmapWidth > bitmapHeight) {
//                    bitmapY = (canvasHeight - bitmapHeight) / 2;
//                } else {
//                    bitmapX = (canvasWidth - bitmapWidth) / 2;
//                }
                isInit = true;
            }
//            int nwidth = bitmap.getWidth();
//            int nheight = bitmap.getHeight();
//            if (mTouchMode == AMPLIFICATION) {
//                if (!(nwidth + (int) fingerDistanceChange > 2000 || nheight + (int) fingerDistanceChange > 2000)) {
//                    bitmap = zoomImg2(bitmap, nwidth + (int) fingerDistanceChange,
//                            nheight + (int) fingerDistanceChange);
//                }
//            } else if (mTouchMode == NARROW) {
//                if (!(nwidth - fingerDistanceChange < 300 ||
//                        nheight - fingerDistanceChange < 300)) {
//                    bitmap = zoomImg2(bitmap, nwidth + (int) fingerDistanceChange,
//                            nheight + (int) fingerDistanceChange);
//                }
//            }
            canvas.drawBitmap(bitmap, bitmapX, bitmapY, getPaint());
            if (line != null && nowState == 2) {
                drawLine(canvas, getPaint(), line);
            }
            if (lines != null) {
                for (ArrayList<PointInfo> list : lines) {
                    drawLine(canvas, getPaint(), list);
                }
            }
        }
        return canvas;
    }

    private void drawLine(Canvas canvas, Paint mPaint, ArrayList<PointInfo> line) {
        mPaint.setColor(Color.RED);
        int size = line.size();
        for (int i = 0; i < size; i++) {
            if (i == 0) {
                continue;
            }
            PointInfo pointInfo1 = line.get(i - 1);
            PointInfo pointInfo2 = line.get(i);
            canvas.drawLine(pointInfo1.x + bitmapX, pointInfo1.y + bitmapY,
                    pointInfo2.x + bitmapX, pointInfo2.y + bitmapY, mPaint);
        }
    }

    /**
     * 数字正负是否不同
     *
     * @param x1
     * @param x2
     * @return true 不同,false 相同
     */
    private boolean isOpposite(float x1, float x2) {
        if (x1 >= 0) {
            return x2 < 0;
        } else {
            return x2 >= 0;
        }
    }

    private float ox, oy, mx, my;
    private int nowState = 0;//0:无状态，1：按下,2:单点滑动,3:多点滑动
    private ArrayList<PointInfo> line = new ArrayList<>();
    private float lmx0, lmy0, lmx1, lmy1;
    private Long startTime;
    private float fingerSapce;
    //放大
    private final int AMPLIFICATION = 7;
    //缩小
    private final int NARROW = 8;
    //移动
    private final int MOVE = 9;
    private float fingerDistanceChange;
    private int mTouchMode;

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                nowState = 1;
                fingerSapce = 0;
                startTime = System.currentTimeMillis();
                ox = event.getX();
                oy = event.getY();
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                if (System.currentTimeMillis() - startTime > 60) {
                    if (nowState == 1 || nowState == 3 || nowState == 2) {
                        mx = event.getX();
                        my = event.getY();
                        if (event.getPointerCount() > 1 || nowState == 3) {
                            if (event.getPointerCount() > 1) {
                                nowState = 3;
                                float mx0 = event.getX(0);
                                float my0 = event.getY(0);
                                float mx1 = event.getX(1);
                                float my1 = event.getY(1);
                                float x1 = mx0 - mx1;
                                float y1 = my0 - my1;
                                float value = (float) Math.sqrt(x1 * x1 + y1 * y1);
                                if (fingerSapce == 0) {
                                    fingerSapce = value;
                                } else {
//                                    fingerDistanceChange = value - fingerSapce;
//                                    Tools.d("fingerDistanceChange:" + fingerDistanceChange);
//                                    if (Math.abs(fingerDistanceChange) > 15) {
//                                        Tools.d("value:" + value + ",fingerSapce:" + fingerSapce);
//                                        float scale = value / fingerSapce;
//                                        if (scale > 1) {
//                                            mTouchMode = AMPLIFICATION;
//                                        } else {
//                                            mTouchMode = NARROW;
//                                        }
//                                    } else {
                                    mTouchMode = MOVE;
                                    float xDistance = ((mx0 - lmx0) + (mx1 - lmx1)) / 2;
                                    float yDistance = ((my0 - lmy0) + (my1 - lmy1)) / 2;
                                    Tools.d("xDistance:" + xDistance + ",yDistance:" + yDistance);
                                    bitmapX += xDistance;
                                    bitmapY += yDistance;
//                                        for (ArrayList<PointInfo> list : lines) {
//                                            for (PointInfo pointInfo : list) {
//                                                pointInfo.x += xDistance;
//                                                pointInfo.y += yDistance;
//                                            }
//                                        }
//                                    }
                                    fingerSapce = value;
                                }
                                lmx0 = mx0;
                                lmy0 = my0;
                                lmx1 = mx1;
                                lmy1 = my1;
                            }
                        } else if (canPaint) {
                            if (nowState != 2) {
                                line.add(new PointInfo(checkPointx(ox) - bitmapX, checkPointy(oy) - bitmapY));
                            }
                            line.add(new PointInfo(checkPointx(mx) - bitmapX, checkPointy(my) - bitmapY));
                            nowState = 2;
                        }
                    }
                }
            }
            break;
            case MotionEvent.ACTION_UP: {
                lmx0 = 0;
                lmy0 = 0;
                lmx1 = 0;
                lmy1 = 0;
                mTouchMode = 0;
                fingerDistanceChange = 0;
                if (nowState == 2) {
                    ArrayList<PointInfo> list = new ArrayList<>();
                    for (PointInfo pointInfo : line) {
                        list.add(new PointInfo(pointInfo.x, pointInfo.y));
                    }
                    list.add(new PointInfo(checkPointx(event.getX()) - bitmapX, checkPointy(event.getY()) - bitmapY));
                    lines.add(list);
                }
                nowState = 0;
                line.clear();
            }
            break;
        }
        invalidate();
        return true;
    }

    private float checkPointx(float x) {
        if (x < bitmapX) {
            x = bitmapX;
        } else if (x > bitmapX + bitmap.getWidth()) {
            x = bitmapX + bitmap.getWidth();
        }
        return x;
    }

    private float checkPointy(float y) {
        if (y < bitmapY) {
            y = bitmapY;
        } else if (y > bitmapY + bitmap.getHeight()) {
            y = bitmapY + bitmap.getHeight();
        }
        return y;
    }

    private Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        if (bm != newbm) {
            bm.recycle();
        }
        return newbm;
    }

    private Bitmap zoomImg2(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        if (scaleWidth > 1 && scaleHeight > 1) {
            if (scaleWidth > scaleHeight) {
                scaleWidth = scaleHeight;
            } else {
                scaleHeight = scaleWidth;
            }
        } else {
            if (scaleWidth < 1 && scaleHeight > 1) {
                scaleHeight = scaleWidth;
            } else if (scaleHeight < 1 && scaleWidth > 1) {
                scaleWidth = scaleHeight;
            } else {
                if (scaleWidth > scaleHeight) {
                    scaleWidth = scaleHeight;
                } else {
                    scaleHeight = scaleWidth;
                }
            }
        }
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
                true);
        if (bm != newbm) {
            bm.recycle();
        }
        return newbm;
    }

    private class PointInfo {
        public PointInfo(float x, float y) {
            this.x = x;
            this.y = y;
        }

        float x, y;
    }

    public interface OnPaintviewsavefinishListener {
        void savefinish();
    }

    private OnPaintviewsavefinishListener onPaintviewsavefinishListener;

    public void setOnPaintviewsavefinishListener(OnPaintviewsavefinishListener onPaintviewsavefinishListener) {
        this.onPaintviewsavefinishListener = onPaintviewsavefinishListener;
    }

    private class saveBitmap extends AsyncTask {
        String path;

        public saveBitmap(String path) {
            this.path = path;
        }

        protected void onPreExecute() {
            CustomProgressDialog.showProgressDialog(getContext(), "");
        }

        protected void onPostExecute(Object object) {
            CustomProgressDialog.Dissmiss();
            if (onPaintviewsavefinishListener != null) {
                onPaintviewsavefinishListener.savefinish();
            }
        }

        protected Object doInBackground(Object[] params) {
            FileOutputStream fos = null;
            boolean resule = false;
            try {
                Bitmap b = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
                bitmapY = 0;
                bitmapX = 0;
                Canvas c = new Canvas(b);
                mDraw(c);
                c.save(Canvas.ALL_SAVE_FLAG);
                c.restore();
                fos = new FileOutputStream(path);
                b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                resule = true;
                IOUtils.safeClose(fos);
                b.recycle();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.safeClose(fos);
            }
            return resule;
        }
    }
}
