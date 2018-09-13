package com.orange.oy.activity.newtask;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
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
 * 800提现===获取更多免税额度
 */
public class ObtainDutyFreeActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.obtaindutyfree_title);
        appTitle.settingName("推荐好友");
        appTitle.showBack(this);
    }

    private void initNetworkConnection() {
        getMonthDutyFreeFriends = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                return params;
            }
        };
        getMonthDutyFreeFriends.setIsShowDialog(true);
        addDutyFreeFriends = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("mobiles", mobiles);
                return params;
            }
        };
    }

    public NetworkConnection getMonthDutyFreeFriends, addDutyFreeFriends;
    private EditText editText1, editText2, editText3, editText4, editText5;
    private ArrayList<String> list, list_detail;
    private String mobiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obtain_duty_free);
        list = new ArrayList<>();
        list_detail = new ArrayList<>();
        initTitle();
        initNetworkConnection();
        editText1 = (EditText) findViewById(R.id.obtaindutyfree_edit1);
        editText2 = (EditText) findViewById(R.id.obtaindutyfree_edit2);
        editText3 = (EditText) findViewById(R.id.obtaindutyfree_edit3);
        editText4 = (EditText) findViewById(R.id.obtaindutyfree_edit4);
        editText5 = (EditText) findViewById(R.id.obtaindutyfree_edit5);
        findViewById(R.id.obtaindutyfree_button).setOnClickListener(this);
        getData();
    }

    private void getData() {
        getMonthDutyFreeFriends.sendPostRequest(Urls.GetMonthDutyFreeFriends, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    if (list_detail == null) {
                        list_detail = new ArrayList<String>();
                    } else {
                        list_detail.clear();
                    }
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        JSONArray jsonArray = jsonObject.optJSONArray("friends");
                        if (jsonArray.length() != 0) {
                            if (jsonArray.length() == 1) {
                                String str1 = jsonArray.getJSONObject(0).getString("friendMobile");
                                editText1.setText(str1);
                                editText1.setFocusable(false);
                                list_detail.add(str1);
                            } else if (jsonArray.length() == 2) {
                                String str1 = jsonArray.getJSONObject(0).getString("friendMobile");
                                String str2 = jsonArray.getJSONObject(1).getString("friendMobile");
                                editText1.setText(str1);
                                editText2.setText(str2);
                                editText1.setFocusable(false);
                                editText2.setFocusable(false);
                                list_detail.add(str1);
                                list_detail.add(str2);
                            } else if (jsonArray.length() == 3) {
                                String str1 = jsonArray.getJSONObject(0).getString("friendMobile");
                                String str2 = jsonArray.getJSONObject(1).getString("friendMobile");
                                String str3 = jsonArray.getJSONObject(2).getString("friendMobile");
                                editText1.setText(str1);
                                editText2.setText(str2);
                                editText3.setText(str3);
                                editText1.setFocusable(false);
                                editText2.setFocusable(false);
                                editText3.setFocusable(false);
                                list_detail.add(str1);
                                list_detail.add(str2);
                                list_detail.add(str3);
                            } else if (jsonArray.length() == 4) {
                                String str1 = jsonArray.getJSONObject(0).getString("friendMobile");
                                String str2 = jsonArray.getJSONObject(1).getString("friendMobile");
                                String str3 = jsonArray.getJSONObject(2).getString("friendMobile");
                                String str4 = jsonArray.getJSONObject(3).getString("friendMobile");
                                editText1.setText(str1);
                                editText2.setText(str2);
                                editText3.setText(str3);
                                editText4.setText(str4);
                                editText1.setFocusable(false);
                                editText2.setFocusable(false);
                                editText3.setFocusable(false);
                                editText4.setFocusable(false);
                                list_detail.add(str1);
                                list_detail.add(str2);
                                list_detail.add(str3);
                                list_detail.add(str4);
                            } else if (jsonArray.length() == 5) {
                                String str1 = jsonArray.getJSONObject(0).getString("friendMobile");
                                String str2 = jsonArray.getJSONObject(1).getString("friendMobile");
                                String str3 = jsonArray.getJSONObject(2).getString("friendMobile");
                                String str4 = jsonArray.getJSONObject(3).getString("friendMobile");
                                String str5 = jsonArray.getJSONObject(4).getString("friendMobile");
                                editText1.setText(str1);
                                editText2.setText(str2);
                                editText3.setText(str3);
                                editText4.setText(str4);
                                editText5.setText(str5);
                                editText1.setFocusable(false);
                                editText2.setFocusable(false);
                                editText3.setFocusable(false);
                                editText4.setFocusable(false);
                                editText5.setFocusable(false);
                                list_detail.add(str1);
                                list_detail.add(str2);
                                list_detail.add(str3);
                                list_detail.add(str4);
                                list_detail.add(str5);
                            }
                        }
                    } else {
                        Tools.showToast(ObtainDutyFreeActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(ObtainDutyFreeActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(ObtainDutyFreeActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.obtaindutyfree_button) {
            if (list_detail.size() != 5) {
                if (editText1.getText().toString() != null && !"".equals(editText1.getText().toString())) {
                    if (editText1.getText().toString().length() != 11) {
                        Tools.showToast(ObtainDutyFreeActivity.this, "第一个手机号填写有误~~");
                        return;
                    }
                    list.add(editText1.getText().toString());
                }
                if (editText2.getText().toString() != null && !"".equals(editText2.getText().toString())) {
                    if (editText2.getText().toString().length() != 11) {
                        Tools.showToast(ObtainDutyFreeActivity.this, "第二个手机号填写有误~~");
                        return;
                    }
                    list.add(editText2.getText().toString());
                }
                if (editText3.getText().toString() != null && !"".equals(editText3.getText().toString())) {
                    if (editText3.getText().toString().length() != 11) {
                        Tools.showToast(ObtainDutyFreeActivity.this, "第三个手机号填写有误~~");
                        return;
                    }
                    list.add(editText3.getText().toString());
                }
                if (editText4.getText().toString() != null && !"".equals(editText4.getText().toString())) {
                    if (editText4.getText().toString().length() != 11) {
                        Tools.showToast(ObtainDutyFreeActivity.this, "第四个手机号填写有误~~");
                        return;
                    }
                    list.add(editText4.getText().toString());
                }
                if (editText5.getText().toString() != null && !"".equals(editText5.getText().toString())) {
                    if (editText5.getText().toString().length() != 11) {
                        Tools.showToast(ObtainDutyFreeActivity.this, "第五个手机号填写有误~~");
                        return;
                    }
                    list.add(editText5.getText().toString());
                }
                list.removeAll(list_detail);
                if (list.isEmpty()) {
                    Tools.showToast(this, "请先填写手机号码~~");
                    return;
                }
                mobiles = list.toString().trim().replaceAll("\\[", "").replaceAll("\\]", "");
                sendData();
            }
        }
    }

    private void sendData() {
        addDutyFreeFriends.sendPostRequest(Urls.AddDutyFreeFriends, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        baseFinish();
                    } else {
                        Tools.showToast(ObtainDutyFreeActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(ObtainDutyFreeActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(ObtainDutyFreeActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }
}
