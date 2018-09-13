package com.orange.oy.activity.mycorps_314;

import android.os.Bundle;

import com.orange.oy.R;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.view.AppTitle;

/**
 * 收不到验证码说明页面
 */
public class IdentifycodeExplainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identifycode_explain);
        AppTitle appTitle = (AppTitle) findViewById(R.id.icexplain_title);
        appTitle.settingName("收不到验证码");
        appTitle.showBack(new AppTitle.OnBackClickForAppTitle() {
            @Override
            public void onBack() {
                baseFinish();
            }
        });

    }
}
