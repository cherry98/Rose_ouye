package com.orange.oy.view;

import android.app.AlertDialog;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.allinterface.OnTaskQuestionSumbitListener;
import com.orange.oy.allinterface.TaskEditClearListener;
import com.orange.oy.base.Tools;
import com.orange.oy.info.TaskQuestionInfo;
import com.orange.oy.reord.AudioManager;
import com.orange.oy.reord.Constant;
import com.orange.oy.reord.DialogManager;
import com.orange.oy.util.FileCache;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 填空题
 */
public class TaskEditView2 extends LinearLayout implements View.OnClickListener, View.OnTouchListener, AudioManager
        .OnVolumeChangeListener, TaskEditClearListener {
    private EditText editText;
    private OnTaskQuestionSumbitListener onTaskQuestionSumbitListener;
    private ImageView task_question_record;
    private TextView task_question_play;
    private boolean isRecord = false;
    private Button task_question_presstalk;
    private float downY;
    private DialogManager dialogManager;
    private AudioManager audioManager;
    private AlertDialog recordDialogShow;
    private long time, downTime, number = 9;//9==10
    private MainHander mainHander;
    private String url = null;
    private boolean isCanceled = false, isLastTime = false;
    private Context context;
    private String project_id, task_id, store_id;
    private View task_question_reset;
    private boolean isOnlyRec = false;

    public void setData(String project_id, String task_id, String store_id) {
        this.project_id = project_id;
        this.store_id = store_id;
        this.task_id = task_id;
    }

    public TaskEditView2(Context context, String title, boolean isrequired) {
        this(context, title, isrequired, false);
    }

    /**
     * @param context
     * @param title
     * @param isrequired
     * @param isRecord   是否为语音题
     */
    public TaskEditView2(Context context, String title, boolean isrequired, boolean isRecord) {
        super(context);
        this.context = context;
        Tools.loadLayout(this, R.layout.view_task_question_edit2);
        task_question_reset = findViewById(R.id.task_question_reset);
        editText = (EditText) findViewById(R.id.task_question_edit_edit);
        task_question_presstalk = (Button) findViewById(R.id.task_question_presstalk);
        ((TextView) findViewById(R.id.task_question_edit_name)).setText(title);
        task_question_record = (ImageView) findViewById(R.id.task_question_record);
        task_question_play = (TextView) findViewById(R.id.task_question_play);
        task_question_play.setOnClickListener(this);
        task_question_record.setOnClickListener(this);
        task_question_presstalk.setOnTouchListener(this);
        task_question_reset.setOnClickListener(this);
        mainHander = new MainHander();
        if (isrequired) {
            findViewById(R.id.task_question_edit_img).setVisibility(VISIBLE);
        }
        if (isOnlyRec = isRecord) {//如果是专门的语音题将maxtime设置为相对最大值
            task_question_record.setVisibility(GONE);
            task_question_presstalk.setVisibility(VISIBLE);
            editText.setVisibility(GONE);
            task_question_play.setVisibility(GONE);
            MaxTime = 86400 * 2;//相对无限大，（PS:能一直按两天的变态请让他来找我）
        }
    }

    public void setRecUrl(String url) {
        Tools.d("url:" + url);
        if (audioManager == null)
            audioManager = new AudioManager();
        audioManager.setNetUrl(url);
    }

    public void setEditValue(String value) {
        if ("null".equals(value)) {
            value = "";
        }
        editText.setText(value);
    }

    public void setSubmitText(String text) {
        ((TextView) findViewById(R.id.task_question_edit_sumbit)).setText(text);
    }

    public void setOnTaskQuestionSumbitListener(OnTaskQuestionSumbitListener listener) {
        onTaskQuestionSumbitListener = listener;
        View task_question_edit_sumbit = findViewById(R.id.task_question_edit_sumbit);
        task_question_edit_sumbit.setVisibility(View.VISIBLE);
        task_question_edit_sumbit.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (onTaskQuestionSumbitListener != null) {
                    TaskQuestionInfo taskQuestionInfo = new TaskQuestionInfo();
                    if (isRecord) {
                        taskQuestionInfo.setUrl(audioManager.getRecordFile().getAbsolutePath());
                        editText.setText("");
                    } else {
                        taskQuestionInfo.setId(editText.getText().toString().trim());
                    }
                    onTaskQuestionSumbitListener.sumbit(new TaskQuestionInfo[]{taskQuestionInfo}, null);
                }
            }
        });
    }

    public String getText() {
        return filterEmoji(editText.getText().toString().trim());
    }

    public String getUrl() {
        if (isRecord) {
            return audioManager.getRecordFile().getAbsolutePath();
        } else {
            return "";
        }
    }

    public String getNetUrl() {
        if (audioManager != null) {
            return audioManager.getNetUrl();
        } else {
            return "";
        }
    }

    public static String filterEmoji(String source) {

        if (!containsEmoji(source)) {
            return source;// 如果不包含，直接返回
        }
        // 到这里铁定包含
        StringBuilder buf = null;

        int len = source.length();

        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);

            if (isEmojiCharacter(codePoint)) {
                if (buf == null) {
                    buf = new StringBuilder(source.length());
                }

                buf.append(codePoint);
            } else {
            }
        }

        if (buf == null) {
            return source;// 如果没有找到 emoji表情，则返回源字符串
        } else {
            if (buf.length() == len) {// 这里的意义在于尽可能少的toString，因为会重新生成字符串
                buf = null;
                return source;
            } else {
                return buf.toString();
            }
        }

    }

    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
                || (codePoint == 0xD)
                || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
                || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }

    /**
     * 检测是否有emoji字符
     *
     * @param source
     * @return FALSE，包含图片
     */
    public static boolean containsEmoji(String source) {
        if (source.equals("")) {
            return false;
        }

        int len = source.length();

        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);

            if (isEmojiCharacter(codePoint)) {
                // do nothing，判断到了这里表明，确认有表情字符
                return true;
            }
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.task_question_record) {
            if (isRecord && audioManager.getRecordFile() != null) {
                Tools.showToast(context, "您已经录音了，不能再填写文字了哦");
                return;
            }
            if (task_question_presstalk.getVisibility() == VISIBLE) {
                task_question_record.setImageResource(R.mipmap.record_button);
                task_question_presstalk.setVisibility(GONE);
                editText.setVisibility(VISIBLE);
                task_question_play.setVisibility(GONE);
            } else {
                task_question_record.setImageResource(R.mipmap.keyboard_button);
                task_question_presstalk.setVisibility(VISIBLE);
                editText.setVisibility(GONE);
                task_question_play.setVisibility(GONE);
            }
        } else if (v.getId() == R.id.task_question_play) {//播放
            audioManager.startPlaying(getContext());
        } else if (v.getId() == R.id.task_question_reset) {//重做语音题
            task_question_play.setVisibility(GONE);
            task_question_presstalk.setVisibility(VISIBLE);
            task_question_reset.setVisibility(GONE);
            if (audioManager.getRecordFile() != null) {
                audioManager.getRecordFile().delete();
                audioManager.settingRecordFile(null);
            }
        }
    }

    private boolean isUpSlide = false;//是否向上滑动过

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
//        if (!isStart) {
        Tools.d("><><><>" + motionEvent.getAction());
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                解决点击无效问题
                view.performClick();
                try {
                    handlerActionDown(motionEvent);
                } catch (IOException e) {
                    Tools.showToast(getContext(), "录音启动异常！");
                }
                break;
            case MotionEvent.ACTION_UP:
                Tools.d("isUpSlide:" + isUpSlide);
                if (!isUpSlide && !handlerActionUp()) {
                    isRecord = true;
                    return true;
                }
                isUpSlide = false;
                handlerActionCancel();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isUpSlide)
                    handlerActionMove(motionEvent);
                break;
            case MotionEvent.ACTION_CANCEL:
                handlerActionCancel();
                break;
            case MotionEvent.ACTION_POINTER_UP: {
                handlerActionCancel();
            }
            default:
                break;
        }
