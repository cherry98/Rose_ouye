package com.orange.oy.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;

/**
 * Created by xiedongyan on 2017/1/10.
 */

public class OpenTimeDialog extends RelativeLayout implements View.OnClickListener {

    private static MyDialog myDialog;
    private TextView textView;

    public OpenTimeDialog(Context context) {
        super(context);
        Tools.loadLayout(this, R.layout.dialog_opentime);
        findViewById(R.id.logoff_button).setOnClickListener(this);
        textView = (TextView) findViewById(R.id.dialog_text_opentime);
    }

    public void setText(String msg) {
        if (textView == null || msg == null) {
            return;
        }
        textView.setText(msg);
    }

    public static void createFloatView(Context mContext, float time) {
        String str = "";
        if (time > 60 && time < 1440) {
            int hour = (int) (time / 60);
            int sec = (int) (time % 60);
            str = "查看时间最近的网点将在 " + hour + "时" + sec + "分 后开启，敬请关注！";
        } else if (time >= 1440) {
            int day = (int) (time / 1440);
            int hour = (int) ((time % 1440) / 60);
            int sec = (int) (time % 60);
            str = "查看时间最近的网点将在 " + day + "天" + hour + "时" + sec + "分 后开启，敬请关注！";
        } else if (time <= 60) {
            str = "查看时间最近的网点将在 " + time + "分 后开启，敬请关注！";
        }
        OpenTimeDialog view = new OpenTimeDialog(mContext);
        view.setText(str);
        myDialog = new MyDialog((BaseActivity) mContext, view, false, 0);
        myDialog.showAtLocation(((BaseActivity) mContext).findViewById(R.id.main), Gravity.CENTER, 0, 0); //设置layout在PopupWindow中显示的位置
    }

    public void onClick(View v) {
        try {
            if (myDialog != null) {
                myDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
