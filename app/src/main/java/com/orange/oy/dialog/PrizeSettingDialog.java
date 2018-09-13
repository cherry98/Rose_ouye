package com.orange.oy.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;

/**
 * Created by Lenovo on 2018/8/24.
 * 礼品设置选择 V3.20
 */

public class PrizeSettingDialog extends LinearLayout implements View.OnClickListener {

    private OnPrizeSettingListener listener;
    private TextView dialogprize_add, dialogprize_select;

    public PrizeSettingDialog(Context context) {
        super(context);
        Tools.loadLayout(this, R.layout.dialog_prizesetting);
        findViewById(R.id.dialogprize_close).setOnClickListener(this);
        dialogprize_add = (TextView) findViewById(R.id.dialogprize_add);
        dialogprize_select = (TextView) findViewById(R.id.dialogprize_select);
        dialogprize_add.setOnClickListener(this);
        dialogprize_select.setOnClickListener(this);
    }

    private static AlertDialog dialog;

    public static AlertDialog showDialog(Context context, OnPrizeSettingListener listener) {
        PrizeSettingDialog settingDialog = new PrizeSettingDialog(context);
        settingDialog.listener = listener;
        dialog = new AlertDialog.Builder(context, R.style.DialogTheme).setCancelable(false).create();
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        dialog.show();
        LinearLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        dialog.addContentView(settingDialog, layoutParams);
        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialogprize_add: {//新增礼品
                dialogprize_add.setBackgroundResource(R.drawable.dialog_upload1);
                dialogprize_select.setBackgroundResource(R.drawable.dialog_upload2);
                if (listener != null) {
                    listener.firstClick();
                }
                dissmisDialog();
            }
            break;
            case R.id.dialogprize_select: {//从礼品库选择
                dialogprize_add.setBackgroundResource(R.drawable.dialog_upload2);
                dialogprize_select.setBackgroundResource(R.drawable.dialog_upload1);
                if (listener != null) {
                    listener.secondClick();
                }
                dissmisDialog();
            }
            break;
            case R.id.dialogprize_close: {//关闭
                dissmisDialog();
            }
            break;
        }
    }

    public interface OnPrizeSettingListener {
        void firstClick();

        void secondClick();
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

}
