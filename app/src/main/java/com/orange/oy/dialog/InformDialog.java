package com.orange.oy.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.activity.shakephoto_318.UploadPicturesActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.view.FlowLayoutView;

/**
 * 查看大图举报dialog
 */

public class InformDialog extends LinearLayout implements View.OnClickListener {

    private OnDataUploadClickListener listener;
    private static Dialog dialog;
    private Context mcontext;
    private TextView dialog_srue, confirm_title;
    private ImageView iv_dismiss;
    private EditText item_edit;
    private FlowLayoutView flowLayoutView;

    public interface OnDataUploadClickListener {
        void firstClick(); //×

        void secondClick(String nums, String text);
    }

    private String nums = "";
    private String[] str = new String[]{"无关主题", "翻拍/盗版", "色情", "政治敏感", "其他"};

    public InformDialog(Context context, OnDataUploadClickListener listener) {
        super(context);
        Tools.loadLayout(this, R.layout.item_inform_dialog);
        this.listener = listener;
        mcontext = context;
        dialog_srue = (TextView) findViewById(R.id.dialog_srue);
        item_edit = (EditText) findViewById(R.id.item_edit);
        confirm_title = (TextView) findViewById(R.id.confirm_title);
        iv_dismiss = (ImageView) findViewById(R.id.iv_dismiss);
        flowLayoutView = (FlowLayoutView) findViewById(R.id.createcorps_special);
        iv_dismiss.setOnClickListener(this);
        dialog_srue.setOnClickListener(this);
        getNum();
    }

    public static InformDialog showDialog(Context context, boolean cancelable, OnDataUploadClickListener listener) {
        dissmisDialog();

        InformDialog view = new InformDialog(context, listener);
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

    public static InformDialog showDialog(Context context, String title, boolean cancelable, OnDataUploadClickListener listener) {
        dissmisDialog();
        InformDialog view = new InformDialog(context, listener);
        dialog = new Dialog(context, R.style.DialogTheme);
        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(cancelable);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        view.settingTitle(title);
        dialog.show();
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
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
            case R.id.iv_dismiss: {
                if (listener != null) {
                    listener.firstClick();
                }
            }
            break;
            case R.id.dialog_srue: { //确定
                if (Tools.isEmpty(nums)) {
                    Tools.showToast(mcontext, "请选择标签");
                    return;
                }
                if (listener != null) {
                    listener.secondClick(nums, item_edit.getText().toString());
                }
            }
            break;
        }
        dissmisDialog();
    }


    private void getNum() {
        flowLayoutView.removeAllViews();
        for (int i = 0; i < str.length; i++) {
            TextView textView = new TextView(mcontext);
            textView.setText(str[i]);
            textView.setPadding(5, 5, 5, 5);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(14);
            textView.setWidth(230);
            textView.setMinWidth(230);
            textView.setTextColor(getResources().getColor(R.color.homepage_notselect));
            textView.setBackgroundResource(R.drawable.flowlayout_shape1);
            textView.setId(0);
            flowLayoutView.addView(textView);
            textView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    int isCheck = v.getId();
                    TextView textView = (TextView) v;
                    if (isCheck == 0) {
                        v.setId(1);
                        textView.setTextColor(getResources().getColor(R.color.app_background2));
                        textView.setBackgroundResource(R.drawable.flowlayout_shape2);
                        if (textView.getText().toString().contains("其他")) {
                            int count = flowLayoutView.getChildCount() - 1;
                            for (int i = 0; i < count; i++) {
                                TextView textView1 = (TextView) flowLayoutView.getChildAt(i);
                                textView1.setTextColor(getResources().getColor(R.color.homepage_notselect));
                                textView1.setBackgroundResource(R.drawable.flowlayout_shape1);
                                textView1.setId(0);
                            }
                            nums = textView.getText().toString();
                        } else {
                            if (nums.contains("其他")) {
                                nums = "";
                                TextView textView1 = (TextView) flowLayoutView.getChildAt(flowLayoutView.getChildCount() - 1);
                                textView1.setTextColor(getResources().getColor(R.color.homepage_notselect));
                                textView1.setBackgroundResource(R.drawable.flowlayout_shape1);
                                textView1.setId(0);
                            }
                            if (TextUtils.isEmpty(nums)) {
                                nums = textView.getText().toString();
                            } else {
                                if (!nums.contains(textView.getText().toString())) {
                                    nums = nums + "," + textView.getText().toString();
                                }
                            }
                        }
                    } else {  //不选
                        v.setId(0);
                        textView.setTextColor(getResources().getColor(R.color.homepage_notselect));
                        textView.setBackgroundResource(R.drawable.flowlayout_shape1);
                        if (nums.contains("其他")) {
                            int count = flowLayoutView.getChildCount() - 1;
                            for (int i = 0; i < count; i++) {
                                TextView textView1 = (TextView) flowLayoutView.getChildAt(i);
                                textView1.setTextColor(getResources().getColor(R.color.homepage_notselect));
                                textView1.setBackgroundResource(R.drawable.flowlayout_shape1);
                                textView1.setId(0);
                            }
                            nums = "";

                        } else {
                            if (!Tools.isEmpty(nums)) {
                                if (nums.equals(textView.getText().toString())) {
                                    nums = "";
                                } else if (nums.contains(",") && (nums.substring(0, textView.getText().toString().length() - 1)).equals(textView.getText().toString())) {
                                    nums = nums.replace(textView.getText().toString() + ",", "");
                                } else {
                                    nums = nums.replace("," + textView.getText().toString(), "");
                                }
                            }
                        }
                    }

                }
            });
        }
    }
}
