package com.orange.oy.activity.shakephoto_318;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
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
 * 领取奖励 填写邮件地址 V3.18
 */
public class MailAddressActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.mailaddress_title);
        appTitle.settingName("邮寄地址");
        appTitle.showBack(this);
    }

    private void initNetwork() {
        acceptPrize = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(MailAddressActivity.this));
                params.put("token", Tools.getToken());
                params.put("ai_id", ai_id);
                params.put("consignee_name", mailaddress_name.getText().toString().trim());
                params.put("consignee_phone", mailaddress_phone.getText().toString().trim());
                params.put("consignee_address", mailaddress_address.getText().toString().trim());
                return params;
            }
        };
    }

    private NetworkConnection acceptPrize;
    private String ai_id;
    private EditText mailaddress_name, mailaddress_phone, mailaddress_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_address);
        initTitle();
        initNetwork();
        ImageLoader imageLoader = new ImageLoader(this);
        Intent data = getIntent();
        String prize_image_url = data.getStringExtra("prize_image_url");
        String sponsor_name = data.getStringExtra("sponsor_name");
        String prize_name = data.getStringExtra("prize_name");
        String prize_type = data.getStringExtra("prize_type");
        String acname = data.getStringExtra("acname");
        ai_id = data.getStringExtra("ai_id");
        TextView mailaddress_theme = (TextView) findViewById(R.id.mailaddress_theme);
        TextView mailaddress_sponsor = (TextView) findViewById(R.id.mailaddress_sponsor);
        TextView mailaddress_spize = (TextView) findViewById(R.id.mailaddress_spize);
        TextView mailaddress_desc = (TextView) findViewById(R.id.mailaddress_desc);
        ImageView mailaddress_img = (ImageView) findViewById(R.id.mailaddress_img);
        mailaddress_name = (EditText) findViewById(R.id.mailaddress_name);
        mailaddress_phone = (EditText) findViewById(R.id.mailaddress_phone);
        mailaddress_address = (EditText) findViewById(R.id.mailaddress_address);
        if (!Tools.isEmpty(prize_image_url) && (prize_image_url.startsWith("http://") || prize_image_url.startsWith("https://"))) {
            imageLoader.DisplayImage(prize_image_url, mailaddress_img);
        } else {
            imageLoader.DisplayImage(Urls.Endpoint3 + prize_image_url, mailaddress_img);
        }
        mailaddress_theme.setText("活动主题：" + acname);
        mailaddress_sponsor.setText("赞助商：" + sponsor_name);
        mailaddress_spize.setText(prize_type);
        mailaddress_desc.setText(prize_name);
        findViewById(R.id.mailaddress_button).setOnClickListener(this);
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mailaddress_button) {
            if (Tools.isEmpty(mailaddress_name.getText().toString().trim())) {
                Tools.showToast(this, "请填写收货人姓名");
                return;
            }
            if (Tools.isEmpty(mailaddress_phone.getText().toString().trim())) {
                Tools.showToast(this, "请填写收货人电话");
                return;
            }
            if (Tools.isEmpty(mailaddress_name.getText().toString().trim())) {
                Tools.showToast(this, "请填写收货人地址");
                return;
            }
            acceptPrize();
        }
    }

    private void acceptPrize() {
        acceptPrize.sendPostRequest(Urls.AcceptPrize, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Tools.showToast(MailAddressActivity.this, "操作成功");
                        baseFinish();
                    }
                } catch (JSONException e) {
                    Tools.showToast(MailAddressActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(MailAddressActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }
}
