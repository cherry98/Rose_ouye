package com.orange.oy.activity.shakephoto_316;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.info.TeamSpecialtyInfo;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.TagsView;

import java.util.ArrayList;

/**
 * 编辑关键内容 已有内容不可删除 V3.16
 */
public class EditContentActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, TagsView.OnClick, TagsView.OnOtherClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.editcontent_title);
        appTitle.settingName("关键内容");
        appTitle.showBack(this);
    }

    private String key_concent;
    private TagsView editcontent_tags;
    private TextView editcontent_num;
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_content);
        initTitle();
        key_concent = getIntent().getStringExtra("key_concent");
        String[] str = key_concent.split(",");
        index = str.length;
        ArrayList<TeamSpecialtyInfo> list = new ArrayList<>();
        for (String string : str) {
            TeamSpecialtyInfo teamSpecialtyInfo = new TeamSpecialtyInfo();
            teamSpecialtyInfo.setName(string);
            list.add(teamSpecialtyInfo);
        }
        editcontent_num = (TextView) findViewById(R.id.editcontent_num);
        editcontent_num.setText(index + "/10");
        editcontent_tags = (TagsView) findViewById(R.id.editcontent_tags);
        editcontent_tags.setTeamSpecialtyDefaultLabels(list);
        editcontent_tags.setOnOtherClickListener(this, "请输入活动关键词，5个字以内", 10, 5, true);
        editcontent_tags.setOnClick(this);
        editcontent_tags.notifyDataSetChanged();
        findViewById(R.id.editcontent_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                String str = key_concent + "," + editcontent_tags.getSelectLabelForNet()[1];
                intent.putExtra("key_concent2", str);
                setResult(AppInfo.REQUEST_CODE_COLLECT, intent);
                baseFinish();
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void clickPlus() {
        index++;
        editcontent_num.setText(index + "/10");
    }

    @Override
    public void clickMinus() {
        index--;
        editcontent_num.setText(index + "/10");
    }

    @Override
    public void clickOther() {

    }
}
