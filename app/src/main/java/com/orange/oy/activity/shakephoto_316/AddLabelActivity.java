package com.orange.oy.activity.shakephoto_316;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.orange.oy.R;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.view.AppTitle;

/**
 * Created by Administrator on 2018/6/15.
 * 添加标签页
 */

public class AddLabelActivity extends BaseActivity {
    private EditText addlabel_edit;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addlabel);
        AppTitle appTitle = (AppTitle) findViewById(R.id.addlabel_title);
        addlabel_edit = (EditText) findViewById(R.id.addlabel_edit);
        appTitle.showBack(new AppTitle.OnBackClickForAppTitle() {
            public void onBack() {
                baseFinish();
            }
        });
        appTitle.settingName("添加标签");
        findViewById(R.id.addlabel_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (addlabel_edit.getText().toString().trim().equals("")) {
                    addlabel_edit.setText("");
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("result", addlabel_edit.getText().toString());
                setResult(RESULT_OK, intent);
                baseFinish();
            }
        });
    }
}
