package com.orange.oy.activity.bigchange;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.view.AppTitle;

/**
 *
 * 手机号的修改
 */
public class NicktelphoneReviseActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nicktel_revise);
        final EditText editText = (EditText) findViewById(R.id.nickname_edittext);
        AppTitle appTitle = (AppTitle) findViewById(R.id.nickname_title);
        appTitle.settingName("手机号");
        appTitle.showBack(this);
        appTitle.settingExit("确定", new AppTitle.OnExitClickForAppTitle() {
            @Override
            public void onExit() {
                Tools.d("手机号:" + editText.getText().toString());
                Intent intent = new Intent();
                intent.putExtra("nicktel", editText.getText().toString());
                setResult(AppInfo.REQUEST_CODE_NICKTELPHONE, intent);
                baseFinish();
            }
        });

    }

    @Override
    public void onBack() {
        finish();
    }
}
