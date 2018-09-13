package com.orange.oy.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;

/**
 * Created by Lenovo on 2018/6/27.
 * 支付金额超过10000 弹窗
 */

public class ObtainMoreDialog extends LinearLayout implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private EditText dialogobtain_edit1, dialogobtain_edit2, dialogobtain_edit3, dialogobtain_edit4;
    private RadioGroup dialogobtain_group;
    private String phone_number, company_name, position, name, sex;
    private OnObtainMoreListener onObtainMoreListener;

    public ObtainMoreDialog(Context context) {
        super(context);
        Tools.loadLayout(this, R.layout.dialog_obtainmore);
        initView();
        findViewById(R.id.dialogobtain_submit).setOnClickListener(this);
        findViewById(R.id.dialogobtain_close).setOnClickListener(this);
        dialogobtain_group.setOnCheckedChangeListener(this);
    }

    private void initView() {
        dialogobtain_edit1 = (EditText) findViewById(R.id.dialogobtain_edit1);
        dialogobtain_edit2 = (EditText) findViewById(R.id.dialogobtain_edit2);
        dialogobtain_edit3 = (EditText) findViewById(R.id.dialogobtain_edit3);
        dialogobtain_edit4 = (EditText) findViewById(R.id.dialogobtain_edit4);
        dialogobtain_group = (RadioGroup) findViewById(R.id.dialogobtain_group);
    }

    private static AlertDialog dialog;

    public static ObtainMoreDialog showDialog(Context context, OnObtainMoreListener onObtainMoreListener) {
        if (dialog != null && dialog.isShowing()) {
            dissmisDialog();
        }
        ObtainMoreDialog obtainMoreDialog = new ObtainMoreDialog(context);
        obtainMoreDialog.setOnObtainMoreListener(onObtainMoreListener);
        dialog = new AlertDialog.Builder(context, R.style.DialogTheme).setCancelable(false).create();
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        dialog.show();
        LinearLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        dialog.addContentView(obtainMoreDialog, layoutParams);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return obtainMoreDialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialogobtain_submit: {//提交
                phone_number = Tools.filterEmoji(dialogobtain_edit1.getText().toString().trim());
                if (Tools.isEmpty(phone_number)) {
                    Tools.showToast(getContext(), "请填写您的手机号码");
                    return;
                }
                company_name = Tools.filterEmoji(dialogobtain_edit2.getText().toString().trim());
                position = Tools.filterEmoji(dialogobtain_edit3.getText().toString().trim());
                name = Tools.filterEmoji(dialogobtain_edit4.getText().toString().trim());
                if (Tools.isEmpty(name)) {
                    Tools.showToast(getContext(), "请填写您的称呼");
                    return;
                }
                if (Tools.isEmpty(sex)) {
                    Tools.showToast(getContext(), "请选择性别");
                    return;
                }

                onObtainMoreListener.onSubmit(phone_number, company_name, position, name, sex);
                dissmisDialog();
            }
            break;
            case R.id.dialogobtain_close: {//关闭弹窗
                onObtainMoreListener.cancel();
                dissmisDialog();
            }
            break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.dialogobtain_radio1) {
            sex = "0";
        } else if (checkedId == R.id.dialogobtain_radio2) {
            sex = "1";
        }
    }

    public interface OnObtainMoreListener {
        void onSubmit(String phone_number, String company_name, String position, String name, String sex);

        void cancel();
    }

    public void setOnObtainMoreListener(OnObtainMoreListener onObtainMoreListener) {
        this.onObtainMoreListener = onObtainMoreListener;
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