//        } else {
//            task_question_play.setVisibility(VISIBLE);
//            task_question_presstalk.setVisibility(GONE);
//        }
        return true;
    }

    /**
     * 处理抬手操作
     *
     * @return 语音是否超过1秒
     */
    private boolean handlerActionUp() {
        Tools.d("handlerActionUp");
        audioManager.stopRecording();
        number = -1;
        stopTime();
        if (System.currentTimeMillis() - time < Constant.DELAY_TIME_SHORT) {//1秒误按处理
            if (audioManager.getRecordFile() != null) {
                audioManager.getRecordFile().delete();
                audioManager.settingRecordFile(null);
            }
            dialogManager.updateUI(R.mipmap.no_voice);
            mainHander.sendEmptyMessageDelayed(Constant.WHAT_DIALOG_CLOSE, Constant.DELAY_TIME_SHORT);
            return true;
        }
        if (audioManager.getRecordFile() != null) {
            task_question_play.setVisibility(VISIBLE);
            if (isOnlyRec)
                task_question_reset.setVisibility(VISIBLE);
            task_question_presstalk.setVisibility(GONE);
        } else {
            Tools.showToast(getContext(), "录制失败，请重试");
        }
        recordDialogShow.dismiss();
//                是否取消发送
        if (!isCanceled && !isLastTime) {
//            fileList.add(url);
//            recordAdapter.notifyDataSetChanged();
        }
        return false;
    }

    /**
     * 处理点击操作
     *
     * @param motionEvent 移动事件
     */
    private void handlerActionDown(MotionEvent motionEvent) throws IOException {
        dialogManager = DialogManager.getInstance();
        audioManager = new AudioManager();
        audioManager.clear();
//        录音的音量监听
        audioManager.setOnVolumeChangeListener(this);
//        初始化dialog
        recordDialogShow = dialogManager.recordDialogShow(context);
        startTime();
        downY = motionEvent.getY();
        audioManager.stopPlaying();
        dialogManager.updateUI(R.mipmap.record_01);
        recordDialogShow.show();
        time = System.currentTimeMillis();
        downTime = time;
        audioManager.startRecording(getContext(), project_id + task_id + store_id,
                Tools.getTimeSS() + Tools.getDeviceId(context) + task_id);
        isLastTime = false;
        isCanceled = false;
        number = 9;
    }

    private static Timer mTimer;
    private int currentTime;
    private int MaxTime = 50;

    private void startTime() {
        currentTime = 0;
        if (mTimer != null) {
            return;
        }
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            public long scheduledExecutionTime() {
                return super.scheduledExecutionTime();
            }

            public void run() {
                currentTime++;
                if (currentTime >= MaxTime) {
                    mainHander.sendEmptyMessage(Constant.WHAT_SECOND_FINISH);
                }
                Tools.d("---" + currentTime);
            }
        }, 0, 1000);
    }

    private void stopTime() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    /**
     * 处理上滑取消，下滑发送
     *
     * @param motionEvent 移动事件2
     */
    private void handlerActionMove(MotionEvent motionEvent) {
        float moveY = motionEvent.getY();
//        if (downY - moveY > Constant.VALUE_100) {
//            isCanceled = true;
//            dialogManager.updateUI(R.mipmap.no_voice);
//        }
//        if (downY - moveY < Constant.VALUE_20) {
//            isCanceled = false;
//            dialogManager.updateUI(R.mipmap.record_01);
//        }
        float x = downY - moveY;
        Tools.d("x:" + x);
        if (downY > moveY && Math.abs(x) > 10) {//触发上滑手势
            Tools.d("上滑");
            audioManager.stopRecording();
            stopTime();
            if (audioManager.getRecordFile() != null) {
                audioManager.getRecordFile().delete();
                audioManager.settingRecordFile(null);
            }
            dialogManager.updateUI(R.mipmap.no_voice);
            mainHander.sendEmptyMessageDelayed(Constant.WHAT_DIALOG_CLOSE, Constant.DELAY_TIME_SHORT);
            number = -1;
            isUpSlide = true;
            isCanceled = true;
        }
        Tools.d("正常");
    }

    /**
     * 处理权限申请时的弹出框问题
     */
    private void handlerActionCancel() {
        recordDialogShow.dismiss();
        audioManager.stopRecording();
        stopTime();
        if (audioManager.getRecordFile() != null) {
            audioManager.getRecordFile().delete();
            audioManager.settingRecordFile(null);
        }
    }

    @Override
    public void onVolumeChange(double value) {
        int volume = (int) (value / Constant.VALUE_10);
        int mipmapId = selectVolume(volume);
        if (!isCanceled && !isLastTime) {
            dialogManager.updateUI(mipmapId);
        }
    }

    public int selectVolume(int volume) {
        int mipmapId = 0;
        switch (volume) {
            case 0:
            case 1:
                mipmapId = R.mipmap.record_01;
                break;
            case 2:
            case 3:
                mipmapId = R.mipmap.record_02;
                break;
            case 4:
            case 5:
                mipmapId = R.mipmap.record_03;
                break;
            case 6:
            case 7:
                mipmapId = R.mipmap.record_04;
                break;
            case 8:
            case 9:
                mipmapId = R.mipmap.record_05;
                break;
            case 10:
            case 11:
                mipmapId = R.mipmap.record_06;
                break;
            case 12:
            case 13:
                mipmapId = R.mipmap.record_07;
                break;
            default:
                mipmapId = R.mipmap.record_01;
                break;
        }
        return mipmapId;
    }

    public int selectNumber(int number) {
        int mipmapId = 0;
        switch (number) {
            case 0:
                mipmapId = R.mipmap.number_1;
                break;
            case 1:
                mipmapId = R.mipmap.number_2;
                break;
            case 2:
                mipmapId = R.mipmap.number_3;
                break;
            case 3:
                mipmapId = R.mipmap.number_4;
                break;
            case 4:
                mipmapId = R.mipmap.number_5;
                break;
            case 5:
                mipmapId = R.mipmap.number_6;
                break;
            case 6:
                mipmapId = R.mipmap.number_7;
                break;
            case 7:
                mipmapId = R.mipmap.number_8;
                break;
            case 8:
                mipmapId = R.mipmap.number_9;
                break;
            case 9:
                mipmapId = R.mipmap.number_10;
                break;
        }
        return mipmapId;
    }

    @Override
    public void dataClear() {
        editText.setText("");
        if (audioManager != null && audioManager.getRecordFile() != null) {
            audioManager.getRecordFile().delete();
            audioManager.settingRecordFile(null);
        }
    }

    @Override
    public void isSelect(boolean isSelect) {
        if (isOnlyRec) {//只录音
            if (isSelect) {
                task_question_presstalk.setOnTouchListener(this);
                if (audioManager != null && !TextUtils.isEmpty(audioManager.getNetUrl())) {
                    task_question_play.setVisibility(VISIBLE);
                    task_question_reset.setVisibility(VISIBLE);
                    task_question_presstalk.setVisibility(GONE);
                } else {
                    task_question_record.setVisibility(GONE);
                    task_question_presstalk.setVisibility(VISIBLE);
                    task_question_reset.setVisibility(GONE);
                    task_question_play.setVisibility(GONE);
                    editText.setVisibility(GONE);
                    MaxTime = 86400 * 2;//相对无限大，（PS:能一直按两天的变态请让他来找我）
                }
            } else {
                task_question_presstalk.setOnTouchListener(null);
                task_question_reset.setVisibility(GONE);
                if (audioManager != null && !TextUtils.isEmpty(audioManager.getNetUrl())) {
                    task_question_play.setVisibility(VISIBLE);
                    task_question_presstalk.setVisibility(GONE);
                } else {
                    task_question_play.setVisibility(GONE);
                    task_question_presstalk.setVisibility(VISIBLE);
                }
            }
        } else {
            if (isSelect) {
                task_question_record.setOnClickListener(this);
                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
                task_question_play.setVisibility(GONE);
                if (audioManager != null && !TextUtils.isEmpty(audioManager.getNetUrl())) {
                    task_question_record.setImageResource(R.mipmap.keyboard_button);
                    task_question_presstalk.setVisibility(VISIBLE);
                    editText.setVisibility(GONE);
                } else {
                    task_question_record.setImageResource(R.mipmap.record_button);
                    task_question_presstalk.setVisibility(GONE);
                    editText.setVisibility(VISIBLE);
                }
            } else {
                task_question_record.setOnClickListener(null);
                editText.setFocusable(false);
                editText.setFocusableInTouchMode(false);
                task_question_reset.setVisibility(GONE);
                task_question_presstalk.setVisibility(GONE);
                if (audioManager != null && !TextUtils.isEmpty(audioManager.getNetUrl())) {
                    task_question_record.setImageResource(R.mipmap.keyboard_button);
                    editText.setVisibility(GONE);
                    task_question_play.setVisibility(VISIBLE);
                } else {
                    task_question_record.setImageResource(R.mipmap.record_button);
                    editText.setVisibility(VISIBLE);
                    task_question_play.setVisibility(GONE);
                }
            }
        }
    }

    /**
     * 防止handler的内存泄漏问题
     */
    private class MainHander extends Handler {

        private MainHander() {
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handlerMessgae(msg);
        }
    }

    /**
     * handler的事件处理
     *
     * @param msg 消息
     */
    public void handlerMessgae(Message msg) {
        switch (msg.what) {
            case Constant.WHAT_DIALOG_CLOSE:
                if (recordDialogShow.isShowing()) {
                    recordDialogShow.dismiss();
                    stopTime();
                }
                break;
            case Constant.WHAT_SECOND_FINISH:
                Tools.d("WHAT_SECOND_FINISH");
                stopTime();
                if (downTime != time) {
                    return;
                }
                if (currentTime >= MaxTime) {
                    isLastTime = true;
                    if (number == -1) {
//                        dialogManager.updateUI(R.mipmap.no_voice);
                        audioManager.stopRecording();
                        stopTime();
                        if (audioManager.getRecordFile() != null) {
                            audioManager.getRecordFile().delete();
                            audioManager.settingRecordFile(null);
                        }
                        if (recordDialogShow != null)
                            recordDialogShow.dismiss();
                        isCanceled = true;
                    }

                    if (number >= 0) {
                        Tools.d("number:" + number);
                        int mipmapId = selectNumber((int) number);
                        dialogManager.updateUI(mipmapId);
                        number--;
                        mainHander.sendEmptyMessageDelayed(Constant.WHAT_SECOND_FINISH, Constant.DELAY_TIME_SHORT);
                    }
                }
                break;
            default:
                break;
        }
    }
}
