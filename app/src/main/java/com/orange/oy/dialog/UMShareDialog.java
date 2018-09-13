package com.orange.oy.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.orange.oy.R;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;

/**
 * Created by Lenovo on 2018/4/17.
 * 自定义分享页面
 */

public class UMShareDialog extends LinearLayout implements View.OnClickListener {

    private UMShareListener umShareListener;

    public UMShareDialog(Context context) {
        super(context);
        Tools.loadLayout(this, R.layout.item_umshare);
        initView();
    }

    private void initView() {
        findViewById(R.id.itemshare_cancel).setOnClickListener(this);
        findViewById(R.id.itemshare_layout1).setOnClickListener(this);
        findViewById(R.id.itemshare_layout2).setOnClickListener(this);
        findViewById(R.id.itemshare_layout3).setOnClickListener(this);
        findViewById(R.id.itemshare_layout4).setOnClickListener(this);
        findViewById(R.id.itemshare_layout5).setOnClickListener(this);
    }

    public void showWXMIN() {
        findViewById(R.id.itemshare_layout6).setVisibility(VISIBLE);
        findViewById(R.id.itemshare_layout6_line).setVisibility(VISIBLE);
        findViewById(R.id.itemshare_layout6).setOnClickListener(this);
    }

    private static MyDialog myDialog;
    private static UMShareDialog umShareDialog;

    public static MyDialog showDialog(Context context, boolean cancelable, UMShareListener umShareListener) {
        return showDialog(context, cancelable, umShareListener, false);
    }

    public static MyDialog showDialog(Context context, boolean cancelable, UMShareListener umShareListener, boolean showWXMIN) {
        if (myDialog != null && myDialog.isShowing()) {
            dissmisDialog();
        }
        umShareDialog = new UMShareDialog(context);
        umShareDialog.setUMShareListener(umShareListener);
        if (showWXMIN)
            umShareDialog.showWXMIN();
        myDialog = new MyDialog((BaseActivity) context, umShareDialog, false);
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
            case R.id.itemshare_layout1: {
                umShareListener.shareOnclick(1);
            }
            break;
            case R.id.itemshare_layout2: {
                umShareListener.shareOnclick(2);
            }
            break;
            case R.id.itemshare_layout3: {
                umShareListener.shareOnclick(3);
            }
            break;
            case R.id.itemshare_layout4: {
                umShareListener.shareOnclick(4);
            }
            break;
            case R.id.itemshare_layout5: {
                umShareListener.shareOnclick(5);
            }
            break;
            case R.id.itemshare_layout6: {
                umShareListener.shareOnclick(6);
            }
            break;
        }
        if (myDialog != null && myDialog.isShowing()) {
            dissmisDialog();
        }
    }

    public void setUMShareListener(UMShareListener umShareListener) {
        this.umShareListener = umShareListener;
    }

    public interface UMShareListener {
        void shareOnclick(int type);
    }
}
