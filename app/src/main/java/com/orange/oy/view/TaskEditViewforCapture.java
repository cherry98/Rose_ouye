package com.orange.oy.view;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.allinterface.OnTaskQuestionSumbitListener;
import com.orange.oy.allinterface.TaskEditClearListener;
import com.orange.oy.base.Tools;
import com.orange.oy.info.TaskQuestionInfo;
import com.orange.oy.util.CaptureWindow;

/**
 * 填空题带扫码功能
 */
public class TaskEditViewforCapture extends LinearLayout implements View.OnClickListener, TaskEditClearListener {
    private EditText editText;
    private OnTaskQuestionSumbitListener onTaskQuestionSumbitListener;
    private View appTitle;
    private View task_question_capture;
    private boolean isScan;

    public TaskEditViewforCapture(Context context, String title, boolean isrequired, View appTitle, boolean isScan) {
        super(context);
        this.appTitle = appTitle;
        this.isScan = isScan;
        Tools.loadLayout(this, R.layout.view_task_question_edit3);
        editText = (EditText) findViewById(R.id.task_question_edit_edit);
        ((TextView) findViewById(R.id.task_question_edit_name)).setText(title);
        findViewById(R.id.task_question_capture).setOnClickListener(this);
        if (isrequired) {
            findViewById(R.id.task_question_edit_img).setVisibility(VISIBLE);
        }
        task_question_capture = findViewById(R.id.task_question_capture);
        if (isScan) {
            task_question_capture.setVisibility(VISIBLE);
        } else {
            task_question_capture.setVisibility(GONE);
        }
    }

    public void settingValue(String value) {
        if ("null".equals(value)) {
            value = "";
        }
        editText.setText(value);
    }

    @Override
    public void dataClear() {
        editText.setText("");
    }

    public void isSelect(boolean isSelect) {
        editText.setFocusable(isSelect);
        editText.setFocusableInTouchMode(isSelect);
        if (isSelect) {
            task_question_capture.setVisibility(VISIBLE);
        } else {
            task_question_capture.setVisibility(GONE);
        }
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
                    taskQuestionInfo.setId(editText.getText().toString().trim());
                    onTaskQuestionSumbitListener.sumbit(new TaskQuestionInfo[]{taskQuestionInfo}, null);
                }
            }
        });
    }

    public void settingEdittext(String msg) {
        editText.setText(msg);
    }

    public String getText() {
        return filterEmoji(editText.getText().toString().trim());
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

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.task_question_capture: {//展示扫码页
                int[] location = new int[2];
                appTitle.getLocationOnScreen(location);
                int y = location[1];
                try {
                    CaptureWindow.showWindow(getContext(), this, Tools.getScreeInfoHeight(getContext()) - y, y);
                } catch (Exception e) {
                    e.printStackTrace();
                    Tools.showToast(getContext(), "扫码页打开失败，请重启应用重试");
                }
            }
            break;
        }
    }
}
