package com.orange.oy.activity.shakephoto_320;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.adapter.mycorps_314.MyAddressAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.shakephoto.ReceiveAddressInfo;
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
 * 我的->收货地址 V3.20
 */
public class MyAddressActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, AdapterView.OnItemClickListener {
    private AppTitle appTitle;

    private void initTitle() {
        appTitle = (AppTitle) findViewById(R.id.myaddr_title);
        appTitle.settingName("地址管理");
        appTitle.showBack(this);
        appTitle.setIllustrate(R.mipmap.image_delete, onExitClickForAppTitle1);
    }

    private AppTitle.OnExitClickForAppTitle onExitClickForAppTitle1 = new AppTitle.OnExitClickForAppTitle() {
        public void onExit() {
            appTitle.hideIllustrate();
            appTitle.settingExit("完成", onExitClickForAppTitle2);
            if (myAddressAdapter != null) {
                myAddressAdapter.setDelete(true);
                myAddressAdapter.notifyDataSetChanged();
            }
        }
    };
    private AppTitle.OnExitClickForAppTitle onExitClickForAppTitle2 = new AppTitle.OnExitClickForAppTitle() {
        public void onExit() {
            appTitle.hideExit();
            appTitle.showIllustrate(R.mipmap.image_delete, onExitClickForAppTitle1);
            if (myAddressAdapter != null) {
                myAddressAdapter.setDelete(false);
                myAddressAdapter.notifyDataSetChanged();
            }
        }
    };

    private void initNetwork() {
        consigneeAddressList = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(MyAddressActivity.this));
                params.put("page", page + "");
                return params;
            }
        };
        delConsigneeAddress = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(MyAddressActivity.this));
                params.put("address_id", address_id);
                return params;
            }
        };
        delConsigneeAddress.setIsShowDialog(true);
    }

    private PullToRefreshListView receiveaddr_listview;
    private NetworkConnection consigneeAddressList, delConsigneeAddress;
    private int page = 1;
    private ArrayList<ReceiveAddressInfo> list;
    private MyAddressAdapter myAddressAdapter;
    private String address_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_address);
        list = new ArrayList<>();
        initTitle();
        initNetwork();
        receiveaddr_listview = (PullToRefreshListView) findViewById(R.id.myaddr_listview);
        myAddressAdapter = new MyAddressAdapter(this, list);
        receiveaddr_listview.setAdapter(myAddressAdapter);
        receiveaddr_listview.setOnItemClickListener(this);
        receiveaddr_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                getData();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        consigneeAddressList.sendPostRequest(Urls.ConsigneeAddressList, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (page == 1) {
                            list.clear();
                        }
                        jsonObject = jsonObject.getJSONObject("data");
                        JSONArray jsonArray = jsonObject.optJSONArray("address_list");
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                ReceiveAddressInfo receiveAddressInfo = new ReceiveAddressInfo();
                                receiveAddressInfo.setAddress_id(object.getString("address_id"));
                                receiveAddressInfo.setConsignee_name(object.getString("consignee_name"));
                                receiveAddressInfo.setConsignee_address(object.getString("consignee_address"));
                                receiveAddressInfo.setConsignee_phone(object.getString("consignee_phone"));
                                receiveAddressInfo.setProvince(object.getString("province"));
                                receiveAddressInfo.setCity(object.getString("city"));
                                receiveAddressInfo.setCounty(object.getString("county"));
                                receiveAddressInfo.setDefault_state(object.getString("default_state"));
                                list.add(receiveAddressInfo);
                            }
                            ReceiveAddressInfo receiveAddressInfo = new ReceiveAddressInfo();
                            receiveAddressInfo.setAddress_id("-1");
                            list.add(receiveAddressInfo);
                            if (myAddressAdapter != null) {
                                myAddressAdapter.notifyDataSetChanged();
                            }
                            receiveaddr_listview.onRefreshComplete();
                            if (jsonArray.length() < 15) {
                                receiveaddr_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            } else {
                                receiveaddr_listview.setMode(PullToRefreshBase.Mode.BOTH);
                            }
                        }
                    } else {
                        Tools.showToast(MyAddressActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(MyAddressActivity.this, getResources().getString(R.string.network_error));
                }
                receiveaddr_listview.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(MyAddressActivity.this, getResources().getString(R.string.network_volleyerror));
                receiveaddr_listview.onRefreshComplete();
            }
        });
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (myAddressAdapter != null) {
            ReceiveAddressInfo receiveAddressInfo = list.get(position - 1);
            if ("-1".equals(receiveAddressInfo.getAddress_id())) {
                Intent intent = new Intent(this, AddAddressActivity.class);
                intent.putExtra("which_page", "0");//添加新地址
                startActivity(intent);
            } else if (myAddressAdapter.isClick()) {
                address_id = receiveAddressInfo.getAddress_id();
                delConsigneeAddress();
            } else {
                Intent intent = new Intent(this, AddAddressActivity.class);
                intent.putExtra("which_page", "1");//编辑地址
                intent.putExtra("address_id", receiveAddressInfo.getAddress_id());
                startActivity(intent);
            }
            myAddressAdapter.setClick(false);
        }
    }

    private void delConsigneeAddress() {
        delConsigneeAddress.sendPostRequest(Urls.DelConsigneeAddress, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Tools.showToast(MyAddressActivity.this, "操作成功");
                        page = 1;
                        getData();
                    } else {
                        Tools.showToast(MyAddressActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(MyAddressActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(MyAddressActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }
}
