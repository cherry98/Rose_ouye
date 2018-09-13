package com.orange.oy.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.orange.oy.activity.Camerase;
import com.orange.oy.activity.experience.ExperiencePointActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.MyApplication;
import com.orange.oy.base.Tools;
import com.orange.oy.db.SystemDBHelper;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by xiedongyan on 2017/11/23.
 * 拍照悬浮窗
 */

public class FloatWindowView extends ImageView {

    private Context context;
    int sW;
    int sH;
    private float mTouchStartX;
    private float mTouchStartY;
    private float x;
    private float y;
    private boolean isMove = false;

    private WindowManager wm;
    private WindowManager.LayoutParams wmParams;//此wmParams变量为获取的全局变量，用以保存悬浮窗口的属性
    private final int statusHeight;//系统状态栏的高度
    private float mLastX;
    private float mLastY;
    private float mStartX;
    private float mStartY;
    private long mLastTime;
    private long mCurrentTime;
    private String projectid, packageid = "", taskid, storecode, projectName, storeid;

    public FloatWindowView(Context context) {
        this(context, null);
        this.context = context;

    }

    public FloatWindowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatWindowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        systemDBHelper = new SystemDBHelper(context);
        wm = (WindowManager) getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        wmParams = ((MyApplication) getContext().getApplicationContext()).getMywmParams();
        sW = wm.getDefaultDisplay().getWidth();
        sH = wm.getDefaultDisplay().getHeight();
        statusHeight = getStatusHeight(context);
    }

    public void setData(String projectid, String packageid, String taskid, String storecode, String projectName,
                        String storeid) {
        this.packageid = packageid;
        this.projectid = projectid;
        this.projectName = projectName;
        this.taskid = taskid;
        this.storecode = storecode;
        this.storeid = storeid;
    }

    /**
     * 获得状态栏的高度
     */
    public static int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            Class clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return statusHeight;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获取相对屏幕的坐标，即以屏幕左上角为原点
        x = sW - event.getRawX();
        y = event.getRawY() - statusHeight;
        Tools.d("currX" + x + "====currY" + y + "event.getRawY()" + event.getRawY() + "statusHeight" + statusHeight);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://捕捉手指触摸按下动作
                //获取相对view的坐标，即以此view左上角为原点
                mTouchStartX = event.getX();
                mTouchStartY = event.getY();
                mStartX = event.getRawX();
                mStartY = event.getRawY();
                mLastTime = System.currentTimeMillis();
                Tools.d("startX" + mTouchStartX + "====startY" + mTouchStartY);
                isMove = false;
                break;
            case MotionEvent.ACTION_MOVE://捕获手指触摸移动动作
                updateViewPosition();
                isMove = true;
                break;
            case MotionEvent.ACTION_UP://捕获手指触摸离开动作
                mLastX = event.getRawX();
                mLastY = event.getRawY();
                //抬起手指时让floatview紧贴
//                wmParams.x = wmParams.x <= (sW / 2) ? 0 : sW;
//                wmParams.y = (int) (y - mTouchStartY);
//                wm.updateViewLayout(this, wmParams);

                mCurrentTime = System.currentTimeMillis();
                if (mCurrentTime - mLastTime < 800) {
                    if (Math.abs(mStartX - mLastX) < 10.0 && Math.abs(mStartY - mLastY) < 10.0) {
                        //处理点击事件
                        photoNumber();
                        Intent intent = new Intent(context, Camerase.class);
                        intent.putExtra("isSystem", true);//是否保存在系统相册
                        intent.putExtra("projectid", projectid);
                        intent.putExtra("storeid", storeid);
                        intent.putExtra("storecode", storecode);
                        intent.putExtra("takeSum", originalImgList.size());
                        ((ExperiencePointActivity) context).startActivityForResult(intent, 13);
                    }
                }
                break;
        }
        return true;
    }

    private SystemDBHelper systemDBHelper;
    private ArrayList<String> originalImgList = new ArrayList<>();

    //判断照片数量是否为0（是否拍照）
    public void photoNumber() {
        ArrayList<String> list;
        if (TextUtils.isEmpty(taskid)) {
            list = systemDBHelper.getPictureThumbnail(AppInfo.getName(context), projectid, storeid);
        } else {
            list = systemDBHelper.getPictureThumbnail(AppInfo.getName(context), projectid,
                    storeid, packageid, taskid);
        }
        if (originalImgList.size() != 0) {
            originalImgList.clear();
        }
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                String path = systemDBHelper.searchForOriginalpath(list.get(i));
                File file = new File(path);
                if (file.exists() && file.isFile()) {
                    originalImgList.add(path);
                }
            }
        }
    }

    private void updateViewPosition() {
        //更新浮动窗口位置参数
        wmParams.x = (int) (x - mTouchStartX);
        wmParams.y = (int) (y - mTouchStartY);
        wm.updateViewLayout(this, wmParams);
    }
}
