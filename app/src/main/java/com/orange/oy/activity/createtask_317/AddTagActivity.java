package com.orange.oy.activity.createtask_317;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.orange.oy.base.AppInfo.REQUEST_CODE_ADD;

/**
 * 添加标签
 */
public class AddTagActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.nickname_title);
        appTitle.settingName("创建标签");
        appTitle.showBack(this);
    }

    private void iniNetworkConnection() {
        createLabel = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(AddTagActivity.this));
                params.put("token", Tools.getToken());
                params.put("label_name", editText.getText().toString());
                params.put("usermobilelist", temp); //手机号信息，多个以英文逗号分隔
                Tools.d("tag", params.toString());
                return params;
            }
        };
    }

    private EditText editText;
    private String temp, Isvisible;
    private TextView addphone_button;
    private String invisible_type; //对谁可见的类型【必传】1为全部，2为仅自己可见，3为谁不可见任务，4为谁可见任务
    private NetworkConnection createLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtag);
        initTitle();
        temp = getIntent().getStringExtra("temp");//手机号
        Isvisible = getIntent().getStringExtra("Isvisible"); //  1可见  2 不可见
        if (!Tools.isEmpty(Isvisible)) {   //2为谁不可见红包，3为谁可见红包
            if (Isvisible.equals("1")) {
                invisible_type = "4";
            } else {
                invisible_type = "3";
            }
        }

        iniNetworkConnection();
        addphone_button = (TextView) findViewById(R.id.addphone_button);
        editText = (EditText) findViewById(R.id.nickname_edittext);

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

        //提交 ----从这里回任务模板页面
        addphone_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Tools.isEmpty(editText.getText().toString())) {
                    Tools.showToast(AddTagActivity.this, "请填写标签");
                } else {
                    createLabel();
                }
            }
        });
    }

    private String labelId;

    private void createLabel() {
        createLabel.sendPostRequest(Urls.CreateLabel, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (!jsonObject.isNull("data")) {
                            jsonObject = jsonObject.optJSONObject("data");
                            labelId = jsonObject.getString("labelId"); //标签id
                        }
                        Tools.showToast(AddTagActivity.this, "保存成功~");
                        Intent intent = new Intent();
                        intent.putExtra("invisible_type", invisible_type);
                        intent.putExtra("usermobile_list", temp);
                        intent.putExtra("invisible_label", labelId);
                        Tools.d("tag", "标签创建成功++=================》》》" + invisible_type + "  " + temp + " "
                                + editText.getText().toString() + "   " + labelId);
                        setResult(AppInfo.REQUEST_CODE_ADD, intent);
                        baseFinish();
                    } else {
                        Tools.showToast(AddTagActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.d(e.toString());
                    Tools.showToast(AddTagActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(AddTagActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    @Override
    public void onBack() {
        finish();
    }
}
