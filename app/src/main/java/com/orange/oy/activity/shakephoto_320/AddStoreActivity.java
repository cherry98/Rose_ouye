package com.orange.oy.activity.shakephoto_320;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/***
 * V3.20  添加网店
 */
public class AddStoreActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {
    public void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.title);
        appTitle.settingName("添加网店");
        appTitle.showBack(this);
    }

    public void initNetworkConnection() {
        addOnlineStore = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(AddStoreActivity.this));
                params.put("merchant_id", merchant_id);  //商户id
                params.put("store_name", ed_name.getText().toString().trim()); //网店名称
                params.put("store_num", ed_storeNum.getText().toString().trim());  //  网店编号
                params.put("store_url", ed_storeUrl.getText().toString().trim());  //网店网址
                Tools.d(params.toString());
                return params;
            }
        };
    }

    private NetworkConnection addOnlineStore;
    private EditText ed_name, ed_storeUrl, ed_storeNum;
    private String merchant_id, store_name, store_num, store_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_store);
        initTitle();
        initNetworkConnection();
        merchant_id = getIntent().getStringExtra("merchant_id");
        ed_name = (EditText) findViewById(R.id.ed_name);
        ed_storeUrl = (EditText) findViewById(R.id.ed_storeUrl);
        ed_storeNum = (EditText) findViewById(R.id.ed_storeNum);


        findViewById(R.id.btn_commit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                store_name = ed_name.getText().toString().trim();
                store_url = ed_storeUrl.getText().toString().trim();
                store_num = ed_storeNum.getText().toString().trim();
                if (Tools.isEmpty(store_name)) {
                    Tools.showToast(AddStoreActivity.this, "请填写网店名称~");
                    return;
                }
                if (Tools.isEmpty(store_url)) {
                    Tools.showToast(AddStoreActivity.this, "请填写网店网址~");
                    return;
                }
                if (Tools.isEmpty(store_num)) {
                    Tools.showToast(AddStoreActivity.this, "请填写网店编号~");
                    return;
                }
                sendData();
            }
        });

    }

    //	礼品添加提交接口
    private void sendData() {
        addOnlineStore.sendPostRequest(Urls.AddOnlineStore, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        //回到上个页面去刷新
                        StoreListActivity.isRefresh = true;
                        baseFinish();

                        Tools.showToast(AddStoreActivity.this, jsonObject.getString("msg"));
                    } else {
                        Tools.showToast(AddStoreActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(AddStoreActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(AddStoreActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        }, null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != addOnlineStore) {
            addOnlineStore.stop(Urls.AddOnlineStore);
        }
    }

    @Override
    public void onBack() {
        baseFinish();
    }
}
