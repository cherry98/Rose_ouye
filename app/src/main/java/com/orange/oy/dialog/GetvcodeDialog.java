package com.orange.oy.dialog;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/9/12.
 */

public class GetvcodeDialog extends LinearLayout {
    private TextView identifycode_obtain;
    private EditText identifycode_code;
    private View withdraw_button;
    private static GetvcodeDialog getvcodeDialog;
    private static MyDialog myDialog;

    public static void dismiss() {
        if (myDialog != null && myDialog.isShowing()) {
            myDialog.dismiss();
        }
    }

    public static GetvcodeDialog ShowGetvcodeDialog(Context context) {
        if (myDialog != null && myDialog.isShowing()) {
            myDialog.dismiss();
        }
        getvcodeDialog = new GetvcodeDialog(context);
        myDialog = new MyDialog((BaseActivity) context, getvcodeDialog, false);
        myDialog.showAtLocation(((BaseActivity) context).findViewById(R.id.main), Gravity.CENTER, 0, 0);
        PopupWindow.OnDismissListener onDismissListener = new PopupWindow.OnDismissListener() {
            public void onDismiss() {
                getvcodeDialog.stopSendsms();
                getvcodeDialog = null;
                myDialog.backgroundAlpha(1f);
            }
        };
        myDialog.setOnDismissListener(onDismissListener);
        return getvcodeDialog;
    }

    public GetvcodeDialog(Context context) {
        super(context);
        Tools.loadLayout(this, R.layout.dialog_getvcode);
        identifycode_obtain = (TextView) findViewById(R.id.identifycode_obtain);
        identifycode_code = (EditText) findViewById(R.id.identifycode_code);
        withdraw_button = findViewById(R.id.withdraw_button);
        handler = new MyHandler(context);
        identifycode_obtain.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (!timer) {
                    sendsms();
                }
            }
        });
    }

    public EditText getIdentifycode_code() {
        return identifycode_code;
    }

    public View getWithdraw_button() {
        return withdraw_button;
    }

    public void stopSendsms() {
        handler.removeMessages(0);
        handler = null;
        if (sendsms != null) {
            sendsms.stop(Urls.Sendsms);
        }
    }

    private NetworkConnection sendsms;

    private void sendsms() {
        if (sendsms == null) {
            sendsms = new NetworkConnection(getContext()) {
                public Map<String, String> getNetworkParams() {
                    return null;
                }
            };
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("ident", "5");
        params.put("token", Tools.getToken());
        sendsms.setMapParams(params);
        sendsms.sendPostRequest(Urls.Sendsms, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        handler.sendEmptyMessage(0);
                    } else {
                        Tools.showToast(getContext(), jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(getContext(), getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    private MyHandler handler = null;
    private boolean timer = false;
    private int maxTime = 60;

    private class MyHandler extends Handler {
        Context context;

        MyHandler(Context context) {
            this.context = context;
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: { //倒计时
                    if (maxTime > 0) {
                        timer = true;
                        if (identifycode_obtain != null)
                            identifycode_obtain.setText(maxTime-- + "");
                        sendEmptyMessageDelayed(0, 1000);
                    } else {
                        timer = false;
                        maxTime = 60;
                        if (identifycode_obtain != null)
                            identifycode_obtain.setText(context.getResources().getString(R.string.register_getcaptcha));
                    }
                }
                break;
            }
        }
    }
}
