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
import com.orange.oy.adapter.mycorps_314.ReceiveAddressAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
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
 * 选择收货地址 V3.20
 */
public class ReceiveAddressActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, AdapterView.OnItemClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.receiveaddr_title);
        appTitle.settingName("选择收货地址");
        appTitle.showBack(this);
    }

    private void initNetwork() {
        consigneeAddressList = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(ReceiveAddressActivity.this));
                params.put("page", page + "");
                return params;
            }
        };
    }

    private PullToRefreshListView receiveaddr_listview;
    private ReceiveAddressAdapter receiveAddressAdapter;
    private NetworkConnection consigneeAddressList;
    private int page = 1;
    private ArrayList<ReceiveAddressInfo> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_address);
        list = new ArrayList<>();
        initTitle();
        initNetwork();
        receiveaddr_listview = (PullToRefreshListView) findViewById(R.id.receiveaddr_listview);
        receiveAddressAdapter = new ReceiveAddressAdapter(this, list);
        receiveaddr_listview.setAdapter(receiveAddressAdapter);
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
                            if (receiveAddressAdapter != null) {
                                receiveAddressAdapter.notifyDataSetChanged();
                            }
                            receiveaddr_listview.onRefreshComplete();
                            if (jsonArray.length() < 15) {
                                receiveaddr_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            } else {
                                receiveaddr_listview.setMode(PullToRefreshBase.Mode.BOTH);
                            }
                        }
                    } else {
                        Tools.showToast(ReceiveAddressActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(ReceiveAddressActivity.this, getResources().getString(R.string.network_error));
                }
                receiveaddr_listview.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(ReceiveAddressActivity.this, getResources().getString(R.string.network_volleyerror));
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
        if (receiveAddressAdapter != null) {
            position = position - 1;
            ReceiveAddressInfo receiveAddressInfo = list.get(position);
            if ("-1".equals(receiveAddressInfo.getAddress_id())) {//添加地址
                Intent intent = new Intent(this, AddAddressActivity.class);
                intent.putExtra("which_page", "0");//添加新地址
                startActivity(intent);
            } else {
                receiveAddressAdapter.setmPosition(position);
                receiveAddressAdapter.notifyDataSetChanged();
                Intent intent = new Intent();
                intent.putExtra("consignee_name", receiveAddressInfo.getConsignee_name());
                intent.putExtra("consignee_phone", receiveAddressInfo.getConsignee_phone());
                intent.putExtra("consignee_address", receiveAddressInfo.getConsignee_address());
                intent.putExtra("province", receiveAddressInfo.getProvince());
                intent.putExtra("city", receiveAddressInfo.getCity());
                intent.putExtra("town", receiveAddressInfo.getCounty());
                setResult(RESULT_OK, intent);
                baseFinish();
            }
        }
    }
}
