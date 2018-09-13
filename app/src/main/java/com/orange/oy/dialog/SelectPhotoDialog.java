package com.orange.oy.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;

/**
 * Created by Lenovo on 2018/5/3.
 * 选择拍照还是从相册选择
 */

public class SelectPhotoDialog extends LinearLayout {
    private TextView dialog_photosel_item1, dialog_photosel_item2, dialog_photosel_cancel, dialog_photosel_item3;
    private View dialog_photosel_item1_layout, dialog_photosel_item2_layout, dialog_photosel_item3_layout;
    private ImageView dialog_photosel_item1_img, dialog_photosel_item2_img, dialog_photosel_item3_img;
    private View dialog_photosel_item3_line;

    public SelectPhotoDialog(Context context) {
        super(context);
        Tools.loadLayout(this, R.layout.dialog_bottom_photoselecter);
        initView();
    }

    private void initView() {
        dialog_photosel_item1_layout = findViewById(R.id.dialog_photosel_item1_layout);
        dialog_photosel_item2_layout = findViewById(R.id.dialog_photosel_item2_layout);
        dialog_photosel_item3_layout = findViewById(R.id.dialog_photosel_item3_layout);
        dialog_photosel_item1_img = (ImageView) findViewById(R.id.dialog_photosel_item1_img);
        dialog_photosel_item2_img = (ImageView) findViewById(R.id.dialog_photosel_item2_img);
        dialog_photosel_item3_img = (ImageView) findViewById(R.id.dialog_photosel_item3_img);
        dialog_photosel_item1 = (TextView) findViewById(R.id.dialog_photosel_item1);
        dialog_photosel_item2 = (TextView) findViewById(R.id.dialog_photosel_item2);
        dialog_photosel_cancel = (TextView) findViewById(R.id.dialog_photosel_cancel);
        dialog_photosel_item3 = (TextView) findViewById(R.id.dialog_photosel_item3);
        dialog_photosel_item3_line = findViewById(R.id.dialog_photosel_item3_line);
    }

    private SelectPhotoDialog settingThree(OnClickListener onClickListener) {
        dialog_photosel_item3_layout.setVisibility(VISIBLE);
        dialog_photosel_item3_line.setVisibility(VISIBLE);
        dialog_photosel_item3_layout.setOnClickListener(onClickListener);
        return this;
    }

    public SelectPhotoDialog initShowStr(String s1, String s2, String s3) {
        dialog_photosel_item1.setText(s1);
        dialog_photosel_item2.setText(s2);
        dialog_photosel_item3.setText(s3);
        return this;
    }

    public SelectPhotoDialog settingImg(Activity activity, int resid1, int resid2, int resid3) {
        settingImgItem1(activity, resid1);
        settingImgItem2(activity, resid2);
        settingImgItem3(activity, resid3);
        return this;
    }

    public SelectPhotoDialog settingImgItem1(Activity activity, int resid) {
        dialog_photosel_item1_img.setImageResource(resid);
        dialog_photosel_item1_img.setVisibility(VISIBLE);
        dialog_photosel_item1.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        return this;
    }

    public SelectPhotoDialog settingImgItem2(Activity activity, int resid) {
        dialog_photosel_item2_img.setImageResource(resid);
        dialog_photosel_item2_img.setVisibility(VISIBLE);
        dialog_photosel_item2.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        return this;
    }

    public SelectPhotoDialog settingImgItem3(Activity activity, int resid) {
        dialog_photosel_item3_img.setImageResource(resid);
        dialog_photosel_item3_img.setVisibility(VISIBLE);
        dialog_photosel_item3.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        return this;
    }

    public SelectPhotoDialog goneItem1() {
        dialog_photosel_item1_layout.setVisibility(GONE);
        findViewById(R.id.dialog_photosel_item2_line).setVisibility(GONE);
        if (dialog_photosel_item2_layout.getVisibility() == GONE) {
            findViewById(R.id.dialog_photosel_item3_line).setVisibility(GONE);
        }
        return this;
    }

