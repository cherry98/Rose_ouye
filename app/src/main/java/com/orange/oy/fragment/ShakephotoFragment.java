package com.orange.oy.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.common.utils.IOUtils;
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
import com.baidu.mapapi.utils.DistanceUtil;
import com.orange.oy.R;
import com.orange.oy.activity.mycorps_314.IdentifycodeLoginActivity;
import com.orange.oy.activity.shakephoto_316.ShakeAlbumActivity;
import com.orange.oy.activity.shakephoto_316.ShakeSelectLocationActivity;
import com.orange.oy.activity.shakephoto_318.LeftActivity;
import com.orange.oy.activity.shakephoto_318.ShakephotoActivity;
import com.orange.oy.allinterface.upProgressBar;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseFragment;
import com.orange.oy.base.ScreenManager;
import com.orange.oy.base.Tools;
import com.orange.oy.db.AppDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.info.shakephoto.ShakeThemeInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.service.ShakephotoUpdataService;
import com.orange.oy.util.FileCache;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.util.Utils;
import com.orange.oy.view.CompletedView;
import com.orange.oy.view.FlowLayoutView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by Administrator on 2018/6/7.
 * 甩图拍照页
 */
@SuppressWarnings("ResourceType")
public class ShakephotoFragment extends BaseFragment implements SensorEventListener, upProgressBar,
        View.OnClickListener {
    public void progress(int progress) {
        if (takepicture2 != null) {
            takepicture2.setPostprogress(progress);
        }
    }

    public void resetButton(String fileUrl, String error) {
        Message msg = Message.obtain();
        if (fileUrl == null) {
            msg.arg1 = 1;
            msg.obj = error;
        } else {
            msg.arg1 = 0;
            msg.obj = fileUrl;
        }
        msg.what = 1;
        myHandler.sendMessage(msg);
    }

    private boolean isJoinActivity = false;
    private boolean isuppic = false;

    public void initjoinActivity(ShakeThemeInfo shakeThemeInfo) {
        initjoinActivity(shakeThemeInfo, false, null);
    }

    public void initjoinActivity(ShakeThemeInfo shakeThemeInfo, boolean isuppic, String mPath) {
        this.mPath = mPath;
        this.isuppic = isuppic;
        isJoinActivity = true;
        nowSelAi = shakeThemeInfo;
    }

    public void joinActivity(ShakeThemeInfo shakeThemeInfo) {//参加指定活动
        type = 2;
        shakephoto_free.setBackgroundResource(getResources().getColor(R.color.sobot_transparent));
        shakephoto_scene.setBackgroundResource(R.drawable.change_task2_2);
        shakephoto_labels.setVisibility(View.VISIBLE);
        settingTheme(false);
//        settingAgain(false);
        nowSelAi = shakeThemeInfo;
        creatViews();
    }

    public void onStop() {
        super.onStop();
        if (ActivityIndex != null) {
            ActivityIndex.stop(Urls.ActivityIndex);
        }
        if (ThemeInfo != null) {
            ThemeInfo.stop(Urls.ThemeInfo);
        }
    }

    private void initNetwork() {
        ActivityIndex = new NetworkConnection(getContext()) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(getContext()));
                params.put("type", type + "");
                return params;
            }
        };
        ThemeInfo = new NetworkConnection(getContext()) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(getContext()));
                params.put("isShow", "0");
                return params;
            }
        };
    }

    //    private AutoTextView shakephoto_autotextview;
//    private View shakephoto_autolayout, shakephoto_autoclose;
    private View shakephoto_labels_obs;
    private EditText shakephoto_labels_obs_edittext;
    private View shakephoto_labels_obs_sumbit;

    private void initView(View mView) {
        shakephoto_pre = (ImageView) mView.findViewById(R.id.shakephoto_pre);
        shakephoto_labels_obs = mView.findViewById(R.id.shakephoto_labels_obs);
        shakephoto_labels_obs_edittext = (EditText) mView.findViewById(R.id.shakephoto_labels_obs_edittext);
        shakephoto_labels_obs_sumbit = mView.findViewById(R.id.shakephoto_labels_obs_sumbit);
        takepicture = (ImageView) mView.findViewById(R.id.takepicture);
        picture = (ImageView) mView.findViewById(R.id.picture);
        surfaceView = (SurfaceView) mView.findViewById(R.id.surfaceView);
        shakephoto_spin = (ImageView) mView.findViewById(R.id.shakephoto_spin);
        shakephoto_light = (ImageView) mView.findViewById(R.id.shakephoto_light);
        takepicture = (ImageView) mView.findViewById(R.id.takepicture);
        takepicture2 = (CompletedView) mView.findViewById(R.id.takepicture2);
        picturer_1_1 = mView.findViewById(R.id.picturer_1_1);
        picturer_2_1 = mView.findViewById(R.id.picturer_2_1);
        picturer_1_2 = (TextView) mView.findViewById(R.id.picturer_1_2);
        picturer_2_2 = mView.findViewById(R.id.picturer_2_2);
        shakephoto_view = mView.findViewById(R.id.shakephoto_view);
        shakephoto_zz = (TextView) mView.findViewById(R.id.shakephoto_zz);
        shakephoto_labels = (FlowLayoutView) mView.findViewById(R.id.shakephoto_labels);
        shakephoto_zzlo = mView.findViewById(R.id.shakephoto_zzlo);
        shakephoto_free = (TextView) mView.findViewById(R.id.shakephoto_free);
        shakephoto_scene = (TextView) mView.findViewById(R.id.shakephoto_scene);
        shakephoto_labels_obs_edittext.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String editable = shakephoto_labels_obs_edittext.getText().toString();
                String str = Tools.stringFilter(editable);
                if (!editable.equals(str)) {
                    shakephoto_labels_obs_edittext.setText(str);
                    shakephoto_labels_obs_edittext.setSelection(str.length());
                }
            }

            public void afterTextChanged(Editable s) {
            }
        });
    }

    private NetworkConnection ActivityIndex;
    private NetworkConnection ThemeInfo;
    private MyHandler myHandler = new MyHandler();
    private View mView;
    private SurfaceView surfaceView;
    private ImageView shakephoto_light, shakephoto_spin;
    private View shakephoto_view;
    private TextView shakephoto_zz;
    //    private TextView shakephoto_location2;
