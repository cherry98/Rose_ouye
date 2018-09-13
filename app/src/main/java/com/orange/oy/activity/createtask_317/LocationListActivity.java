package com.orange.oy.activity.createtask_317;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.shakephoto_316.SearchLocationActivity;
import com.orange.oy.adapter.mycorps_314.LocationListAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.LocationListInfo;
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
 * 精确位置投放列表 V3.17
 */
public class LocationListActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, AdapterView.OnItemClickListener, View.OnClickListener {
    private AppTitle appTitle;

    private void initTitle() {
        appTitle = (AppTitle) findViewById(R.id.locationlist_title);
        appTitle.settingName("精确位置投放");
        appTitle.showBack(this);
        appTitle.showIllustrate(R.mipmap.grrw_button_shanchu, onExitClickForAppTitle1);
    }

    private void initNetwork() {
        addressList = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(LocationListActivity.this));
                params.put("token", Tools.getToken());
                params.put("template_id", template_id);
                return params;
            }
        };
        delAddress = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(LocationListActivity.this));
                params.put("token", Tools.getToken());
                params.put("dai_id", dai_id);
                return params;
            }
        };
        delAddress.setIsShowDialog(true);
    }

    private MyListView locationlist_listview;
    private LocationListAdapter locationListAdapter;
    private NetworkConnection addressList, delAddress;
    private String template_id, address_list, exe_num, dai_id, task_size;
    private ArrayList<LocationListInfo> list;
    private EditText locationlist_edit;
    private boolean isCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);
        list = new ArrayList<>();
        template_id = getIntent().getStringExtra("template_id");
        initTitle();
        initNetwork();
        locationlist_listview = (MyListView) findViewById(R.id.locationlist_listview);
        locationlist_edit = (EditText) findViewById(R.id.locationlist_edit);
        String exe_num = getIntent().getStringExtra("exe_num");
        if (!Tools.isEmpty(exe_num)) {
            locationlist_edit.setText(exe_num);
        } else {
            locationlist_edit.setText("");
        }
        locationListAdapter = new LocationListAdapter(this, list);
        locationlist_listview.setAdapter(locationListAdapter);
        locationlist_listview.setOnItemClickListener(this);
        isCreate = getIntent().getBooleanExtra("isCreate", true);
        if (isCreate) {
            getData();
        } else {
            address_list = getIntent().getStringExtra("address_list");
            if (!Tools.isEmpty(address_list) && address_list.length() > 0) {
                try {
                    parseData(new JSONObject(address_list).getJSONArray("address_list"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                getData();
            }
        }
        findViewById(R.id.locationlist_button).setOnClickListener(this);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1: {
                    if (isCreate) {
                        return;
                    }
                    LocationListInfo locationListInfo = new LocationListInfo();
                    locationListInfo.setAddress(data.getStringExtra("address"));
                    locationListInfo.setAddress_name(data.getStringExtra("item1"));
                    locationListInfo.setCity(data.getStringExtra("city"));
                    locationListInfo.setCounty(data.getStringExtra("county"));
                    locationListInfo.setLatitude(data.getStringExtra("latitude"));
                    locationListInfo.setLongitude(data.getStringExtra("longitude"));
                    locationListInfo.setProvince(data.getStringExtra("province"));
                    list.add(list.size() - 1, locationListInfo);
                    if (locationListAdapter != null) {
                        locationListAdapter.setDelete(false);
                        locationListAdapter.notifyDataSetChanged();
                    }
                }
                break;
            }
        }
    }

    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
