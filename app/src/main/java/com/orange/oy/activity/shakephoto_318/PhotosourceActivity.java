package com.orange.oy.activity.shakephoto_318;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.orange.oy.R;
import com.orange.oy.activity.createtask_317.ToWhomInVisibleActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.view.AppTitle;


/**
 * beibei  照片来源
 */
public class PhotosourceActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.album_title);
        appTitle.settingName("照片来源");
        appTitle.showBack(this);
    }


    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_source);
        initTitle();
        findViewById(R.id.lin_allthing).setOnClickListener(this);
        findViewById(R.id.lin_onlymyself).setOnClickListener(this);
        findViewById(R.id.lin_visible).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            ////photo_source_type    照片来源（1：可直接拍摄；2：可从甩吧相册选择；3：可从手机本地相册选择）【必传】
            //可直接拍摄
            case R.id.lin_allthing: {
                Intent intent = new Intent();
                intent.putExtra("photo_source_type", "1");
                setResult(AppInfo.REQUEST_CODE_COLLECT, intent);
                finish();
            }
            break;
            //可从甩吧相册选择
            case R.id.lin_onlymyself: {
                Intent intent = new Intent();
                intent.putExtra("photo_source_type", "2");
                setResult(AppInfo.REQUEST_CODE_COLLECT, intent);
                finish();
            }
            break;
            //可从手机本地相册选择
            case R.id.lin_visible: {
                Intent intent = new Intent(this, ToWhomInVisibleActivity.class);
                intent.putExtra("photo_source_type", "3");
                setResult(AppInfo.REQUEST_CODE_COLLECT, intent);
                finish();
            }
            break;
        }
    }

    @Override
    public void onBack() {
        baseFinish();
    }


}
