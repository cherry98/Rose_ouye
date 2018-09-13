package com.orange.oy.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;


/**
 * 标签提示
 */

public class DotagDialog extends LinearLayout implements View.OnClickListener {

    private OnDataUploadClickListener listener;
    private static Dialog dialog;
    private TextView itemupload_first, itemupload_second, itemupload_prompt, confirm_title;
    private TextView itemupload_thrid;
    private ImageView iv_dismiss;

    public interface OnDataUploadClickListener {
        void firstClick();

        void secondClick();
    }

    public DotagDialog(Context context, OnDataUploadClickListener listener) {
        super(context);
        Tools.loadLayout(this, R.layout.item_dotag);
        this.listener = listener;
        itemupload_first = (TextView) findViewById(R.id.itemupload_first);  //存为标签
        itemupload_second = (TextView) findViewById(R.id.itemupload_second); //忽略
        confirm_title = (TextView) findViewById(R.id.confirm_title);
        iv_dismiss = (ImageView) findViewById(R.id.iv_dismiss);
        itemupload_prompt = (TextView) findViewById(R.id.itemupload_prompt2);
        itemupload_first.setOnClickListener(this);
        itemupload_second.setOnClickListener(this);
        iv_dismiss.setOnClickListener(this);
    }

    public static DotagDialog showDialog(Context context, boolean cancelable, OnDataUploadClickListener listener) {
        dissmisDialog();
        DotagDialog view = new DotagDialog(context, listener);
        dialog = new Dialog(context, R.style.DialogTheme);
        view.setShow();
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

    private void setShow() {
        // itemupload_prompt.setVisibility(VISIBLE);
    }

    public static DotagDialog showDialog(Context context, String title, boolean cancelable, OnDataUploadClickListener listener) {
        dissmisDialog();
        DotagDialog view = new DotagDialog(context, listener);
        dialog = new Dialog(context, R.style.DialogTheme);
        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(cancelable);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        view.settingTitle(title);
        dialog.show();
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        dialog.addContentView(view, params);
        return view;
    }

    public static DotagDialog showDialog2(Context context, String title, boolean cancelable, String str1,
                                          String str2, OnDataUploadClickListener listener) {
        dissmisDialog();
        DotagDialog view = new DotagDialog(context, listener);
        dialog = new Dialog(context, R.style.DialogTheme);
        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(cancelable);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        view.settingTitle(title);
        view.itemupload_first.setText(str1);
        view.itemupload_second.setText(str2);
        view.itemupload_prompt.setVisibility(VISIBLE);
        view.iv_dismiss.setVisibility(VISIBLE);

        view.iv_dismiss.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dissmisDialog();
            }
        });
        dialog.show();
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        dialog.addContentView(view, params);
        return view;
    }

    protected void settingTitle(String title) {
        if (title != null) {
            confirm_title.setVisibility(View.VISIBLE);
            confirm_title.setText(title);
        } else {
            confirm_title.setVisibility(View.GONE);
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
                if (listener != null) {
                    listener.secondClick();
                }
            }
            break;
        }
        dissmisDialog();
    }
}
