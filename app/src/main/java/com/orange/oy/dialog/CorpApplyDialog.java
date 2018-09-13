package com.orange.oy.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.activity.mycorps_315.TaskProtocolActivity;
import com.orange.oy.base.Tools;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Lenovo on 2018/5/23.
 * 战队领取弹出框
 */

public class CorpApplyDialog extends LinearLayout implements View.OnClickListener {

    private CorpApplyListenter corpApplyListenter;
    private TextView corpapply_title, corpapply_message1, corpapply_message2, corpapply_message3;
    private CheckBox corpapply_check;
    private TextView corpapply_confirm, corpapply_cancel;
    private Context context;

    public CorpApplyDialog(Context context) {
        super(context);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        this.context = context;
        Tools.loadLayout(this, R.layout.dialog_corpapply);
        initView();
        corpapply_confirm.setOnClickListener(this);
        corpapply_cancel.setOnClickListener(this);
        findViewById(R.id.corpapply_protocol).setOnClickListener(this);
    }

    private void initView() {
        corpapply_title = (TextView) findViewById(R.id.corpapply_title);
        corpapply_message1 = (TextView) findViewById(R.id.corpapply_message1);
        corpapply_message2 = (TextView) findViewById(R.id.corpapply_message2);
        corpapply_message3 = (TextView) findViewById(R.id.corpapply_message3);
        corpapply_check = (CheckBox) findViewById(R.id.corpapply_check);
        corpapply_confirm = (TextView) findViewById(R.id.corpapply_confirm);
        corpapply_cancel = (TextView) findViewById(R.id.corpapply_cancel);
    }

    //分配人员多条显示
    public static AlertDialog showDialog(Context context, String title, String msg1, CorpApplyListenter corpApplyListenter) {
        return showDialog(context, title, msg1, null, null, null, null, null, "", "", "", false, corpApplyListenter);
    }

    private static AlertDialog myDialog;

    //领取任务确认信息
    public static AlertDialog showDialog(Context context, String title, String msg1, String msg2, String msg2_1, String msg2_2,
                                         String msg3, String msg3_1, String team_id, String project_id, String package_id,
                                         boolean isCheck, CorpApplyListenter corpApplyListenter) {
        if (myDialog != null && myDialog.isShowing()) {
            dissmisDialog();
        }
        CorpApplyDialog corpApplyDialog = new CorpApplyDialog(context);
        corpApplyDialog.setData(title, msg1, msg2, msg2_1, msg2_2, msg3, msg3_1, isCheck, team_id, project_id, package_id);
        corpApplyDialog.setCorpApplyListenter(corpApplyListenter);
        myDialog = new AlertDialog.Builder(context, R.style.DialogTheme).setCancelable(false).create();
        myDialog.setCanceledOnTouchOutside(false);
        Window window = myDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        myDialog.show();
//        if (!isCheck) {//设置弹窗显示高度
//            WindowManager.LayoutParams params = myDialog.getWindow().getAttributes();
//            params.height = ((Activity) context).getWindowManager().getDefaultDisplay().getHeight() / 2;
//            myDialog.getWindow().setAttributes(params);
//        }
        LinearLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        myDialog.addContentView(corpApplyDialog, layoutParams);
        return myDialog;
    }

    private void setData(String title, String msg1, String msg2, String msg2_1, String msg2_2, String msg3,
                         String msg3_1, boolean isCheck, String team_id, String project_id, String package_id) {
        this.team_id = team_id;
        this.package_id = package_id;
        this.projectid = project_id;
        if (TextUtils.isEmpty(title)) {
            title = "提示！";
        }
        corpapply_title.setText(title);
        if (TextUtils.isEmpty(msg1)) {
            corpapply_message1.setVisibility(GONE);
        } else {
            corpapply_message1.setVisibility(VISIBLE);
            corpapply_message1.setText(msg1);
        }
        if (TextUtils.isEmpty(msg2)) {
            corpapply_message2.setVisibility(GONE);
        } else {
            corpapply_message2.setVisibility(VISIBLE);
            int start1 = msg2.indexOf(msg2_1);
            int end1 = start1 + msg2_1.length();
            int start2 = msg2.indexOf(msg2_2);
            int end2 = start2 + msg2_2.length();
            SpannableStringBuilder builder = new SpannableStringBuilder(msg2);
            builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.makesure)),
                    start1, end1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.makesure)),
                    start2, end2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            corpapply_message2.setText(builder);
        }
        if (TextUtils.isEmpty(msg3)) {
            corpapply_message3.setVisibility(GONE);
        } else {
            corpapply_message3.setVisibility(VISIBLE);
            int start1 = msg3.indexOf(msg3_1);
            int end1 = start1 + msg3_1.length();
            SpannableStringBuilder builder = new SpannableStringBuilder(msg3);
            builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.makesure)),
                    start1, end1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            corpapply_message3.setText(builder);
        }
        if (isCheck) {
            corpapply_cancel.setText("取消领取");
            corpapply_confirm.setText("确认领取");
            findViewById(R.id.corpapply_checkly).setVisibility(VISIBLE);
        } else {
            corpapply_cancel.setText("取消");
            corpapply_confirm.setText("确认");
            findViewById(R.id.corpapply_checkly).setVisibility(GONE);
        }
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

    public void setCorpApplyListenter(CorpApplyListenter corpApplyListenter) {
        this.corpApplyListenter = corpApplyListenter;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.corpapply_confirm: {
                if (findViewById(R.id.corpapply_checkly).getVisibility() == VISIBLE) {
                    if (!corpapply_check.isChecked()) {
                        Tools.showToast(context, "请勾选任务协议");
                        return;
                    }
                }
                corpApplyListenter.corpApply_confirm();
                dissmisDialog();
            }
            break;
            case R.id.corpapply_cancel: {
                corpApplyListenter.corpApply_cancel();
            }
            dissmisDialog();
            break;
            case R.id.corpapply_protocol: {//任务协议
                Intent intent = new Intent(context, TaskProtocolActivity.class);
                intent.putExtra("team_id", team_id);
                intent.putExtra("project_id", projectid);
                intent.putExtra("package_id", package_id);
                intent.putExtra("type", "1");//从弹窗跳转
                context.startActivity(intent);
            }
            break;
        }
    }

    private String team_id, projectid, package_id;

    public interface CorpApplyListenter {
        void corpApply_cancel();

        void corpApply_confirm();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String data) {
        if ("1".equals(data)) {
            corpapply_check.setChecked(true);
        }
    }
}
