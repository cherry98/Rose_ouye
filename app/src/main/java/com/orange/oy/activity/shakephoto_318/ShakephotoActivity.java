package com.orange.oy.activity.shakephoto_318;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;

import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.fragment.ShakephotoFragment;
import com.orange.oy.info.shakephoto.ShakeThemeInfo;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/8/10.
 * 甩图&甩吧拍照页
 */

public class ShakephotoActivity extends BaseActivity {
    private int time = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shakephoto);
        checkPermission();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, AppInfo
                        .REQUEST_CODE_ASK_CAMERA);
                time = 1000;
                return;
            }
        }
        myHandler.sendEmptyMessageDelayed(0, time);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        switch (requestCode) {
            case AppInfo.REQUEST_CODE_ASK_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                } else {
                    Tools.showToast(this, "摄像头权限获取失败");
                    baseFinish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    private MyHandler myHandler = new MyHandler();

    private class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ShakephotoFragment shakephotofragment = new ShakephotoFragment();
            Serializable serializable = getIntent().getSerializableExtra("shakeThemeInfo");
            if (serializable != null) {
                shakephotofragment.initjoinActivity((ShakeThemeInfo) serializable, getIntent().getBooleanExtra("isuppic", false),
                        getIntent().getStringExtra("mPath"));
            }
            ft.replace(R.id.main, shakephotofragment, "shakephotofragment");
            ft.commit();
        }
    }
}
