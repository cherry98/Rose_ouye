package com.orange.oy.activity.mycorps_315;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.orange.oy.R;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.fragment.AssignFragment;
import com.orange.oy.view.AppTitle;

/**
 * Created by Administrator on 2018/5/31.
 * 老指派任务
 */

public class AssignActivity extends BaseActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign);
        AppTitle apptitle = (AppTitle) findViewById(R.id.apptitle);
        apptitle.showBack(new AppTitle.OnBackClickForAppTitle() {
            public void onBack() {
                baseFinish();
            }
        });
        apptitle.settingName("指派任务");
        AssignFragment assignFragment = new AssignFragment();
        FragmentManager fMgr = getSupportFragmentManager();
        FragmentTransaction ft = fMgr.beginTransaction();
        ft.replace(R.id.fragmentRoot, assignFragment, "myDetailFragment");
        ft.commit();
    }
}
