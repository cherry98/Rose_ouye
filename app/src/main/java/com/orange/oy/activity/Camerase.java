package com.orange.oy.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.common.utils.IOUtils;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.SystemDBHelper;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.FileCache;
import com.orange.oy.util.Utils;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Camerase extends BaseActivity implements SensorEventListener {
    private Camera camera;
    private ImageView picture;
    private SystemDBHelper systemDBHelper;
    private String projectid, storeid, storecode, packageid, taskid;
    private SurfaceView surfaceView;
    private SurfaceHolder holder;
    private int maxTake, takeSum = 0;
    private boolean isCFouce;//是否需要变换焦距取证
    private boolean isSecond;//如果模式为变焦，此参数为判断是否进行了第二次取证拍照的开关参数，否则此参数无用
    private View takepicture_spin;
    private TextView takepicture_zz, takepicture_back, takepicture_num;
    private View buttonLayout;
    private static final String zz1 = "开启遮罩";
    private static final String zz2 = "关闭遮罩";
    private int state = 0;
    private int returnThumbnail = 250;
    private NetworkConnection photolog;
    private boolean isSystem = false;
    private String cameraseState = null;//摄像头缩放问题状态：null：不支持缩放，1：不做适配
    private ImageView takepicture;//拍照按钮

    private void initNetworkConnection() {
        photolog = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("taskid", taskid);
                params.put("storeid", storeid);
                if (!TextUtils.isEmpty(cameraseState)) {
                    params.put("note", cameraseState);
                }
                params.put("usermobile", AppInfo.getName(Camerase.this));
                params.put("token", Tools.getToken());
                return params;
            }
        };
    }

    protected void onStop() {
        super.onStop();
        if (photolog != null) {
            photolog.stop(Urls.Photolog);
        }
    }

    private void sendPhotoLog() {
        photolog.sendPostRequest(Urls.Photolog, new Response.Listener<String>() {
            public void onResponse(String s) {

            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
    }

    //判断是否支持变焦
    public boolean isSupportZoom() {
        return camera.getParameters().getMaxZoom() != 0;
    }

    private SensorManager sensorManager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setTranslucentStatus();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        setContentView(R.layout.surface_layout);
        systemDBHelper = new SystemDBHelper(this);
        Intent data = getIntent();
        isSystem = data.getBooleanExtra("isSystem", false);
        projectid = data.getStringExtra("projectid");
        storeid = data.getStringExtra("storeid");
        storecode = data.getStringExtra("storecode");
        packageid = data.getStringExtra("packageid");
        taskid = data.getStringExtra("taskid");
        state = data.getIntExtra("state", 0);
        takeSum = data.getIntExtra("takeSum", 0);
        isCFouce = data.getBooleanExtra("isCFouce", false);
        initNetworkConnection();
        boolean isIdentity = data.getBooleanExtra("identityview", false);
        int state = data.getIntExtra("state", 0);
        if (isIdentity) {
            switch (state) {
                case 0: {//身份证
                    findViewById(R.id.identityview).setVisibility(View.VISIBLE);
                }
                break;
                case 1: {//营业执照
                    findViewById(R.id.identityCommpanyview).setVisibility(View.GONE);//去掉不要了
                }
                break;
            }
        }
        returnThumbnail = data.getIntExtra("returnThumbnail", 250);
        if (returnThumbnail > 1000 || returnThumbnail < 250) {
            returnThumbnail = 250;
        }
        maxTake = data.getIntExtra("maxTake", 0);//值为1时单拍模式，值为0时为无限制拍照，其他值为最大限制
        if (maxTake == -1) {//异常值
            maxTake = 9;
        }
        takepicture_zz = (TextView) findViewById(R.id.takepicture_zz);
        takepicture_num = (TextView) findViewById(R.id.takepicture_num);
        takepicture = (ImageView) findViewById(R.id.takepicture);
        takepicture_num.setText(takeSum + "张");
        takepicture_back = (TextView) findViewById(R.id.takepicture_back);
        takepicture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnOnclick();
            }
        });
        buttonLayout = findViewById(R.id.buttonLayout);
        takepicture_spin = findViewById(R.id.takepicture_spin);
        if (maxTake == 1) {
            if (!isCFouce) {
                takepicture_spin.setVisibility(View.VISIBLE);
            }
            takepicture_zz.setVisibility(View.GONE);
        } else {
            takepicture_spin.setVisibility(View.VISIBLE);
//            if (AppInfo.getShade(this) == 1) {//1开启遮罩
            takepicture_zz.setVisibility(View.VISIBLE);
            takepicture_zz.setText(zz1);
            takepicture_zz.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (zz1.equals(takepicture_zz.getText())) {
                        takepicture_zz.setText(zz2);
                        buttonLayout.setBackgroundColor(Color.BLACK);
                    } else {
                        takepicture_zz.setText(zz1);
                        buttonLayout.setBackgroundResource(0);
                    }
                }
            });
