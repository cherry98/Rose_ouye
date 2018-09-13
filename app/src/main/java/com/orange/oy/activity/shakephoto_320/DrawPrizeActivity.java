package com.orange.oy.activity.shakephoto_320;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 领取礼品填写地址页面 V3.20
 */
public class DrawPrizeActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.drawprize_title);
        appTitle.settingName("领取礼品");
        appTitle.showBack(this);
    }

    private void initNetwork() {
        getGift = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(DrawPrizeActivity.this));
                params.put("user_gift_id", user_gift_id);
                params.put("consignee_name", consignee_name);
                params.put("consignee_phone", consignee_phone);
                params.put("province", province);
                params.put("city", city);
//                params.put("town", town);
                params.put("consignee_address", consignee_address);
                return params;
            }
        };
    }

    private EditText drawprize_name, drawprize_phone, drawprize_addr;
    private String user_gift_id;
    private String consignee_name, consignee_phone, consignee_address, province, city, town;
    private NetworkConnection getGift;
    private TextView drawprize_province, drawprize_city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_prize);
        initTitle();
        Intent data = getIntent();
        user_gift_id = data.getStringExtra("user_gift_id");
        String gift_url = data.getStringExtra("gift_url");
        province = data.getStringExtra("province");
        city = data.getStringExtra("city");
//        town = data.getStringExtra("town");
        initNetwork();
        ImageLoader imageLoader = new ImageLoader(this);
        ImageView drawprize_img = (ImageView) findViewById(R.id.drawprize_img);
        TextView drawprize_desc = (TextView) findViewById(R.id.drawprize_desc);
        drawprize_name = (EditText) findViewById(R.id.drawprize_name);
        drawprize_phone = (EditText) findViewById(R.id.drawprize_phone);
        drawprize_addr = (EditText) findViewById(R.id.drawprize_addr);
        drawprize_province = (TextView) findViewById(R.id.drawprize_province);
        drawprize_city = (TextView) findViewById(R.id.drawprize_city);
        imageLoader.setShowWH(200).DisplayImage(Urls.Endpoint3 + gift_url, drawprize_img);
        drawprize_desc.setText(data.getStringExtra("gift_name"));
        findViewById(R.id.drawprize_select).setOnClickListener(this);
        findViewById(R.id.drawprize_submit).setOnClickListener(this);
        drawprize_province.setOnClickListener(this);
        drawprize_city.setOnClickListener(this);
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.drawprize_select: {//选择收货地址
                Intent intent = new Intent(this, ReceiveAddressActivity.class);
                startActivityForResult(intent, 0);
            }
            break;
            case R.id.drawprize_province: {//选择省份
                Intent intent = new Intent(this, SelectDistrictActivity.class);
                intent.putExtra("flag", 0);
                startActivityForResult(intent, 200);
            }
            break;
            case R.id.drawprize_city: {//选择城市
                if (drawprize_province.getTag() == null) {
                    Tools.showToast(this, "请先选择省份");
                    return;
                }
                Intent intent = new Intent(this, SelectDistrictActivity.class);
                intent.putExtra("flag", 2);
                intent.putExtra("provinceId", drawprize_province.getTag().toString());
                startActivityForResult(intent, 200);
            }
            break;
            case R.id.drawprize_submit: {//提交
                consignee_name = drawprize_name.getText().toString().trim();
                if (Tools.isEmpty(consignee_name)) {
                    Tools.showToast(this, "请填写收货人姓名");
                    return;
                }
                consignee_phone = drawprize_phone.getText().toString().trim();
                if (Tools.isEmpty(consignee_phone)) {
                    Tools.showToast(this, "请填写收货人手机号");
                    return;
                }
                consignee_address = drawprize_addr.getText().toString().trim();
                if (Tools.isEmpty(consignee_address)) {
                    Tools.showToast(this, "请填写收货人地址");
                    return;
                }
                getGift();
            }
            break;
        }
    }

    private void getGift() {
        getGift.sendPostRequest(Urls.GetGift, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Tools.showToast(DrawPrizeActivity.this, "操作成功");
                        baseFinish();
                    } else {
                        Tools.showToast(DrawPrizeActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(DrawPrizeActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(DrawPrizeActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (data != null) {
                consignee_name = data.getStringExtra("consignee_name");
                consignee_phone = data.getStringExtra("consignee_phone");
                consignee_address = data.getStringExtra("consignee_address");
                province = data.getStringExtra("province");
                city = data.getStringExtra("city");
                town = data.getStringExtra("town");
                drawprize_name.setText(consignee_name);
                drawprize_phone.setText(consignee_phone);
                drawprize_addr.setText(consignee_address);
                drawprize_province.setText(province);
                drawprize_city.setText(city);
            }
        } else if (resultCode == AppInfo.SelectDistrictResultCode1 && requestCode == 200) {
            if (data != null) {
                drawprize_province.setTag(data.getStringExtra("id"));
                String name = data.getStringExtra("name");
                drawprize_province.setText(name);
                if (!name.equals(province)) {
                    city = "";
                    drawprize_city.setText(city);
                }
                province = name;
            }
        } else if (resultCode == AppInfo.SelectDistrictResultCode2 && requestCode == 200) {
            if (data != null) {
                city = data.getStringExtra("name");
                drawprize_city.setText(city);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
