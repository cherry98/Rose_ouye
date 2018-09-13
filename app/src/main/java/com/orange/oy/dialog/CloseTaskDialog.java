package com.orange.oy.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.activity.ShotActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.info.TaskitemDetailNewInfo;

/**
 * 关闭任务包
 */
public class CloseTaskDialog extends LinearLayout implements View.OnClickListener {
    public interface OnCloseTaskDialogListener {
        void sumbit(String edittext);
    }

    private OnCloseTaskDialogListener onCloseTaskDialogListener;

    public void setOnCloseTaskDialogListener(OnCloseTaskDialogListener listener) {
        onCloseTaskDialogListener = listener;
    }

    private EditText closetask_edittext;
    private TaskitemDetailNewInfo taskitemDetailNewInfo;
    private TextView closetask_text1, closetask_sumbit;

    public void setTaskitemDetailNewInfo(TaskitemDetailNewInfo taskitemDetailNewInfo) {
        this.taskitemDetailNewInfo = taskitemDetailNewInfo;
    }

    public CloseTaskDialog(Context context) {
        super(context);
        Tools.loadLayout(this, R.layout.dialog_closetask);
        closetask_edittext = (EditText) findViewById(R.id.closetask_edittext);
        closetask_text1 = (TextView) findViewById(R.id.closetask_text1);
        closetask_sumbit = (TextView) findViewById(R.id.closetask_sumbit);
        findViewById(R.id.closetask_close).setOnClickListener(this);
        findViewById(R.id.closetask_sumbit).setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.closetask_close: {
                if (myDialog != null && myDialog.isShowing()) {
                    myDialog.dismiss();
                }
            }
            break;
            case R.id.closetask_sumbit: {
                if (onCloseTaskDialogListener != null) {
                    onCloseTaskDialogListener.sumbit(closetask_edittext.getText().toString().trim());
                }
            }
            break;
        }
        close();
    }

    private static MyDialog myDialog;
    private static CloseTaskDialog closeTaskDialog;

    public static MyDialog showDialog(Context context, TaskitemDetailNewInfo taskitemDetailNewInfo, String name,
                                      OnCloseTaskDialogListener listener) {
        if (myDialog != null && myDialog.isShowing()) {
            myDialog.dismiss();
        }
        closeTaskDialog = new CloseTaskDialog(context);
        closeTaskDialog.setOnCloseTaskDialogListener(listener);
        closeTaskDialog.setTaskitemDetailNewInfo(taskitemDetailNewInfo);
        myDialog = new MyDialog((BaseActivity) context, closeTaskDialog, false);
        myDialog.showAtLocation(((BaseActivity) context).findViewById(R.id.main), Gravity.BOTTOM | Gravity
                .CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
        return myDialog;
    }

    public static MyDialog showDialog(Context context, String name,
                                      String button, String defaultText, OnCloseTaskDialogListener listener) {
        return showDialog(context, name, button, defaultText, false, listener);
    }

    public static MyDialog showDialog(Context context, String name, String button, String defaultText
            , boolean isPhone, OnCloseTaskDialogListener listener) {
        if (myDialog != null && myDialog.isShowing()) {
            myDialog.dismiss();
        }
        closeTaskDialog = new CloseTaskDialog(context);
        closeTaskDialog.setOnCloseTaskDialogListener(listener);
        closeTaskDialog.setData(name, button, defaultText, isPhone);
        myDialog = new MyDialog((BaseActivity) context, closeTaskDialog, false);
        myDialog.showAtLocation(((BaseActivity) context).findViewById(R.id.main), Gravity.BOTTOM | Gravity
                .CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
        return myDialog;
    }

    public static MyDialog showDialog2(Context context, String name,
                                       String button, String defaultText, OnCloseTaskDialogListener listener) {
        return showDialog(context, name, button, defaultText, false, listener);
    }


    public static MyDialog showDialog2(Context context, String name, String button, String defaultText
            , boolean isPhone, OnCloseTaskDialogListener listener) {
        if (myDialog != null && myDialog.isShowing()) {
            myDialog.dismiss();
        }
        closeTaskDialog = new CloseTaskDialog(context);
        closeTaskDialog.setOnCloseTaskDialogListener(listener);
        closeTaskDialog.setData2(name, button, defaultText, isPhone);
        myDialog = new MyDialog((BaseActivity) context, closeTaskDialog, false);
        myDialog.showAtLocation(((BaseActivity) context).findViewById(R.id.main), Gravity.BOTTOM | Gravity
                .CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
        return myDialog;
    }

    private void setData2(String name, String button, String defaultText, boolean isPhone) {
        closetask_text1.setText(name);
        closetask_sumbit.setText(button);
        closetask_edittext.setHint(defaultText);
        if (isPhone) {
            findViewById(R.id.closetask_close).setVisibility(GONE);
            findViewById(R.id.closetask_close).setOnClickListener(null);
            closetask_text1.setTextColor(getResources().getColor(R.color.homepage_select));
            closetask_text1.setTextSize(14);
        }
    }

    private void setData(String name, String button, String defaultText, boolean isPhone) {
        closetask_text1.setText(name);
        closetask_sumbit.setText(button);
        closetask_edittext.setText(defaultText);
        if (isPhone) {
            findViewById(R.id.closetask_close).setVisibility(GONE);
            findViewById(R.id.closetask_close).setOnClickListener(null);
            closetask_text1.setTextColor(getResources().getColor(R.color.homepage_select));
            closetask_text1.setTextSize(14);
        }
    }

    public static boolean isOpen() {
        return myDialog != null && myDialog.isShowing();
    }

    public static void close() {
        if (myDialog != null && myDialog.isShowing()) {
            myDialog.dismiss();
        }
    }

    public static boolean isShow() {
        return myDialog != null && myDialog.isShowing();
    }

}
