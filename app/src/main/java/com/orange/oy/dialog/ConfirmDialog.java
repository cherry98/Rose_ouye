package com.orange.oy.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.util.ImageLoader;

public class ConfirmDialog extends LinearLayout implements View.OnClickListener {
    private static AlertDialog dialog;
    private static Object data;
    private OnSystemDialogClickListener listener;
    private TextView confirm_title, confirm_message, confirm_left, confirm_right;
    private EditText confirm_edittext;
    private TextView confirm_edittext_tip;
    private View confirm_edittext_layout;
    private boolean isDissmis;
    private ImageView confirm_img;//(1==对号 2==笑脸 3==哭脸)
    private ImageView confirm_imageview;

    public interface OnSystemDialogClickListener {
        public void leftClick(Object object);

        public void rightClick(Object object);
    }

    public boolean isDissmis() {
        return isDissmis;
    }

    public void setIsDissmis(boolean isDissmis) {
        this.isDissmis = isDissmis;
    }

    public ConfirmDialog(Context context, OnSystemDialogClickListener listener) {
        super(context);
        Tools.loadLayout(this, R.layout.dialog_confirm);
        this.listener = listener;
        initView();
        isDissmis = true;
    }

    private void initView() {
        confirm_imageview = (ImageView) findViewById(R.id.confirm_imageview);
        confirm_title = (TextView) findViewById(R.id.confirm_title);
        confirm_message = (TextView) findViewById(R.id.confirm_message);
        confirm_left = (TextView) findViewById(R.id.confirm_left);
        confirm_right = (TextView) findViewById(R.id.confirm_right);
        confirm_img = (ImageView) findViewById(R.id.confirm_img);
        confirm_edittext = (EditText) findViewById(R.id.confirm_edittext);
        confirm_edittext_tip = (TextView) findViewById(R.id.confirm_edittext_tip);
        confirm_edittext_layout = findViewById(R.id.confirm_edittext_layout);
        confirm_edittext.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                confirm_edittext_tip.setText(s.length() + "/" + maxLength);
            }
        });
        confirm_left.setOnClickListener(this);
        confirm_right.setOnClickListener(this);
    }

    protected void settingLineSpacing() {
        confirm_message.setLineSpacing(10, 1f);
    }

    protected void settingTitle(String title) {
        if (title != null) {
            confirm_title.setVisibility(View.VISIBLE);
            confirm_title.setText(title);
        } else {
            confirm_title.setVisibility(View.GONE);
        }
    }

    protected void settingTitleColor(int color) {
        confirm_title.setTextColor(color);
    }

    protected void settingMessage(String msg) {
        confirm_message.setVisibility(VISIBLE);
        confirm_message.setText(msg);
    }

    protected void settingMessage(Spannable msg) {
        confirm_message.setVisibility(VISIBLE);
        confirm_message.setText(msg);
    }

    protected void settingMessageColor(int color) {
        confirm_message.setTextColor(color);
    }

    protected void settingLeft(String left) {
        confirm_left.setText(left);
    }

    protected void settingLeftColor(int color) {
        confirm_left.setTextColor(color);
    }

    public void goneLeft() {
//        findViewById(R.id.confirm_line).setVisibility(View.GONE);
        confirm_left.setVisibility(View.GONE);
        confirm_right.setPadding(0, 0, 0, 0);
    }

    public ConfirmDialog settingRightOnClick(OnClickListener onClickListener) {
        confirm_right.setOnClickListener(onClickListener);
        return this;
    }

    /**
     * 设置显示的图片
     *
     * @param context
     * @param url
     * @return
     */
    public ConfirmDialog settingShowImage(Context context, String url) {
        confirm_imageview.setVisibility(VISIBLE);
        new ImageLoader(context).DisplayImage(url, confirm_imageview);
        return this;
    }

    private int maxLength = 0;

    /**
     * 显示编辑框
     *
     * @param maxLength 编辑框最大长度
     * @param hint      默认显示信息
     * @return
     */
    public ConfirmDialog showEditText(int maxLength, String hint) {
        this.maxLength = maxLength;
        confirm_edittext.setHint(hint);
        InputFilter[] filters = {new InputFilter.LengthFilter(maxLength)};
        confirm_edittext.setFilters(filters);
        confirm_edittext.setVisibility(VISIBLE);
        confirm_edittext_layout.setVisibility(VISIBLE);
        confirm_edittext_tip.setText("0/" + maxLength);
        return this;
    }

    public EditText getConfirm_edittext() {
        return confirm_edittext;
    }

    protected void settingRight(String right) {
        confirm_right.setText(right);
    }

    protected void settingRightColor(int color) {
        confirm_right.setTextColor(color);
    }

    public static AlertDialog getDialog() {
        return dialog;
    }

    public static AlertDialog showDialogForHint(Context context, String title) {
        return showDialogForHint(context, title, null, null);
    }

    public static AlertDialog showDialogForHint(Context context, String title, OnSystemDialogClickListener listener) {
        return showDialogForHint(context, title, null, listener);
    }

    public static AlertDialog showDialogForHint(Context context, String title, String str,
                                                OnSystemDialogClickListener listener) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        ConfirmDialog view = new ConfirmDialog(context, listener);
        if (title != null) {
            view.settingTitle(title);
        }
        view.goneLeft();
        if (TextUtils.isEmpty(str)) {
            view.settingRight("确定");
        } else {
            view.settingRight(str);
        }
        dialog = new AlertDialog.Builder(context, R.style.DialogTheme).setCancelable(true).create();
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        dialog.show();
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
                .WRAP_CONTENT);
        dialog.addContentView(view, params);
        return dialog;
    }

    public static AlertDialog showDialogForService(Context context, String title) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        ConfirmDialog view = new ConfirmDialog(context, null);
        if (title != null) {
            view.settingTitle(title);
        }
        view.goneLeft();
        view.settingRight("确定");
        dialog = new AlertDialog.Builder(context, R.style.DialogTheme).setCancelable(true).create();
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
                .WRAP_CONTENT);
        dialog.addContentView(view, params);
        return dialog;
    }

    public static ConfirmDialog showDialog(Context context, String title, Object object, boolean cancelable,
                                           OnSystemDialogClickListener listener) {
        return showDialog(context, "提示", 0, title, 0, null, 0, null, 0, object, cancelable, listener);
    }

    public static ConfirmDialog showDialog(Context context, String title, boolean cancelable,
                                           OnSystemDialogClickListener listener) {
        return showDialog(context, "提示", 0, title, 0, null, 0, null, 0, null, cancelable, listener);
    }

    public static ConfirmDialog showDialog(Context context, String title, String msg, String left, String right, Object
            object, boolean cancelable, OnSystemDialogClickListener listener) {
        if (title == null) {
            title = "提示";
        }
        return showDialog(context, title, 0, msg, 0, left, 0, right, 0, object, cancelable, listener);
    }

    public static AlertDialog showDialogForMap(Context context, String title, String msg, String left, String right,
                                               Object object, boolean cancelable, OnSystemDialogClickListener listener) {
        dissmisDialog();
        ConfirmDialog view = new ConfirmDialog(context, listener);
        view.setIsDissmis(false);
        if (title != null) {
            view.settingTitle(title);
        }
        if (msg != null) {
            view.settingLineSpacing();
            view.settingMessage(msg);
        }
        if (left != null) {
            view.settingLeft(left);
        }
        if (right != null) {
            view.settingRight(right);
        }
        data = object;
        dialog = new AlertDialog.Builder(context, R.style.DialogTheme).setCancelable(cancelable).create();
        dialog.setCanceledOnTouchOutside(cancelable);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        dialog.show();
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        dialog.addContentView(view, params);
        return dialog;
    }

    public static ConfirmDialog showDialog(Context context, String title, int titleColor, String msg, int msgColor,
                                           String left, int leftColor, String right, int rightColor, Object object,
                                           boolean cancelable, OnSystemDialogClickListener listener) {
        dissmisDialog();
        ConfirmDialog view = new ConfirmDialog(context, listener);
        view.settingTitle(title);
        if (titleColor != 0) {
            view.settingTitleColor(titleColor);
        }
        if (msg != null) {
            view.settingMessage(msg);
        }
        if (msgColor != 0) {
            view.settingMessageColor(msgColor);
        }
        if (left != null) {
            view.settingLeft(left);
        }
        if (leftColor != 0) {
            view.settingLeftColor(leftColor);
        }
        if (right != null) {
            view.settingRight(right);
        }
        if (rightColor != 0) {
            view.settingRightColor(rightColor);
        }
        data = object;
        dialog = new AlertDialog.Builder(context, R.style.DialogTheme).setCancelable(cancelable).create();
        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(cancelable);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        dialog.show();
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        dialog.addContentView(view, params);
        return view;
    }

    public static ConfirmDialog showDialog(Context context, String title, int imgId, String msg, String left, String right, Object
            object, boolean cancelable, OnSystemDialogClickListener listener) {
        if (title == null) {
            title = "提示";
        }
        return showDialog(context, title, imgId, 0, msg, 0, left, 0, right, 0, object, cancelable, listener);
    }

    public static ConfirmDialog showDialog(Context context, String title, int imgId, int titleColor, String msg, int msgColor,
                                           String left, int leftColor, String right, int rightColor, Object object,
                                           boolean cancelable, OnSystemDialogClickListener listener) {
        dissmisDialog();
        ConfirmDialog view = new ConfirmDialog(context, listener);
        view.settingTitle(title);
        if (titleColor != 0) {
            view.settingTitleColor(titleColor);
        }
        if (msg != null) {
            view.settingMessage(msg);
        }
        if (msgColor != 0) {
            view.settingMessageColor(msgColor);
        }
        if (left != null) {
            view.settingLeft(left);
        }
        if (leftColor != 0) {
            view.settingLeftColor(leftColor);
        }
        if (right != null) {
            view.settingRight(right);
        }
        if (rightColor != 0) {
            view.settingRightColor(rightColor);
        }
        if (imgId != 0) {
            view.settingTitleImg(imgId);
        }
        data = object;
        dialog = new AlertDialog.Builder(context, R.style.DialogTheme).setCancelable(cancelable).create();
        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(cancelable);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        dialog.addContentView(view, params);
        return view;
    }

    public static ConfirmDialog showDialog(Context context, String title, int imgId, int titleColor, Spannable msg,
                                           int msgColor, String left, int leftColor, String right, int rightColor, Object object,
                                           boolean cancelable, OnSystemDialogClickListener listener) {
        dissmisDialog();
        ConfirmDialog view = new ConfirmDialog(context, listener);
        view.settingTitle(title);
        if (titleColor != 0) {
            view.settingTitleColor(titleColor);
        }
        if (msg != null) {
            view.settingMessage(msg);
        }
        if (msgColor != 0) {
            view.settingMessageColor(msgColor);
        }
        if (left != null) {
            view.settingLeft(left);
        }
        if (leftColor != 0) {
            view.settingLeftColor(leftColor);
        }
        if (right != null) {
            view.settingRight(right);
        }
        if (rightColor != 0) {
            view.settingRightColor(rightColor);
        }
        if (imgId != 0) {
            view.settingTitleImg(imgId);
        }
        data = object;
        dialog = new AlertDialog.Builder(context, R.style.DialogTheme).setCancelable(cancelable).create();
        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(cancelable);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        dialog.addContentView(view, params);
        return view;
    }

    private void settingTitleImg(int imgId) {
        if (imgId == 1) {
            confirm_img.setImageResource(R.mipmap.dialogtitle_right);
        } else if (imgId == 2) {
            confirm_img.setImageResource(R.mipmap.dialogtitle_smile);
        } else if (imgId == 3) {
            confirm_img.setImageResource(R.mipmap.dialogtitle_cry);
        }
    }

    public static View getDialogView() {
        return dialog.getWindow().getDecorView();
    }

    public static void dissmisDialog() {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isShow() {
        return dialog != null && dialog.isShowing();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm_right: {
                if (listener != null) {
                    listener.rightClick(data);
                }
            }
            break;
            case R.id.confirm_left: {
                if (listener != null) {
                    listener.leftClick(data);
                }
            }
            break;
        }
        if (dialog != null && dialog.isShowing() && isDissmis) {
            dissmisDialog();
        }
    }
}
