package com.orange.oy.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.orange.oy.R;

/**
 * 身份证遮罩
 */
public class IdentityView extends View {
    private Paint paint;
    private PorterDuffXfermode SRC_OUT;
    private RectF rect;

    public IdentityView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setTextSize(30);
        paint.setColor(Color.BLACK);
        SRC_OUT = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);
    }

    private Bitmap bg;
    public static final int MarWidth = 12;
    public static final int MarHeight = 3;

    protected void onDraw(Canvas canvas) {
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        if (rect == null) {
            rect = new RectF(0, 0, viewWidth, viewHeight);
        }
        int y = viewHeight / MarHeight;
        int x = viewWidth / MarWidth;
        if (bg == null) {
            bg = BitmapFactory.decodeResource(getResources(), R.mipmap.identitybg);
            bg = zoomImg(bg, viewWidth, viewHeight);
        }
        int index;
        paint.setFilterBitmap(false);
        index = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG
                | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG);
        canvas.drawRect(x, y, viewWidth - x, viewHeight - y, paint);
        paint.setXfermode(SRC_OUT);
        canvas.drawBitmap(bg, null, rect, paint);
        paint.setXfermode(null);
        canvas.drawText("请将身份证放入框中拍摄", x, y - paint.getTextSize() - 2, paint);
        canvas.restoreToCount(index);
        super.onDraw(canvas);
    }

    public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        bm.recycle();
        return newbm;
    }

}