//            }
            if (takeSum > 0) {
                takepicture.setImageResource(R.mipmap.camera_button2);
            } else {
                takepicture.setImageResource(R.mipmap.camera_button1);
            }
            takepicture_back.setVisibility(View.VISIBLE);
            takepicture_num.setVisibility(View.VISIBLE);
            takepicture_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post("1");
                    baseFinish();
                }
            });
        }
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        picture = (ImageView) findViewById(R.id.picture);
//        surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder = surfaceView.getHolder();
        holder.setKeepScreenOn(true);// 屏幕常亮
        holder.addCallback(new SurfaceCallback());// 为SurfaceView的句柄添加一个回调函数
        initLocation();
    }

    private void setTranslucentStatus() {//状态栏颜色和标题栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window win = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            winParams.flags |= bits;
            win.setAttributes(winParams);
        }
    }

    /**
     * 按钮被点击触发的事件
     *
     * @paramv
     */
    private void btnOnclick() {
        if (camera != null && !isSave) {
            if (takeSum < maxTake || maxTake == 0) {
                camera.autoFocus(new AutoFocusCallback() {
                    public void onAutoFocus(boolean arg0, Camera arg1) {
                        Tools.d("onAutoFocus:" + arg0);
                        if (camera != null && !isSave) {
                            taskOrientation = exifOrientation;
                            Tools.d("taskOrientation1.1:" + taskOrientation);
                            isSave = true;
                            camera.takePicture(null, null, new MyPictureCallback());
                            camera.cancelAutoFocus();
                        }
                    }
                });
            } else {
                Tools.showToast(this, "此任务已拍摄到最大数量");
            }
        }
    }

    //还原焦距拍摄取证图片
    private void changeFTake() {
        buttonLayout.setBackgroundColor(Color.BLACK);
        settingMinFouces();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        camera.autoFocus(new AutoFocusCallback() {
            public void onAutoFocus(boolean arg0, Camera arg1) {
                Tools.d("onAutoFocus:" + arg0);
                if (camera != null && !isSave) {
                    taskOrientation = exifOrientation;
                    Tools.d("taskOrientation1.2:" + taskOrientation);
                    isSave = true;
                    camera.takePicture(null, null, new MyPictureCallback2());
                    camera.cancelAutoFocus();
                }
            }
        });
    }

    boolean isSave = false;
    int index = 0;
    private int exifOrientation = 1;//当前，持续刷新
    private int taskOrientation = 1;//拍照时，只在拍照时刷新

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (Sensor.TYPE_ACCELEROMETER != event.sensor.getType()) {
            return;
        }
        float[] values = event.values;
        int ax = (int) values[0];
        int ay = (int) values[1];
        int az = (int) values[2];
        if (ax > 5) {//横屏左倒
//            exifOrientation = 6;
            exifOrientation = 8;
        } else if (ax < -5) {//横屏右倒
            exifOrientation = 6;
//            exifOrientation = 3;
        } else if (ay > 5) {//竖屏正
            exifOrientation = 1;
//            exifOrientation = 6;
        } else if (ay < -5) {//竖屏倒
            exifOrientation = 3;
//            exifOrientation = 8;
        } else {
            if (ax >= 0) {//左倒
                if (ax >= ay) {
                    if (ay < 0) {//竖屏倒
                        exifOrientation = 3;
//                        exifOrientation = 8;
                    } else {//横屏
//                        exifOrientation = 6;
                        exifOrientation = 8;
                    }
                } else {//竖屏
                    exifOrientation = 1;
//                    exifOrientation = 6;
                }
            } else {//右倒
                if (ax < ay) {//横屏
//                    exifOrientation = 8;
                    exifOrientation = 6;
                } else {//竖屏
                    exifOrientation = 1;
//                    exifOrientation = 6;
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private final class MyPictureCallback implements PictureCallback {
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                String filename = index++ + "_" + Tools.getTimeSS() + Tools.getDeviceId(Camerase.this) + storeid;
                takeSum++;
                if (isSystem) {//体验项目
                    saveToSystem(data, filename);
                }
                new SaveToSDCardAsyncTask(filename).execute(new Object[]{data});
                if (maxTake != 1 || isCFouce) {
                    camera.startPreview(); // 拍完照后，重新开始预览
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String Ofilename = "";

    //取证图片用回调
    private final class MyPictureCallback2 implements PictureCallback {
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                camera.startPreview();
                new SaveToSDCardAsyncTask(Ofilename).execute(new Object[]{data});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveToSystem(byte[] data, String filename) {//保存到系统相册
        File tempFile = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM).getPath() + "/" + filename + ".jpg");
        if (tempFile.exists()) {
            tempFile.delete();
        }
        try {
            tempFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(tempFile);
            outputStream.write(data);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String release = Build.VERSION.RELEASE;
        String tempID = release.substring(0, 3);

        if (Double.parseDouble(tempID) >= 4.4) {//安卓4.4以上版本的时候使用这个，以下的使用else语句里面的
            MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStoragePublicDirectory(Environment
                    .DIRECTORY_DCIM).getPath() + "/"}, null, null);
        } else {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory
                    ())));
            MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStoragePublicDirectory(Environment
                    .DIRECTORY_DCIM).getPath() + "/"}, null, null);
        }

    }


    private class SaveToSDCardAsyncTask extends AsyncTask {
        String filename, filename2, filename_fouces;

        SaveToSDCardAsyncTask(String filename) {
            if (isCFouce && isSecond) {
                settingMaxFouces();
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.filename = filename;
                this.filename2 = filename.replaceFirst(".ouye", "_2.ouye");
                this.filename_fouces = filename.replaceFirst(".ouye", "o.ouye");
                buttonLayout.setBackgroundResource(0);
            } else {
                filename = checkExists(filename, ".ouye");
                this.filename = filename + ".ouye";
                this.filename2 = filename + "_2.ouye";
            }
        }

        private String checkExists(String filename, String hz) {
            File file = new File(filename + hz);
            if (file.exists()) {
                do {
                    filename = filename + "x";
                    file = new File(filename + hz);
                } while (file.exists());
            }
            return filename;
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Object doInBackground(Object[] params) {
            byte[] bytes = getBytesForexif((byte[]) params[0]);
            if (isCFouce && isSecond) {
                return saveToSDCard2(filename_fouces, filename, bytes);
            } else {
                return saveToSDCard(filename, filename2, bytes);
            }
        }

        protected void onPostExecute(Object o) {
            isSave = false;
            if ((boolean) o) {
                if (isCFouce && !isSecond) {//变焦模式，并且没有拍摄取证图片
                    Ofilename = filename;
                    isSecond = true;
                    changeFTake();
                } else {//非变焦模式
                    if (isSecond)//重置参数
                        isSecond = false;
                    picture.setImageURI(Uri.fromFile(new File(FileCache.getDirForCamerase(Camerase.this).getPath() + "/" +
                            filename2)));
                    if (takeSum >= 1) {
                        takepicture_back.setText("完成");
                        takepicture.setImageResource(R.mipmap.camera_button2);
                    }
                    takepicture_num.setText(takeSum + "张");
                    if (maxTake == 1) {
                        setResult(RESULT_OK, new Intent().putExtra("path", FileCache.getDirForCamerase(Camerase.this).getPath() +
                                "/" + filename2));
                        baseFinish();
                    } else if (maxTake > 1) {
                        if (TextUtils.isEmpty(path)) {
                            path = FileCache.getDirForCamerase(Camerase.this).getPath() + "/" + filename2;
                        } else {
                            path = path + "," + FileCache.getDirForCamerase(Camerase.this).getPath() + "/" + filename2;
                        }
                        setResult(RESULT_OK, new Intent().putExtra("path", path));
                    } else if (maxTake == 0) {//无限制连拍
                        if (TextUtils.isEmpty(path)) {
                            path = FileCache.getDirForCamerase(Camerase.this).getPath() + "/" + filename2;
                        } else {
                            path = path + "," + FileCache.getDirForCamerase(Camerase.this).getPath() + "/" + filename2;
                        }
                        setResult(RESULT_OK, new Intent().putExtra("path", path));
                    }
                }
            } else {
            }
        }
    }

    private String path = null;
    private int cameraPosition = 1;
    private boolean isback = true;

    private void spinCamerase() {  //切换前后摄像头
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
            if (cameraPosition == 1) {
                //现在是后置，变更为前置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {//代表摄像头的方位，CAMERA_FACING_FRONT前置
                    // CAMERA_FACING_BACK后置
                    isback = false;
                    camera.stopPreview();//停掉原来摄像头的预览
                    camera.release();//释放资源
                    camera = null;//取消原来摄像头
                    Tools.d("change surfaceview:" + surfaceView.getWidth() + "," + surfaceView.getHeight());
                    try {
                        camera = Camera.open(i);//打开当前选中的摄像头
                    } catch (Exception e) {
                        Tools.d("摄像头切换失败！");
                        Tools.showToast(this, "摄像头切换失败！");
                        camera = Camera.open();
                    }
                    try {
                        camera.setPreviewDisplay(holder);//通过surfaceview显示取景画面
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    updateCameraParameters();
                    camera.startPreview();//开始预览
                    cameraPosition = 0;
                    break;
                }
            } else {
                //现在是前置， 变更为后置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {//代表摄像头的方位，CAMERA_FACING_FRONT前置
                    // CAMERA_FACING_BACK后置
                    isback = true;
                    camera.stopPreview();//停掉原来摄像头的预览
                    camera.release();//释放资源
                    camera = null;//取消原来摄像头
                    Tools.d("change surfaceview:" + surfaceView.getWidth() + "," + surfaceView.getHeight());
                    try {
                        camera = Camera.open(i);//打开当前选中的摄像头
                    } catch (Exception e) {
                        Tools.d("摄像头切换失败！");
                        Tools.showToast(this, "摄像头切换失败！");
                        camera = Camera.open();
                    }
                    try {
                        camera.setPreviewDisplay(holder);//通过surfaceview显示取景画面
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    updateCameraParameters();
                    camera.startPreview();//开始预览
                    cameraPosition = 1;
                    break;
                }
            }

        }
    }

    /**
     * 将拍下来的照片存放在SD卡中
     */
    private boolean saveToSDCard(String filename, String filename2, byte[] data) {
        boolean resutValue = false;
        File fileFolder = FileCache.getDirForCamerase(Camerase.this);
        File jpgFile = new File(fileFolder, filename);
        File jpgFile1 = new File(fileFolder, "1" + filename);
        File jpgFile2 = new File(fileFolder, filename2);
        FileOutputStream outputStream = null;
        FileOutputStream outputStream1 = null;
        FileOutputStream outputStream2 = null;
        try {
            outputStream1 = new FileOutputStream(jpgFile1);
            outputStream1.write(data);
            outputStream1.flush();
//            ExifInterface exifInterface = new ExifInterface(jpgFile1.getPath());
//            Tools.d("or:" + exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION));
            outputStream2 = new FileOutputStream(jpgFile2);
            Tools.getBitmap(jpgFile1.getPath(), returnThumbnail, returnThumbnail).
                    compress(Bitmap.CompressFormat.JPEG, 100, outputStream2);
            jpgFile1.delete();
            ExifInterface exifInterface = new ExifInterface(jpgFile2.getPath());
            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, taskOrientation + "");
            exifInterface.saveAttributes();
            Tools.d("or:" + exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION));
            if (locationStr == null) {
                locationStr = "";
            }
            for (int i = 0; i < data.length; i++) {
                data[i] = (byte) (255 - data[i]);
            }
            outputStream = new FileOutputStream(jpgFile);
            outputStream.write(data);
            outputStream.flush();
            String time = Tools.getTimeByPattern("yyyy-MM-dd HH:mm:ss");
            systemDBHelper.insertPicture(AppInfo.getName(this), projectid, storeid, storecode, packageid, taskid,
                    jpgFile.getPath(), jpgFile2.getPath(), time, locationStr, location_longitude + "", location_latitude + "", state);
            resutValue = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.safeClose(outputStream);
            IOUtils.safeClose(outputStream1);
            IOUtils.safeClose(outputStream2);
        }
        return resutValue;
    }

    /**
     * 保存取证图片用
     */
    private boolean saveToSDCard2(String filename, String Ofilename, byte[] data) {
        boolean resutValue = false;
        File fileFolder = FileCache.getDirForCamerase(Camerase.this);
        File jpgFile = new File(fileFolder, filename);
        File jpgOFile = new File(fileFolder, Ofilename);
        FileOutputStream outputStream = null;
        try {
            for (int i = 0; i < data.length; i++) {
                data[i] = (byte) (255 - data[i]);
            }
            Tools.d(jpgFile.getPath());
            outputStream = new FileOutputStream(jpgFile);
            outputStream.write(data);
            outputStream.flush();
            systemDBHelper.updataFoucePicture(jpgOFile.getPath(), jpgFile.getPath());
            resutValue = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.safeClose(outputStream);
        }
        return resutValue;
    }

    private int fm = -1;

    /**
     * 设置随机焦距
     */
    private boolean settingMaxFouces() {
        Tools.d("settingMaxFouces");
        Camera.Parameters params = camera.getParameters();
        if (fm == -1) {
            int MAX = params.getMaxZoom();
            Tools.d("MAX:" + MAX);
            if (MAX < 8) return false;
//            if (MAX <= 7) {
//                fm = 1;
//                params.setZoom(fm);
//                camera.setParameters(params);
//                return;
//            }
            if (MAX <= 20) {
                int[] is = {2, 3, 4};
                int ram = (int) Math.round(Math.random() * 2);
                fm = is[ram];
            } else {
                int[] is = {6, 7, 8};
                int ram = (int) Math.round(Math.random() * 2);
                fm = is[ram];
            }
        }
        Tools.d("fm:" + fm);
        params.setZoom(fm);
        camera.setParameters(params);
        return true;
    }


    /**
     * 设置最小焦距
     */
    private void settingMinFouces() {
        Camera.Parameters params = camera.getParameters();
        params.setZoom(0);
        camera.setParameters(params);
    }

    private final class SurfaceCallback implements Callback {
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            try {
                camera.stopPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
            updateCameraParameters();
            try {
                camera.setPreviewDisplay(holder);
                camera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void surfaceCreated(SurfaceHolder holder) {
            if (!Utils.checkCameraHardware(Camerase.this)) {
                Toast.makeText(Camerase.this, "摄像头打开失败！", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                // 获得Camera对象
                camera = getCameraInstance();
                try {
                    // 设置用于显示拍照摄像的SurfaceHolder对象
                    camera.setPreviewDisplay(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                    // 释放手机摄像头
                    camera.release();
                    camera = null;
                }
                updateCameraParameters();
                if (isCFouce) {
                    if (isSupportZoom()) {
                        if (!settingMaxFouces()) {
                            //不做适配
                            isCFouce = false;
                            cameraseState = "1";
                            sendPhotoLog();
                        }
                    } else {
                        Tools.d("不支持缩放");
                        isCFouce = false;
                        sendPhotoLog();
                    }
                }
                if (camera != null) {
                    camera.startPreview();
                    if (Tools.getDeviceModel().startsWith("BKL-") || Tools.getDeviceModel().startsWith("CLT-")) {
                        myHandler.sendEmptyMessageDelayed(3, 100);
                    }
                }
                if (takepicture_spin != null) {
                    takepicture_spin.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            spinCamerase();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            if (camera != null) {
                camera.release(); // 释放照相机
                camera = null;
            }
        }
    }

    private MyHandler myHandler = new MyHandler();

    protected void onDestroy() {
        super.onDestroy();
        myHandler.removeMessages(3);
        if (sensorManager != null)
            sensorManager.unregisterListener(this);
        if (camera != null) {
            camera.release(); // 释放照相机
            camera = null;
        }
        if (mSearch != null) {
            mSearch.destroy();
            mSearch = null;
        }
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_CAMERA: // 按下拍照按钮
                if (camera != null && event.getRepeatCount() == 0) {
                    camera.takePicture(null, null, new MyPictureCallback());
                }
                break;
            case KeyEvent.KEYCODE_BACK: {
                if (maxTake > 1 && !TextUtils.isEmpty(path)) {
                    setResult(RESULT_OK, new Intent().putExtra("path", path));
                    baseFinish();
                    return true;
                }
            }
            break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 获取摄像头实例
     *
     * @return
     */
    private Camera getCameraInstance() {
        Camera c = null;
        try {
            int cameraCount = 0;
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            cameraCount = Camera.getNumberOfCameras(); // get cameras number

            for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
                Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
                // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    try {
                        c = Camera.open(camIdx); // 打开后置摄像头
                    } catch (RuntimeException e) {
                        Tools.d("摄像头打开失败！");
                    }
                }
            }
            if (c == null) {
                c = Camera.open(0); // attempt to get a Camera instance
            }
        } catch (Exception e) {
            Tools.d("摄像头打开失败！");
        }
        return c;
    }

    /**
     * @param p
     */
    private void setParameters(Camera.Parameters p) {
        List<String> focusModes = p.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            p.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        long time = new Date().getTime();
        p.setGpsTimestamp(time);
        // 设置照片格式
        List<Integer> pf = p.getSupportedPictureFormats();
        int pixelFormat = PixelFormat.JPEG;
        for (Integer temp : pf) {
            switch (temp) {
                case PixelFormat.RGB_565: {
                    pixelFormat = PixelFormat.RGB_565;
                }
                break;
                case PixelFormat.RGB_888: {
                    pixelFormat = PixelFormat.RGB_888;
                }
                break;
                case PixelFormat.JPEG: {
                    pixelFormat = PixelFormat.JPEG;
                }
                break;
                default: {
                    pixelFormat = pf.get(0);
                }
            }
        }
        p.setPictureFormat(pixelFormat);
        p.setJpegQuality(100);
        List<Camera.Size> pszize = p.getSupportedPictureSizes();
        if (pszize != null && !pszize.isEmpty()) {
            Collections.sort(pszize, new SizeComparator());
            int size = pszize.size();
            Camera.Size previewSize;
            if (size >= 3) {
                previewSize = pszize.get(pszize.size() - 2);
            } else {
                previewSize = pszize.get(pszize.size() - 1);
            }
            Tools.d(previewSize.width + ":" + previewSize.height);
            p.setPictureSize(previewSize.width, previewSize.height);
        } else {
            p.setPictureSize(Tools.getScreeInfoWidth(this), Tools.getScreeInfoHeight(this));
        }
        List<Camera.Size> previewSizeList = p.getSupportedPreviewSizes();
//        Camera.Size preSize = getProperSize(previewSizeList, ((float) Tools.getScreeInfoWidth(this)) / Tools.getScreeInfoHeight
//                (this));
        Camera.Size previewSize = findPreviewSizeByScreen(p);
        p.setPreviewSize(previewSize.width, previewSize.height);
        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            camera.setDisplayOrientation(90);
            if (isback) {
                p.setRotation(90);
            } else {
                p.setRotation(270);
            }
        }
    }

    private Camera.Size getProperSize(List<Camera.Size> sizeList, float displayRatio) {
        //先对传进来的size列表进行排序
        Collections.sort(sizeList, new SizeComparator());
        Camera.Size result = null;
        for (Camera.Size size : sizeList) {
            float curRatio = ((float) size.width) / size.height;
            if (curRatio - displayRatio == 0) {
                result = size;
            }
        }
        if (null == result) {
            for (Camera.Size size : sizeList) {
                float curRatio = ((float) size.width) / size.height;
                if (curRatio == 3f / 4) {
                    result = size;
                }
            }
        }
        return result;
    }

    class SizeComparator implements Comparator<Camera.Size> {
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            Camera.Size size1 = lhs;
            Camera.Size size2 = rhs;
            if (size1.width < size2.width || size1.width == size2.width && size1.height < size2.height) {
                return -1;
            } else if (!(size1.width == size2.width && size1.height == size2.height)) {
                return 1;
            }
            return 0;
        }
    }

    /**
     * 将预览大小设置为屏幕大小
     *
     * @param parameters
     * @return
     */
    private Camera.Size findPreviewSizeByScreen(Camera.Parameters parameters) {
        if (viewWidth != 0 && viewHeight != 0) {
            return camera.new Size(Math.max(viewWidth, viewHeight), Math.min(viewWidth, viewHeight));
        } else {
            return camera.new Size(Tools.getDpi(this), Tools.getScreeInfoWidth(this));
        }
    }

    private void updateCameraParameters() {
        if (camera != null) {
            Camera.Parameters p = camera.getParameters();
            setParameters(p);
            try {
                camera.setParameters(p);
            } catch (Exception e) {
                Tools.d("to e");
                List<Camera.Size> pszize = p.getSupportedPictureSizes();
                if (pszize != null && !pszize.isEmpty()) {
                    Collections.sort(pszize, new SizeComparator());
                    int size = pszize.size();
                    Camera.Size previewSize;
                    if (size >= 3) {
                        previewSize = pszize.get(pszize.size() - 2);
                    } else {
                        previewSize = pszize.get(pszize.size() - 1);
                    }
                    Tools.d(previewSize.width + ":" + previewSize.height);
                    p.setPictureSize(previewSize.width, previewSize.height);
                } else {
                    p.setPictureSize(Tools.getDpi(this), Tools.getScreeInfoWidth(this));
                }
                p.setPreviewSize(Tools.getDpi(this), Tools.getScreeInfoWidth(this));
                if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                    camera.setDisplayOrientation(90);
                    p.setRotation(90);
                }
                try {
                    camera.setParameters(p);
                } catch (Exception e1) {
                    e.printStackTrace();
                    Tools.d("to e1");
                    List<String> focusModes = p.getSupportedFocusModes();
                    if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                        p.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                    }
                    long time = new Date().getTime();
                    p.setGpsTimestamp(time);
                    // 设置照片格式
                    p.setPictureFormat(PixelFormat.JPEG);
                    p.set("jpeg-quality", 100);
                    List<Camera.Size> pszize1 = p.getSupportedPictureSizes();
                    if (pszize1 != null && !pszize1.isEmpty()) {
                        Collections.sort(pszize1, new SizeComparator());
                        int size = pszize1.size();
                        Camera.Size previewSize;
                        if (size >= 3) {
                            previewSize = pszize1.get(pszize1.size() - 2);
                        } else {
                            previewSize = pszize1.get(pszize1.size() - 1);
                        }
                        Tools.d(previewSize.width + ":" + previewSize.height);
                        p.setPictureSize(previewSize.width, previewSize.height);
                    } else {
                        p.setPictureSize(Tools.getDpi(this), Tools.getScreeInfoWidth(this));
                    }
//                    p.setPictureSize(Tools.getDpi(this), Tools.getScreeInfoWidth(this));
                    if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                        camera.setDisplayOrientation(90);
                        p.setRotation(90);
                    }
                    try {
                        camera.setParameters(p);
                    } catch (Exception e2) {
                        Tools.d("to e2");
                        e2.printStackTrace();
                        List<String> focusModes2 = p.getSupportedFocusModes();
                        if (focusModes2.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                            p.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                        }
                        long time2 = new Date().getTime();
                        p.setGpsTimestamp(time2);
                        // 设置照片格式
                        p.setPictureFormat(PixelFormat.JPEG);
                        p.set("jpeg-quality", 100);
                        p.setPictureSize(Tools.getDpi(this), Tools.getScreeInfoWidth(this));
                        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                            camera.setDisplayOrientation(90);
                            p.setRotation(90);
                        }
                        try {
                            camera.setParameters(p);
                        } catch (Exception e3) {
                            Tools.d("to e3");
                            e2.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private int viewWidth = 0;
    private int viewHeight = 0;

    /**
     * 找到最合适的显示分辨率 （防止预览图像变形）
     *
     * @param parameters
     * @return
     */
    private Camera.Size findBestPreviewSize(Camera.Parameters parameters) {

        // 系统支持的所有预览分辨率
        String previewSizeValueString = null;
        previewSizeValueString = parameters.get("preview-size-values");

        if (previewSizeValueString == null) {
            previewSizeValueString = parameters.get("preview-size-value");
        }

        if (previewSizeValueString == null) { // 有些手机例如m9获取不到支持的预览大小 就直接返回屏幕大小
            return camera.new Size(Tools.getScreeInfoWidth(this), Tools.getScreeInfoHeight(this));
        }
        float bestX = 0;
        float bestY = 0;

        float tmpRadio = 0;
        float viewRadio = 0;

        if (viewWidth != 0 && viewHeight != 0) {
            viewRadio = Math.min((float) viewWidth, (float) viewHeight)
                    / Math.max((float) viewWidth, (float) viewHeight);
        }

        String[] COMMA_PATTERN = previewSizeValueString.split(",");
        for (String prewsizeString : COMMA_PATTERN) {
            prewsizeString = prewsizeString.trim();

            int dimPosition = prewsizeString.indexOf('x');
            if (dimPosition == -1) {
                continue;
            }

            float newX = 0;
            float newY = 0;

            try {
                newX = Float.parseFloat(prewsizeString
                        .substring(0, dimPosition));
                newY = Float.parseFloat(prewsizeString
                        .substring(dimPosition + 1));
            } catch (NumberFormatException e) {
                continue;
            }

            float radio = Math.min(newX, newY) / Math.max(newX, newY);
            if (tmpRadio == 0) {
                tmpRadio = radio;
                bestX = newX;
                bestY = newY;
            } else if (tmpRadio != 0
                    && (Math.abs(radio - viewRadio)) < (Math.abs(tmpRadio
                    - viewRadio))) {
                tmpRadio = radio;
                bestX = newX;
                bestY = newY;
            }
        }

        if (bestX > 0 && bestY > 0) {
            return camera.new Size((int) bestX, (int) bestY);
        }
        return null;
    }

    /**
     * 定位客户端
     */
    public LocationClient mLocationClient = null;
    public MyLocationListenner myListener = new MyLocationListenner();
    private GeoCoder mSearch = null;
    public static double location_latitude, location_longitude;
    public static String locationStr = "";

    /**
     * 初始化定位
     */
    private void initLocation() {
        if (mLocationClient != null && mLocationClient.isStarted()) {
            return;
        }
        if (mSearch == null) {
            mSearch = GeoCoder.newInstance();
            mSearch.setOnGetGeoCodeResultListener(onGetGeoCoderResultListener);
        }
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(myListener);
        setLocationOption();
        mLocationClient.start();
    }

    // 设置相关参数
    private void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll");
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setIsNeedAddress(true);
        option.setScanSpan(10000);
        mLocationClient.setLocOption(option);
    }

    private class MyLocationListenner implements BDLocationListener {
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }
            location_latitude = location.getLatitude();
            location_longitude = location.getLongitude();
            LatLng point = new LatLng(location_latitude, location_longitude);
            if (mSearch != null)
                mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(point));
        }

        public void onConnectHotSpotMessage(String s, int i) {

        }

        public void onReceivePoi(BDLocation arg0) {
        }

    }

    private OnGetGeoCoderResultListener onGetGeoCoderResultListener = new OnGetGeoCoderResultListener() {

        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
        }

        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
            if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                return;
            }
            ReverseGeoCodeResult.AddressComponent addressComponent = reverseGeoCodeResult.getAddressDetail();
            int index = addressComponent.streetNumber.lastIndexOf("号");
            if (index > 0) {
                try {
                    String str = String.valueOf(addressComponent.streetNumber.charAt(index - 1));
                    Tools.d(str);
                    if (Tools.StringToInt(str) != -1) {
                        addressComponent.streetNumber = addressComponent.streetNumber.substring(0, index + 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (addressComponent.province.endsWith("市")) {
                locationStr = addressComponent.city + addressComponent.district +
                        addressComponent.street + addressComponent.streetNumber;
            } else {
                locationStr = addressComponent.province + addressComponent.city +
                        addressComponent.district + addressComponent.street + addressComponent.streetNumber;
            }
        }
    };

    //取到带有exif信息的data数组
    private byte[] getBytesForexif(byte[] bytes) {
        Tools.d("taskOrientation2:" + taskOrientation);
        byte[] result = null;
        FileOutputStream outputStream = null;
        FileInputStream fileInputStream = null;
        String temppath = Environment.getExternalStorageDirectory().getPath() + "/" + Tools.getTimeSS() + ".jpg";
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            outputStream = new FileOutputStream(temppath);
            outputStream.write(bytes);
            outputStream.flush();
            outputStream.close();
            outputStream = null;
            BitmapFactory.Options opt1 = new BitmapFactory.Options();
            opt1.inSampleSize = 1;
            opt1.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(temppath, opt1);
            if (taskOrientation == 1 && opt1.outWidth > opt1.outHeight) {
                taskOrientation = 6;
            } else if ((taskOrientation == 6 || taskOrientation == 8) && opt1.outWidth > opt1.outHeight) {
                if (taskOrientation == 6) {
                    taskOrientation = 3;
                } else {
                    taskOrientation = 1;
                }
            } else if (taskOrientation == 180 && opt1.outWidth > opt1.outHeight) {
                taskOrientation = 8;
            }
            ExifInterface exif = new ExifInterface(temppath);
            exif.setAttribute(ExifInterface.TAG_ORIENTATION, taskOrientation + "");
            exif.saveAttributes();
            byte[] bs = new byte[1024];
            int readLength = -1;
            byteArrayOutputStream = new ByteArrayOutputStream();
            fileInputStream = new FileInputStream(temppath);
            while ((readLength = fileInputStream.read(bs)) != -1) {
                byteArrayOutputStream.write(bs, 0, readLength);
            }
            byteArrayOutputStream.flush();
            result = byteArrayOutputStream.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            new File(temppath).delete();
            IOUtils.safeClose(outputStream);
            IOUtils.safeClose(fileInputStream);
            IOUtils.safeClose(byteArrayOutputStream);
        }
        if (result != null && result.length > 0) {
            return result;
        } else {
            return bytes;
        }
    }

    private class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            try {
                if (camera != null)
                    camera.autoFocus(new AutoFocusCallback() {
                        public void onAutoFocus(boolean success, Camera camera) {
                        }
                    });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
