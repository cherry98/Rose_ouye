package com.orange.oy.activity.shakephoto_316;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/***
 * beibei  拆红包 {1.未拆 }
 */
public class RedPackageStateActivity extends BaseActivity implements View.OnClickListener {
    private NetworkConnection openRedPack;
    private String theme;
    private String time;
    private String ai_id;

    private void initNetworkConnection() {
        openRedPack = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(RedPackageStateActivity.this));
                params.put("ai_id", ai_id);
                return params;
            }
        };
        openRedPack.setIsShowDialog(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_package_state);
        initNetworkConnection();
        ai_id = getIntent().getStringExtra("ai_id");
        theme = getIntent().getStringExtra("tv_theme");
        time = getIntent().getStringExtra("time");
        TextView tv_theme = (TextView) findViewById(R.id.tv_theme);
        TextView tv_time = (TextView) findViewById(R.id.tv_time);
        tv_theme.setText(theme);
        tv_time.setText(time);
        if (!Tools.isEmpty(theme)) {
            tv_theme.setText(theme);
        }
        if (!Tools.isEmpty(time)) {
            tv_time.setText(time);
        }
        findViewById(R.id.iv_closed).setOnClickListener(this);
        findViewById(R.id.tv_chai).setOnClickListener(this);
    }

    private double money;

    private void openRedPack() {
        openRedPack.sendPostRequest(Urls.OpenRedPack, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        JSONObject object = jsonObject.optJSONObject("data");
                        money = object.getDouble("money"); //  "money":"红包金额，金额为0时红包已抢完"

                        Intent intent = new Intent(RedPackageStateActivity.this, RedPackageOpenActivity.class);
                        intent.putExtra("money", money);
                        intent.putExtra("theme", theme); //活动主题
                        intent.putExtra("time", time);  //创建时间
                        startActivity(intent);
                        baseFinish();
                    } else {
                        Tools.showToast(RedPackageStateActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(RedPackageStateActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(RedPackageStateActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_chai: {
                //拆红包
                openRedPack();
            }
            break;
            case R.id.iv_closed: {
                baseFinish();
            }
            break;
        }
    }
}