//        if (!isCreate) {
//            return;
//        }
        addressList.sendPostRequest(Urls.AddressList, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (!list.isEmpty()) {
                            list.clear();
                        }
                        jsonObject = jsonObject.optJSONObject("data");
                        address_list = jsonObject.toString();
                        JSONArray jsonArray = jsonObject.optJSONArray("address_list");
                        parseData(jsonArray);
                    } else {
                        Tools.showToast(LocationListActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(LocationListActivity.this, getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(LocationListActivity.this, getString(R.string.network_batch_error));
            }
        });
    }

    private void parseData(JSONArray jsonArray) throws JSONException {
        if (jsonArray != null) {
            task_size = jsonArray.length() + "";
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.optJSONObject(i);
                LocationListInfo locationListInfo = new LocationListInfo();
                locationListInfo.setDai_id(object.optString("dai_id"));
                locationListInfo.setProvince(object.getString("province"));
                locationListInfo.setCity(object.getString("city"));
                locationListInfo.setCounty(object.getString("county"));
                locationListInfo.setAddress(object.getString("address"));
                locationListInfo.setAddress_name(object.getString("address_name"));
                locationListInfo.setLatitude(object.getString("latitude"));
                locationListInfo.setLongitude(object.getString("longitude"));
                list.add(locationListInfo);
            }
            LocationListInfo locationListInfo = new LocationListInfo();
            locationListInfo.setDai_id("-1");
            list.add(locationListInfo);
            if (locationListAdapter != null) {
                locationListAdapter.setDelete(false);
                locationListAdapter.notifyDataSetChanged();
            }
        }
    }

    public void onBack() {
        baseFinish();
    }

    private AppTitle.OnExitClickForAppTitle onExitClickForAppTitle1 = new AppTitle.OnExitClickForAppTitle() {
        public void onExit() {
            appTitle.hideIllustrate();
            appTitle.settingExit("完成", onExitClickForAppTitle2);
            if (locationListAdapter != null) {
                locationListAdapter.notifyDataSetChanged();
                locationListAdapter.setDelete(true);
            }
        }
    };
    private AppTitle.OnExitClickForAppTitle onExitClickForAppTitle2 = new AppTitle.OnExitClickForAppTitle() {
        public void onExit() {
            appTitle.hideExit();
            appTitle.showIllustrate(R.mipmap.image_delete, onExitClickForAppTitle1);
            if (locationListAdapter != null) {
                locationListAdapter.setDelete(false);
                locationListAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (locationListAdapter != null) {
            LocationListInfo locationListInfo = list.get(position);
            dai_id = locationListInfo.getDai_id();
            if (locationListAdapter.isClick1()) {// 删除位置信息
                if (isCreate) {
                    delAddress();
                } else {
                    list.remove(position);
                    if (locationListAdapter != null) {
                        locationListAdapter.setDelete(false);
                        locationListAdapter.notifyDataSetChanged();
                    }
                    appTitle.hideExit();
                    appTitle.showIllustrate(R.mipmap.image_delete, onExitClickForAppTitle1);
                    if (locationListAdapter != null) {
                        locationListAdapter.setDelete(false);
                        locationListAdapter.notifyDataSetChanged();
                    }
                }
            } else if (locationListAdapter.isClick2()) {//添加位置
                Intent intent = new Intent(this, SearchLocationActivity.class);
                intent.putExtra("isPrecise", false);
                intent.putExtra("title", "精确位置投放");
                intent.putExtra("which_page", "0");
                intent.putExtra("template_id", template_id);
                startActivityForResult(intent, 1);
            }
            locationListAdapter.clearClick();
        }
    }

    private void delAddress() {
        delAddress.sendPostRequest(Urls.DelAddress, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Tools.showToast(LocationListActivity.this, "删除成功");
                        appTitle.hideExit();
                        appTitle.showIllustrate(R.mipmap.image_delete, onExitClickForAppTitle1);
                        if (locationListAdapter != null) {
                            locationListAdapter.setDelete(false);
                            locationListAdapter.notifyDataSetChanged();
                        }
                        getData();
                    } else {
                        Tools.showToast(LocationListActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(LocationListActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(LocationListActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    public void onClick(View v) {
        if (v.getId() == R.id.locationlist_button) {//提交 回传信息
            exe_num = locationlist_edit.getText().toString().trim();
            if (Tools.isEmpty(exe_num)) {
                Tools.showToast(this, "请输入此任务可被领取执行的次数");
                return;
            }
            if (!isCreate) {
                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                try {
                    int size = list.size();
                    for (int i = 0; i < size; i++) {
                        LocationListInfo locationListInfo = list.get(i);
                        if ("-1".equals(locationListInfo.getDai_id())) {
                            continue;
                        }
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put("province", locationListInfo.getProvince());
                        jsonObject1.put("city", locationListInfo.getCity());
                        jsonObject1.put("county", locationListInfo.getCounty());
                        jsonObject1.put("address", locationListInfo.getAddress());
                        jsonObject1.put("address_name", locationListInfo.getAddress_name());
                        jsonObject1.put("latitude", locationListInfo.getLatitude());
                        jsonObject1.put("longitude", locationListInfo.getLongitude());
                        jsonArray.put(jsonObject1);
                    }
                    jsonObject.put("address_list", jsonArray);
                    task_size = jsonArray.length() + "";
                    address_list = jsonObject.toString();
                } catch (JSONException e) {
                    Tools.showToast(this, "地址存储失败");
                }
            }
            if ("0".equals(task_size)) {
                Tools.showToast(this, "请先添加位置");
                return;
            }
            Intent intent = new Intent();
            intent.putExtra("address_list", address_list);
            intent.putExtra("exe_num", exe_num);
            intent.putExtra("task_size", task_size);
            setResult(AppInfo.REQUEST_CODE_COLLECT, intent);
            baseFinish();
        }
    }
}
