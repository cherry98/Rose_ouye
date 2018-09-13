package com.orange.oy.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;

/**
 * 目前电话任务用
 */
public class NonExecutionTask extends LinearLayout implements View.OnClickListener {

    public interface OnNonExecutionTaskDialogListener {
        void sumbit(String edittext);
    }

    private OnNonExecutionTaskDialogListener onNonExecutionTaskDialogListener;
    private EditText closetask_edittext;
    private TextView closetask_text1;

    public void setOnNonExecutionTaskDialogListener(OnNonExecutionTaskDialogListener onNonExecutionTaskDialogListener) {
        this.onNonExecutionTaskDialogListener = onNonExecutionTaskDialogListener;
    }

    public NonExecutionTask(Context context) {
        super(context);
        Tools.loadLayout(this, R.layout.dialog_closetask);
        closetask_edittext = (EditText) findViewById(R.id.closetask_edittext);
        closetask_text1 = (TextView) findViewById(R.id.closetask_text1);
        findViewById(R.id.closetask_close).setOnClickListener(this);
        findViewById(R.id.closetask_sumbit).setOnClickListener(this);
        closetask_edittext.setHint("请输入无法执行原因（500字以内）");
        closetask_edittext.setHintTextColor(Color.parseColor("#FFA0A0A0"));
        closetask_edittext.setFilters(new InputFilter[]{new InputFilter.LengthFilter(500)});
    }

    public void setName(String name1, String name2) {
        closetask_text1.setText(name1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.closetask_close: {
                if (myDialog != null && myDialog.isShowing()) {
                    myDialog.dismiss();
                }
            }
            break;
            case R.id.closetask_sumbit: {
                if (onNonExecutionTaskDialogListener != null) {
                    onNonExecutionTaskDialogListener.sumbit(closetask_edittext.getText().toString().trim());
                }
            }
            break;
        }
    }

    public static MyDialog myDialog;

    public static MyDialog showDialog(Context context, String name1, String name2, OnNonExecutionTaskDialogListener onNonExecutionTaskDialogListener) {
        if (myDialog != null && myDialog.isShowing()) {
            myDialog.dismiss();
        }
        NonExecutionTask nonExecutionTask = new NonExecutionTask(context);
        nonExecutionTask.setName(name1, name2);
        nonExecutionTask.setOnNonExecutionTaskDialogListener(onNonExecutionTaskDialogListener);
        myDialog = new MyDialog((BaseActivity) context, nonExecutionTask, false);
        myDialog.showAtLocation(((BaseActivity) context).findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        return myDialog;
    }

}