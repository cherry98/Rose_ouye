package com.orange.oy.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;

public class ConfirmPayDialog extends LinearLayout implements View.OnClickListener {
    private static AlertDialog dialog;
    private static Object data;
    private OnSystemDialogClickListener listener;
    private TextView confirm_title, tvmoneyEnough;
    private boolean isDissmis;
    private CheckBox cb_zhifubao, cb_moneyEnough;

    public void setOnSystemDialogClickListener(OnSystemDialogClickListener onSystemDialogClickListener) {
        this.listener = onSystemDialogClickListener;
    }

    public interface OnSystemDialogClickListener {
        void rightClick(Object object, boolean moneyEnough, boolean zhifubao);
    }


    public void setIsDissmis(boolean isDissmis) {
        this.isDissmis = isDissmis;
    }

    public ConfirmPayDialog(Context context) {
        super(context);
        Tools.loadLayout(this, R.layout.dialog_confirm_pay);
        initView();
        isDissmis = true;
    }

    private void initView() {
        confirm_title = (TextView) findViewById(R.id.confirm_title);
        cb_zhifubao = (CheckBox) findViewById(R.id.cb_zhifubao);
        cb_moneyEnough = (CheckBox) findViewById(R.id.cb_moneyEnough);
        tvmoneyEnough = (TextView) findViewById(R.id.moneyEnough);


        cb_zhifubao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    cb_zhifubao.setChecked(true);
                    cb_moneyEnough.setChecked(false);
                }
            }
        });

        cb_moneyEnough.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    cb_moneyEnough.setChecked(true);
                    cb_zhifubao.setChecked(false);
                }
            }
        });
        findViewById(R.id.confirm_right).setOnClickListener(this);


    }

    protected void settingTitle(String title) {
        if (title != null) {
            confirm_title.setVisibility(View.VISIBLE);
            confirm_title.setText(title);
        } else {
            confirm_title.setVisibility(View.GONE);
        }
    }

    public static AlertDialog showDialogForPay(Context context, String title, String moneyEnough,

                                               Object object, boolean cancelable,
                                               OnSystemDialogClickListener listener) {
        dissmisDialog();
        ConfirmPayDialog view = new ConfirmPayDialog(context);
        view.setIsDissmis(false);
        view.settingTitle(title);
        if (TextUtils.isEmpty(moneyEnough)) {
            view.tvmoneyEnough.setVisibility(GONE);
            view.cb_moneyEnough.setVisibility(VISIBLE);

        } else {
            view.tvmoneyEnough.setVisibility(VISIBLE);
            view.cb_moneyEnough.setVisibility(GONE);
            view.tvmoneyEnough.setText(moneyEnough);
        }

        view.setOnSystemDialogClickListener(listener);
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
                if (cb_moneyEnough.isChecked() && cb_zhifubao.isChecked()) {
                    Tools.showToast(getContext(), "请选择支付方式");
                    return;
                }
                if (listener != null) {
                    listener.rightClick(data, cb_moneyEnough.isChecked(), cb_zhifubao.isChecked());
                }
            }
            break;
        }
        if (dialog != null && dialog.isShowing() && isDissmis) {
            dissmisDialog();
        }
    }
}
