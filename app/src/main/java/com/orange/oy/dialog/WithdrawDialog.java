package com.orange.oy.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;

/**
 * Created by Lenovo on 2018/3/6.
 */

public class WithdrawDialog extends LinearLayout implements View.OnClickListener {

    private static Dialog dialog;
    private ConfirmDialog.OnSystemDialogClickListener listener;
    private TextView withdraw_editall, withdraw_left, withdraw_right;
    private EditText withdraw_edit;
    private boolean isDissmis;
    private String total;

    public WithdrawDialog(Context context, ConfirmDialog.OnSystemDialogClickListener listener) {
        super(context);
        Tools.loadLayout(this, R.layout.dialog_withdraw);
        this.listener = listener;
        initView();
        isDissmis = true;
    }

    private void initView() {
        withdraw_edit = (EditText) findViewById(R.id.withdraw_edit);
        withdraw_editall = (TextView) findViewById(R.id.withdraw_editall);
        withdraw_left = (TextView) findViewById(R.id.withdraw_left);
        withdraw_right = (TextView) findViewById(R.id.withdraw_right);
        withdraw_left.setOnClickListener(this);
        withdraw_right.setOnClickListener(this);
        withdraw_editall.setOnClickListener(this);
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

    public void setData(String total) {
        this.total = total;
        withdraw_edit.setHint("总金额：" + total);
    }

    public static WithdrawDialog showDialog(Context context, String total, boolean cancelable, ConfirmDialog.OnSystemDialogClickListener listener) {
        dissmisDialog();
        WithdrawDialog view = new WithdrawDialog(context, listener);
        view.setData(total);
        dialog = new Dialog(context, R.style.DialogTheme);
        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(cancelable);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        dialog.show();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        dialog.addContentView(view, params);
        return view;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.withdraw_left: {
                if (listener != null) {
                    listener.leftClick(null);
                }
                if (dialog != null && dialog.isShowing() && isDissmis) {
                    dissmisDialog();
                }
            }
            break;
            case R.id.withdraw_right: {
                if (listener != null) {
                    listener.rightClick(withdraw_edit.getText().toString());
                }
                if (dialog != null && dialog.isShowing() && isDissmis) {
                    dissmisDialog();
                }
            }
            break;
            case R.id.withdraw_editall:
                withdraw_edit.setText(total);
        }
    }
}
