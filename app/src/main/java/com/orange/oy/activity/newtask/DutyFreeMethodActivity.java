package com.orange.oy.activity.newtask;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.BrowserActivity;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.GetvcodeDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 提现800===超800免税方式
 */
public class DutyFreeMethodActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.dutyfreemethod_title);
        appTitle.settingName("提示");
        appTitle.showBack(this);
    }

    private void initNetworkConnection() {
        newGetMoney = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("money", money);
                params.put("type", type);
                if (mobiles != null) {
                    params.put("mobiles", mobiles);
                }
                params.put("vcode", getvcodeDialog.getIdentifycode_code().getText().toString());
                return params;
            }
        };
        newGetMoney.setIsShowDialog(true);
        getVerifiedFriends = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                return params;
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (newGetMoney != null) {
            newGetMoney.stop(Urls.NewGetMoney);
        }
        if (getVerifiedFriends != null) {
            getVerifiedFriends.stop(Urls.GetVerifiedFriends);
        }
    }

    private void initView() {
        ((TextView) findViewById(R.id.text)).setText(Html.fromHtml(getResources().getString(R.string.dutyfree_prompt)));
        dutymethod_phone = findViewById(R.id.dutymethod_phone);
        dutymethod_radio1 = (RadioButton) findViewById(R.id.dutymethod_radio1);
        dutymethod_radio2 = (RadioButton) findViewById(R.id.dutymethod_radio2);
        dutymethod_group = (RadioGroup) findViewById(R.id.dutymethod_group);
        dutymethod_checkbox = (CheckBox) findViewById(R.id.dutymethod_checkbox);
        dutymethod_edit1 = (EditText) findViewById(R.id.dutymethod_edit1);
        dutymethod_edit2 = (EditText) findViewById(R.id.dutymethod_edit2);
        dutymethod_edit3 = (EditText) findViewById(R.id.dutymethod_edit3);
        dutymethod_verified1 = (TextView) findViewById(R.id.dutymethod_verified1);
        dutymethod_verified2 = (TextView) findViewById(R.id.dutymethod_verified2);
        dutymethod_verified3 = (TextView) findViewById(R.id.dutymethod_verified3);
    }

    private String money;
    private NetworkConnection getVerifiedFriends, newGetMoney;
    private RadioGroup dutymethod_group;
    private RadioButton dutymethod_radio1, dutymethod_radio2;
    private View dutymethod_phone;
    private String mobiles, type;
    private CheckBox dutymethod_checkbox;
    private TextView dutymethod_agreement;
    private EditText dutymethod_edit1, dutymethod_edit2, dutymethod_edit3;
    private TextView dutymethod_verified1, dutymethod_verified2, dutymethod_verified3;
    private ArrayList<String> list, list_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duty_free_method);
        list = new ArrayList<>();
        list_detail = new ArrayList<>();
        money = getIntent().getStringExtra("money");
        initTitle();
        initNetworkConnection();
        initView();
        dutymethod_group.setOnCheckedChangeListener(this);
        dutymethod_radio1.setChecked(false);
        dutymethod_radio2.setChecked(false);
        dutymethod_agreement = (TextView) findViewById(R.id.dutymethod_agreement);
        findViewById(R.id.dutymethod_button).setOnClickListener(this);
        findViewById(R.id.dutymethod_agreement).setOnClickListener(this);
        getData();
    }


    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dutymethod_button) {
            if (!dutymethod_radio1.isChecked() && !dutymethod_radio2.isChecked()) {
                Tools.showToast(this, "请选择扣税方式~~");
                return;
            }
            if (dutymethod_radio1.isChecked()) {
                type = "0";
                if (!dutymethod_checkbox.isChecked()) {
                    Tools.showToast(this, "请勾选隐私协议~~");
                    return;
                }
            } else {
                type = "1";
                if (list_detail.size() != 3) {
                    list.clear();
                    if (!TextUtils.isEmpty(dutymethod_edit1.getText().toString().trim())) {
                        if (dutymethod_edit1.getText().toString().trim().length() != 11) {
                            Tools.showToast(this, "第一个手机号填写有误~~");
                            return;
                        }
                        list.add(dutymethod_edit1.getText().toString().trim());
                    }
                    if (!TextUtils.isEmpty(dutymethod_edit2.getText().toString().trim())) {
                        if (dutymethod_edit2.getText().toString().length() != 11) {
                            Tools.showToast(this, "第二个手机号填写有误~~");
                            return;
                        }
                        list.add(dutymethod_edit2.getText().toString().trim());
                    }
                    if (!TextUtils.isEmpty(dutymethod_edit3.getText().toString().trim())) {
                        if (dutymethod_edit3.getText().toString().length() != 11) {
                            Tools.showToast(this, "第一个手机号填写有误~~");
                            return;
                        }
                        list.add(dutymethod_edit3.getText().toString().trim());
                    }
                    if (list.isEmpty()) {
                        Tools.showToast(this, "请先填写手机号码~~");
                        return;
                    }
                    mobiles = list.toString().trim().replaceAll("\\[", "").replaceAll("\\]", "");
                    mobiles = mobiles.replaceAll(" ", "");
                } else {
                    mobiles = list_detail.toString().trim().replaceAll("\\[", "").replaceAll("\\]", "");
                    mobiles = mobiles.replaceAll(" ", "");
                }
            }
            if (!dutymethod_checkbox.isChecked()) {
                Tools.showToast(this, "请勾选隐私协议~~");
                return;
            }
            getvcodeDialog = GetvcodeDialog.ShowGetvcodeDialog(DutyFreeMethodActivity.this);
            getvcodeDialog.getWithdraw_button().setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (getvcodeDialog != null)
                        sendData();//提现操作
                }
            });
        } else if (v.getId() == R.id.dutymethod_agreement) {
            Intent intent = new Intent(DutyFreeMethodActivity.this, BrowserActivity.class);
            intent.putExtra("title", "隐私协议");
            intent.putExtra("content", "http://www.oyearn.com/mobile/policy.html");
            intent.putExtra("flag", "3");
            startActivity(intent);
        }
    }

    private GetvcodeDialog getvcodeDialog;

    private void sendData() {//提现操作
        newGetMoney.sendPostRequest(Urls.NewGetMoney, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Tools.showToast(DutyFreeMethodActivity.this, "提交成功");
                        GetvcodeDialog.dismiss();
                        baseFinish();
                    } else {
                        Tools.showToast(DutyFreeMethodActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(DutyFreeMethodActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(DutyFreeMethodActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, false);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        if (dutymethod_radio1.isChecked()) {
            dutymethod_phone.setVisibility(View.GONE);
        } else {
            dutymethod_phone.setVisibility(View.VISIBLE);
        }
    }

    public void getData() {
        getVerifiedFriends.sendPostRequest(Urls.GetVerifiedFriends, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        JSONArray jsonArray = jsonObject.optJSONArray("mobiles");
                        if (jsonArray != null) {
                            int length = jsonArray.length();
                            for (int i = 0; i < length && i < 3; i++) {
                                switch (i) {
                                    case 0: {
                                        String temp = jsonArray.getString(i);
                                        dutymethod_edit1.setText(hidePhone(temp));
                                        dutymethod_verified1.setVisibility(View.VISIBLE);
                                        dutymethod_edit1.setFocusable(false);
                                        list_detail.add(temp);
                                    }
                                    break;
                                    case 1: {
                                        String temp = jsonArray.getString(i);
                                        dutymethod_edit2.setText(hidePhone(temp));
                                        dutymethod_verified2.setVisibility(View.VISIBLE);
                                        dutymethod_edit2.setFocusable(false);
                                        list_detail.add(temp);
                                    }
                                    break;
                                    case 2: {
                                        String temp = jsonArray.getString(i);
                                        dutymethod_edit3.setText(hidePhone(temp));
                                        dutymethod_verified3.setVisibility(View.VISIBLE);
                                        dutymethod_edit3.setFocusable(false);
                                        list_detail.add(temp);
                                    }
                                    break;
                                }
                            }
                        }
                    } else {
                        Tools.showToast(DutyFreeMethodActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(DutyFreeMethodActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(DutyFreeMethodActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private String hidePhone(String phone) {
        String result;
        if (phone.length() > 7) {
            result = phone.substring(0, 3) + "****" + phone.substring(7, phone.length());
        } else {
            return phone;
        }
        return result;
    }
}
