package com.orange.oy.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.VideoSource;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.util.FileCache;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 视频播放控件
 */
public class MovieRecorderView extends LinearLayout implements OnErrorListener {

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private ProgressBar mProgressBar;

    private MediaRecorder mMediaRecorder;
    private Camera mCamera;
    private Timer mTimer;// 计时器
    private OnRecordFinishListener mOnRecordFinishListener;// 录制完成回调接口

    private int mWidth;// 视频分辨率宽度
    private int mHeight;// 视频分辨率高度
    private boolean isOpenCamera;// 是否一开始就打开摄像头
    private int mRecordMaxTime;// 一次拍摄最长时间
    private int mTimeCount;// 时间计数
    private TextView progressBar_txt;
    private File mRecordFile = null;// 文件

    public interface OnRecordTypeChangeListener {
        void start();

        void exception(Exception e);
    }

    private OnRecordTypeChangeListener onRecordTypeChangeListener;

    public void setOnRecordTypeChangeListener(OnRecordTypeChangeListener onRecordTypeChangeListener) {
        this.onRecordTypeChangeListener = onRecordTypeChangeListener;
    }

    public MovieRecorderView(Context context) {
        this(context, null);
    }

    public MovieRecorderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public MovieRecorderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // 初始化各项组件
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MovieRecorderView, defStyle, 0);
        mWidth = a.getInteger(R.styleable.MovieRecorderView_video_width, 640);// 默认320 176
        mHeight = a.getInteger(R.styleable.MovieRecorderView_video_height, 480);// 默认240 144

        isOpenCamera = a.getBoolean(R.styleable.MovieRecorderView_is_open_camera, true);// 默认打开
        mRecordMaxTime = a.getInteger(R.styleable.MovieRecorderView_record_max_time, 180000);// 默认为10  3分钟 3000分钟
        LayoutInflater.from(context).inflate(R.layout.movie_recorder_view, this);
        progressBar_txt = (TextView) findViewById(R.id.progressBar_txt);
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setMax(mRecordMaxTime);// 设置进度条最大量

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(new CustomCallBack());
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        a.recycle();
    }

    public void setmRecordMaxTime(int mRecordMaxTime) {
        this.mRecordMaxTime = mRecordMaxTime;
        mProgressBar.setMax(mRecordMaxTime);
    }

    public void hideProgressBar() {
        if (progressBar_txt != null) {
            progressBar_txt.setVisibility(View.GONE);
        }
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    /**
     * @date 2015-2-5
     */
    private class CustomCallBack implements Callback {
        public void surfaceCreated(SurfaceHolder holder) {
            if (!isOpenCamera)
                return;
            try {
                initCamera();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            if (!isOpenCamera)
                return;
            freeCameraResource();
        }
    }

    /**
     * 初始化摄像头
     *
     * @throws IOException
     */
    private void initCamera() throws IOException {
        if (mCamera != null) {
            freeCameraResource();
        }
        try {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        } catch (Exception e) {
            e.printStackTrace();
            freeCameraResource();
        }
        if (mCamera == null)
            return;
//        List<int[]> range = mCamera.getParameters().getSupportedPreviewFpsRange();
//        for (int j = 0; j < range.size(); j++) {
//            int[] r = range.get(j);
//            for (int k = 0; k < r.length; k++) {
//                Log.d("zpf", "------" + r[k]);
//            }
//        }
        // setCameraParams();
        try {
//            if (isInit)
            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewDisplay(mSurfaceHolder);
            Camera.Parameters parameters = mCamera.getParameters();
//            parameters.setRecordingHint(true);
//            mCamera.setParameters(parameters);
            List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
            int temp, temp2 = 0;
            for (Camera.Size size : supportedPreviewSizes) {
                Tools.d("width:" + size.width + ",height:" + size.height);
                temp = size.width * size.height;
                if (temp < videoBitRate) {
                    temp2 = temp;
                } else if (temp == videoBitRate) {
                    break;
                } else {
                    if (temp2 != 0)
                        videoBitRate = temp2;
                    break;
                }
            }
            mCamera.startPreview();
            mCamera.unlock();
        } catch (IOException e) {
            Toast.makeText(getContext(), "摄像头调取失败", Toast.LENGTH_SHORT).show();
        } catch (RuntimeException e) {
            Toast.makeText(getContext(), "摄像头信息获取失败，请检查摄像头权限是否开启！", Toast.LENGTH_SHORT).show();
            mCamera.release();
            mCamera = null;
        }
    }

    private int videoBitRate = 1920 * 1080;
    /**
     * 设置摄像头为竖屏
     *
     */
    /*private void setCameraParams() {
        if (mCamera != null) {
            Parameters params = mCamera.getParameters();
            params.set("orientation", "portrait");
            mCamera.setParameters(params);
        }
    }*/

    /**
     * 释放摄像头资源
     */
    public void freeCameraResource() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.lock();
            mCamera.release();
            mCamera = null;
        }
    }

    private boolean createRecordDir(String dirName, String name) {
//        File sampleDir = new File(Environment.getExternalStorageDirectory() + File.separator + "im/video/");
//        if (!sampleDir.exists()) {
//            sampleDir.mkdirs();
//        }
//        File vecordDir = sampleDir;
        File vecordDir = FileCache.getDirForVideo(getContext(), dirName);
        // 创建文件
        try {
            mRecordFile = new File(vecordDir, name + ".mp4");
            if (mRecordFile.exists()) {
                mRecordFile.delete();
            }
            mRecordFile.createNewFile();
//            mRecordFile = File.createTempFile("recording", ".mp4", vecordDir); //mp4格式
            Tools.d(mRecordFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 初始化 TODO
     *
     * @throws IOException
     */
    private void initRecord() throws IOException {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.reset(); //TODO
        if (mCamera != null)
            mMediaRecorder.setCamera(mCamera);
//        mMediaRecorder.setOnErrorListener(this); TODO
        mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
        mMediaRecorder.setVideoSource(VideoSource.CAMERA);// 视频源
        mMediaRecorder.setAudioSource(AudioSource.MIC);// 音频源
//        mMediaRecorder.setOutputFormat(OutputFormat.MPEG_4);// 视频输出格式
//        mMediaRecorder.setAudioEncoder(AudioEncoder.AMR_NB);// 音频格式
//        mMediaRecorder.setAudioSamplingRate(22050);//音频采样率 44100 22050
//        mMediaRecorder.setVideoSize(640, 480);// 设置分辨率：
//        mMediaRecorder.setVideoEncodingBitRate(1 * 1280 * 720);// 设置帧频率 1 * 1280 * 720  352 * 288
        CamcorderProfile cProfile;
        try {
            cProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
        } catch (RuntimeException e) {
            e.printStackTrace();
            cProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        }
        cProfile.videoBitRate = videoBitRate;
//        cProfile.videoCodec = VideoEncoder.MPEG_4_SP;
        mMediaRecorder.setProfile(cProfile);
        mMediaRecorder.setOrientationHint(90);// 输出旋转90度，保持竖屏录制
//        mMediaRecorder.setVideoEncoder(VideoEncoder.MPEG_4_SP);// 视频录制格式 MPEG_4_SP
        // mediaRecorder.setMaxDuration(Constant.MAXVEDIOTIME * 1000);
        mMediaRecorder.setOutputFile(mRecordFile.getAbsolutePath());
//        mMediaRecorder.setMaxFileSize(1024 * 1024 * 3);z
        /*******************************/
//        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
//        mMediaRecorder.setVideoFrameRate(30);// 这个去掉了，感觉没什么用
        /******************************/
        mMediaRecorder.prepare();
        try {
            mMediaRecorder.start();
            if (onRecordTypeChangeListener != null) {
                onRecordTypeChangeListener.start();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Tools.showToast(getContext(), "视频输出异常！");
            if (onRecordTypeChangeListener != null) {
                onRecordTypeChangeListener.exception(e);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            Tools.showToast(getContext(), "运行异常！");
            if (onRecordTypeChangeListener != null) {
                onRecordTypeChangeListener.exception(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Tools.showToast(getContext(), "未知错误！");
            if (onRecordTypeChangeListener != null) {
                onRecordTypeChangeListener.exception(e);
            }
        }
    }

    private Handler timerHandler = new Handler() {
        public void handleMessage(Message msg) {
            int temp = mTimeCount / 60;
            progressBar_txt.setText(temp + ":" + (mTimeCount - temp * 60));
            super.handleMessage(msg);
        }
    };

    /**
     * 开始录制视频
     *
     * @param onRecordFinishListener 达到指定时间之后回调接口
     */
    public void record(String dirName, String name, final OnRecordFinishListener onRecordFinishListener) {
        this.mOnRecordFinishListener = onRecordFinishListener;
        if (!createRecordDir(dirName, name)) {
            Tools.showToast(getContext(), "无法进行文件写入！请检查是否进行了限制！");
            return;
        }
        try {
            if (!isOpenCamera || mCamera == null)// 如果未打开摄像头，则打开
                initCamera();
            initRecord();
            mTimeCount = 0;// 时间计数器重新赋值
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                public long scheduledExecutionTime() {
                    return super.scheduledExecutionTime();
                }

                public void run() {
                    mTimeCount++;
                    mProgressBar.setProgress(mTimeCount);// 设置进度条
                    timerHandler.sendEmptyMessage(0);
                    if (mTimeCount == mRecordMaxTime) {// 达到指定时间，停止拍摄
                        stop();
                        if (mOnRecordFinishListener != null)
                            mOnRecordFinishListener.onRecordFinish();
                    }
                }
            }, 0, 1000);
//            }
        } catch (IOException e) {
            Tools.showToast(getContext(), "摄像头配置失败！");
            if (onRecordTypeChangeListener != null) {
                onRecordTypeChangeListener.exception(e);
            }
        }
    }

    private boolean isInit = true;

    /**
     * 开始录制视频
     *
     * @param onRecordFinishListener 达到指定时间之后回调接口
     */
    public boolean record2(String dirName, String name, final OnRecordFinishListener onRecordFinishListener) {
        this.mOnRecordFinishListener = onRecordFinishListener;
        if (!createRecordDir(dirName, name)) {
            Tools.showToast(getContext(), "无法进行文件写入！请检查是否进行了限制！");
            return false;
        }
        try {
            if (!isOpenCamera)// 如果未打开摄像头，则打开
                initCamera();
            if (!isInit) {
                initCamera();
            }
            initRecord();
            isInit = false;
//            mTimeCount = 0;// 时间计数器重新赋值
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                public long scheduledExecutionTime() {
                    return super.scheduledExecutionTime();
                }

                public void run() {
                    mTimeCount++;
                    Tools.d("movie----" + mTimeCount);
                    mProgressBar.setProgress(mTimeCount);// 设置进度条
                    timerHandler.sendEmptyMessage(0);
                    if (mTimeCount == mRecordMaxTime) {// 达到指定时间，停止拍摄
                        stop();
                        if (mOnRecordFinishListener != null)
                            mOnRecordFinishListener.onRecordFinish();
                    }
                }
            }, 0, 1000);
//            }
            return true;
        } catch (IOException e) {
            Tools.showToast(getContext(), "摄像头配置失败！");
            if (onRecordTypeChangeListener != null) {
                onRecordTypeChangeListener.exception(e);
            }
            return false;
        }
    }

    /**
     * 停止拍摄
     */
    public void stop() {
        stopRecord();
        releaseRecord();
        freeCameraResource();
    }

    /**
     * 停止录制
     */
    public void stopRecord() {
        mProgressBar.setProgress(0);
        if (mTimer != null)
            mTimer.cancel();
        if (mMediaRecorder != null) {
            // 设置后不会崩
            mMediaRecorder.setOnErrorListener(null);
            try {
                mMediaRecorder.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
//            mMediaRecorder.reset();TODO
            mMediaRecorder.setPreviewDisplay(null);
        }
    }

    /**
     * 释放资源
     */
    private void releaseRecord() {
        if (mMediaRecorder != null) {
            mMediaRecorder.setOnErrorListener(null);
            try {
                mMediaRecorder.release();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mMediaRecorder = null;
    }

    public int getTimeCount() {
        return mTimeCount;
    }

    /**
     * @return the mVecordFile
     */
    public File getmRecordFile() {
        return mRecordFile;
    }

    /**
     * 录制完成回调接口
     */
    public interface OnRecordFinishListener {
        public void onRecordFinish();
    }


    public void onError(MediaRecorder mr, int what, int extra) {
        try {
            if (mr != null)
                mr.reset();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}