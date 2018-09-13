package com.orange.oy.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.widget.LinearLayout;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.view.ScrollerNumberPicker;

import java.util.List;

public class SelectListDialog extends LinearLayout {
    private ScrollerNumberPicker list_bottom_wheelview;
    private List<String> datas;
    private static AlertDialog dialog;

    public SelectListDialog(Context context) {
        super(context);
        Tools.loadLayout(this, R.layout.dialog_list_bottom);
        list_bottom_wheelview = (ScrollerNumberPicker) findViewById(R.id.list_bottom_wheelview);
    }

    protected void setOnSelectListener(ScrollerNumberPicker.OnSelectListener listener) {
        list_bottom_wheelview.setOnSelectListener(listener);
    }

    protected void setDatas(List<String> datas) {
        this.datas = datas;
        list_bottom_wheelview.setData(this.datas);
        list_bottom_wheelview.setDefault(0);
    }

    public static AlertDialog showDialog(Context context, List<String> datas, boolean cancelable,
                                         ScrollerNumberPicker.OnSelectListener listener) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        SelectListDialog view = new SelectListDialog(context);
        view.setOnSelectListener(listener);
        view.setDatas(datas);
        dialog = new AlertDialog.Builder(context, R.style.DialogTheme).setCancelable(cancelable).create();
        dialog.setCanceledOnTouchOutside(cancelable);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        dialog.show();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LinearLayout
                .LayoutParams.WRAP_CONTENT);
        dialog.addContentView(view, params);
        return dialog;
    }

}
