package com.orange.oy.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;

/**
 * Created by Lenovo on 2018/3/13.
 */

public class DataUploadDialog extends LinearLayout implements View.OnClickListener {

    private OnDataUploadClickListener listener;
    private static Dialog dialog;
    private TextView itemupload_first, itemupload_second, itemupload_prompt, itemupload_prompts;
    private TextView itemupload_thrid;

    public interface OnDataUploadClickListener {
        void firstClick();

        void secondClick();

        void thirdClick();
    }

    public DataUploadDialog(Context context, OnDataUploadClickListener listener) {
        super(context);
        Tools.loadLayout(this, R.layout.item_upload);
        this.listener = listener;
        itemupload_first = (TextView) findViewById(R.id.itemupload_first);
        itemupload_second = (TextView) findViewById(R.id.itemupload_second);
        itemupload_thrid = (TextView) findViewById(R.id.itemupload_thrid);
        itemupload_prompt = (TextView) findViewById(R.id.itemupload_prompt);
        itemupload_prompts = (TextView) findViewById(R.id.itemupload_prompts);
        itemupload_first.setOnClickListener(this);
        itemupload_second.setOnClickListener(this);
        itemupload_thrid.setOnClickListener(this);
    }

    public static DataUploadDialog showDialog(Context context, boolean cancelable, OnDataUploadClickListener listener) {
        dissmisDialog();
        DataUploadDialog view = new DataUploadDialog(context, listener);
        dialog = new Dialog(context, R.style.DialogTheme);
        view.setShow();
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

    private void setShow() {
        itemupload_prompt.setVisibility(VISIBLE);
    }

    public static DataUploadDialog showDialog(Context context, boolean cancelable, OnDataUploadClickListener listener
            , String prompt, String prompts, String str1, String str2, String str3) {
        dissmisDialog();
        DataUploadDialog view = new DataUploadDialog(context, listener);
        view.settingData(prompt, prompts, str1, str2, str3);
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

    private void settingData(String prompt, String prompts, String str1, String str2, String str3) {
        if (!TextUtils.isEmpty(prompt)) {
            itemupload_prompt.setVisibility(VISIBLE);
            itemupload_prompt.setText(prompt);
            itemupload_prompt.getPaint().setStrokeWidth(1);
        }
        if (!TextUtils.isEmpty(prompts)) {
            itemupload_prompts.setVisibility(VISIBLE);
            itemupload_prompts.setText(prompts);
            itemupload_prompts.getPaint().setStrokeWidth(0);
        }
        if (!TextUtils.isEmpty(str1)) {
            itemupload_first.setText(str1);
            itemupload_first.setVisibility(VISIBLE);
        } else {
            itemupload_first.setOnClickListener(null);
            itemupload_first.setVisibility(GONE);
        }
        if (!TextUtils.isEmpty(str2)) {
            itemupload_second.setText(str2);
            itemupload_second.setVisibility(VISIBLE);
        } else {
            itemupload_second.setOnClickListener(null);
            itemupload_second.setVisibility(GONE);
        }
        if (!TextUtils.isEmpty(str3)) {
            itemupload_thrid.setVisibility(VISIBLE);
            itemupload_thrid.setText(str3);
        } else {
            itemupload_thrid.setOnClickListener(null);
            itemupload_thrid.setVisibility(GONE);
        }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.itemupload_first: {
                itemupload_first.setBackgroundColor(R.drawable.dialog_upload1);
                itemupload_first.setTextColor(getResources().getColor(R.color.app_background2));
                itemupload_second.setBackgroundColor(R.drawable.dialog_upload2);
                itemupload_second.setTextColor(getResources().getColor(R.color.homepage_notselect));
                itemupload_thrid.setBackgroundColor(R.drawable.dialog_upload2);
                itemupload_thrid.setTextColor(getResources().getColor(R.color.homepage_notselect));
                if (listener != null) {
                    listener.firstClick();
                }
            }
            break;
            case R.id.itemupload_second: {
                itemupload_first.setBackgroundColor(R.drawable.dialog_upload2);
                itemupload_first.setTextColor(getResources().getColor(R.color.homepage_notselect));
                itemupload_second.setBackgroundColor(R.drawable.dialog_upload1);
                itemupload_second.setTextColor(getResources().getColor(R.color.app_background2));
                itemupload_thrid.setBackgroundColor(R.drawable.dialog_upload2);
                itemupload_thrid.setTextColor(getResources().getColor(R.color.homepage_notselect));
                if (listener != null) {
                    listener.secondClick();
                }
            }
            break;
            case R.id.itemupload_thrid: {
                itemupload_first.setBackgroundColor(R.drawable.dialog_upload2);
                itemupload_first.setTextColor(getResources().getColor(R.color.homepage_notselect));
                itemupload_second.setBackgroundColor(R.drawable.dialog_upload2);
                itemupload_second.setTextColor(getResources().getColor(R.color.homepage_notselect));
                itemupload_thrid.setBackgroundColor(R.drawable.dialog_upload1);
                itemupload_thrid.setTextColor(getResources().getColor(R.color.app_background2));
                if (listener != null) {
                    listener.thirdClick();
                }
            }
            break;
        }
        dissmisDialog();
    }
}
