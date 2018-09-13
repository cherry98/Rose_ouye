package com.orange.oy.activity.mycorps_315;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.info.TeamSpecialtyInfo;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.TagsView;

import java.util.ArrayList;

/**
 * 空闲时间
 */
public class FreetimeActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, TagsView.OnOtherClickListener, TagsView.OnClick {

    private TagsView teamSpecialtyView;
    private ArrayList<TeamSpecialtyInfo> specialty_list = new ArrayList<>();

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.taskILL_title);
        appTitle.settingName("空闲时间");
        appTitle.showBack(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_often_goto_place);
        initTitle();
        String str = getIntent().getStringExtra("labels");
        if (!TextUtils.isEmpty(str)) {
            String[] strs = str.split(",");
            for (String string : strs) {
                TeamSpecialtyInfo teamSpecialtyInfo = new TeamSpecialtyInfo();
                teamSpecialtyInfo.setCustom(true);
                teamSpecialtyInfo.setSelect(true);
                teamSpecialtyInfo.setName(string);
                specialty_list.add(teamSpecialtyInfo);
            }
        }
        teamSpecialtyView = (TagsView) findViewById(R.id.mydetail_citys_layout);
        teamSpecialtyView.setTeamSpecialtyDefaultLabels(specialty_list);
        teamSpecialtyView.setOnOtherClickListener(this, "请输入空闲时间，最多10个字", 3);
        teamSpecialtyView.setOnClick(this);
        teamSpecialtyView.notifyDataSetChanged();
    }

    public void onBack() {
        String text = "";
        String[] oftengotoPlace = teamSpecialtyView.getSelectLabelForNet();
        for (int i = 0; i < oftengotoPlace.length; i++) {
            text = oftengotoPlace[i] + ",";
        }
        Intent intent = new Intent();
        intent.putExtra("freetime", text.substring(0, text.length() - 1));
        setResult(AppInfo.REQUEST_CODE_FREETIME, intent);
        baseFinish();
    }

    public void onBackPressed() {
        String text = "";
        String[] oftengotoPlace = teamSpecialtyView.getSelectLabelForNet();
        for (int i = 0; i < oftengotoPlace.length; i++) {
            text = oftengotoPlace[i] + ",";
        }
        Intent intent = new Intent();
        intent.putExtra("freetime", text.substring(0, text.length() - 1));
        setResult(AppInfo.REQUEST_CODE_FREETIME, intent);
        super.onBackPressed();
    }

    public void clickOther() {

    }

    @Override
    public void clickPlus() {

    }

    @Override
    public void clickMinus() {

    }
}
