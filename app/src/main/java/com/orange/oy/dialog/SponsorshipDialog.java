package com.orange.oy.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.orange.oy.R;
import com.orange.oy.base.Tools;

/**
 * Created by Lenovo on 2018/6/12.
 * 赞助费弹窗
 */

public class SponsorshipDialog extends LinearLayout implements View.OnClickListener {
    private EditText sponsorship_edit;
    private OnSelectClickListener onSelectClickListener;
    private String num;

    public SponsorshipDialog(Context context) {
        super(context);
        Tools.loadLayout(this, R.layout.dialog_sponsorship);
        sponsorship_edit = (EditText) findViewById(R.id.sponsorship_edit);
        findViewById(R.id.sponsorship_cancel).setOnClickListener(this);
        findViewById(R.id.sponsorship_confirm).setOnClickListener(this);
    }

    private static AlertDialog dialog;

    public static AlertDialog showDialog(Context context, String money, String num, OnSelectClickListener onSelectClickListener) {
        dissmisDialog();
        SponsorshipDialog sponsorshipDialog = new SponsorshipDialog(context);
        sponsorshipDialog.setData(money, num);
        sponsorshipDialog.setOnSelectClickListener(onSelectClickListener);
        dialog = new AlertDialog.Builder(context, R.style.DialogTheme).setCancelable(false).create();
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        dialog.show();
        LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        dialog.addContentView(sponsorshipDialog, params);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }

    private void setData(String money, String num) {
        if (!Tools.isEmpty(money)) {
            sponsorship_edit.setText(money);
        }
        this.num = num;
    }

    public void setOnSelectClickListener(OnSelectClickListener onSelectClickListener) {
        this.onSelectClickListener = onSelectClickListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sponsorship_confirm: {
                double sum = Tools.StringToDouble(sponsorship_edit.getText().toString().trim());
                double scale = sum / Tools.StringToDouble(num);
                if (scale < 1) {
                    Tools.showToast(getContext(), "赞助费金额/目标参与人数的值需大于1");
                    return;
                }
                onSelectClickListener.onConfirm(sponsorship_edit.getText().toString().trim());
            }
            break;
            case R.id.sponsorship_cancel: {
            }
            break;
        }
        dissmisDialog();
    }

    public interface OnSelectClickListener {
        void onConfirm(String sum);
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