//    private ViewPager shakephoto_classify_viewpage;
    private ImageView takepicture;//拍摄按钮
    private ImageView picture;//预览图
    //    private ArrayList<View> points = new ArrayList<>();
//    private ArrayList<ShakeThemeInfo> headlist = new ArrayList<>();//活动列表
//    private TextView shakephoto_classify_point;
    private SurfaceHolder holder;
    private Camera camera;
    private CompletedView takepicture2;
    private View picturer_1_1, picturer_2_1;
    private View picturer_2_2;
    private TextView picturer_1_2;
    //    private View picturer_2;
//    private TextView picturer_1;
    private static ShakephotoFragment shakephotoFragment;
    //    private View shakephoto_location2_ico;
//    private View shakephoto_classify1;
    private View shakephoto_zzlo;
    private FlowLayoutView shakephoto_labels;
    private ImageLoader imageLoader;
    //    private ShakephotoView shakephoto_shakephotoView;
    private TextView shakephoto_free, shakephoto_scene;//自由拍 场景拍
    private AppDBHelper appDBHelper;
    private ImageView shakephoto_pre;

    public static ShakephotoFragment getShakephotoFragment() {
        return shakephotoFragment;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_shakephoto, container, false);
        initView(mView);
        initLocation();
        return mView;
    }

    /**
     * 设置是否显示分类
     *
     * @param isShow
     */
    private void settingTheme(boolean isShow) {
        if (TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
            ConfirmDialog.showDialog(getContext(), null, 2,
                    getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                        public void leftClick(Object object) {
                        }

                        public void rightClick(Object object) {
                            Intent intent = new Intent(getContext(), IdentifycodeLoginActivity.class);
                            startActivity(intent);
                        }
                    });
            return;
        }
        if (isShow) {
            shakephoto_labels.setVisibility(View.GONE);
            shakephoto_labels_obs.setVisibility(View.INVISIBLE);
            picturer_2_2.setVisibility(View.VISIBLE);
            picturer_1_2.setVisibility(View.VISIBLE);
            shakephoto_zzlo.setVisibility(View.GONE);
            shakephoto_light.setVisibility(View.GONE);
            shakephoto_spin.setVisibility(View.GONE);
            if (!isWaitUp) {
                takepicture.setVisibility(View.GONE);
            }
        } else {
            if (!isWaitUp && ShakephotoUpdataService.getShakephotoUpdataService() == null) {
                takepicture.setVisibility(View.VISIBLE);
                shakephoto_zzlo.setVisibility(View.VISIBLE);
                shakephoto_spin.setVisibility(View.VISIBLE);
                if (cameraPosition == 1) {
                    shakephoto_light.setVisibility(View.VISIBLE);
                }
            }
//            picturer_2_2.setVisibility(View.GONE);
//            picturer_1_2.setVisibility(View.GONE);
        }
    }

    /**
     * 设置是否显示重拍
     *
     * @param isShow
     */
    private void settingAgain(boolean isShow) {
        if (isShow) {
            picture.setVisibility(View.GONE);
            takepicture.setVisibility(View.GONE);
            takepicture2.setVisibility(View.VISIBLE);
//            picturer_1_1.setVisibility(View.VISIBLE);
//            picturer_2_1.setVisibility(View.VISIBLE);
            shakephoto_light.setVisibility(View.GONE);
            shakephoto_spin.setVisibility(View.GONE);
            if (isWaitUp) {
                shakephoto_labels.setVisibility(View.VISIBLE);
            } else {
                shakephoto_labels.setVisibility(View.GONE);
                shakephoto_labels_obs.setVisibility(View.INVISIBLE);
            }
            shakephoto_labels_obs.setVisibility(View.VISIBLE);
//            picturer_1_1.setOnClickListener(this);
//            picturer_2_1.setOnClickListener(this);
            settingLabels(nowSelAi.getKey_cencent());
        } else {
            if (shakephoto_view.getVisibility() == View.GONE) {
                picture.setVisibility(View.VISIBLE);
                takepicture.setVisibility(View.VISIBLE);
            }
            takepicture2.setVisibility(View.GONE);
            if (cameraPosition == 1) {
                shakephoto_light.setVisibility(View.VISIBLE);
            }
            shakephoto_spin.setVisibility(View.VISIBLE);
            picturer_1_1.setVisibility(View.GONE);
            picturer_2_1.setVisibility(View.GONE);
            shakephoto_labels.setVisibility(View.GONE);
            shakephoto_labels_obs.setVisibility(View.INVISIBLE);
            settingLabels(null);
            isWaitUp = false;
        }
    }

    /**
     * 创建标签
     *
     * @param strings
     */
    private void settingLabels(String[] strings) {
        shakephoto_labels.removeAllViews();
        if (strings == null) {
            return;
        }
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, Tools.dipToPx(getActivity(), 30));
        boolean first = true;
        for (String string : strings) {
            TextView textView = getTextView(string, false);
            textView.setOnClickListener(LabelsClickListener);
            if (first) {
                first = false;
                textView.setBackgroundResource(R.drawable.bg_r_15_col_fff65d57);
                textView.setId(1);
            } else {
                textView.setId(0);
            }
            shakephoto_labels.addView(textView, lp);
        }
    }

    private View.OnClickListener LabelsClickListener = new View.OnClickListener() {
        public void onClick(View v) {
//            if ("-1".equals(v.getId() + "")) {//添加标签
//                Intent intent = new Intent(getContext(), AddLabelActivity.class);
//                startActivityForResult(intent, 2);
//            } else {
            if ("0".equals(v.getId() + "")) {
                v.setBackgroundResource(R.drawable.bg_r_15_col_fff65d57);
                v.setId(1);
            } else if ("1".equals(v.getId() + "")) {
                v.setBackgroundResource(R.drawable.bg_r_15_col_75000000);
                v.setId(0);
            }
//            }
        }
    };

    private TextView getTextView(String str, boolean isRed) {
        TextView textView = new TextView(getContext());
        textView.setText(str);
        textView.setTextSize(14);
        if (isRed) {
            textView.setBackgroundResource(R.drawable.bg_r_15_col_fff65d57);
        } else {
            textView.setBackgroundResource(R.drawable.bg_r_15_col_75000000);
        }
        textView.setTextColor(Color.WHITE);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(Tools.dipToPx(getActivity(), 8), 0, Tools.dipToPx(getActivity(), 8), 0);
        return textView;
    }

    private SensorManager sensorManager;
    private boolean is_flashmodle = false;

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initNetwork();
        initSettings();
        isWaitUp = false;
        appDBHelper = new AppDBHelper(getContext());
        if (isJoinActivity) {
            clickShakephoto_scene();
            settingTheme(false);
            creatViews();
            if (isuppic) {
                surfaceView.setVisibility(View.GONE);
                mParameter = getParameter(AppInfo.getName(getContext()), Tools.getToken(), nowSelAi.getAi_id(), "jpg", province, city, county, address, longitude,
                        latitude, dai_id, nowSelAi.getCat_id(), county, isShowLocation ? "1" : "0");
//                mPath = FileCache.getDirForShakePhoto(getContext()).getPath() + "/" + filename;
                shakephoto_pre.setVisibility(View.VISIBLE);
                imageLoader.setShowWH(1024).DisplayImage(mPath, shakephoto_pre);
                takepicture.setImageResource(R.mipmap.camera_button2);
                takepicture2.setProgress(0);
                takepicture2.setVisibility(View.VISIBLE);
                isWaitUp = true;
                settingAgain(true);
            }
        } else {
            picturer_2_2.setVisibility(View.INVISIBLE);
            picturer_1_2.setText("取消");
            picturer_1_2.setVisibility(View.VISIBLE);
            clickShakephoto_free();
        }
        myHandler.sendEmptyMessage(5);
    }

    private void initSettings() {
        imageLoader = new ImageLoader(getContext());
        shakephotoFragment = this;
        sensorManager = (SensorManager) getContext().getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        holder = surfaceView.getHolder();
        holder.setKeepScreenOn(true);// 屏幕常亮
        holder.addCallback(new SurfaceCallback());// 为SurfaceView的句柄添加一个回调函数
        takepicture.setOnClickListener(this);
        takepicture2.setOnClickListener(this);
        shakephoto_zz.setOnClickListener(this);
        shakephoto_spin.setOnClickListener(this);
        shakephoto_light.setOnClickListener(this);
        picture.setOnClickListener(this);
        picturer_1_2.setOnClickListener(this);
        picturer_2_2.setOnClickListener(this);
        shakephoto_free.setOnClickListener(this);
        shakephoto_scene.setOnClickListener(this);
        shakephoto_labels_obs_sumbit.setOnClickListener(this);
    }

    private boolean isSavePhoto = false;//是否正在保存图片

    private final class MyPictureCallback implements Camera.PictureCallback {
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                String time = "";
                if (type == 1) {
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    time = sdf.format(date);
                }
                String filename = Tools.getTimeSS() + "";
                new SaveToSDCardAsyncTask(filename, time).execute(new Object[]{data});
                if (type == 1) {
                    camera.startPreview(); // 拍完照后，重新开始预览
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String mParameter = "";
    private String mPath = "";

    private class SaveToSDCardAsyncTask extends AsyncTask {
        String filename, filename2;
        String time;

        SaveToSDCardAsyncTask(String filename, String time) {
            isSavePhoto = true;
            this.filename = filename + ".jpg";
            this.filename2 = filename + "_2.jpg";
            this.time = time;
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Object doInBackground(Object[] params) {
            byte[] bytes = getBytesForexif((byte[]) params[0]);
            if (type == 2) {
                mParameter = getParameter(AppInfo.getName(getContext()), Tools.getToken(), nowSelAi.getAi_id(), "jpg", province, city, county, address, longitude,
                        latitude, dai_id, nowSelAi.getCat_id(), county, isShowLocation ? "1" : "0");
                mPath = FileCache.getDirForShakePhoto(getContext()).getPath() + "/" + filename;
                return saveToSDCard(filename, filename2, bytes);
            } else {
                String path = FileCache.getDirForShakePhoto(getContext()).getPath() + "/" + filename;
                AppInfo.setShakePhotoUrl(getContext(), path);
                return saveToSDCard(filename, filename2, bytes) && saveFreePicture(path
                        , FileCache.getDirForShakePhoto(getContext()).getPath() + "/" + filename2, time);
            }
        }

        protected void onPostExecute(Object o) {
            if ((boolean) o) {
                if (type == 2) {
                    takepicture.setImageResource(R.mipmap.camera_button2);
                    takepicture2.setProgress(0);
                    takepicture2.setVisibility(View.VISIBLE);
                    isWaitUp = true;
                    settingAgain(true);
                } else {
                    String path = FileCache.getDirForShakePhoto(getContext()).getPath() + "/" + filename;
                    picture.setVisibility(View.VISIBLE);
                    imageLoader.setShowWH(200).DisplayImage(path, picture);
                }
            } else {

            }
            isSavePhoto = false;
        }
    }

    private boolean isWaitUp = false;//是否等待上传

    /**
     * 将拍下来的照片存放在SD卡中
     */
    private boolean saveToSDCard(String filename, String filename2, byte[] data) {
        boolean resutValue = false;
        File fileFolder = FileCache.getDirForShakePhoto(getContext());
        File jpgFile = new File(fileFolder, filename);
//        File jpgFile2 = new File(fileFolder, filename2);
        FileOutputStream outputStream = null;
        FileOutputStream outputStream1 = null;
        FileOutputStream outputStream2 = null;
        try {
//            outputStream1 = new FileOutputStream(jpgFile1);
//            outputStream1.write(data);
//            outputStream1.flush();
////            ExifInterface exifInterface = new ExifInterface(jpgFile1.getPath());
////            Tools.d("or:" + exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION));
//            outputStream2 = new FileOutputStream(jpgFile2);
//            Tools.getBitmap(jpgFile1.getPath(), returnThumbnail, returnThumbnail).
//                    compress(Bitmap.CompressFormat.JPEG, 100, outputStream2);
//            jpgFile1.delete();
//            ExifInterface exifInterface = new ExifInterface(jpgFile2.getPath());
//            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, taskOrientation + "");
//            exifInterface.saveAttributes();
//            Tools.d("or:" + exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION));
//            for (int i = 0; i < data.length; i++) {
//                data[i] = (byte) (255 - data[i]);
//            }
            outputStream = new FileOutputStream(jpgFile);
            outputStream.write(data);
            outputStream.flush();
            Tools.d(jpgFile.getPath());
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

    private ShakeThemeInfo nowSelAi;//当前选择的活动

    private void creatViews() {
//        if (shakephoto_classify_point != null) {
//            shakephoto_classify_point.removeAllViews();
//        }
//        if (myPageAdapter != null)
//            myPageAdapter.notifyDataSetChanged();
//        createPoint();
    }

//    private void createPoint() {
//        int size = headlist.size();
//        if (size == 0) {
//            return;
//        }
//        int pointWH = Tools.dipToPx(getActivity(), 11);
//        for (int i = 0; i < size; i++) {
//            View view = new View(getContext());
//            view.setId(i);
//            view.setOnClickListener(pointClick);
//            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(pointWH, pointWH);
//            if (i > 0) {
//                lp.leftMargin = pointWH;
//                view.setBackgroundResource(R.drawable.shake_point2);
//            } else {
//                view.setBackgroundResource(R.drawable.shake_point1);
//            }
//            shakephoto_classify_point.addView(view, lp);
//            points.add(view);
//        }
//    }

//    private View.OnClickListener pointClick = new View.OnClickListener() {
//        public void onClick(View v) {
//            shakephoto_classify_viewpage.setCurrentItem(v.getId());
//        }
//    };

    private String dai_id = "";

    private final class SurfaceCallback implements SurfaceHolder.Callback {
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
                if (Tools.getDeviceModel().startsWith("BKL-") || Tools.getDeviceModel().startsWith("CLT-")) {
                    myHandler.sendEmptyMessageDelayed(3, 1000);
                }
                isFocus = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void surfaceCreated(SurfaceHolder holder) {
            if (!Utils.checkCameraHardware(getContext())) {
                Toast.makeText(getContext(), "摄像头打开失败！", Toast.LENGTH_SHORT).show();
                return;
            }
            is_flashmodle = AppInfo.getFLASH_MODLE_SHAKE(getContext());
            if (is_flashmodle) {
                shakephoto_light.setImageResource(R.mipmap.shake_ico8_1);
            } else {
                shakephoto_light.setImageResource(R.mipmap.shake_ico8);
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
                if (camera != null) {
                    camera.startPreview();
                    isFocus = false;
                    if (Tools.getDeviceModel().startsWith("BKL-") || Tools.getDeviceModel().startsWith("CLT-")) {
                        myHandler.sendEmptyMessageDelayed(3, 1000);
                    }
                }
            } catch (Exception e) {
                if (camera != null) {
                    camera.release(); // 释放照相机
                }
                camera = null;
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            if (camera != null) {
                camera.release(); // 释放照相机
                camera = null;
            }
        }
    }

    private boolean isback = true;

    public void onResume() {
        super.onResume();
        myHandler.sendEmptyMessageDelayed(5, 2000);
    }

    public void onPause() {
        super.onPause();
        myHandler.removeMessages(5);
    }

    public void onDestroyView() {
        super.onDestroyView();
        Tools.d("onDestroyView");
        isJoinActivity = false;
        myHandler.removeMessages(3);
        shakephotoFragment = null;
        nowSelAi = null;
        if (sensorManager != null)
            sensorManager.unregisterListener(this);
        if (camera != null) {
            camera.release(); // 释放照相机
            camera = null;
        }
        if (mSearch != null) {
            mSearch.destroy();
        }
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
//        if (mSearch != null) {
//            mSearch.destroy();
//            mSearch = null;
//        }
//        if (mLocationClient != null && mLocationClient.isStarted()) {
//            mLocationClient.stop();
//        }
    }

    private int cameraPosition = 1;

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
                        Tools.showToast(getContext(), "摄像头切换失败！");
                        camera = Camera.open();
                    }
                    try {
                        camera.setPreviewDisplay(holder);//通过surfaceview显示取景画面
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    updateCameraParameters();
                    camera.startPreview();//开始预览
                    if (Tools.getDeviceModel().startsWith("BKL-") || Tools.getDeviceModel().startsWith("CLT-")) {
                        myHandler.sendEmptyMessageDelayed(3, 1000);
                    }
                    isFocus = false;
                    cameraPosition = 0;
                    shakephoto_light.setVisibility(View.GONE);
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
                        Tools.showToast(getContext(), "摄像头切换失败！");
                        camera = Camera.open();
                    }
                    try {
                        camera.setPreviewDisplay(holder);//通过surfaceview显示取景画面
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    updateCameraParameters();
                    camera.startPreview();//开始预览
                    if (Tools.getDeviceModel().startsWith("BKL-") || Tools.getDeviceModel().startsWith("CLT-")) {
                        myHandler.sendEmptyMessageDelayed(3, 1000);
                    }
                    isFocus = false;
                    cameraPosition = 1;
                    shakephoto_light.setVisibility(View.VISIBLE);
                    break;
                }
            }

        }
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

    private void updateCameraParameters() {
        isFocus = false;
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
                    p.setPictureSize(Tools.getDpi(getActivity()), Tools.getScreeInfoWidth(getActivity()));
                }
                p.setPreviewSize(Tools.getDpi(getActivity()), Tools.getScreeInfoWidth(getActivity()));
                if (getActivity().getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
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
                        p.setPictureSize(Tools.getDpi(getActivity()), Tools.getScreeInfoWidth(getActivity()));
                    }
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
                        p.setPictureSize(Tools.getDpi(getActivity()), Tools.getScreeInfoWidth(getActivity()));
                        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
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

    /**
     * 设置闪光灯
     *
     * @param isON
     */
    private void settingFLASH_MODLE(boolean isON) {
        if (camera == null) {
            return;
        }
        camera.stopPreview();
        Camera.Parameters p = camera.getParameters();
        if (isON) {
            p.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
        } else {
            p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        }
        camera.setParameters(p);
        if (isON) {
            camera.startPreview();
            isFocus = false;
        } else {
            camera.release();
            camera = null;//取消原来摄像头
            try {
                if (cameraPosition == 1) {//后置
                    camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                } else {
                    camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                }
            } catch (Exception e) {
                Tools.d("摄像头打开失败！");
                Tools.showToast(getContext(), "摄像头打开失败！");
                camera = Camera.open();
            }
            try {
                camera.setPreviewDisplay(holder);//通过surfaceview显示取景画面
            } catch (IOException e) {
                e.printStackTrace();
            }
            updateCameraParameters();
            camera.startPreview();//开始预览
            isFocus = false;
        }
        if (Tools.getDeviceModel().startsWith("BKL-") || Tools.getDeviceModel().startsWith("CLT-")) {
            myHandler.sendEmptyMessageDelayed(3, 1000);
        }
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
        if (AppInfo.getFLASH_MODLE_SHAKE(getContext())) {
            p.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
        }
        p.setJpegQuality(100);
        List<Camera.Size> pszize = p.getSupportedPictureSizes();
        if (pszize != null && !pszize.isEmpty()) {
            Collections.sort(pszize, new SizeComparator());
            Camera.Size previewSize = null;
            for (Camera.Size size1 : pszize) {
                if ((size1.width <= 1080 && size1.height <= 1920) || (size1.width <= 1920 && size1.height <= 1080)) {
                    previewSize = size1;
                    break;
                }
            }
            if (previewSize == null) {
                if (pszize.size() < 3) {
                    previewSize = pszize.get(1);
                } else {
                    previewSize = pszize.get(2);
                }
            }
            Tools.d(previewSize.width + ":" + previewSize.height);
            p.setPictureSize(previewSize.width, previewSize.height);
        } else {
            p.setPictureSize(Tools.getScreeInfoWidth(getContext()), Tools.getScreeInfoHeight(getContext()));
        }
//        List<Camera.Size> previewSizeList = p.getSupportedPreviewSizes();
//        Camera.Size preSize = getProperSize(previewSizeList, ((float) Tools.getScreeInfoWidth(this)) / Tools.getScreeInfoHeight
//                (this));
        Camera.Size previewSize = camera.new Size(Tools.getDpi(getContext()), Tools.getScreeInfoWidth(getContext()));
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

    private class SizeComparator implements Comparator<Camera.Size> {
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            Camera.Size size1 = lhs;
            Camera.Size size2 = rhs;
            if (size1.width > size2.width || size1.width == size2.width && size1.height > size2.height) {
                return -1;
            } else if (!(size1.width == size2.width && size1.height == size2.height)) {
                return 1;
            }
            return 0;
        }
    }

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
//        int az = (int) values[2];
//        Tools.d("ax:" + ax + ",ay:" + ay + ",az:" + az);
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

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * 拍照时存的部分
     */
    private String getParameter(String usermobile, String token, String ai_id,
                                String file_type, String province, String city,
                                String county, String address, String longitud, String latitude, String dai_id, String cat_id,
                                String area, String show_address) {
        if (dai_id == null) {
            dai_id = "";
        }
        if (cat_id == null) {
            cat_id = "";
        }
        if (area == null) {
            area = "";
        }
        if (county == null) {
            county = "";
        }
        if (address == null) {
            address = "";
        }
        String parametes = null;
        try {
            parametes = "usermobile=" + URLEncoder.encode(usermobile, "utf-8") +
                    "&token=" + URLEncoder.encode(token, "utf-8") +
                    "&ai_id=" + URLEncoder.encode(ai_id, "utf-8") +
//                    "&oss_name=" + URLEncoder.encode(oss_name, "utf-8") + 上传时存
//                    "&show_name=" + URLEncoder.encode(show_name, "utf-8") + 上传时存
                    "&file_type=" + URLEncoder.encode(file_type, "utf-8") +
//                    "&file_url=" + URLEncoder.encode(file_url, "utf-8") +   上传时存
//                    "&key_concent=" + URLEncoder.encode(key_concent, "utf-8") +   点击上传时存
                    "&province=" + URLEncoder.encode(province, "utf-8") +
                    "&city=" + URLEncoder.encode(city, "utf-8") +
                    "&county=" + URLEncoder.encode(county, "utf-8") +
                    "&address=" + URLEncoder.encode(address, "utf-8") +
                    "&longitude=" + URLEncoder.encode(longitud, "utf-8") +
                    "&latitude=" + URLEncoder.encode(latitude, "utf-8") +
                    "&dai_id=" + URLEncoder.encode(dai_id, "utf-8") +
                    "&cat_id=" + URLEncoder.encode(cat_id, "utf-8") +
                    "&area=" + URLEncoder.encode(area, "utf-8") +
                    "&show_address=" + URLEncoder.encode(show_address, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return parametes;
    }

    private int type = 1;//（1为自由拍，2为场景拍）

    public boolean saveFreePicture(String path, String path2, String time) {
        return appDBHelper.addShakePhoto(path, path2, county, time, longitude, latitude, address, province, city, county);
    }

    private final int jumpshakealbum = 0x10;

    //点击事件
    public void onClick(View v) {
        if (TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
            ConfirmDialog.showDialog(getContext(), null, 2,
                    getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                        public void leftClick(Object object) {
                        }

                        public void rightClick(Object object) {
                            Intent intent = new Intent(getContext(), IdentifycodeLoginActivity.class);
                            startActivity(intent);
                        }
                    });
            return;
        }
        if (!isuppic && camera == null) {
            ConfirmDialog.showDialog(getContext(), "请检查摄像头权限", null, "取消", "设置", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                public void leftClick(Object object) {
                }

                public void rightClick(Object object) {
                    Intent localIntent = new Intent();
                    localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (Build.VERSION.SDK_INT >= 9) {
                        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        localIntent.setData(Uri.fromParts("package", "com.orange.oy", null));
                    } else if (Build.VERSION.SDK_INT <= 8) {
                        localIntent.setAction(Intent.ACTION_VIEW);
                        localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
                        localIntent.putExtra("com.android.settings.ApplicationPkgName", "com.orange.oy");
                    }
                    startActivity(localIntent);
                }
            }).goneLeft();
            return;
        }
        switch (v.getId()) {
            case R.id.picture: {
                Intent intent = new Intent(getContext(), LeftActivity.class);
                intent.putExtra("dai_id", "");
                startActivity(intent);
            }
            break;
            case R.id.shakephoto_light: {//开启/关闭闪光灯
                is_flashmodle = !is_flashmodle;
                AppInfo.setFLASH_MODLE_SHAKE(getContext(), is_flashmodle);
                settingFLASH_MODLE(is_flashmodle);
                if (is_flashmodle) {
                    shakephoto_light.setImageResource(R.mipmap.shake_ico8_1);
                } else {
                    shakephoto_light.setImageResource(R.mipmap.shake_ico8);
                }
            }
            break;
            case R.id.shakephoto_spin: {//反转摄像头
                spinCamerase();
            }
            break;
            case R.id.takepicture2: {
                if (takepicture2.getProgress() <= 0) {
                    if (TextUtils.isEmpty(mPath) || TextUtils.isEmpty(mParameter)) {
                        Tools.showToast(getContext(), "数据异常，请重新拍照");
                        ScreenManager.getScreenManager().finishActivity(ShakephotoActivity.class);
                        return;
                    }
                    if (nowSelAi == null) {
                        Tools.showToast(getContext(), "请选择活动");
                        return;
                    }
                    if (shakephoto_labels == null) {
                        Tools.showToast(getContext(), "请添加标签");
                        return;
                    }
                    String labels = "";
                    int count = shakephoto_labels.getChildCount();
                    for (int i = 0; i < count; i++) {
                        View view = shakephoto_labels.getChildAt(i);
                        if ("1".equals(view.getId() + "")) {
                            if (TextUtils.isEmpty(labels)) {
                                labels = ((TextView) view).getText().toString();
                            } else {
                                labels = labels + "," + ((TextView) view).getText().toString();
                            }
                        }
                    }
                    if (TextUtils.isEmpty(labels)) {
                        Tools.showToast(getContext(), "请选择标签");
                        return;
                    }
                    try {
                        labels = URLEncoder.encode(labels, "utf-8");
                        mParameter += "&key_concent=" + labels;
                        AppDBHelper appDBHelper = new AppDBHelper(getContext());
                        appDBHelper.addShakePhotoUpdata(mPath, mParameter);
                        Intent service = new Intent("com.orange.oy.ShakephotoUpdataService");
                        service.setPackage("com.orange.oy");
                        getContext().startService(service);
                        shakephoto_labels.setVisibility(View.GONE);
                        shakephoto_labels_obs.setVisibility(View.INVISIBLE);
                        shakephoto_labels_obs_edittext.setText("");
                        isWaitUp = false;
                        ScreenManager.getScreenManager().finishActivity(ShakephotoActivity.class);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        Tools.showToast(getContext(), "标签名不合法");
                    }
                }
            }
            break;
            case R.id.takepicture: {
                if (type == 2) {//场景拍
                    selectScene();
                } else {
                    picturer_1_2.setText("返回");
                    selectFree();
                }
            }
            break;
            case R.id.shakephoto_location2_ico:
            case R.id.shakephoto_location2: {
                if (nowSelAi == null) {
                    Tools.showToast(getContext(), "请选择活动");
                    return;
                }
                Intent intent = new Intent(getContext(), ShakeSelectLocationActivity.class);
                intent.putExtra("isShowLocation", isShowLocation);
                intent.putExtra("place_name", nowSelAi.getPlace_name());
                intent.putExtra("province", nowSelAi.getProvince());
                intent.putExtra("city", nowSelAi.getCity());
                intent.putExtra("county", nowSelAi.getCounty());
                intent.putExtra("address", nowSelAi.getAddress());
                intent.putExtra("isPrecise", "1".equals(nowSelAi.getLocation_type()));
                startActivityForResult(intent, 1);
            }
            break;
            case R.id.picturer_2_2://分类-返回
            case R.id.picturer_1_2: {
//                settingTheme(false);
                ScreenManager.getScreenManager().finishActivity(ShakephotoActivity.class);
            }
            break;
            case R.id.picturer_1:
            case R.id.picturer_2: {//分类
                settingTheme(true);
            }
            break;
            case R.id.shakephoto_free: {//自由拍
                clickShakephoto_free();
            }
            break;
            case R.id.shakephoto_scene: {//场景拍
                clickShakephoto_scene();
            }
            break;
            case R.id.shakephoto_labels_obs_sumbit: {//上传评论
                String obs = shakephoto_labels_obs_edittext.getText().toString();
                if (TextUtils.isEmpty(obs)) {
                    Tools.showToast(getContext(), "还没写评论呢～");
                    return;
                }
                try {
                    String obsutf = "&comment=" + URLEncoder.encode(obs, "utf-8");
                    mParameter += obsutf;
                    shakephoto_labels_obs.setVisibility(View.INVISIBLE);
                    shakephoto_labels_obs_edittext.setText("");
                    Tools.showToast(getContext(), "评论成功");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Tools.showToast(getContext(), "请去掉特殊字符～");
                }
            }
            break;
        }
    }

    private void clickShakephoto_free() {
        String path = AppInfo.getShakePhotoUrl(getContext());
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            imageLoader.setShowWH(200).DisplayImage(path, picture);
        } else {
            picture.setImageResource(R.mipmap.dingwei);
        }
        type = 1;
        settingTheme(false);
        shakephoto_free.setBackgroundResource(R.drawable.change_task2_1);
        shakephoto_scene.setBackgroundResource(getResources().getColor(R.color.sobot_transparent));
        shakephoto_labels.setVisibility(View.GONE);
        shakephoto_labels_obs.setVisibility(View.INVISIBLE);
        picturer_1_1.setVisibility(View.GONE);
        picturer_2_1.setVisibility(View.GONE);
        takepicture.setVisibility(View.VISIBLE);
        takepicture2.setVisibility(View.GONE);
    }

    private void clickShakephoto_scene() {
        type = 2;
        settingTheme(false);
        shakephoto_free.setBackgroundResource(getResources().getColor(R.color.sobot_transparent));
        shakephoto_scene.setBackgroundResource(R.drawable.change_task2_2);
        shakephoto_labels.setVisibility(View.VISIBLE);
    }

    /**
     * 自由拍执行方法
     */
    private void selectFree() {
//        if (TextUtils.isEmpty(latitude) || TextUtils.isEmpty(longitude)) {
//            Tools.showToast(getContext(), "未定位到您的位置，请您先进行定位");
//            return;
//        }
        if (isAutoFocus) {
            camera.cancelAutoFocus();
        }
        camera.autoFocus(new Camera.AutoFocusCallback() {
            public void onAutoFocus(boolean arg0, Camera arg1) {
                if (camera != null) {
                    taskOrientation = exifOrientation;
                    camera.takePicture(null, null, new MyPictureCallback());
                    camera.cancelAutoFocus();
                }
            }
        });
    }

    /**
     * 场景拍的执行方法
     */
    private void selectScene() {
        if (nowSelAi == null) {
            Tools.showToast(getContext(), "请选择活动");
            return;
        }
//        if (TextUtils.isEmpty(latitude) || TextUtils.isEmpty(longitude)) {
//            Tools.showToast(getContext(), "未定位到您的位置，请您先进行定位");
//            return;
//        }
//        if ("1".equals(nowSelAi.getLocation_type())) {
//            LatLng p1 = new LatLng(Tools.StringToDouble(latitude), Tools.StringToDouble(longitude));
//            LatLng p2 = new LatLng(Tools.StringToDouble(nowSelAi.getLatitude()), Tools.StringToDouble(nowSelAi.getLongitude()));
//            double result = DistanceUtil.getDistance(p1, p2);
//            if (result > 3000) {
//                ConfirmDialog.showDialog(getContext(), "提示", 3, "请到达指定位置拍摄", "", "我知道了", null, true, null).goneLeft();
//                return;
//            }
//        }
        if (!isFocus) {
            if (isSavePhoto) {
                Tools.showToast(getContext(), "偶业正在处理上一张图片，请稍等片刻～");
                return;
            }
            isFocus = true;
            if (isAutoFocus) {
                camera.cancelAutoFocus();
            }
            camera.autoFocus(new Camera.AutoFocusCallback() {
                public void onAutoFocus(boolean arg0, Camera arg1) {
                    if (camera != null) {
                        taskOrientation = exifOrientation;
                        camera.takePicture(null, null, new MyPictureCallback());
                        camera.cancelAutoFocus();
                    }
                }
            });
        }
    }

    private boolean isFocus = false;//是否正在聚焦
    private boolean isShowLocation = true;

    private boolean isAutoFocus = false;

    private class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 3: {//聚焦
                    if (!isFocus && !isSavePhoto) {
                        try {
                            camera.autoFocus(new Camera.AutoFocusCallback() {
                                public void onAutoFocus(boolean success, Camera camera) {
//                                    myHandler.sendEmptyMessageDelayed(3, 1000);
                                }
                            });
                            isAutoFocus = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            isAutoFocus = false;
                        }
                    } else {
                        myHandler.sendEmptyMessageDelayed(3, 5000);
                    }
                }
                break;
            }
        }
    }

    /**
     * 定位客户端
     */
    public LocationClient mLocationClient = null;
    public MyLocationListenner myListener = new MyLocationListenner();
    private GeoCoder mSearch = null;
    public double location_latitude, location_longitude;
    private String province = "", city = "", county = "", address = "", longitude = "", latitude = "", location_name = "";

    /**
     * 初始化定位
     */
    private void initLocation() {
        if (mLocationClient != null && mLocationClient.isStarted()) {
            return;
        }
        location_latitude = 0;
        location_longitude = 0;
        if (mSearch == null) {
            mSearch = GeoCoder.newInstance();
            mSearch.setOnGetGeoCodeResultListener(onGetGeoCoderResultListener);
        }
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(getContext());
            mLocationClient.registerLocationListener(myListener);
            setLocationOption();
        }
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

    public class MyLocationListenner implements BDLocationListener {

        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }
            location_latitude = location.getLatitude();
            location_longitude = location.getLongitude();
            address = location.getAddress().address;
            longitude = location_longitude + "";
            latitude = location_latitude + "";
            LatLng ptCenter = new LatLng(location_latitude, location_longitude);
            mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ptCenter));
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
            province = reverseGeoCodeResult.getAddressDetail().province;
            city = reverseGeoCodeResult.getAddressDetail().city;
            county = reverseGeoCodeResult.getAddressDetail().district;
        }
    };
}
