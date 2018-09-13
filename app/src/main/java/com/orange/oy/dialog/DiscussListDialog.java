package com.orange.oy.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.view.DiscussListView;

import static com.orange.oy.R.id.dialogprize_add;
import static com.orange.oy.R.id.dialogprize_select;

/**
 * V3.21  查看大图，上拉view
 */

public class DiscussListDialog extends LinearLayout implements View.OnClickListener {

    private OnPrizeSettingListener listener;
    private static DiscussListView discussView;
    private String fi_id; //照片id
    private String sai_id; //广告图片id
    private String is_advertisement; //是否是广告
    private static Activity mcontext;

    private static MyDialog myDialog;
    private static DiscussListDialog discussListDialog;

    public DiscussListDialog(Context context) {
        super(context);
        mcontext = (Activity) context;
        Tools.loadLayout(this, R.layout.dialog_discusslist);
        discussView = (DiscussListView) findViewById(R.id.discussView);

    }

    public static MyDialog showDialog(Context context, boolean cancelable, OnPrizeSettingListener listener) {
        return showDialog(context, cancelable, null, null, null, listener);
    }

    public static MyDialog showDialog(Context context, boolean cancelable, String fi_id, String sai_id, String is_advertisement, OnPrizeSettingListener listener) {
        if (myDialog != null && myDialog.isShowing()) {
            dissmisDialog();
        }
        discussListDialog = new DiscussListDialog(context);
        discussListDialog.setOnPrizeSettingListener(listener);
        discussView.SetData(fi_id, sai_id, is_advertisement);
        myDialog = new MyDialog((BaseActivity) context, discussListDialog, false);
        myDialog.showAtLocation(((BaseActivity) context).findViewById(R.id.main), Gravity.BOTTOM | Gravity
                .CENTER_HORIZONTAL, 0, 0);
        return myDialog;
    }


    public static void dissmisDialog() {
        try {
            if (myDialog != null && myDialog.isShowing()) {
                myDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.itemshare_cancel: {
                if (myDialog != null && myDialog.isShowing()) {
                    dissmisDialog();
                }
            }
            break;

        }
    }

    public void setOnPrizeSettingListener(OnPrizeSettingListener listener) {
        this.listener = listener;
    }


    public interface OnPrizeSettingListener {
        void firstClick();

        void secondClick();
    }
}
