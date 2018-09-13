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
 * Created by Lenovo on 2018/9/3.
 * 任务内容选择任务类型
 */

public class DialogTaskType extends LinearLayout implements View.OnClickListener {

    private OnTaskTypeSelectListener listener;
    private TextView dgtasktype_photo, dgtasktype_viedo, dgtasktype_tape, dgtasktype_record, dgtasktype_exp;
    private View dgtasktype_photo_ly, dgtasktype_viedo_ly, dgtasktype_tape_ly, dgtasktype_record_ly, dgtasktype_exp_ly;

    public DialogTaskType(Context context) {
        super(context);
        Tools.loadLayout(this, R.layout.dialog_tasktype);
        dgtasktype_photo_ly = findViewById(R.id.dgtasktype_photo_ly);
        dgtasktype_viedo_ly = findViewById(R.id.dgtasktype_viedo_ly);
        dgtasktype_tape_ly = findViewById(R.id.dgtasktype_tape_ly);
        dgtasktype_record_ly = findViewById(R.id.dgtasktype_record_ly);
        dgtasktype_exp_ly = findViewById(R.id.dgtasktype_exp_ly);
        dgtasktype_photo = (TextView) findViewById(R.id.dgtasktype_photo);
        dgtasktype_viedo = (TextView) findViewById(R.id.dgtasktype_viedo);
        dgtasktype_tape = (TextView) findViewById(R.id.dgtasktype_tape);
        dgtasktype_record = (TextView) findViewById(R.id.dgtasktype_record);
        dgtasktype_exp = (TextView) findViewById(R.id.dgtasktype_exp);
        dgtasktype_photo_ly.setOnClickListener(this);
        dgtasktype_viedo_ly.setOnClickListener(this);
        dgtasktype_tape_ly.setOnClickListener(this);
        dgtasktype_record_ly.setOnClickListener(this);
        dgtasktype_exp_ly.setOnClickListener(this);
        findViewById(R.id.dgtasktype_close).setOnClickListener(this);
    }

    private static AlertDialog dialog;

