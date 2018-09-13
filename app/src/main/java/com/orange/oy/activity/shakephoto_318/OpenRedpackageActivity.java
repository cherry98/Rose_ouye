package com.orange.oy.activity.shakephoto_318;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.adapter.mycorps_314.OpenRedpackageAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 活动拆红包 V3.18
 */
public class OpenRedpackageActivity extends BaseActivity {

    private void initNetwork() {
        newOpenRedPack = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(OpenRedpackageActivity.this));
                params.put("token", Tools.getToken());
                params.put("ai_id", ai_id);
                return params;
            }
        };
        getRedPack = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(OpenRedpackageActivity.this));
                params.put("token", Tools.getToken());
                params.put("rpi_id", rpi_id);
                return params;
            }
        };
    }

    private OpenRedpackageAdapter openRedpackageAdapter1, openRedpackageAdapter2;
    private ArrayList<RedPackageInfo> list1;
    private ArrayList<RedPackageInfo> list2;
    private String ai_id;
    private ListView openredpackage_listview1, openredpackage_listview2;
    private NetworkConnection newOpenRedPack, getRedPack;
    private String rpi_id;
    private View openredpackage_money_hint, openredpackage_money1;
    private TextView openredpackage_money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_redpackage);
        list1 = new ArrayList<>();
        list2 = new ArrayList<>();
        ai_id = getIntent().getStringExtra("ai_id");
        initNetwork();
        openredpackage_listview1 = (ListView) findViewById(R.id.openredpackage_listview1);
        openredpackage_listview2 = (ListView) findViewById(R.id.openredpackage_listview2);
        openredpackage_money_hint = findViewById(R.id.openredpackage_money_hint);
        openredpackage_money1 = findViewById(R.id.openredpackage_money1);
        openredpackage_money = (TextView) findViewById(R.id.openredpackage_money);
        openRedpackageAdapter1 = new OpenRedpackageAdapter(this, list1, false);
        openRedpackageAdapter2 = new OpenRedpackageAdapter(this, list2, true);
        openredpackage_listview1.setAdapter(openRedpackageAdapter1);
        openredpackage_listview2.setAdapter(openRedpackageAdapter2);
        getData();
        findViewById(R.id.openredpackage_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baseFinish();
            }
        });
        openredpackage_listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RedPackageInfo redPackageInfo = list1.get(position);
                if ("0".equals(redPackageInfo.getOpen_status())) {//拆红包
                    rpi_id = redPackageInfo.getRpi_id();
                    openRedPackage();
                }
            }
        });
        openredpackage_listview2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RedPackageInfo redPackageInfo = list2.get(position);
                if ("0".equals(redPackageInfo.getOpen_status())) {//拆红包
                    rpi_id = redPackageInfo.getRpi_id();
                    openRedPackage();
                }
            }
        });
    }

    private void openRedPackage() {
        getRedPack.sendPostRequest(Urls.GetRedPack, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        openredpackage_money_hint.setVisibility(View.GONE);
                        openredpackage_money1.setVisibility(View.VISIBLE);
                        openredpackage_money.setVisibility(View.VISIBLE);
//                        Tools.showToast(OpenRedpackageActivity.this, "领取成功");
                        getData();
                    } else {
                        Tools.showToast(OpenRedpackageActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(OpenRedpackageActivity.this, getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(OpenRedpackageActivity.this, getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void getData() {
        newOpenRedPack.sendPostRequest(Urls.NewOpenRedPack, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        jsonObject = jsonObject.getJSONObject("data");
                        openredpackage_money.setText("¥" + jsonObject.getString("red_money"));
                        JSONArray get_redpack = jsonObject.optJSONArray("get_redpack");
                        if (get_redpack != null && get_redpack.length() != 0) {
                            openredpackage_listview1.setVisibility(View.VISIBLE);
                            list1.clear();
                            for (int i = 0; i < get_redpack.length(); i++) {
                                JSONObject object = get_redpack.getJSONObject(i);
                                RedPackageInfo redPackageInfo = new RedPackageInfo();
                                redPackageInfo.setMoney(object.getString("money"));
                                redPackageInfo.setSponsor_name(object.getString("sponsor_name"));
                                redPackageInfo.setOpen_status(object.getString("open_status"));
                                redPackageInfo.setRpi_id(object.getString("rpi_id"));
                                if (openredpackage_money_hint.getVisibility() == View.VISIBLE && "1".equals(redPackageInfo.getOpen_status())) {
                                    openredpackage_money_hint.setVisibility(View.GONE);
                                    openredpackage_money1.setVisibility(View.VISIBLE);
                                    openredpackage_money.setVisibility(View.VISIBLE);
                                }
                                list1.add(redPackageInfo);
                            }
                            if (openRedpackageAdapter1 != null) {
                                openRedpackageAdapter1.notifyDataSetChanged();
                            }
                        } else {
                            openredpackage_listview1.setVisibility(View.GONE);
                        }
                        JSONArray outlet_redpack = jsonObject.optJSONArray("outlet_redpack");
                        if (outlet_redpack != null && outlet_redpack.length() != 0) {
                            openredpackage_listview2.setVisibility(View.VISIBLE);
                            list2.clear();
                            for (int i = 0; i < outlet_redpack.length(); i++) {
                                JSONObject object = outlet_redpack.getJSONObject(i);
                                RedPackageInfo redPackageInfo = new RedPackageInfo();
                                redPackageInfo.setGift_money(object.getString("gift_money"));
                                redPackageInfo.setGift_name(object.getString("gift_name"));
                                redPackageInfo.setSponsor_name(object.getString("sponsor_name"));
                                redPackageInfo.setOpen_status(object.getString("open_status"));
                                redPackageInfo.setRpi_id(object.getString("rpi_id"));
                                if (openredpackage_money_hint.getVisibility() == View.VISIBLE && "1".equals(redPackageInfo.getOpen_status())) {
                                    openredpackage_money_hint.setVisibility(View.GONE);
                                    openredpackage_money1.setVisibility(View.VISIBLE);
                                    openredpackage_money.setVisibility(View.VISIBLE);
                                }
                                list2.add(redPackageInfo);
                            }
                            if (openRedpackageAdapter2 != null) {
                                openRedpackageAdapter2.notifyDataSetChanged();
                            }
                        } else {
                            openredpackage_listview2.setVisibility(View.GONE);
                        }
                    } else {
                        Tools.showToast(OpenRedpackageActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(OpenRedpackageActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(OpenRedpackageActivity.this, getResources().getString(R.string.network_error));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    public class RedPackageInfo {

        /**
         * sponsor_name : 已领红包赞助商
         * money : 已领红包金额
         */

        private String sponsor_name;
        private String money;
        private String open_status;
        private String rpi_id;

        public String getOpen_status() {
            return open_status;
        }

        public void setOpen_status(String open_status) {
            this.open_status = open_status;
        }

        public String getRpi_id() {
            return rpi_id;
        }

        public void setRpi_id(String rpi_id) {
            this.rpi_id = rpi_id;
        }

        public String getSponsor_name() {
            return sponsor_name;
        }

        public void setSponsor_name(String sponsor_name) {
            this.sponsor_name = sponsor_name;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        private String gift_money;
        private String gift_name;
        private String img_url;

        public String getGift_money() {
            return gift_money;
        }

        public void setGift_money(String gift_money) {
            this.gift_money = gift_money;
        }

        public String getGift_name() {
            return gift_name;
        }

        public void setGift_name(String gift_name) {
            this.gift_name = gift_name;
        }

        public String getImg_url() {
            return img_url;
        }

        public void setImg_url(String img_url) {
            this.img_url = img_url;
        }
    }
}
