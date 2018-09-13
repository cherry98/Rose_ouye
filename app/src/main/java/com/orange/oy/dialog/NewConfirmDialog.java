package com.orange.oy.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;


public class NewConfirmDialog extends LinearLayout implements View.OnClickListener {
    private static AlertDialog dialog;
    private static Object data;
    private OnSystemDialogClickListener listener;
    private TextView confirm_title, confirm_message, confirm_left, confirm_right;
    private boolean isDissmis;
    private TextView tv_bianhao, tv_name, tv_address, tv_getaddress, tv_gps;

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

    public NewConfirmDialog(Context context, OnSystemDialogClickListener listener) {
        super(context);
        Tools.loadLayout(this, R.layout.dialog_confirm_new);
        this.listener = listener;
        initView();
        isDissmis = true;
    }

    private void initView() {
        confirm_title = (TextView) findViewById(R.id.confirm_title);
        confirm_message = (TextView) findViewById(R.id.confirm_message);
        confirm_left = (TextView) findViewById(R.id.confirm_left);
        confirm_right = (TextView) findViewById(R.id.confirm_right);
        tv_bianhao = (TextView) findViewById(R.id.tv_bianhao);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_getaddress = (TextView) findViewById(R.id.tv_getaddress);
        tv_gps = (TextView) findViewById(R.id.tv_gps);
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
        //confirm_message.setVisibility(VISIBLE);
        // confirm_message.setText(msg);
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
        NewConfirmDialog view = new NewConfirmDialog(context, listener);
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
        NewConfirmDialog view = new NewConfirmDialog(context, null);
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

    public static NewConfirmDialog showDialog(Context context, String title, boolean cancelable,
                                              OnSystemDialogClickListener listener) {
        return showDialog(context, null, 0, title, 0, null, 0, null, 0, null, cancelable, listener);
    }

    public static NewConfirmDialog showDialog(Context context, String title, String msg, String left, String right, Object
            object, boolean cancelable, OnSystemDialogClickListener listener) {
        return showDialog(context, title, 0, msg, 0, left, 0, right, 0, object, cancelable, listener);
    }

    public static AlertDialog showDialogForMap(Context context, String title, String project_type, String msg, String msg2,
                                               String msg3, String msg4, String msg5,
                                               String msg6,
                                               String msg7, String left, String right,
                                               Object object, boolean cancelable, OnSystemDialogClickListener listener) {
        dissmisDialog();
        NewConfirmDialog view = new NewConfirmDialog(context, listener);
        view.setIsDissmis(false);
        view.settingTitle(title);
        if ("5".equals(project_type)) {
            view.findViewById(R.id.tv_bianhao_ly).setVisibility(GONE);
            view.findViewById(R.id.tv_name_ly).setVisibility(GONE);
            view.findViewById(R.id.tv_address_ly).setVisibility(GONE);
        } else {
            view.findViewById(R.id.tv_bianhao_ly).setVisibility(VISIBLE);
            view.findViewById(R.id.tv_name_ly).setVisibility(VISIBLE);
            view.findViewById(R.id.tv_address_ly).setVisibility(VISIBLE);
        }
        view.tv_bianhao.setText(msg2);
        view.tv_name.setText(msg3);
        view.tv_address.setText(msg4);
        view.tv_gps.setText("经度：" + msg5 + "\n" + "纬度：" + msg6);
        view.tv_getaddress.setText(msg7);
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

    public static NewConfirmDialog showDialog(Context context, String title, int titleColor, String msg, int msgColor,
                                              String left, int leftColor, String right, int rightColor, Object object,
                                              boolean cancelable, OnSystemDialogClickListener listener) {
        dissmisDialog();
        NewConfirmDialog view = new NewConfirmDialog(context, listener);
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