    public SelectPhotoDialog goneItem2() {
        dialog_photosel_item2_layout.setVisibility(GONE);
        findViewById(R.id.dialog_photosel_item2_line).setVisibility(GONE);
        if (dialog_photosel_item1_layout.getVisibility() == GONE) {
            findViewById(R.id.dialog_photosel_item3_line).setVisibility(GONE);
        }
        return this;
    }

    public SelectPhotoDialog goneItem3() {
        dialog_photosel_item3_layout.setVisibility(GONE);
        findViewById(R.id.dialog_photosel_item3_line).setVisibility(GONE);
        return this;
    }

    private static MyDialog myDialog;

    //拍照或者从相册选取照片
    public static SelectPhotoDialog showPhotoSelecter(Context context, boolean isFeedback, OnClickListener item1Listener,
                                                      OnClickListener item2Listener) {
        if (myDialog != null && myDialog.isShowing()) {
            myDialog.dismiss();
        }
        SelectPhotoDialog selectPhotoDialog = new SelectPhotoDialog(context);
        if (isFeedback) {
            selectPhotoDialog.dialog_photosel_item2.setText("从手机相册选择");
        }
        selectPhotoDialog.dialog_photosel_item1_layout.setOnClickListener(item1Listener);
        selectPhotoDialog.dialog_photosel_item2_layout.setOnClickListener(item2Listener);
        selectPhotoDialog.dialog_photosel_cancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dissmisDialog();
            }
        });
        myDialog = new MyDialog((BaseActivity) context, selectPhotoDialog, false);
        myDialog.showAtLocation(((BaseActivity) context).findViewById(R.id.main), Gravity.BOTTOM | Gravity
                .CENTER_HORIZONTAL, 0, 0);
        return selectPhotoDialog;
    }

    /***
     *  从相册选取照片
     * @param context
     * @param isvisible
     * @param isFeedback
     * @param item1Listener
     * @param item2Listener
     * @return
     */
    public static SelectPhotoDialog showPhotoSelecter2(Context context, boolean isvisible, boolean isFeedback, OnClickListener item1Listener,
                                                       OnClickListener item2Listener) {
        if (myDialog != null && myDialog.isShowing()) {
            myDialog.dismiss();
        }
        SelectPhotoDialog selectPhotoDialog = new SelectPhotoDialog(context);
        if (isFeedback) {
            selectPhotoDialog.dialog_photosel_item2.setText("从手机相册选择");
            if (isvisible) {
                selectPhotoDialog.dialog_photosel_item2.setText("从本地相册选择");
                selectPhotoDialog.dialog_photosel_item1.setVisibility(GONE);
            }
        }
        selectPhotoDialog.dialog_photosel_item1_layout.setOnClickListener(item1Listener);
        selectPhotoDialog.dialog_photosel_item2_layout.setOnClickListener(item2Listener);
        selectPhotoDialog.dialog_photosel_cancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dissmisDialog();
            }
        });
        myDialog = new MyDialog((BaseActivity) context, selectPhotoDialog, false);
        myDialog.showAtLocation(((BaseActivity) context).findViewById(R.id.main), Gravity.BOTTOM | Gravity
                .CENTER_HORIZONTAL, 0, 0);
        return selectPhotoDialog;
    }

    public static SelectPhotoDialog showPhotoSelecter(Context context, OnClickListener item1Listener, OnClickListener
            item2Listener) {
        return showPhotoSelecter(context, false, item1Listener, item2Listener);
    }

    public static SelectPhotoDialog showPhotoSelecterAll(Context context, OnClickListener item1Listener,
                                                         OnClickListener item2Listener, OnClickListener item3Listener) {
        return showPhotoSelecter(context, false, item1Listener, item2Listener).settingThree(item3Listener);
    }


    public static SelectPhotoDialog showPhotoSelecter2(Context context, boolean b, OnClickListener item1Listener, OnClickListener
            item2Listener) {
        return showPhotoSelecter2(context, false, false, item1Listener, item2Listener);
    }

    public static SelectPhotoDialog showPhotoSelecterAll2(Context context, boolean b, OnClickListener item1Listener,
                                                          OnClickListener item2Listener, OnClickListener item3Listener) {
        return showPhotoSelecter2(context, false, false, item1Listener, item2Listener).settingThree(item3Listener);
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

}
