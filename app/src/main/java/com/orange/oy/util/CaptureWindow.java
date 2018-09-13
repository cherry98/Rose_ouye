package com.orange.oy.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.karics.library.zxing.camera.CameraManager;
import com.karics.library.zxing.view.ViewfinderView;
import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.view.TaskEditViewforCapture;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * 扫码窗口
 */
public final class CaptureWindow implements SurfaceHolder.Callback, View.OnClickListener, View.OnKeyListener {
    private static CaptureWindow captureWindow = null;
    private WindowManager windowManager = null;
    private View mView = null;
    private SurfaceView surfaceView = null;
    private TaskEditViewforCapture taskEditView;
    private Context context = null;
    private ViewfinderView viewfinderView;

    public Context getContext() {
        return context;
    }

    private CaptureWindow(Context context, TaskEditViewforCapture taskEditView, int height, int y) throws Exception {
        if (captureWindow == null) {
            captureWindow = this;
        } else {
            throw new Exception("窗口已经创建");
        }
        this.context = context;
        creatWindow(context, taskEditView, height, y);
    }

    public static void showWindow(Context context, TaskEditViewforCapture taskEditView, int height, int y) throws Exception {
        new CaptureWindow(context, taskEditView, height, y);
    }

    public static void closeWindow() {
        if (captureWindow != null) {
            captureWindow.destoryWindow();
        }
    }

    /**
     * 销毁窗口
     */
    private void destoryWindow() {
        try {
            if (windowManager != null)
                windowManager.removeView(mView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (handler != null) {
                handler.quitSynchronously();
                handler = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            cameraManager.closeDriver();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (surfaceView != null) {
                SurfaceHolder surfaceHolder = surfaceView.getHolder();
                surfaceHolder.removeCallback(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        captureWindow = null;
    }

    /**
     * 创建窗口
     *
     * @param context      上下文信息
     * @param taskEditView editview句柄
     * @param height       需要显示的高
     * @throws Exception 异常处理
     */
    private void creatWindow(Context context, TaskEditViewforCapture taskEditView, int height, int y) throws Exception {
        this.taskEditView = taskEditView;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int type;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            type = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, (height <= 0) ? WindowManager.LayoutParams.WRAP_CONTENT : height,
                type,//TYPE_SYSTEM_OVERLAY
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                PixelFormat.TRANSLUCENT);
        mView = Tools.loadLayout(context, R.layout.capture_window);
        mView.setOnKeyListener(this);
        surfaceView = (SurfaceView) mView.findViewById(R.id.winpreview_view);
        viewfinderView = (ViewfinderView) mView.findViewById(R.id.winviewfinder_view);
        mView.findViewById(R.id.wincapturefinish_button).setOnClickListener(this);
        cameraManager = new CameraManager(context);
        viewfinderView.setCameraManager(cameraManager);
        surfaceView.getHolder().addCallback(this);
        handler = null;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.y = y;
        windowManager.addView(mView, layoutParams);
        decodeFormats = null;
        characterSet = null;
    }

    private CameraManager cameraManager;
    private CaptureWindowHandler handler;

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        initCamera(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    /**
     * 扫描成功，处理反馈信息
     *
     * @param rawResult
     * @param barcode
     * @param scaleFactor
     */
    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        if (barcode != null && !barcode.isRecycled())
            barcode.recycle();
        taskEditView.settingEdittext(rawResult.getText());
        closeWindow();
    }

    private Collection<BarcodeFormat> decodeFormats;
    private Map<DecodeHintType, ?> decodeHints;
    private String characterSet;

    /**
     * 初始化Camera
     *
     * @param surfaceHolder
     */
    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            return;
        }
        try {
            // 打开Camera硬件设备
            cameraManager.openDriver(surfaceHolder);
            // 创建一个handler来打开预览，并抛出一个运行时异常
            if (handler == null) {
                handler = new CaptureWindowHandler(this, decodeFormats,
                        decodeHints, characterSet, cameraManager);
            }
        } catch (IOException ioe) {
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            displayFrameworkBugMessageAndExit();
        }
    }

    /**
     * 显示底层错误信息并退出应用
     */
    private void displayFrameworkBugMessageAndExit() {
        Tools.showToast(context, context.getString(R.string.msg_camera_framework_bug));
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    @Override
    public void onClick(View v) {
        closeWindow();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                closeWindow();
                return true;
        }
        return false;
    }
}
