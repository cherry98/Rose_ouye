package com.orange.oy.activity.shakephoto_320;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.SelectDistrictActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 添加收获地址/编辑地址 V3.20
 */
public class AddAddressActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.addaddress_title);
        String name;
        if ("0".equals(which_page)) {
            name = "添加收货地址";
        } else {
            name = "编辑地址";
        }
        appTitle.settingName(name);
        appTitle.showBack(this);
    }

    private void initNetwork() {
        updateConsigneeAddress = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(AddAddressActivity.this));
                if ("1".equals(which_page)) {
                    params.put("address_id", address_id);
                }
                params.put("consignee_name", consignee_name);
                params.put("consignee_phone", consignee_phone);
                params.put("consignee_address", consignee_address);
                params.put("province", province);
                params.put("province_id", province_id);
                params.put("city", city);
                params.put("default_state", default_state);
                return params;
            }
        };
        editConsigneeAddress = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(AddAddressActivity.this));
                params.put("address_id", address_id);
                return params;
            }
        };
    }

    private String which_page;//0==添加 1==编辑
    private EditText addaddress_name, addaddress_phone, addaddress_addr;
    private CheckBox addaddress_check;
    private NetworkConnection updateConsigneeAddress, editConsigneeAddress;
    private TextView addaddress_city, addaddress_province;
    private String province, city, consignee_name, consignee_phone, consignee_address, default_state, province_id;
    private String address_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        Intent data = getIntent();
        which_page = data.getStringExtra("which_page");
        address_id = data.getStringExtra("address_id");
        initTitle();
        initNetwork();
        addaddress_name = (EditText) findViewById(R.id.addaddress_name);
        addaddress_phone = (EditText) findViewById(R.id.addaddress_phone);
        addaddress_addr = (EditText) findViewById(R.id.addaddress_addr);
        addaddress_check = (CheckBox) findViewById(R.id.addaddress_check);
        addaddress_province = (TextView) findViewById(R.id.addaddress_province);
        addaddress_city = (TextView) findViewById(R.id.addaddress_city);
        findViewById(R.id.addaddress_submit).setOnClickListener(this);
        findViewById(R.id.addaddress_province_ly).setOnClickListener(this);
        findViewById(R.id.addaddress_city_ly).setOnClickListener(this);
        if ("1".equals(which_page)) {
            getData();
        }
    }

    private void getData() {
        editConsigneeAddress.sendPostRequest(Urls.EditConsigneeAddress, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        jsonObject = jsonObject.optJSONObject("data");
                        if (jsonObject != null) {
                            address_id = jsonObject.getString("address_id");
                            consignee_name = jsonObject.getString("consignee_name");
                            consignee_phone = jsonObject.getString("consignee_phone");
                            province = jsonObject.getString("province");
                            city = jsonObject.getString("city");
                            consignee_address = jsonObject.getString("consignee_address");
                            default_state = jsonObject.getString("default_state");
                            addaddress_name.setText(consignee_name);
                            addaddress_phone.setText(consignee_phone);
                            addaddress_addr.setText(consignee_address);
                            addaddress_province.setText(province);
                            addaddress_city.setText(city);
                            addaddress_check.setChecked("1".equals(default_state));
                            province_id = jsonObject.getString("province_id");
                        }
                    } else {
                        Tools.showToast(AddAddressActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(AddAddressActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(AddAddressActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addaddress_submit: {//提交
                consignee_name = addaddress_name.getText().toString().trim();
                if (Tools.isEmpty(consignee_name)) {
                    Tools.showToast(this, "请填写姓名");
                    return;
                }
                consignee_phone = addaddress_phone.getText().toString().trim();
                if (Tools.isEmpty(consignee_phone)) {
                    Tools.showToast(this, "请填写手机号码");
                    return;
                }
                if (consignee_phone.length() != 11) {
                    Tools.showToast(this, "请填写正确的手机号码");
                    return;
                }
                province = addaddress_province.getText().toString().trim();
                if (Tools.isEmpty(province)) {
                    Tools.showToast(this, "请选择省份");
                    return;
                }
                city = addaddress_city.getText().toString().trim();
                if (Tools.isEmpty(city)) {
                    Tools.showToast(this, "请选择城市");
                    return;
                }
                consignee_address = addaddress_addr.getText().toString().trim();
                if (Tools.isEmpty(consignee_address)) {
                    Tools.showToast(this, "请填写详细地址");
                    return;
                }
                if (addaddress_check.isChecked()) {
                    default_state = "1";
                } else {
                    default_state = "0";
                }
                updateConsigneeAddress();
            }
            break;
            case R.id.addaddress_province_ly: {//选择省份
                Intent intent = new Intent(this, SelectDistrictActivity.class);
                intent.putExtra("flag", 0);
                startActivityForResult(intent, 200);
            }
            break;
            case R.id.addaddress_city_ly: {//选择城市
                if (Tools.isEmpty(province_id)) {
                    Tools.showToast(this, "请先选择省份");
                    return;
                }
                Intent intent = new Intent(this, SelectDistrictActivity.class);
                intent.putExtra("flag", 2);
                intent.putExtra("provinceId", province_id);
                startActivityForResult(intent, 200);
            }
            break;
        }
    }

    private void updateConsigneeAddress() {
        updateConsigneeAddress.sendPostRequest(Urls.UpdateConsigneeAddress, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        baseFinish();
                    } else {
                        Tools.showToast(AddAddressActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(AddAddressActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(AddAddressActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AppInfo.SelectDistrictResultCode1 && requestCode == 200) {
            if (data != null) {
                province_id = data.getStringExtra("id");
                String name = data.getStringExtra("name");
                addaddress_province.setText(name);
                if (!name.equals(province)) {
                    city = "";
                    addaddress_city.setText(city);
                }
                province = name;
            }
        } else if (resultCode == AppInfo.SelectDistrictResultCode2 && requestCode == 200) {
            if (data != null) {
                city = data.getStringExtra("name");
                addaddress_city.setText(city);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
