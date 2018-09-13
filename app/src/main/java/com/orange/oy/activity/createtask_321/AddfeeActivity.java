package com.orange.oy.activity.createtask_321;

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
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/***
 *   V3.21  追加费用
 */
public class AddfeeActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {


    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.fee_view);
        appTitle.settingName("追加费用");
        appTitle.showBack(this);
    }


    private void initNetworkConnection() {
        selectprice = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(AddfeeActivity.this));
                params.put("project_id", project_id); // 项目id【必传】
                return params;
            }
        };
        addpricePayInfo = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(AddfeeActivity.this));
                params.put("project_id", project_id); // 项目id【必传】
                params.put("add_price", add_price); //单个任务加价金额
                return params;
            }
        };
    }

    private String add_price;
    private TextView tv_notdo_num, tv_unit_price, tv_allunit_price, tv_commit;
    private ImageView iv_add;
    private NetworkConnection selectprice, addpricePayInfo;
    private String project_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_feectivity);
        project_id = getIntent().getStringExtra("project_id");
        initTitle();
        initNetworkConnection();
        tv_notdo_num = (TextView) findViewById(R.id.tv_notdo_num);
        tv_unit_price = (TextView) findViewById(R.id.tv_unit_price);
        tv_allunit_price = (TextView) findViewById(R.id.tv_allunit_price);
        tv_commit = (TextView) findViewById(R.id.tv_commit);
        iv_add = (ImageView) findViewById(R.id.iv_add);
        iv_add.setOnClickListener(this);
        tv_commit.setOnClickListener(this);

        getData();
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    private double price2, Allprice; //本来的价格

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add: {
                price++;
                tv_unit_price.setText(price + "");
                // 3、加价总额 = 未做的任务数量 * 增加的奖励金额
                Allprice = (left_num * ((price) - (price2)));
                tv_allunit_price.setText(Allprice + "");
            }
            break;
            case R.id.tv_commit: {
                if (Allprice == 0) {
                    Tools.showToast(this, "该任务已做完，不可以追加费用了哦~");
                    return;
                }

                if (price == price2) {
                    Tools.showToast(this, "请追加任务单价~");
                    return;
                }

                add_price = String.valueOf((price - price2));
                commit();
            }
            break;
        }
    }

    private String account_money, additional_id;
    private double price;
    private int left_num;


    private void commit() {
        addpricePayInfo.sendPostRequest(Urls.AddpricePayInfo, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                ConfirmDialog.dissmisDialog();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        JSONObject object = jsonObject.optJSONObject("data");
                        account_money = object.optString("account_money");  // 账户余额
                        additional_id = object.optString("additional_id");  // 项目加价信息id

                        Intent intent = new Intent(AddfeeActivity.this, PayFeeActivity.class);
                        intent.putExtra("account_money", account_money);
                        intent.putExtra("additional_id", additional_id);
                        intent.putExtra("project_id", project_id);
                        intent.putExtra("Allprice", Allprice);
                        startActivity(intent);
                    } else {
                        Tools.showToast(getBaseContext(), jsonObject.optString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.d(e.toString());
                    Tools.showToast(AddfeeActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(AddfeeActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        }, false);
    }

    private void getData() {
        selectprice.sendPostRequest(Urls.Selectprice, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (!jsonObject.isNull("data")) {
                            jsonObject = jsonObject.getJSONObject("data");
                            left_num = jsonObject.optInt("left_num"); //未做的位置数量（即未被执行过的网点数量）
                            price = jsonObject.optDouble("price"); //任务单价
                            //  if (left_num != 0) {
                            tv_notdo_num.setText("未做的位置数量 : " + left_num);
                            // }
                            //  if (0 != price) {
                            price2 = price;
                            tv_unit_price.setText(price + "");
                            //  }

                        }
                    } else {
                        Tools.showToast(AddfeeActivity.this, jsonObject.getString("msg"));
                    }
                } catch (
                        JSONException e)

                {
                    Tools.showToast(AddfeeActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(AddfeeActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }


}
