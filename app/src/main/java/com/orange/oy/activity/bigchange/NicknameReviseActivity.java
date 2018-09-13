package com.orange.oy.activity.bigchange;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.widget.EditText;

import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.view.AppTitle;

public class NicknameReviseActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nickname_revise);
        editText = (EditText) findViewById(R.id.nickname_edittext);
        AppTitle appTitle = (AppTitle) findViewById(R.id.nickname_title);
        appTitle.settingName("昵称");
        appTitle.showBack(this);
        appTitle.settingExit("确定", new AppTitle.OnExitClickForAppTitle() {
            @Override
            public void onExit() {
//                Tools.d("昵称:" + editText.getText().toString());
                Intent intent = new Intent();
                intent.putExtra("nickname", editText.getText().toString());
                setResult(AppInfo.REQUEST_CODE_NICKNAME, intent);
                baseFinish();
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                String str = editText.getText().toString();
                byte[] bytes = str.getBytes();
                if (bytes.length > 30) {
                    byte[] tempBytes = new byte[30];
                    System.arraycopy(bytes, 0, tempBytes, 0, 30);
                    String newStr = new String(tempBytes);
                    editText.setText(newStr);
                    Selection.setSelection(editText.getEditableText(), newStr.length());
                }
            }
        });
    }

    @Override
    public void onBack() {
        finish();
    }
}
