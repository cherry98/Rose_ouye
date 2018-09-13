package com.orange.oy.activity.shakephoto_320;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.adapter.StoreAddressAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.GiftInfo;
import com.orange.oy.info.StoreAddressInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.MyListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * V3.20   店铺地址管理
 */
public class StoreAddressActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, StoreAddressAdapter.AbandonButton {
    private AppTitle appTitle;

    private void initTitle() {
        appTitle = (AppTitle) findViewById(R.id.title);
        appTitle.settingName("店铺地址");
        appTitle.showBack(this);
        settingDel1();
    }

    private void settingDel1() {
        appTitle.hideExit();
        if (adapter != null) {
            adapter.setDelet(false);
        }
        appTitle.showIllustrate(R.mipmap.grrw_button_shanchu, new AppTitle.OnExitClickForAppTitle() {
            public void onExit() {
                settingDel2();
            }
        });
    }

    private void settingDel2() {
        appTitle.hideIllustrate();
        if (adapter != null) {
            adapter.setDelet(true);
        }
        appTitle.settingExit("完成", new AppTitle.OnExitClickForAppTitle() {
            public void onExit() {
                settingDel1();
            }
        });
    }

    private void initNetworkConnection() {
        merchantOutletList = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(StoreAddressActivity.this));
                params.put("merchant_id", merchant_id);  //	商户id【必传】
                //  params.put("page","");//页码，从1开始，不传为全部
                return params;
            }
        };
        delMerchantOutlet = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(StoreAddressActivity.this));
                params.put("merchant_id", merchant_id);  //		礼品id【必传】
                params.put("outlet_id", outlet_id); // 店铺id【必传】
                return params;
            }
        };
    }

    private StoreAddressAdapter adapter;
    private NetworkConnection merchantOutletList, delMerchantOutlet;
    private String merchant_id;
    private ArrayList<StoreAddressInfo> list;
    private MyListView list_view;
    private String gift_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_present_management);
        initTitle();
        list = new ArrayList<>();
        merchant_id = getIntent().getStringExtra("merchant_id");
        list_view = (MyListView) findViewById(R.id.list_view);
        LinearLayout lin_Present = (LinearLayout) findViewById(R.id.lin_Present);
        TextView tv_text = (TextView) findViewById(R.id.tv_text);
        tv_text.setText("添加分店地址");
        initNetworkConnection();
        getData();
        adapter = new StoreAddressAdapter(StoreAddressActivity.this, list);
        list_view.setAdapter(adapter);
        adapter.setAbandonButtonListener(this);
        lin_Present.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加分店地址页面
                Intent intent = new Intent(StoreAddressActivity.this, AddStoreAddressActivity.class);
                intent.putExtra("merchant_id", merchant_id);
                startActivity(intent);
            }
        });
        isRefresh = false;
    }

    public static boolean isRefresh;

    @Override
    protected void onResume() {
        super.onResume();
        if (isRefresh) {
            isRefresh = false;
            getData();
        }
    }

    private void getData() {
        merchantOutletList.sendPostRequest(Urls.MerchantOutletList, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {

                        if (!jsonObject.isNull("data")) {
                            if (list == null) {
                                list = new ArrayList<StoreAddressInfo>();
                            } else {
                                list.clear();
                            }
                            jsonObject = jsonObject.getJSONObject("data");
                            JSONArray jsonArray = jsonObject.getJSONArray("outlet_list");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                StoreAddressInfo addressInfo = new StoreAddressInfo();
                                JSONObject object = jsonArray.getJSONObject(i);
                                addressInfo.setCity(object.getString("city"));
                                addressInfo.setOutlet_address(object.getString("outlet_address"));
                                addressInfo.setOutlet_id(object.getString("outlet_id"));
                                addressInfo.setOutlet_name(object.getString("outlet_name"));
                                addressInfo.setOutlet_num(object.getString("outlet_num"));
                                addressInfo.setProvince(object.getString("province"));
                                addressInfo.setTown(object.getString("town"));
                                list.add(addressInfo);
                            }
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Tools.showToast(StoreAddressActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(StoreAddressActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(StoreAddressActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != merchantOutletList) {
            merchantOutletList.stop(Urls.MerchantOutletList);
        }
        if (null != delMerchantOutlet) {
            delMerchantOutlet.stop(Urls.DelMerchantOutlet);
        }
    }

    private void abandon() {
        delMerchantOutlet.sendPostRequest(Urls.DelMerchantOutlet, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        adapter.setDelet(false);
                        getData();
                        Tools.showToast(StoreAddressActivity.this, "放弃成功");
                    } else {
                        Tools.showToast(StoreAddressActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(StoreAddressActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(StoreAddressActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    private String outlet_id;

    @Override
    public void onclick(int position) {
        outlet_id = list.get(position).getOutlet_id();
        ConfirmDialog.showDialog(this, "您确定要删除吗？", true, new ConfirmDialog.OnSystemDialogClickListener() {
            @Override
            public void leftClick(Object object) {
                adapter.setDelet(false);
            }

            @Override
            public void rightClick(Object object) {
                abandon();
            }
        });
    }

    @Override
    public void onitemclick(int position) {

    }
}
