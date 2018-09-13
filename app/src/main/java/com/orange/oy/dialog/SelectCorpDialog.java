package com.orange.oy.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.activity.mycorps_315.CorpGrabActivity;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;

/**
 * Created by Lenovo on 2018/5/22.
 * 选择战队提示框 V3.15
 */

public class SelectCorpDialog extends LinearLayout implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private TextView corp_name1, corp_name2, corp_name3;
    private CheckBox corp_check1, corp_check2, corp_check3;
    private TextView corp_cancel, corp_confirm;
    private SelectCorpListener selectCorpListener;
    private Context context;

    public SelectCorpDialog(Context context) {
        super(context);
        this.context = context;
        Tools.loadLayout(this, R.layout.dialog_selectcorp);
        initView();
        corp_check1.setOnCheckedChangeListener(this);
        corp_check2.setOnCheckedChangeListener(this);
        corp_check3.setOnCheckedChangeListener(this);
        corp_cancel.setOnClickListener(this);
        corp_confirm.setOnClickListener(this);
    }

    private void initView() {
        corp_name1 = (TextView) findViewById(R.id.corp_name1);
        corp_name2 = (TextView) findViewById(R.id.corp_name2);
        corp_name3 = (TextView) findViewById(R.id.corp_name3);
        corp_check1 = (CheckBox) findViewById(R.id.corp_check1);
        corp_check2 = (CheckBox) findViewById(R.id.corp_check2);
        corp_check3 = (CheckBox) findViewById(R.id.corp_check3);
        corp_cancel = (TextView) findViewById(R.id.corp_cancel);
        corp_confirm = (TextView) findViewById(R.id.corp_confirm);
    }

    private static AlertDialog dialog;

    public static AlertDialog showDialog(Context context, String name1, String tag1, String name2, String tag2, String name3, String tag3,
                                         SelectCorpListener selectCorpListener) {
        if (dialog != null && dialog.isShowing()) {
            dissmisDialog();
        }
        SelectCorpDialog selectCorpDialog = new SelectCorpDialog(context);
        selectCorpDialog.setData(name1, tag1, name2, tag2, name3, tag3);
        selectCorpDialog.setSelectCorpListener(selectCorpListener);
        dialog = new AlertDialog.Builder(context, R.style.DialogTheme).setCancelable(false).create();
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        dialog.show();
        LinearLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        dialog.addContentView(selectCorpDialog, layoutParams);
        return dialog;
    }

    private void setData(String name1, String tag1, String name2, String tag2, String name3, String tag3) {
        if (!TextUtils.isEmpty(name1) && !TextUtils.isEmpty(tag1)) {
            corp_name1.setText(name1);
            corp_name1.setTag(tag1);
            findViewById(R.id.corp_layout1).setVisibility(VISIBLE);
            findViewById(R.id.corp_layout1).setOnClickListener(this);
        } else {
            findViewById(R.id.corp_layout1).setVisibility(GONE);
        }
        if (!TextUtils.isEmpty(name2) && !TextUtils.isEmpty(tag2)) {
            corp_name2.setText(name2);
            corp_name2.setTag(tag2);
            findViewById(R.id.corp_layout2).setVisibility(VISIBLE);
            findViewById(R.id.corp_layout2).setOnClickListener(this);
        } else {
            findViewById(R.id.corp_layout2).setVisibility(GONE);
        }
        if (!TextUtils.isEmpty(name3) && !TextUtils.isEmpty(tag3)) {
            corp_name3.setText(name3);
            corp_name3.setTag(tag3);
            findViewById(R.id.corp_layout3).setVisibility(VISIBLE);
            findViewById(R.id.corp_layout3).setOnClickListener(this);
        } else {
            findViewById(R.id.corp_layout3).setVisibility(GONE);
        }
    }


    public void setSelectCorpListener(SelectCorpListener selectCorpListener) {
        this.selectCorpListener = selectCorpListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.corp_cancel: {
                if (dialog != null && dialog.isShowing()) {
                    dissmisDialog();
                }
            }
            break;
            case R.id.corp_confirm: {
                String select_id = "";
                if (corp_check1.isChecked()) {
                    select_id = corp_name1.getTag().toString();
                }
                if (corp_check2.isChecked()) {
                    select_id = corp_name2.getTag().toString();
                }
                if (corp_check3.isChecked()) {
                    select_id = corp_name3.getTag().toString();
                }
                if (!corp_check1.isChecked() && !corp_check2.isChecked() && !corp_check3.isChecked()) {
                    Tools.showToast(context, "请选择战队");
                    return;
                }
                selectCorpListener.selectCorp(select_id);
                if (dialog != null && dialog.isShowing()) {
                    dissmisDialog();
                }
            }
            break;
            case R.id.corp_layout1: {
                if (corp_check1.isChecked()) {
                    corp_check1.setChecked(false);
                } else {
                    corp_check1.setChecked(true);
                }
            }
            break;
            case R.id.corp_layout2: {
                if (corp_check2.isChecked()) {
                    corp_check2.setChecked(false);
                } else {
                    corp_check2.setChecked(true);
                }
            }
            break;
            case R.id.corp_layout3: {
                if (corp_check3.isChecked()) {
                    corp_check3.setChecked(false);
                } else {
                    corp_check3.setChecked(true);
                }
            }
            break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.corp_check1: {
                if (isChecked) {
                    corp_check2.setChecked(false);
                    corp_check3.setChecked(false);
                }
            }
            break;
            case R.id.corp_check2: {
                if (isChecked) {
                    corp_check1.setChecked(false);
                    corp_check3.setChecked(false);
                }
            }
            break;
            case R.id.corp_check3: {
                if (isChecked) {
                    corp_check1.setChecked(false);
                    corp_check2.setChecked(false);
                }
            }
            break;
        }
    }

    public interface SelectCorpListener {
        void selectCorp(String select_id);
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