    public static DialogTaskType showDialog(Context context, OnTaskTypeSelectListener listener) {
        dissmisDialog();
        DialogTaskType dialogTaskType = new DialogTaskType(context);
        dialogTaskType.setOnTaskTypeSelectListener(listener);
        dialog = new AlertDialog.Builder(context, R.style.DialogTheme).setCancelable(false).create();
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        dialog.show();
        LinearLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        dialog.addContentView(dialogTaskType, layoutParams);
        return dialogTaskType;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dgtasktype_photo_ly: {//拍照
                dgtasktype_photo_ly.setBackgroundResource(R.drawable.itemalltask_background2);
                dgtasktype_viedo_ly.setBackgroundResource(R.drawable.itemalltask_background);
                dgtasktype_record_ly.setBackgroundResource(R.drawable.itemalltask_background);
                dgtasktype_tape_ly.setBackgroundResource(R.drawable.itemalltask_background);
                dgtasktype_exp_ly.setBackgroundResource(R.drawable.itemalltask_background);
                dgtasktype_photo.setTextColor(getResources().getColor(R.color.homepage_select));
                dgtasktype_viedo.setTextColor(getResources().getColor(R.color.homepage_notselect));
                dgtasktype_record.setTextColor(getResources().getColor(R.color.homepage_notselect));
                dgtasktype_tape.setTextColor(getResources().getColor(R.color.homepage_notselect));
                dgtasktype_exp.setTextColor(getResources().getColor(R.color.homepage_notselect));

                if (listener != null) {
                    listener.select(1);
                }
            }
            break;
            case R.id.dgtasktype_viedo_ly: {//视频
                dgtasktype_photo_ly.setBackgroundResource(R.drawable.itemalltask_background);
                dgtasktype_viedo_ly.setBackgroundResource(R.drawable.itemalltask_background2);
                dgtasktype_record_ly.setBackgroundResource(R.drawable.itemalltask_background);
                dgtasktype_tape_ly.setBackgroundResource(R.drawable.itemalltask_background);
                dgtasktype_exp_ly.setBackgroundResource(R.drawable.itemalltask_background);
                dgtasktype_photo.setTextColor(getResources().getColor(R.color.homepage_notselect));
                dgtasktype_viedo.setTextColor(getResources().getColor(R.color.homepage_select));
                dgtasktype_record.setTextColor(getResources().getColor(R.color.homepage_notselect));
                dgtasktype_tape.setTextColor(getResources().getColor(R.color.homepage_notselect));
                dgtasktype_exp.setTextColor(getResources().getColor(R.color.homepage_notselect));
                if (listener != null) {
                    listener.select(2);
                }
            }
            break;
            case R.id.dgtasktype_tape_ly: {//录音
                dgtasktype_photo_ly.setBackgroundResource(R.drawable.itemalltask_background);
                dgtasktype_viedo_ly.setBackgroundResource(R.drawable.itemalltask_background);
                dgtasktype_record_ly.setBackgroundResource(R.drawable.itemalltask_background);
                dgtasktype_tape_ly.setBackgroundResource(R.drawable.itemalltask_background2);
                dgtasktype_exp_ly.setBackgroundResource(R.drawable.itemalltask_background);
                dgtasktype_photo.setTextColor(getResources().getColor(R.color.homepage_notselect));
                dgtasktype_viedo.setTextColor(getResources().getColor(R.color.homepage_notselect));
                dgtasktype_record.setTextColor(getResources().getColor(R.color.homepage_notselect));
                dgtasktype_tape.setTextColor(getResources().getColor(R.color.homepage_select));
                dgtasktype_exp.setTextColor(getResources().getColor(R.color.homepage_notselect));
                if (listener != null) {
                    listener.select(3);
                }
            }
            break;
            case R.id.dgtasktype_record_ly: {//问卷
                dgtasktype_photo_ly.setBackgroundResource(R.drawable.itemalltask_background);
                dgtasktype_viedo_ly.setBackgroundResource(R.drawable.itemalltask_background);
                dgtasktype_record_ly.setBackgroundResource(R.drawable.itemalltask_background2);
                dgtasktype_tape_ly.setBackgroundResource(R.drawable.itemalltask_background);
                dgtasktype_exp_ly.setBackgroundResource(R.drawable.itemalltask_background);
                dgtasktype_photo.setTextColor(getResources().getColor(R.color.homepage_notselect));
                dgtasktype_viedo.setTextColor(getResources().getColor(R.color.homepage_notselect));
                dgtasktype_record.setTextColor(getResources().getColor(R.color.homepage_select));
                dgtasktype_tape.setTextColor(getResources().getColor(R.color.homepage_notselect));
                dgtasktype_exp.setTextColor(getResources().getColor(R.color.homepage_notselect));
                if (listener != null) {
                    listener.select(4);
                }
            }
            break;
            case R.id.dgtasktype_exp_ly: {//体验
                dgtasktype_photo_ly.setBackgroundResource(R.drawable.itemalltask_background);
                dgtasktype_viedo_ly.setBackgroundResource(R.drawable.itemalltask_background);
                dgtasktype_record_ly.setBackgroundResource(R.drawable.itemalltask_background);
                dgtasktype_tape_ly.setBackgroundResource(R.drawable.itemalltask_background);
                dgtasktype_exp_ly.setBackgroundResource(R.drawable.itemalltask_background2);
                dgtasktype_photo.setTextColor(getResources().getColor(R.color.homepage_notselect));
                dgtasktype_viedo.setTextColor(getResources().getColor(R.color.homepage_notselect));
                dgtasktype_record.setTextColor(getResources().getColor(R.color.homepage_notselect));
                dgtasktype_tape.setTextColor(getResources().getColor(R.color.homepage_notselect));
                dgtasktype_exp.setTextColor(getResources().getColor(R.color.homepage_select));
                if (listener != null) {
                    listener.select(5);
                }
            }
            break;
            case R.id.dgtasktype_close: {
            }
            break;
        }
        dissmisDialog();
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

    public interface OnTaskTypeSelectListener {
        void select(int type);
    }

    public void setOnTaskTypeSelectListener(OnTaskTypeSelectListener listener) {
        this.listener = listener;
    }
}
