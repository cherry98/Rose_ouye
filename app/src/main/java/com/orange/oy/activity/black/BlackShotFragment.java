package com.orange.oy.activity.black;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.orange.oy.R;
import com.orange.oy.allinterface.BlackShotlifeListener;
import com.orange.oy.allinterface.BlackworkCloseListener;
import com.orange.oy.base.BaseFragment;
import com.orange.oy.base.Tools;
import com.orange.oy.view.MovieRecorderView;

/**
 * 录制页
 */
public class BlackShotFragment extends BaseFragment {
    private MovieRecorderView mRecorderView;
    private ImageView mShootBtn;
    private String dirName, fileName;
    private View mView;
    private BlackworkCloseListener blackworkCloseListener;
    private ShotstateChangeListener shotstateChangeListener;
    private BlackShotlifeListener blackShotlifeListener;
    private String keys = "";
    private String values = "";

    public interface ShotstateChangeListener {
        void on();

        void off();
    }

    public void setBlackShotlifeListener(BlackShotlifeListener blackShotlifeListener) {
        this.blackShotlifeListener = blackShotlifeListener;
    }

    public void setShotstateChangeListener(ShotstateChangeListener shotstateChangeListener) {
        this.shotstateChangeListener = shotstateChangeListener;
    }

    public void setBlackworkCloseListener(BlackworkCloseListener blackworkCloseListener) {
        this.blackworkCloseListener = blackworkCloseListener;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_shot, container, false);
        return mView;
    }

    private void baseFinish() {
        if (blackworkCloseListener != null) {
            blackworkCloseListener.close(null);
        }
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = getArguments();
        fileName = data.getString("fileName");
        dirName = data.getString("dirName");
        int videotime = data.getInt("videotime");
        mRecorderView = (MovieRecorderView) mView.findViewById(R.id.movieRecorderView);
        mRecorderView.hideProgressBar();
        mRecorderView.setmRecordMaxTime(videotime);
        mShootBtn = (ImageView) mView.findViewById(R.id.shoot_button);
        mShootBtn.setVisibility(View.GONE);
        mRecorderView.setOnRecordTypeChangeListener(new MovieRecorderView.OnRecordTypeChangeListener() {
            public void start() {
            }

            public void exception(Exception e) {
                Tools.d(e.getMessage() + "");
                baseFinish();
            }
        });
        if (blackShotlifeListener != null) {
            blackShotlifeListener.onResume();
        }
    }

    private int index2 = 1;

    public boolean isVoicePermission() {
        try {
            AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.MIC, 22050, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, AudioRecord.getMinBufferSize(22050, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT));
            record.startRecording();
            int recordingState = record.getRecordingState();
            if (recordingState == AudioRecord.RECORDSTATE_STOPPED) {
                return false;
            }
            record.release();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void onResume() {
        super.onResume();
//        if (blackShotlifeListener != null) {
//            blackShotlifeListener.onResume();
//        }
    }

    public void resumeRecordMovie() {
        if (blackShotlifeListener != null) {
            blackShotlifeListener.onResume();
        }
    }

    public void startRecordMovie() {
        if (!isVoicePermission()) {
            Tools.showToast(getContext(), "录音功能不可用，请检查录音权限是否开启！");
            baseFinish();
        } else {
            if (mRecorderView.record2(dirName, fileName + "_" + index2++, new MovieRecorderView.OnRecordFinishListener() {
                public void onRecordFinish() {
                    handler.sendEmptyMessage(1);
                }
            })) {
                mShootBtn.setTag("停止");
                if (shotstateChangeListener != null) {
                    shotstateChangeListener.on();
                }
            }
        }
    }

    public void stopRecordMovie() {
        if (mShootBtn.getTag() == null) {
            return;
        }
        try {
            mRecorderView.stop();
            mShootBtn.setTag(null);
            if (shotstateChangeListener != null) {
                shotstateChangeListener.off();
            }
            if (mRecorderView != null && mRecorderView.getmRecordFile().exists() && mRecorderView.getmRecordFile().length() > 0) {
                if (TextUtils.isEmpty(keys)) {
                    keys = index2 + "";
                } else {
                    keys = keys + "," + index2;
                }
                if (TextUtils.isEmpty(values)) {
                    values = mRecorderView.getmRecordFile().getAbsolutePath();
                } else {
                    values = values + "," + mRecorderView.getmRecordFile().getAbsolutePath();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mRecorderView.stop();
    }

    public void onPause() {
        super.onPause();
        stopRecordMovie();
    }

    public void onDestroy() {
        super.onDestroy();
        if (mRecorderView != null)
            mRecorderView.freeCameraResource();
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            finishActivity();
        }
    };

    private void finishActivity() {
        if (blackworkCloseListener != null) {
            mRecorderView.stop();
            mShootBtn.setTag(null);
            if (mRecorderView != null)
                mRecorderView.freeCameraResource();
            mShootBtn.setTag(null);
            if (mRecorderView.getmRecordFile().exists() && mRecorderView.getmRecordFile().length() > 0) {
                if (TextUtils.isEmpty(keys)) {
                    keys = index2 + "";
                } else {
                    keys = keys + "," + index2;
                }
                if (TextUtils.isEmpty(values)) {
                    values = mRecorderView.getmRecordFile().getAbsolutePath();
                } else {
                    values = values + "," + mRecorderView.getmRecordFile().getAbsolutePath();
                }
            }
            if (TextUtils.isEmpty(values)) {
                blackworkCloseListener.close(null);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("keys", keys);
                bundle.putString("values", values);
                blackworkCloseListener.close(bundle);
            }
            keys = "";
            values = "";
        } else {
            //TODO
        }
    }

}
