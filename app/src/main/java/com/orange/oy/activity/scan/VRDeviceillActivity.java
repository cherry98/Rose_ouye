package com.orange.oy.activity.scan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.orange.oy.R;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.fragment.MyFragment;
import com.orange.oy.view.AppTitle;
import com.zmer.testsdkdemo.activity.ConnectWifiActivity;

public class VRDeviceillActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {


    public void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.vrdeviceill_title);
        appTitle.settingName("VR连接说明");
        appTitle.showBack(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vrdeviceill);
        initTitle();
        MyFragment.isRefresh = true;
    }

    public void myConnectWIFI(View view) {
        Intent intent = new Intent(this, ConnectWifiActivity.class);
        startActivity(intent);
    }

    public void zemr_state(View view) {
        Intent intent = new Intent(this, VRDeviceDetailActivity.class);
        intent.putExtra("vrid", getIntent().getStringExtra("vrid"));
        startActivity(intent);
    }

    @Override
    public void onBack() {
        baseFinish();
    }
}
